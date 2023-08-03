package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.junit.jupiter.api.BeforeEach

abstract class BaseInteractivePlannerUiTest : BaseAngularUiTest() {

	protected lateinit var app: PlannerPage

	@BeforeEach fun beforeEach() {
		app.goToPlanner()
	}
}
