
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.messageservice.Message"%>
<%@ page import="com.vmesteonline.be.messageservice.TopicListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.MessageListPart"%>
<%@ page import="com.vmesteonline.be.messageservice.Topic"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.vmesteonline.be.messageservice.MessageType" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
HttpSession sess = request.getSession();
pageContext.setAttribute("auth",true);
Boolean isAuth = true;

try {
AuthServiceImpl.checkIfAuthorised(sess.getId());
} catch (InvalidOperation ioe) {
    isAuth = false;
    pageContext.setAttribute("auth",false);
    //response.sendRedirect("/login.html");
    //return;
}

    MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());
    //MessageType mesType = MessageType.BASE;

    TopicListPart Blog = messageService.getBlog(0,1000);

        //out.print(Blog.topics.get(0).message.content);

        pageContext.setAttribute("blog",Blog.topics);



%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Блог</title>
    <link rel="stylesheet" href="css/lib/jquery-ui-1.10.3.full.min.css" />
    <link rel="stylesheet" href="css/style.css" />
    <link rel="stylesheet" href="css/lib/fancybox/jquery.fancybox.css"/>
    <link rel="stylesheet" href="css/lib/jquery.Jcrop.css"/>
    <link rel="stylesheet" href="js/forum/bower_components/select2/select2.css"/>

    <script src="js/lib/jquery-2.0.3.js"></script>
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

    <div class="navbar-container" id="navbar-container">
        <div class="navbar-header pull-left">
            <a href="#" class="navbar-brand">
                <img src="i/logo.png" alt="логотип"/>
            </a>
        </div>
    </div>
</div>

<div class="container coming-soon">

    <div class="main-container" id="main-container">
        <div class="main-container-inner">
            <div class="main-content-top clearfix">

                <div class="page-title pull-left">Новости проекта</div>

            </div>

            <div class="wallitem-message blog" data-auth="<%=isAuth%>">


                <% if(Blog.topics != null){
                    int topicsSize = Blog.topics.size();
                    for(int i = 0; i < topicsSize; i++){


                %>
                <div class="post" data-postlink="<%=Blog.topics.get(i).message.content%>" >
                    <div class="topic"></div>
                    <div class="topic-stuff">
                        <a href="#" class="show-comment">Показать комментарии</a>
                        <a href="#" class="make-comment">Комментировать</a>
                    </div>

                    <div class="dialogs">
                        <%
                            Long topicId = Blog.topics.get(i).id;
                            List<Message> comments = messageService.getMessagesAsList(topicId, MessageType.BLOG , 0,false,1000).messages;
                            if(comments != null && comments.size() != 0){
                                
                                int commentsSize = comments.size();
                                for(int j = 0; j < commentsSize; j++){

                                    String classNoLink = "";
                                    String messageAvatar;
                                    String messageName;
                                    Long messageUserId;

                                    if(!isAuth){
                                        messageAvatar = "data/da.gif";
                                        messageName = comments.get(j).anonName;
                                        messageUserId = (long)(0);
                                        classNoLink = "no-link";
                                    }else{
                                        messageAvatar = comments.get(j).userInfo.avatar;
                                        messageName = comments.get(j).userInfo.firstName+" "+comments.get(j).userInfo.lastName;
                                        messageUserId = comments.get(j).userInfo.id;
                                    }
                        %>

                        <div class="itemdiv dialogdiv">
                            <a href="profile-<%=messageUserId%>" class="user <%=classNoLink%>">
                                <div class="avatar short2" style="background-image: url(<%=messageAvatar%>)"></div>
                            </a>

                            <div class="body">

                                <div class="name">
                                    <a href="profile-<%=messageUserId%>" class="<%=classNoLink%>"><%=messageName%></a>
                                </div>
                                <div class="text"><%=comments.get(j).content%></div>

                                <div class="lenta-item-bottom">
                                    <span><%=comments.get(j).created%></span>
                                    <a href="#">Ответить</a>
                                </div>
                            </div>
                        </div>

                        <%
                           }}
                        %>


                    </div>

                    <div class="input-group">

                        <% if(!isAuth){  %>
                            <input type="text" class="anonName" placeholder="Имя Фамилия"/>
                        <% } %>

                        <textarea class="message-textarea"
                                  onblur="if(this.value=='') this.value='Ваш ответ';"
                                  onfocus="if(this.value=='Ваш ответ') this.value='';" ></textarea>


                        <span class="input-group-btn">
                            <button class="btn btn-sm btn-info no-radius no-border send-in-blog" type="button">
                                <i class="icon-share-alt"></i>
                                Комментировать
                            </button>
                            <span class="error-info"></span>

                        </span>
                    </div>

                </div>
        <% }} %>

        </div>

        </div>
    </div>

