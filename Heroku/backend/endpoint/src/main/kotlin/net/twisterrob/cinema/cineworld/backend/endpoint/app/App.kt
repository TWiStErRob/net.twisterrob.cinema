package net.twisterrob.cinema.cineworld.backend.endpoint.app

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.client.HttpClient
import io.ktor.server.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController
import net.twisterrob.ktor.client.configureLogging
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
			configureLogging(LoggerFactory.getLogger(HttpClient::class.java))
			expectSuccess = true
		}
	}
}
