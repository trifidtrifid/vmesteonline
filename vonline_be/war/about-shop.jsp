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
    HttpServletRequest httpReq = (HttpServletRequest)request;
    /*String url1 = httpReq.getContextPath();
    String url2 = httpReq.getRequestURI();*/
    String url = httpReq.getPathInfo();
    //out.println(url);

    if(url.length() == 17){
        char buf[] = new char[16];
        url.getChars(1, 17, buf, 0);
        String shopIdStr = "";
        for (int i = 0; i <= 15; i++) {
            shopIdStr = shopIdStr+buf[i];
        }

        Long shopId = new Long(shopIdStr);
        //out.println(shopId);

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

        try{
            Shop shop = shopService.getShop(shopId);
            UserShopRole userRole = shopService.getUserShopRole(shop.id);
            pageContext.setAttribute("logoURL", shop.logoURL);
            pageContext.setAttribute("shopID", shop.id);
            pageContext.setAttribute("userRole", userRole);
            pageContext.setAttribute("shop", shop);
        }catch(InvalidOperation ioe){

        }
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Магазин</title>
<link rel="stylesheet" href="../build/shop.min.css" />
<!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->

    <script type="text/javascript" data-main="../build/build.js" src="../js/shop/require.min.js"></script>

    <script type="text/javascript">
	globalUserAuth = false;
	<c:if test="${auth}">
	globalUserAuth = true;
	</c:if>
</script>
</head>
<body class="page-about-shop">

    <div class="wrap">
	<div class="main container">
        <%@ include file="templates/header.jsp" %>

		<div class="main-container shop dynamic" id="${shopID}">
            <div class="page shop-about">
                <div class="col-xs-4">
                </div>
                <div class="col-xs-8">
                    <h1>Магазин "<c:out value="${shop.name}" />"</h1>
                </div>
                <div class="col-xs-4">
                    <img src="<c:out value="${logoURL}" />" alt="лого">
                </div>
                <div class="col-xs-8">
                    <p><c:out value="${shop.descr}" /></p>
                    <a class="btn btn-primary no-border btn-sm" href="/shop/<c:out value="${shopID}"/>">Продолжить покупки</a>
                </div>
            </div>
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
                    <div class="shop-products"></div>
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
            <div class="page shop-terms-of-orders"></div>
            <div class="page shop-terms-of-delivery"></div>
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

        <%@ include file="templates/footer.jsp" %>

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