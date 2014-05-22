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
        //AuthServiceImpl.checkIfAuthorised(sess.getId());
        UserServiceImpl userService = new UserServiceImpl(request.getSession());
        ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
        if( null == ShortUserInfo){
        	sess.invalidate();
        	throw new InvalidOperation( com.vmesteonline.be.VoError.NotAuthorized, "");
        }
        pageContext.setAttribute("firstName",ShortUserInfo.firstName);
        pageContext.setAttribute("lastName",ShortUserInfo.lastName);
    } catch (InvalidOperation ioe) {
        pageContext.setAttribute("auth",false);
    }


    ShopServiceImpl shopService = new ShopServiceImpl(request.getSession().getId());

    List<Shop> ArrayShops = shopService.getShops();
    if(ArrayShops != null && ArrayShops.size() > 0){
        Shop shop = shopService.getShop(ArrayShops.get(0).id);
        pageContext.setAttribute("logoURL", shop.logoURL);
        pageContext.setAttribute("shopID", shop.id);
    }

    OrderDetails currentOrderDetails;
    try{
        Order order = shopService.getOrder(0);
        currentOrderDetails = shopService.getOrderDetails(order.id);
        List<OrderLine> orderLines = currentOrderDetails.odrerLines;
        pageContext.setAttribute("orderLines", orderLines);
    } catch(InvalidOperation ioe){
        currentOrderDetails = null;
        //out.print('2');
    }

    List<ProductCategory> ArrayProductCategory = shopService.getProductCategories(0);
    ProductListPart productsListPart = shopService.getProducts(0,1000,0);
    if (productsListPart.products.size() > 0){
        pageContext.setAttribute("products",productsListPart.products);
    }
    if(ArrayProductCategory.size() > 0){
        pageContext.setAttribute("productCategories", ArrayProductCategory);
    }

    List<Producer> producersList = shopService.getProducers();
    pageContext.setAttribute("producersList", producersList);

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Магазин</title>
<link rel="stylesheet" href="build/shop.min.css" />
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
    <div class="wrap">
	<div class="main container">
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

						<li class="active back-to-shop shop-trigger"><a class="btn btn-info no-border" href="shop.jsp">
								Магазин </a></li>
                        <li><a class="btn btn-info no-border go-to-orders shop-trigger" href="#">
                            Заказы </a></li>
						<li class="user-short light-blue">
                            <c:choose>
								<c:when test="${auth}">
									<a data-toggle="dropdown" href="#" class="dropdown-toggle">
										<%--<img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />--%>
                                        <span class="user-info">
                                            <c:out value="${firstName}" />
                                            <c:out value="${lastName}" />
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
				<!-- /.navbar-header -->
			</div>
			<!-- /.container -->
		</div>
		<div class="main-container shop dynamic" id="${shopID}">
            <div class="page main-container-inner">

                <div class="show-right">
                    Заказы
                </div>
                <aside class="sidebar shop-right">
                    <div class="hide-right">×</div>
                    <div class="sidebar-title">
                    </div>

                    <div class="modal-backdrop basket-backdrop"></div>
                </aside>
                <div class="main-content">
                    <div class="shop-products">
                        <div class="btn-group producer-dropdown">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border" data-producerid="0">
                                <span class="btn-group-text">Поиск по производителю</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <c:forEach var="producer" items="${producersList}">
                                    <li data-producerid="${producer.id}"><a href="#">${producer.name}</a></li>
                                </c:forEach>
                                <li class="divider"></li>
                                <li data-producerid="0"><a href="#">Все производители</a></li>
                            </ul>
                        </div>
                        <form method="post" action="#" class="form-group has-info form-search">
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
                                        <a href="#" class="btn btn-app btn-info btn-sm">
                                            <i class="fa fa-beer"></i>
                                            <span>${productCategory.name}</span>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </nav>
                        <section class="catalog-head">
                            <table>
                                <tr>
                                    <td>Название</td>
                                    <td class="td-producer">Производитель</td>
                                    <td class="product-price">Цена (руб)</td>
                                    <td class="td-spinner">Количество</td>
                                    <td class="td-unit">Ед.изм</td>
                                    <td class="td-basket"></td>
                                </tr>
                            </table>
                        </section>
                        <section class="catalog">
                            <table>
                                <c:forEach var="product" items="${products}">
                                    <tr data-productid="${product.id}" data-prepack="${product.prepackRequired}" class="product">
                                        <td>
                                            <a href="#" class="product-link">
                                                <div class="product-pic">
                                                <c:choose>
                                                    <c:when test="${product.imageURL != null}">
                                                        <img src="${product.imageURL}?w=40&h=40" alt="${product.name}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="i/no-photo.png" alt="нет фото"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                </div>

                                                <span>
                                                <span class="product-name">${product.name}</span>
                                                ${product.shortDescr}
                                                </span>
                                            </a>
                                            <div class="modal">
                                            </div>
                                        </td>
                                        <td class="td-producer" data-producerid="${product.producerId}">
                                            <c:forEach var="producer" items="${producersList}">
                                                <c:if test="${producer.id == product.producerId}">
                                                    ${producer.name}
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                        <td class="product-price">${product.price}</td>
                                        <td class="td-spinner">
                                            <input type="text" class="input-mini spinner1" data-step="${product.minClientPack}" />
                                            <span class="added-text">добавлен</span>
                                        </td>
                                        <td class="td-unit">
                                            <span class="unit-name">${product.unitName}</span>
                                        </td>
                                        <td class="td-basket">
                                            <a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>
                                            <span href="#" title="Продукт уже у вас в корзине" class="fa fa-check"></span>
                                        </td>
                                    </tr>
                                </c:forEach>

                            </table>
                        </section>
                    </div>
                    <div class="shop-orders">

                        <h1>Заказы</h1>
                        <div class="orders-tbl-wrap">
                        <table>
                            <tr>
                                <td class="td1"></td>
                                <td class="td2">N</td>
                                <td class="td3">Дата</td>
                                <td class="td4">Статус</td>
                                <td class="td9">Цена доставки</td>
                                <td class="td8">Вес(кг)</td>
                                <td class="td6">Сумма</td>
                            </tr>
                        </table>
                        </div>
                        <div class="orders-list">
                        </div>
                    </div>

                </div>
                <div class="clear"></div>
            </div>
            <div class="page shop-confirm"></div>
            <div class="page shop-profile"></div>
            <div class="page shop-orderEnd"></div>
            <div class="page shop-editPersonal"></div>
        </div>

        <div class="modal modal-error">
            <div class="modal-body">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <p>Произошла ошибка работы приложения. Наши программисты уже получили всю необходимую информацию.
                Приносим извинения за доставленные неудобства.</p>
                <p>Для продолжения работы перезагрузите страницу.</p>
                <div class="details-block">
                    <a href="#" class="error-details-link no-init">Детали</a>
                    <div id="error-details" class="error-info"></div>
                </div>
            </div>
        </div>

		<div class="modal modal-auth">
		</div>

	</div>
    <footer class="short-footer">
        <div class="container">
            <div class="footer-menu">
                <ul>
                    <li><a href="#">О сайте</a></li>
                    <li><a href="#">Правила</a></li>
                    <li><a href="#">Контакты</a></li>
                    <li><a href="#">В начало</a></li>
                </ul>
            </div>
            <div>Вместе Онлайн (c) 2014</div>
        </div>
    </footer>
    </div>
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

    <script type="text/javascript" data-main="/build/shop.min.js" src="/js/require.min.js"></script>


</body>
</html>