require.config({
    baseUrl: "/build",
    paths: {
        "jquery"   : "../js/lib/jquery-2.1.1.min",
        "bootstrap": "../js/lib/bootstrap.min",
        "ace_extra": "../js/lib/ace-extra.min",
        "ace_elements": "../js/lib/ace-elements.min",
        "jquery_ui": "../js/lib/jquery-ui-1.10.3.full.min",
        "flexslider": "../js/lib/jquery.flexslider-min",
        "ace_spinner": "../js/lib/fuelux/fuelux.spinner",
        "datepicker-backoffice": "../js/bootstrap-datepicker-backoffice",
        "datepicker-ru": "../js/lib/date-time/locales/bootstrap-datepicker.ru",
        "multiselect": "../js/lib/jquery.multiselect.min",
        "bootbox":"../js/bootbox.min"
    },
    shim:{
        'ace_spinner':{
            deps: ['jquery',"ace_extra","ace_elements","jquery_ui"],
            exports: 'ace_spinner'
        },
        'jquery_ui':{
            deps: ['jquery'],
            exports: 'jquery_ui'
        },
        'bootstrap':{
            deps: ['jquery'],
            exports: 'bootstrap'
        },
        'datepicker-backoffice':{
            deps: ['jquery','jquery_ui'],
            exports: 'datepicker-backoffice'
        },
         'datepicker-ru':{
         deps: ['jquery','datepicker-backoffice'],
         exports: 'datepicker-ru'
         }  ,
        'flexslider':{
            deps: ['jquery'],
            exports: 'flexslider'
        },
        'multiselect':{
            deps: ['jquery','jquery_ui'],
            exports: 'multiselect'
        },
        'bootbox':{
            deps: ['jquery','bootstrap'],
            exports: 'bootbox'
        }
    }
});

/*
* делаю переменные глобальными, чтобы они были видны в файле datepicker
* */
var deliveryFilterFlag= 0,
    statusFilterFlag = 0,
    dateFilterFlag = 0,
    searchFilterFlag = 0,
    orders;

