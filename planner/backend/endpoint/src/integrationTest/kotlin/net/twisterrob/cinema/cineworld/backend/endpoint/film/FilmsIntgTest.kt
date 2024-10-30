package net.twisterrob.cinema.cineworld.backend.endpoint.film

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.noRedirectClient
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.sendTestAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.setupAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.serialized
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.build
import net.twisterrob.test.buildList
import net.twisterrob.test.mockEngine
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Films.Routes
 * @see FilmsController
 */
class FilmsIntgTest {

	@Inject lateinit var mockRepository: FilmRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	/** @see Films.Routes.ListFilms */
	@Test fun `list all films (unauthenticated)`() = filmsEndpointTest {
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtFilms: List<Film> = fixture.buildList(size = 2)
		val queryCinemaIDs: List<Long> = listOf(123, 456)
		val queryDate = LocalDate.of(2019, Month.MAY, 30)
		whenever(mockRepository.getFilms(any(), any())).thenReturn(fixtFilms)

		val response = noRedirectClient.get("/film?cinemaIDs=123&cinemaIDs=456&date=20190530")

		verify(mockRepository).getFilms(queryDate, queryCinemaIDs)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtFilms[0])},
					${serialized(fixtFilms[1])}
				]
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Films.Routes.ListFilms */
	@Test fun `list all films (authenticated)`() = filmsEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtFilms: List<Film> = fixture.buildList(size = 2)
		val queryCinemaIDs: List<Long> = listOf(123, 456)
		val queryDate = LocalDate.of(2019, Month.MAY, 30)
		whenever(mockRepository.getFilmsAuth(any(), any(), any())).thenReturn(fixtFilms)

		val response = noRedirectClient.get("/film?cinemaIDs=123&cinemaIDs=456&date=20190530") {
			sendTestAuth()
		}

		verify(mockRepository).getFilmsAuth(fixtUser.id, queryDate, queryCinemaIDs)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtFilms[0])},
					${serialized(fixtFilms[1])}
				]
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Films.Routes.GetFilm */
	@Test fun `get film by edi`() = filmsEndpointTest {
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtFilm: Film = fixture.build()
		val fixtEdi: Long = fixture.build()
		whenever(mockRepository.getFilm(fixtEdi)).thenReturn(fixtFilm)

		val response = noRedirectClient.get("/film/${fixtEdi}")

		verify(mockRepository).getFilm(fixtEdi)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(serialized(fixtFilm), response.bodyAsText(), JSONCompareMode.STRICT)
	}

	/** @see Films.Routes.GetFilm */
	@Test fun `get film by edi not found`() = filmsEndpointTest {
		val fixtEdi: Long = fixture.build()
		whenever(mockRepository.getFilm(fixtEdi)).thenReturn(null)

		val response = noRedirectClient.get("/film/${fixtEdi}")

		verify(mockRepository).getFilm(fixtEdi)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.NotFound, response.status)
		assertEquals("Film with EDI #${fixtEdi} is not found.", response.bodyAsText())
	}

	private fun filmsEndpointTest(test: suspend ClientProvider.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerFilmsIntgTestComponent::builder,
					initComponent = { it.repo(mock()).auth(mock()).httpClient(HttpClient(mockEngine())) },
					componentReady = { (it as FilmsIntgTestComponent).inject(this@FilmsIntgTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class,
		Films.FrontendModule::class
	]
)
@Singleton
private interface FilmsIntgTestComponent : ApplicationComponent {

	fun inject(test: FilmsIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun repo(repository: FilmRepository): Builder

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun auth(repository: AuthRepository): Builder

		override fun build(): FilmsIntgTestComponent
	}
}

@Language("json")
fun serialized(film: Film): String =
	// order intentionally switched up
	"""
		{
			"class": "${film.className}",
			"title": "${film.title}",
			"cineworldID": ${film.cineworldID ?: "null"},
			"_created": "${serialized(film.created)}",
			"_updated": "${serialized(film.updated)}",
			"director": "${film.director}",
			"release": "${serialized(film.release)}",
			"format": "${film.format}",
			"runtime": ${film.runtime},
			"poster_url": "${film.posterUrl}",
			"cineworldInternalID": ${film.cineworldInternalID},
			"cert": "${film.cert}",
			"imax": ${film.isIMAX},
			"3D": ${film.is3D},
			"film_url": "${film.filmUrl}",
			"edi": ${film.edi},
			"classification": "${film.classification}",
			"trailer": "${film.trailer ?: "null"}",
			"actors": "${film.actors}",
			"originalTitle": "${film.originalTitle}",
			"categories": ${film.categories},
			"weighted": ${film.weighted},
			"slug": "${film.slug}",
			"group": ${film.group},
			"view": null
		}
	""".trimIndent()
