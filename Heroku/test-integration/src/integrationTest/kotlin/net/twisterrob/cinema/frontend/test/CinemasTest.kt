@file:Suppress("RemoveRedundantBackticks", "ClassName")

package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.anyWithText
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.hasIcon
import net.twisterrob.cinema.frontend.test.framework.hasSelection
import net.twisterrob.cinema.frontend.test.framework.noneWithText
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebElement

@ExtendWith(BrowserExtension::class)
class CinemasTest {

	lateinit var app: PlannerPage

	fun notFavoritedCinema(cinema: WebElement): Boolean =
		noneWithText(app.cinemas.favorites.items, cinema.text)

	fun favoritedCinema(cinema: WebElement): Boolean =
		anyWithText(app.cinemas.favorites.items, cinema.text)

	fun notLondonCinema(cinema: WebElement): Boolean =
		noneWithText(app.cinemas.london.items, cinema.text)

	fun londonCinema(cinema: WebElement): Boolean =
		anyWithText(app.cinemas.london.items, cinema.text)

	@Nested
	inner class `Cinemas display` {

		@BeforeEach fun beforeEach() {
			app.goToPlanner()
		}

		@Nested
		inner class `basics` {

			@Test fun `should show some London cinemas`() {
				assertThat(app.cinemas.london.items).isNotEmpty
				assertThat(app.cinemas.london.items.filter(::notFavoritedCinema)).allMatch { it.hasIcon("star-empty") }
				assertThat(app.cinemas.london.items.filter(::favoritedCinema)).allMatch { it.hasIcon("heart") }
			}

			@Test fun `should show some favorite cinemas`() {
				assertThat(app.cinemas.favorites.items).isNotEmpty
				assertThat(app.cinemas.favorites.items).allMatch { it.isDisplayed }
				assertThat(app.cinemas.favorites.items).allMatch { it.hasIcon("heart") }
			}

			@Test fun `should show no other cinemas`() {
				assertThat(app.cinemas.other.items).isEmpty()
			}
		}

		@Nested
		inner class `accordions` {

			@Nested
			inner class `favorites` {

				@Test fun `should have header`() {
					assertThat(app.cinemas.favorites.header).text().matches("""^Favorite Cinemas \(\d+\)$""")
					val count = app.cinemas.favorites.items.count()
					assertThat(app.cinemas.favorites.header).text().contains(count.toString())
				}

				@Test fun `should expand`() {
					app.cinemas.favorites.expand()

					assertThat(app.cinemas.favorites.items).isNotEmpty
					assertThat(app.cinemas.favorites.items).allMatch { it.isDisplayed }
				}

				@Test fun `should collapse`() {
					app.cinemas.favorites.collapse()

					assertThat(app.cinemas.favorites.items).noneMatch { it.isDisplayed }
				}

				@Test fun `should toggle`() {
					app.cinemas.favorites.expand()
					assertThat(app.cinemas.favorites.items).allMatch { it.isDisplayed }
					app.cinemas.favorites.collapse()
					assertThat(app.cinemas.favorites.items).noneMatch { it.isDisplayed }
					app.cinemas.favorites.expand()
					assertThat(app.cinemas.favorites.items).allMatch { it.isDisplayed }
				}
			}

			@Nested
			inner class `london` {

				@Test fun `should have header`() {
					assertThat(app.cinemas.london.header).text().matches("""^London Cinemas \(\d+\)$""")
					val count = app.cinemas.london.items.count()
					assertThat(app.cinemas.london.header).text().contains(count.toString())
				}

				@Test fun `should expand`() {
					app.cinemas.london.expand()

					assertThat(app.cinemas.london.items).isNotEmpty
					assertThat(app.cinemas.london.items).allMatch { it.isDisplayed }
				}

				@Test fun `should collapse`() {
					app.cinemas.london.collapse()

					assertThat(app.cinemas.london.items).noneMatch { it.isDisplayed }
				}

				@Test fun `should toggle`() {
					app.cinemas.london.expand()
					assertThat(app.cinemas.london.items).allMatch { it.isDisplayed }
					app.cinemas.london.collapse()
					assertThat(app.cinemas.london.items).noneMatch { it.isDisplayed }
					app.cinemas.london.expand()
					assertThat(app.cinemas.london.items).allMatch { it.isDisplayed }
				}
			}

			@Nested
			inner class `other` {

				@Test fun `should have header`() {
					assertThat(app.cinemas.other.header).text().matches("""^Other Cinemas \(\d+\)$""")
					val count = app.cinemas.other.items.count()
					assertThat(app.cinemas.other.header).text().contains(count.toString())
				}

				@Test fun `should expand`() {
					app.cinemas.other.expand()

					assertThat(app.cinemas.other.items).allMatch { it.isDisplayed }
				}

				@Test fun `should collapse`() {
					app.cinemas.other.collapse()

					assertThat(app.cinemas.other.items).noneMatch { it.isDisplayed }
				}

				@Test fun `should toggle`() {
					app.cinemas.other.expand()
					assertThat(app.cinemas.other.items).allMatch { it.isDisplayed }
					app.cinemas.other.collapse()
					assertThat(app.cinemas.other.items).noneMatch { it.isDisplayed }
					app.cinemas.other.expand()
					assertThat(app.cinemas.other.items).allMatch { it.isDisplayed }
				}
			}
		}

		@Nested
		inner class `selection buttons` {

			@BeforeEach fun beforeEach() {
				cinemaListSanityCheck()
			}

			@AfterEach fun afterEach() {
				cinemaListSanityCheck()
			}

			@Test fun `should select all`() {
				app.cinemas.buttons.all.click()

				assertThat(app.cinemas.favorites.items).allMatch { it.hasSelection() }
				assertThat(app.cinemas.london.items).allMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items).allMatch { it.hasSelection() }
			}

			@Test fun `should select none`() {
				app.cinemas.buttons.none.click()

				assertThat(app.cinemas.favorites.items).noneMatch { it.hasSelection() }
			}

			@Test fun `should select London cinemas only`() {
				app.cinemas.buttons.london.click()

				assertThat(app.cinemas.london.items).allMatch { it.hasSelection() }
				assertThat(app.cinemas.favorites.items.filter(::londonCinema)).allMatch { it.hasSelection() }
				assertThat(app.cinemas.favorites.items.filter(::notLondonCinema)).noneMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items).noneMatch { it.hasSelection() }
			}

