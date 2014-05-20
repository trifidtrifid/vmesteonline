define(
    'loginModule.min',
    ['jquery','shop-initThrift.min','shop-basket.min','shop-common.min'],
    function( $,thriftModule,basketModule, commonModule ){
        function initLogin(){
    $('.login-box .btn-primary').click(function(e){
        e.preventDefault();
        login($(this));
    });
    $('.signup-box .btn-success').click(function(e){
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
                    document.location.replace("./shop.jsp");
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

    function AuthRealTime(selector){

        globalUserAuth = true;

        selector.closest('.modal-auth').modal('hide');
        // ставим shopID
        var shops = thriftModule.client.getShops();
        thriftModule.client.getShop(shops[0].id);

        commonModule.changeShortUserInfo();
        $('.user-info').after('<i class="icon-caret-down"></i>');

        var dropdownToggle = $('.dropdown-toggle');
        dropdownToggle.removeClass('no-login');

        $('.user-short .dropdown-toggle:not(".no-login")').click(function(){
            $(this).parent().removeClass('open');
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