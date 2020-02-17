package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import dagger.BindsInstance
import dagger.Component
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.cinema.database.model.Cinema
import net.twisterrob.cinema.database.services.CinemaService
import net.twisterrob.test.TagIntegration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import javax.inject.Inject
import javax.inject.Singleton

@TagIntegration
class CinemasIntgTest {

	@Inject lateinit var mockService: CinemaService

	@Test fun `list all cinemas`() = cinemasEndpointTest {
		val fixtCinemas = listOf(
			Cinema().apply { name = "Fake Cinema 1" },
			Cinema().apply { name = "Fake Cinema 2" }
		)
		whenever(mockService.getActiveCinemas()).thenReturn(fixtCinemas)

		val call = handleRequest { method = HttpMethod.Get; uri = "/cinema/" }

		verify(mockService).getActiveCinemas()
		verifyNoMoreInteractions(mockService)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
			[
				{
					"name": "Fake Cinema 1",
					"fav": false
				},
				{
					"name": "Fake Cinema 2",
					"fav": false
				}
			]
			""",
			call.response.content,
			JSONCompareMode.STRICT
		)
	}

	private fun cinemasEndpointTest(test: TestApplicationEngine.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerCinemasIntgTestComponent::builder,
					initComponent = { it.cinemas(mock()) },
					componentReady = { (it as CinemasIntgTestComponent).inject(this@CinemasIntgTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Cinemas.FrontendModule::class,
		Cinemas.BackendModule::class
	]
)
@Singleton
private interface CinemasIntgTestComponent : ApplicationComponent {

	fun inject(test: CinemasIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun cinemas(service: CinemaService)

		override fun build(): CinemasIntgTestComponent
	}
}
