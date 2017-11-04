import { ElementArrayFinder } from 'protractor';
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
 */
ElementArrayFinder.prototype.filterByText = function (text, inverse = false) {
	const matcher = typeof text === 'string'
			? (label) => label.indexOf(text) !== -1
			: (label) => text.test(label);
	const filter = inverse ? (x) => !matcher(x) : matcher;
	return this.filter(function (item) {
		return item.getText().then(filter);
	});
};
