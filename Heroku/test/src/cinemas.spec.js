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

		cinemas.london.list
				.filter(notFavoritedCinema)
				.each((cinema) => expect(cinema).toHaveIcon('star-empty'));
		cinemas.london.list
				.filter(favoritedCinema)
				.each((cinema) => expect(cinema).toHaveIcon('heart'));
	});

	it('should show some favorite cinemas', function () {
		expect(cinemas.favorites.list).not.toBeEmptyArray();

		cinemas.favorites.list
				.each((cinema) => expect(cinema).toHaveIcon('heart'));
	});

	it('should show no other cinemas', function () {
		expect(cinemas.other.list).toBeEmptyArray();
	});

	describe('selection buttons', function () {

		beforeEach(cinemaListSanityCheck);
		afterEach(cinemaListSanityCheck);

		it('should select all', function () {
			cinemas.buttons.all.click();

			cinemas.favorites.list
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.london.list
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.other.list
					.each((cinema) => expect(cinema).toBeSelected());
		});

		it('should select none', function () {
			cinemas.buttons.none.click();

			cinemas.favorites.list
					.each((cinema) => expect(cinema).not.toBeSelected());
		});

		it('should select London cinemas only', function () {
			cinemas.buttons.london.click();

			cinemas.london.list
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.favorites.list
					.filter(londonCinema)
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.favorites.list
					.filter(notLondonCinema)
					.each((cinema) => expect(cinema).not.toBeSelected());
			cinemas.other.list
					.each((cinema) => expect(cinema).not.toBeSelected());
		});

		it('should display London cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.london.click();

			cinemas.london.list.each((cinema) => expect(cinema).toBeDisplayed());
			cinemas.favorites.list.each((cinema) => expect(cinema).not.toBeDisplayed());
			cinemas.other.list.each((cinema) => expect(cinema).not.toBeDisplayed());
		});

		it('should select favorite cinemas only', function () {
			cinemas.buttons.favorites.click();

			cinemas.favorites.list
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.london.list
					.filter(favoritedCinema)
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.london.list
					.filter(notFavoritedCinema)
					.each((cinema) => expect(cinema).not.toBeSelected());
			cinemas.other.list
					.filter(favoritedCinema)
					.each((cinema) => expect(cinema).toBeSelected());
			cinemas.other.list
					.filter(notFavoritedCinema)
					.each((cinema) => expect(cinema).not.toBeSelected());
		});

		it('should display favorite cinemas', function () {
			cinemas.favorites.collapse();
			cinemas.london.collapse();
			cinemas.other.collapse();

			cinemas.buttons.favorites.click();

			cinemas.london.list.each((cinema) => expect(cinema).not.toBeDisplayed());
			cinemas.favorites.list.each((cinema) => expect(cinema).toBeDisplayed());
			cinemas.other.list.each((cinema) => expect(cinema).not.toBeDisplayed());
		});

		function cinemaListSanityCheck() {
			expect(cinemas.favorites.list).not.toBeEmptyArray();
			expect(cinemas.london.list).not.toBeEmptyArray();
			expect(cinemas.other.list).toBeEmptyArray();
		}
	});
});
