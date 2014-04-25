define(
    'loginModule',
    ['jquery','shop-initThrift','shop-basket','shop-common'],
    function( $,thriftModule,basketModule, commonModule ){
        function initLogin(){

    $('.login-form .btn-submit').click(function(e){
        e.preventDefault();
        login($(this));
    });
    $('.reg-form .btn-submit').click(function(e){
        e.preventDefault();
        reg($(this));
    });
    $('.remember-link').click(function(e){
        e.preventDefault();
        var setNewPassword = $('.set-new-password');
        if(setNewPassword.css('display')=='none'){
            $('label[for="password"]').html('<b>Новый пароль</b>');
            $('.login-form .btn-submit').hide();
            setNewPassword.slideDown();
            $(this).text('Вспомнил пароль !');
            $('.login-error').hide();
        }else{
            setNewPassword.slideUp(function(){
                $('.login-form .btn-submit').show();
                $('.login-error').hide();
                $('label[for="password"]').html('Пароль');
                $('.login-form').height('258px');
            });
            $(this).text('Забыли пароль ?');
        }
    });
    $('.sendConfirmCode').click(function(e){
        e.preventDefault();

        var to = $('#uname').val();
        if (!to){
            $('.login-error').text('Введите пожалуйста e-mail').removeClass('info-good').show();
        }else{
            var resourcefileName = "mailTemplates/changePasswordConfirm.html";
            try{
                thriftModule.authClient.sendConfirmCode(to,resourcefileName);
                $('.login-error').text('На ваш e-mail отправлен код').addClass('info-good').show();
            }catch(e){
                $('.login-error').text('Такой e-mail не зарегистрирован').removeClass('info-good').show();
            }
        }
        $('.login-form').height('280px');

    });
    $('.useConfirmCode').click(function(e){
        e.preventDefault();

        var email = $('#uname').val();
        var confirmCode = $('#confirmCode').val();
        var newPassword = $('#password').val();
        var loginError = $('.login-error');

        try{
            thriftModule.authClient.confirmRequest(email,confirmCode,newPassword);
            loginError.hide();
        }catch(e){
            loginError.text('Неверный код подтверждения !').removeClass('info-good').show();
            $('.login-form').height('280px');
        }
        if(loginError.css('display') == 'none'){
            login($('.login-form .btn-submit'));
        }
    });

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = thriftModule.authClient.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
                $('.login-error').hide();
                if (selector.closest('.modal-auth').length > 0){
                    //document.location.replace("/shop.jsp");
                    AuthRealTime(selector);
                }else{
                    document.location.replace("/shop.jsp");
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
                //document.location.replace("/shop.jsp");
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

    function AuthRealTime(selector){

        globalUserAuth = true;

        selector.closest('.modal-auth').modal('hide');
        // ставим shopID
        var shops = thriftModule.client.getShops();
        thriftModule.client.getShop(shops[0].id);

        commonModule.changeShortUserInfo();
        $('.user-info').after('<i class="icon-caret-down"></i>');
        /*var shortUserInfo = thriftModule.userClient.getShortUserInfo();
        var shortUserInfoHtml =  shortUserInfo.firstName +' '+ shortUserInfo.lastName;
        $('.user-info').html(shortUserInfoHtml).after('<i class="icon-caret-down"></i>');*/

        var dropdownToggle = $('.dropdown-toggle');
        dropdownToggle.removeClass('no-login');
        $('.user-short .dropdown-toggle:not(".no-login")').click(function(){
            alert('sss');
            $(this).parent().addClass('open');
        });

        // callbacks
        commonModule.initBasketInReload();
        var basketModule = require('shop-basket');
        basketModule.callbacks.fire(basketModule.selectorForCallbacks);
        basketModule.callbacks.empty();
    }
    }
        return {initLogin: initLogin}
    }
);