package net.twisterrob.cinema.cineworld.backend.endpoint.app

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object App {

	object Routes {

		@Location("/")
		object Home : LocationRoute
	}

	@Module
	interface FrontendModule {

		@Binds
		@IntoSet
		fun controller(impl: AppController): RouteController

		@Binds
		@IntoSet
		fun testController(impl: TestController): RouteController

		@Binds
		@IntoSet
		fun traceController(impl: TraceController): RouteController
	}

	@Module
	object BackendModule {

		@Provides
		fun httpClient() = HttpClient().config {
			install(Logging) {
				val log = LoggerFactory.getLogger(HttpClient::class.java)!!
				when {
					log.isTraceEnabled -> {
						level = LogLevel.ALL
						logger = LevelLogger(log, Logger::trace)
					}

					log.isDebugEnabled -> {
						level = LogLevel.BODY
						logger = LevelLogger(log, Logger::debug)
					}

					log.isInfoEnabled -> {
						level = LogLevel.INFO
						logger = LevelLogger(log, Logger::info)
					}

					else -> {
						level = LogLevel.NONE
						logger = LevelLogger(log, Logger::error)
					}
				}
			}
		}
	}
}

private class NetworkCall(url: String) : Exception("Callsite for $url")

private class LevelLogger(
	private val log: Logger,
	private val logAtLevel: Logger.(message: String) -> Unit,
) : io.ktor.client.features.logging.Logger {

	override fun log(message: String) {
		if (message.startsWith("REQUEST: ")) {
			val url = message.substringAfter("REQUEST: ")
			log.info("Network call: $url", NetworkCall(url).apply {
				stackTrace = stackTrace
					.drop(1)
					.filterNot {
						it.className.startsWith("io.ktor.")
								|| it.className.startsWith("kotlinx.coroutines.")
								|| it.className.startsWith("io.netty.")
								|| it.className.startsWith("kotlin.coroutines.")
								|| it.className.startsWith("java.")
					}
					.toTypedArray()
			})
		}
		log.logAtLevel(message)
	}
}
