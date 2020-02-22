package net.twisterrob.cinema.cineworld.quickbook

import java.net.URI

data class QuickbookFilmFull(
	override val edi: Long,
	override val title: String,
	val film_url: URI,
	val classification: String,
	val advisory: String?,
	val poster_url: URI,
	val still_url: URI?,
	val `3D`: Boolean,
	val imax: Boolean
) : QuickbookFilm
