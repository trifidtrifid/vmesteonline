define(
    'shop-orders',
    ['jquery','shop-initThrift','shop-basket','shop-common','shop-spinner','initDatepicker'],
    function( $,thriftModule,basketModule,commonModule,spinnerModule, datepickerModule ){

        function createOrdersProductHtml(orderDetails){
            try{
                var ordersProductsHtml = '<section class="catalog">'+
                    '<table>'+
                    '<thead>'+
                    '<tr>'+
                    '<td>Название</td>'+
                    '<td>Цена (руб)</td>'+
                    '<td>Количество</td>'+
                    '<td>Ед.изм</td>'+
                    '<td></td>'+
                    '</tr>'+
                    '</thead>';
                var orderLines = orderDetails.odrerLines;
                var orderLinedLength = orderLines.length;

                for (var j = 0; j < orderLinedLength; j++){
                    //var productDetails = thriftModule.client.getProductDetails(orderLines[j].product.id);
                    //var imagesSet = productDetails.imagesURLset;
                    var unitName = "";
                    if (orderLines[j].product.unitName){unitName = orderLines[j].product.unitName;}
                    ordersProductsHtml += '<tr data-productid="'+ orderLines[j].product.id +'">'+
                        '<td>'+
                        '<a href="#" class="product-link">'+
                        '<img src="'+ orderLines[j].product.imageURL +'" alt="картинка"/>'+
                        '<span>'+
                        '<span>'+orderLines[j].product.name+'</span>'+
                        orderLines[j].product.shortDescr +
                        '</span>'+
                        '</a>'+
                        '<div class="modal">'+
                        '</div>'+
                        '</td>'+
                        '<td class="product-price">'+ orderLines[j].product.price +'</td>'+
                        '<td>'+
                        '<input type="text"';

                    var commonModule = require('shop-common');
                    if (orderLines[j].packs && commonModule.getPacksLength(orderLines[j].packs) > 1){
                        ordersProductsHtml += 'disabled="disabled"';
                    }

                    ordersProductsHtml += ' data-step="'+  orderLines[j].product.minClientPack +'" class="input-mini spinner1" />'+
                        '</td>'+
                        '<td><span class="unit-name">'+unitName+'</span></td>'+
                        '<td>'+
                        '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>'+
                        '</td>'+
                        '</tr>';



                }
                ordersProductsHtml += '</table>'+
                    '</section>';
            }catch(e){
                alert(e+" Функция createOrdersProductHtml");
            }
            return ordersProductsHtml;
        }

        function createOrdersHtml(orders,itsMoreOrders){
            try{
                var ordersHtml = "";
                var ordersLength = orders.length;
                var lastOrderNumber = 0;
                var listLength = 10;

                if (itsMoreOrders){
                    listLength = lengthOrders;
                    ordersLength -= offsetOrders;
                }
                if (ordersLength > listLength){
                    lastOrderNumber = ordersLength - listLength;
                }

                for (var i = ordersLength-1; i >= lastOrderNumber ; i--){
                    // форматирование даты
                    var tempDate = new Date(orders[i].date*1000);
                    // форматирование статуса заказа
                    var orderStatus;
                    var orderLinks = "";
                    switch(orders[i].status){
                        case 0:
                            orderStatus = "Неизвестен" ;
                            break
                        case 1:
                            orderStatus = "Не подтвержден" ;
                            orderLinks = "<a href='#' class='order-confirm'>Подтвердить</a><br><a href='#' class='order-edit'>Изменить</a>"
                            break
                        case 2:
                            orderStatus = "Подтвержден" ;
                            break
                        case 3:
                            orderStatus = "Уже едет" ;
                            break
                        case 4:
                            orderStatus = "Доставлен" ;
                            break
                        case 5:
                            orderStatus = "Закрыт" ;
                            break
                        case 6:
                            orderStatus = "Отменен" ;
                            break
                    }
                    // форматирование типа доставки
                    var orderDetails = thriftModule.client.getOrderDetails(orders[i].id);
                    var orderDelivery;
                    switch(orderDetails.delivery){
                        case 0:
                            orderDelivery = "Неизвестно";
                            break;
                        case 1:
                            orderDelivery = "Самовывоз";
                            break;
                        case 2:
                            orderDelivery = "Курьер рядом";
                            break;
                        case 3:
                            orderDelivery = "Курьер далеко";
                            break
                    }
                    var orderDay = tempDate.getDate();
                    orderDay = (orderDay < 10)? "0" + orderDay: orderDay;

                    var orderMonth = tempDate.getMonth()+1;
                    orderMonth = (orderMonth < 10)? "0" + orderMonth: orderMonth;

                    var basketModule = require('shop-basket');
                    var weekDay = basketModule.getWeekDay(tempDate.getDay());

                    ordersHtml += '<div class="order-item orders-no-init" data-orderid="'+ orders[i].id +'">'+
                        '<table class="orders-tbl">'+
                        '<tbody>'+
                        '<tr>'+
                        '<td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>'+
                        '<td class="td2">'+i+'. Заказ '+orders[i].id+'</td>'+
                        '<td class="td3">'+ orderDay +"."+orderMonth+"<br> ("+weekDay+ ')</td>'+
                        '<td class="td4">'+
                        '<div class="order-status">'+orderStatus +'</div>'+
                        '<div>'+ orderLinks +'</div>'+
                        '</td>'+
                        '<td class="td5">'+ orderDelivery +'<br> ' +
                        orderDetails.deliveryTo.city.name+", "+orderDetails.deliveryTo.street.name+" "+orderDetails.deliveryTo.building.fullNo+", кв."+
                        orderDetails.deliveryTo.flatNo+
                        '</td>'+
                        '<td class="td9">'+ orderDetails.deliveryCost +'</td>'+
                        '<td class="td8">'+ orderDetails.weightGramm +'</td>'+
                        '<td class="td6">'+ orders[i].totalCost.toFixed(1) +'</td>'+
                        '<td class="td7">'+
                        '<button class="btn btn-sm btn-primary no-border repeat-order-btn">Повторить</button>'+
                        '<button class="btn btn-sm btn-primary no-border add-order-btn">Добавить</button>'+
                        '</td>'+
                        '</tr>'+
                        '</tbody>'+
                        '</table>'+
                        '<div class="order-products">'+
                        '</div>'+
                        '</div>';
                }
                var haveMore = ordersLength%listLength;
                if (haveMore && haveMore != ordersLength){
                    //$('.more-orders').show();
                    ordersHtml += '<div class="more-orders"><a href="#">Показать еще</a></div>';
                }else{
                    $('.more-orders').hide();
                }
            }catch(e){
                //alert(e+" Функция createOrdersHtml");
            }
            return ordersHtml;
        }

        function initOrderPlusMinus(selector){
                try{
                    selector.find('.plus-minus').click(function(e){
                        e.preventDefault();

                        var orderItem = $(this).closest('.order-item');
                        var orderProducts = orderItem.find('.order-products');
                        var orderDetails = thriftModule.client.getOrderDetails(orderItem.data('orderid'));
                        var orderLines = orderDetails.odrerLines;
                        var orderLinesLength = orderLines.length;

                        if (orderProducts.find('.catalog').length == 0){
                            orderProducts.append(createOrdersProductHtml(orderDetails));

                            for (var i = 0; i < orderLinesLength; i++){
                                spinnerModule.InitSpinner(orderProducts.find('tbody tr:eq('+ i +') .spinner1'),orderLines[i].quantity,0,orderProducts.find('tbody tr:eq('+ i +') .spinner1').data('step'));
                            }

                            var basketModule = require('shop-basket');
                            var commonModule = require('shop-common');
                            basketModule.InitAddToBasket(orderProducts.find('.fa-shopping-cart'));
                            commonModule.InitProductDetailPopup(orderProducts.find('.product-link'));
                        }

                        orderProducts.slideToggle(200,function(){
                            if ($('.main-content').height() > $(window).height()){
                                $('#sidebar, .shop-right').css('height', $('.main-content').height()+45);
                            }
                        });
                        if ($(this).hasClass('fa-plus')){
                            $(this).removeClass('fa-plus').addClass('fa-minus');
                        }else{
                            $(this).removeClass('fa-minus').addClass('fa-plus');
                        }
                    });
                }catch(e){
                    alert(e+" Функция initOrderPlusMinus");
                }
            }

        function addSingleOrderToBasket(orderId,addType){
            try{
                var orderDetails,
                    curProd,
                    spinVal, i,
                    oldOrderId = $('.tab-pane.active').data('orderid');

                if (addType == 'replace'){
                    orderDetails = thriftModule.client.getOrderDetails(orderId);
                    var orderLines = orderDetails.odrerLines;
                    var orderLinesLength = orderLines.length;
                    for(i = 0; i < orderLinesLength; i++){
                        curProd = orderLines[i].product;
                        spinVal = orderLines[i].quantity;
                        var packs = orderLines[i].packs;
                        thriftModule.client.setOrderLine(oldOrderId,curProd.id,spinVal,"asd",packs);
                    }
                }else if (addType == 'append'){
                    alert(orderId+" "+oldOrderId);
                    orderDetails = thriftModule.client.appendOrder(oldOrderId,orderId);
                }
                orderLines = orderDetails.odrerLines;
                orderLinesLength = orderLines.length;
                var spinnerDisable;
                for(i = 0; i < orderLinesLength; i++){
                    curProd = orderLines[i].product;
                    spinVal = orderLines[i].quantity;
                    spinnerDisable = false;
                    if(orderLines[i].packs && commonModule.getPacksLength(orderLines[i].packs) > 1){
                        spinnerDisable = true;
                    }
                    var basketModule = require('shop-basket');
                    basketModule.AddSingleProductToBasket(curProd,spinVal,spinnerDisable);
                }
            }catch(e){
                alert(e+" Функция addSingleOrderToBasket");
            }
        }

        function AddOrdersToBasket(orderData){
            // добавление целого заказа
            try{
                var addType;
                if (orderData.itsAppend){
                    addType = 'append';
                }else{
                    addType = 'replace';
                }
                $('.catalog-order').html('');
                addSingleOrderToBasket(orderData.orderId,addType);
            }catch(e){
                alert(e+" Функция AddOrdersToBasket");
            }
        }

        function initOrderBtns(selector){
            try{
                selector.find('.repeat-order-btn').click(function(){
                    var orderData= {
                        itsOrder: true,
                        itsAppend: false,
                        orderId : $(this).closest('.order-item').data('orderid')
                    };
                    AddOrdersToBasket(orderData);
                    /*basketModule.flagFromBasketClick = 1;
                    datepickerModule.dPicker.datepicker('setVarFreeDays',0, 0, orderData,0,basketModule.AddSingleProductToBasket,AddOrdersToBasket,basketModule.AddProductToBasketCommon);
                    datepickerModule.dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click').datepicker('triggerFlagBasket');
                    basketModule.flagFromBasketClick = 0;*/
                });
                selector.find('.add-order-btn').click(function(){
                    var orderData= {
                        itsOrder: true,
                        itsAppend: true,
                        orderId : $(this).closest('.order-item').data('orderid')
                    };
                    if ($('.additionally-order').hasClass('hide')){
                        basketModule.flagFromBasketClick = 1;
                        datepickerModule.dPicker.datepicker('setVarFreeDays',0, 0, orderData,0,basketModule.AddSingleProductToBasket,AddOrdersToBasket,basketModule.AddProductToBasketCommon);
                        datepickerModule.dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click');
                    }else{
                        AddOrdersToBasket(orderData);
                    }
                });
            }catch(e){
                alert(e+" Функция initOrderBtns");
            }
        }

        var offsetOrders = 10;
        var lengthOrders = 10;

        function initVarForMoreOrders(){
            offsetOrders = 10;
            lengthOrders = 10;
        }

        function initOrdersLinks(){
            $('.order-edit').click(function(e){
                e.preventDefault();

                var confirmed = confirm('Ваша текущая корзина будет заменена этим заказом. Вы согласны ?');
                if(confirmed){
                    thriftModule.client.getOrder($(this).closest('.order-item').data('orderid'));
                    var orderData= {
                        itsOrder: true,
                        itsAppend: false,
                        orderId : $(this).closest('.order-item').data('orderid')
                    };
                    AddOrdersToBasket(orderData);
                }
            });

            $('.order-confirm').click(function(e){
                e.preventDefault();

                var currentOrder = $(this).closest('.order-item');
                var orderId = currentOrder.data('orderid');

                var amount = currentOrder.find('.td6').text();

                if (currentOrder.find('.catalog').length == 0){
                    var orderDetails = thriftModule.client.getOrderDetails(orderId);
                    currentOrder.find('.order-products').append(createOrdersProductHtml(orderDetails));
                }

                var catalogHtml = "";
                var spinnerValue = [], spinnerStep = [], counter = 0;

                $('.tabs-days .tab-pane').each(function(){
                   if ($(this).data('orderid') == orderId){
                       catalogHtml = $(this).find('.catalog-order').html();

                       $(this).find('.catalog-order td .spinner1').each(function(){
                           spinnerValue[counter] = $(this).closest('.ace-spinner').spinner('value');
                           spinnerStep[counter++] = $(this).data('step');
                       });
                   }
                });

                $('.page').hide();

                var date = currentOrder.find('.td3').text();

                var basketModule = require('shop-basket');
                basketModule.GoToConfirm(catalogHtml,amount,spinnerValue,date);

                /*thriftModule.client.getOrder($(this).closest('.order-item').data('orderid'));
                thriftModule.client.confirmOrder();
                alert('Заказ подтвержден !');
                $(this).closest('td').find('.order-status').text('Подтвержден');
                $(this).parent().remove();*/

            });
        }

        function initShowMoreOrders(orders){
            try{
                $('.more-orders').click(function(e){
                    e.preventDefault();
                    var orderList = $('.orders-list');
                    orderList.find('.more-orders').remove();
                    var itsMoreOrders = true;
                    orderList.append(createOrdersHtml(orders,itsMoreOrders));
                    initShowMoreOrders(orders);
                    initOrdersLinks();
                    var ordersNoInit = $('.orders-no-init');
                    initOrderPlusMinus(ordersNoInit);
                    initOrderBtns(ordersNoInit);
                    ordersNoInit.removeClass('orders-no-init');
                    offsetOrders += lengthOrders;
                    commonModule.setSidebarHeight();
                });
            }catch(e){
                alert(e+" Функция initShowMoreOrders");
            }
        }

        function GoToOrdersTrigger(){
            $('.go-to-orders').trigger('click');
        }

        return{
            createOrdersProductHtml: createOrdersProductHtml,
            createOrdersHtml: createOrdersHtml,
            initOrderPlusMinus: initOrderPlusMinus,
            addSingleOrderToBasket: addSingleOrderToBasket,
            AddOrdersToBasket: AddOrdersToBasket,
            initOrderBtns: initOrderBtns,
            initVarForMoreOrders: initVarForMoreOrders,
            initOrdersLinks: initOrdersLinks,
            initShowMoreOrders: initShowMoreOrders,
            GoToOrdersTrigger: GoToOrdersTrigger
        }
    }
);