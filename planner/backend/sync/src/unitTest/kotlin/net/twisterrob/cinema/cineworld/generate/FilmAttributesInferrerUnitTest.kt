package net.twisterrob.cinema.cineworld.generate

import net.twisterrob.cinema.database.model.Film
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FilmAttributesInferrerUnitTest {

	private val sut = FilmAttributesInferrer()

	@Test fun `title without brackets adds nothing`() {
		val film = film(title = "Some Film")

		val result = sut.infer(film)

		assertEquals(emptyList<String>(), result)
	}

	@Test fun `title with bracketed attribute parses`() {
		val film = film(title = "Some Film [AD]")

		val result = sut.infer(film)

		assertEquals(listOf("AD"), result)
	}

	@Test fun `title with bracketed attributes parses and sorts`() {
		val film = film(title = "Some Film [AD, 4DX, AC]")

		val result = sut.infer(film)

		assertEquals(listOf("4DX", "AC", "AD"), result)
	}

	@Test fun `is3D flag adds 3D`() {
		val film = film(is3D = true)

		val result = sut.infer(film)

		assertEquals(listOf("3D"), result)
	}

	@Test fun `isIMAX flag adds IMAX`() {
		val film = film(isIMAX = true)

		val result = sut.infer(film)

		assertEquals(listOf("IMAX"), result)
	}

	@Test fun `is3D and isIMAX flags add both attributes sorted`() {
		val film = film(is3D = true, isIMAX = true)

		val result = sut.infer(film)

		assertEquals(listOf("3D", "IMAX"), result)
	}

	@Test fun `format IMAX2D adds IMAX and 2D`() {
		val film = film(format = "IMAX2D")

		val result = sut.infer(film)

		assertEquals(listOf("2D", "IMAX"), result)
	}

	@Test fun `format IMAX2D adds IMAX and 2D, but doesn't duplicate`() {
		val film = film(format = "IMAX2D", isIMAX = true)

		val result = sut.infer(film)

		assertEquals(listOf("2D", "IMAX"), result)
	}

	@Test fun `format IMAX3D adds IMAX and 3D`() {
		val film = film(format = "IMAX3D")

		val result = sut.infer(film)

		assertEquals(listOf("3D", "IMAX"), result)
	}

	@Test fun `format IMAX3D adds IMAX and 3D, but doesn't duplicate`() {
		val film = film(format = "IMAX3D", isIMAX = true, is3D = true)

		val result = sut.infer(film)

		assertEquals(listOf("3D", "IMAX"), result)
	}

	@Test fun `format IMAX adds only IMAX`() {
		val film = film(format = "IMAX")

		val result = sut.infer(film)

		assertEquals(listOf("IMAX"), result)
	}

	@Test fun `format IMAX adds only IMAX, but doesn't duplicate`() {
		val film = film(format = "IMAX", isIMAX = true)

		val result = sut.infer(film)

		assertEquals(listOf("IMAX"), result)
	}

	@Test fun `unknown format adds nothing`() {
		val film = film(format = "4DX")

		val result = sut.infer(film)

		assertEquals(emptyList<String>(), result)
	}

	@Test fun `duplicate attributes from flags and title are deduplicated`() {
		val film = film(is3D = true, title = "Some Film [3D, Superscreen]")

		val result = sut.infer(film)

		assertEquals(listOf("3D", "Superscreen"), result)
	}

	@Test fun `duplicate attributes from format and is3D are deduplicated`() {
		val film = film(is3D = true, format = "IMAX3D")

		val result = sut.infer(film)

		assertEquals(listOf("3D", "IMAX"), result)
	}

	@Test fun `all sources combined and deduplicated`() {
		val film = film(title = "Some Film [3D, Superscreen]", format = "IMAX3D", is3D = true, isIMAX = true)

		val result = sut.infer(film)

		assertEquals(listOf("3D", "IMAX", "Superscreen"), result)
	}

	private fun film(
		title: String = "Some Film",
		is3D: Boolean = false,
		isIMAX: Boolean = false,
		format: String = "",
	): Film = Film().apply {
		this.title = title
		this.is3D = is3D
		this.isIMAX = isIMAX
		this.format = format
	}
}
