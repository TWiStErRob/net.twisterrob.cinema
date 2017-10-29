export default class Group {

	constructor(groupID, listID, itemClass) {
		this.header = element(by.id(groupID)).element(by.css('.panel-heading'));
		this.list = element(by.id(listID)).all(by.css(itemClass));
	}

	click() {
		this.header.click();
	}

	collapse() {
		this.list.isDisplayed().then((isDisplayed) => {
			if (isDisplayed) {
				// displayed means it's expanded, so click to collapse
				this.header.click();
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
				this.header.click();
			}
		});
	}
}
