
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

<div class="nextdoors">
    <form method="post" action="#" class="form-group has-info form-search">
        <span class="block input-icon input-icon-right">
            <input id="search-nextdoors" type="text" class="form-control width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';"/>
            <a href="#" class="icon-search icon-on-right bigger-110"></a>
        </span>
    </form>

    <div class="nextdoor-single">
        <a href="#" class="nextdoor-left pull-left">
            <span class="nextdoor-ava pull-left">
                <img src="../../i/avatars/avatar1.png" alt="аватарка"/>
            </span>
            <span class="nextdoor-name">Евгений Плющенко</span>
        </a>
        <div class="nextdoor-right pull-right">
            <a href="#">Написать</a>
        </div>
    </div>
    <div class="nextdoor-single">
        <a href="#" class="nextdoor-left pull-left">
            <span class="nextdoor-ava pull-left">
                <img src="../../i/avatars/avatar1.png" alt="аватарка"/>
            </span>
            <span class="nextdoor-name">Евгений Плющенко</span>
        </a>
        <div class="nextdoor-right pull-right">
            <a href="#">Написать</a>
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
