package net.twisterrob.cinema.cineworld.backend.endpoint.hello

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.test.TagFunctional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

@TagFunctional
class HelloWorldsFuncTest {

	@Test fun `root is groot`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("I am Groot!", response.content)
		}
	}

	@Test fun `hello json`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/resp" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			JSONAssert.assertEquals(
				"""
				{
				  "hello" : "world"
				}
				""",
				response.content,
				JSONCompareMode.STRICT
			)
		}
	}

	@Test fun `static handler returns static file with static contents`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/static/static.file" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("static contents", response.content?.trim())
		}
	}
}
