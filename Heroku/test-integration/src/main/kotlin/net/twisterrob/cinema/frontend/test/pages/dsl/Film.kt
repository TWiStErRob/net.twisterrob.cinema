package net.twisterrob.cinema.frontend.test.pages.dsl

import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class Film(
	val root: WebElement,
) {

	val name: WebElement
		get() = root.findElement(By.className("film-title"))

	val icon: WebElement
		get() = root.findElement(By.className("glyphicon"))

	fun view() {
		assertThat(icon).hasIcon("eye-open")
		icon.click()
	}

	fun unview() {
		assertThat(icon).hasIcon("eye-close")
		icon.click()
	}
}
