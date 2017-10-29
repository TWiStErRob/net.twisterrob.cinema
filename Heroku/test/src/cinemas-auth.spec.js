import app, { cinemas as cinemas } from './dsl/app';

describe('Cinemas display as authenticated user', function () {

	beforeAll(app.login);
	beforeEach(app.goToPlanner);
	afterAll(app.logout);

	it('should allow adding to favorites', function () {
		let cinema = cinemas.london.list.get(1);
		expect(cinemas.favorites.list).toBeArrayOfSize(1);

		cinema.iconEl().click();

		expect(cinemas.favorites.list).toBeArrayOfSize(2);
		expect(cinemas.favorites.list.get(1).getText())
				.toBe(cinema.nameEl().getText());
		expect(cinemas.favorites.list.get(1))
				.toHaveIcon('heart');
	});

	it('should allow removing from favorites', function () {
		pending("Fake data is not consistent with backend");
		let cinema = cinemas.favorites.list.first();

		cinema.iconEl().click();

		expect(cinemas.favorites.list).toBeEmptyArray();
		cinemas.london.list.each(cinema => expect(cinema)
				.not.toHaveIcon('heart'));
	});
});
