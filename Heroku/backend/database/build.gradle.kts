plugins {
	id("net.twisterrob.cinema.library")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)
	implementation(projects.shared)

	Deps.dagger(project)
}

// Graph database
dependencies {
	api(libs.jackson.databind) // override to equalize versions
	api(libs.neo4j.ogm)
	implementation(libs.neo4j.ogm.driver)
	runtimeOnly(libs.neo4j.ogm.driver.bolt)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(libs.test.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
	}
	testImplementation(projects.testHelpers)

	testImplementation(libs.neo4j)
	testImplementation(libs.neo4j.harness)

	Deps.slf4jToLog4jForTest(project)

	testFixturesImplementation(projects.backend.quickbook)
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(libs.test.jfixture)
	testFixturesImplementation(libs.test.junit.jupiter)
	testFixturesImplementation(projects.testHelpers)
}
