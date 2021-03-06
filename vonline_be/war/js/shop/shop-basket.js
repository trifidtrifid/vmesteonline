define(
    'shop-basket',
    ['jquery','shop-initThrift','shop-spinner','shop-common','shop-search','shop-orders','bootbox'],
    function( $,thriftModule,spinnerModule,commonModule,searchModule,ordersModule ){

        function isValidPhone(myPhone) {
            return /^(\+?\d+)?\s*(\(\d+\))?[\s-]*([\d-]*)$/.test(myPhone);
        }
        bootbox.setDefaults({
            locale : 'ru'
        });

        function cleanBasket(){
            var commonModule = require('shop-common');
            commonModule.remarkAddedProduct();
            if($('.tab-pane').length > 1){
                // если есть другие заказы, то каркас оставляем
                var currentTabDay = $('.tabs-days').find('.nav li.active');
                var currentTabPane = $('.tab-pane.active');
                currentTabPane.removeClass('active').next().addClass('active');
                currentTabPane.hide().remove();
                currentTabDay.removeClass('active').next().addClass('active');
                currentTabDay.hide().remove();
            }else{
                // иначе убираем каркас
                 $('.tabs-days').hide().remove();
            }
        }

        function InitDeleteProduct(selector,orderDetails){
            //try{
                selector.click(function(e){
                    e.preventDefault();

                    var currentTab = $('.tab-pane.active'),
                    orderId = currentTab.data('orderid'),
                    currentProductList,
                    inConfirm,myOrderDetails;

                    inConfirm = $(this).closest('.confirm-order').length > 0;
                    (inConfirm) ? currentProductList = $(this).closest('.catalog-confirm').find('.product'):
                        currentProductList = selector.closest('.catalog-order').find('.product');
                    var btnCancel = $(this).closest('.confirm-order').find('.additionally-order .btn-cancel');

                    if (currentProductList.length == 1){
                        if(inConfirm){
                            var productSelector = $(this);
                            bootbox.confirm("Вы действительно хотите отменить заказ ?", function(result) {
                                if(result) {
                                    var deleteOrder = true,
                                        back = true;
                                    productSlideUp(productSelector,deleteOrder,back);

                                }
                            });
                        }else{
                            var deleteOrder = true;
                            productSlideUp($(this),deleteOrder);
                        }
                    }else{
                        productSlideUp($(this));
                    }

                    function productSlideUp(selector,deleteOrder,back){
                        selector.closest('.product').slideUp(function(){
                            var productId = $(this).data('productid');

                            $('.catalog').find('.added').each(function(){
                                if ($(this).data('productid') == productId){
                                    $(this).removeClass('added');
                                }
                            });

                            var commonModule = require('shop-common');
                            if(inConfirm){
                                // если на странице конфирма, то обновляем и корзину на главной
                                myOrderDetails = (orderDetails) ? orderDetails : thriftModule.client.getOrderDetails(orderId);

                                $('.tabs-days .tab-content .catalog-order').find('.product').each(function(){
                                    if($(this).data('productid') == productId){
                                        $(this).css('display','none').detach();
                                    }
                                });

                                $('.itogo-right span').text(commonModule.countAmount(currentTab.find('.catalog-order'),myOrderDetails));
                            }

                            $(this).detach();

                            thriftModule.client.removeOrderLine(orderId,$(this).closest('.product').data('productid'));
                            myOrderDetails = thriftModule.client.getOrderDetails(orderId);

                            currentTab.find('.amount span').text(commonModule.countAmount(currentTab.find('.catalog-order'),myOrderDetails));

                            var weight = commonModule.getOrderWeight(orderId,myOrderDetails);
                            currentTab.find('.weight span').text(weight);
                            $('.weight-right span').text(weight);

                            if (deleteOrder){
                                currentTab.closest('.tabs-days').hide().remove();
                                thriftModule.client.deleteOrder(orderId);
                            }
                            if(back){
                                btnCancel.trigger('click');
                            }
                        });
                    }


                });
            /*}catch(e){
                alert(e+" Функция InitDeleteProduct");
            }*/
        }

        /*--------------------------------------*/
        /* addProduct */
        var flagFromBasketClick = 0;

        var callbacks = $.Callbacks();
        var selectorForCallbacks;

        function InitAddToBasket(selector){
            //try{
                selector.click(function(e){
                    e.preventDefault();

                    var errorPrepack = false;
                    if (!globalUserAuth){
                        // если пользователь не залогинен
                        selectorForCallbacks = $(this);
                        var commonModule = require('shop-common');
                        commonModule.openModalAuth();
                    }else{
                        // если пользователь залогинен
                        var currentProductSelector = $(this).closest('.product');

                        var spinnerValue = currentProductSelector.find('.td-spinner .ace-spinner').spinner('value');
                        var currentProduct = {
                            id : currentProductSelector.data('productid'),
                            imageURL : currentProductSelector.find('.product-link img').attr('src'),
                            name : currentProductSelector.find('.product-name').text(),
                            price : currentProductSelector.find('.product-price').text(),
                            unitName :currentProductSelector.find('.unit-name').text(),
                            producerName :currentProductSelector.find('.td-producer').text(),
                            producerId :currentProductSelector.find('.td-producer').data('producerid'),
                            minClientPack :  currentProductSelector.find('.td-spinner .ace-spinner .spinner1').data('step'),
                            prepackLine : currentProductSelector.find('.prepack-line'),
                            prepackRequired: currentProductSelector.data('prepack'),
                            qnty : spinnerValue,
                            packVal : 1,
                            quantVal :  currentProductSelector.find('.td-spinner .spinner1').data('step')
                        };

                        var packs = [];
                        if (currentProduct.prepackRequired){
                            // если это товар с prepackRequired
                            currentProduct.quantVal = spinnerValue;
                            var isModalWasOpen = (currentProductSelector.find('.modal-body').length > 0);

                            if (isModalWasOpen){
                                // если пользватель открывал модальное окно(в таблице продуктов) с инфой о продукте
                                var packVal = currentProductSelector.find('.packs:eq(0) .ace-spinner').spinner('value');
                                var quantVal = currentProductSelector.find('.qnty .ace-spinner').eq(0).spinner('value');
                                currentProduct.packVal = packVal;
                                currentProduct.quantVal = quantVal;
                                currentProduct.qnty = packVal*quantVal;
                                packs[quantVal] = packVal; // если только одна линия с упаковкой

                                if(currentProduct.prepackLine.length != 0){
                                    // если линий более чем одна
                                    var oldQuantVal = 0;
                                    var firstQuantVal = $('.modal-footer.with-prepack>.qnty .ace-spinner').spinner('value');
                                    currentProduct.prepackLine.each(function(){
                                        packVal = $(this).find('.packs .ace-spinner').spinner('value');
                                        quantVal = $(this).find('.qnty .ace-spinner').spinner('value');
                                        if (quantVal == oldQuantVal || quantVal == firstQuantVal){
                                            currentProductSelector.find('.error-prepack').text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта').show();
                                            errorPrepack = true;
                                        }
                                        packs[quantVal] = packVal;
                                        currentProduct.qnty += packVal*quantVal;
                                        oldQuantVal = quantVal;
                                    });
                                }

                            }else{
                                // если не открывал модальное окно в таблице продуктов
                                var isOrdersHistory = $(this).closest('.order-item').length > 0;
                                if(isOrdersHistory){
                                    // если мы на странице истории заказов
                                    // (то нужно вытащить packs из заказа)
                                    var orderId = $(this).closest('.order-item').data('orderid'),
                                        orderDetails = thriftModule.client.getOrderDetails(orderId);
                                    var orderLines = orderDetails.odrerLines;
                                    var orderLinesLength = orderLines.length;
                                    for(var i = 0; i < orderLinesLength; i++){
                                        if(orderLines[i].product.id == $(this).closest('.product').data('productid')){
                                            packs = orderLines[i].packs;
                                        }
                                    }
                                }else{
                                    // если мы странице продуктов
                                    packs[currentProduct.qnty] = 1; // значаение packs по умолчанию
                                    currentProduct.packsQnty = 1;
                                }
                            }
                        }else{
                            // если обычный товар
                            packs = 0;
                        }
                        if (!errorPrepack){
                            if ($('.tabs-days').length == 0){
                             // если это первый товар в корзине

                                initChooseDatepicker(true,0,null,currentProduct,packs,orderDetails,currentProductSelector);

                             }else{
                                AddProductToBasketCommon(currentProduct,packs);

                                currentProductSelector.addClass('added');
                            }

                        }

                        var isModalWindow = $(this).closest('.modal').length > 0;
                        if (isModalWindow && !errorPrepack){
                            $(this).closest('.modal').find('.close').trigger('click');//modal('hide');
                            currentProductSelector.find('.error-prepack').hide();
                        }
                    }
                });
            /*}catch(e){
                alert(e+" Функция InitAddToBasket");
            }*/
        }

        function initChooseDatepicker(isNewOrder,orderId,dateObj,currentProduct,packs,orderDetails,currentProductSelector,addType){
            $('.modal-chooseDate').modal();
            $('.datepicker-chooseDate').datepicker({language:'ru'});

            if(!isNewOrder){

                $('.chooseDate').attr('data-date',dateObj.date).text(dateObj.text);
                initUpdateOrder(isNewOrder,orderId,currentProduct,packs,orderDetails,currentProductSelector);

            }else{

                initUpdateOrder(isNewOrder,orderId,currentProduct,packs,orderDetails,currentProductSelector,addType);
            }

        }

        function initUpdateOrder(isNewOrder,orderId,currentProduct,packs,orderDetails,currentProductSelector,addType){
            $('.create-order-btn').one('click',function(e){
                e.preventDefault();

                var orderDate = $('.chooseDate').attr('data-date');

                //thriftModule.client.createOrder(orderDate);
                //thriftModule.client.updateOrder(orderId,orderDate);

                if(isNewOrder) {
                    var order = thriftModule.client.createOrder(orderDate);
                    //var nextDateStr = new Date(order.date * 1000);

                    //orderDetails = thriftModule.client.getOrderDetails(order.id);

                    addTabToBasketHtml(order.date, order.id);

                    if(addType) {
                        var ordersModule = require('shop-orders');

                        ordersModule.addOrderTo(orderId, addType)
                    }else{
                        AddProductToBasketCommon(currentProduct,packs);
                        currentProductSelector.addClass('added');
                    }

                    /*if(addType){
                        if (addType == 'replace'){
                            var orderLines = orderDetails.odrerLines;
                            var orderLinesLength = orderLines.length;
                            for(i = 0; i < orderLinesLength; i++){
                                curProd = orderLines[i].product;
                                spinVal = orderLines[i].quantity;
                                var packs = orderLines[i].packs;
                                thriftModule.client.setOrderLine(oldOrderId,curProd.id,spinVal,"",packs);
                            }
                        }else if (addType == 'append'){
                            orderDetails = thriftModule.client.appendOrder(oldOrderId,orderId);
                        }
                        orderLines = orderDetails.odrerLines;
                        orderLinesLength = orderLines.length;
                        var spinnerDisable;
                        for(i = 0; i < orderLinesLength; i++){
                            curProd = orderLines[i].product;
                            spinVal = orderLines[i].quantity;
                            spinnerDisable = false;
                            var commonModule = require('shop-common.min');
                            if(orderLines[i].packs && commonModule.getPacksLength(orderLines[i].packs) > 1){
                                spinnerDisable = true;
                            }
                            basketModule.AddSingleProductToBasket(curProd,spinVal,spinnerDisable,orderDetails);
                        }
                        commonModule.markAddedProduct();
                        tabPaneActive.find('.weight span').text(commonModule.getOrderWeight(orderId,orderDetails));*/

                }else {
                    thriftModule.client.updateOrder(orderId, orderDate);

                    var nextDateStr = new Date(orderDate * 1000);
                    var orderWeekDay = nextDateStr.getDay();

                    // обновить текстовую инфу
                    var tab = $('.tabs-days .nav-tabs a');

                    tab.html(getWeekDay(orderWeekDay) +
                        '<span>'+ $('.chooseDate').text() +'</span>');

                    $('.order-date').html('Заказ на <span>'+$('.chooseDate').text()+
                    ' ('+ getWeekDay(orderWeekDay) +')</span>');

                }

                $('.modal-chooseDate').modal('hide');

            });
        }

        function getWeekDay(orderWeekDay){
            var day;
            switch(orderWeekDay){
                case 0:
                    day = 'ВС';
                    break;
                case 1:
                    day = 'ПН';
                    break;
                case 2:
                    day = 'ВТ';
                    break;
                case 3:
                    day = 'СР';
                    break;
                case 4:
                    day = 'ЧТ';
                    break;
                case 5:
                    day = 'ПТ';
                    break;
                case 6:
                    day = 'СБ';
                    break;
            }
            return day;
        }

        function addTabToBasketHtml(orderDate,orderId,orderDetails){

            var nextDateStr = new Date(orderDate * 1000);

            var orderDay = nextDateStr.getDate();
            var orderWeekDay = nextDateStr.getDay();
            var orderMonth = nextDateStr.getMonth()+1;

            orderDay = (orderDay < 10)? "0" + orderDay: orderDay;
            orderMonth = (orderMonth < 10)? "0" + orderMonth: orderMonth;

            orderWeekDay = getWeekDay(orderWeekDay);

            if($('.tabs-days').length == 0){
                // если это первый заказ в корзине
                var html = '<div class="tabbable tabs-right tabs-days">'+
                    '<ul class="nav nav-tabs" id="myTab3">'+
                    '<li class="active">'+
                    '<a data-toggle="tab" data-date="'+ orderDate +'" href="#day'+orderDay+orderMonth+'">'+
                    orderWeekDay+
                    '<span>'+orderDay+'.'+orderMonth+'</span>'+
                    '</a>'+
                    '</li>'+
                    '</ul>'+
                    '<div class="tab-content">'+
                    '<div id="day'+orderDay+orderMonth+'" data-orderid="'+ orderId +'" class="tab-pane active">'+
                    '<div class="basket-head">'+
                        '<div class="weight">Вес: <span></span> кг.</div>'+
                        '<div class="amount">Итого: <span></span> руб.</div>'+
                    '</div>'+
                    '<ul class="catalog-order">'+
                    '</ul>'+
                    '<div class="basket-bottom">'+
                    '<a class="refresh">Обновить</a>'+
                    '<a href="#" class="btn btn-sm btn-primary no-border btn-order">Оформить</a>'+
                        '<a href="#" class="btn btn-sm btn-cancel no-border">Отменить</a>'+
                    '</div>'+
                    '</div>'+
                    '</div>'+
                    '</div>';

                $('.shop-right').append(html);

            }else{
                var tabDays = $('.tabs-days');
                $('.tabs-days .active').removeClass('active');
                html = '<li class="active">'+
                    '<a data-toggle="tab" data-date="'+ orderDate +'" href="#day'+orderId+'">'+
                    orderWeekDay+
                    '<span>'+orderDay+'.'+orderMonth+'</span>'+
                    '</a>'+
                    '</li>';
                tabDays.find('.nav-tabs').append(html);
                html = '<div id="day'+orderId+'" data-orderid="'+ orderId +'" class="tab-pane active">'+
                    '<div class="basket-head">'+
                    '<div class="amount">Итого: <span></span></div>'+
                    '</div>'+
                    '<ul class="catalog-order">'+
                    '</ul>'+
                    '<div class="basket-bottom">'+
                    '<a class="refresh">Обновить</a>'+
                    '<a href="#" class="btn btn-sm btn-primary no-border btn-order">Оформить</a>'+
                    '<a href="#" class="btn btn-sm btn-cancel no-border">Отменить</a>'+
                    '</div>'+
                    '</div>';
                tabDays.find('.tab-content').append(html);
            }

            $('.nav-tabs a').click(function(){

                var orderId = $(this).closest('.tabs-days').find('.tab-pane.active').attr('data-orderid'),
                    dateObj = {};

                    dateObj.text = $(this).find('span').text();
                    dateObj.date = $(this).attr('data-date');

                initChooseDatepicker(false,orderId,dateObj,null,null,orderDetails);

                // в этом случае на календаре должна будет отображаться другая дата
            });

            var spinnerModule = require('shop-spinner');
            spinnerModule.initRefresh();

            var activeOrder = $('.tabs-days .tab-pane.active');

            /*activeOrder.find('.btn-cancel').click(function(e){
                e.preventDefault();

                var quest = confirm('Вы действительно хотите отменить заказ ?');

                if (quest){
                    var orderId = $('.tab-pane.active').data('orderid');
                    cleanBasket();
                    thriftModule.client.cancelOrder(orderId);
                }
            });*/

            console.log('outside cancel '+activeOrder.find('.btn-cancel').length);
            activeOrder.find('.btn-cancel').on(ace.click_event, function(e,deleteOrderFromHistory) {
                e.preventDefault();
                console.log('inside cancel');

                bootbox.confirm("Вы действительно хотите отменить заказ ?", function(result) {
                    if(result) {
                        var orderId = $('.tab-pane.active').data('orderid');
                        cleanBasket();
                        thriftModule.client.cancelOrder(orderId);
                        if(deleteOrderFromHistory) thriftModule.client.deleteOrder(orderId);
                    }
                });

                $('.modal-backdrop').click(function(){
                    $('.modal.in .close').trigger('click');
                    $(this).hide();
                });
            });

            activeOrder.find('.btn-order').click(function(e){
                e.preventDefault();

                var currentTab = $('.tab-pane.active');
                currentTab.find('.refresh').trigger('click');

                if(!orderDetails) orderDetails = thriftModule.client.getOrderDetails(currentTab.attr('data-orderid'));

                var commonModule = require('shop-common');
                var amount = commonModule.countAmount(currentTab.find('.catalog-order'),orderDetails);
                var weight = $('.weight span').text();
                var catalogHtml = currentTab.find('.catalog-order').html();
                var spinnerValue = [], spinnerStep = [], counter = 0;
                currentTab.find('.catalog-order td .spinner1').each(function(){
                    spinnerValue[counter] = $(this).closest('.ace-spinner').spinner('value');
                    spinnerStep[counter++] = $(this).data('step');
                });

                $('.page').hide();

                var date = {
                    orderDay: orderDay,
                    orderMonth: orderMonth,
                    orderWeekDay: orderWeekDay
                };

                GoToConfirm(catalogHtml,amount,spinnerValue,date,weight,orderDetails);

            });
        }

        function GoToConfirm(catalogHtml,amount,spinnerValue,date,weight,orderDetailsOut){

            $('.shop-confirm').load('../ajax/ajax-confirmOrder.html .dynamic',function(){
                $('footer').removeClass('short-footer');
                /* history */
                var urlHash = document.location.hash;
                if (urlHash != '#confirm-order'){
                    // safari не понимает объект window.history.state
                    var state = {
                        type : 'page',
                        pageName: 'confirm-order'
                    };
                    var tempHash;
                    (urlHash.indexOf('#') == -1) ? tempHash = urlHash : tempHash = "";
                    window.history.pushState(state,null,tempHash+'#'+state.pageName);
                }
                /* --- */
                var myDate;

                var orderId = $('.tab-pane.active').data('orderid');

                var orderDetails;
                (orderDetailsOut) ? orderDetails = orderDetailsOut : orderDetails = thriftModule.client.getOrderDetails(orderId);
                var checkboxDeliveryType = $('.delivery-right .radio');
                checkboxDeliveryType.find('input').prop('checked',false);
                //var shops = thriftModule.client.getShops();
                var shopId = $('.shop.dynamic').attr('id');
                var shop = thriftModule.client.getShop(shopId);
                var shopAddress = shop.address;

                switch (orderDetails.delivery){
                    case 1:
                        checkboxDeliveryType.find('input:not(".courier-delivery")').prop('checked',true);
                        writeAddress(shopAddress);
                        break;
                    default:
                        checkboxDeliveryType.find('input.courier-delivery').prop('checked',true);
                        writeAddress(orderDetails.deliveryTo);
                        var userAddresses = thriftModule.client.getUserDeliveryAddresses().elems;
                        defaultAddressForCourier = orderDetails.deliveryTo;
                        showDeliveryDropdown(orderId,userAddresses);
                        break;
                }

                var confirmOrder = $('.confirm-order .catalog-confirm');
                confirmOrder.html(catalogHtml);

                setDeliveryCost(orderId,orderDetails);
                initRadioBtnClick(shopAddress);

                var dateText = $('.tabs-days .nav-tabs li.active a').html();
                var weekDay = dateText.split('<span>')[0];

                myDate = (typeof date == 'string') ? date : $('.tabs-days .nav-tabs li.active a span').text()+' ('+ weekDay +')';
                $('.order-date span').text(myDate);
                $('.itogo-right span').text(amount);
                $('.weight-right span').text(weight);

                // на случай если в корзине уже открывали попап, удаляем его, чтобы не
                // вносить путаницу
                confirmOrder.find('.modal-body').remove();

                var counter = 0;

                var userContacts = thriftModule.userClient.getUserContacts();
                if(userContacts.mobilePhone){
                    $('#phone-delivery').val(userContacts.mobilePhone);
                }

                confirmOrder.find('td .ace-spinner').each(function(){
                    var step = $(this).find('.spinner1').data('step');
                    $(this).after('<input type="text" data-step="'+ step +'" class="input-mini spinner1 spinner-input form-control no-init" maxlength="3">');
                    spinnerModule.InitSpinner($(this).find('+.spinner1'),spinnerValue[counter++],1,step);
                    if ($(this).find('.spinner1').attr('disabled')) { $(this).find('+.ace-spinner').spinner('disable')}
                    $(this).remove();
                });

                InitDeleteProduct(confirmOrder.find('.delete-product'),orderDetails);
                var commonModule = require('shop-common');
                commonModule.InitProductDetailPopup(confirmOrder.find('.product-link'));
                spinnerModule.initRefresh();

                var optionsForBtnOrderClick = {
                    orderId : orderId,
                    userContacts : userContacts,
                    shop : shop
                };
                initBtnOrderClick($('.confirm-order .btn-order'),optionsForBtnOrderClick);

                $('.confirm-order .btn-cancel').click(function(){
                    //$('.back-to-shop').trigger('click');
                    $('.page.shop-confirm').hide();
                    window.history.back();
                });

                $('.order-date').click(function(e){
                    e.preventDefault();

                    var orderId = $('.tab-pane.active').attr('data-orderid'),
                        dateObj = {};

                    dateObj.text = $(this).find('span').text();
                    dateObj.text = dateObj.text.split(" ")[0];
                    dateObj.date = $('.tabs-days .nav-tabs a.active').attr('data-date');

                    initChooseDatepicker(false,orderId,dateObj,null,null,orderDetails);
                });


            }).show();
        }

        function initBtnOrderClick(selector,options){
            var isAddedAddressSave = false;
            selector.click(function(){
                var phoneDelivery = $('#phone-delivery');
                var alertDeliveryPhone = $('.alert-delivery-phone'),
                    orderId = options.orderId,
                    shop = options.shop,
                    isEmptyAddressDelivery = $('.delivery-address').find('.error-info').length > 0,
                    isAddressInput = false;

                if($('.address-input').css('display')=='block' && !isAddedAddressSave){
                    $('.add-address').trigger('click');
                    if(!isAddError) isAddedAddressSave = true;
                }else if(!phoneDelivery.val()){
                    alertDeliveryPhone.text('Пожалуйста введите номер телефона.').show();
                    $('#phone-delivery').focus();

                }else { //if(!isAddressInput || $('.street-delivery').val() && $('.building-delivery').val() && $('.flat-delivery').val()){

                    var haveError = 0;
                    try{
                        if (options.userContacts.mobilePhone != phoneDelivery.val()){
                            options.userContacts.mobilePhone = phoneDelivery.val();
                            thriftModule.userClient.updateUserContacts(options.userContacts);
                        };
                    }catch(e){
                        haveError = 1;
                        alertDeliveryPhone.text('Телефон должен быть вида 79219876543, +7(821)1234567 и т.п').show();
                    }
                    var orderDetails;
                    if (isEmptyAddressDelivery) {
                        orderDetails = thriftModule.client.setOrderDeliveryType(orderId,1);
                        writeAddress(shop.address);
                    }

                    if(!haveError){
                        alertDeliveryPhone.hide();
                        cleanBasket();
                        $('.shop-orderEnd').load('../ajax/ajax-orderEnd.html .dynamic',function(){

                            var href = document.location.href;
                            href = href.split('#')[0];
                            $('.to-main-link').attr('href',href);//

                            $('.page').hide();
                            $(this).show();

                            var order = thriftModule.client.getOrder(orderId);
                            orderDetails = (orderDetails) ? orderDetails : thriftModule.client.getOrderDetails(orderId);

                            var summaryCost = $('.itogo-right span').text();
                            $('.bill-amount span,.all-amount span').text(summaryCost);
                            $('.bill-delivery-address span').text($('.input-delivery .delivery-address').text());
                            $('.bill-date-delivery span').text($('.order-date span').text());
                            $('.bill-client span').text($('.user-info').text());
                            $('.bill-client-phone span').text($('#phone-delivery').val());
                            $('.bill-pay-type span').text(getPaymentType(orderDetails.paymentType));
                            $('.bill-weight span').text($('.weight-right span').text()+" кг.");

                            var orderDate = new Date(orderDetails.createdAt*1000);
                            var orderDay = orderDate.getDate();
                            var orderWeekDay = orderDate.getDay();
                            var orderMonth = orderDate.getMonth()+1;

                            orderDay = (orderDay < 10)? "0" + orderDay: orderDay;
                            orderMonth = (orderMonth < 10)? "0" + orderMonth: orderMonth;

                            orderWeekDay = getWeekDay(orderWeekDay);

                            $('.bill-shop-name span').text(shop.name);
                            $('.bill-order-number span').text(order.id);
                            $('.bill-date-order span').text(orderDay+"."+orderMonth+" ("+ orderWeekDay +")");
                            $('.bill-shop-phone span').text('---');
                            $('.bill-shop-email span').text('---');
                            $('.order-end-logo img').attr('src',shop.logoURL);

                            var deliveryCost = $('.delivery-cost').text();
                            var costWithoutDelivery = summaryCost-deliveryCost;

                            var placeForDeliveryCost = $('.bill-delivery span');
                            (deliveryCost != '0') ? placeForDeliveryCost.text(deliveryCost+" р.") : placeForDeliveryCost.text("---");

                            $('.bill-amount-order span').text(costWithoutDelivery.toFixed(1));

                            var comment = "";
                            var orderComment = $('#order-comment');
                            if(orderComment.val()){
                                var commentHtml = "<div class='bill-comment'>" +
                                    "<h4>Комментарий: </h4>"+
                                    "<div>"+ orderComment.val() +"</div>"+
                                    "</div>";
                                $('.bill-delivery').after(commentHtml);
                                comment = orderComment.val();
                            }

                            thriftModule.client.confirmOrder(orderId,comment);

                            var noEdit = true;
                            $('.bill-order-list').append(ordersModule.createOrdersProductHtml(orderDetails,noEdit));
                        });
                    }
                }
            });
        }

        function getPaymentType(paymentTypeDigit){
            var paymentType;
             switch(paymentTypeDigit){
                 case 0:
                     paymentType = "Неизвестно";
                     break;
                 case 1:
                     paymentType = "Наличными";
                     break;
                 case 2:
                     paymentType = "Кредитной карточкой";
                     break;
                 case 3:
                     paymentType = "Трансфер";
                     break;
                 case 4:
                     paymentType = "Кредит";
                     break;
             }
            return paymentType;
        }

        function AddProductToBasketCommon(currentProduct,packs){
            var addedProductFlag = 0;
            var currentTab = $('.tab-pane.active');
            var orderId = currentTab.data('orderid');
            var commonModule = require('shop-common');

            $('.catalog-order li').each(function(){
                if ($(this).data('productid') == currentProduct.id){
                    addedProductFlag = 1;
                }
            });

            var myOrderDetails;

            if (addedProductFlag){
                // если такой товар уже есть
                var basketProductSelector = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]');
                var currentSpinner = basketProductSelector.find('.td-spinner .ace-spinner');
                var newSpinnerVal = currentProduct.qnty;
                currentSpinner.spinner('value',parseFloat(newSpinnerVal).toFixed(1));

                var newPacks;

                (commonModule.getPacksLength(packs) <= 1) ? currentSpinner.spinner('enable'):currentSpinner.spinner('disable');

                myOrderDetails = thriftModule.client.setOrderLine(orderId,currentProduct.id,newSpinnerVal,'',packs);

                currentTab.find('.weight span').text(commonModule.getOrderWeight(orderId,myOrderDetails));

                var newSumma = (newSpinnerVal*parseFloat(basketProductSelector.find('.td-price').text())).toFixed(1);
                basketProductSelector.find('.td-summa').text(newSumma);
                currentTab.find('.amount span').text(commonModule.countAmount(currentTab.find('.catalog-order'),myOrderDetails));
            }else{
                // если такого товара еще нет
                myOrderDetails = thriftModule.client.setOrderLine(orderId,currentProduct.id,currentProduct.qnty,'',packs);

                AddSingleProductToBasket(currentProduct,currentProduct.qnty,0,myOrderDetails);

                currentTab.find('.weight span').text(commonModule.getOrderWeight(orderId,myOrderDetails));
            }
            if(currentProduct.packVal > 1 || currentProduct.prepackLine.length != 0 || (packs && commonModule.getPacksLength(packs) > 1)){
                currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('.td-spinner .ace-spinner');
                currentSpinner.spinner('disable');
            }
        }

        function AddSingleProductToBasket(currentProduct,spinnerValue,spinnerDisable,orderDetails){
            //try{
                var myPic;
                var commonModule = require('shop-common');
                myPic = (currentProduct.imageURL) ? currentProduct.imageURL : commonModule.noPhotoPic;

                var productHtml = '<li class="product" data-productid="'+ currentProduct.id +'" data-prepack="'+ currentProduct.prepackRequired +'">'+
                    '<table>'+
                    '<tr>'+
                    '<td class="td-price product-price">'+ currentProduct.price +'</td>'+
                    '<td class="td-spinner"><input type="text" data-step="'+ currentProduct.minClientPack +'" class="input-mini spinner1 no-init" /><span class="unit-name">'+ currentProduct.unitName +'</span></td>'+
                    '<td class="td-summa">'+ (currentProduct.price*spinnerValue).toFixed(1) +'</td>'+
                    '<td class="td-producer" data-producerid="'+ currentProduct.producerId +'">'+ currentProduct.producerName +'</td>'+
                    '<td class="td-close"><a href="#" class="delete-product no-init">×</a></td>'+
                    '</tr>'+
                    '</table>'+
                    '<a href="#" class="product-link no-init">'+
                    '<span><img src="'+ myPic +'" alt="'+ currentProduct.name +'"/></span>'+
                    '<div class="product-right-descr product-name">'+
                    currentProduct.name+
                    '</div>'+
                    '</a>'+
                    '<div class="modal">'+
                    '</div>'+
                    '</li>';
            /*}catch(e){
                alert(e+" Функция AddSingleProductToBasket");
            }*/

            var currentTab = $('.tab-pane.active');
            var catalogOrder = currentTab.find('.catalog-order');
            catalogOrder.append(productHtml);
            currentTab.find('.amount span').text(commonModule.countAmount(catalogOrder,orderDetails));

            var deleteNoInit = currentTab.find('.catalog-order .delete-product.no-init');
            InitDeleteProduct(deleteNoInit,orderDetails);
            deleteNoInit.removeClass('no-init');

            var popupNoInit = currentTab.find('.catalog-order .product-link.no-init');
            commonModule.InitProductDetailPopup(popupNoInit);
            popupNoInit.removeClass('no-init');

            var spinnerNoInit = currentTab.find('.catalog-order .spinner1.no-init');
            var itsBasket = 1;
            spinnerModule.InitSpinner(spinnerNoInit,parseFloat(spinnerValue).toFixed(1),itsBasket,currentProduct.minClientPack);
            if (spinnerDisable){spinnerNoInit.closest('.ace-spinner').spinner('disable');}
            spinnerNoInit.removeClass('no-init');
        }

        function BasketTrigger(selector){
            selector.trigger('click');
        }

        function getNextDate(){
            //try{
                var day = 3600*24;
                var now = parseInt(new Date()/1000);
                now -= now%86400;
                var nextDate = thriftModule.client.getNextOrderDate(now);

            /*}catch(e){
                alert(e + ' Функция SetFreeDates');
            }*/
            return nextDate.orderDate;
        }

        /* delivery */
        var autocompleteAddressFlag = 1;
        var triggerDelivery = 0;

        function writeAddress(address){
            $('.input-delivery .delivery-address').text(address.country.name + ", " + address.city.name + ", "
                + address.street.name + " " + address.building.fullNo + ", кв. " + address.flatNo);

        }

        var mapWidthConst = 400;
        var mapHeightConst = 300;
        var isAddError = false;
        function setDeliveryDropdown(orderId,userAddresses,NoAddAddressAgain){
            var addresses = (userAddresses) ? userAddresses : thriftModule.client.getUserDeliveryAddresses().elems;

            var userAddressesHtml = "";
            var userAddressesLength = addresses.length,
                address;
            for(var i = 0; i < userAddressesLength; i++){
                address = thriftModule.client.getUserDeliveryAddress(addresses[i]);
                userAddressesHtml += '<li><a data-addresstext="'+addresses[i]+'" href="#">'+
                    //addresses[i]+
                    address.street.name+" "+address.building.fullNo+", кв. "+address.flatNo+
                    '</a></li>';
            }
            userAddressesHtml += '<li class="divider"></li>'+
                '<li><a href="#" class="delivery-add-address">Добавить адрес ...</a></li>';

            $('.delivery-dropdown .dropdown-menu').html('').prepend(userAddressesHtml);

            $('.delivery-dropdown .dropdown-menu a:not(".delivery-add-address")').click(function(e){
                e.preventDefault();
                var ind = $(this).parent().index();

                defaultAddressForCourier = thriftModule.client.getUserDeliveryAddress(addresses[ind]);
                writeAddress(defaultAddressForCourier);

                var addressText = $(this).data('addresstext');
                var mapSrc = thriftModule.client.getDeliveryAddressViewURL(addressText,mapWidthConst,mapHeightConst);
                $('.address-map').remove();
                $(this).closest('.input-delivery').find('.address-input').after("<img class='address-map' src='"+ mapSrc +"'>");

                var orderDetails = thriftModule.client.setOrderDeliveryType(orderId,2,defaultAddressForCourier);
                setDeliveryCost(orderId,orderDetails);
            });

            $('.delivery-add-address').click(function(e){
                e.preventDefault();
                $('.address-input .street-delivery').val('');
                $('.address-input .building-delivery').val('');
                $('.address-input .flat-delivery').val('');
                $('.address-input').show();
                $('.delivery-dropdown .btn-group-text').text('Выбрать адрес');
            });

            if(!NoAddAddressAgain){
            $('.add-address').click(function(e){
                e.preventDefault();

                var street = $('.street-delivery').val(),
                    building = $('.building-delivery').val(),
                    flat = $('.flat-delivery').val();

                if (!$('.country-delivery').val() || !$('.city-delivery').val() || !street || !building || !flat){
                    $('.alert-delivery-phone').css('display','none');
                    $('.alert-delivery-addr').text('Введите полный адрес доставки !').css('display','block');

                    isAddError = true;
                }else{
                    var addressText =  street + " " + building;
                    var deliveryAddress = defaultAddressForCourier = thriftModule.client.createDeliveryAddress(addressText,parseInt(flat),0,0,0);
                    var mapSrc = thriftModule.client.getDeliveryAddressViewURL(addressText,mapWidthConst,mapHeightConst);

                    $('.address-map').remove();
                    $(this).closest('.address-input').after("<img class='address-map' src='"+ mapSrc +"'>");

                    $('.alert-delivery-addr').hide();
                    var addressInput = $('.address-input');
                    addressInput.slideUp();

                    // добавление в базу нового города, страны, улицы и т.д (если курьером)

                    var commonModule = require('shop-common');
                    commonModule.addAddressToBase(addressInput,deliveryAddress);
                    writeAddress(deliveryAddress);

                    var orderDetails = thriftModule.client.setOrderDeliveryType(orderId,2,deliveryAddress);
                    setDeliveryCost(orderId,orderDetails);
                    var NoAddAddressAgain = true;
                    setDeliveryDropdown(orderId,0,NoAddAddressAgain);

                    isAddError = false;
                }

            });
            }
        }

        function showDeliveryDropdown(orderId,userAddresses){
            $('.delivery-dropdown').show();

            setDeliveryDropdown(orderId,userAddresses);
        }

        var defaultAddressForCourier;
        function initRadioBtnClick(shopAddress){

            $('.radio input').click(function(){
                var itogoRight = $('.itogo-right span');
                var orderId = $('.tab-pane.active').data('orderid');
                var commonModule = require('shop-common');
                var orderDetails;
                var userAddresses = thriftModule.client.getUserDeliveryAddresses().elems;


                if ($(this).hasClass('courier-delivery')){
                    //если доставка курьером

                    var homeAddress = thriftModule.userClient.getUserContacts().homeAddress;
                    if(!userAddresses.length) defaultAddressForCourier = null;
                    if (!defaultAddressForCourier){
                        if(homeAddress){
                            defaultAddressForCourier = homeAddress;
                        }else if (userAddresses.length){
                            defaultAddressForCourier = thriftModule.client.getUserDeliveryAddress(userAddresses[0]);
                        }
                    }
                    var myAddress = defaultAddressForCourier;

                    showDeliveryDropdown(orderId,userAddresses);

                    if(myAddress){
                        writeAddress(myAddress);
                        orderDetails = thriftModule.client.setOrderDeliveryType(orderId,2,myAddress);
                        setDeliveryCost(orderId,orderDetails);

                    }else{
                        $('.input-delivery .delivery-address').html("<span class='error-info'>Введите пожалуйста адрес доставки.</span>");
                        $('.input-delivery .delivery-address .error-info').show();
                        $('.delivery-add-address').trigger('click');
                    }

                    itogoRight.text(commonModule.countAmount($('.confirm-order'),orderDetails));

                    triggerDelivery = 1;

                    if (autocompleteAddressFlag){
                        var searchModule = require('shop-search');
                        searchModule.initAutocompleteAddress($('.address-input'));
                        autocompleteAddressFlag = 0;
                    }
                    $('.address-map').slideDown(200);

                }else{
                    orderDetails = thriftModule.client.setOrderDeliveryType(orderId,1);
                    $('.address-map').slideUp(200);

                    writeAddress(shopAddress);
                    setDeliveryCost(orderId,orderDetails);
                    $('.delivery-dropdown').hide();

                    if (triggerDelivery){
                        itogoRight.text(commonModule.countAmount($('.confirm-order')),orderDetails);
                        triggerDelivery = 0;
                    }
                }
            });
        }

        function setDeliveryCost(orderId,orderDetails,basketProductsContainer){
            var myOrderDetails = (orderDetails) ? orderDetails : thriftModule.client.getOrderDetails(orderId);

            if (myOrderDetails.deliveryCost){
                $('.delivery-cost').text(myOrderDetails.deliveryCost);
            }else{
                $('.delivery-cost').text('0');
            }

            var commonModule = require('shop-common');

            var container = (basketProductsContainer) ? basketProductsContainer : $('.catalog-confirm');
            $('.itogo-right span,.amount span').text(commonModule.countAmount(container,orderDetails));
        }

        return{
            cleanBasket: cleanBasket,
            InitDeleteProduct: InitDeleteProduct,
            flagFromBasketClick: flagFromBasketClick,
            AddProductToBasketCommon: AddProductToBasketCommon,
            InitAddToBasket: InitAddToBasket,
            BasketTrigger: BasketTrigger,
            AddSingleProductToBasket: AddSingleProductToBasket,
            addTabToBasketHtml: addTabToBasketHtml,
            setDeliveryCost: setDeliveryCost,
            GoToConfirm: GoToConfirm,
            initChooseDatepicker: initChooseDatepicker,
            getWeekDay: getWeekDay,
            getNextDate: getNextDate,
            callbacks: callbacks,
            selectorForCallbacks: selectorForCallbacks
        }

    }
);