// Run from IDEA via NodeJS run configuration: `node_modules\protractor\built\cli.js protractor.config.js`
// Additional useful node arguments: --trace-warnings

// https://github.com/angular/protractor/blob/5.4.0/lib/config.ts
exports.config = {
	baseUrl: `http://localhost:${process.env.PORT}`,
	seleniumAddress: 'http://localhost:4444/wd/hub',
	useBlockingProxy: true,
	highlightDelay: 0,
	webDriverLogDir: 'logs',
	multiCapabilities: [
		{
			browserName: 'chrome',
			loggingPrefs: { browser: 'ALL' },
		},
	],
	specs: [
		'src/app.spec.js',
		'src/date.spec.js',
		'src/cinemas.spec.js',
		'src/films.spec.js',
		'src/performances.spec.js',
		'src/plans.spec.js',
		'src/url.spec.js',
		// TOFIX Tried these: https://stackoverflow.com/q/60117232 + https://www.protractortest.org/#/browser-setup#adding-chrome-specific-options, but no luck.
		//'src/cinemas-auth.spec.js',
		'src/dialogs.spec.js',
		//'src/*.spec.js',
	],
	params: {
		user: {
			name: 'papprs@gmail.com',
			password: 'papprspapprs',
		},
	},
	framework: 'jasmine2',
	jasmineNodeOpts: {
		// Disable the "....F....x.." logging in favor of custom reporters
		print: () => void 0,
		// sets jasmine.DEFAULT_TIMEOUT_INTERVAL
		defaultTimeoutInterval: 30 * 1000,
	},
	plugins: [
		/*{ // Warning: protractor-browser-logs cannot work together with this
			package: 'protractor-screenshoter-plugin',
			screenshotPath: 'logs/reports',
			screenshotOnExpect: 'failure',
			screenshotOnSpec: 'failure',
			withLogs: 'true',
			writeReportFreq: 'asap',
			clearFoldersBeforeTest: true,
		},*/
	],
	onPrepare: function () {
		patchJasmineMethods();
		require('jasmine-expect');
		require('jasmine-expect-moment');
		require('protractor-helpers');
		require('babel-core/register'); // import/export/class/etc only works after this
		require('./src/helpers/protractor-shortcuts');
		jasmine.getEnv().beforeAll(function () {
			jasmine.MAX_PRETTY_PRINT_DEPTH = 4;
			jasmine.addMatchers(require('./src/matchers/generic').default);
			jasmine.addMatchers(require('./src/matchers/app').default);
			browser.driver.manage().window().maximize();
			disableAnimations();
		});
		printTestingProgress();
		verifyLogsAroundEachTest();
	},
};

function disableAnimations() {
	browser.addMockModule('disableNgAnimate', function () {
		/* global angular: false // do not require() angular, we need the one in the browser */
		angular.module('disableNgAnimate', []).run(['$animate', function ($animate) {
			$animate.enabled(false);
		}]);
	});
	// TODO consider also https://declara.com/content/J1J2Gkk1
	//element('body').allowAnimations(false);
}

function printTestingProgress() {
	const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
	//noinspection JSCheckFunctionSignatures
	jasmine.getEnv().addReporter(new SpecReporter({
		colors: {
			enabled: true,
			successful: 'green',
			failed: ['bold', 'red'],
			pending: 'yellow',
		},
		suite: {
			displayNumber: true,
		},
		spec: {
			displaySuccessful: true,
			displayFailed: true,
			displayPending: true,
			displayDuration: true,
			displayErrorMessages: true,
			displayStacktrace: true,
		},
		summary: {
			displaySuccessful: false,
			displayFailed: true,
			displayPending: false,
			displayDuration: false,
			displayErrorMessages: true,
			displayStacktrace: false,
		},
	}));
}

function verifyLogsAroundEachTest() {
	if (global.logs) {
		throw new Error('Cannot set up protractor logging checks, name is already in use by someone!');
	}
	// better than protractor/protractor-console-plugin@0.1.1 because it actually fails individual tests
	global.logs = require('protractor-browser-logs')(browser, {
		reporters: [
			logEntries,
		],
	});
	logs.byText = text => entry => entry.message.indexOf(text) !== -1;

	jasmine.getEnv().beforeEach(function () {
		logs.reset();
		logs.ignore('favicon');
		logs.ignore(logs.or(logs.INFO, logs.DEBUG));
		// everything else is unexpected and fails the test
		// Custom ignores: logs.expect(logs.and(logs.WARNING, logs.byText("message")));
	});

	jasmine.getEnv().afterEach(function () {
		return logs.verify();
	});

	const COLORS = { magenta: 35, yellow: 33, red: 31, grey: 37 };
	const LOG_COLORS = { INFO: COLORS.magenta, WARNING: COLORS.yellow, SEVERE: COLORS.red };
	const LOG_METHODS = { INFO: "log", WARNING: "warn", SEVERE: "error" };
	const logging = require('selenium-webdriver').logging;

	function logEntries(entries) {
		entries
				.filter(function (entry) {
					return logging.Level.DEBUG.value < entry.level.value;
				})
				.forEach(function (entry) {
					const color = LOG_COLORS[entry.level.name] || COLORS.grey;
					const method = LOG_METHODS[entry.level.name] || entry.level.name.toLowerCase();
					console.log(`console.\u001b[${color}m${method} - ${entry.message}\u001b[39m`);
				});
	}
}

function patchJasmineMethods() {
	patchPendingToIncludeSpace();
	patchPrettyPrintNotDeepPrintElementFinders();
}
function patchPendingToIncludeSpace() {
	const originalPending = global.pending;
	global.pending =  function (message) {
		originalPending(": " + message);
	};
}
function patchPrettyPrintNotDeepPrintElementFinders() {
	const originalPP = jasmine.pp;
	jasmine.pp = function (arg) {
		// better alternative to: jasmine.MAX_PRETTY_PRINT_DEPTH = 1;
		if (arg instanceof protractor.ElementFinder) {
			return `ElementFinder{${arg.locator().toString()}}`;
		}
		if (arg instanceof protractor.ElementArrayFinder) {
			return `ElementArrayFinder{${arg.locator().toString()}}`;
		}
		if (arg instanceof Array) {
			if (arg.every((item) =>
							item instanceof protractor.ElementFinder
							|| item instanceof protractor.ElementArrayFinder)) {
				return `[${arg.map(item => item.locator().toString()).join(", ")}]`;
			}
		}
		return originalPP(arg);
	};
}
