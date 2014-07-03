<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShopBOServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.FileServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>

<%
    HttpSession sess = request.getSession();

    UserServiceImpl userService = new UserServiceImpl(sess);
    AuthServiceImpl authService = new AuthServiceImpl(sess.getId());
    MessageServiceImpl messageService = new MessageServiceImpl(sess.getId());
    ShopServiceImpl shopService = new ShopServiceImpl(sess.getId());
    ShopBOServiceImpl shopBOService = new ShopBOServiceImpl(sess.getId());
    FileServiceImpl fileService = new FileServiceImpl();

    String email = ""+'a';
    String passw = ""+'a';
    Boolean isLogin = authService.login(email,passw);

    List<Group> Groups = userService.getUserGroups();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Тесты thrift</title>
</head>
<body>
<h1>Тесты thrift</h1>
<div>IsLogin: <%=isLogin%></div>
<div>GroupsSize: <%=Groups.size()%></div>
</body>
</html>