$(document).ready(function(){

    var transport = new Thrift.Transport("/thrift/AuthService");
    var protocol = new Thrift.Protocol(transport);
    var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

    var URL = document.location.hash;

    if(URL) {
        var URLArray = URL.split(';');
        var email = URLArray[0].split('=')[1],
            address = URLArray[2].split('=')[1],
            code = URLArray[3].split('=')[1];

        var mapUrlTemp = URLArray[1].split('=');
        mapUrlTemp.shift();
        var mapUrl = mapUrlTemp.join('=');

        $('#email').val(email);
        $('.mapUrl').attr('src', mapUrl);
        $('.address').text(address);

        document.location.hash = "";
    }

    $('#login-box .btn-login').click(function(e){
        e.preventDefault();
        login($(this));
    });

    $('.btn-reg').click(function(){
        var gender = "";
        $('input[name="sex"]').each(function(){
            if($(this).prop("checked")){
                gender = $(this).index();
            }
        });

        try{
            authClient.registerNewUser($('#ufirstname').val(), $('#ulastname').val(), $('#password').val(), $('#email').val(), code, gender);
            document.location.replace('coming-soon.html');
        }catch(e){
           $('.error-info').text('Ошибка регистрации. Вы указали не все данные').show();
        }

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

    $('.show-remember').click(function(e){
        e.preventDefault();

       $('.login-main').addClass('hidden');
       $('.remember').removeClass('hidden');
        $('.login-error').hide();
    });

    $('.btn-back').click(function(e){
        e.preventDefault();

        $('.remember').addClass('hidden');
        $('.login-main').removeClass('hidden');
        $('.login-error').hide();

    });

    $('.btn-remember').click(function(e){
        var email = $('#email').val();

        if(email) {
            try {
                authClient.sendConfirmCode(email);
                $('.login-error').removeClass('.error-info').addClass('info-good').text('Вам отправлено письмо для восстановления пароля').show();
            }catch(e){
                $('.login-error').text('Пользователь с таким email не зарегистрирован').show();
            }

        }else{
            $('.login-error').text('Введите пожалуйста email').show();
        }

    });
});
