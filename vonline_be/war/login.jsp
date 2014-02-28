<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Login</title>
<link rel="stylesheet" href="css/style.css"/>
<script src="js/jquery-2.0.3.min.js" type="text/javascript"></script>
<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/authservice_types.js" type="text/javascript"></script>
<script src="gen-js/AuthService.js" type="text/javascript"></script>

<script type="text/javascript" charset="utf-8">
    $(document).ready(function(){
        var transport = new Thrift.Transport("/thrift/AuthService");
        var protocol = new Thrift.Protocol(transport);
        var client = new com.vmesteonline.be.AuthServiceClient(protocol);

        $('.login-form .btn-submit').click(function(e){
            e.preventDefault();
            login();
        });
        $('.reg-form .btn-submit').click(function(e){
            e.preventDefault();
            reg();
        });
        $('.remember-link').click(function(e){
            e.preventDefault();
            client.sendChangePasswordCodeRequest('забыл пароль адресат','sdf%code%sdf%name%sdf');
            //client.changePasswordOfUser('qq@qq.ru','qq','qq');
        });

        function login() {
            var result = $('#result');
            try {
                var accessGranted = client.login($("#uname").val(), $("#password").val());
                if (accessGranted) {
                    document.location.replace("/main.jsp");
                } else {
                    result.val(session.error);
                    result.css('color', 'black');
                }

            } catch (ouch) {
                result.val("smth happen");
                result.css('color', 'red');
            }

        }

        function reg() {

            var groupSelect = document.getElementById("selectGroup");
            var groupId = groupSelect.options[groupSelect.selectedIndex].value;
            if (client.checkEmailRegistered($("#email").val())) {
                $('.email-alert').css('display','block');
            }else{
                var userId = client.registerNewUser($("#login").val(), "family", $("#pass")
                        .val(), $("#email").val());
                if ( true ) {
                    document.location.replace("/main.jsp");
                }
            }
        }
    });

</script>

</head>
<body>
<div class="container">

	<!--<h2>Login page</h2>
	<form action="">
		<table class="login">
			<tr>
				<td>login</td>
				<td><input type="text" id="uname" value="testLogin" /></td>
			</tr>
			<tr>
				<td>password</td>
				<td><input type="text" id="password" value="testPassword" /></td>
			</tr>
			<tr>
				<td>result</td>
				<td><input type="text" id="result" value="" /></td>
			</tr>
			<tr>
				<td><input type="button" id="go" value="go"
					onclick="javascript:login();" /></td>
			</tr>
			<tr>
				<td><a href="/register.jsp" />register</a></td>
			</tr>

		</table>
	</form>-->
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
                    <a href="#" class="remember-link">Забыли пароль ?</a>
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
                <span class="email-alert">Такой e-mail уже зарегистрирован</span>
                <div>
                    <select id="selectGroup">
                        <%
                            List<String> codes = UserServiceImpl.getLocationCodesForRegistration();
                            for (String code : codes) {
                                out.print("<option value=\"" + code + "\">" + code + "</option>");
                            }
                        %>

                    </select>
                </div>
                <button class="btn-submit btn-sm no-border">Регистрация</button>
            </div>
        </form>
    </div>
</div>


</body>
</html>