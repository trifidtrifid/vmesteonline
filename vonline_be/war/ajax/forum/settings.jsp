<%--<%@page import="com.vmesteonline.be.utils.SessionHelper"%>--%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.vmesteonline.be.UserServiceImpl"%>
<%@ page import="com.vmesteonline.be.ShortUserInfo"%>
<%@ page import="com.vmesteonline.be.AuthServiceImpl"%>
<%@ page import="com.vmesteonline.be.InvalidOperation"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
	HttpSession sess = request.getSession();
	try {
	 	AuthServiceImpl.checkIfAuthorised(sess.getId());
	} catch (InvalidOperation ioe) {
		response.sendRedirect("/login.jsp");
		return; 
	}
    UserServiceImpl userService = new UserServiceImpl(request.getSession());
    ShortUserInfo ShortUserInfo = userService.getShortUserInfo();
    pageContext.setAttribute("firstName",ShortUserInfo.firstName);
    pageContext.setAttribute("lastName",ShortUserInfo.lastName);
    
%>

        <div class="dynamic">
            <section class="settings">
                <h3>Настройки</h3>
                <div class="tabbable">
                    <ul class="nav nav-tabs padding-12 tab-color-blue background-blue" id="myTab4">

                        <li class="active"><a data-toggle="tab" href="#settings-base">Основное</a>
                        </li>

                        <li><a data-toggle="tab" href="#private">Приватность</a>
                        </li>

                        <li class=""><a data-toggle="tab" href="#alerts">Оповещения</a>
                        </li>

                        <li class=""><a data-toggle="tab" href="#contacts">Контакты</a>
                        </li>

                        <li class=""><a data-toggle="tab" href="#family">Семья</a>
                        </li>

                        <li class=""><a data-toggle="tab" href="#interests">Интересы</a>
                        </li>
                    </ul>

                    <div class="tab-content">
                        <div id="settings-base" class="tab-pane active">

                            <a class="btn btn-sm no-border btn-primary pull-right" href="#">Загрузить данные из Вконтакте</a>

                            <div>
                                <label for="settings-input-1">Фамилия</label>
                                <input type="text" id="settings-input-1"/>
                            </div>
                            <div>
                                <label for="settings-input-2">Имя</label>
                                <input type="text" id="settings-input-2"/>
                            </div>
                            <div>
                                <label for="settings-input-3">Дата рождения</label>
                                <input type="text" id="settings-input-3"/>
                            </div>
                            <div>
                                <label for="settings-select-1">Пол</label>

                                <select class="form-control" id="settings-select-1">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Мужчина</option>
                                    <option value="AK">Женщина</option>
                                    <option value="AK">Маккартни</option>
                                </select>
                            </div>

                            <h4>Изменить пароль</h4>
                            <div>
                                <label for="settings-input-4">Старый пароль</label>
                                <input type="password" id="settings-input-4"/>
                            </div>
                            <div>
                                <label for="settings-input-5">Новый пароль</label>
                                <input type="password" id="settings-input-5"/>
                            </div>
                            <div>
                                <label for="settings-input-6">Повторите пароль</label>
                                <input type="password" id="settings-input-6"/>
                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>

                        </div>

                        <div id="private" class="tab-pane">
                            <div>
                                <label for="settings-select-2" class="long">Показывать мой профайл</label>

                                <select class="form-control" id="settings-select-2">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Никому</option>
                                    <option value="AK">Васе</option>
                                </select>
                            </div>
                            <div>
                                <label for="settings-select-3" class="long">Показывать контактную информацию</label>

                                <select class="form-control" id="settings-select-3">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Никому</option>
                                    <option value="AK">Васе</option>
                                </select>
                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>
                        </div>

                        <div id="alerts" class="tab-pane">
                            <div>
                                <label>E-mail для оповещений</label> ttt@sdf.ru <a href="#">изменить</a>
                            </div>
                            <div>
                                <label for="settings-select-4">Частота оповещений</label>
                                <select class="form-control" id="settings-select-4">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Никогда</option>
                                    <option value="AK">Васе</option>
                                </select>
                            </div>
                            <div class="checkbox">
                                <label> <input name="settings-checkbox"
                                    type="checkbox" class="ace"> <span class="lbl">
                                        Важные сообщения</span>
                                </label>
                            </div>
                            <div class="checkbox">
                                <label> <input name="settings-checkbox"
                                    type="checkbox" class="ace"> <span class="lbl">
                                        Новые обсуждения</span>
                                </label>
                            </div>
                            <div class="checkbox">
                                <label> <input name="settings-checkbox"
                                    type="checkbox" class="ace"> <span class="lbl">
                                        Новые сообщения</span>
                                </label>
                            </div>
                            <div class="checkbox">
                                <label> <input name="settings-checkbox"
                                    type="checkbox" class="ace"> <span class="lbl">
                                        Личные сообщения</span>
                                </label>
                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>
                        </div>

                        <div id="contacts" class="tab-pane">

                            <div>
                                <label for="settings-input-7">Email</label>
                                <input type="text" id="settings-input-7"/>
                            </div>
                            <div>
                                <label for="settings-input-8">Телефон</label>
                                <input type="text" id="settings-input-8"/>
                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>
                        </div>

                        <div id="family" class="tab-pane">

                            <div>
                                <label for="settings-select-5">Семейное положение</label>

                                <select class="form-control short" id="settings-select-5">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Не женат</option>
                                    <option value="AK">Женат</option>
                                </select>
                            </div>
                            <div>
                                <label for="settings-input-9">Ребенок</label>
                                <input type="text" class="short" id="settings-input-9"/>

                                <label for="settings-select-6" class="no-width">родился</label>
                                <select class="form-control short" id="settings-select-6">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Месяц</option>
                                    <option value="AK">Январь</option>
                                </select>

                                <input type="text" class="short" placeholder="год" id="settings-input-10"/>

                                <div class="settings-add-link"><a href="#">Добавить</a></div>

                            </div>

                            <div>
                                <label for="settings-input-11">Питомец</label>
                                <input type="text" class="short" id="settings-input-11"/>

                                <select class="form-control short" id="settings-select-7">
                                    <option value="">&nbsp;</option>
                                    <option value="AL">Кошка</option>
                                    <option value="AK">Собака</option>
                                </select>

                                <input type="text" class="short" placeholder="порода" id="settings-input-12"/>

                                <div class="settings-add-link"><a href="#">Добавить</a></div>

                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>

                        </div>

                        <div id="interests" class="tab-pane">

                            <div>
                                <label for="settings-textarea-1">Интересы</label>
                                <textarea id="settings-textarea-1"></textarea>
                            </div>
                            <div>
                                <label for="settings-textarea-2">Работа</label>
                                <textarea id="settings-textarea-2"></textarea>
                            </div>

                            <a class="btn btn-sm btn-primary no-border" href="#">Сохранить</a>
                        </div>

                    </div>
                </div>

            </section>
        </div>




