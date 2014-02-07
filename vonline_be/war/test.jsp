<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<title> Tag Example</title>
</head>
<body>
<jsp:useBean id="VoRubric" class="com.vmesteonline.be"/>

<c:forEach var="r" items="${com.vmesteonline.be.utils.Defaults.getRubrics}">
   Item ${r.visibleName}<p>
</c:forEach>
</body>
</html>