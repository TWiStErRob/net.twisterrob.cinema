plugins {
	`kotlin-dsl`
	id("io.gitlab.arturbosch.detekt") version "1.20.0"
}

repositories {
	mavenCentral()
}

dependencies {
	// TODO Review validation.mode in gradle.properties when bumping version.
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
	implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.20.0")
}

detekt {
	buildUponDefaultConfig = true
	allRules = true
	config = rootProject.files("../config/detekt/detekt.yml")
	baseline = rootProject.file("../config/detekt/detekt-baseline-${project.name}.xml")

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
