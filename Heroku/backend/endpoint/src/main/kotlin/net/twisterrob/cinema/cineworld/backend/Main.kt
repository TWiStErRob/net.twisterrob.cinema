package net.twisterrob.cinema.cineworld.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import java.io.File

fun main(vararg args: String) {
	val port = System.getProperty("PORT", "8080").toInt()
	val staticContentPath = if (args.isNotEmpty()) args[0] else null
	embeddedServer(Netty, port) {
		if (staticContentPath != null) {
			configuration(staticRootFolder = File(staticContentPath))
		} else {
			configuration()
		}
		daggerApplication()
	}.start(wait = true)
}
