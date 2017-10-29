import app from './dsl/app';

describe('Planner', function () {

	beforeEach(app.goToPlanner);

	it('page should have a title', function () {
		expect(browser.getTitle()).toContain('Cineworld Cinemas Planner');
	});
});
