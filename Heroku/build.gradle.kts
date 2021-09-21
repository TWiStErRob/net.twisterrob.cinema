import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
	id("org.jetbrains.kotlin.kapt") version "1.5.31" apply false
	// https://github.com/detekt/detekt/blob/3357abba87e1550c65b6610012bb291e0fbb64ce/build.gradle.kts#L280-L295 and whole file
	id("io.gitlab.arturbosch.detekt") version "1.18.1"
}

allprojects {
	repositories {
		jcenter()
		Deps.Ktor.repo(this@allprojects)
	}

	plugins.withId("org.jetbrains.kotlin.kapt") {
		val kapt = this@allprojects.extensions.getByName<KaptExtension>("kapt")
		kapt.apply {
			includeCompileClasspath = false
		}
	}
	plugins.withId("org.jetbrains.kotlin.jvm") {
		tasks.withType<KotlinCompile> {
			kotlinOptions {
				jvmTarget = JavaVersion.VERSION_1_8.toString()
				allWarningsAsErrors = true
				verbose = true
				freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
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
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	// Target version of the generated JVM bytecode. It is used for type resolution.
	jvmTarget = "1.8"
}
detekt {
	buildUponDefaultConfig = true // preconfigure defaults
	allRules = false // activate all available (even unstable) rules.
	config = files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
	baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt

	reports {
		html.enabled = true // observe findings in your browser with structure and code snippets
		xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
		txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
		sarif.enabled = true // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
	}
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
