package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.database.services.ViewService
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphViewRepository @Inject constructor(
	private val service: ViewService,
	private val mapper: ViewMapper,
) : ViewRepository {

	override fun addView(user: String, film: Long, cinema: Long, time: OffsetDateTime): View? =
		service.addView(user = user, film = film, cinema = cinema, time = time)?.let(mapper::map)

	override fun removeView(user: String, film: Long, cinema: Long, time: OffsetDateTime) {
		service.removeView(user = user, film = film, cinema = cinema, time = time)
	}

	override fun ignoreView(user: String, film: Long, reason: String): IgnoreResponse {
		TODO("Not implemented")
		@Suppress("UNREACHABLE_CODE")
		return IgnoreResponse(
			film = IgnoreResponse.Film(edi = film),
			reason = reason,
			date = OffsetDateTime.now()
		)
	}
}
