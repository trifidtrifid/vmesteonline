<%@page import="com.vmesteonline.be.utils.Defaults"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.google.appengine.api.utils.SystemProperty"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>



<body>

	<h2>Reset to defaults</h2>

	<%
	if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
		Defaults.initDefaultData();
	
//		Defaults.initializeShop();
	} else {
		%>
		<h1>THIS IS PRODUCTION YOU NEVER SHOULD USE init.jsp!</h1>
		<%
	}
	%>

</body>
</html>
