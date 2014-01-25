
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
<%@ page import="com.vmesteonline.be.Message"%>
<%@ page import="com.vmesteonline.be.MessageType"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%> 

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Главная</title>
  <link rel="stylesheet" href="css/style.css"/>

<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
<script src="gen-js/MessageService.js" type="text/javascript"></script>
<script src="js/jquery-2.0.3.min.js"></script>
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
<%

	UserServiceImpl userService = new UserServiceImpl(request.getSession());
			
	List<Group> someGroupId = userService.getUserGroups();	
	List<Rubric> someRoubricId = userService.getUserRubrics();	
	
	MessageServiceImpl messageService = new MessageServiceImpl();
	//out.print("Topics:<br>");
	MessageType mesType = MessageType.BASE;
	TopicListPart Topics = messageService.getTopics(someGroupId.get(0).id,someRoubricId.get(0).id,mesType,20,0,10);	
		
	//out.print("TopicListPart Size: " + Topics.totalSize + "<br>"); 
	//out.print("Topics size: " + Topics.topics.size() + "<br>"); 
		
	Topic[] currTopic= new Topic[100];// = (Topic)Topics.topics.toArray()[0];
	int topicsLen = Topics.topics.toArray().length;
	
	//out.print(Topics.topics.toArray().length);
	
	for (Topic t : Topics.topics) {
		//out.print("topic id: " + t.id + "<br>");		
	}
	
	
