@file:Suppress("RemoveRedundantBackticks", "ClassName")

package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.iconEl
import net.twisterrob.cinema.frontend.test.framework.nameEl2
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BrowserExtension::class)
class DialogsUiTest {

	lateinit var app: PlannerPage

	@Nested
	inner class `Dialogs` {

		@BeforeEach fun beforeEach() {
			app.goToPlanner()
		}

		@Test fun `opens calendar picker`() {
			assertThat(app.date.buttons.change).isEnabled()

			app.date.buttons.change.click()

			assertThat(app.date.buttons.done).isDisplayed()
		}

		@Test fun `can add arbitrary view`() {
			assertThat(app.films.buttons.addView).isEnabled()

			app.films.buttons.addView.click()

			assertThat(app.films.addViewDialog.element).isDisplayed()
			assertThat(app.films.addViewDialog.header).text().matches("""^View of""")
			assertThat(app.films.addViewDialog.buttons.add).isDisplayed()
			assertThat(app.films.addViewDialog.buttons.cancel).isDisplayed()
		}

		@Test fun `can add non-watched film view`() {
			app.films.new.expand()

			val film = app.films.new.items[1]
			film.iconEl.click()

			assertThat(app.films.addViewDialog.element).isDisplayed()
			assertThat(app.films.addViewDialog.header).text().matches("""^View of""")
			assertThat(app.films.addViewDialog.header.text).contains(film.nameEl2.text)
			assertThat(app.films.addViewDialog.buttons.add).isDisplayed()
			assertThat(app.films.addViewDialog.buttons.cancel).isDisplayed()
		}

		@Test fun `can remove watched film view`() {
			app.films.watched.expand()

			val film = app.films.watched.items[0]
			film.iconEl.click()

			assertThat(app.films.removeViewDialog.element).isDisplayed()
			assertThat(app.films.removeViewDialog.header).text().isEqualTo("Deleting a View")
			assertThat(app.films.removeViewDialog.element.text).contains(film.nameEl2.text)
			assertThat(app.films.removeViewDialog.buttons.ok).isDisplayed()
			assertThat(app.films.removeViewDialog.buttons.cancel).isDisplayed()
		}

		@Test fun `can open plan options`() {
			assertThat(app.films.buttons.all).isEnabled()
			app.films.buttons.all.click()
			app.waitToLoad()
			assertThat(app.performances.buttons.options).isEnabled()

			app.performances.buttons.options.click()

			assertThat(app.performances.optionsDialog.element).isDisplayed()
			assertThat(app.performances.optionsDialog.header).text().isEqualTo("Planner Options")
			assertThat(app.performances.optionsDialog.buttons.plan).isDisplayed()
			assertThat(app.performances.optionsDialog.buttons.cancel).isDisplayed()
		}
	}
}
