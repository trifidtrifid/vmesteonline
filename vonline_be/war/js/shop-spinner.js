define(
    'shop-spinner',
    ['jquery','shop-common'],
    function( $,commonModule ){

        function InitSpinner(selector,spinnerValue,itsBasket,spinnerStep){
            try{
                var step = 1;
                if (spinnerStep){step = spinnerStep}
                // делаем +spinnerValue для строгого преобразования к числу
                selector.ace_spinner({value:+spinnerValue,min:step,max:100000,step:step, btn_up_class:'btn-info' , btn_down_class:'btn-info'});
            }catch(e){
                alert(e+" Функция InitSpinner");
            }
            if (selector.closest('.modal-order-end').length > 0){
                InitSpinnerChangeInFinal(selector);
            }else{
                if (itsBasket){
                    InitSpinnerChangeInBasket(selector);
                }else{
                    InitSpinnerChange(selector);
                }
            }
        }

        function InitSpinnerChangeInFinal(selector){
            selector.on('change',function(e){
                var productSelector = $(this).closest('tr');
                var price = productSelector.find('.td-price').text();
                var qnty = $(this).val();
                price = parseFloat(price);
                $('.catalog-order>li').each(function(){
                    if ($(this).data('productid') == productSelector.data('productid')){
                        $(this).find('.ace-spinner').spinner('value',qnty);
                        $(this).find('.td-summa').text((price*qnty).toFixed(1));
                    }
                });
                var productDetails = client.getProductDetails(productSelector.data('productid'));
                var packs=[];
                if (productDetails.prepackRequired){
                    var orderDetails = client.getOrderDetails(currentOrderId);
                    var orderLines = orderDetails.odrerLines;
                    var orderLinesLength = orderDetails.odrerLines.length;
                    for (var i = 0; i < orderLinesLength ; i++){
                        if (orderLines[i].product.id == productSelector.data('productid')){
                            packs = orderLines[i].packs;
                            break;
                        }
                    }
                    // а здесь нам нужно менять packs, если меняем его внутри modal-order-end
                    // но пока такой логики нет
                    // может быть так и оставим
                }else{
                    packs = 0;
                }
                client.setOrderLine(productSelector.data('productid'),qnty,'asd',packs);
                productSelector.find('.td-summa').text((price*qnty).toFixed(1));

                $('.itogo-right span,.modal-itogo span').text(commonModule.countAmount($('.modal-body-list')));
            });
        }

        function InitSpinnerChangeInBasket(selector){
            selector.on('change',function(e){
                var currentValue = $(this).closest('.ace-spinner').spinner('value');
                $(this).closest('.ace-spinner').spinner('value',+currentValue.toFixed(1));
                var qnty = $(this).val();
                var packs;
                var productSelector = $(this).closest('li');
                var orderDetails = client.getOrderDetails(currentOrderId);
                var orderLinesLength = orderDetails.odrerLines.length;
                var productId = $(this).closest('li').data('productid');
                for (var i = 0; i < orderLinesLength; i++){
                    if (orderDetails.odrerLines[i].product.id == productId){
                        packs = orderDetails.odrerLines[i].packs;
                    }
                }
                if ($(this).closest('.modal').length > 0){
                    // если мы в модальном окне
                    if ($(this).closest('.modal-footer').hasClass('with-prepack')){
                        //если продукт с prepack
                        var qntyVal,packsVal;
                        var firstPacksVal = $(this).closest('.modal-footer').find('>.prepack-item.packs .ace-spinner').spinner('value');
                        var firstQntyVal = $(this).closest('.modal-footer').find('>.prepack-item:not(".packs") .ace-spinner').spinner('value');
                        var firstPack = [];
                        firstPack[firstQntyVal]=firstPacksVal;

                        qnty = firstPacksVal*firstQntyVal;
                        var tempPacksVal,tempQntyVal;
                        var errorFlag = false;
                        var errorPrepack = $('.error-prepack');
                        errorPrepack.text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта');
                        errorPrepack.hide();
                        var qntyValItems = [],
                            counter = 0;
                        $(this).closest('.modal-footer').find('.prepack-line').each(function(){
                            tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                            tempQntyVal = $(this).find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
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

                        if ($(this).closest('.packs').length > 0){
                            // если меняем кол-во упаковок (то просто меняем старую запись )
                            qntyVal = parseFloat($(this).closest('.prepack-item').next().next().find('.ace-spinner').spinner('value')).toFixed(1);
                            packsVal = $(this).closest('.ace-spinner').spinner('value');
                            packs[qntyVal] = packsVal;
                        }else{
                            // если меняем кол-во товара (то перезаписываем все packs)
                            packs = firstPack;
                            $(this).closest('.modal-footer').find('.prepack-line').each(function(){
                                tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                                tempQntyVal = $(this).find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
                                tempQntyVal = parseFloat(tempQntyVal).toFixed(1);
                                packs[tempQntyVal]=tempPacksVal;
                            });
                        }
                    }
                    if(!errorFlag){
                        productSelector.find('td>.ace-spinner').spinner('value',qnty);
                    }
                } else{
                    qnty = productSelector.find('td .ace-spinner').spinner('value').toFixed(1);

                    packsVal = 0;
                    for(var p in packs){
                        packsVal = packs[p];
                    }
                    packs = [];
                    if(packsVal){
                        packs[qnty]=packsVal
                        qnty = qnty*packsVal;
                    }
                    if(productSelector.find('.modal-body').length > 0){
                        // если мы уже инициировали окно
                        // нужно поменять спиннер этого popup
                        productSelector.find('.modal-footer>.prepack-item:not(".packs") .ace-spinner').spinner('value',qnty);
                    }
                }

                if (!packs){packs = 0;}
                if(productId && !errorFlag){
                    /* for (var p in packs){
                     alert(p+" "+packs[p]);
                     }*/
                    client.setOrderLine(productId,qnty,'sdf',packs);
                    var price = productSelector.find('.td-price').text();
                    price = parseFloat(price);
                    productSelector.find('.td-summa').text((price*qnty).toFixed(1));
                    $('.itogo-right span').text(commonModule.countAmount($('.catalog-order')));
                    $('.modal-itogo span').text(commonModule.countAmount($('.modal-body-list')));
                }

            })
        }

        function InitSpinnerChange(selector){
            try{
                selector.on('change',function(e){
                    var currentValue = $(this).closest('.ace-spinner').spinner('value');
                    $(this).closest('.ace-spinner').spinner('value',+currentValue.toFixed(1));

                    var productSelector = $(this).closest('tr');

                    var qnty = $(this).val();
                    if ($(this).closest('.modal').length > 0){
                        // если мы не в корзине и
                        // в модальном окне с подробной инфой о продукте
                        // автом. считаем spinner для этого продукта в таблице
                        if ($(this).closest('.modal-footer').hasClass('with-prepack')){
                            // если продукт с prepack
                            if ($(this).closest('.modal-footer').find('.prepack-line').length == 0 &&
                                $(this).closest('.modal-footer').find('>.prepack-item.packs .ace-spinner').spinner('value') == 1){
                                // если одна линия и упаковок не больше 1
                                if ($(this).closest('.prepack-item').hasClass('packs')){
                                    // если меняем кол-во упаковок
                                    qnty = $(this).closest('.modal-footer').find('>.prepack-item:not(".packs") .ace-spinner').spinner('value');
                                }
                                productSelector.find('td>.ace-spinner').spinner('enable');
                                productSelector.find('td>.ace-spinner').spinner('value',qnty);
                            }else{
                                // если линия не одна или упаковок больше одной, то делаем spinner disable
                                productSelector.find('td>.ace-spinner').spinner('disable');
                                var firstPacksVal = $(this).closest('.modal-footer').find('>.prepack-item.packs .ace-spinner').spinner('value');
                                var firstQntyVal = $(this).closest('.modal-footer').find('>.prepack-item:not(".packs") .ace-spinner').spinner('value');
                                var newQnty = firstPacksVal*firstQntyVal;

                                $('.prepack-line').each(function(){
                                    var tempPacksVal = $(this).find('.packs .ace-spinner').spinner('value');
                                    var tempQntyVal = $(this).find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
                                    newQnty += tempPacksVal*tempQntyVal;
                                });
                                productSelector.find('td>.ace-spinner').spinner('value',newQnty);
                            }
                        }else{
                            productSelector.find('td>.ace-spinner').spinner('value',qnty);
                        }
                    } else{
                        // значит мы в таблице продуктов и нам нужно автом. поменять значение
                        // spinner у соотв. модального окна и посчитать сумму
                        var productDetails = client.getProductDetails(productSelector.data('productid'));
                        if (productDetails.prepackRequired){
                            productSelector.find('.modal .prepack-item:not(".packs") .ace-spinner').spinner('value',qnty);
                            productSelector.find('.modal .prepack-item.packs .ace-spinner').spinner('value',1);
                        }else{
                            productSelector.find('.modal .ace-spinner').spinner('value',qnty);
                        }
                    }
                });
            }catch(e){
                alert(e+" Функция InitSpinnerChange");
            }
        }

        function initRemovePrepackLine(selector,productId,productSelector){
            try{
                selector.click(function(){
                    var currentSpinnerVal = parseFloat($(this).closest('.prepack-line').find('.prepack-item:not(".packs") .ace-spinner').spinner('value')).toFixed(1);
                    var setFlag = true,
                        counterForSetFlag = 0;
                    $(this).closest('.modal-footer').find('.prepack-item:not(".packs") .ace-spinner').each(function(){
                        if  (parseFloat($(this).spinner('value')).toFixed(1) == currentSpinnerVal){
                            counterForSetFlag++;
                        }
                    });

                    if ($(this).closest('.catalog-order').length > 0){
                        // если мы в корзине, то нужно менять packs и делать setOrderLine
                        var orderDetails = client.getOrderDetails(currentOrderId);
                        var orderLinesLength = orderDetails.odrerLines.length;
                        var packs,qnty;
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
                            var quantVal = $(this).closest('.prepack-line').find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
                            var packVal = $(this).closest('.prepack-line').find('.packs .ace-spinner').spinner('value');
                            qnty = (qnty - quantVal*packVal).toFixed(1);
                            productSelector.find('td>.ace-spinner').spinner('value',qnty);
                            productSelector.find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));
                            client.setOrderLine(productId,qnty,'asd',packs);
                            $('.itogo-right span').text(countAmount($('.catalog-order')));
                        }
                    }
                    $(this).closest('.prepack-line').slideUp(function(){
                        var oldHeight = $(this).closest('.modal').height();
                        $(this).closest('.modal').height(oldHeight - 53);
                        if (counterForSetFlag <= 2){
                            $('.error-prepack').hide();
                        }
                        if ($(this).closest('.prepack-list').find('.prepack-line').length == 1){
                            productSelector.find('td>.ace-spinner').spinner('enable');
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

        function initPrepackRequiredInModal(linkSelector,currentModal,productSelector,isFirstModal){

            var orderId = currentOrderId;
            var productId = linkSelector.closest('li').data('productid');
            var unitName = linkSelector.closest('li').find('.unit-name').text();
            if (linkSelector.closest('.order-products').length > 0){
                // если это заказы
                productId = linkSelector.closest('tr').data('productid');
                orderId = linkSelector.closest('.order-item').data('orderid');
                unitName = linkSelector.closest('tr').find('.unit-name').text();
            }
            var orderDetails = client.getOrderDetails(orderId);
            var orderLinesLength = orderDetails.odrerLines.length;
            var packs;
            for (var i = 0; i < orderLinesLength; i++){
                if (orderDetails.odrerLines[i].product.id == productId){
                    packs = orderDetails.odrerLines[i].packs;
                }
            }

            var counter = 0;
            var prepackHtml;
            if(!isFirstModal){
                currentModal.find('.prepack-list').html('');
            }
            var modalHeight;
            (currentModal.height > 265) ? modalHeight = currentModal.height : modalHeight = 265;
            for(var p in packs){
                if (p && packs[p]){
                    //alert(p+" "+packs[p]);
                    // чтобы предотвратить вывод удаленных линий, где packs[p]=0
                    if (counter == 0){
                        // если самая первая линия
                        if(!isFirstModal){
                            currentModal.find('.packs .ace-spinner').spinner('value',packs[p]);
                            currentModal.find('.prepack-item:not(".packs") .ace-spinner').spinner('value',p);
                        }else{
                            InitSpinner(currentModal.find('.packs .spinner1'),packs[p],1,1);
                            InitSpinner(currentModal.find('.prepack-item:not(".packs") .spinner1'),p,1,(productSelector.find('td>.ace-spinner .spinner1').data('step')));
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
                                '<div class="prepack-item">'+
                                '<input type="text" data-step="'+ productSelector.find('.spinner1').data('step') +'" class="input-mini spinner1" />'+
                                '<span>'+ unitName +'</span>'+
                                '</div>'+
                                '<div class="prepack-item">'+
                                '<a href="#" class="close" title="Удалить">×</a>'+
                                '</div>'+
                                '</div>';
                            currentModal.find('.prepack-list').append(prepackHtml);
                            InitSpinner(currentModal.find('.no-init .packs .spinner1'),packs[p],1);
                            InitSpinner(currentModal.find('.no-init .prepack-item:not(".packs") .spinner1'),p,1,productSelector.find('td>.ace-spinner .spinner1').data('step'));
                            var currentPrepackLine = currentModal.find('.prepack-line.no-init');
                            initRemovePrepackLine(currentPrepackLine.find('.prepack-item .close'),productId,productSelector);

                            currentPrepackLine.removeClass('no-init');
                            modalHeight += 53;
                            currentModal.height(modalHeight);
                        }
                    }
                    counter++;
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
    }
);