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
			const element = actual.locator().message || actual.locator().toString();
			verification.message = `Expected to have class '${expectedClasses}', but had '${actualClasses}' on ${element}`;
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
		const element = actual.locator().message || actual.locator().toString();
		verification.message = `Missing class '${expectedClass}' from '${classes}' on ${element}`;
		deferred.fulfill(hasClass);
		return hasClass;
	});
	return verification;
}

export default {
	toHaveClass: () => ({ compare: toHaveClass }),
	toContainClasses: () => ({ compare: toContainClasses }),
};
