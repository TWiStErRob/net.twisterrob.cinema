'use strict';
const HtmlWebpackPlugin = require('html-webpack-plugin'); // https://github.com/jantimon/html-webpack-plugin#options
const CopyWebpackPlugin = require('copy-webpack-plugin'); // https://github.com/webpack-contrib/copy-webpack-plugin
const MiniCssExtractPlugin = require("mini-css-extract-plugin"); // https://github.com/webpack-contrib/mini-css-extract-plugin
const WarningsToErrorsPlugin = require('warnings-to-errors-webpack-plugin'); // https://github.com/taehwanno/warnings-to-errors-webpack-plugin
const path = require('path');
const ONE_MIB = 1024 * 1024;
const TEN_MIB = 10 * ONE_MIB;

module.exports = (env, argv) => {
	const dist = path.resolve(__dirname, '..', 'deploy', 'frontend');
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
			new WarningsToErrorsPlugin(),
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
								warnRuleAsWarning: true,
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
		performance: {
			// Be strict.
			hints: 'error',
			// WARNING in entrypoint size limit: The following entrypoint(s) combined asset size exceeds the recommended limit (244 KiB). This can impact web performance.
			// Entrypoints:
			//   planner/index (903 KiB) [production]
			//   planner/index (8.61 MiB) [development]
			//       planner/index.css
			//       planner/index.bundle.js
			maxEntrypointSize: devMode ? TEN_MIB : ONE_MIB,
			// WARNING in asset size limit: The following asset(s) exceed the recommended size limit (244 KiB).
			// This can impact web performance.
			// Assets:
			//   planner/index.bundle.js (753 KiB) [production]
			//   planner/index.bundle.js (7.94 MiB) [development]
			maxAssetSize: devMode ? TEN_MIB : ONE_MIB,
		},
	};
};
