<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ include file="templates/preload.jsp" %>
<%
    int ArrayShopsSize = ArrayShops.size();

    int now = (int) (System.currentTimeMillis() / 1000L);
    //int day = 3600 * 24;
    double[] totalShopArray = new double[ArrayShopsSize];

    for(int i = 0; i < ArrayShopsSize; i++){
        totalShopArray[i] = shopBOService.totalShopReturn(ArrayShops.get(i).id, 0, now);
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Админка</title>
  <link rel="stylesheet" href="/build/shop.min.css"/>
    <!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->
</head>
<body>

<div class="container adminka">
<%--    <div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try{ace.settings.check('navbar' , 'fixed')}catch(e){}
    </script>

    <div class="navbar-container" id="navbar-container">
    <div class="navbar-header pull-left">
        <a href="shop.jsp" class="navbar-brand">
            <img src="<c:out value="${logoURL}" />" alt="лого">
        </a><!-- /.brand -->
    </div><!-- /.navbar-header -->

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">

                <li class="active"><a class="btn btn-info no-border" href="shop.jsp">
                    Магазин </a></li>
                <li class="user-short light-blue">
                    <c:choose>
                        <c:when test="${isAuth}">
                            <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                                <span class="user-info">
                                    <c:out value="${firstName}" /> <c:out value="${lastName}" />
                                </span>
                                    <i class="icon-caret-down"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a data-toggle="dropdown" href="#" class="dropdown-toggle no-login">
                                <span class="user-info">
                                    Войти
									</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <ul	class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">

                        <li><a href="#"> <i class="icon-user"></i> Профиль
                        </a></li>

                        <li class="divider"></li>

                        <li><a href="#"> <i class="icon-off"></i> Выход
                        </a></li>
                    </ul>
                </li>
            </ul>
            <!-- /.ace-nav -->
        </div>
    </div><!-- /.container -->
    </div>--%>

    <%@ include file="templates/header.jsp" %>

        <div class="main-container backoffice adminka dynamic">
            <c:if test="${isAuth}">
            <div class="page main-container-inner bo-page">
                <aside class="sidebar" id="sidebar">
                    <script type="text/javascript">
                        try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
                    </script>
                    <div class="show-left">
                        Меню
                    </div>
                    <ul class="nav nav-list">
                        <li class="active">
                            <a href="#">
                                <span class="menu-text"> Магазины </span>
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                <span class="menu-text"> Статистика </span>
                            </a>
                        </li>
                    </ul><!-- /.nav-list -->
                </aside>

                <div class="main-content">
                    <div class="adminka-shops back-tab">
                        <a class="btn btn-primary btn-sm no-border create-shop" href="regShop.jsp">Создать магазин</a>
                        <table>
                            <c:forEach var="shop" items="${shops}">
                                <tr id="${shop.id}">
                                    <td class="shop-name">${shop.name}</td>
                                    <td class="shop-activation">
                                        <div class="checkbox">
                                            <label>
                                                <input name="form-field-checkbox" type="checkbox" class="ace">
                                                <span class="lbl"> Активен</span>
                                            </label>
                                        </div>
                                    </td>
                                    <td class="owner-name">
                                        <span></span>
                                        <a class="update-owner-link fa fa-pencil" href="#"></a>
                                    </td>
                                    <td class="owner-contacts"></td>
                                    <td class="td-icon"><a href="#" class="remove-item">&times;</a></td>
                                </tr>
                            </c:forEach>

                        </table>
                    </div>
                    <div class="adminka-statistics back-tab">
                        <h1>Оборот магазина</h1>
                        <div>(по умолчанию за все время)</div><br>
                        <%--<div class="btn-group adminka-statistics-period">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                <span class="btn-group-text">Выберите период</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <li><a href="#">За месяц</a></li>
                                <li class="divider"></li>
                                <li><a href="#">За все время</a></li>
                            </ul>
                        </div>--%>
                        <div class="adminka-statistics-date">
                            <label for="datepicker-from">от</label>
                            <input class="form-control date-picker" id="datepicker-from" type="text" data-date-format="mm-dd-yyyy" value="Дата" onblur="if(this.value=='') this.value='Дата';" onfocus="if(this.value=='Дата') this.value='';"/>
                        </div>
                        <div class="adminka-statistics-date">
                            <label for="datepicker-from">до</label>
                            <input class="form-control date-picker" id="datepicker-to" type="text" data-date-format="mm-dd-yyyy" value="Дата" onblur="if(this.value=='') this.value='Дата';" onfocus="if(this.value=='Дата') this.value='';"/>
                        </div>
                        <div class="adminka-statistics-date">
                            <button class="btn no-border btn-sm btn-primary">Показать</button>
                        </div>
                        <table>
                            <% for( int i = 0; i < ArrayShopsSize; i ++ ){ %>
                            <%--<c:forEach var="shop" items="${shops}">--%>
                                <tr id="<%=ArrayShops.get(i).id%>">
                                    <td class="shop-name"><%=ArrayShops.get(i).name%></td>
                                    <td class="shop-total"><%=totalShopArray[i]%></td>
                                </tr>
                            <%--</c:forEach>--%>
                            <% } %>
                        </table>
                    </div>
                </div>
            </div>
            <div class="page shop-profile"></div>
            <div class="page shop-editPersonal"></div>
            </c:if>
        </div>

    <div class="modal modal-auth">
    </div>

</div>


<!-- файлы thrift -->
<script src="/build/thrift.min.js" type="text/javascript"></script>
<script src="/build/gen-js/bedata_types.js" type="text/javascript"></script>

<script src="/build/gen-js/shop_types.js" type="text/javascript"></script>
<script src="/build/gen-js/ShopFEService.js" type="text/javascript"></script>
<script src="/build/gen-js/shop.bo_types.js" type="text/javascript"></script>
<script src="/build/gen-js/ShopBOService.js" type="text/javascript"></script>

<script src="/build/gen-js/authservice_types.js" type="text/javascript"></script>
<script src="/build/gen-js/AuthService.js" type="text/javascript"></script>
<script src="/build/gen-js/userservice_types.js" type="text/javascript"></script>
<script src="/build/gen-js/UserService.js" type="text/javascript"></script>
<!-- -->

<script type="text/javascript" data-main="/build/backoffice.min.js" src="/js/shop/require.min.js"></script>

</body>
</html>
