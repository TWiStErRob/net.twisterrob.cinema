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

/** Property names: `twisterrob.cinema.*`. */
val ApplicationConfig.app: ApplicationConfig
	get() = this.config("twisterrob.cinema")

/** Property name: `twisterrob.cinema.environment`. */
val ApplicationConfig.environment: Env
	get() = Env.valueOf(this.app.property("environment").getString().uppercase())

/** Property name: `twisterrob.cinema.staticRootFolder`. */
val ApplicationConfig.staticRootFolder: File
	get() = File(this.app.property("staticRootFolder").getString())

/** Property name: `twisterrob.cinema.fakeRootFolder`. */
val ApplicationConfig.fakeRootFolder: File
	get() = File(this.app.property("fakeRootFolder").getString())

fun MapApplicationConfig.putIfAbsent(key: String, value: String) {
	if (key !in keys()) {
		put(key, value)
	}
}
