@file:Suppress("MatchingDeclarationName")

package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import java.io.File

enum class Env {
	PRODUCTION,
	TEST,
	DEVELOPMENT
}

val ApplicationConfig.app: ApplicationConfig
	get() = this.config("twisterrob.cinema")

val ApplicationConfig.environment: Env
	get() = Env.valueOf(this.app.property("environment").getString().uppercase())

val ApplicationConfig.staticRootFolder: File
	get() = File(this.app.property("staticRootFolder").getString())

val ApplicationConfig.fakeRootFolder: File
	get() = File(this.app.property("fakeRootFolder").getString())

fun MapApplicationConfig.putIfAbsent(key: String, value: String) {
	if (key !in keys()) {
		put(key, value)
	}
}
