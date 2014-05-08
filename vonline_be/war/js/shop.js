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
        modules.shopCommonModule.setCatalogTopOffset();

        if(globalUserAuth){
            modules.shopCommonModule.initBasketInReload();
            var catalogHeight = $('.catalog').height();
            $('.catalog-order').css('max-height',catalogHeight-100)
        }
        modules.categoryModule.InitClickOnCategory();
        // переключение между категориями
        modules.ordersModule.initOrderPlusMinus($('.shop-orders'));

        commonM.init();

        modules.shopCommonModule.InitProductDetailPopup($('.catalog .product-link'));
        modules.basketModule.InitAddToBasket($('.fa-shopping-cart'));
        modules.spinnerModule.initProductsSpinner();

        var urlHash = document.location.hash;

        var state = {
            type : 'default'
        };

        if($('.login-page').length == 0) window.history.replaceState(state,null,'shop.jsp');

        if (urlHash){
            if (urlHash.indexOf('p=') != -1){
                // значит url с modal
                var hashParts = urlHash.split('=');
                modules.shopCommonModule.identificateModal(hashParts[1]);

            }else if (urlHash.indexOf('cat=') != -1){
                // значит категория
                var categoriesHistory = urlHash.split(';');
                var categoriesHistoryLength = categoriesHistory.length;
                for(var i = 0; i < categoriesHistoryLength; i++){
                    hashParts = categoriesHistory[i].split('=');
                    $('.shop-menu li').each(function(){
                        if($(this).data('catid') == hashParts[1]){
                            $(this).find('a').trigger('click');
                        }
                    });
                }
            }else if (urlHash == '#orders-history'){

                $('.shop-trigger.go-to-orders').trigger('click');

            }else if (urlHash == '#profile'){

                $('.user-menu a:eq(0)').trigger('click');

            }else if (urlHash == '#edit-profile'){

                var loadEditPersonal = true;
                $('.user-menu a:eq(0)').trigger('click',[loadEditPersonal]);

            }else if (urlHash == '#confirm-order'){

                $('.basket-bottom .btn-order').trigger('click');

            }
        }

        window.addEventListener('popstate',makeHistoryNav,false);

        function makeHistoryNav(e){
            // действия для корректной навигации по истории
            var isHistoryNav = true;
            //alert('makeHistory '+e.state.locationModal);
            if(e.state){
                if(e.state.type == 'modal'){

                    modules.shopCommonModule.identificateModal(e.state.productid,isHistoryNav);

                }else if(e.state.type == 'category'){

                    var categoriesHistory = e.state.categoriesHash.split(';');
                    var categoriesHistoryLength = categoriesHistory.length;
                    var prevCategoryIndexPos = categoriesHistoryLength - 1;
                    var prevCategoryIndex;

                    if (prevCategoryIndexPos < 0){
                        prevCategoryIndex = 0;
                    }else{
                        var hashParts = categoriesHistory[prevCategoryIndexPos].split('=');
                        prevCategoryIndex = hashParts[1];
                    }
                    modules.categoryModule.InitLoadCategory(prevCategoryIndex);

                }else if(e.state.type == 'page'){

                    if(e.state.pageName == 'orders-history'){

                        $('.shop-trigger.go-to-orders').trigger('click',[isHistoryNav]);

                    }else if(e.state.pageName == 'profile'){

                        $('.user-menu a:eq(0)').trigger('click');

                    }else if(e.state.pageName == 'edit-profile'){

                        var loadEditPersonal = true;
                        $('.user-menu a:eq(0)').trigger('click',[loadEditPersonal]);

                    }else if(e.state.pageName == 'confirm-order'){

                        $('.basket-bottom .btn-order').trigger('click');

                    }
                }else if(e.state.type == 'default'){

                    $('.shop-trigger.back-to-shop').trigger('click',[isHistoryNav]);
                    $('.modal.in .close').trigger('click',[isHistoryNav]);
                    modules.categoryModule.InitLoadCategory(0);
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
        shopRight.css('min-height', w.height()-115);

        showRight.click(function(){
            if (!$(this).hasClass('active')){
                $(this).animate({'right':'442px'},200).addClass('active');
                $(this).parent().animate({'right':0},200);
            }else{
                hideRight.trigger('click');
            }
        });

        hideRight.click(function(){
            $(this).parent().animate({'right':'-470px'},200);
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

        $('.navbar-brand').click(function(){
           $('.back-to-shop a').trigger('click');
            return false;
        });

    }catch(e){
        //alert(e + ' Ошибка в простых обработчиках');
    }



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