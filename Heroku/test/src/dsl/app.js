import { ElementFinder } from 'protractor';
import { login, logout } from './non-app';
import PlanGroup from './PlanGroup';

export const plans = {
	/**
	 * @member {ElementArrayFinder}
	 */
	groups: element(by.id('plan-results')).all(by.repeater('cPlan in plans')),
	/**
	 * @returns {PlanGroup}
	 */
	groupForCinema(cinemaName) {
		/**
		 * @param {ElementFinder} group
		 * @returns {Promise<boolean>}
		 */
		function byCinemaName(group) {
			return group.element(by.className('cinema-name')).filterByText(cinemaName);
		}
		return new PlanGroup(this.groups.filter(byCinemaName).first()); // TODO only() === firstOrFail
	},
};

export default {
	wait,
	goToPlanner: function (url = '') {
		return browser.get('/planner' + url);
	},
	login,
	logout,
	date,
	cinemas,
	films,
	performances,
	plans,
};
