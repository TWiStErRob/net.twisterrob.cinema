'use strict';
import 'angular'; // exports angular to global namespace
import ngResource from 'angular-resource';
import ngAnimate from 'angular-animate';

import 'angular-ui-bootstrap';
import 'bootstrap/dist/css/bootstrap.css';
import './ui-bootstrap-tpls-0.6+-confirm';

import '../styles/planner.scss';

/* App Module */
var app = angular.module('app', [
	'appUtils',
	'appControllers',
	'appServices',
	'appFilters',
	'appDirectives',
	'appAnimations',
	'ui.bootstrap',
	'lodash',
	'moment'
]);
angular.module('appUtils', []);
angular.module('appControllers', []);
angular.module('appServices', ['ngResource']);
angular.module('appFilters', []);
angular.module('appDirectives', []);
angular.module('appAnimations', ['ngAnimate']);

app.config(['$locationProvider', function($locationProvider) {
	$locationProvider.html5Mode(true);
	$locationProvider.hashPrefix('');
}]);

app.config(['$sceDelegateProvider', function($sceDelegateProvider) {
	$sceDelegateProvider.resourceUrlWhitelist([
		'self',
		'http://webcache1.bbccustomerpublishing.com/cineworld/trailers/**'
	]);
}]);

app.run(['$templateCache', function ($templateCache) {
	$templateCache.put('../templates/plan-vis.html', require('../templates/plan-vis.html'));
}]);
