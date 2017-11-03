import app, { cinemas as cinemas } from './dsl/app';
import { anyWithText, noneWithText } from './helpers/protractor-filters';

function notFavoritedCinema(cinema) {
	return noneWithText(cinemas.favorites.items, cinema.getText());
}

function favoritedCinema(cinema) {
	return anyWithText(cinemas.favorites.items, cinema.getText());
}

function notLondonCinema(cinema) {
	return noneWithText(cinemas.london.items, cinema.getText());
}

function londonCinema(cinema) {
	return anyWithText(cinemas.london.items, cinema.getText());
}

describe('Cinemas display', function () {

	beforeEach(app.goToPlanner);

	describe('', function () {

		it('should show some London cinemas', function () {
			expect(cinemas.london.items).not.toBeEmptyArray();
			expectEach(cinemas.london.items.filter(notFavoritedCinema)).toHaveIcon('star-empty');
			expectEach(cinemas.london.items.filter(favoritedCinema)).toHaveIcon('heart');
		});

		it('should show some favorite cinemas', function () {
			expect(cinemas.favorites.items).not.toBeEmptyArray();
			expectEach(cinemas.favorites.items).toBeDisplayed();
			expectEach(cinemas.favorites.items).toHaveIcon('heart');
		});

		it('should show no other cinemas', function () {
			expect(cinemas.other.items).toBeEmptyArray();
		});
	});

	describe('accordions', function () {

		describe('favorites', function () {

			it('should have header', function () {
				expect(cinemas.favorites.header).toMatchRegex(/^Favorite Cinemas \(\d+\)$/);
				cinemas.favorites.items.count().then((count) =>
						expect(cinemas.favorites.header.getText()).toContain(count.toString()));
			});

			it('should expand', function () {
				cinemas.favorites.expand();

				expect(cinemas.favorites.items).not.toBeEmptyArray();
				expectEach(cinemas.favorites.items).toBeDisplayed();
			});

			it('should collapse', function () {
				cinemas.favorites.collapse();

				expectEach(cinemas.favorites.items).not.toBeDisplayed();
			});

			it('should toggle', function () {
				cinemas.favorites.expand();
				expectEach(cinemas.favorites.items).toBeDisplayed();
				cinemas.favorites.collapse();
				expectEach(cinemas.favorites.items).not.toBeDisplayed();
				cinemas.favorites.expand();
				expectEach(cinemas.favorites.items).toBeDisplayed();
			});
		});

		describe('london', function () {

			it('should have header', function () {
				expect(cinemas.london.header).toMatchRegex(/^London Cinemas \(\d+\)$/);
				cinemas.london.items.count().then((count) =>
						expect(cinemas.london.header.getText()).toContain(count.toString()));
			});

			it('should expand', function () {
				cinemas.london.expand();

				expect(cinemas.london.items).not.toBeEmptyArray();
				expectEach(cinemas.london.items).toBeDisplayed();
			});

			it('should collapse', function () {
				cinemas.london.collapse();

				expectEach(cinemas.london.items).not.toBeDisplayed();
			});

			it('should toggle', function () {
				cinemas.london.expand();
				expectEach(cinemas.london.items).toBeDisplayed();
				cinemas.london.collapse();
				expectEach(cinemas.london.items).not.toBeDisplayed();
				cinemas.london.expand();
				expectEach(cinemas.london.items).toBeDisplayed();
			});
		});

		describe('other', function () {

			it('should have header', function () {
				expect(cinemas.other.header).toMatchRegex(/^Other Cinemas \(\d+\)$/);
				cinemas.other.items.count().then((count) =>
						expect(cinemas.other.header.getText()).toContain(count.toString()));
			});

			it('should expand', function () {
				cinemas.other.expand();

				expectEach(cinemas.other.items).toBeDisplayed();
			});

			it('should collapse', function () {
				cinemas.other.collapse();

				expectEach(cinemas.other.items).not.toBeDisplayed();
			});

			it('should toggle', function () {
				cinemas.other.expand();
				expectEach(cinemas.other.items).toBeDisplayed();
				cinemas.other.collapse();
				expectEach(cinemas.other.items).not.toBeDisplayed();
				cinemas.other.expand();
				expectEach(cinemas.other.items).toBeDisplayed();
			});
		});
	});

	describe('selection buttons', function () {

		beforeEach(cinemaListSanityCheck);
		afterEach(cinemaListSanityCheck);

		it('should select all', function () {
			cinemas.buttons.all.click();

			expectEach(cinemas.favorites.items).toBeSelected();
			expectEach(cinemas.london.items).toBeSelected();
			expectEach(cinemas.other.items).toBeSelected();
		});

		it('should select none', function () {
			cinemas.buttons.none.click();

			expectEach(cinemas.favorites.items).not.toBeSelected();
		});

		it('should select London cinemas only', function () {
			cinemas.buttons.london.click();

			expectEach(cinemas.london.items).toBeSelected();
			expectEach(cinemas.favorites.items.filter(londonCinema)).toBeSelected();
			expectEach(cinemas.favorites.items.filter(notLondonCinema)).not.toBeSelected();
			expectEach(cinemas.other.items).not.toBeSelected();
		});

		it('should display London cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.london.click();

			expectEach(cinemas.london.items).toBeDisplayed();
			expectEach(cinemas.favorites.items).not.toBeDisplayed();
			expectEach(cinemas.other.items).not.toBeDisplayed();
		});

		it('should select favorite cinemas only', function () {
			cinemas.buttons.favorites.click();

			expectEach(cinemas.favorites.items).toBeSelected();
			expectEach(cinemas.london.items.filter(favoritedCinema)).toBeSelected();
			expectEach(cinemas.london.items.filter(notFavoritedCinema)).not.toBeSelected();
			expectEach(cinemas.other.items.filter(favoritedCinema)).toBeSelected();
			expectEach(cinemas.other.items.filter(notFavoritedCinema)).not.toBeSelected();
		});

		it('should display favorite cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.favorites.click();

			expectEach(cinemas.london.items).not.toBeDisplayed();
			expectEach(cinemas.favorites.items).toBeDisplayed();
			expectEach(cinemas.other.items).not.toBeDisplayed();
		});

		function cinemaListSanityCheck() {
			expect(cinemas.favorites.items).not.toBeEmptyArray();
			expect(cinemas.london.items).not.toBeEmptyArray();
			expect(cinemas.other.items).toBeEmptyArray();
		}
	});
});
