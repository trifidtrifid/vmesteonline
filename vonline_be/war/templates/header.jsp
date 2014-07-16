<%@ page contentType="text/html;charset=UTF-8" language="java"%>


		<div class="navbar navbar-default" id="navbar">
			<script type="text/javascript">
				try {
					ace.settings.check('navbar', 'fixed')
				} catch (e) {
				}
			</script>

			<div class="navbar-container" id="navbar-container">
				<div class="navbar-header pull-left">
                    <c:if test="${!isAdminka}">

                        <c:choose>
                            <c:when test="${shop.hostName != null && isProduction}">
                                <a href="http://<c:out value="${shop.hostName+URLrest}"/>/shop/" class="navbar-brand">
                            </c:when>
                            <c:otherwise>
                                <a href="/shop/<c:out value="${shop.id}"/>" class="navbar-brand">
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${logoURL!= null}">
                                <img src="<c:out value="${logoURL}" />" alt="лого">
                            </c:when>
                            <c:otherwise>
                                <img src="<c:out value="${noPhotoPic}" />" alt="лого">
                            </c:otherwise>
                        </c:choose>

					            </a>

                        <c:if test="${shopPages.aboutPageContentURL != null && shopPages.aboutPageContentURL != ''}">

                            <c:choose>
                                <c:when test="${shop.hostName != null && isProduction}">
                                    <a href="about/${shopPages.aboutPageContentURL}" class="about-shop-link header-link">О магазине</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/about/${shop.id}" class="about-shop-link header-link">О магазине</a>
                                </c:otherwise>
                            </c:choose>

                        </c:if>
                        <c:if test="${shopPages.conditionsPageContentURL != null && shopPages.conditionsPageContentURL != ''}">

                            <c:choose>
                                <c:when test="${shop.hostName != null && isProduction}">
                                    <a href="/${shopPages.conditionsPageContentURL}" class="terms-of-orders header-link">Условия</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/terms/${shop.id}" class="terms-of-orders header-link">Условия</a>
                                </c:otherwise>
                            </c:choose>

                        </c:if>
                        <c:if test="${shopPages.deliveryPageContentURL != null && shopPages.deliveryPageContentURL != ''}">
                            <c:choose>
                                <c:when test="${shop.hostName != null && isProduction}">
                                    <a href="/${shopPages.deliveryPageContentURL}" class="terms-of-delivery header-link">Доставка</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/delivery-terms/${shop.id}" class="terms-of-delivery header-link">Доставка</a>
                                </c:otherwise>
                            </c:choose>

                        </c:if>
                    </c:if>
				</div>

				<div class="navbar-header pull-right" role="navigation">
					<ul class="nav ace-nav">

                        <li>
                            <c:choose>
                                <c:when test="${isProduction}">
                                <a class="btn btn-info no-border no-prevent" href="http://voclub.co<c:out value="${URLrest}"/>">
                                </c:when>
                                <c:otherwise>
                                    <a class="btn btn-info no-border no-prevent" href="/">
                                </c:otherwise>
                            </c:choose>

                            Главная </a></li>
                        <c:if test="${!isAdminka}">
                            <li>
                                <c:choose>
                                    <c:when test="${shop.hostName != null && isProduction}">
                                    <a class="btn btn-info no-border back-to-shop shop-trigger no-prevent" href="http://<c:out value="${shop.hostName+URLrest}"/>/shop/">
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn btn-info no-border back-to-shop shop-trigger no-prevent" href="/shop/<c:out value="${shop.id}"/>">
                                    </c:otherwise>
                                </c:choose>

                                    Магазин </a></li>
                            <li><a class="btn btn-info no-border go-to-orders shop-trigger" href="#orders-history">
                                Заказы </a></li>


                            <li>

                                <c:choose>
                                    <c:when test="${shop.hostName != null && isProduction}">
                                        <a class="btn btn-info no-border bo-link <c:out value="${hiddenClass}"/>"
                                           href="http://<c:out value="${shop.hostName+URLrest}"/>/backoffice/">
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn btn-info no-border bo-link <c:out value="${hiddenClass}"/>"
                                           href="/backoffice/<c:out value="${shop.id}"/>">
                                    </c:otherwise>
                                </c:choose>

                                Бэкоффис</a></li>
                        </c:if>

						<li class="user-short light-blue">
                            <c:choose>
								<c:when test="${isAuth}">
									<a data-toggle="dropdown" href="#" class="dropdown-toggle">
                                        <span class="user-info">
                                            <c:out value="${firstName}" />
                                            <c:out value="${lastName}" />
									    </span>
                                            <i class="icon-caret-down"></i>
									</a>
								</c:when>
								<c:otherwise>
									<a href="#" class="dropdown-toggle no-login">
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

                                <li><a href="#"> <i class="icon-off"></i> Выход
                                </a></li>
                            </ul>
                        </li>
					</ul>

				</div>

			</div>

		</div>

