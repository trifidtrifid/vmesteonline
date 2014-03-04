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

    w.resize(function(){
        if ($(this).width() > 975){
            shopRight.css({'right':'0'});
        }else{
            shopRight.css({'right':'-250px'});
        }
        /*if ($(this).width() > 753){
            sidebar.css({'marginLeft':'0'});
        }else{
            sidebar.css({'marginLeft':'-190px'});
        }*/
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
            $(this).closest('.delivery-right').find('.input-delivery').addClass('active').slideDown();
        }else{
            $(this).closest('.delivery-right').find('.input-delivery').removeClass('active').slideUp();
        }
    });


/* функции */
    var prevParentId = [],
        parentCounter = 0;

//    setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

    var prevCatCounter = getCookie('prevCatCounter');
    if (prevCatCounter !== undefined){
        parentCounter = parseInt(prevCatCounter);
    }
    var arrayPrevCatCookie = getCookie('arrayPrevCat');
    if (arrayPrevCatCookie !== undefined){
        prevParentId = arrayPrevCatCookie.split(',');
        //alert(prevParentId);
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

            $(this).find('+.modal').modal();
            var carousel = $(this).find('+.modal').find('.carousel');
            var slider = $(this).find('+.modal').find('.slider');

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

    function InitAddToBasket(){
        $('.fa-shopping-cart').click(function(){

            if (!globalUserAuth){
                $('.modal-auth').modal();
            }else{

            if ($('.additionally-order').hasClass('hide')){
                $('.additionally-order').removeClass('hide');
                $('.empty-basket').addClass('hide');
            }

            var currentProduct = $(this).closest('tr');
            var spinnerValue = currentProduct.find('.ace-spinner').spinner('value');
                //alert(parseInt(new Date().getTime()/1000));
                var nowTime = parseInt(new Date().getTime()/1000)+86400;
                var nowTimeArray = [];
                nowTimeArray[nowTime] = com.vmesteonline.be.shop.DateType.NEXT_ORDER;
                client.setDates(nowTimeArray);
                //var t = client.getDates(0,nowTime);
                //alert(t.length);
                /*for (var p in t){
                    alert(t[p]);
                }*/
            //client.createOrder(nowTime,'asd2',0);
                /*var orders = client.getOrders(0,nowTime+1000);
                alert(orders[2].id);*/


            //var orderLine = client.setOrderLine(parseInt(currentProduct.data('productid')),parseInt(spinnerValue),'sdf');
                //alert(orderLine.product.name);
                var addedProductFlag = 0;
                $('.catalog-order li').each(function(){
                    if ($(this).data('productid') == currentProduct.data('productid')){
                        addedProductFlag = 1;
                    }
                });
            if (addedProductFlag){
                var currentSpinner = $('.catalog-order li[data-productid="'+ currentProduct.data('productid') +'"]').find('.ace-spinner');
                currentSpinner.spinner('value',currentSpinner.spinner('value')+spinnerValue);
           }else{
                var productDetails = client.getProductDetails(currentProduct.data('productid'));
                var productHtml = '<li data-productid="'+ currentProduct.data('productid') +'">'+
                '<a href="#" class="product-link no-init">'+
                '<img src="'+ currentProduct.find('.product-link img').attr('src') +'" alt="картинка"/>'+
                '<div class="product-right-descr">'+
                currentProduct.find('.product-link span').text()+
                '</div>'+
                '</a>'+
                '<div class="modal">'+
                '<div class="modal-body">'+
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                '<div class="product-slider">'+
                '<div class="slider flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ currentProduct.find('.product-link img').attr('src') +'" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="carousel flexslider">'+
                '<ul class="slides">'+
                '<li>'+
                '<img src="'+ currentProduct.find('.product-link img').attr('src') +'" />'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '</div>'+
                '<div class="product-descr">'+
                '<h3>'+ currentProduct.find('.product-link span').text() +'</h3>'+
                '<div class="product-text">'+
                productDetails.fullDescr+
                '</div>'+
                '<div class="modal-footer">'+
                '<span>Цена: '+ currentProduct.find('.product-price').text() +'</span>'+
                '</div>'+
                '</div>'+
                '</div>'+
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
                '<td class="td-price">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><input type="text" class="input-mini spinner1 no-init" /></td>'+
                '<td class="td-summa">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><a href="#" class="delete-product no-init">Удалить</a></td>'+
                '</tr>'+
                '</table>'+
                '</li>';

             $('.catalog-order').append(productHtml);
              currentProduct.addClass('added');

               var deleteNoInit = $('.catalog-order .delete-product.no-init');
               InitDeleteProduct(deleteNoInit,currentProduct);
               deleteNoInit.removeClass('no-init');

               var popupNoInit = $('.catalog-order .product-link.no-init');
               InitProductDetailPopup(popupNoInit);
               popupNoInit.removeClass('no-init');

               var spinnerNoInit = $('.catalog-order .spinner1.no-init');
               InitSpinner(spinnerNoInit,spinnerValue);
               spinnerNoInit.removeClass('no-init');

           }
            }
        });
    }

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
        /*alert("--"+catID);
        var tempCatID = catID;
        if (!tempCatID){alert('1');tempCatID = productCategories[1].id;}
        if (!tempCatID){tempCatID = productCategories[0].id;}
        alert("-- "+tempCatID);*/
        var tempCatID = productCategories[0].id  ;
        var productsList = client.getProducts(0,10,tempCatID).products;
        var productListLength = productsList.length;
        var productsHtml = '';
        var productDetails;
        for (i = 0; i < productListLength; i++){
            productDetails = client.getProductDetails(productsList[i].id);
            productsHtml += '<tr data-productid="'+ productsList[i].id +'">'+
                '<td>'+
                '<a href="#" class="product-link">'+
                '<img src="'+ productsList[i].imageURL +'" alt="картинка"/>'+
                '<span>'+ productsList[i].name +'<br>'+ productsList[i].shortDescr +'</span>'+
                '</a>'+
                '<div class="modal">'+
                '<div class="modal-body">'+
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
                '<span>Цена: '+ productsList[i].price +'</span>'+
                '<input type="text" class="input-mini spinner1" />'+
                '<i class="fa fa-shopping-cart"></i>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '</td>'+
                '<td class="product-price">'+ productsList[i].price  +'</td>'+
                '<td>'+
                '<input type="text" class="input-mini spinner1" />'+
                '</td>'+
                '<td>'+
                '<i class="fa fa-shopping-cart"></i>'+
                '</td>'+
                '</tr>';
        }
        $('.main-content .catalog table tbody').html("").append(productsHtml);

        /* подключение событий */
        InitSpinner($('.catalog table .spinner1'));
        InitProductDetailPopup($('.product-link'));
        InitAddToBasket();
        InitClickOnCategory()

    }

    function InitClickOnCategory(){
        $('.shop-menu li a').click(function(e){
            e.preventDefault();
            if ($(this).hasClass('fa-reply-all')){

                InitLoadCategory(prevParentId[parentCounter]);
                parentCounter--;
                setCookie('catid',prevParentId[parentCounter]);
                setCookie('arrayPrevCat',prevParentId);
                setCookie('prevCatCounter',parentCounter);
            }
            else {
                parentCounter++;
                prevParentId[parentCounter] = $(this).parent().data('parentid');
                InitLoadCategory($(this).parent().data('catid'));
                setCookie('catid',$(this).parent().data('catid'));
                setCookie('arrayPrevCat',prevParentId);
                setCookie('prevCatCounter',parentCounter);
            }
        });
    }

    function InitSpinner(selector,spinnerValue){
        selector.ace_spinner({value:spinnerValue,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
            .on('change', function(){
                //alert(this.value)
            });
        InitSpinnerChange(selector);
    }

    function InitSpinnerChange(selector){
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

        });
    }

    function countItogo(sel){
        var summa = 0;
        sel.find('.td-summa').each(function(){
            summa += parseInt($(this).text());
        });
        return summa;
    }

    function InitDeleteProduct(selector,currentProduct){
        selector.click(function(){
            $(this).closest('li').slideUp(function(){
                $(this).detach();
                $('.itogo-right span').text(countItogo($('.catalog-order')));
                if ($('.catalog-order li').length == 0){
                    $('.additionally-order').addClass('hide');
                    $('.empty-basket').removeClass('hide');
                }
            });
            if (currentProduct){
                if(currentProduct.hasClass('added')){currentProduct.removeClass('added');}
            }
        });
    }

