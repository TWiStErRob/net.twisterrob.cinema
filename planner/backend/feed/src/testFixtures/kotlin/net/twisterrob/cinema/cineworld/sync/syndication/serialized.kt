package net.twisterrob.cinema.cineworld.sync.syndication

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

internal fun serialized(offsetDateTime: OffsetDateTime?): String =
	ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(offsetDateTime?.atZoneSameInstant(ZoneOffset.UTC))

/**
 * Make sure trailing zeros are serialized.
 * Milliseconds are never visible as is in feed.
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
		.appendLiteral('Z')
		.toFormatter(Locale.ROOT)
