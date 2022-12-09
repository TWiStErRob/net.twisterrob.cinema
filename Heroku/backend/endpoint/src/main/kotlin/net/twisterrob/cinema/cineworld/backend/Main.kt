package net.twisterrob.cinema.cineworld.backend

import io.ktor.server.application.Application
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.stop
import io.ktor.server.netty.NettyApplicationEngine
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import java.util.concurrent.TimeUnit

fun main(vararg args: String) {
	// Same as io.ktor.server.netty.EngineMain.main(args.toList().toTypedArray()), but with extra module:
	val applicationEnvironment = commandLineEnvironment(args.toList().toTypedArray()) {
		modules.add {
			// TODO re-wire to use proper configuration.
			// Note: can't use YAML because of this lambda, but can use HOCON.
			configuration()
		}
		// Note: because this has to be second, cannot use ktor.application.modules.
		modules.add(Application::daggerApplication)
	}
	val engine = NettyApplicationEngine(applicationEnvironment)
	engine.addShutdownHook {
		@Suppress("MagicNumber")
		engine.stop(3, 5, TimeUnit.SECONDS)
	}
	engine.start(true)
}
