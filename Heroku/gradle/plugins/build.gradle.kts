plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
	alias(libs.plugins.detekt)
}

repositories {
	mavenCentral()
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
	lockFile.set(file("../../gradle/dependency-locks/plugins.lockfile"))
}

gradlePlugin {
	plugins {
		create("net.twisterrob.cinema.heroku.plugins.settings") {
			id = "net.twisterrob.cinema.heroku.plugins.settings"
			implementationClass = "net.twisterrob.cinema.heroku.plugins.SettingsPlugin"
		}
		create("net.twisterrob.cinema.heroku.plugins.detekt") {
			id = "net.twisterrob.cinema.heroku.plugins.detekt"
			implementationClass = "net.twisterrob.cinema.heroku.plugins.DetektPlugin"
		}
	}
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
	config = rootProject.files("../../config/detekt/detekt.yml")
	baseline = rootProject.file("../../config/detekt/detekt-baseline-${project.name}.xml")
	basePath = rootProject.projectDir.parentFile.parentFile.parentFile.absolutePath

	parallel = true

	tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
		reports {
			html.required.set(true) // human
			xml.required.set(true) // checkstyle
			txt.required.set(true) // console
			// https://sarifweb.azurewebsites.net
			sarif.required.set(true) // GitHub Code Scanning
		}
	}
}

val detektReportMergeTask = rootProject.tasks.register<io.gitlab.arturbosch.detekt.report.ReportMergeTask>("detektReportMergeSarif") {
	output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
	reports {
		// https://sarifweb.azurewebsites.net
		sarif.required.set(true) // GitHub Code Scanning
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
	// Note: this includes :detekt which will run without type resolution, that's an accepted hit for simplicity.
	dependsOn(tasks.withType<io.gitlab.arturbosch.detekt.Detekt>())
}

val isCI: Boolean
	get() = System.getenv("GITHUB_ACTIONS") == "true"
