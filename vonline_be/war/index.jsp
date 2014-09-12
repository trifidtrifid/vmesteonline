<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="templates/preload.jsp" %>

<!DOCTYPE html>
<html class="no-js">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>Во Маркет !</title>
<meta name="description"
	content="Simple Responsive Template is a template for responsive web design. Mobile first, responsive grid layout, toggle menu, navigation bar with unlimited drop downs, responsive slideshow">
<meta name="keywords" content="">

<!-- Mobile viewport -->
<meta name="viewport" content="width=device-width; initial-scale=1.0">

<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />

<link href='http://fonts.googleapis.com/css?family=Droid+Serif|Ubuntu' rel='stylesheet' type='text/css'>

<link rel="stylesheet" href="css/landing/normalize.css">
<link rel="stylesheet" href="js/shop/landing/flexslider/flexslider.css" />
<link rel="stylesheet" href="css/landing/basic-style.css">

    <script src="js/shop/landing/libs/modernizr-2.6.2.min.js"></script>
<script type="text/javascript">
	    globalUserAuth = false;
	<c:if test="${isAuth}">
	    globalUserAuth = true;
	</c:if>
</script>
</head>

<body id="home" class="shop-landing">

	<header>
		<div class="wrapper clearfix">

			<div id="banner">
				<div id="logo">
					<a href="/">Во!Маркет</a>
				</div>
			</div>

			<nav id="topnav" role="navigation">

				<div class="navbar-header pull-right" role="navigation">
					<ul class="nav ace-nav">
						<li class="user-short">
                            <c:choose>
								<c:when test="${isAuth}">
									<a data-toggle="dropdown" href="#" class="dropdown-toggle"> <%--<img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />--%> <span class="user-info"> <c:out
												value="${firstName}" /> <c:out value="${lastName}" />
									</span> <i class="icon-caret-down"></i>
									</a>
								</c:when>
								<c:otherwise>
									<a data-toggle="dropdown" href="#" class="dropdown-toggle no-login landing-login"> <span class="user-info"> Войти </span>
									</a>
								</c:otherwise>
							</c:choose>
							<ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">

								<li><a href="#"> <i class="icon-user"></i> Профиль
								</a></li>

								<li class="divider"></li>

								<li><a href="#" class="landing-logout"> <i class="icon-off"></i> Выход
								</a></li>
							</ul></li>
					</ul>
				</div>

			</nav>
		</div>

	</header>

    <div class="page landing-page">
	<section id="hero" class="clearfix">
		<div class="wrapper">
			<div class="grid_5 alpha">
				<h1>Самые удобные магазины на планете</h1>
				<p>Мы внимательно относимся к качеству продуктов питания, потому что верим, что от этого зависит качество нашей жизни. Наши эксперты определяют какие свойтсва продуктов важны, а чего следует
					остерегаться при выборе. Огромное внимание мы уделяем свежести продуктов, поэтому продукты доставлятюся Вам напрямую от производителя, минуя магазины, часто, прямо в день производства.</p>
				<p class="align-center">
					<a href="#shops" class="btn no-border btn-primary pull-right">Далее</a>
				</p>
			</div>
			<div class="grid_7 omega rightfloat">
				<div class="flexslider">
					<ul class="slides">
						<li><img src="i/landing/slider1.jpg" /></li>
						<li><img src="i/landing/slider2.jpg" /></li>
						<li><img src="i/landing/slider3.jpg" /></li>
						<li><img src="i/landing/slider4.jpg" /></li>
					</ul>
				</div>
			</div>
		</div>
	</section>

	<div id="main" class="wrapper">

		<section id="content" class="wide-content">
			<div class="grid_4">
				<h1 class="first-header">Качество!</h1>
				<img src="i/landing/quality.jpg" />
				<p/>
				<p align="center">Мы предпочитаем продукцию крупных компаний, с налаженной и хорошо развитой системой производства, стандартизированным контролем качества и многолетней историей постоянных покупателей.
					Многие наши поставщики имеют сертификаты добровольных сообществ, объединяющих качественных производителей.</p>
			</div>

			<div class="grid_4">
				<h1 class="first-header">Свежесть!</h1>
				<img src="i/landing/fresh.jpg" />
				<p/>
				<p align="center">Мы работаем напрямую с лучшими призводителями и доставляем продукцию к вам домой сразу, как только она приезжает к нам. Так мы обеспечиваем макисмальную свежесть. <br/>Часто вы получаете продукты прямо в день их производства!</p>
			</div>

			<div class="grid_4">
				<h1 class="first-header">Удобство !</h1>
				<img src="i/landing/usability.jpg" />
				<p/>
				<p align="center">Мы постоянно оптимизируем доставку, для того чтобы предложить Вам лучшие условия. Чем больше заказов поступает из одного дома - тем дешевле.<br/> Коопрерируйтесь с соседями и цена доставки будет
					уменьшаться!</p>
			</div>

		</section>


		<section id="columnsdemo" style="margin-bottom: 60px; width: 100%" class="clearfix">
			<div class="clearfix"></div>

			<div id="shops" class="grid_6 shops active-shops no-left-margin">

				<div class="shops-block-title">Подключенные магазины</div>
				<ul>
					<c:forEach var="shop" items="${activeShops}">
						<c:if test="${shop.id != null}">
							<li>
                            <c:choose>
                                <c:when test="${shop.hostName != null && isProduction}">
	                                <c:choose>
										<c:when test="${isAuth}">
											<a href="http://${shop.hostName}<%=URLrest%>/shop/?rt=ci">
										</c:when>
										<c:otherwise>
											<a href="http://${shop.hostName}<%=URLrest%>/shop/">		
										</c:otherwise>
									</c:choose>
                                </c:when>
                                <c:otherwise>
                                    <a href="/shop/${shop.id}">
                                </c:otherwise>
                            </c:choose>

                                    <c:choose>
                                        <c:when test="${shop.logoURL!= null}">
                                            <img src="${shop.logoURL}" alt="логотип" />
                                        </c:when>
                                        <c:otherwise>
                                            <img src="<c:out value="${noPhotoPic}" />" alt="лого">
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="landing-shop-right">
										<h3>${shop.name}</h3>
										<p>${shop.descr}</p>
								    </span>
							    </a>

                                <c:if test="${shopPages.socialNetworks != null}">
                                    <div class="shop-social">
                                        <c:forEach var="soc" items="${shopPages.socialNetworks}">
                                            <a href="${soc.value}"><img src="../i/${soc.key}.png" alt="лого"/></a>
                                        </c:forEach>
                                    </div>
                                </c:if>

                                <div>
                                <span class="voice-counter"></span>

                                    <c:choose>
                                        <c:when test="${shop.hostName != null && isProduction}">
                                          <a class="btn no-border btn-primary" href="http://${shop.hostName}<%=URLrest%>/shop/">
                                        </c:when>
                                        <c:otherwise>
                                           <a class="btn no-border btn-primary" href="shop/${shop.id}">
                                        </c:otherwise>
                                    </c:choose>

                                    Перейти в магазин</a>
                                </div>
                            </li>
						</c:if>
					</c:forEach>

				</ul>
			</div>
			<div class="grid_6 shops no-active-shops bigger-left-margin">
				<div class="shops-block-title">В очереди</div>
				<ul>
					<c:forEach var="shop" items="${noActiveShops}">
						<c:if test="${shop.id != null}">
							<li id="${shop.id}">
                                <c:choose>
                                    <c:when test="${shop.hostName != null && isProduction}">
                                        <a href="http://${shop.hostName}<%=URLrest%>/about/">
                                    </c:when>
                                    <c:otherwise>
                                        <a href="about/${shop.id}">
                                    </c:otherwise>
                                </c:choose>

                                    <c:choose>
                                        <c:when test="${shop.logoURL!= null}">
                                            <img src="${shop.logoURL}" alt="логотип" />
                                        </c:when>
                                        <c:otherwise>
                                            <img src="<c:out value="${noPhotoPic}" />" alt="лого">
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="landing-shop-right">
										<h3>${shop.name}</h3>
										<p>${shop.descr}</p>
								    </span>
							    </a>
								<div>
									<span class="voice-counter"></span>
                                    <a class="btn no-border btn-primary vote-btn" href="#">Голосовать</a>
                                    <span class="error-info"></span>
								</div>
                            </li>
						</c:if>
					</c:forEach>
				</ul>
			</div>
		</section>


	</div>
    </div>

    <div class="page shop-profile wrapper"></div>
    <div class="page shop-editPersonal wrapper"></div>

    <%@ include file="templates/footer.jsp" %>

	<div class="modal modal-auth"></div>


	<!-- jQuery -->
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
	<script>
		window.jQuery
				|| document
						.write('<script src="js/shop/landing/libs/jquery-1.9.0.min.js">\x3C/script>')
	</script>

	<script defer src="js/shop/landing/flexslider/jquery.flexslider-min.js"></script>
	<script src="js/shop/landing/initSlider.js"></script>

	<script type="text/javascript" data-main="/build/buildv1.js" src="/js/shop/require.min.js"></script>

</body>
</html>