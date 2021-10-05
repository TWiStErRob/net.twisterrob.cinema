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
	implementation(Deps.Kotlin.core)
	implementation(Deps.Kotlin.reflect)

	Deps.Dagger2.default(project)

	implementation(Deps.Jackson.dataformat_xml)
	implementation(Deps.Jackson.module_kotlin)
	implementation(Deps.Jackson.datatype_java8)
}

// Network
dependencies {
	Deps.Ktor.client.default(project)
}

// Logging
dependencies {
	implementation(Deps.SLF4J.core)
}

// Test
dependencies {
	Deps.JUnit.junit5(project)
	testImplementation(Deps.JFixture.core)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(project(":test-helpers"))
	Deps.Log4J2.slf4jForTest(project)
}
