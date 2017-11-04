import { ElementArrayFinder, ElementFinder } from 'protractor';
import expectEach from './expectEach';

//ElementFinder.prototype.expect = function () {
//	return expect(this);
//};

//ElementArrayFinder.prototype.expect = function () {
//	return expect(this);
//};

//ElementArrayFinder.prototype.expectEach = function (filter) {
//	return expectEach(this, filter);
//};

global.expectEach = function (list) {
	return expectEach(list);
};

/**
 * @param {string|RegExp} text string contains or regex match
 * @param {boolean} inverse negate the result
 * @return {ElementArrayFinder}
 * @see ElementArrayFinder.filter
 * @see ElementFinder.filterByText
 */
ElementArrayFinder.prototype.filterByText = function (text, inverse = false) {
	return this.filter((item) => item.filterByText(text, inverse));
};

/**
 * Creates a filter function to match the text of the element.
 * @param {string|RegExp} text string contains or regex match
 * @param {boolean} inverse negate the result
 * @return {Promise<boolean>}
 * @see ElementArrayFinder.filter
 */
ElementFinder.prototype.filterByText = function (text, inverse = false) {
	const matcher = typeof text === 'string'
			? (label) => label.indexOf(text) !== -1
			: (label) => text.test(label);
	const filter = inverse ? (x) => !matcher(x) : matcher;
	return this.getText().then(filter);
};

/**
 *
 * @param {function(ElementFinder): Promise<boolean>} filter
 * @return {Promise<int>}
 */
ElementArrayFinder.prototype.indexOf = function (filter) {
	const INITIAL_VALUE = -1;
	const stack = new Error().stack;
	//noinspection JSValidateTypes it will be a Promise<int>, but the generics don't resolve it on reduce/then
	return this
			.reduce(function (acc, element, index) {
				if (acc !== INITIAL_VALUE) return acc;
				return filter(element).then(function combine(passed) {
					return passed ? index : acc;
				});
			}, INITIAL_VALUE)
			.then((index) => {
				expect(index).toBeGreaterThanOrEqual(0, `Cannot find index of ${filter} in ${jasmine.pp(this)}\n${stack}`);
				return index;
			});
};
