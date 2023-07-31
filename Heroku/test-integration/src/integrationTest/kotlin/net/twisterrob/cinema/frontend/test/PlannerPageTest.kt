package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.WebDriverExtension
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.WebDriver

@ExtendWith(WebDriverExtension::class)
class PlannerPageTest {

	@Test fun `Cineworld planner loads some data`(driver: WebDriver) {
		val page = PlannerPage(driver)

		page.open()

		page.assertHasSomeCinemas()
		page.assertHasSomeFilms()
	}
}
