package net.twisterrob.cinema.build.testing

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.jvm.JvmComponentDependencies
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension

@Suppress("UnstableApiUsage")
fun wireKapt(project: Project) {
	project.the<TestingExtension>().suites.withType<JvmTestSuite>().configureEach {
		val kaptName = "kapt${this.name.replaceFirstChar(Char::titlecase)}"
		val kaptConfiguration = project.configurations.getByName(kaptName)
		val collector: DependencyCollector = project.objects.dependencyCollector()
		kaptConfiguration.dependencies.addAllLater(collector.dependencies)
		// For use later by .kapt extension.
		(this.dependencies as ExtensionAware).extensions.add("kapt", collector)
	}
}

@Suppress("UnstableApiUsage")
val JvmComponentDependencies.kapt: DependencyCollector
	get() = (this as ExtensionAware).extensions.getByName<DependencyCollector>("kapt")
