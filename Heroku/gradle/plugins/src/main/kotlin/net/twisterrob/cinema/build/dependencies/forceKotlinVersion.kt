package net.twisterrob.cinema.build.dependencies

import net.twisterrob.cinema.build.dsl.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.forceKotlinVersion() {
	afterEvaluate {
		dependencies {
			if ("implementation" in configurations.names) {
				add("implementation", platform(libs.kotlin.bom))
			}
		}
	}
}
