var _ = require('underscore');        // http://underscorejs.org/

exports.changeSets = {
	setUp: function (callback) {
		this.db = [1,2,3,4];
		this.ajax = [3,4,5,6];
		callback();
	},
	tearDown: function (callback) {
		callback();
	},
	existing: function (test) {
		test.expect(1);
		var data = this;
		var existing = _.filter(data.ajax, function(num) {
			return _.contains(data.db, num);
		});
		test.deepEqual(existing, [3,4]);
		test.done();
	},
	existing2: function (test) {
		test.expect(1);
		var data = this;
		var existing = _.filter(data.db, function(num) {
			return _.contains(data.ajax, num);
		});
		test.deepEqual(existing, [3,4]);
		test.done();
	},
	incoming: function (test) {
		test.expect(1);
		var data = this;
		var incoming = _.reject(data.ajax, function(num) {
			return _.contains(data.db, num);
		});
		test.deepEqual(incoming, [5,6]);
		test.done();
	},
	deleted: function (test) {
		test.expect(1);
		var data = this;
		var deleted = _.reject(data.db, function(num) {
			return _.contains(data.ajax, num);
		});
		test.deepEqual(deleted, [1,2]);
		test.done();
	}
};
