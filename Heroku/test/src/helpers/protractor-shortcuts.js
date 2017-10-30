//import { ElementArrayFinder, ElementFinder } from 'protractor';
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
