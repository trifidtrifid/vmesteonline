
<!-- Временная страница. должна быть заменена главной страницей проекта. создана для теста логина  -->

<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.MessageService"%>
<%@ page import="com.vmesteonline.be.MessageServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.Rubric"%>
<%@ page import="com.vmesteonline.be.TopicListPart"%>
<%@ page import="com.vmesteonline.be.Topic"%>
<%@ page import="com.vmesteonline.be.MessageType"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<html>
<%
	out.print("user session id: " + request.getSession().getId() + "<br>");
	out.print("user id: " + Long.toString(SessionHelper.getUserId(request.getSession().getId())) + "<br>");

	UserServiceImpl userService = new UserServiceImpl(request.getSession());
	out.print("Groups:<br>");
	//long someGroupId=0;
	for (Group g : userService.getUserGroups()) {
		out.print("group id: " + g.id + " group name: " + g.visibleName + " radius: " + g.radius + "<br>");
		//someGroupId = g.id;
	}	
	List<Group> someGroupId = userService.getUserGroups();
	out.print("someRoubricId:"+someGroupId.get(0).id+"<br>");

	out.print("Rubrics:<br>");
	//long someRoubricId=0;
	for (Rubric r : userService.getUserRubrics()) {
		out.print("rubric id: " + r.id + " rubric name: " + r.visibleName + "<br>");
		//someRoubricId = r.id;
	}
	List<Rubric> someRoubricId = userService.getUserRubrics();
	out.print("someRoubricId:"+someRoubricId.get(0).id+"<br>");
	
	
	MessageServiceImpl messageService = new MessageServiceImpl();
	//out.print("Topics:<br>");
	MessageType mesType = MessageType.BASE;
	TopicListPart Topics = messageService.getTopics(someGroupId.get(3).id,someRoubricId.get(0).id,mesType,20,0,10);
		
	out.print("TopicListPart Size: " + Topics.totalSize + "<br>"); // дает число: 7 или 8 или т.п
	out.print("Topics size: " + Topics.topics.size() + "<br>"); // дает 0
	
	//Iterator<Topic> Iter = Topics.topics.iterator();
	//Topic[] topicsArr = Topics.topics.toArray(Topic);
	String topicsStr = Topics.topics.toString();
	//Set<Topic> topicsSet = Topics.topics;//.toArray();
	//Object topicsArr0 = topicsArr[0];
	out.print(topicsStr);
	
	
%>

</html>