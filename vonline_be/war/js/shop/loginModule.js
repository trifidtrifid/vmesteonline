define(
    'loginModule.min',
    ['jquery','shop-initThrift.min','shop-basket.min','shop-common.min'],
    function( $,thriftModule,basketModule, commonModule ){
        function initLogin(){

    $('#login-box .btn-primary').click(function(e){
        e.preventDefault();
        login($(this));
    });
    $('#signup-box .btn-success').click(function(e){
        e.preventDefault();
        reg($(this));
    });

    $('.sendConfirmCode').click(function(e){
        e.preventDefault();

        var to = $('#email-forgot').val();
        if (!to){
            $('.email-forgot-error').text('Введите пожалуйста e-mail').removeClass('info-good').show();
        }else{
            var resourcefileName = "mailTemplates/changePasswordConfirm.html";
            try{
                thriftModule.authClient.sendConfirmCode(to,resourcefileName);
                $('.email-forgot-error').text('На ваш e-mail отправлен код').addClass('info-good').show();
            }catch(e){
                $('.email-forgot-error').text('Такой e-mail не зарегистрирован').removeClass('info-good').show();
            }
        }

    });

    $('.useConfirmCode').click(function(e){
        e.preventDefault();

        var email = $('#email-forgot').val();
        var confirmCode = $('#confirmCode').val();
        var newPassword = $('#password-new').val();
        var error = $('.password-new-error');

        try{
            thriftModule.authClient.confirmRequest(email,confirmCode,newPassword);
            error.hide();
        }catch(e){
            error.text('Неверный код подтверждения !').removeClass('info-good').show();
        }
        if(error.css('display') == 'none'){
            login($('.login-box .btn-primary'));
        }
    });

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = thriftModule.authClient.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
                $('.login-error').hide();
                if (selector.closest('.modal-auth').length > 0){
                    AuthRealTime(selector);
                }else{
                    //document.location.replace("./shop.jsp");
                    if($('.adminka').length){
                        document.location.replace("./adminka.jsp");
                    }else if($('.backoffice').length){
                        document.location.replace("./backoffice.jsp");
                    }

                }
            } else {
                result.val(session.error);
                result.css('color', 'black');
            }

        } catch (ouch) {
            $('.login-error').text('Вы ввели неккоректный e-mail или пароль').removeClass('info-good').show();
        }
    }

    function reg(selector) {

        thriftModule.authClient.checkEmailRegistered($("#email").val());
        var t;
        if($('#login').val()=="" || $('#email').val()=="" || $('#pass').val()==""){
            $('.email-alert').text('Вы заполнили не все поля !').css('display','block');
        }
        else if (thriftModule.authClient.checkEmailRegistered($("#email").val())) {
            $('.email-alert').text('Такой e-mail уже зарегистрирован !').css('display','block');
        }else if(!commonModule.isValidEmail($('#email').val())){
            $('.email-alert').text('Некорректный email !').css('display','block');
        }else{
            var userId = thriftModule.authClient.registerNewUser($("#login").val(), "", $("#pass").val(), $("#email").val());
            thriftModule.authClient.login($("#email").val(), $("#pass").val());
            if ( selector.closest('.modal-auth').length > 0) {
                AuthRealTime(selector);
            }else{
                document.location.replace("/shop.jsp");
            }
        }
    }

    $('#email').keypress(function(){
        var emailAlert = $('.email-alert');
       if (emailAlert.css('display') == 'block'){
           emailAlert.hide();
       }
    });

    function show_box(id) {
        $('.widget-box.visible').removeClass('visible');
        $('#'+id).addClass('visible');
    }

    $('.forgot-password-link').click(function(){
        show_box('forgot-box');
        return false;
    });
    $('.user-signup-link').click(function(){
        show_box('signup-box');
        return false;
    });
    $('.back-to-login-link').click(function(){
        show_box('login-box');
        return false;
    });

    function AuthRealTime(selector) {

        globalUserAuth = true;

        if ($('.shop.dynamic').length) {
            selector.closest('.modal-auth').modal('hide');
            // ставим shopID

            var shopId = $('.shop').attr('id');
            thriftModule.client.getShop(shopId);

            commonModule.changeShortUserInfo();
            $('.user-info').after('<i class="icon-caret-down"></i>');

            var dropdownToggle = $('.dropdown-toggle');
            dropdownToggle.removeClass('no-login');

            $('.user-short .dropdown-toggle:not(".no-login")').click(function (e) {
                $(this).parent().toggleClass('open');

                e.stopPropagation();
            });

            var userRole = thriftModule.client.getUserShopRole(shopId);
            if (userRole == 2 || userRole == 99) {
                $('.bo-link').removeClass('hidden');
            }

            // callbacks
            commonModule.initBasketInReload();
            var basketModule = require('shop-basket.min');
            basketModule.callbacks.fire(basketModule.selectorForCallbacks);
            basketModule.callbacks.empty();
        }else if(($('.shop-landing').length) ){
            document.location.replace("/");
        } else if(($('.adminka').length)){
            document.location.replace("/adminka.jsp");
        } else if(($('.backoffice.dynamic').not('.adminka').length)){
            document.location.replace("/backoffice.jsp");
        }
    }

    }
        return {initLogin: initLogin}
    }
);