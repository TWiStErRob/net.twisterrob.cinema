@Suppress("MatchingDeclarationName")

package net.twisterrob.cinema.cineworld.backend.ktor

import io.ktor.config.ApplicationConfig

enum class Env {
	PRODUCTION,
	TEST,
	DEVELOPMENT
}

val ApplicationConfig.environment: Env
	get() = this.propertyOrNull("environment")
		?.let { Env.valueOf(it.getString().uppercase()) }
		?: Env.DEVELOPMENT
