<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>




<%
	if (request.getServerName().split("\\.")[0].equals("market")) {
		out.print("hello market<br>");
		response.sendRedirect("shop.jsp");
	} else
		out.print("hello default <br>");
%>

<html>
<body>
	<h2>init page</h2>

	<FORM action="https://accounts.google.com/o/oauth2/auth" method="post">
		<input type="hidden" name="response_type" value="code"> <input type="hidden" name="client_id" value="290786477692.apps.googleusercontent.com"> <input type="hidden"
			name="redirect_uri" value="https://1-dot-vmesteonline.appspot.com/oauth"> <input type="hidden" name="scope" value="email"> <input type="hidden" name="state" value="google">
		<INPUT type="submit" value="Google">
	</FORM>


	<!--  https://www.facebook.com/dialog/oauth?client_id=293608184137183&redirect_uri=https://www.vmesteonline.ru/oauth&scope=email&state=facebook -->


	<FORM action="https://www.facebook.com/dialog/oauth" method="get">
		<input type="hidden" name="client_id" value="293608184137183"> <input type="hidden" name="redirect_uri" value="https://1-dot-vmesteonline.appspot.com/oauth"> <input type="hidden"
			name="scope" value="email"><input type="hidden" name="state" value="facebook"> <INPUT type="submit" value="Facebook">
	</FORM>


	<FORM action="https://oauth.vk.com/authorize" method="get">
		<input type="hidden" name="client_id" value="4429306"> <input type="hidden" name="redirect_uri" value="https://1-dot-vmesteonline.appspot.com/oauth"> <input type="hidden"
			name="scope" value="4194305"><input type="hidden" name="display" value="popup"><input type="hidden" name="v" value="5.21"><input type="hidden" name="state" value="vk"> <INPUT type="submit" value="vkontakte">
	</FORM>

</body>
</html>
