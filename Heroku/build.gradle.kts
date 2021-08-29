import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.5.21" apply false
	id("org.jetbrains.kotlin.kapt") version "1.5.21" apply false
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
				jvmTarget = JavaVersion.VERSION_1_8.toString()
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
			/**
			 * https://github.com/neo4j/neo4j/issues/12712
			 */
			fun Test.allowUnsafe() {
				if (JavaVersion.current() < JavaVersion.VERSION_1_9) return
				// WARNING: Illegal reflective access using Lookup on org.neo4j.memory.RuntimeInternals
				// (org.neo4j/neo4j-unsafe/4.2.0/neo4j-unsafe-4.2.0.jar)
				// to class java.lang.String
				jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
				// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
				// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
				// to field sun.nio.ch.FileChannelImpl.positionLock
				jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED")
				// WARNING: Illegal reflective access by org.apache.commons.lang3.reflect.FieldUtils
				// (org.apache.commons/commons-lang3/3.11/commons-lang3-3.11.jar)
				// to field java.io.FileDescriptor.fd
				jvmArgs("--add-opens", "java.base/java.io=ALL-UNNAMED")
				// WARNING: Illegal reflective access by com.shazam.shazamcrest.CyclicReferenceDetector
				// (com.shazam/shazamcrest/0.11/shazamcrest-0.11.jar)
				// to field java.time.OffsetDateTime.serialVersionUID
				// to field java.net.URI.serialVersionUID
				jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
				jvmArgs("--add-opens", "java.base/java.net=ALL-UNNAMED")
				// WARNING: Illegal reflective access by org.eclipse.collections.impl.utility.ArrayListIterate
				// (org.eclipse.collections/eclipse-collections/10.3.0//eclipse-collections-10.3.0.jar)
				// to field java.util.ArrayList.elementData
				jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
			}

			val test = "test"(Test::class) {
				allowUnsafe()
				useJUnitPlatform {
				}
			}
			val unitTest = register<Test>("unitTest") {
				allowUnsafe()
				useJUnitPlatform {
					excludeTags("functional", "integration")
				}
				shouldRunAfter()
			}
			val functionalTest = register<Test>("functionalTest") {
				allowUnsafe()
				useJUnitPlatform {
					includeTags("functional")
				}
				shouldRunAfter(unitTest)
			}
			val integrationTest = register<Test>("integrationTest") {
				allowUnsafe()
				maxParallelForks = 2
				useJUnitPlatform {
					includeTags("integration")
					excludeTags("external")
				}
				shouldRunAfter(unitTest, functionalTest)
			}
			val integrationExternalTest = register<Test>("integrationExternalTest") {
				allowUnsafe()
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
