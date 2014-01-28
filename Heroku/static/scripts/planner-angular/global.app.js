'use strict';

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
angular.module('appAnimations', []);

app.config(['$locationProvider', function($locationProvider) {
	$locationProvider.html5Mode(true);
	$locationProvider.hashPrefix('');
}]);

app.config([function fixCineworldCallbacks() {
	var $window = window,
	    callbacks = $window.angular.callbacks,
	    pendingCallbacks = {},
	    counter = callbacks.counter;
	Object.defineProperty(callbacks, "counter", {
		get: function() {
			cleanFixedCallbacks();
			var originalName = '_' + counter;
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
