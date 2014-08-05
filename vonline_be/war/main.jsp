
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.messageservice.TopicListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.Topic"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();
    pageContext.setAttribute("auth",true);

	try {
	 	AuthServiceImpl.checkIfAuthorised(sess.getId());
        UserServiceImpl userService = new UserServiceImpl(request.getSession());

        List<Group> Groups = userService.getUserGroups();
        List<Rubric> Rubrics = userService.getUserRubrics();
        ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
        MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());
        //MessageType mesType = MessageType.BASE;

        TopicListPart Topics = new TopicListPart( new ArrayList<Topic>(), 0);
        if( Groups.size() > 0 && Rubrics.size() > 0 )
            Topics = messageService.getTopics(Groups.get(0).id,Rubrics.get(0).id,0,0,10);

        //out.print(ShortUserInfo.firstName);

        pageContext.setAttribute("groups",Groups);
        pageContext.setAttribute("rubrics",Rubrics);
        pageContext.setAttribute("topics",Topics.topics);
        pageContext.setAttribute("firstName",ShortUserInfo.firstName);
        pageContext.setAttribute("lastName",ShortUserInfo.lastName);
        pageContext.setAttribute("userAvatar",ShortUserInfo.avatar);
	} catch (InvalidOperation ioe) {
        pageContext.setAttribute("auth",false);
		response.sendRedirect("/login.html");
		return;
	}


%>

<!DOCTYPE html>
<html ng-app="forum">
<head>
<meta charset="utf-8" />
<title>Главная</title>
    <link rel="icon" href="i/home1.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="i/home1.ico" type="image/x-icon" />

    <link rel="stylesheet" href="css/lib/jquery-ui-1.10.3.full.min.css" />
<link rel="stylesheet" href="css/style.css" />
<link rel="stylesheet" href="css/lib/fancybox/jquery.fancybox.css"/>
<link rel="stylesheet" href="css/lib/jquery.Jcrop.css"/>
<link rel="stylesheet" href="js/forum/bower_components/select2/select2.css"/>

<%--<script src="js/lib/jquery-2.1.1.min.js"></script>--%>
<script src="js/lib/jquery-2.0.3.js"></script>
    <script src="js/forum/angular/angular.js"></script>
<!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->
   <%-- <script type="text/javascript">
        globalUserAuth = false;
        <c:if test="${auth}">
        globalUserAuth = true;
        </c:if>
    </script>--%>


    <script src="js/lib/jquery.Jcrop.min.js"></script>
    <script src="js/forum/bower_components/select2/select2.min.js"></script>
    <script src="js/forum/bower_components/angular-ui-select2/src/select2.js"></script>

