require.config({
    baseUrl: "/js",
    paths: {
        "jquery"   : "/lib/jquery-2.0.3.min"
        /*"calendar" : "zeitproject.calendar",
        "formatter": "/utils/formatter",
        "tooltip"  : "/controls/zeitproject.tooltip",
        "tiptip"   : "/controls/jquery.tiptip.min" */   }
});

require(["jquery", "util","shop-search"], function($, util,calendar) {
    // глобальные переменные для callback полсе логина в реальном времени


    try{
        var transport = new Thrift.Transport("/thrift/ShopService");
        var protocol = new Thrift.Protocol(transport);
        var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/UserService");
        protocol = new Thrift.Protocol(transport);
        var userServiceClient = new com.vmesteonline.be.UserServiceClient(protocol);
    }catch(e){
        alert(e + " Ошибка иниицализации thrift");
    }


    /* простые обработчики событий */
    try{
        var w = $(window),
            showRight = $('.show-right'),
            hideRight = $('.hide-right'),
            shopRight = $('.shop-right'),
            showRightTop = (w.height()-showRight.width())/ 2;

        showRight.css('top',showRightTop);
        $('.shop-right').css('min-height', w.height()-45);

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

    initProductsSpinner();

   /* var triggerDelivery = 0;
    var autocompleteAddressFlag = 1;*/
    initRadioBtnClick();

    initBasketInReload();
    InitAddToBasket($('.fa-shopping-cart'));
    InitProductDetailPopup($('.product-link'));
    // переключение между категориями
    InitClickOnCategory();
    initOrderPlusMinus($('.shop-orders'));
}); 