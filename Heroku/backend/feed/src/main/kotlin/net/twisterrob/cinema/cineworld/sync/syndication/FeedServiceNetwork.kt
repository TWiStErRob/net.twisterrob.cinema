package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.readText
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

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
		runBlocking { FeedDownloader(client).getUKWeeklyFilmTimes().receive() }

	private fun getIrelandWeeklyFilmTimes(): Feed =
		runBlocking { FeedDownloader(client).getIrelandWeeklyFilmTimes().receive() }
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
