define(
    'shop-search',
    ['jquery','jquery_ui','shop-initThrift','shop-basket','shop-orders','shop-category','shop-common','shop-spinner'],
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

            var shopArray = thriftModule.client.getShops();
            var currentShop = thriftModule.client.getShop(shopArray[0].id);
            var arr = thriftModule.client.getProductsByCategories(currentShop.id);
            var arrLength = arr.length;
            var counter = 0,
                dataSearch = [];
            for(var i = 0; i < arrLength ;i++){
                var childLength = arr[i].childs.length;
                for(var j = 0; j < childLength; j++){
                    dataSearch[counter++]={
                        label : arr[i].childs[j].name,
                        category: arr[i].name
                    }
                }
            }

            $( "#search" ).catcomplete({
                delay: 0,
                source: dataSearch,
                select: function(event,ui){
                    var basketModule = require('shop-basket');
                    var commonModule = require('shop-common');

                    if (!globalUserAuth){
                        // если пользователь не залогинен
                        basketModule.selectorForCallbacks = $(this);
                        basketModule.callbacks.add(basketModule.BasketTrigger);
                        //$('.modal-auth').modal();
                        commonModule.openModalAuth();
                    }else{

                    var productsListPart = thriftModule.client.getProducts(0,10,0);
                    var products = productsListPart.products;
                    var productsLength = products.length;
                    var packs = [];

                    for(var i = 0; i < productsLength; i++){

                        if(products[i].name == ui.item['label']){

                            products[i].qnty = products[i].minClientPack;
                            products[i].prepackLine = [];
                            var productDetails = thriftModule.client.getProductDetails(products[i].id);

                            if(productDetails.prepackRequired){
                                packs[products[i].minClientPack] = 1;
                            }

                            //var basketModule = require('shop-basket');
                            if ($('.tabs-days').length == 0){
                                // если это первый товар в корзине
                                var nextDate = basketModule.getNextDate();
                                var nextDateStr = new Date(nextDate*1000);
                                var orderId = thriftModule.client.createOrder(nextDate,'asd',0);
                                basketModule.addTabToBasketHtml(nextDateStr,orderId);
                            }

                            basketModule.AddProductToBasketCommon(products[i],packs);

                            commonModule.markAddedProduct();
                        }
                    }
                    }

                }
            });

            $('.form-group').submit(function(e){
                e.preventDefault();
                var searchWord = $('#search').val().toLowerCase();
                var productsListPart = thriftModule.client.getProducts(0,10,0);
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

                /* подключение событий */
                spinnerModule.initProductsSpinner();
                var commonModule = require('shop-common');
                commonModule.InitProductDetailPopup($('.product-link'));
                var basketModule = require('shop-basket');
                basketModule.InitAddToBasket($('.fa-shopping-cart'));
                categoryModule.InitClickOnCategory();
            });

            /* автозаполнение адреса доставки  */
            function initAutocompleteAddress(selector){
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
                    //alert(countries[i].name);
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
            }


        }catch(e){
            alert(e+ " Ошибка shop-search")
        }
        return {
            initAutocompleteAddress: initAutocompleteAddress
        }

    }
);