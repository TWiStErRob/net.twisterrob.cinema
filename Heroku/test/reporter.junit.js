var nodeunit = require('nodeunit');
var assert = require('assert');
var fs = require('fs');
var path = require('path');
var _ = require('underscore');
var extend = require('node.extend');
var AssertionError = nodeunit.assert.AssertionError;

/**
 * Reporter info string
 */
exports.info = "JUnit-like Reporter";

function ansi(code) {
	return '\u001B[' + code + 'm';
}
function color(surrounding, text) {
	return surrounding.before + text + surrounding.after;
}
exports.makeColor = ansi;
exports.run = function(files, options, callback) {
	var options = extend(true, {
		success: {
			before: ansi('22;32'),
			after: ansi('22;1;37')
		},
		error: {
			before: ansi('22;31'),
			after: ansi('22;1;37')
		},
		failure: {
			before: ansi('22;33'),
			after: ansi('22;1;37')
		},
		test: {
			before: ansi('22;1;34'),
			after: ansi('22;1;37')
		},
		module: {
			before: ansi('22;37'),
			after: ansi('22;1;37')
		},
		file: {
			before: ansi('22;1;35'),
			after: ansi('22;1;37')
		},
		method: {
			before: ansi('22;1;36'),
			after: ansi('22;1;37')
		},
		assertion: {
			before: ansi('22;36'),
			after: ansi('22;1;37')
		},
		exception: {
			before: ansi('41'),
			after: ansi('40')
		}
	}, options);
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
			console.log('Running module ' + color(options.module, name));
			tracker.module = tracker.modules[name] = {
				name: name,
				started: new Date().getTime(),
				tests: {},
				testsDone: []
			};
		},
		testStart: function(name) {
			console.log('Running ' + color(options.test, name));
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
			console.log('Assertions run: ' + color(options.assertion, test.assertions.length)
					+ ', Failures: ' + color(options.failure, test.fails.length)
					+ ', Errors: ' + color(options.error, test.errors.length)
					+ ', Time elapsed: ' + color(options.success, ((test.finished - test.started) / 1000)) + ' sec'
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

			console.log('Tests run: ' + color(options.test, _.size(module.tests))
					+ ', Failures: ' + color(options.failure, module.fails.length)
					+ ', Errors: ' + color(options.error, module.errors.length)
					+ ', Assertions: ' + color(options.assertion, module.assertions.length)
					+ ', Time elapsed: ' + color(options.success, ((module.finished - module.started) / 1000)) + ' sec'
			);
			_.each(module.errors, function(t) {
				console.log(color(options.error, "ERROR " + t.name));
			});
			_.each(module.fails, function(t) {
				console.log(color(options.failure, "FAIL " + t.name));
			});
		},
		log: function(assertion) {
			if(assertion.failed()) {
				var e = assertion.error;
				var stack = e.stack.split('\n');
				var l = stack.length;
				for(var i = stack.length - 1, prev = true; 0 < i /* skip message */; --i) {
					if(prev && /    at .*node_modules[\/\\]nodeunit[\/\\].*/.test(stack[i])) {
						stack.splice(i, 1);
						prev &= true;
					} else {
						prev &= false;
					}
				}
				var dirname = __dirname.replace(/(.*[\/\\]).*/, "$1");
				stack = _.map(stack, function(stackElem) {
					stackElem = stackElem.replace(dirname, "");
					stackElem = stackElem.replace(/    at (.*\.|)(.*) \((.*):(\d+):(\d+)\)/,
							color(options.exception,
								"    at " + color(options.module, "$1") + color(options.method, "$2")
								+ " (" + color(options.file, "$3") + ":" + color(options.success, "$4") + ":$5)"));
					return stackElem;
				});
				if(e instanceof AssertionError) {
					e.stack = stack.join('\n');
					var newStack = nodeunit.utils.betterErrors(assertion).error.stack.split('\n');
					newStack.splice(newStack.length - stack.length + 1, stack.length - 1);
					console.log(color(options.failure, newStack.join('\n')));
				} else {
					console.log(color(options.error, stack[0]));
				}
				stack.splice(0, 1);
				console.log(stack.join('\n'));
				var hidden = l - stack.length - 1;
				if(hidden > 0) {
					console.log('    (' + hidden + ' more hidden)\n');
				}
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
			console.log('Modules run: ' + color(options.module, _.size(tracker.modules))
					+ ', Failures: ' + color(options.failure, tracker.fails.length)
					+ ', Errors: ' + color(options.error, tracker.errors.length)
					+ ', Assertions: ' + color(options.assertion, tracker.assertions.length)
					+ ', Time elapsed: ' + color(options.success, ((tracker.finished - tracker.started) / 1000)) + ' sec'
			);
			_.each(tracker.errors, function(m) {
				console.log(color(options.error, "ERROR " + m.name));
			});
			_.each(tracker.fails, function(m) {
				console.log(color(options.failure, "FAIL " + m.name));
			});

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
