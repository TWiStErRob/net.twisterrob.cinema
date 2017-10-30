import app, { films as films } from './dsl/app';
import { anyWithText, noneWithText } from './helpers/protractor-filters';

function notWatchedFilm(film) {
	return noneWithText(films.watched.list, film.getText());
}

function watchedFilm(film) {
	return anyWithText(films.watched.list, film.getText());
}

function notNewFilm(film) {
	return noneWithText(films.new.list, film.getText());
}

function newFilm(film) {
	return anyWithText(films.new.list, film.getText());
}

describe('Films display', function () {

	beforeEach(function () {
		app.goToPlanner();
		app.wait();
	});

	describe('', function () {

		it('should show some new films', function () {
			expect(films.new.list).not.toBeEmptyArray();
			expectEach(films.new.list).toHaveIcon('eye-open');
		});

		it('should show some watched films', function () {
			films.watched.expand();

			expect(films.watched.list).not.toBeEmptyArray();
			expectEach(films.watched.list).toHaveIcon('eye-close');
		});
	});

	describe('accordions', function () {

		it('new should expand', function () {
			films.new.expand();

			expect(films.new.list).not.toBeEmptyArray();
			expectEach(films.new.list).toBeDisplayed();
		});

		it('new should collapse', function () {
			films.new.collapse();

			expectEach(films.new.list).not.toBeDisplayed();
		});

		it('watched should expand', function () {
			films.watched.expand();

			expect(films.watched.list).not.toBeEmptyArray();
			expectEach(films.watched.list).toBeDisplayed();
		});

		it('watched should collapse', function () {
			films.watched.collapse();

			expectEach(films.watched.list).not.toBeDisplayed();
		});
	});

	describe('selection buttons', function () {

		beforeEach(filmListSanityCheck);
		afterEach(filmListSanityCheck);

		it('should select all', function () {
			films.buttons.all.click();

			expectEach(films.watched.list).toBeSelected();
			expectEach(films.new.list).toBeSelected();
		});

		it('should select none', function () {
			films.watched.expand();

			films.buttons.none.click();

			expectEach(films.watched.list).not.toBeSelected();
			expectEach(films.new.list).not.toBeSelected();
		});

		it('should select new films only', function () {
			films.watched.expand();

			films.buttons.new.click();

			expectEach(films.new.list).toBeSelected();
			expectEach(films.watched.list).not.toBeSelected();
		});

		it('should display new films', function () {
			films.new.collapse();
			films.watched.collapse();

			films.buttons.new.click();

			expectEach(films.new.list).toBeDisplayed();
			expectEach(films.watched.list).not.toBeDisplayed();
		});

		function filmListSanityCheck() {
			// not empty test data
			expect(films.new.list).not.toBeEmptyArray();
			expect(films.watched.list).not.toBeEmptyArray();

			// distinct films
			expect(films.new.list.filter(watchedFilm)).toBeEmptyArray();
			expect(films.watched.list.filter(newFilm)).toBeEmptyArray();

			// correct icons
			expectEach(films.new.list).toHaveIcon('eye-open');
			expectEach(films.watched.list).toHaveIcon('eye-close');
		}
	});
});
