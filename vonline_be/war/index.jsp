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
    if(ArrayShops != null && ArrayShops.size() > 0){
        pageContext.setAttribute("shops", ArrayShops);
        //out.print('s');
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
                <div id="logo"><a href="index.jsp">Во!Маркет</a></div>
            </div>

            <nav id="topnav" role="navigation">
                <a href="login.jsp" class="landing-login">Войти</a>
                <%--<div class="menu-toggle">Menu</div>
                    <ul class="srt-menu" id="menu-main-navigation">
                    <li class="current"><a href="index.jsp">Home page</a></li>
                    <li><a href="basic-internal.html">Internal page demo</a></li>
                    <li><a href="#">menu item 3</a>
                        <ul>
                            <li>
                                <a href="#">menu item 3.1</a>
                            </li>
                            <li class="current">
                                <a href="#">menu item 3.2</a>
                                <ul>
                                    <li><a href="#">menu item 3.2.1</a></li>
                                    <li><a href="#">menu item 3.2.2 with longer link name</a></li>
                                    <li><a href="#">menu item 3.2.3</a></li>
                                    <li><a href="#">menu item 3.2.4</a></li>
                                    <li><a href="#">menu item 3.2.5</a></li>
                                </ul>
                            </li>
                            <li><a href="#">menu item 3.3</a></li>
                            <li><a href="#">menu item 3.4</a></li>
                        </ul>
                    </li>
                    <li>
                        <a href="#">menu item 4</a>
                        <ul>
                            <li><a href="#">menu item 4.1</a></li>
                            <li><a href="#">menu item 4.2</a></li>
                        </ul>
                    </li>
                    <li>
                        <a href="#">menu item 5</a>
                    </li>
                </ul> --%>
            </nav>
        </div>
  
    </header>
 
 
<!-- hero area (the grey one with a slider -->
    <section id="hero" class="clearfix">    
    <!-- responsive FlexSlider image slideshow -->
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
                            <img src="i/landing/basic-pic1.jpg" />
                            <p class="flex-caption">Love Brazil !!! Sea view from Rio de Janeiro fort.</p>
                        </li>
                        <li>
                            <a href="http://www.prowebdesign.ro"><img src="i/landing/basic-pic2.jpg" /></a>
                            <p class="flex-caption">El Arco Cabo Mexico. This image is wrapped in a link.</p>
                        </li>
                        <li>
                            <img src="i/landing/basic-pic3.jpg" />
                            <p class="flex-caption">Arches National Park, Utah, Usa.</p>
                        </li>
                    </ul>
                  </div>
                </div><!-- FlexSlider -->
        </div>
    </section><!-- end hero area -->

<!-- main content area -->   
<div id="main" class="wrapper">
    
    
<!-- content area -->    
	<section id="content" class="wide-content">
    	<div class="grid_4">
        	<h1 class="first-header">Brazil!</h1>
            <img src="i/landing/basic-pic1.jpg" />
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
        </div>
        
        <div class="grid_4">
        	<h1 class="first-header">Mexico!</h1>
            <img src="i/landing/basic-pic2.jpg" />
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
        </div>
        
        <div class="grid_4">
        	<h1 class="first-header">US!</h1>
            <img src="i/landing/basic-pic3.jpg" />
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum</p>
        </div>

	</section><!-- #end content area -->   
      
      
    <!-- columns demo, delete it!-->
    <section id="columnsdemo" style="margin-bottom:60px; width:100%" class="clearfix">
        <div class="clearfix"></div>
        <h2 class="for-auth-user">Спасибо, что выбираете нас !</h2>

        <div class="grid_6 shops for-auth-user">
            <div>Для совершения покупок, выберете магазин из списка:</div>
        </div>
        <div class="grid_6 shops for-auth-user">
            <div>Какой следующий магазин должен быть подключен?</div>
        </div>
        
        <div class="grid_6 shops">

            <div class="shops-block-title">Подключенные магазины</div>
            <ul>
                <c:forEach var="shop" items="${shops}">
                    <li>
                        <a href="about/${shop.id}">
                            <img src="${shop.logoURL}" alt="логотип"/>
                        <span class="shop-right">
                            <h3>${shop.name}</h3>
                            <p>${shop.descr}</p>
                        </span>
                        </a>
                    </li>
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
        <div class="grid_6 shops">
            <div class="shops-block-title">В очереди</div>
            <ul>
                <li>
                    <a href="#">
                        <img src="i/landing/basic-pic3.jpg" alt="картинка"/>
                        <span class="shop-right">
                            <h3>ВоМолоко</h3>
                            <p>Магазинкачественной молочной продукции из столицы российского сельского хозяйства Вологды</p>
                        </span>
                    </a>
                    <div>
                        <span class="voice-counter">35</span>
                        <a class="btn buttonlink" href="#">Голосовать</a>
                    </div>
                </li>
                <li>
                    <a href="#">
                        <img src="i/landing/basic-pic3.jpg" alt="картинка"/>
                        <span class="shop-right">
                            <h3>ВоМолоко</h3>
                            <p>Магазинкачественной молочной продукции из столицы российского сельского хозяйства Вологды</p>
                        </span>
                    </a>
                    <div>
                        <span class="voice-counter">35</span>
                        <a class="btn buttonlink" href="#">Голосовать</a>
                    </div>
                </li>
            </ul>
        </div>
    </section>
    <!-- end columns demo -->  
    
      
  </div><!-- #end div #main .wrapper -->


<!-- footer area -->    
<footer>
	<div id="colophon" class="wrapper clearfix">
    	(c) Во!Маркет Санкт-Петербург, 2014
        <div>email: info@vomarket.ru</div>
    </div>

    
</footer><!-- #end footer area -->

    <div class="modal modal-auth">
    </div>


<!-- jQuery -->
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/shop/landing/libs/jquery-1.9.0.min.js">\x3C/script>')</script>

<script defer src="js/shop/landing/flexslider/jquery.flexslider-min.js"></script>

<!-- fire ups - read this file!  -->   
<script src="js/shop/landing/main.js"></script>

<%--<script type="text/javascript" data-main="/build/build.js" src="/js/shop/require.min.js"></script>--%>

</body>
</html>