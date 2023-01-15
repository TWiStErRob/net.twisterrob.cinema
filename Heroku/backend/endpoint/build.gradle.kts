plugins {
	id("java")
	id("application")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("org.jetbrains.kotlin.plugin.serialization")
	id("net.twisterrob.cinema.heroku.plugins.detekt")
}

application {
	publishSlimJar()
	mainClass.set("io.ktor.server.netty.EngineMain")
	tasks.named<JavaExec>("run") {
		jvmArgs(
			"-Dlog4j.configurationFile=log4j2.xml,log4j2-sync.xml"
		)
		// Can be overridden with `gradlew :backend:endpoint:run --args="-P:twisterrob.cinema.staticRootFolder=<static-folder> -P:twisterrob.cinema.fakeRootFolder=<fake-folder>"`.
		args(
			// The folder that :frontend builds into.
			"-P:twisterrob.cinema.staticRootFolder=../../deploy/frontend/static",
			// The folder that is looked at for debug builds for fake data for reproducible testing.
			"-P:twisterrob.cinema.fakeRootFolder=../src/test/fake"
		)
	}
}

dependencies {
	implementation(projects.backend.database)
	implementation(projects.backend.quickbook)
	implementation(projects.backend.network)

	implementation(libs.kotlin.stdlib8)
	testImplementation(libs.kotlin.stdlib8)

	implementation(libs.ktor.server)
	implementation(libs.ktor.server.auth)
	implementation(libs.ktor.server.content)
	implementation(libs.ktor.serialization.jackson)
	implementation(libs.ktor.server.logging)
	implementation(libs.ktor.server.caching)
	implementation(libs.ktor.server.compression)
	implementation(libs.ktor.server.headers)
	implementation(libs.ktor.server.status)
	implementation(libs.ktor.server.resources)
	implementation(libs.ktor.server.engine.netty)
	implementation(libs.ktor.server.content.html)

	runtimeOnly(libs.ktor.client.engine.okhttp)
	implementation(libs.ktor.client.content)
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
	testImplementation(libs.test.mockito)
	testImplementation(libs.test.mockito.kotlin)
	testImplementation(libs.test.mockito.jupiter)
	testImplementation(projects.testHelpers)
	testImplementation(testFixtures(projects.backend.database))

	testImplementation(libs.jackson.module.kotlin)
	testImplementation(libs.jackson.datatype.java8)
	testImplementation(libs.kotlinx.serialization.json)
	testImplementation(libs.ktor.client.mock)
	testImplementation(libs.ktor.server.test)
	kaptTest(libs.dagger.apt)
}
