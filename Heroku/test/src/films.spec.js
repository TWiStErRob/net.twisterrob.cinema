import app, { films as films } from './dsl/app';
import { anyWithText, noneWithText } from './helpers/protractor-filters';

function notWatchedFilm(film) {
	return noneWithText(films.watched.items, film.getText());
}

function watchedFilm(film) {
	return anyWithText(films.watched.items, film.getText());
}

function notNewFilm(film) {
	return noneWithText(films.new.items, film.getText());
}

function newFilm(film) {
	return anyWithText(films.new.items, film.getText());
}

describe('Films display', function () {

	beforeEach(app.goToPlanner);

	describe('', function () {

		it('should show some new films', function () {
			expect(films.new.items).not.toBeEmptyArray();
			expectEach(films.new.items).toHaveIcon('eye-open');
		});

		it('should show some watched films', function () {
			films.watched.expand();

			expect(films.watched.items).not.toBeEmptyArray();
			expectEach(films.watched.items).toHaveIcon('eye-close');
		});
	});

	describe('accordions', function () {

		describe('new', function () {

			it('should have header', function () {
				expect(films.new.header).toMatchRegex(/^New Films \(\d+\)$/);
				films.new.items.count().then((count) =>
						expect(films.new.header.getText()).toContain(count.toString()));
			});

			it('should expand', function () {
				films.new.expand();

				expect(films.new.items).not.toBeEmptyArray();
				expectEach(films.new.items).toBeDisplayed();
			});

			it('should collapse', function () {
				films.new.collapse();

				expectEach(films.new.items).not.toBeDisplayed();
			});

			it('should toggle', function () {
				films.new.expand();
				expectEach(films.new.items).toBeDisplayed();
				films.new.collapse();
				expectEach(films.new.items).not.toBeDisplayed();
				films.new.expand();
				expectEach(films.new.items).toBeDisplayed();
			});
		});

		describe('watched', function () {

			it('should have header', function () {
				expect(films.watched.header).toMatchRegex(/^Watched \(\d+\)$/);
				films.watched.items.count().then((count) =>
						expect(films.watched.header.getText()).toContain(count.toString()));
			});

			it('should expand', function () {
				films.watched.expand();

				expect(films.watched.items).not.toBeEmptyArray();
				expectEach(films.watched.items).toBeDisplayed();
			});

			it('should collapse', function () {
				films.watched.collapse();

				expectEach(films.watched.items).not.toBeDisplayed();
			});

			it('should toggle', function () {
				films.watched.expand();
				expectEach(films.watched.items).toBeDisplayed();
				films.watched.collapse();
				expectEach(films.watched.items).not.toBeDisplayed();
				films.watched.expand();
				expectEach(films.watched.items).toBeDisplayed();
			});
		});
	});

	describe('selection buttons', function () {

		beforeEach(filmListSanityCheck);
		afterEach(filmListSanityCheck);

		it('should select all', function () {
			films.buttons.all.click();

			expectEach(films.watched.items).toBeSelected();
			expectEach(films.new.items).toBeSelected();
		});

		it('should select none', function () {
			films.watched.expand();

			films.buttons.none.click();

			expectEach(films.watched.items).not.toBeSelected();
			expectEach(films.new.items).not.toBeSelected();
		});

		it('should select new films only', function () {
			films.watched.expand();

			films.buttons.new.click();

			expectEach(films.new.items).toBeSelected();
			expectEach(films.watched.items).not.toBeSelected();
		});

		it('should display new films', function () {
			films.new.collapse();
			films.watched.collapse();

			films.buttons.new.click();

			expectEach(films.new.items).toBeDisplayed();
			expectEach(films.watched.items).not.toBeDisplayed();
		});

		function filmListSanityCheck() {
			// not empty test data
			expect(films.new.items).not.toBeEmptyArray();
			expect(films.watched.items).not.toBeEmptyArray();

			// distinct films
			// TODO this fails weirdly if neither list is visible (accordions collapsed)
			expect(films.new.items.filter(watchedFilm)).toBeEmptyArray();
			expect(films.watched.items.filter(newFilm)).toBeEmptyArray();

			// correct icons
			expectEach(films.new.items).toHaveIcon('eye-open');
			expectEach(films.watched.items).toHaveIcon('eye-close');
		}
	});
});
