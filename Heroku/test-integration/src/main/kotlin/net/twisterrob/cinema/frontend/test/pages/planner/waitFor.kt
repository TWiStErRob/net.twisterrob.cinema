package net.twisterrob.cinema.frontend.test.pages.planner

import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import org.openqa.selenium.By

internal fun Browser.waitFor(css: String) {
	driver.waitForElementToDisappear(driver.findElement(By.cssSelector(css)))
}
