package net.twisterrob.cinema.cineworld.endpoint

import io.ktor.application.log
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import net.twisterrob.test.TagFunctional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.Logger

@TagFunctional
class EndpointsFuncTest {

	@Test fun `root is groot`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals("I am Groot!", response.content)
		}
	}

	@Test fun `hello json`() = endpointTest {
		handleRequest { method = HttpMethod.Get; uri = "/resp" }.apply {
			assertEquals(HttpStatusCode.OK, response.status())
			assertEquals(
				"""
				{
				  "hello" : "world"
				}
				""".trimIndent(),
				response.content
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

private fun endpointTest(test: TestApplicationEngine.() -> Unit) {
	withApplication(
		environment = createTestEnvironment {
			val originalLog = log
			log = object : Logger by originalLog {
				override fun info(msg: String?) {
					when (msg) {
						"No ktor.deployment.watch patterns specified, automatic reload is not active" -> Unit
						else -> originalLog.info(msg)
					}
				}
			}
		}
	) {
		application.apply {
			configuration()
			endpoints()
			log.trace("Endpoint test starting {}", test::class)
			test()
			log.trace("Endpoint test finished {}", test::class)
		}
	}
}
