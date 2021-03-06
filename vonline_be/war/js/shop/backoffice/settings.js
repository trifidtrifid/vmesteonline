define(
    'settings',
    ['jquery','shop-initThrift'],
    function( $,thriftModule ){

        var isSettingsInitSet = 0;

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

            var shopId = $('.backoffice.dynamic').attr('id');
            var myShop = thriftModule.client.getShop(shopId);

            /* настройки ссылок  */

            var shopPages = thriftModule.clientBO.getShopPages(shopId);

            if (shopPages.aboutPageContentURL) $('#settings-about-link').val(shopPages.aboutPageContentURL);
            if (shopPages.conditionsPageContentURL) $('#settings-terms-link').val(shopPages.conditionsPageContentURL);
            if (shopPages.deliveryPageContentURL) $('#settings-delivery-link').val(shopPages.deliveryPageContentURL);

            if (shopPages.socialNetworks) {
                for(var p in shopPages.socialNetworks) {
                    if(p == "vk"){
                        $('#settings-socvk-link').val(shopPages.socialNetworks[p]);
                    }else if(p == "fb"){
                        $('#settings-socfb-link').val(shopPages.socialNetworks[p]);
                    }
                }
            }

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
                deliveryCostByDistanceHtml = "",
                ind = 0, sign, intervalTypeClass;
            for(var p in deliveryCostByDistance){
                sign = "&ndash;";
                intervalTypeClass = "remove-interval";

                if (ind == 0) {
                    sign = "+";
                    intervalTypeClass = "add-interval";
                }

                deliveryCostByDistanceHtml += '<div class="delivery-interval delivery-price-type">'+
                    '<input type="text" value="'+ p +'"><span>км</span>'+
                    '<input type="text" value="'+ deliveryCostByDistance[p] +'"><span>руб</span>'+
                    '<a href="#" class="add-delivery-interval '+ intervalTypeClass +'">'+ sign +'</a>'+
                    '</div>';

                ind++;
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
            ind = 0;
            for(var p in deliveryByWeightIncrement){
                sign = "&ndash;";
                intervalTypeClass = "remove-interval";

                if (ind == 0) {
                    sign = "+";
                    intervalTypeClass = "add-interval";
                }

                deliveryByWeightIncrementHtml += '<div class="delivery-weight delivery-price-type">'+
                    '<input type="text" value="'+ p +'"><span>кг</span>'+
                    '<input type="text" value="'+ deliveryByWeightIncrement[p] +'"><span>руб</span>'+
                    '<a href="#" class="add-delivery-interval '+ intervalTypeClass +'">'+ sign +'</a>'+
                    '</div>';

                ind++;
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

            $('.remove-interval').click(function(e){
                e.preventDefault();

                $(this).closest('.settings-delivery-container').addClass('changed');

                if($(this).closest('.delivery-interval').length){

                    $(this).closest('.delivery-interval').slideUp(200,function(){
                        $(this).detach();
                    });

                }else if($(this).closest('.delivery-weight').length){

                    $(this).closest('.delivery-weight').slideUp(200,function(){
                        $(this).detach();
                    });

                }

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
            $('#settings-delivery .add-interval').click(function(){
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

                        if ($('#settings-socvk-link').val() || $('#settings-socfb-link').val()) {
                            pagesInfo.socialNetworks = [];

                            if($('#settings-socvk-link').val()){

                                pagesInfo.socialNetworks['vk'] = $('#settings-socvk-link').val();

                            }

                            if($('#settings-socfb-link').val()){

                                pagesInfo.socialNetworks['fb'] = $('#settings-socfb-link').val();

                            }

                        }

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
                                counter ? key = com.vmesteonline.be.shop.DeliveryType.LONG_RANGE : key = com.vmesteonline.be.shop.DeliveryType.SHORT_RANGE;
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


        return {
            initSettings : initSettings,
            isSettingsInitSet : isSettingsInitSet
        }

    });