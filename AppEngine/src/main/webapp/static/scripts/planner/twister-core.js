"use strict";
// twister.core.namespaces.create("twister.core.namespaces");

(function twister_core_namespaces() {
	var Namespace = function (name) {
		this.name = name;
	};
	var createNamespaceInternal = function(namespaceName) {
		namespaceName = namespaceName.replace(/[.]/g, "_");
		var NS = eval('(function NS_{0}() {})'.format(namespaceName)); // give it a name
		NS.prototype =  new Namespace(namespaceName);
		return new NS();
	};
	var createNamespace = function(namespaceName) {
		var namespaceParts = (""+namespaceName).split(".");
		var namespace = window;
		for(var i = 0; i < namespaceParts.length; ++i) {
			var fullNamespaceName = namespaceParts.slice(0, i + 1).join(".");
			namespace = namespace[namespaceParts[i]] =
					namespace[namespaceParts[i]] || createNamespaceInternal(fullNamespaceName);
		}
		return namespace;
	};
	
	var checkNamespaceCollisions = function checkNamespaceCollisions_recurse(ignore, extension) {
		var collides = false;
		for(var name in extension) {
			if(Object.prototype.hasOwnProperty.call(extension, name) && name in this) {
				var displayName = this instanceof Namespace? this.name : {}.toString();
				if($.isPlainObject(this[name]) || this[name] instanceof Namespace) {
					// update collides value, but check the whole structure to get all warnings
					collides = collides | checkNamespaceCollisions_recurse.call(this[name], null, extension[name]);
				} else {
					collides = true;
					console.warn("Overriding {0} in {1}!".format(name, displayName), "\n", this, "\n", extension);
				}
			}
		}
		return collides;
	};
	
	var extendNamespace = function(/*extensionObjects...*/) {
		var newArgs = Array.prototype.slice.call(arguments, 0);
		$.each(newArgs.slice(1), $.proxy(checkNamespaceCollisions, arguments[0]));
		newArgs.unshift(true);
		return $.extend.apply($, newArgs);
	};
	
	extendNamespace(createNamespace("twister.core.namespaces"), {
		create: createNamespace,
		extend: extendNamespace
	});
})();
