
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortProfile"%>
<%@ page import="com.vmesteonline.be.UserInfo"%>
<%@ page import="com.vmesteonline.be.UserContacts"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();

    UserServiceImpl userService = new UserServiceImpl(request.getSession());
    AuthServiceImpl authService = new AuthServiceImpl();
    boolean ifEmailConfirmed = authService.checkIfEmailConfirmed(userService.getUserContacts().email);
    pageContext.setAttribute("ifEmailConfirmed",ifEmailConfirmed);

    UserInfo UserInfo = userService.getUserInfo();
    pageContext.setAttribute("userInfo",UserInfo);
    ShortProfile shortProfile = userService.getShortProfile();
    pageContext.setAttribute("shortProfile",shortProfile);
    UserContacts userContacts = userService.getUserContacts();
    pageContext.setAttribute("userContacts",userContacts);
%>

    <div class="profile">
        <section class="user-descr">
            <div class="text-area">
                <div class="user-head" >
                    <%--<c:if test="${!ifEmailConfirmed}">
                        <span class="confirm-alert">Аккаунт не подтвержден !</span>
                    </c:if>--%>
                    <h1><c:out value="${userInfo.firstName}"/> <c:out value="${userInfo.lastName}"/></h1>
                    <a class="edit-personal-link" href="#">Редактировать</a>
                </div>
                <%--<c:if test="${!ifEmailConfirconfirm">
                    <input id="confirmCode" type="text" class="form-control" value="Введите код подтверждения" onblur="if(this.value=='') this.value='Введите код подтверждения';" onfocus="if(this.value=='Введите код подтверждения') this.value='';"/>
                    <input type="submimed}">
                    <form class="account-no-t" value="Подтвердить" class="btn btn-primary btn-sm no-border useConfirmCode">
                        <button class="btn btn-primary btn-sm no-border sendConfirmCode">Получить код повторно</button>
                        <div class="confirm-info"></div>
                    </form>
                </c:if>--%>
                <div class="user-body">
                    <div class="user-body-left pull-left">
                        <label class="block clearfix logo-container">
                            <img src="../../i/avatars/clint.jpg" alt="логотип"/>
                            <input type="file" id="profile-ava">
                        </label>
                        <%--<img src="../../i/avatars/avatar1.png" alt="аватарка"/>--%>
                    </div>
                    
                    <div class="user-body-right">
                        <div><span>Адрес проживания:</span> <c:out value="${shortProfile.address}"/></div>
                        <div><span>День рождения:</span> <c:out value="${userInfo.birthday}"/></div>
                        <h3>Контактная информация</h3>
                        <div><span>Телефон:</span> <c:out value="${userContacts.mobilePhone}"/></div>
                        <div><span>Email:</span> <c:out value="${userContacts.email}"/></div>                        
                    </div>
                </div>
            </div>
        </section>
    </div>


