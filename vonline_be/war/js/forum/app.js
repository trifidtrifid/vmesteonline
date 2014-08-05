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
        .state('adverts', {
            url: "/adverts",
            templateUrl: "partials/adverts.html",
            controller: 'AdvertsController as adverts'
        })
        .state('adverts-single', {
            url: "/adverts-single-:advertId",
            templateUrl: "partials/adverts-single.html",
            controller: 'AdvertsSingleController as adverts'
        })
        .state('dialogs', {
            url: "/dialogs",
            templateUrl: "partials/dialogs.html",
            controller: 'dialogsController as dialogs'
        })
        .state('dialog-single', {
            url: "/dialog-single-:dialogId",
            templateUrl: "partials/dialog-single.html",
            controller: 'dialogController as dialog'
        })
        .state('neighbours', {
            url: "/neighbours",
            templateUrl: "partials/neighbours.html",
            controller: 'neighboursController as neighbours'
        })
        .state('profile', {
            url: "/profile-:userId",
            templateUrl: "partials/profile.html",
            controller: 'ProfileController as profile'
        })
        .state('profile.change-avatar', {
            url: "/change-avatar",
            templateUrl: "partials/profile.changeAvatar.html",
            controller: 'changeAvatarController as changeAvatar'
        })
        .state('settings', {
            url: "/settings",
            templateUrl: "partials/settings.html",
            controller: 'SettingsController as settings'
        })
        .state('maps', {
            url: "/maps",
            templateUrl: "partials/maps.html",
            controller: 'MapsController as maps'
        })
        /*.state('blog', {
            url: "/blog",
            templateUrl: "partials/blog.html",
            controller: 'BlogController as blog'
        })*/

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

