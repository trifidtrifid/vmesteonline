<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.jdo2.VoSession"%>
<%@ page import="com.vmesteonline.be.jdo2.VoFileAccessRecord"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>

<%@ page import="java.nio.Buffer"%>
<%@ page import="java.nio.ByteBuffer"%>
<%@ page import="java.nio.ByteOrder"%>
<%@ page import="java.nio.CharBuffer"%>
<%@ page import="com.vmesteonline.be.shop.*" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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

    Cookie cookies [] = request.getCookies();
    String cookieName = "catid";
    String cookieName2 = "arrayPrevCat";
    Cookie catIdCookie = null;
    //Cookie arrayPrevCat = null;
    if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
            //out.print(cookies[i].getName());
            if (cookies[i].getName().equals (cookieName)) {
                catIdCookie = cookies[i];
            }
            /*if (cookies[i].getName().equals (cookieName2)) {
                arrayPrevCat = cookies[i];
            }*/
        }
    }
    /*if (arrayPrevCat != null){
        out.print("==");
        out.print(arrayPrevCat.getValue());
        out.print("==");
    }*/

    long catId = 0;
    try{
        if (catIdCookie != null){catId = Long.parseLong(catIdCookie.getValue());}
        if (catId != 0){
            pageContext.setAttribute("innerCategoryFlag",true);
        }
    }catch(Exception e){
        catId = 0;
    }
    //out.print(catId);

    List<ProductCategory> ArrayProductCategory = shopService.getProductCategories(catId);
    ProductListPart productsListPart = shopService.getProducts(0,10,catId);
    if (productsListPart.products.size() > 0){
        ProductDetails productDetails = shopService.getProductDetails(productsListPart.products.get(0).id);
        pageContext.setAttribute("products",productsListPart.products);
        pageContext.setAttribute("productDetails",productDetails);
    }
    pageContext.setAttribute("productCategories", ArrayProductCategory);

    //String productURL = new String( productsListPart.products.get(0).imageURL);

    /*List<Order> ArrayOrders = shopService.getOrders(0,(int)(System.currentTimeMillis()/1000L)+86400*30);
    Order order = shopService.getOrder(ArrayOrders.get(2).id);
    OrderDetails orderDetails = shopService.getOrderDetails(order.id);
    List<OrderLine> orderLineArray= orderDetails.odrerLines;*/

    //out.print(orderDetails.odrerLines.size());
    //out.print(orderDetails.odrerLines.get(0).product.id);
    //out.print(ArrayProductCategory.get(0).logoURLset);
    //out.print(new String( productsListPart.products.get(0).imageURL));
    //out.print(ArrayProductCategory.get(1).id);


    //pageContext.setAttribute("productURL",productURL);
    //pageContext.setAttribute("orderLines",orderLineArray);
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Магазин</title>
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

        <script type="text/javascript">
            globalUserAuth = false;
            <c:if test="${auth}">
                globalUserAuth = true;
            </c:if>
        </script>
