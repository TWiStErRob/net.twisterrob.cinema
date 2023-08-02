@file:Suppress("RemoveRedundantBackticks", "ClassName") // STOPSHIP disable in detekt config

package net.twisterrob.cinema.frontend.test

import net.twisterrob.cinema.frontend.test.framework.BrowserExtension
import net.twisterrob.cinema.frontend.test.framework.all
import net.twisterrob.cinema.frontend.test.framework.anyWithText
import net.twisterrob.cinema.frontend.test.framework.assertThat
import net.twisterrob.cinema.frontend.test.pages.PlannerPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By

@ExtendWith(BrowserExtension::class)
class PlansUiTest {
	lateinit var app: PlannerPage
	
	@Nested inner class `Planner display` {

		@Nested inner class `Groups` {

			@Test fun `loads plans`() {
				app.goToPlanner("?d=2017-07-14&c=103&f=184739&f=189108")

				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.list).isDisplayed()
				assertThat(cinemaPlan.items).hasSize(1)

				val plan = cinemaPlan[0]
				assertThat(plan.root).isDisplayed()
			}

			@Test fun `should be collapsible`() {
				app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.list).isDisplayed()

				cinemaPlan.collapse()

				assertThat(cinemaPlan.list).isNotDisplayed()
			}

			@Test fun `should be expandable`() {
				app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
				val cinemaPlan = app.plans.groupForCinema("London - Wood Green")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.list).isNotDisplayed()

				cinemaPlan.expand()

				assertThat(cinemaPlan.list).isDisplayed()
			}

			@Test fun `should toggle`() {
				app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.list).isDisplayed()

				cinemaPlan.expand()
				assertThat(cinemaPlan.list).isDisplayed()

				cinemaPlan.collapse()
				assertThat(cinemaPlan.list).isNotDisplayed()

				cinemaPlan.expand()
				assertThat(cinemaPlan.list).isDisplayed()

				cinemaPlan.collapse()
				assertThat(cinemaPlan.list).isNotDisplayed()
			}

			@Test fun `should show more`() {
				app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.items).hasSize(1)

				cinemaPlan.moreN.click()

				assertThat(cinemaPlan.items).hasSize(6)
			}

			@Test fun `should show all`() {
				app.goToPlanner("?d=2017-07-14&c=70&c=103&f=184739&f=189108")
				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.items).hasSize(1)

				cinemaPlan.moreAll.click()

				assertThat(cinemaPlan.items).hasSize(12)
			}
		}

		@Nested inner class `Plan` {

			fun gotoPlan() : PlannerPage.Plan {
				app.goToPlanner("?d=2017-07-14&c=103&f=184739&f=189108")

				val cinemaPlan = app.plans.groupForCinema("London - Leicester Square")
				cinemaPlan.listPlans()
				assertThat(cinemaPlan.list).isDisplayed()
				assertThat(cinemaPlan.items).hasSize(1)
				// noinspection ES6RedundantAwait, this helps synchronize the await and assertThat mixture.
				return cinemaPlan[0]
			}

			@Test fun `should be removable`() {
				val plan = gotoPlan()
				assertThat(plan.root).isDisplayed()
				assertThat(plan.delete).isDisplayed()

				plan.delete.click()

				//assertThat(plan.root).not.toBePresent() // STOPSHIP how?
			}

			@Test fun `should contain films and breaks`() {
				val plan = gotoPlan()
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

				cinemaPlan.each { plan ->
					val titles = plan.scheduleMovies.all(By.className("film-title"))
					assertThat(anyWithText(titles, firstMovieInPlan.title.text)).isTrue()
				}
				assertThat(cinemaPlan.moreAll).isDisplayed()
				assertThat(cinemaPlan.moreN).isDisplayed()
				assertThat(cinemaPlan.moreN).text().doesNotMatch("""\b0\b""")
			}
		}
	}
}
