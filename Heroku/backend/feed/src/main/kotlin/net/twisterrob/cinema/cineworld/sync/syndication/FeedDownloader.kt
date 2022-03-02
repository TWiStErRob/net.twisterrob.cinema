package net.twisterrob.cinema.cineworld.sync.syndication

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.features.onDownload
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.fullPath
import org.slf4j.LoggerFactory
import java.io.File

private val LOG = LoggerFactory.getLogger(FeedDownloader::class.java)

internal class FeedDownloader(
	private val client: HttpClient,
	private val baseFolder: File = File("build/sync")
) {

	suspend fun getUKWeeklyFilmTimes(): HttpResponse =
		downloadFeed("https://www.cineworld.co.uk/syndication/weekly_film_times.xml")

	suspend fun getIrelandWeeklyFilmTimes(): HttpResponse =
		downloadFeed("https://www.cineworld.co.uk/syndication/weekly_film_times_ie.xml")

	private suspend fun downloadFeed(source: String): HttpResponse =
		client
			.get<HttpStatement>(source) {
				url(source)
				header(HttpHeaders.Connection, "close")
				onDownloadDebounceTrace("Downloading ${source}", @Suppress("MagicNumber") 500)
			}
			.execute()
			.also {
				val raw = it.receive<ByteArray>()
				require(baseFolder.isDirectory || baseFolder.mkdirs())
				val fileName = it.request.url.fullPath.substringAfterLast('/')
				val output = baseFolder.resolve(fileName)
				output.writeBytes(raw)
				LOG.info("Dumped response (${raw.size} bytes) to ${output.absolutePath}.")
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
