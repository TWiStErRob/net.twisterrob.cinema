var _ = require('lodash');        // https://lodash.com/docs

exports.changeSetsNum = {
	setUp: function (callback) {
		this.db = [1,2,3,4];
		this.ajax = [3,4,5,6];
		callback();
	},
	tearDown: function (callback) {
		callback();
	},
	incoming: function (test) {
		test.data = this;
		var incoming = _.reject(test.data.ajax, function(num) {
			return _.includes(test.data.db, num);
		});
		test.deepEqual(incoming, [5,6]);
		test.done();
	},
	existing: function (test) {
		test.data = this;
		var existing = _.filter(test.data.ajax, function(num) {
			return _.includes(test.data.db, num);
		});
		test.deepEqual(existing, [3,4]);
		test.done();
	},
	existing2: function (test) {
		test.data = this;
		var existing = _.filter(test.data.db, function(num) {
			return _.includes(test.data.ajax, num);
		});
		test.deepEqual(existing, [3,4]);
		test.done();
	},
	deleted: function (test) {
		test.data = this;
		var deleted = _.reject(test.data.db, function(num) {
			return _.includes(test.data.ajax, num);
		});
		test.deepEqual(deleted, [1,2]);
		test.done();
	}
};

exports.changeSetsObj = {
	setUp: function (callback) {
		this.db = [
			{ idDB: 1, name: "Name 1"},
			{ idDB: 2, name: "Name 2"},
			{ idDB: 3, name: "Name 3"},
			{ idDB: 4, name: "Name 4"}
		];
		this.dbByID = _.keyBy(this.db, "idDB");
		this.dbIDs = _.keys(this.dbByID);

		this.ajax = [
			{ idAjax: 3, name: "Name 3"},
			{ idAjax: 4, name: "Name 4"},
			{ idAjax: 5, name: "Name 5"},
			{ idAjax: 6, name: "Name 6"}
		];
		this.ajaxByID = _.keyBy(this.ajax, "idAjax");
		this.ajaxIDs = _.keys(this.ajaxByID);

		callback();
	},
	tearDown: function (callback) {
		callback();
	},
	checkSetup: function(test) {
		test.data = this;
//		console.log("DB:", test.data.dbByID);
//		console.log("Ajax:", test.data.ajaxByID);
		test.deepEqual(test.data.dbIDs, [1,2,3,4]);
		test.deepEqual(test.data.ajaxIDs, [3,4,5,6]);
		test.done();
	},
	incoming: function (test) {
		test.data = this;
		var incoming = _.omit(_.clone(test.data.ajaxByID), test.data.dbIDs);
		test.deepEqual(_.keys(test.data.ajaxByID), [3,4,5,6]); // no change
		test.deepEqual(_.keys(incoming), [5,6]); // filtered
		test.deepEqual(_.values(incoming), [
			{ idAjax: 5, name: "Name 5"},
			{ idAjax: 6, name: "Name 6"}
		]);
		test.done();
	},
	existingDB: function (test) {
		test.data = this;
		var existing = _.pick(_.clone(test.data.dbByID), test.data.ajaxIDs);
		test.deepEqual(_.keys(test.data.dbByID), [1,2,3,4]); // no change
		test.deepEqual(_.keys(existing), [3,4]); // filtered
		test.deepEqual(_.values(existing), [
			{ idDB: 3, name: "Name 3"},
			{ idDB: 4, name: "Name 4"}
		]);
		test.done();
	},
	existingAjax: function (test) {
		test.data = this;
		var existing = _.pick(_.clone(test.data.ajaxByID), test.data.dbIDs);
		test.deepEqual(_.keys(test.data.ajaxByID), [3,4,5,6]); // no change
		test.deepEqual(_.keys(existing), [3,4]); // filtered
		test.deepEqual(_.values(existing), [
			{ idAjax: 3, name: "Name 3"},
			{ idAjax: 4, name: "Name 4"}
		]);
		test.done();
	},
	deleted: function (test) {
		test.data = this;
		var deleted = _.omit(_.clone(test.data.dbByID), test.data.ajaxIDs);
		test.deepEqual(_.keys(test.data.dbByID), [1,2,3,4]); // no change
		test.deepEqual(_.keys(deleted), [1,2]); // filtered
		test.deepEqual(_.values(deleted), [
			{ idDB: 1, name: "Name 1"},
			{ idDB: 2, name: "Name 2"}
		]);
		test.done();
	}
};
