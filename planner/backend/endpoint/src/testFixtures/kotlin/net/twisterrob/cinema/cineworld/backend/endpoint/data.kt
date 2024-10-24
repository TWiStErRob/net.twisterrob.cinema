package net.twisterrob.cinema.cineworld.backend.endpoint

import net.twisterrob.cinema.cineworld.backend.ktor.ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun serialized(offsetDateTime: OffsetDateTime?): String =
	ISO_OFFSET_DATE_TIME_FORMATTER_FIXED_WIDTH.format(offsetDateTime?.atZoneSameInstant(ZoneOffset.UTC))
