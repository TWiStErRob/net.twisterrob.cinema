package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import java.time.OffsetDateTime

/**
 * Repository that will handle operations related to the cinemas.
 */
interface ViewRepository {

	fun addView(userID: String, film: Long, cinema: Long, time: OffsetDateTime): View?

	fun removeView(userID: String, film: Long, cinema: Long, time: OffsetDateTime)
}
