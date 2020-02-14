import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.61" apply false
	id("org.jetbrains.kotlin.kapt") version "1.3.61" apply false
}

allprojects {
	repositories {
		jcenter()
		Deps.Ktor.repo(project)
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
				jvmTarget = "1.8"
				freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
			}
		}
	}
	plugins.withId("java") {
		tasks.withType<Test> {
			maxHeapSize = "512M"
			//afterTest(KotlinClosure2({ descriptor: TestDescriptor, result: TestResult ->
			//	logger.quiet("Executing test ${descriptor.className}.${descriptor.name} with result: ${result.resultType}")
			//}))
		}

		// JUnit 5 Tag setup, see JUnit5Tags.kt
		@Suppress("UnstableApiUsage")
		this@allprojects.tasks {
			val test = "test"(Test::class) {
				useJUnitPlatform {
					excludeTags("functional", "integration")
				}
			}
			val functionalTest = register<Test>("functionalTest") {
				useJUnitPlatform {
					includeTags("functional")
				}
				shouldRunAfter(test)
			}
			val integrationTest = register<Test>("integrationTest") {
				maxParallelForks = 2
				useJUnitPlatform {
					includeTags("integration")
					excludeTags("external")
				}
				shouldRunAfter(test, functionalTest)
			}
			val integrationExternalTest = register<Test>("integrationExternalTest") {
				useJUnitPlatform {
					includeTags("integration & external")
				}
				shouldRunAfter(test, functionalTest, integrationTest)
			}
			"check" {
				dependsOn(functionalTest)
				dependsOn(integrationTest)
				// Don't want to run it automatically, ever.
				setDependsOn(dependsOn - integrationExternalTest)
			}
		}
	}
}
