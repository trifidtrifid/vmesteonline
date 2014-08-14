
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.UserContacts"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
    HttpSession sess = request.getSession();
    pageContext.setAttribute("isAuth",true);
    Boolean isAuth = true;

    try {
        AuthServiceImpl.checkIfAuthorised(sess.getId());

        UserServiceImpl userService = new UserServiceImpl(request.getSession());

        ShortUserInfo shortUserInfo = userService.getShortUserInfo();
        UserContacts userContacts = userService.getUserContacts();

        pageContext.setAttribute("shortUserInfo",shortUserInfo);
        pageContext.setAttribute("userContacts",userContacts);


    } catch (InvalidOperation ioe) {
        isAuth = false;
        pageContext.setAttribute("isAuth",false);
    }

%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Контакты</title>
    <link rel="stylesheet" href="css/lib/jquery-ui-1.10.3.full.min.css" />
    <link rel="stylesheet" href="css/style.css" />
    <link rel="stylesheet" href="css/lib/fancybox/jquery.fancybox.css"/>
    <link rel="stylesheet" href="css/lib/jquery.Jcrop.css"/>
    <link rel="stylesheet" href="js/forum/bower_components/select2/select2.css"/>

    <script src="js/lib/jquery-2.0.3.js"></script>
    <!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->

</head>
<body class="height100 height100-2">
<div class="navbar navbar-default" id="navbar">

    <div class="navbar-container" id="navbar-container">
        <div class="navbar-header pull-left">
            <a href="/" class="navbar-brand">
                <img src="i/logo.png" alt="логотип"/>
            </a>
        </div>
    </div>
</div>

<div class="container coming-soon">

    <div class="main-container" id="main-container">
        <div class="main-container-inner">
            <div class="wrap contacts">
                <br/>
                <br/>
                <br/>
            <p>Если вам есть что сказать, заполните форму и нажмите "Отправить"</p>
                <br/>

            <form action="#">

                <c:choose>
                    <c:when test="${isAuth}">

                        <div class="contacts-user clearfix">
                            <div class="avatar short pull-left" style="background-image: url(<c:out value="${shortUserInfo.avatar}"/>)"></div>
                            <div class="user-name" data-email="<c:out value="${userContacts.email}"/>"><c:out value="${shortUserInfo.firstName}"/> <c:out value="${shortUserInfo.lastName}"/></div>
                        </div>

                    </c:when>

                    <c:otherwise>

                        <input class="input name" type="text" placeholder="Имя"/>
                        <input class="input email" type="email" placeholder="E-mail"/>

                    </c:otherwise>

                </c:choose>

                <textarea class="content" onblur="if(this.value=='') this.value='Сообщение';" onfocus="if(this.value=='Сообщение') this.value='';">Сообщение</textarea>

                <button class="btn btn-sm btn-primary no-border send">Отправить</button>
                <span class="info-good hidden">Ваше сообщение отправлено</span>
            </form>
            </div>

        </div>
    </div>

    <div class="footer footer-bottom clearfix">
        <div class="pull-left">(c) Вместе Онлайн 2014</div>
        <div class="pull-right">
            <ul>
                <li><a href="about" target="_blank">О сайте</a></li>
                <li><a href="blog" target="_blank">Блог</a></li>
                <li><a href="contacts" target="_blank">Контакты</a></li>
            </ul>
        </div>
    </div>

</div>


<!-- файлы thrift -->
<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/bedata_types.js" type="text/javascript"></script>
<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
<script src="gen-js/MessageService.js" type="text/javascript"></script>
<!-- -->
<script type="text/javascript">
    $(document).ready(function(){
        var transport = new Thrift.Transport("/thrift/MessageService");
        var protocol = new Thrift.Protocol(transport);
        var messageClient = new com.vmesteonline.be.messageservice.MessageServiceClient(protocol);

        $('.send').click(function(e){
            e.preventDefault();

            var email, name,
                content = $('.content').val();
            if($('.contacts-user').length){
                email = $('.user-name').attr('data-email');
                name = $('.user-name').text();
            }else{
                email = $('.email').val();
                name = $('.name').val();
            }

            messageClient.sendInfoEmail(email,name,content);

            $('.info-good').removeClass('hidden');
            setTimeout(hideInfo,2000);
        });

        function hideInfo(){
            $('.info-good').addClass('hidden');
        }
    });
</script>


</body>


</html>