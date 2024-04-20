package net.twisterrob.neo4j.ogm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.OffsetDateTime

class TimestampConverterUnitTest {

	private val sut = TimestampConverter()

	@Test fun `null serialization`() {
		val result = sut.toGraphProperty(null)

		assertNull(result)
	}

	@Test fun `null deserialization`() {
		val result = sut.toEntityAttribute(null)

		assertNull(result)
	}

	@CsvSource(
		value = [
			"2014-12-23T15:36:47.891Z,     2014-12-23T15:36:47.891Z", // full time stamp is serialized
			"2014-05-06T07:08:09.100Z,     2014-05-06T07:08:09.100Z", // optional parts are kept
			"2000-01-01T00:00:00.000Z,     2000-01-01T00:00:00.000Z", // zero parts are kept
			"2021-12-21T15:36:47.891+04:30,2021-12-21T11:06:47.891Z", // time zone is serialized without offset
		]
	)
	@ParameterizedTest fun `test serialization`(input: OffsetDateTime, expected: String) {
		val result = sut.toGraphProperty(input)

		assertNotNull(result)
		assertEquals(expected, result)
	}

	@CsvSource(
		value = [
			"2014-12-23T15:36:47.891Z, 2014-12-23T15:36:47.891Z", // full time stamp is serialized
			"2014-05-06T07:08:09.100Z, 2014-05-06T07:08:09.100Z", // optional parts are kept
			"2000-01-01T00:00:00.000Z, 2000-01-01T00:00:00.000Z", // zero parts are kept
		]
	)
	@ParameterizedTest fun `test deserialization`(input: String, expected: OffsetDateTime) {
		val result = sut.toEntityAttribute(input)

		assertNotNull(result)
		assertEquals(expected, result)
	}

	@Test fun `test deserialization failure`() {
		val input = "2021-12-21T15:36:47.891+04:30"

		val result = assertThrows<IllegalArgumentException> {
			sut.toEntityAttribute(input)
		}

		assertEquals("$input must end in Z to signify UTC time zone!", result.message)
	}
}
