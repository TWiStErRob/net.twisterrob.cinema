package net.twisterrob.cinema.cineworld.sync.syndication

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FeedServiceNetwork @Inject constructor(
	client: HttpClient
) : FeedService {

	@Suppress("detekt.MissingUseCall") // This dies when process dies.
	private val client = client.config {
		install(ContentNegotiation) {
			// Poor man's XmlPlugin
			register(ContentType.Application.Xml, JacksonConverter(feedMapper()))
		}
	}

	override fun getWeeklyFilmTimes(): Feed =
		getUKWeeklyFilmTimes() + getIrelandWeeklyFilmTimes()

	private fun getUKWeeklyFilmTimes(): Feed =
		runBlocking { FeedDownloader(client).getUKWeeklyFilmTimes().body() }

	private fun getIrelandWeeklyFilmTimes(): Feed =
		runBlocking { FeedDownloader(client).getIrelandWeeklyFilmTimes().body() }
}
