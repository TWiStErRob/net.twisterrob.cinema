package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Matches any element in [elementList] that has the same text as the resolved value of [text].
 */
fun anyWithText(elementList: List<WebElement>, text: String): Boolean {
	//noinspection JSValidateTypes reduce will resolve correctly: `false` + `||` -> boolean
	return elementList.fold(false) { acc, elem ->
		return@fold acc || text == elem.text
	}
}

/**
 * Checks if there are no elements in [elementList] that have the same text as the resolved value of [text].
 */
fun noneWithText(elementList: List<WebElement>, text: String): Boolean =
	!anyWithText(elementList, text)

/**
 * @param text string contains match
 * @param inverse negate the result
 * @see ElementArrayFinder.filter
 * @see ElementFinder.filterByText
 */
fun List<WebElement>.filterByText(text: String, inverse: Boolean = false): List<WebElement> =
	this.filter { item -> item.filterByText(text, inverse) }

fun List<WebElement>.filterByText(text: Regex, inverse: Boolean = false): List<WebElement> =
	this.filter { item -> item.filterByText(text, inverse) }

/**
 * Creates a filter function to match the text of the element.
 * @param text string contains or regex match
 * @param inverse negate the result
 * @see ElementArrayFinder.filter
 */
fun WebElement.filterByText(text: String, inverse: Boolean = false): Boolean =
	filterByText(Regex(Regex.escape(text)), inverse)

fun WebElement.filterByText(text: Regex, inverse: Boolean = false): Boolean {
	val matcher = fun(label: String) = text.matches(label)
	val filter = if (inverse) ({ x -> !matcher(x) }) else matcher
	return this.text.let(filter)
}

/**
 * Creates a filter function to match that the element has a class.
 */
fun WebElement.filterByClass(className: String): Boolean {
	return this
		.getAttribute("class")
		.let { classes -> (classes ?: "").split(Regex("""\s+""")).indexOf(className) != -1 }
}

fun List<WebElement>.indexOf(filter: (WebElement) -> Boolean): Int {
	val INITIAL_VALUE = -1
	val stack = Throwable().stackTrace
	//noinspection JSValidateTypes it will be a Promise<int>, but the generics don't resolve it on reduce/then
	return this
		.foldIndexed(INITIAL_VALUE) { acc, index, element ->
			if (acc != INITIAL_VALUE) return@foldIndexed acc
			return@foldIndexed if (filter(element)) index else acc
		}
		.let { index ->
			Assertions.assertThat(index)
				.overridingErrorMessage { "Cannot find index of ${filter} in ${this}\n${stack}" }
				.isGreaterThanOrEqualTo(0)
			return@let index
		}
}

fun WebElement.hasSelection(): Boolean =
	this.findElement(By.cssSelector("""[type="checkbox"]""")).isSelected

/**
 * Matches the element to have a Bootstrap glyphicon element inside with the given icon name
 * @param iconName the end of `glyphicon-name`
 */
fun WebElement.hasIcon(iconName: String): Boolean { // STOPSHIP use Condition API?
	val iconElement = this.element(By.className("glyphicon"))
	return iconElement.hasClass("glyphicon-${iconName}")
}

/**
 * Matches the element to have a single class among others.
 * @param expectedClass single class to check for
 */
fun WebElement.hasClass(expectedClass: String): Boolean = // STOPSHIP use Condition API?
	// STOPSHIP val message = "Missing class '${expectedClass}' from '${classes}' on ${this}"
	expectedClass in this.classes

val WebElement.classes: List<String>
	get() {
		val classes = this.getAttribute("class")
		return classes.split(Regex("""\s+"""))
	}

val WebElement.iconEl get() = this.element(By.className("glyphicon"))
val WebElement.nameEl get() = this.element(By.className("cinema-name"))
val WebElement.nameEl2 get() = this.element(By.className("film-title"))