</div>


<!-- файлы thrift -->
<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/bedata_types.js" type="text/javascript"></script>
<script src="gen-js/messageservice_types.js" type="text/javascript"></script>
<script src="gen-js/MessageService.js" type="text/javascript"></script>
<!-- -->
<script type="text/javascript">
    $(document).ready(function(){
        var transport = new Thrift.Transport("/thrift/MessageService");
        var protocol = new Thrift.Protocol(transport);
        var messageClient = new com.vmesteonline.be.messageservice.MessageServiceClient(protocol);

        $('.post').each(function(){
            var link = $(this).attr('data-postlink');

            $(this).find('.topic').load(link+' .post', function(){
            });
        });

        $('.show-comment').click(function(e){
            e.preventDefault();

            if($(this).text() == "Показать комментарии"){
                $(this).text("Скрыть комментарии");
            }else{
                $(this).text("Показать комментарии");
            }

            $(this).closest('.post').find('.dialogs').slideToggle(200);
        });

        $('.make-comment').click(function(e){
            e.preventDefault();

            $(this).closest('.post').find('.input-group').slideToggle(200,function(){
               $(this).find('textarea').focus();
            });
        });

        function initNoLink(selector){

            selector.find('.no-link').click(function(e){
                e.preventDefault();

            });

        }
        initNoLink($('.blog'));

        $('.send-in-blog').click(function(){
            var message = new com.vmesteonline.be.messageservice.Message();

            message.id = 0;
            message.type = com.vmesteonline.be.messageservice.MessageType.BLOG;//5;
            message.groupId = 5277655813324800;
            message.content = $(this).closest('.input-group').find('.message-textarea').val();
            message.parentId = 0;
            message.created = Date.parse(new Date)/1000;
            var isAuth = true;

            if($('.blog').attr('data-auth') == 'false') isAuth = false;

            if(!isAuth){
                message.anonName = $(this).closest('.input-group').find('.anonName').val();
            };

            var returnComment = messageClient.postBlogMessage(message),
                    classNoLink = "";

            if(!isAuth){
                message.avatar = "data/da.gif";
                message.name = message.anonName;
                message.userId = 0;
                classNoLink = "no-link";
            }else{
                message.avatar = returnComment.userInfo.avatar;
                message.name = returnComment.userInfo.firstName+" "+returnComment.userInfo.lastName;
                message.userId = returnComment.userInfo.id ;
            }

            var newCommentHTML = '<div class="itemdiv dialogdiv">'+
                '<a href="profile-'+ message.userId +'" class="user '+ classNoLink +'">'+
                        '<div class="avatar short2" style="background-image: url('+ message.avatar +')"></div>'+
                        '</a>'+
                        '<div class="body">'+
                        '<div class="name">'+
                        '<a href="profile-'+ message.userId +'" class="'+ classNoLink +'" >'+ message.name +'</a>'+
                        '</div>'+
                    '<div class="text">'+ message.content +'</div>'+
            '<div class="lenta-item-bottom">'+
                    '<span>'+ message.created +'</span>'+
            '<a href="#">Ответить</a>'+
            '</div>'+
            '</div>'+
            '</div>';

            $(this).closest('.post').find('.dialogs').append(newCommentHTML);
            initNoLink($(this).closest('.post'));
        });

    });
</script>


</body>


</html>