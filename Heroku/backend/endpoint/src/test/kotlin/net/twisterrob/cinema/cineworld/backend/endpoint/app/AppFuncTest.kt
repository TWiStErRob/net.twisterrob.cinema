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

	@Test fun `other files are directly accessible`(@TempDir tempDir: File) = endpointTest(staticRootFolder = tempDir) {
		tempDir.resolve("other.file").writeText("fake content")
		handleRequest { method = HttpMethod.Get; uri = "/other.file" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("fake content", response.content)
		}
	}

	@Test fun `other files in subfolders are directly accessible`(@TempDir tempDir: File) =
		endpointTest(staticRootFolder = tempDir) {
			tempDir.resolve("sub/folder/other.file").ensureParent().writeText("fake content")
			handleRequest { method = HttpMethod.Get; uri = "/sub/folder/other.file" }.apply {
				assertEquals(HttpStatusCode.OK, response.status())
				assertEquals("fake content", response.content)
			}
		}

	@Test fun `planner serves index html`(@TempDir tempDir: File) = endpointTest(staticRootFolder = tempDir) {
		tempDir.resolve("planner/index.html").ensureParent().writeText("fake index")
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

private fun File.ensureParent(): File = also { it.parentFile.mkdirs() }
