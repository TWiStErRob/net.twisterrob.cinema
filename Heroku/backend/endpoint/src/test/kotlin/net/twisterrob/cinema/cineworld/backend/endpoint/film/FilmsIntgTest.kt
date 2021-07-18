package net.twisterrob.cinema.cineworld.backend.endpoint.film

import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
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
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.Film
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import net.twisterrob.cinema.cineworld.backend.ktor.ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.build
import net.twisterrob.test.mockEngine
import org.eclipse.jetty.http.HttpHeader
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Films.Routes
 * @see FilmsController
 */
@TagIntegration
class FilmsIntgTest {

	@Inject lateinit var mockRepository: FilmRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	/** @see Films.Routes.GetFilm */
	@Test fun `get film by edi`() = filmsEndpointTest {
		fixture.customise().sameInstance(View::class.java, null) // prevent loop in val Film.view: View
		val fixtFilm: Film = fixture.build()
		whenever(mockRepository.getFilm(123)).thenReturn(fixtFilm)

		val call = handleRequest { method = HttpMethod.Get; uri = "/film/123" }

		verify(mockRepository).getFilm(123)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(serialized(fixtFilm), call.response.content, JSONCompareMode.STRICT)
	}

	private fun filmsEndpointTest(test: TestApplicationEngine.() -> Unit) {
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
private fun serialized(film: Film): String =
	// order intentionally switched up
	"""
	{
		"class": "${film.`class`}",
		"title": "${film.title}",
		"cineworldID": ${film.cineworldID},
		"_created": "${ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(film._created.atZoneSameInstant(ZoneOffset.UTC))}",
		"_updated": "${ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(film._updated?.atZoneSameInstant(ZoneOffset.UTC))}",
		"director": "${film.director}",
		"release": "${ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(film.release.atZoneSameInstant(ZoneOffset.UTC))}",
		"format": "${film.format}",
		"runtime": ${film.runtime},
		"poster_url": "${film.poster_url}",
		"cineworldInternalID": ${film.cineworldInternalID},
		"cert": "${film.cert}",
		"imax": ${film.imax},
		"3D": ${film.`3D`},
		"film_url": "${film.film_url}",
		"edi": ${film.edi},
		"classification": "${film.classification}",
		"trailer": "${film.trailer}",
		"actors": "${film.actors}",
		"originalTitle": "${film.originalTitle}",
		"categories": ${film.categories},
		"weighted": ${film.weighted},
		"slug": "${film.slug}",
		"group": ${film.group},
		"view": null
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
