package net.twisterrob.cinema.build

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.report.ReportMergeTask
import net.twisterrob.cinema.build.dsl.isCI
import net.twisterrob.cinema.build.dsl.libs
import net.twisterrob.cinema.build.dsl.maybeRegister

plugins {
	id("dev.detekt")
}

detekt {
	ignoreFailures = isCI
	buildUponDefaultConfig = false
	allRules = true
	config.setFrom(rootProject.file("config/detekt/detekt.yml"))
	baseline = rootProject.file("config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.parentFile

	parallel = true

	tasks.withType<Detekt>().configureEach {
		// Target version of the generated JVM bytecode. It is used for type resolution.
		jvmTarget = libs.versions.java.get()
		reports {
			html.required = true // human
			checkstyle.required = true // checkstyle
			markdown.required = true // console
			// https://sarifweb.azurewebsites.net
			sarif.required = true // GitHub Code Scanning
		}
	}
}

tasks.register("detektEach") {
	dependsOn(tasks.withType<Detekt>().named { it != "detekt" })
}

configureSarifMerging()

fun Project.configureSarifMerging() {
	check(this != rootProject) { "Sarif merging cannot be applied on root project." }
	rootProject.tasks.maybeRegister<ReportMergeTask>("detektReportMergeSarif") {
		output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
	}
	tasks.withType<Detekt>().configureEach {
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
			detektReportMergeTask.input.from(detektReportingTask.reports.sarif.outputLocation)
		}
		gradle.includedBuilds.forEach { includedBuild ->
			detektReportMergeTask.dependsOn(includedBuild.task(":detektReportMergeSarif"))
			detektReportMergeTask.input.from(includedBuild.projectDir.resolve("build/reports/detekt/merge.sarif"))
		}
	}
}
