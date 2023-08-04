package net.twisterrob.cinema.frontend.test.pages.planner

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress("UseDataClass") // TODEL https://github.com/detekt/detekt/issues/5339
class ScheduleMovieItem(
	private val root: WebElement
) {

	val startTime: WebElement
		get() = root.findElement(By.className("film-start"))

	val endTime: WebElement
		get() = root.findElement(By.className("film-end"))

	val title: WebElement
		get() = root.findElement(By.className("film-title"))

	val runtime: WebElement
		get() = root.findElement(By.className("film-runtime"))

	val filterByFilm: WebElement
		get() = root.findElement(By.xpath("""button[i[contains(@class, "glyphicon-time")]]"""))

	val filterByScreening: WebElement
		get() = root.findElement(By.xpath("""button[i[contains(@class, "glyphicon-film")]]"""))
}
