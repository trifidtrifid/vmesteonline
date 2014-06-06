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
        UserShopRole userRole = shopService.getUserShopRole(shop.id);
        pageContext.setAttribute("logoURL", shop.logoURL);
        pageContext.setAttribute("shopID", shop.id);
        pageContext.setAttribute("userRole", userRole);
        //out.print(userRole);
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


    <%--<script src="/build/thrift.min.js" type="text/javascript"></script>
    <script src="/build/gen-js/bedata_types.js" type="text/javascript"></script>

    <script src="/build/gen-js/shop_types.js" type="text/javascript"></script>
    <script src="/build/gen-js/ShopFEService.js" type="text/javascript"></script>
    <script src="/build/gen-js/shop.bo_types.js" type="text/javascript"></script>
    <script src="/build/gen-js/ShopBOService.js" type="text/javascript"></script>

    <script src="/build/gen-js/authservice_types.js" type="text/javascript"></script>
    <script src="/build/gen-js/AuthService.js" type="text/javascript"></script>
    <script src="/build/gen-js/userservice_types.js" type="text/javascript"></script>
    <script src="/build/gen-js/UserService.js" type="text/javascript"></script>--%>

    <%--<script type="text/javascript" data-main="/build/shop.min.js" src="/js/require.min.js"></script>--%>
    <script type="text/javascript" data-main="/build/build.js" src="/js/require.min.js"></script>

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


                        <li><a class="btn btn-info no-border bo-link
                        <c:if test="${userRole != 'BACKOFFICER' && userRole != 'ADMIN'}">
                        hidden
                        </c:if>
                        " href="backoffice.jsp">
                            Админка</a></li>

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
            <h1>Магазин название</h1>
            <p>о магазине</p>
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
    <footer>
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

    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-51489479-1', 'vomoloko.ru');
        ga('send', 'pageview');

    </script>

</body>
</html>