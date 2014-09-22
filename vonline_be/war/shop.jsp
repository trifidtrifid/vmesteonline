<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/templates/preload.jsp" %>
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
    ProductListPart productsListPart = shopService.getProducts(0,25,0);
    //out.print(productsListPart.products.size());

    if (productsListPart.products.size() > 0){
        pageContext.setAttribute("products",productsListPart.products);
    }
    int ArrayProductCategorySize = ArrayProductCategory.size();
    if(ArrayProductCategorySize > 0){
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

<link rel="stylesheet" href="/build/shop.min.css" />
<!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->

    <script type="text/javascript" data-main="/build/build.js" src="/js/shop/require.min.js"></script>

    <%--<script src="../js/lib/jquery-2.1.1.min.js"></script>
    <script src="../js/lib/bootstrap.min.js"></script>
    <script src="../js/lib/ace-extra.min.js"></script>
    <script src="../js/lib/ace-elements.min.js"></script>


    <script src="../js/lib/jquery.nestable.min.js"></script>

    <script src="../js/lib/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="../js/lib/markdown/markdown.min.js"></script>
    <script src="../js/lib/markdown/bootstrap-markdown.min.js"></script>
    <script src="../js/lib/jquery.hotkeys.min.js"></script>
    <script src="../js/lib/bootstrap-wysiwyg.min.js"></script>
    <script src="../js/bootbox.min.js"></script>
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

    <script type="text/javascript" data-main="../js/shop/shop.js" src="../js/shop/require.min.js"></script>--%>

</head>
<body>
    <div class="wrap">
	<div class="main container">

<%@ include file="templates/header.jsp" %>

		<div class="main-container shop dynamic" id="${shopID}">
            <div class="page main-container-inner shop-page">

                <%@ include file="templates/shop-sidebar.jsp" %>

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
                                <%
                                    for( int val = 0; val < ArrayProductCategorySize; val ++ ){
                                %>
                                        <li data-parentid="<%=ArrayProductCategory.get(val).parentId%>" data-catid="<%=ArrayProductCategory.get(val).id%>">
                                            <a href="#"  style="z-index: <%=50-val*2%>" class="btn btn-app btn-info btn-sm">
                                                <span><%=ArrayProductCategory.get(val).name%></span>
                                            </a>
                                            <%
                                                if(ArrayProductCategory.get(val).socialNetworks != null){
                                            %>
                                            <div class="category-label"></div>
                                            <div class="category-soc-links" style="z-index: <%=50-val*2-1%>">
                                                <%
                                                   Map<String,String> socialNetworks = ArrayProductCategory.get(val).socialNetworks;
                                                    
                                                   int socialNetworksSize = socialNetworks.size();
                                                    
                                                    String imgSrc=" ";
                                                    String[] keys = socialNetworks.keySet().toArray(new String[socialNetworksSize]);

                                                    for( int i = 0; i < socialNetworksSize ; i ++ ){
                                                        if(!socialNetworks.get(keys[i]).equals("")){
                                                            if (socialNetworks.get(keys[i]).indexOf("vk") != -1){
                                                                imgSrc = "/i/vk.png";
                                                            }else if(socialNetworks.get(keys[i]).indexOf("fb") != -1){
                                                                imgSrc = "/i/fb.png";
                                                            }

                                                %>
                                                <a class="category-soc-single" target="_blank" href="<%=socialNetworks.get(keys[i])%>"><img src="<%=imgSrc%>" alt="картинка"/></a>
                                                <%
                                                        }}
                                                %>
                                            </div>
                                            <%
                                                }
                                            %>
                                        </li>
                                <%
                                    }
                                %>
                                <%--<c:forEach var="productCategory" items="${productCategories}">
                                    <li data-parentid="${productCategory.parentId}" data-catid="${productCategory.id}">
                                        <a href="#" class="btn btn-app btn-info btn-sm">
                                            <c:if test="${productCategory.logoURLset != null}">
                                                <div class="category-label"></div>
                                                <div class="category-soc-links">
                                                    <%
                                                        for( int val = 0; val < ; val ++ ){

                                                        }
                                                    %>
                                                </div>
                                            </c:if>
                                            <span>${productCategory.name}</span>
                                        </a>
                                    </li>
                                </c:forEach>--%>
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
                                                        <img src="${product.imageURL}?w=40&h=40" alt="фото"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="/i/no-photo.png" alt="нет фото" />
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
                                            <span title="Продукт уже у вас в корзине" class="fa fa-check"></span>
                                        </td>
                                    </tr>
                                </c:forEach>

                            </table>
                        </section>
                    </div>
                    <%@ include file="templates/shop-orders.jsp" %>

                </div>
                <div class="clear"></div>
            </div>
            <div class="page shop-confirm"></div>
            <div class="page shop-orderEnd"></div>
            <div class="page shop-editPersonal"></div>
            <div class="page shop-profile"></div>
            <div class="page shop-about"></div>
            <div class="page shop-terms-of-orders"></div>
            <div class="page shop-terms-of-delivery"></div>
        </div>

        <%@ include file="templates/modals.jsp" %>

	</div>

    <%@ include file="templates/footer.jsp" %>

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