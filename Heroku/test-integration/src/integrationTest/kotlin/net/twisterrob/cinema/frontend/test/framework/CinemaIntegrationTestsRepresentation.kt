package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.presentation.Representation
import org.openqa.selenium.WebElement

/**
 * [org.assertj.core.api.AbstractAssert.withFailMessage] would be a nice way to create a readable error message,
 * but it would blow up the test lines. This helps a bit by formatting
 */
class CinemaIntegrationTestsRepresentation : Representation {

	override fun toStringOf(obj: Any?): String? =
		when (obj) {
			is WebElement ->
				"WebElement(${obj})"
					// See org.openqa.selenium.remote.RemoteWebDriver.toString.
					.replace(
						Regex("""\[ChromeDriver: chrome on .*? \((.*?)\)] ->"""),
						"", // "[SessionId: $1]", but it's redundant because logs will contain that.
					)

			is Iterable<*> ->
				if (obj.any() && obj.first() is WebElement) {
					val type = "${obj::class.java.simpleName}<WebElement>"
					obj.joinToString(
						// These spaces heavily depend on the way the following classes print [actual] into a message:
						// NoElementsShouldMatch, ElementsShouldMatch, AnyElementShouldMatch, ...
						prefix = "${type}[\n    ",
						separator = "\n    ",
						postfix = "\n  ]"
					) {
						toStringOf(it) ?: error("Mixed list of WebElements and other types: ${obj}")
					}
				} else {
					null
				}

			else ->
				null
		}
}
