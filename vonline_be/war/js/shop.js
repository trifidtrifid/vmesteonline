$(document).ready(function(){
    try{
    var transport = new Thrift.Transport("/thrift/ShopService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

    transport = new Thrift.Transport("/thrift/UserService");
    protocol = new Thrift.Protocol(transport);
    var userServiceClient = new com.vmesteonline.be.UserServiceClient(protocol);
    }catch(e){
        alert(e + " Ошибка иниицализации thrift");
    }

    /* простые обработчики событий */
    try{
    var w = $(window),
        showRight = $('.show-right'),
        hideRight = $('.hide-right'),
        shopRight = $('.shop-right'),
        showRightTop = (w.height()-showRight.width())/ 2;

    showRight.css('top',showRightTop);
    $('.shop-right').css('min-height', w.height()-45);

    showRight.click(function(){
        if (!$(this).hasClass('active')){
            $(this).animate({'right':'222px'},200).addClass('active');
            $(this).parent().animate({'right':0},200);
        }else{
            hideRight.trigger('click');
        }
    });

    hideRight.click(function(){
        $(this).parent().animate({'right':'-250px'},200);
        showRight.animate({'right':'-28px'},200).removeClass('active');
    });

    $('.dropdown-menu li a').click(function(e){
        e.preventDefault();
        $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
    });

    $('.nav-list a').click(function(e){
        e.preventDefault();
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });

    $('.modal-order-end .btn-grey').click(function(){
        $('.modal-order-end').modal('hide');
    });

    if ($('.catalog-order li').length == 0){
        $('.additionally-order').addClass('hide');
    }

    $('.login-link').click(function(){
        $('.modal-login').modal();
    });

     //custom autocomplete (category selection)
    try{
    $.widget( "custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function( ul, items ) {
            var that = this,
                currentCategory = "";
            $.each( items, function( index, item ) {
                if ( item.category != currentCategory ) {
                    ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
                    currentCategory = item.category;
                }
                that._renderItemData( ul, item );
            });
        }
    });

    var categories = client.getProductCategories(0);
    var products = client.getProducts(0,10,0);
    var productsLength = products.length;
        var dataSearch = [];
        for(i = 0; i < productsLength; i++){
            dataSearch[i]={
                label : products.products[i].name,
                category: ""
            }
        }

    /*var dataSearch = [
        { label: "anders", category: "" },
        { label: "andreas", category: "" },
        { label: "antal", category: "" },
        { label: "annhhx10", category: "" },
        { label: "annk K12", category: "" },
        { label: "annttop C13", category: "" },
        { label: "anders andersson", category: "" },
        { label: "andreas andersson", category: "" },
        { label: "andreas johnson", category: "" }
    ];*/
    $( "#search" ).catcomplete({
        delay: 0,
        source: dataSearch
    });

    /* автозаполнение адреса доставки  */
        function initAutocompleteAddress(){ var addressesBase = userServiceClient.getAddressCatalogue();

            var countries = addressesBase.countries;
            var countriesLength = countries.length;
            var countryTags = [],
                countryId = [],
                cityTags = [],
                cityId = [],
                streetTags = [],
                streetId = [];

            for (var i = 0; i < countriesLength; i++){
                //alert(countries[i].name);
                countryTags[i] = countries[i].name;
                countryId[i] = countries[i].id;
            }

            $( "#country-delivery" ).autocomplete({
                source: countryTags
            });
            $('#city-delivery').focus(function(){
                var prevField = $('#country-delivery').val();
                var cities;
                if(prevField){
                    for (var i = 0; i < countriesLength; i++){
                        if (prevField == countryTags[i]){
                            cities = userServiceClient.getCities(countryId[i]);
                            break;
                        }
                    }
                }
                if (!cities){
                    cities = addressesBase.cities;
                }
                var citiesLength = cities.length;
                var cityTags = [];
                for (i = 0; i < citiesLength; i++){
                    cityTags[i] = cities[i].name;
                }

                $(this).autocomplete({
                    source: cityTags
                });
            });

            $( "#street-delivery" ).focus(function(){
                var prevField = $('#city-delivery').val();
                var streets;
                var citiesLength = addressesBase.cities.length;
                if(prevField){
                    for (var i = 0; i < citiesLength; i++){
                        if (prevField == cityTags[i]){
                            streets = userServiceClient.getStreets(cityId[i]);
                            break;
                        }
                    }
                }
                if (!streets){
                    streets = addressesBase.streets;
                }
                var streetsLength = streets.length;
                var streetTags = [];
                for (i = 0; i < streetsLength; i++){
                    streetTags[i] = streets[i].name;
                }

                $(this).autocomplete({
                    source: streetTags
                });
            });

            $( "#building-delivery" ).focus(function(){
                var prevField = $('#street-delivery').val();
                var buildings;
                var streetLength = addressesBase.streets.length;
                if(prevField){
                    for (var i = 0; i < streetLength; i++){
                        if (prevField == streetTags[i]){
                            buildings = userServiceClient.getBuildings(streetId[i]);
                            break;
                        }
                    }
                }
                if (!buildings){
                    buildings = addressesBase.buildings;
                }
                var buildingsLength = buildings.length;
                var buildingsTags = [];
                for (i = 0; i < buildingsLength; i++){
                    buildingsTags[i] = buildings[i].fullNo;
                }

                $(this).autocomplete({
                    source: buildingsTags
                });
            });
        }

    }catch(e){
            alert(e+ " Ошибка autocomplete")
    }

    /* --- --- */

    var dPicker = $('.date-picker');

    dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    var datepickerFunc = {
        AddSingleProductToBasket: AddSingleProductToBasket,
        initVarForMoreOrders: initVarForMoreOrders,
        createOrdersHtml: createOrdersHtml,
        initShowMoreOrders: initShowMoreOrders,
        initOrderPlusMinus: initOrderPlusMinus,
        initOrderBtns: initOrderBtns,
        setSidebarHeight: setSidebarHeight,
        initOrdersLinks: initOrdersLinks
    };

    dPicker.datepicker('setVarOrderDates',datepickerFunc);


    initProductsSpinner();

    var triggerDelivery = 0;
    var autocompleteAddressFlag = 1;
    initRadioBtnClick();

    initBasketInReload();
    InitAddToBasket($('.fa-shopping-cart'));
    InitProductDetailPopup($('.product-link'));
    // переключение между категориями
    InitClickOnCategory();
    initOrderPlusMinus($('.shop-orders'));
    //shopTriggerClick();

/* функции */
    var prevParentId = [],
        parentCounter = 0;

//setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

    var prevCatCounter = getCookie('prevCatCounter');
    if (prevCatCounter !== undefined){
        parentCounter = parseInt(prevCatCounter);
    }
    var arrayPrevCatCookie = getCookie('arrayPrevCat');
    if (arrayPrevCatCookie !== undefined){
        prevParentId = arrayPrevCatCookie.split(',');
    }

    // возвращает cookie с именем name, если есть, если нет, то undefined
    function getCookie(name) {
        try{
        var matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
        ));
        }
        catch(e){
            alert(e+" Функция getCookie");
        }
        return matches ? decodeURIComponent(matches[1]) : undefined;
    }

    function setCookie(name, value, options) {
        try{
        options = options || {};

        var expires = options.expires;

        if (typeof expires == "number" && expires) {
            var d = new Date();
            d.setTime(d.getTime() + expires*1000);
            expires = options.expires = d;
        }
        if (expires && expires.toUTCString) {
            options.expires = expires.toUTCString();
        }

        value = encodeURIComponent(value);

        var updatedCookie = name + "=" + value;

        for(var propName in options) {
            updatedCookie += "; " + propName;
            var propValue = options[propName];
            if (propValue !== true) {
                updatedCookie += "=" + propValue;
            }
        }

        document.cookie = updatedCookie;
        }catch(e){
            alert(e+" Функция setCookie");
        }
    }

    function writeAddress(address){
        if(address){
            $('#country-delivery').val(address.country.name);
            $('#city-delivery').val(address.city.name);
            $('#street-delivery').val(address.street.name);
            $('#building-delivery').val(address.building.fullNo);
            $('#flat-delivery').val(address.flatNo);
        }else{
            $('#country-delivery').val('');
            $('#city-delivery').val('');
            $('#street-delivery').val('');
            $('#building-delivery').val('');
            $('#flat-delivery').val('');
        }
    }

    function initRadioBtnClick(){
        $('.radio input').click(function(){
            var itogoRight = $('.itogo-right span');
            var orderDetails = 0;
            if ($(this).hasClass('courier-delivery')){
                //если доставка курьером
                client.setOrderDeliveryType(2);
                if (autocompleteAddressFlag){
                    initAutocompleteAddress();

                    var userAddresses = userServiceClient.getUserAddresses();
                    var userPhone = userServiceClient.getUserContacts().mobilePhone;
                    if(userPhone){
                        $('#phone-delivery').val(userPhone);
                    }
                    if(userAddresses.length > 0){
                        var homeAddress = userServiceClient.getUserContacts().homeAddress;
                        if(homeAddress){
                            writeAddress(homeAddress);
                        }
                        var userAddressesHtml = "";
                        var userAddressesLength = userAddresses.length;
                        for(var i = 0; i < userAddressesLength; i++){
                            userAddressesHtml += '<li><a href="#">'+
                                userAddresses[i].country.name+", "+userAddresses[i].city.name+", "+userAddresses[i].street.name+" "+userAddresses[i].building.fullNo+", кв. "+userAddresses[i].flatNo+
                                '</a></li>';
                        }

                        $('.delivery-dropdown .dropdown-menu').prepend(userAddressesHtml);
                        $('.delivery-dropdown .dropdown-menu a:not(".delivery-add-address")').click(function(e){
                            e.preventDefault();
                            var ind = $(this).parent().index();
                            writeAddress(userAddresses[ind]);
                        });
                        $('.delivery-add-address').click(function(e){
                            e.preventDefault();
                            writeAddress();
                            $('.delivery-dropdown .btn-group-text').text('Выбрать адрес');
                        });
                    }

                    autocompleteAddressFlag = 0;
                }

                $(this).closest('.delivery-right').find('.input-delivery').addClass('active').slideDown();
                orderDetails = client.getOrderDetails(currentOrderId);
                if (orderDetails.deliveryCost){
                    $('.delivery-cost').text(orderDetails.deliveryCost);
                }
                itogoRight.text(countItogo($('.catalog-order')));
                triggerDelivery = 1;
            }else{
                client.setOrderDeliveryType(1);
                $(this).closest('.delivery-right').find('.input-delivery').removeClass('active').slideUp();
                //var order = client.getOrder(currentOrderId);
                if (triggerDelivery){itogoRight.text(countItogo($('.catalog-order'))); triggerDelivery = 0;}
            }
        });
    }

    }catch(e){
        //alert(e + ' Ошибка в простых обработчиках');
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
                    $('.itogo-right span').text(countItogo($('.catalog-order')));
                }
            }
            $(this).closest('.prepack-line').slideUp(function(){
                var oldHeight = $(this).closest('.modal').height();
                $(this).closest('.modal').height(oldHeight - 53);
                $(this).remove();
                if (counterForSetFlag <= 2){
                    $('.error-prepack').hide();
                    productSelector.find('td>.ace-spinner').spinner('enable');
                }
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

    function initBasketInReload(){
        var catalogOrderLi = $('.catalog-order li');
        if(catalogOrderLi.length > 0){
            var order = client.getOrder(0);
            currentOrderId = order.id;
            var orderDetails = client.getOrderDetails(currentOrderId);
            var orderLines = orderDetails.odrerLines;
            var orderLinesLength = orderLines.length;
            var itsBasket = 1;
            var catalogOrder = $('.catalog-order');

            catalogOrderLi.each(function(){
                InitDeleteProduct($(this).find('.delete-product'));
                InitProductDetailPopup($(this).find('.product-link'));
                var newTdSumma = parseFloat($(this).find('.td-summa').text()).toFixed(1);
                $(this).find('.td-summa').text(newTdSumma);

                var productId = $(this).data('productid');
                for(var i = 0; i < orderLinesLength; i++){
                    if (orderLines[i].product.id == productId){
                        var minClientPack = $(this).find('td .spinner1').data('step');
                        InitSpinner($(this).find('td .spinner1'),orderLines[i].quantity,itsBasket,minClientPack);
                        if( getPacksLength(orderLines[i].packs) > 1 ){
                            $(this).find('td .ace-spinner').spinner('disable');
                        }
                        break;
                    }
                }

            });
            if(orderDetails.delivery == 1){
                $('.radio input:not(".courier-delivery")').trigger('click');
            }else if (orderDetails.delivery == 2){
                $('.radio .courier-delivery').trigger('click');
            }
            $('.itogo-right span').text(countItogo(catalogOrder));
            $('.empty-basket').hide();
        }
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
            //alert(p+" "+packs[p]);
            if (p && packs[p]){
                // чтобы предотвратить вывод удаленных линий, где packs[p]=0
                if (counter == 0){
                    // если самая первая линия
                    if(!isFirstModal){
                        currentModal.find('.packs .ace-spinner').spinner('value',packs[p]);
                        currentModal.find('.prepack-item:not(".packs") .ace-spinner').spinner('value',p);
                    }else{
                        InitSpinner(currentModal.find('.packs .spinner1'),packs[p],1,1);
                        InitSpinner(currentModal.find('.prepack-item:not(".packs") .spinner1'),p,1,productSelector.find('.spinner1').data('step'));
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

    function InitProductDetailPopup(selector){
        try{
        selector.click(function(e){
            e.preventDefault();

            var productSelector,
                name;
            if ($(this).closest('tr').length > 0 ){
                // если таблица
                productSelector = $(this).closest('tr');
                name = $(this).find('span span').text();
            }else{
                // если в корзине
                productSelector = $(this).closest('li');
                name= $(this).find('.product-right-descr').text()
            }

            var product= {
                name : name,
                price : productSelector.find('.product-price').text(),
                unitName: productSelector.find('.unit-name').text(),
                imageURL : $(this).find('img').attr('src')
            };
            var productDetails = client.getProductDetails(productSelector.data('productid'));
            var imagesSet = productDetails.imagesURLset;
            var options = productDetails.optionsMap;
            var currentModal = $(this).find('+.modal');

            if(imagesSet.length){
                var oldHeight = currentModal.height();
                currentModal.height(oldHeight + 25);
            }
            if (productDetails.prepackRequired){
                currentModal.addClass('modal-with-prepack');
            }

            var popupHtml = "";
            popupHtml += '<div class="modal-body">'+
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                '<div class="product-slider">'+
                    '<div class="slider flexslider">'+
                        '<ul class="slides">'+
                            '<li>'+
                                '<img src="'+product.imageURL+'" />'+
                            '</li>'+
                        '</ul>'+
                    '</div>';

                if(imagesSet.length){
                    popupHtml += '<div class="carousel flexslider">'+
                        '<ul class="slides">'+
                        '<li>'+
                        '<img src="'+product.imageURL+'" />'+
                        '</li>'+
                        '</ul>'+
                        '</div>';
                }

                popupHtml += '</div>'+
                '<div class="product-descr">'+
                    '<h3>'+product.name+'</h3>'+
                    '<div class="product-text">'+
                    '<div class="product-options">';
                    for(var p in options){
                        popupHtml += '<div>'+p+" "+options[p]+'</div>';
                    }
                    popupHtml += '</div>';
                        /*productDetails.fullDescr+
                    '</div>';*/

                    if (productDetails.prepackRequired){
                        popupHtml += '<div class="modal-footer with-prepack">'+
                            '<span>Цена: '+product.price+'</span>'+
                            '<div class="prepack-item packs">'+
                            '<input type="text" class="input-mini spinner1 prepack" />'+
                            '<span>упаковок</span>'+
                            '</div>'+
                            '<div class="prepack-item">по</div>'+
                            '<div class="prepack-item">'+
                            '<input type="text" class="input-mini spinner1" />'+
                            '<span>'+ product.unitName +'</span>'+
                            '</div>'+
                            '<div class="prepack-item"><a href="#" title="Добавить" class="fa fa-plus prepack-open"></a></div>';
                    }else{
                        popupHtml += '<div class="modal-footer">'+
                            '<span>Цена: '+product.price+'</span>'+
                            '<div class="prepack-item">'+
                            '<input type="text" class="input-mini spinner1" />'+
                            '<span>'+ product.unitName +'</span>'+
                            '</div>';
                    }
            popupHtml += "<div class='error-info error-prepack'></div>"+
                '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>';


                popupHtml += '<div class="prepack-list"></div><br>'+
                    '<a href="#" class="btn btn-primary btn-sm no-border full-descr">Подробное описание</a>'+
                    "<div class='product-fullDescr'>"+ productDetails.fullDescr +"</div>"+
                    '</div>'+
                '</div>'+
            '</div>';

            var spinnerStep = productSelector.find('td>.ace-spinner .spinner1').data('step');
            var fullDescrHeight;
            if (currentModal.find('.modal-body').length == 0){
                // если еще не открывали popup
                currentModal.append(popupHtml);
                if ($(this).closest('tr').length == 0 || $(this).closest('.order-products').length > 0){
                    // если мы в корзине или на странице заказов
                    if (productDetails.prepackRequired){
                        var isFirstModal = true;
                        initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal);
                    }else{
                        //если обычный товар
                        InitSpinner(currentModal.find('.spinner1'), productSelector.find('.ace-spinner').spinner('value'),1,productSelector.find('td>.ace-spinner .spinner1').data('step'));
                    }
                }else{
                    // если не в корзине
                    if (productDetails.prepackRequired){
                        InitSpinner(currentModal.find('.prepack-item.packs .spinner1'), 1);
                        InitSpinner(currentModal.find('.prepack-item:not(".packs") .spinner1'), productSelector.find('.ace-spinner').spinner('value'),0,spinnerStep);
                    }else{
                        InitSpinner(currentModal.find('.spinner1'), productSelector.find('.ace-spinner').spinner('value'),0,spinnerStep);
                    }
                    InitAddToBasket(currentModal.find('.fa-shopping-cart'));
                }
                currentModal.find('.prepack-open').click(function(e){
                    e.preventDefault();

                    var prepackHtml = '<div class="prepack-line no-init">' +
                        '<div class="prepack-item packs">'+
                        '<input type="text" class="input-mini spinner1 prepack" />'+
                        '<span>упаковок</span>'+
                        '</div>'+
                        '<div class="prepack-item">по</div>'+
                        '<div class="prepack-item">'+
                        '<input type="text" data-step="'+ spinnerStep +'" class="input-mini spinner1" />'+
                        '<span>'+ $(this).closest('.prepack-item').prev().find('span').text() +'</span>'+
                        '</div>'+
                        '<div class="prepack-item">'+
                        '<a href="#" class="close" title="Удалить">×</a>'+
                        '</div>'+
                        '</div>';
                    $(this).closest('.modal-footer').find('.prepack-list').append(prepackHtml);
                    var currentPrepackLine = $('.prepack-line.no-init');
                    var itsBasket;
                    ($(this).closest('tr').length == 0) ? itsBasket = 1 : itsBasket = 0;
                    InitSpinner(currentPrepackLine.find('.prepack-item.packs .spinner1'), 1,itsBasket);
                    InitSpinner(currentPrepackLine.find('.prepack-item:not(".packs") .spinner1'), spinnerStep,itsBasket,spinnerStep);
                    var productId = $(this).closest('tr').data('productid');

                    if ($(this).closest('tr').length == 0){
                        //если мы в корзине
                        // нужно сделать setOrderLine
                        var orderDetails = client.getOrderDetails(currentOrderId);
                        var orderLinesLength = orderDetails.odrerLines.length;
                        productId = $(this).closest('li').data('productid');
                        var packs,qnty;
                        var reCount = true;
                        for (var i = 0; i < orderLinesLength; i++){
                            if (orderDetails.odrerLines[i].product.id == productId){
                                // если это наш продукт в заказе
                                var tempPacks = orderDetails.odrerLines[i].packs;
                                if (tempPacks && tempPacks[parseFloat(spinnerStep).toFixed(1)]){
                                    reCount = false;
                                    productSelector.find('.error-prepack').text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта').show();
                                }
                                packs = tempPacks;
                                qnty = orderDetails.odrerLines[i].quantity;
                            }
                        }
                        var addedPackVal = currentPrepackLine.find('.packs .ace-spinner').spinner('value');
                        var addedQntyVal = parseFloat(currentPrepackLine.find('.prepack-item:not(".packs") .ace-spinner').spinner('value')).toFixed(1);
                        if (reCount) {
                            productSelector.find('.error-prepack').hide();
                            qnty += addedPackVal*addedQntyVal;
                            packs[addedQntyVal] = addedPackVal;
                            /*for(var p in packs){
                                alert(p+" "+packs[p]);
                            }*/
                            client.setOrderLine(productId,qnty,'sdf',packs);
                            productSelector.find('td>.ace-spinner').spinner('value',qnty);
                            productSelector.find('.td-summa').text((qnty*productSelector.find('.td-price').text()).toFixed(1));
                            $('.itogo-right span').text(countItogo($('.catalog-order')));
                        }
                        productSelector.find('td .ace-spinner').spinner('disable');
                    }
                    initRemovePrepackLine(currentPrepackLine.find('.prepack-item .close'),productId,productSelector);
                    currentPrepackLine.removeClass('no-init');

                    var oldHeight = $(this).closest('.modal').height();
                    $(this).closest('.modal').height(oldHeight + 53);

                });
                currentModal.find('.full-descr').click(function(){
                    var fullDescr = $('.product-fullDescr');
                    var oldHeight;
                    oldHeight = $(this).closest('.modal').height();
                    if(fullDescr.css('display') == 'none'){
                        $(this).closest('.modal').height(oldHeight + fullDescrHeight+10);
                        fullDescr.show(200);
                    }else{
                        //fullDescrHeight = $(this).closest('.modal').find('.product-fullDescr').height();
                        fullDescr.hide(200,function(){
                            $(this).closest('.modal').height(oldHeight - fullDescrHeight-10);
                        });
                    }
                });
                if($(this).closest('.catalog-order').length > 0){
                    //если это popup для корзины
                    currentModal.find('.fa-shopping-cart').click(function(){
                       currentModal.modal('hide');
                    });
                }
            }else{
                //если popup уже открывали
                alert('1');
                if ($(this).closest('tr').length == 0 || $(this).closest('.order-products').length > 0){
                    // если мы в корзине или на странице заказов
                    if (productDetails.prepackRequired){
                        isFirstModal = false;
                        initPrepackRequiredInModal($(this),currentModal,productSelector,isFirstModal);
                    }
                }
            }
            currentModal.modal();
            fullDescrHeight = currentModal.find('.product-fullDescr').height();
            currentModal.find('.product-fullDescr').hide();

            var carousel = currentModal.find('.carousel');
            var slider = currentModal.find('.slider');

            carousel.flexslider({
                animation: "slide",
                controlNav: false,
                animationLoop: false,
                slideshow: false,
                itemWidth: 60,
                itemMargin: 5,
                asNavFor: slider
            });

            slider.flexslider({
                animation: "slide",
                controlNav: false,
                animationLoop: false,
                slideshow: false,
                sync: carousel
            });

        });
        }catch(e){
            alert(e+" Функция InitProductDetailPopup");
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

    $('.btn-order').click(function(){
        try{
        if($('.input-delivery').hasClass('active') && !$('#phone-delivery').val()){
            $('.alert-delivery-phone').show();
        }else if ($('.input-delivery').hasClass('active') && (!$('#country-delivery').val() || !$('#city-delivery').val() || !$('#street-delivery').val() || !$('#building-delivery').val() || !$('#flat-delivery').val())){
            $('.alert-delivery-phone').hide();
            $('.alert-delivery-addr').show();
        }else{
            $('.alert-delivery-addr').hide();
            $('.alert-delivery-phone').hide();
            var popup = $('.modal-order-end');
            popup.modal();
            var orderList = $('.catalog-order>li');
            var productsHtmlModal = "";
            var i = 0;
            var spinnerValue = [];
            orderList.each(function(){
                spinnerValue[i++] = $(this).find('td>.ace-spinner').spinner('value');
                var productDetails = client.getProductDetails($(this).data('productid'));
                var disableClass;
                (productDetails.prepackRequired)? disableClass='class="prepack-disable"': disableClass='';
                productsHtmlModal+= '<tr data-productid="'+ $(this).data('productid') +'">'+
                    '<td>'+
                    '<div>'+
                    '<img src="'+ $(this).find('img').attr('src') +'" alt="картинка"/>'+
                    '<span>'+ $(this).find('.product-right-descr').text() +'</span>'+
                    '</div>'+
                    '</td>'+
                    '<td class="td-price">'+ $(this).find('.td-price').text()  +'</td>'+
                    '<td '+ disableClass +'>'+
                    '<input type="text" data-step="'+ $(this).find('td .spinner1').data('step') +'" class="input-mini spinner1 no-init" />'+
                    '</td>'+
                    '<td>'+ $(this).find('td .unit-name').text() +'</td>'+
                    '<td class="td-summa">'+ $(this).find('.td-summa').text()+
                    '</td>'+
                    '</tr>';
            });
            popup.find('.modal-body-list tbody').html('').append(productsHtmlModal);
            var inputDelivery = $('.input-delivery');

            if(inputDelivery.hasClass('active')){
                popup.find('.modal-footer').before('<div class="delivery-in-modal">Стоимость доставки: <span class="delivery-cost">'+ inputDelivery.find('.delivery-cost').text() +'</span> руб</div>');
            }else{
                popup.find('.delivery-in-modal').hide();
            }

            $('.modal-itogo span').text($('.itogo-right span').text());

            var spinnerNoInit = popup.find('.spinner1.no-init');
            i = 0;
            spinnerNoInit.each(function(){
                InitSpinner($(this),spinnerValue[i++],0,$(this).data('step'));
            });
            spinnerNoInit.removeClass('no-init');
            $('.prepack-disable').find('.ace-spinner').spinner('disable');

            popup.find('.btn-order').click(function(){
                // добавление в базу нового города, страны, улицы и т.д (если курьером)
                if ($('.input-delivery').hasClass('active')){
                    var countries = userServiceClient.getCounties();
                    var countriesLength = countries.length;
                    var inputCountry = $('#country-delivery').val();
                    var country,countryId = 0;
                    for (var i = 0; i < countriesLength; i++){
                        if (countries[i].name == inputCountry){
                            country = countries[i];
                            countryId = country.id;
                        }
                    }
                    if (!countryId){
                        country = userServiceClient.createNewCountry(inputCountry);
                        countryId = country.id;
                    }

                    var cities = userServiceClient.getCities(countryId);
                    var citiesLength = cities.length;
                    var inputCity = $('#city-delivery').val();
                    var city,cityId = 0;
                    for (i = 0; i < citiesLength; i++){
                        if (cities[i].name == inputCity){
                            city = cities[i];
                            cityId = city.id;
                        }
                    }
                    if (!cityId){
                        city = userServiceClient.createNewCity(countryId,inputCity);
                        cityId = city.id;
                    }

                    var streets = userServiceClient.getStreets(cityId);
                    var streetsLength = streets.length;
                    var inputStreet = $('#street-delivery').val();
                    var street,streetId = 0;
                    for (i = 0; i < streetsLength; i++){
                        if (streets[i].name == inputCity){
                            street = streets[i];
                            streetId = street.id;
                        }
                    }
                    if (!streetId){
                        street = userServiceClient.createNewStreet(cityId,inputStreet);
                        streetId = street.id;
                    }

                    var buildings = userServiceClient.getBuildings(streetId);
                    var buildingsLength = buildings.length;
                    var inputBuilding = $('#building-delivery').val();
                    var building,buildingId = 0;
                    for (i = 0; i < buildingsLength; i++){
                        if (buildings[i].fullNo == inputBuilding){
                            building = buildings[i];
                            buildingId = building.id;
                        }
                    }
                    if (!buildingId){
                        building = userServiceClient.createNewBuilding(streetId,inputBuilding,0,0);
                        buildingId = building.id;
                    }


                    // передаем адресс доставки
                    //console.log(country.id+" "+city.id+" "+street.id+" "+building.id+" "+$('#flat-delivery').val()+" "+$('#order-comment').val());
                    /*var deliveryAddress = new com.vmesteonline.be.PostalAddress(
                        country : country,
                        city : city,
                        street : street,
                        building : building,
                        staircase : 0,
                        floor: 0,
                        flatNo: parseInt($('#flat-delivery').val()),
                        comment: $('#order-comment').val()
                };*/
                var deliveryAddress = new com.vmesteonline.be.PostalAddress();
                    deliveryAddress.country = country;
                    deliveryAddress.city = city;
                    deliveryAddress.street = street;
                    deliveryAddress.building = building;
                    deliveryAddress.staircase = 0;
                    deliveryAddress.floor= 0;
                    deliveryAddress.flatNo = parseInt($('#flat-delivery').val());
                    deliveryAddress.comment = $('#order-comment').val();

                     client.setOrderDeliveryAddress(deliveryAddress);
                }
                client.confirmOrder();
                alert('Ваш заказ принят !');
                $('.modal-order-end').modal('hide');
                cleanBasket();
            })
        }
        }catch(e){
            alert(e+" Функция $('.btn-order').click");
        }
    });

    $('.btn-cancel').click(function(){
       var quest = confirm('Вы действительно хотите отменить заказ ? Ваша корзина вновь станет пустой.');
        if (quest){
            cleanBasket();
            client.cancelOrder();
        }
    });

    function cleanBasket(){
        $('.additionally-order').addClass('hide');
        $('.empty-basket').removeClass('hide');
        $('.catalog-order').html('');
    }

    function InitLoadCategory(catID){
        try{
        /* замена меню категорий */

        var productCategories = client.getProductCategories(catID);
        var categoriesLength = productCategories.length;
        var shopMenu = '';
        var firstMenuItem = "";

        if (productCategories[0] && productCategories[0].parentId != 0 || productCategories[0] === undefined){
            firstMenuItem = '<li>'+
                '<a href="#">'+
                    '<i class="fa fa-reply-all"></i>'+
                    '<span>Назад</span>'+
                '</a>'+
                '</li>';
        }

        for(var i = 0; i < categoriesLength; i++){
            shopMenu += '<li data-parentid="'+ productCategories[i].parentId +'" data-catid="'+ productCategories[i].id +'">'+
                '<a href="#">'+
                '<i class="fa fa-beer"></i>'+
                '<span>'+ productCategories[i].name +'</span>'+
                '</a>'+
                '</li>';
        }

        $('.shop-menu ul').html(firstMenuItem).append(shopMenu);

        /* новый список товаров */
        var productsList = client.getProducts(0,10,catID).products;
        var productListLength = productsList.length;
        var productsHtml = '';
        var productDetails;
        for (i = 0; i < productListLength; i++){
            productDetails = client.getProductDetails(productsList[i].id);
            var unitName = "";
            if (productsList[i].unitName){unitName = productsList[i].unitName;}
            productsHtml += '<tr data-productid="'+ productsList[i].id +'">'+
                '<td>'+
                '<a href="#" class="product-link">'+
                '<img src="'+ productsList[i].imageURL +'" alt="картинка"/>'+
                '<span><span>'+ productsList[i].name +'</span>'+ productsList[i].shortDescr +'</span>'+
                '</a>'+
                '<div class="modal">'+
                '</div>'+
                '</td>'+
                '<td class="product-price">'+ productsList[i].price  +'</td>'+
                '<td>'+
                '<input type="text" data-step="'+ productsList[i].minClientPack +'" class="input-mini spinner1" /> '+
                '</td>'+
                '<td>'+ '<span class="unit-name">'+ unitName +'</span></td>'+
                '<td>'+
                '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>'+
                '</td>'+
                '</tr>';
        }
        $('.main-content .catalog table tbody').html("").append(productsHtml);

        }catch(e){
            alert(e+" Функция InitLoadCategory");
        }

        /* подключение событий */
        initProductsSpinner();
        InitProductDetailPopup($('.product-link'));
        InitAddToBasket($('.fa-shopping-cart'));
        InitClickOnCategory();

    }

    function InitClickOnCategory(){
        try{
        $('.shop-menu li a').click(function(e){
            e.preventDefault();
            if ($(this).hasClass('fa-reply-all')){
                InitLoadCategory(prevParentId[parentCounter]);
                setCookie('catid',prevParentId[parentCounter]);
                parentCounter--;
                setCookie('arrayPrevCat',prevParentId);
                setCookie('prevCatCounter',parentCounter);

            }
            else {
                parentCounter++;
                //console.log(prevParentId[parentCounter]);
                prevParentId[parentCounter] = $(this).parent().data('parentid');
                //console.log($(this).parent().data('catid'));
                InitLoadCategory($(this).parent().data('catid'));
                setCookie('catid',$(this).parent().data('catid'));
                setCookie('arrayPrevCat',prevParentId);
                setCookie('prevCatCounter',parentCounter);
            }
        });
        }catch(e){
            alert(e+" Функция InitClickOnCategory");
        }
    }

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

            $('.itogo-right span,.modal-itogo span').text(countItogo($('.modal-body-list')));
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
                //alert('2');
                var price = productSelector.find('.td-price').text();
                price = parseFloat(price);
                productSelector.find('.td-summa').text((price*qnty).toFixed(1));
                $('.itogo-right span').text(countItogo($('.catalog-order')));
                $('.modal-itogo span').text(countItogo($('.modal-body-list')));
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

    function countItogo(sel){
        try{
        var summa = 0;
        sel.find('.td-summa').each(function(){
            summa += parseFloat($(this).text());
        });
        var orderDetails = client.getOrderDetails(currentOrderId);
        summa += orderDetails.deliveryCost;
        }catch(e){
            //alert(e+" Функция countItogo");
        }
        return summa.toFixed(1);
    }

    function InitDeleteProduct(selector){
        try{
        selector.click(function(){
            $(this).closest('li').slideUp(function(){
                $(this).detach();
                $('.itogo-right span').text(countItogo($('.catalog-order')));
                if ($('.catalog-order li').length == 0){
                    $('.additionally-order').addClass('hide');
                    $('.empty-basket').removeClass('hide');
                    client.deleteOrder();
                }
            });
            client.removeOrderLine($(this).closest('li').data('productid'));
        });
        }catch(e){
            alert(e+" Функция InitDeleteProduct");
        }
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
                '<input type="text" data-step="'+  orderLines[j].product.minClientPack +'" class="input-mini spinner1" />'+
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
                '<td class="td3">'+ tempDate.getDate() +"."+tempDate.getMonth()+"."+tempDate.getFullYear()+ '</td>'+
                '<td class="td4">'+
                '<div class="order-status">'+orderStatus +'</div>'+
                '<div>'+ orderLinks +'</div>'+
                '</td>'+
                '<td class="td5">'+ orderDelivery +'<br> ' +
                orderDetails.deliveryTo.city.name+", "+orderDetails.deliveryTo.street.name+" "+orderDetails.deliveryTo.building.fullNo+", кв."+
                orderDetails.deliveryTo.flatNo+
                '</td>'+
                '<td class="td6">'+ orders[i].totalCost.toFixed(1) +'</td>'+
                '<td class="td7">'+
                '<button class="btn btn-sm btn-primary no-border repeat-order-btn">Повторить</button>'+
                '<button class="btn btn-sm btn-primary no-border add-order-btn">Добавить в корзину</button>'+
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
            alert(e+" Функция createOrdersHtml");
        }
        return ordersHtml;
    }

    function initOrderPlusMinus(selector){
        try{
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
                    InitSpinner(orderProducts.find('tbody tr:eq('+ i +') .spinner1'),orderLines[i].quantity,0,orderProducts.find('tbody tr:eq('+ i +') .spinner1').data('step'));
                }

                InitAddToBasket(orderProducts.find('.fa-shopping-cart'));
                InitProductDetailPopup(orderProducts.find('.product-link'));
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
        }catch(e){
            alert(e+" Функция initOrderPlusMinus");
        }
    }

    var flagFromBasketClick = 0;
    var selectorForCallbacks;

    function BasketTrigger(selector){
        selector.trigger('click');
    }

    function getPacksLength(packs){
        var counter = 0;
        for(var p in packs){
            //alert(p+" "+packs[p]);
            if(p && packs[p]){
            counter++;
            }
        }
        return counter;
    }

    function AddProductToBasketCommon(currentProduct,packs){
        var addedProductFlag = 0;
        $('.catalog-order li').each(function(){
            if ($(this).data('productid') == currentProduct.id){
                addedProductFlag = 1;
                if(packs){
                    $(this).find('td>.ace-spinner').spinner('disable');
                }
            }
        });
        if (addedProductFlag){
            // если такой товар уже есть
            var basketProductSelector = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]');
            var currentSpinner = basketProductSelector.find('td>.ace-spinner');
            //var newSpinnerVal = currentSpinner.spinner('value')+currentProduct.qnty;
            var newSpinnerVal = currentProduct.qnty;
            currentSpinner.spinner('value',newSpinnerVal);
            /* здесь комментарии хранят код, к-й добавляет добавляемое кол-во к тому, что уже есть в корзине,
                а не заменяет
            */
            var newPacks;
/*            if (packs){
                var orderDetails = client.getOrderDetails(currentOrderId);
                var orderLines = orderDetails.odrerLines;
                var orderLinesLength = orderLines.length;
                var orderPacks;
                for(var i = 0; i < orderLinesLength; i++){
                    if (orderLines[i].product.id == currentProduct.id){
                       orderPacks = orderLines[i].packs;
                    }
                }
                var oldPacksQnty,newPacksQnty;

                if(currentProduct.prepackLine.length != 0){
                    // если prepackLine не одна
                    currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('td>.ace-spinner');
                    currentSpinner.spinner('disable');
                        newPacks = orderPacks;
                        for(var p1 in packs){
                            oldPacksQnty = 0;
                            for (var p2 in orderPacks){
                                if(parseFloat(p1).toFixed(1) == parseFloat(p2).toFixed(1)){
                                    // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                                    oldPacksQnty = orderPacks[p2];
                                    newPacksQnty = oldPacksQnty + packs[p1];
                                    newPacks[p2] = newPacksQnty;
                                    break;
                                }
                            }
                            if(!oldPacksQnty){
                                newPacks[p1] = packs[p1];
                            }
                        }
                }else{
                    // если одна линия
                    if(getPacksLength(orderPacks) == 1){
                        // если у этого товара в заказе только одна линия
                        for(var p in orderPacks){
                            // т.к линия одна у цикла будет только один проход
                            newPacks = orderPacks;
                            //alert(p+" "+currentProduct.quantVal);
                            if(p == currentProduct.quantVal){
                                // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                                oldPacksQnty = orderPacks[p];
                                newPacksQnty = oldPacksQnty + currentProduct.packVal;
                                newPacks[p] = newPacksQnty;
                            }else{
                                // если в корзине товар, где prepackLine с другим кол-м товара (то меняем packs, добавляя новую линию prepackLine)
                                newPacks[currentProduct.quantVal] = currentProduct.packVal;
                            }
                        }
                    }else{
                        // если у этого товара в заказе несколько линий
                        newPacks = orderPacks;
                        for(var p in orderPacks){
                            //alert(p+" "+currentProduct.quantVal);
                            if(p == currentProduct.quantVal){
                                // если в корзине товар, где prepackLine с таким же кол-м товара (то оставляем эту линию, но увеличиваем соответственно кол-во упаковок)
                                oldPacksQnty = orderPacks[p];
                                newPacksQnty = oldPacksQnty + currentProduct.packVal;
                                newPacks[p] = newPacksQnty;
                                //alert('4 '+ oldPacksQnty+" "+currentProduct.packVal+" "+newPacksQnty );
                                break;
                            }
                        }
                        if(!oldPacksQnty){
                            newPacks[currentProduct.quantVal] = currentProduct.packVal;
                        }
                    }
                }

            }*/

            client.setOrderLine(currentProduct.id,newSpinnerVal,'sdf',packs);
            var newSumma = (newSpinnerVal*parseFloat(basketProductSelector.find('.td-price').text())).toFixed(1);
            basketProductSelector.find('.td-summa').text(newSumma);
            $('.itogo-right span').text(countItogo($('.catalog-order')));
        }else{
            // если такого товара еще нет
            AddSingleProductToBasket(currentProduct,currentProduct.qnty,currentProduct.unitName);
            client.setOrderLine(currentProduct.id,currentProduct.qnty,'sdf',packs);
            /* повтор функции заменить потом --- */
            if(currentProduct.prepackLine.length != 0){
                currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.id +'"]').find('td>.ace-spinner');
                currentSpinner.spinner('disable');
            }
            /* ---- */
        }
    }

    function InitAddToBasket(selector){
        try{
        selector.click(function(e){
            e.preventDefault();
            var errorPrepack = false;
            if (!globalUserAuth){
                // если пользователь не залогинен
                selectorForCallbacks = $(this);
                callbacks.add(BasketTrigger);
                $('.modal-auth').modal();
            }else{
                // если пользователь залогинен
                var currentProductSelector = $(this).closest('tr');
                var spinnerValue = currentProductSelector.find('td>.ace-spinner').spinner('value');
                var currentProduct = {
                    id : currentProductSelector.data('productid'),
                    imageURL : currentProductSelector.find('.product-link img').attr('src'),
                    name : currentProductSelector.find('.product-link span span').text(),
                    price : currentProductSelector.find('.product-price').text(),
                    unitName :currentProductSelector.find('.unit-name').text(),
                    step :  currentProductSelector.find('.spinner1').data('step'),
                    prepackLine : currentProductSelector.find('.prepack-line'),
                    qnty : spinnerValue,
                    packVal : 1,
                    quantVal :  currentProductSelector.find('td .spinner1').data('step')
                    //btnSelector: $(this)
                };
                var productDetails = client.getProductDetails(currentProduct.id);
                var packs = [];
                if (productDetails.prepackRequired){
                   // если это товар с prepackRequired
                    currentProduct.quantVal = spinnerValue;
                    if (currentProductSelector.find('.modal-body').length > 0){
                        // если пользватель открывал модальное окно с инфой о продукте
                        var packVal = currentProductSelector.find('.packs:eq(0) .ace-spinner').spinner('value');
                        var quantVal = currentProductSelector.find('.prepack-item:not(".packs") .ace-spinner').eq(0).spinner('value');
                        currentProduct.packVal = packVal;
                        currentProduct.quantVal = quantVal;
                        currentProduct.qnty = packVal*quantVal;
                        packs[quantVal] = packVal; // если только одна линия с упаковкой
                      if(currentProduct.prepackLine.length != 0){
                          // если линий более чем одна
                          var oldQuantVal = 0;
                          var firstQuantVal = $('.modal-footer.with-prepack>.prepack-item:not(".packs") .ace-spinner').spinner('value');
                          currentProduct.prepackLine.each(function(){
                              packVal = $(this).find('.packs .ace-spinner').spinner('value');
                              quantVal = $(this).find('.prepack-item:not(".packs") .ace-spinner').spinner('value');
                              if (quantVal == oldQuantVal || quantVal == firstQuantVal){
                                  currentProductSelector.find('.error-prepack').text('Товар не возможно добавить: вы создали две линни с одинаковым количеством продукта').show();
                                  errorPrepack = true;
                              }
                              packs[quantVal] = packVal;
                              currentProduct.qnty += packVal*quantVal;
                              oldQuantVal = quantVal;
                          });
                      }
                    }else{
                        // если не открывал модальное окно
                        packs[currentProduct.qnty] = 1; // значаение packs по умолчанию
                        currentProduct.packsQnty = 1;
                    }
                }else{
                    // если обычный товар
                    packs = 0;
                }
                /*for(var p in packs){
                    alert(p+" "+packs[p]);
                }*/
               if (!errorPrepack){
                    if ($('.additionally-order').hasClass('hide')){
                        // если это первый товар в корзине
                        flagFromBasketClick = 1;
                        dPicker.datepicker('setVarFreeDays',currentProduct, currentProduct.qnty,0,packs,AddSingleProductToBasket,AddOrdersToBasket,AddProductToBasketCommon);
                        dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click');
                    }else{
                        // если в корзине уже что-то есть
                        AddProductToBasketCommon(currentProduct,packs)
                    }
                }

                if ($(this).closest('.modal').length>0 && !errorPrepack){
                    $(this).closest('.modal').modal('hide');
                    currentProductSelector.find('.error-prepack').hide();
                }
            }
        });
        }catch(e){
            alert(e+" Функция InitAddToBasket");
        }
    }

    function AddSingleProductToBasket(currentProduct,spinnerValue,unitName){
        try{
        var productDetails = client.getProductDetails(currentProduct.id);
        var productHtml = '<li data-productid="'+ currentProduct.id +'">'+
            '<table>'+
            '<tr>'+
            '<td class="td-price product-price">'+ currentProduct.price +'</td>'+
            '<td><input type="text" data-step="'+ currentProduct.step +'" class="input-mini spinner1 no-init" /><span class="unit-name">'+ unitName +'</span></td>'+
            '<td class="td-summa">'+ (currentProduct.price*spinnerValue).toFixed(1) +'</td>'+
            '<td><a href="#" class="delete-product no-init">×</a></td>'+
            '</tr>'+
            '</table>'+
            '<a href="#" class="product-link no-init">'+
            '<span><img src="'+ currentProduct.imageURL +'" alt="картинка"/></span>'+
            '<div class="product-right-descr">'+
            currentProduct.name+
            '</div>'+
            '</a>'+
            '<div class="modal">'+
            '</div>'+
            '</li>';
        }catch(e){
            alert(e+" Функция AddSingleProductToBasket");
        }

        var catalogOrder = $('.catalog-order');
        catalogOrder.append(productHtml);
        $('.itogo-right span').text(countItogo(catalogOrder));

        var deleteNoInit = $('.catalog-order .delete-product.no-init');
        InitDeleteProduct(deleteNoInit);
        deleteNoInit.removeClass('no-init');

        var popupNoInit = $('.catalog-order .product-link.no-init');
        InitProductDetailPopup(popupNoInit);
        popupNoInit.removeClass('no-init');

        var spinnerNoInit = $('.catalog-order .spinner1.no-init');
        var itsBasket = 1;
        InitSpinner(spinnerNoInit,spinnerValue,itsBasket,currentProduct.step);
        spinnerNoInit.removeClass('no-init');

    }

    dPicker.click(function(){
        try{
        if (flagFromBasketClick){
            // клик при добавленни товара в корзину
            dPicker.on('hide',function(){
                if (flagFromBasketClick){
                    $(this).datepicker('triggerFlagBasket');
                    flagFromBasketClick = 0;
                }
            });
        }
        }catch(e){
            alert(e+" Функция dPicker.click");
        }
    });

    function addSingleOrderToBasket(orderId,addType){
        try{
        var orderDetails,
            curProd,
            spinVal,i;
        if (addType == 'replace'){
            orderDetails = client.getOrderDetails(orderId);
            var orderLines = orderDetails.odrerLines;
            var orderLinesLength = orderLines.length;
            for(i = 0; i < orderLinesLength; i++){
                curProd = orderLines[i].product;
                spinVal = orderLines[i].quantity;
                var packs = orderLines[i].packs;
                client.setOrderLine(curProd.id,spinVal,"asd",packs);
            }
        }else if (addType == 'append'){
            orderDetails = client.appendOrder(orderId);
        }
        orderLines = orderDetails.odrerLines;
        orderLinesLength = orderLines.length;
        for(i = 0; i < orderLinesLength; i++){
            curProd = orderLines[i].product;
            spinVal = orderLines[i].quantity;
            AddSingleProductToBasket(curProd,spinVal,curProd.unitName);
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
            flagFromBasketClick = 1;
            dPicker.datepicker('setVarFreeDays',0, 0, orderData,0,AddSingleProductToBasket,AddOrdersToBasket,AddProductToBasketCommon);
            dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click').datepicker('triggerFlagBasket');
            flagFromBasketClick = 0;
        });
        selector.find('.add-order-btn').click(function(){
            var orderData= {
                itsOrder: true,
                itsAppend: true,
                orderId : $(this).closest('.order-item').data('orderid')
            };
            if ($('.additionally-order').hasClass('hide')){
                flagFromBasketClick = 1;
                dPicker.datepicker('setVarFreeDays',0, 0, orderData,0,AddSingleProductToBasket,AddOrdersToBasket,AddProductToBasketCommon);
                dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click');
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
                client.getOrder($(this).closest('.order-item').data('orderid'));
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

           var tempOrderId = currentOrderId;
           client.getOrder($(this).closest('.order-item').data('orderid'));
           client.confirmOrder();
            if(tempOrderId){
                client.getOrder(tempOrderId);
            }
            alert('Заказ подтвержден !');
            $(this).closest('td').find('.order-status').text('Подтвержден');
            $(this).parent().remove();

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
            setSidebarHeight();
        });
        }catch(e){
            alert(e+" Функция initShowMoreOrders");
        }
    }

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

    function GoToOrdersTrigger(){
        $('.go-to-orders').trigger('click');
    }

    $('.shop-trigger').click(function(e){
        e.preventDefault();
        try{
            var shopOrders = $('.shop-orders');
            var ordersList = $('.orders-list');

            if($(this).hasClass('back-to-shop')){
                shopOrders.hide();
                $('.shop-products').show(function(){
                    setSidebarHeight();
                });
            }else{
                if (!globalUserAuth){
                    callbacks.add(GoToOrdersTrigger);
                    $('.modal-auth').modal();
                }else{
                    $('.shop-products').hide();
                    var nowTime = parseInt(new Date().getTime()/1000);
                    var day = 3600*24;
                    var orders = client.getOrders(0,nowTime+90*day);
                    initVarForMoreOrders();
                    // если всегда делать createOrdersHtml, то странциа заказов будет обновляться в реальном времени
                    // а так можно оптимизировать и не делать createOrderHtml каждый раз при перезагрузке
                    ordersList.html('').append(createOrdersHtml(orders));
                    InitProductDetailPopup($('.product-link'));
                    initShowMoreOrders(orders);
                    initOrdersLinks();
                    var ordersNoInit = $('.orders-no-init');
                    initOrderPlusMinus(ordersNoInit);
                    initOrderBtns(ordersNoInit);
                    ordersNoInit.removeClass('orders-no-init');
                    shopOrders.show();
                    setSidebarHeight();
                }
            }
        }catch(e){
            alert(e+" Функция $('.shop-trigger').click");
        }
    });

// логин

    var callbacks = $.Callbacks();

    transport = new Thrift.Transport("/thrift/AuthService");
    protocol = new Thrift.Protocol(transport);
    var clientAuth = new com.vmesteonline.be.AuthServiceClient(protocol);

    $('.login-form .btn-submit').click(function(e){
        e.preventDefault();
        login($(this));
    });
    $('.reg-form .btn-submit').click(function(e){
        e.preventDefault();
        reg($(this));
    });
    $('.remember-link').click(function(e){
        e.preventDefault();
        //clientAuth.sendChangePasswordCodeRequest('забыл пароль адресат','sdf%code%sdf%name%sdf');
        //clientAuth.changePasswordOfUser('qq@qq.ru','qq','qq');
    });

    function AuthRealTime(selector){
        globalUserAuth = true;
        selector.closest('.modal-auth').modal('hide');
        // ставим shopID
        var shops = client.getShops();
        client.getShop(shops[0].id);

        //$('.user-info').html('');
        var shortUserInfo = userServiceClient.getShortUserInfo();
        var shortUserInfoHtml =  '<small>'+ shortUserInfo.firstName +'</small>'+ shortUserInfo.lastName;
        $('.user-info').html(shortUserInfoHtml).after('<i class="icon-caret-down"></i>');
        var dropdown = '<ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">'+
            '<li><a href="#"> <i class="icon-cog"></i> Настройки'+
            '</a></li>'+
            '<li><a href="#"> <i class="icon-user"></i> Профиль'+
            '</a></li>'+
            '<li class="divider"></li>'+
            '<li><a href="#"> <i class="icon-off"></i> Выход'+
            '</a></li>'+
        '</ul>';
        $('.user-short .dropdown-toggle').append(dropdown);

        $('.dropdown-toggle').click(function(){
           $(this).find('.user-menu').toggle();
        });

        callbacks.fire(selectorForCallbacks);
        callbacks.empty();
    }

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = clientAuth.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
                $('.login-error').hide();
                if (selector.closest('.modal-auth').length > 0){
                    //document.location.replace("/shop.jsp");
                    AuthRealTime(selector);
                }else{
                    document.location.replace("/main.jsp");
                }
            } else {
                result.val(session.error);
                result.css('color', 'black');
            }

        } catch (ouch) {
            $('.login-error').show();
        }
    }

    function reg(selector) {
        if (clientAuth.checkEmailRegistered($("#email").val())) {
            $('.email-alert').css('display','block');
        }else{
            var userId = clientAuth.registerNewUser($("#login").val(), "", $("#pass").val(), $("#email").val());
            clientAuth.login($("#email").val(), $("#pass").val());
            if ( selector.closest('.modal-auth').length > 0) {
                //document.location.replace("/shop.jsp");
                AuthRealTime(selector);
            }else{
                document.location.replace("/main.jsp");
            }
        }
    }

    /*----*/
    /*var nowTime = parseInt(new Date().getTime()/1000);
    nowTime -= nowTime%86400;
    var dateArray = [];
    var day = 3600*24;
    dateArray[nowTime] = 1;
    dateArray[nowTime+day] = 1;
    dateArray[nowTime+2*day] = 2;
    dateArray[nowTime+3*day] = 2;
    dateArray[nowTime+4*day] = 1;
    dateArray[nowTime+6*day] = 1;
    dateArray[nowTime+9*day] = 1;
    dateArray[nowTime-9*day] = 2;
    client.setDates(dateArray);
    //var datesArray = client.getDates(nowTime-10*day,nowTime+10*day);
    for (var p in datesArray){
        //console.log(p);
    }*/
    /*------*/

    /*var orderDate = parseInt(new Date().getTime()/1000);
     orderDate -= orderDate%86400
     var day = 3600*24;
     //var orders = client.getOrders(orderDate-day,orderDate+day);
     var orders = client.getOrders(0,orderDate+60*day);
     var ordersLength = orders.length;
     /*var orderList = [];
     var counter = 0;
     for (var i = 0; i < ordersLength; i++){
     if (orders[i].date = orderDate){
     orderList[counter++] = orders[i];
     }
     console.log(i+ " "+orders[i].date+" "+orderDate+" "+orders[i].id);
     }*/

});