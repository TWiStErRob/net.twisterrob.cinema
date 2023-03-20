import net.twisterrob.gradle.doNotNagAbout
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

allprojects {
	this@allprojects.configureDependencyLocking()
	this@allprojects.forceKotlinVersion()
	this@allprojects.configureSLF4JBindings()

	plugins.withId("java") {
		configure<JavaPluginExtension> {
			sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
			targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
		}
	}

	plugins.withId("org.jetbrains.kotlin.jvm") {
		tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
			compilerOptions {
				jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
				allWarningsAsErrors = true
				verbose = true
			}
		}
	}

	plugins.withId("java") {
		configure<BasePluginExtension> {
			archivesName.set("twisterrob-cinema-${slug}")
		}
	}

	plugins.withId("java") {
		tasks.withType<Test> {
			maxHeapSize = "512M"
			allowUnsafe()
			if (project.property("net.twisterrob.build.verboseReports").toString().toBoolean()) {
				configureVerboseReportsForGithubActions()
			} else {
				//afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
				//	logger.quiet("Executing test ${descriptor.className}.${descriptor.name} with result: ${result.resultType}")
				//}))
			}
			jvmArgs(
				"-Djava.util.logging.config.file=${rootProject.file("config/logging.properties")}"
			)
		}

		// JUnit 5 Tag setup, see JUnit5Tags.kt
		@Suppress("UnstableApiUsage")
		this@allprojects.tasks {
			val test = "test"(Test::class) {
				parallelJUnit5Execution(Concurrency.PerMethod)
				useJUnitPlatform {
				}
			}
			val unitTest = register<Test>("unitTest") {
				// Logging is not relevant in unit tests.
				parallelJUnit5Execution(Concurrency.PerMethod)
				useJUnitPlatform {
					excludeTags("functional", "integration")
				}
				shouldRunAfter()
			}
			val functionalTest = register<Test>("functionalTest") {
				// Logging is relevant in functional tests, so the methods need to be synchronized.
				parallelJUnit5Execution(Concurrency.PerClass)
				useJUnitPlatform {
					includeTags("functional")
				}
				shouldRunAfter(unitTest)
			}
			val integrationTest = register<Test>("integrationTest") {
				// Logging is relevant in integration tests, so the methods need to be synchronized.
				parallelJUnit5Execution(Concurrency.PerClass)
				// For for each test as it needs more memory to set up embedded Neo4j.
				setForkEvery(1)
				useJUnitPlatform {
					includeTags("integration")
					excludeTags("external")
				}
				shouldRunAfter(unitTest, functionalTest)
			}
			val integrationExternalTest = register<Test>("integrationExternalTest") {
				// Logging is relevant in integration tests.
				// In these tests global state may be used, so everything needs to be synchronized.
				parallelJUnit5Execution(Concurrency.PerSuite)
				// Separate integration tests as much as possible.
				setForkEvery(1)
				useJUnitPlatform {
					includeTags("integration & external")
				}
				shouldRunAfter(unitTest, functionalTest, integrationTest)
			}
			val tests = register<Task>("tests") {
				dependsOn(unitTest)
				dependsOn(functionalTest)
				dependsOn(integrationTest)
			}
			// TODEL https://github.com/TWiStErRob/net.twisterrob.cinema/issues/306
			afterEvaluate {
				withType<Test>().configureEach {
					@Suppress("NAME_SHADOWING")
					val test = this@allprojects.the<TestingExtension>().suites["test"] as JvmTestSuite
					testClassesDirs = test.sources.output.classesDirs
					classpath = test.sources.runtimeClasspath
				}
			}
			"check" {
				// Remove default dependency, because it runs all tests.
				notDependsOn { it == test.name }
				dependsOn(tests)
				// Don't want to run it automatically, ever.
				notDependsOn { it == integrationExternalTest.name }
			}
		}
	}
	plugins.withId("java") {
		this@allprojects.tasks {
			val sourceSets = this@allprojects.the<JavaPluginExtension>().sourceSets
			val copyLoggingResources = register<Copy>("copyLoggingResources") {
				from(rootProject.file("config/log4j2.xml"))
				into(sourceSets["main"].resources.srcDirs.first())
			}
			"processResources" {
				dependsOn(copyLoggingResources)
			}
			val copyLoggingTestResources = register<Copy>("copyLoggingTestResources") {
				from(rootProject.file("config/log4j2.xml"))
				into(sourceSets["test"].resources.srcDirs.first())
			}
			"processTestResources"{
				dependsOn(copyLoggingTestResources)
			}
		}
	}
}

