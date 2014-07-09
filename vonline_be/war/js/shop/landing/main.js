/*require.config({
    baseUrl: "/build",
    paths: {

    }
});

require(["jquery",'shop-modules.min','commonM.min','loginModule.min'],
    function($,modules,commonM,loginModule) {

        $('.landing-login').click(function(){
            modules.shopCommonModule.openModalAuth();
        });

    });*/


// Fireup the plugins
$(document).ready(function(){
	
	// initialise  slideshow
	 $('.flexslider').flexslider({
        animation: "slide",
        start: function(slider){
          $('body').removeClass('loading');
        }
      });

    /*var transport = new Thrift.Transport("/thrift/ShopFEService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopFEServiceClient(protocol);*/


    /*$('.vote-btn').click(function(e){
        e.preventDefault();

        if(!globalUserAuth){
            $('.landing-login').trigger('click');
        }else{

            var currentItem = $(this).closest('li'),
                currentVoiceCounter = currentItem.find('.voice-counter'),
                shopId = currentItem.attr('id'),
                currentVoicesNum = currentVoiceCounter.text();

            currentVoiceCounter.text(++currentVoicesNum);
            client.vote(shopId,'1');

        }
    });*/


});
/**
 * Handles toggling the navigation menu for small screens.
 */
/*( function() {
	var button = document.getElementById( 'topnav' ).getElementsByTagName( 'div' )[0],
	    menu   = document.getElementById( 'topnav' ).getElementsByTagName( 'ul' )[0];

	if ( undefined === button )
		return false;

	// Hide button if menu is missing or empty.
	if ( undefined === menu || ! menu.childNodes.length ) {
		button.style.display = 'none';
		return false;
	}

	button.onclick = function() {
		if ( -1 == menu.className.indexOf( 'srt-menu' ) )
			menu.className = 'srt-menu';

		if ( -1 != button.className.indexOf( 'toggled-on' ) ) {
			button.className = button.className.replace( ' toggled-on', '' );
			menu.className = menu.className.replace( ' toggled-on', '' );
		} else {
			button.className += ' toggled-on';
			menu.className += ' toggled-on';
		}
	};
} )();*/
