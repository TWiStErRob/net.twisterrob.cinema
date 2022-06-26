plugins {
	`kotlin-dsl`
	alias(libs.plugins.detekt)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.kotlin.gradle)
	implementation(libs.detekt.gradle)

	// TODEL https://github.com/gradle/gradle/issues/15383
	implementation(files(libs::class.java.protectionDomain.codeSource.location))
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
