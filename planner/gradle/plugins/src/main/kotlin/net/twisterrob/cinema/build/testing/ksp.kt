package net.twisterrob.cinema.build.testing

import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.jvm.JvmComponentDependencies
import org.gradle.kotlin.dsl.getByName

/**
 * @see `test-suite-ksp.gradle.kts`
 */
@Suppress("UnstableApiUsage")
val JvmComponentDependencies.ksp: DependencyCollector
	get() = (this as ExtensionAware).extensions.getByName<DependencyCollector>("ksp")