require(["jquery",'shop-initThrift.min','commonM.min','shop-orders.min','datepicker-backoffice','datepicker-ru','bootstrap','multiselect'],
    function($,thriftModule,commonM,ordersModule) {

if($('.container.backoffice').hasClass('noAccess')){
    bootbox.alert("У вас нет прав доступа !", function() {
        document.location.replace("./shop.jsp");
    });
}else{

        commonM.init();

        var w = $(window);

        var nowTime = parseInt(new Date().getTime()/1000);
        nowTime -= nowTime%86400;
        var day = 3600*24;

        function showAllOrders(){
            try{
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
        showAllOrders();

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
                    (orderLines[j].product.imageURL) ? myPic = orderLines[j].product.imageURL : myPic = 'i/no-photo.png';
                    ordersProductsHtml += '<tr class="product" data-prepack="'+ orderLines[j].product.prepackRequired +'" data-productid="'+ orderLines[j].product.id +'">'+
                        '<td>'+
                        '<a href="#" class="product-link">'+
                        '<div class="product-pic"><img src="'+ myPic +'" alt="картинка"/></div>'+
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
        console.log('---------------------');

        dPickerExport.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
            $(this).prev().focus();
        });
        $('#date-picker-6').datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
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
        dPickerExport.datepicker('setVarOrderDates',datepickerFunc);


        $('.reset-filters').click(function(){
            showAllOrders();
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
            var deliveryType = getDeliveryTypeByText(deliveryText);

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

        /* import */
        var dataCSV = [],
            elems, rowCount,colCount;

        var fileUrl;
        $('.form-import').submit(function(e){
            e.preventDefault();
            var path = $('#import-data').val();
            if($('.import-dropdown .btn-group-text').text() == 'Тип импортируемых данных'){
                $('.import').find('.error-info').text('Пожалуйста, укажите тип импортируемых данных.').show();
            }else if(!path){
                $('.import').find('.error-info').text('Пожалуйста, выберите файл.').show();
            }else{
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

                        var matrixAsList = thriftModule.clientBO.parseCSVfile(fileUrl);
                        elems = matrixAsList.elems;
                        rowCount = matrixAsList.rowCount;
                        var dataCSVLength = elems.length;
                        colCount = dataCSVLength/rowCount;
                        var counter = 0;
                        dataCSV = [];

                        for(var i = 0; i < rowCount; i++){
                            dataCSV[i] = [];
                            for(var j = 0; j < colCount; j++){
                                dataCSV[i][j] = elems[counter++];
                            }
                        }

                        // формирование таблицы для вывода на экран
                        var importType = $('.import-dropdown .btn-group-text').text();
                        var dropdownColArray = [];
                        var dropdownColArrayFieldType = [];
                        var ExField = com.vmesteonline.be.shop.bo.ExchangeFieldType;
                        counter = 0;

                        switch (importType){
                            case "Продукты" :
                                for(var p in ExField){
                                    if (ExField[p] >= 300 && ExField[p] < 330  ){
                                        dropdownColArray[counter] = p;
                                        dropdownColArrayFieldType[counter++] = ExField[p];
                                    }
                                }
                                break;
                            case "Категории продуктов" :
                                for(p in ExField){
                                    if (ExField[p] >= 200 && ExField[p] < 230  ){
                                        dropdownColArray[counter] = p;
                                        dropdownColArrayFieldType[counter++] = ExField[p];
                                    }
                                }
                                break;
                            case "Производители" :
                                for(p in ExField){
                                    if (ExField[p] >= 100 && ExField[p] < 130  ){
                                        dropdownColArray[counter] = p;
                                        dropdownColArrayFieldType[counter++] = ExField[p];
                                    }
                                }
                                break;
                        }

                        //использую эти переменные для меню дроплауна, которое
                        //должно быть неизменным независимо от кол-ва загруженных столбцов
                        var dropdownColArrayMenu = dropdownColArray.slice(0);
                        var dropdownColArrayMenuFieldType = dropdownColArrayFieldType.slice(0);

                        var begin,end;
                        if(colCount < counter ){
                            // если стобцов в таблице меньше чем default удаляем лишние default столбцы
                            begin = counter;
                            end = colCount;

                            for(i = begin-1; i >= end ; i--){
                                dropdownColArray.pop();
                                dropdownColArrayFieldType.pop();
                            }
                        }
                        if(colCount > counter){
                            // если столбцов больше, то добавляем столбцы со значеним SKIP
                            begin = colCount;
                            end = counter;

                            for(i = begin-1; i >= end ; i--){
                                dropdownColArray[i] = skipConst;
                                dropdownColArrayFieldType[i] = -1;
                            }
                        }

                        var importHtml = '<table><thead>' +
                            '<tr>' +
                            createImportDropdownLine(dropdownColArray,dropdownColArrayFieldType,dropdownColArrayMenu,dropdownColArrayMenuFieldType)+
                            '</tr>'+
                            '</thead>' +
                            '<tbody>'+
                            createImportTable(dataCSV,dropdownColArray.length)+
                            '</tbody>' +
                            '</table>';

                        $('.import-table').html("").prepend(importHtml).parent().show();

                        $('.top-scroll').remove();
                        DoubleScroll(document.getElementById('doublescroll'));

                        initShowFullText();
                        initCloseFullText();

                        $('.import-field-dropdown .dropdown-toggle').click(function(e){
                            e.preventDefault();

                            var ind = $(this).closest('td').index();
                            var coordX = 0, tdIndex = 0;
                            $('.import-table table').find('td').each(function(){
                                if (tdIndex < ind){
                                    coordX += $(this).width();
                                }
                                tdIndex ++;
                            });
                            coordX -= $('.import-table').scrollLeft();

                            var coordY = 94;
                            $(this).parent().find('.dropdown-menu').css({'left':coordX,'top':coordY});
                        });

                        $('.import-field-dropdown .dropdown-menu').find('li a').click(function(e){
                            e.preventDefault();

                            var fieldName = $(this).parent().text();
                            var fieldType = $(this).parent().data('fieldtype');

                            $('.import-field-dropdown').each(function(){
                               if ($(this).find('.btn-group-text').text() == fieldName){
                                   $(this).find('.dropdown-toggle').attr('data-fieldtype',-1);
                                   $(this).find('.btn-group-text').text(skipConst);
                               }
                            });

                            var currentDropdown = $(this).closest('.import-field-dropdown');
                            //alert(fieldType);
                            currentDropdown.find('.btn-group-text').text(fieldName).parent().data('fieldtype',fieldType);
                            //currentDropdown.find('.btn-group-text').text(fieldName).closest('.btn').dataset.fieldtype = fieldType;
                            //alert(currentDropdown.find('.btn-group-text').text(fieldName).closest('.btn').data('fieldtype'));
                        });

                    }
                });

            }
        });

        function initShowFullText(){
            $('.show-full-text').click(function(e){
                e.preventDefault();
                if($(this).closest('.export-table').length > 0){
                    var coordX = e.pageX-330;
                    var coordY = e.pageY-90;
                }else{
                    coordX = e.pageX-200;
                    coordY = e.pageY-200;
                }
                $('.full-text').hide();
                $(this).parent().find('.full-text').css({'left':coordX,'top':coordY}).show();
            });
        }

        function initCloseFullText(){
            $('.full-text .close').click(function(e){
                e.preventDefault();
                $(this).parent().hide();
            });
        }

        function createImportDropdownLine(dropdownColArray,dropdownColArrayFieldType,dropdownColArrayMenu,dropdownColArrayMenuFieldType){

            var importDropdownLine = "";
            var importDropdownLineLength = dropdownColArray.length;
            var dropdownColArrayMenuLength = dropdownColArrayMenu.length;
            //alert(importDropdownLineLength+" "+dropdownColArrayMenuLength);

            var importDropdownMenu = "",
                haveSkip = false;
            for(var i = 0; i < dropdownColArrayMenuLength; i++){
                if(!haveSkip) importDropdownMenu += '<li data-fieldtype="'+ dropdownColArrayMenuFieldType[i] +'"><a href="#">'+ dropdownColArrayMenu[i] +'</a></li>';

                if(dropdownColArrayMenu[i] == skipConst){
                    // на случай если добавляется таблица с большим чем Default кол-вом стобцов, чтобы SKIP не
                    // дублировался в dropdown
                    haveSkip = true;
                }
            }
            if(!haveSkip) importDropdownMenu += '<li data-fieldtype="'+ -1 +'"><a href="#">'+ skipConst +'</a></li>';

            for(i = 0; i < importDropdownLineLength; i++){
                importDropdownLine += '<td>' +
                    '<div class="btn-group import-field-dropdown">'+
                    '<button data-toggle="dropdown" data-fieldtype="'+ dropdownColArrayFieldType[i] +'" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                    '<span class="btn-group-text">'+ dropdownColArray[i] +'</span>'+
                    '<span class="icon-caret-down icon-on-right"></span>'+
                    '</button>'+
                    '<ul class="dropdown-menu dropdown-blue">'+
                    importDropdownMenu+
                    '</ul>'+
                    '</div>'+
                    '</td>';
            }

            return importDropdownLine;

        }

        function createImportTable(dataCSV,colCount){
            var importTable = "";
            var dataCSVLength = dataCSV.length;

            for(var i = 0; i < dataCSVLength; i++){
                importTable += '<tr>'+
                    createImportLine(dataCSV[i],colCount)+
                    '</tr>';
            }

            function createImportLine(dataCSVrow,colCount){
                var importLine = "";
                var cont = "",fullText;
                //alert(dataCSVrow.length+" "+colCount);

                for(var i = 0; i < colCount; i++){
                    cont = dataCSVrow[i];
                    if (cont && cont.length > 30){
                        // урезаем длинный текст
                        fullText = cont;
                        cont = cont.slice(0,30);
                        cont += " <a class='show-full-text' href='#'>...</a>"+
                            '<div class="full-text"><a href="#" class="close">×</a>'+ fullText +'</div>';
                    }
                    importLine += '<td>'+
                        cont +
                        '</td>';
                }

                return importLine;
            }
            return importTable;
        }

        $('#import-data').click(function(){
            $('.import').find('.error-info').hide();
        });

        $('.checkbox .lbl').click(function(){
            $(this).closest('.checkbox').toggleClass('active');
        });

        $('.import-dropdown .dropdown-menu li').click(function(){
            $('.import').find('.error-info').hide();
        });

        var skipConst = "SKIP";
        $('.import-btn').click(function(e){
            e.preventDefault();

            var dropdowns = $('.import-field-dropdown');
            var dropdownLength = dropdowns.length;
            var repeatDropdown = "";
            var haveRepeat = false;
            dropdowns.each(function(){
                if(!haveRepeat){
                    var ind = $(this).parent().index();
                    var dropdownName = $(this).find('.btn-group-text').text();
                    for(var i = ind+1; i < dropdownLength; i++){
                        if (dropdownName == dropdowns.eq(i).find('.btn-group-text').text() && dropdownName != skipConst){
                            repeatDropdown = dropdownName;
                            haveRepeat = true;
                            break;
                        }
                    }
                }
            });

            var confirmInfo = $(this).closest('.back-tab').find('.confirm-info');
            if(haveRepeat){
                confirmInfo.text('Вы указали два столбца с одинаковым занчением : ' + repeatDropdown ).show();
            }else{
                confirmInfo.hide();

                var importType = $('.import-dropdown .btn-group-text').text();
                var ImExType;

                switch (importType){
                    case 'Продукты':
                        ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_PRODUCTS;
                        break;
                    case 'Категории продуктов':
                        ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_CATEGORIES;
                        break;
                    case 'Производители':
                        ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_PRODUCERS;
                        break;
                }

                var fieldsMap = [];
                var fieldsCounter = 0;
                $('.import .import-field-dropdown').each(function(){
                    var fieldName = $(this).find('.btn-group-text').text();
                    if(fieldName != skipConst){
                        //alert(fieldsCounter+ " " + $(this).find('.btn .btn-group-text').text() +" "+ $(this).find('.btn').data('fieldtype'));
                        fieldsMap[fieldsCounter] = parseInt($(this).find('.btn').data('fieldtype'));
                    }
                    fieldsCounter++;
                });

                var importElement = new com.vmesteonline.be.shop.bo.ImportElement;
                importElement.type = ImExType;
                importElement.filename = 'filename';
                importElement.fieldsMap = fieldsMap;
                importElement.url = fileUrl;
                var temp = [];
                temp[0] = importElement;

                var dataSet = new com.vmesteonline.be.shop.bo.DataSet;
                dataSet.name = "name";
                dataSet.date = nowTime;
                dataSet.data = temp;


                    $('.loading').fadeIn(function(){
                        try{
                            thriftModule.clientBO.importData(dataSet);
                            confirmInfo.text('Данные успешно импортированы.').addClass('info-good').show();
                        }catch(e){
                            confirmInfo.text('Ошибка импорта.').show();
                        }
                        $('.loading').fadeOut(0);
                        setTimeout(hideConfirm,3000);
                    });

                function hideConfirm(){
                    $('.confirm-info').hide();
                }

            }

        });

        /* /import */

        /* export */


        $('.export-orders-checklist select, .export-products-checklist select, .export-packs-checklist select').multiselect({
            noneSelectedText: "Опции",
            selectedText: "Опции (# выбрано)",
            checkAllText: "Выбрать все",
            uncheckAllText: "Отменить все"
        });
        $('.ui-multiselect').addClass('btn btn-sm btn-info no-border');
        $('.ui-multiselect .ui-icon').addClass('icon-caret-down');

        $('.export .nav-tabs').find('li').click(function(){
            var ind = $(this).index();

            var contentH = $('.export .tab-pane:eq('+ ind +')').addClass('active').height();
            setSidebarHeight(contentH);
        });

        $('.export-btn').click(function(e){
            e.preventDefault();
            var currentTab = $(this).closest('.tab-pane');
            var deliveryText = currentTab.find('.export-delivery-dropdown .btn-group-text').text();
            //var selectOrderDate = currentTab.find('.datepicker-export').data('selectorderdate');
            var selectOrderDate = currentTab.find('.datepicker-export').attr('data-selectorderdate');

            if(!selectOrderDate){
                currentTab.find('.error-info').text('Пожалуйста, укажите дату с заказом.').show();
            }else{
                currentTab.find('.error-info').hide();
                currentTab.find('.confirm-info').hide();

                var tabs = $('.export .nav-tabs').find('li');
                var currentInd = 0;
                tabs.each(function(){
                    if ($(this).hasClass('active')){
                        currentInd = $(this).index();
                    }
                });

                var deliveryType;
                (deliveryText != 'Тип доставки' && deliveryText != 'Все') ? deliveryType = getDeliveryTypeByText(deliveryText):deliveryType=0;

                var dataSet;

                try{
                    switch (currentInd){
                        case 0:
                            var exportFieldsOrder = getExportFields('orders');

                            var exportFieldsOrderLine = getExportFields('orderLine');

                            dataSet = thriftModule.clientBO.getTotalOrdersReport(selectOrderDate,deliveryType,exportFieldsOrder.fieldsMap,exportFieldsOrderLine.fieldsMap);

                            var tablesCount;

                            if(dataSet.data){

                                tablesCount = dataSet.data.length;  // кол-во таблиц в нашем отчете

                                drawExportTables(dataSet,tablesCount,exportFieldsOrder,exportFieldsOrderLine);

                            }

                            break;
                        case 1:
                            var exportFields = getExportFields('products');

                            dataSet = thriftModule.clientBO.getTotalProductsReport(selectOrderDate,deliveryType,exportFields.fieldsMap);

                            if(dataSet.data){

                                tablesCount = dataSet.data.length;  // кол-во таблиц в нашем отчете

                                drawExportTables(dataSet,tablesCount,exportFields);

                            }

                            break;
                        case 2:
                            exportFields = getExportFields('packs');

                            dataSet = thriftModule.clientBO.getTotalPackReport(selectOrderDate,deliveryType,exportFields.fieldsMap);

                            if(dataSet.data){

                                //tablesCount = 1;  // кол-во таблиц в нашем отчете
                                tablesCount = dataSet.data.length;

                                drawExportTables(dataSet,tablesCount,exportFields);
                            }

                            break;
                    }
                    initShowFullText();
                    initCloseFullText();

                    if(!dataSet.data) currentTab.find('.confirm-info').text('Нет данных на такое сочетание даты и типа доставки.').show();

                    setSidebarHeight();

                }catch(e){
                    currentTab.find('.confirm-info').text('Ошибка экспорта.').show();
                }
            }
        });

        function getExportFields(exportType){
            var fieldsMap = [];
            var fieldsCounter = 0;
            var currentCheckbox;
            var headColArray = [];
            counter = 0;

            var delimiterIndexes = [];
            var orderCheckboxes = $('.ui-multiselect-menu:eq(0) .ui-multiselect-checkboxes li');

            orderCheckboxes.each(function(){
                if($(this).hasClass('ui-multiselect-optgroup-label')){
                    delimiterIndexes[counter++] = $(this).index();
                }
            });

            switch (exportType){
                case 'orders':
                    var myCheckboxes = orderCheckboxes.slice(delimiterIndexes[0]+1,delimiterIndexes[1]);
                    currentCheckbox = myCheckboxes.find('input[aria-selected="true"]');
                    break;
                case 'orderLine':
                    myCheckboxes = orderCheckboxes.slice(delimiterIndexes[1]+1);
                    currentCheckbox = myCheckboxes.find('input[aria-selected="true"]');
                    break;
                case 'products':
                    var productsCheckboxes = $('.ui-multiselect-menu:eq(1) .ui-multiselect-checkboxes li');
                    currentCheckbox =  productsCheckboxes.find('input[aria-selected="true"]');
                    //currentCheckbox =  $('.export-products-checklist .checkbox.active');
                    break;
                case "packs":
                    var packsCheckboxes = $('.ui-multiselect-menu:eq(2) .ui-multiselect-checkboxes li');
                    currentCheckbox =  packsCheckboxes.find('input[aria-selected="true"]');
                    break;
            }

            counter = 0;
            currentCheckbox.each(function(){
                //fieldsMap[fieldsCounter++] = parseInt($(this).data('exchange'));
                fieldsMap[fieldsCounter++] = parseInt($(this).val());
                headColArray[counter++] = $(this).find('+span').text();
            });

            return {fieldsMap: fieldsMap,headColArray: headColArray }
        };

        /*function getExportFieldsByMap(fieldsMap){
         var counter = 0;

         for (var p in fieldsMap){
         console.log(p+" "+fieldsMap[p]);
         }
         }*/

        function drawExportTables(dataSet,tablesCount,exportFields,exportFieldsOrderLine){

            var currentExportTable = $('.tab-pane.active').find('.export-table');
            currentExportTable.html("");
            var exportData = [];
            for(var z = tablesCount-1 ; z >= 0; z--){
                // формирование данных экспорта для каждой таблицы
                exportData = [];
                var fieldsData = dataSet.data[z].fieldsData;
                var rowCount = fieldsData.rowCount;
                // округляем до наибольшего целого
                var colCount = Math.ceil(fieldsData.elems.length/rowCount);
                var counter = 0;
                for(var i = 0; i < rowCount; i++){
                    exportData[i] = [];
                    for(var j = 0; j < colCount; j++){
                        exportData[i][j] = fieldsData.elems[counter++];
                    }
                }

                //var headColArray = exportFields.headColArray;
                var headColArray, beginRow = 0;

                if(dataSet.data[z].fieldsMap){
                    headColArray = exportFields.headColArray;
                    if(exportFieldsOrderLine){
                        (z == tablesCount-1) ? headColArray = exportFields.headColArray: headColArray = exportFieldsOrderLine.headColArray;
                    }
                }else{
                    headColArray = exportData[0];
                    beginRow = 1;
                }

                var reportTable = '<div class="export-single-table"><table>' +
                    '<thead>' +
                    '<tr>' +
                    createExportHeadLine(headColArray)+
                    '</tr>'+
                    '</thead>'+
                    '<tbody>' +
                    createExportTable(exportData,colCount,beginRow)+
                    '</tbody>'+
                    '</table></div>';

                reportTable += '<div class="report-link"><a href="'+ dataSet.data[z].url +'">Скачать отчет</a>';

                currentExportTable.append(reportTable);
            }

        }

        function createExportHeadLine(headColArray){

            var exportHeadLine = "";
            var exportHeadLineLength = headColArray.length;

            for(i = 0; i < exportHeadLineLength; i++){
                exportHeadLine += '<td>' +
                    headColArray[i]+
                    '</td>';
            }

            return exportHeadLine;

        }

        function createExportTable(exportData,colCount,beginRow){
            var exportTable = "";
            var exportDataLength = exportData.length;

            for(var i = beginRow; i < exportDataLength; i++){
                exportTable += '<tr>'+
                    createExportLine(exportData[i],colCount)+
                    '</tr>';
            }

            function createExportLine(exportDatarow,colCount){
                var exportLine = "";
                var cont = "",fullText;

                for(var i = 0; i < colCount; i++){
                    cont = exportDatarow[i];
                    if (cont && cont.length > 30){
                        fullText = cont;
                        cont = cont.slice(0,30);
                        cont += " <a class='show-full-text' href='#'>...</a>"+
                            '<div class="full-text"><a href="#" class="close">×</a>'+ fullText +'</div>';
                    }
                    exportLine += '<td>'+
                        cont +
                        '</td>';
                }

                return exportLine;
            }
            return exportTable;
        }

        /* /export */

        function setSidebarHeight(contentH){
            try{

                var mainContent = $('.main-content');
                var contH = (contentH) ? contentH : mainContent.height();
                //alert(contH+" "+w.height());

                if (contH > w.height()){
                    contH = (contentH) ? contentH+100 : mainContent.height()+45;
                    $('#sidebar').css('height', contH);
                }else{
                    $('#sidebar').css('height', '100%');
                }
            }catch(e){
                alert(e+" Функция setSidebarHeight");
            }
        }

        $('.container.backoffice .nav-list a').click(function(e){
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
                case 3:
                    $('.bo-settings').show();
                    break;
                case 4:
                    $('.bo-edit').show();
                    break;
            }
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
            setSidebarHeight();
        });

    $('#id-input-file-2').ace_file_input({
        style:'well',
        btn_choose:'Загрузить логотип',
        btn_change:null,
        no_icon:'icon-cloud-upload',
        droppable:true,
        thumbnail:'large'//large | fit
        //,icon_remove:null//set null, to hide remove/reset button
        /**,before_change:function(files, dropped) {
						//Check an example below
						//or examples/file-upload.html
						return true;
					}*/
        /**,before_remove : function() {
						return true;
					}*/
        ,
        preview_error : function(filename, error_code) {
            //name of the file that failed
            //error_code values
            //1 = 'FILE_LOAD_FAILED',
            //2 = 'IMAGE_LOAD_FAILED',
            //3 = 'THUMBNAIL_FAILED'
            //alert(error_code);
        }

    }).on('change', function(){
            //console.log($(this).data('ace_input_files'));
            //console.log($(this).data('ace_input_method'));
        });

        function DoubleScroll(element) {
            var scrollbar= document.createElement('div');
            scrollbar.appendChild(document.createElement('div'));
            scrollbar.style.overflow= 'auto';
            scrollbar.style.overflowY= 'hidden';
            scrollbar.firstChild.style.width= element.scrollWidth+'px';
            scrollbar.firstChild.style.paddingTop= '1px';
            scrollbar.firstChild.style.marginTop= '-5px';
            scrollbar.firstChild.appendChild(document.createTextNode('\xA0'));
            scrollbar.onscroll= function() {
                element.scrollLeft= scrollbar.scrollLeft;
            };
            element.onscroll= function() {
                scrollbar.scrollLeft= element.scrollLeft;
            };
            element.parentNode.insertBefore(scrollbar, element);
            $(element).prev().addClass('top-scroll');
        }

    /* settings */
    $('.settings-item .btn-save').click(function(){
        var id = $(this).closest('.settings-item').attr('id');

        switch(id) {
            case "settings-common":
                var settingsCommon = $('#settings-common');
                var newShopInfo = new com.vmesteonline.be.shop.Shop();
                newShopInfo.name = settingsCommon.find($('#name')).val();
                newShopInfo.descr = settingsCommon.find($('#descr')).val();
                newShopInfo.address = thriftModule.client.createDeliveryAddress(settingsCommon.find($('#address')).val());

                thriftModule.clientBO.updateShop(newShopInfo);
                break;
            case "settings-shedule":
                var dates = new com.vmesteonline.be.shop.OrderDates();
                dates.type = new com.vmesteonline.be.shop.OrderDatesType;
                dates.orderDay = "";
                dates.orderBefore = $('#days-before').val(); // перевести в правильный формат
                dates.eachOddEven = "";
                dates.priceTypeToUse = "";

                thriftModule.clientBO.setDate(dates);
                break;
            case "settings-delivery":
                break;
        }
    });
    $('.shedule-dates .day').click(function(){
        alert('1');
        $(this).addClass('selected');
        alert('2');
        $(this).append("<a class='remove-date' href='#'>&times;</a>");

        $('.remove-date').click(function(){
           $(this).parent().removeClass('selected');
           $(this).hide().detach();
        });
    });
    $('.add-interval').click(function(e){
        e.preventDefault();

        var isDeliveryType = $(this).closest('.delivery-price-type').hasClass('delivery-interval');
        var newInterval;
        if(isDeliveryType){

            newInterval = '<div class="delivery-interval new-interval delivery-price-type">'+
                '<input type="text" placeholder="Интервал"><span>км</span>&nbsp;'+
                '<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;'+
                '<a href="#" class="add-delivery-interval remove-interval">&ndash;</a>'+
                '</div>';
        }else{
            newInterval = '<div class="delivery-weight new-interval delivery-price-type">'+
                '<input type="text" placeholder="Интервал"><span>кг</span>&nbsp;'+
                '<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;'+
                '<a href="#" class="add-delivery-interval remove-interval">&ndash;</a>'+
                '</div>';
        }
       $(this).closest('.delivery-price-type').after(newInterval);

        $('.new-interval .remove-interval').click(function(e){
            e.preventDefault();

            $(this).closest('.new-interval').slideUp(200,function(){
              $(this).detach();
            });
        });

        $('.new-interval').slideDown().removeClass('.new-interval');
    });

    /* edit */
    $('.edit-show-add').click(function(e){
        e.preventDefault();

        $('.table-add-product').slideToggle();
    });
    $('.edit-add').click(function(e){
        e.preventDefault();

    });
    $('#edit-product table input').change(function(){
        $(this).closest('tr').addClass('changed');
    });
    $('.save-products').click(function(e){
        e.preventDefault();

        $('#edit-product table .changed').each(function(){

            var productInfo = new com.vmesteonline.be.shop.FullProductInfo();
            productInfo.product = new com.vmesteonline.be.shop.Product();
            productInfo.details = new com.vmesteonline.be.shop.ProductDetails();

            productInfo.product.id = $(this).closest('tr').attr('id');
            productInfo.product.name = $(this).find('td:eq(0) input').val();
            productInfo.product.shortDescr = $(this).find('td:eq(1) input').val();
            productInfo.product.weight = $(this).find('td:eq(2) input').val();
            productInfo.product.price = $(this).find('td:eq(3) input').val();
            alert($(this).find('td:eq(3) input').val());

            thriftModule.clientBO.updateProduct(productInfo);
        });


    })
}

    });
