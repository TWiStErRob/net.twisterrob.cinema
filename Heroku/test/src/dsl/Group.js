export default class Group {

	constructor(groupID, listID, itemClass) {
		this.header = element(by.id(groupID)).element(by.className('panel-heading'));
		this._list = element(by.id(listID));
		this.list = this._list.all(by.className(itemClass));
	}

	click() {
		this.header.click();
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
