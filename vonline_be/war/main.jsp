
<!-- Временная страница. должна быть заменена главной страницей проекта. создана для теста логина  -->

<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<html>
<%
	out.print("user session id: " + request.getSession().getId() + "<br>");
	out.print("user id: " + Long.toString(SessionHelper.getUserId(request.getSession().getId())) + "<br>");

	UserServiceImpl userService = new UserServiceImpl(request.getSession());
	List<Group> groups = userService.getUserGroups();
	if (groups != null) {
		for (Group g : groups) {
			out.print("group id: " + g.id + " group name: " + g.visibleName + "<br>");
		}

	} else {
		out.print("can't find groups for user<br>");
	}
%>

</html>