package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.notDependsOn
import net.twisterrob.cinema.build.testing.Concurrency
import net.twisterrob.cinema.build.testing.allowUnsafe
import net.twisterrob.cinema.build.testing.parallelJUnit5Execution

plugins {
	id("org.gradle.jvm-test-suite")
}

tasks.withType<Test> {
	maxHeapSize = "512M"
	allowUnsafe()
}

// JUnit 5 Tag setup, see JUnit5Tags.kt
tasks {
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
			val test = testing.suites["test"] as JvmTestSuite
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
