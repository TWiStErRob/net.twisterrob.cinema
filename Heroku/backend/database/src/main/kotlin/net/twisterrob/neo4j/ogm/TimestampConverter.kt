package net.twisterrob.neo4j.ogm

import org.neo4j.ogm.typeconversion.AttributeConverter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

class TimestampConverter : AttributeConverter<OffsetDateTime, String> {

	override fun toGraphProperty(value: OffsetDateTime?): String? {
		if (value == null) return null
		return ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(value.withOffsetSameInstant(ZoneOffset.UTC))
	}

	override fun toEntityAttribute(value: String?): OffsetDateTime? {
		if (value == null) return null
		val result = OffsetDateTime.parse(value, ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH)
		require(ZoneOffset.UTC.equals(result.offset)) {
			"$value must end in Z to signify UTC time zone!"
		}
		return result
	}
}

/**
 * Make sure trailing zeros are serialized.
 * Milliseconds are always 3, regardless of precision (Java 9+).
 * Work around https://github.com/FasterXML/jackson-modules-java8/issues/76
 *
 * @see DateTimeFormatter.ISO_OFFSET_DATE_TIME
 */
@Suppress("MagicNumber") // See appendValue parameter hints, they're all "width".
private val ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH: DateTimeFormatter =
	DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
		.appendLiteral('-')
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.appendLiteral('T')
		.appendValue(ChronoField.HOUR_OF_DAY, 2)
		.appendLiteral(':')
		.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
		.appendLiteral(':')
		.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
		.appendLiteral('.')
		.appendValue(ChronoField.MILLI_OF_SECOND, 3)
		.appendOffsetId()
		.toFormatter(Locale.ROOT)