rootProject.tasks.register<Delete>("clean") {
	delete(rootProject.buildDir)
}

project.tasks.register<Task>("allDependencies") {
	val projects = project.allprojects.sortedBy { it.name }
	doFirst {
		println(projects.joinToString(prefix = "Printing dependencies for modules:\n", separator = "\n") { " * ${it}" })
	}
	val dependenciesTasks = projects.map { it.tasks.named("dependencies") }
	// Builds a dependency chain: 1 <- 2 <- 3 <- 4, so when executed they're in order.
	dependenciesTasks.reduce { acc, task -> task.apply { get().dependsOn(acc) } }
	// Use finalizedBy instead of dependsOn to make sure this task executes first.
	this@register.finalizedBy(dependenciesTasks)
}

// Need to eagerly create this, so that we can call tasks.withType in it.
project.tasks.create<TestReport>("allTestsReport") {
	destinationDirectory.set(file("${buildDir}/reports/tests/all"))
	project.evaluationDependsOnChildren()
	allprojects.forEach { subproject ->
		subproject.tasks.withType<Test> {
			if (this.name == "unitTest" || this.name == "functionalTest" || this.name == "integrationTest") {
				ignoreFailures = true
				reports.junitXml.required.set(true)
				this@create.testResults.from(this@withType)
			}
		}
	}
	doLast {
		val reportFile = destinationDirectory.file("index.html").get().asFile
		val successRegex = """(?s)<div class="infoBox" id="failures">\s*<div class="counter">0<\/div>""".toRegex()
		if (!successRegex.containsMatchIn(reportFile.readText())) {
			val message = "There were failing tests. See the report at: ${reportFile.toURI()}"
			if (isCI) {
				// On CI we follow the ignoreFailures = true of tests for this task too. Report will fail the check run.
				logger.warn(message)
			} else {
				// Locally blow up.
				throw GradleException(message)
			}
		}
	}
}

val gradleVersion: String = GradleVersion.current().version

// TODEL Gradle sync in IDEA 2022.3.1: https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Please use the archiveFile property instead. " +
			"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.builder.ExternalProjectBuilderImpl\$_getSourceSets_closure"
)

// TODEL Gradle sync in IDEA 2022.3.1: https://youtrack.jetbrains.com/issue/IDEA-306975
@Suppress("MaxLineLength")
doNotNagAbout(
	"The AbstractArchiveTask.archivePath property has been deprecated. " +
			"This is scheduled to be removed in Gradle 9.0. " +
			"Please use the archiveFile property instead. " +
			"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
	"at org.jetbrains.plugins.gradle.tooling.util.SourceSetCachedFinder.createArtifactsMap"
)

if (libs.versions.appengine.get() < "2.4.6") {
	// TODEL AppEngine 2.4.5 v Gradle 8: https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/446
	@Suppress("MaxLineLength")
	doNotNagAbout(
		"The AbstractArchiveTask.archivePath property has been deprecated. " +
				"This is scheduled to be removed in Gradle 9.0. " +
				"Please use the archiveFile property instead. " +
				"See https://docs.gradle.org/${gradleVersion}/dsl/org.gradle.api.tasks.bundling.AbstractArchiveTask.html#org.gradle.api.tasks.bundling.AbstractArchiveTask:archivePath for more details.",
		"at com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin.lambda\$configureExtensions$0"
	)
} else {
	error("AppEngine 2.4.6 deprecation fixed, remove suppression.")
}
