'use strict';
const HtmlWebpackPlugin = require('html-webpack-plugin'); // https://github.com/jantimon/html-webpack-plugin#options
const CopyWebpackPlugin = require('copy-webpack-plugin'); // https://github.com/webpack-contrib/copy-webpack-plugin
const MiniCssExtractPlugin = require("mini-css-extract-plugin"); // https://github.com/webpack-contrib/mini-css-extract-plugin
const Path = require('path');

const dist = Path.resolve(__dirname, '..', 'deploy');
module.exports = {
	entry: {
		'planner/index': './src/planner/scripts/index.js'
	},
	output: {
		path: Path.join(dist, 'static'),
		// don't set publicPath to an absolute path because it emits a <script> with that prefix
		//publicPath: dist,
		filename: '[name].js'
	},
	resolve: {
		extensions: ['.js', '.json'/*, '.ts'*/]
	},
	module: {
		// https://webpack.js.org/guides/migrating/#chaining-loaders
		rules: [
//			{
//				test: /\.ts$/,
//				use: ['ts-loader'],
//				query: {
//					ignoreDiagnostics: [
//						2403, // 2403 -> Subsequent variable declarations
//						2300, // 2300 -> Duplicate identifier
//						2374, // 2374 -> Duplicate number index signature
//						2375, // 2375 -> Duplicate string index signature
//					]
//				}
//			},
			{ test: /\.json$/, use: ['raw-loader'] },
			{ test: /\.html$/, use: ['raw-loader'] },
			{
				test: /\.css$/,
				use: [
					{
						// https://webpack.js.org/plugins/mini-css-extract-plugin/
						loader: MiniCssExtractPlugin.loader,
					},
					{
						loader: 'css-loader',
					},
				]
			},
			{
				test: /\.(sass|scss)/,
				use: [
					{
						// https://webpack.js.org/plugins/mini-css-extract-plugin/
						loader: MiniCssExtractPlugin.loader,
					},
					{
						loader: 'css-loader',
						options: {
							sourceMap: true,
						},
					},
					{
						loader: 'sass-loader',
						options: {
							sourceMap: true,
							sassOptions: {
								outputStyle: 'expanded',
							},
						},
					},
				],
			},
			// TODO https://webpack.js.org/guides/asset-modules/
			{ test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
				loader: 'url-loader',
				options: {
					outputPath: 'planner/',
					name: '[contenthash]_[name].[ext]',
					limit: 1000,
					useRelativePath: true
				}
			},
		]
	},
	plugins: [
		new MiniCssExtractPlugin(),
		new CopyWebpackPlugin({
			patterns: [
				{ from: 'src/old', to: 'planner-old' }
			]
		}),
		new HtmlWebpackPlugin({
			chunks: [],
			filename: 'index.html',
			template: 'src/main/pages/index.html'
		}),
		new HtmlWebpackPlugin({
			filename: 'planner/index.html',
			chunks: ['planner/index'],
			inject: 'head', // temporary workaround while all dependencies are migrated
			template: 'src/planner/pages/index.html'
		})
	]
};
