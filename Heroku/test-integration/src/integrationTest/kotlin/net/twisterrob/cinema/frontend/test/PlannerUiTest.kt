package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(BrowserExtension::class)
class PlannerUiTest {

	private lateinit var app: PlannerPage

	@BeforeEach fun setup() {
		app.goToPlanner()
	}

	@Test fun `page should have a title`(browser: Browser) {
		assertThat(browser.title).contains("Cineworld Cinemas Planner")
	}
}
