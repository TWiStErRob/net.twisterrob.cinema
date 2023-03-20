package net.twisterrob.cinema.build

import Concurrency
import allowUnsafe
import configureDependencyLocking
import configureSLF4JBindings
import configureVerboseReportsForGithubActions
import forceKotlinVersion
import net.twisterrob.cinema.heroku.plugins.internal.libs
import notDependsOn
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import parallelJUnit5Execution
import slug

project.configureDependencyLocking()
project.forceKotlinVersion()
project.configureSLF4JBindings()

plugins.withId("java") {
	configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
		targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
	}
}

plugins.withId("org.jetbrains.kotlin.jvm") {
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		compilerOptions {
			jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
			allWarningsAsErrors = true
			verbose = true
		}
	}
}

plugins.withId("java") {
	configure<BasePluginExtension> {
		archivesName.set("twisterrob-cinema-${slug}")
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
	project.tasks {
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
			forkEvery = 1
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
			forkEvery = 1
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
		// TODEL https://github.com/TWiStErRob/net.twisterrob.cinema/issues/306
		afterEvaluate {
			withType<Test>().configureEach {
				@Suppress("NAME_SHADOWING")
				val test = project.the<TestingExtension>().suites["test"] as JvmTestSuite
				testClassesDirs = test.sources.output.classesDirs
				classpath = test.sources.runtimeClasspath
			}
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
	project.tasks {
		val sourceSets = project.the<JavaPluginExtension>().sourceSets
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
