<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>


<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/user_types.js" type="text/javascript"></script>
<script src="gen-js/AuthService.js" type="text/javascript"></script>

<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>

<script type="text/javascript" charset="utf-8">
	function reg() {
		var transport = new Thrift.Transport("/thrift/AuthService");
		var protocol = new Thrift.Protocol(transport);
		var client = new com.vmesteonline.be.AuthServiceClient(protocol);	

		var groupSelect = document.getElementById("selectGroup");		
		var groupId = groupSelect.options[groupSelect.selectedIndex].value;		
		var userId = client.registerNewUser($("#uname").val(), "family", $("#password")
				.val(), $("#email").val(), groupId);		
		if ( true ) { 
			document.location.replace("/");
		}
	}
</script>

<body>

	<h2>Register page</h2>
	<form action="">
		<table class="login">
			<tr>
				<td>login</td>
				<td><input type="text" id="uname" value="" /></td>
			</tr>
			<tr>
				<td>password</td>
				<td><input type="text" id="password" value="" /></td>
			</tr>
			<tr>
				<td>email</td>
				<td><input type="text" id="email" value="" /></td>
			</tr>
			<tr>
				<td>group</td>
				<td><select id="selectGroup">
						<%
							List<String> codes = UserServiceImpl.getLocationCodesForRegistration();
							for (String code : codes) {
								out.print("<option value=\"" + code + "\">" + code + "</option>");
							}
						%>

				</select></td>
			</tr>
			<tr>
				<td><input type="button" id="register" value="register"
					onclick="javascript:reg();" /></td>
			</tr>
		</table>
	</form>
</body>
</html>
