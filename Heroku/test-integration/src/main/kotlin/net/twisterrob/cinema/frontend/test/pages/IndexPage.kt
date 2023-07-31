package net.twisterrob.cinema.frontend.test.pages

import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.initElements
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class IndexPage(
	browser: Browser
) : BasePage(browser) {

	@FindBy(css = "h1")
	private lateinit var heading: WebElement

	override fun open() {
		check(!::heading.isInitialized) { "Already initialized" }
		browser.get("/")
		browser.initElements(this)
	}

	override fun assertOpened() {
		assertThat(browser.currentUrl).isEqualTo("/")
		assertThat(heading.text).isEqualTo("Cineworld Server API")
	}
}
