package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

/**
 * Repository that will handle operations related to the cinemas.
 */
interface CinemaRepository {

	fun getActiveCinemas(): List<Cinema>

	fun getCinemasAuth(userID: Long): List<Cinema>

	fun getFavoriteCinemas(userID: Long): List<Cinema>
}
