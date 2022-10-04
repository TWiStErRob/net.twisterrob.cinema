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
	implementation(files(libs::class.java.protectionDomain.codeSource.location))
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

detekt {
	// TODEL https://github.com/detekt/detekt/issues/4926
	buildUponDefaultConfig = false
	allRules = true
	config = rootProject.files("../../config/detekt/detekt.yml")
	baseline = rootProject.file("../../config/detekt/detekt-baseline-${project.name}.xml")

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
