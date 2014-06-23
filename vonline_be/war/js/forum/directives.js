'use strict';

/* Directives */


angular.module('forum.directives', []).
  directive('ngFocus', ['version', function(version) {
    return function(scope, elm, attrs) {
      elm.text(version);

       elm.focus();
    };
  }]);
