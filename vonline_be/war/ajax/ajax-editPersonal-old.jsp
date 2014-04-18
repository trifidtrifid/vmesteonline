
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

    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
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
    <div class="main-container" id="main-container">
        <div class="main-container-inner">
            <aside class="sidebar" id="sidebar">
                <script type="text/javascript">
                    try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
                </script>
                <ul class="nav nav-list">
                
                <c:forEach var="rubric" items="${rubrics}">
                  	<li><a href="#" data-rubricid="${rubric.id}">
                  		<span class="menu-text">${rubric.visibleName}</span>
                        <b>(3)</b>                  	
                  	</a></li> 
                </c:forEach>
            	
                </ul><!-- /.nav-list -->
            </aside>
            <div class="main-content">
                <nav class="submenu">                
                    <ul>                    
                    <c:forEach var="group" items="${groups}">
                    	<li><a class="btn btn-sm btn-info no-border" data-groupid="${group.id}" href="#">${group.visibleName}</a></li>
                    </c:forEach>
                    	<li class="btn-group">
                    	<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                            <span class="btn-group-text">Действие</span>
                            <span class="icon-caret-down icon-on-right"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-yellow">
                            <li>
                                <a href="#">Действие 1</a>
                            </li>

                            <li>
                                <a href="#">Действие 2</a>
                            </li>

                            <li>
                                <a href="#">Что-то еще</a>
                            </li>
                        </ul>
                    	</li>
                     <%-- <% for (Group g : userService.getUserGroups()) { %>                    
                		<li><a class="btn btn-sm btn-info no-border" href="#"><%= g.visibleName %></a></li>            				
            		<% } %>  --%>                        
                    </ul>
                     <div class="clear"></div>
                </nav>
                <div class="dynamic">
                   <section class="edit-personal">
                        <h3>Редактировать профиль</h3>
                        <div class="tabbable">
                            <ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab4">
                                <li class="active">
                                    <a data-toggle="tab" href="#main">Основное</a>
                                </li>

                                <li class="">
                                    <a data-toggle="tab" href="#contacts">Контакты</a>
                                </li>

                                <li class="">
                                    <a data-toggle="tab" href="#interests">Интересы</a>
                                </li>
                            </ul>

                            <div class="tab-content">
                                <div id="main" class="tab-pane active">
                                    <div>
                                        <label for="edit-name">Имя</label>
                                        <input id="edit-name" value="<c:out value="${userInfo.firstName}"/>" type="text"/>
                                    </div>
                                    <div>
                                        <label for="edit-surname">Фамилия</label>
                                        <input id="edit-surname" value="<c:out value="${userInfo.lastName}"/>" type="text"/>
                                    </div>
                                    <div>
                                        <label for="edit-biz">Должность</label>

                                        <select class="form-control" id="edit-biz">
                                            <option value="">&nbsp;</option>
                                            <option value="AL">Гончар</option>
                                            <option value="AK">Копьеносец</option>
                                        </select>
                                    </div>
                                    <div>

                                        <label for="date-picker-birthday">Дата рождения</label>
                                        <input class="form-control date-picker" id="date-picker-birthday" type="text" data-date-format="dd-mm-yyyy" data-date-viewmode="years" value="Выберите дату"/>

                                    </div>
                                </div>

                                <div id="contacts" class="tab-pane">
                                    <div>
                                        <label for="edit-email">E-mail</label>
                                        <input id="edit-email" value="<c:out value="${userContacts.email}"/>" type="email"/>
                                        <span class="error-info"></span>
                                    </div>
                                    <div>
                                        <label for="edit-phone">Телефон</label>
                                        <input id="edit-phone" value="<c:out value="${userContacts.mobilePhone}"/>" type="text"/>
                                        <span class="error-info"></span>
                                    </div>
                                </div>

                                <div id="interests" class="tab-pane">
                                    <div>
                                        <label for="edit-about">О себе</label>
                                        <textarea name="edit-about" id="edit-about" cols="30" rows="5"></textarea>
                                    </div>
                                    <div>
                                        <label for="edit-interests">Интересы</label>
                                        <textarea name="edit-interests" id="edit-interests" cols="30" rows="5"></textarea>
                                    </div>

                                </div>
                            </div>
                        </div>
                        <a class="btn btn-primary save-changes no-border" href="#">Сохранить</a>
                        <span class="save-status">Сохранено</span>
                    </section>
                </div>
            </div>
        </div>
    </div>

</div>
<div class="wysiwig-wrap">
    <div class="widget-box wysiwig-box">
        <div class="widget-header widget-header-small  header-color-blue2">

        </div>

        <div class="widget-body">
            <div class="widget-main no-padding">
                <div class="wysiwyg-editor"></div>
            </div>

            <div class="widget-toolbox padding-4 clearfix">
                <div class="btn-group pull-left">
                    <button class="btn btn-sm btn-grey btn-cancel">
                        <i class="icon-remove bigger-125"></i>
                        Отмена
                    </button>
                </div>

                <div class="btn-group pull-right">
                    <button class="btn btn-sm btn-primary">
                        <i class="icon-globe bigger-125"></i>
                        Отправить
                        <i class="icon-arrow-right icon-on-right bigger-125"></i>
                    </button>
                </div>
            </div>
        </div>
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
