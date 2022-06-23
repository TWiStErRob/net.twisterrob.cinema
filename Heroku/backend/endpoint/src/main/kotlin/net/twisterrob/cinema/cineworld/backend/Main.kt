package net.twisterrob.cinema.cineworld.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import java.io.File

fun main(vararg args: String) {
	val port = getPort()
	val staticContentPath = if (args.isNotEmpty()) args[0] else null
	val fakeContentPath = if (args.size >= 2) args[1] else null
	embeddedServer(Netty, port) {
		when {
			staticContentPath != null && fakeContentPath != null ->
				configuration(staticRootFolder = File(staticContentPath), fakeRootFolder = File(fakeContentPath))
			staticContentPath != null ->
				configuration(staticRootFolder = File(staticContentPath))
			else ->
				configuration()
		}
		daggerApplication()
	}.start(wait = true)
}

private fun getPort(): Int {
	val port = System.getenv("PORT")
		?: error("PORT environment variable must be defined (=1234) asdfasdf asdf asdf asd fasdfasdf asdf asdf asd fasdf asd fasd fasd fasd fasd fasdf asdfasdf asdfasdf asdfasdfasd fasdfasdfasd fasd fasdf asdfasdfasdf sdafasdfasd fasdf.")
	return port.toInt()
}
