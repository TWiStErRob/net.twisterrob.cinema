package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf

fun Browser.initElements(page: Any) {
	PageFactory.initElements(driver, page)
}

fun List<WebElement>.findElements(by: By): List<WebElement> =
	this.flatMap { it.findElements(by) }

fun Browser.waitForElementToDisappear(element: WebElement) {
	check(driver.waitFor(invisibilityOf(element))) { "${element} did not disappear." }
}

fun Browser.delayedExecute(locator: By, action: (WebElement) -> Unit) {
	val element = driver.wait().until(ExpectedConditions.presenceOfElementLocated(locator))
	driver.wait().until(ExpectedConditions.visibilityOf(element))
	action(element)
}
