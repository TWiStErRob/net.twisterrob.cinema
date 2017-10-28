import { until } from 'selenium-webdriver';
import { ElementFinder } from 'protractor';

const driver = browser.driver;

function delayedExecute(locator, action) {
	driver.wait(until.elementLocated(locator));
	const element = driver.findElement(locator);
	driver.wait(until.elementIsVisible(element));
	action(element);
}

let nonAngular = function (action) {
	browser.waitForAngularEnabled(false);
	try {
		action(browser);
	} finally {
		browser.waitForAngularEnabled(true);
	}
};

ElementFinder.prototype.iconEl = function () {
	return this.element(by.css('.glyphicon'));
};
ElementFinder.prototype.nameEl = function () {
	return this.element(by.css('.cinema-name'));
};
const cinemaLocatorInList = by.css('.cinema');
export const cinemas = {
	londonList: element(by.id('cinemas-list-london')).all(cinemaLocatorInList),
	favoriteList: element(by.id('cinemas-list-favs')).all(cinemaLocatorInList),
	otherList: element(by.id('cinemas-list-other')).all(cinemaLocatorInList),
};

export default {
	goToPlanner: function () {
		browser.get('/planner');
	},
	login: function () {
		nonAngular((browser) => browser.get('/login'));
		expect(browser.driver.getCurrentUrl()).toMatch(/^https:\/\/accounts\.google\.com\/.*/);

		delayedExecute(By.name('identifier'), (identifier) => identifier.sendKeys('papprs@gmail.com'));
		delayedExecute(By.id('identifierNext'), (next) => next.click());

		// Semi-transparent blocker is shown above the form, wait for it to disappear.
		driver.wait(until.stalenessOf(driver.findElement(By.css('#initialView > footer ~ div'))));

		delayedExecute(By.name('password'), (password) => password.sendKeys('papprspapprs'));
		delayedExecute(By.id('passwordNext'), (next) => next.click());

		driver.wait(until.urlMatches(/\/#$/), jasmine.DEFAULT_TIMEOUT_INTERVAL,
				"Google OAuth Login should redirect to home page");
	},
	logout: function () {
		nonAngular((browser) => browser.get('/logout'));

		driver.wait(until.urlMatches(/\//), jasmine.DEFAULT_TIMEOUT_INTERVAL,
				"Logout should redirect to home page");
	},
	cinemas: cinemas,
};
