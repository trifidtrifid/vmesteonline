<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="templates/preload.jsp" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Во!Молоко Натуральные Вологодские продукты</title> 
<meta property="og:title" content="Свежие продукты из Вологды / СПб / Заказ и доставка / Во!Молоко" />
<meta property="og:site_name" content="Свежие продукты из Вологды в СПб" />
<meta name="description" content="СПб, Свежие продукты из Вологды с доставкой на дом. Натуральные молоко, творог, масло, сметана, и другие продукты от лучших из Вологоды: Тарнога, Шексна, Череповец, конфеты Атаг"/>

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
	<c:if test="${isAuth}">
	globalUserAuth = true;
	</c:if>
</script>
</head>
<body class="page-about-shop">

    <div class="wrap">
	<div class="main container">
        <%@ include file="templates/header.jsp" %>

		<div class="main-container shop dynamic" id="${shopID}">

            <div class="page main-container-inner shop-page">
                <%@ include file="templates/shop-sidebar.jsp" %>

                <div class="main-content">
                    <div class="shop-products"></div>
                    <%@ include file="templates/shop-orders.jsp" %>

                </div>
                <div class="clear"></div>
            </div>

            <%@ include file="templates/pages.jsp" %>


        </div>

        <%@ include file="templates/modals.jsp" %>

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