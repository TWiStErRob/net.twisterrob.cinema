"use strict";
twister.core.namespaces.create("twister.utils");
twister.core.namespaces.create("twister.utils.time");
twister.core.namespaces.create("twister.utils.url");
var NS = twister.core.namespaces.extend;

twister.utils = NS(twister.utils, {
	/**
	 * Remove attribute notation from JSON response.
	 */
	clean: function(obj) {
		for (var prop in obj) {
			if (Object.prototype.hasOwnProperty.call(obj, prop)) {
				var val = obj[prop];
				if(typeof(val) === "object") {
					twister.utils.clean(val);
				}
				if(prop.length >= 1 && prop[0] == '@') {
					delete obj[prop];
					obj[prop.substring(1)] = val;
				}
			}
		}
		return obj;
	},
	unPackArrayPromise: function(/*...*/) {
		var array = $.map(arguments, function (arg) {
			return arg != undefined? arg : {};
		});
		return $.when.apply(this, array);
	},
	unPackAjaxPromises: function(/*...*/) {
		var firstOfAllArrays = $.map(arguments, function (ajaxPromiseResult) {
			var first = $.isArray(ajaxPromiseResult) ? ajaxPromiseResult[0] : undefined;
			return first || {};
		});
		return $.when.apply(this, firstOfAllArrays);
	},
	/**
	 $.Deferred(bindDeferredThis(deferredThis, realDeferredFunc));
	 or later: bindDeferredThis(deferredThis, dfd);
	 */
	bindDeferredThis: function bindDeferredThis(deferredThis, deferredOrFunc) {
		var bind = function(deferred) {
			deferred.resolve = function(/*...*/) {
				this.resolveWith(deferredThis, arguments);
			};
			deferred.reject = function(/*...*/) {
				this.rejectWith(deferredThis, arguments);
			};
			deferred.notify = function(/*...*/) {
				this.notifyWith(deferredThis, arguments);
			};
			if (typeof deferredOrFunc === "function") {
				deferredOrFunc.call( deferred, deferred );
			}
			return deferred;
		};
		if (deferredOrFunc != undefined && "promise" in deferredOrFunc) {
			// it's a deferred: (deferredThis, dfd)
			return bind(deferredOrFunc);
		} else {
			// it's a callback: (deferredThis, realDeferredFunc)
			return bind;
		}
	}
});

twister.utils = NS(twister.utils, (function Class_DelayedExecutor() {
	var defaultConfig = {
		timeout: 1000,
		callback: function() {},
		verbose: false,
		name: "DelayedExecutor",
		logMethod: $.proxy(console.debug, console)
	};
	var DelayedExecutor_ = { // private extension methods, use ClassName_.method.call(this, args)
		printProgress: function (event, extraArg) {
			if(this._config.verbose) {
				this._config.logMethod("[" + this._config.name + "/" + this._timer + "] " + event + ": " + extraArg);
			}
		},
		onTimeout: function () {
			DelayedExecutor_.printProgress.call(this, "onTimeout", this._config.callback !== undefined? this._config.callback.name : undefined);
			this._timer = undefined;
			if(typeof this._config.callback === "function") this._config.callback();
		}
	};

	var DelayedExecutor = function DelayedExecutor_constructor(config) {
		this._config = $.extend({}, defaultConfig, config);
		this._timer = undefined;
	};
	DelayedExecutor.prototype.start = function() {
		this.stop();
		//DelayedExecutor_.printProgress.call(this, "starting", "for " + this._config.timeout + "ms");
		if (!this.isInProgress()) {
			this._timer = setTimeout($.proxy(DelayedExecutor_.onTimeout, this), this._config.timeout);
			DelayedExecutor_.printProgress.call(this, "started", "for " + this._config.timeout + "ms");
		}
	};
	DelayedExecutor.prototype.stop = function() {
		DelayedExecutor_.printProgress.call(this, "stopping", (this.isInProgress()? "in progress" : "already stopped"));
		if (this.isInProgress()) {
			var timer = this._timer;
			clearTimeout(timer);
			this._timer = undefined;
			DelayedExecutor_.printProgress.call(this, "stopped", timer);
		}
	};
	DelayedExecutor.prototype.isInProgress = function() {
		return this._timer !== undefined;
	};
	DelayedExecutor.prototype.updateConfig = function(overrides) {
		$.extend(this._config, overrides);
	};
	return {DelayedExecutor: DelayedExecutor};
})());

twister.utils.url = NS(twister.utils.url, {
	getHashAnchor: function() {
		// #anchor!param1=value1&param2=&param3=value3
		var end = window.location.hash.indexOf('!');
		if (end === -1) end = window.location.hash.length;
		return window.location.hash.substring(1, end);
	},

	getHashParam: function(name) {
		// #anchor!param1=value1&param2=&param3=value3
		var start = window.location.hash.indexOf('!') + 1;
		var match = new RegExp('(?:^|&)' + name + '=([^&]*)')
					.exec(window.location.hash.substring(start));
		return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	},

	getQueryParam: function(name) {
		// ?param1=value1&param2=&param3=value3
		return twister.utils.url.getUrlParams(null, false, false)[name];
	},

	getQueryParams: function(name) {
		// ?param1=value1&param2=&param3=value3
		return twister.utils.url.getUrlParams(null, true, true)[name];
	},


	// http://stackoverflow.com/a/520845/253468 + http://stackoverflow.com/a/11030951/253468
	getUrlParams: function (url, supportArrays, alwaysArray) {
		var re = /(?:\?|&(?:amp;)?)([^=&#]+)(?:=?([^&#]*))/g,
			match, params = {},
		decode = function (s) {return decodeURIComponent(s.replace(/\+/g, " "));};

		url = url || document.location.href; // default value is current url
		alwaysArray = alwaysArray || false; // default is false
		// TODO figure it out
		supportArrays = supportArrays === false? false : supportArrays || alwaysArray || true; // default is true

		while (match = re.exec(url)) {
			var pName = decode(match[1]);
			var pValue = decode(match[2]);
			if (supportArrays && pName in params) { // if the value already exists
				// if no support for arrays, don't care if it exists
				if (params[pName] instanceof Array) { // already array
					params[pName].push(pValue);
				} else { // single elem, make it array
					params[pName] = [ params[pName], pValue ];
				}
			} else if (alwaysArray) {
				params[pName] = [ pValue ];
			} else {
				params[pName] = pValue;
			}
		}
		return params;
	}
});

twister.utils.time = NS(twister.utils.time, {
		parseDate: function (dateString, timeZone) {
			return moment(google.gdata.DateTime.fromIso8601(dateString).date)
				.utc()
				.add('hours', twister.utils.time.parseOffset(dateString));
			//var date = new timezoneJS.Date(dateString);
			//date.setTimezone(timeZone);
			//return date;
		},
		parseOffset: function (dateString) {
			var offset = dateString.substring(23);
			var hours = offset.substring(1, 3);
			var minutes = offset.substring(4, 6);
			result = +hours + minutes / 60;
			if (offset[0] == '-') { result *= -1; }
			return result;
		}
});
