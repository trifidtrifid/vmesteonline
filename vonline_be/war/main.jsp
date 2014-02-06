
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
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.jdo2.VoSession"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>    

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();
	try {
	 	AuthServiceImpl.checkIfAuthorised(sess.getId());
	} catch (InvalidOperation ioe) {
		response.sendRedirect("/login.html");
		return; 
	}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Главная</title>
  <link rel="stylesheet" href="css/style.css"/>

<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
<script src="gen-js/MessageService.js" type="text/javascript"></script>
<script src="gen-js/userservice_types.js" type="text/javascript"></script>
<script src="gen-js/UserService.js" type="text/javascript"></script>

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
			
	List<Group> Groups = userService.getUserGroups();
	List<Rubric> Rubrics = userService.getUserRubrics();
	MessageServiceImpl messageService = new MessageServiceImpl();
	MessageType mesType = MessageType.BASE;

	TopicListPart Topics = messageService.getTopics(Groups.get(0).id,Rubrics.get(0).id,mesType,20,0,10);
			
	Topic[] currTopic= new Topic[100];// = (Topic)Topics.topics.toArray()[0];
	int topicsLen = Topics.topics.toArray().length;
	
	pageContext.setAttribute("groups",Groups);
	pageContext.setAttribute("rubrics",Rubrics);
	pageContext.setAttribute("topics",Topics.topics);

