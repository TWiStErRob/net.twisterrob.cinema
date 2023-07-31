package net.twisterrob.cinema.frontend.test.framework

abstract class BasePage(
	protected val browser: Browser
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
