/*require.config({
    baseUrl: "/js",
    paths: {
        "jquery"   : "lib/jquery-2.0.3.min"
    }
});*/
require(["jquery",'loginModule'],
    function($,loginModule) {
        loginModule.initLogin();
    });