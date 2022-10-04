package net.twisterrob.cinema.heroku.plugins

import io.gitlab.arturbosch.detekt.Detekt
import net.twisterrob.cinema.heroku.plugins.internal.detekt
import net.twisterrob.cinema.heroku.plugins.internal.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.detekt {
			ignoreFailures = true
			// TODEL https://github.com/detekt/detekt/issues/4926
			buildUponDefaultConfig = false
			allRules = true
			config = project.rootProject.files("config/detekt/detekt.yml")
			baseline = project.rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
			basePath = project.rootProject.projectDir.parentFile.absolutePath

			parallel = true

			project.tasks.withType<Detekt>().configureEach {
				// Target version of the generated JVM bytecode. It is used for type resolution.
				jvmTarget = project.libs.versions.java.get()
				reports {
					html.required.set(true) // human
					xml.required.set(true) // checkstyle
					txt.required.set(true) // console
					// https://sarifweb.azurewebsites.net
					sarif.required.set(true) // GitHub Code Scanning
				}
			}
		}
	}
}
