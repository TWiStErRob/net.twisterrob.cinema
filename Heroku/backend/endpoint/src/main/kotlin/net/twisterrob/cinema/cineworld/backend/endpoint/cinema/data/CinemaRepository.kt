package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

interface CinemaRepository {

	fun getActiveCinemas(): List<Cinema>

	fun getCinemasAuth(userID: String): List<Cinema>

	fun getFavoriteCinemas(userID: String): List<Cinema>

	fun addFavorite(userID: String, cinema: Long): Cinema?

	fun removeFavorite(userID: String, cinema: Long): Cinema?
}
