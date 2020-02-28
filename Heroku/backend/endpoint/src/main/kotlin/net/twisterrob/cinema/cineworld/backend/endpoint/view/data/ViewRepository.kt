package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import java.time.OffsetDateTime

interface ViewRepository {

	fun addView(user: String, film: Long, cinema: Long, time: OffsetDateTime): View?

	fun removeView(user: String, film: Long, cinema: Long, time: OffsetDateTime)

	fun ignoreView(user: String, film: Long, reason: String): IgnoreResponse
}
