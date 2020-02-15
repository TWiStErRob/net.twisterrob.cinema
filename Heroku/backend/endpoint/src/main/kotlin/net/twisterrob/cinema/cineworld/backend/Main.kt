package net.twisterrob.cinema.cineworld.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication

fun main(vararg args: String) {
	embeddedServer(Netty, 8080) {
		configuration()
		daggerApplication()
	}.start(wait = true)
}
