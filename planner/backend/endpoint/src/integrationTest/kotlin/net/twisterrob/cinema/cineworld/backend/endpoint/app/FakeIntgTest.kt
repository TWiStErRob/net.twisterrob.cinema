package net.twisterrob.cinema.cineworld.backend.endpoint.app

import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ClientProvider
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.noRedirectClient
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.Cinemas
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.film.Films
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.Performances
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.PerformanceRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.view.Views
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.ViewRepository
import net.twisterrob.cinema.cineworld.backend.ktor.Env
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.mockEngine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @see TestController
 */
class FakeIntgTest {

	@Inject lateinit var mockAuth: AuthRepository
	@Inject lateinit var mockCinemas: CinemaRepository
	@Inject lateinit var mockFilms: FilmRepository
	@Inject lateinit var mockPerformances: PerformanceRepository
	@Inject lateinit var mockViews: ViewRepository

	@AfterEach
	fun tearDown() {
		verifyNoMoreInteractions(mockAuth, mockCinemas, mockFilms, mockPerformances, mockViews)
	}

	@Test fun `unknown file is not served`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		val response = noRedirectClient.get("/anything")

		assertEquals(HttpStatusCode.NotFound, response.status)
	}

	@Test fun `file is served from fake folder`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		tempDir.resolve("anything").writeText("fake thing")

		val response = noRedirectClient.get("/anything")

		response.assertFake("fake thing")
	}

	@Test fun `file is not served in production`(@TempDir tempDir: File) = fakeEndpointTest(
		env = Env.PRODUCTION,
		fakeRoot = tempDir,
	) {
		// Even if the file exists on disk.
		tempDir.resolve("anything").writeText("fake thing")

		val response = noRedirectClient.get("/anything")

		assertEquals(HttpStatusCode.NotFound, response.status)
	}

	@Test fun `overrides normal routing`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		tempDir.resolve("cinema").writeText("fake cinemas")

		val response = noRedirectClient.get("/cinema")

		response.assertFake("fake cinemas")
	}

	@Test fun `does not override normal routing in production`(@TempDir tempDir: File) = fakeEndpointTest(
		env = Env.PRODUCTION,
		fakeRoot = tempDir,
	) {
		// Even if the file exists on disk.
		tempDir.resolve("cinema").writeText("fake cinemas")

		val response = noRedirectClient.get("/cinema")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("[ ]", response.bodyAsText())
		verify(mockCinemas).getActiveCinemas()
	}

	@Test fun `file is served even if there's unrecognized query parameters`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		tempDir.resolve("anything").writeText("fake thing")

		val response = noRedirectClient.get("/anything?foo=bar&baz=qux")

		response.assertFake("fake thing")
	}

	@Test fun `root is served from index`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		tempDir.resolve("index.html").writeText("fake thing")

		val response = noRedirectClient.get("/")

		response.assertFake("fake thing")
	}

	@Test fun `file is served with query parameters`(@TempDir tempDir: File) = fakeEndpointTest(
		fakeRoot = tempDir,
	) {
		tempDir.resolve("anything").writeText("fake thing")
		tempDir.resolve("anything%3Ffoo=bar&baz=qux").writeText("fake thing - more specific")

		val response = noRedirectClient.get("/anything?foo=bar&baz=qux")

		response.assertFake("fake thing - more specific")
	}

	private fun fakeEndpointTest(
		fakeRoot: File? = null,
		env: Env? = null,
		test: suspend ClientProvider.() -> Unit,
	) {
		endpointTest(
			test = test,
			daggerApp = {
				@Suppress("detekt.MissingUseCall") // TODO close HttpClient.
				daggerApplication(
					createComponentBuilder = DaggerFakeIntgTestComponent::builder,
					initComponent = { componentBuilder ->
						componentBuilder
							.httpClient(HttpClient(mockEngine()))
							.auth(mock())
							.film(mock())
							.cinema(mock())
							.performance(mock())
							.view(mock())
					},
					componentReady = { (it as FakeIntgTestComponent).inject(this@FakeIntgTest) }
				)
			},
			testConfig = mapOf(
				"twisterrob.cinema.environment" to env?.let { it.name.lowercase(Locale.ROOT) },
				"twisterrob.cinema.fakeRootFolder" to fakeRoot?.let { it.absolutePath },
			).filterValues { it != null }.mapValues { it.value!! },
		)
	}
}

private suspend fun HttpResponse.assertFake(content: String) {
	assertEquals(HttpStatusCode.OK, status)
	assertEquals(content, bodyAsText())
	@Suppress("UastIncorrectHttpHeaderInspection")
	assertEquals("fakes", headers["X-Forwarded-Server"])
	// TODO verify logs
}

@Component(
	modules = [
		App.FrontendModule::class,
		Auth.FrontendModule::class,
		Cinemas.FrontendModule::class,
		Films.FrontendModule::class,
		Performances.FrontendModule::class,
		Views.FrontendModule::class,
	]
)
@Singleton
private interface FakeIntgTestComponent : ApplicationComponent {

	fun inject(test: FakeIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun auth(repository: AuthRepository): Builder

		@BindsInstance
		fun cinema(repository: CinemaRepository): Builder

		@BindsInstance
		fun film(repository: FilmRepository): Builder

		@BindsInstance
		fun performance(repository: PerformanceRepository): Builder

		@BindsInstance
		fun view(repository: ViewRepository): Builder

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		override fun build(): FakeIntgTestComponent
	}
}
