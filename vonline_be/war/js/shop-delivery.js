define(
    'shop-delivery',
    ['jquery','shop-initThrift','shop-search','shop-common'],
    function( $,thriftModule, searchModule, commonModule ){

        alert('delivery '+ searchModule+" "+commonModule );
        var autocompleteAddressFlag = 1;
        var triggerDelivery = 0;

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
                    thriftModule.client.setOrderDeliveryType(2);
                    if (autocompleteAddressFlag){
                        searchModule.initAutocompleteAddress();

                        var userAddresses = thriftModule.userServiceClient.getUserAddresses();
                        var userPhone = thriftModule.userServiceClient.getUserContacts().mobilePhone;
                        if(userPhone){
                            $('#phone-delivery').val(userPhone);
                        }
                        if(userAddresses.length > 0){
                            var homeAddress = thriftModule.userServiceClient.getUserContacts().homeAddress;
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
                    orderDetails = thriftModule.client.getOrderDetails(currentOrderId);
                    if (orderDetails.deliveryCost){
                        $('.delivery-cost').text(orderDetails.deliveryCost);
                    }
                    itogoRight.text(commonModule.countAmount($('.catalog-order')));
                    triggerDelivery = 1;
                }else{
                    thriftModule.client.setOrderDeliveryType(1);
                    $(this).closest('.delivery-right').find('.input-delivery').removeClass('active').slideUp();
                    if (triggerDelivery){itogoRight.text(commonModule.countAmount($('.catalog-order'))); triggerDelivery = 0;}
                }
            });
        }

        return {
            writeAddress: writeAddress,
            initRadioBtnClick: initRadioBtnClick
        }
    }
);