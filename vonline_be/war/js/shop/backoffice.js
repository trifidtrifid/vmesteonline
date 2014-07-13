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
        "datepicker-backoffice": "../js/shop/bootstrap-datepicker-backoffice",
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

require(["jquery",'shop-initThrift.min','commonM.min','shop-orders.min','shop-common.min','datepicker-backoffice','datepicker-ru','bootstrap','multiselect'],
    function($,thriftModule,commonM,ordersModule,commonModule) {
        var w = $(window);

        function setSidebarHeight(contentH){
            try{

                var mainContent = $('.main-content');
                var contH = (contentH) ? contentH : mainContent.height(),
                    wHeight = w.height();
                //alert(contH+" "+w.height());

                if (contH > wHeight){
                    contH = (contentH) ? contentH+100 : mainContent.height()+45;
                    $('#sidebar').css('height', contH);
                    //alert('1 '+contH);
                }else{
                    //alert('2 '+wHeight);
                    $('#sidebar').css('height', wHeight-45);
                }
            }catch(e){
                alert(e+" Функция setSidebarHeight");
            }
        }

        var nowTime = parseInt(new Date().getTime()/1000);
        nowTime -= nowTime%86400;
        var day = 3600*24;

        commonM.init();
        $('#sidebar').css('min-height', w.height()-45);

if($('.container.backoffice').hasClass('noAccess')){

    bootbox.alert("У вас нет прав доступа !", function() {
        document.location.replace("/");
    });
}else if (!$('.backoffice.dynamic').hasClass('adminka')){
    /* history */
    var urlHash = document.location.hash;

    var state = {
        type: 'default'
    };

    window.history.replaceState(state,null,urlHash);

    window.addEventListener('popstate', makeHistoryNav, false);

    function makeHistoryNav(e) {
        // действия для корректной навигации по истории
        $('.navbar').removeClass('over-rightbar');
        var isHistoryNav = true;
        if (e.state) {
             if (e.state.type == 'page') {

                if (e.state.pageName == 'orders-history') {

                    $('.shop-trigger.go-to-orders').trigger('click', [isHistoryNav]);

                } else if (e.state.pageName == 'profile') {

                    $('.user-menu a:eq(0)').trigger('click');

                } else if (e.state.pageName == 'edit-profile') {

                    var loadEditPersonal = true;
                    $('.user-menu a:eq(0)').trigger('click', [loadEditPersonal]);

                }
            } else if (e.state.type == 'default') {

                //$('.shop-trigger.back-to-shop').trigger('click', [isHistoryNav]);
                 $('.page').hide();
                 $('.bo-page').show();
                 $('.navbar-header li.active').removeClass('active');
                 $('.bo-link').parent().addClass('active');
            }
        }
    }

        $('.bo-link').parent().addClass('active');

    /*$('.user-short a.dropdown-toggle').click(function (e) {
        e.preventDefault();

        if ($(this).hasClass('no-login')) {
            modules.shopCommonModule.openModalAuth();
        } else {
            $(this).closest('.navbar').toggleClass('over-rightbar');
        }
    });*/


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

        dPickerExport.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
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
                /*for(var p in input[0].files[0]){
                    alert(p+" "+ input[0].files[0][p]);
                }*/

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

                    setSidebarHeight(0);

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


        $('.container.backoffice .nav-list a').click(function(e){
            e.preventDefault();
            $('.back-tab').hide();
            var index = $(this).parent().index();
            switch (index){
                case 0:
                    $('.back-orders').show();
                    break;
                case 1:
                    $('.bo-edit').show();
                    if(!isEditInitSet) initEdit();
                    break;
                case 2:
                    $('.export').show();
                    break;
                case 3:
                    $('.import').show();
                    break;
                case 4:
                    $('.bo-settings').show();
                    if(!isSettingsInitSet) initSettings();
                    break;
            }
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
            setSidebarHeight();
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

    /* --------------------------------------------------*/
    /* -------------------- SETTINGS --------------------*/
    /* --------------------------------------------------*/


    function initSettings(){
        $('#settings-logo').ace_file_input({
            style:'well',
            btn_choose:'Изменить логотип',
            btn_change:null,
            no_icon:'',
            droppable:true,
            thumbnail:'large',
            icon_remove:null
        }).on('change', function(){
                $('.logo-container>img').hide();
            }).parent().addClass('settings-logo');


        $('#date-picker-6').datepicker({
            autoclose:true,
            language:'ru'
        });

        var shopid = $('.backoffice.dynamic').attr('id');
        var myShop = thriftModule.client.getShop(shopid);

        /* настройки ссылок  */

        var shopPages = thriftModule.clientBO.getShopPages(shopid);

        if (shopPages.aboutPageContentURL) $('#settings-about-link').val(shopPages.aboutPageContentURL);
        if (shopPages.conditionsPageContentURL) $('#settings-terms-link').val(shopPages.conditionsPageContentURL);
        if (shopPages.deliveryPageContentURL) $('#settings-delivery-link').val(shopPages.deliveryPageContentURL);

        /* настройки даты */
        var datesArray = thriftModule.clientBO.getDates();
        var datesArrayLength = datesArray.length,
            singleDate,deliveryDatesHtml = "";

        for(var i = 0; i < datesArrayLength; i++){
            singleDate = datesArray[i];

            deliveryDatesHtml += getDatesHtml(singleDate);

        };
        $('.delivery-period').after(deliveryDatesHtml);
        initClickOnDeliveryDayDropdown($('.delivery-day-dropdown a'));

        function reloadDeliveryDayDropdowns(){
            var dropdownLength,
                datesHtml= "";

            dropdownLength = getDropdownDaysLength();

            var addedClass,
                isFirstLoad = false;
            for(var j = 1; j <= dropdownLength; j++){
                checkUsedDays(j,isFirstLoad) ? addedClass = "disable" : addedClass = "";
                //console.log(checkUsedDays(j,isFirstLoad)+' addedClass '+addedClass);

                datesHtml +=  '<li class="'+ addedClass +'"><a href="#">'+j+'</a></li>';
            }

            $('.shedule-item').each(function(){
                console.log('iter');
               $(this).find('.delivery-day-dropdown .dropdown-menu').html(datesHtml);
            });

            initClickOnDeliveryDayDropdown($('.delivery-day-dropdown a'));

        }

        $('.delivery-period-dropdown a').click(function(e){
            e.preventDefault();
            var newDropdownHtml = "",
                dropdownLength;

            if($(this).text() == 'неделя'){
                dropdownLength = 7;
            }else if (($(this).text() == 'месяц')){
                dropdownLength = 31;
            }

            var addedClass,
                isFirstLoad = false;
            for(var i = 1; i <= dropdownLength; i++){
                checkUsedDays(i,isFirstLoad) ? addedClass = "disable" : addedClass = "";

                newDropdownHtml += '<li class="'+ addedClass +'"><a href="#">'+i+'</a></li>';
            }

            $('.delivery-day-dropdown').each(function(){

                $(this).find('.dropdown-menu').html(newDropdownHtml);

            });

            initClickOnDeliveryDayDropdown($('.delivery-day-dropdown a'));
        });

        function initClickOnDeliveryDayDropdown(selector){
            selector.click(function(e){
                e.preventDefault();

                if(!$(this).parent().hasClass('disable')) {
                    var dayText = $(this).text();

                    $(this).closest('.btn-group').find('.btn-group-text').text(dayText);

                    reloadDeliveryDayDropdowns();

                }
            });
        }

        function getDropdownDaysLength(){
            var dropdownLength;
            if($('.delivery-period-dropdown .btn-group-text').text() == 'неделя'){
                dropdownLength = 7;
            }else{
                dropdownLength = 31;
            }

            return dropdownLength;
        }

        function getDatesHtml(singleDate,isAddNew){
            var dropdownLength,
                datesHtml= "";

            dropdownLength = getDropdownDaysLength();

            datesHtml += '<div class="shedule-item new-interval">';
            if( i == 0){
                datesHtml += '<a href="#" class="add-delivery-interval pull-right add-interval">+</a>';
            }else{
                datesHtml += '<a href="#" class="add-delivery-interval pull-right remove-interval">&ndash;</a>';
            }

            datesHtml += '<div class="shedule-confirm"><span>День доставки</span>'+
                '<div class="btn-group delivery-day-dropdown">'+
                '<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                '<span class="btn-group-text">'+singleDate.orderDay+'</span>'+
                '<span class="icon-caret-down icon-on-right"></span>'+
                '</button>'+
                '<ul class="dropdown-menu dropdown-blue">';

            var addedClass,
                isFirstLoad;

            isAddNew ? isFirstLoad = false : isFirstLoad = true;
            for(var j = 1; j <= dropdownLength; j++){
                checkUsedDays(j,isFirstLoad) ? addedClass = "disable" : addedClass = "";

                datesHtml +=  '<li class="'+ addedClass +'"><a href="#">'+j+'</a></li>';
            }

            datesHtml += '</ul>'+
                '</div>'+
                '</div>'+
                '<div class="shedule-confirm"><span>подтверждать заказ за</span><input type="text" class="days-before" value="'+singleDate.orderBefore+'"><span>дня до доставки</span></div>'+
                '</div>';

            return datesHtml;

        }

        function checkUsedDays(dayNumber,isFirstLoad){
            var isUsed = false;
            if(!isFirstLoad) {
                $('.shedule-item').each(function () {
                    var currentOrderDay = $(this).find('.delivery-day-dropdown .btn-group-text').text();
                    console.log(currentOrderDay+" "+dayNumber);
                    if (currentOrderDay == dayNumber) {
                        console.log('!!!!');
                        isUsed = true;
                    }
                });
            }else{
                for(var i = 0; i < datesArrayLength; i++){
                    if(datesArray[i].orderDay == dayNumber){
                        isUsed = true;
                        break;
                    }
                }
            }

            return isUsed;

        }

        /* настройки доставки */

        var deliveryCostByDistance = myShop.deliveryCostByDistance,
            deliveryCostByDistanceHtml = "";
        for(var p in deliveryCostByDistance){
            deliveryCostByDistanceHtml += '<div class="delivery-interval delivery-price-type">'+
                '<input type="text" value="'+ p +'"><span>км</span>'+
                    '<input type="text" value="'+ deliveryCostByDistance[p] +'"><span>руб</span>'+
                        '<a href="#" class="add-delivery-interval add-interval">+</a>'+
                    '</div>';
        }
        if(deliveryCostByDistanceHtml == ""){
            deliveryCostByDistanceHtml += '<div class="delivery-interval delivery-price-type">'+
                '<input type="text" placeholder="Интервал"><span>км</span>'+
                '<input type="text" placeholder="Стоимость"><span>руб</span>'+
                '<a href="#" class="add-delivery-interval add-interval">+</a>'+
                '</div>';
        }
        $('.delivery-interval-container').append(deliveryCostByDistanceHtml);
        //----------------
        var deliveryByWeightIncrement = myShop.deliveryByWeightIncrement,
            deliveryByWeightIncrementHtml = "";
        for(var p in deliveryByWeightIncrement){
            deliveryByWeightIncrementHtml += '<div class="delivery-weight delivery-price-type">'+
                '<input type="text" value="'+ p +'"><span>кг</span>'+
                '<input type="text" value="'+ deliveryByWeightIncrement[p] +'"><span>руб</span>'+
                '<a href="#" class="add-delivery-interval add-interval">+</a>'+
                '</div>';
        }
        if(deliveryByWeightIncrementHtml == ""){
            deliveryByWeightIncrementHtml += '<div class="delivery-weight delivery-price-type">'+
                '<input type="text" placeholder="Интервал"><span>кг</span>'+
                '<input type="text" placeholder="Стоимость"><span>руб</span>'+
                '<a href="#" class="add-delivery-interval add-interval">+</a>'+
                '</div>';
        }
        $('.delivery-weight-container').append(deliveryByWeightIncrementHtml);
        //----------------
        var deliveryTypeAddressMasks = myShop.deliveryTypeAddressMasks,
            deliveryTypeAddressMasksHtml = "";
        for(var p in deliveryTypeAddressMasks){
            var deliveryType;
            if(p == 2){
                deliveryType = "Близко";
            }else if(p == 3){
                deliveryType = "Далеко";
            }
            deliveryTypeAddressMasksHtml += '<div class="delivery-area  delivery-price-type">'+
                '<span>'+ deliveryType +'</span><input type="text" value="'+ deliveryTypeAddressMasks[p] +'"><span>руб</span>'+
                '</div>';
        }
        if(deliveryTypeAddressMasksHtml == ""){
            deliveryTypeAddressMasksHtml += '<div class="delivery-area  delivery-price-type">'+
                '<span>Близко</span><input type="text" placeholder="Стоимость"><span>руб</span>'+
                '</div>'+
                '<div class="delivery-area  delivery-price-type">'+
                '<span>Далеко</span><input type="text" placeholder="Стоимость"><span>руб</span>'+
                '</div>';
        }
        $('.delivery-area-container').append(deliveryTypeAddressMasksHtml);
        //----------------
        var deliveryTypeCosts = myShop.deliveryCosts,
            deliveryTypeCostsHtml = "";
        for(var p in deliveryTypeCosts){
            deliveryType="";
            if(p == 1){
                deliveryType = "Самовывоз";
            }else if(p == 2){
                deliveryType = "Близко";
            }else if(p == 3){
                deliveryType = "Далеко";
            }
            deliveryTypeCostsHtml += '<div class="delivery-type  delivery-price-type">'+
                '<span>'+ deliveryType +'</span><input type="text" value="'+ deliveryTypeCosts[p] +'"><span>руб</span>'+
                '</div>';
        }
        if(deliveryTypeCostsHtml == ""){
            deliveryTypeCostsHtml += '<div class="delivery-type  delivery-price-type">'+
                '<span>Близко</span><input type="text" placeholder="Стоимость"><span>руб</span>'+
                '</div>'+
                '<div class="delivery-type  delivery-price-type">'+
                '<span>Далеко</span><input type="text" placeholder="Стоимость"><span>руб</span>'+
                '</div>';
        }
        $('.delivery-type-container').append(deliveryTypeCostsHtml);


        $('.add-interval').click(function(e){
            e.preventDefault();

            var isDeliveryType = $(this).closest('.delivery-price-type').hasClass('delivery-interval'),
                isDateType = $(this).closest('.shedule-item').length;
            var newInterval;
            if(isDateType){

                var dateObj = {};
                dateObj.orderDay = 1;
                var dropdownLength = getDropdownDaysLength(),
                    isFirstLoad = false,
                    isAddNew = true;
                for(var i = 1; i < dropdownLength; i++){
                    if(!checkUsedDays(i,isFirstLoad)){
                        dateObj.orderDay = i;
                        break;
                    }
                }
                    dateObj.orderBefore = 2;
                $(this).closest('#settings-shedule').find('.shedule-item').last().after(getDatesHtml(dateObj,isAddNew));
                reloadDeliveryDayDropdowns();
                //initClickOnDeliveryDayDropdown($('.delivery-day-dropdown').last().find('a'));

            }else {
                if (isDeliveryType) {

                    newInterval = '<div class="delivery-interval new-interval delivery-price-type">' +
                        '<input type="text" placeholder="Интервал"><span>км</span>&nbsp;' +
                        '<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;' +
                        '<a href="#" class="add-delivery-interval remove-interval">&ndash;</a>' +
                        '</div>';
                } else {
                    newInterval = '<div class="delivery-weight new-interval delivery-price-type">' +
                        '<input type="text" placeholder="Интервал"><span>кг</span>&nbsp;' +
                        '<input type="text" placeholder="Стоимость"><span>руб</span>&nbsp;' +
                        '<a href="#" class="add-delivery-interval remove-interval">&ndash;</a>' +
                        '</div>';
                }
                $(this).closest('.delivery-price-type').after(newInterval);
            }


            $('.new-interval .remove-interval').click(function(e){
                e.preventDefault();

                $(this).closest('.new-interval').slideUp(200,function(){
                    $(this).detach();
                });
            });

            $('.new-interval').slideDown().removeClass('.new-interval');
        });

        $('#settings-shedule .remove-interval').click(function(e){
            e.preventDefault();

            $(this).closest('.new-interval').slideUp(200,function(){
                $(this).detach();
            });
        });

        $('#settings-delivery input').focus(function(){
            $(this).closest('.settings-delivery-container').addClass('changed');
        });

        /*
        * Сохранение
        * */

    $('.settings-item .btn-save').click(function(e){
        e.preventDefault();
        var id = $(this).closest('.settings-item').attr('id');

        switch(id) {
            case "settings-common":
                var settingsCommon = $('#settings-common');
                var newShopInfo = myShop;

                newShopInfo.name = settingsCommon.find($('#name')).val();
                newShopInfo.hostName = settingsCommon.find($('#hostName')).val();
                newShopInfo.descr = settingsCommon.find($('#descr')).val();
                newShopInfo.address = thriftModule.client.createDeliveryAddress(settingsCommon.find($('#address')).val());

                var newLogo = $('.logo-container .file-name img');
                newLogo.length ? newShopInfo.logoURL = newLogo.css('background-image') :
                    newShopInfo.logoURL = $('.logo-container>img').attr('src');

                thriftModule.clientBO.updateShop(newShopInfo);
                break;
            case "settings-links":
                var pagesInfo = new com.vmesteonline.be.shop.ShopPages();
                pagesInfo.aboutPageContentURL = $('#settings-about-link').val();
                pagesInfo.conditionsPageContentURL = $('#settings-terms-link').val();
                pagesInfo.deliveryPageContentURL = $('#settings-delivery-link').val();
                thriftModule.clientBO.setShopPages(pagesInfo);
                break;
            case "settings-shedule":
                var dates = new com.vmesteonline.be.shop.OrderDates();
                if($('.delivery-period-dropdown .btn-group-text').text() == 'неделя'){
                    dates.type = com.vmesteonline.be.shop.OrderDatesType.ORDER_WEEKLY;
                }else{
                    dates.type = com.vmesteonline.be.shop.OrderDatesType.ORDER_MOUNTHLY;
                }


                $('.shedule-item').each(function(){

                    var orderDayText = $(this).find('.delivery-day-dropdown .btn-group-text').text();
                    var orderBeforeText = $(this).find('.days-before').val();
                    dates.orderDay = parseInt(orderDayText);
                    dates.orderBefore = parseInt(orderBeforeText);

                    var isAlreadyExist = false,
                        isNotRemoved = [];
                    for(var i = 0; i < datesArrayLength; i++){
                        isNotRemoved[i] = false;
                        if(dates.orderDay == datesArray[i].orderDay && dates.orderBefore == datesArray[i].orderBefore){
                            isAlreadyExist = true;
                            isNotRemoved[i] = true;
                        }
                    }

                    /*for(var j = 0; j < datesArrayLength; j++){
                        if(!isNotRemoved[j]) thriftModule.clientBO.removeDate(datesArray[j]);
                    }*/

                    if(!isAlreadyExist) thriftModule.clientBO.setDate(dates);

                });

                var sheduleItemLength = $('.shedule-item').length;
                //if(datesArrayLength > sheduleItemLength) {
                    for (var i = 0; i < datesArrayLength; i++) {
                            var isRemoved = true;

                        for (var j = 0; j < sheduleItemLength; j++) {
                            var currentSheduleItem = $('.shedule-item:eq(' + j + ')');

                            var orderDayText = currentSheduleItem.find('.delivery-day-dropdown .btn-group-text').text();
                            var orderBeforeText = currentSheduleItem.find('.days-before').val();
                            dates.orderDay = parseInt(orderDayText);
                            dates.orderBefore = parseInt(orderBeforeText);

                            if (dates.orderDay == datesArray[i].orderDay && dates.orderBefore == datesArray[i].orderBefore) {
                                isRemoved = false;
                            }
                        }
                        console.log(isRemoved);

                        if (isRemoved) thriftModule.clientBO.removeDate(datesArray[i]);
                    }
                //}

                break;
            case "settings-delivery":
                newShopInfo = myShop;
                var isDeliveryIntervalChanged = false,
                    isDeliveryAreaChanged = false,
                    isDeliveryWeightChanged = false,
                    isDeliveryTypeCostsChanged = false;

                $('#settings-delivery').find('.changed').each(function(){
                   if($(this).hasClass('delivery-interval-container')){
                       isDeliveryIntervalChanged = true;
                   }
                    if($(this).hasClass('delivery-area-container')){
                        isDeliveryAreaChanged = true;
                    }
                    if($(this).hasClass('delivery-weight-container')){
                        isDeliveryWeightChanged = true;
                    }
                    if($(this).hasClass('delivery-type-container')){
                        isDeliveryTypeCostsChanged = true;
                    }
                });

                if(isDeliveryIntervalChanged) {
                    var deliveryCostByDistance = [];
                    $('.delivery-interval').each(function () {
                        var key = parseInt($(this).find('input:eq(0)').val()),
                            value = parseFloat($(this).find('input:eq(1)').val());

                        if (key && value) deliveryCostByDistance[key] = value;
                        //console.log("key " + key + "; value " + value);
                    });

                    thriftModule.clientBO.setShopDeliveryCostByDistance(shopId, deliveryCostByDistance);
                }
                if(isDeliveryAreaChanged) {

                    var deliveryTypeAddressMasks = [],
                        counter = 0;
                    $('.delivery-area').each(function () {
                        var key;
                        counter ? key = com.vmesteonline.be.shop.DeliveryType.SHORT_RANGE : key = com.vmesteonline.be.shop.DeliveryType.LONG_RANGE;
                        counter++;
                        var value = $(this).find('input:eq(0)').val();

                        if (value) deliveryTypeAddressMasks[key] = value;
                    });
                    thriftModule.clientBO.setShopDeliveryTypeAddressMasks(shopId, deliveryTypeAddressMasks);
                }
                if(isDeliveryWeightChanged) {
                    var deliveryByWeightIncrement = [];
                    $('.delivery-weight').each(function () {
                        var key = parseInt($(this).find('input:eq(0)').val()),
                            value = parseInt($(this).find('input:eq(1)').val());

                        if (key && value) deliveryByWeightIncrement[key] = value;
                    });
                    thriftModule.clientBO.setShopDeliveryByWeightIncrement(shopId, deliveryByWeightIncrement);
                }
                if(isDeliveryTypeCostsChanged) {

                    var deliveryTypeCosts = [];
                        counter = 0;
                    $('.delivery-type').each(function () {
                        var key;
                        switch(counter){
                            case 0:
                                key = com.vmesteonline.be.shop.DeliveryType.SELF_PICKUP;
                                break;
                            case 1:
                                key = com.vmesteonline.be.shop.DeliveryType.SHORT_RANGE;
                                break;
                            case 2:
                                key = com.vmesteonline.be.shop.DeliveryType.LONG_RANGE;
                                break;
                        }
                        counter++;
                        var value = $(this).find('input:eq(0)').val();

                        if (value) deliveryTypeCosts[key] = value;
                    });
                    thriftModule.clientBO.setDeliveryCosts(deliveryTypeCosts);
                }

                break;
        }
    });

/*    $('.shedule-dates td').click(function(e){
        e.preventDefault();

        if(!$(this).hasClass('new') && !$(this).hasClass('old')){

            e.stopPropagation();

            $(this).addClass('selected');

            var selectedDay = $(this).text();
            var metaTime = parseInt(getMetaDate_ver2($(this).closest('.shedule-dates'),selectedDay)/1000);
            metaTime -= metaTime%86400;
            //var day = 3600*24;

            $(this).attr('data-date',metaTime);


            if($(this).find('.remove-date').length == 0){
                $(this).append("<a class='remove-date' href='#'>&times;</a>");
            }

            $(this).find('.remove-date').click(function(e){
                e.preventDefault();
                e.stopPropagation();

                var dates = new com.vmesteonline.be.shop.OrderDates();
                dates.type = com.vmesteonline.be.shop.OrderDatesType.ORDER_WEEKLY;

                var day = 3600*24;
                dates.orderBefore = parseInt($('#days-before').val())*day;
                dates.orderDay = $(this).parent().data('date');

                thriftModule.clientBO.removeDate(dates);
               $(this).parent().removeClass('selected');
               $(this).hide().detach();
            });
        }
    });

    function getMetaDate_ver2(calendarPicker,day){
        try {
            var strDate = calendarPicker.find('.datepicker-days .switch').text().split(" ");

            var strMonth="";
            var year = strDate[1];
            switch(strDate[0]){
                case 'Январь':
                    strMonth = "Jan";
                    break;
                case 'Февраль':
                    strMonth = "Feb";
                    break;
                case 'Март':
                    strMonth = "March";
                    break;
                case 'Апрель':
                    strMonth = "Apr";
                    break;
                case 'Май':
                    strMonth = "May";
                    break;
                case 'Июнь':
                    strMonth = "June";
                    break;
                case 'Июль':
                    strMonth = "July";
                    break;
                case 'Август':
                    strMonth = "Aug";
                    break;
                case 'Сентябрь':
                    strMonth = "Sen";
                    break;
                case 'Октябрь':
                    strMonth = "Oct";
                    break;
                case 'Ноябрь':
                    strMonth = "Nov";
                    break;
                case 'Декабрь':
                    strMonth = "Dec";
                    break;
            }
        } catch(e){
            alert(e + ' Функция getMetaDate');
        }
        return (Date.parse(day+" "+strMonth+" "+year));
    }
*/
    isSettingsInitSet = 1;
}

    function setDropdownWithoutOverFlow(dropdownSelector,tableContainerSelector,coordY,coordXOffset){
        dropdownSelector.find('.dropdown-toggle').click(function(e){
            e.preventDefault();

            var ind = $(this).closest('td').index();
            var coordX = 0, tdIndex = 0;
            tableContainerSelector.find('table').find('td').each(function(){
                if (tdIndex < ind){
                    coordX += $(this).width();
                }
                tdIndex ++;
            });

            var offset = coordXOffset || 0;
            coordX -= tableContainerSelector.scrollLeft() + offset;

            $(this).parent().find('.dropdown-menu').css({'left':coordX,'top':coordY});
        });
    }


    /* ----------------------------------------------*/
    /* -------------------- EDIT --------------------*/
    /* ----------------------------------------------*/

    var isProductInitSet = 0,
        isCategoryInitSet = 0,
        isProducerInitSet = 0,
        isEditInitSet = 0,
        isSettingsInitSet = 0;

    var shopId = $('.backoffice.dynamic').attr('id'),
        allCategories = thriftModule.client.getAllCategories(shopId);

    function getCategoriesHtml(searchCategory){
        //var shopId = $('.backoffice.dynamic').attr('id');
        var categories = allCategories;
        var categoriesLength = categories.length;
        var categoriesListHtml = '';
        if(searchCategory){
            var searchedCategory;
            for(var i = 0; i < categoriesLength; i++){
                if(categories[i].id == searchCategory){
                    searchedCategory = categories[i];
                }
            }

            return searchedCategory;
        }else{
            for(var i = 0; i < categoriesLength; i++){
                categoriesListHtml += '<li data-categoryid="'+ categories[i].id +'"><a href="#">'+categories[i].name+'</a></li>';
            }

            var categoriesHtml = '<div class="btn-group categories-dropdown">'+
                '<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                '<span class="btn-group-text">Добавить</span>'+
                '<span class="icon-caret-down icon-on-right"></span>'+
                '</button>'+
                '<ul class="dropdown-menu dropdown-blue">'+
                categoriesListHtml+
                '</ul>'+
                '</div>';

            return categoriesHtml;
        }
    }

    var allCategoriesHtml = getCategoriesHtml(),
        allProducers = thriftModule.client.getProducers();

    function initEdit(){

     if (!isProductInitSet) initEditProduct();

     /* общие */
    if(!isEditInitSet){

         $('.edit-show-add').click(function(e){

            e.preventDefault();

            $(this).find('+.table-add').slideToggle();

            var currentPane =$(this).closest('.tab-pane').attr('id');
            switch (currentPane){
                case "edit-product" :
                    //------------------

                    var tr = $('.table-add-product table tr');
                    $('#imageURL-add').ace_file_input({
                        style:'well',
                        btn_choose:'',
                        btn_change:null,
                        no_icon:'',
                        droppable:true,
                        icon_remove:null,
                        thumbnail:'large'
                    }).on('change', function(){
                            tr.find('.product-imageURL .file-label').css('opacity',1);
                            tr.find('.product-imageURL>img').hide();
                        });

                    //------------------

                    $('#imageURLSet-add').ace_file_input({
                        style:'well',
                        btn_choose:'Добавить изображение',
                        btn_change:null,
                        no_icon:'',
                        droppable:true,
                        thumbnail:'large'
                    }).on('change', function(){
                            var el = $(this);
                            setAddUrlImageSet(0,0,el)
                        });
                    initRemoveImageSetItem($('.table-add-product table .product-imagesSet'));

                    //------------------

                    var categoriesHtml = allCategoriesHtml;

                    var categoryTd = $('.table-add-product .product-categories');
                    categoryTd.html(categoriesHtml);
                    initRemoveCategoryItem(categoryTd);
                    initCategoryDropdownClick(categoryTd);
                    var tableSelector = $('.table-add-product .table-overflow');
                    setDropdownWithoutOverFlow(tableSelector.find('.categories-dropdown'),tableSelector,84);
/*                    $('.categories-dropdown .dropdown-toggle').click(function(e){
                        e.preventDefault();

                        var ind = $(this).closest('td').index();
                        var coordX = 0, tdIndex = 0;
                        $('.table-add-product table').find('td').each(function(){
                            if (tdIndex < ind){
                                coordX += $(this).width();
                            }
                            tdIndex ++;
                        });
                        coordX -= $('.table-add-product').scrollLeft();

                        var coordY = 134;
                        $(this).parent().find('.dropdown-menu').css({'left':coordX,'top':coordY});
                    });*/

                    //------------------

                    var optionsTd = $('.table-add-product .product-options'),
                        linksTd = $('.table-add-product .product-links');
                    initRemoveOptionsItem(optionsTd);
                    initAddOptionsItem(optionsTd);

                    initAddLinksItem(linksTd);
                    initRemoveLinksItem(linksTd);
                    setDropdownWithoutOverFlow(tableSelector.find('.producers-dropdown'),tableSelector,84);

                    //------------------

                    $(this).find('+.table-add .product-producer .dropdown-menu a').click(function(){
                        var producerId = $(this).parent().data('producerid');
                        $(this).closest('td').attr('data-producerid',producerId);
                    });
                    break;
                case "edit-category" :
                    linksTd = $('.table-add-category .category-links');
                    initAddLinksItem(linksTd);
                    initRemoveLinksItem(linksTd);
                    break;
                case "edit-producer" :
                    break;
            }


        });

         $('.bo-edit .nav-tabs a').click(function(){
            var ind = $(this).parent().index();

             switch(ind){
                 case 1:
                     if (!isCategoryInitSet) initEditCategory();
                     break;
                 case 2:
                     if (!isProducerInitSet) initEditProducer();
                     break;
             }
             setSidebarHeight($('.main-content').height());
         });
    }

     /* редактирование продуктов */

     function initEditProduct(){

         FillProductDetails();

         DoubleScroll(document.getElementById('doublescroll-2'));

         /* добавление нового продукта  */

         $('.table-add-product .edit-add').click(function(e){
             e.preventDefault();

             var shopId = $('.backoffice.dynamic').attr('id');
             var tableLine = $(this).closest('.table-add-product').find('table tr'),
                 isAdd = true;
             var productInfo = createProductInfoObject(tableLine,isAdd);

             if(!productInfo.product.producerId){
                 $('.error-info').text('Вы не указали производителя !').show();
             }else{
                 $('.error-info').hide();
                 thriftModule.clientBO.registerProduct(productInfo, shopId);

                 $(this).closest('.table-add-product').slideUp(function(){
                     var newLine = tableLine.clone();
                     newLine.addClass('hidden');
                     $('.product-table table tbody').prepend(newLine);
                     newLine.slideDown();
                 });
             }

         });

         function createProductInfoObject(tableLine,isAdd){
             var productInfo = new com.vmesteonline.be.shop.FullProductInfo();
             productInfo.product = new com.vmesteonline.be.shop.Product();
             productInfo.details = new com.vmesteonline.be.shop.ProductDetails();

             isAdd ? productInfo.product.id = 0 : productInfo.product.id = tableLine.attr('id');

             productInfo.product.name = tableLine.find('.product-name textarea').val();
             productInfo.product.shortDescr = tableLine.find('.product-shortDescr textarea').val();

             productInfo.product.producerId = tableLine.find('.product-producer').data('producerid');

             var loadedLogo = tableLine.find('.product-imageURL .file-name img');
             loadedLogo.length ? productInfo.product.imageURL = loadedLogo.css('background-image')
                 : productInfo.product.imageURL = tableLine.find('.product-imageURL>img').attr('src');

             tableLine.find('.product-weight input').val() ?
                 productInfo.product.weight = parseFloat(tableLine.find('.product-weight input').val()) :
                 productInfo.product.weight = 0;

             tableLine.find('.product-price input').val() ?
                 productInfo.product.price = parseFloat(tableLine.find('.product-price input').val()):
                 productInfo.product.price = 0;

             productInfo.product.unitName = tableLine.find('.product-unitName input').val();

             tableLine.find('.product-pack input').val() ?
                 productInfo.product.minClientPack = parseFloat(tableLine.find('.product-pack input').val()):
                 productInfo.product.minClientPack = 0;

             productInfo.product.prepackRequired = tableLine.find('.product-prepack input').attr('checked') ? true : false;

             productInfo.details.fullDescr = tableLine.find('.product-fullDescr textarea').val();

             var counter = 0;
             var isImagesLoaded = tableLine.find('.product-imagesSet').find('.file-name img').length;
             if(isImagesLoaded){
                 var imagesURLset = [];

                 tableLine.find('.product-imagesSet .ace-file-input.added').each(function(){
                     imagesURLset[counter++] = $(this).find('.file-name img').css('background-image');
                 });

                 productInfo.details.imagesURLset = imagesURLset;
             }

             counter = 0;
             var categoriesIdList = [];
             tableLine.find('.product-categories .category-item').each(function(){
                 categoriesIdList[counter++] = $(this).data('categoryid');
             });
             productInfo.details.categories = categoriesIdList;

             var optionsMap = [];
             tableLine.find('.product-options table tr').each(function(){
                 optionsMap[$(this).find('td:eq(0)').text()] = $(this).find('td:eq(1)').text();
             });
             productInfo.details.options = optionsMap;

             var linksMap = [],ind = 0;
             tableLine.find('.product-links table tr').each(function(){
                  var socVal = $(this).find('input').val(),
                      key;
                 if(socVal.indexOf('vk') != -1){
                     key = 'vk';
                 }else if(socVal.indexOf('facebook') != -1){
                     key = 'facebook';
                 }
                 linksMap[key] = socVal;
             });
             productInfo.details.socialNetworks = linksMap;

             return productInfo;
         }

         /* первоначальное заполнение таблицы продуктов */

         function FillProductDetails(){
             var productsTable = $('.products-table>table'),
                 productDetails;
             productsTable.find('tbody tr').each(function(){
                 var productId = $(this).attr('id');
                 productDetails = thriftModule.client.getProductDetails(productId);

                 // -------------
                 var imagesURLSet = productDetails.imagesURLset,
                     imagesURLSetLength = imagesURLSet.length,
                     imagesURLSetHtml = "<div class='images-set'>";
                 if(imagesURLSetLength != 0){
                     for(var i = 0; i < imagesURLSetLength; i++){
                         imagesURLSetHtml += "<div class='image-item map-item'>" +
                             "<a href='#' class='remove-image-item remove-item'>&times</a>"+
                             "<img src='"+ imagesURLSet[i] +"'>"+
                             "</div>";
                     }
                 }
                 imagesURLSetHtml += "</div>"+
                     "<input type='file' id='imagesURLSet-"+ productId+"-"+imagesURLSetLength +"'>";
                 // -------------
                 var categories = productDetails.categories,
                     categoriesLength = categories.length,
                     categoriesHtml = "";
                 for(var i = 0; i < categoriesLength; i++){
                     categoriesHtml += "<div class='category-item map-item' data-categoryid='"+ categories[i] +"'>"+
                         "<a href='#' class='remove-category-item remove-item'>&times</a>"+
                         getCategoriesHtml(categories[i]).name+
                         "</div>";
                 }

                 categoriesHtml += allCategoriesHtml;
                 // -------------
                 var options = productDetails.optionsMap,
                     optionsHtml = "<table><tbody>";
                 for(var p in options){
                     optionsHtml += "<tr>" +
                         "<td><input type='text' value='"+ p +"'></td>" +
                         "<td><input type='text' value='"+ options[p] +"'></td>" +
                         "<td class='td-remove-options'><a href='' class='remove-options-item remove-item'>&times;</a></td>" +
                         "</tr>";
                 }
                 optionsHtml += "</tbody></table><a href='#' class='add-options-item add-item'>Добавить</a>";
                 // -------------
                 var links = productDetails.socialNetworks,
                     linksHtml = "<table><tbody>";
                 //console.log(links.length);
                 for(var p in links){
                     linksHtml += "<tr class='product-link-wrap'>" +
                         "<td><input type='text' value='"+ links[p] +"'></td>" +
                         "<td class='td-remove-link'><a href='' class='remove-link-item remove-item'>&times;</a></td>" +
                         "</tr>";
                 }
                 linksHtml += "</tbody></table><a href='#' class='add-link-item add-item'>Добавить</a>";
                 // -------------
                 var currentProducer,
                     producerId = $(this).find('.product-producer').data('producerid'),
                     producersList = allProducers,
                     producersListLength = producersList.length,
                     producersListHtml = "";
                 for(var i = 0; i < producersListLength; i++ ){
                     if(producersList[i].id == producerId){
                         currentProducer = producersList[i];
                     }
                     producersListHtml += "<li data-producerid='"+ producersList[i].id +"'><a href='#'>"+ producersList[i].name +"</a></li>";
                 }
                 var producersHtml = "<span>" + currentProducer.name + "</span>"+
                     '<div class="btn-group producers-dropdown">'+
                     '<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                     '<span class="btn-group-text">Изменить</span>'+
                     '<span class="icon-caret-down icon-on-right"></span>'+
                     '</button>'+
                     '<ul class="dropdown-menu dropdown-blue">'+
                     producersListHtml+
                     '</ul>'+
                     '</div>';


                 // -------------

                 var tr = $(this);
                 $('#imageURL-'+productId).ace_file_input({
                     style:'well',
                     btn_choose:'',
                     btn_change:null,
                     no_icon:'',
                     droppable:true,
                     icon_remove:null,
                     thumbnail:'large'
                 }).on('change', function(){
                         tr.find('.product-imageURL .file-label').css('opacity',1);
                         tr.find('.product-imageURL>img').hide();
                     });

                 $(this).find('.product-fullDescr textarea').val(productDetails.fullDescr);

                 $(this).find('.product-imagesSet').html(imagesURLSetHtml);

                 $('#imagesURLSet-' + productId + "-" + imagesURLSetLength).ace_file_input({
                     style:'well',
                     btn_choose:'Добавить', //
                     btn_change:null,
                     no_icon:'',
                     droppable:true,
                     //icon_remove:null,
                     thumbnail:'large'
                 }).on('change', function(){
                         var el = $(this);
                         setAddUrlImageSet(productId,imagesURLSetLength,el);
                     });
                 initRemoveImageSetItem($(this).find('.ace-file-input'));


                 $(this).find('.product-categories').html(categoriesHtml);
                 initRemoveCategoryItem($(this));
                 initCategoryDropdownClick($(this));

                 $(this).find('.product-options').html(optionsHtml);
                 initRemoveOptionsItem($(this));
                 initAddOptionsItem($(this));

                 $(this).find('.product-links').html(linksHtml);
                 initRemoveLinksItem($(this));
                 initAddLinksItem($(this));

                 $(this).find('.product-producer').html(producersHtml);
                 initChangeProducer($(this));
             });
         }

         /* действия внутри таблицы продуктов */

         $('.product-remove a').click(function(e){
             e.preventDefault();

             var productId = $(this).closest('tr').attr('id');
             var shopId = $('.backoffice.dynamic').attr('id');

             $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                 $(this).detach();
             });

             thriftModule.clientBO.deleteProduct(productId,shopId);
         });

         function initChangeProducer(selector){
             selector.find('.producers-dropdown .dropdown-menu a').click(function(e){
                 e.preventDefault();

                 var producerName = $(this).text();
                 var producerId = $(this).closest('li').data('producerid');
                 $(this).closest('td').attr('id',producerId).find('>span').text(producerName);
             });
         }

         /* сохранение изменений в таблице продуктов */

         $('.save-products').click(function(e){
             e.preventDefault();

             $('#edit-product table .changed').each(function(){

                 var tableLine = $(this),
                     isAdd = false;
                 var productInfo = createProductInfoObject(tableLine,isAdd);

                 thriftModule.clientBO.updateProduct(productInfo);
             });
         });

         $('#edit-product .products-table table tr,#edit-category .category-table tr, #edit-producer .producer-table tr').click(function(){
             $(this).addClass('changed');
         });

         isProductInitSet = 1;
     }

     function initRemoveImageSetItem(selector){
         selector.find('.remove').click(function(e){
             e.preventDefault();

             $(this).closest('.ace-file-input').hide().detach();

         })
     }

     function setAddUrlImageSet(productId,imagesURLSetLength,el){
         if(!el.parent().hasClass('added')){
             el.parent().addClass('added');
             imagesURLSetLength++;
             var newInputHtml = "<input type='file' class='new-input' id='imagesURLSet-"+ productId+"-"+imagesURLSetLength +"'>";
             el.parent().after(newInputHtml);

             $('#imagesURLSet-' + productId + "-" + imagesURLSetLength).ace_file_input({
                 style:'well',
                 btn_choose:'Добавить', //
                 btn_change:null,
                 no_icon:'',
                 droppable:true,
                 //icon_remove:null,
                 thumbnail:'large'
             }).on('change',function(){
                     var el = $(this);
                     setAddUrlImageSet(productId,imagesURLSetLength,el);
                 });
             initRemoveImageSetItem(el.parent());
         }
     }

     function initRemoveCategoryItem(selector){
         selector.find('.remove-category-item').click(function(e){
             e.preventDefault();

             $(this).closest('.map-item').fadeOut(200);

         });
     }

     function initCategoryDropdownClick(selector){
         selector.find('.categories-dropdown .dropdown-menu a').click(function(e){
             e.preventDefault();
             var newCategoryItemHtml = '<div class="category-item map-item" data-categoryid="'+ $(this).closest('li').data('categoryid') +'">' +
                 '<a href="#" class="remove-category-item remove-item">×</a>'+ $(this).text() +'</div>';
             $(this).closest('td').find('.categories-dropdown').before(newCategoryItemHtml);

             initRemoveCategoryItem($(this).closest('td'));
         });
     }

     function initRemoveOptionsItem(selector){
         selector.find('.remove-options-item').click(function(e){
             e.preventDefault();

             $(this).closest('tr').slideUp(200,function(){
                 $(this).detach();
             });
         })
     }

     function initAddOptionsItem(selector){
         selector.find('.add-options-item ').click(function(e){
             e.preventDefault();
             var newOptionsLine = '<tr>' +
                 '<td><input type="text" value=""></td>' +
                 '<td><input type="text" value=""></td>' +
                 '<td class="td-remove-options"><a href="" class="remove-options-item remove-item">×</a></td>' +
                 '</tr>';

             $(this).closest('td').find('table tbody').append(newOptionsLine);

             initRemoveOptionsItem($(this).closest('td'));
         });
     }

        function initRemoveLinksItem(selector){
            selector.find('.remove-link-item').click(function(e){
                e.preventDefault();

                $(this).closest('.product-link-wrap').slideUp(200,function(){
                    $(this).detach();
                });
            })
        }

        function initAddLinksItem(selector){
            selector.find('.add-link-item ').click(function(e){
                e.preventDefault();
                var newLinksLine = '<tr class="product-link-wrap">'+
                    '<td><input type="text" placeholder="Ссылка на соц. сеть"></td>'+
                        '<td class="td-remove-link no-init"><a href="#" class="remove-link-item remove-item">&times;</a></td>'+
                    '</tr>';

                $(this).closest('td').find('table tbody').append(newLinksLine);

                var removeLinkNoInit = $('.td-remove-link.no-init');
                initRemoveLinksItem(removeLinkNoInit);
                removeLinkNoInit.removeClass('.no-init');
            });
        }

    /* редактирование категорий */

     function initEditCategory() {

         var shopId = $('.backoffice.dynamic').attr('id'),
             categories = thriftModule.client.getAllCategories(shopId),
             categoriesLength = categories.length,
             logoURLsetArr = [],ind = 0;

         for(var i = 0; i < categoriesLength ; i++){
             logoURLsetArr[i] = categories[i].socialNetworks;
         }

         if(logoURLsetArr.length) {
             $('.category-table tbody tr').each(function () {
                 if(logoURLsetArr[ind]) {
                     var linksHtml = "<table><tbody>",
                         logoURLsetArrLength = logoURLsetArr[ind].length;

                     for (var i = 0; i < logoURLsetArrLength; i++) {
                         linksHtml += "<tr class='product-link-wrap'>" +
                             "<td><input type='text' value='" + logoURLsetArr[ind][i] + "'></td>" +
                             "<td class='td-remove-link'><a href='' class='remove-link-item remove-item'>&times;</a></td>" +
                             "</tr>";
                     }
                     linksHtml += "</tbody></table><a href='#' class='add-link-item add-item'>Добавить</a>";

                     $(this).find('.category-links').html(linksHtml);
                     initRemoveLinksItem($(this));
                     initAddLinksItem($(this));

                     ind++;
                 }
             });
         }

        function setParentCategory(selector){
            var newCategory = selector.text();
            var categoryId = selector.closest('li').data('categoryid');
            selector.closest('.category-parent').attr('data-parentid',categoryId).find('.btn-group-text').text(newCategory);
        }

        $('.category-parent').each(function(){
           //var categoriesHtml = getCategoriesHtml();
            $(this).html(allCategoriesHtml);

            $(this).find('.btn-group-text').text('Выбрать родительскую');

            $(this).find('.dropdown-menu a').click(function(e){
                e.preventDefault();

                setParentCategory($(this));

            });

            if($(this).closest('.table-add').length == 0){
                var parentId = $(this).data('parentid');

                $(this).find('.dropdown-menu li').each(function(){
                    if($(this).data('categoryid') == parentId){
                        setParentCategory($(this));
                    }
                })
            }

        });

        $('.table-add-category .edit-add').click(function(e){
            e.preventDefault();

            var shopId = $('.backoffice.dynamic').attr('id');
            var tableLine = $(this).closest('.table-add-category').find('table tr'),
                isAdd = true;
            var productCategory = createCategoryObject(tableLine,isAdd);

            /*if(!productInfo.product.producerId){
                $('.error-info').text('Вы не указали производителя !').show();
            }else{*/
                $('.error-info').hide();
                thriftModule.clientBO.registerProductCategory(productCategory, shopId);

                $(this).closest('.table-add-category').slideUp(function(){
                    var newLine = tableLine.clone();
                    newLine.addClass('hidden');
                    $('.product-table table tbody').prepend(newLine);
                    newLine.slideDown();
                });
            //}
        });

        function createCategoryObject(tableLine,isAdd){
            var productCategory = new com.vmesteonline.be.shop.ProductCategory();

            isAdd ? productCategory.id = 0 : productCategory.id = tableLine.attr('id');
            productCategory.name = tableLine.find('.category-name textarea').val();
            productCategory.descr = tableLine.find('.category-descr textarea').val();
            productCategory.parentId = tableLine.find('.category-parent').attr('data-parentid');
            productCategory.socialNetworks = [];
            tableLine.find('.category-links table tr').each(function(){
                var socVal = $(this).find('input').val(),
                    key;
                if(socVal.indexOf('vk')){
                    key = 'vk';
                }else if(socVal.indexOf('facebook')){
                    key = 'facebook';
                }
                productCategory.socialNetworks[key] = socVal;
            });

            return productCategory;
        }

        $('.save-categories').click(function(e){
            e.preventDefault();

            $('.category-table tr.changed').each(function(){
                var tableLine = $(this),
                    isAdd = false;

                var productCategory = createCategoryObject(tableLine,isAdd);
                thriftModule.clientBO.updateCategory(productCategory);

            })
        });

        $('.category-remove a').click(function(e){
            e.preventDefault();

            var categoryId = $(this).closest('tr').attr('id');
            var shopId = $('.backoffice.dynamic').attr('id');

            $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                $(this).detach();
            });

            thriftModule.clientBO.deleteCategory(categoryId,shopId);
        });

         isCategoryInitSet = 1;


     }

    /* редактирование производителей */

     function initEditProducer(){

         var shopId = $('.backoffice.dynamic').attr('id'),
             producers = thriftModule.client.getProducers(),
             producersLength = producers.length,
             logoURLsetArr = [],ind = 0;

         for(var i = 0; i < producersLength ; i++){
             logoURLsetArr[i] = producers[i].descr;
         }

/*         if(logoURLsetArr.length) {
             $('.category-table tbody tr').each(function () {
                 if(logoURLsetArr[ind]) {
                     var linksHtml = "<table><tbody>",
                         logoURLsetArrLength = logoURLsetArr[ind].length;

                     for (var i = 0; i < logoURLsetArrLength; i++) {
                         linksHtml += "<tr class='product-link-wrap'>" +
                             "<td><input type='text' value='" + logoURLsetArr[ind][i] + "'></td>" +
                             "<td class='td-remove-link'><a href='' class='remove-link-item remove-item'>&times;</a></td>" +
                             "</tr>";
                     }
                     linksHtml += "</tbody></table><a href='#' class='add-link-item add-item'>Добавить</a>";

                     $(this).find('.category-links').html(linksHtml);
                     initRemoveLinksItem($(this));
                     initAddLinksItem($(this));

                     ind++;
                 }
             });
         }*/

        $('.table-add-producer .edit-add').click(function(e){
            e.preventDefault();

            var shopId = $('.backoffice.dynamic').attr('id');
            var tableLine = $(this).closest('.table-add-producer').find('table tr'),
                isAdd = true;
            var producer = createProducerObject(tableLine,isAdd);

            /*if(!productInfo.product.producerId){
             $('.error-info').text('Вы не указали производителя !').show();
             }else{*/
            $('.error-info').hide();
            thriftModule.clientBO.registerProducer(producer, shopId);

            $(this).closest('.table-add-producer').slideUp(function(){
                var newLine = tableLine.clone();
                newLine.addClass('hidden');
                $('.product-table table tbody').prepend(newLine);
                newLine.slideDown();
            });
            //}
        });

        function createProducerObject(tableLine,isAdd){
            var producer = new com.vmesteonline.be.shop.Producer();

            isAdd ? producer.id = 0 : producer.id = tableLine.attr('id');
            producer.name = tableLine.find('.producer-name textarea').val();
            producer.descr = tableLine.find('.producer-descr textarea').val();

            return producer;
        }

        $('.save-producers').click(function(e){
            e.preventDefault();

            $('.producer-table tr.changed').each(function(){
                var tableLine = $(this),
                    isAdd = false;

                var producer = createProducerObject(tableLine,isAdd);
                thriftModule.clientBO.updateProducer(producer);
            })
        });

        $('.producer-remove a').click(function(e){
            e.preventDefault();

            var producerId = $(this).closest('tr').attr('id');
            var shopId = $('.backoffice.dynamic').attr('id');

            $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                $(this).detach();
            });

            thriftModule.clientBO.deleteProducer(producerId,shopId);
        });

         isProducerInitSet = 1;

     }

     isEditInitSet = 1;
 }

}else{

        /* -------------------------------------------------*/
        /* -------------------- ADMINKA --------------------*/
        /* -------------------------------------------------*/


        var contentH = $('body').height();
        setSidebarHeight(contentH);

        $('.adminka-shops table tr').each(function(){
            var shopId = $(this).attr('id');
            var shop = thriftModule.client.getShop(shopId);
            var userInfo = thriftModule.userClient.getUserInfoExt(shop.ownerId);

            var userContacts = getUserContacts(shop.ownerId);

            $(this).find('.owner-name span').text(userInfo.firstName+" "+userInfo.lastName); //
            $(this).find('.owner-contacts').html(userContacts);

            if(thriftModule.client.isActivated(shopId)){
                $(this).find('.shop-activation input').attr('checked','checked');
            };

        });

        $('.shop-activation .checkbox .lbl').click(function(){
           var shopId = $(this).closest('tr').attr('id'),
               flag = true;
            if ($(this).parent().find('input.ace').prop('checked')){
                flag = false;
            }
            //console.log("flag "+$(this).parent().find('input.ace').prop('checked'));

            thriftModule.clientBO.activate(shopId,flag);
        });

        function getUserContacts(userId,withoutBr){

            var userContacts = thriftModule.userClient.getUserContactsExt(userId);

            var delimeter;
            withoutBr ? delimeter = "; " : delimeter = "<br>";
            var userContactsText = "Email: "+userContacts.email;
            userContacts.mobilePhone ? userContactsText += delimeter+" Телефон: "+userContacts.mobilePhone : false;
            userContacts.homeAddress ? userContactsText += delimeter+ " Адрес: "+userContacts.homeAddress.street.name+" "+
                userContacts.homeAddress.building.fullNo+", "+ userContacts.homeAddress.flatNo : false;

            return userContactsText;
        }

        $('.update-owner-link').click(function(e){
            e.preventDefault();

            var updateHtml = "<div class='update-owner-line'>" +
                "<input type='text' class='owner-email' placeholder='email нового владельца'>" +
                "<a href='#' class='btn btn-sm no-border btn-primary btn-update'>Изменить</a>"+
                "</div>";
            var td = $(this).closest('td');
            if (td.find('.update-owner-line').length == 0){
                td.append(updateHtml);
            }
            td.find('.update-owner-line').slideToggle();

            td.find('.btn-update').one('click',function(e){
                e.preventDefault();

                var shopId = $(this).closest('tr').attr('id');
                var ownerEmail = $(this).closest('td').find('.owner-email').val();

                var newOwnerInfo = thriftModule.clientBO.setUserShopRole(shopId ,ownerEmail,3);
                td.find('.update-owner-line').slideToggle();

                updateOwnerHtml(td,newOwnerInfo);
            });
        });

        function updateOwnerHtml(selector,newOwnerInfo){
            selector.find('span').text(newOwnerInfo.firstName+" "+newOwnerInfo.lastName);
            selector.closest('tr').find('.owner-contacts').html(getUserContacts(newOwnerInfo.id));
        }

    /*$('.adminka-statistics-period .dropdown-menu a').click(function(e){
        e.preventDefault();

        var newText = $(this).text(),
            dateFrom, dateTo;
        $(this).closest('.btn-group').find('.btn-group-text').text(newText);

        switch(newText){
            case "За месяц":
                reloadShopTotal(nowTime-30*day,nowTime);
                break;
            case "За все время":
                reloadShopTotal(0,nowTime);
                break;
        }
    });*/

    $('.adminka-statistics-date .btn').click(function(e){
        e.preventDefault();

        var dateFrom, dateTo;

        dateFrom = Date.parse(datepickerFrom.val())/1000;
        dateTo = Date.parse(datepickerTo.val())/1000;

        dateFrom -= dateFrom%86400;
        dateTo -= dateTo%86400;

        reloadShopTotal(dateFrom,dateTo);

    });

    function reloadShopTotal(dateFrom,dateTo){
        var shopId, total;
        $('.adminka-statistics tr').each(function(){
           shopId = $(this).attr('id');

            total = thriftModule.clientBO.totalShopReturn(shopId,dateFrom,dateTo);

            $(this).find('.shop-total').text(total);
        });
    };

    var datepickerFrom = $('#datepicker-from');
    var datepickerTo = $('#datepicker-to');

        $('.adminka .nav-list a').click(function(e){
            e.preventDefault();
            $('.back-tab').hide();
            var index = $(this).parent().index();
            switch (index){
                case 0:
                    $('.adminka-shops').show();
                    break;
                case 1:
                    $('.adminka-statistics').show();

                    datepickerFrom.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                        $(this).prev().focus();
                    });
                    datepickerTo.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                        $(this).prev().focus();
                    });

                    var lastMonth = nowTime-30*day;
                    var tempDate = new Date(lastMonth*1000);

                    var lastMonthDay = tempDate.getDate();
                    lastMonthDay = (lastMonthDay < 10)? "0" + lastMonthDay: lastMonthDay;

                    var lastMonthMonth = tempDate.getMonth()+1;
                    lastMonthMonth = (lastMonthMonth < 10)? "0" + lastMonthMonth: lastMonthMonth;

                    var lastMonthYear= tempDate.getFullYear();

                    datepickerTo.datepicker('setValue', nowTime);
                    datepickerFrom.val(lastMonthMonth+"-"+lastMonthDay+"-"+lastMonthYear);
                    break;
            }
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });

    $('.user-short a.no-login').click(function (e) {
        e.preventDefault();

        $(this).parent().addClass('open');
        commonModule.openModalAuth();

    });
}

    });
