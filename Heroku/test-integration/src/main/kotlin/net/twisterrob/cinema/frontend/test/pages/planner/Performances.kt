package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.safeIndexOf
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@Suppress(
	"UseDataClass", // TODEL https://github.com/detekt/detekt/issues/5339
	"ClassOrdering", // Logical order.
)
class Performances(
	private val app: WebElement,
	private val root: WebElement,
) {

	fun waitToLoad(browser: Browser) {
		browser.waitFor("#performances-waiting")
		browser.waitFor("#performances-loading")
	}

	val buttons get() = Buttons()

	inner class Buttons {

		val plan: WebElement
			get() = root.findElement(By.id("plan-plan"))

		val options: WebElement
			get() = root.findElement(By.id("plan-options"))
	}

	val byFilm get() = ByFilm()

	inner class ByFilm {

		val table: WebElement
			get() = root.findElement(By.id("performances-by-film"))

		val cinemas: List<WebElement>
			get() = table.findElement(By.tagName("thead"))
				.findElements(ByAngular.repeater("cinema in cineworld.cinemas"))

		val films: List<WebElement>
			get() = table.findElements(ByAngular.repeater("film in cineworld.films"))

		fun performances(filmName: String, cinemaName: String): List<WebElement> =
			@Suppress("ReplaceGetOrSet", "ExplicitCollectionElementAccessMethod")
			this
				.films
				// find the row for the film
				.single { it.findElement(By.className("film-title")).text == filmName }
				// in all columns
				.findElements(ByAngular.repeater("cinema in cineworld.cinemas"))
				// pick the one that has the same index as the cinema
				.get(this.cinemas.safeIndexOf { it.findElement(By.className("cinema-name")).text == cinemaName })
				// get the cell contents
				.findElements(ByAngular.repeater("performance in performances"))
				// and drill down into the performance (the separating comma is just outside this)
				.flatMap { it.findElements(By.cssSelector(".performance")) }
	}

	val byCinema get() = ByCinema()

	inner class ByCinema {

		val table: WebElement
			get() = root.findElement(By.id("performances-by-cinema"))

		val cinemas: List<WebElement>
			get() = table.findElements(ByAngular.repeater("cinema in cineworld.cinemas"))

		val films: List<WebElement>
			get() = table.findElement(By.tagName("thead"))
				.findElements(ByAngular.repeater("film in cineworld.films"))

		fun performances(cinemaName: String, filmName: String): List<WebElement> =
			@Suppress("ReplaceGetOrSet", "ExplicitCollectionElementAccessMethod")
			this
				.cinemas
				// find the row for the cinema
				.single { it.findElement(By.className("cinema-name")).text == cinemaName }
				// in all columns
				.findElements(ByAngular.repeater("film in cineworld.films"))
				// pick the one that has the same index as the film
				.get(this.films.safeIndexOf { it.findElement(By.className("film-title")).text == filmName })
				// get the cell contents (cannot use this because empty cells cannot be matched with repeater)
				//.all(ByAngular.repeater("performance in performances"))
				// and drill down into the performance (the separating comma is just outside this)
				.findElements(By.cssSelector(".performance"))
	}

	val optionsDialog get() = OptionsDialog(app.findElement(By.className("modal-dialog")))

	class OptionsDialog(
		val element: WebElement,
	) {

		val header: WebElement
			get() = element.findElement(By.tagName("h3"))

		val buttons get() = Buttons()

		inner class Buttons {

			val plan: WebElement
				get() = element.findElement(ByAngular.buttonText("Plan"))

			val cancel: WebElement
				get() = element.findElement(ByAngular.buttonText("Cancel"))
		}
	}
}
