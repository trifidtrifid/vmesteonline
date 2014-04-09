define(
    'shop-addProduct2',
    ['jquery','shop-initThrift','shop-common','shop-orders','initDatepicker','shop-basket','shop-spinner'],
    function( $,thriftModule,commonModule,ordersModule,datepickerModule,basketModule,spinnerModule ){

        alert('addProduct2 '+ commonModule+" "+ordersModule+" "+datepickerModule+" "+basketModule+" "+spinnerModule);
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
            basketModule.InitDeleteProduct(deleteNoInit);
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
            flagFromBasketClick: flagFromBasketClick,
            callbacks: callbacks,
            AddProductToBasketCommon: AddProductToBasketCommon,
            InitAddToBasket: InitAddToBasket,
            BasketTrigger: BasketTrigger,
            AddSingleProductToBasket: AddSingleProductToBasket
        }
    }
);