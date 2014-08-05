
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

        ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
        MessageServiceImpl messageService = new MessageServiceImpl(request.getSession().getId());
        //MessageType mesType = MessageType.BASE;

        TopicListPart Blog = messageService.getBlog(0,1000);

            //out.print(ShortUserInfo.firstName);

            pageContext.setAttribute("blog",Blog.topics);
            pageContext.setAttribute("firstName",ShortUserInfo.firstName);
            pageContext.setAttribute("lastName",ShortUserInfo.lastName);
            pageContext.setAttribute("userAvatar",ShortUserInfo.avatar);
            } catch (InvalidOperation ioe) {
                pageContext.setAttribute("auth",false);
                //response.sendRedirect("/login.html");
            return;
            }


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
            <div class="main-content-top">

                <div class="page-title pull-left">Новости проекта</div>

            </div>

<div class="wallitem-message">

    <c:forEach var="topic" items="blog">

        <div class="first-message clearfix" data-link="<c:out value="${topic.content}"/>">
            <a href="profile-<c:out value="${topic.userInfo.id}"/>" class="user">
                <div class="avatar" style="background-image: url(<c:out value="${topic.userInfo.avatar}"/>)"></div>
            </a>

            <div class="body">

                <div class="name">
                    <a href="profile-<c:out value="${topic.userInfo.id}"/>"><c:out value="${topic.userInfo.firstName}"/> <c:out value="${topic.userInfo.lastName}"/></a>
                </div>

                <div class="text"><c:out value="${topic.content}"/></div>

                <div class="lenta-item-bottom">
                    <span><c:out value="${topic.lastUpdate}"/></span>
                    <a href="#">Комментировать</a>
                </div>

            </div>
        </div>

        <div class="dialogs">
            <div class="itemdiv dialogdiv">
                <a href="profile-" class="user">
                    <div class="avatar short2" style="background-image: url()"></div>
                </a>

                <div class="body">

                    <div class="name">
                        <a href="profile-">{{blogMessage.authorName}}</a>
                    </div>
                    <div class="text">{{blogMessage.content}}</div>

                    <div class="lenta-item-bottom">
                        <span>{{blogMessage.createdEdit}}</span>
                        <a href="#">Ответить</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="input-group">
            <textarea name="answerInput{{blog.topic.id}}" id="name{{blog.topic.id}}" class="message-textarea no-resize"
                      onblur="if(this.value=='') this.value='Ваш ответ';"
                      onfocus="if(this.value=='Ваш ответ') this.value='';" ></textarea>


        <span class="input-group-btn">
            <button class="btn btn-sm btn-info no-radius no-border send-in-blog" type="button">
                <i class="icon-share-alt"></i>
                Отправить
            </button>
            <span class="error-info">{{blog.createCommentErrorText}}</span>

        </span>
        </div>

    </c:forEach>

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


        $('.send-in-blog').click(function(){
            //userClient.postMessage();
        })
    });
</script>


</body>


</html>