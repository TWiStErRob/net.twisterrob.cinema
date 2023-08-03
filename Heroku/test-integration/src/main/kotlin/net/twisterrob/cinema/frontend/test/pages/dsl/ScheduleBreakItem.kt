package net.twisterrob.cinema.frontend.test.pages.dsl

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class ScheduleBreakItem(
	private val root: WebElement
) {

	val length: WebElement
		get() = root.findElement(By.className("length"))
}
