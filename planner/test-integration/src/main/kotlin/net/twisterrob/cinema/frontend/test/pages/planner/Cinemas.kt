package net.twisterrob.cinema.frontend.test.pages.planner

import net.twisterrob.cinema.frontend.test.framework.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress(
	"UseDataClass", // TODEL https://github.com/detekt/detekt/issues/5339
	"ClassOrdering", // Logical order.
)
class Cinemas(
	private val root: WebElement,
) {

	fun waitToLoad(browser: Browser) {
		browser.waitFor("#cinemas-group-favs .cinemas-loading")
	}

	val buttons get() = Buttons()

	inner class Buttons {

		val all: WebElement
			get() = root.findElement(By.id("cinemas-all"))

		val favorites: WebElement
			get() = root.findElement(By.id("cinemas-favs"))

		val london: WebElement
			get() = root.findElement(By.id("cinemas-london"))

		val none: WebElement
			get() = root.findElement(By.id("cinemas-none"))
	}

	val london get() = CinemaGroup("#cinemas-group-london", "#cinemas-list-london")
	val favorites get() = CinemaGroup("#cinemas-group-favs", "#cinemas-list-favs")
	val other get() = CinemaGroup("#cinemas-group-other", "#cinemas-list-other")

	inner class CinemaGroup(
		groupCSS: String,
		listCSS: String,
	) : Group(root.findElement(By.cssSelector(groupCSS)), listCSS, ".cinema") {

		operator fun get(index: Int): Cinema =
			Cinema(this.items[index])

		val cinemas: List<Cinema>
			get() = this.items.map(::Cinema)
	}
}
