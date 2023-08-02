@file:Suppress("RemoveRedundantBackticks", "ClassName")

package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.element
import net.twisterrob.cinema.frontend.test.framework.hasSelection
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.time.LocalDate

@ExtendWith(BrowserExtension::class)
class UrlUiTest {

	lateinit var app: PlannerPage
	lateinit var browser: Browser

	@Nested
	inner class `URL Hash` {

		@Nested
		inner class `Date` {

			@Test fun `should preselect today`() {
				app.goToPlanner()

				assertThat(browser).url().hasParameter("d", LocalDate.now().format(PlannerPage.D_FORMAT))
			}

			@Test fun `should preselect date`() {
				app.goToPlanner("?d=2017-07-14")

				assertThat(browser).url().hasParameter("d", "2017-07-14")
				assertThat(app.date.editor.element).text().isEqualTo("7/14/17")
				assertThat(app.date.label.element).text().isEqualTo("Friday, July 14, 2017")
			}
		}

		@Nested
		inner class `Cinemas` {

			@Test fun `should preselect favorites`() {
				app.goToPlanner()
				// Not sure why, but this particular test was really flaky without this sleep, but only on GitHub Actions CI.
				Thread.sleep(3000)

				assertThat(browser).url().hasParameter("c", "103")
			}

			@Test fun `should preselect cinemas`() {
				app.goToPlanner("?c=70")

				assertThat(app.cinemas.london.items)
					.filteredOn { it.text == "London - Wood Green" }
					.allMatch { it.hasSelection() }
				assertThat(app.cinemas.london.items)
					.filteredOn { it.text != "London - Wood Green" }
					.noneMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items).noneMatch { it.hasSelection() }
			}
		}

		@Nested
		inner class `Films` {

			@Test fun `should preselect films`() {
				app.goToPlanner("?f=189108&f=223046")

				val titles = Regex("""All Eyez On Me|Baby Driver""")
				fun filter(item: WebElement): Boolean = // STOPSHIP generalize
					item.element(By.className("film-title")).text.matches(titles)
				assertThat(app.films.new.items).filteredOn(::filter).allMatch { it.hasSelection() }
				assertThat(app.films.new.items).filteredOn { !filter(it) }.noneMatch { it.hasSelection() }
				assertThat(app.films.watched.items).noneMatch { it.hasSelection() }
			}
		}

		@Nested
		inner class `Initial` {

			@Test fun `should preselect everything`() {
				app.goToPlanner("?c=70&f=189108&f=223046&d=2017-07-14")

				assertThat(app.date.editor.element).text().isEqualTo("7/14/17")
				assertThat(app.cinemas.london.items)
					.filteredOn { it.text == "London - Wood Green" }
					.allMatch { it.hasSelection() }
				assertThat(app.cinemas.london.items)
					.filteredOn { it.text != "London - Wood Green" }
					.noneMatch { it.hasSelection() }
				assertThat(app.cinemas.other.items).noneMatch { it.hasSelection() }
				val titles = Regex("""All Eyez On Me|Baby Driver""")
				fun filter(item: WebElement): Boolean = // STOPSHIP generalize
					item.element(By.className("film-title")).text.matches(titles)
				assertThat(app.films.new.items.filter(::filter)).allMatch { it.hasSelection() }
				assertThat(app.films.new.items.filterNot(::filter)).noneMatch { it.hasSelection() }
				assertThat(app.films.watched.items).noneMatch { it.hasSelection() }
			}
		}
	}
}
