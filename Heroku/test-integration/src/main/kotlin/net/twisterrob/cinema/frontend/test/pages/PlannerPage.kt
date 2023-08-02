package net.twisterrob.cinema.frontend.test.pages

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.all
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.delayedExecute
import net.twisterrob.cinema.frontend.test.framework.element
import net.twisterrob.cinema.frontend.test.framework.hasSelection
import net.twisterrob.cinema.frontend.test.framework.indexOf
import net.twisterrob.cinema.frontend.test.framework.initElements
import net.twisterrob.cinema.frontend.test.framework.nonAngular
import net.twisterrob.cinema.frontend.test.framework.wait
import net.twisterrob.cinema.frontend.test.framework.waitForAngular
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf
import org.openqa.selenium.support.ui.ExpectedConditions.urlMatches
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PlannerPage(
	browser: Browser
) : BasePage(browser) {

	@FindBy(id = "cineworldDate-display")
	private lateinit var dateLabel: WebElement

	@FindBy(className = "performances-loading")
	private lateinit var performancesEmpty: WebElement

	fun waitFor(css: String) {
		val elemToWaitFor = browser.findElements(By.cssSelector(css)).single()
		return browser.waitForElementToDisappear(elemToWaitFor)
	}

	inner class CinemaGroup(groupCSS: String, listCSS: String) : Group(element(By.cssSelector(groupCSS)), listCSS, ".cinema")
	inner class FilmGroup(groupCSS: String, listCSS: String) : Group(element(By.cssSelector(groupCSS)), listCSS, ".film")
	inner class PlanGroup(root: WebElement) : Group(root, ".plans", ".plan") {
		val moreN get() = root.element(By.className("plans-footer")).element(ByAngular.partialButtonText("more ..."))
		val moreAll get() = root.element(By.className("plans-footer")).element(ByAngular.partialButtonText("All"))
		val scheduleExplorer get() = root.element(By.className("schedule-explorer"))

		operator fun get(index: Int): Plan =
			Plan(this.items[index])

		val plans get() = this.items.map(::Plan)

		// TODO This doesn't work yet, expects after this don't see this.list.
		fun listPlans() {
			if (this.scheduleExplorer.isSelected()) {
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
	open inner class Group(
		val root: WebElement,
		private val content: String,
		private val _items: String,
	) {
		val header get() = root.element(By.className("accordion-toggle"))
		val list get() = root.element(By.cssSelector(content))
		val items get() = list.all(By.cssSelector(_items))

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
		val delete get() = root.element(ByAngular.buttonText("×"))
		val schedule get() = root.element(By.className("plan-films")) // STOPSHIP was typed array
		val scheduleItems get() = this.schedule.all(By.cssSelector(".plan-film, .plan-film-break"))
		val scheduleMovies get() = this.schedule.all(By.cssSelector(".plan-film"))
		val scheduleBreaks get() = this.schedule.all(By.cssSelector(".plan-film-break"))

		// start and end are classed the same way in the global timings as in individual films
		val scheduleStart get() = root.all(By.cssSelector(".film-start")).first()
		val scheduleEnd get() = root.all(By.cssSelector(".film-end")).first() // STOPSHIP last?

		fun getItem(index: Int): WebElement =
			this.scheduleItems[index]

		fun getItemAsMovie(index: Int): ScheduleMovieItem {
			val item = this.getItem(index)
			assertThat(item).classes().contains("plan-film")
			return ScheduleMovieItem(item)
		}

		fun getItemAsBreak(index: Int): ScheduleBreakItem {
			val item = this.getItem(index)
			assertThat(item).classes().contains("plan-film-break")
			return ScheduleBreakItem(item)
		}
	}

	class ScheduleMovieItem(
		private val root: WebElement
	) {
		val startTime get() = root.element(By.className("film-start"))
		val endTime get() = root.element(By.className("film-end"))
		val title get() = root.element(By.className("film-title"))
		val runtime get() = root.element(By.className("film-runtime"))
		val filterByFilm get() = root.element(By.xpath("""button[i[contains(@class, "glyphicon-time")]]"""))
		val filterByScreening get() = root.element(By.xpath("""button[i[contains(@class, "glyphicon-film")]]"""))
	}

	class ScheduleBreakItem(
		private val root: WebElement
	) {
		val length get() = root.element(By.className("length"))
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

			val change get() = element(By.id("date")).element(By.cssSelector("button"))
			val today get() = element(By.id("date")).element(ByAngular.buttonText("Today"))
			val clear get() = element(By.id("date")).element(ByAngular.buttonText("Clear"))
			val done get() = element(By.id("date")).element(ByAngular.buttonText("Done"))
			fun day(day: String) = element(By.id("date")).element(ByAngular.buttonText(day))
		}

		val editor = Editor()
		inner class Editor {
			val element get() = element(By.id("cineworldDate"))
			fun getText(): String? = this.element.getAttribute("value")
			fun getTextAsMoment(): LocalDate =
				LocalDate.parse(getText(), DateTimeFormatter.ofPattern("M/d/yy"))
		}

		val label = Label()
		inner class Label {
			val element get() = element(By.id("date")).element(By.cssSelector("em.ng-binding"))

			fun getText(): String? =
				this.element.text

			fun getTextAsMoment(): LocalDate =
				LocalDate.parse(getText(), DateTimeFormatter.ofPattern("EEEE, LLLL d, yyyy"))
		}
	}

	val cinemas get() = Cinemas()
	inner class Cinemas {
		fun waitToLoad() {
			waitFor("#cinemas-group-favs .cinemas-loading")
		}

		val buttons get() = Buttons()
		inner class Buttons {
			val all get() = element(By.id("cinemas-all"))
			val favorites get() = element(By.id("cinemas-favs"))
			val london get() = element(By.id("cinemas-london"))
			val none get() = element(By.id("cinemas-none"))
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
			val addView get() = element(By.id("films-addView"))
			val all get() = element(By.id("films-all"))
			val new get() = element(By.id("films-new"))
			val none get() = element(By.id("films-none"))
		}

		val new = FilmGroup("#films-group", "#films-list")
		val watched = FilmGroup("#films-group-watched", "#films-list-watched")

		val addViewDialog get() = AddViewDialog()
		inner class AddViewDialog {
			val element get() = element(By.className("modal-dialog"))
			val header get() = element(By.className("modal-dialog")).element(By.tagName("h3"))

			val buttons get() = Buttons()
			inner class Buttons {

				val add get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Add"))
				val cancel get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Cancel"))
			}
		}

		val removeViewDialog get() = RemoveViewDialog()
		inner class RemoveViewDialog {
			val element get() = element(By.className("modal-dialog"))
			val header get() = element(By.className("modal-dialog")).element(By.tagName("h1"))

			val buttons get() = Buttons()
			inner class Buttons {

				val ok get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Yes"))
				val cancel get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Cancel"))
			}
		}
	}

	val byFilmRoot by lazy { element(By.id("performances-by-film")) }
	val byCinemaRoot by lazy { element(By.id("performances-by-cinema")) }

	val performances by lazy { Performances() }
	inner class Performances {
		fun waitToLoad() {
			waitFor("#performances-waiting")
			waitFor("#performances-loading")
		}
		val buttons get() = Buttons()
		inner class Buttons {
			val plan get() = element(By.id("plan-plan"))
			val options get() = element(By.id("plan-options"))
		}
		val byFilm get() = ByFilm()
		inner class ByFilm {
			val table get() = byFilmRoot
			val cinemas get() = byFilmRoot.element(By.tagName("thead")).all(ByAngular.repeater("cinema in cineworld.cinemas"))
			val films get() = byFilmRoot.all(ByAngular.repeater("film in cineworld.films"))
			fun performances(filmName: String, cinemaName: String): List<WebElement> {
				return this
					.films
					// find the row for the film
					.single { it.element(By.className("film-title")).text == filmName }
					// in all columns
					.all(ByAngular.repeater("cinema in cineworld.cinemas"))
					// pick the one that has the same index as the cinema
					.get(this.cinemas.indexOf { it.element(By.className("cinema-name")).text == cinemaName })
					// get the cell contents
					.all(ByAngular.repeater("performance in performances"))
					// and drill down into the performance (the separating comma is just outside this)
					.all(By.cssSelector(".performance"))
			}
		}
		val byCinema get() = ByCinema()
		inner class ByCinema {
			val table get() = byCinemaRoot
			val cinemas get() = byCinemaRoot.all(ByAngular.repeater("cinema in cineworld.cinemas"))
			val films get() = byCinemaRoot.element(By.tagName("thead")).all(ByAngular.repeater("film in cineworld.films"))
			fun performances(cinemaName: String, filmName: String): List<WebElement> {
				return this
					.cinemas
					// find the row for the cinema
					.single { it.element(By.className("cinema-name")).text == cinemaName }
					// in all columns
					.all(ByAngular.repeater("film in cineworld.films"))
					// pick the one that has the same index as the film
					.get(this.films.indexOfFirst { it.element(By.className("film-title")).text == filmName })
					// get the cell contents (cannot use this because empty cells cannot be matched with repeater)
					//.all(ByAngular.repeater("performance in performances"))
					// and drill down into the performance (the separating comma is just outside this)
					.all(By.cssSelector(".performance"))
			}
		}
		val optionsDialog get() = OptionsDialog()
		inner class OptionsDialog {
			val element get() = element(By.className("modal-dialog"))
			val header get() = element(By.className("modal-dialog")).element(By.tagName("h3"))

			val buttons get() = Buttons()
			inner class Buttons {

				val plan get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Plan"))
				val cancel get() = element(By.className("modal-dialog")).element(ByAngular.buttonText("Cancel"))
			}
		}
	}

	val plans by lazy { Plans() }
	inner class Plans {
		val groups get() = element(By.id("plan-results")).all(ByAngular.repeater("cPlan in plans"))

		fun groupForCinema(cinemaName: String): PlanGroup {
			fun byCinemaName(group: WebElement): Boolean =
				group.element(By.className("cinema-name")).text == cinemaName
			return PlanGroup(this.groups.single(::byCinemaName))
		}
	}

	fun waitToLoad() {
		cinemas.waitToLoad()
		element(By.id("cinemas"))
			.all(By.className("cinema"))
			.count { it.hasSelection() }
			.let { count ->
				if (count > 0) {
					films.waitToLoad()
				}
			}
		element(By.id("films"))
			.all(By.className("film"))
			.count { it.hasSelection() }
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

	override fun open() {
		check(!::dateLabel.isInitialized) { "Already initialized" }
		browser.get("/planner")
		browser.initElements(this)
	}

	override fun assertOpened() {
		assertThat(browser.currentUrl).startsWith("${Options.baseUrl}/planner")
		assertThat(browser.title).isEqualTo("Cineworld Cinemas Planner - Developer Beta")

		// static content
		assertThat(dateLabel.text).startsWith("Selected date is: ")

		// dynamic content
		assertThat(browser.currentUrl).contains("d=${LocalDate.now().year}")
		assertThat(performancesEmpty).text().isEqualTo("Please select a film...")
	}
	
	companion object {
		val D_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
	}
}
