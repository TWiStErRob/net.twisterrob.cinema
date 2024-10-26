package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import net.twisterrob.cinema.cineworld.backend.endpoint.auth.noRedirectClient
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AppIntgTest {

	/**
	 * @see App.Routes.Home
	 */
	@Test fun `root serves index html`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("index.html").writeText("fake index")

		val response = noRedirectClient.get("/")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("fake index", response.bodyAsText())
	}

	@Test fun `index is directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("index.html").writeText("fake index")

		val response = noRedirectClient.get("/index.html")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("fake index", response.bodyAsText())
	}

	@Test fun `other files are directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("other.file").writeText("fake content")

		val response = noRedirectClient.get("/other.file")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("fake content", response.bodyAsText())
	}

	@Test fun `other files in subfolders are directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("sub/folder/other.file").ensureParent().writeText("fake content")

		val response = noRedirectClient.get("/sub/folder/other.file")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("fake content", response.bodyAsText())
	}

	@Test fun `planner serves index html`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("planner/index.html").ensureParent().writeText("fake index")

		val response = noRedirectClient.get("/planner")

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals("fake index", response.bodyAsText())
	}

	@Test fun `favicon is served from Cineworld`() = endpointTest {
		val response = noRedirectClient.get("/favicon.ico")

		assertEquals(HttpStatusCode.Found, response.status)
		assertEquals(
			"https://www.google.com/s2/favicons?domain=www.cineworld.co.uk",
			response.headers[HttpHeaders.Location]
		)
	}
}

private fun File.ensureParent(): File = also { it.parentFile.mkdirs() }
