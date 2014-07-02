
// Fireup the plugins
$(document).ready(function(){
	
	// initialise  slideshow
	 $('.flexslider').flexslider({
        animation: "slide",
        start: function(slider){
          $('body').removeClass('loading');
        }
      });


    /*function openModalAuth(){
        var modalIn = $('.modal.in');
        if(modalIn.length) modalIn.modal('hide');
        var modalAuth = $('.modal-auth');
        modalAuth.load('login.jsp .login-container',function(){
            var closeHtml = '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>';
            modalAuth.find('.reg-form').prepend(closeHtml);

            // запускаем скрипты логина через ajax
            $.ajax({
                url: 'js/shop/login.js',
                dataType: 'script'
            });

            $(this).hover(function(){
                $('.login-close').removeClass('hide');
            },function(){
                $('.login-close').addClass('hide');
            });

        }).modal();
    }*/

});
/**
 * Handles toggling the navigation menu for small screens.
 */
( function() {
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
} )();
