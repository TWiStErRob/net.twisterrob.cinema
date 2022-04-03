plugins {
	id("io.gitlab.arturbosch.detekt")
}

val javaVersion = JavaVersion.VERSION_1_8

allprojects {
	repositories {
		mavenCentral()
	}
	this@allprojects.configureDependencyLocking()

	plugins.withId("java") {
		configure<JavaPluginExtension> {
			sourceCompatibility = javaVersion
			targetCompatibility = javaVersion
		}
	}

	plugins.withId("org.jetbrains.kotlin.jvm") {
		tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
			kotlinOptions {
				jvmTarget = javaVersion.toString()
				allWarningsAsErrors = true
				verbose = true
			}
		}
	}

	plugins.withId("java") {
		configure<BasePluginExtension> {
			archivesName.set("twisterrob-cinema-${slug ?: "root"}")
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
	plugins.withId("io.gitlab.arturbosch.detekt") {
		val detekt = this@allprojects.extensions
			.getByName<io.gitlab.arturbosch.detekt.extensions.DetektExtension>("detekt")
		detekt.apply {
			ignoreFailures = true
			buildUponDefaultConfig = true
			allRules = true
			config = rootProject.files("config/detekt/detekt.yml")
			baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = rootProject.projectDir.parentFile.absolutePath

			parallel = true

			tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
				// Target version of the generated JVM bytecode. It is used for type resolution.
				jvmTarget = javaVersion.toString()
				reports {
					html.required.set(true) // human
					xml.required.set(true) // checkstyle
					txt.required.set(true) // console
					// https://sarifweb.azurewebsites.net
					sarif.required.set(true) // Github Code Scanning
				}
			}
		}
	}
}

// When running "gradlew detekt", it'll double-execute, be more specific:
//  * gradlew :detekt
//  * gradlew detekt -x :detekt
tasks.named<io.gitlab.arturbosch.detekt.Detekt>("detekt") {
	description = "Runs over whole code base without the starting overhead for each module."
	// Reconfigure the detekt task rather than registering a separate detektAll task.
	// This inherits the default configuration from the detekt extension, because it's created by the plugin.
	// The root project has no source code, so we can include everything to check.
	setSource(files(rootProject.projectDir))
}

project.tasks.register<Task>("allDependencies") {
	val projects = project.allprojects.sortedBy { it.name }
	doFirst {
		println("Printing dependencies for modules:")
		projects.forEach { println(" * ${it}") }
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
			throw GradleException("There were failing tests. See the report at: ${reportFile.toURI()}")
		}
	}
}
