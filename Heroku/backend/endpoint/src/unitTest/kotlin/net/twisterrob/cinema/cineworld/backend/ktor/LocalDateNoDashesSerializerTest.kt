package net.twisterrob.cinema.cineworld.backend.ktor

import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.time.format.DateTimeParseException

class LocalDateNoDashesSerializerTest {

	@CsvSource(
		value = [
			"2014-12-23, \"20141223\"", // full time stamp is serialized
			"2014-10-02, \"20141002\"", // optional parts are kept
			"2014-03-15, \"20140315\"", // optional parts are kept
			"2000-01-01, \"20000101\"", // zero parts are kept
		]
	)
	@ParameterizedTest fun `test serialization`(input: LocalDate, expected: String) {
		val result = Json.encodeToString(LocalDateNoDashesSerializer, input)

		assertEquals(expected, result)
	}

	@CsvSource(
		value = [
			"\"20141223\", 2014-12-23", // full time stamp is serialized
			"\"20141002\", 2014-10-02", // optional parts are kept
			"\"20140315\", 2014-03-15", // optional parts are kept
			"\"20000101\", 2000-01-01", // zero parts are kept
		]
	)
	@ParameterizedTest fun `test deserialization`(input: String, expected: LocalDate) {
		val result = Json.decodeFromString(LocalDateNoDashesSerializer, input)

		assertEquals(expected, result)
	}

	@Test fun `test deserialization failure`() {
		@Language("json")
		val input = """"""""

		val result = assertThrows<DateTimeParseException> {
			Json.decodeFromString(LocalDateNoDashesSerializer, input)
		}

		assertEquals("Text '' could not be parsed at index 0", result.message)
	}
}
