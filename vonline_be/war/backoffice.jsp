<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShopBOServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.shop.*"%>
<%@ page import="com.vmesteonline.be.shop.bo.*"%>

<%
    HttpSession sess = request.getSession();
    pageContext.setAttribute("auth",true);
    try {
    AuthServiceImpl.checkIfAuthorised(sess.getId());
    UserServiceImpl userService = new UserServiceImpl(request.getSession());
    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
    if( null == ShortUserInfo){
        sess.invalidate();
        throw new InvalidOperation( com.vmesteonline.be.VoError.NotAuthorized, "");
    }
    pageContext.setAttribute("firstName",ShortUserInfo.firstName);
    pageContext.setAttribute("lastName",ShortUserInfo.lastName);
    } catch (InvalidOperation ioe) {
    //pageContext.setAttribute("auth",false);
        response.sendRedirect("/login.jsp");
        sess.setAttribute("successLoginURL", request.getQueryString());
        return;
    }

    ShopServiceImpl shopService = new ShopServiceImpl(request.getSession().getId());

    List<Shop> ArrayShops = shopService.getShops();
    if(ArrayShops != null && ArrayShops.size() > 0){
        Shop shop = shopService.getShop(ArrayShops.get(0).id);
        pageContext.setAttribute("logoURL", shop.logoURL);
        pageContext.setAttribute("shopID", shop.id);
    }

    int now = (int) (System.currentTimeMillis() / 1000L);
    int day = 3600 * 24;
    List<Order> orders = shopService.getOrders(0, now + 180*day);
    if(orders.size() > 0 ){
        pageContext.setAttribute("orders", orders);
    }

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Бэкоффис</title>
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
<div class="container backoffice">
    <div class="navbar navbar-default" id="navbar">
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
                        <c:when test="${auth}">
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
    </div>
    <div class="main-container backoffice dynamic" id="${shopID}">
        <div class="page main-container-inner">
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
                            <span class="menu-text"> Заказы </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Импорт </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Экспорт </span>
                        </a>
                    </li>
                </ul><!-- /.nav-list -->
            </aside>

            <div class="main-content">
                <div class="back-orders back-tab">
                    <div class="back-orders-top">
                        <a href="#" class="btn btn-grey btn-sm no-border reset-filters">Сбросить</a>
                        <div class="btn-group status-dropdown">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                <span class="btn-group-text">Статус заказа</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <li><a href="#">Подтвержден</a></li>
                                <li><a href="#">Не подтвержден</a></li>
                                <li><a href="#">Отменен</a></li>
                            </ul>
                        </div>
                        <div class="btn-group type-delivery-dropdown">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                <span class="btn-group-text">Тип доставки</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <li><a href="#">Самовывоз</a></li>
                                <li><a href="#">Курьер рядом</a></li>
                                <li><a href="#">Курьер далеко</a></li>
                            </ul>
                        </div>
                        <div class="input-group">
                            <input class="form-control date-picker" id="date-picker-1" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                        </div>
                        <form method="post" action="#" class="form-group has-info search-form">
                            <span class="block input-icon input-icon-right">
                                <input id="back-search" type="text" class="form-control width-100" value="Поиск по имени клиента или номеру телефона" onblur="if(this.value=='') this.value='Поиск по имени клиента или номеру телефона';" onfocus="if(this.value=='Поиск по имени клиента или номеру телефона') this.value='';"/>
                                <a href="#" class="icon-search icon-on-right bigger-110"></a>
                            </span>
                        </form>

                    </div>
                    <div class="orders-list">
                    </div>

                </div>
                <div class="import back-tab">
                    <h2>Здесь вы можете загрузить список продуктов в магазин</h2>
                    <div class="btn-group import-dropdown">
                        <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                            <span class="btn-group-text">Тип импортируемых данных</span>
                            <span class="icon-caret-down icon-on-right"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-blue">
                            <li><a href="#">Продукты</a></li>
                            <li><a href="#">Категории продуктов</a></li>
                            <li><a href="#">Производители</a></li>
                        </ul>
                    </div>

                    <form action="/file/" method="post"  class="form-import" enctype="multipart/form-data"> <%--enctype="multipart/form-data"--%>
                        <input id="import-data" type="file" name="data"/>
                        <input id="import-public" type="hidden" name="public"/> <!-- Be sure that you gona make file accessible for everyone -->
                        <input class="btn btn-primary btn-sm no-border" type="submit" value="Загрузить" />
                    </form>
                    <div class="error-info"></div>

                    <div class="import-show">
                        <div class="import-btn-top">
                            <span class="confirm-info"></span>
                            <a class="btn btn-primary btn-sm no-border import-btn" href="#">Импортировать</a>
                        </div>
                        <h3>Поставьте соответствие данных и столбцов</h3>

                        <div id="doublescroll" class="import-table">

                        </div>
                        <div>
                            <a class="btn btn-primary btn-sm no-border import-btn" href="#">Импортировать</a>
                            <span class="confirm-info"></span>
                        </div>
                    </div>
                </div>
                <div class="export back-tab tabbable">
                    <ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab4">
                        <li class="active">
                            <a data-toggle="tab" href="#orders">Отчет о Заказах</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#products">Отчет о продуктах</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#pack">Отчет об упаковках</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div id="orders" class="tab-pane in active">
                            <p>Отчет о Заказах</p>

                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-2" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>

                            <div class="export-orders-checklist">
                                <select multiple>
                                <optgroup label="Order">
                                    <%
                                        for( int val = ExchangeFieldType.ORDER_ID.getValue(); val < ExchangeFieldType.ORDER_LINE_ID.getValue(); val ++ ){
                                            ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                            if(null!=value){
                                                if(value.name() == "ORDER_PRICE_TYPE" || value.name() == "ORDER_PAYMENT_TYPE"
                                                  || value.name() == "ORDER_PAYMENT_STATUS" || value.name() == "ORDER_USER_ID"){
                                    %>
                                                    <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                                <% } else{ %>
                                                    <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                        <%}}}%>

                                </optgroup>
                                <optgroup label="Order_Line">
                                        <%for( int val = ExchangeFieldType.ORDER_LINE_ID.getValue(); val < ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val ++ ){
                                            ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                            if(null!=value){
                                            if(value.name() == "ORDER_LINE_ID" || value.name() == "ORDER_LINE_OPRDER_ID"
                                                || value.name() == "ORDER_LINE_PRODUCT_ID" || value.name() == "ORDER_LINE_PRODUCER_ID"
                                                || value.name() == "ORDER_LINE_PRODUCT_ID" || value.name() == "ORDER_LINE_PRODUCER_ID"){
                                                    %>

                                            <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                            <% } else{ %>
                                            <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                        <%}}}%>
                                </optgroup>
                                </select>

                            </div>

                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">

                            </div>
                        </div>

                        <div id="products" class="tab-pane">
                            <p>Отчет о продуктах</p>

                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-3" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>

                            <div class="export-products-checklist">
                                <select multiple>

                                    <%for( int val = ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val < ExchangeFieldType.TOTAL_PACK_SIZE.getValue(); val ++ ){
                                        ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                        if(null!=value){
                                            if(value.name() == "TOTAL_PROUCT_ID" || value.name() == "TOTAL_PRODUCER_ID"){
                                    %>

                                    <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                    <% } else{ %>
                                    <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                    <%}}}%>
                                </select>

                            </div>
                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">

                            </div>
                        </div>

                        <div id="pack" class="tab-pane">
                            <p>Отчет об упаковках</p>
                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-4" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>


                            <div class="export-packs-checklist">
                                <select multiple>
                                <%for( int val = ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val <= ExchangeFieldType.TOTAL_DELIVERY_TYPE.getValue(); val ++ ){
                                    ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                    if(null!=value){
                                        if(value.name() == "TOTAL_PROUCT_ID" || value.name() == "TOTAL_PRODUCER_ID"|| value.name() == "TOTAL_DELIVERY_TYPE"){
                                %>

                                <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                <% } else{ %>
                                <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                <%}}}%>
                                </select>

                            </div>
                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="page shop-profile"></div>
        <div class="page shop-editPersonal"></div>

    </div>
    <div class="loading">
        <div class="loading-inside">
            <img src="i/wait1.png" alt="загрузка">
            <span>Подождите, идет загрузка ...</span>
        </div>
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

<script type="text/javascript" data-main="/build/backoffice.min.js" src="/js/require.min.js"></script>


</body>
</html>