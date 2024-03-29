package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BasePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.allMeet
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.noneMeet
import net.twisterrob.cinema.frontend.test.framework.not
import net.twisterrob.cinema.frontend.test.framework.or
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import net.twisterrob.cinema.frontend.test.pages.planner.Cinema
import net.twisterrob.cinema.frontend.test.pages.planner.Film
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UrlUiTest : BasePlannerUiTest() {

	private fun filterFilmName(name: String): (Film) -> Boolean =
		{ it.name.text == name }

	private fun filterCinemaName(name: String): (Cinema) -> Boolean =
		{ it.name.text == name }

	@Nested
	inner class Date {

		@Test fun `should preselect today`(browser: Browser) {
			app.goToPlanner()

			assertThat(browser).url().hasParameter("d", LocalDate.now().format(PlannerPage.D_FORMAT))
		}

		@Test fun `should preselect date`(browser: Browser) {
			app.goToPlanner("?d=2017-07-14")

			assertThat(browser).url().hasParameter("d", "2017-07-14")
			assertThat(app.date.editor.element).hasText("7/14/17")
			assertThat(app.date.label.element).hasText("Friday, July 14, 2017")
		}
	}

	@Nested
	inner class Cinemas {

		@Test fun `should preselect favorites`(browser: Browser) {
			app.goToPlanner()

			assertThat(browser).url().hasParameter("c", "103")
		}

		@Test fun `should preselect cinemas`() {
			app.goToPlanner("?c=70")

			val preselectedCinema = filterCinemaName("London - Wood Green")
			assertThat(app.cinemas.london.cinemas).filteredOn(preselectedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.london.cinemas).filteredOn(!preselectedCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).noneMeet { isChecked() }
		}
	}

	@Nested
	inner class Films {

		@Test fun `should preselect films`() {
			app.goToPlanner("?f=189108&f=223046")

			val preselectedFilm = filterFilmName("All Eyez On Me") or filterFilmName("Baby Driver")
			assertThat(app.films.new.films).filteredOn(preselectedFilm).allMeet { isChecked() }
			assertThat(app.films.new.films).filteredOn(!preselectedFilm).noneMeet { isChecked() }
			assertThat(app.films.watched.films).noneMeet { isChecked() }
		}
	}

	@Nested
	inner class Initial {

		@Test fun `should preselect everything`() {
			app.goToPlanner("?c=70&f=189108&f=223046&d=2017-07-14")

			assertThat(app.date.editor.element).hasText("7/14/17")
			val preselectedCinema = filterCinemaName("London - Wood Green")
			assertThat(app.cinemas.london.cinemas).filteredOn(preselectedCinema).allMeet { isChecked() }
			assertThat(app.cinemas.london.cinemas).filteredOn(!preselectedCinema).noneMeet { isChecked() }
			assertThat(app.cinemas.other.cinemas).noneMeet { isChecked() }
			val preselectedFilm = filterFilmName("All Eyez On Me") or filterFilmName("Baby Driver")
			assertThat(app.films.new.films).filteredOn(preselectedFilm).allMeet { isChecked() }
			assertThat(app.films.new.films).filteredOn(!preselectedFilm).noneMeet { isChecked() }
			assertThat(app.films.watched.films).noneMeet { isChecked() }
		}
	}
}
