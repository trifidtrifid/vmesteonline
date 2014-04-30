define(
    'commonM',
    ['jquery','shop-initThrift','shop-search','shop-common'],
    function( $,thriftModule,searchModule, commonModule ){

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
            var isBackoffise = $(this).closest('.backoffice').length;
            if(!isBackoffise) e.preventDefault();
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });
        /* --- */
        /* переключения на настройки, профиль и выход */

        function SetJSForEditPersonal(){
            //$('#date-picker-birthday').datepickerSimple({startView: 2, viewMode: 2,autoclose:true, language:'ru'});

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

                var commonModule = require('shop-common');
                if (commonModule.isValidEmail(newEmail)){
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

                commonModule.changeShortUserInfo(userInfo);

                function hideSaveStatus(){
                    $('.save-status').removeClass('active')
                }
                setTimeout(hideSaveStatus,2000);
            }

            });

            $('.main-container').css('min-height', w.height()-45);

        }

        function SetJSForProfile(){

            $('.main-container').css('min-height', $(window).height()-45);

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

            var userAddresses = thriftModule.client.getUserDeliveryAddresses().elems;
            //var userAddresses = thriftModule.userClient.getUserAddresses();

            var userAddressesLength = userAddresses.length;

            if(userAddressesLength > 0){
                var userAddressesHtml = "";
                for(var i = 0; i < userAddressesLength; i++){
                    userAddressesHtml +='<div class="user-address-item" data-index="'+ i +'">'+
                        '<span>'+
                        userAddresses[i]+
                        //userAddresses[i].country.name+", "+userAddresses[i].city.name+", "+userAddresses[i].street.name+" "+userAddresses[i].building.fullNo+", кв. "+userAddresses[i].flatNo+
                        '</span>'+
                        '<a href="#" class="edit-user-addr">редактировать</a>'+
                        '<a href="#" title="Удалить" class="remove-user-addr">&times;</a>'+
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

                    initSaveNewAddr(currentForm);
                    searchModule.initAutocompleteAddress(currentForm);
                }else{
                    if(currentForm.css('display') == 'block'){
                        currentForm.slideUp(200);
                    }else{
                        currentForm.slideDown(200);
                    }
                }


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

        function initSaveNewAddr(selector,addressForDelete){
            selector.find('.save-new-addr').click(function(e){
                e.preventDefault();
                var currentForm = $(this).closest('.form-edit');
                currentForm.prev().find('.edit-user-addr').text('редактировать');
                var flatNo = parseInt(currentForm.find('.flat-delivery').val());

                if(!flatNo){
                    currentForm.find('.error-info').text('Номер квартиры должен быть числом !').show();
                }else{
                    currentForm.find('.error-info').hide();

                currentForm.slideUp();

                var commonModule = require('shop-common');

                var street = currentForm.find('.street-delivery').val();
                var building = currentForm.find('.building-delivery').val();
                var deliveryAddress;

                if(!currentForm.prev().hasClass('add-user-address')){
                    // если сохраняем при редактировании
                    currentForm.prev().find('span').text(street + " " + building + ", кв. " + flatNo);

                    if (addressForDelete) thriftModule.client.deleteDeliveryAddress(addressForDelete.street.name+" "+addressForDelete.building.fullNo);
                    //thriftModule.client.createDeliveryAddress(deliveryAddress.street.name+" "+deliveryAddress.building.fullNo,deliveryAddress.flatNo,0,0,0);
                    deliveryAddress = thriftModule.client.createDeliveryAddress(street+" "+building,flatNo,0,0,0);

                }else{
                    //если сохраняем при добавлении
                    var ind = $('.user-address-item').length;
                    if(!ind){ind = 1;}
                    var newAddressesHtml ='<div class="user-address-item no-init" data-index="'+ ind +'">'+
                        '<span>'+
                         street + " " + building + ", кв. " + flatNo+
                        '</span>'+
                        '<a href="#" class="edit-user-addr">редактировать</a>'+
                        '<a href="#" title="Удалить" class="remove-user-addr">&times;</a>'+
                        '</div>';
                   $('.user-addresses').prepend(newAddressesHtml);

                    deliveryAddress = thriftModule.client.createDeliveryAddress(street+" "+building,flatNo,0,0,0);
                    var userAddresses = thriftModule.client.getUserDeliveryAddresses().elems;

                    var noInit = $('.user-address-item.no-init');
                    initEditAddress(noInit,userAddresses,deliveryAddress);
                    noInit.removeClass('no-init');
                }
                    //commonModule.addAddressToBase(currentForm);
                    commonModule.addAddressToBase(currentForm,deliveryAddress);

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
                    var currentForm = currentAddrItem.find('+.form-edit');

                    var ind = currentAddrItem.data('index');
                    if(!currentAddress){currAddr = thriftModule.client.getUserDeliveryAddress(userAddresses[ind]);}

                    WriteAddress(currentForm,currAddr);
                    var addressForDelete = commonModule.addAddressToBase(currentForm);
                    initSaveNewAddr(currentForm,addressForDelete);
                    searchModule.initAutocompleteAddress(currentForm);
                }

                currentAddrItem.find('+.form-edit').slideToggle(200,function(){
                    var link = $(this).prev().find('.edit-user-addr');
                    (currentAddrItem.find('+.form-edit').css('display') == 'block') ? link.text('отменить') : link.text('редактировать');
                });

            });

            selector.find('.remove-user-addr').click(function(e){
                e.preventDefault();

                $(this).closest('.user-address-item').slideUp(function(){
                    var ind = $(this).data('index');
                    var currAddr;
                    currAddr = (currentAddress) ? currentAddress : userAddresses[ind];
                    //thriftModule.userClient.deleteUserAddress(currAddr);
                    thriftModule.client.deleteDeliveryAddress(currAddr);
                });
            })
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

        $('.user-short .dropdown-toggle.no-login').click(function(){
                $(this).parent().addClass('open');
        });

        commonModule.setSidebarHeight();

        }


        return {
            init: init
        };

    });




