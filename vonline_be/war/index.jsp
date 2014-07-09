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

    ShopServiceImpl shopService = new ShopServiceImpl(sess.getId());

    List<Shop> ArrayShops = shopService.getShops();
    int ArrayShopsLength = ArrayShops.size();
    Shop[] activeShops = new Shop[ArrayShopsLength];
    Shop[] noActiveShops = new Shop[ArrayShopsLength];
    int activeShopsCounter = 0;
    int noActiveShopsCounter = 0;

    for (int i = 0; i < ArrayShopsLength; i++) {
        if(shopService.isActivated(ArrayShops.get(i).id)){
            activeShops[activeShopsCounter] = ArrayShops.get(i);
            activeShopsCounter++;
        }else{
            noActiveShops[noActiveShopsCounter] = ArrayShops.get(i);
            noActiveShopsCounter++;
        }
    }
    pageContext.setAttribute("activeShops", activeShops);
    pageContext.setAttribute("noActiveShops", noActiveShops);

        if(ArrayShops != null && ArrayShops.size() > 0){
        pageContext.setAttribute("shops", ArrayShops);
        }
    pageContext.setAttribute("auth",true);
    try {

        UserServiceImpl userService = new UserServiceImpl(sess);
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
%>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>Во Маркет !</title>
<meta name="description" content="Simple Responsive Template is a template for responsive web design. Mobile first, responsive grid layout, toggle menu, navigation bar with unlimited drop downs, responsive slideshow">
<meta name="keywords" content="">

<!-- Mobile viewport -->
<meta name="viewport" content="width=device-width; initial-scale=1.0">

<link rel="shortcut icon" href="images/favicon.ico"  type="image/x-icon" />

<!-- CSS-->
<!-- Google web fonts. You can get your own bundle at http://www.google.com/fonts. Don't forget to update the CSS accordingly!-->
<link href='http://fonts.googleapis.com/css?family=Droid+Serif|Ubuntu' rel='stylesheet' type='text/css'>

<link rel="stylesheet" href="css/landing/normalize.css">
<link rel="stylesheet" href="js/shop/landing/flexslider/flexslider.css" />

<link rel="stylesheet" href="css/landing/basic-style.css">
<%--<link rel="stylesheet" href="css/login.css">--%>

<!-- end CSS-->
    
<!-- JS-->
<script src="js/shop/landing/libs/modernizr-2.6.2.min.js"></script>
<!-- end JS-->


<!-- columns demo style. DELETE IT! -->
<style type="text/css">
<!--

#columnsdemo .grid_1,
#columnsdemo .grid_2,
#columnsdemo .grid_3,
#columnsdemo .grid_4,
#columnsdemo .grid_5,
#columnsdemo .grid_6,
#columnsdemo .grid_7,
#columnsdemo .grid_8,
#columnsdemo .grid_9,
#columnsdemo .grid_10,
#columnsdemo .grid_11,
#columnsdemo .grid_12 {
border: solid 1px #999;
color:#999;
text-align: center;
margin-top:20px;
padding:20px;
}
-->
</style>

    <script type="text/javascript">
        globalUserAuth = false;
        <c:if test="${auth}">
        globalUserAuth = true;
        </c:if>
    </script>
</head>

