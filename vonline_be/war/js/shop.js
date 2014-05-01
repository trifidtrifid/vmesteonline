require.config({
    baseUrl: "/js",
    paths: {
        "jquery"   : "lib/jquery-2.0.3.min",
        "ace_spinner": "lib/fuelux/fuelux.spinner",
        "bootstrap": "lib/bootstrap",
        "ace_extra": "lib/ace-extra.min",
        "ace_elements": "lib/ace-elements.min",
        "flexslider": "lib/jquery.flexslider-min",
        "jquery_ui": "lib/jquery-ui-1.10.3.full.min"
        //"uriAnchor": "lib/jquery.uriAnchor"
    },
    shim:{
      'ace_spinner':{
         deps: ['jquery',"ace_extra","ace_elements","jquery_ui"],
         exports: 'ace_spinner'
      },
      'jquery_ui':{
          deps: ['jquery'],
          exports: 'jquery_ui'
      },
    'bootstrap':{
        deps: ['jquery'],
        exports: 'bootstrap'
    },/*
    'uriAnchor':{
        deps: ['jquery'],
        exports: 'uriAnchor'
    },*/
    'flexslider':{
        deps: ['jquery'],
        exports: 'flexslider'
    }
    }
});

require(["jquery",'shop-modules','commonM','loginModule'],
    function($,modules,commonM,loginModule) {

        modules.shopCommonModule.InitProductDetailPopup($('.product-link'));
        modules.spinnerModule.initProductsSpinner();

        var urlHash = document.location.hash;

        var state = {
            type : 'default'
        };
        window.history.pushState(state,null,'shop.jsp');

        if (urlHash){
            if (urlHash.indexOf('p') != -1){
                // значит url с modal
                var hashParts = urlHash.split('=');
                modules.shopCommonModule.identificateModal(hashParts[1]);
            }
        }

        window.addEventListener('popstate',makeHistory,false);

        function makeHistory(e){
            // действия для корректной навигации по истории
            var isHistoryNav = true;
            //alert('makeHistory '+e.state.locationModal);
            if(e.state){
                if(e.state.type == 'modal'){

                    modules.shopCommonModule.identificateModal(e.state.productid,isHistoryNav);

                }else if(e.state.type == 'default'){

                    $('.modal.in .close').trigger('click',[isHistoryNav]);
                }
            }
        }

    /* простые обработчики событий */
        loginModule.initLogin();
    try{
        var w = $(window),
            showRight = $('.show-right'),
            hideRight = $('.hide-right'),
            shopRight = $('.shop-right'),
            showRightTop = (w.height()-showRight.width())/ 2;

        showRight.css('top',showRightTop);
        shopRight.css('min-height', w.height()-45);

        showRight.click(function(){
            if (!$(this).hasClass('active')){
                $(this).animate({'right':'352px'},200).addClass('active');
                $(this).parent().animate({'right':0},200);
            }else{
                hideRight.trigger('click');
            }
        });

        hideRight.click(function(){
            $(this).parent().animate({'right':'-380px'},200);
            showRight.animate({'right':'-28px'},200).removeClass('active');
        });

        $('.dropdown-menu li a').click(function(e){
            e.preventDefault();
            $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
        });

        $('.nav-list a').click(function(e){
            e.preventDefault();
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });

        $('.modal-order-end .btn-grey').click(function(){
            $('.modal-order-end').modal('hide');
        });

        if ($('.catalog-order li').length == 0){
            $('.additionally-order').addClass('hide');
        }

        $('.login-link').click(function(){
            $('.modal-login').modal();
        });

    }catch(e){
        //alert(e + ' Ошибка в простых обработчиках');
    }

        if(globalUserAuth){
            modules.shopCommonModule.initBasketInReload();
        }
        modules.categoryModule.InitClickOnCategory();
        modules.basketModule.InitAddToBasket($('.fa-shopping-cart'));
    // переключение между категориями
        modules.ordersModule.initOrderPlusMinus($('.shop-orders'));

        commonM.init();

      window.onerror = function(message, source, lineno) {
            /*alert("Ошибка:"+message +"\n" +
                "файл:" + source + "\n" +
                "строка:" + lineno);*/
          $('.modal.in').find('.close').trigger('click');
            var errorDetails = $('#error-details');
            var modalError= $('.modal-error');
            modalError.height('130px').modal();
            errorDetails.hide().removeClass('active');

            errorDetails.html("Ошибка:"+message +"<br>" +
                "Файл:" + source + "<br>" +
                "Строка:" + lineno);

            $('.error-details-link.no-init').click(function(){

                var h1 = modalError.height();
                var h2 = $('#error-details').height();
                var h;

                errorDetails.toggleClass('active');
                if (errorDetails.hasClass('active')) {
                    h = h1+h2;
                    errorDetails.slideDown(200);
                    modalError.height(h);
                }else{
                    h = h1-h2;
                    errorDetails.slideUp(200,function(){
                    modalError.height(h);
                    });
                };
                $(this).removeClass('no-init');
            });

            $('.modal-backdrop').click(function(){
              $('.modal.in').find('.close').trigger('click');
            });

        };
    });