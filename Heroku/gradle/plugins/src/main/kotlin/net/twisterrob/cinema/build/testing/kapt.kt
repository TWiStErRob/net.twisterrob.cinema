package net.twisterrob.cinema.build.testing

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.jvm.JvmComponentDependencies
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.getByName

@Suppress("UnstableApiUsage")
fun JvmTestSuite.wireKaptDependencies(configurations: ConfigurationContainer, objects: ObjectFactory) {
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

@Suppress("UnstableApiUsage")
val JvmComponentDependencies.kapt: DependencyCollector
	get() = (this as ExtensionAware).extensions.getByName<DependencyCollector>("kapt")
