package net.twisterrob.cinema.cineworld.quickbook

import java.net.URI
import java.time.LocalTime

data class QuickbookPerformance(
	val time: LocalTime,
	val available: Boolean,
	val booking_url: URI,
	val type: String,
	val ad: Boolean,
	val ss: Boolean,
	val subtitled: Boolean
)
