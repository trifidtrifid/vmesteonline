<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.shop.*"%>

<%
    HttpSession sess = request.getSession();
    pageContext.setAttribute("auth",true);
    try {
    AuthServiceImpl.checkIfAuthorised(sess.getId());
    UserServiceImpl userService = new UserServiceImpl(request.getSession());
    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
    pageContext.setAttribute("firstName",ShortUserInfo.firstName);
    pageContext.setAttribute("lastName",ShortUserInfo.lastName);
    } catch (InvalidOperation ioe) {
    pageContext.setAttribute("auth",false);
    }


    ShopServiceImpl shopService = new ShopServiceImpl(request.getSession().getId());

    List<Shop> ArrayShops = shopService.getShops();
    Shop shop = shopService.getShop(ArrayShops.get(0).id);
    pageContext.setAttribute("logoURL", shop.logoURL);

    //OrderDetails currentOrderDetails;
    //try{

    int now = (int) (System.currentTimeMillis() / 1000L);
    int day = 3600 * 24;
    List<Order> orders = shopService.getOrders(0, now + 180*day);
    pageContext.setAttribute("orders", orders);

    /*currentOrderDetails = shopService.getOrderDetails(order.id);
    List<OrderLine> orderLines = currentOrderDetails.odrerLines;
    pageContext.setAttribute("orderLines", orderLines);
    } catch(InvalidOperation ioe){
    currentOrderDetails = null;
    }*/

   /* Cookie cookies [] = request.getCookies();
    String cookieName = "catid";
    Cookie catIdCookie = null;
    if (cookies != null) {
    for (int i = 0; i < cookies.length; i++) {
    if (cookies[i].getName().equals (cookieName)) {
    catIdCookie = cookies[i];
    }
    }
    }

    long catId = 0;

    try{
    if (catIdCookie != null){catId = Long.parseLong(catIdCookie.getValue());}
    if (catId != 0){
    pageContext.setAttribute("innerCategoryFlag",true);
    }
    }catch(Exception e){
    catId = 0;
    }

    List<ProductCategory> ArrayProductCategory = shopService.getProductCategories(catId);
    ProductListPart productsListPart = shopService.getProducts(0,10,catId);
    if (productsListPart.products.size() > 0){
    pageContext.setAttribute("products",productsListPart.products);
    }
    pageContext.setAttribute("productCategories", ArrayProductCategory);*/

    //String productURL = new String( productsListPart.products.get(0).imageURL);
    //out.print(ArrayProductCategory.get(1).id);

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Бэкоффис</title>
  <link rel="stylesheet" href="css/shop.css"/>
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
        <a href="#" class="navbar-brand">
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
                                <img class="nav-user-photo" src="i/avatars/user.jpg"
                                     alt="Jason's Photo" /> <span class="user-info"> <small><c:out
                                    value="${firstName}" /></small> <c:out value="${lastName}" />
									</span> <i class="icon-caret-down"></i>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a data-toggle="dropdown" href="#" class="dropdown-toggle no-login">
                                <img class="nav-user-photo" src="i/avatars/user.jpg"
                                     alt="Jason's Photo" /> <span class="user-info"> <small>Привет,</small>
											Гость
									</span>
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <ul	class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                        <li><a href="#"> <i class="icon-cog"></i> Настройки
                        </a></li>

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
    <div class="main-container backoffice dynamic" id="main-container">
        <div class="main-container-inner">
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
                    <%--<c:forEach var="orders" items="${orders}">
                        <div class="order-item" data-orderid="${orders.id}">
                            <table class="orders-tbl">
                                <tbody>
                                <tr>
                                    <td class="td1"><a class="fa fa-plus plus-minus" href="#"></a></td>
                                    <td class="td2">Заказ N 124</td>
                                    <td class="td3">${orders.date}</td>
                                    <td class="td4">${orders.status}</td>
                                    <td class="td5">Курьер рядом<br> Санкт-Петербург, e e, кв.12</td>
                                    <td class="td6">${orders.totalCost}</td>
                                </tr>
                                </tbody>
                            </table>
                            <div class="order-products">
                                &lt;%&ndash;<section class="catalog">
                                    <table>
                                        <thead>
                                        <tr>
                                            <td>Название</td>
                                            <td>Цена (руб)</td>
                                            <td>Количество</td>
                                            <td>Ед.изм</td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr data-productid="5435985487724544">
                                            <td>
                                                <a href="#" class="product-link">
                                                    <img src="/file/FVAAAAAAAA=.jpg" alt="картинка">
                                                <span>
                                                    <span>Молоко 3,2%</span>
                                                    Молоко питьевое пастеризованное 2%, 1л, ГОСТ Р 52090
                                                </span>
                                                </a>
                                                <div class="modal"></div>
                                            </td>
                                            <td class="product-price">48</td>
                                            <td>
                                                <input type="text" class="input-mini spinner1" />
                                            </td>
                                            <td><span class="unit-name">пак</span></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </section>&ndash;%&gt;
                            </div>
                        </div>
                    </c:forEach>--%>
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
                        <h3>Поставьте соответствие данных и столбцов</h3>
                        <div class="import-table">

                        </div>
                        <div>
                            <a class="btn btn-primary btn-sm no-border import-btn" href="#">Импортировать</a>
                            <span class="confirm-info"></span>
                        </div>
                    </div>
