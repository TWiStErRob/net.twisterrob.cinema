'use strict';
const merge = require('webpack-merge').merge; // https://github.com/survivejs/webpack-merge
const common = require('./webpack.common.js');

const TerserPlugin = require('terser-webpack-plugin'); // https://github.com/webpack-contrib/terser-webpack-plugin

module.exports = merge(common, {
	mode: "production",
	// equivalent of `--optimize-minimize`, but using specific version above
	optimization: {
		minimize: true,
		minimizer: [new TerserPlugin()],
	},
});
