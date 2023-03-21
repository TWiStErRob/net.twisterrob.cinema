package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import com.google.gson.JsonParser
import dagger.Component
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.app.App
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.cinema.database.Neo4J
import net.twisterrob.cinema.database.Neo4JModule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.inject.Singleton

class CinemasIntgExtTest {

	@Test fun `list all cinemas`() = cinemasEndpointTest {
		val call = handleRequest { method = HttpMethod.Get; uri = "/cinema" }

		assertEquals(HttpStatusCode.OK, call.response.status())

		// Should be an array of cinema objects.
		val parse = JsonParser().parse(call.response.content).asJsonArray
		assertThat(parse.toList(), hasSize(102))
	}

	private fun cinemasEndpointTest(test: TestApplicationEngine.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = { daggerApplication(DaggerCinemasIntgExtTestComponent::builder) }
		)
	}
}

@Component(
	modules = [
		App.BackendModule::class,
		Cinemas.FrontendModule::class,
		Cinemas.BackendModule::class,
		Neo4JModule::class
	]
)
@Singleton
@Neo4J
private interface CinemasIntgExtTestComponent : ApplicationComponent {

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		override fun build(): CinemasIntgExtTestComponent
	}
}
