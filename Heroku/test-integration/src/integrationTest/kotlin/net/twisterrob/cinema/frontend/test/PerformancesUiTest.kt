package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BasePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PerformancesUiTest : BasePlannerUiTest() {

	@Nested
	inner class TableByFilm {

		@BeforeEach fun beforeEach() {
			app.goToPlanner("?d=2017-07-14&c=103&f=184739&f=189108")
		}

		@Test fun `shows film headers`() {
			assertThat(app.performances.byFilm.films).hasSize(2)
			assertThat(app.performances.byFilm.films[0]).text().contains("Spider-Man")
			assertThat(app.performances.byFilm.films[1]).text().contains("Baby Driver")
		}

		@Test fun `shows cinema headers`() {
			assertThat(app.performances.byFilm.cinemas).hasSize(1)
			assertThat(app.performances.byFilm.cinemas[0]).text().contains("Leicester Square")
		}

		@Test fun `shows performances (Baby Driver)`() {
			val times = app.performances.byFilm.performances("Baby Driver", "Leicester Square")
			assertThat(times).hasSize(4)
			assertThat(times[0]).text().isEqualTo("12:00")
			assertThat(times[1]).text().isEqualTo("14:40")
			assertThat(times[2]).text().isEqualTo("18:10")
			assertThat(times[3]).text().isEqualTo("20:50")
		}

		@Test fun `shows performances (Spider-Man)`() {
			val times = app.performances.byFilm.performances("(IMAX 3-D) Spider-Man : HOMECOMING", "Leicester Square")
			assertThat(times).hasSize(4)
			assertThat(times[0]).text().isEqualTo("11:00")
			assertThat(times[1]).text().isEqualTo("14:10")
			assertThat(times[2]).text().isEqualTo("17:20")
			assertThat(times[3]).text().isEqualTo("20:30")
		}
	}

	@Nested
	inner class TableByCinema {

		@BeforeEach fun beforeEach() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
		}

		@Test fun `shows cinema headers`() {
			assertThat(app.performances.byCinema.cinemas).hasSize(2)
			assertThat(app.performances.byCinema.cinemas[0]).text().contains("Leicester Square")
			assertThat(app.performances.byCinema.cinemas[1]).text().contains("Wood Green")
		}

		@Test fun `shows film headers`() {
			assertThat(app.performances.byCinema.films).hasSize(2)
			assertThat(app.performances.byCinema.films[0]).text().contains("Spider-Man")
			assertThat(app.performances.byCinema.films[1]).text().contains("Baby Driver")
		}

		@Test fun `shows performances (1)`() {
			val times = app.performances.byCinema.performances("Leicester Square", "Baby Driver")
			assertThat(times).hasSize(4)
			assertThat(times[0]).text().isEqualTo("12:00")
			assertThat(times[1]).text().isEqualTo("14:40")
			assertThat(times[2]).text().isEqualTo("18:10")
			assertThat(times[3]).text().isEqualTo("20:50")
		}

		@Test fun `shows performances (2)`() {
			val times = app.performances.byCinema.performances("Wood Green", "Baby Driver")
			assertThat(times).hasSize(5)
			assertThat(times[0]).text().isEqualTo("11:50")
			assertThat(times[1]).text().isEqualTo("14:00")
			assertThat(times[2]).text().isEqualTo("17:40")
			assertThat(times[3]).text().isEqualTo("20:50")
			assertThat(times[4]).text().isEqualTo("23:30")
		}

		@Test fun `shows performances (3)`() {
			val times = app.performances.byCinema.performances("Wood Green", "(IMAX 3-D) Spider-Man : HOMECOMING")
			assertThat(times).isEmpty()
		}
	}
}
