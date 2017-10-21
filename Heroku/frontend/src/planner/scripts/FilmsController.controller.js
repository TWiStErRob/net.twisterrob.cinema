'use strict';
var module = angular.module('appControllers'); // see app.js

module.controller('FilmsController', [
	        '$scope', '$modal', '$dialog', '_', 'moment', 'Film',
	function($scope,   $modal,   $dialog,   _,   moment,   Film) {
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
				film.selected = !film.view; // doesn't have a View
			} },
			watched: { label: "Watched", selector: function(film) {
				film.selected = !!film.view; // has view
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
			}
		};

		function addView(film, cinema, date) {
			if(film.processingView) return;
			film.processingView = true;
			Film.addView({
				edi: film.edi,
				cinema: cinema.cineworldID,
				date: moment(date).utc().valueOf()
			}, function(view) {
				var existing = _($scope.cineworld.films).find(function(film) {
					return film.view
						&& film.view.cinema.cineworldID === view.cinema.cineworldID
						&& film.view.film.cineworldID === view.film.cineworldID
						&& film.view.date === view.date
					;
				});
				if(existing) {
					existing.view = view;
				} else if (_($scope.cineworld.films).filter({ edi: view.film.edi, view: null }).size() === 1) {
					film.view = view;
				} else {
					existing = _.cloneDeep(view.film);
					existing.view = view;
					$scope.cineworld.films.push(existing);
				}
				film.processingView = false;
			});
		}
		function ignore(film, reason) {
			if(film.processingView) return;
			film.processingView = true;
			Film.ignore({
				edi: film.edi,
				reason: reason
			}, function(ignore) {
				film.ignore = ignore;
				film.processingView = false;
			})
		};
		$scope.addViewPopup = function(cinema, film) {
			var modalInstance = $modal.open({
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
			if(films.any('processingView')) return;
			films.each(function(film) { film.processingView = true; });
			Film.removeView({
				edi: view.film.edi,
				cinema: view.cinema.cineworldID,
				date: view.date // not conversion, it's already UTC
			}, function() {
				if(films.size() === 1) {
					films.first().view = null;
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
	}
]);
