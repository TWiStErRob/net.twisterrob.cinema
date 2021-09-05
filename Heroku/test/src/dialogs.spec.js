import app, { date, cinemas, films, performances, plans } from './dsl/app';

describe('Dialogs', function () {

	beforeEach(app.goToPlanner);

	it('opens calendar picker', function () {
		expect(date.buttons.change).not.toBeDisabled();

		date.buttons.change.click();

		expect(date.buttons.done).toBeDisplayed();
	});

	it('can add arbitrary view', function () {
		expect(films.buttons.addView).not.toBeDisabled();

		films.buttons.addView.click();

		expect(films.addViewDialog.element).toBeDisplayed();
		expect(films.addViewDialog.header).toMatchRegex(/^View of/);
		expect(films.addViewDialog.buttons.add).toBeDisplayed();
		expect(films.addViewDialog.buttons.cancel).toBeDisplayed();
	});

	it('can add non-watched film view', function () {
		films.new.expand();

		const film = films.new.items.get(1);
		film.iconEl().click();

		expect(films.addViewDialog.element).toBeDisplayed();
		expect(films.addViewDialog.header).toMatchRegex(/^View of/);
		expect(films.addViewDialog.header.getText()).toContain(film.nameEl2().getText());
		expect(films.addViewDialog.buttons.add).toBeDisplayed();
		expect(films.addViewDialog.buttons.cancel).toBeDisplayed();
	});

	it('can remove watched film view', function () {
		films.watched.expand();

		const film = films.watched.items.get(0);
		film.iconEl().click();

		expect(films.removeViewDialog.element).toBeDisplayed();
		expect(films.removeViewDialog.header).toHaveText('Deleting a View');
		expect(films.removeViewDialog.element.getText()).toContain(film.nameEl2().getText());
		expect(films.removeViewDialog.buttons.ok).toBeDisplayed();
		expect(films.removeViewDialog.buttons.cancel).toBeDisplayed();
	});

	it('can open plan options', function () {
		expect(films.buttons.all).not.toBeDisabled();
		films.buttons.all.click();
		app.wait();
		expect(performances.buttons.options).not.toBeDisabled();

		performances.buttons.options.click();

		expect(performances.optionsDialog.element).toBeDisplayed();
		expect(performances.optionsDialog.header).toHaveText('Planner Options');
		expect(performances.optionsDialog.buttons.plan).toBeDisplayed();
		expect(performances.optionsDialog.buttons.cancel).toBeDisplayed();
	});
});
