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

        //document.location.hash = "";
    }

    $('#login-box .btn-login').click(function(e){
        e.preventDefault();
        login($(this));
    });

    $('.btn-reg').click(function(){
        var gender,
            email = $('#email').val(),
            firstName = $('#ufirstname').val(),
            lastName = $('#ulastname').val(),
            pass = $('#password').val();

        $('input[name="sex"]').each(function(){
            if($(this).prop("checked")){
                gender = $(this).index();
            }
        });

        if(!firstName || !lastName || !email || !pass || gender === undefined){

            $('.error-info').text('Ошибка регистрации. Вы указали не все данные').show();

        }else{

            try{
                console.log('2');
                authClient.registerNewUser(firstName, lastName, pass, email, code, gender);
                document.location.replace('coming-soon.html');
            }catch(e){
                console.log('3');
                $('.error-info').html('Такой адрес email уже зарегистрирован. <a href="#" class="reg-remember">Забыли пароль?</a>').show();

                $('.reg-remember').click(function(e){
                    e.preventDefault();

                    authClient.sendConfirmCode(email);
                    $('.login-error').removeClass('.error-info').text('На ваш email отправлен код подтверждения').show();
                });
            }
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
