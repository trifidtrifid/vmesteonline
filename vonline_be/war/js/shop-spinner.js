define(
    'shop-spinner',
    ['jquery','ace_spinner','shop-initThrift','shop-common','shop-basket'],
    function( $,aceSpinner,thriftModule,commonModule,basketModule ){

        function InitSpinner(selector,spinnerValue,isBasket,spinnerStep){
            try{
                var step = 1;
                if (spinnerStep){step = spinnerStep}
                // делаем +spinnerValue для строгого преобразования к числу
                selector.ace_spinner({value:+spinnerValue,min:step,max:100000,step:step,on_sides: true,hold:false, btn_up_class:'btn-info' , btn_down_class:'btn-info'});
            }catch(e){
                alert(e+" Функция InitSpinner");
            }
            var isConfirm = selector.closest('.catalog-confirm').length > 0;
            if (isConfirm){
                InitSpinnerChangeInFinal(selector);
            }else{
                if (isBasket){
                    InitSpinnerChangeInBasket(selector);
                }else{
                    InitSpinnerChange(selector);
                }
            }
        }

        function InitSpinnerChangeInFinal(selector){
            var oldSpinnerValue = selector.data('step');

            selector.on('focusout',function(){

                if($(this).val() != oldSpinnerValue){
                    $(this).trigger('change');
                }

            });

            selector.on('change',function(e){

                // действия для окргуления и избежания неправильного ввода (0 например)
                var currentValue = $(this).closest('.ace-spinner').spinner('value');
                if (currentValue == 0 || currentValue === undefined){
                    currentValue = ($(this).data('step')) ? $(this).data('step') : 1;
                }
                if(currentValue) currentValue = parseFloat(currentValue).toFixed(1);
                $(this).closest('.ace-spinner').spinner('value',currentValue);

                if (oldSpinnerValue != currentValue){
                    // чтобы не обрабатывать щелчки ниже 1
                oldSpinnerValue = currentValue;

                var productSelector = $(this).closest('.product');
                var price = productSelector.find('.td-price').text();
                var orderId = $('.tab-pane.active').data('orderid');
                var qnty = $(this).val();
                price = parseFloat(price);

                var packs=[];
                var packsObj = {};
                if (productSelector.data('prepack')){
                    var orderDetails = thriftModule.client.getOrderDetails(orderId);

                    // если фасованный товар
                    var orderLines = orderDetails.odrerLines;
                    var orderLinesLength = orderDetails.odrerLines.length;
                    for (var i = 0; i < orderLinesLength ; i++){
                        if (orderLines[i].product.id == productSelector.data('productid')){
                            packs = orderLines[i].packs;
                            break;
                        }
                    }
                }else{
                    packs = 0;
                }
                /* ----- */
                packsObj = changePacks($(this),productSelector,packs);
                /* ----- */

                    var commonModule = require('shop-common');
                    $('.catalog-order>li').each(function(){
                    if ($(this).data('productid') == productSelector.data('productid')){
                        $(this).find('.td-spinner .ace-spinner').spinner('value',packsObj.qnty);
                        if(packsObj.packs && commonModule.getPacksLength(packsObj.packs) > 1){
                            $(this).find('.td-spinner .ace-spinner').spinner('disable');
                        }

                        $(this).find('.qnty .ace-spinner').spinner('value',packsObj.qnty);
                        $(this).find('.td-summa').text((price*packsObj.qnty).toFixed(1));
                    }
                });

                if(!packsObj.errorFlag){
                    thriftModule.client.setOrderLine(orderId,productSelector.data('productid'),packsObj.qnty,'',packsObj.packs);
                    productSelector.find('.td-summa').text((price*packsObj.qnty).toFixed(1));
                    orderDetails = thriftModule.client.getOrderDetails(orderId);

                    $('.itogo-right span,.amount span').text(commonModule.countAmount($('.catalog-confirm'),orderDetails));

                    $('.weight-right span,.weight span').text(commonModule.getOrderWeight(orderId,orderDetails));
                    checkBigWeight(orderId,orderDetails);
                }
                }
            });
        }

        function checkBigWeight(orderId,orderDetails){
            var bigWeight = 15;
            var weight;
            var weightRight = $('.weight-right span');

            weight = (weightRight.length) ? parseFloat(weightRight.text()) : parseFloat($('.weight span').text());

            if(weight >= bigWeight){
                var basketModule = require('shop-basket');
                basketModule.setDeliveryCost(orderId,orderDetails);
            }
        }

        function changePacks(selector,productSelector,packs){

            var qnty = selector.closest('.ace-spinner').spinner('value').toFixed(1);

            var isModalWindow = selector.closest('.modal').length > 0;
            if (isModalWindow){
                // если мы в модальном окне
                var errorFlag = false;

                var isPrepack = selector.closest('.modal-footer').hasClass('with-prepack');
                if (isPrepack){
                    //если продукт с prepack
                    var qntyVal,packsVal;
                    var firstPacksVal = selector.closest('.modal-footer').find('>.packs .ace-spinner').spinner('value');
                    var firstQntyVal = selector.closest('.modal-footer').find('>.qnty .ace-spinner').spinner('value');
                    var firstPack = [];
                    firstPack[firstQntyVal]=firstPacksVal;

                    qnty = firstPacksVal*firstQntyVal;
                    var tempPacksVal,tempQntyVal;
                    var errorPrepack = $('.error-prepack');
                    errorPrepack.text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта');
                    errorPrepack.hide();
                    var qntyValItems = [],
                        counter = 0;
                    selector.closest('.modal-footer').find('.prepack-line').each(function(){
                        tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                        tempQntyVal = $(this).find('.qnty .ace-spinner').spinner('value');
                        qnty += tempPacksVal*tempQntyVal;
                        qntyValItems[counter++] = tempQntyVal;
                        if(tempQntyVal == firstQntyVal){
                            errorPrepack.show();
                            errorFlag = true;
                        }
                        for(var i = 0; i < counter-1; i++){
                            if(tempQntyVal == qntyValItems[i]){
                                errorPrepack.show();
                                errorFlag = true;
                            }
                        }
                    });
                    qnty = qnty.toFixed(1);

                    var isPacksSpinner = selector.closest('.packs').length > 0;
                    if (isPacksSpinner){
                        // если меняем кол-во упаковок (то просто меняем старую запись )
                        qntyVal = parseFloat(selector.closest('.prepack-item').next().next().find('.ace-spinner').spinner('value')).toFixed(1);
                        packsVal = selector.closest('.ace-spinner').spinner('value');
                        packs[qntyVal] = packsVal;
                    }else{
                        // если меняем кол-во товара (то перезаписываем все packs)
                        packs = firstPack;
                        selector.closest('.modal-footer').find('.prepack-line').each(function(){
                            tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                            tempQntyVal = $(this).find('.qnty .ace-spinner').spinner('value');
                            tempQntyVal = parseFloat(tempQntyVal).toFixed(1);
                            packs[tempQntyVal]=tempPacksVal;
                        });
                    }
                }

                if(!errorFlag){
                    productSelector.find('.td-spinner .ace-spinner').spinner('value',qnty);
                }
            } else{

                if (packs){
                    packsVal = 0;
                    for(var p in packs){
                        packsVal = packs[p];
                    }
                    packs = [];
                    if(packsVal){
                        packs[qnty]=packsVal;
                        qnty = qnty*packsVal;
                    }
                }
                var isModalWasOpen = productSelector.find('.modal-body').length > 0;
                if(isModalWasOpen){
                    // если мы уже инициировали окно
                    // нужно поменять спиннер этого popup
                    productSelector.find('.modal-footer>.qnty .ace-spinner').spinner('value',qnty);
                }
            }

            var prepackLines = selector.closest('.modal-footer').find('.prepack-line');
            var packsQnty = selector.closest('.modal-footer').find('>.packs .ace-spinner').spinner('value');

            if (prepackLines.length > 0 || packsQnty > 1){
                productSelector.find('.td-spinner .ace-spinner').spinner('disable');
            }else{
                productSelector.find('.td-spinner .ace-spinner').spinner('enable');
            }

            return {
                packs: packs,
                qnty: qnty,
                errorFlag: errorFlag
            }
        }

        function InitSpinnerChangeInBasket(selector){
            var oldSpinnerValue = selector.data('step');

            selector.on('focusout',function(){

               if($(this).val() != oldSpinnerValue){
                   $(this).trigger('change');
               }

            });

            selector.on('change',function(e){

                // действия для окргуления и избежания неправильного ввода (0 например)
                var currentValue = $(this).closest('.ace-spinner').spinner('value');
                if (currentValue == 0 || currentValue === undefined){
                    currentValue = ($(this).data('step')) ? $(this).data('step') : 1;
                }
                if(currentValue) currentValue = parseFloat(currentValue).toFixed(1);
                $(this).closest('.ace-spinner').spinner('value',currentValue);

                if (oldSpinnerValue != currentValue){
                    // чтобы не обрабатывать щелчки ниже 1
                    oldSpinnerValue = currentValue;
                    var productSelector = $(this).closest('.product');
                    var packs = 0;
                    var currentTab = $(this).closest('.tab-pane');
                    var orderId = currentTab.data('orderid');
                    var productId = $(this).closest('.product').data('productid');
                    //var qnty;

                    if(productSelector.data('prepack')){
                        var orderDetails = thriftModule.client.getOrderDetails(orderId);
                        var orderLinesLength = orderDetails.odrerLines.length;
                        for (var i = 0; i < orderLinesLength; i++){
                            if (orderDetails.odrerLines[i].product.id == productId){
                                packs = orderDetails.odrerLines[i].packs;
                            }
                        }
                    }
                    //var packsObj = changePacks($(this),productSelector,packs);
                    changePacks($(this),productSelector,packs);

                    /*if (!packsObj.packs){packsObj.packs = 0;}
                    if(productId && !packsObj.errorFlag){

                        thriftModule.client.setOrderLine(orderId,productId,packsObj.qnty,'',packsObj.packs);
                        orderDetails = thriftModule.client.getOrderDetails(orderId);

                        var commonModule = require('shop-common');
                        currentTab.find('.weight span').text(commonModule.getOrderWeight(orderId,orderDetails));

                        var price = productSelector.find('.td-price').text();
                        price = parseFloat(price);
                        productSelector.find('.td-summa').text((price*packsObj.qnty).toFixed(1));

                        var currentPane = $(this).closest('.tab-pane');
                        currentPane.find('.amount span').text(commonModule.countAmount(currentPane.find('.catalog-order'),orderDetails));
                    }*/

                    var price = productSelector.find('.td-price').text();
                    var qnty = productSelector.find('.td-spinner .ace-spinner').spinner('value');
                    price = parseFloat(price);
                    productSelector.find('.td-summa').text((price*qnty).toFixed(1));
                }

            })
        }

        function initRefresh(){
            $('.refresh').click(function(){
                var isBasket = $(this).closest('.basket-bottom').length > 0;
                var currentTab = $(this).closest('.tab-pane');
                var orderId = currentTab.data('orderid');

                var orderDetails = thriftModule.client.getOrderDetails(orderId);
                var orderLinesLength = orderDetails.odrerLines.length;

                if(isBasket){
                    $('.catalog-order .product').each(function(){
                        var productId = $(this).data('productid');
                        var productSelector = $(this);
                        var qnty;
                        var packs = 0,
                            selector;

                        if(productSelector.data('prepack')){
                            for (var i = 0; i < orderLinesLength; i++){
                                if (orderDetails.odrerLines[i].product.id == productId){
                                    packs = orderDetails.odrerLines[i].packs;
                                }
                            }
                            selector = $(this).find('.modal-footer').find('>.qnty .ace-spinner .spinner1');
                        }else{
                            selector = $(this).find('.td-spinner .spinner1');
                        }
                        var packsObj = changePacks(selector,productSelector,packs);

                        if (!packsObj.packs){packsObj.packs = 0;}
                        if(productId && !packsObj.errorFlag){
                            thriftModule.client.setOrderLine(orderId,productId,packsObj.qnty,'',packsObj.packs);

                        }
                    });

                    orderDetails = thriftModule.client.getOrderDetails(orderId);

                    var commonModule = require('shop-common');
                    currentTab.find('.weight span').text(commonModule.getOrderWeight(orderId,orderDetails));

                    var currentPane = $(this).closest('.tab-pane');
                    currentPane.find('.amount span').text(commonModule.countAmount(currentPane.find('.catalog-order'),orderDetails));
                }
            });
        }

        function InitSpinnerChange(selector){
            var oldSpinnerValue = selector.data('step');

            selector.on('focusout',function(){

                if($(this).val() != oldSpinnerValue){
                    $(this).trigger('change');
                }

            });
            try{
                selector.on('change',function(e){

                    // действия для окргуления и избежания неправильного ввода (0 например)
                    var currentValue = $(this).closest('.ace-spinner').spinner('value');
                    if (currentValue == 0 || currentValue === undefined){
                        currentValue = ($(this).data('step')) ? $(this).data('step') : 1;
                    }
                    if(currentValue) currentValue = parseFloat(currentValue).toFixed(1);
                    $(this).closest('.ace-spinner').spinner('value',currentValue);

                    if (oldSpinnerValue != currentValue){
                        // чтобы не обрабатывать щелчки ниже 1
                    oldSpinnerValue = currentValue;
                    var productSelector = $(this).closest('.product');
                    oldSpinnerValue = currentValue;

                    var qnty = $(this).val();
                    var isModalWindow = $(this).closest('.modal').length > 0;
                    if (isModalWindow){
                        // если мы не в корзине и
                        // в модальном окне с подробной инфой о продукте
                        // автом. считаем spinner для этого продукта в таблице
                        var isPrepack = $(this).closest('.modal-footer').hasClass('with-prepack');
                        if (isPrepack){
                            // если продукт с prepack
                            var prepackLines = $(this).closest('.modal-footer').find('.prepack-line');
                            var packsQnty = $(this).closest('.modal-footer').find('>.packs .ace-spinner').spinner('value');

                            if (prepackLines.length == 0 && packsQnty == 1){
                                // если одна линия и упаковок не больше 1
                                if ($(this).closest('.prepack-item').hasClass('packs')){
                                    // если меняем кол-во упаковок
                                    qnty = $(this).closest('.modal-footer').find('>.qnty .ace-spinner').spinner('value');
                                }
                                productSelector.find('.td-spinner .ace-spinner').spinner('enable');
                                productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(qnty).toFixed(1));
                            }else{
                                // если линия не одна или упаковок больше одной, то делаем spinner disable
                                productSelector.find('.td-spinner .ace-spinner').spinner('disable');
                                var firstPacksVal = $(this).closest('.modal-footer').find('>.packs .ace-spinner').spinner('value');
                                var firstQntyVal = $(this).closest('.modal-footer').find('>.qnty .ace-spinner').spinner('value');
                                var newQnty = firstPacksVal*firstQntyVal;

                                $('.prepack-line').each(function(){
                                    var tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                                    var tempQntyVal = $(this).find('.qnty .ace-spinner').spinner('value');
                                    newQnty += tempPacksVal*tempQntyVal;
                                });
                                productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(newQnty).toFixed(1));
                            }
                        }else{
                            productSelector.find('.td-spinner .ace-spinner').spinner('value',parseFloat(qnty).toFixed(1));
                        }
                    } else{
                        // значит мы в таблице продуктов и нам нужно автом. поменять значение
                        // spinner у соотв. модального окна и посчитать сумму
                        //var productDetails = thriftModule.client.getProductDetails(productSelector.data('productid'));
                        if (productSelector.data('prepack')){
                            productSelector.find('.modal .qnty .ace-spinner').spinner('value',qnty);
                            productSelector.find('.modal .packs .ace-spinner').spinner('value',1);
                        }else{
                            productSelector.find('.modal .ace-spinner').spinner('value',qnty);
                        }
                    }
                    }
                });
            }catch(e){
                alert(e+" Функция InitSpinnerChange");
            }
        }

        function initRemovePrepackLine(selector,productId,productSelector){
            try{
                selector.click(function(e){
                    e.preventDefault();

                    var currentSpinnerVal = parseFloat($(this).closest('.prepack-line').find('.qnty .ace-spinner').spinner('value')).toFixed(1);
                    var setFlag = true,
                        counterForSetFlag = 0;
                    $(this).closest('.modal-footer').find('.qnty .ace-spinner').each(function(){
                        if  (parseFloat($(this).spinner('value')).toFixed(1) == currentSpinnerVal){
                            counterForSetFlag++;
                        }
                    });

                    var catalogOrder = $(this).closest('.catalog-order');
                    var catalogConfirm = $(this).closest('.catalog-confirm');

                    var quantVal = $(this).closest('.prepack-line').find('.qnty .ace-spinner').spinner('value');
                    var packVal = $(this).closest('.prepack-line').find('.packs .ace-spinner').spinner('value');
                    var qnty;

                    //if (catalogOrder.length || catalogConfirm.length){
                        // если мы в корзине или на странице конфирма,
                        // то нужно менять packs и делать setOrderLine
                        /*var currentPane = $('.tab-pane.active');
                        var orderId = currentPane.data('orderid');
                        var orderDetails = thriftModule.client.getOrderDetails(orderId);
                        var orderLinesLength = orderDetails.odrerLines.length;
                        var packs;
                        for (var i = 0; i < orderLinesLength; i++){
                            if (orderDetails.odrerLines[i].product.id == productId){
                                packs = orderDetails.odrerLines[i].packs;
                                qnty = orderDetails.odrerLines[i].quantity;
                            }
                        }
                        if (counterForSetFlag >= 2){
                            setFlag = false;
                        }
                        if (setFlag){
                            packs[currentSpinnerVal] = 0;
                            //alert(qnty+" "+quantVal+" "+packVal);
                            qnty = (qnty - quantVal*packVal).toFixed(1);
                            productSelector.find('.td-spinner .ace-spinner').spinner('value',qnty);
                            productSelector.find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));

                            var commonModule = require('shop-common');

                            if(catalogConfirm.length){
                                // меняем общий спинер в корзине если мы в конфирме
                                currentPane.find('.catalog-order .product').each(function(){
                                    if($(this).data('productid') == productSelector.data('productid')){
                                        $(this).find('.td-spinner .ace-spinner').spinner('value',qnty);

                                        if(commonModule.getPacksLength(packs) == 1){
                                            $(this).find('.td-spinner .ace-spinner').spinner('enable');
                                        }

                                        $(this).find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));
                                    }
                                });
                             }

                            thriftModule.client.setOrderLine(orderId,productId,qnty,'',packs);
                            var currentTab = $('.tab-pane.active');
                            currentTab.find('.weight span').text(commonModule.getOrderWeight(orderId,orderDetails));

                            var currentCatalog;
                            currentCatalog = (catalogOrder.length) ? catalogOrder: catalogConfirm;
                            $('.itogo-right span,.amount span').text(commonModule.countAmount(currentCatalog,orderDetails));

                        }
                    }else{*/
                        // если мы в таблице продуктов
                        qnty = productSelector.find('.td-spinner .ace-spinner').spinner('value');
                        qnty = (qnty - quantVal*packVal).toFixed(1);
                        productSelector.find('.td-spinner .ace-spinner').spinner('value',qnty);
                    //}

                    if (catalogOrder.length || catalogConfirm.length){
                        productSelector.find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));
                    }

                    $(this).closest('.prepack-line').slideUp(function(){
                        var oldHeight = $(this).closest('.modal').height();
                        $(this).closest('.modal').height(oldHeight - 53).css('min-height','268px');
                        if (counterForSetFlag <= 2){
                            $('.error-prepack').hide();
                        }
                        var leftover = $(this).closest('.prepack-list').find('.prepack-line');
                        var lastPacksQnty = productSelector.find('.with-prepack>.packs .ace-spinner').spinner('value');
                        if (leftover.length == 1 && lastPacksQnty == 1){
                            productSelector.find('.td-spinner .ace-spinner').spinner('enable');
                        }
                        $(this).remove();
                    });
                })
            }catch(e){
                alert(e+" Функция initRemovePrepackLine");
            }
        }

        function initProductsSpinner(){
            $('.catalog table .spinner1').each(function(){
                var minClientPack = $(this).data('step');

                InitSpinner($(this),minClientPack,0,minClientPack);
            });
        }

        function initPrepackRequiredInModal(linkSelector,currentModal,productSelector,isFirstModal,isBasketBool){
                         alert('1');
            var isBasket;
            isBasket = (isBasketBool === undefined) ? true : isBasketBool;

            var orderId = linkSelector.closest('.tab-pane').data('orderid');
            if(!orderId){orderId = $('.tab-pane.active').data('orderid')}

            var productId = linkSelector.closest('.product').data('productid');
            var unitName = linkSelector.closest('.product').find('.unit-name').text();

            var isOrdersHistory = linkSelector.closest('.order-products').length > 0;
            if (isOrdersHistory){
                // если это заказы
                orderId = linkSelector.closest('.order-item').data('orderid');
            }

            if(isFirstModal){
                var orderDetails = thriftModule.client.getOrderDetails(orderId);
                var orderLinesLength = orderDetails.odrerLines.length;
                var packs;
                for (var i = 0; i < orderLinesLength; i++){
                    if (orderDetails.odrerLines[i].product.id == productId){
                        packs = orderDetails.odrerLines[i].packs;
                    }
                }

                var counter = 0;
                var prepackHtml;

                /*if(!isFirstModal){
                    currentModal.find('.prepack-list').html('');
                }*/
                var modalHeight = (currentModal.height > 265) ? currentModal.height :  265;
                for(var p in packs){
                    if (p && packs[p]){
                        //alert(p+" "+packs[p]);
                        // чтобы предотвратить вывод удаленных линий, где packs[p]=0
                        if (counter == 0){
                            // если самая первая линия
                            if(!isFirstModal){
                                //alert("1 "+packs[p]+" "+p);
                                currentModal.find('.packs .ace-spinner').spinner('value',packs[p]);
                                currentModal.find('.qnty .ace-spinner').spinner('value',p);
                            }else{
                                InitSpinner(currentModal.find('.packs .spinner1'),packs[p],isBasket,1);
                                InitSpinner(currentModal.find('.qnty .spinner1'),p,isBasket,(productSelector.find('.td-spinner .spinner1').data('step')));
                            }
                        }else{
                            if (packs[p] != 0){
                                // если другая линия
                                prepackHtml = '<div class="prepack-line no-init">' +
                                    '<div class="prepack-item packs">'+
                                    '<input type="text" class="input-mini spinner1 prepack" />'+
                                    '<span>упаковок</span>'+
                                    '</div>'+
                                    '<div class="prepack-item">по</div>'+
                                    '<div class="prepack-item qnty">'+
                                    '<input type="text" data-step="'+ productSelector.find('.spinner1').data('step') +'" class="input-mini spinner1" />'+
                                    '<span>'+ unitName +'</span>'+
                                    '</div>'+
                                    '<div class="prepack-item">'+
                                    '<a href="#" class="remove-prepack-line" title="Удалить">&times;</a>'+
                                    '</div>'+
                                    '</div>';
                                currentModal.find('.prepack-list').append(prepackHtml);
                                InitSpinner(currentModal.find('.no-init .packs .spinner1'),packs[p],isBasket);
                                InitSpinner(currentModal.find('.no-init .qnty .spinner1'),p,isBasket,productSelector.find('.td-spinner .spinner1').data('step'));

                                var currentPrepackLine = currentModal.find('.prepack-line.no-init');
                                initRemovePrepackLine(currentPrepackLine.find('.prepack-item .remove-prepack-line'),productId,productSelector);
                                currentPrepackLine.removeClass('no-init');

                                modalHeight += 53;
                                currentModal.height(modalHeight);
                            }
                        }
                        counter++;
                    }
                }
            }
        }

        $('.ace-spinner').click(function(e){
            // popup для disabled spinner
            var target = e.target;
            if ($(target).hasClass('spinner1')){
                if ($(target).attr('disabled') == 'disabled'){
                    ($(this).closest('tr').length > 0) ? $(this).closest('tr').find('.product-link').trigger('click'):$(this).closest('li').find('.product-link').trigger('click');
                }
            }else if($(target).hasClass('spinner-buttons')){
                if ($(target).prev().attr('disabled') == 'disabled'){
                    ($(this).closest('tr').length > 0) ? $(this).closest('tr').find('.product-link').trigger('click'):$(this).closest('li').find('.product-link').trigger('click');
                }
            }
        });

        return {
            InitSpinner: InitSpinner,
            InitSpinnerChangeInFinal: InitSpinnerChangeInFinal,
            InitSpinnerChangeInBasket: InitSpinnerChangeInBasket,
            InitSpinnerChange: InitSpinnerChange,
            initRemovePrepackLine: initRemovePrepackLine,
            initProductsSpinner: initProductsSpinner,
            initPrepackRequiredInModal: initPrepackRequiredInModal,
            initRefresh: initRefresh
        }
    }
);