
<%--<%@page import="com.vmesteonline.be.utils.SessionHelper"%>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.ShortProfile"%>
<%@ page import="com.vmesteonline.be.UserInfo"%>
<%@ page import="com.vmesteonline.be.UserContacts"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.jdo2.VoSession"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>    

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();
	try {
	 	AuthServiceImpl.checkIfAuthorised(sess.getId());
	} catch (InvalidOperation ioe) {
		response.sendRedirect("/login.jsp");
		return; 
	}

    UserServiceImpl userService = new UserServiceImpl(request.getSession());
    AuthServiceImpl authService = new AuthServiceImpl();
    boolean ifEmailConfirmed = authService.checkIfEmailConfirmed(userService.getUserContacts().email);
    pageContext.setAttribute("ifEmailConfirmed",ifEmailConfirmed);
    //out.print(ifEmailConfirmed);

    /*List<Group> Groups = userService.getUserGroups();
    List<Rubric> Rubrics = userService.getUserRubrics();
    pageContext.setAttribute("groups",Groups);
    pageContext.setAttribute("rubrics",Rubrics);
    pageContext.setAttribute("topics",Topics.topics);
    MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());
    TopicListPart Topics = messageService.getTopics(Groups.get(0).id,Rubrics.get(0).id,0,0,10);
    */

    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
    //set<PostalAddress> userAddresses = userService.getUserAddresses();
    pageContext.setAttribute("firstName",ShortUserInfo.firstName);
    pageContext.setAttribute("lastName",ShortUserInfo.lastName);

    UserInfo UserInfo = userService.getUserInfo();
    pageContext.setAttribute("userInfo",UserInfo);
    ShortProfile shortProfile = userService.getShortProfile();
    pageContext.setAttribute("shortProfile",shortProfile);
    UserContacts userContacts = userService.getUserContacts();
    pageContext.setAttribute("userContacts",userContacts);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Главная</title>
  <link rel="stylesheet" href="css/style.css"/>

<script src="js/lib/jquery-2.0.3.min.js"></script>
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
<body>
<div class="container">
    <div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try{ace.settings.check('navbar' , 'fixed')}catch(e){}
    </script>

    <div class="navbar-container" id="navbar-container">
    <div class="navbar-header pull-left">
        <a href="#" class="navbar-brand">
            <small>
                <i class="icon-leaf"></i>
                Ace Admin
            </small>
        </a><!-- /.brand -->
    </div><!-- /.navbar-header -->

    <div class="navbar-header pull-right" role="navigation">
        <ul class="nav ace-nav">
            <li class="active">
                <a class="btn btn-info no-border" href="#">
                    Сообщения
                </a>
            </li>

            <li>
                <a class="btn btn-info no-border" href="#">
                  Архив
                </a>
            </li>

            <li>
                <a class="btn btn-info no-border" href="#">
                  Избранное
                </a>
            </li>
            <li>
                <a class="btn btn-info no-border" href="/shop.jsp">
                Магазин
                </a>
            </li>

            <li class="user-short light-blue">
                <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                    <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                    <span class="user-info">
                        <small><c:out value="${firstName}"/></small>
                        <c:out value="${lastName}"/>
                    </span>
                    <i class="icon-caret-down"></i>
                </a>

                <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                    <li>
                        <a href="settings.html">
                            <i class="icon-cog"></i>
                            Настройки
                        </a>
                    </li>

                    <li>
                        <a href="profile.html">
                            <i class="icon-user"></i>
                            Профиль
                        </a>
                    </li>

                    <li class="divider"></li>

                    <li>
                        <a href="#">
                            <i class="icon-off"></i>
                            Выход
                        </a>
                    </li>
                </ul>
            </li>
        </ul><!-- /.ace-nav -->
    </div><!-- /.navbar-header -->
    </div><!-- /.container -->
    </div>

    <div class="profile">
        <section class="user-descr">
            <div class="text-area">
                <div class="user-head" >
                    <%--<c:if test="${!ifEmailConfirmed}">
                        <span class="confirm-alert">Аккаунт не подтвержден !</span>
                    </c:if>--%>
                    <h1><c:out value="${userInfo.firstName}"/> <c:out value="${userInfo.lastName}"/></h1>
                    <a class="edit-personal-link" href="#">Редактировать</a>
                </div>
                <%--<c:if test="${!ifEmailConfirconfirm">
                    <input id="confirmCode" type="text" class="form-control" value="Введите код подтверждения" onblur="if(this.value=='') this.value='Введите код подтверждения';" onfocus="if(this.value=='Введите код подтверждения') this.value='';"/>
                    <input type="submimed}">
                    <form class="account-no-t" value="Подтвердить" class="btn btn-primary btn-sm no-border useConfirmCode">
                        <button class="btn btn-primary btn-sm no-border sendConfirmCode">Получить код повторно</button>
                        <div class="confirm-info"></div>
                    </form>
                </c:if>--%>
                <div class="user-body">
                    <div class="user-body-left pull-left">
                        <label class="block clearfix logo-container">
                            <img src="../../i/avatars/clint.jpg" alt="логотип"/>
                            <input type="file" id="profile-ava">
                        </label>
                        <%--<img src="../../i/avatars/avatar1.png" alt="аватарка"/>--%>
                    </div>
                    
                    <div class="user-body-right">
                        <div><span>Адрес проживания:</span> <c:out value="${shortProfile.address}"/></div>
                        <div><span>День рождения:</span> <c:out value="${userInfo.birthday}"/></div>
                        <h3>Контактная информация</h3>
                        <div><span>Телефон:</span> <c:out value="${userContacts.mobilePhone}"/></div>
                        <div><span>Email:</span> <c:out value="${userContacts.email}"/></div>                        
                    </div>
                </div>
            </div>
        </section>
    </div>


</div>


<!-- общие библиотеки -->
<script src="js/lib/bootstrap.min.js"></script>
<script src="js/lib/ace-extra.min.js"></script>
<script src="js/lib/ace-elements.min.js"></script>

<!-- конкретные плагины -->

    <!-- библиотеки для wysiwig редактора  -->
<script src="js/lib/jquery-ui-1.10.3.custom.min.js"></script>
<script src="js/lib/markdown/markdown.min.js"></script>
<script src="js/lib/markdown/bootstrap-markdown.min.js"></script>
<script src="js/lib/jquery.hotkeys.min.js"></script>
<script src="js/lib/bootstrap-wysiwyg.min.js"></script>
<script src="js/lib/bootbox.min.js"></script>
    <!-- -->
<script src="js/lib/jquery.scrollTo.min.js"></script>

<!-- -->
<!-- файлы thrift -->
<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/bedata_types.js" type="text/javascript"></script>
<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
<script src="gen-js/MessageService.js" type="text/javascript"></script>
<script src="gen-js/userservice_types.js" type="text/javascript"></script>
<script src="gen-js/UserService.js" type="text/javascript"></script>
<script src="gen-js/authservice_types.js" type="text/javascript"></script>
<script src="gen-js/AuthService.js" type="text/javascript"></script>
<!-- -->
<!-- собственные скрипты  -->
<script src="js/common.js"></script>
<script src="js/main.js"></script>

</body>


</html>
