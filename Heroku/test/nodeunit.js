exports.testSomething = function(test) {
	test.expect(1);
	test.ok(true, "this assertion should pass");
	test.done();
};

exports.testSomethingElse = function(test) {
	test.ok(false, "this assertion should fail");
	test.done();
};

exports.test1 = function (test) {
	test.done();
};

exports.group = {
	test2: function (test) {
		test.done();
	},
	test3: function (test) {
		test.done();
	}
};

exports.surround = {
	setUp: function (callback) {
		this.foo = 'bar';
		callback();
	},
	tearDown: function (callback) {
		// clean up
		callback();
	},
	test1: function (test) {
		test.equals(this.foo, 'bar');
		test.done();
	}
};