%>
    <script type="text/javascript">
        $(document).ready(function(){
        	var transport = new Thrift.Transport("/thrift/MessageService");
    		var protocol = new Thrift.Protocol(transport);
    		var client = new com.vmesteonline.be.MessageServiceClient(protocol);   		
        	var Messages;
        	        	
        	var messageList = '';
        	//alert('1');
        	<%
        	for(int i=0; i<topicsLen; i++){
        		currTopic[i] = (Topic)Topics.topics.toArray()[i];	%>
        		
        		Messages = client.getMessages(<%= currTopic[i].id %>,<%= someGroupId.get(0).id %>, 'BASE', <%= currTopic[i].id %>,false,0,2);
        		alert(Messages.messages[0].id);
        		
        		<%if(currTopic[i].messageNum > 0){
        			MessageListPart Messages = messageService.getMessages(currTopic[i].id,someGroupId.get(0).id,mesType,currTopic[i].id,false,0,2);
        			for (Message m : Messages.messages) {
        		%>
        			messageList += '<ol class="dd-list">'+                    
                    '<li class="dd-item dd2-item topic-item" data-id="19">'+
                        '<div class="dd2-content topic-descr answer-item widget-body">'+
                            '<div class="widget-main">'+
                                '<div class="topic-left">'+
                                    '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"></a>'+
                                '</div>'+
                                '<div class="topic-right">'+
                                    '<div class="message-author">'+
                                        '<a class="fa fa-link fa-relations" href="#" style="display: none;"></a>'+
                                        '<a class="fa fa-sitemap" href="#" style="display: none;"></a>'+
                                        '<a href="#">Иван Грозный</a>'+
                                    '</div>'+
                                    "<p class=\"alert \"><%= m.content %></p>"+
                                    '<div class="likes">'+
                                        '<div class="answer-date"><%= m.created %></div>'+
                                        '<a href="#" class="like-item like">'+
                                            '<i class="fa fa-thumbs-o-up"></i>'+
                                            '<span><%= m.likesNum %></span>'+
                                        '</a>'+
                                        '<a href="#" class="like-item dislike">'+
                                            '<i class="fa fa-thumbs-o-down"></i>'+
                                            '<span><%= m.unlikesNum %></span>'+
                                        '</a>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                            '<footer class="widget-toolbox padding-4 clearfix">'+
                                '<div class="btn-group ans-btn">'+
                                    '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">'+
                                        'Ответить'+
                                        '<span class="icon-caret-down icon-on-right"></span>'+
                                    '</button>'+
                                    '<ul class="dropdown-menu dropdown-warning">'+
                                        '<li>'+
                                            '<a href="#">Ответить лично</a>'+
                                        '</li>'+
                                    '</ul>'+
                                '</div>'+
                                '<div class="answers-ctrl">'+
                                    '<a class="fa fa-minus plus-minus" href="#"></a>'+
                                    '<span> <span>8</span> <a href="#">(3)</a></span>'+
                                '</div>'+
                            '</footer>'+
                        '</div>'+
                    '</li>'+
                '</ol>';
        		<% } %>	
        			$('.dd>.dd-list>.topic-item:eq(<%= i %>)').append(messageList);
       		
       		<%
        		}        		
        	}
        	%>        	
        });
    </script>

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

            <li class="user-short light-blue">
                <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                    <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                    <span class="user-info">
                        <small>Welcome,</small>
                        Jason
                    </span>
                    <i class="icon-caret-down"></i>
                </a>

                <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                    <li>
                        <a href="#">
                            <i class="icon-cog"></i>
                            Настройки
                        </a>
                    </li>

                    <li>
                        <a href="#">
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
                <% for (Rubric r : userService.getUserRubrics()) {%>
                	<li>
                        <a href="index.html">
                            <span class="menu-text"><%= r.visibleName %></span>
                            <b>(3)</b>
                        </a>
                    </li>            				
            	<% 	} %>
                </ul><!-- /.nav-list -->
            </aside>
            <div class="main-content">
                <nav class="submenu">                
                    <ul>
                    <jsp:useBean id="g" class="com.vmesteonline.be.Group"></jsp:useBean>
                    <c:forEach var="g" items="${userService.userGroups}">                    
                    	<li><a class="btn btn-sm btn-info no-border" href="#">"${g.visibleName}"</a></li> 
                    </c:forEach>
                    <%-- <% for (Group g : userService.getUserGroups()) { %>                    
                		<li><a class="btn btn-sm btn-info no-border" href="#"><%= g.visibleName %></a></li>            				
            		<% } %> --%>                       
                    </ul>
                    <div class="btn-group">
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
                    </div><!-- /btn-group -->
                    <div class="clear"></div>
                </nav>
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

                    <a class="btn btn-primary btn-sm no-border" href="#">Создать тему</a>

                    <form method="post" action="#" class="form-group has-info">
                        <span class="block input-icon input-icon-right">
                            <input type="text" id="inputInfo" class="width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';" />
                            <a href="#" class="icon-search icon-on-right bigger-110"></a>
                        </span>
                    </form>
                        <div class="clear"></div>
                </section>
                <section class="forum">

                    <div class="dd dd-draghandle">
                        <ol class="dd-list">
                        <% for (Topic t : Topics.topics) { %>
                        	<li class="dd-item dd2-item topic-item" data-id="15">
                                <div class="dd2-content widget-box topic-descr">
                                    <header class="widget-header header-color-blue2">
                                        <span class="topic-header-date">01.04.2014 10:10</span>
                                        <span class="topic-header-left">
                                            <i class="fa fa-minus"></i>
                                            <i class="fa fa-sitemap"></i>
                                        </span>
                                        <h2><%= t.subject %></h2>

                                        <div class="widget-toolbar no-border">
                                            <a class="fa fa-thumb-tack fa-2x" href="#"></a>
                                            <a class="fa fa-check-square-o fa-2x" href="#"></a>
                                            <a class="fa fa-times fa-2x" href="#"></a>
                                        </div>
                                    </header>

                                    <div class="widget-body">
                                        <div class="widget-main">
                                            <div class="topic-left">
                                                <a href="#"><img src="i/avatars/clint.jpg" alt="картинка"/></a>
                                                <div class="topic-author">
                                                    <a href="#">Иван Грозный</a>
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
                                                <p class="alert "><%= t.message.content %></p>
                                                <div class="likes">
                                                    <a href="#" class="like-item like">
                                                        <i class="fa fa-thumbs-o-up"></i>
                                                        <span><%= t.likesNum %></span>
                                                    </a>
                                                    <a href="#" class="like-item dislike">
                                                        <i class="fa fa-thumbs-o-down"></i>
                                                        <span><%= t.unlikesNum %></span>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <footer class="widget-toolbox clearfix">
                                            <div class="btn-group ans-btn">
                                                <button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">
                                                    Ответить
                                                    <span class="icon-caret-down icon-on-right"></span>
                                                </button>

                                                <ul class="dropdown-menu dropdown-warning">
                                                    <li>
                                                        <a href="#">Ответить лично</a>
                                                    </li>
                                                </ul>
                                            </div>
                                            <div class="answers-ctrl">
                                                <a class="fa fa-minus plus-minus" href="#"></a>
                                                <span> <span><%= t.messageNum %></span> <a href="#">(3)</a></span>
                                            </div>
                                            <div class="topic-statistic">
                                                Участников <%= t.usersNum %> Просмторов <%= t.viewers %>
                                            </div>
                                        </footer>
                                    </div>
                                </div>
                            </li>                    
                    				
                    	<% } %>
                            
                        </ol>
                    </div>
                </section>
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
<script src="js/bootstrap.min.js"></script>
<script src="js/ace-extra.min.js"></script>
<script src="js/ace-elements.min.js"></script>

<!-- конкретные плагины -->

    <!-- библиотека для дерева сообщений -->
<script src="js/jquery.nestable.min.js"></script>

    <!-- библиотеки для wysiwig редактора  -->
<script src="js/jquery-ui-1.10.3.custom.min.js"></script>
<script src="js/markdown/markdown.min.js"></script>
<script src="js/markdown/bootstrap-markdown.min.js"></script>
<script src="js/jquery.hotkeys.min.js"></script>
<script src="js/bootstrap-wysiwyg.min.js"></script>
<script src="js/bootbox.min.js"></script>
    <!-- -->

<!-- -->

<!-- собственные скрипты  -->
<script src="js/main.js"></script>

</body>


</html>
