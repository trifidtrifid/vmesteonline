<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../templates/preload.jsp" %>

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
        <%@ include file="../templates/header.jsp" %>

        <div class="main-container shop dynamic" id="${shopID}">
            <div class="page shop-about clearfix">
                <div class="col-xs-4">
                    <img src="<c:out value="${logoURL}" />" alt="лого">
                </div>
                <div class="col-xs-8">
                    <%--<h1>Магазин "<c:out value="${shop.name}" />"</h1>--%>
                    <h1>О магазине</h1>

                    <p>Рады представить вам продукцию производителей Вологодского Края. Мы работаем с лучшими из них, с теми кто завоевал признание и прошел все проверки качества необходимые для получения титулов “Настоящий Вологодский Продукт”, чьи продукты заслужили звание “Молочной Гордости России” и другие знаки качества. Уверены, что качество вас порадует, мы стараемся предлагать вам лучшее что можем найти.</p>
                    <p/>
                    <p>Мы собираем заказы заранее, передаем их на производства и, в определенные дни недели, наша машина объезжает производителей, собирает все, что вы заказали и привозит к нам в магазин, где мы собираем заказы и в этот же день отправляем продукты к вам. Именно так мы обеспечиваем минимальный срок доставки.</p>
                    <p/>
                    <p><b>Наш магазин находится по адресу:</b> г. Пушкин Детскосельский бульвар д 9А ТЦ Платформа <i>(угол Детскосельского и Железнодорожной, недалеко от платформы 21 км) Здесь можно попробовать всю продукцию. А в среду и воскресенье, у нас акция ЦЕЛЫЙ ДЕНЬ СКИДКА 20% на товары с исходящим сроком годности.</i></p>
                    <p><b>Телефон для связи</b> - 7 95О О32 7137</p>
                    <p/>

                    <h1>Правила заказов</h1>
                    <p>Заказы мы доставляем ДВА раза в неделю ПОНЕДЕЛЬНИК и ЧЕТВЕРГ</p>
                    <p>Принимаем заказы на понедельник до 22.00 четверга, а на четверг, до 22.00 понедельника.</p>
                    <p>Машина приходит к нам рано утром в названные выше дни.</p>
                    <p>В эти же дни мы разводим все заказанное. Чтобы сэкономить для вас на стоимости доставки мы разделили дни заказов на районы:</p>
                    <ul type="square">
                        <li><b>Понедельник:</b> Приморский, Северные районы, а так же Пушкин, Павловск, Колпино, Шушары, Славянка</li>
                        <li><b>Четверг:</b> Васильевский Остров, Петроградская Сторона, Центр и Южные Районы.</li>
                        <li><b>Самовывоз:</b> из нашего магазина возможен в любой день заказа.</li>
                    </ul>
                    <p/>
                    <p><b>Внимание!</b> Мы работаем с поставщиками в горячем режиме и они не всегда могут удовлетворить весь заказ. В этом случае мы, по возможности, заменяем товар одного производителя на аналогичный товар другого. Например, сметану Шекснинского завода на сметану Вологжанка, безусловно учитывая разницу в цене.
                        Так мы стараемся полностью удовлетворить ваши запросы. Если вы не хотите замены, отметьте это в комментарии к заказу.</p>
                    <p/>
                    <h1>Доставка:</h1>
                    <ul type="square">
                        <li><b>Самовывоз в Пушкине</b> - Детскосельский бульвар дом 9А ТЦ Платформа</li>
                        <li><b>Доставка Пушкин, Павловск, Славянка</b> 		100р</li>
                        <li><b>Доставка Шушары, Колпино и все районы города</b> 	150р</li>
                        <li><b>Доставка тяжелых коробок (больше 15кг) добавляет</b> 50р </li>
                        <li>Доставка нескольких заказов одному адресату считается как одна доставка - кооперируйтесь!</li>
                    </ul>
                    <h1>Оплата</h1>
                    <p>Оплата производится при получении заказа курьеру</p>
                    <p>Возможна предоплата переводом на счет или карточку Альфабанка или Сбербанка, спрашивайте, мы отправим вам параметры на вашу электронную почту.</p>
                    <p>Постоянные клиенты, те кто сделал хотя бы один заказ, могут оплатить заказы переводом уже после его доставки.</p>
                </div>
                <div class="col-xs-4">

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

    <%@ include file="../templates/footer.jsp" %>

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



