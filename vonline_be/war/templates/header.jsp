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
					<a href="/" class="navbar-brand">
                            <img src="<c:out value="${logoURL}" />" alt="лого">
					</a>
					<!-- /.brand -->
				</div>
				<!-- /.navbar-header -->

				<div class="navbar-header pull-right" role="navigation">
					<ul class="nav ace-nav">

						<li class="back-to-shop shop-trigger"><a class="btn btn-info no-border" href="http://127.0.0.1:8888/shop/<c:out value="${shopID}" />">
								Магазин </a></li>
                        <li><a class="btn btn-info no-border go-to-orders shop-trigger" href="#">
                            Заказы </a></li>


                        <li><a class="btn btn-info no-border bo-link
                        <c:if test="${userRole != 'BACKOFFICER' && userRole != 'ADMIN'}">
                        hidden
                        </c:if>
                        " href="backoffice.jsp">
                            Админка</a></li>

						<li class="user-short light-blue">
                            <c:choose>
								<c:when test="${auth}">
									<a data-toggle="dropdown" href="#" class="dropdown-toggle">
										<%--<img class="nav-user-photo" src="i/avatars/user.jpg" alt="Jason's Photo" />--%>
                                        <span class="user-info">
                                            <c:out value="${firstName}" />
                                            <c:out value="${lastName}" />
									    </span>
                                            <i class="icon-caret-down"></i>
									</a>
								</c:when>
								<c:otherwise>
									<a data-toggle="dropdown" href="#" class="dropdown-toggle no-login">
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
					<!-- /.ace-nav -->
				</div>
				<!-- /.navbar-header -->
			</div>
			<!-- /.container -->
		</div>

