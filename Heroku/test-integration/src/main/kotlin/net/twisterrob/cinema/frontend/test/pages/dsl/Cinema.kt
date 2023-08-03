package net.twisterrob.cinema.frontend.test.pages.dsl

import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class Cinema(
	val root: WebElement,
) {

	val name: WebElement
		get() = root.findElement(By.className("cinema-name"))

	val icon: WebElement
		get() = root.findElement(By.className("glyphicon"))

	fun favorite() {
		assertThat(icon).hasIcon("star-empty")
		icon.click()
	}

	fun unfavorite() {
		assertThat(icon).hasIcon("heart")
		icon.click()
	}
}
