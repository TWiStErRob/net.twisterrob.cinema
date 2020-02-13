plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
	id("org.jetbrains.kotlin.kapt")
}

sourceSets {
	test {
		resources.srcDir(project(":backend:sync").file("test"))
	}
}

dependencies {
	implementation(Deps.Kotlin.core)
	implementation(Deps.Kotlin.reflect)

	implementation(Deps.Dagger2.core)
	kapt(Deps.Dagger2.apt)

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
	testImplementation(Deps.JFixture.jfixture)
	testImplementation(Deps.Hamcrest.core)
	testImplementation(project(":test-helpers"))
}
