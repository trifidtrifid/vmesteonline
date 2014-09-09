define(
    'orders.min',
    ['jquery','shop-initThrift.min','shop-orders.min','bo-common.min','datepicker-backoffice','datepicker-ru'],
    function( $,thriftModule,ordersModule, boCommon ){

        function initBackofficeOrders(){

            var w = $(window);
            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;

            function showAllOrders(){
                try{
                    // глобальная переменная для совмесстного использования с datepicker
                    orders = thriftModule.client.getOrdersByStatus(0,nowTime+180*day,0);

                    $('.orders-list').html("").append(createOrdersHtml(orders));

                    var ordersNoInit = $('.orders-no-init');
                    initOrderPlusMinus(ordersNoInit);
                    ordersNoInit.removeClass('orders-no-init');

                    deliveryFilterFlag = 0;
                    statusFilterFlag = 0;
                    dateFilterFlag = 0;
                    searchFilterFlag = 0;
                }catch(e){

                }
            }

            //showAllOrders();

            function initOrderPlusMinus(selector){
                selector.find('.plus-minus').click(function(e){
                    e.preventDefault();

                    var orderItem = $(this).closest('.order-item');
                    var orderProducts = orderItem.find('.order-products');


                    if (orderProducts.find('.catalog').length == 0){
                        var orderId = orderItem.data('orderid');
                        var orderDetails = thriftModule.client.getOrderDetails(orderId);

                        ordersModule.showOrderDetails(orderItem,orderId,orderDetails);
                        orderProducts.append(createOrdersProductHtml(orderDetails));
                    }

                    orderProducts.slideToggle(200,function(){
                        if ($('.main-content').height() > w.height()){
                            $('#sidebar, .shop-right').css('height', $('.main-content').height()+45);
                        }
                    });
                    if ($(this).hasClass('fa-plus')){
                        $(this).removeClass('fa-plus').addClass('fa-minus');
                    }else{
                        $(this).removeClass('fa-minus').addClass('fa-plus');
                    }
                });
            }

            var producers;
            var producersLength;
            function createOrdersProductHtml(orderDetails){
                try{
                    var ordersProductsHtml = '<section class="catalog">'+
                        '<table>'+
                        '<thead>'+
                        '<tr>'+
                        '<td>Название</td>'+
                        '<td>Производитель</td>'+
                        '<td>Цена (руб)</td>'+
                        '<td>Количество</td>'+
                        '<td>Стоимость</td>'+
                        '<td>Ед.изм</td>'+
                        '<td></td>'+
                        '</tr>'+
                        '</thead>';
                    var orderLines = orderDetails.odrerLines;
                    var orderLinedLength = orderLines.length;

                    producers = (producers) ? producers : thriftModule.client.getProducers();
                    producersLength = (producersLength) ? producersLength : producers.length;

                    for (var j = 0; j < orderLinedLength; j++){
                        //var productDetails = thriftModule.client.getProductDetails(orderLines[j].product.id);
                        //var imagesSet = productDetails.imagesURLset;
                        var unitName = "",
                            producerName,producerId;

                        for(var i = 0; i < producersLength; i++){
                            if(producers[i].id == orderLines[j].product.producerId){
                                producerName = producers[i].name;
                                producerId = producers[i].id;
                                break;
                            }
                        }

                        if (orderLines[j].product.unitName){unitName = orderLines[j].product.unitName;}
                        var myPic;
                        (orderLines[j].product.imageURL) ? myPic = orderLines[j].product.imageURL : myPic = '/i/no-photo.png';
                        ordersProductsHtml += '<tr class="product" data-prepack="'+ orderLines[j].product.prepackRequired +'" data-productid="'+ orderLines[j].product.id +'">'+
                            '<td>'+
                            '<a href="#" class="product-link">'+
                            /*'<div class="product-pic"><img src="'+ myPic +'" alt="картинка"/></div>'+*/
                            '<span>'+
                            '<span class="product-name">'+orderLines[j].product.name+'</span>'+
                            orderLines[j].product.shortDescr +
                            '</span>'+
                            '</a>'+
                            '<div class="modal">'+
                            '</div>'+
                            '</td>'+
                            '<td class="td-producer" data-producerid="'+ producerId +'">'+ producerName +'</td>'+
                            '<td class="product-price">'+ orderLines[j].product.price +'</td>'+
                            '<td class="td-spinner">'+
                            orderLines[j].quantity+
                            '</td>'+
                            '<td class="orderLine-amount"><span>'+(orderLines[j].product.price*orderLines[j].quantity).toFixed(1)+'</span></td>'+
                            '<td><span class="unit-name">'+unitName+'</span></td>'+
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
                    var listLength = 100;

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
                        //if (orderDetails.deliveryTo){

                        var orderDay = tempDate.getDate();
                        orderDay = (orderDay < 10)? "0" + orderDay: orderDay;

                        var orderMonth = tempDate.getMonth()+1;
                        orderMonth = (orderMonth < 10)? "0" + orderMonth: orderMonth;

                        var deleteClass = "";
                        if(orderStatus == "Подтвержден"){
                            deleteClass = "passive";
                        }

                        ordersHtml += '<div class="order-item orders-no-init" data-orderid="'+ orders[i].id +'">'+
                            '<table class="orders-tbl">'+
                            '<tbody>'+
                            '<tr>'+
                            '<td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>'+
                            '<td class="td2">'+orders[i].id +'</td>'+
                            '<td class="td8 user-name">'+
                            orders[i].userName +
                            '</td>'+
                            '<td class="td3" id="'+ orders[i].date +'">'+ orderDay +"."+orderMonth+"."+tempDate.getFullYear()+ '</td>'+
                            '<td class="td4">'+
                            '<div class="order-status">'+orderStatus +'</div>'+
                            '</td>'+
                            /*'<td class="td5">+
                             '</td>'+*/
                            '<td class="td9"></td>'+
                            '<td class="td8"></td>'+
                            '<td class="td6">'+ orders[i].totalCost.toFixed(1) +'</td>'+
                            '<td class="td1"><a href="#" title="Удалить заказ" class="delete-order '+deleteClass+'">&times;</a></td>'+
                            '</tr>'+
                            '</tbody>'+
                            '</table>'+
                            '<div class="order-bottom">' +
                            '<div class="order-delivery"></div>'+
                            '</div>'+
                            '<div class="order-products">'+
                            '</div>'+
                            '</div>';
                        //}
                    }
                    /*var haveMore = ordersLength%listLength;
                     if (haveMore && haveMore != ordersLength){
                     //$('.more-orders').show();
                     ordersHtml += '<div class="more-orders"><a href="#">Показать еще</a></div>';
                     }else{
                     $('.more-orders').hide();
                     }*/
                }catch(e){
                    alert(e+" Функция createOrdersHtml");
                }
                return ordersHtml;
            }

            $('.delete-order').click(function(e){
                e.preventDefault();

                if(!$(this).hasClass('passive')){
                    var orderItem = $(this).closest('.order-item');
                    var orderId = orderItem.data('orderid');

                    thriftModule.client.deleteOrder(orderId);
                    orderItem.slideUp();
                }
            });

            try{
                var dPicker = $('#date-picker-1');
                var dPickerExport = $('.datepicker-export');

                globalUserAuth = true;

                dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                    $(this).prev().focus();
                });

                dPickerExport.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                    $(this).prev().focus();
                });

                var datepickerFunc = {
                    createOrdersHtml: createOrdersHtml,
                    initOrderPlusMinus: initOrderPlusMinus,
                    setSidebarHeight: boCommon.setSidebarHeight,
                    filterByStatus: filterByStatus,
                    filterByDelivery: filterByDelivery,
                    filterBySearch: filterBySearch
                };

                dPicker.datepicker('setVarOrderDates',datepickerFunc);
                dPickerExport.datepicker('setVarOrderDates',datepickerFunc);


                $('.reset-filters').click(function(){
                    //showAllOrders();

                    deliveryFilterFlag = 0;
                    statusFilterFlag = 0;
                    dateFilterFlag = 0;
                    searchFilterFlag = 0;

                    $('.orders-list').html("");
                    $('.type-delivery-dropdown .btn-group-text').text('Тип доставки');
                    $('.status-dropdown .btn-group-text').text('Статус заказа');
                    $('#back-search').val('Поиск по имени клиента или номеру телефона');
                    dPicker.val('Фильтр по дате');
                });
            }catch(e){}

            /* сброс */
            $('#date-picker-2,#date-picker-3,#date-picker-4').val('Фильтр по дате');
            $('.export .checkbox input.ace').prop('checked',false);
            /* --- */

            function getStatusTypeByText(statusText){
                var statusType;

                switch (statusText){
                    case "Подтвержден":
                        statusType = 2;
                        break;
                    case "Не подтвержден":
                        statusType = 1;
                        break;
                    case "Отменен":
                        statusType = 6;
                        break;
                }

                return statusType;
            }

            $('.status-dropdown .dropdown-menu li').click(function(e){
                e.preventDefault();

                var statusText = $(this).find('a').text();
                var statusType = getStatusTypeByText(statusText);

                var newOrders = thriftModule.client.getOrdersByStatus(0,nowTime + 180*day,statusType);

                if(deliveryFilterFlag){
                    newOrders = filterByDelivery(newOrders);
                }
                if(searchFilterFlag){
                    newOrders = filterBySearch(newOrders,$('#back-search').val());
                }
                if(dateFilterFlag){
                    newOrders = filterByDate(newOrders);
                }

                $('.orders-list').html("").append(createOrdersHtml(newOrders));

                initPlusMinus();

                statusFilterFlag = 1;
            });

            function filterByDelivery(orders){
                var ordersInFilter = [],
                    counter = 0;
                var ordersLength = orders.length;

                var deliveryText = $('.type-delivery-dropdown .btn-group-text').text();
                var deliveryType = boCommon.getDeliveryTypeByText(deliveryText);

                for (var i = 0; i < ordersLength; i++){
                    var orderDetails = thriftModule.client.getOrderDetails(orders[i].id);
                    if (deliveryType == orderDetails.delivery){
                        ordersInFilter[counter++] = orders[i];
                    }
                }

                return ordersInFilter;
            }

            function getMetaDate(){
                try {
                    var strDate = dPicker.val().split("-");
                    var strMonth="";
                    var year = strDate[2];
                    switch(strDate[1]){
                        case '01':
                            strMonth = "Jan";
                            break;
                        case '02':
                            strMonth = "Feb";
                            break;
                        case '03':
                            strMonth = "March";
                            break;
                        case '04':
                            strMonth = "Apr";
                            break;
                        case '05':
                            strMonth = "May";
                            break;
                        case '06':
                            strMonth = "June";
                            break;
                        case '07':
                            strMonth = "July";
                            break;
                        case '08':
                            strMonth = "Aug";
                            break;
                        case '09':
                            strMonth = "Sep";
                            break;
                        case '10':
                            strMonth = "Oct";
                            break;
                        case '11':
                            strMonth = "Nov";
                            break;
                        case '12':
                            strMonth = "Dec";
                            break;
                    }
                } catch(e){
                    alert(e + ' Функция getMetaDate');
                }
                return (Date.parse(strDate[0]+" "+strMonth+" "+year));
            }

            function filterByDate(orders){
                var orderDate = parseInt(getMetaDate()/1000);
                orderDate -= orderDate%86400;
                orderDate += day;

                var ordersLength = orders.length;
                var orderList = [];
                var counter = 0;
                for (var i = 0; i < ordersLength; i++){
                    if (orders[i].date == orderDate){
                        orderList[counter++] = orders[i];
                    }
                }

                return orderList;
            }

            $('.type-delivery-dropdown .dropdown-menu li').click(function(e){
                e.preventDefault();

                var orders = thriftModule.client.getOrdersByStatus(0,nowTime+180*day,0);
                var newOrders = filterByDelivery(orders);

                if(statusFilterFlag){
                    newOrders = filterByStatus(newOrders);
                }
                if(searchFilterFlag){
                    newOrders = filterBySearch(newOrders,$('#back-search').val());
                }
                if(dateFilterFlag){
                    newOrders = filterByDate(newOrders);
                }

                $('.orders-list').html("").append(createOrdersHtml(newOrders));

                initPlusMinus();

                deliveryFilterFlag = 1;

            });

            function initPlusMinus(){
                var ordersNoInit = $('.orders-no-init');
                initOrderPlusMinus(ordersNoInit);
                ordersNoInit.removeClass('orders-no-init');
            }

            function filterByStatus(orders){
                var counter = 0,
                    ordersInFilter=[];
                var ordersLength = orders.length;

                var statusText = $('.status-dropdown .btn-group-text').text();
                var statusType = getStatusTypeByText(statusText);

                for (i = 0; i < ordersLength; i++){
                    if (statusType == orders[i].status){
                        ordersInFilter[counter++] = orders[i];
                    }
                }
                return ordersInFilter;
            }

            /* ----------------------- Поиск -----------------------------------*/
            /* создаем массив из имен клиентов, без повторений */
            var clients = [];
            var counter = 0;
            $('.back-orders .order-item').each(function(){
                clients[counter++] =  $(this).find('.user-name').text();
            });
            var clientLength = clients.length;
            var clientsNoRepeat = [],
                repeatFlag = 0;
            counter = 0;
            for(var i = 0; i < clientLength-1 ;i++){
                repeatFlag = 0;
                for(var j = i+1; j < clientLength; j++){
                    if(clients[i] == clients[j]){
                        repeatFlag = 1;
                    }
                }
                if(!repeatFlag){
                    clientsNoRepeat[counter++] = clients[i];
                }
            }
            clientsNoRepeat[counter] = clients[clientLength-1];
            /* --- */

            function searchByWord(word){
                var orders = thriftModule.client.getOrdersByStatus(0,nowTime+180*day,0);

                var filterOrders = filterBySearch(orders,word);

                if(statusFilterFlag){
                    filterOrders = filterByStatus(filterOrders);
                }
                if(deliveryFilterFlag){
                    filterOrders = filterByDelivery(filterOrders);
                }
                if(dateFilterFlag){
                    filterOrders = filterByDate(filterOrders);
                }
                searchFilterFlag = 1;
                return filterOrders;
            }

            function filterBySearch(orders,word){
                var ordersLength = orders.length;
                var filterOrders = [];
                counter = 0;
                for(var i = 0; i < ordersLength; i++){
                    if(orders[i].userName.toLowerCase().indexOf(word.toLowerCase()) != -1){
                        filterOrders[counter++] = orders[i];
                    }
                }

                return filterOrders;
            }

            $('#back-search').focus(function(){
                $(this).autocomplete({
                    source: clientsNoRepeat,
                    select: function(event,ui){
                        $('.orders-list').html("").append(createOrdersHtml(searchByWord(ui.item['label'])));
                        initPlusMinus();
                    }
                });
            });

            $('.search-form').submit(function(e){
                e.preventDefault();

                var searchWord = $('#back-search').val();
                $('.orders-list').html("").append(createOrdersHtml(searchByWord(searchWord)));
                initPlusMinus();
            });

            /* ------------------------------- Конец Поиск ---------------------------- */

        }

        return  {
            initBackofficeOrders: initBackofficeOrders
        };

    });