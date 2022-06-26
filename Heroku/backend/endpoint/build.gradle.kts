import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java")
	id("application")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

application {
	publishSlimJar()
	mainClass.set("net.twisterrob.cinema.cineworld.backend.MainKt")
	tasks.named<JavaExec>("run") {
		jvmArgs(
			"-Dlog4j.configurationFile=log4j2.xml,log4j2-sync.xml"
		)
		// Can be overridden with `gradlew :backend:endpoint:run --args <static-folder> <fake-folder>`.
		args(
			// The folder that :frontend builds into.
			"../../deploy/static",
			// The folder that is looked at for debug builds for fake data for reproducible testing.
			"../src/test/fake"
		)
	}
}

dependencies {
	implementation(project(":backend:database"))
	implementation(project(":backend:quickbook"))
	implementation(project(":backend:network"))

	implementation(libs.kotlin.stdlib8)

	implementation(libs.ktor.server.core)
	implementation(libs.ktor.server.locations)
	implementation(libs.ktor.server.engine.netty)
	implementation(libs.ktor.server.content.jackson)
	implementation(libs.ktor.server.content.html)

	runtimeOnly(libs.ktor.client.engine.okhttp)
	implementation(libs.ktor.client.jackson)
	Deps.slf4jToLog4j(project)
	Deps.dagger(project)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(libs.test.jsonAssert)
	testImplementation(libs.test.shazamcrest) {
		// Exclude JUnit 4, we're on JUnit 5, no need for old annotations and classes
		// Except for ComparisonFailure, which is provided by :test-helpers.
		exclude(group = libs.test.junit.vintage.get().module.group, module = libs.test.junit.vintage.get().module.name)
	}
	testImplementation(libs.test.mockito.inline)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(project(":test-helpers"))
	testImplementation(testFixtures(project(":backend:database")))

	testImplementation(libs.jackson.module.kotlin)
	testImplementation(libs.jackson.datatype.java8)
	testImplementation(libs.ktor.client.mock.jvm)
	testImplementation(libs.ktor.server.test) {
		exclude(group = "ch.qos.logback", module = "logback-classic")
	}
	kaptTest(libs.dagger.apt)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = freeCompilerArgs + listOf(
			"-opt-in=io.ktor.locations.KtorExperimentalLocationsAPI"
		)
	}
}
