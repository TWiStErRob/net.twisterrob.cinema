plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
	alias(libs.plugins.detekt)
	id("org.gradle.idea")
}

dependencies {
	api(libs.kotlin.gradle)
	api(libs.kotlin.gradle.ksp)
	implementation(libs.kotlin.serialization.gradle)
	api(libs.detekt.gradle)

	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

dependencyLocking {
	lockAllConfigurations()
	lockFile = file("../../gradle/dependency-locks/plugins.lockfile")
}

detekt {
	ignoreFailures = isCI
	buildUponDefaultConfig = true
	allRules = true
	config.setFrom(rootProject.file("../../config/detekt/detekt.yml"))
	baseline = rootProject.file("../../config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.parentFile.parentFile.parentFile
	// TODO doesn't work, "detektMain" is disabled because https://github.com/detekt/detekt/issues/5501.
	// > Execution failed for task ':plugins:detektMain'.
	// > > Front-end Internal error: Failed to analyze declaration gradle_plugins_src_main_kotlin_net_twisterrob_cinema_build_compilation_gradle
	// > File being compiled: (4,49) in gradle\plugins\src\main\kotlin\net\twisterrob\cinema\build\compilation.gradle.kts
	// > The root cause org.jetbrains.kotlin.resolve.lazy.NoDescriptorForDeclarationException was thrown at:
	// > org.jetbrains.kotlin.resolve.lazy.BasicAbsentDescriptorHandler.diagnoseDescriptorNotFound(AbsentDescriptorHandler.kt:18)
	//source.setFrom(source.asFileTree.filter { file -> !file.name.endsWith(".gradle.kts") })

	parallel = true

	tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
		// Exclude generated sources from analysis to avoid false positives
		// The trivial `exclude("**/build/generated-sources/**/*.kt")` doesn't work,
		// because the trees are rooted at the "kotlin" folder, so exclude known patterns:
		exclude("gradle/kotlin/dsl/accessors/")
		exclude("net/twisterrob/cinema/build/*Plugin.kt")
		exclude("Net_twisterrob_cinema_*.kt")
		// UnnecessaryFullyQualifiedName because of implicitly imported extensions (kotlin.target.compilations.* looks like FQFN)
		exclude("net/twisterrob/cinema/build/testing.gradle.kts")
		reports {
			html.required = true // human
			checkstyle.required = true // checkstyle
			markdown.required = true // console
			// https://sarifweb.azurewebsites.net
			sarif.required = true // GitHub Code Scanning
		}
	}
}

val detektReportMergeTask = rootProject.tasks.register<dev.detekt.gradle.report.ReportMergeTask>("detektReportMergeSarif") {
	val detektReportMergeTask = this@register
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
	tasks.withType<dev.detekt.gradle.Detekt> { // Intentionally eager. When running report, we must configure all tasks.
		detektReportMergeTask.mustRunAfter(this)
		detektReportMergeTask.input.from(this.reports.sarif.outputLocation)
	}

}
tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
	reports {
		// https://sarifweb.azurewebsites.net
		sarif.required = true // GitHub Code Scanning
	}
}


// Expose :detektEach for included build to easily run all Detekt tasks.
tasks.register("detektEach") {
	// Note: this includes :detekt which will run without type resolution, that's an accepted hit,
	// because it's not possible to use `kotlin-dsl` otherwise, see detekt { source } above.
	dependsOn(tasks.withType<dev.detekt.gradle.Detekt>().named { it != "detektMain" })
}

val isCI: Boolean
	get() = System.getenv("GITHUB_ACTIONS") == "true"

idea {
	module {
		// TODO/REPORT not working.
		fun excludeGenerated(dir: String) {
			val generated = layout.buildDirectory.dir("build/generated-sources")
			excludeDirs.add(generated.map { it.dir(dir).dir("kotlin/gradle/kotlin/dsl") }.get().asFile)
		}
		excludeGenerated("kotlin-dsl-accessors")
		excludeGenerated("kotlin-dsl-external-plugin-spec-builders")
		excludeGenerated("kotlin-dsl-plugins")
	}
}
