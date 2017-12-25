import Group from './Group';
import Plan from './Plan';

export default class PlanGroup extends Group {

	/**
	 * @param {ElementFinder} root
	 */
	constructor(root) {
		super(root, '.plans', '.plan');
		this.moreN = root.element(by.className('plans-footer')).element(by.partialButtonText("more ..."));
		this.moreAll = root.element(by.className('plans-footer')).element(by.partialButtonText("All"));
		this.scheduleExplorer = root.element(by.className('schedule-explorer'));
	}

	/**
	 * @returns {Plan}
	 */
	get(index) {
		return new Plan(this.items.get(index));
	}

	/**
	 * @param {function(Plan)} func
	 */
	each(func) {
		this.items.each((item) => func(new Plan(item)));
	}

	// TODO this doesn't work yet, expects after this don't see this.list
	listPlans() {
		this.scheduleExplorer.isSelected().then((isSelected) => {
			if (isSelected) {
				// selected means it's checked, so click to un-check
				this.scheduleExplorer.click();
			} else {
				// not selected, so it's already un-checked
			}
		});
	}
}
