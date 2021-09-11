'use strict';
const merge = require('webpack-merge').merge; // https://github.com/survivejs/webpack-merge
const common = require('./webpack.common.js');

module.exports = merge(common, {
	mode: "development",
	devtool: 'inline-source-map',
});
