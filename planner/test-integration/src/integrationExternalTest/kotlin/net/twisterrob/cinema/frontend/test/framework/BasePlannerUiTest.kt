package net.twisterrob.cinema.frontend.test.framework

import net.twisterrob.cinema.frontend.test.pages.PlannerPage

/**
 * For automatic opening of planner, use [BaseInteractivePlannerUiTest].
 */
@Suppress("detekt.AbstractClassCanBeConcreteClass") // Ensure it cannot be instantiated on its own.
abstract class BasePlannerUiTest : BaseAngularUiTest() {

	protected lateinit var app: PlannerPage
}
