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

	Deps.dagger(project)

	implementation(libs.jackson.dataformat.xml)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.jackson.datatype.java8)
}

// Network
dependencies {
	Deps.ktorClient(project)
}

// Logging
dependencies {
	implementation(libs.slf4j.core)
}

// Test
dependencies {
	Deps.junit5(project)
	testImplementation(libs.test.jfixture)
	testImplementation(libs.test.hamcrest)
	testImplementation(project(":test-helpers"))
	Deps.slf4jToLog4jForTest(project)
}
