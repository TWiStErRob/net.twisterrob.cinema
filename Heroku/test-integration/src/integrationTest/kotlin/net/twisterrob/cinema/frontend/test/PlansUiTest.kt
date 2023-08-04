package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BasePlannerUiTest
import net.twisterrob.cinema.frontend.test.framework.anyMeet
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.pages.planner.PlanGroup
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlansUiTest  : BasePlannerUiTest() {

	@Nested
	inner class Groups {

		@Test fun `loads plans`() {
			app.goToPlanner("?d=2017-07-14&c=103&f=184739&f=189108")

			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.list).isDisplayed()
			assertThat(cinemaPlan.items).hasSize(1)

			val plan = cinemaPlan[0]
			assertThat(plan.root).isDisplayed()
		}

		@Test fun `should be collapsible`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.list).isDisplayed()

			cinemaPlan.tryCollapse()

			assertThat(cinemaPlan.list).isNotDisplayed()
		}

		@Test fun `should be expandable`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
			val cinemaPlan = app.plans.groupForCinema("London - Wood Green")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.list).isNotDisplayed()

			cinemaPlan.tryExpand()

			assertThat(cinemaPlan.list).isDisplayed()
		}

		@Test fun `should toggle`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.list).isDisplayed()

			cinemaPlan.tryExpand()
			assertThat(cinemaPlan.list).isDisplayed()

			cinemaPlan.tryCollapse()
			assertThat(cinemaPlan.list).isNotDisplayed()

			cinemaPlan.tryExpand()
			assertThat(cinemaPlan.list).isDisplayed()

			cinemaPlan.tryCollapse()
			assertThat(cinemaPlan.list).isNotDisplayed()
		}

		@Test fun `should show more`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.items).hasSize(1)

			cinemaPlan.moreN.click()

			assertThat(cinemaPlan.items).hasSize(6)
		}

		@Test fun `should show all`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.items).hasSize(1)

			cinemaPlan.moreAll.click()

			assertThat(cinemaPlan.items).hasSize(12)
		}
	}

	@Nested
	inner class Plan {

		private fun gotoSinglePlan(): PlanGroup {
			app.goToPlanner("?d=2017-07-14&c=103&f=184739&f=189108")

			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.tryListPlans()
			assertThat(cinemaPlan.list).isDisplayed()
			assertThat(cinemaPlan.items).hasSize(1)
			return cinemaPlan
		}

		@Test fun `should be removable`() {
			val cinemaPlan = gotoSinglePlan()
			val plan = cinemaPlan[0]
			assertThat(plan.root).isDisplayed()
			assertThat(plan.delete).isDisplayed()

			plan.delete.click()

			assertThat(cinemaPlan.items).isEmpty()
			assertThat(cinemaPlan.list).isDisplayed()
			assertThat(cinemaPlan.moreN).isDisplayed()
			assertThat(cinemaPlan.moreAll).isDisplayed()
		}

		@Test fun `should contain films and breaks`() {
			val plan = gotoSinglePlan()[0]
			assertThat(plan.scheduleStart).text().isEqualTo("18:25")
			assertThat(plan.scheduleEnd).text().isEqualTo("22:58")
			assertThat(plan.scheduleItems).hasSize(3)
			assertThat(plan.getItemAsMovie(0).startTime).text().isEqualTo("18:25")
			assertThat(plan.getItemAsMovie(0).endTime).text().isEqualTo("20:18")
			assertThat(plan.getItemAsMovie(0).title).text().isEqualTo("Baby Driver")
			assertThat(plan.getItemAsBreak(1).length).text().isEqualTo("27 minutes")
			assertThat(plan.getItemAsMovie(2).startTime).text().isEqualTo("20:45")
			assertThat(plan.getItemAsMovie(2).endTime).text().isEqualTo("22:58")
			assertThat(plan.getItemAsMovie(2).title).text().isEqualTo("(IMAX 3-D) Spider-Man : HOMECOMING")
		}

		@Test fun `should filter by film`() {
			app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108&f=223046")
			val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
			cinemaPlan.moreAll.click()
			val firstMovieInPlan = cinemaPlan[0].getItemAsMovie(0)

			firstMovieInPlan.filterByFilm.click()

			val firstMovieTitle = firstMovieInPlan.title.text
			assertThat(cinemaPlan.plans).allSatisfy { plan ->
				assertThat(plan.scheduleMovieTitles).anyMeet { text().isEqualTo(firstMovieTitle) }
			}
			assertThat(cinemaPlan.moreAll).isDisplayed()
			assertThat(cinemaPlan.moreN).isDisplayed()
			assertThat(cinemaPlan.moreN).text().doesNotMatch("""\b0\b""")
		}
	}
}
