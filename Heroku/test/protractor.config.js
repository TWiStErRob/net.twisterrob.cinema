// Run from IDEA via NodeJS run configuration: `node_modules\protractor\built\cli.js protractor.config.js`
// Additional useful node arguments: --trace-warnings

// https://github.com/angular/protractor/blob/5.2.0/lib/config.ts
exports.config = {
	baseUrl: `http://localhost:${process.env.PORT}`,
	framework: 'jasmine',
	seleniumAddress: 'http://localhost:4444/wd/hub',
	// is broken in protractor@5.2: E/BlockingProxy - (node:2484) TypeError: Cannot read property 'toString' of null
	// but forcing protractor to use blocking-proxy@1.0.1 instead of 0.0.5 via blocking-proxy-hack works.
	// Debug attach from IntelliJ IDEA is not working with the following message:
	// I/BlockingProxy - Starting BlockingProxy with args: --fork,--seleniumAddress,http://localhost:4444/wd/hub,--logDir,logs
	// E/BlockingProxy - Error: listen EADDRINUSE :::64617
	// to fix it, change bpRunner.js this way:
	//+var execArgv = process.execArgv.filter(function(arg) {
	//+    return arg.indexOf('--debug-brk=') !== 0 && arg.indexOf('--inspect') !== 0; });
	//-this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true });
	//+this.bpProcess = child_process_1.fork(BP_PATH, args, { silent: true, execArgv });
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
		'src/cinemas.spec.js',
		'src/films.spec.js',
		'src/performances.spec.js',
		'src/cinemas-auth.spec.js',
		'src/*.spec.js',
	],
	params: {
		user: {
			name: 'papprs@gmail.com',
			password: 'papprspapprs',
		},
	},
	jasmineNodeOpts: {
		// Disable the "....F....x.." logging in favor of custom reporters
		print: () => void 0,
	},
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
