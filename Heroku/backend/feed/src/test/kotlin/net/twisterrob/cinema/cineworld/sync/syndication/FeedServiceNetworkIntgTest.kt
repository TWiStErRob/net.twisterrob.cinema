package net.twisterrob.cinema.cineworld.sync.syndication

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.GENRES
import net.twisterrob.cinema.cineworld.sync.syndication.FeedData.SCREENING_TYPES
import net.twisterrob.test.TagIntegration
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

@TagIntegration
class FeedServiceNetworkIntgTest {

	private val mockClient = HttpClient(mockEngine())

	private val sut = FeedServiceNetwork(mockClient)

	@Test fun `read weekly film times XML`() {
		mockClient.stub { request ->
			@Suppress("UseIfInsteadOfWhen") // Conventionally this is a when-expression.
			when {
				request.url.toString().startsWith("https://classic.cineworld.co.uk/syndication/") -> {
					val fileName = request.url.encodedPath.split("/").last()
					val responseHeaders = headersOf(
						HttpHeaders.ContentType to listOf(Application.Xml.toString()),
						HttpHeaders.Date to listOf("""Thu, 13 Feb 2020 01:00:53 GMT"""),
						HttpHeaders.ETag to listOf("""W/"173826-1581547056000"""")
					)
					respond(loadFeed(fileName), headers = responseHeaders)
				}

				else -> error("Unhandled ${request.url}")
			}
		}

		val feed = sut.getWeeklyFilmTimes()

		assertNotNull(feed)
		feed.sanityCheck()
		feed.verifyHasAllAttributes(SCREENING_TYPES + GENRES)
	}
}

private fun loadFeed(fileName: String): ByteArray {
	val resource = "/$fileName"
	val stream = FeedServiceNetworkIntgTest::class.java.getResourceAsStream(resource)
		?: error("Cannot find $resource near ${FeedServiceNetworkIntgTest::class.java}")
	return stream.readBytes()
}
