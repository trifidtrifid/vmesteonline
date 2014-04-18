require.config({
    baseUrl: "/js",
    paths: {
        "jquery"   : "lib/jquery-2.0.3.min",
        "ace_spinner": "lib/fuelux/fuelux.spinner",
        "bootstrap": "lib/bootstrap.min",
        "ace_extra": "lib/ace-extra.min",
        "ace_elements": "lib/ace-elements.min",
        "flexslider": "lib/jquery.flexslider-min",
        "jquery_ui": "lib/jquery-ui-1.10.3.full.min",
        "datepicker-simple": "lib/date-time/bootstrap-datepicker-simple"/*,
        "datepicker": "lib/date-time/bootstrap-datepicker",
        "datepicker-ru": "lib/date-time/locales/bootstrap-datepicker.ru"*/
    },
    shim:{
      'ace_spinner':{
         deps: ['jquery',"bootstrap","ace_extra","ace_elements","jquery_ui"],
         exports: 'ace_spinner'
      },
      'jquery_ui':{
          deps: ['jquery'],
          exports: 'jquery_ui'
      },
        'flexslider':{
            deps: ['jquery'],
            exports: 'flexslider'
        },
      'datepicker-simple':{
          deps: ['jquery',"bootstrap"],
          exports: 'datepicker-simple'
      }/*,
    'datepicker-ru':{
        deps: ['jquery','datepicker'],
        exports: 'datepicker-ru'
    }*/

    }
});

/*require(["jquery",'shop-initThrift','shop-common','shop-spinner','shop-addProduct','shop-category','shop-orders','shop-delivery'],
    function($,thriftModule,commonModule,spinnerModule,addProduct,categoryModule,ordersModule,deliveryModule) {*/
require(["jquery",'shop-modules','commonM','loginModule'],
    function($,modules,commonM,loginModule) {
    /* простые обработчики событий */
        //alert('app '+commonModule+" "+spinnerModule+" "+addProduct+" "+categoryModule+" "+ordersModule+" "+deliveryModule);
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

        modules.spinnerModule.initProductsSpinner();

   /* var triggerDelivery = 0;
    var autocompleteAddressFlag = 1;*/
        //modules.deliveryModule.initRadioBtnClick();

        if(globalUserAuth){
        modules.shopCommonModule.initBasketInReload();
        }
        modules.categoryModule.InitClickOnCategory();
        modules.basketModule.InitAddToBasket($('.fa-shopping-cart'));
        modules.shopCommonModule.InitProductDetailPopup($('.product-link'));
    // переключение между категориями
        modules.ordersModule.initOrderPlusMinus($('.shop-orders'));

        commonM.init();

        window.onerror = function(message, source, lineno) {
            alert("Ошибка:"+message +"\n" +
            "файл:" + source + "\n" +
            "строка:" + lineno);
        };
    });