var utils = require('../utils');
var fs = require('fs');          // http://nodejs.org/api/fs.html
var path = require('path');      // http://nodejs.org/api/path.html

exports.testReaddir = {
	setUp: function (callback) {
		var test = this;

		test.dir = path.resolve(__dirname, 'testUtils-readdir');
		test.file1 = path.join(test.dir, 'file1.ext');
		test.file1Contents = "test.file1 contents";
		test.file2 = path.join(test.dir, 'file2.ext');
		test.file2Contents = "test.file2 contents";
		test.file3 = path.join(test.dir, 'file3.ext');
		test.file3Contents = "test.file3 contents";

		fs.mkdirSync(test.dir);
		fs.writeFileSync(test.file1, test.file1Contents);
		fs.writeFileSync(test.file2, test.file2Contents);
		fs.writeFileSync(test.file3, test.file3Contents);

		callback();
	},
	tearDown : function(callback) {
		var test = this;

		fs.unlinkSync(test.file1);
		fs.unlinkSync(test.file2);
		fs.unlinkSync(test.file3);
		fs.rmdirSync(test.dir);

		callback();
	},
	validateSetup: function(test) {
		test.data = this;
		var file1Contents = fs.readFileSync(test.data.file1, "utf8");
		test.equal(file1Contents, test.data.file1Contents);
		var file2Contents = fs.readFileSync(test.data.file2, "utf8");
		test.equal(file2Contents, test.data.file2Contents);
		var file3Contents = fs.readFileSync(test.data.file3, "utf8");
		test.equal(file3Contents, test.data.file3Contents);
		test.done();
	},
	testNoExt: function(test) {
		test.data = this;
		utils.readFiles(test.data.dir, false, function(err, results) {
			test.ifError(err);
			var expected = {};
			expected[path.basename(test.data.file1)] = test.data.file1Contents;
			expected[path.basename(test.data.file2)] = test.data.file2Contents;
			expected[path.basename(test.data.file3)] = test.data.file3Contents;
			test.deepEqual(results, expected);
			test.done();
		});
	},
	testStripExt: function(test) {
		test.data = this;
		utils.readFiles(test.data.dir, true, function(err, results) {
			test.ifError(err);
			var expected = {
				file1: test.data.file1Contents,
				file2: test.data.file2Contents,
				file3: test.data.file3Contents
			};
			test.deepEqual(results, expected);
			test.done();
		});
	}
};
