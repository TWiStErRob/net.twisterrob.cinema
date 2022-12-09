@file:Suppress("MatchingDeclarationName")

package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.server.config.ApplicationConfig
import java.io.File

enum class Env {
	PRODUCTION,
	TEST,
	DEVELOPMENT
}

val ApplicationConfig.app: ApplicationConfig
	get() = this.config("twisterrob.cinema")

val ApplicationConfig.environment: Env
	get() = this.app.property("environment")
		.let { Env.valueOf(it.getString().uppercase()) }

val ApplicationConfig.staticRootFolder: File
	get() = this.app.property("staticRootFolder")
		.getString()
		.let(::File)

val ApplicationConfig.fakeRootFolder: File
	get() = this.app.property("fakeRootFolder")
		.getString()
		.let(::File)
