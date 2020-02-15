package net.twisterrob.cinema.cineworld.backend.app

import dagger.BindsInstance
import dagger.Component
import io.ktor.application.Application
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.Cinemas
import net.twisterrob.cinema.cineworld.backend.endpoint.hello.HelloWorlds
import net.twisterrob.cinema.cineworld.backend.ktor.RouteControllerRegistrar
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import javax.inject.Singleton

@Component(
	modules = [
		Neo4JModule::class,
		HelloWorlds.FrontendModule::class,
		Cinemas.FrontendModule::class,
		Cinemas.BackendModule::class
	]
)
@Singleton
@Neo4J
interface ApplicationComponent {

	val controllers: RouteControllerRegistrar

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		@BindsInstance
		fun application(application: Application): Builder

		fun build(): ApplicationComponent
	}
}
