/*
* делаю переменные глобальными, чтобы они были видны в файле datepicker
* */
var deliveryFilterFlag= 0,
    statusFilterFlag = 0,
    dateFilterFlag = 0,
    searchFilterFlag = 0;

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

    function showAllOrders(){
        var orders = client.getOrdersByStatus(0,nowTime+180*day,0);
        $('.orders-list').html("").append(createOrdersHtml(orders));

        var ordersNoInit = $('.orders-no-init');
        initOrderPlusMinus(ordersNoInit);
        ordersNoInit.removeClass('orders-no-init');

        deliveryFilterFlag = 0;
        statusFilterFlag = 0;
        dateFilterFlag = 0;
        searchFilterFlag = 0;
    }
    showAllOrders();

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
                if (orderDetails.deliveryTo){

                    var orderDay = tempDate.getDate();
                    orderDay = (orderDay < 10)? "0" + orderDay: orderDay;

                    var orderMonth = tempDate.getMonth()+1;
                    orderMonth = (orderMonth < 10)? "0" + orderMonth: orderMonth;

                ordersHtml += '<div class="order-item orders-no-init" data-orderid="'+ orders[i].id +'">'+
                    '<table class="orders-tbl">'+
                    '<tbody>'+
                    '<tr>'+
                    '<td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>'+
                    '<td class="td2">'+i+'. Заказ '+orders[i].id +'</td>'+
                    '<td class="td8 user-name">'+
                    orders[i].userName +
                    '</td>'+
                    '<td class="td3">'+ orderDay +"."+orderMonth+"."+tempDate.getFullYear()+ '</td>'+
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

    var dPicker = $('.date-picker');

    globalUserAuth = true;
    dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    var datepickerFunc = {
        createOrdersHtml: createOrdersHtml,
        initOrderPlusMinus: initOrderPlusMinus,
        setSidebarHeight: setSidebarHeight,
        filterByStatus: filterByStatus,
        filterByDelivery: filterByDelivery,
        filterBySearch: filterBySearch
    };

    dPicker.datepicker('setVarOrderDates',datepickerFunc);


    $('.reset-filters').click(function(){
        showAllOrders();
        $('.type-delivery-dropdown .btn-group-text').text('Тип доставки');
        $('.status-dropdown .btn-group-text').text('Статус заказа');
        $('#back-search').val('Поиск по имени клиента или номеру телефона');
        dPicker.val('Фильтр по дате');
    });

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

    $('.status-dropdown .dropdown-menu li').click(function(e){
        e.preventDefault();

        var statusText = $(this).find('a').text();
        var statusType = getStatusTypeByText(statusText);

        var newOrders = client.getOrdersByStatus(0,nowTime + 180*day,statusType);

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
        var deliveryType = getDeliveryTypeByText(deliveryText);

        for (var i = 0; i < ordersLength; i++){
            var orderDetails = client.getOrderDetails(orders[i].id);
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
                    strMonth = "Sen";
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

        var orders = client.getOrdersByStatus(0,nowTime+180*day,0);
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
        var orders = client.getOrdersByStatus(0,nowTime+180*day,0);

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
            if(orders[i].userName.toLowerCase() == word.toLowerCase()){
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

    /* import */
    var dataCSV = [],
        elems, rowCount,colCount;

    var fileUrl;
    $('.form-import').submit(function(e){
        e.preventDefault();

        var path = $('#import-data').val();
        var data = path;
        var importPublic = $('#import-public').val();
        path = path.split("\\");
        var pathLength = path.length;
        var fname = path[pathLength-1];

        var fd = new FormData();
        var input = $('.form-import #import-data');
        fd.append( 'data', input[0].files[0]);

        $.ajax({
            type: "POST",
            url: "/file/",
            cache: false,
            processData: false,
            //contentType: 'multipart/form-data',
            contentType: false,
            data: fd,
            /*data: {
                fname: fname,
                data: data,
                public: importPublic
            },*/
            success: function(html) {
                fileUrl = html;
                var matrixAsList = client.parseCSVfile(fileUrl);
                elems = matrixAsList.elems;
                rowCount = matrixAsList.rowCount;
                var dataCSVLength = elems.length;
                colCount = dataCSVLength/rowCount;
                var counter = 0;

                for(var i = 0; i < rowCount; i++){
                    dataCSV[i] = [];
                    for(var j = 0; j < colCount; j++){
                        dataCSV[i][j] = elems[counter++];
                     //   console.log(dataCSV[i][j]);
                    }
                }
            }
        });
    });

    $('.checkbox .lbl').click(function(){
        $(this).closest('.checkbox').toggleClass('active');
    });

    $('.import-dropdown .dropdown-menu li').click(function(){
       var ind = $(this).index();
       var currentChecklist;
       switch (ind){
           case(0):
               currentChecklist = $('.products-checklist');
               break;
           case(1):
               currentChecklist = $('.categories-checklist');
               break;
           case(2):
               currentChecklist = $('.producers-checklist');
               break;
       }
        $('.checklist').hide();
        currentChecklist.slideDown();
    });

    var dataCSVShow;
    var constColProductsCount = 21;
    var checkboxCount = $('.import-checklist .products-checklist').find('.checkbox').length;
    $('.import-btn').click(function(e){
        e.preventDefault();

        dataCSVShow = dataCSV;
        var colCountLocal = colCount;
            for(var i = 0; i < rowCount; i++){
                // временная мера, для тестирования
                // убирает из массива столбцы которые пока не задействованы
                dataCSVShow[i].splice(checkboxCount,constColProductsCount-checkboxCount);
                colCountLocal = dataCSVShow[i].length;
            }

            var counterRemove = 0;
            for(var j = 0; j < colCountLocal; j++){
                // формируем массив для вывода на странице
                var associateCheckbox = $('.import-checklist .products-checklist').find('.checkbox:eq('+ j +')');
                //alert(j+" "+associateCheckbox.hasClass('active'));

                if(!associateCheckbox.hasClass('active')){
                    for(i = 0; i < rowCount; i++){
                        dataCSVShow[i].splice(j-counterRemove,1);
                    }
                    counterRemove++;
                }
            }
        /*for(i = 0; i < rowCount; i++){
            alert(dataCSVShow[i].length);
        }*/

        var importType = $('.import-dropdown .btn-group-text').text();
        var ImExType;// = com.vmesteonline.be.shop.ImExType;
        /*for(var p in ImExType){
            alert(p+" "+ImExType[p]);
        }*/
        switch (importType){
            case 'Продукты':
                ImExType = com.vmesteonline.be.shop.ImExType.IMPORT_PRODUCTS;
                break;
            case 'Категории продуктов':
                ImExType = com.vmesteonline.be.shop.ImExType.IMPORT_CATEGORIES;
                break;
            case 'Производители':
                ImExType = com.vmesteonline.be.shop.ImExType.IMPORT_PRODUCERS;
                break;
        }

        var fieldsMap = [];
        var filedsCounter = 0;
        $('.checkbox.active').each(function(){
            fieldsMap[filedsCounter++] = parseInt($(this).data('exchange'));
        });
        var importElement = new com.vmesteonline.be.shop.ImportElement;
        importElement.type = ImExType;
        importElement.filename = 'filename';
        importElement.fieldsMap = fieldsMap;
        importElement.url = fileUrl;
        var temp = [];
        temp[0] = importElement;

        var dataSet = new com.vmesteonline.be.shop.DataSet;
        dataSet.name = "name";
        dataSet.date = nowTime;
        dataSet.data = temp;
        client.importData(dataSet);

    });

   /* import */


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

});