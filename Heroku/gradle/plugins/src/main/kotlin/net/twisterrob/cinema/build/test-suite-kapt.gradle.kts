package net.twisterrob.cinema.build

import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.withType

plugins {
	id("org.gradle.jvm-test-suite")
	id("org.jetbrains.kotlin.kapt")
}

@Suppress("UnstableApiUsage")
testing.suites.withType<JvmTestSuite>().configureEach {
	// Target
	val kaptName = "kapt${this.name.replaceFirstChar(Char::titlecase)}"
	val kaptConfiguration = configurations.getByName(kaptName)

	// Source
	val collector: DependencyCollector = objects.dependencyCollector()
	// For use later by JvmComponentDependencies.kapt extension function.
	(this.dependencies as ExtensionAware).extensions.add("kapt", collector)

	// Link
	kaptConfiguration.dependencies.addAllLater(collector.dependencies)
}
