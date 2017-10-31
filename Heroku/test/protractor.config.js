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
	params: {
		user: {
			name: 'papprs@gmail.com',
			password: 'papprspapprs',
		},
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
