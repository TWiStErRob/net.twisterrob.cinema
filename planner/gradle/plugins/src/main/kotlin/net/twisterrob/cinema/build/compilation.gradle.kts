package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
	id("org.gradle.java")
	id("org.jetbrains.kotlin.jvm")
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
		
		// Enable flag to help suppress compiler warnings.
		freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
	}
}
