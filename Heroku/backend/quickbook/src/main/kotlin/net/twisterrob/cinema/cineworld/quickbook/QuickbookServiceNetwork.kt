package net.twisterrob.cinema.cineworld.quickbook

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
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
		install(JsonFeature) {
			serializer = JacksonSerializer {
				// Deserialize whatever is thrown at us, maybe stricten this to specific types?
				registerModule(JavaTimeModule())
				disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
			}
			acceptContentTypes = listOf(ContentType.Application.Json)
		}
	}

	fun cinemas(full: Boolean): List<QuickbookCinema> = runBlocking {
		client.getCinemas(full) {
			url("https://www.cineworld.co.uk/api/quickbook/cinemas")
			parameter("key", key)
			parameter("full", full)
		}.throwErrorOrReturn { it.cinemas }
	}

	fun films(full: Boolean): List<QuickbookFilm> = runBlocking {
		client.getFilms(full) {
			url("https://www.cineworld.co.uk/api/quickbook/films")
			parameter("key", key)
			parameter("full", full)
		}.throwErrorOrReturn { it.films }
	}

	override fun films(date: LocalDate, cinemas: List<Long>, full: Boolean): List<QuickbookFilm> = runBlocking {
		client.getFilms(full) {
			url("https://www.cineworld.co.uk/api/quickbook/films")
			parameter("key", key)
			parameter("date", date.formatDateParam())
			parameter("full", full)
			cinemas.forEach { parameter("cinema", it) }
		}.throwErrorOrReturn { it.films }
	}

	override fun performances(date: LocalDate, cinema: Long, film: Long): List<QuickbookPerformance> = runBlocking {
		client.get<PerformancesResponse> {
			url("https://www.cineworld.co.uk/api/quickbook/performances")
			parameter("key", key)
			parameter("date", date.formatDateParam())
			parameter("cinema", cinema)
			parameter("film", film)
		}.throwErrorOrReturn { it.performances }
	}
}

/**
 * Tell Ktor [HttpClient] which type to parse the JSON as.
 * @param full whether the JSON has full information
 */
private suspend inline fun HttpClient.getCinemas(
	full: Boolean,
	block: HttpRequestBuilder.() -> Unit = {}
): CinemasResponse<out QuickbookCinema> =
	if (full) {
		get<CinemasResponse<QuickbookCinemaFull>>(block = block)
	} else {
		get<CinemasResponse<QuickbookCinemaSimple>>(block = block)
	}

/**
 * Tell Ktor [HttpClient] which type to parse the JSON as.
 * @param full whether the JSON has full information
 */
private suspend inline fun HttpClient.getFilms(
	full: Boolean,
	block: HttpRequestBuilder.() -> Unit = {}
): FilmsResponse<out QuickbookFilm> =
	if (full) {
		get<FilmsResponse<QuickbookFilmFull>>(block = block)
	} else {
		get<FilmsResponse<QuickbookFilmSimple>>(block = block)
	}

/**
 * Cineworld API requires dates to be in format: `YYYYMMDD`.
 */
private fun LocalDate.formatDateParam(): String =
	this.toString().replace("-", "")
