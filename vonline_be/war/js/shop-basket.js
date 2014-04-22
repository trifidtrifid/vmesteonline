define(
    'shop-basket',
    ['jquery','shop-initThrift','shop-spinner','shop-common','shop-search','shop-orders'],
    function( $,thriftModule,spinnerModule,commonModule,searchModule,ordersModule ){


        function isValidPhone(myPhone) {
            //return /^\+\d{2}\(\d{3}\)\d{3}-\d{2}-\d{2}$/.test(myPhone);
            //return /^8\-(?:\(\d{4}\)\-\d{6}|\(\d{3}\)\-\d{3}\-\d{2}\-\d{2})$/.test(myPhone);
            //return /^((((\(\d{3}\))|(\d{3}-))\d{3}-\d{4})|(\+?\d{1,3}((-| |\.)(\(\d{1,4}\)(-| |\.|^)?)?\d{1,8}){1,5}))(( )?(x|ext)\d{1,5}){0,1}$/.test(myPhone);
            return /^(\+?\d+)?\s*(\(\d+\))?[\s-]*([\d-]*)$/.test(myPhone);
        }

        function cleanBasket(){
            //$('.catalog-order').html('');
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

        function InitDeleteProduct(selector){
            try{
                selector.click(function(e){
                    e.preventDefault();

                    var currentTab = $('.tab-pane.active');
                    var orderId = currentTab.data('orderid');
                    var currentProductList;
                    var inConfirm;

                    ($(this).closest('.confirm-order').length) ? inConfirm = true : inConfirm = false;
                    (inConfirm) ? currentProductList = $(this).closest('.catalog-confirm').find('li'):
                        currentProductList = selector.closest('.catalog-order').find('li');

                    $(this).closest('li').slideUp(function(){
                        var productId = $(this).data('productid');

                        $('.catalog').find('.added').each(function(){
                           if ($(this).data('productid') == productId){
                               $(this).removeClass('added');
                           }
                        });

                        var commonModule = require('shop-common');
                        if(inConfirm){
                            // если на странице конфирма, то обновляем и корзину на главной
                            $('.tabs-days .tab-content .catalog-order').find('li').each(function(){
                                if($(this).data('productid') == productId){
                                    $(this).css('display','none').detach();
                                }
                            });

                            $('.itogo-right span').text(commonModule.countAmount(currentTab.find('.catalog-order')));
                        }

                        $(this).detach();
                        currentTab.find('.amount span').text(commonModule.countAmount(currentTab.find('.catalog-order')));

                        if (currentProductList.length == 1){
                            // если это был последний товар в корзине
                            currentTab.closest('.tabs-days').hide().remove();
                            thriftModule.client.deleteOrder(orderId);
                        }
                    });


                    thriftModule.client.removeOrderLine(orderId,$(this).closest('li').data('productid'));

                    var orderDetails = thriftModule.client.getOrderDetails(orderId);
                    currentTab.find('.weight span').text(orderDetails.weightGramm);
                });
            }catch(e){
                alert(e+" Функция InitDeleteProduct");
            }
        }

        /*--------------------------------------*/
        /* addProduct */
        var flagFromBasketClick = 0;

        var callbacks = $.Callbacks();
        var selectorForCallbacks;

        function InitAddToBasket(selector){
            try{
                selector.click(function(e){
                    e.preventDefault();
                    var errorPrepack = false;
                    if (!globalUserAuth){
                        // если пользователь не залогинен
                        selectorForCallbacks = $(this);
                        //callbacks.add(BasketTrigger);
                        //$('.modal-auth').modal();
                        var commonModule = require('shop-common');
                        commonModule.openModalAuth();
                    }else{
                        // если пользователь залогинен
                        var currentProductSelector = $(this).closest('tr');
                        currentProductSelector.addClass('added');

                        var spinnerValue = currentProductSelector.find('td>.ace-spinner').spinner('value');
                        var currentProduct = {
                            id : currentProductSelector.data('productid'),
                            imageURL : currentProductSelector.find('.product-link img').attr('src'),
                            name : currentProductSelector.find('.product-link span span').text(),
                            price : currentProductSelector.find('.product-price').text(),
                            unitName :currentProductSelector.find('.unit-name').text(),
                            minClientPack :  currentProductSelector.find('td>.ace-spinner .spinner1').data('step'),
                            prepackLine : currentProductSelector.find('.prepack-line'),
                            qnty : spinnerValue,
                            packVal : 1,
                            quantVal :  currentProductSelector.find('td .spinner1').data('step')
                        };
                        var productDetails = thriftModule.client.getProductDetails(currentProduct.id);
                        var packs = [];
                        if (productDetails.prepackRequired){
                            // если это товар с prepackRequired
                            currentProduct.quantVal = spinnerValue;

                            if (currentProductSelector.find('.modal-body').length > 0){
                                // если пользватель открывал модальное окно(в таблице продуктов) с инфой о продукте
                                var packVal = currentProductSelector.find('.packs:eq(0) .ace-spinner').spinner('value');
                                var quantVal = currentProductSelector.find('.prepack-item:not(".packs") .ace-spinner').eq(0).spinner('value');
                                currentProduct.packVal = packVal;
                                currentProduct.quantVal = quantVal;
                                currentProduct.qnty = packVal*quantVal;
                                packs[quantVal] = packVal; // если только одна линия с упаковкой

                                if(currentProduct.prepackLine.length != 0){
                                    // если линий более чем одна
                                    var oldQuantVal = 0;
                                    var firstQuantVal = $('.modal-footer.with-prepack>.prepack-item:not(".packs") .ace-spinner').spinner('value');
                                    currentProduct.prepackLine.each(function(){
                                        packVal = $(this).find('.packs .ace-spinner').spinner('value');
                                        quantVal = $(this).find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
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
                                if($(this).closest('.order-item').length > 0){
                                    // если мы на странице истории заказов
                                    // (то нужно вытащить packs из заказа)
                                    var orderId = $(this).closest('.order-item').data('orderid');
                                    var orderDetails = thriftModule.client.getOrderDetails(orderId);
                                    var orderLines = orderDetails.odrerLines;
                                    var orderLinesLength = orderLines.length;
                                    for(var i = 0; i < orderLinesLength; i++){
                                        if(orderLines[i].product.id == $(this).closest('tr').data('productid')){
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
                        /*for(var p in packs){
                         alert(p+" "+packs[p]);
                         }*/
                        if (!errorPrepack){
                            if ($('.tabs-days').length == 0){
                             // если это первый товар в корзине
                                var nextDate = getNextDate();
                                var nextDateStr = new Date(nextDate*1000);
                                orderId = thriftModule.client.createOrder(nextDate,'asd',0);
                                addTabToBasketHtml(nextDateStr,orderId);
                             }
                            //else{
                            // если в корзине уже что-то есть
                            AddProductToBasketCommon(currentProduct,packs);
                            //}
                        }

                        if ($(this).closest('.modal').length>0 && !errorPrepack){
                            $(this).closest('.modal').modal('hide');
                            currentProductSelector.find('.error-prepack').hide();
                        }
                    }
                });
            }catch(e){
                alert(e+" Функция InitAddToBasket");
            }
        }

        /*function initCancel(selector){
            selector.find('.btn-cancel').click(function(e){
                e.preventDefault();
                var quest = confirm('Вы действительно хотите отменить заказ ?');
                if (quest){
                    cleanBasket();
                    var orderId = $('.tab-pane.active').data('orderid');
                    thriftModule.client.cancelOrder(orderId);
                }
            });
        }*/

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

        function addTabToBasketHtml(nextDateStr,orderId){

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
                    '<a data-toggle="tab" href="#day'+orderDay+orderMonth+'">'+
                    orderWeekDay+
                    '<span>'+orderDay+'.'+orderMonth+'</span>'+
                    '</a>'+
                    '</li>'+
                    '</ul>'+
                    '<div class="tab-content">'+
                    '<div id="day'+orderDay+orderMonth+'" data-orderid="'+ orderId +'" class="tab-pane active">'+
                    '<div class="basket-head">'+
                        '<div class="weight">Вес: <span></span> гр.</div>'+
                        '<div class="amount">Итого: <span></span> руб.</div>'+
                    '</div>'+
                    '<ul class="catalog-order">'+
                    '</ul>'+
                    '<div class="basket-bottom">'+
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
                    '<a data-toggle="tab" href="#day'+orderId+'">'+
                    orderWeekDay+
                    '<span>'+orderDay+'.'+orderMonth+'</span>'+
                    '</a>'+
                    '</li>';
                //alert('1 '+tabDays.length);
                tabDays.find('.nav-tabs').append(html);
                html = '<div id="day'+orderId+'" data-orderid="'+ orderId +'" class="tab-pane active">'+
                    '<div class="basket-head">'+
                    '<div class="amount">Итого: <span></span></div>'+
                    '</div>'+
                    '<ul class="catalog-order">'+
                    '</ul>'+
                    '<div class="basket-bottom">'+
                    '<a href="#" class="btn btn-sm btn-primary no-border btn-order">Оформить</a>'+
                    '<a href="#" class="btn btn-sm btn-cancel no-border">Отменить</a>'+
                    '</div>'+
                    '</div>';
                tabDays.find('.tab-content').append(html);
            }

            var activeOrder = $('.tabs-days .tab-pane.active');

            activeOrder.find('.btn-cancel').click(function(e){
                e.preventDefault();

                var quest = confirm('Вы действительно хотите отменить заказ ?');
                if (quest){
                    var orderId = $('.tab-pane.active').data('orderid');
                    cleanBasket();
                    thriftModule.client.cancelOrder(orderId);
                }
            });

            activeOrder.find('.btn-order').click(function(e){
                e.preventDefault();

                var currentTab = $('.tab-pane.active');
                var commonModule = require('shop-common');
                var amount = commonModule.countAmount(currentTab.find('.catalog-order'));
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

                GoToConfirm(catalogHtml,amount,spinnerValue,date);

            });
        }

        function GoToConfirm(catalogHtml,amount,spinnerValue,date){

            $('.shop-confirm').load('ajax/ajax-confirmOrder.html .dynamic',function(){
                var myDate;
                $('.main-container').css('min-height', $(window).height()-45);

                (typeof date == 'string') ? myDate = date : myDate = date.orderDay+'.'+date.orderMonth+' ('+ date.orderWeekDay +')';
                $('.order-date span').text(myDate);
                $('.itogo-right span').text(amount);

                var confirmOrder = $('.confirm-order .catalog-confirm');
                confirmOrder.html(catalogHtml);

                // на случай если в корзине уже открывали попап, удаляем его, чтобы не
                // вносить путаницу
                confirmOrder.find('.modal-body').remove();

                var counter = 0;

                var userPhone = thriftModule.userClient.getUserContacts().mobilePhone;
                if(userPhone){
                    $('#phone-delivery').val(userPhone);
                }

                confirmOrder.find('td .ace-spinner').each(function(){
                    var step = $(this).find('.spinner1').data('step');
                    $(this).after('<input type="text" data-step="'+ step +'" class="input-mini spinner1 spinner-input form-control no-init" maxlength="3">');
                    spinnerModule.InitSpinner($(this).find('+.spinner1'),spinnerValue[counter++],1,step);
                    if ($(this).find('.spinner1').attr('disabled')) { $(this).find('+.ace-spinner').spinner('disable')}
                    $(this).remove();
                });

                InitDeleteProduct(confirmOrder.find('.delete-product'));
                var commonModule = require('shop-common');
                commonModule.InitProductDetailPopup($('.product-link'));

                var shops = thriftModule.client.getShops();
                var shop = thriftModule.client.getShop(shops[0].id);
                var shopAddress = shop.address;
                SetShopAddress(shopAddress);
                initRadioBtnClick(shopAddress);

                $('.confirm-order .btn-order').click(function(){
                    var phoneDelivery = $('#phone-delivery');
                    var alertDeliveryPhone = $('.alert-delivery-phone');
                    if(!phoneDelivery.val()){

                        alertDeliveryPhone.text('Введите номер телефона !').show();

                    }else if(!$('.input-delivery .delivery-address .error-info').length){

                        var userContacts = thriftModule.userClient.getUserContacts();
                        userContacts.mobilePhone = phoneDelivery.val();
                        var haveError = 0;

                        try{
                            thriftModule.userClient.updateUserContacts(userContacts);
                        }catch(e){
                            haveError = 1;
                            alertDeliveryPhone.text('Телефон должен быть вида 79219876543, +7(821)1234567 и т.п').show();
                        }

                        if(!haveError){
                            alertDeliveryPhone.hide();
                            var orderId = $('.tab-pane.active').data('orderid');
                            thriftModule.client.confirmOrder(orderId);
                            //alert('Ваш заказ принят !');
                            cleanBasket();
                            $('.shop-orderEnd').load('ajax/ajax-orderEnd.html .dynamic',function(){
                                $('.page').hide();
                                $(this).show();
                                $('.main-container').css('min-height', $(window).height()-45);

                                var order = [];
                                order[0] = thriftModule.client.getOrder(orderId);
                                $('.order-end-info').append(ordersModule.createOrdersHtml(order));
                            });
                        }
                    }
                });

                $('.confirm-order .btn-cancel').click(function(){
                    $('.back-to-shop').trigger('click');
                });

            }).show();
        }

        function SetShopAddress(shopAddress){
            $('.input-delivery .delivery-address').text(shopAddress.country.name + ", " + shopAddress.city.name + ", "
                + shopAddress.street.name + " " + shopAddress.building.fullNo + ", кв. " + shopAddress.flatNo);

            $('.delivery-cost').text('0');
        }

        function AddProductToBasketCommon(currentProduct,packs){
            var addedProductFlag = 0;
            var currentTab = $('.tab-pane.active');
            var orderId = currentTab.data('orderid');
            var commonModule = require('shop-common');

            $('.catalog-order li').each(function(){
                if ($(this).data('productid') == currentProduct.id){
                    addedProductFlag = 1;
                    /*if(packs){
                     $(this).find('td>.ace-spinner').spinner('disable');
                     }*/
                }
            });
            if (addedProductFlag){
                // если такой товар уже есть
                var basketProductSelector = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]');
                var currentSpinner = basketProductSelector.find('td>.ace-spinner');
                //var newSpinnerVal = currentSpinner.spinner('value')+currentProduct.qnty;
                var newSpinnerVal = currentProduct.qnty;
                currentSpinner.spinner('value',newSpinnerVal);
                /* здесь комментарии хранят код, к-й добавляет добавляемое кол-во к тому, что уже есть в корзине,
                 а не заменяет
                 */
                var newPacks;
                /*            if (packs){
                 var orderDetails = thriftModule.client.getOrderDetails(currentOrderId);
                 var orderLines = orderDetails.odrerLines;
                 var orderLinesLength = orderLines.length;
                 var orderPacks;
                 for(var i = 0; i < orderLinesLength; i++){
                 if (orderLines[i].product.id == currentProduct.id){
                 orderPacks = orderLines[i].packs;
                 }
                 }
                 var oldPacksQnty,newPacksQnty;

                 if(currentProduct.prepackLine.length != 0){
                 // если prepackLine не одна
                 currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('td>.ace-spinner');
                 currentSpinner.spinner('disable');
                 newPacks = orderPacks;
                 for(var p1 in packs){
                 oldPacksQnty = 0;
                 for (var p2 in orderPacks){
                 if(parseFloat(p1).toFixed(1) == parseFloat(p2).toFixed(1)){
                 // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                 oldPacksQnty = orderPacks[p2];
                 newPacksQnty = oldPacksQnty + packs[p1];
                 newPacks[p2] = newPacksQnty;
                 break;
                 }
                 }
                 if(!oldPacksQnty){
                 newPacks[p1] = packs[p1];
                 }
                 }
                 }else{
                 // если одна линия
                 if(getPacksLength(orderPacks) == 1){
                 // если у этого товара в заказе только одна линия
                 for(var p in orderPacks){
                 // т.к линия одна у цикла будет только один проход
                 newPacks = orderPacks;
                 //alert(p+" "+currentProduct.quantVal);
                 if(p == currentProduct.quantVal){
                 // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                 oldPacksQnty = orderPacks[p];
                 newPacksQnty = oldPacksQnty + currentProduct.packVal;
                 newPacks[p] = newPacksQnty;
                 }else{
                 // если в корзине товар, где prepackLine с другим кол-м товара (то меняем packs, добавляя новую линию prepackLine)
                 newPacks[currentProduct.quantVal] = currentProduct.packVal;
                 }
                 }
                 }else{
                 // если у этого товара в заказе несколько линий
                 newPacks = orderPacks;
                 for(var p in orderPacks){
                 //alert(p+" "+currentProduct.quantVal);
                 if(p == currentProduct.quantVal){
                 // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                 oldPacksQnty = orderPacks[p];
                 newPacksQnty = oldPacksQnty + currentProduct.packVal;
                 newPacks[p] = newPacksQnty;
                 //alert('4 '+ oldPacksQnty+" "+currentProduct.packVal+" "+newPacksQnty );
                 break;
                 }
                 }
                 if(!oldPacksQnty){
                 newPacks[currentProduct.quantVal] = currentProduct.packVal;
                 }
                 }
                 }

                 }*/

                (commonModule.getPacksLength(packs) <= 1) ? currentSpinner.spinner('enable'):currentSpinner.spinner('disable');

                thriftModule.client.setOrderLine(orderId,currentProduct.id,newSpinnerVal,'sdf',packs);

                var orderDetails = thriftModule.client.getOrderDetails(orderId);
                currentTab.find('.weight span').text(orderDetails.weightGramm);

                var newSumma = (newSpinnerVal*parseFloat(basketProductSelector.find('.td-price').text())).toFixed(1);
                basketProductSelector.find('.td-summa').text(newSumma);
                currentTab.find('.amount span').text(commonModule.countAmount(currentTab.find('.catalog-order')));
                //currentTab.find('.amount span').text(commonModule.countAmount(currentTab.find('.catalog-order')));
            }else{
                // если такого товара еще нет
                AddSingleProductToBasket(currentProduct,currentProduct.qnty);

                thriftModule.client.setOrderLine(orderId,currentProduct.id,currentProduct.qnty,'sdf',packs);

                orderDetails = thriftModule.client.getOrderDetails(orderId);
                currentTab.find('.weight span').text(orderDetails.weightGramm);


                if(currentProduct.prepackLine.length != 0 || (packs && commonModule.getPacksLength(packs) > 1)){
                    currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('td>.ace-spinner');
                    currentSpinner.spinner('disable');
                }
            }
        }

        function AddSingleProductToBasket(currentProduct,spinnerValue,spinnerDisable){
            try{
                //var productDetails = thriftModule.client.getProductDetails(currentProduct.id);
                var myPic;
                var commonModule = require('shop-common');
                (currentProduct.imageURL) ? myPic = currentProduct.imageURL : myPic = commonModule.noPhotoPic;

                var productHtml = '<li data-productid="'+ currentProduct.id +'">'+
                    '<table>'+
                    '<tr>'+
                    '<td class="td-price product-price">'+ currentProduct.price +'</td>'+
                    '<td class="td-spinner"><input type="text" data-step="'+ currentProduct.minClientPack +'" class="input-mini spinner1 no-init" /><span class="unit-name">'+ currentProduct.unitName +'</span></td>'+
                    '<td class="td-summa">'+ (currentProduct.price*spinnerValue).toFixed(1) +'</td>'+
                    '<td class="td-close"><a href="#" class="delete-product no-init">×</a></td>'+
                    '</tr>'+
                    '</table>'+
                    '<a href="#" class="product-link no-init">'+
                    '<span><img src="'+ myPic +'" alt="картинка"/></span>'+
                    '<div class="product-right-descr">'+
                    currentProduct.name+
                    '</div>'+
                    '</a>'+
                    '<div class="modal">'+
                    '</div>'+
                    '</li>';
            }catch(e){
                alert(e+" Функция AddSingleProductToBasket");
            }

            var currentTab = $('.tab-pane.active');
            var catalogOrder = currentTab.find('.catalog-order');
            catalogOrder.append(productHtml);
            currentTab.find('.amount span').text(commonModule.countAmount(catalogOrder));

            var deleteNoInit = $('.catalog-order .delete-product.no-init');
            InitDeleteProduct(deleteNoInit);
            deleteNoInit.removeClass('no-init');

            var popupNoInit = $('.catalog-order .product-link.no-init');
            commonModule.InitProductDetailPopup(popupNoInit);
            popupNoInit.removeClass('no-init');

            var spinnerNoInit = $('.catalog-order .spinner1.no-init');
            var itsBasket = 1;
            spinnerModule.InitSpinner(spinnerNoInit,spinnerValue,itsBasket,currentProduct.minClientPack);
            if (spinnerDisable){spinnerNoInit.closest('.ace-spinner').spinner('disable');}
            spinnerNoInit.removeClass('no-init');
        }

        function BasketTrigger(selector){
            selector.trigger('click');
        }

        function getNextDate(){
            try{
                var day = 3600*24;
                var now = parseInt(new Date()/1000);
                now -= now%86400;

                var datesArray = thriftModule.client.getDates(now+day,now+32*day);
                var nextDate;
                for (var date in datesArray){
                    if(datesArray[date] == 1){
                      // значит это ближайшая дата
                        nextDate = date;
                        break;
                    }
                    // если это следующий день то нельзя
                    /*var firstMarkDay = $('.mark-day:eq(0)');
                    if (!firstMarkDay.hasClass('.closed-day')){
                        // если ближайший к сегодня marked day является не closed-day, то очищаем его
                        // если же ближайший close day, то все остается как есть
                        firstMarkDay.removeClass('free-day day-with-order special-day');
                    }*/
                }
            }catch(e){
                alert(e + ' Функция SetFreeDates');
            }
            return nextDate;
        }

        /* delivery */
        var autocompleteAddressFlag = 1;
        var triggerDelivery = 0;

        function writeAddress(address){
            $('.input-delivery .delivery-address').text(address.country.name + ", " + address.city.name + ", "
                + address.street.name + " " + address.building.fullNo + ", кв. " + address.flatNo);
        }

        function setDeliveryDropdown(orderId,userAddresses){

            var addresses;
            (userAddresses) ? addresses = userAddresses : addresses = thriftModule.userClient.getUserAddresses();

            var userAddressesHtml = "";
            var userAddressesLength = addresses.length;
            for(var i = 0; i < userAddressesLength; i++){
                userAddressesHtml += '<li><a href="#">'+
                    addresses[i].country.name+", "+addresses[i].city.name+", "+addresses[i].street.name+" "+addresses[i].building.fullNo+", кв. "+addresses[i].flatNo+
                    '</a></li>';
            }
            userAddressesHtml += '<li class="divider"></li>'+
                '<li><a href="#" class="delivery-add-address">Добавить адрес ...</a></li>';

            $('.delivery-dropdown .dropdown-menu').html('').prepend(userAddressesHtml);
            $('.delivery-dropdown .dropdown-menu a:not(".delivery-add-address")').click(function(e){
                e.preventDefault();
                var ind = $(this).parent().index();

                writeAddress(addresses[ind]);
                thriftModule.client.setOrderDeliveryType(orderId,2,addresses[ind]);
                setDeliveryCost(orderId);
            });

            $('.delivery-add-address').click(function(e){
                e.preventDefault();
                $('.address-input .street-delivery').val('');
                $('.address-input .building-delivery').val('');
                $('.address-input .flat-delivery').val('');
                $('.address-input').show();
                $('.delivery-dropdown .btn-group-text').text('Выбрать адрес');
            });
        }

        function initRadioBtnClick(shopAddress){
            $('.radio input').click(function(){
                var itogoRight = $('.itogo-right span');
                var orderDetails = 0;
                var orderId = $('.tab-pane.active').data('orderid');
                var commonModule = require('shop-common');

                if ($(this).hasClass('courier-delivery')){
                    //если доставка курьером

                    var homeAddress = thriftModule.userClient.getUserContacts().homeAddress;
                    var userAddresses = thriftModule.userClient.getUserAddresses();
                    var myAddress;
                    (homeAddress) ? myAddress = homeAddress : myAddress = userAddresses[0];


                    if(myAddress){
                        $('.input-delivery .delivery-address').text(myAddress.country.name + ", " + myAddress.city.name + ", "
                            + myAddress.street.name + " " + myAddress.building.fullNo + ", кв. " + myAddress.flatNo);

                        thriftModule.client.setOrderDeliveryType(orderId,2,myAddress);
                        setDeliveryCost(orderId);

                    }else{
                        $('.input-delivery .delivery-address').html("<span class='error-info'>У вас не указано ни одного адреса доставки.</span>");
                        $('.input-delivery .delivery-address .error-info').show();

                    }

                    itogoRight.text(commonModule.countAmount($('.confirm-order')));


                    $('.delivery-dropdown').show();

                    $('.delivery-dropdown').click(function(){
                        $(this).addClass('open');
                    });

                    triggerDelivery = 1;

                    if (autocompleteAddressFlag){
                        var searchModule = require('shop-search');
                        searchModule.initAutocompleteAddress($('.address-input'));
                        autocompleteAddressFlag = 0;
                    }

                    if(userAddresses.length > 0){
                        homeAddress = thriftModule.userClient.getUserContacts().homeAddress;
                        if(homeAddress){
                            writeAddress(homeAddress);
                        }
                    }
                    setDeliveryDropdown(orderId,userAddresses);


                    $('.add-address').click(function(e){
                        e.preventDefault();

                        if (!$('.country-delivery').val() || !$('.city-delivery').val() || !$('.street-delivery').val() || !$('.building-delivery').val() || !$('.flat-delivery').val()){
                            $('.alert-delivery-phone').hide();
                            $('.alert-delivery-addr').text('Введите полный адресс доставки !').show();
                        }else{
                            $('.alert-delivery-addr').hide();
                            var addressInput = $('.address-input');
                            addressInput.slideUp();

                            // добавление в базу нового города, страны, улицы и т.д (если курьером)

                            var deliveryAddress = commonModule.addAddressToBase(addressInput);
                            writeAddress(deliveryAddress);

                            thriftModule.userClient.addUserAddress(deliveryAddress);

                            thriftModule.client.setOrderDeliveryType(orderId,2,deliveryAddress);
                            setDeliveryCost(orderId);
                            setDeliveryDropdown(orderId);
                        }

                    });

                    /*$(this).closest('.delivery-right').find('.input-delivery').addClass('active').slideDown();
                    orderDetails = thriftModule.client.getOrderDetails(orderId);
                    if (orderDetails.deliveryCost){
                        $('.delivery-cost').text(orderDetails.deliveryCost);
                    }
                    var commonModule = require('shop-common');
                    itogoRight.text(commonModule.countAmount($('.confirm-order')));

                    //thriftModule.client.setOrderDeliveryType(orderId,2);
                    triggerDelivery = 1;*/
                }else{
                    thriftModule.client.setOrderDeliveryType(orderId,1);

                    //$(this).closest('.delivery-right').find('.input-delivery').removeClass('active').slideUp();
                    SetShopAddress(shopAddress);
                    $('.delivery-dropdown').hide();
                    if (triggerDelivery){itogoRight.text(commonModule.countAmount($('.confirm-order'))); triggerDelivery = 0;}
                }
            });
        }

        function setDeliveryCost(orderId){
            var orderDetails = thriftModule.client.getOrderDetails(orderId);
            if (orderDetails.deliveryCost){
                $('.delivery-cost').text(orderDetails.deliveryCost);
            }
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
            GoToConfirm: GoToConfirm,
            getWeekDay: getWeekDay,
            getNextDate: getNextDate,
            callbacks: callbacks,
            selectorForCallbacks: selectorForCallbacks
        }

    }
);