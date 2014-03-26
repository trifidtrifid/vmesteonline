
<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.MessageService"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.TopicListPart"%>
<%@ page import="com.vmesteonline.be.MessageListPart"%>
<%@ page import="com.vmesteonline.be.Topic"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.Message"%>
<%@ page import="com.vmesteonline.be.MessageType"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
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

    List<Group> Groups = userService.getUserGroups();
    List<Rubric> Rubrics = userService.getUserRubrics();
    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
    MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());
    //MessageType mesType = MessageType.BASE;

    TopicListPart Topics = messageService.getTopics(Groups.get(0).id,Rubrics.get(0).id,0,0,10);
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
                    <div class="forum-wrap">

                        <section class="forum">
                            <section class="options">
                                <div class="btn-group">
                                    <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                        <span class="btn-group-text">Сортировка</span>
                                        <span class="icon-caret-down icon-on-right"></span>
                                    </button>
                                    <ul class="dropdown-menu dropdown-info pull-right">
                                        <li>
                                            <a href="#">Сортировка 1</a>
                                        </li>

                                        <li>
                                            <a href="#">Сортировка2</a>
                                        </li>

                                        <li>
                                            <a href="#">Что-то еще</a>
                                        </li>
                                    </ul>
                                </div><!-- /btn-group -->

                                <a class="btn btn-primary btn-sm no-border create-topic-show" href="#">Создать тему</a>

                                <form method="post" action="#" class="form-group has-info">
                        <span class="block input-icon input-icon-right">
                            <input type="text" id="inputInfo" class="width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';" />
                            <a href="#" class="icon-search icon-on-right bigger-110"></a>
                        </span>
                                </form>
                                <div class="clear"></div>
                            </section>

                            <%--<c:forEach var="topic" items="${topics}">

                            </c:forEach>--%>

                            <div class="dd dd-draghandle">
                                <ol class="dd-list">
                                    <c:forEach var="topic" items="${topics}">
                                        <li class="dd-item dd2-item topic-item" data-topicid="${topic.id}">
                                            <div class="dd2-content widget-box topic-descr">
                                                <header class="widget-header header-color-blue2">
                                                    <span class="topic-header-date">${topic.message.created}</span>
                                        <span class="topic-header-left">
                                            <i class="fa fa-minus"></i>
                                            <i class="fa fa-sitemap"></i>
                                        </span>
                                                    <div class="widget-toolbar no-border">
                                                        <a class="fa fa-thumb-tack fa-2x" href="#"></a>
                                                        <a class="fa fa-check-square-o fa-2x" href="#"></a>
                                                        <a class="fa fa-times fa-2x" href="#"></a>
                                                    </div>
                                                    <h2><span>${topic.subject}</span></h2>
                                                </header>

                                                <div class="widget-body">
                                                    <div class="widget-main">
                                                        <div class="topic-left">
                                                            <a href="#"><img src="i/avatars/clint.jpg" alt="картинка"/></a>
                                                            <div class="topic-author">
                                                                <a href="#">${topic.userInfo.firstName} ${topic.userInfo.lastName}</a>
                                                                <div class="author-rating">
                                                                    <a href="#" class="fa fa-star"></a>
                                                                    <a class="fa fa-star" href="#"></a>
                                                                    <a class="fa fa-star" href="#"></a>
                                                                    <a class="fa fa-star-half-o" href="#"></a>
                                                                    <a class="fa fa-star-o" href="#"></a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="topic-right">
                                                            <a class="fa fa-link fa-relations" href="#"></a>
                                                            <p class="alert ">${topic.message.content}</p>
                                                            <div class="likes">
                                                                <a href="#" class="like-item like">
                                                                    <i class="fa fa-thumbs-o-up"></i>
                                                                    <span>${topic.likesNum}</span>
                                                                </a>
                                                                <a href="#" class="like-item dislike">
                                                                    <i class="fa fa-thumbs-o-down"></i>
                                                                    <span>${topic.unlikesNum}</span>
                                                                </a>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <footer class="widget-toolbox clearfix">
                                                        <div class="btn-group ans-btn">
                                                            <button class="btn btn-primary btn-sm dropdown-toggle no-border ans-all">Ответить</button>
                                                            <button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border ans-pers">
                                                                <span class="icon-caret-down icon-only smaller-90"></span>
                                                            </button>
                                                            <ul class="dropdown-menu dropdown-warning">
                                                                <li>
                                                                    <a href="#">Ответить лично</a>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                        <div class="answers-ctrl">
                                                            <a class="fa fa-plus plus-minus <c:if test="${topic.messageNum == 0}">hide</c:if>" href="#"></a>
                                                            <span> <span>${topic.messageNum}</span> <a href="#">(3)</a></span>
                                                        </div>
                                                        <div class="topic-statistic">
                                                            Участников ${topic.usersNum} Просмотров ${topic.viewers}
                                                        </div>
                                                    </footer>
                                                </div>
                                            </div>
                                        </li>
                                    </c:forEach>
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
<div class="create-topic-wrap">
    <section class="create-topic">
        <div class="btn-group">
            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                <span class="btn-group-text">Писать могут</span>
                <span class="icon-caret-down icon-on-right"></span>
            </button>
            <ul class="dropdown-menu dropdown-info pull-right">
                <li>
                    <a href="#">Группа 1</a>
                </li>

                <li>
                    <a href="#">Группа 2</a>
                </li>

                <li>
                    <a href="#">Все</a>
                </li>
            </ul>
        </div><!-- /btn-group -->
        <div class="btn-group">
            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                <span class="btn-group-text">Читать могут</span>
                <span class="icon-caret-down icon-on-right"></span>
            </button>
            <ul class="dropdown-menu dropdown-info pull-right">
                <li>
                    <a href="#">Группа 1</a>
                </li>

                <li>
                    <a href="#">Группа 2</a>
                </li>

                <li>
                    <a href="#">Все</a>
                </li>
            </ul>
        </div><!-- /btn-group -->
        <h1>Создание темы</h1>
        <div class="has-info form-group">
            <input type="text" class="width-100 head" value="Заголовок" onblur="if(this.value=='') this.value='Заголовок';" onfocus="if(this.value=='Заголовок') this.value='';" />
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
                        <button class="btn btn-sm btn-primary">
                            Предпросмотр
                        </button>
                        <button class="btn btn-sm btn-primary">
                            В черновик
                        </button>
                    </div>

                    <div class="btn-group pull-right">
                        <button class="btn btn-sm btn-primary">
                            <i class="icon-globe bigger-125"></i>
                            Создать
                            <i class="icon-arrow-right icon-on-right bigger-125"></i>
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
            <img src="i/avatars/clint.jpg" alt="картинка"/>
            <div class="user-rating">
                <a href="#" class="fa fa-star"></a>
                <a class="fa fa-star" href="#"></a>
                <a class="fa fa-star" href="#"></a>
                <a class="fa fa-star-half-o" href="#"></a>
                <a class="fa fa-star-o" href="#"></a>
            </div>
            <div>Класс С</div>
        </div>
        <div class="soc-accounts">
            <div>Подключить аккаунт:</div>
            <a class="fa fa-vk" href="#"></a>
            <a class="fa fa-facebook-square" href="#"></a>
            <a class="fa fa-google-plus-square" href="#"></a>
            <a class="fa fa-twitter-square" href="#"></a>
        </div>
        <div class="text-area">
            <div class="user-head" >
                <span class="confirm-alert">Аккаунт не подтвержден !</span>
                <h1>Иван Грозный</h1>
                <a class="edit-personal-link" href="#">Редактировать</a>
            </div>
            <div class="user-body">
                <div><span>статус:</span> настроение хорошее</div>
                <div><span>Адрес проживания:</span> ул.Коссмонавтов 34</div>
                <div><span>День рождения:</span> 23.07.2000</div>
                <div><span>Семейное положение:</span> холост</div>
                <h3>Контактная информация</h3>
                <div><span>Телефон:</span> 8-911-3333333</div>
                <div><span>Email:</span> asdf@sdf.ru</div>
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
                <li class="active">
                    <a data-toggle="tab" href="#private">Приватность</a>
                </li>

                <li class="">
                    <a data-toggle="tab" href="#subscription">Подписка</a>
                </li>

                <li class="">
                    <a data-toggle="tab" href="#alerts">Оповещения</a>
                </li>
            </ul>

            <div class="tab-content">
                <div id="private" class="tab-pane active">
                    <div>
                        <label for="form-field-select-1">Показывать мой адрес</label>

                        <select class="form-control" id="form-field-select-1">
                            <option value="">&nbsp;</option>
                            <option value="AL">Никому</option>
                            <option value="AK">Васе</option>
                        </select>
                    </div>
                    <div>
                        <label for="form-field-select-2">Показывать мой email</label>

                        <select class="form-control" id="form-field-select-2">
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
                            <label>
                                <input name="form-field-checkbox" type="checkbox" class="ace">
                                <span class="lbl"> Обо всем</span>
                            </label>
                        </div>
                        <div class="checkbox">
                            <label>
                                <input name="form-field-checkbox" type="checkbox" class="ace">
                                <span class="lbl"> Здоровье</span>
                            </label>
                        </div>
                        <div class="checkbox">
                            <label>
                                <input name="form-field-checkbox" type="checkbox" class="ace">
                                <span class="lbl"> Спорт</span>
                            </label>
                        </div>
                    </div>

                </div>

                <div id="alerts" class="tab-pane">
                    <h4>Оповещения на сайте:</h4>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Включить звуковые оповещения</span>
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Включить световые оповещения</span>
                        </label>
                    </div>
                    <br>
                    <h4>Оповещения по e-mail:</h4>
                    <div>
                        <label>E-mail для оповещений</label>
                        ttt@sdf.ru <a href="#">изменить</a>
                    </div>
                    <div>
                        <label for="form-field-select-4">Частота оповещений</label>

                        <select class="form-control" id="form-field-select-4">
                            <option value="">&nbsp;</option>
                            <option value="AL">Никогда</option>
                            <option value="AK">Васе</option>
                        </select>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Новые темы</span>
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Ответы</span>
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Приглашения в новые чаты</span>
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input name="form-field-checkbox" type="checkbox" class="ace">
                            <span class="lbl"> Приглашения в сообщества</span>
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
                        <input id="edit-name" type="text"/>
                    </div>
                    <div>
                        <label for="edit-surname">Фамилия</label>
                        <input id="edit-surname" type="text"/>
                    </div>
                    <div>
                        <label for="form-field-select-5">Должность</label>

                        <select class="form-control" id="form-field-select-5">
                            <option value="">&nbsp;</option>
                            <option value="AL">Гончар</option>
                            <option value="AK">Копьеносец</option>
                        </select>
                    </div>
                    <div>
                        <label for="form-field-select-2">Дата рождения</label>

                        <select class="form-control" id="form-field-select-2">
                            <option value="">&nbsp;</option>
                            <option value="AL">Никому</option>
                            <option value="AK">Васе</option>
                        </select>
                    </div>
                </div>

                <div id="contacts" class="tab-pane">
                    <div>
                        <label for="edit-email">E-mail</label>
                        <input id="edit-email" type="text"/>
                    </div>
                    <div>
                        <label for="edit-phone">Телефон</label>
                        <input id="edit-phone" type="text"/>
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
