package net.twisterrob.cinema.heroku.plugins

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import net.twisterrob.cinema.heroku.plugins.internal.detekt
import net.twisterrob.cinema.heroku.plugins.internal.libs
import net.twisterrob.cinema.heroku.plugins.internal.maybeRegister
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

class DetektPlugin : Plugin<Project> {

	override fun apply(project: Project) {
		project.plugins.apply("io.gitlab.arturbosch.detekt")
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
		project.configureSarifMerging()
	}
}

private fun Project.configureSarifMerging() {
	check(this != rootProject) { "Sarif merging cannot be applied on root project." }
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeSarif") {
		output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
	}
	tasks.withType<Detekt>().configureEach {
		reports {
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // Github Code Scanning
		}
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.sarifReportFile)
		}
	}
}
