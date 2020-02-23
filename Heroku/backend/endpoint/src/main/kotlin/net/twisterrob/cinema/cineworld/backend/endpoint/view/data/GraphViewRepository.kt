package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserMapper
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
	private val filmMapper: FilmMapper,
	private val userMapper: UserMapper
) : ViewRepository {

	override fun addView(userID: String, film: Long, cinema: Long, time: OffsetDateTime): View? =
		service.addView(userID, film, cinema, time)?.let {
			View(
				date = it.date.toEpochMilli(),
				film = filmMapper.map(it.watchedFilm),
				cinema = cinemaMapper.map(it.atCinema),
				user = userMapper.map(it.userRef)
			)
		}

	override fun removeView(userID: String, film: Long, cinema: Long, time: OffsetDateTime) {
		service.removeView(userID, film, cinema, time)
	}
}
