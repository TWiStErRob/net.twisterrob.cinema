package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.database.services.ViewService
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphViewRepository @Inject constructor(
	private val service: ViewService,
	private val mapper: ViewMapper
) : ViewRepository {

	override fun addView(userID: String, film: Long, cinema: Long, time: OffsetDateTime): View? =
		service.addView(userID, film, cinema, time)?.let(mapper::map)

	override fun removeView(userID: String, film: Long, cinema: Long, time: OffsetDateTime) {
		service.removeView(userID, film, cinema, time)
	}
}
