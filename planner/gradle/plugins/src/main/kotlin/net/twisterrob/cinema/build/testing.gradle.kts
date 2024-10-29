package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.extendsFrom
import net.twisterrob.cinema.build.dsl.libs
import net.twisterrob.cinema.build.testing.Concurrency
import net.twisterrob.cinema.build.testing.allowUnsafe
import net.twisterrob.cinema.build.testing.parallelJUnit5Execution

plugins {
	id("org.gradle.jvm-test-suite")
	id("org.jetbrains.kotlin.jvm")
	id("com.google.devtools.ksp")
	id("org.gradle.java-test-fixtures")
	id("net.twisterrob.cinema.build.test-suite-ksp")
}

tasks.withType<Test> {
	maxHeapSize = "512M"
	allowUnsafe()
}

// Allow testFixtures to see into production internals.
kotlin.target.compilations.named("testFixtures") {
	associateWith(kotlin.target.compilations.getByName("main"))
}

@Suppress("UnstableApiUsage")
testing {
	suites {
		withType<JvmTestSuite>().configureEach {
			targets.configureEach {
				testTask.configure {
					jvmArgs(
						// Reduce occurrences of warning:
						// > Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
						"-Xshare:off",
					)
				}
			}
		}
		withType<JvmTestSuite>().named { it != JvmTestSuitePlugin.DEFAULT_TEST_SUITE_NAME }.configureEach {
			useJUnitJupiter(libs.versions.test.junit.jupiter)
			conventionalSetup(configurations)
			centralizedSetup(configurations)
		}

		/**
		 * Standard Unit tests for a single class or function.
		 * Most/all dependencies are mocked, stubbed or faked out.
		 */
		val unitTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.UNIT_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is not relevant in unit tests.
					parallelJUnit5Execution(Concurrency.PerMethod)
					shouldRunAfter()
				}
			}
		}

		/**
		 * Functional tests test multiple classes in tandem.
		 * Building a dependency graph, but still mocking/stubbing out partially.
		 */
		val functionalTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.FUNCTIONAL_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in functional tests, so the methods need to be synchronized.
					parallelJUnit5Execution(Concurrency.PerClass)
					shouldRunAfter(unitTest)
				}
			}
		}

		/**
		 * Integration test uses an internal third party to simulate real behavior.
		 * For example using a full embedded database.
		 */
		val integrationTest by registering(JvmTestSuite::class) {
			testType = TestSuiteType.INTEGRATION_TEST
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in integration tests, so the methods need to be synchronized.
					parallelJUnit5Execution(Concurrency.PerClass)
					// Fork for each test as it needs more memory to set up embedded Neo4j.
					forkEvery = 1
					shouldRunAfter(unitTest, functionalTest)
				}
			}
			configurations.named(sources.runtimeClasspathConfigurationName).configure {
				// Prevent Neo4J stealing log output:
				// > SLF4J(W): Class path contains multiple SLF4J providers.
				// > SLF4J(W): Found provider [org.neo4j.server.logging.slf4j.SLF4JLogBridge@6f576b33]
				// > SLF4J(W): Found provider [org.apache.logging.slf4j.SLF4JServiceProvider@541f03d7]
				// > SLF4J(W): See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
				// > SLF4J(I): Actual provider is of type [org.neo4j.server.logging.slf4j.SLF4JLogBridge@6f576b33]
				exclude("org.neo4j", "neo4j-slf4j-provider")
			}
		}

		/**
		 * Integration tests use an external third party to execute real behavior.
		 * This means that test status depends on something external to the test.
		 * For example hitting a network endpoint.
		 */
		val integrationExternalTest by registering(JvmTestSuite::class) {
			testType = "integration-external-test"
			targets.configureEach {
				testTask.configure {
					// Logging is relevant in integration tests.
					// In these tests global state may be used, so everything needs to be synchronized.
					parallelJUnit5Execution(Concurrency.PerSuite)
					// Separate integration tests as much as possible.
					forkEvery = 1
					shouldRunAfter(unitTest, functionalTest, integrationTest)
				}
			}
		}

		/*
		 * This is a dummy test suite that is never executed.
		 * It's created by default and there's no way to prevent that.
		 * So reusing it as a hook for all other tests.
		 */
		named<JvmTestSuite>(JvmTestSuitePlugin.DEFAULT_TEST_SUITE_NAME) {
			testType = "ignored" // Allow unitTest to be unit-test.
			targets.configureEach {
				testTask.configure {
					dependsOn(unitTest)
					dependsOn(functionalTest)
					dependsOn(integrationTest)
					@Suppress("ConstantConditionIf")
					if (false) {
						// Don't want to run it automatically, ever.
						dependsOn(integrationExternalTest)
					}
					doFirst {
						// TaskActions won't execute, because the task outcome will be NO-SOURCE.
						// Dependencies will still execute when this task is requested, just like `check` and `build`.
						error("This should never execute, because it has no sources. Move test code to `src/*Test/`.")
					}
				}
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
	// Allow usages of internal code elements in tests.
	kotlin.target.compilations.named(this.name) {
		associateWith(kotlin.target.compilations.getByName("main"))
	}
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
	//configurations.named("ksp${baseName.replaceFirstChar(Char::titlecase)}").extendsFrom(configurations.kspTest)
}
