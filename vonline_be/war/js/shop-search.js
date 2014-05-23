define(
    'shop-search.min',
    ['jquery','jquery_ui','shop-initThrift.min','shop-basket.min','shop-orders.min','shop-category.min','shop-common.min','shop-spinner.min'],
    function( $,jquery_ui,thriftModule,basketModule,ordersModule,categoryModule,commonModule,spinnerModule ){

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

            if($('.login-layout').length == 0) {
                var currentShopID =$('.main-container.shop').attr('id');

                var arr = thriftModule.client.getProductsByCategories(currentShopID);
                var arrLength = arr.length;
                var counter = 0,
                    dataSearch = [];
                for (var i = 0; i < arrLength; i++) {
                    var childLength = arr[i].childs.length;
                    for (var j = 0; j < childLength; j++) {
                        dataSearch[counter++] = {
                            label: arr[i].childs[j].name,
                            productId: arr[i].childs[j].id,
                            category: arr[i].name
                        }
                    }
                }

            $( "#search" ).catcomplete({
                delay: 0,
                source: dataSearch,
                select: function(event,ui){
                    event.preventDefault();

                    //var basketModule = require('shop-basket.min');
                    var commonModule = require('shop-common.min');

                    $('.form-search').trigger('submit');

                    $('.catalog .product').each(function(){
                       if($(this).data('productid') == ui.item['productId']){
                           $(this).find('.product-link').trigger('click');
                       }
                    });

                    /*if (!globalUserAuth){
                        // если пользователь не залогинен
                        basketModule.selectorForCallbacks = $(this);
                        basketModule.callbacks.add(basketModule.BasketTrigger);
                        commonModule.openModalAuth();
                    }else{

                    var productsListPart = thriftModule.client.getProducts(0,1000,0);
                    var products = productsListPart.products;
                    var productsLength = products.length;
                    var packs = [];

                    for(var i = 0; i < productsLength; i++){

                        if(products[i].name == ui.item['label']){

                            products[i].qnty = products[i].minClientPack;
                            products[i].prepackLine = [];

                            if(products[i].prepackRequired){
                                packs[products[i].minClientPack] = 1;
                            }

                            if ($('.tabs-days').length == 0){
                                // если это первый товар в корзине
                                var order = thriftModule.client.createOrder(0);
                                var nextDateStr = new Date(order.date*1000);
                                basketModule.addTabToBasketHtml(nextDateStr,order.id);
                            }

                            basketModule.AddProductToBasketCommon(products[i],packs);

                            commonModule.markAddedProduct();
                        }
                    }
                    }*/

                }
            });

            }

            $('.form-search').submit(function(e){
                e.preventDefault();
                $('.ui-autocomplete').hide();

                var searchWord = $('#search').val().toLowerCase();
                var productsListPart = thriftModule.client.getProducts(0,1000,0);
                var products = productsListPart.products;
                var productsLength = products.length;
                var searchedProducts = [];
                var counter = 0;
                for (var i = 0; i < productsLength; i++){
                    if(products[i].name.toLowerCase().indexOf(searchWord) != -1){
                        searchedProducts[counter++] = products[i];
                    }
                }
                $('.main-content .catalog table tbody').html("").append(categoryModule.createProductsTableHtml(searchedProducts));
                $('.main-content .catalog').addClass('searched').attr('data-searchWord',searchWord);

                /* подключение событий */
                spinnerModule.initProductsSpinner();
                var commonModule = require('shop-common.min');
                commonModule.InitProductDetailPopup($('.product-link'));
                commonModule.markAddedProduct();
                var basketModule = require('shop-basket.min');
                basketModule.InitAddToBasket($('.fa-shopping-cart'));
            });

            $('.producer-dropdown .dropdown-menu').find('li').click(function(){
                var producerId = $(this).data('producerid');
                $(this).closest('.btn-group').find('.btn').data('producerid',producerId);

                var catalog = $('.catalog');
                if(catalog.hasClass('searched')){
                    var searchWord = catalog.data('searchWord');

                    $('.form-search').trigger('submit');

                }else{
                    var urlHash = document.location.hash;
                    if(urlHash){
                        categoryModule.LoadCategoryByURLHash(urlHash);
                    }else{
                        categoryModule.InitLoadCategory(0);
                    }
                }

                if(producerId != 0){
                    filterByProducer(producerId);
                }

            });

            function filterByProducer(producerId){
                $('.catalog').find('.product').each(function(){
                    console.log($(this).find('.td-producer').data('producerid'));

                    if($(this).find('.td-producer').data('producerid') != producerId){
                        $(this).slideUp(0);
                    }
                });
            }

            /* автозаполнение адреса доставки  */
            function initAutocompleteAddress(selector){
                try{
                var addressesBase = thriftModule.userClient.getAddressCatalogue();

                var countries = addressesBase.countries;
                var countriesLength = countries.length;
                var countryTags = [],
                    countryId = [],
                    cityTags = [],
                    cityId = [],
                    streetTags = [],
                    streetId = [];

                for (var i = 0; i < countriesLength; i++){
                    countryTags[i] = countries[i].name;
                    countryId[i] = countries[i].id;
                }

                selector.find(".country-delivery").autocomplete({
                    source: countryTags
                });
                selector.find('.city-delivery').focus(function(){
                    var prevField = selector.find(".country-delivery" ).val();
                    var cities;
                    if(prevField){
                        for (var i = 0; i < countriesLength; i++){
                            if (prevField == countryTags[i]){
                                cities = thriftModule.userClient.getCities(countryId[i]);
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

                selector.find(".street-delivery").focus(function(){
                    var prevField = selector.find('.city-delivery').val();
                    var streets;
                    var citiesLength = addressesBase.cities.length;
                    if(prevField){
                        for (var i = 0; i < citiesLength; i++){
                            if (prevField == cityTags[i]){
                                streets = thriftModule.userClient.getStreets(cityId[i]);
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

                selector.find(".building-delivery").focus(function(){
                    var prevField = selector.find(".street-delivery").val();
                    var buildings;
                    var streetLength = addressesBase.streets.length;
                    if(prevField){
                        for (var i = 0; i < streetLength; i++){
                            if (prevField == streetTags[i]){
                                buildings = thriftModule.userClient.getBuildings(streetId[i]);
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
                }catch(e){

                }
            }


        }catch(e){
            alert(e+ " Ошибка shop-search")
        }
        return {
            initAutocompleteAddress: initAutocompleteAddress,
            filterByProducer : filterByProducer
        }

    }
);