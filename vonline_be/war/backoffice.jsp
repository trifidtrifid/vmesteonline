<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ include file="templates/preload.jsp" %>
<%

if(isAuth){

    int now = (int) (System.currentTimeMillis() / 1000L);
    int day = 3600 * 24;
    List<Order> orders = shopService.getOrders(0, now + 180*day);
    if(orders.size() > 0 ){
        pageContext.setAttribute("orders", orders);
    }

    ProductListPart productsList = shopService.getProducts(0,1000,0);
    if(productsList != null && productsList.length > 0){
        pageContext.setAttribute("products", productsList.products);
    }

    List<Producer> producersList = shopService.getProducers();
    if(producersList != null && producersList.size() > 0){
        pageContext.setAttribute("producers", producersList);
    }

}

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
  <title>Бэкоффис</title>
  <link rel="stylesheet" href="/build/shop.min.css"/>
    <!--[if lt IE 9]>
    <script>
        document.createElement('header');
        document.createElement('section');
        document.createElement('footer');
        document.createElement('aside');
        document.createElement('nav');
    </script>
    <![endif]-->
</head>
<body>
<div class="wrap">
<div class="container backoffice <c:if test="${userRole != 'BACKOFFICER' && userRole != 'ADMIN' && userRole != 'OWNER'}"> noAccess </c:if> ">

    <%@ include file="templates/header.jsp" %>

    <div class="main-container backoffice dynamic" id="${shopID}">
    <c:if test="${isAuth}">
        <div class="page main-container-inner bo-page">
            <aside class="sidebar" id="sidebar">
                <script type="text/javascript">
                    try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
                </script>
                <div class="show-left">
                    Меню
                </div>
                <ul class="nav nav-list">
                    <li class="active">
                        <a href="#">
                            <span class="menu-text"> Заказы </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Продукты/категории </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Экспорт </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Импорт </span>
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <span class="menu-text"> Настройки </span>
                        </a>
                    </li>
                </ul><!-- /.nav-list -->
            </aside>

            <div class="main-content">
                <div class="back-orders back-tab">
                    <div class="back-orders-top">
                        <a href="#" class="btn btn-grey btn-sm no-border reset-filters">Сбросить</a>
                        <div class="btn-group status-dropdown">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                <span class="btn-group-text">Статус заказа</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <li><a href="#">Подтвержден</a></li>
                                <li><a href="#">Не подтвержден</a></li>
                                <li><a href="#">Отменен</a></li>
                            </ul>
                        </div>
                        <div class="btn-group type-delivery-dropdown">
                            <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                <span class="btn-group-text">Тип доставки</span>
                                <span class="icon-caret-down icon-on-right"></span>
                            </button>

                            <ul class="dropdown-menu dropdown-blue">
                                <li><a href="#">Самовывоз</a></li>
                                <li><a href="#">Курьер рядом</a></li>
                                <li><a href="#">Курьер далеко</a></li>
                            </ul>
                        </div>
                        <div class="input-group">
                            <input class="form-control date-picker" id="date-picker-1" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                        </div>
                        <form method="post" action="#" class="form-group has-info search-form">
                            <span class="block input-icon input-icon-right">
                                <input id="back-search" type="text" class="form-control width-100" value="Поиск по имени клиента или номеру телефона" onblur="if(this.value=='') this.value='Поиск по имени клиента или номеру телефона';" onfocus="if(this.value=='Поиск по имени клиента или номеру телефона') this.value='';"/>
                                <a href="#" class="icon-search icon-on-right bigger-110"></a>
                            </span>
                        </form>

                    </div>
                    <div class="orders-list">
                    </div>

                </div>
                <div class="import back-tab">
                    <h2>Здесь вы можете загрузить список продуктов в магазин</h2>
                    <div class="btn-group import-dropdown">
                        <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                            <span class="btn-group-text">Тип импортируемых данных</span>
                            <span class="icon-caret-down icon-on-right"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-blue">
                            <li><a href="#">Продукты</a></li>
                            <li><a href="#">Категории продуктов</a></li>
                            <li><a href="#">Производители</a></li>
                        </ul>
                    </div>

                    <form action="/file/" method="post"  class="form-import" enctype="multipart/form-data"> <%--enctype="multipart/form-data"--%>
                        <input id="import-data" type="file" name="data"/>
                        <input id="import-public" type="hidden" name="public"/> <!-- Be sure that you gona make file accessible for everyone -->
                        <input class="btn btn-primary btn-sm no-border" type="submit" value="Загрузить" />
                    </form>
                    <div class="error-info"></div>

                    <div class="import-show">
                        <div class="import-btn-top">
                            <span class="confirm-info"></span>
                            <a class="btn btn-primary btn-sm no-border import-btn" href="#">Импортировать</a>
                        </div>
                        <h3>Поставьте соответствие данных и столбцов</h3>

                        <div id="doublescroll" class="import-table">

                        </div>
                        <div>
                            <a class="btn btn-primary btn-sm no-border import-btn" href="#">Импортировать</a>
                            <span class="confirm-info"></span>
                        </div>
                    </div>
                </div>
                <div class="export back-tab tabbable">
                    <ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab4">
                        <li class="active">
                            <a data-toggle="tab" href="#orders">Отчет о Заказах</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#products">Отчет о продуктах</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#pack">Отчет об упаковках</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div id="orders" class="tab-pane in active">
                            <p>Отчет о Заказах</p>

                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-2" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>

                            <div class="export-orders-checklist">
                                <select multiple>
                                <optgroup label="Order">
                                    <%
                                        for( int val = ExchangeFieldType.ORDER_ID.getValue(); val < ExchangeFieldType.ORDER_LINE_ID.getValue(); val ++ ){
                                            ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                            if(null!=value){
                                                if(value.name() == "ORDER_PRICE_TYPE" || value.name() == "ORDER_PAYMENT_TYPE"
                                                  || value.name() == "ORDER_PAYMENT_STATUS" || value.name() == "ORDER_USER_ID"){
                                    %>
                                                    <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                                <% } else{ %>
                                                    <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                        <%}}}%>

                                </optgroup>
                                <optgroup label="Order_Line">
                                        <%for( int val = ExchangeFieldType.ORDER_LINE_ID.getValue(); val < ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val ++ ){
                                            ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                            if(null!=value){
                                            if(value.name() == "ORDER_LINE_ID" || value.name() == "ORDER_LINE_OPRDER_ID"
                                                || value.name() == "ORDER_LINE_PRODUCT_ID" || value.name() == "ORDER_LINE_PRODUCER_ID"
                                                || value.name() == "ORDER_LINE_PRODUCT_ID" || value.name() == "ORDER_LINE_PRODUCER_ID"){
                                                    %>

                                            <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                            <% } else{ %>
                                            <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                        <%}}}%>
                                </optgroup>
                                </select>

                            </div>

                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">

                            </div>
                        </div>

                        <div id="products" class="tab-pane">
                            <p>Отчет о продуктах</p>

                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-3" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>

                            <div class="export-products-checklist">
                                <select multiple>

                                    <%for( int val = ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val < ExchangeFieldType.TOTAL_PACK_SIZE.getValue(); val ++ ){
                                        ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                        if(null!=value){
                                            if(value.name() == "TOTAL_PROUCT_ID" || value.name() == "TOTAL_PRODUCER_ID"){
                                    %>

                                    <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                    <% } else{ %>
                                    <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                    <%}}}%>
                                </select>

                            </div>
                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">

                            </div>
                        </div>

                        <div id="pack" class="tab-pane">
                            <p>Отчет об упаковках</p>
                            <div class="btn-group export-delivery-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">Тип доставки</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                    <li><a href="#">Самовывоз</a></li>
                                    <li><a href="#">Курьер рядом</a></li>
                                    <li><a href="#">Курьер далеко</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Все</a></li>
                                </ul>
                            </div>

                            <div class="input-group">
                                <input class="form-control date-picker datepicker-export" id="date-picker-4" type="text" data-date-format="dd-mm-yyyy" value="Фильтр по дате" onblur="if(this.value=='') this.value='Фильтр по дате';" onfocus="if(this.value=='Фильтр по дате') this.value='';"/>
                            </div>


                            <div class="export-packs-checklist">
                                <select multiple>
                                <%for( int val = ExchangeFieldType.TOTAL_PROUCT_ID.getValue(); val <= ExchangeFieldType.TOTAL_DELIVERY_TYPE.getValue(); val ++ ){
                                    ExchangeFieldType value = ExchangeFieldType.findByValue(val);
                                    if(null!=value){
                                        if(value.name() == "TOTAL_PROUCT_ID" || value.name() == "TOTAL_PRODUCER_ID"|| value.name() == "TOTAL_DELIVERY_TYPE"){
                                %>

                                <option value="<%=value.getValue()%>"><%=value.name()%> </option>

                                <% } else{ %>
                                <option selected="selected" value="<%=value.getValue()%>"><%=value.name()%> </option>

                                <%}}}%>
                                </select>

                            </div>
                            <div class="error-info"></div>

                            <div class="export-btn-line">
                                <a class="btn btn-primary btn-sm no-border export-btn" href="#">Создать отчет</a>
                                <span class="confirm-info"></span>
                            </div>

                            <div class="export-table">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="bo-settings back-tab signup-shop">
                    <div id="settings-common" class="settings-item">
                    <h2>Общая информация</h2>
                    <fieldset>
                        <label class="block clearfix">
                            <span class="block input-icon input-icon-right">
                                <input type="text" id="name" class="form-control" value="${shop.name}" />
                            </span>
                        </label>

                        <label class="block clearfix">
                            <span class="block input-icon input-icon-right">
                                <textarea name="descr" id="descr" cols="30" rows="10">${shop.descr}</textarea>
                            </span>
                        </label>

                        <label class="block clearfix">
                            <span class="block input-icon input-icon-right">
                                <textarea name="address" id="address" cols="30" rows="10">${shop.address.street.name} ${shop.address.building.fullNo}, офис ${shop.address.flatNo}</textarea>
                            </span>
                        </label>

                        <label class="block clearfix">
                            <span class="block input-icon input-icon-right">
                                <input type="text" id="hostName" class="form-control" value="${shop.hostName}" />
                            </span>
                        </label>


                        <label class="block clearfix logo-container">
                            <img src="${shop.logoURL}" alt="логотип"/>
                            <input type="file" id="settings-logo">
                        </label>

                    </fieldset>
                        <a class="btn btn-sm no-border btn-primary btn-save" href="#">Сохранить</a>
                    </div>

                    <div id="settings-links" class="settings-item">
                        <h2>Ссылки</h2>

                        <div class="settings-links-item">
                            <label for="settings-about-link">
                                О магазине
                            </label>
                            <input id="settings-about-link" type="text"/>
                        </div>
                        <div class="settings-links-item">
                            <label for="settings-terms-link">
                                Условия
                            </label>
                            <input id="settings-terms-link" type="text"/>
                        </div>
                        <div class="settings-links-item">
                            <label for="settings-delivery-link">
                                Доставка
                            </label>
                            <input id="settings-delivery-link" type="text"/>
                        </div>
                        <div class="settings-links-item">
                            <label for="settings-socvk-link">
                                Социальные сети
                            </label>
                            <input id="settings-socvk-link" placeholder="vk" type="text"/><br>
                            <label></label>
                            <input id="settings-socfb-link" placeholder="fb" type="text"/>
                        </div>

                        <a class="btn btn-sm no-border btn-primary btn-save" href="#">Сохранить</a>
                    </div>

                    <div id="settings-shedule" class="settings-item">
                        <h2>Расписание завоза</h2>
                        <%--<div id="date-picker-6" class="shedule-dates"></div>--%>

                        <div class="delivery-period"><span>Периодичность доставки</span>
                            <div class="btn-group delivery-period-dropdown">
                                <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                    <span class="btn-group-text">неделя</span>
                                    <span class="icon-caret-down icon-on-right"></span>
                                </button>

                                <ul class="dropdown-menu dropdown-blue">
                                        <li><a href="#">неделя</a></li>
                                        <li><a href="#">месяц</a></li>
                                </ul>
                            </div>
                        </div>

                        <%--<div class="shedule-item">
                            <a href="#" title="Добавить день доставки" class="add-delivery-interval add-interval pull-right">+</a>
                            <div class="shedule-confirm"><span>День доставки</span>
                                <div class="btn-group delivery-day-dropdown">
                                    <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                        <span class="btn-group-text">1</span>
                                        <span class="icon-caret-down icon-on-right"></span>
                                    </button>

                                    <ul class="dropdown-menu dropdown-blue">
                                        <c:forEach  var="i" begin="1" end="7">
                                            <li><a href="#">${i}</a></li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                            <div class="shedule-confirm"><span>подтверждать заказ за</span><input type="text" id="days-before" value="2"><span>дня до доставки</span></div>
                        </div>--%>

                        <a class="btn btn-sm no-border btn-primary btn-save" href="#">Сохранить</a>
                    </div>

                    <div id="settings-delivery" class="settings-item">
                        <h2>Доставка</h2>
                        <h5> Определение типов доставки в зависимости от населенных пунктов</h5>

                        <div class="radio settings-delivery-container delivery-area-container">
                            <label>
                                <span class="lbl"></span>
                            </label>
                        </div>
                        <h5>Стоимость доставки в зависимости от расстояния</h5>
                        <div class="radio settings-delivery-container delivery-type-container">
                            <label>
                                <span class="lbl"> Стоимость в зависимости от типа доставки</span>
                            </label>
                        </div>
                        <div class="radio settings-delivery-container delivery-interval-container">
                            <label>
                                <span class="lbl"> Стоимость в интервалах (например 100 р до 10км, 200р при > 10км)</span>
                            </label>
                        </div>
                        <h5>Стоимость доставки в зависимости от веса заказа</h5>
                            <div class="settings-delivery-container delivery-weight-container">
                            </div>
                            <br>
                        <a class="btn btn-primary btn-sm no-border btn-save" href="#">Сохранить</a>
                    </div>
                </div>
                <div class="bo-edit back-tab">
                    <ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab5">
                        <li class="active">
                            <a data-toggle="tab" href="#edit-product">Продукты</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#edit-category">Категории</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#edit-producer">Производители</a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div id="edit-product" class="tab-pane active">

                            <a class="btn btn-sm no-border btn-primary edit-show-add" href="#">Добавить продукт</a>

                            <div class="table-add table-add-product">
                            <div class="modal modal-editProduct">
                                <div class="modal-body">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                                    <h3>Создание продукта</h3>
                                    <p></p>

                                    <ul class="edit-product-list clearfix">
                                        <li>
                                            <label>Название</label>
                                            <span class="edit-product-item product-name"><textarea></textarea></span>
                                        </li>
                                        <li>
                                            <label>Сокр. описание</label>
                                            <span class="edit-product-item product-shortDescr"><textarea></textarea></span>
                                        </li>
                                        <li>
                                            <label>Полное описание</label>
                                            <span class="edit-product-item product-fullDescr"><textarea></textarea></span>
                                        </li>
                                        <li class="pull-left short1">
                                            <label>Аватар</label>
                                            <span class="edit-product-item product-imageURL">
                                                <input type="file" id="imageURL-add">
                                                <img src="../i/no-photo.png" alt="картинка"/>

                                            </span>
                                        </li>
                                        <li class="pull-left short1">
                                            <label>Другие изображения</label>
                                            <span class="edit-product-item product-imagesSet">
                                                <input type="file" id="imageURLSet-add">
                                            </span>
                                        </li>
                                        <li class="pull-left short1 clear">
                                            <label>Категории</label>
                                            <span class="edit-product-item product-categories"></span>
                                        </li>
                                        <li class="pull-left short1">
                                            <label>Производитель</label>
                                            <span class="edit-product-item product-producer" data-producerid="${product.producerId}">

                                                <div class="btn-group producers-dropdown">
                                                    <button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">
                                                        <span class="btn-group-text">Выбрать производителя</span>
                                                        <span class="icon-caret-down icon-on-right"></span>
                                                    </button>

                                                    <ul class="dropdown-menu dropdown-blue">
                                                        <c:forEach var="producer" items="${producers}">
                                                            <li data-producerid="${producer.id}"><a href="#">${producer.name}</a></li>
                                                        </c:forEach>
                                                    </ul>
                                                </div>

                                            </span>
                                        </li>
                                        <li class="pull-left short2 clear">
                                            <label>Вес</label>
                                            <span class="edit-product-item product-weight">
                                                <input type="text" value=""/>
                                            </span>
                                        </li>
                                        <li class="pull-left short2">
                                            <label>Цена</label>
                                            <span class="edit-product-item product-price">
                                                <input type="text" value=""/>
                                            </span>
                                        </li>
                                        <li class="pull-left short2">
                                            <label>Ед.изм</label>
                                            <span class="edit-product-item product-unitName">
                                                <input type="text" value=""/>
                                            </span>
                                        </li>
                                        <li class="pull-left short2">
                                            <label>Мин.шаг</label>
                                            <span class="edit-product-item product-pack">
                                                <input type="text" value=""/>
                                            </span>
                                        </li>
                                        <li class="pull-left short2">
                                            <label>Весовой</label>
                                            <span class="edit-product-item product-prepack">
                                                <input type="checkbox"/>
                                            </span>
                                        </li>

                                        <li class="pull-left short1 clear">
                                            <label>Опции</label>
                                            <span class="edit-product-item product-options">
                                                <table>
                                                    <tr>
                                                        <td><input type='text' placeholder="опция"></td>
                                                        <td><input type='text' placeholder="описание"></td>
                                                        <td class='td-remove-options'><a href='#' class='remove-options-item remove-item'>&times;</a></td>
                                                    </tr>
                                                </table>
                                            <a href='#' class='add-options-item add-item'>Добавить</a>
                                            </span>
                                        </li>
                                        <li class="pull-left short1">
                                            <label>Ссылки</label>
                                            <span class="edit-product-item product-links">
                                                <table>
                                                    <tbody>
                                                    <tr>
                                                        <td class="link-key">vk</td>
                                                        <td><input type="text"/></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="link-key">fb</td>
                                                        <td><input type="text"/></td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </span>
                                        </li>
                                    </ul>

                                    <a class="btn btn-sm btn-primary no-border edit-add clear" href="#">Сохранить</a>

                                </div>
                            </div>

                            </div>

                            <div class="table-overflow products-table" id="doublescroll-2">
                                <table>
                                    <thead>
                                    <tr>
                                        <td>Название</td>
                                        <td>Сокр. описание</td>
                                        <td>Аватар</td>
                                        <td>Вес</td>
                                        <td>Ед.изм</td>
                                        <td>Мин.шаг</td>
                                        <td>Весовой</td>
                                    </tr>
                                    </thead>
                                    <c:forEach var="product" items="${products}">
                                        <tr id="${product.id}">
                                            <td class="product-name">
                                                ${product.name}

                                                    <div class="modal modal-editProduct">
                                                        <div class="modal-body">
                                                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                                                            <h3>Редактирование продукта</h3>
                                                            <p></p>

                                                            <ul class="edit-product-list clearfix">
                                                                <li>
                                                                    <label>Название</label>
                                                                    <span class="edit-product-item product-name"><textarea>${product.name}</textarea></span>
                                                                </li>
                                                                <li>
                                                                    <label>Сокр. описание</label>
                                                                    <span class="edit-product-item product-shortDescr"><textarea>${product.shortDescr}</textarea></span>
                                                                </li>
                                                                <li>
                                                                    <label>Полное описание</label>
                                                                    <span class="edit-product-item product-fullDescr"><textarea></textarea></span>
                                                                </li>
                                                                <li class="pull-left short1">
                                                                    <label>Аватар</label>
                                                                        <span class="edit-product-item product-imageURL">
                                                                            <input type="file" id="imageURL-${product.id}">
                                                                            <c:choose>
                                                                                <c:when test="${product.imageURL != null}">
                                                                                    <img src="${product.imageURL}" alt="картинка"/>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    <img src="../i/no-photo.png" alt="картинка"/>
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </span>
                                                                </li>
                                                                <li class="pull-left short1">
                                                                    <label>Другие изображения</label>
                                            <span class="edit-product-item product-imagesSet">
                                                <input type="file" id="imagesSetURL">
                                            </span>
                                                                </li>
                                                                <li class="pull-left short1 clear">
                                                                    <label>Категории</label>
                                                                    <span class="edit-product-item product-categories"></span>
                                                                </li>
                                                                <li class="pull-left short1">
                                                                    <label>Производитель</label>
                                            <span class="edit-product-item product-producer" data-producerid="${product.producerId}">

                                            </span>
                                                                </li>
                                                                <li class="pull-left short2 clear">
                                                                    <label>Вес</label>
                                            <span class="edit-product-item product-weight">
                                                <input type="text" value="${product.weight}"/>
                                            </span>
                                                                </li>
                                                                <li class="pull-left short2">
                                                                    <label>Цена</label>
                                            <span class="edit-product-item product-price">
                                                <input type="text" value=""/>
                                            </span>
                                                                </li>
                                                                <li class="pull-left short2">
                                                                    <label>Ед.изм</label>
                                            <span class="edit-product-item product-unitName">
                                                <input type="text" value="${product.unitName}"/>
                                            </span>
                                                                </li>
                                                                <li class="pull-left short2">
                                                                    <label>Мин.шаг</label>
                                            <span class="edit-product-item product-pack">
                                                <input type="text" value="${product.minClientPack}"/>
                                            </span>
                                                                </li>
                                                                <li class="pull-left short2">
                                                                    <label>Весовой</label>
                                                                    <span class="edit-product-item product-prepack">
                                                                        <input type="checkbox" <c:if test="${product.prepackRequired}"> checked </c:if> />
                                                                    </span>
                                                                </li>

                                                                <li class="pull-left short1 clear">
                                                                    <label>Опции</label>
                                                                    <span class="edit-product-item product-options"></span>
                                                                </li>
                                                                <li class="pull-left short1">
                                                                    <label>Ссылки</label>
                                                                    <span class="edit-product-item product-links">
                                                                        <table>
                                                                            <tbody>
                                                                            <tr>
                                                                                <td class="link-key">vk</td>
                                                                                <td><input type="text"/></td>
                                                                            </tr>
                                                                            <tr>
                                                                                <td class="link-key">fb</td>
                                                                                <td><input type="text"/></td>
                                                                            </tr>
                                                                            </tbody>
                                                                        </table>
                                                                    </span>
                                                                </li>
                                                            </ul>

                                                            <a class="btn btn-sm btn-primary no-border save-products clear" href="#">Сохранить</a>


                                                        </div>
                                                    </div>


                                            </td>
                                            <td class="product-shortDescr">
                                                ${product.shortDescr}
                                            </td>
                                            <%--<td class="product-fullDescr">
                                                <textarea></textarea>
                                            </td>--%>
                                            <td class="product-imageURL">
                                                <%--<input type="file" id="imageURL-${product.id}">--%>
                                                <c:choose>
                                                    <c:when test="${product.imageURL != null}">
                                                        <img src="${product.imageURL}" alt="картинка"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="../i/no-photo.png" alt="картинка"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="product-weight">${product.weight}</td>
                                            <td class="product-unitName">${product.unitName}</td>
                                            <td class="product-pack">${product.minClientPack}</td>

                                            <td class="product-prepack">
                                                <input type="checkbox" <c:if test="${product.prepackRequired}"> checked </c:if> />
                                            </td>
                                            <td class="product-remove"><a href="#" title="Удалить" class="remove-item">&times;</a></td>
                                        </tr>

                                    </c:forEach>

                                </table>
                            </div>

                        </div>
                        <div id="edit-category" class="tab-pane">
                            <a class="btn btn-sm no-border btn-primary edit-show-add" href="#">Добавить категорию</a>

                            <div class="table-add table-add-category">
                                <table>
                                    <tr>
                                        <td class="category-name"><textarea>Название</textarea></td>
                                        <td class="category-descr"><textarea>Описание</textarea></td>
                                        <td class="category-links">
                                            <table>
                                                <tr>
                                                    <td class="link-key">vk</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td class="link-key">fb</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td class="category-parent"></td>
                                    </tr>
                                </table>
                                <a class="btn btn-sm no-border btn-primary edit-add" href="#">Добавить</a>
                            </div>

                            <table class="category-table">
                                <thead>
                                <tr>
                                    <td>Название</td>
                                    <td>Описание</td>
                                    <td>Ссылки</td>
                                    <td>Родительская категория</td>
                                    <td></td>
                                </tr>
                                </thead>
                                <c:forEach var="category" items="${categories}">
                                    <tr id="${category.id}">
                                        <td class="category-name"><textarea>${category.name}</textarea></td>
                                        <td class="category-descr"><textarea>${category.descr}</textarea></td>
                                        <td class="category-links">
                                            <table>
                                                <tr>
                                                    <td class="link-key">vk</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td class="link-key">fb</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td class="category-parent" data-parentid="${category.parentId}">
                                        </td>
                                        <td class="category-remove"><a href="#" class="remove-item">&times;</a></td>
                                    </tr>
                                </c:forEach>

                            </table>
                            <a class="btn btn-sm btn-primary no-border save-categories" href="#">Сохранить изменения</a>

                        </div>
                        <div id="edit-producer" class="tab-pane">
                            <a class="btn btn-sm no-border btn-primary edit-show-add" href="#">Добавить производителя</a>

                            <div class="table-add table-add-producer">
                                <table>
                                    <tr>
                                        <td class="producer-name"><textarea>Название</textarea></td>
                                        <td class="producer-descr"><textarea>Описание</textarea></td>
                                        <td class="producer-links">
                                            <table>
                                                <tr>
                                                    <td class="link-key">vk</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td class="link-key">fb</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                                <a class="btn btn-sm no-border btn-primary edit-add" href="#">Добавить</a>
                            </div>

                            <table class="producer-table">
                                <thead>
                                <tr>
                                    <td>Название</td>
                                    <td>Описание</td>
                                    <td>Ссылки</td>
                                    <td></td>
                                </tr>
                                </thead>
                                <c:forEach var="producer" items="${producers}">
                                    <tr id="${producer.id}">
                                        <td class="producer-name"><textarea>${producer.name}</textarea></td>
                                        <td class="producer-descr"><textarea>${producer.descr}</textarea></td>
                                        <td class="producer-links">
                                            <table>
                                                <tr>
                                                    <td class="link-key">vk</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                                <tr>
                                                    <td class="link-key">fb</td>
                                                    <td><input type="text"/></td>
                                                </tr>
                                            </table>
                                        </td>
                                        <td class="producer-remove"><a href="#" class="remove-item">&times;</a></td>
                                    </tr>
                                </c:forEach>

                            </table>
                            <a class="btn btn-sm btn-primary no-border save-producers" href="#">Сохранить изменения</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="page main-container-inner shop-page">
            <%@ include file="templates/shop-sidebar.jsp" %>

            <div class="main-content">
                <div class="shop-products"></div>
                <%@ include file="templates/shop-orders.jsp" %>

            </div>
            <div class="clear"></div>
        </div>

        <%@ include file="templates/pages-profile.jsp" %>
    </c:if>
    </div>
    <div class="loading">
        <div class="loading-inside">
            <img src="../i/wait1.png" alt="загрузка">
            <span>Подождите, идет загрузка ...</span>
        </div>
    </div>
</div>
<%@ include file="templates/footer.jsp" %>
</div>


<!-- файлы thrift -->
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
<!-- -->

<%--<script type="text/javascript" data-main="/build/backoffice.min.js" src="/js/shop/require.min.js"></script>--%>
<script type="text/javascript" data-main="/js/shop/backoffice/backoffice.js" src="/js/shop/require.min.js"></script>


</body>
</html>