plugins {
	`kotlin-dsl`
	id("io.gitlab.arturbosch.detekt") version "1.18.1"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.32")
	implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.18.1")
}

detekt {
	buildUponDefaultConfig = true
	allRules = true
	config = rootProject.files("../config/detekt/detekt.yml")
	baseline = rootProject.file("../config/detekt/detekt-baseline-${project.name}.xml")

	parallel = true

	reports {
		html.enabled = true // human
		xml.enabled = true // checkstyle
		txt.enabled = true // console
		// https://sarifweb.azurewebsites.net
		sarif.enabled = true // Github Code Scanning
	}
}
