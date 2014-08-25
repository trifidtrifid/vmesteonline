<%@page import="com.vmesteonline.be.data.PMF"%>
<%@page import="com.vmesteonline.be.utils.Defaults"%>
<%@page import="com.vmesteonline.be.utils.VoHelper"%>
<%@page import="com.google.appengine.api.utils.SystemProperty"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="javax.jdo.PersistenceManager"%>
<html>



<body>

	<h2>Reset to defaults</h2>

	<%
	if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
		if(null==request.getParameter("key")){
			%>
			<h1>key parameter required!</h1>
			<%
		} else if( VoHelper.checkInitKey(request.getParameter("key"))){
			Defaults.initDefaultData();
			%>
			<h1>Init done!</h1>
			<%
		} else {
			%>
			<h1>Init key sent!</h1>
			<%
		}
	} else {
		Defaults.initDefaultData();
		%>
		<h1>Init done!</h1>
		<%
	}
		
	//}
	%>

</body>
</html>
