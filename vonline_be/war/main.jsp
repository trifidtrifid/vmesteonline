
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();
    pageContext.setAttribute("auth",true);

	try {
	 	AuthServiceImpl.checkIfAuthorised(sess.getId());
        UserServiceImpl userService = new UserServiceImpl(request.getSession());

        MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());

        session.setAttribute("toURL","");

	} catch (InvalidOperation ioe) {
        pageContext.setAttribute("auth",false);
        session.setAttribute("toURL", request.getRequestURL());
		response.sendRedirect("/login");
		return;
	}


%>

<!DOCTYPE html>
<html ng-app="forum">
<head>
<meta charset="utf-8" />
<title>Главная</title>
    <link rel="icon" href="i/landing/vmesteonline.png" type="image/x-icon" />
    <link rel="shortcut icon" href="i/landing/vmesteonline.png" type="image/x-icon" />

    <link rel="stylesheet" href="css/lib/jquery-ui-1.10.3.full.min.css" />
<link rel="stylesheet" href="css/style.css" />
<link rel="stylesheet" href="css/lib/fancybox/jquery.fancybox.css"/>
<link rel="stylesheet" href="css/lib/jquery.Jcrop.css"/>
<link rel="stylesheet" href="js/forum/bower_components/select2/select2.css"/>

<%--<script src="js/lib/jquery-2.1.1.min.js"></script>--%>
    <script type="text/javascript" src="//api-maps.yandex.ru/2.1/?load=package.full&lang=ru_RU&coordorder=longlat"></script>


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

    <script src="js/lib/jquery.Jcrop.min.js"></script>
    <script src="js/forum/bower_components/select2/select2.min.js"></script>
    <script src="js/forum/bower_components/angular-ui-select2/src/select2.js"></script>
    <script src="js/forum/angular/ng-infinite-scroll.js"></script>