/* --- */

    InitSpinner($('.spinner1'),1);
    InitAddToBasket();
    InitProductDetailPopup($('.product-link'));
    // переключение между категориями
   InitClickOnCategory();
   InitDeleteProduct($('.delete-product'));

    $('.btn-order').click(function(){

        if ($('.input-delivery').hasClass('active') && (!$('#country-delivery').val() || !$('#city-delivery').val() || !$('#street-delivery').val() || !$('#building-delivery').val() || !$('#flat-delivery').val())){
            $('.alert-delivery').show();
        }else{
        $('.alert-delivery').hide();
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
            //client.createOrder(new Date().getTime()+10000000,'asd',0);

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
            var deliveryAddress = {
                country : country,
                city : city,
                street : street,
                building : building,
                staircase : 0,
                floor: 0,
                flatNo: parseInt($('#flat-delivery').val()),
                comment: $('#order-comment').val()
            };
            client.setOrderDeliveryAddress(deliveryAddress);
            }
        })
        }
    });

    /* --- */
    var dPicker = $('.date-picker');

    dPicker.datepicker({autoclose:true,language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    dPicker.click(function(){
        $('.day').each(function(){
            var day = $(this).text();
            if  (day == '15' || day == "23"){ $(this).addClass('made')}
            if  (day == '7' || day == "13"){ $(this).addClass('soon')}
            if  (day == '26' || day == "30"){ $(this).addClass('prepare')}
        });
    });

    $('.day').click(function(){
        var madeMenu = '<div class="day-menu">' +
            '<a href="#" class="day-repeat">Повторить</a>'+
            '<a href="#" class="day-add">Добавить</a>'+
            '</div>';
        $(this).append(madeMenu);
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
});