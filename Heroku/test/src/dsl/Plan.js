export default class Plan {

	/**
	 * @param {ElementFinder} root
	 */
	constructor(root) {
		/** @member {ElementFinder} */
		this.root = root;
		/** @member {ElementFinder} */
		this.delete = root.element(by.buttonText("×"));
		/** @member {ElementArrayFinder} */
		this.schedule = root.element(by.className('plan-films'));
		/** @member {ElementArrayFinder} */
		this.scheduleItems = this.schedule.all(by.css('.plan-film, .plan-film-break'));
		/** @member {ElementArrayFinder} */
		this.scheduleMovies = this.schedule.all(by.css('.plan-film'));
		/** @member {ElementArrayFinder} */
		this.scheduleBreaks = this.schedule.all(by.css('.plan-film-break'));
		// start and end are classed the same way in the global timings as in individual films
		/** @member {ElementFinder} */
		this.scheduleStart = root.all(by.css('.film-start')).first();
		/** @member {ElementFinder} */
		this.scheduleEnd = root.all(by.css('.film-end')).first();
	}

	/**
	 * @param {number|Promise<number>} index
	 * @returns {ElementFinder}
	 */
	getItem(index) {
		//noinspection JSCheckFunctionSignatures can't resolve Promise in a way that's accepted
		return this.scheduleItems.get(index);
	}

	/**
	 *
	 * @param {number|Promise<number>} index
	 * @returns {ScheduleMovieItem}
	 */
	getItemAsMovie(index) {
		const item = this.getItem(index);
		expect(item).toHaveClass('plan-film');
		return new ScheduleMovieItem(item);
	}

	/**
	 * @param {number|Promise<number>} index
	 * @returns {ScheduleBreakItem}
	 */
	getItemAsBreak(index) {
		const item = this.getItem(index);
		expect(item).toHaveClass('plan-film-break');
		return new ScheduleBreakItem(item);
	}
}

export class ScheduleMovieItem {
	/**
	 * @param {ElementFinder} root
	 */
	constructor(root) {
		/** @member {ElementFinder} */
		this.startTime = root.element(by.className('film-start'));
		/** @member {ElementFinder} */
		this.endTime = root.element(by.className('film-end'));
		/** @member {ElementFinder} */
		this.title = root.element(by.className('film-title'));
		/** @member {ElementFinder} */
		this.runtime = root.element(by.className('film-runtime'));
		/** @member {ElementFinder} */
		this.filterByFilm = root.element(by.xpath('button[i[contains(@class, "glyphicon-time")]]'));
		/** @member {ElementFinder} */
		this.filterByScreening = root.element(by.xpath('button[i[contains(@class, "glyphicon-film")]]'));
	}
}

export class ScheduleBreakItem {
	/**
	 * @param {ElementFinder} root
	 */
	constructor(root) {
		/** @member {ElementFinder} */
		this.length = root;
	}
}
