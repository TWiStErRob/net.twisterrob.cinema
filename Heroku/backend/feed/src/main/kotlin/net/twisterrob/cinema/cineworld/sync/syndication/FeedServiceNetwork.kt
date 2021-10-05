package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.onDownload
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import javax.inject.Inject

private val LOG = LoggerFactory.getLogger(FeedServiceNetwork::class.java)

class FeedServiceNetwork @Inject constructor(
	client: HttpClient
) : FeedService {

	private val client = client.config {
		install(JsonFeature) {
			// Poor man's XmlFeature
			acceptContentTypes = listOf(ContentType.Application.Xml)
			serializer = JacksonXmlSerializer(feedReader())
		}
	}

	override fun getWeeklyFilmTimes(): Feed =
		getUKWeeklyFilmTimes() + getIrelandWeeklyFilmTimes()

	private fun getUKWeeklyFilmTimes(): Feed =
		downloadFeed("https://www.cineworld.co.uk/syndication/weekly_film_times.xml")

	private fun getIrelandWeeklyFilmTimes(): Feed =
		downloadFeed("https://www.cineworld.co.uk/syndication/weekly_film_times_ie.xml")

	private fun downloadFeed(source: String): Feed = runBlocking {
		client.get {
			url(source)
			header(HttpHeaders.Connection, "close")
			onDownloadDebounceTrace("Downloading ${source}", 500)
		}
	}
}

/**
 * @param source prefix every message
 * @param frequency how often do we log in milliseconds.
 */
private fun HttpRequestBuilder.onDownloadDebounceTrace(source: String, frequency: Long) {
	var lastMessage = Long.MIN_VALUE
	onDownload { bytesSentTotal, contentLength ->
		val now = System.currentTimeMillis()
		if (lastMessage < now - frequency) {
			LOG.trace("$source: ${bytesSentTotal}/${contentLength}")
			lastMessage = now
		}
	}
}

private class JacksonXmlSerializer(
	private val backend: ObjectMapper
) : JsonSerializer {

	override fun read(type: TypeInfo, body: Input): Any =
		backend.readValue(body.readText(), backend.typeFactory.constructType(type.reifiedType))

	override fun write(data: Any, contentType: ContentType): OutgoingContent =
		TextContent(backend.writeValueAsString(data), contentType)

	override fun write(data: Any): OutgoingContent =
		write(data, ContentType.Application.Xml)
}
