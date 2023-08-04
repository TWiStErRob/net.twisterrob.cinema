package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BaseInteractivePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.Browser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlannerUiTest : BaseInteractivePlannerUiTest() {

	@Test fun `page should have a title`(browser: Browser) {
		assertThat(browser.title).contains("Cineworld Cinemas Planner")
	}
}
