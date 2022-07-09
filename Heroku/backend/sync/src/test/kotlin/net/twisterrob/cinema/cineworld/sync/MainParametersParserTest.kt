package net.twisterrob.cinema.cineworld.sync

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File

class MainParametersParserTest {

	private val sut = MainParametersParser()

	@Test fun `invalid argument throws`() {
		assertThrows<IllegalArgumentException> {
			sut.parse("hello")
		}
	}

	@Test fun `invalid argument among others doesn't fail`() {
		val ex = assertThrows<IllegalArgumentException> {
			sut.parse("cinemas", "hello", "films")
		}
		assertThat(ex.message, containsString("hello"))
	}

	@Test fun `performances sync everything`() {
		val result = sut.parse("performances")

		assertTrue(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertTrue(result.syncPerformances)
		assertNull(result.fromFolder)
	}

	@Test fun `'cinemas' sync cinemas`() {
		val result = sut.parse("cinemas")

		assertTrue(result.syncCinemas)
		assertFalse(result.syncFilms)
		assertFalse(result.syncPerformances)
		assertNull(result.fromFolder)
	}

	@Test fun `'films' sync films`() {
		val result = sut.parse("films")

		assertFalse(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertFalse(result.syncPerformances)
		assertNull(result.fromFolder)
	}

	@Test fun `'cinemas' and 'films' sync cinemas and films`() {
		val result = sut.parse("cinemas", "films")

		assertTrue(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertFalse(result.syncPerformances)
		assertNull(result.fromFolder)
	}

	@Test fun `'cinemas', 'films' and 'performances' sync those`() {
		val result = sut.parse("cinemas", "films", "performances")

		assertTrue(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertTrue(result.syncPerformances)
		assertNull(result.fromFolder)
	}

	@Test fun `'performances' from specific folder`(@TempDir tempDir: File) {
		val result = sut.parse("performances", "--folder=${tempDir.absolutePath}")

		assertTrue(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertTrue(result.syncPerformances)
		assertEquals(tempDir, result.fromFolder)
	}

	@Test fun `'films' and 'cinemas' from specific folder`(@TempDir tempDir: File) {
		val result = sut.parse("--folder=${tempDir.absolutePath}", "films", "cinemas")

		assertTrue(result.syncCinemas)
		assertTrue(result.syncFilms)
		assertFalse(result.syncPerformances)
		assertEquals(tempDir, result.fromFolder)
	}
}
