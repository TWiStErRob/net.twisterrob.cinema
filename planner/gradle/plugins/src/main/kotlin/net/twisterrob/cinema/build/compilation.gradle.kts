package net.twisterrob.cinema.build

import net.twisterrob.cinema.build.dsl.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("org.gradle.java")
	id("org.jetbrains.kotlin.jvm")
}

java {
	sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.fromTarget(libs.versions.java.get())
		allWarningsAsErrors = true
		verbose = true

		// Opt in to Context Parameters.
		freeCompilerArgs.add("-Xcontext-parameters")

		// Enable flag to help suppress compiler warnings.
		freeCompilerArgs.add("-Xrender-internal-diagnostic-names")

		// Kotlin 2.2: ANNOTATION_WILL_BE_APPLIED_ALSO_TO_PROPERTY_OR_FIELD
		// > This annotation is currently applied to the value parameter only,
		// > but in the future it will also be applied to field.
		// > - To opt in to applying to both value parameter and field,
		// >   add '-Xannotation-default-target=param-property' to your compiler arguments.
		// > - To keep applying to the value parameter only, use the '@param:' annotation target.
		// > See https://youtrack.jetbrains.com/issue/KT-73255 for more details.
		freeCompilerArgs.add("-Xannotation-default-target=param-property")
	}
}
