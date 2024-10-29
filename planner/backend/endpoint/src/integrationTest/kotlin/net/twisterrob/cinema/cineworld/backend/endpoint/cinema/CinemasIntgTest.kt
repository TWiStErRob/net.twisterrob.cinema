package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.noRedirectClient
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.sendTestAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.setupAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.serialized
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.mockEngine
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Cinemas.Routes
 * @see CinemasController
 */
class CinemasIntgTest {

	@Inject lateinit var mockRepository: CinemaRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	/** @see Cinemas.Routes.ListCinemas */
	@Test fun `list all cinemas (unauthenticated)`() = cinemasEndpointTest {
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getActiveCinemas()).thenReturn(fixtCinemas)

		val response = noRedirectClient.get("/cinema")

		verify(mockRepository).getActiveCinemas()
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtCinemas[0])},
					${serialized(fixtCinemas[1])}
				]
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.ListCinemas */
	@Test fun `list all cinemas (authenticated)`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getCinemasAuth(fixtUser.id)).thenReturn(fixtCinemas)

		val response = noRedirectClient.get("/cinema") { sendTestAuth() }

		verify(mockRepository).getCinemasAuth(fixtUser.id)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtCinemas[0])},
					${serialized(fixtCinemas[1])}
				]
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.ListFavoriteCinemas */
	@Test fun `list all favorite cinemas (unauthenticated)`() = cinemasEndpointTest {
		val response = noRedirectClient.get("/cinema/favs")

		verifyNoInteractions(mockRepository)
		assertEquals(HttpStatusCode.NotFound, response.status)
	}

	/** @see Cinemas.Routes.ListFavoriteCinemas */
	@Test fun `list all favorite cinemas (authenticated)`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinemas: List<Cinema> = fixture.buildList(size = 2)
		whenever(mockRepository.getFavoriteCinemas(fixtUser.id)).thenReturn(fixtCinemas)

		val response = noRedirectClient.get("/cinema/favs") { sendTestAuth() }

		verify(mockRepository).getFavoriteCinemas(fixtUser.id)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtCinemas[0])},
					${serialized(fixtCinemas[1])}
				]
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Cinemas.Routes.AddFavorite */
	@Test fun `add cinema as favorite for a user (unauthenticated)`() = cinemasEndpointTest {
		val response = noRedirectClient.put("/cinema/123/favorite")

		verifyNoInteractions(mockRepository)
		assertEquals(HttpStatusCode.NotFound, response.status)
	}

	/** @see Cinemas.Routes.AddFavorite */
	@Test fun `add cinema as favorite for a user`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinema: Cinema = fixture.build()
		whenever(mockRepository.addFavorite(fixtUser.id, 123)).thenReturn(fixtCinema)

		val response = noRedirectClient.put("/cinema/123/favorite") { sendTestAuth() }

		verify(mockRepository).addFavorite(fixtUser.id, 123)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(serialized(fixtCinema), response.bodyAsText(), JSONCompareMode.STRICT)
	}

	/** @see Cinemas.Routes.RemoveFavorite */
	@Test fun `remove cinema as favorite for a user (unauthenticated)`() = cinemasEndpointTest {
		val response = noRedirectClient.delete("/cinema/123/favorite")

		verifyNoInteractions(mockRepository)
		assertEquals(HttpStatusCode.NotFound, response.status)
	}

	/** @see Cinemas.Routes.RemoveFavorite */
	@Test fun `remove cinema as favorite for a user`() = cinemasEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtCinema: Cinema = fixture.build()
		whenever(mockRepository.removeFavorite(fixtUser.id, 123)).thenReturn(fixtCinema)

		val response = noRedirectClient.delete("/cinema/123/favorite") { sendTestAuth() }

		verify(mockRepository).removeFavorite(fixtUser.id, 123)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(serialized(fixtCinema), response.bodyAsText(), JSONCompareMode.STRICT)
	}

	private fun cinemasEndpointTest(test: suspend ClientProvider.() -> Unit) {
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
fun serialized(cinema: Cinema): String =
	// order intentionally switched up
	"""
		{
			"class": "${cinema.className}",
			"name": "${cinema.name}",
			"cineworldID": ${cinema.cineworldID},
			"postcode": "${cinema.postcode}",
			"telephone": "${cinema.telephone ?: "null"}",
			"cinema_url": "${cinema.cinemaUrl}",
			"_created": "${serialized(cinema.created)}",
			"_updated": "${serialized(cinema.updated)}",
			"address": "${cinema.address}",
			"fav": ${cinema.isFavorited}
		}
	""".trimIndent()
