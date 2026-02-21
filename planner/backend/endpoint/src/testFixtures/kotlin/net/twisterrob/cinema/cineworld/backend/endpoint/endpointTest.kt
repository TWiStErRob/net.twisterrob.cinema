package net.twisterrob.cinema.cineworld.backend.endpoint

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ClientProvider
import io.ktor.server.testing.TestApplication
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.twisterrob.cinema.cineworld.backend.app.ApplicationAttributes.testInstance
import net.twisterrob.cinema.cineworld.backend.ktor.ServerLogging
import net.twisterrob.cinema.cineworld.backend.ktor.autoDaggerApplication
import net.twisterrob.cinema.cineworld.backend.ktor.configuration
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.cinema.cineworld.backend.ktor.putIfAbsent

/**
 * @param configure A call to [configuration], but some tests will need to pass in an optional argument.
 * @param daggerApp A call to [daggerApplication], but some tests may decide to call with a custom [dagger.Component].
 * @param logLevel How much logging should be done.
 * @param testConfig Configuration overrides for the ktor application. See `src/main/resources/application.conf`.
 * @param test Test code to execute after the application has started up.
 */
fun Any.endpointTest(
	configure: Application.() -> Unit = { configuration() },
	daggerApp: Application.() -> Unit = { autoDaggerApplication() },
	logLevel: ServerLogging.LogLevel = ServerLogging.LogLevel.ALL,
	testConfig: Map<String, String> = emptyMap(),
	test: suspend ClientProvider.() -> Unit
) {
	val testLog = KtorSimpleLogger("ktor.test")
	val application = TestApplication {
		application {
			intercept(ApplicationCallPipeline.Monitoring) {
				call.attributes.testInstance = this@endpointTest
				proceed()
			}
		}
		environment {
			config = MapApplicationConfig(testConfig.entries.map { it.key to it.value }).apply {
				putIfAbsent("twisterrob.cinema.environment", "test")
				putIfAbsent("twisterrob.cinema.fakeRootFolder", ".")
				putIfAbsent("twisterrob.cinema.staticRootFolder", ".")
			}
			log = testLog
		}
		serverConfig {
			developmentMode = true
		}
		install(ServerLogging) {
			logger = testLog
			level = logLevel
		}
		application(configure)
		application(daggerApp)
	}
	runBlocking(application.client.engine.coroutineContext) {
		application.start()
		try {
			testLog.trace("Endpoint test starting {}", test::class)
			application.test()
		} finally {
			testLog.trace("Endpoint test finished {}", test::class)
			withContext(NonCancellable) { application.stop() }
		}
	}
}
