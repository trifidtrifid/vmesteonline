
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
<html>
<head>
<meta charset="utf-8" />
<title>Главная</title>
<link rel="stylesheet" href="build/style.min.css" />

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
<body>
<div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try {
            ace.settings.check('navbar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="navbar-container" id="navbar-container">
        <div class="navbar-header pull-left">
            <a href="#" class="navbar-brand"> <small> <i
                    class="icon-leaf"></i> Ace Admin
            </small>
            </a>
            <!-- /.brand -->
        </div>
        <!-- /.navbar-header -->

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">
                <li class="active"><a class="btn btn-info no-border" href="#">
                    Сообщения </a></li>

                <li><a class="btn btn-info no-border" href="#"> Архив </a></li>

                <li><a class="btn btn-info no-border" href="#"> Избранное
                </a></li>
                <li><a class="btn btn-info no-border" href="/shop.jsp">
                    Магазин </a></li>

                <li class="user-short light-blue"><a data-toggle="dropdown"
                                                     href="#" class="dropdown-toggle"> <img class="nav-user-photo"
                                                                                            src="i/avatars/user.jpg" alt="Jason's Photo" /> <span
                        class="user-info"> <small><c:out
                        value="${firstName}" /></small> <c:out value="${lastName}" />
							</span> <i class="icon-caret-down"></i>
                </a>

                    <ul
                            class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li><a href="settings.html"> <i class="icon-cog"></i>
                            Настройки
                        </a></li>

                        <li><a href="profile.html"> <i class="icon-user"></i>
                            Профиль
                        </a></li>

                        <li class="divider"></li>

                        <li><a href="#"> <i class="icon-off"></i> Выход
                        </a></li>
                    </ul></li>
            </ul>
            <!-- /.ace-nav -->
        </div>
        <!-- /.navbar-header -->
    </div>
    <!-- /.container -->
</div>

	<div class="container">

		<div class="main-container" id="main-container">
			<div class="main-container-inner">
				<aside class="sidebar" id="sidebar">
					<script type="text/javascript">
						try {
							ace.settings.check('sidebar', 'fixed')
						} catch (e) {
						}
					</script>
					<ul class="nav nav-list">
                        <li><a href="#"> <span class="menu-text">Лента</span> </a></li>
                        <li><a href="#"> <span class="menu-text">Обсуждения</span> </a></li>
                        <li><a href="#"> <span class="menu-text">Личные сообщения</span> </a></li>
						<%--<c:forEach var="rubric" items="${rubrics}">
							<li><a href="#" data-rubricid="${rubric.id}"> <span
									class="menu-text">${rubric.visibleName}</span> <b>(3)</b>
							</a></li>
						</c:forEach>--%>

					</ul>
					<!-- /.nav-list -->
				</aside>
				<div class="main-content">
					<nav class="submenu">
						<ul>
							<%--<c:forEach var="group" items="${groups}">
								<li><a class="btn btn-sm btn-info no-border"
									data-groupid="${group.id}" href="#">${group.visibleName}</a></li>
							</c:forEach>--%>
							<%--<li class="btn-group">
								<button data-toggle="dropdown"
									class="btn btn-info btn-sm dropdown-toggle no-border">
									<span class="btn-group-text">Действие</span> <span
										class="icon-caret-down icon-on-right"></span>
								</button>

								<ul class="dropdown-menu dropdown-yellow">
									<li><a href="#">Действие 1</a></li>

									<li><a href="#">Действие 2</a></li>

									<li><a href="#">Что-то еще</a></li>
								</ul>
							</li>--%>
						</ul>
						<div class="clear"></div>
					</nav>
					<div class="dynamic">
						<div class="forum-wrap">


							<section class="forum talks-single">


								<div class="dd dd-draghandle">
									<ol class="dd-list">
<%--										<c:forEach var="topic" items="${topics}">
											<li class="dd-item dd2-item topic-item"	data-topicid="${topic.id}">
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
										</c:forEach>--%>

                                            <li class="dd-item dd2-item topic-item"	data-topicid="${topic.id}">
                                                <div class="dd2-content widget-box topic-descr">
                                                    <div class="widget-body">
                                                        <div class="widget-main">
                                                            <div class="topic-left">
                                                                <a href="#">
                                                                    <img src="i/avatars/clint.jpg" alt="картинка" /></a>
                                                                <div class="topic-author">
                                                                    <a class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>" ng-class="{fa-plus:!talks.isTreeOpen}" ng-class="{fa-minus: talks.isTreeOpen}"
                                                                       ng-click="talks.toggleInsideTreeOfMessages()" href="#"></a>
                                                                </div>
                                                            </div>
                                                            <div class="topic-right">
                                                                    <h2>
                                                                        <span>Давайте уберем мусор</span>
                                                                    </h2>
                                                                <p class="alert ">Хочу создать тему , в которой будем писать только о проблемах в доме! А затем по-возможности писать решена ли она и как быстро! Предлагаю создать обсуждение где будут собраны все реальные проблемы в доме!!!! Предлагаю нумерацию сквозную вести!</p>
                                                                    <div>
                                                                        <span class="topic-header-date">18.01.14 23:01</span>
                                                                        <a class="answer-link" href="#">Ответить</a>
                                                                    </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <ol class="dd-list" ng-show="talks.isTreeOpen">

                                                    <li class="dd-item dd2-item topic-item"	data-topicid="${topic.id}">
                                                        <div class="dd2-content widget-box topic-descr">
                                                            <div class="widget-body">
                                                                <div class="widget-main">
                                                                    <div class="topic-left">
                                                                        <a href="#">
                                                                            <img src="i/avatars/clint.jpg" alt="картинка" /></a>
                                                                        <div class="topic-author">
                                                                            <a class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>" href="#"></a>
                                                                        </div>
                                                                    </div>
                                                                    <div class="topic-right">
                                                                        <h2>
                                                                            <span>Давайте уберем мусор</span>
                                                                        </h2>
                                                                        <p class="alert ">Хочу создать тему , в которой будем писать только о проблемах в доме! А затем по-возможности писать решена ли она и как быстро! Предлагаю создать обсуждение где будут собраны все реальные проблемы в доме!!!! Предлагаю нумерацию сквозную вести!</p>
                                                                        <div>
                                                                            <span class="topic-header-date">18.01.14 23:01</span>
                                                                            <a class="answer-link" href="#">Ответить</a>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </li>
                                                </ol>

                                                <ol class="dd-list">

                                                    <li class="dd-item dd2-item topic-item"	data-topicid="${topic.id}">
                                                        <div class="dd2-content widget-box topic-descr">
                                                            <div class="widget-body">
                                                                <div class="widget-main">
                                                                    <div class="topic-left">
                                                                        <a href="#">
                                                                            <img src="i/avatars/clint.jpg" alt="картинка" /></a>
                                                                        <div class="topic-author">
                                                                            <a class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>" href="#"></a>
                                                                        </div>
                                                                    </div>
                                                                    <div class="topic-right">
                                                                        <h2>
                                                                            <span>Давайте уберем мусор</span>
                                                                        </h2>
                                                                        <p class="alert ">Хочу создать тему , в которой будем писать только о проблемах в доме! А затем по-возможности писать решена ли она и как быстро! Предлагаю создать обсуждение где будут собраны все реальные проблемы в доме!!!! Предлагаю нумерацию сквозную вести!</p>
                                                                        <div>
                                                                            <span class="topic-header-date">18.01.14 23:01</span>
                                                                            <a class="answer-link" href="#">Ответить</a>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <ol class="dd-list">

                                                            <li class="dd-item dd2-item topic-item"	data-topicid="${topic.id}">
                                                                <div class="dd2-content widget-box topic-descr">
                                                                    <div class="widget-body">
                                                                        <div class="widget-main">
                                                                            <div class="topic-left">
                                                                                <a href="#">
                                                                                    <img src="i/avatars/clint.jpg" alt="картинка" /></a>
                                                                                <div class="topic-author">
                                                                                    <a class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>" href="#"></a>
                                                                                </div>
                                                                            </div>
                                                                            <div class="topic-right">
                                                                                <h2>
                                                                                    <span>Давайте уберем мусор</span>
                                                                                </h2>
                                                                                <p class="alert ">Хочу создать тему , в которой будем писать только о проблемах в доме! А затем по-возможности писать решена ли она и как быстро! Предлагаю создать обсуждение где будут собраны все реальные проблемы в доме!!!! Предлагаю нумерацию сквозную вести!</p>
                                                                                <div>
                                                                                    <span class="topic-header-date">18.01.14 23:01</span>
                                                                                    <a class="answer-link" href="#">Ответить</a>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </ol>
                                                    </li>
                                                </ol>
                                            </li>
									</ol>
								</div>
							</section>

						</div>
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
	<!-- собственные скрипты  -->
	<script src="js/common.js"></script>
	<script src="js/main.js"></script>

</body>


</html>
