package net.twisterrob.cinema.frontend.test.pages

import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.Byk
import net.twisterrob.cinema.frontend.test.framework.Options
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.buttonText
import net.twisterrob.cinema.frontend.test.framework.initElements
import net.twisterrob.cinema.frontend.test.framework.repeater
import net.twisterrob.cinema.frontend.test.framework.waitForElementToDisappear
import net.twisterrob.cinema.frontend.test.framework.waitForJQuery
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

/**
 * @param {string|RegExp} text string contains or regex match
 * @param {boolean} inverse negate the result
 * @return {ElementArrayFinder}
 * @see ElementArrayFinder.filter
 * @see ElementFinder.filterByText
 */
fun List<WebElement>.filterByText(text: String, inverse: Boolean = false): List<WebElement> {
	return this.filter { item -> item.filterByText(text, inverse) }
}

/**
 * Creates a filter function to match the text of the element.
 * @param {string|RegExp} text string contains or regex match
 * @param {boolean} inverse negate the result
 * @return {Promise<boolean>}
 * @see ElementArrayFinder.filter
 */
fun WebElement.filterByText(text: String, inverse: Boolean = false): Boolean =
	filterByText(Regex(Regex.escape(text)), inverse)

fun WebElement.filterByText(text: Regex, inverse: Boolean = false): Boolean {
	val matcher = fun (label: String) = text.matches(label)
	val filter = if (inverse) ({ x -> !matcher(x) }) else matcher
	return this.text.let(filter)
}

/**
 * Creates a filter function to match that the element has a class.
 * @return {Promise<boolean>}
 */
fun WebElement.filterByClass(className: String): Boolean {
	return this
		.getAttribute("class")
		.let { classes -> (classes ?: "").split(Regex("""\s+""")).indexOf(className) != -1 }
}

/**
 *
 * @param {function(ElementFinder): Promise<boolean>} filter
 * @return {Promise<int>}
 */
