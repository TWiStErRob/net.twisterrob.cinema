package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import net.twisterrob.cinema.database.services.CinemaService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GraphCinemaRepository @Inject constructor(
	private val service: CinemaService
) : CinemaRepository {

	override fun getActiveCinemas(): List<Cinema> = service.findAll().map { Cinema(it.name) }

	override fun getCinemasAuth(userID: Long): List<Cinema> = emptyList()

	override fun getFavoriteCinemas(userID: Long): List<Cinema> = emptyList()
}
