package net.twisterrob.cinema.frontend.test.pages

import net.twisterrob.cinema.frontend.test.framework.BasePage
import net.twisterrob.cinema.frontend.test.framework.Browser
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.framework.delayedExecute
import net.twisterrob.cinema.frontend.test.framework.isChecked
import net.twisterrob.cinema.frontend.test.framework.nonAngular
import net.twisterrob.cinema.frontend.test.framework.wait
import net.twisterrob.cinema.frontend.test.framework.waitForAngular
import net.twisterrob.cinema.frontend.test.pages.planner.Cinemas
import net.twisterrob.cinema.frontend.test.pages.planner.Date
import net.twisterrob.cinema.frontend.test.pages.planner.Films
import net.twisterrob.cinema.frontend.test.pages.planner.Performances
import net.twisterrob.cinema.frontend.test.pages.planner.Plans
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf
import org.openqa.selenium.support.ui.ExpectedConditions.urlMatches
import java.time.format.DateTimeFormatter

class PlannerPage(
	browser: Browser,
) : BasePage(browser) {

	@FindBy(css = "html[ng-app]")
	private lateinit var app: WebElement

	val date by lazy { Date(app.findElement(By.id("date"))) }
	val cinemas by lazy { Cinemas(app.findElement(By.id("cinemas"))) }
	val films by lazy { Films(app, app.findElement(By.id("films"))) }
	val performances by lazy { Performances(app, app.findElement(By.id("performances"))) }
	val plans by lazy { Plans(app.findElement(By.id("plan-results"))) }

	fun goToPlanner(url: String = "") {
		browser.get("/planner$url")
		browser.initElements(this)
		waitToLoad()
	}

	fun waitToLoad() {
		cinemas.waitToLoad(browser)
		// CONSIDER using the DSL.
		app
			.findElement(By.id("cinemas"))
			.findElements(By.className("cinema"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					films.waitToLoad(browser)
				}
			}
		app
			.findElement(By.id("films"))
			.findElements(By.className("film"))
			.count(WebElement::isChecked)
			.let { count ->
				if (count > 0) {
					performances.waitToLoad(browser)
				}
			}
		browser.waitForAngular()
	}

	// TODO this should be a separate page
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

	// TODO this should be a separate page
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
