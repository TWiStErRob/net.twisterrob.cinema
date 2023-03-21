package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins.withId("java") {
	configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
		targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
	}
}

plugins.withId("org.jetbrains.kotlin.jvm") {
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		compilerOptions {
			jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
			allWarningsAsErrors = true
			verbose = true
		}
	}
}