fun List<WebElement>.indexOf(filter: (WebElement) -> Boolean): Int {
	val INITIAL_VALUE = -1
	val stack = Throwable().stackTrace
	//noinspection JSValidateTypes it will be a Promise<int>, but the generics don't resolve it on reduce/then
	return this
		.foldIndexed(INITIAL_VALUE) { acc, index, element ->
			if (acc != INITIAL_VALUE) return@foldIndexed acc
			return@foldIndexed if(filter(element)) index  else acc
		}
		.let { index ->
			assertThat(index)
				.overridingErrorMessage { "Cannot find index of ${filter} in ${jasmine.pp(this)}\n${stack}" }
				.isGreaterThanOrEqualTo(0)
			return@let index
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

	fun waitFor(classNameToWaitFor: String) {
		val elemToWaitFor = browser.findElements(By.cssSelector(classNameToWaitFor)).single()
		return browser.waitForElementToDisappear(elemToWaitFor)
	}

	class CinemaGroup(groupCSS: String, listCSS: String): Group(groupCSS, listCSS, ".cinema")
	class FilmGroup(groupCSS: String, listCSS: String): Group(groupCSS, listCSS, ".film")
	class PlanGroup(root: String): Group(root, ".plans", ".plan")
	sealed class Group(root: String, content: String, items: String) {
		init {
			println("Group: $root, $content, $items")
		}
	}
	fun element(by: By): WebElement = browser.findElement(by)
	fun SearchContext.element(by: By): WebElement = this.findElement(by)
	fun WebElement.element(by: By): WebElement = this.findElement(by)
	fun WebElement.all(by: By): List<WebElement> = this.findElements(by)
	fun List<WebElement>.all(by: By): List<WebElement> = this.flatMap { it.findElements(by) }

	fun goToPlanner(url: String = "") {
		browser.get("/planner$url")
	}
	
	val date get() = Date()
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
			waitFor(".cinemas-loading")
		}
		val buttons = Buttons()
		inner class Buttons {
			val all = element(By.id("cinemas-all"))
			val favorites = element(By.id("cinemas-favs"))
			val london = element(By.id("cinemas-london"))
			val none = element(By.id("cinemas-none"))
		}
		val london = CinemaGroup("#cinemas-group-london", "#cinemas-list-london")
		val favorites = CinemaGroup("#cinemas-group-favs", "#cinemas-list-favs")
		val other = CinemaGroup("#cinemas-group-other", "#cinemas-list-other")
	}

	val films get() = Films()
	inner class Films {
		fun waitToLoad() {
			waitFor(".films-loading")
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
	};

	val byFilmRoot get() = element(By.id("performances-by-film"))
	val byCinemaRoot get() = element(By.id("performances-by-cinema"))
	val performances get() = Performances()
	inner class Performances {
		fun waitToLoad() {
			waitFor("#performances-waiting")
			waitFor("#performances-loading")
		}
		val buttons = Buttons()
		inner class Buttons {
			val plan = element(By.id("plan-plan"))
			val options = element(By.id("plan-options"))
		}
		inner class byFilm {
			val table = byFilmRoot
			val cinemas = byFilmRoot.element(By.tagName("thead")).all(Byk.repeater("cinema in cineworld.cinemas"))
			val films = byFilmRoot.all(Byk.repeater("film in cineworld.films"))
			fun performances(filmName: String, cinemaName: String): List<WebElement> {
				return this
					.films
					// find the row for the film
					.filterByText(filmName).first()
					// in all columns
					.all(Byk.repeater("cinema in cineworld.cinemas"))
					// pick the one that has the same index as the cinema
					.get(this.cinemas.indexOf { element -> element.filterByText(cinemaName) })
					// get the cell contents
					.all(Byk.repeater("performance in performances"))
					// and drill down into the performance (the separating comma is just outside this)
					.all(By.cssSelector(".performance"))
			}
		}
		inner class byCinema {
			val table = byCinemaRoot
			val cinemas = byCinemaRoot.all(Byk.repeater("cinema in cineworld.cinemas"))
			val films = byCinemaRoot.element(By.tagName("thead")).all(Byk.repeater("film in cineworld.films"))
			fun performances(cinemaName: String, filmName: String): List<WebElement> {
				return this
					.cinemas
					// find the row for the cinema
					.filterByText(cinemaName).first()
					// in all columns
					.all(Byk.repeater("film in cineworld.films"))
					// pick the one that has the same index as the film
					.get(this.films.indexOfFirst { it.filterByText(filmName) })
					// get the cell contents
					.all(Byk.repeater("performance in performances"))
					// and drill down into the performance (the separating comma is just outside this)
					.all(By.cssSelector(".performance"));
			}
		}
		inner class optionsDialog {
			val element = element(By.className("modal-dialog"))
			val header = element(By.className("modal-dialog")).element(By.tagName("h3"))
			inner class buttons {
				val plan = element(By.className("modal-dialog")).element(Byk.buttonText("Plan"))
				val cancel = element(By.className("modal-dialog")).element(Byk.buttonText("Cancel"))
			}
		}
	}

	inner class plans {
		/**
		 * @member {ElementArrayFinder}
		 */
		val groups = element(By.id("plan-results")).all(Byk.repeater("cPlan in plans"))

		fun groupForCinema(cinemaName: String): PlanGroup {
			fun byCinemaName(group: WebElement): Boolean =
				group.element(By.className("cinema-name")).filterByText(cinemaName)
			return PlanGroup(this.groups.filter(::byCinemaName).first().toString()) // TODO only() === firstOrFail // STOPSHIP removetostring
		}
	}
	
	fun waitToLoad() {
		cinemas.waitToLoad()
		element(By.id("cinemas"))
			.all(By.className("cinema"))
			.filter { cinema -> cinema.element(By.cssSelector("""[type="checkbox"]""")).getAttribute("checked") == "checked" }
			.count()
			.let { count ->
				if (count > 0) {
					films.waitToLoad()
				}
			}
		element(By.id("films"))
			.all(By.className("film"))
			.filter { film -> film.element(By.cssSelector("""[type="checkbox"]""")).getAttribute("checked") == "checked" }
			.count()
			.let { count ->
				if (count > 0) {
					performances.waitToLoad()
				}
			}
		browser.waitForJQuery() // STOPSHIP waitForAngular()
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
