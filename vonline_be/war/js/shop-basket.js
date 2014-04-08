define(
    'shop-basket',
    ['jquery','shop-spinner','shop-common'],
    function( $,spinnerModule,commonModule ){

        function isValidPhone(myPhone) {
            //return /^\+\d{2}\(\d{3}\)\d{3}-\d{2}-\d{2}$/.test(myPhone);
            //return /^8\-(?:\(\d{4}\)\-\d{6}|\(\d{3}\)\-\d{3}\-\d{2}\-\d{2})$/.test(myPhone);
            //return /^((((\(\d{3}\))|(\d{3}-))\d{3}-\d{4})|(\+?\d{1,3}((-| |\.)(\(\d{1,4}\)(-| |\.|^)?)?\d{1,8}){1,5}))(( )?(x|ext)\d{1,5}){0,1}$/.test(myPhone);
            return /^(\+?\d+)?\s*(\(\d+\))?[\s-]*([\d-]*)$/.test(myPhone);
        }

        $('.btn-order').click(function(){
            try{
                var inputDelivery = $('.input-delivery');
                var phoneDelivery = $('#phone-delivery');
                if(inputDelivery.hasClass('active') && !phoneDelivery.val()){
                    $('.alert-delivery-phone').text('Введите номер телефона !').show();
                }else if(!isValidPhone(phoneDelivery.val())){
                    $('.alert-delivery-phone').text('Не корректный номер телефона !').show();
                }else if (inputDelivery.hasClass('active') && (!$('#country-delivery').val() || !$('#city-delivery').val() || !$('#street-delivery').val() || !$('#building-delivery').val() || !$('#flat-delivery').val())){
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

                    if(inputDelivery.hasClass('active')){
                        popup.find('.modal-footer').before('<div class="delivery-in-modal">Стоимость доставки: <span class="delivery-cost">'+ inputDelivery.find('.delivery-cost').text() +'</span> руб</div>');
                    }else{
                        popup.find('.delivery-in-modal').hide();
                    }

                    $('.modal-itogo span').text($('.itogo-right span').text());

                    var spinnerNoInit = popup.find('.spinner1.no-init');
                    i = 0;
                    spinnerNoInit.each(function(){
                        spinnerModule.InitSpinner($(this),spinnerValue[i++],0,$(this).data('step'));
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

                        // сохранение телефона
                        var userContacts = userServiceClient.getUserContacts();
                        userContacts.mobilePhone = $('#phone-delivery').val();
                        userServiceClient.updateUserContacts(userContacts);

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

        function InitDeleteProduct(selector){
            try{
                selector.click(function(){
                    $(this).closest('li').slideUp(function(){
                        $(this).detach();
                        $('.itogo-right span').text(commonModule.countAmount($('.catalog-order')));
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

    }
);