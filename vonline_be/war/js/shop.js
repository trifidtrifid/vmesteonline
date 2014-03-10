$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/ShopService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

    transport = new Thrift.Transport("/thrift/UserService");
    protocol = new Thrift.Protocol(transport);
    var userServiceClient = new com.vmesteonline.be.UserServiceClient(protocol);

    /* простые обработчики событий */
    var w = $(window),
        showRight = $('.show-right'),
        hideRight = $('.hide-right'),
        shopRight = $('.shop-right'),
        showRightTop = (w.height()-showRight.width())/ 2;

    showRight.css('top',showRightTop);
    $('#sidebar, .shop-right').css('min-height', w.height());

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

    $('.radio input').click(function(){
        if ($(this).hasClass('courier-delivery')){
            client.setOrderDeliveryType(2);
            $(this).closest('.delivery-right').find('.input-delivery').addClass('active').slideDown();
        }else{
            client.setOrderDeliveryType(1);
            $(this).closest('.delivery-right').find('.input-delivery').removeClass('active').slideUp();
        }
    });

    //custom autocomplete (category selection)
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

    var dataSearch = [
        { label: "anders", category: "" },
        { label: "andreas", category: "" },
        { label: "antal", category: "" },
        { label: "annhhx10", category: "Products" },
        { label: "annk K12", category: "Products" },
        { label: "annttop C13", category: "Products" },
        { label: "anders andersson", category: "People" },
        { label: "andreas andersson", category: "People" },
        { label: "andreas johnson", category: "People" }
    ];
    $( "#search" ).catcomplete({
        delay: 0,
        source: dataSearch
    });

    /* автозаполнение адреса доставки  */
    var addressesBase = userServiceClient.getAddressCatalogue();

    var countries = addressesBase.countries;
    var countriesLength = countries.length;
    var countryTags = [];
    for (var i = 0; i < countriesLength; i++){
        countryTags[i] = countries[i].name;
    }
    var cities = addressesBase.cities;
    var citiesLength = cities.length;
    var cityTags = [];
    for (i = 0; i < citiesLength; i++){
        cityTags[i] = cities[i].name;
    }
    var streets = addressesBase.streets;
    var streetsLength = streets.length;
    var streetTags = [];
    for (i = 0; i < streetsLength; i++){
        streetTags[i] = streets[i].name;
    }
    var buildings = addressesBase.buildings;
    var buildingsLength = buildings.length;
    var buildingTags = [];
    for (i = 0; i < buildingsLength; i++){
        buildingTags[i] = buildings[i].fullNo;
    }
    $( "#country-delivery" ).autocomplete({
        source: countryTags
    });
    $( "#city-delivery" ).autocomplete({
        source: cityTags
    });
    $( "#street-delivery" ).autocomplete({
        source: streetTags
    });
    $( "#building-delivery" ).autocomplete({
        source: buildingTags
    });

    /*var dataDeliveryCities = [
     { label: "Санкт-Петербург", category: "" },
     { label: "Москва", category: "" },
     { label: "Казань", category: "" }
     ];
     var dataDeliveryStreets = [
     { label: "Ленинградская", category: "" },
     { label: "Московский проспект", category: "" },
     { label: "Шаумяна", category: "" }
     ];
     $( "#city-delivery" ).catcomplete({
     delay: 0,
     source: dataDeliveryCities
     });
     $( "#street-delivery" ).catcomplete({
     delay: 0,
     source: dataDeliveryStreets
     });*/

    var dPicker = $('.date-picker');

    dPicker.datepicker({autoclose:true,language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    $('.datepicker').find('.prev').click(function(){
        //alert('1');
    });


    InitSpinner($('.spinner1'),1);
    InitAddToBasket($('.fa-shopping-cart'));
    InitProductDetailPopup($('.product-link'));
    // переключение между категориями
    InitClickOnCategory();
    InitDeleteProduct($('.delete-product'));
    initOrderPlusMinus($('.shop-orders'));

/* функции */
    var prevParentId = [],
        parentCounter = 0;

    setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

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
        var matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
        ));
        return matches ? decodeURIComponent(matches[1]) : undefined;
    }

    function setCookie(name, value, options) {
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
    }

    function InitProductDetailPopup(selector){
        selector.click(function(e){
            e.preventDefault();

            var productSelector,
                name;
            if ($(this).closest('tr').length > 0 ){
                productSelector = $(this).closest('tr');
                name = $(this).find('span span').text();
            }else{
                productSelector = $(this).closest('li');
                name= $(this).find('.product-right-descr').text()
            }

            var product= {
                name : name,
                price : productSelector.find('.product-price').text(),
                unitName: productSelector.find('.ace-spinner + span').text(),
                imageURL : $(this).find('img').attr('src')
            };
            var productDetails = client.getProductDetails(productSelector.data('productid'));
            var imagesSet = productDetails.imagesURLset;
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
                    productDetails.fullDescr+
                    '</div>'+
                    '<div class="modal-footer">'+
                        '<span>Цена: '+product.price+'</span>'+
                        '<input type="text" class="input-mini spinner1" />'+
                     product.unitName;

            if ($(this).closest('tr').length > 0 ){
                popupHtml += '<i class="fa fa-shopping-cart"></i>';
            }

                popupHtml += '</div>'+
                '</div>'+
            '</div>';

            var currentModal = $(this).find('+.modal');
            if (currentModal.find('.modal-body').length == 0){
                currentModal.append(popupHtml);
                InitSpinner(currentModal.find('.spinner1'), productSelector.find('.ace-spinner').spinner('value'));
            }
            currentModal.modal();
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
    }

    $('.btn-order').click(function(){
        if(!$('#phone-delivery').val()){
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
                spinnerValue[i++] = $(this).find('.ace-spinner').spinner('value');
                productsHtmlModal+= '<tr>'+
                    '<td>'+
                    '<div>'+
                    '<img src="'+ $(this).find('img').attr('src') +'" alt="картинка"/>'+
                    '<span>'+ $(this).find('.product-right-descr').text() +'</span>'+
                    '</div>'+
                    '</td>'+
                    '<td class="td-price">'+ $(this).find('.td-price').text()  +'</td>'+
                    '<td>'+
                    '<input type="text" class="input-mini spinner1 no-init" />'+
                    '</td>'+
                    '<td class="td-summa">'+ $(this).find('.td-summa').text()+
                    '</td>'+
                    '</tr>';
            });
            popup.find('.modal-body-list tbody').html('').append(productsHtmlModal);

            var spinnerNoInit = popup.find('.spinner1.no-init');
            i = 0;
            spinnerNoInit.each(function(){
                InitSpinner($(this),spinnerValue[i++]);
            });
            spinnerNoInit.removeClass('no-init');

            popup.find('.btn-order').click(function(){
                // добавление в базу нового города, страны, улицы и т.д (если курькером)
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
                    console.log(country.id+" "+city.id+" "+street.id+" "+building.id+" "+$('#flat-delivery').val()+" "+$('#order-comment').val());
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
            })
        }
    });

    function InitLoadCategory(catID){
        /* замена меню категорий */

        var productCategories = client.getProductCategories(catID);
        var categoriesLength = productCategories.length;
        var shopMenu = '';
        var firstMenuItem = "";

        if (productCategories[0] && productCategories[0].parentId != 0 || productCategories[0] === undefined){
            firstMenuItem = '<li>'+
                '<a href="#" class="fa fa-reply-all"></a>'+
                '<div>Назад</div>'+
                '</li>';
        }

        for(var i = 0; i < categoriesLength; i++){
            shopMenu += '<li data-parentid="'+ productCategories[i].parentId +'" data-catid="'+ productCategories[i].id +'">'+
                '<a href="#" class="fa fa-beer"></a>'+
                '<div>'+ productCategories[i].name +'</div>'+
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
                '<input type="text" class="input-mini spinner1" /> '+
                unitName+" "+productDetails.prepackRequired+
                '</td>'+
                '<td>'+
                '<i class="fa fa-shopping-cart"></i>'+
                '</td>'+
                '</tr>';

            /*'<div class="modal-body">'+
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                '<div class="product-slider">'+
                '<div class="slider flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ productsList[i].imageURL +'" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="carousel flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ productsList[i].imageURL +'" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</div>'+
                '<div class="product-descr">'+
                '<h3>'+ productsList[i].name +'</h3>'+
                '<div class="product-text">'+
                productDetails.fullDescr+
                '</div>'+
                '<div class="modal-footer">'+
                '<span>Цена: '+ productsList[i].price +'</span>';
            if(productDetails.prepackRequired){
                productsHtml += '<input type="text" class="input-mini spinner1 prepack-spinner" /> '+
                    '<span class="unit-name bottom">упаковок</span>'+
                    '<input type="text" class="input-mini spinner1" /> '+
                    '<span class="unit-name bottom">'+unitName+'</span>'+
                    '<a href="#" class="fa fa-plus"></a>'+
                    div;
            }else{
                productsHtml += '<input type="text" class="input-mini spinner1" /> '+
                    '<span class="unit-name">'+unitName+'</span>';
            }

            productsHtml += '<i class="fa fa-shopping-cart"></i>'+
                '</div>'+
                '</div>'+
                '</div>'+*/

        }
        $('.main-content .catalog table tbody').html("").append(productsHtml);

        /* подключение событий */
        InitSpinner($('.catalog table .spinner1'));
        InitProductDetailPopup($('.product-link'));
        InitAddToBasket($('.fa-shopping-cart'));
        InitClickOnCategory();

    }

    function InitClickOnCategory(){
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
    }

    function InitSpinner(selector,spinnerValue,itsBasket){
        selector.ace_spinner({value:spinnerValue,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
            .on('change', function(){
                //alert(this.value)
            });
        InitSpinnerChange(selector,itsBasket);
    }

    function InitSpinnerChange(selector,itsBasket){
        selector.on('change',function(){
            var myTable = $(this).closest('tr');
            var qnty = $(this).val();
            if ($(this).closest('.modal').length > 0){
                // значит мы в модальном окне с подробной инфой о продукте
                myTable.find('td>.ace-spinner').spinner('value',qnty);
            } else{
                myTable.find('.modal .ace-spinner').spinner('value',qnty);
            }
            var price = myTable.find('.td-price').text();
            price = parseInt(price);
            myTable.find('.td-summa').text(price*qnty+'р');
            $('.itogo-right span').text(countItogo($('.catalog-order')));
            $('.modal-itogo span').text(countItogo($('.modal-body-list')));
            if (itsBasket){
                client.setOrderLine($(this).closest('li').data('productid'),qnty,'sdf',0);
            }

        });
    }

    function countItogo(sel){
        var summa = 0;
        sel.find('.td-summa').each(function(){
            summa += parseInt($(this).text());
        });
        return summa;
    }

    function InitDeleteProduct(selector){
        selector.click(function(){
            $(this).closest('li').slideUp(function(){
                $(this).detach();
                $('.itogo-right span').text(countItogo($('.catalog-order')));
                if ($('.catalog-order li').length == 0){
                    $('.additionally-order').addClass('hide');
                    $('.empty-basket').removeClass('hide');
                }
            });
            client.removeOrderLine($(this).closest('li').data('productid'));
        });
    }

    function createOrdersProductHtml(orderDetails){
        var ordersProductsHtml = '<section class="catalog">'+
            '<table>'+
            '<thead>'+
            '<tr>'+
            '<td>Название</td>'+
            '<td>Цена</td>'+
            '<td>Количество</td>'+
            '<td></td>'+
            '</tr>'+
            '</thead>';
        var orderLines = orderDetails.odrerLines;
        var orderLinedLength = orderLines.length;

        for (var j = 0; j < orderLinedLength; j++){
            var productDetails = client.getProductDetails(orderLines[j].product.id);
            var unitName = "";
            if (orderLines[j].product.unitName){unitName = orderLines[j].product.unitName;}
            ordersProductsHtml += '<tr data-productid="'+ orderLines[j].product.id +'">'+
                '<td>'+
                '<a href="#" class="product-link">'+
                '<img src="'+ orderLines[j].product.imageURL +'" alt="картинка"/>'+
                '<span>'+
                orderLines[j].product.name+'<br>'+
                orderLines[j].product.shortDescr +
                '</span>'+
                '</a>'+
                '<div class="modal">'+
                '<div class="modal-body">'+
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                '<div class="product-slider">'+
                '<div class="slider flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ orderLines[j].product.imageURL +'" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/2.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/3.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/4.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/5.jpg" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="carousel flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ orderLines[j].product.imageURL +'" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/2.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/3.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/4.jpg" />'+
                '</li>'+
                '<li>'+
                '<img src="i/shop/5.jpg" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</div>'+
                '<div class="product-descr">'+
                '<h3>'+ orderLines[j].product.name +'</h3>'+
                '<div class="product-text">'+
                productDetails.fullDescr+
                '</div>'+
                '<div class="modal-footer">'+
                '<span>Цена: '+ orderLines[j].product.price +'</span>'+
                '<input type="text" class="input-mini spinner1" />'+
                unitName+
                '<i class="fa fa-shopping-cart"></i>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '</td>'+
                '<td class="product-price">'+ orderLines[j].product.price +'</td>'+
                '<td>'+
                '<input type="text" class="input-mini spinner1" />'+
                unitName+
                '</td>'+
                '<td>'+
                '<i class="fa fa-shopping-cart"></i>'+
                '</td>'+
                '</tr>';
        }
        ordersProductsHtml += '</table>'+
            '</section>';

        return ordersProductsHtml;
    }

    function createOrdersHtml(orders,itsMoreOrders){
        var ordersHtml = "";
        var ordersLength = orders.length;
        //alert(ordersLength);
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
                '<button class="btn btn-sm btn-primary no-border repeat-order-btn">Повторить</button>'+
                '<button class="btn btn-sm btn-primary no-border add-order-btn">Добавить в корзину</button>'+
                '<table class="orders-tbl">'+
                '<tbody>'+
                '<tr>'+
                '<td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>'+
                '<td class="td2">Заказ N '+i+'</td>'+
                '<td class="td3">'+ tempDate.getDate() +"."+tempDate.getMonth()+"."+tempDate.getFullYear()+ '</td>'+
                '<td class="td4">'+ orderStatus +'</td>'+
                '<td class="td5">'+ orderDelivery +'<br> ' +
                orderDetails.deliveryTo.city.name+", "+orderDetails.deliveryTo.street.name+" "+orderDetails.deliveryTo.building.fullNo+", кв."+
                orderDetails.deliveryTo.flatNo+
                '</td>'+
                '<td class="td6">'+ orders[i].totalCost +'</td>'+
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
        return ordersHtml;
    }

/*----*/
    var nowTime = parseInt(new Date().getTime()/1000);
    nowTime -= nowTime%86400;
    var dateArray = [];
    var day = 3600*24;
    /*dateArray[nowTime] = 1;
    dateArray[nowTime+day] = 1;
    dateArray[nowTime+2*day] = 2;
    dateArray[nowTime+3*day] = 2;
    dateArray[nowTime+4*day] = 1;
    dateArray[nowTime+6*day] = 1;
    dateArray[nowTime+9*day] = 1;
    dateArray[nowTime-9*day] = 2;
    client.setDates(dateArray); */
    var datesArray = client.getDates(nowTime-10*day,nowTime+10*day);
    for (var p in datesArray){
        console.log(p);
    }
/*------*/

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
    }

    var flagFromBasketClick = 0;

    function InitAddToBasket(selector){
        selector.click(function(){

            if (!globalUserAuth){
                $('.modal-auth').modal();
            }else{

                var currentProductSelector = $(this).closest('tr');
                var spinnerValue = currentProductSelector.find('.ace-spinner').spinner('value');
                var currentProduct = {
                    id : currentProductSelector.data('productid'),
                    imageURL : currentProductSelector.find('.product-link img').attr('src'),
                    name : currentProductSelector.find('.product-link span span').text(),
                    price : currentProductSelector.find('.product-price').text()
                };

                if ($('.additionally-order').hasClass('hide')){
                    flagFromBasketClick = 1;
                    dPicker.trigger('focus').trigger('click',[currentProduct, spinnerValue,0, 'Event']);
                }else{

                    client.setOrderLine(parseInt(currentProductSelector.data('productid')),parseInt(spinnerValue),'sdf');
                    var addedProductFlag = 0;
                    $('.catalog-order li').each(function(){
                        if ($(this).data('productid') == currentProductSelector.data('productid')){
                            addedProductFlag = 1;
                        }
                    });
                    if (addedProductFlag){
                        var currentSpinner = $('.catalog-order li[data-productid="'+ currentProductSelector.data('productid') +'"]').find('.ace-spinner');
                        var newSpinnerVal = currentSpinner.spinner('value')+spinnerValue;
                        currentSpinner.spinner('value',newSpinnerVal);
                        client.setOrderLine(currentProduct.id,newSpinnerVal,'sdf',0);
                   }else{
                        AddSingleProductToBasket(currentProduct,spinnerValue);
                    }
            }
                if ($(this).closest('.modal').length>0){
                    $(this).closest('.modal').modal('hide');
                }
            }
        });
    }

    function AddSingleProductToBasket(currentProduct,spinnerValue){
        var productDetails = client.getProductDetails(currentProduct.id);
        var unitName = "";
        if (productDetails.unitName){unitName = productDetails.unitName;}
        var productHtml = '<li data-productid="'+ currentProduct.id +'">'+
            '<a href="#" class="product-link no-init">'+
            '<span><img src="'+ currentProduct.imageURL +'" alt="картинка"/></span>'+
            '<div class="product-right-descr">'+
            currentProduct.name+
            '</div>'+
            '</a>'+
            '<div class="modal">'+
            '</div>'+
            '<table>'+
            '<thead>'+
            '<tr>'+
            '<td>Цена(шт)</td>'+
            '<td>Кол-во</td>'+
            '<td>Сумма</td>'+
            '<td></td>'+
            '</tr>'+
            '</thead>'+
            '<tr>'+
            '<td class="td-price">'+ currentProduct.price +'</td>'+
            '<td><input type="text" class="input-mini spinner1 no-init" /><span>'+ unitName +'</span></td>'+
            '<td class="td-summa">'+ currentProduct.price +'</td>'+
            '<td><a href="#" class="delete-product no-init">Удалить</a></td>'+
            '</tr>'+
            '</table>'+
            '</li>';


        /*'<div class="modal-body">'+
            '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
            '<div class="product-slider">'+
            '<div class="slider flexslider">'+
            '<ul class="slides">'+
            '<li>'+
            '<img src="'+ currentProduct.imageUrl +'" />'+
            '</li>'+
            '</ul>'+
            '</div>'+
            '<div class="carousel flexslider">'+
            '<ul class="slides">'+
            '<li>'+
            '<img src="'+ currentProduct.imageUrl +'" />'+
            '</li>'+
            '</ul>'+
            '</div>'+
            '</div>'+
            '<div class="product-descr">'+
            '<h3>'+ currentProduct.name +'</h3>'+
            '<div class="product-text">'+
            productDetails.fullDescr+
            '</div>'+
            '<div class="modal-footer">'+
            '<span>Цена: '+ currentProduct.price +'</span>'+
            '</div>'+
            '</div>'+
            '</div>';*/

        $('.catalog-order').append(productHtml);
        //currentProduct.addClass('added');

        var deleteNoInit = $('.catalog-order .delete-product.no-init');
        InitDeleteProduct(deleteNoInit);
        deleteNoInit.removeClass('no-init');

        var popupNoInit = $('.catalog-order .product-link.no-init');
        InitProductDetailPopup(popupNoInit);
        popupNoInit.removeClass('no-init');

        var spinnerNoInit = $('.catalog-order .spinner1.no-init');
        var itsBasket = true;
        InitSpinner(spinnerNoInit,spinnerValue,itsBasket);
        spinnerNoInit.removeClass('no-init');
    }

    /*var orderDate = parseInt(new Date().getTime()/1000);
    orderDate -= orderDate%86400
    var day = 3600*24;
    //var orders = client.getOrders(orderDate-day,orderDate+day);
    var orders = client.getOrders(0,orderDate+60*day);
    var ordersLength = orders.length;
    alert(ordersLength);
    /*var orderList = [];
    var counter = 0;
    for (var i = 0; i < ordersLength; i++){
        if (orders[i].date = orderDate){
            orderList[counter++] = orders[i];
        }
        console.log(i+ " "+orders[i].date+" "+orderDate+" "+orders[i].id);
    }*/

    dPicker.click(function(e,currentProduct,spinnerValue,orderData){
        if (flagFromBasketClick){
            // клик при добавленни товара в корзину
            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;
            //var nowDateItem = new Date(nowTime*1000).toLocaleString().split('.');
            var nowDateItem = new Date(nowTime*1000);
            var nowMonth = nowDateItem.getMonth();

            var datesArray = client.getDates(nowTime-30*day,nowTime+30*day);
            for (var date in datesArray){
                var tempDate = new Date(date*1000);
                //var tempDateItem = [];
                //var tempDateItem = tempDate;//.toLocaleString().split('.');
                var cleanDay = "";
                var freeDay = "";
                var specialDay = "";
                var closedDay = "";
                switch(datesArray[date]){
                    case 0:
                        cleanDay = tempDate.getDate();
                        break;
                    case 1:
                        freeDay = tempDate.getDate();
                        break ;
                    case 2:
                        specialDay = tempDate.getDate();
                        break;
                    case 3:
                        closedDay = tempDate.getDate();
                        break
                }
                //var tempDay = tempDate[0];
                var tempMonth = tempDate.getMonth();
                //var tempYear = tempDate[2].slice(0,5);

                $('.day').each(function(){
                    var day = $(this).text();
                    console.log(tempMonth+" "+nowMonth);
                    if (tempMonth == nowMonth){
                        if  (day == freeDay && !$(this).hasClass('old') && !$(this).hasClass('new')){ $(this).addClass('free-day');$(this).attr('id',date);}
                        if  (day == specialDay && !$(this).hasClass('old') && !$(this).hasClass('new')){ $(this).addClass('special-day');$(this).attr('id',date);}
                        if  (day == closedDay && !$(this).hasClass('old') && !$(this).hasClass('new')){ $(this).addClass('closed-day');$(this).attr('id',date);}
                    }
                });
            }

            $('.free-day').click(function(){
                // время должно быть не "сейчас" а то что указано в календаре
                //var nowTime = parseInt(new Date().getTime()/1000);
                var dateLabel = parseInt($(this).attr('id'));
                if (orderData && orderData.itsOrder){
                    addOrdersToBasket(orderData,$(this).attr('id'));
                }else{
                    // добавление одного продукта
                    client.createOrder(dateLabel,'asd2',0);
                    client.setOrderLine(currentProduct.id,parseInt(spinnerValue),'sdf');
                    AddSingleProductToBasket(currentProduct,spinnerValue);
                }
                if ($('.additionally-order').hasClass('hide')){
                    $('.additionally-order').removeClass('hide');
                    $('.empty-basket').addClass('hide');
                }
            });
            flagFromBasketClick = 0;
        }else{
            // просто клик - показваем даты с заказами
            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;
            var nowDateItem = new Date(nowTime*1000).toLocaleString().split('.');
            var nowMonth = nowDateItem[1];

            //var datesArray = client.getDates(nowTime-30*day,nowTime+30*day);
            var orders = client.getOrders(nowTime-30*day,nowTime+30*day);
            var ordersLength = orders.length;

            for (var i = 0; i < ordersLength; i++){
                var orderDateLabel = orders[i].date;
                var orderDate = new Date(orderDateLabel*1000).toLocaleString().split('.');
                var day = orderDate[0];
                var ordersCount = 0;
                $('.day').each(function(){
                  if ($(this).text() == day){
                      $(this).addClass('order-day').attr('id',orders[i].date);
                  }
                });
            }

            $('.order-day').click(function(){
                 var orderDate = parseInt($(this).attr('id'));
                var day = 3600*24;
                var orders = client.getOrders(orderDate,orderDate+day);
                var ordersLength = orders.length;
                var orderList = [];
                var counter = 0;
                for (var i = 0; i < ordersLength; i++){
                    if (orders[i].date = orderDate){
                        orderList[counter++] = orders[i];
                    }
                }
                $('.shop-products').hide();
                var shopOrdersList = $('.orders-list');
                initVarForMoreOrders();
                shopOrdersList.html('').append(createOrdersHtml(orderList));
                $('.shop-orders').show();
                initShowMoreOrders(orderList);
                initOrderPlusMinus(shopOrdersList);
                initOrderBtns();
            });

        }

    });

    function addSingleOrderToBasket(orderId,addType){
        var orderDetails;
        if (addType == 'replace'){
            orderDetails = client.getOrderDetails(orderId);
        }else if (addType == 'append'){
            orderDetails = client.appendOrder(orderId);
        }
        var orderLines = orderDetails.odrerLines;
        var orderLinesLength = orderLines.length;
        for(var i = 0; i < orderLinesLength; i++){
            var curProd = orderLines[i].product;
            var spinVal = orderLines[i].quantity;
            AddSingleProductToBasket(curProd,spinVal);
        }
    }

    function addOrdersToBasket(orderData,data){
        // добавление целого заказа
        var addType;
        if (orderData.itsAppend){
            addType = 'append';
        }  else{
            client.createOrder(data,'asd2',0);
            addType = 'replace';
        }
        $('.catalog-order').html('');
        addSingleOrderToBasket(orderData.orderId,addType);
    }

    function initOrderBtns(){
        $('.repeat-order-btn').click(function(){
            var orderData= {
                itsOrder: true,
                itsAppend: false,
                orderId : $(this).closest('.order-item').data('orderid')
            };
            flagFromBasketClick = 1;
            dPicker.trigger('focus').trigger('click',[0, 0, orderData, 'Event']);
            flagFromBasketClick = 0;

        });
        $('.add-order-btn').click(function(){
            var orderData= {
                itsOrder: true,
                itsAppend: true,
                orderId : $(this).closest('.order-item').data('orderid')
            };
            if ($('.additionally-order').hasClass('hide')){
                dPicker.trigger('focus').trigger('click',[0, 0, orderData, 'Event']);
            }else{
                addOrdersToBasket(orderData);
            }
        });
    }

    var offsetOrders = 10;
    var lengthOrders = 10;

    function initVarForMoreOrders(){
        offsetOrders = 10;
        lengthOrders = 10;
    }

    function initShowMoreOrders(orders){
        $('.more-orders').click(function(e){
            e.preventDefault();
            var orderList = $('.orders-list');
            orderList.find('.more-orders').remove();
            var itsMoreOrders = true;
            orderList.append(createOrdersHtml(orders,itsMoreOrders));
            initShowMoreOrders(orders);
            initOrderPlusMinus($('.orders-no-init'));
            $('.orders-no-init').removeClass('orders-no-init');
            offsetOrders += lengthOrders;
            setSidebarHeight();
        });
    }

    function setSidebarHeight(){
        var mainContent = $('.main-content');
        if (mainContent.height() > w.height()){
            $('.shop-right').css('height', mainContent.height()+45);
        }
    }

    $('.shop-trigger').click(function(e){
        e.preventDefault();

        var shopOrders = $('.shop-orders');
        var ordersList = $('.orders-list');

       if($(this).hasClass('back-to-shop')){
          shopOrders.hide();
          $('.shop-products').show();
       }else{
           $('.shop-products').hide();
           var nowTime = parseInt(new Date().getTime()/1000);
           var day = 3600*24;
           var orders = client.getOrders(0,nowTime+30*day);
           initVarForMoreOrders();
           ordersList.html('').append(createOrdersHtml(orders));
           initShowMoreOrders(orders);
           initOrderPlusMinus($('.orders-no-init'));
           $('.orders-no-init').removeClass('orders-no-init');
           initOrderBtns();
            shopOrders.show();
           setSidebarHeight();
       }
    });

});