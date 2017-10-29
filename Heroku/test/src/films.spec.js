import app, { films as films } from './dsl/app';

describe('Films display', function () {

	beforeEach(function () {
		app.goToPlanner();
		app.wait();
		app.cinemas.buttons.all.click();
		app.wait();
		films.buttons.none.click();
	});

	it('should show some new films', function () {
		expect(films.new.list).not.toBeEmptyArray();

		films.new.list
				.each((film) => expect(film).toHaveIcon('eye-open'));
	});

	it('should show some watched films', function () {
		films.watched.expand();

		expect(films.watched.list).not.toBeEmptyArray();

		films.watched.list
				.each((film) => expect(film).toHaveIcon('eye-close'));
	});
});
