package net.twisterrob.cinema.cineworld.backend.endpoint.performance.data

import java.net.URI
import java.time.OffsetDateTime

data class Performances(
	val date: OffsetDateTime,
	val cinema: Long,
	val film: Long,
	val performances: List<Performance>
) {

	class Performance(
		val time: OffsetDateTime,
		val available: Boolean,
		val booking_url: URI,
		val type: String,
		val ad: Boolean,
		val ss: Boolean,
		val subtitled: Boolean
	)
}
