package net.twisterrob.cinema.cineworld.backend.ktor

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

/**
 * @see DateTimeFormatter.ISO_LOCAL_DATE
 */
val ISO_LOCAL_DATE_FORMATTER_NO_DASHES: DateTimeFormatter =
	DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.toFormatter(Locale.ROOT)

/**
 * Make sure trailing zeros are serialized.
 * Milliseconds are always 3, regardless of precision (Java 9+).
 * Work around https://github.com/FasterXML/jackson-modules-java8/issues/76
 *
 * @see DateTimeFormatter.ISO_OFFSET_DATE_TIME
 */
val ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH: DateTimeFormatter =
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
		.appendLiteral('Z')
		.toFormatter(Locale.ROOT)
