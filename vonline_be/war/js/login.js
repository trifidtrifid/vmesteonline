
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
        client.sendChangePasswordCodeRequest('забыл пароль адресат','sdf%code%sdf%name%sdf');
        //client.changePasswordOfUser('qq@qq.ru','qq','qq');
    });

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = client.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
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
            result.val("smth happen");
            result.css('color', 'red');
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