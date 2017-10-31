export default class Group {

	constructor(groupID, listID, itemClass) {
		this._header = element(by.id(groupID)).element(by.className('accordion-toggle'));
		this._list = element(by.id(listID));
		this.list = this._list.all(by.className(itemClass));
	}

	click() {
		this._header.click();
	}

	collapse() {
		this._list.isDisplayed().then((isDisplayed) => {
			if (isDisplayed) {
				// displayed means it's expanded, so click to collapse
				this.click();
			} else {
				// not displayed, so it's already collapsed
			}
		});
	}

	expand() {
		this._list.isDisplayed().then((isDisplayed) => {
			if (isDisplayed) {
				// displayed means it's already expanded
			} else {
				// not displayed means it's expanded, so click to collapse
				this.click();
			}
		});
	}
}