<body id="home" class="shop-landing <c:if test="${auth}">user-is-auth</c:if>">
  
    <header>
        <div class="wrapper clearfix">

            <div id="banner">
                <div id="logo"><a href="/">Во!Маркет</a></div>
            </div>

            <nav id="topnav" role="navigation">
                <%--<a href="#" class="landing-login">Войти</a>
                <a href="#" class="for-auth-user landing-logout">Выйти</a>--%>

                <div class="navbar-header pull-right" role="navigation">
                        <ul class="nav ace-nav">
                            <li class="user-short">
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
                                        <a data-toggle="dropdown" href="#" class="dropdown-toggle no-login landing-login">
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

                                    <li><a href="#" class="landing-logout"> <i class="icon-off"></i> Выход
                                    </a></li>
                                </ul>
                            </li>
                        </ul>
                        <!-- /.ace-nav -->
                    </div>

            </nav>
        </div>
  
    </header>
 
    <div class="page landing-page">
    <section id="hero" class="clearfix">
    <div class="wrapper">
        <div class="grid_5 alpha">
                <h1>Самые удобные магазины на планете</h1>
            <p>
            ...is a template for responsive web design. A small set of tools and best practices that allows web designers to build responsive websites faster. Websites built with Simple Responsive Template will be optimized for screen widths between 320px and anything. Resize your browser to check it out.
            </p>
            <p class="align-center"><a href="#" class="buttonlink">Далее</a></p>
        </div>
        <div class="grid_7 omega rightfloat">
                <div class="flexslider">
                    <ul class="slides">
                        <li>
                            <img src="i/landing/slider1.jpg" />
                            <p class="flex-caption">Love Brazil !!! Sea view from Rio de Janeiro fort.</p>
                        </li>
                        <li>
                            <img src="i/landing/slider2.jpg" />
                            <p class="flex-caption">El Arco Cabo Mexico. This image is wrapped in a link.</p>
                        </li>
                        <li>
                            <img src="i/landing/slider3.jpg" />
                            <p class="flex-caption">Arches National Park, Utah, Usa.</p>
                        </li>
                        <li>
                            <img src="i/landing/slider4.jpg" />
                            <p class="flex-caption">Arches National Park, Utah, Usa.</p>
                        </li>
                    </ul>
                  </div>
                </div><!-- FlexSlider -->
        </div>
    </section>

    <div id="main" class="wrapper">


    <!-- content area -->
        <section id="content" class="wide-content">
            <div class="grid_4">
                <h1 class="first-header">Качество!</h1>
                <img src="i/landing/quality.jpg" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
            </div>

            <div class="grid_4">
                <h1 class="first-header">Свежесть!</h1>
                <img src="i/landing/fresh.jpg" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
            </div>

            <div class="grid_4">
                <h1 class="first-header">Удобство !</h1>
                <img src="i/landing/usability.jpg" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
            </div>

        </section><!-- #end content area -->


        <!-- columns demo, delete it!-->
        <section id="columnsdemo" style="margin-bottom:60px; width:100%" class="clearfix">
            <div class="clearfix"></div>
            <h2 class="for-auth-user">Спасибо, что выбираете нас !</h2>

            <div class="grid_6 shops for-auth-user no-left-margin">
                <div>Для совершения покупок, выберете магазин из списка:</div>
            </div>
            <div class="grid_6 shops for-auth-user bigger-left-margin">
                <div>Какой следующий магазин должен быть подключен?</div>
            </div>

            <div class="grid_6 shops active-shops no-left-margin">

                <div class="shops-block-title">Подключенные магазины</div>
                <ul>
                    <c:forEach var="shop" items="${activeShops}">
                        <c:if test="${shop.id != null}">
                        <li>
                            <a href="shop/${shop.id}">
                                <img src="${shop.logoURL}" alt="логотип"/>
                            <span class="landing-shop-right">
                                <h3>${shop.name}</h3>
                                <p>${shop.descr}</p>
                            </span>
                            </a>
                            <div>
                                <span class="voice-counter"></span>
                                <a class="buttonlink" href="shop/${shop.id}">Перейти в магазин</a>
                            </div>
                        </li>
                        </c:if>
                    </c:forEach>

                    <%--<li>
                        <a href="#">
                            <img src="i/landing/basic-pic3.jpg" alt="картинка"/>
                            <span class="shop-right">
                                <h3>ВоМолоко</h3>
                                <p>Магазинкачественной молочной продукции из столицы российского сельского хозяйства Вологды</p>
                            </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <img src="i/landing/basic-pic3.jpg" alt="картинка"/>
                            <span class="shop-right">
                                <h3>ВоМолоко</h3>
                                <p>Магазинкачественной молочной продукции из столицы российского сельского хозяйства Вологды</p>
                            </span>
                        </a>
                    </li>--%>
                </ul>
            </div>
            <div class="grid_6 shops no-active-shops bigger-left-margin">
                <div class="shops-block-title">В очереди</div>
                <ul>
                    <c:forEach var="shop" items="${noActiveShops}">
                        <c:if test="${shop.id != null}">
                            <li id="${shop.id}">
                                <a href="shop/${shop.id}">
                                    <img src="${shop.logoURL}" alt="логотип"/>
                                <span class="landing-shop-right">
                                    <h3>${shop.name}</h3>
                                    <p>${shop.descr}</p>
                                </span>
                                </a>
                                <div>
                                    <span class="voice-counter"></span>
                                    <a class="buttonlink vote-btn" href="#">Голосовать</a>
                                    <span class="error-info"></span>
                                </div>
                            </li>
                        </c:if>
                    </c:forEach>
                    <%--<li>
                        <a href="#">
                            <img src="i/landing/basic-pic3.jpg" alt="картинка"/>
                            <span class="landing-shop-right">
                                <h3>ВоМолоко</h3>
                                <p>Магазинкачественной молочной продукции из столицы российского сельского хозяйства Вологды</p>
                            </span>
                        </a>
                        <div>
                            <span class="voice-counter">35</span>
                            <a class="buttonlink vote-btn" href="#">Голосовать</a>
                        </div>
                    </li>--%>
                </ul>
            </div>
        </section>
        <!-- end columns demo -->


      </div>
 </div>

    <div class="page shop-profile wrapper"></div>
    <div class="page shop-editPersonal wrapper"></div>

<footer>
	<div id="colophon" class="wrapper clearfix">
    	(c) Во!Маркет Санкт-Петербург, 2014
        <div>email: info@vomarket.ru</div>
    </div>

    
</footer>



    <div class="modal modal-auth">
    </div>


<!-- jQuery -->
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/shop/landing/libs/jquery-1.9.0.min.js">\x3C/script>')</script>

<script defer src="js/shop/landing/flexslider/jquery.flexslider-min.js"></script>

    <script src="/build/thrift.min.js" type="text/javascript"></script>

    <script src="/build/gen-js/bedata_types.js" type="text/javascript"></script>

    <script src="/build/gen-js/shop_types.js" type="text/javascript"></script>
    <script src="/build/gen-js/ShopFEService.js" type="text/javascript"></script>

<!-- fire ups - read this file!  -->
<%--<script type="text/javascript" data-main="/js/shop/landing/main.js" src="/js/shop/require.min.js"></script>--%>
<script src="js/shop/landing/main.js"></script>

<script type="text/javascript" data-main="/build/build.js" src="/js/shop/require.min.js"></script>

</body>
</html>