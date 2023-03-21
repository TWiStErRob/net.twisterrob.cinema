package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.extendsFrom
import net.twisterrob.cinema.build.dsl.libs
import net.twisterrob.cinema.build.testing.Concurrency
import net.twisterrob.cinema.build.testing.allowUnsafe
import net.twisterrob.cinema.build.testing.parallelJUnit5Execution
import org.gradle.api.internal.tasks.JvmConstants

plugins {
	id("org.gradle.jvm-test-suite")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("org.gradle.java-test-fixtures")
}

tasks.withType<Test> {
	maxHeapSize = "512M"
	allowUnsafe()
}

@Suppress("UnstableApiUsage")
testing {
	// JUnit 5 Tag setup, see JUnit5Tags.kt
	suites {
		withType<JvmTestSuite>().configureEach {
			if (this.name == "test") return@configureEach
			useJUnitJupiter(libs.versions.test.junit.jupiter)
			conventionalSetup(configurations)
			centralizedSetup(configurations)
		}

		named<JvmTestSuite>("test") {
			testType = "ignored"
			targets.configureEach {
				testTask.configure {
					doFirst { error("This should never execute, because it has no sources. Move test code to `src/*Test/`.") }
				}
			}
		}

		val unitTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.UNIT_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is not relevant in unit tests.
					parallelJUnit5Execution(Concurrency.PerMethod)
					options {
						this as JUnitPlatformOptions
						excludeTags("functional", "integration")
					}
					shouldRunAfter()
				}
			}
		}

		val functionalTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.FUNCTIONAL_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in functional tests, so the methods need to be synchronized.
					parallelJUnit5Execution(Concurrency.PerClass)
					options {
						this as JUnitPlatformOptions
						includeTags("functional")
					}
					shouldRunAfter(unitTest)
				}
			}
		}
		val integrationTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.INTEGRATION_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in integration tests, so the methods need to be synchronized.
					parallelJUnit5Execution(Concurrency.PerClass)
					// Fork for each test as it needs more memory to set up embedded Neo4j.
					forkEvery = 1
					options {
						this as JUnitPlatformOptions
						includeTags("integration")
						excludeTags("external")
					}
					shouldRunAfter(unitTest, functionalTest)
				}
			}
		}
		val integrationExternalTest by registering(JvmTestSuite::class) {
			testType = "integration-external-test"
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in integration tests.
					// In these tests global state may be used, so everything needs to be synchronized.
					parallelJUnit5Execution(Concurrency.PerSuite)
					// Separate integration tests as much as possible.
					forkEvery = 1
					options {
						this as JUnitPlatformOptions
						includeTags("integration & external")
					}
					shouldRunAfter(unitTest, functionalTest, integrationTest)
				}
			}
		}
		tasks.named("test") {
			dependsOn(unitTest)
			dependsOn(functionalTest)
			dependsOn(integrationTest)
			@Suppress("ConstantConditionIf")
			if (false) {
				// Don't want to run it automatically, ever.
				dependsOn(integrationExternalTest)
			}
		}
	}
}

/**
 * Simulate conventional test setup, similar to how src/test/java is set up with testImplementation and the like.
 */
@Suppress("UnstableApiUsage")
fun JvmTestSuite.conventionalSetup(configurations: ConfigurationContainer) {
	dependencies {
		// Depend on main project.
		this.implementation(project())
		// Depend on testFixtures of the project.
		this.implementation(this.testFixtures(project()))
	}
	// Depend on main project's internal dependencies.
	configurations.named(sources.implementationConfigurationName)
		.extendsFrom(configurations.implementation)
}

/**
 * Simulate AGP's approach to flavors by reusing the built-in test configurations.
 * ```
 * project.testing.suites.withType<JvmTestSuite>().configureEach { dependencies { implementation(...) } }
 * ```
 * becomes
 * ```
 * project.dependencies { testImplementation(...) }
 * ```
 * and similarly for `compileOnly` and `runtimeOnly`.
 */
@Suppress("UnstableApiUsage")
fun JvmTestSuite.centralizedSetup(configurations: ConfigurationContainer) {
	configurations.named(sources.implementationConfigurationName).extendsFrom(configurations.testImplementation)
	configurations.named(sources.compileOnlyConfigurationName).extendsFrom(configurations.testCompileOnly)
	configurations.named(sources.runtimeOnlyConfigurationName).extendsFrom(configurations.testRuntimeOnly)
	// Doesn't work for some reason, probably some internal Kotlin magic.
	//val baseName = sources.annotationProcessorConfigurationName
	//	.removeSuffix(JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.replaceFirstChar(Char::titlecase))
	//configurations.named("kapt${baseName.replaceFirstChar(Char::titlecase)}").extendsFrom(configurations.kaptTest)
}
