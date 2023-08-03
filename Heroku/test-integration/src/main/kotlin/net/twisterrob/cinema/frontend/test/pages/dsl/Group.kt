package net.twisterrob.cinema.frontend.test.pages.dsl

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * @param root root element or CSS selector
 * @param content content element or CSS selector inside root
 * @param _items elements or CSS selector in content
 */
open class Group(
	val root: WebElement,
	private val content: String,
	private val _items: String,
) {

	val header: WebElement
		get() = root.findElement(By.className("accordion-toggle"))

	val list: WebElement
		get() = root.findElement(By.cssSelector(content))

	val items: List<WebElement>
		get() = list.findElements(By.cssSelector(_items))

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
