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
}
