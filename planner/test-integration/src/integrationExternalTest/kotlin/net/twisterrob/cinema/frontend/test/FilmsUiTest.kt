package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BaseInteractivePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.allMeet
import net.twisterrob.cinema.frontend.test.framework.noneMeet
import net.twisterrob.cinema.frontend.test.pages.planner.Film
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FilmsUiTest : BaseInteractivePlannerUiTest() {

	private fun watchedFilm(film: Film): Boolean =
		app.films.watched.films.any { it.name.text == film.name.text }

	private fun newFilm(film: Film): Boolean =
		app.films.new.films.any { it.name.text == film.name.text }

	@Nested
	inner class Initial {

		@Test fun `should show some new films`() {
			assertThat(app.films.new.films).isNotEmpty()
			assertThat(app.films.new.films).allSatisfy { assertThat(it.icon).hasIcon("eye-open") }
		}

		@Test fun `should show some watched films`() {
			app.films.watched.tryExpand()

			assertThat(app.films.watched.films).isNotEmpty()
			assertThat(app.films.watched.films).allSatisfy { assertThat(it.icon).hasIcon("eye-close") }
		}
	}

	@Nested
	inner class Accordions {

		@Nested
		inner class New {

			@Test fun `should have header`() {
				assertThat(app.films.new.header).text().matches("""^New Films \(\d+\)$""")
				val count = app.films.new.films.count()
				assertThat(app.films.new.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.films.new.tryExpand()

				assertThat(app.films.new.films).isNotEmpty()
				assertThat(app.films.new.films).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.films.new.tryCollapse()

				assertThat(app.films.new.films).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.films.new.tryExpand()
				assertThat(app.films.new.films).allMeet { isDisplayed() }
				app.films.new.tryCollapse()
				assertThat(app.films.new.films).noneMeet { isDisplayed() }
				app.films.new.tryExpand()
				assertThat(app.films.new.films).allMeet { isDisplayed() }
			}
		}

		@Nested
		inner class Watched {

			@Test fun `should have header`() {
				assertThat(app.films.watched.header).text().matches("""^Watched \(\d+\)$""")
				val count = app.films.watched.films.count()
				assertThat(app.films.watched.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.films.watched.tryExpand()

				assertThat(app.films.watched.films).isNotEmpty()
				assertThat(app.films.watched.films).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.films.watched.tryCollapse()

				assertThat(app.films.watched.films).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.films.watched.tryExpand()
				assertThat(app.films.watched.films).allMeet { isDisplayed() }
				app.films.watched.tryCollapse()
				assertThat(app.films.watched.films).noneMeet { isDisplayed() }
				app.films.watched.tryExpand()
				assertThat(app.films.watched.films).allMeet { isDisplayed() }
			}
		}
	}

	@Nested
	inner class Selection {

		@BeforeEach fun beforeEach() {
			filmListSanityCheck()
		}

		@AfterEach fun afterEach() {
			filmListSanityCheck()
		}

		@Test fun `should select all`() {
			app.films.buttons.all.click()

			assertThat(app.films.watched.films).allMeet { isChecked() }
			assertThat(app.films.new.films).allMeet { isChecked() }
		}

		@Test fun `should select none`() {
			app.films.watched.tryExpand()

			app.films.buttons.none.click()

			assertThat(app.films.watched.films).noneMeet { isChecked() }
			assertThat(app.films.new.films).noneMeet { isChecked() }
		}

		@Test fun `should select new films only`() {
			app.films.watched.tryExpand()

			app.films.buttons.new.click()

			assertThat(app.films.new.films).allMeet { isChecked() }
			assertThat(app.films.watched.films).noneMeet { isChecked() }
		}

		@Test fun `should display new films`() {
			app.films.new.tryCollapse()
			app.films.watched.tryCollapse()

			app.films.buttons.new.click()

			assertThat(app.films.new.films).allMeet { isDisplayed() }
			assertThat(app.films.watched.films).noneMeet { isDisplayed() }
		}

		private fun filmListSanityCheck() {
			// not empty test data
			assertThat(app.films.new.films).isNotEmpty()
			assertThat(app.films.watched.films).isNotEmpty()

			// distinct films
			// TODO this fails weirdly if neither list is visible (accordions collapsed)
			assertThat(app.films.new.films).filteredOn(::watchedFilm).isEmpty()
			assertThat(app.films.watched.films).filteredOn(::newFilm).isEmpty()

			// correct icons
			assertThat(app.films.new.films).allSatisfy { assertThat(it.icon).hasIcon("eye-open") }
			assertThat(app.films.watched.films).allSatisfy { assertThat(it.icon).hasIcon("eye-close") }
		}
	}
}
