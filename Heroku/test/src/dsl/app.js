import helpers from 'protractor-helpers';
import { ElementFinder } from 'protractor';
import { login, logout } from './non-app';
import CinemaGroup from './CinemaGroup';
import FilmGroup from './FilmGroup';
import PlanGroup from './PlanGroup';
import moment from 'moment/moment';

ElementFinder.prototype.iconEl = function () {
	return this.element(by.className('glyphicon'));
};
ElementFinder.prototype.nameEl = function () {
	return this.element(by.className('cinema-name'));
};
ElementFinder.prototype.nameEl2 = function () {
	return this.element(by.className('film-title'));
};

function waitFor(classNameToWaitFor) {
	const elemToWaitFor = element.all(by.css(classNameToWaitFor)).first();
	helpers.waitForElementToDisappear(elemToWaitFor, jasmine.DEFAULT_TIMEOUT_INTERVAL);
}

export const date = {
	buttons: {
		change: element(by.id('date')).element(by.css('button')),
		today: element(by.id('date')).element(by.buttonText('Today')),
		clear: element(by.id('date')).element(by.buttonText('Clear')),
		done: element(by.id('date')).element(by.buttonText('Done')),
		day(day) {
			return element(by.id('date')).element(by.buttonText(day.toString()));
		},
	},
	editor: {
		element: element(by.id('cineworldDate')),
		getText: function () {
			return this.element.getAttribute('value');
		},
		getTextAsMoment: function () {
			return this.getText().then(t => moment(t, 'M/D/YY'));
		},
	},
	label: {
		element: element(by.id('date')).element(by.css('em.ng-binding')),
		getText: function () {
			return this.element.getText();
		},
		getTextAsMoment: function () {
			return this.getText().then(t => moment(t, 'dddd, MMMM D, YYYY'));
		},
	},
};
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
	london: new CinemaGroup('#cinemas-group-london', '#cinemas-list-london'),
	favorites: new CinemaGroup('#cinemas-group-favs', '#cinemas-list-favs'),
	other: new CinemaGroup('#cinemas-group-other', '#cinemas-list-other'),
};

export const films = {
	wait() {
		waitFor('.films-loading');
	},
	buttons: {
		addView: element(by.id('films-addView')),
		all: element(by.id('films-all')),
		new: element(by.id('films-new')),
		none: element(by.id('films-none')),
	},
	new: new FilmGroup('#films-group', '#films-list'),
	watched: new FilmGroup('#films-group-watched', '#films-list-watched'),
	addViewDialog: {
		element: element(by.className('modal-dialog')),
		header: element(by.className('modal-dialog')).element(by.tagName('h3')),
		buttons: {
			add: element(by.className('modal-dialog')).element(by.buttonText('Add')),
			cancel: element(by.className('modal-dialog')).element(by.buttonText('Cancel')),
		},
	},
	removeViewDialog: {
		element: element(by.className('modal-dialog')),
		header: element(by.className('modal-dialog')).element(by.tagName('h1')),
		buttons: {
			ok: element(by.className('modal-dialog')).element(by.buttonText('Yes')),
			cancel: element(by.className('modal-dialog')).element(by.buttonText('Cancel')),
		},
	},
};

const byFilmRoot = element(by.id('performances-by-film'));
const byCinemaRoot = element(by.id('performances-by-cinema'));
export const performances = {
	wait() {
		waitFor('.performances-loading');
	},
	buttons: {
		plan: element(by.id('plan-plan')),
		options: element(by.id('plan-options')),
	},
	byFilm: {
		table: byFilmRoot,
		cinemas: byFilmRoot.element(by.tagName('thead')).all(by.repeater('cinema in cineworld.cinemas')),
		films: byFilmRoot.all(by.repeater('film in cineworld.films')),
		performances(filmName, cinemaName) {
			return this
					.films
					// find the row for the film
					.filterByText(filmName).first()
					// in all columns
					.all(by.repeater('cinema in cineworld.cinemas'))
					// pick the one that has the same index as the cinema
					.get(this.cinemas.indexOf((element) => element.filterByText(cinemaName)))
					// get the cell contents
					.all(by.repeater('performance in performances'))
					// and drill down into the performance (the separating comma is just outside this)
					.all(by.css('.performance'));
		},
	},
	byCinema: {
		table: byCinemaRoot,
		cinemas: byCinemaRoot.all(by.repeater('cinema in cineworld.cinemas')),
		films: byCinemaRoot.element(by.tagName('thead')).all(by.repeater('film in cineworld.films')),
		performances(cinemaName, filmName) {
			return this
					.cinemas
					// find the row for the cinema
					.filterByText(cinemaName).first()
					// in all columns
					.all(by.repeater('film in cineworld.films'))
					// pick the one that has the same index as the film
					.get(this.films.indexOf((element) => element.filterByText(filmName)))
					// get the cell contents
					.all(by.repeater('performance in performances'))
					// and drill down into the performance (the separating comma is just outside this)
					.all(by.css('.performance'));
		},
	},
	optionsDialog: {
		element: element(by.className('modal-dialog')),
		header: element(by.className('modal-dialog')).element(by.tagName('h3')),
		buttons: {
			plan: element(by.className('modal-dialog')).element(by.buttonText('Plan')),
			cancel: element(by.className('modal-dialog')).element(by.buttonText('Cancel')),
		},
	},
};

export const plans = {
	/**
	 * @member {ElementArrayFinder}
	 */
	groups: element(by.id('plan-results')).all(by.repeater('cPlan in plans')),
	/**
	 * @returns {PlanGroup}
	 */
	groupForCinema(cinemaName) {
		/**
		 * @param {ElementFinder} group
		 * @returns {Promise<boolean>}
		 */
		function byCinemaName(group) {
			return group.element(by.className('cinema-name')).filterByText(cinemaName);
		}
		return new PlanGroup(this.groups.filter(byCinemaName).first()); // TODO only() === firstOrFail
	},
};

function wait() {
	cinemas.wait();
	element(by.id('cinemas'))
			.all(by.className('cinema'))
			.filter((cinema) => cinema.element(by.css('[type="checkbox"]')).getAttribute('checked'))
			.count()
			.then((count) => {
				if (count > 0) {
					films.wait();
				}
			});
	element(by.id('films'))
			.all(by.className('film'))
			.filter((film) => film.element(by.css('[type="checkbox"]')).getAttribute('checked'))
			.count()
			.then((count) => {
				if (count > 0) {
					performances.wait();
				}
			});
	browser.waitForAngular();
}

export default {
	wait,
	goToPlanner: function (url = '') {
		browser.get('/planner' + url);
	},
	login,
	logout,
	date,
	cinemas,
	films,
	performances,
	plans,
};
