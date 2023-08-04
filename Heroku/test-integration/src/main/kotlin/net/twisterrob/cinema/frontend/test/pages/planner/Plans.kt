package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class Plans(
	private val root: WebElement,
) {

	val groups: List<WebElement>
		get() = root.findElements(ByAngular.repeater("cPlan in plans"))

	fun groupForCinema(cinemaName: String): PlanGroup {
		fun byCinemaName(group: WebElement): Boolean =
			group.findElement(By.className("cinema-name")).text == cinemaName
		return PlanGroup(this.groups.single(::byCinemaName))
	}
}
