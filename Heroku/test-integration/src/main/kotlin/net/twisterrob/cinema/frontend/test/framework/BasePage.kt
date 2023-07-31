package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.WebDriver

abstract class BasePage(
	protected val driver: WebDriver
) {

	/**
	 * Open the page and initialize all the bindings.
	 */
	abstract fun open()

	/**
	 * Verify the page is opened via title or significant element (e.g. header, logo)
	 */
	abstract fun assertOpened()
}
