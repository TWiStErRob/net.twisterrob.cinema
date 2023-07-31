package net.twisterrob.cinema.frontend.test.pages

import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.initElements
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class IndexPage(
	driver: WebDriver
) : BasePage(driver) {

	@FindBy(css = "h1")
	private lateinit var heading: WebElement

	override fun open() {
		check(!::heading.isInitialized) { "Already initialized" }
		driver.get("${Options.host}/")
		driver.initElements(this)
	}

	override fun assertOpened() {
		assertThat(driver.currentUrl).isEqualTo("${Options.host}/")
		assertThat(heading.text).isEqualTo("Cineworld Server API")
	}
}