<%--                    <section class="catalog">
                        <table>
                            <thead>
                            <tr>
                                <td>Название</td>
                                <td>Цена (руб)</td>
                                <td>Количество</td>
                                <td>Ед.изм</td>
                            </tr>
                            </thead>

                            <tbody>

                            <tr data-productid="6139672929501184">
                                <td>
                                    <a href="#" class="product-link">
                                        <img src="" alt="картинка">
                                            <span>
                                            <span>Творог 5% весовой</span>
                                            Творог 5% на развес, ГОСТ Р 52096
                                            </span>
                                    </a>
                                    <div class="modal">
                                    </div>
                                </td>
                                <td class="product-price">38.0</td>
                                <td>
                                    <input type="text" class="input-mini spinner1 spinner-input form-control" maxlength="3">
                                </td>
                                <td>
                                    <span class="unit-name">гр</span>
                                </td>
                            </tr>


                            </tbody></table>
                    </section>--%>
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
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-2" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>
                            <div class="error-info"></div>

                            <div class="checkbox check-all">
                                <label>
                                    <input name="form-field-checkbox" type="checkbox" class="ace">
                                    <span class="lbl"> check all</span>
                                </label>
                            </div>

                            <div class="export-orders-checklist">
                                <div class="checkbox"  data-exchange="1000">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1001">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_DATE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1002">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_STATUS</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1003">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_PRICE_TYPE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1004">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_TOTAL_COST</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1005">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_CREATED</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1006">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_DELIVERY_TYPE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1007">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_DELIVERY_COST</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1008">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_DELIVERY_ADDRESS</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1009">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_PAYMENT_TYPE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1010">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_PAYMENT_STATUS</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1011">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_COMMENT</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1012">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_USER_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1013">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_USER_NAME</span>
                                    </label>
                                </div>
                            </div>

                            <div class="export-orderLine-checklist">
                                <div class="checkbox"  data-exchange="1100">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1101">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_QUANTITY</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1102">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_OPRDER_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1103">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PRODUCT_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1104">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PRODUCT_NAME</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1105">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PRODUCER_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1106">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PRODUCER_NAME</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1107">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PRICE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1108">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_COMMENT</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="1109">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> ORDER_LINE_PACKETS</span>
                                    </label>
                                </div>
                            </div>

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
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-3" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>
                            <div class="error-info"></div>

                            <div class="checkbox check-all">
                                <label>
                                    <input name="form-field-checkbox" type="checkbox" class="ace">
                                    <span class="lbl"> check all</span>
                                </label>
                            </div>

                            <div class="export-products-checklist">
                                <div class="checkbox"  data-exchange="2000">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PROUCT_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2001">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PRODUCT_NAME</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2002">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PRODUCER_ID</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2003">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PRODUCER_NAME</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2004">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PRODUCT_MIN_PACK</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2005">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_ORDERED</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2006">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_MIN_QUANTITY</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2007">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_REST</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2008">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PREPACK_REQUIRED</span>
                                    </label>
                                </div>
                            </div>

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
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-4" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>
                            <div class="error-info"></div>

                            <div class="checkbox check-all">
                                <label>
                                    <input name="form-field-checkbox" type="checkbox" class="ace">
                                    <span class="lbl"> check all</span>
                                </label>
                            </div>

                            <div class="export-packs-checklist">
                                <div class="checkbox"  data-exchange="2009">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PACK_SIZE</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2010">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_PACK_QUANTYTY</span>
                                    </label>
                                </div>
                                <div class="checkbox"  data-exchange="2011">
                                    <label>
                                        <input name="form-field-checkbox" type="checkbox" class="ace">
                                        <span class="lbl"> TOTAL_DELIVERY_TYPE</span>
                                    </label>
                                </div>
                            </div>

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
    </div>

</div>
<!-- общие библиотеки -->
<script src="js/lib/jquery-2.0.3.min.js"></script>
<script src="js/lib/bootstrap.min.js"></script>

<!-- файлы thrift -->
<script src="js/thrift.js" type="text/javascript"></script>
<script src="gen-js/bedata_types.js" type="text/javascript"></script>
<script src="gen-js/shop_types.js" type="text/javascript"></script>
<script src="gen-js/ShopService.js" type="text/javascript"></script>
<script src="gen-js/authservice_types.js" type="text/javascript"></script>
<script src="gen-js/AuthService.js" type="text/javascript"></script>
<script src="gen-js/userservice_types.js" type="text/javascript"></script>
<script src="gen-js/UserService.js" type="text/javascript"></script>
<!-- -->

<!-- конкретные плагины -->
<script src="js/lib/jquery-ui-1.10.3.full.min.js"></script>
<script src="js/lib/fuelux/fuelux.spinner.min.js"></script>
<script src="js/lib/date-time/bootstrap-datepicker-backoffice.js"></script>
<script src="js/lib/date-time/locales/bootstrap-datepicker.ru.js"></script>
<script src="js/lib/jquery.flexslider-min.js"></script>

<!-- -->
<!-- собственные скрипты  -->
<script src="js/lib/ace-extra.min.js"></script>
<script src="js/lib/ace-elements.min.js"></script>
<script src="js/common.js"></script>
<script src="js/shop-backoffice.js"></script>

</body>
</html>