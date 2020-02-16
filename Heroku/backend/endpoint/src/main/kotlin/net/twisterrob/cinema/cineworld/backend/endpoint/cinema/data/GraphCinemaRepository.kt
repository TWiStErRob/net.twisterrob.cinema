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

	override fun getCinemasAuth(userID: Long): List<FrontendCinema> =
		service.getCinemasAuth(userID.toString())
			.map(mapper::map)

	override fun getFavoriteCinemas(userID: Long): List<FrontendCinema> =
		service.getFavoriteCinemas(userID.toString())
			.map(mapper::map)
}

class CinemaMapper @Inject constructor(
) {

	fun map(db: DBCinema): FrontendCinema =
		FrontendCinema(
			name = db.name
		)

	fun map(favoritedDB: Map.Entry<DBCinema, Boolean>): FrontendCinema =
		FrontendCinema(
			name = favoritedDB.key.name,
			fav = favoritedDB.value
		)
}
