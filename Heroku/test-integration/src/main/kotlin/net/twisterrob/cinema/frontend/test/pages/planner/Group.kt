package net.twisterrob.cinema.frontend.test.pages.planner

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * @param root root element
 * @param contentCss CSS selector inside root
 * @param itemsCss CSS selector in content
 */
@Suppress("OutdatedDocumentation") // TODEL https://github.com/detekt/detekt/issues/6362
open class Group(
	val root: WebElement,
	private val contentCss: String,
	private val itemsCss: String,
) {

	val header: WebElement
		get() = root.findElement(By.className("accordion-toggle"))

	val list: WebElement
		get() = root.findElement(By.cssSelector(contentCss))

	val items: List<WebElement>
		get() = list.findElements(By.cssSelector(itemsCss))

	fun click() {
		header.click()
	}

	fun collapse() {
		if (list.isDisplayed) {
			// displayed means it's expanded, so click to collapse
			click()
		} else {
			// not displayed, so it's already collapsed
		}
	}

	fun expand() {
		if (list.isDisplayed) {
			// displayed means it's already expanded
		} else {
			// not displayed means it's expanded, so click to collapse
			click()
		}
	}
}
