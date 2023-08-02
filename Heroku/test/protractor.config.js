// Run from IDEA via NodeJS run configuration: `node_modules\protractor\built\cli.js protractor.config.js`
// Additional useful node arguments: --trace-warnings

// https://github.com/angular/protractor/blob/5.4.0/lib/config.ts
exports.config = {
	baseUrl: `http://localhost:${process.env.PORT}`,
	highlightDelay: 0,
	webDriverLogDir: 'logs',
	multiCapabilities: [
		{
			chromeOptions: {
				args: [
					"--headless",
					"--disable-gpu",
				]
			}
		},
	],
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
		printTestingProgress();
		verifyLogsAroundEachTest();
	},
};

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
			displayStacktrace: 'pretty',
		},
		summary: {
			displaySuccessful: false,
			displayFailed: true,
			displayPending: false,
			displayDuration: false,
			displayErrorMessages: true,
			displayStacktrace: 'none',
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
