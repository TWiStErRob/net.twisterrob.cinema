'use strict';
const webpack = require('webpack'); // https://github.com/webpack/webpack
const HtmlWebpackPlugin = require('html-webpack-plugin'); // https://github.com/jantimon/html-webpack-plugin#options
const CopyWebpackPlugin = require('copy-webpack-plugin'); // https://github.com/webpack-contrib/copy-webpack-plugin
const MiniCssExtractPlugin = require("mini-css-extract-plugin"); // https://github.com/webpack-contrib/mini-css-extract-plugin
const path = require('path');

module.exports = (env, argv) => {
	const dist = path.resolve(__dirname, '..', 'deploy');
	console.log(`Running in mode: ${argv.mode}; deploying to ${dist}.`);
	const devMode = argv.mode === 'development';
	return {
		entry: {
			'planner/index': {
				import: './src/planner/scripts/index.js',
				dependOn: ['shared'],
			},
			// Explicitly declare module groups as entry points.
			// This will generate separate .bundle.js files for each group.
			// Helps with build not having to re-write an 8MB file, and also downloading in the browser.
			'shared': {
				import: ['lodash'],
				dependOn: ['shared-moment', 'shared-angular', 'shared-bootstrap'],
			},
			'shared-angular': ['angular', 'angular-resource', 'angular-animate', 'angular-ui-bootstrap'],
			'shared-bootstrap': ['bootstrap'],
			'shared-moment': ['moment', 'moment-timezone', 'moment-range'],
		},
		output: {
			path: path.join(dist, 'static'),
			// don't set publicPath to an absolute path because it emits a <script> with that prefix
			//publicPath: dist,
			filename: '[name].bundle.js'
		},
		devtool: devMode
				? 'inline-source-map'
				: undefined,
		plugins: [
			new webpack.ProvidePlugin({
				// Workaround from https://github.com/shakacode/bootstrap-loader/issues/85#issuecomment-217081025 for:
				// > transition.js:59 Uncaught ReferenceError: jQuery is not defined
				// >     at Object../node_modules/bootstrap/js/transition.js (transition.js:59)
				jQuery: 'jquery'
			}),
			new MiniCssExtractPlugin(),
			new CopyWebpackPlugin({
				patterns: [
					{ from: 'src/old', to: 'planner-old' },
				],
			}),
			new HtmlWebpackPlugin({
				template: 'src/main/pages/index.html',
				filename: 'index.html',
				chunks: [],
			}),
			new HtmlWebpackPlugin({
				template: 'src/planner/pages/index.html',
				filename: 'planner/index.html',
				chunks: ['shared', 'shared-moment', 'shared-angular', 'shared-bootstrap', 'planner/index'],
			}),
		],
		module: {
			// https://webpack.js.org/guides/migrating/#chaining-loaders
			rules: [
				{
					test: /\.(html)$/,
					// These templates/.html files are loaded into the $templateCache raw/as is.
					include: [path.join(__dirname, 'src/planner/templates/')],
					// When loaded with require() only asset/source gives contents.
					// asset/resource gives url and asset/inline gives encoded data URI.
					type: 'asset/source',
				},
				{
					test: /\.(css)$/,
					use: [
						{
							loader: MiniCssExtractPlugin.loader,
						},
						{
							loader: 'css-loader',
						},
					],
				},
				{
					test: /\.(sass|scss)/,
					use: [
						{
							loader: MiniCssExtractPlugin.loader,
						},
						{
							loader: 'css-loader',
						},
						{
							loader: 'sass-loader',
							options: {
								sassOptions: {
									outputStyle: 'expanded',
								},
							},
						},
					],
				},
				{
					test: /\.(png|jpg|gif)$/,
					type: 'asset/resource',
					generator: {
						filename: 'images/[name]-[hash][ext][query]',
					},
				},
				{
					test: /\.(svg|eot|ttf|woff|woff2)$/,
					type: 'asset/resource',
					generator: {
						filename: 'fonts/[name]-[hash][ext][query]',
					},
				},
			],
		},
	};
};
