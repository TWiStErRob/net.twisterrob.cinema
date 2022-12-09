package net.twisterrob.cinema.cineworld.backend.endpoint.app

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.test.TagFunctional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TagFunctional
class AppFuncTest {

	/**
	 * @see App.Routes.Home
	 */
	@Test fun `root serves index html`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("index.html").writeText("fake index")

		val call = handleRequest { method = HttpMethod.Get; uri = "/" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		assertEquals("fake index", call.response.content)
	}

	@Test fun `index is directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("index.html").writeText("fake index")

		val call = handleRequest { method = HttpMethod.Get; uri = "/index.html" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		assertEquals("fake index", call.response.content)
	}

	@Test fun `other files are directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("other.file").writeText("fake content")

		val call = handleRequest { method = HttpMethod.Get; uri = "/other.file" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		assertEquals("fake content", call.response.content)
	}

	@Test fun `other files in subfolders are directly accessible`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("sub/folder/other.file").ensureParent().writeText("fake content")

		val call = handleRequest { method = HttpMethod.Get; uri = "/sub/folder/other.file" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		assertEquals("fake content", call.response.content)
	}

	@Test fun `planner serves index html`(@TempDir tempDir: File) = endpointTest(
		testConfig = mapOf("twisterrob.cinema.staticRootFolder" to tempDir.absolutePath),
	) {
		tempDir.resolve("planner/index.html").ensureParent().writeText("fake index")

		val call = handleRequest { method = HttpMethod.Get; uri = "/planner" }

		assertEquals(HttpStatusCode.OK, call.response.status())
		assertEquals("fake index", call.response.content)
	}

	@Test fun `favicon is served from Cineworld`() = endpointTest {
		val call = handleRequest { method = HttpMethod.Get; uri = "/favicon.ico" }

		assertEquals(HttpStatusCode.Found, call.response.status())
		assertEquals(
			"https://www.google.com/s2/favicons?domain=www.cineworld.co.uk",
			call.response.headers[HttpHeaders.Location]
		)
	}
}

private fun File.ensureParent(): File = also { it.parentFile.mkdirs() }
