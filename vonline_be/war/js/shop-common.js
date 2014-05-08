define(
    'shop-common',
    ['jquery','flexslider','shop-initThrift','shop-basket','shop-spinner','shop-orders','shop-category','bootstrap'],
    function( $ ,flexsliderModule,thriftModule,basketModule,spinnerModule,categoryModule,ordersModule ){

        var noPhotoPic = "i/no-photo.png";
        // возвращает cookie с именем name, если есть, если нет, то undefined
        function getCookie(name) {
            try{
                var matches = document.cookie.match(new RegExp(
                    "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
                ));
            }
            catch(e){
                alert(e+" Функция getCookie");
            }
            return matches ? decodeURIComponent(matches[1]) : undefined;
        }

        function setCookie(name, value, options) {
            try{
                options = options || {};

                var expires = options.expires;

                if (typeof expires == "number" && expires) {
                    var d = new Date();
                    d.setTime(d.getTime() + expires*1000);
                    expires = options.expires = d;
                }
                if (expires && expires.toUTCString) {
                    options.expires = expires.toUTCString();
                }

                value = encodeURIComponent(value);

                var updatedCookie = name + "=" + value;

                for(var propName in options) {
                    updatedCookie += "; " + propName;
                    var propValue = options[propName];
                    if (propValue !== true) {
                        updatedCookie += "=" + propValue;
                    }
                }

                document.cookie = updatedCookie;
            }catch(e){
                alert(e+" Функция setCookie");
            }
        }

        function getOrderWeight(orderId,orderDetails){
            var myOrderDetails = (orderDetails) ? orderDetails : thriftModule.client.getOrderDetails(orderId);
            return (myOrderDetails.weightGramm/1000).toFixed(1);
        }

        function addAddressToBase(currentForm,address){
            var countries = thriftModule.userClient.getCounties();
            var countriesLength = countries.length;
            var inputCountry = (address) ? address.country.name : currentForm.find('.country-delivery').val();
            var country,countryId = 0;
            for (var i = 0; i < countriesLength; i++){
                if (countries[i].name == inputCountry){
                    country = countries[i];
                    countryId = country.id;
                }
            }
            if (!countryId){
                country = thriftModule.userClient.createNewCountry(inputCountry);
                countryId = country.id;
            }

            var cities = thriftModule.userClient.getCities(countryId);
            var citiesLength = cities.length;
            var inputCity = (address) ? address.city.name : currentForm.find('.city-delivery').val();
            var city,cityId = 0;
            for (i = 0; i < citiesLength; i++){
                if (cities[i].name == inputCity){
                    city = cities[i];
                    cityId = city.id;
                }
            }
            if (!cityId){
                city = thriftModule.userClient.createNewCity(countryId,inputCity);
                cityId = city.id;
            }

            var streets = thriftModule.userClient.getStreets(cityId);
            var streetsLength = streets.length;
            var inputStreet = (address) ? address.street.name : currentForm.find('.street-delivery').val();
            var street,streetId = 0;
            for (i = 0; i < streetsLength; i++){
                if (streets[i].name == inputStreet){
                    street = streets[i];
                    streetId = street.id;
                }
            }
            if (!streetId){
                street = thriftModule.userClient.createNewStreet(cityId,inputStreet);
                streetId = street.id;
            }

            var buildings = thriftModule.userClient.getBuildings(streetId);
            var buildingsLength = buildings.length;
            var inputBuilding = (address) ? address.building.fullNo : currentForm.find('.building-delivery').val();

            var building,buildingId = 0;
            for (i = 0; i < buildingsLength; i++){
                if (buildings[i].fullNo == inputBuilding){
                    building = buildings[i];
                    buildingId = building.id;
                }
            }
            if (!buildingId){
                building = thriftModule.userClient.createNewBuilding(streetId,inputBuilding);
                buildingId = building.id;
            }

            var deliveryAddress = new com.vmesteonline.be.PostalAddress();
            deliveryAddress.country = country;
            deliveryAddress.city = city;
            deliveryAddress.street = street;
            deliveryAddress.building = building;
            deliveryAddress.staircase = 0;
            deliveryAddress.floor= 0;
            deliveryAddress.flatNo = parseInt((address) ? address.flatNo : currentForm.find('.flat-delivery').val());
            deliveryAddress.comment = $('#order-comment').val();

            return deliveryAddress;
        }

        function initBasketInReload(){

            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;

            var currentOrders = thriftModule.client.getMyOrdersByStatus(nowTime,nowTime+90*day,1);
            var currentOrdersLength = currentOrders.length;
            for(var i = 0; i < currentOrdersLength; i++){
                var orderid = currentOrders[i].id;

                var orderDetails = thriftModule.client.getOrderDetails(orderid);
                basketModule.addTabToBasketHtml(new Date(currentOrders[i].date*1000),orderid,orderDetails);
                $('.tab-pane.active').find('.weight span').text(getOrderWeight(orderid,orderDetails));

                var orderLines = orderDetails.odrerLines;
                var orderLinesLength = orderDetails.odrerLines.length;
                for(var j = 0; j < orderLinesLength; j++){
                    //currentProduct,spinnerValue,spinnerDisable
                    var spinnerDisable;
                    var packs = orderLines[j].packs;
                    var packsQnty = 0;
                    for(var p in packs){
                        if(packs[p] > 1){packsQnty = 1}
                    }

                    spinnerDisable = (getPacksLength(packs) > 1 || packsQnty ) ? true : false;
                    basketModule.AddSingleProductToBasket(orderLines[j].product,orderLines[j].quantity,spinnerDisable,orderDetails);

                }
            }

            markAddedProduct();
        }

        function InitProductDetailPopup(selector){
            try{
                selector.click(function(e,isHistoryNav){
                    e.preventDefault();

                    var  isOrdersHistory = ($(this).closest('.order-products').length) ? true : false,
                        isCatalog = ($(this).closest('.catalog').length && !isOrdersHistory) ? true : false,
                        isBasket = ($(this).closest('.catalog-order').length || $(this).closest('.catalog-confirm').length) ? true : false;

                    var productSelector = $(this).closest('.product'),
                    name= $(this).find('.product-name').text(),
                    productId = productSelector.data('productid');

                    var product= {
                        name : name,
                        price : productSelector.find('.product-price').text(),
                        unitName: productSelector.find('.unit-name').text(),
                        imageURL : $(this).find('img').attr('src')
                    };

                    var currentModal = $(this).find('+.modal');
                    var isModalWasOpen = currentModal.find('.modal-body').length;
                    var boolPrepackRequired = productSelector.data('prepack');

                    if (!isModalWasOpen){

                        var productDetails = thriftModule.client.getProductDetails(productId);
                        var imagesSet = productDetails.imagesURLset;
                        var options = productDetails.optionsMap;


                    if(imagesSet.length){
                        //var oldHeight = currentModal.height();
                        currentModal.height('275px');
                    }

                    if (boolPrepackRequired){
                        currentModal.addClass('modal-with-prepack');
                    }
                    var spinnerStep = productSelector.find('.td-spinner .spinner1').data('step');
                    var popupHtml = "";
                    popupHtml += '<div class="modal-body">'+
                        '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                        '<div class="product-slider">'+
                        '<div class="slider flexslider">'+
                        '<ul class="slides">'+
                        '<li>'+
                        '<img src="'+product.imageURL+'" />'+
                        '</li>';
                    if(imagesSet.length){
                        for(var i = 0; i < imagesSet.length; i++){
                            popupHtml += '<li>'+
                                '<img src="'+imagesSet[i]+'" />'+
                                '</li>';
                        }
                    }
                        popupHtml += '</ul>'+
                        '</div>';

                    if(imagesSet.length){
                        popupHtml += '<div class="carousel flexslider">'+
                            '<ul class="slides">'+
                            '<li>'+
                            '<img src="'+product.imageURL+'" />'+
                            '</li>';
                        for(i = 0; i < imagesSet.length; i++){
                            popupHtml += '<li>'+
                                '<img src="'+imagesSet[i]+'" />'+
                                '</li>';
                        }
                        popupHtml += '</ul>'+
                            '</div>';
                    }

                    popupHtml += '</div>'+
                        '<div class="product-descr">'+
                        '<h3>'+product.name+'</h3>'+
                        '<div class="product-text">'+
                        '<div class="product-options">';
                    for(var p in options){
                        popupHtml += '<div>'+p+" "+options[p]+'</div>';
                    }
                    popupHtml += '</div>';

                    if (boolPrepackRequired){
                        popupHtml += '<div class="modal-footer with-prepack">'+
                            '<span>Цена: '+product.price+'</span>'+
                            '<div class="prepack-item packs">'+
                            '<input type="text" class="input-mini spinner1 prepack" />'+
                            '<span>упаковок</span>'+
                            '</div>'+
                            '<div class="prepack-item">по</div>'+
                            '<div class="prepack-item qnty">'+
                            '<input type="text" data-step="'+ spinnerStep +'" class="input-mini spinner1" />'+
                            '<span>'+ product.unitName +'</span>'+
                            '</div>'+
                            '<div class="prepack-item"><a href="#" title="Добавить" class="fa fa-plus prepack-open"></a></div>';
                    }else{
                        popupHtml += '<div class="modal-footer">'+
                            '<span>Цена: '+product.price+'</span>'+
                            '<div class="prepack-item">'+
                            '<input type="text" class="input-mini spinner1" />'+
                            '<span>'+ product.unitName +'</span>'+
                            '</div>';
                    }
                    popupHtml += '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>';


                    popupHtml += '<div class="prepack-list"></div><br>'+
                        '<div class="error-info error-prepack"></div>'+
                        '<a href="#" class="btn btn-primary btn-sm no-border full-descr">Подробное описание</a>'+
                        "<div class='product-fullDescr'>"+ productDetails.fullDescr +"</div>"+
                        '</div>'+
                        '</div>'+
                        '</div>';

                        // если еще не открывали popup
                        currentModal.append(popupHtml);
                        var currentSpinnerValue,currentSpinnerStep;

                        if (isBasket || isOrdersHistory){
                            // если мы в корзине или на странице заказов
                            //($(this).closest('.order-products').length > 0) ? isBasket = false : isBasket = true ;
                            if (boolPrepackRequired){
                                var isFirstModal = true;
                                spinnerModule.initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal,isBasket);
                            }else{
                                //если обычный товар
                                currentSpinnerValue = productSelector.find('.ace-spinner').spinner('value');
                                currentSpinnerStep = productSelector.find('.td-spinner .spinner1').data('step');
                                spinnerModule.InitSpinner(currentModal.find('.spinner1'), currentSpinnerValue,isBasket,currentSpinnerStep);
                            }
                            if(isOrdersHistory){
                                // если на странице истории заказов, то нужно инициализировать AddToBasket
                                basketModule.InitAddToBasket(currentModal.find('.fa-shopping-cart'));
                            }
                        }else{
                            // если не в корзине
                            currentSpinnerValue = productSelector.find('.ace-spinner').spinner('value');
                            if (boolPrepackRequired){
                                spinnerModule.InitSpinner(currentModal.find('.packs .spinner1'), 1);
                                spinnerModule.InitSpinner(currentModal.find('.qnty .spinner1'),currentSpinnerValue,0,spinnerStep);
                            }else{
                                spinnerModule.InitSpinner(currentModal.find('.spinner1'),currentSpinnerValue,0,spinnerStep);
                            }
                            basketModule.InitAddToBasket(currentModal.find('.fa-shopping-cart'));
                        }

                        var optionsForPrepackOpenClick = {
                                isBasket : isBasket,
                                productSelector : productSelector,
                                productId : productId
                        };
                        initPrepackOpenClick(currentModal,optionsForPrepackOpenClick);

                        initFullDescrClick(currentModal);

                        if(isBasket){
                            //если это popup для корзины
                            currentModal.find('.fa-shopping-cart').click(function(){

                                var orderId = $('.tab-pane.active').data('orderid');

                                var fromModal = true;
                                var packsObj = spinnerModule.singleSetOrderLine(productSelector,orderId,productId,fromModal);

                                if(!packsObj.errorFlag){
                                    var basketProductsContainer = ($(this).closest('.catalog-order').length) ?
                                        $(this).closest('.catalog-order') : $(this).closest('.catalog-confirm');
                                    spinnerModule.updateWeightAndAmount(orderId,basketProductsContainer,packsObj.updateInfo);

                                    currentModal.modal('hide');
                                }

                                return false;
                            });
                        }

                        currentModal.find('.close').click(function(e,isHistoryNav){
                            //isCatalog
                            if (!isHistoryNav && isCatalog) {
                                window.history.back();
                            }
                        });

                    }else{
                        //если popup уже открывали
                        if (isBasket || isOrdersHistory){
                            // если мы в корзине или на странице заказов
                            if (boolPrepackRequired){
                                isFirstModal = false;
                                spinnerModule.initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal);
                            }
                        }else{
                            if (boolPrepackRequired){
                                var prepackLineLength;

                                //чтобы высота была корректной у добавленного товара
                                (productSelector.hasClass('added')) ? prepackLineLength = 0 : prepackLineLength = currentModal.find('.prepack-line').length;

                                var modalHeight = 268;
                                modalHeight += prepackLineLength*53;
                                currentModal.height(modalHeight);
                            }
                        }
                    }
                    //var beginHash = (isBasket || isOrdersHistory) ? "p-basket" : "p";
                    var beginHash = "p";

                    if (!isHistoryNav && isCatalog){
                        var state = {
                            type : 'modal',
                            productid : productId
                        };
                        window.history.pushState(state,null,'shop.jsp#'+ beginHash +'='+productId);
                    }
                    currentModal.modal();
                    fullDescrHeight = currentModal.find('.product-fullDescr').height();
                    currentModal.find('.product-fullDescr').hide();

                    $('.modal-backdrop').click(function(){
                        if (isCatalog) window.history.back();
                        $('.modal.in .close').trigger('click');
                    });

                    var carousel = currentModal.find('.carousel');
                    var slider = currentModal.find('.slider');

                    carousel.flexslider({
                        animation: "slide",
                        controlNav: false,
                        animationLoop: false,
                        slideshow: false,
                        itemWidth: 60,
                        itemMargin: 5,
                        asNavFor: slider
                    });

                    slider.flexslider({
                        animation: "slide",
                        controlNav: false,
                        animationLoop: false,
                        slideshow: false,
                        sync: carousel
                    });

                });
            }catch(e){
                alert(e+" Функция InitProductDetailPopup");
            }
        }

        function identificateModal(productId,historyBool){
            var isHistoryNav = (historyBool) ? historyBool : false;
            $('.catalog .product').each(function(){
                if($(this).data('productid') == productId){
                    $(this).find('.product-link').trigger('click',[isHistoryNav]);
                }
            })
        }

        function initPrepackOpenClick(selector,options){
            selector.find('.prepack-open').click(function(e){
                e.preventDefault();

                var spinnerStep = $(this).closest('.modal-footer').find('>.qnty .spinner1').data('step');
                var isBasket = options.isBasket,
                    productSelector = options.productSelector,
                    productId = options.productId;

                var prepackHtml = '<div class="prepack-line no-init">' +
                    '<div class="prepack-item packs">'+
                    '<input type="text" class="input-mini spinner1 prepack" />'+
                    '<span>упаковок</span>'+
                    '</div>'+
                    '<div class="prepack-item">по</div>'+
                    '<div class="prepack-item qnty">'+
                    '<input type="text" data-step="'+ spinnerStep +'" class="input-mini spinner1" />'+
                    '<span>'+ $(this).closest('.prepack-item').prev().find('span').text() +'</span>'+
                    '</div>'+
                    '<div class="prepack-item">'+
                    '<a href="#" class="remove-prepack-line" title="Удалить">×</a>'+
                    '</div>'+
                    '</div>';
                $(this).closest('.modal-footer').find('.prepack-list').append(prepackHtml);
                var currentPrepackLine = $('.prepack-line.no-init');

                spinnerModule.InitSpinner(currentPrepackLine.find('.packs .spinner1'), 1,isBasket);
                spinnerModule.InitSpinner(currentPrepackLine.find('.qnty .spinner1'), spinnerStep,isBasket,spinnerStep);

                /*var addedPackVal = currentPrepackLine.find('.packs .ace-spinner').spinner('value');
                var addedQntyVal = parseFloat(currentPrepackLine.find('.qnty .ace-spinner').spinner('value')).toFixed(1);*/

                /*if (isBasket){
                    //если мы в корзине
                    // нужно сделать setOrderLine
                    var orderId = $('.tab-pane.active').data('orderid'),
                        orderDetails = thriftModule.client.getOrderDetails(orderId),
                        orderLinesLength = orderDetails.odrerLines.length,
                        packs, reCount = true;

                    for (var i = 0; i < orderLinesLength; i++){
                        if (orderDetails.odrerLines[i].product.id == productId){
                            // если это наш продукт в заказе
                            var tempPacks = orderDetails.odrerLines[i].packs;
                            if (tempPacks && tempPacks[parseFloat(spinnerStep).toFixed(1)]){
                                reCount = false;
                                productSelector.find('.error-prepack').text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта').show();
                            }
                            packs = tempPacks;
                            qnty = orderDetails.odrerLines[i].quantity;
                        }
                    }
                    if (reCount) {
                        productSelector.find('.error-prepack').hide();
                        qnty += addedPackVal*addedQntyVal;
                        packs[addedQntyVal] = addedPackVal;

                        thriftModule.client.setOrderLine(orderId,productId,qnty,'',packs);

                        productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(qnty).toFixed(1));

                    var sum = qnty*productSelector.find('.td-price').text();
                        productSelector.find('.td-summa').text(sum.toFixed(1));

                        $('.itogo-right span').text(countAmount($('.catalog-order'),0));
                    }
                }else{
                    // если в таблице продуктов
                    qnty = productSelector.find('.td-spinner .ace-spinner').spinner('value');
                    qnty += addedPackVal*addedQntyVal;
                    productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(qnty).toFixed(1));
                }*/

                /*var qnty = productSelector.find('.td-spinner .ace-spinner').spinner('value');
                qnty += addedPackVal*addedQntyVal*/

                /*var sum = qnty*productSelector.find('.td-price').text();
                productSelector.find('.td-summa').text(sum.toFixed(1));
                productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(qnty).toFixed(1));*/

                //productSelector.find('.td-spinner .ace-spinner').spinner('disable');

                spinnerModule.initRemovePrepackLine(currentPrepackLine.find('.prepack-item .remove-prepack-line'),productId,productSelector);
                currentPrepackLine.removeClass('no-init');

                var oldHeight = $(this).closest('.modal').height();
                $(this).closest('.modal').height(oldHeight + 53);

            });
        }

        var fullDescrHeight;
        function initFullDescrClick(selector){

            selector.find('.full-descr').click(function(){
                var fullDescr = $('.product-fullDescr');
                var oldHeight;
                oldHeight = $(this).closest('.modal').height();
                var newHeight;

                if(fullDescr.css('display') == 'none'){
                    newHeight = oldHeight + fullDescrHeight+10;
                    $(this).closest('.modal').height(newHeight);
                    fullDescr.show(200);
                }else{
                    newHeight = oldHeight - fullDescrHeight - 10;
                    fullDescr.hide(200,function(){
                        $(this).closest('.modal').height(newHeight);
                    });
                }
            });

        }

        function countAmount(sel,orderDetails){
            try{
                /*var summa = 0;
                sel.find('.td-summa').each(function(){
                    summa += parseFloat($(this).text());
                });*/
                var orderId = $('.tab-pane.active').data('orderid');

                var myOrderDetails = (orderDetails) ? orderDetails : thriftModule.client.getOrderDetails(orderId);
                var summa = myOrderDetails.totalCost;

                if(!summa){
                    summa = 0;
                    sel.find('.td-summa').each(function(){
                        summa += parseFloat($(this).text());
                    });
                }

                summa += myOrderDetails.deliveryCost;
            }catch(e){
                //alert(e+" Функция countAmount");
            }
            return summa.toFixed(1);
        }

        function markAddedProduct(){
            var productsInTable = $('.catalog tr');
            var productsInBasket = $('.tabs-days .tab-content .catalog-order li');
            var basketLength = productsInBasket.length;
            var addedProductId;

            productsInTable.each(function(){
                for(var i = 0; i < basketLength; i++){
                    addedProductId = productsInBasket.eq(i).data('productid');
                    if($(this).data('productid') == addedProductId){
                        $(this).addClass('added');
                    }
                }
            });

        }

        function remarkAddedProduct(){
            var addedProducts= $('.catalog tr.added');
            var productsInBasket = $('.tabs-days .tab-content .catalog-order li');
            var basketLength = productsInBasket.length;
            var addedProductId;

            addedProducts.each(function(){
                for(var i = 0; i < basketLength; i++){
                    addedProductId = productsInBasket.eq(i).data('productid');
                    if($(this).data('productid') == addedProductId){
                        $(this).removeClass('added');
                    }
                }
            });
        }

        function getPacksLength(packs){
            var counter = 0;
            for(var p in packs){
                //alert(p+" "+packs[p]);
                if(p && packs[p]){
                    counter++;
                }
            }
            return counter;
        }

        function setSidebarHeight(){
            try{
                var mainContent = $('.main-content');
                //alert(mainContent.height()+" "+$(window).height());

                if (mainContent.height() > $(window).height()-45){
                    $('.shop-right').css('height', mainContent.height()+45);
                }else{
                    $('.shop-right').css('height', '100%');
                }
            }catch(e){
                alert(e+" Функция setSidebarHeight");
            }
        }

        function setCatalogTopOffset(){
            var catalogTop = $('.shop-products .form-group').height()+$('.shop-menu').height()+$('.catalog-head').height()+$('.navbar').height();
            var catalogBottom = $('.wrap footer').height();
            var h = $(window).height() - catalogTop - catalogBottom-30;
            $('.catalog').css('height',h);
        }

        $('.shop-trigger').click(function(e,isHistoryNav){
            e.preventDefault();
            //try{
                var shopOrders = $('.shop-orders');
                var ordersList = $('.orders-list');
                $('.page').hide();
                var state;

                if($(this).hasClass('back-to-shop')){
                    shopOrders.hide();
                    $('.shop-confirm').hide();
                    $('.main-container-inner').show();
                    $('.navbar .nav li.active').removeClass('active');
                    $('.navbar .nav li:eq(0)').addClass('active');
                    var shopProducts = $('.shop-products');
                    if(shopProducts.find('.shop-menu .shopmenu-back').length){
                        // если у нас загружена подкатегория а не коренвая, то нужно загрузить коренвую
                        var categoryModule = require('shop-category');
                        categoryModule.InitLoadCategory(0);
                    }
                    shopProducts.show(function(){
                        //setSidebarHeight();
                    });
                    state = {
                        type : 'default'
                    };
                    window.history.pushState(state,null,'shop.jsp');

                }else{
                    /* history */
                    if (!isHistoryNav && window.history.state.pageName != 'orders-history'){
                        state = {
                            type : 'page',
                            pageName: 'orders-history'
                        };
                        window.history.pushState(state,null,'shop.jsp#'+state.pageName);
                    }
                    /**/
                    var ordersModule = require('shop-orders');
                    if (!globalUserAuth){
                        var basketModule = require('shop-basket');
                        basketModule.callbacks.add(ordersModule.GoToOrdersTrigger);
                        //$('.modal-auth').modal();
                        openModalAuth();
                    }else{
                        $('.shop-products').hide();
                        $('.shop-confirm').hide();
                        $('.main-container-inner').show();
                        var nowTime = parseInt(new Date().getTime()/1000);
                        nowTime -= nowTime%86400;
                        var day = 3600*24;
                        //var orders = thriftModule.client.getOrders(0,nowTime+90*day);
                        var orders = thriftModule.client.getMyOrdersByStatus(0,nowTime+90*day,0);
                        ordersModule.initVarForMoreOrders();

                        // если всегда делать createOrdersHtml, то странциа заказов будет обновляться в реальном времени
                        // а так можно оптимизировать и не делать createOrderHtml каждый раз при перезагрузке
                        ordersList.html('').append(ordersModule.createOrdersHtml(orders));

                        InitProductDetailPopup($('.shop-orders .product-link'));
                        ordersModule.initShowMoreOrders(orders);
                        ordersModule.initOrdersLinks();
                        var ordersNoInit = $('.orders-no-init');
                        ordersModule.initOrderPlusMinus(ordersNoInit);
                        ordersModule.initOrderBtns(ordersNoInit);
                        ordersNoInit.removeClass('orders-no-init');
                        shopOrders.show();
                        setSidebarHeight();
                    }
                }
            markAddedProduct();
            /*}catch(e){
                alert(e+" Функция $('.shop-trigger').click");
            }*/
        });

        function openModalAuth(){
            //$('.modal.in .close').trigger('click');
            var modalIn = $('.modal.in');
            if(modalIn.length) modalIn.modal('hide');
            var modalAuth = $('.modal-auth');
            modalAuth.load('login.jsp .container',function(){
                var closeHtml = '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>';
                modalAuth.find('.reg-form').prepend(closeHtml);

                // запускаем скрипты логина через ajax
                $.ajax({
                    url: 'js/login.js',
                    dataType: 'script'
                });

            }).modal();
        }

        function isValidEmail(myEmail) {

            return /^([a-z0-9_-]+\.)*[a-z0-9_-]+@[a-z0-9_-]+(\.[a-z0-9_-]+)*\.[a-z]{2,6}$/.test(myEmail);

        }

        function changeShortUserInfo(newUserInfo){
            var shortUserInfo = (newUserInfo) ?  newUserInfo : thriftModule.userClient.getShortUserInfo();
            var shortUserInfoHtml =  shortUserInfo.firstName +' '+ shortUserInfo.lastName;
            $('.user-info').html(shortUserInfoHtml);
        }

        return{
            getCookie: getCookie,
            setCookie: setCookie,
            noPhotoPic: noPhotoPic,
            initBasketInReload: initBasketInReload,
            InitProductDetailPopup: InitProductDetailPopup,
            countAmount: countAmount,
            getPacksLength: getPacksLength,
            setSidebarHeight: setSidebarHeight,
            openModalAuth: openModalAuth,
            markAddedProduct: markAddedProduct,
            remarkAddedProduct: remarkAddedProduct,
            addAddressToBase: addAddressToBase,
            isValidEmail: isValidEmail,
            changeShortUserInfo: changeShortUserInfo,
            getOrderWeight: getOrderWeight,
            identificateModal: identificateModal,
            setCatalogTopOffset: setCatalogTopOffset
        }
    }
);