package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.PlannerPage

/**
 * For automatic opening of planner, use [BaseInteractivePlannerUiTest].
 */
abstract class BasePlannerUiTest : BaseAngularUiTest() {

	protected lateinit var app: PlannerPage
}
