import app, { cinemas as cinemas } from './dsl/app';
import { anyWithText, noneWithText } from './helpers/protractor-filters';

function notFavoritedCinema(cinema) {
	return noneWithText(cinemas.favorites.list, cinema.getText());
}

function favoritedCinema(cinema) {
	return anyWithText(cinemas.favorites.list, cinema.getText());
}

function notLondonCinema(cinema) {
	return noneWithText(cinemas.london.list, cinema.getText());
}

function londonCinema(cinema) {
	return anyWithText(cinemas.london.list, cinema.getText());
}

describe('Cinemas display', function () {

	beforeEach(app.goToPlanner);

	it('should show some London cinemas', function () {
		expect(cinemas.london.list).not.toBeEmptyArray();
		expectEach(cinemas.london.list.filter(notFavoritedCinema)).toHaveIcon('star-empty');
		expectEach(cinemas.london.list.filter(favoritedCinema)).toHaveIcon('heart');
	});

	it('should show some favorite cinemas', function () {
		expect(cinemas.favorites.list).not.toBeEmptyArray();

		expectEach(cinemas.favorites.list).toHaveIcon('heart');
	});

	it('should show no other cinemas', function () {
		expect(cinemas.other.list).toBeEmptyArray();
	});

	describe('selection buttons', function () {

		beforeEach(cinemaListSanityCheck);
		afterEach(cinemaListSanityCheck);

		it('should select all', function () {
			cinemas.buttons.all.click();

			expectEach(cinemas.favorites.list).toBeSelected();
			expectEach(cinemas.london.list).toBeSelected();
			expectEach(cinemas.other.list).toBeSelected();
		});

		it('should select none', function () {
			cinemas.buttons.none.click();

			expectEach(cinemas.favorites.list).not.toBeSelected();
		});

		it('should select London cinemas only', function () {
			cinemas.buttons.london.click();

			expectEach(cinemas.london.list).toBeSelected();
			expectEach(cinemas.favorites.list.filter(londonCinema)).toBeSelected();
			expectEach(cinemas.favorites.list.filter(notLondonCinema)).not.toBeSelected();
			expectEach(cinemas.other.list).not.toBeSelected();
		});

		it('should display London cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.london.click();

			expectEach(cinemas.london.list).toBeDisplayed();
			expectEach(cinemas.favorites.list).not.toBeDisplayed();
			expectEach(cinemas.other.list).not.toBeDisplayed();
		});

		it('should select favorite cinemas only', function () {
			cinemas.buttons.favorites.click();

			expectEach(cinemas.favorites.list).toBeSelected();
			expectEach(cinemas.london.list.filter(favoritedCinema)).toBeSelected();
			expectEach(cinemas.london.list.filter(notFavoritedCinema)).not.toBeSelected();
			expectEach(cinemas.other.list.filter(favoritedCinema)).toBeSelected();
			expectEach(cinemas.other.list.filter(notFavoritedCinema)).not.toBeSelected();
		});

		it('should display favorite cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.favorites.click();

			expectEach(cinemas.london.list).not.toBeDisplayed();
			expectEach(cinemas.favorites.list).toBeDisplayed();
			expectEach(cinemas.other.list).not.toBeDisplayed();
		});

		function cinemaListSanityCheck() {
			expect(cinemas.favorites.list).not.toBeEmptyArray();
			expect(cinemas.london.list).not.toBeEmptyArray();
			expect(cinemas.other.list).toBeEmptyArray();
		}
	});
});
