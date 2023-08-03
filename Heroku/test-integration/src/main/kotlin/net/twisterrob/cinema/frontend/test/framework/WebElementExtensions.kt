package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

fun List<WebElement>.safeIndexOf(filter: (WebElement) -> Boolean): Int {
	val index = this.indexOfFirst(filter)
	assertThat(index)
		.overridingErrorMessage { "Cannot find index of ${filter} in ${this}" }
		.isGreaterThanOrEqualTo(0)
	assertThat(this)
		.overridingErrorMessage { "Found multiple matches for ${filter} in ${this}" }
		.satisfiesOnlyOnce { assertThat(it).matches(filter) }
	return index
}

fun WebElement.hasSelection(): Boolean =
	this.findElement(By.cssSelector("""[type="checkbox"]""")).isSelected

/**
 * Matches the element to have a Bootstrap glyphicon element inside with the given icon name.
 * @param iconName the end of `glyphicon-name`
 */
fun WebElement.hasIcon(iconName: String): Boolean { // STOPSHIP use Condition API?
	val iconElement = this.findElement(By.className("glyphicon"))
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

val WebElement.iconEl: WebElement
	get() = this.findElement(By.className("glyphicon"))

val WebElement.nameEl: WebElement
	get() = this.findElement(By.className("cinema-name"))

val WebElement.nameEl2: WebElement // STOPSHIP generalize
	get() = this.findElement(By.className("film-title"))
