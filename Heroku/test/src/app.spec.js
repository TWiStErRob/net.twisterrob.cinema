const url = 'http://localhost:8080/planner';

describe('Cineworld Cinemas Planner', function () {
	beforeEach(function () {
		browser.get(url);
	});
	it('should have a title', function () {
		expect(browser.getTitle()).toContain('Cineworld Cinemas Planner');
	});
});
