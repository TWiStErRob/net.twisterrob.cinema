package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake in-memory implementation of [CinemaRepository].
 */
@Singleton
class FakeCinemaRepository @Inject constructor(
) : CinemaRepository {

	override fun getActiveCinemas(): List<Cinema> = listOf(Cinema("Fake Cinema"))

	override fun getCinemasAuth(userID: Long): List<Cinema> = emptyList()

	override fun getFavoriteCinemas(userID: Long): List<Cinema> = emptyList()
}
