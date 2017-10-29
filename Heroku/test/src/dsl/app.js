import helpers from 'protractor-helpers';
import { ElementFinder } from 'protractor';
import { login, logout } from './non-app';
import CinemaGroup from './CinemaGroup';
import FilmGroup from './FilmGroup';

ElementFinder.prototype.iconEl = function () {
	return this.element(by.css('.glyphicon'));
};
ElementFinder.prototype.nameEl = function () {
	return this.element(by.css('.cinema-name'));
};

function waitFor(classNameToWaitFor) {
	const elemToWaitFor = element.all(by.css(classNameToWaitFor)).first();
	helpers.waitForElementToDisappear(elemToWaitFor, jasmine.DEFAULT_TIMEOUT_INTERVAL);
}

export const cinemas = {
	wait() {
		waitFor('.cinemas-loading');
	},
	buttons: {
		all: element(by.id('cinemas-all')),
		favorites: element(by.id('cinemas-favs')),
		london: element(by.id('cinemas-london')),
		none: element(by.id('cinemas-none')),
	},
	london: new CinemaGroup('cinemas-group-london', 'cinemas-list-london'),
	favorites: new CinemaGroup('cinemas-group-favs', 'cinemas-list-favs'),
	other: new CinemaGroup('cinemas-group-other', 'cinemas-list-other'),
};

export const films = {
	wait() {
		cinemas.wait();
		waitFor('.films-loading');
	},
	buttons: {
		addView: element(by.id('films-addView')),
		all: element(by.id('films-all')),
		new: element(by.id('films-new')),
		none: element(by.id('films-none')),
	},
	new: new FilmGroup('films-group', 'films-list'),
	watched: new FilmGroup('films-group-watched', 'films-list-watched'),
};

export const performances = {
	wait() {
		films.wait();
		waitFor('.performances-loading');
	},
	buttons: {
		plan: element(by.id('plan-plan')),
		options: element(by.id('plan-options')),
	},
};

export default {
	goToPlanner: function () {
		browser.get('/planner');
	},
	login,
	logout,
	cinemas,
	films,
	performances,
};
