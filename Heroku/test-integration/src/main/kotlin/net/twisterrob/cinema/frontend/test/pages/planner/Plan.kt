package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class Plan(
	val root: WebElement,
) {

	val delete: WebElement
		get() = root.findElement(ByAngular.buttonText("×"))

	val schedule: WebElement
		get() = root.findElement(By.className("plan-films"))

	val scheduleItems: List<WebElement>
		get() = this.schedule.findElements(By.cssSelector(".plan-film, .plan-film-break"))

	val scheduleMovies: List<WebElement>
		get() = this.schedule.findElements(By.cssSelector(".plan-film"))

	val scheduleMovieTitles: List<WebElement>
		get() = this.scheduleMovies.map { it.findElement(By.className("film-title")) }

	val scheduleBreaks: List<WebElement>
		get() = this.schedule.findElements(By.cssSelector(".plan-film-break"))

	val scheduleStart: WebElement
		get() = root.findElement(By.cssSelector(".plan-header .film-start"))

	val scheduleEnd: WebElement
		get() = root.findElement(By.cssSelector(".plan-header .film-end"))

	operator fun get(index: Int): WebElement =
		this.scheduleItems[index]

	fun getItemAsMovie(index: Int): ScheduleMovieItem {
		val item = this[index]
		assertThat(item).hasClass("plan-film")
		return ScheduleMovieItem(item)
	}

	fun getItemAsBreak(index: Int): ScheduleBreakItem {
		val item = this[index]
		assertThat(item).hasClass("plan-film-break")
		return ScheduleBreakItem(item)
	}
}
