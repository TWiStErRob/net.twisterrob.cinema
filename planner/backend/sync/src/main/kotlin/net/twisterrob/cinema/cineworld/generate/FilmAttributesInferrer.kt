package net.twisterrob.cinema.cineworld.generate

import net.twisterrob.cinema.database.model.Film
import javax.inject.Inject

class FilmAttributesInferrer @Inject constructor() {

	/** @see net.twisterrob.cinema.cineworld.sync.copyPropertiesFrom */
	fun infer(film: Film): List<String> =
		listOfNotNull(
			if (film.is3D) "3D" else null,
			if (film.isIMAX) "IMAX" else null,
			// See net.twisterrob.cinema.cineworld.sync.findFormat
			*when (film.format) {
				"IMAX2D" -> arrayOf("IMAX", "2D")
				"IMAX3D" -> arrayOf("IMAX", "3D")
				"IMAX" -> arrayOf("IMAX")
				else -> emptyArray()
			},
			*parseAttributesFromTitle(film.title).toTypedArray()
		).distinct().sorted()

	/** @see net.twisterrob.cinema.cineworld.sync.formatTitle */
	private fun parseAttributesFromTitle(title: String): List<String> =
		@Suppress("RegExpRedundantEscape")
		Regex("""^.*? \[(.*)\]$""")
			.find(title)
			?.let { it.groupValues[1].split(", ") }
			.orEmpty()
}
