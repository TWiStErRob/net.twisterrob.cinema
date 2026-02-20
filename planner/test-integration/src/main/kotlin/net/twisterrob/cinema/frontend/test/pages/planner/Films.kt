package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress(
	"UseDataClass", // TODEL https://github.com/detekt/detekt/issues/5339
	"ClassOrdering", // Logical order.
)
class Films(
	private val app: WebElement,
	private val root: WebElement,
) {

	fun waitToLoad(browser: Browser) {
		browser.waitFor("#films-group .films-loading")
	}

	val buttons get() = Buttons()

	inner class Buttons {

		val addView: WebElement
			get() = root.findElement(By.id("films-addView"))

		val all: WebElement
			get() = root.findElement(By.id("films-all"))

		val new: WebElement
			get() = root.findElement(By.id("films-new"))

		val none: WebElement
			get() = root.findElement(By.id("films-none"))
	}

	val new
		get() = FilmGroup(
			groupCSS = "#films-group",
			listCSS = "#films-list",
		)

	val watched
		get() = FilmGroup(
			groupCSS = "#films-group-watched",
			listCSS = "#films-list-watched",
		)

	val addViewDialog get() = AddViewDialog(app.findElement(By.className("modal-dialog")))

	class AddViewDialog(
		val element: WebElement,
	) {

		val header: WebElement
			get() = element.findElement(By.tagName("h3"))

		val buttons get() = Buttons()

		inner class Buttons {

			val add: WebElement
				get() = element.findElement(ByAngular.buttonText("Add"))

			val cancel: WebElement
				get() = element.findElement(ByAngular.buttonText("Cancel"))
		}
	}

	val removeViewDialog get() = RemoveViewDialog(app.findElement(By.className("modal-dialog")))

	class RemoveViewDialog(
		val element: WebElement,
	) {

		val header: WebElement
			get() = element.findElement(By.tagName("h1"))

		val buttons get() = Buttons()

		inner class Buttons {

			val ok: WebElement
				get() = element.findElement(ByAngular.buttonText("Yes"))

			val cancel: WebElement
				get() = element.findElement(ByAngular.buttonText("Cancel"))
		}
	}

	inner class FilmGroup(
		groupCSS: String,
		listCSS: String,
	) : Group(root.findElement(By.cssSelector(groupCSS)), listCSS, ".film") {

		operator fun get(index: Int): Film =
			Film(this.items[index])

		val films: List<Film>
			get() = this.items.map(::Film)
	}
}
