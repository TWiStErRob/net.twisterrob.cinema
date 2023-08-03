package net.twisterrob.cinema.frontend.test.pages.planner

import com.paulhammant.ngwebdriver.ByAngular
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class PlanGroup(
	root: WebElement,
) : Group(root, ".plans", ".plan") {

	val footer: WebElement
		get() = root.findElement(By.className("plans-footer"))

	val moreN: WebElement
		get() = footer.findElement(ByAngular.partialButtonText("more ..."))

	val moreAll: WebElement
		get() = footer.findElement(ByAngular.partialButtonText("All"))

	val scheduleExplorer: WebElement
		get() = root.findElement(By.className("schedule-explorer"))

	val plans: List<Plan>
		get() = this.items.map(::Plan)

	operator fun get(index: Int): Plan =
		Plan(this.items[index])

	fun tryListPlans() {
		if (this.scheduleExplorer.isSelected) {
			// selected means it's checked, so click to un-check
			this.scheduleExplorer.click()
		} else {
			// not selected, so it's already un-checked
		}
	}
}
