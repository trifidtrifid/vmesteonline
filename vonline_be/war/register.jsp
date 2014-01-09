<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.GroupServiceImpl"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>

<body>
	<select name="selectYear" id="selectGroup">
		<%
			GroupServiceImpl groupService = new GroupServiceImpl();
			List<Group> groups = groupService.getGroupsForRegistration();
			for (Group g : groups) {
				out.print("<option value=\"" + g.shortName + "\">" + g.shortName + "</option>" );
			}
		%>

	</select>

</body>
</html>
