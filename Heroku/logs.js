var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');

extend(exports, {
	app: bunyan.createLogger({
		name: 'app',
		stream: process.stderr
	}),
	audit: bunyan.createLogger({
		name: 'audit',
		stream: process.stdout
	}),
	task: bunyan.createLogger({
		name: 'task',
		stream: process.stderr,
		level: 'TRACE'
	})
});
