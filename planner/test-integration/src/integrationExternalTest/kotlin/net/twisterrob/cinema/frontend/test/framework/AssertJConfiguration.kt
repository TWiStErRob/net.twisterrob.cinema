package net.twisterrob.cinema.frontend.test.framework

import org.assertj.core.api.Assertions
import org.assertj.core.configuration.Configuration
import org.assertj.core.configuration.PreferredAssumptionException

class AssertJConfiguration : Configuration() {
	init {
		setPreferredAssumptionException(PreferredAssumptionException.JUNIT5)
		setPrintAssertionsDescriptionEnabled(true)
		registerFormatterForType<org.openqa.selenium.WebElement> { element ->
			"selenium.WebElement(${element})"
				// See org.openqa.selenium.remote.RemoteWebDriver.toString.
				.replace(
					Regex("""\[ChromeDriver: chrome on .*? \((.*?)\)] ->"""),
					"", // "[SessionId: $1]", but it's redundant because logs will contain that.
				)
		}
		registerFormatterForType<net.twisterrob.cinema.frontend.test.pages.planner.Film> {
			"planner.Film(${it.safe { name.text }})"
		}
		registerFormatterForType<net.twisterrob.cinema.frontend.test.pages.planner.Cinema> {
			"planner.Cinema(${it.safe { name.text }})"
		}
	}
}

private inline fun <T> T.safe(block: T.() -> String): String =
	runCatching(block).recover { it.toString() }.getOrThrow()

private inline fun <reified T> registerFormatterForType(noinline formatter: (T) -> String) {
	Assertions.registerFormatterForType(T::class.java, formatter)
}
