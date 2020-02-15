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
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.TagFunctional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import javax.inject.Inject
import javax.inject.Singleton

@TagFunctional
class CinemasFuncTest {

	@Inject lateinit var mockRepository: CinemaRepository

	@Test fun `list all cinemas`() = cinemasEndpointTest {
		val fixtCinemas = listOf(
			Cinema(name = "Fake Cinema 1"),
			Cinema(name = "Fake Cinema 2")
		)
		whenever(mockRepository.getActiveCinemas()).thenReturn(fixtCinemas)

		val call = handleRequest { method = HttpMethod.Get; uri = "/cinema/" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
			[
				{
					"name": "Fake Cinema 1"
				},
				{
					"name": "Fake Cinema 2"
				}
			]
			""",
			call.response.content,
			JSONCompareMode.STRICT
		)
		verify(mockRepository).getActiveCinemas()
		verifyNoMoreInteractions(mockRepository)
	}

	private fun cinemasEndpointTest(test: TestApplicationEngine.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerCinemasFuncTestComponent::builder,
					initComponent = { it.cinemas(mock()) },
					componentReady = { (it as CinemasFuncTestComponent).inject(this@CinemasFuncTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Cinemas.FrontendModule::class
	]
)
@Singleton
private interface CinemasFuncTestComponent : ApplicationComponent {

	fun inject(test: CinemasFuncTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun cinemas(repository: CinemaRepository)

		override fun build(): CinemasFuncTestComponent
	}
}
