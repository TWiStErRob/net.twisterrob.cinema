'use strict';
import 'angular'; // exports angular to global namespace
import ngResource from 'angular-resource';
import ngAnimate from 'angular-animate';

import 'angular-ui-bootstrap/ui-bootstrap-tpls';
import 'bootstrap/dist/css/bootstrap.css';
import './ui-bootstrap-tpls-0.6+-confirm';

import '../styles/planner.css';

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
angular.module('appServices', [ngResource]);
angular.module('appFilters', []);
angular.module('appDirectives', []);
angular.module('appAnimations', [ngAnimate]);

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

// https://github.com/angular/angular.js/issues/1551
app.config([function fixCineworldCallbacks() {
	var $window = window,
	    callbacks = $window.angular.callbacks,
	    pendingCallbacks = {},
	    counter = callbacks.counter;
	Object.defineProperty(callbacks, "counter", {
		get: function() {
			cleanFixedCallbacks();
			var originalName = '_' + counter.toString(36);
			var fixedName = ('angular.callbacks.' + originalName)
					.replace(/[^a-zA-Z0-9_]/g, '');
			function fixedCallback(data) {
				callbacks[originalName](data);
				delete $window[fixedName];
				delete pendingCallbacks[fixedName];
			}
			fixedCallback.originalName = originalName;
			$window[fixedName] = fixedCallback;
			pendingCallbacks[fixedName] = fixedCallback;
			return counter;
		},
		set: function(value) {
			counter = value;
		}
	});

	/**
	 * Clear uncalled callback fixes.
	 */
	function cleanFixedCallbacks() {
		angular.forEach(pendingCallbacks, function(fixedCallback, fqnStripped) {
			if(!(fixedCallback.originalName in callbacks)) {
				delete pendingCallbacks[fqnStripped];
			}
		});
	}
}]);
