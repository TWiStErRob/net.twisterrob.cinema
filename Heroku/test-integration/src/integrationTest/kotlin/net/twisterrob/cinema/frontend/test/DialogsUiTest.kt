package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BaseInteractivePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.junit.jupiter.api.Test

class DialogsUiTest : BaseInteractivePlannerUiTest() {

	@Test fun `opens calendar picker`() {
		assertThat(app.date.buttons.change).isEnabled()

		app.date.buttons.change.click()

		assertThat(app.date.buttons.done).isDisplayed()
	}

	@Test fun `can add arbitrary view`() {
		assertThat(app.films.buttons.addView).isEnabled()

		app.films.buttons.addView.click()

		assertThat(app.films.addViewDialog.element).isDisplayed()
		assertThat(app.films.addViewDialog.header).text().isEqualTo("View of")
		assertThat(app.films.addViewDialog.buttons.add).isDisplayed()
		assertThat(app.films.addViewDialog.buttons.cancel).isDisplayed()
	}

	@Test fun `can add non-watched film view`() {
		app.films.new.tryExpand()

		val film = app.films.new[1]
		film.view()

		assertThat(app.films.addViewDialog.element).isDisplayed()
		assertThat(app.films.addViewDialog.header).text().startsWith("View of ")
		assertThat(app.films.addViewDialog.header).text().contains(film.name.text)
		assertThat(app.films.addViewDialog.buttons.add).isDisplayed()
		assertThat(app.films.addViewDialog.buttons.cancel).isDisplayed()
	}

	@Test fun `can remove watched film view`() {
		app.films.watched.tryExpand()

		val film = app.films.watched[0]
		film.unview()

		assertThat(app.films.removeViewDialog.element).isDisplayed()
		assertThat(app.films.removeViewDialog.header).text().isEqualTo("Deleting a View")
		assertThat(app.films.removeViewDialog.element).text().contains(film.name.text)
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
