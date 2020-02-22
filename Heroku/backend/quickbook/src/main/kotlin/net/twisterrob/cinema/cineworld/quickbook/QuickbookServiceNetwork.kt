package net.twisterrob.cinema.cineworld.quickbook

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

class QuickbookServiceNetwork @Inject constructor(
	client: HttpClient,
	private val key: String
) : QuickbookService {

	@UseExperimental(KtorExperimentalAPI::class)
	private val client = client.config {
		install(JsonFeature) {
			acceptContentTypes = listOf(ContentType.Application.Json)
		}
	}

	override fun films(full: Boolean): List<QuickbookFilm> = runBlocking {
		client.getWithFull(full) {
			url("https://www.cineworld.co.uk/api/quickbook/films")
			parameter("key", key)
			parameter("full", full)
		}.films
	}

	override fun films(date: LocalDate, cinemas: List<Long>, full: Boolean): List<QuickbookFilm> = runBlocking {
		client.getWithFull(full) {
			url("https://www.cineworld.co.uk/api/quickbook/films")
			parameter("key", key)
			parameter("date", date.formatDateParam())
			parameter("full", full)
			cinemas.forEach { parameter("cinema", it) }
		}.films
	}
}

/**
 * Tell Ktor [HttpClient] which type to parse the JSON as.
 * @param full whether the JSON has full information
 */
private suspend inline fun HttpClient.getWithFull(
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
