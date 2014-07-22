'use strict';

// Declare app level module which depends on filters, and services
var main = angular.module('forum', [
  //'ngRoute',
  'ui.router',
 /* 'forum.filters',
  'forum.services',*/
  'forum.directives',
  'forum.controllers'
]);

main.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/main");

    $stateProvider
        .state('main', {
            url: "/main",
            templateUrl: "partials/main.html",
            controller: 'LentaController as lenta'
        })
        .state('talks', {
            url: "/talks",
            templateUrl: "partials/talks.html",
            controller: 'TalksController as talks'
        })
        .state('talks-single', {
            url: "/talks-single-:talkId",
            templateUrl: "partials/talks-single.html",
            controller: 'TalksSingleController as talks'
        })
        .state('services', {
            url: "/services",
            templateUrl: "partials/services.html",
            controller: 'ServicesController as services'
        })
        .state('private-messages', {
            url: "/private-messages",
            templateUrl: "partials/private-messages.html",
            controller: 'privateMessagesController as privateMessages'
        })
        .state('dialog', {
            url: "/dialog",
            templateUrl: "partials/dialogs.html",
            controller: 'dialogController as dialog'
        })
        .state('nextdoors', {
            url: "/nextdoors",
            templateUrl: "partials/nextdoors.html",
            controller: 'nextdoorsController as nextdoors'
        })
        .state('profile', {
            url: "/profile-:userId",
            templateUrl: "partials/profile.html",
            controller: 'ProfileController as profile'
        })
        .state('settings', {
            url: "/settings",
            templateUrl: "partials/settings.html",
            controller: 'SettingsController as settings'
        })

});

main.config(function($locationProvider){
    $locationProvider.html5Mode(true);
});

   /* .config(['$routeProvider', function($routeProvider,$locationProvider) {
    $routeProvider.when('/main', {templateUrl: 'ajax/forum/profile.html', controller: 'baseController'});
    $routeProvider.when('/settings', {templateUrl: 'ajax/forum/settings.html', controller: 'SettingsController'});
    $routeProvider.otherwise({redirectTo: '/main'});


}]).config(function($locationProvider){
    $locationProvider.html5Mode(true);
});*/

