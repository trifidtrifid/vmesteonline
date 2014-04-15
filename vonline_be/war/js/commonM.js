define(
    'commonM',
    ['jquery','shop-initThrift','datepicker-simple'],
    function( $,thriftModule,datepicker ){

        function init(){
        /* простые обработчики событий */
        var w = $(window),
            sidebar = $('#sidebar'),
            showLeft = $('.show-left'),
            showLeftTop = (w.height()-showLeft.width())/ 2;

        showLeft.css('top',showLeftTop);
        sidebar.css('min-height', w.height());

        showLeft.click(function(){
            if (!$(this).hasClass('active')){
                $(this).animate({'margin-left':'190px'},200).addClass('active');
                $(this).parent().animate({'marginLeft':0},200);
            }else{
                $(this).parent().animate({'marginLeft':'-190px'},200);
                $(this).animate({'marginLeft':'0'},200).removeClass('active');
            }
        });

        $('.dropdown-menu li a').click(function(e){
            e.preventDefault();
            $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
        });

        $('.nav-list a,.navbar .nav a:not(".dropdown-toggle")').click(function(e){
            e.preventDefault();
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });
        /* --- */
        /* переключения на настройки, профиль и выход */

        function SetJSForEditPersonal(){
            $('#date-picker-birthday').datepickerSimple({startView: 2, viewMode: 2,autoclose:true, language:'ru'});

            $('.save-changes').click(function(e){
                e.preventDefault();
                if ($('#main').hasClass('active')){

                    var newName = $('#edit-name').val();
                    var newSurname = $('#edit-surname').val();
                    var newBiz = $('#edit-biz option:selected').text();
                    var newBirth = $('#date-picker-birthday').val();
                    var userInfo = thriftModule.userClient.getUserInfo();

                    userInfo.firstName = newName;
                    userInfo.lastName = newSurname;
                    userInfo.birthday = newBirth;

                    thriftModule.userClient.updateUserInfo(userInfo);
                    //userInfo = thriftModule.userClient.getUserInfo();

                }else if($('#contacts').hasClass('active')){

                    var newEmail = $('#edit-email').val();
                    var newPhone = $('#edit-phone').val();
                    var userContacts = thriftModule.userClient.getUserContacts();

                    userContacts.email = newEmail;
                    userContacts.mobilePhone = newPhone;

                    thriftModule.userClient.updateUserContacts(userContacts);

                }else if($('#interests').hasClass('active')){

                }

                $('.save-status').addClass('active');
                function hideSaveStatus(){
                    $('.save-status').removeClass('active')
                }
                setTimeout(hideSaveStatus,2000);


            });
        }

        function SetJSForProfile(){

            $('.edit-personal-link').click(function(e){
                e.preventDefault();
                $('.page').hide();

                $('.shop-editPersonal').load("ajax/ajax-editPersonal.jsp .dynamic",function(){
                    SetJSForEditPersonal();
                }).show();
            });

            $('.sendConfirmCode').click(function(e){
                e.preventDefault();
                var to = thriftModule.userClient.getUserContacts().email;
                var resourcefileName = "mailTemplates/changePasswordConfirm.html";
                thriftModule.authClient.sendConfirmCode(to,resourcefileName);
                $('.confirm-info').text('На ваш e-mail отправлен код').addClass('info-good').show();
            });

            $('.useConfirmCode').click(function(e){
                e.preventDefault();
                var email = thriftModule.userClient.getUserContacts().email;
                var confirmCode = $('#confirmCode').val();
                var confirmInfo = $('.confirm-info');
                try{
                    thriftModule.authClient.confirmRequest(email,confirmCode);
                    confirmInfo.text('Код принят !').addClass('info-good').show();
                    function closeConfirm(){
                        $('.account-no-confirm').slideUp();
                    }
                    setTimeout(closeConfirm,4000);
                }catch(e){
                    confirmInfo.text('Неверный код подтверждения !').removeClass('info-good').show();
                }
            });

            var userAddresses = thriftModule.userClient.getUserAddresses();
            var userAddressesLength = userAddresses.length;
            if(userAddressesLength > 0){
                var userAddressesHtml = "";
                for(var i = 0; i < userAddressesLength; i++){
                    userAddressesHtml +='<div class="user-address-item" data-index="'+ i +'">'+
                        '<span>'+
                        userAddresses[i].country.name+", "+userAddresses[i].city.name+", "+userAddresses[i].street.name+" "+userAddresses[i].building.fullNo+", кв. "+userAddresses[i].flatNo+
                        '</span>'+
                        '<a href="#" class="edit-user-addr">редактировать</a>'+
                    '</div>';
                }
                $('.user-addresses').prepend(userAddressesHtml);

                initEditAddress($('.edit-user-addr').parent(),userAddresses);

                var formEditHtml = $('.form-edit-wrap').html();

                $('.add-user-address').click(function(e){
                    e.preventDefault();

                    var currentForm = $(this).find('+.form-edit');
                    if(currentForm.length == 0){
                        $(this).after(formEditHtml);
                        WriteAddress(currentForm);
                        currentForm = $(this).find('+.form-edit');
                        currentForm.slideDown(200);
                    }else{
                        if(currentForm.css('display') == 'block'){
                            currentForm.slideUp(200);
                        }else{
                            currentForm.slideDown(200);
                        }
                    }
                    initSaveNewAddr(currentForm,userAddresses);
                });
            }
        }

        function WriteAddress(selector,address){
            if(address){
                selector.find('.country-delivery').val(address.country.name);
                selector.find('.city-delivery').val(address.city.name);
                selector.find('.street-delivery').val(address.street.name);
                selector.find('.building-delivery').val(address.building.fullNo);
                selector.find('.flat-delivery').val(address.flatNo);
             }else{
                selector.find('.country-delivery').val('');
                selector.find('.city-delivery').val('');
                selector.find('.street-delivery').val('');
                selector.find('.building-delivery').val('');
                selector.find('.flat-delivery').val('');
             }
        }

        function initSaveNewAddr(selector,userAddresses){
            selector.find('.save-new-addr').click(function(){
                var currentForm = $(this).closest('.form-edit');
                currentForm.slideUp();
                var countries = thriftModule.userClient.getCounties();
                var countriesLength = countries.length;
                var inputCountry = currentForm.find('.country-delivery').val();
                var country,countryId = 0;
                for (var i = 0; i < countriesLength; i++){
                    if (countries[i].name == inputCountry){
                        country = countries[i];
                        countryId = country.id;
                    }
                }
                if (!countryId){
                    country = thriftModule.userClient.createNewCountry(inputCountry);
                    countryId = country.id;
                }

                var cities = thriftModule.userClient.getCities(countryId);
                var citiesLength = cities.length;
                var inputCity = currentForm.find('.city-delivery').val();
                var city,cityId = 0;
                for (i = 0; i < citiesLength; i++){
                    if (cities[i].name == inputCity){
                        city = cities[i];
                        cityId = city.id;
                    }
                }
                if (!cityId){
                    city = thriftModule.userClient.createNewCity(countryId,inputCity);
                    cityId = city.id;
                }

                var streets = thriftModule.userClient.getStreets(cityId);
                var streetsLength = streets.length;
                var inputStreet = currentForm.find('.street-delivery').val();
                var street,streetId = 0;
                for (i = 0; i < streetsLength; i++){
                    if (streets[i].name == inputStreet){
                        street = streets[i];
                        streetId = street.id;
                    }
                }
                if (!streetId){
                    street = thriftModule.userClient.createNewStreet(cityId,inputStreet);
                    streetId = street.id;
                }

                var buildings = thriftModule.userClient.getBuildings(streetId);
                var buildingsLength = buildings.length;
                var inputBuilding = currentForm.find('.building-delivery').val();
                var building,buildingId = 0;
                for (i = 0; i < buildingsLength; i++){
                    if (buildings[i].fullNo == inputBuilding){
                        building = buildings[i];
                        buildingId = building.id;
                    }
                }
                if (!buildingId){
                    building = thriftModule.userClient.createNewBuilding(streetId,inputBuilding,0,0);
                    buildingId = building.id;
                }


                // передаем адресс доставки
                //console.log(country.id+" "+city.id+" "+street.id+" "+building.id+" "+$('#flat-delivery').val()+" "+$('#order-comment').val());

                var deliveryAddress = new com.vmesteonline.be.PostalAddress();
                deliveryAddress.country = country;
                deliveryAddress.city = city;
                deliveryAddress.street = street;
                deliveryAddress.building = building;
                deliveryAddress.staircase = 0;
                deliveryAddress.floor= 0;
                deliveryAddress.flatNo = parseInt(currentForm.find('.flat-delivery').val());
                deliveryAddress.comment = $('#order-comment').val();

                if(!currentForm.prev().hasClass('add-user-address')){
                currentForm.prev().find('span').text(country.name + ", " + city.name + ", "
                    + street.name + " " + building.fullNo + ", кв. " + deliveryAddress.flatNo);
                }else{
                    var ind = $('.user-address-item').length;
                    var newAddressesHtml ='<div class="user-address-item no-init" data-index="'+ ind +'">'+
                        '<span>'+
                        country.name + ", " + city.name + ", "
                        + street.name + " " + building.fullNo + ", кв. " + deliveryAddress.flatNo+
                        '</span>'+
                        '<a href="#" class="edit-user-addr">редактировать</a>'+
                        '</div>';
                   $('.user-address-item:eq('+ --ind +')').after(newAddressesHtml);

                    var noInit = $('.user-address-item.no-init');
                    initEditAddress(noInit,userAddresses);
                    noInit.removeClass('no-init');
                }

                //thriftModule.client.setOrderDeliveryAddress(deliveryAddress);
            });
        }

        function initEditAddress(selector,userAddresses){
            var formEditHtml = $('.form-edit-wrap').html();

            selector.find('.edit-user-addr').click(function(e){
                e.preventDefault();
                $('.form-edit-wrap').remove();
                var currentAddrItem = $(this).closest('.user-address-item');
                if(currentAddrItem.find('+.form-edit').length == 0){
                    currentAddrItem.after(formEditHtml);
                    var ind = currentAddrItem.data('index');
                    WriteAddress(currentAddrItem.find('+.form-edit'),userAddresses[ind]);
                    initSaveNewAddr(currentAddrItem,userAddresses);
                }
                currentAddrItem.find('+.form-edit').slideToggle(200);
            });
        }

        $('.user-menu a').click(function(e){
            e.preventDefault();
            $('.navbar .nav .active').removeClass('active');

            $(this).closest('.user-short').removeClass('open');
            e.stopPropagation();

            var ind = $(this).parent().index();
            //var dynamic = $('.dynamic');
            if (ind == 0){
                $('.page').hide();
                $('.shop-profile').load("ajax/ajax-profile.jsp .dynamic",function(){
                    SetJSForProfile();
                }).show();
            } else {
                thriftModule.authClient.logout();

                document.location.replace("login.jsp");
            }
        });
            $('.user-short .dropdown-toggle:not(".no-login")').click(function(){
                $(this).parent().addClass('open');
            });
        }

        return {
            init: init
        };

    });




