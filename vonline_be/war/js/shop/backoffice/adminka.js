define(
    'adminka',
    ['jquery','shop-initThrift','bo-common'],
    function( $,thriftModule, boCommon ){

        function initAdminka(){
            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;

            var contentH = $('body').height();
            boCommon.setSidebarHeight(contentH);

            $('.adminka-shops table tr').each(function(){
                var shopId = $(this).attr('id');
                var shop = thriftModule.client.getShop(shopId);
                var userInfo = thriftModule.userClient.getUserInfoExt(shop.ownerId);

                var userContacts = getUserContacts(shop.ownerId);

                $(this).find('.owner-name span').text(userInfo.firstName+" "+userInfo.lastName); //
                $(this).find('.owner-contacts').html(userContacts);

                if(thriftModule.client.isActivated(shopId)){
                    $(this).find('.shop-activation input').attr('checked','checked');
                };

            });

            $('.shop-activation .checkbox .lbl').click(function(){
                var shopId = $(this).closest('tr').attr('id'),
                    flag = true;
                if ($(this).parent().find('input.ace').prop('checked')){
                    flag = false;
                }
                //console.log("flag "+$(this).parent().find('input.ace').prop('checked'));

                thriftModule.clientBO.activate(shopId,flag);
            });

            function getUserContacts(userId,withoutBr){

                var userContacts = thriftModule.userClient.getUserContactsExt(userId);

                var delimeter;
                withoutBr ? delimeter = "; " : delimeter = "<br>";
                var userContactsText = "Email: "+userContacts.email;
                userContacts.mobilePhone ? userContactsText += delimeter+" Телефон: "+userContacts.mobilePhone : false;
                userContacts.homeAddress ? userContactsText += delimeter+ " Адрес: "+userContacts.homeAddress.street.name+" "+
                    userContacts.homeAddress.building.fullNo+", "+ userContacts.homeAddress.flatNo : false;

                return userContactsText;
            }

            $('.update-owner-link').click(function(e){
                e.preventDefault();

                var updateHtml = "<div class='update-owner-line'>" +
                    "<input type='text' class='owner-email' placeholder='email нового владельца'>" +
                    "<a href='#' class='btn btn-sm no-border btn-primary btn-update'>Изменить</a>"+
                    "</div>";
                var td = $(this).closest('td');
                if (td.find('.update-owner-line').length == 0){
                    td.append(updateHtml);
                }
                td.find('.update-owner-line').slideToggle();

                td.find('.btn-update').one('click',function(e){
                    e.preventDefault();

                    var shopId = $(this).closest('tr').attr('id');
                    var ownerEmail = $(this).closest('td').find('.owner-email').val();

                    var newOwnerInfo = thriftModule.clientBO.setUserShopRole(shopId ,ownerEmail,3);
                    td.find('.update-owner-line').slideToggle();

                    updateOwnerHtml(td,newOwnerInfo);
                });
            });

            function updateOwnerHtml(selector,newOwnerInfo){
                selector.find('span').text(newOwnerInfo.firstName+" "+newOwnerInfo.lastName);
                selector.closest('tr').find('.owner-contacts').html(getUserContacts(newOwnerInfo.id));
            }

            $('.adminka-statistics-date .btn').click(function(e){
                e.preventDefault();

                var dateFrom, dateTo;

                dateFrom = Date.parse(datepickerFrom.val())/1000;
                dateTo = Date.parse(datepickerTo.val())/1000;

                dateFrom -= dateFrom%86400;
                dateTo -= dateTo%86400;

                reloadShopTotal(dateFrom,dateTo);

            });

            function reloadShopTotal(dateFrom,dateTo){
                var shopId, total;
                $('.adminka-statistics tr').each(function(){
                    shopId = $(this).attr('id');

                    total = thriftModule.clientBO.totalShopReturn(shopId,dateFrom,dateTo);

                    $(this).find('.shop-total').text(total);
                });
            };

            var datepickerFrom = $('#datepicker-from');
            var datepickerTo = $('#datepicker-to');

            $('.adminka .nav-list a').click(function(e){
                e.preventDefault();
                $('.back-tab').hide();
                var index = $(this).parent().index();
                switch (index){
                    case 0:
                        $('.adminka-shops').show();
                        break;
                    case 1:
                        $('.adminka-statistics').show();

                        datepickerFrom.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                            $(this).prev().focus();
                        });
                        datepickerTo.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
                            $(this).prev().focus();
                        });

                        var lastMonth = nowTime-30*day;
                        var tempDate = new Date(lastMonth*1000);

                        var lastMonthDay = tempDate.getDate();
                        lastMonthDay = (lastMonthDay < 10)? "0" + lastMonthDay: lastMonthDay;

                        var lastMonthMonth = tempDate.getMonth()+1;
                        lastMonthMonth = (lastMonthMonth < 10)? "0" + lastMonthMonth: lastMonthMonth;

                        var lastMonthYear= tempDate.getFullYear();

                        datepickerTo.datepicker('setValue', nowTime);
                        datepickerFrom.val(lastMonthMonth+"-"+lastMonthDay+"-"+lastMonthYear);
                        break;
                }
                $(this).closest('ul').find('.active').removeClass('active');
                $(this).parent().addClass('active');
            });

            $('.user-short a.no-login').click(function (e) {
                e.preventDefault();

                $(this).parent().addClass('open');
                commonModule.openModalAuth();

            });
        }

        return {
            initAdminka : initAdminka
        }

    });