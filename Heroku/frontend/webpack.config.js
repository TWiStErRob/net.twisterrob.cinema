'use strict';
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
			'planner/index': './src/planner/scripts/index.js',
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
				minify: false,
			}),
			new HtmlWebpackPlugin({
				template: 'src/planner/pages/index.html',
				filename: 'planner/index.html',
				chunks: ['planner/index'],
				minify: false,
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
