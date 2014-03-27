
$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/AuthService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.AuthServiceClient(protocol);

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
        }else{
            setNewPassword.slideUp(function(){
                $('.login-form .btn-submit').show();
                $('.login-error').hide();
                $('label[for="password"]').html('Пароль');
                $('.login-form').height('258px');
            });
        }
    });
    $('.sendConfirmCode').click(function(e){
        e.preventDefault();

        var to = $('#uname').val();
        if (!to){
            $('.login-error').text('Введите пожалуйста e-mail').removeClass('login-good').show();
        }else{
            $('.login-error').text('На ваш e-mail отправлен код').addClass('login-good').show();
            var resourcefileName = "mailTemplates/changePasswordConfirm.html";
            client.sendConfirmCode(to,resourcefileName);
        }
        $('.login-form').height('280px');

    });
    $('.useConfirmCode').click(function(e){
        e.preventDefault();

        var email = $('#uname').val();
        var confirmCode = $('#confirmCode').val();
        var newPassword = $('#password').val();
        try{
            client.confirmRequest(email,confirmCode,newPassword);
            client.login($('.login-form .btn-submit'));
        }catch(e){
            $('.login-error').text('Неверный код подтверждения !').removeClass('login-good').show();
            $('.login-form').height('280px');

        }
    });

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = client.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
                $('.login-error').hide();
                if (selector.closest('.modal-auth').length > 0){
                    document.location.replace("/shop.jsp");
                }else{
                    document.location.replace("/main.jsp");
                }
            } else {
                result.val(session.error);
                result.css('color', 'black');
            }

        } catch (ouch) {
            $('.login-error').text('Вы ввели неккоректный e-mail или пароль').show();
        }
    }

    function reg(selector) {
        if($('#login').val()=="" || $('#email').val()=="" || $('#pass').val()==""){
            $('.email-alert').text('Вы заполнили не все поля !').css('display','block');
        }
        else if (client.checkEmailRegistered($("#email").val())) {
            $('.email-alert').text('Такой e-mail уже зарегистрирован !').css('display','block');
        }else{
            var userId = client.registerNewUser($("#login").val(), "", $("#pass").val(), $("#email").val());
            client.login($("#email").val(), $("#pass").val());
            if ( selector.closest('.modal-auth').length > 0) {
                document.location.replace("/shop.jsp");
            }else{
                document.location.replace("/main.jsp");
            }
        }
    }
    $('#email').keypress(function(){
        var emailAlert = $('.email-alert');
       if (emailAlert.css('display') == 'block'){
           emailAlert.hide();
       }
    });
});