package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BaseInteractivePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.allMeet
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.noneMeet
import net.twisterrob.cinema.frontend.test.framework.not
import net.twisterrob.cinema.frontend.test.pages.planner.Cinema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CinemasUiTest : BaseInteractivePlannerUiTest() {

	private fun favoritedCinema(cinema: Cinema): Boolean =
		app.cinemas.favorites.cinemas.any { it.name.text == cinema.name.text }

	private fun londonCinema(cinema: Cinema): Boolean =
		app.cinemas.london.cinemas.any { it.name.text == cinema.name.text }

	@Nested
	inner class Initial {

		@Test fun `should show some London cinemas`() {
			assertThat(app.cinemas.london.cinemas).isNotEmpty
			assertThat(app.cinemas.london.cinemas).filteredOn(!::favoritedCinema)
				.allSatisfy { assertThat(it.icon).hasIcon("star-empty") }
			assertThat(app.cinemas.london.cinemas).filteredOn(::favoritedCinema)
				.allSatisfy { assertThat(it.icon).hasIcon("heart") }
		}

		@Test fun `should show some favorite cinemas`() {
			assertThat(app.cinemas.favorites.cinemas).isNotEmpty
			assertThat(app.cinemas.favorites.cinemas).allMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.cinemas).allSatisfy { assertThat(it.icon).hasIcon("heart") }
		}

		@Test fun `should show no other cinemas`() {
			assertThat(app.cinemas.other.items).isEmpty()
		}
	}

	@Nested
	inner class Accordions {

		@Nested
		inner class Favorites {

			@Test fun `should have header`() {
				assertThat(app.cinemas.favorites.header).text().matches("""^Favorite Cinemas \(\d+\)$""")
				val count = app.cinemas.favorites.items.count()
				assertThat(app.cinemas.favorites.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.cinemas.favorites.tryExpand()

				assertThat(app.cinemas.favorites.cinemas).isNotEmpty
				assertThat(app.cinemas.favorites.cinemas).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.favorites.tryCollapse()

				assertThat(app.cinemas.favorites.cinemas).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.favorites.tryExpand()
				assertThat(app.cinemas.favorites.cinemas).allMeet { isDisplayed() }
				app.cinemas.favorites.tryCollapse()
				assertThat(app.cinemas.favorites.cinemas).noneMeet { isDisplayed() }
				app.cinemas.favorites.tryExpand()
				assertThat(app.cinemas.favorites.cinemas).allMeet { isDisplayed() }
			}
		}

		@Nested
		inner class London {

			@Test fun `should have header`() {
				assertThat(app.cinemas.london.header).text().matches("""^London Cinemas \(\d+\)$""")
				val count = app.cinemas.london.cinemas.count()
				assertThat(app.cinemas.london.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.cinemas.london.tryExpand()

				assertThat(app.cinemas.london.cinemas).isNotEmpty
				assertThat(app.cinemas.london.cinemas).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.london.tryCollapse()

				assertThat(app.cinemas.london.cinemas).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.london.tryExpand()
				assertThat(app.cinemas.london.cinemas).allMeet { isDisplayed() }
				app.cinemas.london.tryCollapse()
				assertThat(app.cinemas.london.cinemas).noneMeet { isDisplayed() }
				app.cinemas.london.tryExpand()
				assertThat(app.cinemas.london.cinemas).allMeet { isDisplayed() }
			}
		}

		@Nested
		inner class Other {

			@Test fun `should have header`() {
				assertThat(app.cinemas.other.header).text().matches("""^Other Cinemas \(\d+\)$""")
				val count = app.cinemas.other.cinemas.count()
				assertThat(app.cinemas.other.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.cinemas.other.tryExpand()

				assertThat(app.cinemas.other.cinemas).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.other.tryCollapse()

				assertThat(app.cinemas.other.cinemas).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.other.tryExpand()
				assertThat(app.cinemas.other.cinemas).allMeet { isDisplayed() }
				app.cinemas.other.tryCollapse()
				assertThat(app.cinemas.other.cinemas).noneMeet { isDisplayed() }
				app.cinemas.other.tryExpand()
				assertThat(app.cinemas.other.cinemas).allMeet { isDisplayed() }
			}
		}
	}

	@Nested
	inner class SelectionButtons {

		@BeforeEach fun beforeEach() {
			cinemaListSanityCheck()
		}

		@AfterEach fun afterEach() {
			cinemaListSanityCheck()
		}

		@Test fun `should select all`() {
			app.cinemas.buttons.all.click()

			assertThat(app.cinemas.favorites.cinemas).allMeet { isChecked() }
			assertThat(app.cinemas.london.cinemas).allMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).allMeet { isChecked() }
		}

		@Test fun `should select none`() {
			app.cinemas.buttons.none.click()

			assertThat(app.cinemas.favorites.cinemas).noneMeet { isChecked() }
		}

		@Test fun `should select London cinemas only`() {
			app.cinemas.buttons.london.click()

			assertThat(app.cinemas.london.cinemas).allMeet { isChecked() }
			assertThat(app.cinemas.favorites.cinemas).filteredOn(::londonCinema).allMeet { isChecked() }
			assertThat(app.cinemas.favorites.cinemas).filteredOn(!::londonCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).noneMeet { isChecked() }
		}

		@Test fun `should display London cinemas`() {
			app.cinemas.favorites.tryCollapse()
			app.cinemas.london.tryCollapse()
			app.cinemas.other.tryCollapse()

			app.cinemas.buttons.london.click()

			assertThat(app.cinemas.london.cinemas).allMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.cinemas).noneMeet { isDisplayed() }
			assertThat(app.cinemas.other.cinemas).noneMeet { isDisplayed() }
		}

		@Test fun `should select favorite cinemas only`() {
			app.cinemas.buttons.favorites.click()

			assertThat(app.cinemas.favorites.cinemas).allMeet { isChecked() }
			assertThat(app.cinemas.london.cinemas).filteredOn(::favoritedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.london.cinemas).filteredOn(!::favoritedCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).filteredOn(::favoritedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).filteredOn(!::favoritedCinema).noneMeet { isChecked() }
		}

		@Test fun `should display favorite cinemas`() {
			app.cinemas.favorites.tryCollapse()
			app.cinemas.london.tryCollapse()
			app.cinemas.other.tryCollapse()

			app.cinemas.buttons.favorites.click()

			assertThat(app.cinemas.london.cinemas).noneMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.cinemas).allMeet { isDisplayed() }
			assertThat(app.cinemas.other.cinemas).noneMeet { isDisplayed() }
		}

		private fun cinemaListSanityCheck() {
			assertThat(app.cinemas.favorites.cinemas).isNotEmpty
			assertThat(app.cinemas.london.cinemas).isNotEmpty
			assertThat(app.cinemas.other.cinemas).isEmpty()
		}
	}

	/**
	 * TOFIX Tried these: https://stackoverflow.com/q/60117232 + https://www.protractortest.org/#/browser-setup#adding-chrome-specific-options, but no luck.
	 */
	@Nested
	@Disabled("Login doesn't work: Access blocked: This app’s request is invalid Error 400: redirect_uri_mismatch")
	inner class Authenticated {

		@BeforeEach fun beforeEach() {
			app.login(Options.userName, Options.userPass)
			app.goToPlanner()
		}

		@AfterEach fun afterAll() {
			app.logout()
		}

		@Test fun `should allow adding to favorites`() {
			val cinema = app.cinemas.london[1]
			assertThat(app.cinemas.favorites.cinemas).hasSize(1)

			cinema.favorite()

			assertThat(app.cinemas.favorites.cinemas).hasSize(2)
			assertThat(app.cinemas.favorites.cinemas[1].name).text().isEqualTo(cinema.name.text)
			assertThat(app.cinemas.favorites.cinemas[1].icon).hasIcon("heart")
		}

		@Disabled("Fake data is not consistent with backend")
		@Test fun `should allow removing from favorites`() {
			val cinema = app.cinemas.favorites.cinemas.first()

			cinema.unfavorite()

			assertThat(app.cinemas.favorites.cinemas).isEmpty()
			assertThat(app.cinemas.london.cinemas).noneSatisfy { assertThat(it.icon).hasIcon("heart") }
		}
	}
}
