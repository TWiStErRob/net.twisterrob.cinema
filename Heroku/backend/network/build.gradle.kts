plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm")
}

dependencies {
	implementation(Deps.Kotlin.core)
	implementation(Deps.Ktor.client.client)
	implementation(Deps.Ktor.client.logging_jvm)
	Deps.Log4J2.slf4j(project)
}
