// Run from IDEA via NodeJS run configuration: `node_modules\protractor\built\cli.js protractor.config.js`
// Additional useful node arguments: --trace-warnings

// https://github.com/angular/protractor/blob/5.2.0/lib/config.ts
exports.config = {
	baseUrl: `http://localhost:${process.env.PORT}`,
	framework: 'jasmine',
	seleniumAddress: 'http://localhost:4444/wd/hub',
	multiCapabilities: [
		{ browserName: 'chrome' },
	],
	specs: [
		'src/app.spec.js',
		'src/cinemas.spec.js',
		'src/films.spec.js',
		'src/performances.spec.js',
		'src/cinemas-auth.spec.js',
		'src/*.spec.js',
	],
	// TODO useBlockingProxy: true, // needs a new protractor (5.2 is breaking)
	onPrepare: function () {
		require('jasmine-expect');
		require('protractor-helpers');
		require('babel-core/register'); // import/export/class/etc only works after this
		require('./src/helpers/protractor-shortcuts');
		jasmine.getEnv().beforeAll(function () {
			jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000;
			jasmine.addMatchers(require('./src/matchers/generic').default);
			jasmine.addMatchers(require('./src/matchers/app').default);
			browser.driver.manage().window().maximize();
			browser.addMockModule('disableNgAnimate', function () {
				/* global angular: false // do not require() angular, we need the one in the browser */
				angular.module('disableNgAnimate', []).run(['$animate', function ($animate) {
					$animate.enabled(false);
				}]);
			});
			// TODO consider also https://declara.com/content/J1J2Gkk1
			//element('body').allowAnimations(false);
		});
	},
};
