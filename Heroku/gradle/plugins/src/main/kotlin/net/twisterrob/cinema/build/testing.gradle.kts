package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.libs
import net.twisterrob.cinema.build.testing.Concurrency
import net.twisterrob.cinema.build.testing.allowUnsafe
import net.twisterrob.cinema.build.testing.parallelJUnit5Execution

plugins {
	id("org.gradle.jvm-test-suite")
	id("org.jetbrains.kotlin.jvm")
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
			useJUnitJupiter(libs.versions.test.junit.jupiter)
			// Simulate conventional test setup
			dependencies {
				// Depend on main project.
				implementation(project())
				// Depend on main project's internal dependencies.
				configurations.named(sources.implementationConfigurationName)
					.configure { extendsFrom(configurations.implementation.get()) }
				// Depend on testFixtures of the project.
				implementation(testFixtures(project()))
			}
		}

		val unitTest = named<JvmTestSuite>("test") {
			testType.set(TestSuiteType.UNIT_TEST)
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
			testType.set(TestSuiteType.FUNCTIONAL_TEST)
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
			testType.set(TestSuiteType.INTEGRATION_TEST)
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
		val integrationExternalTest  by registering(JvmTestSuite::class) {
			testType.set("integration-external-test")
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
		tasks.register<Task>("tests") {
			dependsOn(unitTest)
			dependsOn(functionalTest)
			dependsOn(integrationTest)
		}

		tasks.named("check") {
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
