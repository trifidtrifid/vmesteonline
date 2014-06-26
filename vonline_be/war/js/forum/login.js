$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/AuthService");
    var protocol = new Thrift.Protocol(transport);
    var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

    $('#login-box .btn-login').click(function(e){
        e.preventDefault();
        login($(this));
    });

    function login(selector) {
        var result = $('#result');
        try {
            var accessGranted = authClient.login($("#uname").val(), $("#password").val());
            if (accessGranted) {
                $('.login-error').hide();
                if (selector.closest('.modal-auth').length > 0){
                    AuthRealTime(selector);
                }else{
                    document.location.replace("./main.jsp");
                }
            } else {
                result.val(session.error);
                result.css('color', 'black');
            }

        } catch (ouch) {
            $('.login-error').text('Вы ввели неккоректный e-mail или пароль').removeClass('info-good').show();
        }
    }
});
