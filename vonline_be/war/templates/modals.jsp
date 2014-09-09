<%@ page contentType="text/html;charset=UTF-8" language="java"%>

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

<div class="modal modal-chooseDate">
    <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <div class="clearfix"></div>
        <p></p>
        <h3>Выберите день доставки</h3>

        <div class="datepicker-chooseDate"></div>
        <%--<input type="text" class="datepicker-chooseDate"/>--%>

        <h5 class="pull-left chooseDate-block">Дата доставки: <span class="chooseDate"></span></h5>
        <div class="align-right">
            <a class="btn no-border btn-primary create-order-btn" href="#">Далее</a>
        </div>

    </div>
</div>

