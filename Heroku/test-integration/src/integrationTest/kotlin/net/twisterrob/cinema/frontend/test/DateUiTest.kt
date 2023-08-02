@file:Suppress("RemoveRedundantBackticks", "ClassName")

package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(BrowserExtension::class)
class DateUiTest {

	lateinit var app: PlannerPage

	@Nested
	inner class `Date display` {

		@BeforeEach fun beforeEach() {
			app.goToPlanner()
		}

		@Nested
		inner class `editor` {

			@Test fun `should not be empty`() {
				assertThat(app.date.editor.getText()).isNotEmpty()
			}

			@Test fun `should be today's date`() {
				assertThat(app.date.editor.getTextAsMoment()).isToday()
			}
		}

		@Nested
		inner class `label` {

			@Test fun `should not be empty`() {
				assertThat(app.date.label.getText()).isNotEmpty()
			}

			@Test fun `should be today's date`() {
				assertThat(app.date.label.getTextAsMoment()).isToday()
			}
		}

		@Nested
		inner class `changing` {

			private val day = 15
			private val selectedDate = LocalDate.now().withDayOfMonth(day)

			@BeforeEach fun beforeEach() {
				app.date.buttons.change.click()
				app.date.buttons.day(day.toString()).click()
			}

			@Test fun `should update the editor`() {
				assertThat(app.date.editor.getText()).isNotEmpty()
				assertThat(app.date.editor.getTextAsMoment()).isEqualTo(selectedDate)
			}

			@Test fun `should update the label`() {
				assertThat(app.date.label.getText()).isNotEmpty()
				assertThat(app.date.label.getTextAsMoment()).isEqualTo(selectedDate)
			}

			@Test fun `should update the url`(browser: Browser) {
				assertThat(browser).url().hasParameter("d", selectedDate.format(PlannerPage.D_FORMAT))
			}
		}
	}
}
