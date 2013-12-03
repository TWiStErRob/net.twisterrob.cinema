var nodeunit = require('nodeunit');
var assert = require('assert');
var fs = require('fs');
var path = require('path');
var _ = require('underscore');
var AssertionError = nodeunit.assert.AssertionError;

/**
 * Reporter info string
 */
exports.info = "JUnit-like Reporter";

exports.run = function(files, options, callback) {
	var options = options || {};
	var tracker = {
		started: new Date().getTime(),
		modules: {},
		modulesDone: []
	};
	process.on('exit', function() {
		var unfinished = _.size(tracker.modules) - _.size(tracker.modulesDone);
		if (unfinished > 0) {
			console.log('INCONCLUSIVE: Undone tests (or their setUp/tearDown-s):');
			// TODO tests
			_.each(tracker.modules, function(m) {
				if(!_.contains(modulesDone, m)) {
					console.log(m.name);
				}
			});
			console.log('To fix this, make sure all tests call test.done()');
			process.reallyExit(unfinished);
		}
	});

	var opts = {
		testspec: options.testspec,
		testFullSpec: options.testFullSpec,
		moduleStart: function(name) {
			console.log('Running module ' + name);
			tracker.module = tracker.modules[name] = {
				name: name,
				started: new Date().getTime(),
				tests: {},
				testsDone: []
			};
		},
		testStart: function(name) {
			console.log('Running ' + name );
			tracker.module.tests[name] = {
				name: name,
				started: new Date().getTime()
			};
		},
		testDone: function(name, assertions) {
			var test = tracker.module.tests[name];
			test.finished = new Date().getTime();
			test.assertions = assertions;
			tracker.module.testsDone.push(test);
			test.errors = [];
			test.fails = [];
			_.each(test.assertions, function(a) {
				if (a.failed()) {
					if (a.error instanceof AssertionError) {
						test.fails.push(a);
					} else {
						test.errors.push(a);
					}
				}
			});
			console.log('Assertions run: ' + test.assertions.length
					+ ', Failures: ' + test.fails.length
					+ ', Errors: ' + test.errors.length
					+ ', Time elapsed: ' + ((test.finished - test.started) / 1000) + ' sec'
			);
			console.log('');
		},
		moduleDone: function(name, assertions) {
			var module = tracker.modules[name];
			assert.strictEqual(module, tracker.module);
			module.finished = new Date().getTime();
			module.assertions = assertions;
			delete tracker.module;
			tracker.modulesDone.push(module);
			module.errors = [];
			module.fails = [];
			_.each(module.tests, function(t) {
				if(t.errors.length) {
					module.errors.push(t);
				}
				if(t.fails.length) {
					module.fails.push(t);
				}
			});

			console.log('Tests run: ' + _.size(module.tests)
					+ ', Failures: ' + module.fails.length
					+ ', Errors: ' + module.errors.length
					+ ', Assertions: ' + module.assertions.length
					+ ', Time elapsed: ' + ((module.finished - module.started) / 1000) + ' sec'
			);
			_.each(module.errors, function(t) {
				console.log("ERROR " + t.name);
			});
			_.each(module.fails, function(t) {
				console.log("FAIL " + t.name);
			});
			console.log();
			console.log();
		},
		log: function(assertion) {
			if(assertion.failed()) {
				var e = assertion.error;
				var stack = e.stack.split('\n');
				console.log(stack.shift());
				var l = stack.length;
				for(var i = stack.length - 1, prev = true; 0 <= i; --i) {
					if(prev && /    at .*node_modules[\/\\]nodeunit[\/\\].*/.test(stack[i])) {
						stack.splice(i, 1);
						prev &= true;
					} else {
						prev &= false;
					}
				}
				if(e instanceof AssertionError) {
					console.log('    at assert.' + e.operator + ' (expected=[' + e.expected + '], actual=[' + e.actual + '])');
				}
				stack = _.map(stack, function(stackElem) {
					return stackElem.replace(/D:\\Programming\\workspace\\Cinema\\Heroku\\/, "");
				});
				console.log(stack.join('\n'));
				console.log('    (' + (l - stack.length) + ' more hidden)\n');
			}
		},
		done: function(assertions, end) {
			tracker.finished = end || new Date().getTime();
			tracker.assertions = assertions;
			tracker.fails = [];
			tracker.errors = [];
			_.each(tracker.modules, function(m) {
				if(m.errors.length) {
					tracker.errors.push(m);
				}
				if(m.fails.length) {
					tracker.fails.push(m);
				}
			});
			console.log('Modules run: ' + _.size(tracker.modules)
					+ ', Failures: ' + tracker.fails.length
					+ ', Errors: ' + tracker.errors.length
					+ ', Assertions: ' + tracker.assertions.length
					+ ', Time elapsed: ' + ((tracker.finished - tracker.started) / 1000) + ' sec'
			);
			_.each(tracker.errors, function(m) {
				console.log("ERROR " + m.name);
			});
			_.each(tracker.fails, function(m) {
				console.log("FAIL " + m.name);
			});
			console.log();
			console.log();
			console.log();

			if (callback) {
				callback(assertions.failures() ? new Error('We have got test failures.') : undefined);
			}
		}
	};
	if (files && files.length) {
		var paths = files.map(function(p) {
			return path.join(process.cwd(), p);
		});
		nodeunit.runFiles(paths, opts);
	} else {
		nodeunit.runModules(files, opts);
	}
};
