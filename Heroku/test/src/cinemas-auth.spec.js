import app, { cinemas as cinemas } from './dsl/app';

describe('Cinemas display as authenticated user', function () {

	beforeAll(app.login);
	beforeEach(app.goToPlanner);
	afterAll(app.logout);

	it('should allow adding to favorites', function () {
		let cinema = cinemas.london.items.get(1);
		expect(cinemas.favorites.items).toBeArrayOfSize(1);

		cinema.iconEl().click();

		expect(cinemas.favorites.items).toBeArrayOfSize(2);
		expect(cinemas.favorites.items.get(1).getText())
				.toBe(cinema.nameEl().getText());
		expect(cinemas.favorites.items.get(1))
				.toHaveIcon('heart');
	});

	it('should allow removing from favorites', function () {
		pending("Fake data is not consistent with backend");
		let cinema = cinemas.favorites.items.first();

		cinema.iconEl().click();

		expect(cinemas.favorites.items).toBeEmptyArray();
		expectEach(cinemas.london.items).not.toHaveIcon('heart');
	});
});
