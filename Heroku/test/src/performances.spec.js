import app, { performances as performances } from './dsl/app';

describe('Performances display', function () {

	describe('performances table by film', function () {

		beforeEach(() => app.goToPlanner('?d=2017-07-14&c=103&f=184739&f=189108'));

		it('shows film headers', function () {
			expect(performances.byFilm.films).toBeArrayOfSize(2);
			expect(performances.byFilm.films.get(0).getText()).toContain("Spider-Man");
			expect(performances.byFilm.films.get(1).getText()).toContain("Baby Driver");
		});

		it('shows cinema headers', function () {
			expect(performances.byFilm.cinemas).toBeArrayOfSize(1);
			expect(performances.byFilm.cinemas.get(0).getText()).toContain("Leicester Square");
		});

		it('shows performances', function () {
			const times = performances.byFilm.performances("Baby Driver", "Leicester Square");
			expect(times).toBeArrayOfSize(4);
			expect(times.get(0)).toHaveText("12:00");
			expect(times.get(1)).toHaveText("14:40");
			expect(times.get(2)).toHaveText("18:10");
			expect(times.get(3)).toHaveText("20:50");
		});

		it('shows performances', function () {
			const times = performances.byFilm.performances(/Spider-Man/, "Leicester Square");
			expect(times).toBeArrayOfSize(4);
			expect(times.get(0)).toHaveText("11:00");
			expect(times.get(1)).toHaveText("14:10");
			expect(times.get(2)).toHaveText("17:20");
			expect(times.get(3)).toHaveText("20:30");
		});
	});

	describe('performances table by cinema', function () {

		beforeEach(() => app.goToPlanner('?d=2017-07-14&c=70&c=103&f=184739&f=189108'));

		it('shows cinema headers', function () {
			expect(performances.byCinema.cinemas).toBeArrayOfSize(2);
			expect(performances.byCinema.cinemas.get(0).getText()).toContain("Leicester Square");
			expect(performances.byCinema.cinemas.get(1).getText()).toContain("Wood Green");
		});

		it('shows film headers', function () {
			expect(performances.byCinema.films).toBeArrayOfSize(2);
			expect(performances.byCinema.films.get(0).getText()).toContain("Spider-Man");
			expect(performances.byCinema.films.get(1).getText()).toContain("Baby Driver");
		});

		it('shows performances', function () {
			const times = performances.byCinema.performances("Leicester Square", "Baby Driver");
			expect(times).toBeArrayOfSize(4);
			expect(times.get(0)).toHaveText("12:00");
			expect(times.get(1)).toHaveText("14:40");
			expect(times.get(2)).toHaveText("18:10");
			expect(times.get(3)).toHaveText("20:50");
		});

		it('shows performances', function () {
			const times = performances.byCinema.performances("Wood Green", "Baby Driver");
			expect(times).toBeArrayOfSize(5);
			expect(times.get(0)).toHaveText("11:50");
			expect(times.get(1)).toHaveText("14:00");
			expect(times.get(2)).toHaveText("17:40");
			expect(times.get(3)).toHaveText("20:50");
			expect(times.get(4)).toHaveText("23:30");
		});

		it('shows performances', function () {
			const times = performances.byCinema.performances("Wood Green", /Spider-Man/);
			expect(times).toBeEmptyArray();
		});
	});
});
