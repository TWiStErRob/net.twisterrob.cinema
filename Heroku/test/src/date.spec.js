import app, {date} from './dsl/app';
import moment from 'moment';

describe('Date display', function () {

	beforeEach(app.goToPlanner);

	describe('editor', function () {

		it('should not be empty', function () {
			expect(date.editor.getText()).toBeNonEmptyString();
		});

		it('should be today\'s date', function () {
			expect(date.editor.getTextAsMoment()).toBeSameMoment(moment(), 'day');
		});
	});

	describe('label', function () {

		it('should not be empty', function () {
			expect(date.label.getText()).toBeNonEmptyString();
		});

		it('should be today\'s date', function () {
			expect(date.label.getTextAsMoment()).toBeSameMoment(moment(), 'day');
		});
	});

	describe('changing', function () {

		const day = 15;
		const selectedDate = moment().date(day);

		beforeEach(function () {
			date.buttons.change.click();
			date.buttons.day(day).click();
		});

		it('should update the editor', function () {
			expect(date.editor.getText()).toBeNonEmptyString();
			expect(date.editor.getTextAsMoment()).toBeSameMoment(selectedDate, 'day');
		});

		it('should update the label', function () {
			expect(date.label.getText()).toBeNonEmptyString();
			expect(date.label.getTextAsMoment()).toBeSameMoment(selectedDate, 'day');
		});

		it('should update the url', function () {
			expect(browser).toHaveUrlQuery('d', (d) =>
					d === selectedDate.format('YYYY-MM-DD'));
		});
	});
});
