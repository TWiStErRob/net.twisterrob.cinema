import app, { cinemas as cinemas } from './dsl/app';
import { anyWithText, noneWithText } from './helpers/protractor-filters';

function notFavoritedCinema(cinema) {
	return noneWithText(cinemas.favoriteList, cinema.getText());
}

function favoritedCinema(cinema) {
	return anyWithText(cinemas.favoriteList, cinema.getText());
}

describe('Cinemas display', function () {
	beforeEach(app.goToPlanner);

	it('should show some London cinemas', function () {
		expect(cinemas.londonList).not.toBeEmptyArray();

		cinemas.londonList
				.filter(notFavoritedCinema)
				.each((cinema) => expect(cinema).toHaveIcon('star-empty'));

		cinemas.londonList
				.filter(favoritedCinema)
				.each((cinema) => expect(cinema).toHaveIcon('heart'));
	});

	it('should show some favorite cinemas', function () {
		expect(cinemas.favoriteList).not.toBeEmptyArray();

		cinemas.favoriteList
				.each((cinema) => expect(cinema).toHaveIcon('heart'));
	});

	it('should show no other cinemas', function () {
		expect(cinemas.otherList).toBeEmptyArray();
	});
});
