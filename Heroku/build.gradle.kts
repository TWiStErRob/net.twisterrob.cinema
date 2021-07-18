import org.gradle.util.VersionNumber
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.72" apply false
	id("org.jetbrains.kotlin.kapt") version "1.3.72" apply false
}

allprojects {
	repositories {
		jcenter()
		Deps.Ktor.repo(project)
	}

	configurations.all {
		resolutionStrategy.dependencySubstitution.all {
			val requested = this.requested
			// Waiting for Neo4J OGM release with https://github.com/neo4j/neo4j-ogm/pull/762.
			if (requested is ModuleComponentSelector
				&& requested.module == "classgraph"
				&& requested.group == "io.github.classgraph"
			) {
				if (VersionNumber.parse(requested.version) < VersionNumber.parse("4.8.62")) {
					// 4.8.63 doesn't work on JDK 8
					useTarget(
						"${requested.group}:${requested.module}:4.8.64",
						"Performance issue with low memory: https://github.com/classgraph/classgraph/issues/400"
					)
				}
			}
		}
	}

	plugins.withId("org.jetbrains.kotlin.kapt") {
		val kapt = this@allprojects.extensions.getByName<KaptExtension>("kapt")
		kapt.apply {
			includeCompileClasspath = false
//			javacOptions {
//				option("-source", "8")
//				option("-target", "8")
//			}
		}
	}
	plugins.withId("org.jetbrains.kotlin.jvm") {
		tasks.withType<KotlinCompile> {
			kotlinOptions {
				jvmTarget = JavaVersion.VERSION_1_8.toString()
//				jvmTarget = JavaVersion.VERSION_11.toString()
				freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
			}
		}
	}
	plugins.withId("java") {
//		configure<JavaPluginConvention> {
//			sourceCompatibility = JavaVersion.VERSION_1_8
//			targetCompatibility = JavaVersion.VERSION_11
//		}
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
				maxParallelForks = 2
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
