import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.5.21" apply false
	id("org.jetbrains.kotlin.kapt") version "1.5.21" apply false
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
			parallelJUnit5Execution()
		}

		// JUnit 5 Tag setup, see JUnit5Tags.kt
		@Suppress("UnstableApiUsage")
		this@allprojects.tasks {
			val test = "test"(Test::class) {
				useJUnitPlatform {
				}
			}
			val unitTest = register<Test>("unitTest") {
				useJUnitPlatform {
					excludeTags("functional", "integration")
				}
				shouldRunAfter()
			}
			val functionalTest = register<Test>("functionalTest") {
				useJUnitPlatform {
					includeTags("functional")
				}
				shouldRunAfter(unitTest)
			}
			val integrationTest = register<Test>("integrationTest") {
				// For for each test as it needs more memory to set up embedded Neo4j.
				setForkEvery(1)
				useJUnitPlatform {
					includeTags("integration")
					excludeTags("external")
				}
				shouldRunAfter(unitTest, functionalTest)
			}
			val integrationExternalTest = register<Test>("integrationExternalTest") {
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
