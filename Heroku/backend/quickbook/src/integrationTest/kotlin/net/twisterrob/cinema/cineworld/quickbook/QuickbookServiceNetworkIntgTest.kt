package net.twisterrob.cinema.cineworld.quickbook

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.headersOf
import net.twisterrob.test.mockEngine
import net.twisterrob.test.stub
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class QuickbookServiceNetworkIntgTest {

	private val mockClient = HttpClient(mockEngine()).config {
		install(Logging) {
			logger = Logger.DEFAULT
			level = LogLevel.HEADERS
		}
	}

	private val sut = QuickbookServiceNetwork(mockClient, "fake-key")

	@Test fun `can load all cinemas`() {
		mockClient.stubQuickbook()

		val cinemas = sut.cinemas(false)

		assertNotNull(cinemas)
		cinemas.forEach { cinema ->
			validate(assertInstanceOf(QuickbookCinemaSimple::class.java, cinema))
		}
	}

	@Test fun `can load all cinemas (full)`() {
		mockClient.stubQuickbook()

		val cinemas = sut.cinemas(true)

		assertNotNull(cinemas)
		cinemas.forEach { cinema ->
			validate(assertInstanceOf(QuickbookCinemaFull::class.java, cinema))
		}
	}

	@Test fun `can load all films`() {
		mockClient.stubQuickbook()

		val films = sut.films(false)

		assertNotNull(films)
		films.forEach { film ->
			validate(assertInstanceOf(QuickbookFilmSimple::class.java, film))
		}
	}

	@Test fun `can load all films (full)`() {
		mockClient.stubQuickbook()

		val films = sut.films(true)

		assertNotNull(films)
		films.forEach { film ->
			validate(assertInstanceOf(QuickbookFilmFull::class.java, film))
		}
	}

	@Test fun `can load filtered films (full)`() {
		mockClient.stubQuickbook()

		val films = sut.films(LocalDate.now(), emptyList(), true)

		assertNotNull(films)
		films.forEach { film ->
			validate(assertInstanceOf(QuickbookFilmFull::class.java, film))
		}
	}

	@Test fun `can load filtered films`() {
		mockClient.stubQuickbook()

		val films = sut.films(LocalDate.now(), emptyList(), false)

		assertNotNull(films)
		films.forEach { film ->
			validate(assertInstanceOf(QuickbookFilmSimple::class.java, film))
		}
	}

	@Test fun `can load filtered performances`() {
		mockClient.stubQuickbook()

		val performances = sut.performances(LocalDate.now(), 1, 2)

		assertNotNull(performances)
		performances.forEach { performance ->
			validate(assertInstanceOf(QuickbookPerformance::class.java, performance))
		}
	}
}

private fun validate(obj: Any) {
	obj::class.memberProperties.forEach { property ->
		@Suppress("UNCHECKED_CAST")
		val value = (property as KProperty1<Any, *>).get(obj)
		if (!property.returnType.isMarkedNullable) {
			assertNotNull(value) { "Property ${property.name} is null on ${obj}." }
		}
	}
}

private fun HttpClient.stubQuickbook() {
	stub { request ->
		@Suppress("UseIfInsteadOfWhen") // Conventionally this is a when-expression.
		when {
			request.isQuickbook -> respondQuickbook(request.url)
			else -> error("Unhandled ${request.url}")
		}
	}
}

private val HttpRequestData.isQuickbook: Boolean
	get() = url.toString().startsWith("https://www.cineworld.co.uk/api/quickbook/")

private fun MockRequestHandleScope.respondQuickbook(request: Url): HttpResponseData {
	val name = request.encodedPath.split("/").last()
	val responseHeaders = headersOf(
		HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString()),
	)
	val fileName = "${name}${if (request.parameters["full"] == "true") "_full" else ""}.json"
	return respond(loadFeed(fileName), headers = responseHeaders)
}

private fun loadFeed(fileName: String): ByteArray {
	val stream = QuickbookServiceNetworkIntgTest::class.java.getResourceAsStream(fileName)
		?: error("Cannot find $fileName near ${QuickbookServiceNetworkIntgTest::class.java}")
	return stream.readBytes()
}
