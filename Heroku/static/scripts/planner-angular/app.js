'use strict';

/* App Module */
var app = angular.module('app', [
	'utils',
	'appControllers',
	'appServices',
	'appFilters',
	'appDirectives',
	'appAnimations',
	'ui.bootstrap'
]);

app.config([function fixCineworldCallbacks() {
	var $window = window,
	    callbacks = $window.angular.callbacks,
	    c = callbacks.counter,
	    pendingCallbacks = {};
	Object.defineProperty(callbacks, "counter", {
		get: function() {
			cleanFixedCallbacks();
			var counter = this.realCounter;
			var originalName = '_' + counter;
			var fixedName = ('angular.callbacks.' + originalName).replace(/[^a-zA-Z0-9_]/g, '');
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
		set: function(counter) {
			this.realCounter = counter;
		}
	});
	callbacks.counter = c;

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
