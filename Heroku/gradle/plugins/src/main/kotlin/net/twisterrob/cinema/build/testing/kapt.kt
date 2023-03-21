package net.twisterrob.cinema.build.testing

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyAdder
import org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyAdder
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.plugins.jvm.JvmComponentDependencies
import org.gradle.kotlin.dsl.newInstance

@Suppress("UnstableApiUsage")
val JvmComponentDependencies.kapt: DependencyAdder
	get() {
		val aptConfiguration = DefaultDependencyAdder::class.java
			.getDeclaredField("configuration")
			.apply { isAccessible = true }
			.get(this.annotationProcessor) as Configuration
		val aptSuffix = JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.replaceFirstChar(Char::titlecase)
		val baseName = aptConfiguration.name.removeSuffix(aptSuffix)
		val kaptName = "kapt${baseName.replaceFirstChar(Char::titlecase)}"
		val kaptConfiguration = this.project.configurations.getByName(kaptName)
		return objectFactory.newInstance<DefaultDependencyAdder>(kaptConfiguration)
	}
