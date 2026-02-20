package net.twisterrob.cinema.cineworld.sync

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("detekt.UnnecessaryInnerClass") // TODEL https://github.com/detekt/detekt/issues/9072
class FilmPropertiesUpdaterKtUnitTest {

	@Nested
	inner class FormatTitle {

		@Test fun `empty title returns empty`() {
			val result = formatTitle("", emptyList())

			assertEquals("", result)
		}

		@Test fun `no attributes returns title unchanged`() {
			val result = formatTitle("Some Film", emptyList())

			assertEquals("Some Film", result)
		}

		@Test fun `genre attributes are filtered out`() {
			val result = formatTitle("Some Film", listOf("gn:drama", "gn:comedy"))

			assertEquals("Some Film", result)
		}

		@Test fun `2D attribute is filtered out`() {
			val result = formatTitle("Some Film", listOf("2D"))

			assertEquals("Some Film", result)
		}

		@Test fun `only genre and 2D attributes produce no bracket suffix`() {
			val result = formatTitle("Some Film", listOf("2D", "gn:drama"))

			assertEquals("Some Film", result)
		}

		@Test fun `single format attribute is appended in brackets`() {
			val result = formatTitle("Some Film", listOf("3D"))

			assertEquals("Some Film [3D]", result)
		}

		@Test fun `multiple format attributes are sorted and appended in brackets`() {
			val result = formatTitle("Some Film", listOf("IMAX", "3D"))

			assertEquals("Some Film [3D, IMAX]", result)
		}

		@Test fun `format attributes are sorted alphabetically`() {
			val result = formatTitle("Some Film", listOf("Superscreen", "AD", "3D"))

			assertEquals("Some Film [3D, AD, Superscreen]", result)
		}

		@Test fun `2d attribute excluded while format attributes included`() {
			val result = formatTitle("Some Film", listOf("2D", "IMAX", "AD"))

			assertEquals("Some Film [AD, IMAX]", result)
		}

		@Test fun `genre attributes excluded while format attributes included`() {
			val result = formatTitle("Some Film", listOf("gn:drama", "3D", "IMAX"))

			assertEquals("Some Film [3D, IMAX]", result)
		}

		@Test fun `parenthesised prefix in title is stripped`() {
			val result = formatTitle("(Subtitled) Some Film (Telugu)", emptyList())

			assertEquals("Some Film (Telugu)", result)
		}

		@Test fun `existing bracket suffix is replaced with new attributes`() {
			val result = formatTitle("Some Film (Telugu) [3D]", listOf("IMAX"))

			assertEquals("Some Film (Telugu) [IMAX]", result)
		}

		@Test fun `existing bracket suffix is removed when no format attributes remain`() {
			val result = formatTitle("Some Film (Telugu) [3D]", emptyList())

			assertEquals("Some Film (Telugu)", result)
		}

		@Test fun `parenthesised prefix and last bracket suffix are stripped`() {
			val result = formatTitle("(IMAX) Some Film (Telugu) [SS, ST]", listOf("IMAX", "SS", "ST"))

			assertEquals("Some Film (Telugu) [IMAX, SS, ST]", result)
		}

		@Test fun `only last bracket suffix is stripped when title contains multiple bracket groups`() {
			val result = formatTitle("(IMAX) Some Film (Telugu) [foo] [SS, ST]", listOf("3D"))

			assertEquals("Some Film (Telugu) [foo] [3D]", result)
		}

		@Test fun `empty bracket suffix is replaced by format attributes`() {
			val result = formatTitle("(IMAX) Some Film (Telugu) []", listOf("IMAX"))

			assertEquals("Some Film (Telugu) [IMAX]", result)
		}

		@Test fun `parenthesised prefix is stripped when there is no bracket suffix`() {
			val result = formatTitle("(IMAX) Some Film (Telugu)", listOf("3D"))

			assertEquals("Some Film (Telugu) [3D]", result)
		}

		@Test fun `empty bracket suffix is removed when no format attributes remain`() {
			val result = formatTitle("Some Film (Telugu) []", emptyList())

			assertEquals("Some Film (Telugu)", result)
		}

		@Test fun `weird bracket suffix is removed`() {
			val result = formatTitle("Some Film [SS] [] []", listOf("3D"))

			assertEquals("Some Film [3D]", result)
		}

		@Test fun `bracket suffix is re-added identically when attributes are unchanged`() {
			val result = formatTitle("Some Film (Telugu) [SS]", listOf("SS"))

			assertEquals("Some Film (Telugu) [SS]", result)
		}
	}

	@Nested
	inner class FindFormat {

		@Test fun `no attributes returns empty`() {
			val result = findFormat(emptyList())

			assertEquals("", result)
		}

		@Test fun `unrecognised attributes return empty`() {
			val result = findFormat(listOf("SS", "AD", "gn:drama"))

			assertEquals("", result)
		}

		@Test fun `2D returns 2D`() {
			val result = findFormat(listOf("2D"))

			assertEquals("2D", result)
		}

		@Test fun `3D returns 3D`() {
			val result = findFormat(listOf("3D"))

			assertEquals("3D", result)
		}

		@Test fun `IMAX returns IMAX`() {
			val result = findFormat(listOf("IMAX"))

			assertEquals("IMAX", result)
		}

		@Test fun `IMAX with 2D returns IMAX2D`() {
			val result = findFormat(listOf("IMAX", "2D"))

			assertEquals("IMAX2D", result)
		}

		@Test fun `IMAX with 3D returns IMAX3D`() {
			val result = findFormat(listOf("IMAX", "3D"))

			assertEquals("IMAX3D", result)
		}

		@Test fun `IMAX with 3D takes priority over 2D`() {
			val result = findFormat(listOf("IMAX", "3D", "2D"))

			assertEquals("IMAX3D", result)
		}

		@Test fun `other attributes alongside IMAX are ignored`() {
			val result = findFormat(listOf("IMAX", "SS", "AD"))

			assertEquals("IMAX", result)
		}

		@Test fun `other attributes alongside 3D are ignored`() {
			val result = findFormat(listOf("3D", "SS", "AD"))

			assertEquals("3D", result)
		}
	}
}
