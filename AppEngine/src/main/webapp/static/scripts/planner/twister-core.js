"use strict";
// twister.core.namespaces.create("twister.core.namespaces");

(function twister_core_namespaces() {
	var createNamespace = function(namespaceName) {
		var namespaceParts = (""+namespaceName).split(".");
		var namespace = window[namespaceParts[0]] = window[namespaceParts[0]] || {};
		for(var i = 1; i < namespaceParts.length; ++i) {
			namespace = namespace[namespaceParts[i]] = namespace[namespaceParts[i]] || {};
		}
		return namespace;
	};
	
	var extendNamespace = function(/*extensionObjects...*/) {
		var newArgs = Array.prototype.slice.call(arguments, 0);
		newArgs.unshift(true);
		return $.extend.apply($, newArgs);
	};
	
	extendNamespace(createNamespace("twister.core.namespaces"), {
		create: createNamespace,
		extend: extendNamespace
	});
})();
