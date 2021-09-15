package net.twisterrob.cinema.cineworld.backend.app

import dagger.BindsInstance
import dagger.Component
import io.ktor.application.Application
import net.twisterrob.cinema.cineworld.backend.endpoint.app.App
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.Cinemas
import net.twisterrob.cinema.cineworld.backend.endpoint.film.Films
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.Performances
import net.twisterrob.cinema.cineworld.backend.endpoint.view.Views
import net.twisterrob.cinema.cineworld.backend.ktor.RouteControllerRegistrar
import net.twisterrob.cinema.cineworld.quickbook.QuickbookModule
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import javax.inject.Singleton

@Component(
	modules = [
		Neo4JModule::class,
		App.FrontendModule::class,
		App.BackendModule::class,
		Auth.FrontendModule::class,
		Auth.BackendModule::class,
		Cinemas.FrontendModule::class,
		Cinemas.BackendModule::class,
		Films.FrontendModule::class,
		Films.BackendModule::class,
		Views.FrontendModule::class,
		Views.BackendModule::class,
		Performances.FrontendModule::class,
		Performances.BackendModule::class
	]
)
@Singleton
@Neo4J
interface ApplicationComponent {

	val controllers: RouteControllerRegistrar

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder>, QuickbookModule.Dependencies<Builder> {

		@BindsInstance
		fun application(application: Application): Builder

		@BindsInstance
		fun featureToggles(featureToggles: FeatureToggles): Builder

		fun build(): ApplicationComponent
	}
}
