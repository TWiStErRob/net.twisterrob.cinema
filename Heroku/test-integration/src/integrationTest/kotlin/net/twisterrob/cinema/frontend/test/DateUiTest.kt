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

	private lateinit var app: PlannerPage

	@BeforeEach fun beforeEach() {
		app.goToPlanner()
	}

	@Nested
	inner class Editor {

		@Test fun `should not be empty`() {
			assertThat(app.date.editor.element).text().isNotEmpty()
		}

		@Test fun `should be today's date`() {
			assertThat(app.date.editor.date).isToday()
		}
	}

	@Nested
	inner class Label {

		@Test fun `should not be empty`() {
			assertThat(app.date.label.element).text().isNotEmpty()
		}

		@Test fun `should be today's date`() {
			assertThat(app.date.label.date).isToday()
		}
	}

	@Nested
	inner class Changing {

		private val day = 15
		private val selectedDate = LocalDate.now().withDayOfMonth(day)

		@BeforeEach fun beforeEach() {
			app.date.buttons.change.click()
			app.date.buttons.day(day.toString()).click()
		}

		@Test fun `should update the editor`() {
			assertThat(app.date.editor.element).text().isNotEmpty()
			assertThat(app.date.editor.date).isEqualTo(selectedDate)
		}

		@Test fun `should update the label`() {
			assertThat(app.date.label.element).text().isNotEmpty()
			assertThat(app.date.label.date).isEqualTo(selectedDate)
		}

		@Test fun `should update the url`(browser: Browser) {
			assertThat(browser).url().hasParameter("d", selectedDate.format(PlannerPage.D_FORMAT))
		}
	}
}
