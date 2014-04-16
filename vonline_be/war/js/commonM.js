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

                var errorInfo = $('.error-info');
                errorInfo.hide();

                var editEmail = $('#edit-email'),
                editPhone = $('#edit-phone'),
                newName = $('#edit-name').val(),
                newSurname = $('#edit-surname').val(),
                newEmail = editEmail.val(),
                newPhone = editPhone.val(),
                userInfo = thriftModule.userClient.getUserInfo(),
                userContacts = thriftModule.userClient.getUserContacts(),
                haveError = 0;

                userInfo.firstName = newName;
                userInfo.lastName = newSurname;
                userContacts.mobilePhone = newPhone;

                if (isValidEmail(newEmail)){
                    userContacts.email = newEmail;
                }else{
                    haveError = 1;
                    editEmail.find('+.error-info').text('Некорректный email').show();
                }

                try{
                    thriftModule.userClient.updateUserContacts(userContacts);
                }catch(e){
                    haveError = 1;
                    editPhone.find('+.error-info').text('Телефон должен быть вида 79219876543, +7(821)1234567 и т.п').show();
                }

            if(!haveError){
                thriftModule.userClient.updateUserInfo(userInfo);
                $('.save-status').addClass('active');
                function hideSaveStatus(){
                    $('.save-status').removeClass('active')
                }
                setTimeout(hideSaveStatus,2000);
            }

            });

            function isValidEmail(myEmail) {

                return /^([a-z0-9_-]+\.)*[a-z0-9_-]+@[a-z0-9_-]+(\.[a-z0-9_-]+)*\.[a-z]{2,6}$/.test(myEmail);

            }
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
                        $('.confirm-alert').hide();
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
            }
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
            selector.find('.save-new-addr').click(function(e){
                e.preventDefault();
                var currentForm = $(this).closest('.form-edit');
                var flatNo = parseInt(currentForm.find('.flat-delivery').val());

                if(!flatNo){
                    currentForm.find('.error-info').text('Номер квартиры должен быть числом !').show();
                }else{
                    currentForm.find('.error-info').hide();

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
                    if(!ind){ind = 1;}
                    var newAddressesHtml ='<div class="user-address-item no-init" data-index="'+ ind +'">'+
                        '<span>'+
                        country.name + ", " + city.name + ", "
                        + street.name + " " + building.fullNo + ", кв. " + deliveryAddress.flatNo+
                        '</span>'+
                        '<a href="#" class="edit-user-addr">редактировать</a>'+
                        '</div>';
                   $('.user-addresses').prepend(newAddressesHtml);

                    thriftModule.userClient.addUserAddress(deliveryAddress);
                    var userAddresses = thriftModule.userClient.getUserAddresses();

                    var noInit = $('.user-address-item.no-init');
                    initEditAddress(noInit,userAddresses,deliveryAddress);
                    noInit.removeClass('no-init');

                }

                //thriftModule.client.setOrderDeliveryAddress(deliveryAddress);
                }
            });
        }

        function initEditAddress(selector,userAddresses,currentAddress){
            var formEditHtml = $('.form-edit-wrap').html();

            selector.find('.edit-user-addr').click(function(e){
                e.preventDefault();
                $('.form-edit-wrap').remove();
                var currAddr = currentAddress;
                var currentAddrItem = $(this).closest('.user-address-item');
                if(currentAddrItem.find('+.form-edit').length == 0){
                    currentAddrItem.after(formEditHtml);
                    var ind = currentAddrItem.data('index');
                    if(!currentAddress){currAddr = userAddresses[ind];}
                    WriteAddress(currentAddrItem.find('+.form-edit'),currAddr);//userAddresses[ind]
                    initSaveNewAddr(currentAddrItem.find('+.form-edit'),userAddresses);
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




