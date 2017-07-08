var extend = require('node.extend');  // https://github.com/dreamerslab/node.extend
var bunyan = require('bunyan');       // https://github.com/trentm/node-bunyan

extend(exports, {
	app: bunyan.createLogger({
		name: 'app',
		stream: process.stderr,
		level: 'TRACE'
	}),
	auth: bunyan.createLogger({
		name: 'auth',
		stream: process.stderr,
		level: 'TRACE'
	}),
	audit: bunyan.createLogger({
		name: 'audit',
		stream: process.stdout,
		level: 'TRACE'
	}),
	task: bunyan.createLogger({
		name: 'task',
		stream: process.stderr,
		level: 'TRACE'
	}),
	test: bunyan.createLogger({
		name: 'task',
		stream: process.stdout,
		level: 'TRACE'
	})
});
