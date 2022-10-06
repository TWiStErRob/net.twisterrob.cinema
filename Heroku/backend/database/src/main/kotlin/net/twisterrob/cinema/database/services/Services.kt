package net.twisterrob.cinema.database.services

interface Services {

	val cinemaService: CinemaService
	val filmService: FilmService
	val viewService: ViewService
	val userService: UserService
}
