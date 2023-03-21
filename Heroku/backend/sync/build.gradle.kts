import Deps.junit5
import net.twisterrob.cinema.build.testing.kapt

plugins {
	id("net.twisterrob.cinema.application")
}

application {
	mainClass = "net.twisterrob.cinema.cineworld.sync.Main"
	tasks.named<JavaExec>("run") {
		jvmArgs(
			if (project.property("net.twisterrob.run.verboseSync").toString().toBoolean())
				"-Dlog4j.configurationFile=log4j2.xml"
			else
				"-Dlog4j.configurationFile=log4j2.xml,log4j2-sync.xml"
		)
		// Can be overridden with `gradlew :backend:sync:run --args="<sync-type...>"`.
		args(
			// Types of entities synchronized from syndication.
			"cinemas", "films", "performances"
		)
	}
}

tasks.register<JavaExec>("generate") {
	dependsOn(tasks.jar)
	classpath(sourceSets["main"].runtimeClasspath)
	mainClass = "net.twisterrob.cinema.cineworld.generate.Main"
	args("test/weekly_film_times.xml")
}

dependencies {
	implementation(projects.backend.database)
	implementation(projects.backend.feed)
	implementation(projects.backend.network)

	implementation(libs.kotlin.stdlib8)
	runtimeOnly(libs.ktor.client.engine.okhttp)
	Deps.slf4jToLog4j(project)
	Deps.dagger(project)
}

// Test
dependencies {
	testFixturesImplementation(projects.backend.database)
	testFixturesImplementation(projects.testHelpers)
	testFixturesImplementation(libs.kotlin.stdlib8)
	testFixturesImplementation(libs.test.jfixture)
}

configurations.all {
	//resolutionStrategy.failOnVersionConflict()
}

testing {
	suites.withType<JvmTestSuite>().configureEach {
		configurations.named(sources.implementationConfigurationName)
			.configure { extendsFrom(configurations.implementation.get()) }
		dependencies {
			junit5(project)
			implementation(libs.test.jfixture)
			implementation(libs.test.hamcrest)
			implementation(libs.test.shazamcrest) {
				// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
				// Except for ComparisonFailure, which is provided by :test-helpers.
				exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
			}
			implementation(libs.test.mockito)
			implementation(libs.test.mockito.kotlin)
			implementation(projects.testHelpers)
			implementation(testFixtures(projects.backend.database))

			implementation(libs.jackson.module.kotlin)
			implementation(libs.jackson.datatype.java8)
			implementation(libs.neo4j)
			implementation(libs.neo4j.harness)
			kapt(project.libs.dagger.apt)
		}
	}
}
