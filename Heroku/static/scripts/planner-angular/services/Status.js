'use strict';
var module = angular.module('appServices'); // see app.js

module.factory('Status', [
	        '$timeout',
	function($timeout) {
		var stati = [];
		var timeout = 2000;
		return {
			showStatus: function addBottomMessage(message) {
				stati.push(message); // ad new message to the queue
				$timeout(function removeTopMessage() {
					stati.shift();
				}, timeout);
			},
			get timeout() { return timeout; },
			set timeout(value) { timeout = value; },
			get stati() { return stati; }
		};
	}
]);
