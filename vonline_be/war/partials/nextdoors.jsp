
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.Group"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
    HttpSession sess = request.getSession();
    pageContext.setAttribute("auth",true);

    try {
        AuthServiceImpl.checkIfAuthorised(sess.getId());
        UserServiceImpl userService = new UserServiceImpl(request.getSession());

        List<Group> Groups = userService.getUserGroups();

        List<ShortUserInfo> neighboors = userService.getNeighbors(Groups.get(0).id);

        pageContext.setAttribute("neighboors",neighboors);
        pageContext.setAttribute("neighboorsSize",neighboors.size());

    } catch (InvalidOperation ioe) {
        pageContext.setAttribute("auth",false);
        response.sendRedirect("/login.html");
        return;
    }


%>
<section class="nextdoors page" ng-class="base.nextdoorsLoadStatus" ng-show="base.nextdoorsIsActive">

<div class="nextdoors">
    <div class="nextdoors-amount pull-left">Зарегестрировано соседей: <span><c:out value="${neighboorsSize}"/></span></div>


    <form method="post" action="#" class="form-group has-info form-search">
        <span class="block input-icon input-icon-right">
            <input id="search-nextdoors" type="text" class="form-control width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';"/>
            <a href="#" class="icon-search icon-on-right bigger-110"></a>
        </span>
    </form>

    <c:forEach var="neighboor" items="${neighboors}">
        <div class="nextdoor-single">
            <a href="#" class="nextdoor-left pull-left">
            <span class="nextdoor-ava pull-left">
                <%--<img src="../../i/avatars/avatar1.png" alt="аватарка"/>--%>
                <img src="${neighboor.avatar}" alt="аватарка"/>
            </span>
                <span class="nextdoor-name">${neighboor.firstName} ${neighboor.lastName}</span>
            </a>
            <div class="nextdoor-right pull-right">
                <a href="#" class="btn btn-sm btn-primary no-border">Написать</a>
            </div>
        </div>
    </c:forEach>

</div>

</section>

