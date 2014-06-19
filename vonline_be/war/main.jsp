
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.messageservice.MessageService"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.messageservice.TopicListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.MessageListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.Topic"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.messageservice.Message"%>
<%@ page import="com.vmesteonline.be.messageservice.MessageType"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.jdo2.VoSession"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="java.util.ArrayList"%>

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
%>

<!DOCTYPE html>
<html ng-app="forum">
<head>
<meta charset="utf-8" />
<title>Главная</title>
<link rel="stylesheet" href="css/style.css" />

<script src="js/lib/jquery-2.1.1.min.js"></script>
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
<body ng-controller="baseController as base">
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
                <small> <i class="icon-leaf"></i> Ace Admin </small>
            </a>
        </div>

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">
                <li ng-class="navbar.privateMessagesBtnStatus"><a class="btn btn-info no-border private-messages-link" ng-click="navbar.goToPrivateMessages($event)" href="#">Личные сообщения </a></li>

                <li ng-class="navbar.nextdoorsBtnStatus"><a class="btn btn-info no-border nextdoors-link" href="#" ng-click="navbar.goToNextdoors($event)"> Соседи</a></li>

                <li class="user-short light-blue">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                        <span class="user-info">
                            <small><c:out value="${firstName}" /></small>
                            <c:out value="${lastName}" />
                        </span>
                        <i class="icon-caret-down"></i>
                    </a>

                    <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li><a href="settings.html"> <i class="icon-cog"></i>
                            Настройки
                        </a></li>

                        <li><a href="#" ng-click="navbar.goToProfile($event)"> <i class="icon-user"></i>
                            Профиль
                        </a></li>

                        <li class="divider"></li>

                        <li><a href="#"> <i class="icon-off"></i> Выход
                        </a></li>
                    </ul></li>
            </ul>
        </div>

        <form method="post" action="#" class="form-group has-info form-search">
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
                        <li ng-class="{active:leftbar.isSet(1)}"><a href="#" ng-click="leftbar.setTab($event,1)"> <span class="menu-text">Новости</span> </a></li>
                        <li ng-class="{active:leftbar.isSet(2)}"><a href="#" ng-click="leftbar.setTab($event,2)"> <span class="menu-text">Обсуждения</span> </a></li>
                        <li ng-class="{active:leftbar.isSet(3)}"><a href="#" ng-click="leftbar.setTab($event,3)"> <span class="menu-text">Услуги и объявления</span> </a></li>
						<%--<c:forEach var="rubric" items="${rubrics}">
							<li><a href="#" data-rubricid="${rubric.id}"> <span
									class="menu-text">${rubric.visibleName}</span> <b>(3)</b>
							</a></li>
						</c:forEach>--%>
                        <%--<c:forEach var="group" items="${groups}">
                            <li><a href="#" data-rubricid="${group.id}"> <span
                                    class="menu-text">${group.visibleName}</span> <b>(3)</b>
                            </a></li>
                        </c:forEach>--%>

					</ul>
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

                    <div class="main-content-top" ng-hide="base.mainContentTopIsHide" ng-controller="mainContentTopController as mainContentTop">
                        <div class="page-title pull-left">Новости</div>

                        <nav class="submenu pull-right clearfix">
                            <button class="btn btn-sm btn-info no-border pull-right" ng-repeat="group in mainContentTop.groups"
                            id="{{group.id}}" ng-class="{active : group.selected}" ng-click="mainContentTop.selectGroup(group.id)">{{group.visibleName}}</button>

                            <%--<button class="btn btn-sm btn-info no-border pull-right">Дом</button>
                            <button class="btn btn-sm btn-info no-border pull-right">Парадная</button>
                            <button class="btn btn-sm btn-info no-border pull-right">Все</button>--%>
                        </nav>

                        <div class="create-topic-btn pull-right" ng-show="base.talksIsActive">
                            <a class="btn btn-primary btn-sm no-border clearfix" href="#" ng-click="base.showCreateTopic($event)">Создать тему</a>
                        </div>
                    </div>

						<div class="forum-wrap">
                            <section class="forum page" ng-show="base.lentaIsActive" ng-controller="LentaController as lenta">
                                <div class="message-input clearfix">
                                    <textarea name="" id="" cols="30" rows="10">Написать сообщение</textarea>
                                    <div class="message-input-bottom">
                                        <a class="btn btn-sm no-border btn-primary pull-right" href="#">Отправить</a>
                                        <div class="btn-group attach-dropdown pull-left">
                                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border" data-producerid="0">
                                                <span class="btn-group-text">Прикрепить</span>
                                                <span class="icon-caret-down icon-on-right"></span>
                                            </button>

                                            <ul class="dropdown-menu dropdown-blue">
                                                <li><a href="#">Видео</a></li>
                                                <li><a href="#">Документ</a></li>
                                                <li><a href="#">Изображение</a></li>
                                                <li><a href="#">Опрос</a></li>
                                            </ul>
                                        </div>
                                        <div class="hashtag pull-left">
                                            <span>группа</span>
                                            <div class="btn-group hashtag-dropdown">
                                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border" data-producerid="0">
                                                    <span class="btn-group-text">#Дом</span>
                                                    <span class="icon-caret-down icon-on-right"></span>
                                                </button>

                                                <ul class="dropdown-menu dropdown-blue">
                                                    <li><a href="#">#Парадная</a></li>
                                                    <li><a href="#">#Квартал</a></li>
                                                </ul>
                                            </div>
                                        </div>

                                    </div>
                                </div>

                                <div class="lenta">
                                    <div class="lenta-item">

                                        <div class="first-message clearfix">
                                            <div class="user">
                                                <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                            </div>

                                            <div class="body">
                                                <span class="label label-lg label-pink arrowed lenta-item-hashtag">Парадная</span>

                                                <div class="name">
                                                    <a href="#">Alexa</a>
                                                </div>
                                                <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>
                                                <div class="lenta-item-bottom">
                                                    <span>17.02.2014 23:01</span>
                                                    <a href="#">Ответить</a>
                                                </div>

                                            </div>
                                        </div>

                                        <div class="dialogs">
                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Alexa</a>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="John's Avatar" src="i/avatars/avatar.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">John</a>
                                                    </div>
                                                    <div class="text">Raw denim you probably haven't heard of them jean shorts Austin.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Bob's Avatar" src="i/avatars/user.jpg">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Bob</a>
                                                        <span class="label label-info arrowed arrowed-in-right">admin</span>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Jim's Avatar" src="i/avatars/avatar4.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Jim</a>
                                                    </div>
                                                    <div class="text">Raw denim you probably haven't heard of them jean shorts Austin.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Alexa</a>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="input-group">
                                            <%--<input placeholder="Введите сообщение..." type="text" class="form-control" name="message">--%>
                                            <textarea name="message" class="message-textarea"></textarea>
                                                        <span class="input-group-btn">
                                                            <button class="btn btn-sm btn-info no-radius no-border" type="button">
                                                                <i class="icon-share-alt"></i>
                                                                Отправить
                                                            </button>
                                                        </span>
                                        </div>

                                    </div>

                                    <div class="lenta-item">

                                        <div class="first-message clearfix">
                                            <div class="user">
                                                <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                            </div>

                                            <div class="body">
                                                <span class="label label-lg label-yellow arrowed lenta-item-hashtag">Район</span>

                                                <div class="name">
                                                    <a href="#">Alexa</a>
                                                </div>
                                                <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>

                                                <div class="lenta-item-bottom">
                                                    <span>17.02.2014 23:01</span>
                                                    <a href="#">Ответить</a>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="dialogs">
                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Alexa</a>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="John's Avatar" src="i/avatars/avatar.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">John</a>
                                                    </div>
                                                    <div class="text">Raw denim you probably haven't heard of them jean shorts Austin.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Bob's Avatar" src="i/avatars/user.jpg">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Bob</a>
                                                        <span class="label label-info arrowed arrowed-in-right">admin</span>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque commodo massa sed ipsum porttitor facilisis.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Jim's Avatar" src="i/avatars/avatar4.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Jim</a>
                                                    </div>
                                                    <div class="text">Raw denim you probably haven't heard of them jean shorts Austin.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="itemdiv dialogdiv">
                                                <div class="user">
                                                    <img alt="Alexa's Avatar" src="i/avatars/avatar1.png">
                                                </div>

                                                <div class="body">

                                                    <div class="name">
                                                        <a href="#">Alexa</a>
                                                    </div>
                                                    <div class="text">Lorem ipsum dolor sit amet, consectetur adipiscing elit.</div>

                                                    <div class="lenta-item-bottom">
                                                        <span>17.02.2014 23:01</span>
                                                        <a href="#">Ответить</a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="input-group">
                                            <textarea name="message" class="message-textarea"></textarea>
                                                        <span class="input-group-btn">
                                                            <button class="btn btn-sm btn-info no-radius no-border" type="button">
                                                                <i class="icon-share-alt"></i>
                                                                Отправить
                                                            </button>
                                                        </span>
                                        </div>

                                    </div>

                                    <div class="talks-title clearfix">
                                        <div class="talks-title-left load-talk">
                                            <div><a href="#">Проблемы в намшем доме !</a></div>
                                            <div>74 сообщения</div>
                                        </div>
                                        <div class="talks-title-right">
                                            <div>Последнее обновление:</div>
                                            <div>18 июля 2014 23:01</div>
                                        </div>
                                    </div>
                                </div>

                            </section>

                            <section class="talks page" ng-show="base.talksIsActive" ng-controller="TalksController as talks">

                                <section class="create-topic" ng-hide="base.createTopicIsHide">
                                    <div class="has-info form-group">
                                        <input type="text" class="width-100 head" value="Заголовок"
                                               onblur="if(this.value=='') this.value='Заголовок';"
                                               onfocus="if(this.value=='Заголовок') this.value='';" ng-model="base.subject" />
                                    </div>
                                    <div class="topic-body">
                                        <textarea ng-model="base.content"></textarea>
                                        <div class="btn-group">
                                            <button data-toggle="dropdown"
                                                    class="btn btn-info btn-sm dropdown-toggle no-border">
                                                <span class="btn-group-text">Прикрепить</span> <span class="icon-caret-down icon-on-right"></span>
                                            </button>

                                            <ul class="dropdown-menu dropdown-yellow">
                                                <li><a href="#">Видео</a></li>

                                                <li><a href="#">Фотографию</a></li>

                                                <li><a href="#">Документ</a></li>

                                                <li><a href="#">Опрос</a></li>
                                            </ul>
                                        </div>

                                        <div class="btn-group pull-right">
                                            <button class="btn btn-sm btn-primary" ng-click="base.addSingleTalk()">
                                                Создать
                                            </button>
                                        </div>
                                    </div>
