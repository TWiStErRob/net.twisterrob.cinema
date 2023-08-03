package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.allMeet
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.iconEl
import net.twisterrob.cinema.frontend.test.framework.nameEl
import net.twisterrob.cinema.frontend.test.framework.noneMeet
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebElement

@ExtendWith(BrowserExtension::class)
class CinemasUiTest {

	private lateinit var app: PlannerPage

	private fun notFavoritedCinema(cinema: WebElement): Boolean =
		app.cinemas.favorites.items.none { it.text == cinema.text }

	private fun favoritedCinema(cinema: WebElement): Boolean =
		app.cinemas.favorites.items.any { it.text == cinema.text }

	private fun notLondonCinema(cinema: WebElement): Boolean =
		app.cinemas.london.items.none { it.text == cinema.text }

	private fun londonCinema(cinema: WebElement): Boolean =
		app.cinemas.london.items.any { it.text == cinema.text }

	@BeforeEach fun beforeEach() {
		app.goToPlanner()
	}

	@Nested
	inner class Initial {

		@Test fun `should show some London cinemas`() {
			assertThat(app.cinemas.london.items).isNotEmpty
			assertThat(app.cinemas.london.items).filteredOn(::notFavoritedCinema).allMeet { hasIcon("star-empty") }
			assertThat(app.cinemas.london.items).filteredOn(::favoritedCinema).allMeet { hasIcon("heart") }
		}

		@Test fun `should show some favorite cinemas`() {
			assertThat(app.cinemas.favorites.items).isNotEmpty
			assertThat(app.cinemas.favorites.items).allMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.items).allMeet { hasIcon("heart") }
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
				app.cinemas.favorites.expand()

				assertThat(app.cinemas.favorites.items).isNotEmpty
				assertThat(app.cinemas.favorites.items).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.favorites.collapse()

				assertThat(app.cinemas.favorites.items).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.favorites.expand()
				assertThat(app.cinemas.favorites.items).allMeet { isDisplayed() }
				app.cinemas.favorites.collapse()
				assertThat(app.cinemas.favorites.items).noneMeet { isDisplayed() }
				app.cinemas.favorites.expand()
				assertThat(app.cinemas.favorites.items).allMeet { isDisplayed() }
			}
		}

		@Nested
		inner class London {

			@Test fun `should have header`() {
				assertThat(app.cinemas.london.header).text().matches("""^London Cinemas \(\d+\)$""")
				val count = app.cinemas.london.items.count()
				assertThat(app.cinemas.london.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.cinemas.london.expand()

				assertThat(app.cinemas.london.items).isNotEmpty
				assertThat(app.cinemas.london.items).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.london.collapse()

				assertThat(app.cinemas.london.items).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.london.expand()
				assertThat(app.cinemas.london.items).allMeet { isDisplayed() }
				app.cinemas.london.collapse()
				assertThat(app.cinemas.london.items).noneMeet { isDisplayed() }
				app.cinemas.london.expand()
				assertThat(app.cinemas.london.items).allMeet { isDisplayed() }
			}
		}

		@Nested
		inner class Other {

			@Test fun `should have header`() {
				assertThat(app.cinemas.other.header).text().matches("""^Other Cinemas \(\d+\)$""")
				val count = app.cinemas.other.items.count()
				assertThat(app.cinemas.other.header).text().contains(count.toString())
			}

			@Test fun `should expand`() {
				app.cinemas.other.expand()

				assertThat(app.cinemas.other.items).allMeet { isDisplayed() }
			}

			@Test fun `should collapse`() {
				app.cinemas.other.collapse()

				assertThat(app.cinemas.other.items).noneMeet { isDisplayed() }
			}

			@Test fun `should toggle`() {
				app.cinemas.other.expand()
				assertThat(app.cinemas.other.items).allMeet { isDisplayed() }
				app.cinemas.other.collapse()
				assertThat(app.cinemas.other.items).noneMeet { isDisplayed() }
				app.cinemas.other.expand()
				assertThat(app.cinemas.other.items).allMeet { isDisplayed() }
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

			assertThat(app.cinemas.favorites.items).allMeet { isChecked() }
			assertThat(app.cinemas.london.items).allMeet { isChecked() }
			assertThat(app.cinemas.other.items).allMeet { isChecked() }
		}

		@Test fun `should select none`() {
			app.cinemas.buttons.none.click()

			assertThat(app.cinemas.favorites.items).noneMeet { isChecked() }
		}

		@Test fun `should select London cinemas only`() {
			app.cinemas.buttons.london.click()

			assertThat(app.cinemas.london.items).allMeet { isChecked() }
			assertThat(app.cinemas.favorites.items).filteredOn(::londonCinema).allMeet { isChecked() }
			assertThat(app.cinemas.favorites.items).filteredOn(::notLondonCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.items).noneMeet { isChecked() }
		}

		@Test fun `should display London cinemas`() {
			app.cinemas.favorites.collapse()
			app.cinemas.london.collapse()
			app.cinemas.other.collapse()

			app.cinemas.buttons.london.click()

			assertThat(app.cinemas.london.items).allMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.items).noneMeet { isDisplayed() }
			assertThat(app.cinemas.other.items).noneMeet { isDisplayed() }
		}

		@Test fun `should select favorite cinemas only`() {
			app.cinemas.buttons.favorites.click()

			assertThat(app.cinemas.favorites.items).allMeet { isChecked() }
			assertThat(app.cinemas.london.items).filteredOn(::favoritedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.london.items).filteredOn(::notFavoritedCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.items).filteredOn(::favoritedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.other.items).filteredOn(::notFavoritedCinema).noneMeet { isChecked() }
		}

		@Test fun `should display favorite cinemas`() {
			app.cinemas.favorites.collapse()
			app.cinemas.london.collapse()
			app.cinemas.other.collapse()

			app.cinemas.buttons.favorites.click()

			assertThat(app.cinemas.london.items).noneMeet { isDisplayed() }
			assertThat(app.cinemas.favorites.items).allMeet { isDisplayed() }
			assertThat(app.cinemas.other.items).noneMeet { isDisplayed() }
		}

		private fun cinemaListSanityCheck() {
			assertThat(app.cinemas.favorites.items).isNotEmpty
			assertThat(app.cinemas.london.items).isNotEmpty
			assertThat(app.cinemas.other.items).isEmpty()
		}
	}

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
			val cinema = app.cinemas.london.items[1]
			assertThat(app.cinemas.favorites.items).hasSize(1)

			cinema.iconEl.click()

			assertThat(app.cinemas.favorites.items).hasSize(2)
			assertThat(app.cinemas.favorites.items[1]).text().isEqualTo(cinema.nameEl.text)
			assertThat(app.cinemas.favorites.items[1]).hasIcon("heart")
		}

		@Disabled("Fake data is not consistent with backend")
		@Test fun `should allow removing from favorites`() {
			val cinema = app.cinemas.favorites.items.first()

			cinema.iconEl.click()

			assertThat(app.cinemas.favorites.items).isEmpty()
			assertThat(app.cinemas.london.items).noneMeet { hasIcon("heart") }
		}
	}
}
