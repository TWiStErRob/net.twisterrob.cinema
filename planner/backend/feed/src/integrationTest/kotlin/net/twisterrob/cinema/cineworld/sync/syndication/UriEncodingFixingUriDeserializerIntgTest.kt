package net.twisterrob.cinema.cineworld.sync.syndication

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI

class UriEncodingFixingUriDeserializerIntgTest {

	data class Film(
		@JsonDeserialize(using = UriEncodingFixingUriDeserializer::class)
		val url: URI,
	)

	/**
	 * This calls [UriEncodingFixingUriDeserializer] through [JsonDeserialize] in a realistic context.
	 */
	private fun executeSutCall(input: String): Film =
		input.byteInputStream().use { feedMapper().readValue(it) }

	@Suppress("HttpUrlsUsage") // Testing old style URLs.
	@Test fun `can decode valid url`() {
		@Language("xml")
		val input = """<film><url>http://www1.cineworld.co.uk/films/deadpool-2</url></film>"""

		val film: Film = executeSutCall(input)

		assertEquals(URI.create("http://www1.cineworld.co.uk/films/deadpool-2"), film.url)
	}

	@Test fun `can decode brackets`() {
		@Language("xml")
		val input = """<film><url>https://www.cineworld.co.uk/films/scream-1997-[re-issue]</url></film>"""

		val film: Film = executeSutCall(input)

		assertEquals(URI.create("https://www.cineworld.co.uk/films/scream-1997-%5Bre-issue%5D"), film.url)
	}

	@Test fun `fails on unexpected problems`() {
		@Language("xml")
		val input = """<film><url>This is not a URL!</url></film>"""

		val ex = assertThrows<Exception> {
			executeSutCall(input)
		}

		assertThat(ex.message, containsString("Illegal character in path at index 4"))
	}

	@Test fun `fails on other bad characters`() {
		@Language("xml")
		val input = """<film><url>https://www.cineworld.co.uk/films/invalid space</url></film>"""

		val ex = assertThrows<Exception> {
			executeSutCall(input)
		}

		assertThat(ex.message, containsString("Illegal character in path at index 41"))
	}

	@Test fun `fails on other bad characters when fixed ones are mixed`() {
		@Language("xml")
		val input = """<film><url>https://www.cineworld.co.uk/films/invalid[ ]space</url></film>"""

		val ex = assertThrows<Exception> {
			executeSutCall(input)
		}

		// 44 is actually at 42 in input, but since `[` is replaced with `%5B`, the text before space gets 2 character longer.
		assertThat(ex.message, containsString("Illegal character in path at index 44"))
	}
}
