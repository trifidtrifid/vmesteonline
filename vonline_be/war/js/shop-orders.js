define(
    'shop-orders',
    ['jquery','shop-initThrift','shop-basket','shop-common','shop-spinner'],
    function( $,thriftModule,basketModule,commonModule,spinnerModule ){

        function createOrdersProductHtml(orderDetails,noEdit){
            try{
                var ordersProductsHtml = '<section class="catalog">'+
                    '<table>'+
                    '<thead>'+
                    '<tr>'+
                    '<td>Название</td>'+
                    '<td>Цена (руб)</td>'+
                    '<td>Количество</td>'+
                    '<td>Стоимость</td>'+
                    '<td>Ед.изм</td>'+
                    '<td></td>'+
                    '</tr>'+
                    '</thead>';
                var orderLines = orderDetails.odrerLines;
                var orderLinedLength = orderLines.length;

                for (var j = 0; j < orderLinedLength; j++){
                    var unitName = "";
                    var myPic;
                    commonModule = require('shop-common');
                    (orderLines[j].product.imageURL) ? myPic = orderLines[j].product.imageURL : myPic = commonModule.noPhotoPic;
                    if (orderLines[j].product.unitName){unitName = orderLines[j].product.unitName;}
                    ordersProductsHtml += '<tr class="product" data-prepack="'+ orderLines[j].product.prepackRequired +'" data-productid="'+ orderLines[j].product.id +'">'+
                        '<td>'+
                        '<a href="#" class="product-link">'+
                        '<div class="product-pic"><img src="'+ myPic + '?w=40&h=40" alt="'+ orderLines[j].product.name +'"/></div>'+
                        '<span>'+
                        '<span class="product-name">'+orderLines[j].product.name+'</span>'+
                        orderLines[j].product.shortDescr +
                        '</span>'+
                        '</a>'+
                        '<div class="modal">'+
                        '</div>'+
                        '</td>'+
                        '<td class="product-price">'+ orderLines[j].product.price +'</td>'+
                        '<td class="td-spinner">';

                    if(noEdit){
                        ordersProductsHtml += orderLines[j].quantity;
                    }else{
                        ordersProductsHtml += '<input type="text"';

                        var commonModule = require('shop-common');
                        if (orderLines[j].packs && commonModule.getPacksLength(orderLines[j].packs) > 1){
                            ordersProductsHtml += 'disabled="disabled"';
                        }

                        ordersProductsHtml += ' data-step="'+  orderLines[j].product.minClientPack +'" class="input-mini spinner1" />';
                    }

                    ordersProductsHtml += '<span class="added-text">добавлен</span></td>'+
                        '<td class="orderLine-amount"><span>'+(orderLines[j].product.price*orderLines[j].quantity).toFixed(1)+'</span></td>'+
                        '<td><span class="unit-name">'+unitName+'</span></td>';

                    if(!noEdit){
                        ordersProductsHtml += '<td>'+
                        '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>'+
                        '<span href="#" title="Продукт уже у вас в корзине" class="fa fa-check"></span>'+
                        '</td>';
                    }
                    ordersProductsHtml +=  '</tr>';
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
                    switch(orders[i].status){
                        case 0:
                            orderStatus = "Неизвестен" ;
                            break
                        case 1:
                            orderStatus = "Не подтвержден" ;
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
                    /*var orderDetails = thriftModule.client.getOrderDetails(orders[i].id);
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
                    }*/
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
                        '<td class="td2">'+orders[i].id+'</td>'+
                        '<td class="td3">'+ orderDay +"."+orderMonth+" ("+weekDay+ ')</td>'+
                        '<td class="td4">'+
                        '<div class="order-status">'+orderStatus +'</div>'+
                        '</td>'+
                        '<td class="td9"></td>'+
                        '<td class="td8"></td>'+
                        '<td class="td6">'+ orders[i].totalCost.toFixed(1) +'</td>'+
                        '</tr>'+
                        '</tbody>'+
                        '</table>'+
                        '<div class="order-bottom">' +
                        '<a href="#" title="Удалить" class="delete-order-from-history">&times;</a>'+
                        '<button class="btn btn-sm btn-primary no-border repeat-order-btn">Повторить</button>'+
                        '<button class="btn btn-sm btn-primary no-border add-order-btn">Добавить</button>' +
                        '<div class="order-delivery"></div>'+
                        '</div>'+
                        '<div class="order-products">'+
                        '</div>'+
                        '</div>';
                }
                var haveMore = ordersLength%listLength;
                if (haveMore && haveMore != ordersLength){
                    ordersHtml += '<div class="more-orders"><a href="#">Показать еще</a></div>';
                }else{
                    $('.more-orders').hide();
                }
            }catch(e){
                //alert(e+" Функция createOrdersHtml");
            }
            return ordersHtml;
        }


        function showOrderDetails(orderItem,orderid,details)
        {
            var orderDetails = (details) ? details : thriftModule.client.getOrderDetails(orderid);
            orderItem.find('.td9').text(orderDetails.deliveryCost);
            orderItem.find('.td8').text((orderDetails.weightGramm/1000).toFixed(1));

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

            orderItem.find('.order-delivery').html('<span><b>Доставка:</b> '+ orderDelivery +',  ' +
            orderDetails.deliveryTo.city.name+", "+orderDetails.deliveryTo.street.name+" "+orderDetails.deliveryTo.building.fullNo+", кв."+
            orderDetails.deliveryTo.flatNo +'</span>');
        }

        function deleteOrderFromHistory(){
          $('.delete-order-from-history').click(function(e){
              e.preventDefault();

              var orderItem = $(this).closest('.order-item');
              var orderId = orderItem.data('orderid');
              var productId;

              if (!orderItem.find('.product').length){
                  orderItem.find('.plus-minus').trigger('click');
              }
              orderItem.find('.product').each(function(){
                  productId = $(this).data('productid');
                  thriftModule.client.removeOrderLine(orderId,productId);
              });

              thriftModule.client.deleteOrder(orderId);

              $(this).closest('.order-item').slideUp();
          });
        }

        function initOrderPlusMinus(selector){
                try{
                    selector.find('.plus-minus').click(function(e){
                        e.preventDefault();

                        var orderItem = $(this).closest('.order-item');
                        var orderProducts = orderItem.find('.order-products');
                        var orderId = orderItem.data('orderid');

                        if (orderProducts.find('.catalog').length == 0){

                            var orderDetails = thriftModule.client.getOrderDetails(orderId);
                            var orderLines = orderDetails.odrerLines;
                            var orderLinesLength = orderLines.length;

                            showOrderDetails(orderItem,orderId,orderDetails);
                            orderProducts.append(createOrdersProductHtml(orderDetails));

                            for (var i = 0; i < orderLinesLength; i++){
                                var spinnerSelector = orderProducts.find('tbody tr:eq('+ i +') .spinner1');
                                var spinnerStep = orderProducts.find('tbody tr:eq('+ i +') .spinner1').data('step');
                                spinnerModule.InitSpinner(spinnerSelector,orderLines[i].quantity,0,spinnerStep);

                                if(spinnerSelector.attr('disabled') == 'disabled'){
                                    spinnerSelector.closest('.ace-spinner').spinner('disable');
                                }
                            }

                            var basketModule = require('shop-basket');
                            var commonModule = require('shop-common');
                            basketModule.InitAddToBasket(orderProducts.find('.fa-shopping-cart'));
                            commonModule.InitProductDetailPopup(orderProducts.find('.product-link'));
                            commonModule.markAddedProduct();
                        }

                        orderProducts.slideToggle(200,function(){
                            if ($('.main-content').height() > $(window).height()){
                                $('#sidebar, .shop-right').css('height', $('.main-content').height()+45);
                            }else{
                                $('#sidebar, .shop-right').css('height', '100%');
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
                    tabPaneActive = $('.tab-pane.active'),
                    oldOrderId = tabPaneActive.data('orderid');
                    var basketModule = require('shop-basket');
                    if (!oldOrderId){
                        var oldOrder = thriftModule.client.createOrder(0);
                        oldOrderId = oldOrder.id;
                        var nextDateStr = new Date(oldOrder.date*1000);
                        basketModule.addTabToBasketHtml(nextDateStr,oldOrderId);
                        tabPaneActive = $('.tab-pane.active')
                    }

                if (addType == 'replace'){
                    orderDetails = thriftModule.client.getOrderDetails(orderId);
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
                    var commonModule = require('shop-common');
                    if(orderLines[i].packs && commonModule.getPacksLength(orderLines[i].packs) > 1){
                        spinnerDisable = true;
                    }
                    basketModule.AddSingleProductToBasket(curProd,spinVal,spinnerDisable);
                }
                commonModule.markAddedProduct();
                tabPaneActive.find('.weight span').text(commonModule.getOrderWeight(orderId));
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
                });
                selector.find('.add-order-btn').click(function(){
                    var orderData= {
                        itsOrder: true,
                        itsAppend: true,
                        orderId : $(this).closest('.order-item').data('orderid')
                    };
                    if ($('.additionally-order').hasClass('hide')){
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

/*        function initOrdersLinks(){
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

        }*/

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
                    var commonModule = require('shop-common');
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
            //initOrdersLinks: initOrdersLinks,
            initShowMoreOrders: initShowMoreOrders,
            GoToOrdersTrigger: GoToOrdersTrigger,
            showOrderDetails : showOrderDetails,
            deleteOrderFromHistory : deleteOrderFromHistory
        }
    }
);