</head>
<body ng-controller="baseController as base" ng-cloak ng-class="{'height100': !base.isFooterBottom}"> <!--  -->
<div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try {
            ace.settings.check('navbar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="navbar-container ng-cloak" id="navbar-container" ng-controller="navbarController as navbar">
        <div class="navbar-header pull-left">
            <a href="/" class="navbar-brand">
                <img src="i/logo.png" alt="логотип"/>
            </a>
        </div>

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">
                <li ng-class="navbar.mapsBtnStatus"><a class="btn btn-info no-border private-messages-link"
                                                                  ui-sref="maps">Карты</a></li>

                <%--<li ng-class="navbar.privateMessagesBtnStatus">
                    <a class="btn btn-info no-border private-messages-link"
                                                                  ui-sref="dialogs">Личные сообщения</a>

                    <a ui-sref="dialog-single({dialogId: {{ base.biggestCountDialogId }} })" class="new-private-message-count" ng-if="base.newPrivateMessagesCount">{{base.newPrivateMessagesCount}}</a>
                </li>--%>

                <li ng-class="navbar.neighboursBtnStatus"><a class="btn btn-info no-border nextdoors-link"
                                                            ui-sref="neighbours">Соседи</a></li>

                <li class="user-short light-blue">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <div class="nav-user-photo" style="background-image: url('{{ base.user.avatar }}')"></div> <!-- -->
                        <span class="user-info">
                            <small>{{base.me.firstName}}</small>
                            {{ base.me.lastName }}
                        </span>
                        <i class="icon-caret-down"></i>
                    </a>

                    <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li><a ui-sref="profile({ userId : 0})"> <i class="icon-user"></i>
                            Профиль
                        </a></li>
                        <li><a ui-sref="counters"> <i class="icon-fa fa fa-tachometer"></i>
                            Счетчики
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
                        <li>
                            <a ui-sref="important"> <span class="menu-text">Важные сообщения</span> </a>
                        </li>
                        <li ng-class="{active:base.newPrivateMessagesCount}">
                            <a ui-sref="dialogs">
                                <span class="menu-text">
                                    Личные сообщения
                                    <span class="new-private-message-count bold" ng-if="base.newPrivateMessagesCount">+{{base.newPrivateMessagesCount}}</span>
                                </span>
                            </a>
                            <%--<a ui-sref="dialog-single({dialogId: {{ base.biggestCountDialogId }} })" class="new-private-message-count" ng-if="base.newPrivateMessagesCount">{{base.newPrivateMessagesCount}}</a>--%>
                        </li>
                        <li><a ui-sref="main"> <span class="menu-text">Новости</span> </a></li>
                        <li><a ui-sref="talks"> <span class="menu-text">Обсуждения</span> </a></li> <!-- ng-click="setTab($event,2)" -->
                        <li><a ui-sref="profit"> <span class="menu-text">Услуги и объявления</span> </a></li>

					</ul>
                    <div class="footer footer-left" ng-hide="base.isFooterBottom">
                        <span class="copypast">&copy;</span> ВместеОнлайн 2014
                        <!-- временная обработка ссылок пока не адаптирован блог, о нас и контакты под ангуляр -->
                        <ul>
                            <li><a href="about" onclick="document.location.replace('about');">О сайте</a></li>
                            <li><a href="blog" onclick="document.location.replace('blog');">Блог</a></li>
                            <li><a href="contacts" onclick="document.location.replace('contacts');">Контакты</a></li>
                        </ul>
                    </div>
				</aside>
                <%--<aside class="sidebar-right ng-cloak" ng-controller="rightBarController as rightbar">
                    <div class="importantly-top">
                        Важно
                    </div>
                    <div class="importantly-middle" ng-show="importantTopics.topics == null || importantTopics.topics.length == 0">{{base.emptyMessage}}</div>
                    <ul>
                        <li ng-repeat="importantTopic in importantTopics.topics" class="clearfix">
                            <div class="importantly-left">
                                <div class="avatar short" style="background-image: url({{importantTopic.userInfo.avatar}})"></div>
                            </div>
                            <div class="importantly-right">
                                <h3>{{importantTopic.userInfo.firstName}}</h3>
                                <p ng-show="importantTopic.message.content.length <= 50">{{ importantTopic.message.content }}</p>
                                <p ng-hide="importantTopic.message.content.length <= 50">{{ importantTopic.message.content.slice(0,50)+"..." }}</p>

                                <div ng-switch on="importantTopic.message.type" >

                                    <a ui-sref="wall-single({ topicId :{{ importantTopic.id }} })" ng-switch-when="5">Перейти к записи</a>
                                    <a ui-sref="talks-single({ talkId :{{ importantTopic.id }} })" ng-switch-when="1">Перейти к записи</a>

                                </div>
                            </div>
                        </li>
                    </ul>
                    <div class="importantly-bottom" ng-hide="importantTopics.topics == null || importantTopics.topics.length == 0">
                        Больше важных сообщений нет
                    </div>

                </aside>--%>

				<div class="main-content dynamic ng-cloak">

                    <div class="user-notification" ng-show="base.me.userNotification && base.me.notificationIsShow">
                        <a href="#" class="pull-right" ng-click="base.me.notificationIsShow = false">&times;</a>
                        <span>
                            {{ base.me.userNotification }}
                        </span>
                    </div>

                    <div class="main-content-top" ng-hide="base.mainContentTopIsHide" ng-controller="mainContentTopController as mainContentTop"
                         ng-class="{'top-overflow-auto' : base.pageTitle.length}" ng-cloak>

                        <div class="ng-cloak">
                        <div class="page-title pull-left" ng-show="base.pageTitle.length">{{base.pageTitle}}</div>
                        </div>

                        <nav class="submenu pull-right clearfix">
                            <button class="btn btn-sm btn-info no-border pull-right" ng-repeat="group in groups"
                            id="{{group.id}}" ng-class="{active : currentGroup.id == group.id && group.id != 0}" ng-click="selectGroup(group)" ng-show="group.isShow">{{group.visibleName}}</button> <!-- {active : group.selected} -->
                        </nav>

                        <div class="create-topic-btn pull-right ng-cloak" ng-show="base.talksIsActive || base.advertsIsActive">
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
        
        <div class="footer footer-bottom clearfix ng-cloak" ng-show="base.isFooterBottom">
            <div class="pull-left"><span class="copypast">&copy;</span> ВместеОнлайн 2014</div>
            <div class="pull-right">
                <ul>
                    <li><a href="about" onclick="document.location.replace('about');">О сайте</a></li>
                    <li><a href="blog" onclick="document.location.replace('blog');">Блог</a></li>
                    <li><a href="contacts" onclick="document.location.replace('contacts');">Контакты</a></li>
                </ul>
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
    <script src="gen-js/utilityservces_types.js" type="text/javascript"></script>
    <script src="gen-js/UtilityService.js" type="text/javascript"></script>
    <script src="gen-js/fileutils_types.js" type="text/javascript"></script>
    <script src="gen-js/FileService.js" type="text/javascript"></script>
	<!-- -->

	<!-- собственные скрипты  -->
	<%--<script src="js/common.js"></script>--%>
	<%--<script src="js/forum/main.js"></script>--%>
<script src="js/forum/directives.js"></script>
<script src="js/forum/controllers.js"></script>
<script src="js/forum/angular/angular-ui-router.js"></script>
<script src="js/forum/angular/sanitize.js"></script>
<script src="js/forum/angular/linky-custom.js"></script>

	<script src="js/forum/app.js"></script>

<script src="js/forum/angular/ya-map-2.1.min.js" type="text/javascript"></script>

<!-- Yandex.Metrika counter -->
<script type="text/javascript">
    (function (d, w, c) {
        (w[c] = w[c] || []).push(function() {
            try {
                w.yaCounter25964365 = new Ya.Metrika({id:25964365,
                    clickmap:true,
                    trackLinks:true,
                    accurateTrackBounce:true,
                    trackHash:true,
                    ut:"noindex"});
            } catch(e) { }
        });

        var n = d.getElementsByTagName("script")[0],
                s = d.createElement("script"),
                f = function () { n.parentNode.insertBefore(s, n); };
        s.type = "text/javascript";
        s.async = true;
        s.src = (d.location.protocol == "https:" ? "https:" : "http:") + "//mc.yandex.ru/metrika/watch.js";

        if (w.opera == "[object Opera]") {
            d.addEventListener("DOMContentLoaded", f, false);
        } else { f(); }
    })(document, window, "yandex_metrika_callbacks");
</script>
<noscript><div><img src="//mc.yandex.ru/watch/25964365?ut=noindex" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
<!-- /Yandex.Metrika counter -->

</body>


</html>
