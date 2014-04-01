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

    OrderDetails currentOrderDetails;
    try{
        Order order = shopService.getOrder(0);
        currentOrderDetails = shopService.getOrderDetails(order.id);
        List<OrderLine> orderLines = currentOrderDetails.odrerLines;
        pageContext.setAttribute("orderLines", orderLines);
        //out.print(order.id);
    } catch(InvalidOperation ioe){
        currentOrderDetails = null;
        //out.print('2');
    }

    Cookie cookies [] = request.getCookies();
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
    pageContext.setAttribute("productCategories", ArrayProductCategory);

    //String productURL = new String( productsListPart.products.get(0).imageURL);
    //out.print(ArrayProductCategory.get(1).id);

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Магазин</title>
<link rel="stylesheet" href="css/shop.css" />
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
				try {
					ace.settings.check('navbar', 'fixed')
				} catch (e) {
				}
			</script>

			<div class="navbar-container" id="navbar-container">
				<div class="navbar-header pull-left">
					<a href="#" class="navbar-brand">
                            <img src="<c:out value="${logoURL}" />" alt="лого">
					</a>
					<!-- /.brand -->
				</div>
				<!-- /.navbar-header -->

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
				<!-- /.navbar-header -->
			</div>
			<!-- /.container -->
		</div>
		<div class="main-container shop dynamic" id="main-container">
			<div class="main-container-inner">

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
                        </div>
                    </nav>
                </div>

                <ul class="catalog-order">
                    <c:forEach var="orderLine" items="${orderLines}">
                        <li data-productid="${orderLine.product.id}">
                            <table>
                                <tr>
                                    <td class="td-price product-price">${orderLine.product.price}</td>
                                    <td><input type="text" class="input-mini spinner1" data-step="${orderLine.product.minClientPack}" /><span class="unit-name">${orderLine.product.unitName}</span></td>
                                    <td class="td-summa">${orderLine.price*orderLine.quantity}</td>
                                    <td><a href="#" class="delete-product no-init">×</a></td>
                                </tr>
                            </table>
                            <a href="#" class="product-link no-init">
                                <span><img src="${orderLine.product.imageURL}" alt="картинка"/></span>
                                <div class="product-right-descr"> ${orderLine.product.name}</div>
                            </a>
                            <div class="modal">
                            </div>
                        </li>
                    </c:forEach>
                </ul>
                <div class="additionally-order">
                    <div class="itogo-right">
                        Товаров на сумму: <span></span> руб.
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
                                <span class="lbl"> Курьер </span>
                            </label>
                        </div>
                        <div class="input-delivery">
                            <span class="lbl">Стоимость доставки : <span class="delivery-cost"></span> руб</span>
                            <input id="phone-delivery" type="tel" placeholder="Номер телефона"/>
                            <div class="alert-delivery alert-delivery-phone">Введите номер телефона !</div>
                            <div class="btn-group delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Выбрать адрес</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li class="divider"></li>
                                    <li><a href="#" class="delivery-add-address">Добавить адрес ...</a></li>
                                </ul>
                            </div>
                            <span class="lbl"> Адрес доставки</span>
                            <input id="country-delivery" type="text" value="Россия" placeholder="Страна"/>
                            <input id="city-delivery" type="text" value="Санкт-Петербург" placeholder="Город"/>
                            <input id="street-delivery" type="text" placeholder="Улица"/>
                            <input id="building-delivery" type="text" class="short first" placeholder="Дом"/>
                            <input id="flat-delivery" type="text" class="short" placeholder="Квартира"/>
                        </div>
                        <div class="alert-delivery alert-delivery-addr">Введите адрес доставки !</div>
                    </div>
                    <textarea name="order-comment" id="order-comment" placeholder="Комментарий к заказу"></textarea>
                    <button class="btn btn-sm btn-grey no-border btn-cancel">Отменить</button>
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
                                    <a href="#">
                                    <i class="fa fa-reply-all"></i>
                                    <span>Назад</span>
                                    </a>
                                </li>
                            </c:if>
                            <c:forEach var="productCategory" items="${productCategories}">
                                <li data-parentid="${productCategory.parentId}" data-catid="${productCategory.id}">
                                    <a href="#">
                                        <i class="fa fa-beer"></i>
                                        <span>${productCategory.name}</span>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                    <section class="catalog">
                        <table>
                            <thead>
                            <tr>
                                <td>Название</td>
                                <td>Цена (руб)</td>
                                <td>Количество</td>
                                <td>Ед.изм</td>
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
                                        </div>
                                    </td>
                                    <td class="product-price">${product.price}</td>
                                    <td>
                                        <input type="text" class="input-mini spinner1" data-step="${product.minClientPack}" />
                                    </td>
                                    <td>
                                        <span class="unit-name">${product.unitName}</span>
                                    </td>
                                    <td>
                                        <a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>
                                    </td>
                                </tr>
                            </c:forEach>

                        </table>
                    </section>
                </div>
                <div class="shop-orders">

                    <a href="#" class="back-to-shop shop-trigger">Вернуться в магазин</a>
                    <h1>Заказы</h1>
                    <div class="orders-tbl-wrap">
                    <table>
                        <tr>
                            <td class="td1"></td>
                            <td class="td2">N</td>
                            <td class="td3">Дата</td>
                            <td class="td4">Статус</td>
                            <td class="td5">Доставка</td>
                            <td class="td6">Сумма</td>
                            <td class="td7"></td>
                        </tr>
                    </table>
                    </div>
                    <div class="orders-list">
                    </div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>

		<div class="modal modal-order-end">
			<div class="modal-body">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">×</button>
				<h3>Ваш заказ :</h3>
				<section class="catalog">
					<%--<table>
					</table>--%>
					<table class="modal-body-list">
                        <thead>
                            <tr>
                                <td>Название</td>
                                <td>Цена(руб)</td>
                                <td>Количество</td>
                                <td>Ед.изм</td>
                                <td>Сумма</td>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
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
							<label for="uname">E-mail</label> <input type="text" id="uname" />
						</div>
						<div>
							<label for="password">Пароль</label> <input type="password"
								id="password" /> <a href="#" class="remember-link">Забыли
								пароль ?</a>
                            <div class="error-info login-error">Вы ввели неккоректный e-mail или пароль</div>
                        </div>
						<button id="go" class="btn-submit btn-sm no-border">Войти</button>
					</div>
				</form>
				<form action="#" class="registration-form reg-form">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h1>Регистрация</h1>
					<div class="reg-now">
						<br>
						<div>
							<label for="login">Логин</label> <input type="text" id="login" />
						</div>
						<div>
							<label for="email">E-mail</label> <input type="email"
								required="required" id="email" />
						</div>
						<div>
							<label for="pass">Пароль</label> <input type="password" id="pass" />
						</div>
						<span class="email-alert">Такой e-mail уже зарегистрирован
							!</span>
						<button class="btn-submit btn-sm no-border">Регистрация</button>
					</div>
				</form>
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
	<script src="js/lib/date-time/bootstrap-datepicker.js"></script>
	<script src="js/lib/date-time/bootstrap-datepicker-simple.js"></script>
    <script src="js/lib/date-time/locales/bootstrap-datepicker.ru.js"></script>
	<%--<script src="js/lib/date-time/bootstrap-datepicker.js"></script>--%>

	<script src="js/lib/jquery.flexslider-min.js"></script>

	<!-- -->
	<script src="js/lib/ace-extra.min.js"></script>
	<script src="js/lib/ace-elements.min.js"></script>
	<!-- собственные скрипты  -->
<%--
	<script src="js/login.js"></script>
--%>
	<script src="js/common.js"></script>
	<script src="js/shop.js"></script>

</body>
</html>