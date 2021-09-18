import app from './dsl/app';
import moment from 'moment';

describe('URL Hash', function () {

	describe('Date', function () {

		it('should preselect today', function () {
			app.goToPlanner();

			expect(browser).toHaveUrlQuery('d', (d) =>
					moment(d, 'YYYY-MM-DD').isSame(moment(), 'day'));
		});

		it('should preselect date', function () {
			app.goToPlanner('?d=2017-07-14');

			expect(browser).toHaveUrlQuery('d', (d) =>
					moment(d, 'YYYY-MM-DD').isSame(moment({year: 2017, month: 6, day: 14}), 'day'));
			expect(app.date.editor).toHaveText('7/14/17');
			expect(app.date.label).toHaveText('Friday, July 14, 2017');
		});
	});

	describe('Cinemas', function () {

		it('should preselect favorites', function () {
			app.goToPlanner();

			expect(browser).toHaveUrlQuery('c', (c) =>
					c === '103');
		});

		it('should preselect cinemas', function () {
			app.goToPlanner('?c=70');

			expectEach(app.cinemas.london.items.filterByText("Wood Green")).toBeSelected();
			expectEach(app.cinemas.london.items.filterByText("Wood Green", true)).not.toBeSelected();
			expectEach(app.cinemas.other.items).not.toBeSelected();
		});
	});

	describe('Films', function () {

		it('should preselect films', function () {
			app.goToPlanner('?f=189108&f=223046');

			expectEach(app.films.new.items.filterByText(/All Eyez On Me|Baby Driver/)).toBeSelected();
			expectEach(app.films.new.items.filterByText(/All Eyez On Me|Baby Driver/, true)).not.toBeSelected();
			expectEach(app.films.watched.items).not.toBeSelected();
		});
	});

	describe('', function () {

		it('should preselect everything', function () {
			app.goToPlanner('?c=70&f=189108&f=223046&d=2017-07-14');

			expect(app.date.editor).toHaveText('7/14/17');
			expectEach(app.cinemas.london.items.filterByText("Wood Green")).toBeSelected();
			expectEach(app.cinemas.london.items.filterByText("Wood Green", true)).not.toBeSelected();
			expectEach(app.cinemas.other.items).not.toBeSelected();
			expectEach(app.films.new.items.filterByText(/All Eyez On Me|Baby Driver/)).toBeSelected();
			expectEach(app.films.new.items.filterByText(/All Eyez On Me|Baby Driver/, true)).not.toBeSelected();
			expectEach(app.films.watched.items).not.toBeSelected();
		});
	});
});
