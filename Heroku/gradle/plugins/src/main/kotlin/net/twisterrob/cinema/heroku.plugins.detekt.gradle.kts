package net.twisterrob.cinema

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import isCI
import net.twisterrob.cinema.heroku.plugins.internal.libs
import net.twisterrob.cinema.heroku.plugins.internal.maybeRegister

plugins {
	id("io.gitlab.arturbosch.detekt")
}

		detekt {
			ignoreFailures = isCI
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
				// Detekt false positive: potential-bugs - Deprecation - [reports is deprecated.]
				// https://github.com/detekt/detekt/issues/5328#issuecomment-1272534271
				// Reason: kotlin-dsl transforms configureEach(Action) to have a receiver, but Detekt doesn't see this,
				// so it resolves to DetektExtension.reports (from project.detekt) instead of Detekt.reports(Action).
				@Suppress("Deprecation", "KotlinRedundantDiagnosticSuppress")
				reports {
					html.required = true // human
					xml.required = true // checkstyle
					txt.required = true // console
					// https://sarifweb.azurewebsites.net
					sarif.required = true // GitHub Code Scanning
				}
			}
		}
		project.configureSarifMerging()

fun Project.configureSarifMerging() {
	check(this != rootProject) { "Sarif merging cannot be applied on root project." }
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeSarif") {
		output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
	}
	tasks.withType<Detekt>().configureEach {
		@Suppress("NestedScopeFunctions") // False positive due to kotlin-dsl https://github.com/detekt/detekt/issues/5328#issuecomment-1272534271.
		reports {
			// https://sarifweb.azurewebsites.net
			sarif.required = true // GitHub Code Scanning
		}
	}
	rootProject.tasks.named<ReportMergeTask>("detektReportMergeSarif") {
		val detektReportMergeTask = this@named
		tasks.withType<Detekt> {
			val detektReportingTask = this@withType
			detektReportMergeTask.mustRunAfter(detektReportingTask)
			detektReportMergeTask.input.from(detektReportingTask.sarifReportFile)
		}
		gradle.includedBuilds.forEach { includedBuild ->
			detektReportMergeTask.dependsOn(includedBuild.task(":detektReportMergeSarif"))
			detektReportMergeTask.input.from(includedBuild.projectDir.resolve("build/reports/detekt/merge.sarif"))
		}
	}
}
