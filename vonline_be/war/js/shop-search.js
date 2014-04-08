define(
    'shop-search',
    ['jquery','shop-addProduct','initDatepicker','shop-orders','shop-category','shop-common','shop-spinner'],
    function( $,addProduct,datepickerModule,ordersModule,categoryModule,commonModule,spinnerModule ){
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

            var shopArray = client.getShops();
            var currentShop = client.getShop(shopArray[0].id);
            var arr = client.getProductsByCategories(currentShop.id);
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
                    //event.preventDefault();
                    var productsListPart = client.getProducts(0,10,0);
                    var products = productsListPart.products;
                    var productsLength = products.length;
                    var packs = [];
                    for(var i = 0; i < productsLength; i++){
                        if(products[i].name == ui.item['label']){
                            products[i].qnty = products[i].minClientPack;
                            products[i].prepackLine = [];
                            var productDetails = client.getProductDetails(products[i].id);
                            if(productDetails.prepackRequired){
                                packs[products[i].minClientPack] = 1;
                            }
                            if(currentOrderId){
                                // если в корзине уже что-то есть
                                addProduct.AddProductToBasketCommon(products[i],packs);
                            }else{
                                // если первый товар в корзине
                                addProduct.flagFromBasketClick = 1;
                                datepickerModule.dPicker.datepicker('setVarFreeDays',products[i], products[i].qnty,0,packs,addProduct.AddSingleProductToBasket,ordersModule.AddOrdersToBasket,addProduct.AddProductToBasketCommon);
                                datepickerModule.dPicker.datepicker('triggerFlagBasket').trigger('focus').trigger('click');
                            }
                        }
                    }

                    /*for (var p in ui.item){
                     alert(p+" "+ui.item[p]);
                     }*/
                }
            });

            $('.form-group').submit(function(e){
                e.preventDefault();
                var searchWord = $('#search').val().toLowerCase();
                var productsListPart = client.getProducts(0,10,0);
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
                commonModule.InitProductDetailPopup($('.product-link'));
                addProduct.InitAddToBasket($('.fa-shopping-cart'));
                categoryModule.InitClickOnCategory();
            });

            /* автозаполнение адреса доставки  */
            function initAutocompleteAddress(){
                var addressesBase = userServiceClient.getAddressCatalogue();

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

    }
);