package net.twisterrob.cinema.cineworld.generate

import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import net.twisterrob.cinema.database.services.Services
import net.twisterrob.ktor.client.configureLogging
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Component(modules = [Neo4JModule::class, GenerateAppModule::class])
@Singleton
@Neo4J
interface GenerateAppComponent : Services {

	val main: Main

	@Component.Builder
	interface Builder : Neo4JModule.Dependencies<Builder> {

		@BindsInstance
		fun params(params: MainParameters): Builder

		fun build(): GenerateAppComponent
	}
}

@Module
class GenerateAppModule {

	@Provides
	fun httpClient(): HttpClient =
		@Suppress("detekt.MissingUseCall") // This dies when process dies.
		HttpClient().config {
			configureLogging(LoggerFactory.getLogger(HttpClient::class.java))
			expectSuccess = true
		}
}
