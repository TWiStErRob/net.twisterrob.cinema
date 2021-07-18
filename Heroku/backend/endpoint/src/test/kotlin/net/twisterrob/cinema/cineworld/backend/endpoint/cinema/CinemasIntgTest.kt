package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthIntgTest
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.ktor.ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.mockEngine
import org.eclipse.jetty.http.HttpHeader
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Cinemas.Routes
 * @see CinemasController
 */
@TagIntegration
class CinemasIntgTest {

	@Inject lateinit var mockRepository: CinemaRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	/** @see Cinemas.Routes.ListCinemas */
	@Test fun `list all cinemas (unauthenticated)`() = cinemasEndpointTest {
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getActiveCinemas()).thenReturn(fixtCinemas)

		val call = handleRequest { method = HttpMethod.Get; uri = "/cinema" }

		verify(mockRepository).getActiveCinemas()
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
			[
				${serialized(fixtCinemas[0])},
				${serialized(fixtCinemas[1])}
			]
			""",
			call.response.content,
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.ListCinemas */
	@Test fun `list all cinemas`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getCinemasAuth(fixtUser.id)).thenReturn(fixtCinemas)

		val call = handleRequestAuth { method = HttpMethod.Get; uri = "/cinema" }

		verify(mockRepository).getCinemasAuth(fixtUser.id)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
			[
				${serialized(fixtCinemas[0])},
				${serialized(fixtCinemas[1])}
			]
			""",
			call.response.content,
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.ListFavoriteCinemas */
	@Test fun `list all favorite cinemas (unauthenticated)`() = cinemasEndpointTest {
		val call = handleRequest { method = HttpMethod.Get; uri = "/cinema/favs" }

		verifyZeroInteractions(mockRepository)

		assertEquals(HttpStatusCode.NotFound, call.response.status())
	}

	/** @see Cinemas.Routes.ListFavoriteCinemas */
	@Test fun `list all favorite cinemas`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getFavoriteCinemas(fixtUser.id)).thenReturn(fixtCinemas)

		val call = handleRequestAuth { method = HttpMethod.Get; uri = "/cinema/favs" }

		verify(mockRepository).getFavoriteCinemas(fixtUser.id)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
			[
				${serialized(fixtCinemas[0])},
				${serialized(fixtCinemas[1])}
			]
			""",
			call.response.content,
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.AddFavorite */
	@Test fun `add cinema as favorite for a user (unauthenticated)`() = cinemasEndpointTest {
		val call = handleRequest { method = HttpMethod.Put; uri = "/cinema/123/favorite" }

		verifyZeroInteractions(mockRepository)
		assertEquals(HttpStatusCode.NotFound, call.response.status())
	}

	/** @see Cinemas.Routes.AddFavorite */
	@Test fun `add cinema as favorite for a user`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinema: Cinema = fixture.build()
		whenever(mockRepository.addFavorite(fixtUser.id, 123)).thenReturn(fixtCinema)

		val call = handleRequestAuth { method = HttpMethod.Put; uri = "/cinema/123/favorite" }

		verify(mockRepository).addFavorite(fixtUser.id, 123)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(serialized(fixtCinema), call.response.content, JSONCompareMode.STRICT)
	}

	/** @see Cinemas.Routes.RemoveFavorite */
	@Test fun `remove cinema as favorite for a user (unauthenticated)`() = cinemasEndpointTest {
		val call = handleRequest { method = HttpMethod.Delete; uri = "/cinema/123/favorite" }

		verifyZeroInteractions(mockRepository)
		assertEquals(HttpStatusCode.NotFound, call.response.status())
	}

	/** @see Cinemas.Routes.RemoveFavorite */
	@Test fun `remove cinema as favorite for a user`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinema: Cinema = fixture.build()
		whenever(mockRepository.removeFavorite(fixtUser.id, 123)).thenReturn(fixtCinema)

		val call = handleRequestAuth { method = HttpMethod.Delete; uri = "/cinema/123/favorite" }

		verify(mockRepository).removeFavorite(fixtUser.id, 123)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(serialized(fixtCinema), call.response.content, JSONCompareMode.STRICT)
	}

	private fun cinemasEndpointTest(test: TestApplicationEngine.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerCinemasIntgTestComponent::builder,
					initComponent = { it.repo(mock()).auth(mock()).httpClient(HttpClient(mockEngine())) },
					componentReady = { (it as CinemasIntgTestComponent).inject(this@CinemasIntgTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class,
		Cinemas.FrontendModule::class
	]
)
@Singleton
private interface CinemasIntgTestComponent : ApplicationComponent {

	fun inject(test: CinemasIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun repo(repository: CinemaRepository): Builder

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun auth(repository: AuthRepository): Builder

		override fun build(): CinemasIntgTestComponent
	}
}

@Language("json")
private fun serialized(cinema: Cinema): String =
	// order intentionally switched up
	"""
	{
		"class": "${cinema.`class`}",
		"name": "${cinema.name}",
		"cineworldID": ${cinema.cineworldID},
		"postcode": "${cinema.postcode}",
		"telephone": "${cinema.telephone}",
		"cinema_url": "${cinema.cinema_url}",
		"_created": "${ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(cinema._created.withOffsetSameInstant(ZoneOffset.UTC))}",
		"_updated": "${ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(cinema._updated?.withOffsetSameInstant(ZoneOffset.UTC))}",
		"address": "${cinema.address}",
		"fav": ${cinema.fav}
	}
	"""

/**
 * Makes sure that auth interceptor works as expected.
 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthController
 */
private fun AuthRepository.setupAuth(): User {
	val fixtUser: User = JFixture().build()
	whenever(this.findUser(AuthIntgTest.realisticUserId)).thenReturn(fixtUser)
	return fixtUser
}

/**
 * Makes sure that auth interceptor works as expected.
 * @see net.twisterrob.cinema.cineworld.backend.endpoint.auth.AuthController
 */
private fun TestApplicationEngine.handleRequestAuth(block: TestApplicationRequest.() -> Unit): TestApplicationCall =
	handleRequest {
		addHeader(HttpHeader.COOKIE.toString(), AuthIntgTest.realisticCookie)
		block()
	}