%>

   <script type="text/javascript">
        $(document).ready(function(){
        	var transport = new Thrift.Transport("/thrift/UserService");
    		var protocol = new Thrift.Protocol(transport);
    		var client = new com.vmesteonline.be.UserServiceClient(protocol);

    		var Groups = client.getUserGroups();
			var Rubrics = client.getUserRubrics();

        	transport = new Thrift.Transport("/thrift/MessageService");
    		protocol = new Thrift.Protocol(transport);
    		client = new com.vmesteonline.be.MessageServiceClient(protocol);

            var Messages,
                    mesLen,
                    mesNew,
                    mesNewLen,
                    mes,level=0,
                    message,
                    messageListNew = "",
                    messageList = '',
                    messageListTopLevel = "",
                    topicsList="",
                    topic,
                    iterator = 0;

            $('.submenu li:first-child, #sidebar .nav-list li:first-child').addClass('active');

            $('.submenu .btn').click(function(e){
                e.preventDefault();
                $(this).closest('.submenu').find('.active').removeClass('active');
                $(this).parent().addClass('active');
                var groupID = $(this).data('groupid');
                 Topics = client.getTopics(groupID,Rubrics[0].id, 1,0,0,100);
                 var topicLen = Topics.topics.length;
                $('.dd>.dd-list').html('');
                //alert('s');

                for(var i = 0; i < topicLen; i++){
                    topic = Topics.topics[i];
                    topicsList = '<li class="dd-item dd2-item topic-item" data-id="15">'+
                        '<div class="dd2-content widget-box topic-descr">'+
                        '<header class="widget-header header-color-blue2">'+
                            '<span class="topic-header-date">01.04.2014 10:10</span>'+
                            '<span class="topic-header-left">'+
                                '<i class="fa fa-minus"></i>'+
                                '<i class="fa fa-sitemap"></i>'+
                            '</span>'+
                            '<div class="widget-toolbar no-border">'+
                                '<a class="fa fa-thumb-tack fa-2x" href="#"></a>'+
                                '<a class="fa fa-check-square-o fa-2x" href="#"></a>'+
                                '<a class="fa fa-times fa-2x" href="#"></a>'+
                            '</div>'+
                            '<h2>'+ topic.subject +'</h2>'+
                        '</header>'+
                        '<div class="widget-body">'+
                            '<div class="widget-main">'+
                                '<div class="topic-left">'+
                                    '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"/></a>'+
                                    '<div class="topic-author">'+
                                        '<a href="#">Иван Грозный</a>'+
                                        '<div class="author-rating">'+
                                        '<a href="#" class="fa fa-star"></a>'+
                                        '<a class="fa fa-star" href="#"></a>'+
                                        '<a class="fa fa-star" href="#"></a>'+
                                        '<a class="fa fa-star-half-o" href="#"></a>'+
                                        '<a class="fa fa-star-o" href="#"></a>'+
                                '</div>'+
                            '</div>'+
                        '</div>'+
                        '<div class="topic-right">'+
                            '<a class="fa fa-link fa-relations" href="#"></a>'+
                            '<p class="alert ">'+ topic.message.content +'</p>'+
                            '<div class="likes">'+
                                '<a href="#" class="like-item like">'+
                                '<i class="fa fa-thumbs-o-up"></i>'+
                                '<span>'+ topic.likesNum +'</span>'+
                                '</a>'+
                                '<a href="#" class="like-item dislike">'+
                                '<i class="fa fa-thumbs-o-down"></i>'+
                                '<span>'+ topic.unlikesNum +'</span>'+
                                '</a>'+
                            '</div>'+
                        '</div>'+
                        '</div>'+
                        '<footer class="widget-toolbox clearfix">'+
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
                                '<span> <span>'+ topic.messageNum +'</span> <a href="#">(3)</a></span>'+
                            '</div>'+
                            '<div class="topic-statistic">'+
                                'Участников '+ topic.usersNum + 'Просмторов '+ topic.viewers +
                            '</div>'+
                        '</footer>'+
                        '</div>'+
                        '</div>'+
                '</li>';
                    $('.dd>.dd-list').append(topicsList);
                    //message = Topics.topics[i];
                    iterator = 0;
                    messageList="";
                    level = 0;
                    messageListNew="";
                    messageListTopLevel = getMessageList(topic.id, groupID, topic.id);
                    //console.log(messageListTopLevel);
                    $('.dd>.dd-list>.topic-item:eq(' + i + ')').append(messageListTopLevel);
                }
            });

            //client.postTopic(client.createTopic(Groups[0].id,'Тест тема-1',1,'некий контент 1',0,0,Rubrics[0].id,1));
            var Topics = client.getTopics(Groups[0].id,Rubrics[0].id, 1,0,0,100);
            var topicLen = Topics.topics.length;
            /*for (var z = 0; z<topicLen ;z++){
             if (Topics.topics[z].subject == 'Тест тема-1'){
             alert('ya');
             }
             }*/

            function getMessageList(topicID, groupID, parentID){
                //console.log(Topics.topics[i].id+" "+Groups[0].id+" "+message.id)    ;
                //console.log('topic '+topicID+" groupID "+ groupID+" parent "+ parentID);
                mes = client.getMessages(topicID, groupID, 1, parentID, false, 0, 2);
                mes = mes.messages;
                mesLen = mes.length;
                if (mesLen > 0 && level < 3){
                    //console.log('level'+level);
                        level++;
                    while(iterator < mesLen){
                      //console.log('parentId '+message.id+' iterator '+iterator);
                      mesNew = mes[iterator];
                      messageListNew += getMessageList(topicID, groupID, mesNew.id);
                      iterator++;
                    }
                    iterator=0;
                    messageList = '<ol class="dd-list">'+
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
                                        '<p class="alert">'+ mes.content+" "+ message.id+ '</p>'+
                                        '<div class="likes">'+
                                            '<div class="answer-date">' + mes.created + '</div>'+
                                            '<a href="#" class="like-item like">'+
                                                '<i class="fa fa-thumbs-o-up"></i>'+
                                                '<span>' + mes.likesNum + '</span>'+
                                            '</a>'+
                                            '<a href="#" class="like-item dislike">'+
                                            '<i class="fa fa-thumbs-o-down"></i>'+
                                            '<span>' + mes.unlikesNum + '</span>'+
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
                            messageListNew +
                        '</li>'+
                        '</ol>';
                } else {
                    messageListNew = "";
                }
                return messageList;
            }

            for(var i = 0; i < topicLen; i++){

                message = Topics.topics[i];
                iterator = 0;
                messageList="";
                messageListNew="";
                level = 0;
                messageListTopLevel = getMessageList(message.id,Groups[0].id,message.id);
                console.log('finish '+ i);
                $('.dd>.dd-list>.topic-item:eq(' + i + ')').append(messageListTopLevel);
                //console.log('finish-2');
            }
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
            <li>
                <a class="btn btn-info no-border" href="#">
                Магазин
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
                
                <c:forEach var="rubric" items="${rubrics}">
                  	<li><a href="#">
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
                    	<li><a class="btn btn-sm btn-info no-border" data-groupID="${group.id}" href="#">${group.visibleName}</a></li>
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
                            <c:forEach var="topic" items="${topics}">
                                <li class="dd-item dd2-item topic-item" data-id="15">
                                    <div class="dd2-content widget-box topic-descr">
                                        <header class="widget-header header-color-blue2">
                                            <span class="topic-header-date">01.04.2014 10:10</span>
                                        <span class="topic-header-left">
                                            <i class="fa fa-minus"></i>
                                            <i class="fa fa-sitemap"></i>
                                        </span>
                                            <div class="widget-toolbar no-border">
                                                <a class="fa fa-thumb-tack fa-2x" href="#"></a>
                                                <a class="fa fa-check-square-o fa-2x" href="#"></a>
                                                <a class="fa fa-times fa-2x" href="#"></a>
                                            </div>
                                            <h2>${topic.subject}</h2>
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
                                                    <span> <span>${topic.messageNum}</span> <a href="#">(3)</a></span>
                                                </div>
                                                <div class="topic-statistic">
                                                    Участников ${topic.usersNum} Просмторов ${topic.viewers}
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
