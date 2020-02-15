package net.twisterrob.cinema.cineworld.backend.endpoint

import io.ktor.application.log
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import org.slf4j.Logger

fun endpointTest(test: TestApplicationEngine.() -> Unit) {
	withApplication(
		environment = createTestEnvironment {
			val originalLog = log
			log = object : Logger by originalLog {
				override fun info(msg: String?) {
					when (msg) {
						"No ktor.deployment.watch patterns specified, automatic reload is not active" -> Unit
						else -> originalLog.info(msg)
					}
				}
			}
		}
	) {
		application.apply {
			configuration()
			daggerApplication()
			log.trace("Endpoint test starting {}", test::class)
			test()
			log.trace("Endpoint test finished {}", test::class)
		}
	}
}
