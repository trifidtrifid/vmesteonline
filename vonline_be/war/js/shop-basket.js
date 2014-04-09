define(
    'shop-basket',
    ['jquery','shop-initThrift','shop-spinner','shop-common','shop-orders','initDatepicker'],
    function( $,thriftModule,spinnerModule,commonModule,ordersModule,datePickerModule ){

        function isValidPhone(myPhone) {
            //return /^\+\d{2}\(\d{3}\)\d{3}-\d{2}-\d{2}$/.test(myPhone);
            //return /^8\-(?:\(\d{4}\)\-\d{6}|\(\d{3}\)\-\d{3}\-\d{2}\-\d{2})$/.test(myPhone);
            //return /^((((\(\d{3}\))|(\d{3}-))\d{3}-\d{4})|(\+?\d{1,3}((-| |\.)(\(\d{1,4}\)(-| |\.|^)?)?\d{1,8}){1,5}))(( )?(x|ext)\d{1,5}){0,1}$/.test(myPhone);
            return /^(\+?\d+)?\s*(\(\d+\))?[\s-]*([\d-]*)$/.test(myPhone);
        }

        $('.btn-order').click(function(){
            try{
                var inputDelivery = $('.input-delivery');
                var phoneDelivery = $('#phone-delivery');
                if(inputDelivery.hasClass('active') && !phoneDelivery.val()){
                    $('.alert-delivery-phone').text('Введите номер телефона !').show();
                }else if(!isValidPhone(phoneDelivery.val())){
                    $('.alert-delivery-phone').text('Не корректный номер телефона !').show();
                }else if (inputDelivery.hasClass('active') && (!$('#country-delivery').val() || !$('#city-delivery').val() || !$('#street-delivery').val() || !$('#building-delivery').val() || !$('#flat-delivery').val())){
                    $('.alert-delivery-phone').hide();
                    $('.alert-delivery-addr').show();
                }else{
                    $('.alert-delivery-addr').hide();
                    $('.alert-delivery-phone').hide();
                    var popup = $('.modal-order-end');
                    popup.modal();
                    var orderList = $('.catalog-order>li');
                    var productsHtmlModal = "";
                    var i = 0;
                    var spinnerValue = [];
                    orderList.each(function(){
                        spinnerValue[i++] = $(this).find('td>.ace-spinner').spinner('value');
                        var productDetails = thriftModule.client.getProductDetails($(this).data('productid'));
                        var disableClass;
                        (productDetails.prepackRequired)? disableClass='class="prepack-disable"': disableClass='';
                        productsHtmlModal+= '<tr data-productid="'+ $(this).data('productid') +'">'+
                            '<td>'+
                            '<div>'+
                            '<img src="'+ $(this).find('img').attr('src') +'" alt="картинка"/>'+
                            '<span>'+ $(this).find('.product-right-descr').text() +'</span>'+
                            '</div>'+
                            '</td>'+
                            '<td class="td-price">'+ $(this).find('.td-price').text()  +'</td>'+
                            '<td '+ disableClass +'>'+
                            '<input type="text" data-step="'+ $(this).find('td .spinner1').data('step') +'" class="input-mini spinner1 no-init" />'+
                            '</td>'+
                            '<td>'+ $(this).find('td .unit-name').text() +'</td>'+
                            '<td class="td-summa">'+ $(this).find('.td-summa').text()+
                            '</td>'+
                            '</tr>';
                    });
                    popup.find('.modal-body-list tbody').html('').append(productsHtmlModal);

                    if(inputDelivery.hasClass('active')){
                        popup.find('.modal-footer').before('<div class="delivery-in-modal">Стоимость доставки: <span class="delivery-cost">'+ inputDelivery.find('.delivery-cost').text() +'</span> руб</div>');
                    }else{
                        popup.find('.delivery-in-modal').hide();
                    }

                    $('.modal-itogo span').text($('.itogo-right span').text());

                    var spinnerNoInit = popup.find('.spinner1.no-init');
                    i = 0;
                    spinnerNoInit.each(function(){
                        spinnerModule.InitSpinner($(this),spinnerValue[i++],0,$(this).data('step'));
                    });
                    spinnerNoInit.removeClass('no-init');
                    $('.prepack-disable').find('.ace-spinner').spinner('disable');

                    popup.find('.btn-order').click(function(){
                        // добавление в базу нового города, страны, улицы и т.д (если курьером)
                        if ($('.input-delivery').hasClass('active')){
                            var countries = thriftModule.userClient.getCounties();
                            var countriesLength = countries.length;
                            var inputCountry = $('#country-delivery').val();
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
                            var inputCity = $('#city-delivery').val();
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
                            var inputStreet = $('#street-delivery').val();
                            var street,streetId = 0;
                            for (i = 0; i < streetsLength; i++){
                                if (streets[i].name == inputCity){
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
                            var inputBuilding = $('#building-delivery').val();
                            var building,buildingId = 0;
                            for (i = 0; i < buildingsLength; i++){
                                if (buildings[i].fullNo == inputBuilding){
                                    building = buildings[i];
                                    buildingId = building.id;
                                }
                            }
                            if (!buildingId){
                                building = thriftModule.userClient.createNewBuilding(streetId,inputBuilding,0,0);
                                buildingId = building.id;
                            }


                            // передаем адресс доставки
                            //console.log(country.id+" "+city.id+" "+street.id+" "+building.id+" "+$('#flat-delivery').val()+" "+$('#order-comment').val());
                            /*var deliveryAddress = new com.vmesteonline.be.PostalAddress(
                             country : country,
                             city : city,
                             street : street,
                             building : building,
                             staircase : 0,
                             floor: 0,
                             flatNo: parseInt($('#flat-delivery').val()),
                             comment: $('#order-comment').val()
                             };*/
                            var deliveryAddress = new com.vmesteonline.be.PostalAddress();
                            deliveryAddress.country = country;
                            deliveryAddress.city = city;
                            deliveryAddress.street = street;
                            deliveryAddress.building = building;
                            deliveryAddress.staircase = 0;
                            deliveryAddress.floor= 0;
                            deliveryAddress.flatNo = parseInt($('#flat-delivery').val());
                            deliveryAddress.comment = $('#order-comment').val();

                            thriftModule.client.setOrderDeliveryAddress(deliveryAddress);
                        }

                        // сохранение телефона
                        var userContacts = thriftModule.userClient.getUserContacts();
                        userContacts.mobilePhone = $('#phone-delivery').val();
                        thriftModule.userClient.updateUserContacts(userContacts);

                        thriftModule.client.confirmOrder();
                        alert('Ваш заказ принят !');
                        $('.modal-order-end').modal('hide');
                        cleanBasket();
                    })
                }
            }catch(e){
                alert(e+" Функция $('.btn-order').click");
            }
        });

        $('.btn-cancel').click(function(){
            var quest = confirm('Вы действительно хотите отменить заказ ? Ваша корзина вновь станет пустой.');
            if (quest){
                cleanBasket();
                thriftModule.client.cancelOrder();
            }
        });

        function cleanBasket(){
            $('.additionally-order').addClass('hide');
            $('.empty-basket').removeClass('hide');
            $('.catalog-order').html('');
        }

        function InitDeleteProduct(selector){
            try{
                selector.click(function(){
                    $(this).closest('li').slideUp(function(){
                        $(this).detach();
                        $('.itogo-right span').text(commonModule.countAmount($('.catalog-order')));
                        if ($('.catalog-order li').length == 0){
                            $('.additionally-order').addClass('hide');
                            $('.empty-basket').removeClass('hide');
                            thriftModule.client.deleteOrder();
                        }
                    });
                    thriftModule.client.removeOrderLine($(this).closest('li').data('productid'));
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

        function AddProductToBasketCommon(currentProduct,packs){
            var addedProductFlag = 0;
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

                thriftModule.client.setOrderLine(currentProduct.id,newSpinnerVal,'sdf',packs);
                var newSumma = (newSpinnerVal*parseFloat(basketProductSelector.find('.td-price').text())).toFixed(1);
                basketProductSelector.find('.td-summa').text(newSumma);
                $('.itogo-right span').text(commonModule.countAmount($('.catalog-order')));
            }else{
                // если такого товара еще нет
                AddSingleProductToBasket(currentProduct,currentProduct.qnty);

                thriftModule.client.setOrderLine(currentProduct.id,currentProduct.qnty,'sdf',packs);
                if(currentProduct.prepackLine.length != 0 || (packs && commonModule.getPacksLength(packs) > 0)){
                    currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('td>.ace-spinner');
                    currentSpinner.spinner('disable');
                }
            }
        }

        function InitAddToBasket(selector){
            try{
                selector.click(function(e){
                    e.preventDefault();
                    var errorPrepack = false;
                    if (!globalUserAuth){
                        // если пользователь не залогинен
                        selectorForCallbacks = $(this);
                        callbacks.add(BasketTrigger);
                        //$('.modal-auth').modal();
                        commonModule.openModalAuth();
                    }else{
                        // если пользователь залогинен
                        var currentProductSelector = $(this).closest('tr');
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
                            if ($('.additionally-order').hasClass('hide')){
                                // если это первый товар в корзине
                                flagFromBasketClick = 1;
                                datepickerModule.dPicker.datepicker('setVarFreeDays',currentProduct, currentProduct.qnty,0,packs,AddSingleProductToBasket,ordersModule.AddOrdersToBasket,AddProductToBasketCommon);
                                datepickerModule.dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click');
                            }else{
                                // если в корзине уже что-то есть
                                AddProductToBasketCommon(currentProduct,packs)
                            }
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

        function BasketTrigger(selector){
            selector.trigger('click');
        }

        function AddSingleProductToBasket(currentProduct,spinnerValue,spinnerDisable){
            try{
                var productDetails = thriftModule.client.getProductDetails(currentProduct.id);
                var productHtml = '<li data-productid="'+ currentProduct.id +'">'+
                    '<table>'+
                    '<tr>'+
                    '<td class="td-price product-price">'+ currentProduct.price +'</td>'+
                    '<td><input type="text" data-step="'+ currentProduct.minClientPack +'" class="input-mini spinner1 no-init" /><span class="unit-name">'+ currentProduct.unitName +'</span></td>'+
                    '<td class="td-summa">'+ (currentProduct.price*spinnerValue).toFixed(1) +'</td>'+
                    '<td><a href="#" class="delete-product no-init">×</a></td>'+
                    '</tr>'+
                    '</table>'+
                    '<a href="#" class="product-link no-init">'+
                    '<span><img src="'+ currentProduct.imageURL +'" alt="картинка"/></span>'+
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

            var catalogOrder = $('.catalog-order');
            catalogOrder.append(productHtml);
            $('.itogo-right span').text(commonModule.countAmount(catalogOrder));

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

        return{
            cleanBasket: cleanBasket,
            InitDeleteProduct: InitDeleteProduct,
            flagFromBasketClick: flagFromBasketClick,
            AddProductToBasketCommon: AddProductToBasketCommon,
            InitAddToBasket: InitAddToBasket,
            BasketTrigger: BasketTrigger,
            AddSingleProductToBasket: AddSingleProductToBasket
        }

    }
);