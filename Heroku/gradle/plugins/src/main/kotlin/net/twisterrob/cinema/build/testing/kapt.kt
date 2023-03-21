package net.twisterrob.cinema.build.testing

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyAdder
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyAdder
import org.gradle.api.plugins.jvm.JvmComponentDependencies
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.newInstance

val JvmComponentDependencies.kapt: DependencyAdder
	get() {
		val implementationConfiguration = DefaultDependencyAdder::class.java
			.getDeclaredField("configuration")
			.apply { isAccessible = true }
			.get(this.implementation) as Configuration
		val name = implementationConfiguration.name.removeSuffix("Implementation")
		val kaptConfiguration = this.project.configurations.getByName("kapt${name.capitalized()}")
		return objectFactory.newInstance<DefaultDependencyAdder>(kaptConfiguration)
	}
