package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.hasIcon
import net.twisterrob.cinema.frontend.test.framework.hasSelection
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebElement

@ExtendWith(BrowserExtension::class)
class FilmsUiTest {

	private lateinit var app: PlannerPage

	private fun watchedFilm(film: WebElement): Boolean =
		app.films.watched.items.any { it.text == film.text }

	private fun newFilm(film: WebElement): Boolean =
		app.films.new.items.any { it.text == film.text }

	@BeforeEach fun beforeEach() {
		app.goToPlanner()
	}

	@Nested
	inner class Initial {

		@Test fun `should show some new films`() {
			assertThat(app.films.new.items).isNotEmpty()
			assertThat(app.films.new.items).allMatch { it.hasIcon("eye-open") }
		}

		@Test fun `should show some watched films`() {
			app.films.watched.expand()

			assertThat(app.films.watched.items).isNotEmpty()
			assertThat(app.films.watched.items).allMatch { it.hasIcon("eye-close") }
		}
	}

	@Nested
	inner class Accordions {

		@Nested
		inner class New {

			@Test fun `should have header`() {
				assertThat(app.films.new.header).text().matches("""^New Films \(\d+\)$""")
				val count = app.films.new.items.count()
				assertThat(app.films.new.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.films.new.expand()

				assertThat(app.films.new.items).isNotEmpty()
				assertThat(app.films.new.items).allMatch { it.isDisplayed }
			}

			@Test fun `should collapse`() {
				app.films.new.collapse()

				assertThat(app.films.new.items).noneMatch { it.isDisplayed }
			}

			@Test fun `should toggle`() {
				app.films.new.expand()
				assertThat(app.films.new.items).allMatch { it.isDisplayed }
				app.films.new.collapse()
				assertThat(app.films.new.items).noneMatch { it.isDisplayed }
				app.films.new.expand()
				assertThat(app.films.new.items).allMatch { it.isDisplayed }
			}
		}

		@Nested
		inner class Watched {

			@Test fun `should have header`() {
				assertThat(app.films.watched.header).text().matches("""^Watched \(\d+\)$""")
				val count = app.films.watched.items.count()
				assertThat(app.films.watched.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.films.watched.expand()

				assertThat(app.films.watched.items).isNotEmpty()
				assertThat(app.films.watched.items).allMatch { it.isDisplayed }
			}

			@Test fun `should collapse`() {
				app.films.watched.collapse()

				assertThat(app.films.watched.items).noneMatch { it.isDisplayed }
			}

			@Test fun `should toggle`() {
				app.films.watched.expand()
				assertThat(app.films.watched.items).allMatch { it.isDisplayed }
				app.films.watched.collapse()
				assertThat(app.films.watched.items).noneMatch { it.isDisplayed }
				app.films.watched.expand()
				assertThat(app.films.watched.items).allMatch { it.isDisplayed }
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

			assertThat(app.films.watched.items).allMatch { it.hasSelection() }
			assertThat(app.films.new.items).allMatch { it.hasSelection() }
		}

		@Test fun `should select none`() {
			app.films.watched.expand()

			app.films.buttons.none.click()

			assertThat(app.films.watched.items).noneMatch { it.hasSelection() }
			assertThat(app.films.new.items).noneMatch { it.hasSelection() }
		}

		@Test fun `should select new films only`() {
			app.films.watched.expand()

			app.films.buttons.new.click()

			assertThat(app.films.new.items).allMatch { it.hasSelection() }
			assertThat(app.films.watched.items).noneMatch { it.hasSelection() }
		}

		@Test fun `should display new films`() {
			app.films.new.collapse()
			app.films.watched.collapse()

			app.films.buttons.new.click()

			assertThat(app.films.new.items).allMatch { it.isDisplayed }
			assertThat(app.films.watched.items).noneMatch { it.isDisplayed }
		}

		private fun filmListSanityCheck() {
			// not empty test data
			assertThat(app.films.new.items).isNotEmpty()
			assertThat(app.films.watched.items).isNotEmpty()

			// distinct films
			// TODO this fails weirdly if neither list is visible (accordions collapsed)
			assertThat(app.films.new.items).filteredOn(::watchedFilm).isEmpty()
			assertThat(app.films.watched.items).filteredOn(::newFilm).isEmpty()

			// correct icons
			assertThat(app.films.new.items).allMatch { it.hasIcon("eye-open") }
			assertThat(app.films.watched.items).allMatch { it.hasIcon("eye-close") }
		}
	}
}
