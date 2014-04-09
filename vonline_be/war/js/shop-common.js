define(
    'shop-common',
    ['jquery','shop-initThrift','shop-basket','shop-spinner','shop-orders'],
    function( $ ,thriftModule,basketModule,spinnerModule,ordersModule ){

        alert('common'+ basketModule+" "+spinnerModule+" "+basketModule+" "+ordersModule);
        //setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

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

        function initBasketInReload(){
            var catalogOrderLi = $('.catalog-order li');
            if(catalogOrderLi.length > 0){
                var order = thriftModule.client.getOrder(0);
                currentOrderId = order.id;
                var orderDetails = thriftModule.client.getOrderDetails(currentOrderId);
                var orderLines = orderDetails.odrerLines;
                var orderLinesLength = orderLines.length;
                var itsBasket = 1;
                var catalogOrder = $('.catalog-order');

                catalogOrderLi.each(function(){
                    basketModule.InitDeleteProduct($(this).find('.delete-product'));
                    InitProductDetailPopup($(this).find('.product-link'));
                    var newTdSumma = parseFloat($(this).find('.td-summa').text()).toFixed(1);
                    $(this).find('.td-summa').text(newTdSumma);

                    var productId = $(this).data('productid');
                    for(var i = 0; i < orderLinesLength; i++){
                        if (orderLines[i].product.id == productId){
                            var minClientPack = $(this).find('td .spinner1').data('step');
                            spinnerModule.InitSpinner($(this).find('td .spinner1'),orderLines[i].quantity,itsBasket,minClientPack);
                            if( getPacksLength(orderLines[i].packs) > 1 ){
                                $(this).find('td .ace-spinner').spinner('disable');
                            }
                            break;
                        }
                    }

                });
                if(orderDetails.delivery == 1){
                    $('.radio input:not(".courier-delivery")').trigger('click');
                }else if (orderDetails.delivery == 2){
                    $('.radio .courier-delivery').trigger('click');
                }
                $('.itogo-right span').text(countAmount(catalogOrder));
                $('.empty-basket').hide();
            }
        }

        function InitProductDetailPopup(selector){
            try{
                selector.click(function(e){
                    e.preventDefault();

                    var productSelector,
                        name;
                    if ($(this).closest('tr').length > 0 ){
                        // если таблица
                        productSelector = $(this).closest('tr');
                        name = $(this).find('span span').text();
                    }else{
                        // если в корзине
                        productSelector = $(this).closest('li');
                        name= $(this).find('.product-right-descr').text()
                    }

                    var product= {
                        name : name,
                        price : productSelector.find('.product-price').text(),
                        unitName: productSelector.find('.unit-name').text(),
                        imageURL : $(this).find('img').attr('src')
                    };
                    var productDetails = thriftModule.client.getProductDetails(productSelector.data('productid'));
                    var imagesSet = productDetails.imagesURLset;
                    var options = productDetails.optionsMap;
                    var currentModal = $(this).find('+.modal');

                    if(imagesSet.length){
                        var oldHeight = currentModal.height();
                        currentModal.height(oldHeight + 25);
                    }
                    if (productDetails.prepackRequired){
                        currentModal.addClass('modal-with-prepack');
                    }
                    var spinnerStep = productSelector.find('td>.ace-spinner .spinner1').data('step');
                    var popupHtml = "";
                    popupHtml += '<div class="modal-body">'+
                        '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                        '<div class="product-slider">'+
                        '<div class="slider flexslider">'+
                        '<ul class="slides">'+
                        '<li>'+
                        '<img src="'+product.imageURL+'" />'+
                        '</li>'+
                        '</ul>'+
                        '</div>';

                    if(imagesSet.length){
                        popupHtml += '<div class="carousel flexslider">'+
                            '<ul class="slides">'+
                            '<li>'+
                            '<img src="'+product.imageURL+'" />'+
                            '</li>'+
                            '</ul>'+
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
                    /*productDetails.fullDescr+
                     '</div>';*/

                    if (productDetails.prepackRequired){
                        popupHtml += '<div class="modal-footer with-prepack">'+
                            '<span>Цена: '+product.price+'</span>'+
                            '<div class="prepack-item packs">'+
                            '<input type="text" class="input-mini spinner1 prepack" />'+
                            '<span>упаковок</span>'+
                            '</div>'+
                            '<div class="prepack-item">по</div>'+
                            '<div class="prepack-item">'+
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
                    popupHtml += "<div class='error-info error-prepack'></div>"+
                        '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>';


                    popupHtml += '<div class="prepack-list"></div><br>'+
                        '<a href="#" class="btn btn-primary btn-sm no-border full-descr">Подробное описание</a>'+
                        "<div class='product-fullDescr'>"+ productDetails.fullDescr +"</div>"+
                        '</div>'+
                        '</div>'+
                        '</div>';


                    var fullDescrHeight;
                    if (currentModal.find('.modal-body').length == 0){
                        // если еще не открывали popup
                        currentModal.append(popupHtml);
                        if ($(this).closest('tr').length == 0 || $(this).closest('.order-products').length > 0){
                            // если мы в корзине или на странице заказов
                            if (productDetails.prepackRequired){
                                var isFirstModal = true;
                                spinnerModule.initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal);
                            }else{
                                //если обычный товар
                                spinnerModule.InitSpinner(currentModal.find('.spinner1'), productSelector.find('.ace-spinner').spinner('value'),1,productSelector.find('td>.ace-spinner .spinner1').data('step'));
                            }
                            if($(this).closest('.order-products').length > 0){
                                // если на странице истории заказов, то нужно инициализировать AddToBasket
                                basketModule.InitAddToBasket(currentModal.find('.fa-shopping-cart'));
                            }
                        }else{
                            // если не в корзине
                            if (productDetails.prepackRequired){
                                spinnerModule.InitSpinner(currentModal.find('.prepack-item.packs .spinner1'), 1);
                                spinnerModule.InitSpinner(currentModal.find('.prepack-item:not(".packs") .spinner1'), productSelector.find('.ace-spinner').spinner('value'),0,spinnerStep);
                            }else{
                                spinnerModule.InitSpinner(currentModal.find('.spinner1'), productSelector.find('.ace-spinner').spinner('value'),0,spinnerStep);
                            }
                            basketModule.InitAddToBasket(currentModal.find('.fa-shopping-cart'));
                        }
                        currentModal.find('.prepack-open').click(function(e){
                            e.preventDefault();
                            var spinnerStep = $(this).parent().prev().find('.spinner1').data('step');

                            var prepackHtml = '<div class="prepack-line no-init">' +
                                '<div class="prepack-item packs">'+
                                '<input type="text" class="input-mini spinner1 prepack" />'+
                                '<span>упаковок</span>'+
                                '</div>'+
                                '<div class="prepack-item">по</div>'+
                                '<div class="prepack-item">'+
                                '<input type="text" data-step="'+ spinnerStep +'" class="input-mini spinner1" />'+
                                '<span>'+ $(this).closest('.prepack-item').prev().find('span').text() +'</span>'+
                                '</div>'+
                                '<div class="prepack-item">'+
                                '<a href="#" class="close" title="Удалить">×</a>'+
                                '</div>'+
                                '</div>';
                            $(this).closest('.modal-footer').find('.prepack-list').append(prepackHtml);
                            var currentPrepackLine = $('.prepack-line.no-init');
                            var itsBasket;
                            ($(this).closest('tr').length == 0) ? itsBasket = 1 : itsBasket = 0;
                            spinnerModule.InitSpinner(currentPrepackLine.find('.prepack-item.packs .spinner1'), 1,itsBasket);
                            spinnerModule.InitSpinner(currentPrepackLine.find('.prepack-item:not(".packs") .spinner1'), spinnerStep,itsBasket,spinnerStep);
                            var productId = $(this).closest('tr').data('productid');

                            if ($(this).closest('tr').length == 0){
                                //если мы в корзине
                                // нужно сделать setOrderLine
                                var orderDetails = thriftModule.client.getOrderDetails(currentOrderId);
                                var orderLinesLength = orderDetails.odrerLines.length;
                                productId = $(this).closest('li').data('productid');
                                var packs,qnty;
                                var reCount = true;
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
                                var addedPackVal = currentPrepackLine.find('.packs .ace-spinner').spinner('value');
                                var addedQntyVal = parseFloat(currentPrepackLine.find('.prepack-item:not(".packs") .ace-spinner').spinner('value')).toFixed(1);
                                if (reCount) {
                                    productSelector.find('.error-prepack').hide();
                                    qnty += addedPackVal*addedQntyVal;
                                    packs[addedQntyVal] = addedPackVal;
                                    /*for(var p in packs){
                                     alert(p+" "+packs[p]);
                                     }*/
                                    thriftModule.client.setOrderLine(productId,qnty,'sdf',packs);
                                    productSelector.find('td>.ace-spinner').spinner('value',qnty);
                                    productSelector.find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));
                                    $('.itogo-right span').text(countAmount($('.catalog-order')));
                                }
                                productSelector.find('td .ace-spinner').spinner('disable');
                            }
                            spinnerModule.initRemovePrepackLine(currentPrepackLine.find('.prepack-item .close'),productId,productSelector);
                            currentPrepackLine.removeClass('no-init');

                            var oldHeight = $(this).closest('.modal').height();
                            $(this).closest('.modal').height(oldHeight + 53);

                        });
                        currentModal.find('.full-descr').click(function(){
                            var fullDescr = $('.product-fullDescr');
                            var oldHeight;
                            oldHeight = $(this).closest('.modal').height();
                            if(fullDescr.css('display') == 'none'){
                                $(this).closest('.modal').height(oldHeight + fullDescrHeight+10);
                                fullDescr.show(200);
                            }else{
                                //fullDescrHeight = $(this).closest('.modal').find('.product-fullDescr').height();
                                fullDescr.hide(200,function(){
                                    $(this).closest('.modal').height(oldHeight - fullDescrHeight-10);
                                });
                            }
                        });
                        if($(this).closest('.catalog-order').length > 0){
                            //если это popup для корзины
                            currentModal.find('.fa-shopping-cart').click(function(){
                                currentModal.modal('hide');
                            });
                        }
                    }else{
                        //если popup уже открывали
                        if ($(this).closest('tr').length == 0 || $(this).closest('.order-products').length > 0){
                            // если мы в корзине или на странице заказов
                            if (productDetails.prepackRequired){
                                isFirstModal = false;
                                spinnerModule.initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal);
                            }
                        }
                    }
                    currentModal.modal();
                    fullDescrHeight = currentModal.find('.product-fullDescr').height();
                    currentModal.find('.product-fullDescr').hide();

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

        function countAmount(sel){
            try{
                var summa = 0;
                sel.find('.td-summa').each(function(){
                    summa += parseFloat($(this).text());
                });
                var orderDetails = thriftModule.client.getOrderDetails(currentOrderId);
                summa += orderDetails.deliveryCost;
            }catch(e){
                //alert(e+" Функция countAmount");
            }
            return summa.toFixed(1);
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

                if (mainContent.height() > $(window).height()){
                    $('.shop-right').css('height', mainContent.height()+45);
                }else{
                    $('.shop-right').css('height', '100%');
                }
            }catch(e){
                alert(e+" Функция setSidebarHeight");
            }
        }

        $('.shop-trigger').click(function(e){
            e.preventDefault();
            try{
                var shopOrders = $('.shop-orders');
                var ordersList = $('.orders-list');

                if($(this).hasClass('back-to-shop')){
                    shopOrders.hide();
                    $('.shop-products').show(function(){
                        setSidebarHeight();
                    });
                }else{
                    if (!globalUserAuth){
                        callbacks.add(ordersModule.GoToOrdersTrigger);
                        //$('.modal-auth').modal();
                        openModalAuth();
                    }else{
                        $('.shop-products').hide();
                        var nowTime = parseInt(new Date().getTime()/1000);
                        nowTime -= nowTime%86400;
                        var day = 3600*24;
                        var orders = thriftModule.client.getOrders(0,nowTime+90*day);
                        ordersModule.initVarForMoreOrders();
                        // если всегда делать createOrdersHtml, то странциа заказов будет обновляться в реальном времени
                        // а так можно оптимизировать и не делать createOrderHtml каждый раз при перезагрузке
                        ordersList.html('').append(ordersModule.createOrdersHtml(orders));
                        InitProductDetailPopup($('.product-link'));
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
            }catch(e){
                alert(e+" Функция $('.shop-trigger').click");
            }
        });

        function openModalAuth(){
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

        return{
            getCookie: getCookie,
            setCookie: setCookie,
            initBasketInReload: initBasketInReload,
            InitProductDetailPopup: InitProductDetailPopup,
            countAmount: countAmount,
            getPacksLength: getPacksLength,
            setSidebarHeight: setSidebarHeight,
            openModalAuth: openModalAuth
        }
    }
);