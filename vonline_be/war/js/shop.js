require.config({
    baseUrl: "/build",
    paths: {
        //"jquery"   : "../js/lib/jquery-2.1.1.min",
        //"ace_spinner": "../js/lib/fuelux/fuelux.spinner",
        //"bootstrap": "../js/lib/bootstrap.min",
        ////"ace_extra": "../js/lib/ace-extra.min",
        //"ace_elements": "../js/lib/ace-elements.min",
        //"flexslider": "../js/lib/jquery.flexslider-min",
        //"jquery_ui": "../js/lib/jquery-ui-1.10.3.full.min",
        //"bootbox":"../js/bootbox.min"
    },
    shim:{
      /*'ace_spinner':{
         deps: ['jquery',"ace_elements","jquery_ui"],
         exports: 'ace_spinner'
      },*/
     /* 'jquery_ui':{
          deps: ['jquery'],
          exports: 'jquery_ui'
      },*/
    /*'bootstrap':{
        deps: ['jquery'],
        exports: 'bootstrap'
    },*/
    /*'flexslider':{
        deps: ['jquery'],
        exports: 'flexslider'
    },*/
    /*'bootbox':{
        deps: ['jquery','bootstrap'],
        exports: 'bootbox'
    }*/
    }
});

require(["jquery",'shop-modules.min','commonM.min','loginModule.min'],
    function($,modules,commonM,loginModule) {

        modules.spinnerModule.initProductsSpinner();

        if(globalUserAuth){
            modules.shopCommonModule.initBasketInReload();
        }
        modules.categoryModule.InitClickOnCategory();
        // переключение между категориями
        modules.ordersModule.initOrderPlusMinus($('.shop-orders'));

        commonM.init();

        modules.shopCommonModule.InitProductDetailPopup($('.catalog .product-link'));
        modules.basketModule.InitAddToBasket($('.fa-shopping-cart'));

        /* history */
        var urlHash = document.location.hash;

        var state = {
            type : 'default'
        };

        if($('.login-page').length == 0 && $('.page-about-shop').length == 0) window.history.replaceState(state,null,'shop.jsp');

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
            if(e.state){
                if(e.state.type == 'modal'){

                    $('.page').hide();
                    $('.page.main-container-inner').show();
                    $('.shop-orders').hide();
                    $('.shop-products').show();
                    modules.shopCommonModule.identificateModal(e.state.productid,isHistoryNav);
                    $('footer').addClass('short-footer');

                }else if(e.state.type == 'category'){

                    $('.page').hide();
                    $('.page.main-container-inner').show();
                    $('.shop-orders').hide();
                    $('.shop-products').show();
                    modules.categoryModule.LoadCategoryByURLHash(e.state.categoriesHash);
                    $('footer').addClass('short-footer');

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
                }
            }
        }

        /* --- */

        loginModule.initLogin();

    /* простые обработчики событий */
    try{
        var w = $(window),
            showRight = $('.show-right'),
            hideRight = $('.hide-right'),
            shopRight = $('.shop-right'),
            shopRightWidth = 470;

        shopRight.css('min-height', w.height()-115);

        showRight.click(function(){
            if (!$(this).hasClass('active')){
                $(this).animate({'right':'439px'},200).addClass('active');
                shopRight.css('display','block').animate({'right':0},200);
            }else{
                hideRight.trigger('click');
            }
        });

        hideRight.click(function(){
            shopRight.animate({'right':-shopRightWidth},200,function(){
                $(this).css('display','none');
            });
            showRight.animate({'right':'-31px'},200).removeClass('active');
        });

        var planshetRotate = function(){

            var height = w.height();

            var catalogHeight = height - $('.navbar').height() - $('footer').height();
            $('.catalog-order').css('max-height',catalogHeight-215);
            shopRight.css('min-height', height-115);

        };

        var resizeWidthOld = w.width();
        w.resize(function(){
            var width = w.width();

            if (width > 980){
               shopRight.css({
                   'right':'0',
                   'display':'block'
               });
           }else{
                if(width != resizeWidthOld){
                    // чтобы не срабатывал ресайз  при обычом скролле (для планшета)
                    shopRight.css('right',-shopRightWidth);
                    showRight.css({'right':'-31px'}).removeClass('active');
                }
            }

            // симуляция пворота для смартфонов, не поддерживающих событие поворота
            if(screen.width < 321 || screen.height < 321) planshetRotate();

            setShopRightWidthOnSmallPhone(width);
            resizeWidthOld = width
        });

        function setShopRightWidthOnSmallPhone(width){
            if (width < 470){
                shopRightWidth = width;
            }else{
                shopRightWidth = 470;
            }
            shopRight.width(shopRightWidth);
        }
        setShopRightWidthOnSmallPhone(w.width());

        window.addEventListener( 'orientationchange', planshetRotate, false );
        window.onorientationchange = planshetRotate;


        $('.user-short a.dropdown-toggle').click(function(e){
            e.preventDefault();
            //alert('1');

            if($(this).hasClass('no-login')){
                modules.shopCommonModule.openModalAuth();
            }else{
                $(this).closest('.navbar').toggleClass('over-rightbar');
            }
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

        $('.about-shop-link').click(function(e){
            e.preventDefault();

           $('.page').hide();

            $('.shop-about').load("ajax/about-shop.jsp .dynamic",function(){
                $(this).show();
                $('footer').removeClass('short-footer');
            });
        });

        $('html,body').click(function(e){
            //e.stopPropagation();

            if ($('.user-short').hasClass('open')){
                $('.navbar').removeClass('over-rightbar');
            }
        });

    }catch(e){
        //alert(e + ' Ошибка в простых обработчиках');
    }

      window.onerror = function(message, source, lineno) {
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