<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ include file="templates/preload.jsp" %>

<%

    OrderDetails currentOrderDetails;
    try{
        Order order = shopService.getOrder(0);
        currentOrderDetails = shopService.getOrderDetails(order.id);
        List<OrderLine> orderLines = currentOrderDetails.odrerLines;
        pageContext.setAttribute("orderLines", orderLines);
    } catch(InvalidOperation ioe){
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

</head>
<body>
    <div class="wrap">
	<div class="main container">

<%@ include file="templates/header.jsp" %>

		<div class="main-container shop dynamic" id="${shopID}">
            <div class="page main-container-inner shop-page">

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
            <div class="page shop-about"></div>
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
    <footer class="short-footer">
        <div class="container">
            <div class="footer-menu">
                <ul>
                    <li><a href="#">О сайте</a></li>
                    <li><a href="/about/${shopID}" class="about-shop-link">О магазине</a></li>
                    <li><a href="#">Правила</a></li>
                    <li><a href="#">Контакты</a></li>
                    <li><a href="#">В начало</a></li>
                </ul>
            </div>
            <div>Вместе Онлайн (c) 2014</div>
        </div>
    </footer>
    </div>

    <script type="text/javascript">
        globalUserAuth = false;
        <c:if test="${isAuth}">
        globalUserAuth = true;
        </c:if>
    </script>

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