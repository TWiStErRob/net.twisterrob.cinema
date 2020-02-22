package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.User
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaMapper
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmMapper
import net.twisterrob.cinema.database.services.ViewService
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphViewRepository @Inject constructor(
	private val service: ViewService,
	private val cinemaMapper: CinemaMapper,
	private val filmMapper: FilmMapper
) : ViewRepository {

	override fun addView(userID: String, film: Long, cinema: Long, time: OffsetDateTime): View? =
		service.addView(userID, film, cinema, time)?.let {
			View(
				filmMapper.map(it.watchedFilm),
				cinemaMapper.map(it.atCinema),
				User(it.userRef.id, it.userRef.email)
			)
		}

	override fun removeView(userID: String, film: Long, cinema: Long, time: OffsetDateTime) {
		service.removeView(userID, film, cinema, time)
	}
}
