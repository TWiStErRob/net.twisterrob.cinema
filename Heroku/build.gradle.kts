plugins {
	id("io.gitlab.arturbosch.detekt")
}

allprojects {
	repositories {
		jcenter()
		Deps.Ktor.repo(this@allprojects)
	}

	plugins.withId("org.jetbrains.kotlin.kapt") {
		val kapt = this@allprojects.extensions.getByName<org.jetbrains.kotlin.gradle.plugin.KaptExtension>("kapt")
		kapt.apply {
			includeCompileClasspath = false
		}
	}

	plugins.withId("java") {
		configure<JavaPluginConvention> {
			sourceCompatibility = JavaVersion.VERSION_1_8
			targetCompatibility = JavaVersion.VERSION_1_8
		}
	}

	plugins.withId("org.jetbrains.kotlin.jvm") {
		tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
			kotlinOptions {
				allWarningsAsErrors = true
				verbose = true
				freeCompilerArgs = freeCompilerArgs + listOf(
					"-Xopt-in=kotlin.RequiresOptIn"
				)
			}
		}
	}

	tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
		// Target version of the generated JVM bytecode. It is used for type resolution.
		jvmTarget = JavaVersion.VERSION_1_8.toString()
	}

	if (this@allprojects.path != ":") {
		afterEvaluate {
			tasks.matching { it.name == LifecycleBasePlugin.CHECK_TASK_NAME }.configureEach {
				setDependsOn(dependsOn.filterNot { it is TaskProvider<*> && it.name == "detekt" })
			}
		}
	}

	plugins.withId("java") {
		val base = this@allprojects.the<BasePluginConvention>()
		base.archivesBaseName = "twisterrob-cinema-" + this@allprojects.path.substringAfter(":").replace(":", "-")
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
			"check" {
				// Remove default dependency, because it runs all tests.
				setDependsOn(dependsOn.filterNot { it is TaskProvider<*> && it.name == test.name })
				dependsOn(unitTest)
				dependsOn(functionalTest)
				dependsOn(integrationTest)
				// Don't want to run it automatically, ever.
				setDependsOn(dependsOn.filterNot { it is TaskProvider<*> && it.name == integrationExternalTest.name })
			}
		}
	}
	plugins.withId("java") {
		this@allprojects.tasks {
			val sourceSets = this@allprojects.the<JavaPluginConvention>().sourceSets
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
			buildUponDefaultConfig = true
			allRules = true
			config = rootProject.files("config/detekt/detekt.yml")
			baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")

			parallel = true

			reports {
				html.enabled = true // human
				xml.enabled = true // checkstyle
				txt.enabled = true // console
				// https://sarifweb.azurewebsites.net
				sarif.enabled = true // Github Code Scanning
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

// Need to eagerly create this, so that we can call tasks.withType in it.
project.tasks.create<TestReport>("tests") {
	destinationDir = file("${buildDir}/reports/tests/all")
	project.evaluationDependsOnChildren()
	allprojects.forEach { subproject ->
		subproject.tasks.withType<Test> {
			if (this.name == "unitTest" || this.name == "functionalTest" || this.name == "integrationTest") {
				ignoreFailures = true
				reports.junitXml.isEnabled = true
				this@create.reportOn(this@withType)
			}
		}
	}
	doLast {
		val reportFile = File(destinationDir, "index.html")
		val successRegex = """(?s)<div class="infoBox" id="failures">\s*<div class="counter">0<\/div>""".toRegex()
		if (!successRegex.containsMatchIn(reportFile.readText())) {
			throw GradleException("There were failing tests. See the report at: ${reportFile.toURI()}")
		}
	}
}
