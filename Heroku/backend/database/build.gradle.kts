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
	api(libs.jackson.databind) // override to equalize versions
	api(libs.neo4j.ogm)
	implementation(libs.neo4j.ogm.driver)
	runtimeOnly(libs.neo4j.ogm.driver.bolt)
	runtimeOnly(libs.neo4j.ogm.driver.bolt.nativeTypes)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(libs.test.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
	}
	testImplementation(project(":test-helpers"))

	testImplementation(libs.neo4j.harness) {
		exclude(group = libs.slf4j.nop.get().module.group, module = libs.slf4j.nop.get().module.name)
	}

	Deps.Log4J2.slf4jForTest(project)

	testFixturesImplementation(project(":backend:quickbook"))
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(libs.test.jfixture)
	testFixturesImplementation(libs.test.junit.jupiter)
	testFixturesImplementation(project(":test-helpers"))
}