<%--                                    <div class="widget-box wysiwig-box">
                                        <div class="widget-header widget-header-small  header-color-blue2">

                                        </div>

                                        <div class="widget-body">
                                            <div class="widget-main no-padding">
                                                <div class="wysiwyg-editor"></div>
                                            </div>

                                            <div class="widget-toolbox padding-4 clearfix">
                                                <div class="btn-group">
                                                    <button data-toggle="dropdown"
                                                            class="btn btn-info btn-sm dropdown-toggle no-border">
                                                        <span class="btn-group-text">Прикрепить</span> <span class="icon-caret-down icon-on-right"></span>
                                                    </button>

                                                    <ul class="dropdown-menu dropdown-yellow">
                                                        <li><a href="#">Видео</a></li>

                                                        <li><a href="#">Фотографию</a></li>

                                                        <li><a href="#">Документ</a></li>

                                                        <li><a href="#">Опрос</a></li>
                                                    </ul>
                                                </div>

                                                <div class="btn-group pull-right">
                                                    <button class="btn btn-sm btn-primary">
                                                        <i class="icon-globe bigger-125"></i> Создать <i
                                                            class="icon-arrow-right icon-on-right bigger-125"></i>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>--%>
                                </section>

                                <section class="talks-title-block" ng-show="talks.isTitles">

                                    <div class="talks-title" ng-repeat="talk in talks.topics" id="{{talk.id}}">
                                        <div class="talks-title-left load-talk">
                                            <div><a href="#" ng-click="talks.showFullTalk($event)">{{talk.subject}}</a></div>
                                            <div>{{talk.messageNum}} сообщений</div>
                                        </div>
                                        <div class="talks-title-right">
                                            <div>Последнее обновление:</div>
                                            <div>{{talk.lastUpdate}}</div>
                                        </div>
                                    </div>

                                    <%--<div class="talks-title">
                                        <div class="talks-title-left">
                                            <div><a href="#" ng-click="talks.showFullTalk($event)">Проблемы в намшем доме !</a></div>
                                            <div>74 сообщения</div>
                                        </div>
                                        <div class="talks-title-right">
                                            <div>Последнее обновление:</div>
                                            <div>18 июля 2014 23:01</div>
                                        </div>
                                    </div>--%>
                                </section>

                                <section class="talks-block" ng-hide="talks.isTitles"> </section>

                            </section>

                            <section class="services page" ng-show="base.servicesIsActive" ng-controller="ServicesController as services"></section>

                            <section class="private-messages page" ng-show="base.privateMessagesIsActive" ng-controller="privateMessagesController as privateMessages"></section>

                            <section class="nextdoors page" ng-class="base.nextdoorsLoadStatus" ng-show="base.nextdoorsIsActive" ng-controller="nextdoorsController as nextdoors"></section>

                            <section class="profile page" ng-show="base.profileIsActive" ng-controller="ProfileController as profile"></section>

							<%--<section class="forum">
								<section class="options">
									<div class="btn-group">
										<button data-toggle="dropdown"
											class="btn btn-info btn-sm dropdown-toggle no-border">
											<span class="btn-group-text">Сортировка</span> <span
												class="icon-caret-down icon-on-right"></span>
										</button>
										<ul class="dropdown-menu dropdown-info pull-right">
											<li><a href="#">Сортировка 1</a></li>

											<li><a href="#">Сортировка2</a></li>

											<li><a href="#">Что-то еще</a></li>
										</ul>
									</div>
									<!-- /btn-group -->

									<a class="btn btn-primary btn-sm no-border create-topic-show"
										href="#">Создать тему</a>

									<form method="post" action="#" class="form-group has-info">
										<span class="block input-icon input-icon-right"> <input
											type="text" id="inputInfo" class="width-100" value="Поиск"
											onblur="if(this.value=='') this.value='Поиск';"
											onfocus="if(this.value=='Поиск') this.value='';" /> <a
											href="#" class="icon-search icon-on-right bigger-110"></a>
										</span>
									</form>
									<div class="clear"></div>
								</section>

								<div class="dd dd-draghandle">
									<ol class="dd-list">
										<c:forEach var="topic" items="${topics}">
											<li class="dd-item dd2-item topic-item"
												data-topicid="${topic.id}">
												<div class="dd2-content widget-box topic-descr">
													<header class="widget-header header-color-blue2">
														<span class="topic-header-date">${topic.message.created}</span>
														<span class="topic-header-left"> <i
															class="fa fa-minus"></i> <i class="fa fa-sitemap"></i>
														</span>
														<div class="widget-toolbar no-border">
															<a class="fa fa-thumb-tack fa-2x" href="#"></a> <a
																class="fa fa-check-square-o fa-2x" href="#"></a> <a
																class="fa fa-times fa-2x" href="#"></a>
														</div>
														<h2>
															<span>${topic.subject}</span>
														</h2>
													</header>

													<div class="widget-body">
														<div class="widget-main">
															<div class="topic-left">
																<a href="#"><img src="i/avatars/clint.jpg"
																	alt="картинка" /></a>
																<div class="topic-author">
																	<a href="#">${topic.userInfo.firstName}
																		${topic.userInfo.lastName}</a>
																	<div class="author-rating">
																		<a href="#" class="fa fa-star"></a> <a
																			class="fa fa-star" href="#"></a> <a
																			class="fa fa-star" href="#"></a> <a
																			class="fa fa-star-half-o" href="#"></a> <a
																			class="fa fa-star-o" href="#"></a>
																	</div>
																</div>
															</div>
															<div class="topic-right">
																<a class="fa fa-link fa-relations" href="#"></a>
																<p class="alert ">${topic.message.content}</p>
																<div class="likes">
																	<a href="#" class="like-item like"> <i
																		class="fa fa-thumbs-o-up"></i> <span>${topic.likesNum}</span>
																	</a> <a href="#" class="like-item dislike"> <i
																		class="fa fa-thumbs-o-down"></i> <span>${topic.unlikesNum}</span>
																	</a>
																</div>
															</div>
														</div>

														<footer class="widget-toolbox clearfix">
															<div class="btn-group ans-btn">
																<button
																	class="btn btn-primary btn-sm dropdown-toggle no-border ans-all">Ответить</button>
																<button data-toggle="dropdown"
																	class="btn btn-primary btn-sm dropdown-toggle no-border ans-pers">
																	<span class="icon-caret-down icon-only smaller-90"></span>
																</button>
																<ul class="dropdown-menu dropdown-warning">
																	<li><a href="#">Ответить лично</a></li>
																</ul>
															</div>
															<div class="answers-ctrl">
																<a
																	class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>"
																	href="#"></a> <span> <span>${topic.messageNum}</span>
																	<a href="#">(3)</a></span>
															</div>
															<div class="topic-statistic">Участников
																${topic.usersNum} Просмотров ${topic.viewers}</div>
														</footer>
													</div>
												</div>
											</li>
										</c:forEach>
									</ol>
								</div>
							</section>--%>

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
							<i class="icon-remove bigger-125"></i> Отмена
						</button>
					</div>

					<div class="btn-group pull-right">
						<button class="btn btn-sm btn-primary">
							<i class="icon-globe bigger-125"></i> Отправить <i
								class="icon-arrow-right icon-on-right bigger-125"></i>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="create-topic-wrap">
		<section class="create-topic">
			<div class="btn-group">
				<button data-toggle="dropdown"
					class="btn btn-info btn-sm dropdown-toggle no-border">
					<span class="btn-group-text">Писать могут</span> <span
						class="icon-caret-down icon-on-right"></span>
				</button>
				<ul class="dropdown-menu dropdown-info pull-right">
					<li><a href="#">Группа 1</a></li>

					<li><a href="#">Группа 2</a></li>

					<li><a href="#">Все</a></li>
				</ul>
			</div>
			<!-- /btn-group -->
			<div class="btn-group">
				<button data-toggle="dropdown"
					class="btn btn-info btn-sm dropdown-toggle no-border">
					<span class="btn-group-text">Читать могут</span> <span
						class="icon-caret-down icon-on-right"></span>
				</button>
				<ul class="dropdown-menu dropdown-info pull-right">
					<li><a href="#">Группа 1</a></li>

					<li><a href="#">Группа 2</a></li>

					<li><a href="#">Все</a></li>
				</ul>
			</div>
			<!-- /btn-group -->
			<h1>Создание темы</h1>
			<div class="has-info form-group">
				<input type="text" class="width-100 head" value="Заголовок"
					onblur="if(this.value=='') this.value='Заголовок';"
					onfocus="if(this.value=='Заголовок') this.value='';" />
			</div>
			<div class="widget-box wysiwig-box">
				<div class="widget-header widget-header-small  header-color-blue2">

				</div>

				<div class="widget-body">
					<div class="widget-main no-padding">
						<div class="wysiwyg-editor"></div>
					</div>

					<div class="widget-toolbox padding-4 clearfix">
						<div class="btn-group pull-left">
							<button class="btn btn-sm btn-primary">Предпросмотр</button>
							<button class="btn btn-sm btn-primary">В черновик</button>
						</div>

						<div class="btn-group pull-right">
							<button class="btn btn-sm btn-primary">
								<i class="icon-globe bigger-125"></i> Создать <i
									class="icon-arrow-right icon-on-right bigger-125"></i>
							</button>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>

	<div class="user-descr-wrap">
		<section class="user-descr">
			<div class="ava-area">
				<img src="i/avatars/clint.jpg" alt="картинка" />
				<div class="user-rating">
					<a href="#" class="fa fa-star"></a> <a class="fa fa-star" href="#"></a>
					<a class="fa fa-star" href="#"></a> <a class="fa fa-star-half-o"
						href="#"></a> <a class="fa fa-star-o" href="#"></a>
				</div>
				<div>Класс С</div>
			</div>
			<div class="soc-accounts">
				<div>Подключить аккаунт:</div>
				<a class="fa fa-vk" href="#"></a> <a class="fa fa-facebook-square"
					href="#"></a> <a class="fa fa-google-plus-square" href="#"></a> <a
					class="fa fa-twitter-square" href="#"></a>
			</div>
			<div class="text-area">
				<div class="user-head">
					<span class="confirm-alert">Аккаунт не подтвержден !</span>
					<h1>Иван Грозный</h1>
					<a class="edit-personal-link" href="#">Редактировать</a>
				</div>
				<div class="user-body">
					<div>
						<span>статус:</span> настроение хорошее
					</div>
					<div>
						<span>Адрес проживания:</span> ул.Коссмонавтов 34
					</div>
					<div>
						<span>День рождения:</span> 23.07.2000
					</div>
					<div>
						<span>Семейное положение:</span> холост
					</div>
					<h3>Контактная информация</h3>
					<div>
						<span>Телефон:</span> 8-911-3333333
					</div>
					<div>
						<span>Email:</span> asdf@sdf.ru
					</div>
					<h3>Интересы</h3>
					<div>футбол</div>
					<div>пиво</div>
					<div>балет</div>
				</div>
			</div>
		</section>
	</div>

	<div class="settings-wrap">
		<section class="settings">
			<h3>Настройки</h3>
			<div class="tabbable">
				<ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab4">
					<li class="active"><a data-toggle="tab" href="#private">Приватность</a>
					</li>

					<li class=""><a data-toggle="tab" href="#subscription">Подписка</a>
					</li>

					<li class=""><a data-toggle="tab" href="#alerts">Оповещения</a>
					</li>
				</ul>

				<div class="tab-content">
					<div id="private" class="tab-pane active">
						<div>
							<label for="form-field-select-1">Показывать мой адрес</label> <select
								class="form-control" id="form-field-select-1">
								<option value="">&nbsp;</option>
								<option value="AL">Никому</option>
								<option value="AK">Васе</option>
							</select>
						</div>
						<div>
							<label for="form-field-select-2">Показывать мой email</label> <select
								class="form-control" id="form-field-select-2">
								<option value="">&nbsp;</option>
								<option value="AL">Никому</option>
								<option value="AK">Васе</option>
							</select>
						</div>
					</div>

					<div id="subscription" class="tab-pane">
						<label for="form-field-select-3">Группа</label>

						<div class="subs-group">
							<select class="form-control" id="form-field-select-3">
								<option value="">&nbsp;</option>
								<option value="AL">Дом</option>
								<option value="AK">Район</option>
							</select>
							<div class="checkbox">
								<label> <input name="form-field-checkbox"
									type="checkbox" class="ace"> <span class="lbl">
										Обо всем</span>
								</label>
							</div>
							<div class="checkbox">
								<label> <input name="form-field-checkbox"
									type="checkbox" class="ace"> <span class="lbl">
										Здоровье</span>
								</label>
							</div>
							<div class="checkbox">
								<label> <input name="form-field-checkbox"
									type="checkbox" class="ace"> <span class="lbl">
										Спорт</span>
								</label>
							</div>
						</div>

					</div>

					<div id="alerts" class="tab-pane">
						<h4>Оповещения на сайте:</h4>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Включить звуковые
									оповещения</span>
							</label>
						</div>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Включить световые
									оповещения</span>
							</label>
						</div>
						<br>
						<h4>Оповещения по e-mail:</h4>
						<div>
							<label>E-mail для оповещений</label> ttt@sdf.ru <a href="#">изменить</a>
						</div>
						<div>
							<label for="form-field-select-4">Частота оповещений</label> <select
								class="form-control" id="form-field-select-4">
								<option value="">&nbsp;</option>
								<option value="AL">Никогда</option>
								<option value="AK">Васе</option>
							</select>
						</div>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Новые темы</span>
							</label>
						</div>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Ответы</span>
							</label>
						</div>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Приглашения в
									новые чаты</span>
							</label>
						</div>
						<div class="checkbox">
							<label> <input name="form-field-checkbox" type="checkbox"
								class="ace"> <span class="lbl"> Приглашения в
									сообщества</span>
							</label>
						</div>

					</div>
				</div>
			</div>
			<a class="btn btn-primary no-border" href="#">Сохранить</a>
		</section>
	</div>

	<div class="edit-personal-wrap">
		<section class="edit-personal">
			<h3>Редактировать профиль</h3>
			<div class="tabbable">
				<ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab5">
					<li class="active"><a data-toggle="tab" href="#main">Основное</a>
					</li>

					<li class=""><a data-toggle="tab" href="#contacts">Контакты</a>
					</li>

					<li class=""><a data-toggle="tab" href="#interests">Интересы</a>
					</li>
				</ul>

				<div class="tab-content">
					<div id="main" class="tab-pane active">
						<div>
							<label for="edit-name">Имя</label> <input id="edit-name"
								type="text" />
						</div>
						<div>
							<label for="edit-surname">Фамилия</label> <input
								id="edit-surname" type="text" />
						</div>
						<div>
							<label for="form-field-select-5">Должность</label> <select
								class="form-control" id="form-field-select-5">
								<option value="">&nbsp;</option>
								<option value="AL">Гончар</option>
								<option value="AK">Копьеносец</option>
							</select>
						</div>
						<div>
							<label for="form-field-select-6">Дата рождения</label> <select
								class="form-control" id="form-field-select-6">
								<option value="">&nbsp;</option>
								<option value="AL">Никому</option>
								<option value="AK">Васе</option>
							</select>
						</div>
					</div>

					<div id="contacts" class="tab-pane">
						<div>
							<label for="edit-email">E-mail</label> <input id="edit-email"
								type="text" />
						</div>
						<div>
							<label for="edit-phone">Телефон</label> <input id="edit-phone"
								type="text" />
						</div>
					</div>

					<div id="interests" class="tab-pane">
						<div>
							<label for="edit-about">О себе</label>
							<textarea name="edit-about" id="edit-about" cols="30" rows="5"></textarea>
						</div>
						<div>
							<label for="edit-interests">Интересы</label>
							<textarea name="edit-interests" id="edit-interests" cols="30"
								rows="5"></textarea>
						</div>

					</div>
				</div>
			</div>
			<a class="btn btn-primary no-border" href="#">Сохранить</a>
		</section>
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
	<script src="js/bootbox.min.js"></script>
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

    <script src="js/forum/angular/angular.js"></script>
	<!-- собственные скрипты  -->
	<%--<script src="js/common.js"></script>--%>
	<%--<script src="js/forum/main.js"></script>--%>
<script src="js/forum/controllers.js"></script>
	<script src="js/forum/app.js"></script>

</body>


</html>
