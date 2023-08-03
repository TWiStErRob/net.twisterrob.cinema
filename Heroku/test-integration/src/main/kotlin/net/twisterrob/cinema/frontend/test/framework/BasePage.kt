package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.support.PageFactory

@Suppress("UnnecessaryAbstractClass")
abstract class BasePage(
	protected val browser: Browser
) {

	fun initElements() {
		PageFactory.initElements(browser.driver, this)
	}
}
