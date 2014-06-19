'use strict';

/* jasmine specs for controllers go here */

describe('controllers', function(){
  beforeEach(module('forum.controllers'));


  it('should ....', inject(function($controller) {
    //spec body
    var baseController = $controller('baseController', { $scope: {} });
    expect(baseController).toBeDefined();
    expect(baseController.nextdoorsLoadStatus).toBeDefined();

  }));

  it('should ....', inject(function($controller) {
    //spec body
    var navbarController = $controller('navbarController', { $scope: {} });
    expect(navbarController).toBeDefined();
  }));
});
