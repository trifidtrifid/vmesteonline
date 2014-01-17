
<!-- Временная страница. должна быть заменена главной страницей проекта. создана для теста логина  -->

<%@page import="com.vmetsteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.GroupServiceImpl"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<html>
<%
	out.print("user session id: " + request.getSession().getId()
			+ "<br>");
	out.print("user id: "
			+ Long.toString(SessionHelper.getUserId(request
					.getSession().getId())) + "<br>");

	GroupServiceImpl groupService = new GroupServiceImpl(request.getSession());
	List<Group> groups = groupService.getUserGroups();
	for (Group g : groups) {
		out.print("group id: " + g.id + " group name: " + g.shortName
				+ "<br>");
	}
%>

</html>