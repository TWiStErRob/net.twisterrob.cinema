package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

/**
 * TODO account for http://www.cineworld.co.uk/api/film/detail?key=...&film=6237
 * TODO account for https://www.cineworld.co.uk/api/film/list, see [QuickbookFilmInternal].
 */
class QuickbookServiceNetwork @Inject constructor(
	client: HttpClient,
	@Named(QuickbookModule.API_KEY)
	private val key: String
) : QuickbookService {

	private val client = client.config {
		install(ContentNegotiation) {
			jackson(contentType = ContentType.Application.Json) {
				// Deserialize whatever is thrown at us, maybe stricten this to specific types?
				registerModule(JavaTimeModule())
				disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
			}
		}
	}

	fun cinemas(full: Boolean): List<QuickbookCinema> = runBlocking {
		client
			.getCinemasAsync(full) {
				url("https://www.cineworld.co.uk/api/quickbook/cinemas")
				parameter("key", key)
				parameter("full", full)
			}
			.await()
			.throwErrorOrReturn { it.cinemas }
	}

	fun films(full: Boolean): List<QuickbookFilm> = runBlocking {
		client
			.getFilmsAsync(full) {
				url("https://www.cineworld.co.uk/api/quickbook/films")
				parameter("key", key)
				parameter("full", full)
			}
			.await()
			.throwErrorOrReturn { it.films }
	}

	override fun films(date: LocalDate, cinemas: List<Long>, full: Boolean): List<QuickbookFilm> = runBlocking {
		client
			.getFilmsAsync(full) {
				url("https://www.cineworld.co.uk/api/quickbook/films")
				parameter("key", key)
				parameter("date", date.formatDateParam())
				parameter("full", full)
				cinemas.forEach { parameter("cinema", it) }
			}
			.await()
			.throwErrorOrReturn { it.films }
	}

	override fun performances(date: LocalDate, cinema: Long, film: Long): List<QuickbookPerformance> = runBlocking {
		client
			.get {
				url("https://www.cineworld.co.uk/api/quickbook/performances")
				parameter("key", key)
				parameter("date", date.formatDateParam())
				parameter("cinema", cinema)
				parameter("film", film)
			}
			.body<PerformancesResponse>()
			.throwErrorOrReturn { it.performances }
	}
}

/**
 * Tell Ktor [HttpClient] which type to parse the JSON as.
 * @param full whether the JSON has full information
 * @param block to set up the request.
 */
private inline fun HttpClient.getCinemasAsync(
	full: Boolean,
	crossinline block: HttpRequestBuilder.() -> Unit = {}
): Deferred<CinemasResponse<out QuickbookCinema>> =
	if (full) {
		async {
			get(block = block).body<CinemasResponse<QuickbookCinemaFull>>()
		}
	} else {
		async {
			get(block = block).body<CinemasResponse<QuickbookCinemaSimple>>()
		}
	}

/**
 * Tell Ktor [HttpClient] which type to parse the JSON as.
 * @param full whether the JSON has full information
 * @param block to set up the request.
 */
private inline fun HttpClient.getFilmsAsync(
	full: Boolean,
	crossinline block: HttpRequestBuilder.() -> Unit = {}
): Deferred<FilmsResponse<out QuickbookFilm>> =
	if (full) {
		async {
			get(block = block).body<FilmsResponse<QuickbookFilmFull>>()
		}
	} else {
		async {
			get(block = block).body<FilmsResponse<QuickbookFilmSimple>>()
		}
	}

/**
 * Cineworld API requires dates to be in format: `YYYYMMDD`.
 */
private fun LocalDate.formatDateParam(): String =
	this.toString().replace("-", "")
