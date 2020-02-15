package net.twisterrob.cinema.cineworld.backend.endpoint.cinema

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import net.twisterrob.cinema.cineworld.backend.endpoint.endpointTest
import net.twisterrob.test.TagFunctional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@TagFunctional
class CinemasFuncTest {

	@Test fun `list all cinemas`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/cinema/" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals(
				"""
				[ {
				  "name" : "Fake Cinema"
				} ]
				""".trimIndent(),
				response.content
			)
		}
	}
}
