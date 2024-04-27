package net.twisterrob.cinema.build

import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.withType

plugins {
	id("org.gradle.jvm-test-suite")
	id("com.google.devtools.ksp")
}

@Suppress("UnstableApiUsage")
testing.suites.withType<JvmTestSuite>().configureEach {
	// Target
	val kspName = "ksp${this.name.replaceFirstChar(Char::titlecase)}"
	val kspConfiguration = configurations.getByName(kspName)

	// Source
	val collector: DependencyCollector = objects.dependencyCollector()
	// For use later by JvmComponentDependencies.ksp extension function.
	(this.dependencies as ExtensionAware).extensions.add("ksp", collector)

	// Link
	kspConfiguration.dependencies.addAllLater(collector.dependencies)
}
