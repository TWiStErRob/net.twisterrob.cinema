plugins {
	id("java-library")
	id("java-test-fixtures")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.Dagger2.default(project)
}

// Graph database
dependencies {
	api(Deps.Jackson.databind) // override to equalize versions
	api(Deps.Neo4JOGM.core)
	implementation(Deps.Neo4JOGM.driver)
	runtimeOnly(Deps.Neo4JOGM.driver_bolt)
	runtimeOnly(Deps.Neo4JOGM.driver_bolt_native_types)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.JFixture.core)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(Deps.Hamcrest.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = "junit", module = "junit")
	}
	testImplementation(project(":test-helpers"))

	testImplementation(Deps.Neo4JOGM.harness) {
		exclude(group = "org.slf4j", module = "slf4j-nop")
	}

	Deps.Log4J2.slf4jForTest(project)

	testFixturesImplementation(project(":backend:quickbook"))
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(Deps.JFixture.core)
	testFixturesImplementation(libs.test.junit.jupiter)
	testFixturesImplementation(project(":test-helpers"))
}
