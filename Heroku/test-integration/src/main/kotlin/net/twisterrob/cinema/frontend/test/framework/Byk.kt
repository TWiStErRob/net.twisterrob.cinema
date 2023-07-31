@file:Suppress("UnusedReceiverParameter")

package net.twisterrob.cinema.frontend.test.framework

import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import java.util.Locale

object Byk

fun Byk.buttonText(text: String): By = ByButtonText(text)
fun Byk.repeater(text: String): By = TODO(text)

private class ByButtonText(private val exactText: String) : By() {

	override fun findElements(context: SearchContext): List<WebElement> = context
		.findElements(By.cssSelector("""button, input[type="button"], input[type="submit"]"""))
		.filter { element ->
			val elementText = when (element.tagName.lowercase(Locale.ROOT)) {
				"button" -> element.text
				else -> element.getAttribute("value")
			}
			elementText == exactText
		}
}
