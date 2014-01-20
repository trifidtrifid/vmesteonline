
<!-- Временная страница. должна быть заменена главной страницей проекта. создана для теста логина  -->

<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<html>
<%
	out.print("user session id: " + request.getSession().getId() + "<br>");
	out.print("user id: " + Long.toString(SessionHelper.getUserId(request.getSession().getId())) + "<br>");

	UserServiceImpl userService = new UserServiceImpl(request.getSession());
	out.print("Groups:<br>");
	for (Group g : userService.getUserGroups()) {
		out.print("group id: " + g.id + " group name: " + g.visibleName + " radius: " + g.radius + "<br>");
	}

	out.print("Rubrics:<br>");
	for (Rubric r : userService.getUserRubrics()) {
		out.print("rubric id: " + r.id + " rubric name: " + r.visibleName + "<br>");
	}
%>

</html>