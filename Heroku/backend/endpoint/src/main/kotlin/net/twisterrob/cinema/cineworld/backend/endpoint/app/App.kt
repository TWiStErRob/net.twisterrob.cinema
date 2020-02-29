package net.twisterrob.cinema.cineworld.backend.endpoint.app

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.ktor.locations.Location
import net.twisterrob.cinema.cineworld.backend.ktor.LocationRoute
import net.twisterrob.cinema.cineworld.backend.ktor.RouteController

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
	}
}
