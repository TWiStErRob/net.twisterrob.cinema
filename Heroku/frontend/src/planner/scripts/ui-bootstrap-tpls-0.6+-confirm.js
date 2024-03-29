'use strict';
/**
 * Usage: Add 'ui.bootstrap.modal.dialog' to app dependencies, and then '$dialog' to module dependencies.
 * Use as:
 *   $dialog.messageBox('Title', 'Message', [{result:'cancel', label: 'Cancel'}, {result:'yes', label: 'Yes', cssClass: 'btn-primary'}])
 *     .open()
 *     .then(function(result) {
 *
 *     });
 *
 * Or just:
 *
 *  $dialog.prompt('Title', 'Message').then(function(result) { } );
 */
var module = angular.module('ui.bootstrap.modal');

module.factory('$dialog', [
          '$rootScope', '$uibModal',
function ( $rootScope,   $uibModal) {
    var prompt = function(title, message, buttons) {
        if(typeof buttons === 'undefined') {
            buttons = [
                {result:'cancel', label: 'Cancel'},
                {result:'yes', label: 'Yes', cssClass: 'btn-primary'}
            ];
        }

        return $uibModal.open({
            templateUrl: 'template/dialog/message.html',
            controller: ['$scope', '$uibModalInstance',
            function(     $scope,   $uibModalInstance) {
                $scope.title = title;
                $scope.message = message;
                $scope.buttons = buttons;
    
                $scope.close = function(result) {
                    $uibModalInstance.close(result);
                };
            }]
        }).result;
    };

    return {
        prompt:     prompt,
        messageBox: function(title, message, buttons) {
            return {
                open: function() {
                    return prompt(title, message, buttons);
                }
            };
        }
    };
}]);

module.run(['$templateCache', function ($templateCache) {
	$templateCache.put("template/dialog/message.html", ''
		+ '<div class="modal-header">\n'
		+ '	<h1>{{ title }}</h1>\n'
		+ '</div>\n'
		+ '<div class="modal-body">\n'
		+ '	<p>{{ message }}</p>\n'
		+ '</div>\n'
		+ '<div class="modal-footer">\n'
		+ '	<button ng-repeat="btn in buttons" ng-click="close(btn.result)" class=btn ng-class="btn.cssClass">{{ btn.label }}</button>\n'
		+ '</div>');
}]);
