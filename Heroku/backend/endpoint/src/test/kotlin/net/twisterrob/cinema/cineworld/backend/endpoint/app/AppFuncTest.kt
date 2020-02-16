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

	@Test fun `root serves index html`(@TempDir tempDir: File) = endpointTest(staticRootFolder = tempDir) {
		tempDir.resolve("index.html").writeText("fake index")
		handleRequest { method = HttpMethod.Get; uri = "/" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("fake index", response.content)
		}
	}

	@Test fun `index is directly accessible`(@TempDir tempDir: File) = endpointTest(staticRootFolder = tempDir) {
		tempDir.resolve("index.html").writeText("fake index")
		handleRequest { method = HttpMethod.Get; uri = "/index.html" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("fake index", response.content)
		}
	}

	@Test fun `planner serves index html`(@TempDir tempDir: File) = endpointTest(staticRootFolder = tempDir) {
		tempDir.resolve("planner/index.html").also { it.parentFile.mkdirs() }.writeText("fake index")
		handleRequest { method = HttpMethod.Get; uri = "/planner" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("fake index", response.content)
		}
	}

	@Test fun `favicon is served from Cineworld`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/favicon.ico" }.apply {
			assertEquals(HttpStatusCode.Found, response.status())
			assertEquals(
				"https://www.google.com/s2/favicons?domain=www.cineworld.co.uk",
				response.headers[HttpHeaders.Location]
			)
		}
	}
}
