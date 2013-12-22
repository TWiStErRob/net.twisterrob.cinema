'use strict';
var module = angular.module('appUtils'); // see app.js

module.service('AngularHacks', function _constructor() { 
	this.fixNextJSONP = function () { 
		var c = $window.angular.callbacks.counter;
		$window['angularcallbacks_' + c] = function (data) {
			$window.angular.callbacks['_' + c](data);
			delete $window['angularcallbacks_' + c];
		};
	};
});

var underscore = angular.module('underscore', []);
underscore.service('_', function() {
	return window._.noConflict(); // assumes underscore has already been loaded on the page
});
