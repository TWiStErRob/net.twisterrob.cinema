package net.twisterrob.cinema.cineworld.backend.endpoint.view

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.noRedirectClient
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.setupAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.sendTestAuth
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.serialized
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.film.serialized
import net.twisterrob.cinema.cineworld.backend.endpoint.serialized
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.IgnoreResponse
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.ServerLogging
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.applyCustomisation
import net.twisterrob.test.build
import net.twisterrob.test.java8TimeRealistic
import net.twisterrob.test.mockEngine
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see Views.Routes
 * @see ViewsController
 */
class ViewsIntgTest {

	@Inject lateinit var mockRepository: ViewRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	@BeforeEach fun setUp() {
		fixture.applyCustomisation {
			add(java8TimeRealistic())
			circularDependencyBehaviour().omitSpecimen() // View -> Film -> View
		}
	}

	/** @see Views.Routes.AddView */
	@Test fun `add a view (unauthenticated)`() = viewEndpointTest(posts = true) {
		val response = noRedirectClient.post {
			url("/film/123/view")
		}

		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.NotFound, response.status)
		assertEquals("Can't find user.", response.bodyAsText())
	}

	/** @see Views.Routes.AddView */
	@Test fun `add a view (authenticated)`() = viewEndpointTest(posts = true) {
		val fixtUser = mockAuth.setupAuth()
		val fixtView: View = fixture.build()
		whenever(mockRepository.addView(any(), any(), any(), any())).thenReturn(fixtView)
		val fixtDate: OffsetDateTime = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.MILLIS)

		val response = noRedirectClient.post {
			url("/film/123/view")
			sendTestAuth()
			header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
			@Language("json")
			val body = """
				{
					"edi": 456,
					"cinema": 789,
					"date": ${fixtDate.toInstant().toEpochMilli()}
				}
			""".trimIndent()
			setBody(body)
		}

		verify(mockRepository).addView(fixtUser.id, 456, 789, fixtDate.withOffsetSameInstant(ZoneOffset.UTC))
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				${serialized(fixtView)}
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	/** @see Views.Routes.RemoveView */
	@Test fun `remove a view (unauthenticated)`() = viewEndpointTest {
		val fixtDate: OffsetDateTime = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.MILLIS)

		val response = noRedirectClient.delete {
			url("/film/123/view?cinema=789&date=${fixtDate.toInstant().toEpochMilli()}")
		}

		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.NotFound, response.status)
		assertEquals("Can't find user.", response.bodyAsText())
	}

	/** @see Views.Routes.RemoveView */
	@Test fun `remove a view (authenticated)`() = viewEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtDate: OffsetDateTime = fixture.build<OffsetDateTime>().truncatedTo(ChronoUnit.MILLIS)

		val response = noRedirectClient.delete {
			url("/film/123/view?cinema=456&date=${fixtDate.toInstant().toEpochMilli()}")
			sendTestAuth()
		}

		verify(mockRepository).removeView(fixtUser.id, 123, 456, fixtDate.withOffsetSameInstant(ZoneOffset.UTC))
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("", response.bodyAsText())
	}

	/** @see Views.Routes.IgnoreView */
	@Test fun `ignore a view (unauthenticated)`() = viewEndpointTest {
		val response = noRedirectClient.put {
			url("/film/123/ignore?reason=${fixture.build<String>()}")
		}

		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.NotFound, response.status)
		assertEquals("Can't find user.", response.bodyAsText())
	}

	/** @see Views.Routes.IgnoreView */
	@Test fun `ignore a view (authenticated)`() = viewEndpointTest {
		val fixtUser = mockAuth.setupAuth()
		val fixtReason: String = fixture.build()
		val fixtIgnore: IgnoreResponse = fixture.build()
		whenever(mockRepository.ignoreView(any(), any(), any())).thenReturn(fixtIgnore)

		val response = noRedirectClient.put {
			url("/film/123/ignore?reason=${fixtReason}")
			sendTestAuth()
		}

		verify(mockRepository).ignoreView(fixtUser.id, 123, fixtReason)
		verifyNoMoreInteractions(mockRepository)

		assertEquals(HttpStatusCode.OK, response.status)
		JSONAssert.assertEquals(
			"""
				${serialized(fixtIgnore)}
			""".trimIndent(),
			response.bodyAsText(),
			JSONCompareMode.STRICT
		)
	}

	private fun viewEndpointTest(posts: Boolean = false, test: suspend ClientProvider.() -> Unit) {
		endpointTest(
			test = test,
			logLevel = if (posts) ServerLogging.LogLevel.HEADERS else ServerLogging.LogLevel.ALL,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerViewsIntgTestComponent::builder,
					initComponent = { it.repo(mock()).auth(mock()).httpClient(HttpClient(mockEngine())) },
					componentReady = { (it as ViewsIntgTestComponent).inject(this@ViewsIntgTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class,
		Views.FrontendModule::class
	]
)
@Singleton
private interface ViewsIntgTestComponent : ApplicationComponent {

	fun inject(test: ViewsIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun repo(repository: ViewRepository): Builder

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun auth(repository: AuthRepository): Builder

		override fun build(): ViewsIntgTestComponent
	}
}

@Language("json")
fun serialized(view: View): String =
	// order intentionally switched up
	"""
		{
			"cinema": ${serialized(view.cinema)},
			"date": ${view.date},
			"film": ${serialized(view.film)},
			"user": ${serialized(view.user)}
		}
	""".trimIndent()

@Language("json")
fun serialized(user: User): String =
	// order intentionally switched up
	"""
		{
			"_created": "${serialized(user.created)}",
			"id": "${user.id}",
			"email": "${user.email}",
			"name": "${user.name}",
			"realm": "${user.realm}"
		}
	""".trimIndent()

@Language("json")
fun serialized(ignore: IgnoreResponse): String =
	// order intentionally switched up
	"""
		{
			"reason": "${ignore.reason}",
			"film": { "edi": ${ignore.film.edi} },
			"date": "${serialized(ignore.date)}"
		}
	""".trimIndent()
