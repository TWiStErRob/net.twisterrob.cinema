import app, { plans as plans } from './dsl/app';
import { anyWithText } from './helpers/protractor-filters';

describe('Planner display', function () {

	describe('Groups', function () {

		it('loads plans', async function () {
			await app.goToPlanner('?d=2017-07-14&c=103&f=184739&f=189108');

			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.list).toBeDisplayed();
			expect(cinemaPlan.items).toBeArrayOfSize(1);

			const plan = cinemaPlan.get(0);
			expect(plan.root).toBeDisplayed();
		});

		it('should be collapsible', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108');
			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.list).toBeDisplayed();

			cinemaPlan.collapse();

			expect(cinemaPlan.list).not.toBeDisplayed();
		});

		it('should be expandable', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108');
			const cinemaPlan = plans.groupForCinema("Wood Green");
			cinemaPlan.listPlans();
			expect(cinemaPlan.list).not.toBeDisplayed();

			cinemaPlan.expand();

			expect(cinemaPlan.list).toBeDisplayed();
		});

		it('should toggle', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108');
			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.list).toBeDisplayed();

			cinemaPlan.expand();
			expect(cinemaPlan.list).toBeDisplayed();

			cinemaPlan.collapse();
			expect(cinemaPlan.list).not.toBeDisplayed();

			cinemaPlan.expand();
			expect(cinemaPlan.list).toBeDisplayed();

			cinemaPlan.collapse();
			expect(cinemaPlan.list).not.toBeDisplayed();
		});

		it('should show more', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108');
			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.items).toBeArrayOfSize(1);

			cinemaPlan.moreN.click();

			expect(cinemaPlan.items).toBeArrayOfSize(6);
		});

		it('should show all', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108');
			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.items).toBeArrayOfSize(1);

			cinemaPlan.moreAll.click();

			expect(cinemaPlan.items).toBeArrayOfSize(12);
		});
	});

	describe('Plan', function () {

		/**
		 * @returns {Plan}
		 */
		async function gotoPlan() {
			await app.goToPlanner('?d=2017-07-14&c=103&f=184739&f=189108');

			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.listPlans();
			expect(cinemaPlan.list).toBeDisplayed();
			expect(cinemaPlan.items).toBeArrayOfSize(1);
			return cinemaPlan.get(0);
		}

		it('should be removable', async function () {
			const plan = await gotoPlan();
			expect(plan.root).toBeDisplayed();
			expect(plan.delete).toBeDisplayed();

			plan.delete.click();

			expect(plan.root).not.toBePresent();
		});

		it('should contain films and breaks', async function () {
			const plan = await gotoPlan();
			expect(plan.scheduleStart).toHaveText("18:25");
			expect(plan.scheduleEnd).toHaveText("22:58");
			expect(plan.scheduleItems).toBeArrayOfSize(3);
			expect(plan.getItemAsMovie(0).startTime).toHaveText("18:25");
			expect(plan.getItemAsMovie(0).endTime).toHaveText("20:18");
			expect(plan.getItemAsMovie(0).title).toMatchRegex(/Baby Driver/);
			expect(plan.getItemAsBreak(1).length).toMatchRegex(/\b27 minutes\b/);
			expect(plan.getItemAsMovie(2).startTime).toHaveText("20:45");
			expect(plan.getItemAsMovie(2).endTime).toHaveText("22:58");
			expect(plan.getItemAsMovie(2).title).toMatchRegex(/Spider-Man/);
		});

		it('should filter by film', async function () {
			await app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108&f=223046');
			const cinemaPlan = plans.groupForCinema("Leicester Square");
			cinemaPlan.moreAll.click();
			const plan = cinemaPlan.get(0);
			const firstMovieInPlan = plan.getItemAsMovie(0);

			firstMovieInPlan.filterByFilm.click();

			cinemaPlan.each((plan) => {
				const titles = plan.scheduleMovies.all(by.className('film-title'));
				expect(anyWithText(titles, firstMovieInPlan.title.getText())).toBe(true);
			});
			expect(cinemaPlan.moreAll).toBeDisplayed();
			expect(cinemaPlan.moreN).toBeDisplayed();
			expect(cinemaPlan.moreN).not.toMatchRegex(/\b0\b/);
		});
	});
});
