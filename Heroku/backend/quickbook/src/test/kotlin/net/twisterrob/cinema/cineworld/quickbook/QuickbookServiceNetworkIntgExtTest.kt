package net.twisterrob.cinema.cineworld.quickbook

import io.ktor.client.HttpClient
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import net.twisterrob.test.TagExternal
import net.twisterrob.test.TagIntegration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.time.LocalDate

@TagIntegration
@TagExternal
class QuickbookServiceNetworkIntgExtTest {

	private val client = HttpClient().config {
		install(Logging) {
			logger = Logger.DEFAULT
			level = LogLevel.ALL
		}
	}
	private val sut: QuickbookService = QuickbookServiceNetwork(client, "9qfgpF7B")

	companion object {
		private val londonCinemas = listOf<Long>(22, 25, 37, 45, 65, 66, 70, 79, 89, 103, 113)
		private val today = LocalDate.now()
		private val tomorrow = today.plusDays(1)
	}

	@Test fun `get films by cinema in London`() {
		val films = sut.films(date = tomorrow, cinemas = londonCinemas)

		assertThat(films, not(empty()))
	}

	@Test fun `get films by cinema in London with full details`() {
		val films = sut.films(date = tomorrow, cinemas = londonCinemas, full = true)

		assertThat(films, not(empty()))
	}

	@Test fun `get all films`() {
		val films = sut.films()

		assertThat(films, not(empty()))
	}

	@Test fun `get performances`() {
		val cinema = londonCinemas.random()
		val film = sut.films(date = tomorrow, cinemas = listOf(cinema)).random().edi
		val performances = sut.performances(date = tomorrow, cinema = cinema, film = film)

		assertThat(performances, not(empty()))
	}
}
