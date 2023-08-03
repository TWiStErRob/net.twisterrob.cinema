package net.twisterrob.cinema.frontend.test.pages

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.delayedExecute
import net.twisterrob.cinema.frontend.test.framework.findElements
import net.twisterrob.cinema.frontend.test.framework.isChecked
import net.twisterrob.cinema.frontend.test.framework.nonAngular
import net.twisterrob.cinema.frontend.test.framework.safeIndexOf
import net.twisterrob.cinema.frontend.test.framework.textContent
import net.twisterrob.cinema.frontend.test.framework.wait
import net.twisterrob.cinema.frontend.test.framework.waitForAngular
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf
import org.openqa.selenium.support.ui.ExpectedConditions.urlMatches
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlannerPage(
	browser: Browser,
) : BasePage(browser) {

	private fun waitFor(css: String) {
		browser.waitForElementToDisappear(element(By.cssSelector(css)))
	}

	inner class CinemaGroup(
		groupCSS: String,
		listCSS: String,
	) : Group(element(By.cssSelector(groupCSS)), listCSS, ".cinema")

	inner class FilmGroup(
		groupCSS: String,
		listCSS: String,
	) : Group(element(By.cssSelector(groupCSS)), listCSS, ".film")

	inner class PlanGroup(
		root: WebElement,
	) : Group(root, ".plans", ".plan") {

		val moreN: WebElement
			get() = root.findElement(By.className("plans-footer")).findElement(ByAngular.partialButtonText("more ..."))

		val moreAll: WebElement
			get() = root.findElement(By.className("plans-footer")).findElement(ByAngular.partialButtonText("All"))

		val scheduleExplorer: WebElement
			get() = root.findElement(By.className("schedule-explorer"))

		operator fun get(index: Int): Plan =
			Plan(this.items[index])

		val plans: List<Plan>
			get() = this.items.map(::Plan)

		// TODO This doesn't work yet, expects after this don't see this.list.
		fun listPlans() {
			if (this.scheduleExplorer.isSelected) {
				// selected means it's checked, so click to un-check
				this.scheduleExplorer.click()
			} else {
				// not selected, so it's already un-checked
			}
		}
	}

	/**
	 * @param root root element or CSS selector
	 * @param content content element or CSS selector inside root
	 * @param _items elements or CSS selector in content
	 */
	open class Group(
		val root: WebElement,
		private val content: String,
		private val _items: String,
	) {

		val header: WebElement
			get() = root.findElement(By.className("accordion-toggle"))

		val list: WebElement
			get() = root.findElement(By.cssSelector(content))

		val items: List<WebElement>
			get() = list.findElements(By.cssSelector(_items))

		fun click() {
			header.click()
		}

		fun collapse() {
			if (list.isDisplayed) {
				// displayed means it's expanded, so click to collapse
				click()
			} else {
				// not displayed, so it's already collapsed
			}
		}

		fun expand() {
			if (list.isDisplayed) {
				// displayed means it's already expanded
			} else {
				// not displayed means it's expanded, so click to collapse
				click()
			}
		}
	}

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
			assertThat(item).classes().contains("plan-film")
			return ScheduleMovieItem(item)
		}

		fun getItemAsBreak(index: Int): ScheduleBreakItem {
			val item = this[index]
			assertThat(item).classes().contains("plan-film-break")
			return ScheduleBreakItem(item)
		}
	}

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

	class ScheduleBreakItem(
		private val root: WebElement
	) {

		val length: WebElement
			get() = root.findElement(By.className("length"))
	}

	fun element(by: By): WebElement = browser.findElement(by)

	fun goToPlanner(url: String = "") {
		browser.get("/planner$url")
		waitToLoad()
	}

	val date by lazy { Date() }

	inner class Date {

		val buttons = Buttons()

		inner class Buttons {

			val london: By = By.cssSelector("#cinemas-list-london li")

			val change: WebElement
				get() = element(By.id("date")).findElement(By.cssSelector("button"))

			val today: WebElement
				get() = element(By.id("date")).findElement(ByAngular.buttonText("Today"))

			val clear: WebElement
				get() = element(By.id("date")).findElement(ByAngular.buttonText("Clear"))

			val done: WebElement
				get() = element(By.id("date")).findElement(ByAngular.buttonText("Done"))

			fun day(day: String): WebElement =
				element(By.id("date")).findElement(ByAngular.buttonText(day))
		}

		val editor = Editor()

		inner class Editor {

			val element: WebElement
				get() = element(By.id("cineworldDate"))

			val date: LocalDate
				get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("M/d/yy"))
		}

		val label = Label()

		inner class Label {

			val element: WebElement
				get() = element(By.id("date")).findElement(By.cssSelector("em.ng-binding"))

			val date: LocalDate
				get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("EEEE, LLLL d, yyyy"))
		}
	}

	val cinemas get() = Cinemas()

	inner class Cinemas {

		fun waitToLoad() {
			waitFor("#cinemas-group-favs .cinemas-loading")
		}

		val buttons get() = Buttons()

		inner class Buttons {

			val all: WebElement
				get() = element(By.id("cinemas-all"))

			val favorites: WebElement
				get() = element(By.id("cinemas-favs"))

			val london: WebElement
				get() = element(By.id("cinemas-london"))

			val none: WebElement
				get() = element(By.id("cinemas-none"))
		}

		val london get() = CinemaGroup("#cinemas-group-london", "#cinemas-list-london")
		val favorites get() = CinemaGroup("#cinemas-group-favs", "#cinemas-list-favs")
		val other get() = CinemaGroup("#cinemas-group-other", "#cinemas-list-other")
	}

	val films by lazy { Films() }

	inner class Films {

		fun waitToLoad() {
			waitFor("#films-group .films-loading")
		}

		val buttons get() = Buttons()

		inner class Buttons {

			val addView: WebElement
				get() = element(By.id("films-addView"))

			val all: WebElement
				get() = element(By.id("films-all"))

			val new: WebElement
				get() = element(By.id("films-new"))

			val none: WebElement
				get() = element(By.id("films-none"))
		}

		val new get() = FilmGroup("#films-group", "#films-list")
		val watched get() = FilmGroup("#films-group-watched", "#films-list-watched")

		val addViewDialog get() = AddViewDialog()

		inner class AddViewDialog {

			val element: WebElement
				get() = element(By.className("modal-dialog"))

			val header: WebElement
				get() = element(By.className("modal-dialog")).findElement(By.tagName("h3"))

			val buttons get() = Buttons()

			inner class Buttons {

				val add: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Add"))

				val cancel: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Cancel"))
			}
		}

		val removeViewDialog get() = RemoveViewDialog()

		inner class RemoveViewDialog {

			val element: WebElement
				get() = element(By.className("modal-dialog"))

			val header: WebElement
				get() = element(By.className("modal-dialog")).findElement(By.tagName("h1"))

			val buttons get() = Buttons()

			inner class Buttons {

				val ok: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Yes"))

				val cancel: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Cancel"))
			}
		}
	}

	val performances by lazy { Performances() }

	inner class Performances {

		fun waitToLoad() {
			waitFor("#performances-waiting")
			waitFor("#performances-loading")
		}

		val buttons get() = Buttons()

		inner class Buttons {

			val plan: WebElement
				get() = element(By.id("plan-plan"))

			val options: WebElement
				get() = element(By.id("plan-options"))
		}

		val byFilm get() = ByFilm()

		inner class ByFilm {

			val table: WebElement
				get() = element(By.id("performances-by-film"))

			val cinemas: List<WebElement>
				get() = table.findElement(By.tagName("thead"))
					.findElements(ByAngular.repeater("cinema in cineworld.cinemas"))

			val films: List<WebElement>
				get() = table.findElements(ByAngular.repeater("film in cineworld.films"))

			fun performances(filmName: String, cinemaName: String): List<WebElement> =
				@Suppress("ReplaceGetOrSet")
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
					.findElements(By.cssSelector(".performance"))
		}

		val byCinema get() = ByCinema()

		inner class ByCinema {

			val table: WebElement
				get() = element(By.id("performances-by-cinema"))

			val cinemas: List<WebElement>
				get() = table.findElements(ByAngular.repeater("cinema in cineworld.cinemas"))

			val films: List<WebElement>
				get() = table.findElement(By.tagName("thead"))
					.findElements(ByAngular.repeater("film in cineworld.films"))

			fun performances(cinemaName: String, filmName: String): List<WebElement> =
				@Suppress("ReplaceGetOrSet")
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

		val optionsDialog get() = OptionsDialog()

		inner class OptionsDialog {

			val element: WebElement
				get() = element(By.className("modal-dialog"))

			val header: WebElement
				get() = element(By.className("modal-dialog")).findElement(By.tagName("h3"))

			val buttons get() = Buttons()

			inner class Buttons {

				val plan: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Plan"))

				val cancel: WebElement
					get() = element(By.className("modal-dialog")).findElement(ByAngular.buttonText("Cancel"))
			}
		}
	}

	val plans by lazy { Plans() }

	inner class Plans {

		val groups: List<WebElement>
			get() = element(By.id("plan-results")).findElements(ByAngular.repeater("cPlan in plans"))

		fun groupForCinema(cinemaName: String): PlanGroup {
			fun byCinemaName(group: WebElement): Boolean =
				group.findElement(By.className("cinema-name")).text == cinemaName
			return PlanGroup(this.groups.single(::byCinemaName))
		}
	}

	fun waitToLoad() {
		cinemas.waitToLoad()
		element(By.id("cinemas"))
			.findElements(By.className("cinema"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					films.waitToLoad()
				}
			}
		element(By.id("films"))
			.findElements(By.className("film"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					performances.waitToLoad()
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
