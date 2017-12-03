import { until } from 'selenium-webdriver';
/* global By */
const driver = browser.driver;

function delayedExecute(locator, action) {
	driver.wait(until.elementLocated(locator));
	const element = driver.findElement(locator);
	driver.wait(until.elementIsVisible(element));
	action(element);
}

function nonAngular(action) {
	browser.waitForAngularEnabled(false);
	try {
		action();
	} finally {
		browser.waitForAngularEnabled(true);
	}
}

function _login() {
	browser.get('/login');
	expect(driver.getCurrentUrl()).toMatch(/^https:\/\/accounts\.google\.com\/.*/);

	delayedExecute(By.name('identifier'), (identifier) => identifier.sendKeys(browser.params.user.name));
	delayedExecute(By.id('identifierNext'), (next) => next.click());

	// Semi-transparent blocker is shown above the form, wait for it to disappear.
	driver.wait(until.stalenessOf(driver.findElement(By.css('#initialView > footer ~ div'))));

	delayedExecute(By.name('password'), (password) => password.sendKeys(browser.params.user.password));
	delayedExecute(By.id('passwordNext'), (next) => next.click());

	driver.wait(until.urlMatches(/\/#$/), jasmine.DEFAULT_TIMEOUT_INTERVAL,
			"Google OAuth Login should redirect to home page");
}

export function login() {
	nonAngular(_login);
}

function _logout() {
	browser.get('/logout');

	driver.wait(until.urlMatches(/\//), jasmine.DEFAULT_TIMEOUT_INTERVAL,
			"Logout should redirect to home page");
}

export function logout() {
	nonAngular(_logout);
}