</head>
<body>
<div class="container">
    <div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try{ace.settings.check('navbar' , 'fixed')}catch(e){}
    </script>

    <div class="navbar-container" id="navbar-container">
    <div class="navbar-header pull-left">
        <a href="#" class="navbar-brand">
            <small>
                <i class="icon-leaf"></i>
                Ace Admin
            </small>
        </a><!-- /.brand -->
    </div><!-- /.navbar-header -->

    <div class="navbar-header pull-right" role="navigation">
        <ul class="nav ace-nav">

            <li class="active">
                <a class="btn btn-info no-border" href="#">
                    Магазин
                </a>
            </li>
            <li class="user-short light-blue">
                <c:choose>
                    <c:when test="${auth}">
                <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                    <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                    <span class="user-info">
                        <small><c:out value="${firstName}"/></small>
                        <c:out value="${lastName}"/>
                    </span>
                    <i class="icon-caret-down"></i>
                </a>

                <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                    <li>
                        <a href="#">
                            <i class="icon-cog"></i>
                            Настройки
                        </a>
                    </li>

                    <li>
                        <a href="#">
                            <i class="icon-user"></i>
                            Профиль
                        </a>
                    </li>

                    <li class="divider"></li>

                    <li>
                        <a href="#">
                            <i class="icon-off"></i>
                            Выход
                        </a>
                    </li>
                </ul>
                    </c:when>
                    <c:otherwise>
                        <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                            <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                    <span class="user-info">
                        <small>Привет,</small>
                        Гость
                    </span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </li>
        </ul><!-- /.ace-nav -->
    </div><!-- /.navbar-header -->
    </div><!-- /.container -->
    </div>
    <div class="main-container shop" id="main-container">
        <div class="main-container-inner">
            <%--<aside class="sidebar" id="sidebar">
                <script type="text/javascript">
                    try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
                </script>
                <div class="show-left">
                    Меню
                </div>
                <ul class="nav nav-list">
                    <li>
                        <a href="index.html">
                            <span class="menu-text"> Главная </span>
                        </a>
                    </li>
                    <li class="active">
                        <a href="index.html">
                            <span class="menu-text"> Товары/Заказы </span>
                        </a>
                    </li>
                    <li>
                        <a href="index.html">
                            <span class="menu-text"> Новости </span>
                        </a>
                    </li>
                    <li>
                        <a href="index.html">
                            <span class="menu-text"> Форум </span>
                        </a>
                    </li>
                    <li>
                        <a href="index.html">
                            <span class="menu-text"> Настройки </span>
                        </a>
                    </li>

                </ul><!-- /.nav-list -->
            </aside>--%>
            <aside class="sidebar shop-right">
                <div class="show-right">
                    Заказы
                </div>
                <div class="hide-right">×</div>
                <div class="sidebar-title">
                    <a href="#" class="go-to-orders shop-trigger">Заказы</a>
                    <nav>
                        <div class="input-group">
                            <input class="form-control date-picker" id="id-date-picker-1" type="text" data-date-format="dd-mm-yyyy" value="Выберите дату" onblur="if(this.value=='') this.value='Выберите дату';" onfocus="if(this.value=='Выберите дату') this.value='';"/>
                            <%--<span class="input-group-addon">
                                <i class="icon-calendar bigger-110"></i>
                            </span>--%>
                        </div>
                    </nav>
                </div>

                <ul class="catalog-order">
                    <%--<c:forEach var="orderLine" items="${orderLines}">
                        <li>
                            <img src="${orderLine.product.imageURL}" alt="картинка"/>
                            <div class="product-right-descr">
                                ${orderLine.product.name}  <br>
                                ${orderLine.product.shortDescr}
                            </div>
                            <table>
                                <thead>
                                <tr>
                                    <td>Цена(шт)</td>
                                    <td>Кол-во</td>
                                    <td>Сумма</td>
                                    <td></td>
                                </tr>
                                </thead>
                                <tr>
                                    <td class="td-price">${orderLine.product.price}</td>
                                    <td><input type="text" class="input-mini spinner1" /></td>
                                    <td class="td-summa">${orderLine.product.price}</td>
                                    <td><a href="#" class="delete-product">Удалить</a></td>
                                </tr>
                            </table>
                        </li>
                    </c:forEach>--%>
                    <%--<li>
                        <img src="i/shop/1.jpg" alt="картинка"/>
                        <div class="product-right-descr">
                            100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас
                        </div>
                        <table>
                            <thead>
                            <tr>
                                <td>Цена(шт)</td>
                                <td>Кол-во</td>
                                <td>Сумма</td>
                                <td></td>
                            </tr>
                            </thead>
                            <tr>
                                <td class="td-price">111р.</td>
                                <td><input type="text" class="input-mini spinner1" /></td>
                                <td class="td-summa">111р.</td>
                                <td><a href="#" class="delete-product">Удалить</a></td>
                            </tr>
                        </table>
                    </li>
                    <li>
                        <img src="i/shop/1.jpg" alt="картинка"/>
                        <div class="product-right-descr">
                            100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас
                        </div>
                        <table>
                            <thead>
                            <tr>
                                <td>Цена(шт)</td>
                                <td>Кол-во</td>
                                <td>Сумма</td>
                                <td></td>
                            </tr>
                            </thead>
                            <tr>
                                <td class="td-price">111р.</td>
                                <td><input type="text" class="input-mini spinner1" /></td>
                                <td class="td-summa">111р.</td>
                                <td><a href="#" class="delete-product">Удалить</a></td>
                            </tr>
                        </table>
                    </li>
                    <li>
                        <img src="i/shop/1.jpg" alt="картинка"/>
                        <div class="product-right-descr">
                            100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас
                        </div>
                        <table>
                            <thead>
                            <tr>
                                <td>Цена(шт)</td>
                                <td>Кол-во</td>
                                <td>Сумма</td>
                                <td></td>
                            </tr>
                            </thead>
                            <tr>
                                <td class="td-price">111р.</td>
                                <td><input type="text" class="input-mini spinner1" /></td>
                                <td class="td-summa">111р.</td>
                                <td><a href="#" class="delete-product">Удалить</a></td>
                            </tr>
                        </table>
                    </li>--%>
                </ul>
                <div class="additionally-order">
                    <div class="itogo-right">
                        Товаров на сумму: <span>333</span> руб.
                    </div>
                    <div class="delivery-right">
                        <h3>Доставка</h3>
                        <div class="radio">
                            <label>
                                <input name="form-field-radio" type="radio" checked="checked" class="ace">
                                <span class="lbl"> Самовывоз</span>
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input name="form-field-radio" type="radio" class="ace courier-delivery">
                                <span class="lbl"> Курьер</span>
                            </label>
                        </div>
                        <div class="input-delivery">
                            <input type="tel" placeholder="Номер телефона"/>
                            <span class="lbl"> Адрес доставки</span>
                            <input id="country-delivery" type="text" value="Россия" placeholder="Страна"/>
                            <input id="city-delivery" type="text" value="Санкт-Петербург" placeholder="Город"/>
                            <input id="street-delivery" type="text" placeholder="Улица"/>
                            <input id="building-delivery" type="text" class="short first" placeholder="Дом"/>
                            <input id="flat-delivery" type="text" class="short" placeholder="Квартира"/>
                        </div>
                        <div class="alert-delivery">Введите адрес доставки !</div>
                    </div>
                    <textarea name="order-comment" id="order-comment" placeholder="Комментарий к заказу"></textarea>
                    <button class="btn btn-sm btn-grey no-border">Отменить</button>
                    <button class="btn btn-sm btn-primary no-border btn-order">Заказать</button>
                </div>
                <div class="empty-basket">
                    Ваша корзина пуста
                </div>
            </aside>
            <div class="main-content">
                <div class="shop-products">
                    <nav class="breadcrambs">
                        <a href="#">Главная</a><span> > </span>
                        <a href="#">Товары</a><span> > </span>
                        Я здесь
                    </nav>
                    <form method="post" action="#" class="form-group has-info">
                        <span class="block input-icon input-icon-right">
                            <input id="search" type="text" class="form-control width-100" value="Поиск" onblur="if(this.value=='') this.value='Поиск';" onfocus="if(this.value=='Поиск') this.value='';"/>
                            <a href="#" class="icon-search icon-on-right bigger-110"></a>
                        </span>
                    </form>
                    <nav class="shop-menu">
                        <ul>
                            <c:if test="${innerCategoryFlag}">
                                <li>
                                    <a href="#" class="fa fa-reply-all"></a>
                                    <div>Назад</div>
                                </li>
                            </c:if>
                            <c:forEach var="productCategory" items="${productCategories}">
                                <li data-parentid="${productCategory.parentId}" data-catid="${productCategory.id}">
                                    <a href="#" class="fa fa-beer"></a>
                                    <div>${productCategory.name}</div>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                    <section class="catalog">
                        <table>
                            <thead>
                            <tr>
                                <td>Название</td>
                                <td>Цена</td>
                                <td>Количество</td>
                                <td></td>
                            </tr>
                            </thead>
                            <c:forEach var="product" items="${products}">
                                <tr data-productid="${product.id}">
                                    <td>
                                        <a href="#" class="product-link">
                                            <img src="${product.imageURL}" alt="картинка"/>
                                            <span>
                                            <span>${product.name}</span>
                                            ${product.shortDescr}
                                            </span>
                                        </a>
                                        <div class="modal">
                                            <div class="modal-body">
                                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                                <div class="product-slider">
                                                    <div class="slider flexslider">
                                                        <ul class="slides">
                                                            <li>
                                                                <img src="${product.imageURL}" />
                                                            </li>
                                                            &lt;%&ndash;<li>
                                                                <img src="i/shop/2.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/3.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/4.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/5.jpg" />
                                                            </li>&ndash;%&gt;
                                                            <!-- items mirrored twice, total of 12 -->
                                                        </ul>
                                                    </div>
                                                    <div class="carousel flexslider">
                                                        <ul class="slides">
                                                            <li>
                                                                <img src="${product.imageURL}" />
                                                            </li>
                                                            &lt;%&ndash;<li>
                                                                <img src="i/shop/2.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/3.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/4.jpg" />
                                                            </li>
                                                            <li>
                                                                <img src="i/shop/5.jpg" />
                                                            </li>&ndash;%&gt;
                                                            <!-- items mirrored twice, total of 12 -->
                                                        </ul>
                                                    </div>
                                                </div>
                                                <div class="product-descr">
                                                    <h3>${product.name}</h3>
                                                    <div class="product-text">
                                                        ${productDetails.fullDescr}
                                                    </div>
                                                    <div class="modal-footer">
                                                        <span>Цена: ${product.price}</span>
                                                        <input type="text" class="input-mini spinner1" />
                                                        ${productDetails.unitName}
                                                        <i class="fa fa-shopping-cart"></i>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="product-price">${product.price}</td>
                                    <td>
                                        <input type="text" class="input-mini spinner1" />
                                        ${productDetails.unitName}
                                    </td>
                                    <td>
                                        <i class="fa fa-shopping-cart"></i>
                                    </td>
                                </tr>
                            </c:forEach>

                        </table>
                    </section>
                </div>
                <div class="shop-orders">

                    <a href="#" class="back-to-shop shop-trigger">Вернуться в магазин</a>
                    <h1>Заказы</h1>
                    <%--<div class="order-item">
                        <table>
                            <thead>
                            <tr>
                                <td>Номер заказа</td>
                                <td>Дата</td>
                                <td>Статус заказа</td>
                                <td>Доставка</td>
                                <td>Кол-во продуктов</td>
                                <td>Сумма</td>
                                <td></td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>N 1</td>
                                <td>13.02.2014</td>
                                <td>Выполнен</td>
                                <td>Самовывоз</td>
                                <td>10</td>
                                <td>1000 р.</td>
                                <td><a class="fa fa-plus plus-minus" href="#"></a></td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="order-products">
                            <section class="catalog">
                                <table>
                                    <thead>
                                    <tr>
                                        <td>Название</td>
                                        <td>Цена</td>
                                        <td>Количество</td>
                                        <td></td>
                                    </tr>
                                    </thead>
                                    <c:forEach var="product" items="${products}">
                                        <tr data-productid="${product.id}">
                                            <td>
                                                <a href="#" class="product-link">
                                                    <img src="${product.imageURL}" alt="картинка"/>
                                            <span>
                                            ${product.name} <br>
                                            ${product.shortDescr}
                                            </span>
                                                </a>
                                                <div class="modal">
                                                    <div class="modal-body">
                                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                                        <div class="product-slider">
                                                            <div class="slider flexslider">
                                                                <ul class="slides">
                                                                    <li>
                                                                        <img src="${product.imageURL}" />
                                                                    </li>
                                                                    &lt;%&ndash;<li>
                                                                    <img src="i/shop/2.jpg" />
                                                                </li>
                                                                    <li>
                                                                        <img src="i/shop/3.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/4.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/5.jpg" />
                                                                    </li>&ndash;%&gt;
                                                                    <!-- items mirrored twice, total of 12 -->
                                                                </ul>
                                                            </div>
                                                            <div class="carousel flexslider">
                                                                <ul class="slides">
                                                                    <li>
                                                                        <img src="${product.imageURL}" />
                                                                    </li>
                                                                    &lt;%&ndash;<li>
                                                                    <img src="i/shop/2.jpg" />
                                                                </li>
                                                                    <li>
                                                                        <img src="i/shop/3.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/4.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/5.jpg" />
                                                                    </li>&ndash;%&gt;
                                                                    <!-- items mirrored twice, total of 12 -->
                                                                </ul>
                                                            </div>
                                                        </div>
                                                        <div class="product-descr">
                                                            <h3>${product.name}</h3>
                                                            <div class="product-text">
                                                                    ${productDetails.fullDescr}
                                                            </div>
                                                            <div class="modal-footer">
                                                                <span>Цена: ${product.price}</span>
                                                                <input type="text" class="input-mini spinner1" />
                                                                    ${productDetails.unitName}
                                                                <i class="fa fa-shopping-cart"></i>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="product-price">${product.price}</td>
                                            <td>
                                                <input type="text" class="input-mini spinner1" />
                                                    ${productDetails.unitName}
                                            </td>
                                            <td>
                                                <i class="fa fa-shopping-cart"></i>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                </table>
                            </section>
                        </div>
                    </div>
                    <div class="order-item">
                        <table>
                            <thead>
                            <tr>
                                <td>Номер заказа</td>
                                <td>Дата</td>
                                <td>Статус заказа</td>
                                <td>Доставка</td>
                                <td>Кол-во продуктов</td>
                                <td>Сумма</td>
                                <td></td>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>N 1</td>
                                <td>13.02.2014</td>
                                <td>Выполнен</td>
                                <td>Самовывоз</td>
                                <td>10</td>
                                <td>1000 р.</td>
                                <td><a class="fa fa-plus plus-minus" href="#"></a></td>
                            </tr>
                            </tbody>
                        </table>
                        <div class="order-products">
                            <section class="catalog">
                                <table>
                                    <thead>
                                    <tr>
                                        <td>Название</td>
                                        <td>Цена</td>
                                        <td>Количество</td>
                                        <td></td>
                                    </tr>
                                    </thead>
                                    <c:forEach var="product" items="${products}">
                                        <tr data-productid="${product.id}">
                                            <td>
                                                <a href="#" class="product-link">
                                                    <img src="${product.imageURL}" alt="картинка"/>
                                            <span>
                                            ${product.name} <br>
                                            ${product.shortDescr}
                                            </span>
                                                </a>
                                                <div class="modal">
                                                    <div class="modal-body">
                                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                                        <div class="product-slider">
                                                            <div class="slider flexslider">
                                                                <ul class="slides">
                                                                    <li>
                                                                        <img src="${product.imageURL}" />
                                                                    </li>
                                                                    &lt;%&ndash;<li>
                                                                    <img src="i/shop/2.jpg" />
                                                                </li>
                                                                    <li>
                                                                        <img src="i/shop/3.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/4.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/5.jpg" />
                                                                    </li>&ndash;%&gt;
                                                                    <!-- items mirrored twice, total of 12 -->
                                                                </ul>
                                                            </div>
                                                            <div class="carousel flexslider">
                                                                <ul class="slides">
                                                                    <li>
                                                                        <img src="${product.imageURL}" />
                                                                    </li>
                                                                    &lt;%&ndash;<li>
                                                                    <img src="i/shop/2.jpg" />
                                                                </li>
                                                                    <li>
                                                                        <img src="i/shop/3.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/4.jpg" />
                                                                    </li>
                                                                    <li>
                                                                        <img src="i/shop/5.jpg" />
                                                                    </li>&ndash;%&gt;
                                                                    <!-- items mirrored twice, total of 12 -->
                                                                </ul>
                                                            </div>
                                                        </div>
                                                        <div class="product-descr">
                                                            <h3>${product.name}</h3>
                                                            <div class="product-text">
                                                                    ${productDetails.fullDescr}
                                                            </div>
                                                            <div class="modal-footer">
                                                                <span>Цена: ${product.price}</span>
                                                                <input type="text" class="input-mini spinner1" />
                                                                    ${productDetails.unitName}
                                                                <i class="fa fa-shopping-cart"></i>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="product-price">${product.price}</td>
                                            <td>
                                                <input type="text" class="input-mini spinner1" />
                                                    ${productDetails.unitName}
                                            </td>
                                            <td>
                                                <i class="fa fa-shopping-cart"></i>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                </table>
                            </section>
                        </div>
                    </div>--%>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>

    <div class="modal modal-order-end">
        <div class="modal-body">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h3>Ваш заказ : </h3>
        <section class="catalog">
            <table>
                <thead>
                <tr>
                    <td>Название</td>
                    <td>Цена</td>
                    <td>Количество</td>
                    <td>Сумма</td>
                </tr>
                </thead>
            </table>
            <table class="modal-body-list">
                <tr>
                    <td>
                        <a href="#" class="product-link">
                            <img src="i/shop/1.jpg" alt="картинка"/>
                            <span>100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас</span>
                        </a>
                    </td>
                    <td class="td-price">111р</td>
                    <td>
                        <input type="text" class="input-mini spinner1" />
                    </td>
                    <td class="td-summa">111р</td>
                </tr>
                <tr>
                    <td>
                        <a href="#" class="product-link">
                            <img src="i/shop/1.jpg" alt="картинка"/>
                            <span>100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас</span>
                        </a>
                    </td>
                    <td class="td-price">111р.</td>
                    <td>
                        <input type="text" class="input-mini spinner1" />
                    </td>
                    <td class="td-summa">111р</td>
                </tr>
                <tr>
                    <td>
                        <a href="#" class="product-link">
                            <img src="i/shop/1.jpg" alt="картинка"/>
                            <span>100г Вкусный обед рагу с индейкой и кроликом Whiskas Вискас</span>
                        </a>
                    </td>
                    <td class="td-price">111р.</td>
                    <td>
                        <input type="text" class="input-mini spinner1" />
                    </td>
                    <td class="td-summa">111р</td>
                </tr>
            </table>
        </section>
        </div>
        <div class="modal-footer">
            <div class="modal-itogo">
                Итого : <span>333</span> р.
            </div>
            <button class="btn btn-sm btn-primary no-border btn-order">Заказать</button>
            <button class="btn btn-sm btn-grey no-border">Отменить</button>
        </div>
    </div>

    <div class="modal modal-auth">
        <div class="login-forms">
            <form action="#" class="registration-form login-form">
                <h1>Вход</h1>
                <div class="reg-now">
                    <br>
                    <div>
                        <label for="uname">E-mail</label>
                        <input type="text" id="uname"/>
                    </div>
                    <div>
                        <label for="password">Пароль</label>
                        <input type="password" id="password"/>
                        <a href="#" class="remember-link">Забыли пароль ?</a>
                    </div>
                    <button id="go" class="btn-submit btn-sm no-border">Войти</button>
                </div>
            </form>
            <form action="#" class="registration-form reg-form">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h1>Регистрация</h1>
                <div class="reg-now">
                    <br>
                    <div>
                        <label for="login">Логин</label>
                        <input type="text" id="login"/>
                    </div>
                    <div>
                        <label for="email">E-mail</label>
                        <input type="email" required="required" id="email"/>
                    </div>
                    <div>
                        <label for="pass">Пароль</label>
                        <input type="password" id="pass"/>
                    </div>
                    <span class="email-alert">Такой e-mail уже зарегистрирован !</span>
                    <button class="btn-submit btn-sm no-border">Регистрация</button>
                </div>
            </form>
        </div>
    </div>

</div>
<!-- общие библиотеки -->
<script src="js/jquery-2.0.3.min.js"></script>
<script src="js/bootstrap.min.js"></script>

<!-- конкретные плагины -->
<script src="js/jquery-ui-1.10.3.full.min.js"></script>
<script src="js/fuelux/fuelux.spinner.min.js"></script>
<script src="js/date-time/bootstrap-datepicker.min.js"></script>
<script src="js/date-time/locales/bootstrap-datepicker.ru.js"></script>
<script src="js/jquery.flexslider-min.js"></script>

<!-- -->
<script src="js/ace-extra.min.js"></script>
<script src="js/ace-elements.min.js"></script>
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
<!-- собственные скрипты  -->
<script src="js/login.js"></script>
<script src="js/common.js"></script>
<script src="js/shop.js"></script>

</body>
</html>