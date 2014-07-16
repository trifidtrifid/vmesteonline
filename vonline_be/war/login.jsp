<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Логин</title>

<meta name="description" content="User login page" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link rel="stylesheet" href="build/shop.min.css" />
<script src="./js/lib/jquery-2.1.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
	globalUserAuth = false;
</script>

</head>

<body class="login-layout">
	<div class="main-container">
		<div class="main-content">
			<div class="row">
				<div class="col-sm-10 col-sm-offset-1">
					<div class="login-container">

						<div class="space-6"></div>

						<div class="position-relative">
							<button type="button" class="login-close pull-right hide" data-dismiss="modal" aria-hidden="true">&times;</button>

							<div id="login-box" class="login-box visible widget-box no-border">
								<div class="widget-body">
									<div class="widget-main">
										<%--<h4 class="header blue lighter bigger">
												<i class="icon-coffee green"></i>
												Вход
											</h4>--%>

										<div class="space-6"></div>

										<form>
											<fieldset>
												<a class="btn btn-sm no-border btn-primary width-100 btn-vk"
													href="https://oauth.vk.com/authorize?client_id=4463293&redirect_uri=https%3A%2F%2F1-dot-vmesteonline.appspot.com%2Foauth&scope=4194305&display=popup&v=5.21&state="> Войти через
													Вконтакте</a>
												<div class="or align-center">или</div>
												<label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="text" id="uname" class="form-control" placeholder="Ваш email" /> <i
														class="icon-user"></i>
												</span>
												</label> <label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="password" id="password" class="form-control" placeholder="Пароль" /> <i
														class="icon-lock"></i>
												</span>
												</label>

												<div class="error-info login-error"></div>

												<div class="space"></div>

												<div class="clearfix">

													<button type="button" class="width-50 pull-right btn no-border btn-sm btn-primary btn-enter">
														<i class="icon-key"></i> Войти
													</button>
												</div>

												<div class="space-4"></div>
											</fieldset>
										</form>


									</div>
									<!-- /widget-main -->

									<div class="toolbar clearfix">
										<div>
											<a href="#" class="forgot-password-link"> <i class="icon-arrow-left"></i> Забыл пароль
											</a>
										</div>

										<div>
											<a href="#" class="user-signup-link"> Регистрация <i class="icon-arrow-right"></i>
											</a>
										</div>

									</div>

									<div>
										<a href="#" class="user-signup-link"> Регистрация <i class="icon-arrow-right"></i>
										</a>
									</div>
								</div>
							</div>
							<!-- /widget-body -->
						</div>
						<!-- /login-box -->

						<div id="forgot-box" class="forgot-box widget-box no-border">
							<div class="widget-body">
								<div class="widget-main">
									<h4 class="header red lighter bigger">
										<i class="icon-key"></i> Восстановление пароля
									</h4>

									<div class="space-6"></div>
									<p>Введите ваш email и получите код подтверждения.</p>

									<form>
										<fieldset>
											<label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="email" id="email-forgot" class="form-control" placeholder="Email" /> <i
													class="icon-envelope"></i>
											</span>
											</label>
											<div class="error-info email-forgot-error"></div>

											<div class="clearfix">
												<button type="button" class="width-50 pull-right btn btn-sm btn-danger sendConfirmCode">
													<%--<i class="icon-lightbulb"></i>--%>
													Получить код
												</button>
											</div>
											<div class="space"></div>

											<label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="text" id="confirmCode" class="form-control" placeholder="Код подтверждения" /> <i
													class="icon-key"></i>
											</span>
											</label> <label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="password" id="password-new" class="form-control" placeholder="Новый пароль" />
													<i class="icon-lock"></i>
											</span>
											</label>
											<div class="error-info password-new-error"></div>

											<div class="clearfix">
												<button type="button" class="width-50 pull-right btn btn-sm btn-danger useConfirmCode">
													<%--<i class="icon-lightbulb"></i>--%>
													Подтвердить
												</button>
											</div>
										</fieldset>
									</form>
								</div>
								<!-- /widget-main -->

								<div class="toolbar center">
									<a href="#" class="back-to-login-link"> Назад <i class="icon-arrow-right"></i>
									</a>
								</div>
							</div>
							<!-- /widget-body -->
						</div>
						<!-- /forgot-box -->

						<div id="signup-box" class="signup-box widget-box no-border">
							<div class="widget-body">
								<div class="widget-main">
									<h4 class="header green lighter bigger">
										<i class="icon-group blue"></i> Регистрация
									</h4>

									<div class="space-6"></div>

									<form>
										<fieldset>
											<label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="email" id="email" class="form-control" placeholder="Email" /> <i
													class="icon-envelope"></i>
											</span>
											</label> <label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="text" id="login" class="form-control" placeholder="Логин" /> <i
													class="icon-user"></i>
											</span>
											</label> <label class="block clearfix"> <span class="block input-icon input-icon-right"> <input type="password" id="pass" class="form-control" placeholder="Пароль" /> <i
													class="icon-lock"></i>
											</span>
											</label> <span class="email-alert error-info"></span>

											<div class="space"></div>

											<div class="clearfix">
												<%--<button type="reset" class="width-30 pull-left btn btn-sm">
															<i class="icon-refresh"></i>
															Сброс
														</button>--%>

												<button type="button" class="width-65 pull-right btn btn-sm btn-success">
													Регистрация <i class="icon-arrow-right icon-on-right"></i>
												</button>
											</div>
										</fieldset>
									</form>
								</div>

								<div class="toolbar center">
									<a href="#" class="back-to-login-link"> <i class="icon-arrow-left"></i> Назад
									</a>
								</div>
							</div>
							<!-- /widget-body -->
						</div>
						<!-- /signup-box -->
					</div>
					<!-- /position-relative -->
				</div>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->
	</div>
	</div>
	<!-- /.main-container -->

	<script type="text/javascript" data-main="/build/build.js" src="/js/shop/require.min.js"></script>
</body>
</html>
