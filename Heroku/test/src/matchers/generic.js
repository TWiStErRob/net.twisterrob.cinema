function describe(actual) {
	const locator = actual.locator();
	return !locator ? "" + locator : locator.message || locator.toString();
}

/**
 * Matches the element to have all the passed in classes in order.
 * @param {ElementFinder} actual
 * @param {string} expectedClasses single or multiple classes (in strict order)
 * @returns {{message: string, pass: boolean|Promise<boolean>}}
 */
export function toContainClasses(actual, expectedClasses) {
	const verification = {
		message: "unknown failure",
		pass: expect(actual.getAttribute('class').then(function (actualClasses) {
			verification.message = `Expected to have class '${expectedClasses}', but had '${actualClasses}' on ${describe(actual)}`;
			return actualClasses;
		})).toContain(expectedClasses),
	};
	return verification;
}

/**
 * Matches the element to have a single class among others.
 * @param {ElementFinder} actual
 * @param {string} expectedClass single class to check for
 * @returns {{message: string, pass: boolean|Promise<boolean>}}
 */
export function toHaveClass(actual, expectedClass) {
	const deferred = protractor.promise.defer();
	const verification = {
		message: "unknown failure",
		pass: deferred.promise,
	};
	actual.getAttribute('class').then(function (classes) {
		const hasClass = classes.split(/\s+/).indexOf(expectedClass) >= 0;
		verification.message = `Missing class '${expectedClass}' from '${classes}' on ${describe(actual)}`;
		deferred.fulfill(hasClass);
		return hasClass;
	});
	return verification;
}

/**
 * Matches the element to have a single class among others.
 * @param {ElementFinder} actual
 * @param {string} expectedClass single class to check for
 * @returns {{message: string, pass: boolean|Promise<boolean>}}
 */
export function toBeSelected(actual, expectedClass) {
	const deferred = protractor.promise.defer();
	const verification = {
		message: "unknown failure",
		pass: deferred.promise,
	};
	const checkbox = actual.element(by.css('[type="checkbox"]'));
	checkbox.getAttribute('checked').then(function (checkedAttr) {
		const isChecked = !!checkedAttr;
		verification.message = `Expected ${describe(actual)} to be checked, but was not`;
		deferred.fulfill(isChecked);
		return isChecked;
	});
	return verification;
}

export default {
	toHaveClass: () => ({ compare: toHaveClass }),
	toContainClasses: () => ({ compare: toContainClasses }),
	toBeSelected: () => ({ compare: toBeSelected }),
};
