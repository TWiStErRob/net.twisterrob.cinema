plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
	id("io.gitlab.arturbosch.detekt")
}

sourceSets {
	test {
		resources.srcDir(project(":backend:sync").file("test"))
	}
}

dependencies {
	implementation(libs.kotlin.stdlib8)
	implementation(libs.kotlin.reflect)

	Deps.Dagger2.default(project)

	implementation(libs.jackson.dataformat.xml)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.jackson.datatype.java8)
}

// Network
dependencies {
	Deps.Ktor.client.default(project)
}

// Logging
dependencies {
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
