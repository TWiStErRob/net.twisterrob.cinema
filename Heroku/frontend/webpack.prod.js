'use strict';
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

const UglifyJsPlugin = require('uglifyjs-webpack-plugin'); // needed to use plugin 1.x which uses UglifyES

module.exports = merge(common, {
	plugins: [
		// equivalent of `--optimize-minimize`, but using specific version above
		new UglifyJsPlugin(),
	],
});