			@Test fun `should display London cinemas`() {
				app.cinemas.favorites.collapse()
				app.cinemas.london.collapse()
				app.cinemas.other.collapse()

				app.cinemas.buttons.london.click()

				assertThat(app.cinemas.london.items).allMatch { it.isDisplayed }
				assertThat(app.cinemas.favorites.items).noneMatch { it.isDisplayed }
				assertThat(app.cinemas.other.items).noneMatch { it.isDisplayed }
			}

			@Test fun `should select favorite cinemas only`() {
				app.cinemas.buttons.favorites.click()

				assertThat(app.cinemas.favorites.items).allMatch { it.hasSelection() }
				assertThat(app.cinemas.london.items.filter(::favoritedCinema)).allMatch { it.hasSelection() }
				assertThat(app.cinemas.london.items.filter(::notFavoritedCinema)).noneMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items.filter(::favoritedCinema)).allMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items.filter(::notFavoritedCinema)).noneMatch { it.hasSelection() }
			}

			@Test fun `should display favorite cinemas`() {
				app.cinemas.favorites.collapse()
				app.cinemas.london.collapse()
				app.cinemas.other.collapse()

				app.cinemas.buttons.favorites.click()

				assertThat(app.cinemas.london.items).noneMatch { it.isDisplayed }
				assertThat(app.cinemas.favorites.items).allMatch { it.isDisplayed }
				assertThat(app.cinemas.other.items).noneMatch { it.isDisplayed }
			}

			private fun cinemaListSanityCheck() {
				assertThat(app.cinemas.favorites.items).isNotEmpty
				assertThat(app.cinemas.london.items).isNotEmpty
				assertThat(app.cinemas.other.items).isEmpty()
			}
		}
	}
}
