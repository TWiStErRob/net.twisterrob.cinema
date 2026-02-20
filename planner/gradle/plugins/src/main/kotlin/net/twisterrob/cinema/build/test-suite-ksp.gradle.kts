package net.twisterrob.cinema.build

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
	// For use later by JvmComponentDependencies.ksp extension property.
	(this.dependencies as ExtensionAware).extensions.add("ksp", collector)

	// Link
	kspConfiguration.dependencies.addAllLater(collector.dependencies)
}

/**
 * https://docs.gradle.org/9.2.0-rc-1/userguide/upgrading_version_9.html#removed_incubating_objectfactorydependencycollector_method
 */
private fun ObjectFactory.dependencyCollector(): DependencyCollector {
	@Suppress("detekt.AbstractClassCanBeInterface") // TODEL https://github.com/detekt/detekt/issues/9073
	abstract class DependencyCollectorCreator {
		abstract val dependencyCollector: DependencyCollector
	}
	return newInstance<DependencyCollectorCreator>().dependencyCollector
}
