
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
                    <section class="user-descr">
                        <div class="text-area">
                            <div class="user-head" >
                                <span class="confirm-alert">Аккаунт не подтвержден !</span>
                                <h1><c:out value="${userInfo.firstName}"/> <c:out value="${userInfo.lastName}"/></h1>
                                <a class="edit-personal-link" href="#">Редактировать</a>
                            </div>
                            <c:if test="${!ifEmailConfirmed}">
                                <form class="account-no-confirm">
                                    <input id="confirmCode" type="text" class="form-control" value="Введите код подтверждения" onblur="if(this.value=='') this.value='Введите код подтверждения';" onfocus="if(this.value=='Введите код подтверждения') this.value='';"/>
                                    <input type="submit" value="Подтвердить" class="btn btn-primary btn-sm no-border useConfirmCode">
                                    <button class="btn btn-primary btn-sm no-border sendConfirmCode">Получить код повторно</button>
                                    <div class="confirm-info"></div>
                                </form>
                            </c:if>
                            <div class="user-body">
                                <div><span>Адрес проживания:</span> <c:out value="${shortProfile.address}"/></div>
                                <div><span>День рождения:</span> <c:out value="${userInfo.birthday}"/></div>
                                <h3>Контактная информация</h3>
                                <div><span>Телефон:</span> <c:out value="${userContacts.mobilePhone}"/></div>
                                <div><span>Email:</span> <c:out value="${userContacts.email}"/></div>
                                <h3>Мои адреса</h3>
                                <div class="user-addresses">
                                    <%--<div class="user-address-item">
                                        <span></span>
                                        <a href="#">редактировать</a>
                                    </div>--%>
                                    <div class="form-edit-wrap hide">
                                        <div class="form-edit">
                                            <input id="country-delivery" type="text" value="Россия" placeholder="Страна"/>
                                            <input id="city-delivery" type="text" value="Санкт-Петербург" placeholder="Город"/>
                                            <input id="street-delivery" type="text" placeholder="Улица"/>
                                            <input id="building-delivery" type="text" class="short first" placeholder="Дом"/>
                                            <input id="flat-delivery" type="text" class="short" placeholder="Квартира"/>
                                            <a class="btn btn-primary btn-sm no-border" href="#">Сохранить</a>
                                        </div>
                                    </div>
                                    <a class="btn btn-primary btn-sm no-border add-user-address" href="#">Добавить</a>
                                </div>
                            </div>
                        </div>
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
