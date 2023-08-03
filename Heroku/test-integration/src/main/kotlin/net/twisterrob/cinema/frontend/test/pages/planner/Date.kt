package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import net.twisterrob.cinema.frontend.test.framework.textContent
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress(
	"UseDataClass", // TODEL https://github.com/detekt/detekt/issues/5339
	"ClassOrdering", // Logical order.
)
class Date(
	private val root: WebElement,
) {

	val buttons = Buttons()

	inner class Buttons {

		val change: WebElement
			get() = root.findElement(By.cssSelector("button"))

		val today: WebElement
			get() = root.findElement(ByAngular.buttonText("Today"))

		val clear: WebElement
			get() = root.findElement(ByAngular.buttonText("Clear"))

		val done: WebElement
			get() = root.findElement(ByAngular.buttonText("Done"))

		fun day(day: String): WebElement =
			root.findElement(ByAngular.buttonText(day))
	}

	val editor = Editor()

	inner class Editor {

		val element: WebElement
			get() = root.findElement(By.id("cineworldDate"))

		val date: LocalDate
			get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("M/d/yy"))
	}

	val label = Label()

	inner class Label {

		val element: WebElement
			get() = root.findElement(By.cssSelector("em.ng-binding"))

		val date: LocalDate
			get() = LocalDate.parse(element.textContent, DateTimeFormatter.ofPattern("EEEE, LLLL d, yyyy"))
	}
}
