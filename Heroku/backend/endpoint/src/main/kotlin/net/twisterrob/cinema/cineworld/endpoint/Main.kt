package net.twisterrob.cinema.cineworld.endpoint

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(vararg args: String) {
	embeddedServer(Netty, 8080) {
		configuration()
		endpoints()
	}.start(wait = true)
}
