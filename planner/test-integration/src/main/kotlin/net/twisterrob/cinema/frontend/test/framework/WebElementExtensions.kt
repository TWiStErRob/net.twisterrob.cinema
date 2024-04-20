package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions.assertThat
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

val WebElement.textContent: String?
	get() = when (this.tagName) {
		"input", "textarea" -> this.getAttribute("value")
		else -> this.text
	}

val WebElement.isChecked: Boolean
	get() = this.findElement(By.cssSelector("""[type="checkbox"]""")).isSelected

val WebElement.glyphicon: String?
	get() {
		val icons = this.classes.filter { it.startsWith("glyphicon-") }
		assertThat(icons).hasSizeLessThan(2)
		return icons.singleOrNull()?.substringAfter("glyphicon-")
	}

val WebElement.classes: List<String>
	get() = this.getAttribute("class").split(Regex("""\s+"""))

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
