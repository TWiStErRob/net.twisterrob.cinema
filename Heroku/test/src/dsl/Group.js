export default class Group {

	/**
	 * @param {ElementFinder|string} root root element or CSS selector
	 * @param {ElementFinder|string} content content element or CSS selector inside root
	 * @param {ElementArrayFinder|string} items elements or CSS selector in content
	 */
	constructor(root, content, items) {
		/** @member {ElementFinder} */
		this.root = typeof root !== 'string' ? root
				: element(by.css(root));
		/** @member {ElementFinder} */
		this.header = this.root.element(by.className('accordion-toggle'));
		/** @member {ElementFinder} */
		this.list = typeof  content !== 'string' ? content
				: this.root.element(by.css(content));
		/** @member {ElementArrayFinder} */
		this.items = typeof items !== 'string' ? items
				: this.list.all(by.css(items));
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
}
