package net.twisterrob.cinema.frontend.test.pages

import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.Byk
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.all
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.buttonText
import net.twisterrob.cinema.frontend.test.framework.element
import net.twisterrob.cinema.frontend.test.framework.filterByText
import net.twisterrob.cinema.frontend.test.framework.hasSelection
import net.twisterrob.cinema.frontend.test.framework.indexOf
import net.twisterrob.cinema.frontend.test.framework.initElements
import net.twisterrob.cinema.frontend.test.framework.repeater
import net.twisterrob.cinema.frontend.test.framework.waitForAngular
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import java.time.Duration
import java.time.LocalDate

object jasmine {
	val DEFAULT_TIMEOUT_INTERVAL: Duration = Duration.ofMillis(30000)
	fun pp(any: Any) {
		println("jasmine.pp: $any")
	}
}

class PlannerPage(
	browser: Browser
) : BasePage(browser) {

	@FindBy(id = "cineworldDate-display")
	private lateinit var dateLabel: WebElement

	@FindBy(className = "performances-loading")
	private lateinit var performancesEmpty: WebElement

	val SearchContext.iconEl get() = this.element(By.className("glyphicon"))
	val SearchContext.nameEl get() = this.element(By.className("cinema-name"))
	val SearchContext.nameEl2 get() = this.element(By.className("film-title"))

	fun waitFor(css: String) {
		val elemToWaitFor = browser.findElements(By.cssSelector(css)).single()
		return browser.waitForElementToDisappear(elemToWaitFor)
	}

	inner class CinemaGroup(groupCSS: String, listCSS: String) : Group(groupCSS, listCSS, ".cinema")
	inner class FilmGroup(groupCSS: String, listCSS: String) : Group(groupCSS, listCSS, ".film")
	inner class PlanGroup(root: String) : Group(root, ".plans", ".plan")

	/**
	 * @param root root element or CSS selector
	 * @param content content element or CSS selector inside root
	 * @param items elements or CSS selector in content
	 */
	open inner class Group(
		private val _root: String,
		private val _content: String,
		private val _items: String,
	) {
		val root get() = element(By.cssSelector(_root))
		val header get() = this.root.element(By.className("accordion-toggle"))
		val list get() = this.root.element(By.cssSelector(_content))
		val items get() = this.list.all(By.cssSelector(_items))

		fun click() {
			this.header.click()
		}

		fun collapse() {
			if (this.list.isDisplayed) {
				// displayed means it's expanded, so click to collapse
				this.click()
			} else {
				// not displayed, so it's already collapsed
			}
		}

		fun expand() {
			if (this.list.isDisplayed) {
				// displayed means it's already expanded
			} else {
				// not displayed means it's expanded, so click to collapse
				this.click()
			}
		}
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

			val change = element(By.id("date")).element(By.cssSelector("button"))
			val today = element(By.id("date")).element(Byk.buttonText("Today"))
			val clear = element(By.id("date")).element(Byk.buttonText("Clear"))
			val done = element(By.id("date")).element(Byk.buttonText("Done"))
			fun day(day: String) = element(By.id("date")).element(Byk.buttonText(day))
		}
		val editor = Editor()
		inner class Editor {
			val element = element(By.id("cineworldDate"))
			fun getText(): String? {
				return this.element.getAttribute("value")
			}
			fun getTextAsMoment() {
				TODO() //return this.getText().then(t -> moment(t, 'M/D/YY"))
			}
		}
		val label = Label()
		inner class Label {
			val element = element(By.id("date")).element(By.cssSelector("em.ng-binding"))
			fun getText(): String? =
				this.element.text
			fun getTextAsMoment() {
				TODO() //return this.getText().then(t -> moment(t, 'dddd, MMMM D, YYYY"))
			}
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
		val buttons = Buttons()
		inner class Buttons {
			val addView = element(By.id("films-addView"))
			val all = element(By.id("films-all"))
			val new = element(By.id("films-new"))
			val none = element(By.id("films-none"))
		}
		val new = FilmGroup("#films-group", "#films-list")
		val watched = FilmGroup("#films-group-watched", "#films-list-watched")
		inner class addViewDialog {
			val element = element(By.className("modal-dialog"))
			val header = element(By.className("modal-dialog")).element(By.tagName("h3"))
			inner class buttons {
				val add = element(By.className("modal-dialog")).element(Byk.buttonText("Add"))
				val cancel = element(By.className("modal-dialog")).element(Byk.buttonText("Cancel"))
			}
		}
		inner class removeViewDialog {
			val element = element(By.className("modal-dialog"))
			val header = element(By.className("modal-dialog")).element(By.tagName("h1"))
			inner class buttons {
				val ok = element(By.className("modal-dialog")).element(Byk.buttonText("Yes"))
				val cancel = element(By.className("modal-dialog")).element(Byk.buttonText("Cancel"))
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
			val cinemas  get() = byFilmRoot.element(By.tagName("thead")).all(Byk.repeater("cinema in cineworld.cinemas"))
			val films  get() = byFilmRoot.all(Byk.repeater("film in cineworld.films"))
			fun performances(filmName: String, cinemaName: String): List<WebElement> {
				return this
					.films
					// find the row for the film
					.single { it.element(By.className("film-title")).text == filmName }
					// in all columns
					.all(Byk.repeater("cinema in cineworld.cinemas"))
					// pick the one that has the same index as the cinema
					.get(this.cinemas.indexOf { it.element(By.className("cinema-name")).text == cinemaName })
					// get the cell contents
					.all(Byk.repeater("performance in performances"))
					// and drill down into the performance (the separating comma is just outside this)
					.all(By.cssSelector(".performance"))
			}
		}
		val byCinema get() = ByCinema()
		inner class ByCinema {
			val table get() = byCinemaRoot
			val cinemas get() = byCinemaRoot.all(Byk.repeater("cinema in cineworld.cinemas"))
			val films get() = byCinemaRoot.element(By.tagName("thead")).all(Byk.repeater("film in cineworld.films"))
			fun performances(cinemaName: String, filmName: String): List<WebElement> {
				return this
					.cinemas
					// find the row for the cinema
					.single { it.element(By.className("cinema-name")).text == cinemaName }
					// in all columns
					.all(Byk.repeater("film in cineworld.films"))
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
			inner class buttons {
				val plan get() = element(By.className("modal-dialog")).element(Byk.buttonText("Plan"))
				val cancel get() = element(By.className("modal-dialog")).element(Byk.buttonText("Cancel"))
			}
		}
	}

	val plans by lazy { Plans() }
	inner class Plans {
		/**
		 * @member {ElementArrayFinder}
		 */
		val groups get() = element(By.id("plan-results")).all(Byk.repeater("cPlan in plans"))

		fun groupForCinema(cinemaName: String): PlanGroup {
			fun byCinemaName(group: WebElement): Boolean =
				group.element(By.className("cinema-name")).filterByText(cinemaName)
			// TODO only() == firstOrFail // STOPSHIP removetostring
			return PlanGroup(this.groups.first(::byCinemaName).toString())
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

	override fun open() {
		check(!::dateLabel.isInitialized) { "Already initialized" }
		browser.get("/planner")
		browser.initElements(this)
	}

	override fun assertOpened() {
		assertThat(browser.currentUrl).startsWith("${Options.host}/planner")
		assertThat(browser.title).isEqualTo("Cineworld Cinemas Planner - Developer Beta")

		// static content
		assertThat(dateLabel.text).startsWith("Selected date is: ")

		// dynamic content
		assertThat(browser.currentUrl).contains("d=${LocalDate.now().year}")
		assertThat(performancesEmpty).text().isEqualTo("Please select a film...")
	}
}
