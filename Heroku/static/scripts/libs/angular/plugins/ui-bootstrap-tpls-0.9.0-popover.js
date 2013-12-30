angular.module( 'ui.bootstrap.popover')
.directive( 'popoverTemplatePopup', [ '$http', '$templateCache', '$compile', '$sce', function ( $http, $templateCache, $compile, $sce ) {
	return {
		restrict: 'EA',
		replace: true,
		scope: { title: '@', content: '@', placement: '@', animation: '&', isOpen: '&', compileScope: '&' },
		templateUrl: 'template/popover/popover-template.html',
		link: function( scope, iElement, attr ) {
			scope.$watch($sce.parseAsResourceUrl(attr.content), function( templateUrl ) {
				if ( !templateUrl ) { return; }
				$http.get( templateUrl, { cache: $templateCache } )
				.then( function( response ) {
					var contentEl = angular.element( iElement[0].querySelector( '.popover-content' ) );
					//contentEl.children().remove();
					//contentEl.append( $compile( response.data.trim() )( scope ) );
					contentEl.html(response.data);
					$compile(contentEl.contents())(scope.$parent.$parent);
				});
			});
		}
	};
}])
.directive( 'popoverTemplate', [ '$tooltip', function ( $tooltip ) {
	return $tooltip( 'popoverTemplate', 'popover', 'click' );
}]);

angular.module("template/popover/popover-template.html", []).run(["$templateCache", function($templateCache) {
	$templateCache.put("template/popover/popover-template.html",
		"<div class=\"popover {{placement}}\" ng-class=\"{ in: isOpen(), fade: animation() }\">\n" +
		"		<div class=\"arrow\"></div>\n" +
		"		<div class=\"popover-inner\">\n" +
		"				<h3 class=\"popover-title\" ng-bind=\"title\" ng-show=\"title\"></h3>\n" +
		"				<div class=\"popover-content\"></div>\n" +
		"		</div>\n" +
		"</div>");
}]);

angular.module("ui.bootstrap.tpls").requires.push("template/popover/popover-template.html");
