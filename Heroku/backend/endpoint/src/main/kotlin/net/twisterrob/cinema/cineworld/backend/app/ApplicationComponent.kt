package net.twisterrob.cinema.cineworld.backend.app

import dagger.BindsInstance
import dagger.Component
import io.ktor.application.Application
import net.twisterrob.cinema.cineworld.backend.ktor.RouteControllerRegistrar
import net.twisterrob.cinema.cineworld.backend.endpoint.hello.HelloWorlds
import javax.inject.Singleton

@Component(
	modules = [
		HelloWorlds.FrontendModule::class
	]
)
@Singleton
interface ApplicationComponent {

	val controllers: RouteControllerRegistrar

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		fun build(): ApplicationComponent
	}
}
