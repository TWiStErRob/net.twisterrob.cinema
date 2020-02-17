package net.twisterrob.cinema.cineworld.backend.endpoint

import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import org.slf4j.Logger
import java.io.File

/**
 * @param daggerApp A call to [daggerApplication], but some tests may decide to call with a custom [dagger.Component].
 * @param configure A call to [configuration], but some tests will need to pass in an optional argument.
 * @param test Test code to execute after the application has started up.
 */
fun endpointTest(
	configure: Application.() -> Unit = { configuration() },
	daggerApp: Application.() -> Unit = { daggerApplication() },
	test: TestApplicationEngine.() -> Unit
) {
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
			configure()
			daggerApp()
			log.trace("Endpoint test starting {}", test::class)
			test()
			log.trace("Endpoint test finished {}", test::class)
		}
	}
}