</head>
<body ng-controller="baseController as base" ng-cloak>
<div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try {
            ace.settings.check('navbar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="navbar-container" id="navbar-container" ng-controller="navbarController as navbar">
        <div class="navbar-header pull-left">
            <a href="#" class="navbar-brand">
                <img src="i/logo.png" alt="логотип"/>
            </a>
        </div>

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">
                <li ng-class="navbar.mapsBtnStatus"><a class="btn btn-info no-border private-messages-link"
                                                                  ui-sref="maps">Карты</a></li>

                <li ng-class="navbar.privateMessagesBtnStatus"><a class="btn btn-info no-border private-messages-link"
                                                                  ui-sref="dialogs">Личные сообщения </a></li>

                <li ng-class="navbar.nextdoorsBtnStatus"><a class="btn btn-info no-border nextdoors-link"
                                                            ui-sref="neighbours"> Соседи</a></li>

                <li class="user-short light-blue">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <div class="nav-user-photo" style="background-image: url('{{ base.user.avatar }}')"></div> <!-- -->
                        <%--<img class="nav-user-photo" ng-src="<c:out value="${userAvatar}"/>" alt="аватар" />--%>
                        <span class="user-info">
                            <small><c:out value="${firstName}" /></small>
                            <c:out value="${lastName}" />
                        </span>
                        <i class="icon-caret-down"></i>
                    </a>

                    <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li><a ui-sref="profile"> <i class="icon-user"></i>
                            Профиль
                        </a></li>

                        <li><a ui-sref="settings"> <i class="icon-cog"></i> <!--   -->
                            Настройки
                        </a></li>

                        <li class="divider"></li>

                        <li><a href="#"  ng-click="navbar.logout($event)"> <i class="icon-off"></i> Выход
                        </a></li>
                    </ul></li>
            </ul>
        </div>

    </div>
    <div class="container">

        <form method="post" action="#" class="form-group has-info form-search" ng-show="isTopSearchShow">
            <span class="block input-icon input-icon-right">
                <input id="search" type="text" class="form-control width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';"/>
                <a href="#" class="icon-search icon-on-right bigger-110"></a>
            </span>
        </form>
    </div>
</div>

	<div class="container">

		<div class="main-container" id="main-container">
			<div class="main-container-inner">
				<aside class="sidebar" id="sidebar" ng-controller="leftBarController as leftbar">
					<script type="text/javascript">
						try {
							ace.settings.check('sidebar', 'fixed')
						} catch (e) {
						}
					</script>
					<ul class="nav nav-list">
                        <li ng-class="{active:isSet(1)}"><a ui-sref="main"> <span class="menu-text">Новости</span> </a></li> <!--  ng-click="setTab($event,1)" -->
                        <li ng-class="{active:isSet(2)}"><a ui-sref="talks"> <span class="menu-text">Обсуждения</span> </a></li> <!-- ng-click="setTab($event,2)" -->
                        <li ng-class="{active:isSet(3)}"><a ui-sref="adverts"> <span class="menu-text">Услуги и объявления</span> </a></li>

					</ul>
                    <div class="footer-left">
                        (c) Вместе Онлайн 2014
                        <ul>
                            <li><a href="#">О нас</a></li>
                            <li><a href="#">Блог</a></li>
                            <li><a href="#">Контакты</a></li>
                        </ul>
                    </div>
				</aside>
                <aside class="sidebar-right" ng-controller="rightBarController as rightbar">
                    <div class="importantly-top">
                        Важно
                    </div>
                    <ul>
                        <li>
                            <div class="importantly-left"><img src="i/avatars/avatar.png" alt="картинка"/></div>
                            <div class="importantly-right">
                                <h3>Ольга Янина</h3>
                                <p>Уважаемые соседи, 16 мая будет отключена горячая вода на один день</p>
                                <a href="#">Перейти к записи</a>
                            </div>
                        </li>
                        <li>
                            <div class="importantly-left"><img src="i/avatars/avatar.png" alt="картинка"/></div>
                            <div class="importantly-right">
                                <h3>Ольга Янина</h3>
                                <p>Уважаемые соседи, 16 мая будет отключена горячая вода на один день</p>
                                <a href="#">Перейти к записи</a>
                            </div>
                        </li>
                    </ul>
                    <div class="importantly-bottom">
                        Больше важных сообщений нет
                    </div>

                </aside>
				<div class="main-content dynamic">

                    <div class="main-content-top" ng-hide="base.mainContentTopIsHide" ng-controller="mainContentTopController as mainContentTop"
                         ng-class="{'overflow-auto' : base.pageTitle.length}" ng-cloak>

                        <div class="ng-cloak">
                        <div class="page-title pull-left" ng-show="base.pageTitle.length">{{base.pageTitle}}</div>
                        </div>

                        <nav class="submenu pull-right clearfix">
                            <button class="btn btn-sm btn-info no-border pull-right ng-cloak" ng-repeat="group in groups"
                            id="{{group.id}}" ng-class="{active : group.selected}" ng-click="selectGroup(group)" ng-show="group.isShow">{{group.visibleName}}</button>

                        </nav>

                        <div class="create-topic-btn pull-right" ng-show="base.talksIsActive || base.advertsIsActive">
                            <a class="btn btn-primary btn-sm no-border clearfix" href="#" ng-click="mainContentTop.showCreateTopic($event)">
                                <span ng-show="base.talksIsActive">Создать тему</span>
                                <span ng-hide="base.talksIsActive">Создать объявление</span>
                            </a>
                        </div>
                    </div>

						<div class="forum-wrap" ng-cloak ui-view>

						</div>

				</div>
			</div>
		</div>

	</div>

	<!-- общие библиотеки -->
	<script src="js/lib/bootstrap.min.js"></script>
    <script src="js/lib/jquery-ui-1.10.3.full.min.js"></script>
    <script src="js/lib/jquery.ui.datepicker-ru.js"></script>
	<script src="js/lib/ace-extra.min.js"></script>
	<%--<script src="js/lib/ace-elements.min.js"></script>--%>
	<script src="js/ace-elements.js"></script>

    <script src="js/lib/jquery.fancybox.js"></script>

	<!-- конкретные плагины -->

	<!-- библиотеки для wysiwig редактора  -->
	<script src="js/lib/markdown/markdown.min.js"></script>
	<script src="js/lib/markdown/bootstrap-markdown.min.js"></script>
	<script src="js/lib/jquery.hotkeys.min.js"></script>
	<script src="js/lib/bootstrap-wysiwyg.min.js"></script>
	<script src="js/bootbox.min.js"></script>
	<!-- -->
	<script src="js/lib/jquery.scrollTo.min.js"></script>
    <script src="js/lib/ace.min.js"></script>

	<!-- -->
	<!-- файлы thrift -->
	<script src="js/thrift.js" type="text/javascript"></script>
	<script src="gen-js/bedata_types.js" type="text/javascript"></script>
	<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
	<script src="gen-js/MessageService.js" type="text/javascript"></script>
	<script src="gen-js/DialogService.js" type="text/javascript"></script>
	<script src="gen-js/userservice_types.js" type="text/javascript"></script>
	<script src="gen-js/UserService.js" type="text/javascript"></script>
	<script src="gen-js/authservice_types.js" type="text/javascript"></script>
	<script src="gen-js/AuthService.js" type="text/javascript"></script>
    <script src="gen-js/fileutils_types.js" type="text/javascript"></script>
    <script src="gen-js/FileService.js" type="text/javascript"></script>
	<!-- -->


	<!-- собственные скрипты  -->
	<%--<script src="js/common.js"></script>--%>
	<%--<script src="js/forum/main.js"></script>--%>
<script src="js/forum/directives.js"></script>
<script src="js/forum/controllers.js"></script>
<script src="js/forum/angular/angular-ui-router.js"></script>

	<script src="js/forum/app.js"></script>

</body>


</html>
