package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.lang.reflect.Method

plugins {
	id("org.gradle.java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

java {
	sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	compilerOptions {
		jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
		allWarningsAsErrors = true
		verbose = true

		languageVersion = KotlinVersion.KOTLIN_2_0
		// Suppress compileKotlin's warning:
		// > Language version 2.0 is experimental, there are no backwards compatibility guarantees for new language and library features
		freeCompilerArgs.add("-Xsuppress-version-warnings")
	}
}

// TODEL Workaround for https://youtrack.jetbrains.com/issue/KT-63165
// Everything involved is internal, so we need to use reflection.

@Suppress("UNCHECKED_CAST")
val kotlinCKGPCEClass: Class<DefaultTask> = Class
	.forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.CheckKotlinGradlePluginConfigurationErrors")
		as Class<DefaultTask>

val getBuildServiceProvider: Method = Class
	.forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnosticsCollectorKt")
	.getDeclaredMethod("getKotlinToolingDiagnosticsCollectorProvider", Project::class.java)

tasks.withType(kotlinCKGPCEClass).configureEach {
	@Suppress("UNCHECKED_CAST")
	val buildServiceProvider = getBuildServiceProvider.invoke(null, project)
			as Provider<BuildService<*>>
	usesService(buildServiceProvider)
}
