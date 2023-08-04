@file:Suppress("UnusedImport")

package net.twisterrob.cinema.frontend.test.framework

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

@Disabled("Only for quick execution for development.")
class DevelopmentUiTest {

	@Test fun test() = test {
		assertThat(findElement(By.tagName("body"))).displayed().isTrue()
	}

	private fun test(block: WebDriver.() -> Unit) {
		val driver = ChromeDriver()
		driver.get("https://www.google.com")
		try {
			driver.block()
		} finally {
			driver.quit()
		}
	}
}
