plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
	alias(libs.plugins.detekt)
	id("org.gradle.idea")
}

dependencies {
	api(libs.kotlin.gradle)
	implementation(libs.kotlin.serialization.gradle)
	api(libs.detekt.gradle)

	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

dependencyLocking {
	lockAllConfigurations()
	lockFile = file("../../gradle/dependency-locks/plugins.lockfile")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
		freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
	}
}

detekt {
	ignoreFailures = isCI
	// TODEL https://github.com/detekt/detekt/issues/4926
	buildUponDefaultConfig = false
	allRules = true
	config.setFrom(rootProject.file("../../config/detekt/detekt.yml"))
	baseline = rootProject.file("../../config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.parentFile.parentFile.parentFile.absolutePath
	// REPORT doesn't work, "detektMain" is disabled below.
	// > Execution failed for task ':plugins:detektMain'.
	// > > Front-end Internal error: Failed to analyze declaration P__projects_workspace_net_twisterrob_cinema_Heroku_gradle_plugins_src_main_kotlin_net_twisterrob_cinema_build_compilation_gradle
	// > File being compiled: (4,49) in /P:\projects\workspace\net.twisterrob.cinema\Heroku\gradle\plugins\src\main\kotlin\net\twisterrob\cinema\build\compilation.gradle.kts
	// > The root cause org.jetbrains.kotlin.resolve.lazy.NoDescriptorForDeclarationException was thrown at: org.jetbrains.kotlin.resolve.lazy.BasicAbsentDescriptorHandler.diagnoseDescriptorNotFound(AbsentDescriptorHandler.kt:18)
	//source = files(source.asFileTree.filter { !it.name.endsWith(".gradle.kts") })

	parallel = true

	tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
		reports {
			html.required = true // human
			xml.required = true // checkstyle
			txt.required = true // console
			// https://sarifweb.azurewebsites.net
			sarif.required = true // GitHub Code Scanning
		}
	}
}

val detektReportMergeTask = rootProject.tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output = rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif")
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	reports {
		// https://sarifweb.azurewebsites.net
		sarif.required = true // GitHub Code Scanning
	}
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
	val detektReportingTask = this@withType
	detektReportMergeTask.configure {
		mustRunAfter(detektReportingTask)
		input.from(detektReportingTask.sarifReportFile)
	}
}

// Expose :detektEach for included build to easily run all Detekt tasks.
tasks.register("detektEach") {
	// TODO see why detektMain is disabled at detekt.source.
	// Note: this includes :detekt which will run without type resolution, that's an accepted hit for simplicity.
	dependsOn(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().matching { it.name != "detektMain" })
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

// TODEL Workaround for https://github.com/gradle/gradle/issues/26981
@Suppress("UNCHECKED_CAST")
tasks.withType(
	Class.forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.CheckKotlinGradlePluginConfigurationErrors")
			as Class<Task>
).configureEach {
	usesService(
		Class
			.forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnosticsCollectorKt")
			.getDeclaredMethod("getKotlinToolingDiagnosticsCollectorProvider", Project::class.java)
			.invoke(null, project) as Provider<BuildService<*>>
	)
}
