package net.twisterrob.cinema.cineworld.backend.endpoint.performance

import com.flextrade.jfixture.JFixture
import dagger.BindsInstance
import dagger.Component
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import net.twisterrob.cinema.cineworld.backend.app.ApplicationComponent
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.Auth
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.AuthRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.PerformanceRepository
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.Performances.Performance
import net.twisterrob.cinema.cineworld.backend.endpoint.serialized
import net.twisterrob.cinema.cineworld.backend.ktor.daggerApplication
import net.twisterrob.test.buildList
import net.twisterrob.test.mockEngine
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
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
import net.twisterrob.cinema.cineworld.backend.endpoint.performance.data.Performances as PerformancesModel

/**
 * @see Performances.Routes
 * @see PerformanceController
 */
class PerformancesIntgTest {

	@Inject lateinit var mockRepository: PerformanceRepository
	@Inject lateinit var mockAuth: AuthRepository

	private val fixture = JFixture()

	/** @see Performances.Routes.ListPerformances */
	@Test fun `list all performances`() = performancesEndpointTest {
		val fixtPerformances: List<PerformancesModel> = fixture.buildList(size = 2)
		val queryCinemaIDs: List<Long> = listOf(123, 456)
		val queryFilmIDs: List<Long> = listOf(234, 789)
		val queryDate = LocalDate.of(2019, Month.MAY, 30)
		whenever(mockRepository.list(any(), any(), any())).thenReturn(fixtPerformances)

		val call = handleRequest {
			method = HttpMethod.Get
			uri = "/performance?cinemaIDs=123&cinemaIDs=456&date=20190530&filmEDIs=234&filmEDIs=789"
		}

		verify(mockRepository).list(queryDate, queryFilmIDs, queryCinemaIDs)
		verifyNoMoreInteractions(mockRepository)

		Assertions.assertEquals(HttpStatusCode.OK, call.response.status())
		JSONAssert.assertEquals(
			"""
				[
					${serialized(fixtPerformances[0])},
					${serialized(fixtPerformances[1])}
				]
			""".trimIndent(),
			call.response.content,
			JSONCompareMode.STRICT
		)
	}

	private fun performancesEndpointTest(test: TestApplicationEngine.() -> Unit) {
		endpointTest(
			test = test,
			daggerApp = {
				daggerApplication(
					createComponentBuilder = DaggerPerformancesIntgTestComponent::builder,
					initComponent = { it.repo(mock()).auth(mock()).httpClient(HttpClient(mockEngine())) },
					componentReady = { (it as PerformancesIntgTestComponent).inject(this@PerformancesIntgTest) }
				)
			}
		)
	}
}

@Component(
	modules = [
		Auth.FrontendModule::class,
		Performances.FrontendModule::class
	]
)
@Singleton
private interface PerformancesIntgTestComponent : ApplicationComponent {

	fun inject(test: PerformancesIntgTest)

	@Component.Builder
	interface Builder : ApplicationComponent.Builder {

		@BindsInstance
		fun repo(repository: PerformanceRepository): Builder

		@BindsInstance
		fun httpClient(client: HttpClient): Builder

		@BindsInstance
		fun auth(repository: AuthRepository): Builder

		override fun build(): PerformancesIntgTestComponent
	}
}

@Language("json")
private fun serialized(performances: PerformancesModel): String =
	// order intentionally switched up
	"""
		{
			"film": ${performances.film},
			"date": "${serialized(performances.date)}",
			"performances": [
				${performances.performances.joinToString(",\n") { serialized(it) }}
			],
			"cinema": ${performances.cinema}
		}
	""".trimIndent()

@Language("json")
private fun serialized(performance: Performance): String =
	// order intentionally switched up
	"""
		{
			"time": "${serialized(performance.time)}",
			"available": ${performance.isAvailable},
			"booking_url": "${performance.bookingUrl}",
			"type": "${performance.type}",
			"ad": ${performance.isAudioDescribed},
			"ss": ${performance.isSuperScreen},
			"subtitled": ${performance.isSubtitled}
		}
	""".trimIndent()
