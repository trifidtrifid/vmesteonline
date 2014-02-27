<%@page import="com.vmesteonline.be.utils.SessionHelper"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.ShopServiceImpl"%>
<%@ page import="com.vmesteonline.be.ServiceImpl"%>
<%@ page import="com.vmesteonline.be.jdo2.VoSession"%>
<%@ page import="com.vmesteonline.be.jdo2.VoFileAccessRecord"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>

<%@ page import="java.nio.Buffer"%>
<%@ page import="java.nio.ByteBuffer"%>
<%@ page import="java.nio.ByteOrder"%>
<%@ page import="java.nio.CharBuffer"%>
<%@ page import="com.vmesteonline.be.shop.*" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%

    ShopServiceImpl shopService = new ShopServiceImpl(request.getSession().getId());

    List<Shop> ArrayShops = shopService.getShops();
    Shop shop = shopService.getShop(ArrayShops.get(0).id);

    List<ProductCategory> ArrayProductCategory = shopService.getProductCategories(0);
    ProductListPart productsListPart = shopService.getProducts(0,10,ArrayProductCategory.get(1).id);
    ProductDetails productDetails = shopService.getProductDetails(productsListPart.products.get(0).id);
    //String productURL = new String( productsListPart.products.get(0).imageURL);

    /*List<Order> ArrayOrders = shopService.getOrders(0,(int)(System.currentTimeMillis()/1000L)+86400*30);
    Order order = shopService.getOrder(ArrayOrders.get(1).id);
    OrderDetails orderDetails = shopService.getOrderDetails(order.id);
    List<OrderLine> orderLineArray= orderDetails.odrerLines;*/

    //out.print(orderDetails.odrerLines.size());
    //out.print(orderDetails.odrerLines.get(0).product.id);
    //out.print(ArrayProductCategory.get(0).logoURLset);
    //out.print(new String( productsListPart.products.get(0).imageURL));
    //out.print(productsListPart.products.size());

    pageContext.setAttribute("productCategories", ArrayProductCategory);
    pageContext.setAttribute("products",productsListPart.products);
    pageContext.setAttribute("productDetails",productDetails);
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
            <li>
                <a class="btn btn-info no-border" href="#">
                    Сообщения
                </a>
            </li>

            <li>
                <a class="btn btn-info no-border" href="#">
                    Архив
                </a>
            </li>

            <li>
                <a class="btn btn-info no-border" href="#">
                    Избранное
                </a>
            </li>
            <li class="active">
                <a class="btn btn-info no-border" href="#">
                    Магазин
                </a>
            </li>
            <li class="user-short light-blue">
                <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                    <img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />
                    <span class="user-info">
                        <small>Welcome,</small>
                        Jason
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
            </li>
        </ul><!-- /.ace-nav -->
    </div><!-- /.navbar-header -->
    </div><!-- /.container -->
    </div>
    <div class="main-container" id="main-container">
        <div class="main-container-inner">
            <aside class="sidebar" id="sidebar">
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
            </aside>
            <aside class="sidebar shop-right">
                <div class="show-right">
                    Заказы
                </div>
                <div class="hide-right">×</div>
                <div class="sidebar-title">Заказы</div>
                <nav>
                    <div class="input-group">
                        <input class="form-control date-picker" id="id-date-picker-1" type="text" data-date-format="dd-mm-yyyy" value="Выберите дату" onblur="if(this.value=='') this.value='Выберите дату';" onfocus="if(this.value=='Выберите дату') this.value='';"/>
                        <span class="input-group-addon">
                            <i class="icon-calendar bigger-110"></i>
                        </span>
                    </div>
                </nav>
                <ul class="catalog-order">
<%--                    <c:forEach var="orderLine" items="${orderLines}">
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
                        <div class="checkbox">
                            <label>
                                <input name="form-field-checkbox" type="checkbox" class="ace">
                                <span class="lbl"> Доставка</span>
                            </label>
                        </div>
                        <div class="input-delivery">
                            <label for="delivery-addr">Введите адрес доставки</label>
                            <textarea id="delivery-addr"></textarea>
                        </div>
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
                        <li>
                            <a href="#" class="fa fa-reply-all"></a>
                            <div>Назад</div>
                        </li>
                        <c:forEach var="productCategory" items="${productCategories}">
                            <li data-catid="${productCategory.id}">
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
                            <tr>
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
                                                    <i class="fa fa-shopping-cart"></i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td class="product-price">${product.price}</td>
                                <td>
                                    <input type="text" class="input-mini spinner1" />
                                </td>
                                <td>
                                    <i class="fa fa-shopping-cart"></i>
                                </td>
                            </tr>
                        </c:forEach>

                    </table>
                </section>

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
<script src="gen-js/shop_types.js" type="text/javascript"></script>
<script src="gen-js/ShopService.js" type="text/javascript"></script>
<!-- -->
<!-- собственные скрипты  -->
<script src="js/common.js"></script>
<script src="js/shop.js"></script>

</body>
</html>