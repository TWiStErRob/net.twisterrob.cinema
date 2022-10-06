package net.twisterrob.cinema.cineworld.backend.endpoint

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import net.twisterrob.cinema.cineworld.backend.ktor.ServerLogging
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import org.slf4j.Logger

/**
 * @param configure A call to [configuration], but some tests will need to pass in an optional argument.
 * @param daggerApp A call to [daggerApplication], but some tests may decide to call with a custom [dagger.Component].
 * @param logLevel How much logging should be done.
 * @param test Test code to execute after the application has started up.
 */
fun endpointTest(
	configure: Application.() -> Unit = { configuration() },
	daggerApp: Application.() -> Unit = { daggerApplication() },
	logLevel: ServerLogging.LogLevel = ServerLogging.LogLevel.ALL,
	test: TestApplicationEngine.() -> Unit
) {
	@Suppress("DEPRECATION") // TODO https://github.com/TWiStErRob/net.twisterrob.cinema/issues/167
	withApplication(
		environment = createTestEnvironment {
			developmentMode = true
			val originalLog = log
			log = object : Logger by originalLog {

				override fun debug(msg: String?) {
					@Suppress("UseIfInsteadOfWhen")
					when {
						// Don't show very long classpath on test startup.
						msg?.startsWith("Class Loader: ") == true -> Unit
						else -> originalLog.debug(msg)
					}
				}

				override fun info(msg: String?) {
					@Suppress("UseIfInsteadOfWhen")
					when (msg) {
						"No ktor.deployment.watch patterns specified, automatic reload is not active" -> Unit
						else -> originalLog.info(msg)
					}
				}
			}
		}
	) {
		application.install(ServerLogging) {
			logger = application.log
			level = logLevel
		}
		application.apply {
			configure()
			daggerApp()
			try {
				log.trace("Endpoint test starting {}", test::class)
				test()
			} finally {
				log.trace("Endpoint test finished {}", test::class)
			}
		}
	}
}
