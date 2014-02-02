'use strict';
var module = angular.module('appDirectives'); // see app.js

// Taken from http://stackoverflow.com/a/20432961/253468
module.directive("twPoster", function() {
	return {
		priority: 99, // it needs to run after the attributes are interpolated
		link: function(scope, element, attr) {
			attr.$observe('twPoster', function(value) {
				if (!value)
					return;
				attr.$set('poster', value);

				// on IE, if "ng:src" directive declaration is used and "src" attribute doesn't exist
				// then calling element.setAttribute('src', 'foo') doesn't do anything, so we need
				// to set the property as well to achieve the desired effect.
				// we use attr[attrName] value since $set can sanitize the url.
				// if (msie) element.prop(attrName, attr[attrName]);
			});
		}
	};
});
