package net.twisterrob.cinema.frontend.test.pages

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.delayedExecute
import net.twisterrob.cinema.frontend.test.framework.isChecked
import net.twisterrob.cinema.frontend.test.framework.nonAngular
import net.twisterrob.cinema.frontend.test.framework.safeIndexOf
import net.twisterrob.cinema.frontend.test.framework.textContent
import net.twisterrob.cinema.frontend.test.framework.wait
import net.twisterrob.cinema.frontend.test.framework.waitForAngular
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import net.twisterrob.cinema.frontend.test.pages.planner.Cinema
import net.twisterrob.cinema.frontend.test.pages.planner.Film
import net.twisterrob.cinema.frontend.test.pages.planner.Group
import net.twisterrob.cinema.frontend.test.pages.planner.PlanGroup
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf
import org.openqa.selenium.support.ui.ExpectedConditions.urlMatches
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress(
	"StringLiteralDuplication", // Lots of CSS and angular selectors which might be the same.
	"ClassOrdering", // In logical order.
)
class PlannerPage(
	browser: Browser,
) : BasePage(browser) {

	fun goToPlanner(url: String = "") {
		browser.get("/planner$url")
		browser.initElements(this)
		waitToLoad()
	}

	@FindBy(css = "html[ng-app]")
	private lateinit var app: WebElement

	val date by lazy { Date(app.findElement(By.id("date"))) }

	class Date(
		private val root: WebElement,
	) {

		val buttons = Buttons()

		inner class Buttons {

			val london: By = By.cssSelector("#cinemas-list-london li")

			val change: WebElement
				get() = root.findElement(By.cssSelector("button"))

			val today: WebElement
				get() = root.findElement(ByAngular.buttonText("Today"))

			val clear: WebElement
				get() = root.findElement(ByAngular.buttonText("Clear"))

			val done: WebElement
				get() = root.findElement(ByAngular.buttonText("Done"))

			fun day(day: String): WebElement =
				root.findElement(ByAngular.buttonText(day))
		}

		val editor = Editor()

		inner class Editor {

			val element: WebElement
				get() = root.findElement(By.id("cineworldDate"))

			val date: LocalDate
				get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("M/d/yy"))
		}

		val label = Label()

		inner class Label {

			val element: WebElement
				get() = root.findElement(By.cssSelector("em.ng-binding"))

			val date: LocalDate
				get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("EEEE, LLLL d, yyyy"))
		}
	}

	val cinemas by lazy { Cinemas(app.findElement(By.id("cinemas"))) }

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

	val films by lazy { Films(app, app.findElement(By.id("films"))) }

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

		val new get() = FilmGroup("#films-group", "#films-list")
		val watched get() = FilmGroup("#films-group-watched", "#films-list-watched")

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

	val performances by lazy { Performances(app, app.findElement(By.id("performances"))) }

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

	val plans by lazy { Plans(app.findElement(By.id("plan-results"))) }

	class Plans(
		private val root: WebElement,
	) {

		val groups: List<WebElement>
			get() = root.findElements(ByAngular.repeater("cPlan in plans"))

		fun groupForCinema(cinemaName: String): PlanGroup {
			fun byCinemaName(group: WebElement): Boolean =
				group.findElement(By.className("cinema-name")).text == cinemaName
			return PlanGroup(this.groups.single(::byCinemaName))
		}
	}

	fun waitToLoad() {
		cinemas.waitToLoad(browser)
		// CONSIDER using the DSL.
		app
			.findElement(By.id("cinemas"))
			.findElements(By.className("cinema"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					films.waitToLoad(browser)
				}
			}
		app
			.findElement(By.id("films"))
			.findElements(By.className("film"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					performances.waitToLoad(browser)
				}
			}
		browser.waitForAngular()
	}

	fun login(userName: String, password: String) {
		browser.nonAngular {
			browser.get("/login")
			assertThat(browser).url().hasScheme("https").hasHost("accounts.google.com")

			browser.delayedExecute(By.name("identifier")) { identifierElem -> identifierElem.sendKeys(userName) }
			browser.delayedExecute(By.id("identifierNext")) { next -> next.click() }

			// Semi-transparent blocker is shown above the form, wait for it to disappear.
			val blocker = browser.driver.findElement(By.cssSelector("#initialView > footer ~ div"))
			browser.driver.wait().until(stalenessOf(blocker))

			browser.delayedExecute(By.name("password")) { passwordElem -> passwordElem.sendKeys(password) }
			browser.delayedExecute(By.id("passwordNext")) { next -> next.click() }

			browser.driver.wait()
				.withMessage("Google OAuth Login should redirect to home page")
				.until(urlMatches("""/#$"""))
		}
	}

	fun logout() {
		browser.nonAngular {
			browser.get("/logout")

			browser.driver.wait()
				.withMessage("Logout should redirect to home page")
				.until(urlMatches("""/"""))
		}
	}

	companion object {

		val D_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
	}
}

private fun Browser.waitFor(css: String) {
	this.waitForElementToDisappear(this.findElement(By.cssSelector(css)))
}
