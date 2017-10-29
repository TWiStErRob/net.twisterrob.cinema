import app, { performances as performances } from './dsl/app';

describe('Performances display', function () {

	it('loads', function () {
		browser.get('/planner/?d=2017-07-14&c=103&f=184739&f=189108');

		performances.wait();

		const allPlans = element(by.id('plan-results')).all(by.css('.plans .plan'));
		expect(allPlans.first()).toBeDisplayed();
	});
});
