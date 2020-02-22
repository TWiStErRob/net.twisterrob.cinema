package net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data

import net.twisterrob.cinema.database.services.CinemaService
import javax.inject.Inject
import javax.inject.Singleton
import net.twisterrob.cinema.cineworld.backend.endpoint.cinema.data.Cinema as FrontendCinema
import net.twisterrob.cinema.database.model.Cinema as DBCinema

@Singleton
class GraphCinemaRepository @Inject constructor(
	private val service: CinemaService,
	private val mapper: CinemaMapper
) : CinemaRepository {

	override fun getActiveCinemas(): List<FrontendCinema> =
		service.getActiveCinemas()
			.map(mapper::map)

	override fun getCinemasAuth(userID: String): List<FrontendCinema> =
		service.getCinemasAuth(userID)
			.map(mapper::map)

	override fun getFavoriteCinemas(userID: String): List<FrontendCinema> =
		service.getFavoriteCinemas(userID)
			.map(mapper::map)

	override fun addFavorite(userID: String, cinema: Long): FrontendCinema? =
		service.addFavorite(userID, cinema)?.let { mapper.map(it) }

	override fun removeFavorite(userID: String, cinema: Long): FrontendCinema? =
		service.removeFavorite(userID, cinema)?.let { mapper.map(it) }
}
