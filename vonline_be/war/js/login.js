
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
            $('.login-form .btn-submit').hide();
            setNewPassword.slideDown();
            $('.login-form').height('290px');
        }else{
            setNewPassword.slideUp(function(){
                $('.login-form .btn-submit').show();
                $('.login-error').hide();
            });
            $('.login-form').height('258px');
        }
    });
    $('.sendConfirmCode').click(function(e){
        e.preventDefault();

        var to = $('#uname').val();
        if (!to){
            $('.login-error').text('Введите пожалуйста e-mail').show();
            $('.login-form').height('320px');
        }else{
            $('.login-error').hide();
            var resourcefileName = "mailTemplates/changePasswordConfirm.html";
            client.sendConfirmCode(to,resourcefileName);
        }
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
            $('.login-error').text('Неверный код подтверждения !').show();
            $('.login-form').height('320px');

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
        if (client.checkEmailRegistered($("#email").val())) {
            $('.email-alert').css('display','block');
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
});