'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('FilmsController', [
	        '$rootScope','$scope', '$uibModal', '$dialog', '_', 'moment', 'Film',
	function($rootScope,  $scope,   $uibModal,   $dialog,   _,   moment,   Film) {
		$scope.$watch('cineworld.cinemas | filter: { selected: true }', function (newValue, oldValue, scope) {
			$scope.cineworld.updateFilms();
		}, true);
		$scope.loading = true;
		$scope.$on('FilmsLoading', function(event) {
			$scope.loading = true;
		});
		$scope.$on('FilmsLoaded', function(event, films) {
			$scope.loading = false;
		});

		$scope.buttons = {
			all: { label: "All", selector: function(film) {
				film.selected = true;
			} },
			none: { label: "None", selector: function(film) {
				film.selected = false;
			} },
			invert: { label: "Invert", selector: function(film) {
				film.selected = !film.selected;
			}, hidden: true },
			new: { label: "New", selector: function(film) {
				film.selected = !$scope.hasView(film);
			}, isOpen: { films: true } },
			watched: { label: "Watched", selector: function(film) {
				film.selected = $scope.hasView(film);
			}, hidden: true },
			addView: { label: "Add View", handle: function() {
				$scope.addViewPopup(null, null);
			} }
		};
		$scope.buttonClick = function(button) {
			if(button.handle) {
				return button.handle();
			} else {
				_.forEach($scope.cineworld.films, button.selector);
				_.extend($scope.isOpen, button.isOpen);
			}
		};

		function addView(film, cinema, date) {
			if(film.processingView) return;
			film.processingView = true;
			Film.addView({
				edi: film.edi,
				cinema: cinema.cineworldID,
				date: moment(date).utc().valueOf()
			}).$promise.then(function(view) {
				var existing = _($scope.cineworld.films).find(function(film) {
					return film.view
						&& film.view.cinema.cineworldID === view.cinema.cineworldID
						&& film.view.film.edi === view.film.edi
						&& film.view.date === view.date
					;
				});
				if(existing) {
					existing.view = view;
				} else if (_($scope.cineworld.films).filter({ edi: view.film.edi }).filter($scope.not($scope.hasView)).size() === 1) {
					film.view = view;
				} else {
					existing = _.cloneDeep(view.film);
					existing.view = view;
					$scope.cineworld.films.push(existing);
				}
				film.processingView = false;
			}, function(httpResponse) {
				film.processingView = false;
				$rootScope.$broadcast('ResourceError', httpResponse);
			});
		}
		function ignore(film, reason) {
			if(film.processingView) return;
			film.processingView = true;
			Film.ignore({
				edi: film.edi,
				reason: reason
			}).$promise.then(function(ignore) {
				film.ignore = ignore;
				film.processingView = false;
			}, function(httpResponse) {
				film.processingView = false;
				$rootScope.$broadcast('ResourceError', httpResponse);
			});
		}
		$scope.addViewPopup = function(cinema, film) {
			var modalInstance = $uibModal.open({
				templateUrl: 'templates/viewPopup.html',
				controller: 'ViewPopupController',
				resolve: {
					cinemas: function () {
						return $scope.cineworld.cinemas;
					},
					films: function() {
						return $scope.cineworld.films;
					},
					defaultCinema: function() {
						return cinema || _($scope.cineworld.cinemas).sortBy('name').find('selected');
					},
					defaultFilm: function() {
						return film || _($scope.cineworld.films).sortBy('title').find('selected');
					},
					defaultDate: function() {
						return moment().startOf('day').add('hours', 18).toDate(); // today at 6 pm
					}
				}
			});

			modalInstance.result.then(
				function (modalResult) {
					if(modalResult.ignore !== undefined) {
						ignore(modalResult.film, modalResult.ignore);
					} else {
						addView(modalResult.film, modalResult.cinema, modalResult.date);
					}
				},
				function () {
					// ignore cancel
				}
			);
		};

		function removeView(view) {
			var films = _($scope.cineworld.films).filter({ edi: view.film.edi });
			if (_.some(films, 'processingView')) return;
			films.each(function(film) { film.processingView = true; });
			Film.removeView({
				edi: view.film.edi,
				cinema: view.cinema.cineworldID,
				date: view.date // not conversion, it's already UTC
			}).$promise.then(function() {
				if(films.size() === 1) {
					delete films.first().view;
				} else {
					var film = _($scope.cineworld.films).find(function(film) {
						return film.view
							&& film.view.cinema.cineworldID === view.cinema.cineworldID
							&& film.view.film.cineworldID === view.film.cineworldID
							&& film.view.date === view.date
						;
					});
					if(film) {
						_.pull($scope.cineworld.films, film);
					} else {
						console.error("Should have found it.", view);
					}
				}
				films.each(function(film) { film.processingView = false; });
			}, function(httpResponse) {
				films.each(function(film) { film.processingView = false; });
				$rootScope.$broadcast('ResourceError', httpResponse);
			});
		}
		$scope.removeViewPopup = function(view) {
			$dialog
				.prompt("Deleting a View", "Are you sure you want to delete this view of " + view.film.title + "?")
				.then(function(result) {
				if(result === 'yes') {
					removeView(view);
				}
			});
		};
		$scope.hasView = function(film) {
			return _.isObject(film.view);
		};
	}
]);

module.run(['$templateCache', function ($templateCache) {
	$templateCache.put('templates/viewPopup.html', require('../templates/viewPopup.html'));
}]);
