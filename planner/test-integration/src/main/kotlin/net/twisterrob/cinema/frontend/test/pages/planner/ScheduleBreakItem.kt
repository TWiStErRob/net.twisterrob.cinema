package net.twisterrob.cinema.frontend.test.pages.planner

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress("UseDataClass") // TODEL https://github.com/detekt/detekt/issues/5339
class ScheduleBreakItem(
	private val root: WebElement
) {

	val length: WebElement
		get() = root.findElement(By.className("length"))
}
