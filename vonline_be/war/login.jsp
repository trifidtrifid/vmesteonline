<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="utf-8"/>
<title>Login</title>
<link rel="stylesheet" href="/css/style.css"/>
<script src="./js/lib/jquery-2.0.3.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        globalUserAuth = false;
    </script>
</head>
<body class="login-page">
<div class="container">

    <div class="login-forms">
        <form action="#" class="registration-form login-form">
            <h1>Вход</h1>
            <div class="reg-now">
                <br>
                <div>
                    <label for="uname">E-mail</label>
                    <input type="text" id="uname"/>
                </div>
                <div>
                    <label for="password">Пароль</label>
                    <input type="password" id="password"/>
                    <div class="set-new-password">
                        <div>
                            <label for="confirmCode">Код</label>
                            <input type="text" id="confirmCode"/>
                        </div>
                        <button class="btn btn-primary btn-sm no-border useConfirmCode">Подтвердить</button>
                        <button class="btn btn-primary btn-sm no-border sendConfirmCode">Получить код</button>
                    </div>
                    <a href="#" class="remember-link">Забыли пароль ?</a>
                    <div class="error-info login-error"></div>
                </div>
                <button id="go" class="btn-submit btn-sm no-border">Войти</button>
            </div>
        </form>
        <form action="#" class="registration-form reg-form">
            <h1>Регистрация</h1>
            <div class="reg-now">
                <br>
                <div>
                    <label for="login">Логин</label>
                    <input type="text" id="login"/>
                </div>
                <div>
                    <label for="email">E-mail</label>
                    <input type="email" required="required" id="email"/>
                </div>
                <div>
                    <label for="pass">Пароль</label>
                    <input type="password" id="pass"/>
                </div>
                <span class="email-alert"></span>
                <button class="btn-submit btn-sm no-border">Регистрация</button>
            </div>
        </form>
    </div>
</div>

<!-- файлы thrift  -->
<script src="/js/thrift.js" type="text/javascript"></script>
<script src="/gen-js/bedata_types.js" type="text/javascript"></script>

<script src="/gen-js/shop_types.js" type="text/javascript"></script>
<script src="/gen-js/ShopFEService.js" type="text/javascript"></script>
<script src="/gen-js/shop.bo_types.js" type="text/javascript"></script>
<script src="/gen-js/ShopBOService.js" type="text/javascript"></script>

<script src="/gen-js/authservice_types.js" type="text/javascript"></script>
<script src="/gen-js/AuthService.js" type="text/javascript"></script>
<script src="/gen-js/userservice_types.js" type="text/javascript"></script>
<script src="/gen-js/UserService.js" type="text/javascript"></script>
<!-- --- -->
<script type="text/javascript" data-main="/js/shop.js" src="/js/require.js"></script>

</body>
</html>