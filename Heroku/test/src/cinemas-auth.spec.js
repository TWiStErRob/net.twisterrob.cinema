import app, { cinemas as cinemas } from './dsl/app';

describe('Cinemas display as authenticated user', function () {
	beforeAll(app.login);
	beforeEach(app.goToPlanner);
	afterAll(app.logout);

	it('should allow adding to favorites', function () {
		let cinema = cinemas.londonList.get(1);
		expect(cinemas.favoriteList).toBeArrayOfSize(1);

		cinema.iconEl().click();

		expect(cinemas.favoriteList).toBeArrayOfSize(2);
		expect(cinemas.favoriteList.get(1).getText())
				.toBe(cinema.nameEl().getText());
		expect(cinemas.favoriteList.get(1))
				.toHaveIcon('heart');
	});

	it('should allow removing from favorites', function () {
		pending("Fake data is not consistent with backend");
		let cinema = cinemas.favoriteList.first();

		cinema.iconEl().click();

		expect(cinemas.favoriteList).toBeEmptyArray();
		cinemas.londonList.each(cinema => expect(cinema)
				.not.toHaveIcon('heart'));
	});
});
