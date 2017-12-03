var async = require('async');    // https://github.com/caolan/async
var fs = require('fs');          // http://nodejs.org/api/fs.html
var path = require('path');      // http://nodejs.org/api/path.html
var _ = require('lodash');       // https://lodash.com/docs

exports.remap = function(arr, map) {
	var result = {};
	_.each(arr, function(value, key) {
		key = map(key) || key;
		result[key] = value;
	});
	return result;
};
exports.readFiles = function(folder, stripExts, callback) {
	async.waterfall([
		function(callback) {
			fs.readdir(folder, callback);
		},
		function(files, callback) {
			async.map(files, function(item, callback) {
				var fullName = path.join(folder, item);
				fs.readFile(fullName, "utf8", function(err, contents) {
					var result = {};
					result[item] = contents;
					callback(err, result);
				});
			}, callback);
		},
		function(contents, callback) {
			callback(null, _.extend.apply(null, contents));
		},
		function(contents, callback) {
			if(stripExts) {
				contents = exports.remap(contents, function(key) {
					return path.basename(key, path.extname(key));
				});
			}
			callback(null, contents);
		}
	], callback);
};
