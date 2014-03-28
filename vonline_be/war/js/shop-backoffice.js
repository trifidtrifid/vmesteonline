/*var control = document.getElementById("file");
control.addEventListener("change", function(event) {
    // Когда происходит изменение элементов управления, значит появились новые файлы
    var i = 0,
        files = control.files,
        len = files.length;

    for (; i < len; i++) {
        console.log("Filename: " + files[i].name);
        console.log("Type: " + files[i].type);
        console.log("Size: " + files[i].size + " bytes");
    }

}, false);*/
$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/ShopService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

    transport = new Thrift.Transport("/thrift/UserService");
    protocol = new Thrift.Protocol(transport);
    var userServiceClient = new com.vmesteonline.be.UserServiceClient(protocol);
    var w = $(window);

    var nowTime = parseInt(new Date().getTime()/1000);
    nowTime -= nowTime%86400;
    var day = 3600*24;
    var orders = client.getOrders(0,nowTime+180*day);
    $('.orders-list').append(createOrdersHtml(orders));

    var ordersNoInit = $('.orders-no-init');
    initOrderPlusMinus(ordersNoInit);
    ordersNoInit.removeClass('orders-no-init');
    var deliveryFilterFlag= 0,
        statusFilterFlag = 0;

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

        var nowTime = parseInt(new Date().getTime()/1000);
        nowTime -= nowTime%86400;
        var day = 3600*24;
        var newOrders = client.getOrdersByStatus(0,nowTime + 180*day,statusType);
        var newOrdersLength = newOrders.length;
        var ordersInFilter = [],
            counter = 0;
        if(deliveryFilterFlag){
           var deliveryText = $('.type-delivery-dropdown .btn-group-text').text();
           var deliveryType = getDeliveryTypeByText(deliveryText);
            for (var i = 0; i < newOrdersLength; i++){
                var orderDetails = client.getOrderDetails(newOrders[i].id);
                if (deliveryType == orderDetails.delivery){
                    ordersInFilter[counter++] = client.getOrder(newOrders[i].id);
                }
            }
            newOrders = ordersInFilter;
        }

        $('.orders-list').html("").append(createOrdersHtml(newOrders));
        var ordersNoInit = $('.orders-no-init');
        initOrderPlusMinus(ordersNoInit);
        ordersNoInit.removeClass('orders-no-init');

        statusFilterFlag = 1;
    });

    function getDeliveryTypeByText(deliveryText){
        var deliveryType;

        switch (deliveryText){
            case "Самовывоз":
                deliveryType = "1";
                break;
            case "Курьер рядом":
                deliveryType = "2";
                break;
            case "Курьер далеко":
                deliveryType = "3";
                break;
        }
        return deliveryType;
    }

    $('.type-delivery-dropdown .dropdown-menu li').click(function(e){
        e.preventDefault();

       var deliveryText = $(this).find('a').text();
       var deliveryType = getDeliveryTypeByText(deliveryText);

        var nowTime = parseInt(new Date().getTime()/1000);
        nowTime -= nowTime%86400;
        var day = 3600*24;
        var orders = client.getOrders(0,nowTime+180*day);
        var ordersLength = orders.length;
        var orderDetails,
            newOrders = [],
            counter = 0,
            ordersInFilter=[];


        for(var i = 0; i < ordersLength; i++){
            orderDetails = client.getOrderDetails(orders[i].id);
            if (orderDetails.delivery == deliveryType){
                newOrders[counter++] = orders[i];
            }
        }
        var newOrdersLength = newOrders.length;
        counter = 0;

        if(statusFilterFlag){
            var statusText = $('.status-dropdown .btn-group-text').text();
            var statusType = getStatusTypeByText(statusText);
            for (i = 0; i < newOrdersLength; i++){
                if (statusType == newOrders[i].status){
                    ordersInFilter[counter++] = client.getOrder(newOrders[i].id);
                }
            }
            newOrders = ordersInFilter;
        }

        $('.orders-list').html("").append(createOrdersHtml(newOrders));

        var ordersNoInit = $('.orders-no-init');
        initOrderPlusMinus(ordersNoInit);
        ordersNoInit.removeClass('orders-no-init');

        deliveryFilterFlag = 1;

    });

    function initOrderPlusMinus(selector){
        selector.find('.plus-minus').click(function(e){
            e.preventDefault();

            var orderItem = $(this).closest('.order-item');
            var orderProducts = orderItem.find('.order-products');
            var orderDetails = client.getOrderDetails(orderItem.data('orderid'));
            var orderLines = orderDetails.odrerLines;
            var orderLinesLength = orderLines.length;
            //var order = client.getOrder(orderItem.data('orderid'));

            if (orderProducts.find('.catalog').length == 0){
                orderProducts.append(createOrdersProductHtml(orderDetails));

                for (var i = 0; i < orderLinesLength; i++){
                    InitSpinner(orderProducts.find('tbody tr:eq('+ i +') .spinner1'),orderLines[i].quantity);
                }

                //InitAddToBasket(orderProducts.find('.fa-shopping-cart'));
                //InitProductDetailPopup(orderProducts.find('.product-link'));
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

    function InitSpinner(selector,spinnerValue,itsBasket){
        selector.ace_spinner({value:spinnerValue,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
            .on('change', function(){
            });
    }

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
                //var productDetails = client.getProductDetails(orderLines[j].product.id);
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
                    orderLines[j].quantity+
                    '</td>'+
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
                var orderDetails = client.getOrderDetails(orders[i].id);
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
                ordersHtml += '<div class="order-item orders-no-init" data-orderid="'+ orders[i].id +'">'+
                    '<table class="orders-tbl">'+
                    '<tbody>'+
                    '<tr>'+
                    '<td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>'+
                    '<td class="td2">Заказ N '+i+'</td>'+
                    '<td class="td8 user-name">'+
                    orders[i].userName +
                    '</td>'+
                    '<td class="td3">'+ tempDate.getDate() +"."+tempDate.getMonth()+"."+tempDate.getFullYear()+ '</td>'+
                    '<td class="td4">'+
                    '<div class="order-status">'+orderStatus +'</div>'+
                    '</td>'+
                    '<td class="td5"><span class="delivery-status">'+ orderDelivery +'</span><br> ' +
                    orderDetails.deliveryTo.city.name+", "+orderDetails.deliveryTo.street.name+" "+orderDetails.deliveryTo.building.fullNo+", кв."+
                    orderDetails.deliveryTo.flatNo+
                    '</td>'+
                    '<td class="td6">'+ orders[i].totalCost.toFixed(1) +'</td>'+
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
            alert(e+" Функция createOrdersHtml");
        }
        return ordersHtml;
    }

    var dPicker = $('.date-picker');

    globalUserAuth = true;
    dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    var datepickerFunc = {
        createOrdersHtml: createOrdersHtml,
        initOrderPlusMinus: initOrderPlusMinus,
        setSidebarHeight: setSidebarHeight
    };

    dPicker.datepicker('setVarOrderDates',datepickerFunc);

    function setSidebarHeight(){
        try{

            var mainContent = $('.main-content');

            if (mainContent.height() > w.height()){
                $('.shop-right').css('height', mainContent.height()+45);
            }else{
                $('.shop-right').css('height', '100%');
            }
        }catch(e){
            alert(e+" Функция setSidebarHeight");
        }
    }

    $('.nav-list a').click(function(e){
        e.preventDefault();
        $('.back-tab').hide();
        var index = $(this).parent().index();
        switch (index){
            case 0:
                $('.back-orders').show();
                break;
            case 1:
                $('.import').show();
                break;
            case 2:
                $('.export').show();
                break;
        }
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });

    var clients = [];
    var counter = 0;
    $('.back-orders .order-item').each(function(){
        clients[counter++] =  $(this).find('.td8.user-name').text();
    });
    var clientLength = clients.length;
    var clientsNoRepeat = [],
        repeatFlag = 0;
    counter = 0;
    for(var i = 0; i < clientLength-1 ;i++){
        for(var j = i+1; j < clientLength; j++){
          if(clients[i]==clients[j]){
             repeatFlag = 1;
          }
        }
        if(!repeatFlag){
            clientsNoRepeat[counter++] = clients[i];
        }
    }
    clientsNoRepeat[counter] = clients[clientLength-1];

    $('#back-search').focus(function(){
        $(this).autocomplete({
            source: clientsNoRepeat,
            select: function(event,ui){
                var orders = client.getOrders(0,nowTime+180*day);
                var ordersLength = orders.length;
                var filterOrders = [];
                counter = 0;
                for(var i = 0; i < ordersLength; i++){
                    if(orders[i].userName == ui.item['label']){
                        filterOrders[counter++] = orders[i];
                    }
                }
                $('.orders-list').html("").append(createOrdersHtml(filterOrders));
            }
        });
    });
    /* import */

/*    $('.form-import').submit(function(e){
        e.preventDefault();
        var path = $('#file').val();
        alert(path);
        path = path.split("\\");
        var pathLength = path.length;
        var fname = path[pathLength-1];

        $.post(
            "http://localhost:8888/file/",
            {
              fname: fname,
              extUrl: path
            },
            function(data, textStatus, jqXHR){

            }
        );
    });*/

/*    $('.form-import input').click(function(){
        var importElement = {
            type: 'type',
            filename: 'filename',
            fieldsMap: 'fieldsMap',
            url: 'url',
            fieldsData: 'fieldsData'
        };
        var data = {
            id: 1,
            name: "name",
            date: 11111111111,
            data: importElement
        };
        //client.importData(data);
    });*/

});