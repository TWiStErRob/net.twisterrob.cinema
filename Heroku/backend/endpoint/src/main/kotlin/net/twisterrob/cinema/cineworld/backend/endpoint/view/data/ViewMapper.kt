package net.twisterrob.cinema.cineworld.backend.endpoint.view.data

import net.twisterrob.cinema.cineworld.backend.endpoint.auth.data.UserMapper
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.CinemaMapper
import net.twisterrob.cinema.cineworld.backend.endpoint.film.data.FilmMapper
import javax.inject.Inject
import net.twisterrob.cinema.cineworld.backend.endpoint.view.data.View as FrontendView
import net.twisterrob.cinema.database.model.View as DBView

class ViewMapper @Inject constructor(
	private val cinemaMapper: CinemaMapper,
	private val filmMapper: FilmMapper,
	private val userMapper: UserMapper
) {

	fun map(view: DBView): FrontendView =
		FrontendView(
			date = view.date.toEpochMilli(),
			film = filmMapper.map(view.watchedFilm, mapViews = false),
			cinema = cinemaMapper.map(view.atCinema),
			user = userMapper.map(view.userRef)
		)
}
