export default class Group {

	constructor(groupID, listID, itemClass) {
		this.header = element(by.id(groupID)).element(by.className('accordion-toggle'));
		this.list = element(by.id(listID));
		this.items = this.list.all(by.className(itemClass));
	}

	click() {
		this.header.click();
	}

	collapse() {
		this.list.isDisplayed().then((isDisplayed) => {
			if (isDisplayed) {
				// displayed means it's expanded, so click to collapse
				this.click();
			} else {
				// not displayed, so it's already collapsed
			}
		});
	}

	expand() {
		this.list.isDisplayed().then((isDisplayed) => {
			if (isDisplayed) {
				// displayed means it's already expanded
			} else {
				// not displayed means it's expanded, so click to collapse
				this.click();
			}
		});
	}

	/**
	 * @param {string|RegExp} text
	 * @param {boolean} inverse
	 */
	filter(text, inverse = false) {
		const matcher = typeof text === 'string'
				? (label) => label.indexOf(text) !== -1
				: (label) => text.test(label);
		const filter = inverse ? (x) => !matcher(x) : matcher;
		return this.items.filter(function (item) {
			return item.getText().then(filter);
		});
	}
}
