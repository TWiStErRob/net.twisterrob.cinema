/* global protractor */

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
 * @param {protractor.ProtractorBrowser|WebDriver} browser
 * @param {string} queryKey
 * @param {function(string): boolean} matcher
 * @returns {{message: string, pass: boolean|Promise<boolean>}}
 */
export function toHaveUrlQuery(browser, queryKey, matcher) {
	const url = require('url');
	const deferred = protractor.promise.defer();
	const verification = {
		message: "unknown failure",
		pass: deferred.promise,
	};
	browser.getCurrentUrl().then(function (currentUrl) {
		const urlObj = url.parse(currentUrl, true);

		if (!urlObj) {
			verification.message = `Url '${currentUrl}' could not be parsed`;
			deferred.fulfill(false);
		} else if (!urlObj.query) {
			verification.message = `Url '${currentUrl}' does not have a query string`;
			deferred.fulfill(false);
		} else if (!urlObj.query[queryKey]) {
			verification.message = `Url '${currentUrl}' does not have a query string param named ${queryKey}.`;
			deferred.fulfill(false);
		} else {
			let queryValue = urlObj.query[queryKey];
			if (matcher(queryValue)) {
				deferred.fulfill(true);
			} else {
				verification.message = `Url '${currentUrl}' does not satisfy a condition for ${queryKey}=${queryValue} given by\n${matcher}`;
				deferred.fulfill(false);
			}
		}
	});
	return verification;
}

export default {
	toHaveClass: () => ({ compare: toHaveClass }),
	toContainClasses: () => ({ compare: toContainClasses }),
	toBeSelected: () => ({ compare: toBeSelected }),
	toHaveUrlQuery: () => ({ compare: toHaveUrlQuery }),
};
