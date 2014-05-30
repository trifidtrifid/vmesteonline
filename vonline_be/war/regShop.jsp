<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>Регистрация магазина</title>

		<meta name="description" content="User login page" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />

        <link rel="stylesheet" href="build/style.min.css"/>
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
								<div id="signup-box" class="signup-box signup-shop widget-box no-border visible">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header green lighter bigger">
												<i class="icon-group blue"></i>
												Регистрация магазина
											</h4>

											<div class="space-6"></div>

											<form>
												<fieldset>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" id="email" class="form-control" placeholder="Название магазина" />
															<i class="icon-envelope"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <textarea name="descr" id="" cols="30" rows="10">Описание</textarea>
															<%--<input type="text" id="login" class="form-control" placeholder="Логин" />--%>
															<i class="icon-user"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <textarea name="descr" id="" cols="30" rows="10">Адрес</textarea>
															<%--<input type="password" id="pass" class="form-control" placeholder="Адрес" />--%>
															<i class="icon-lock"></i>
														</span>
													</label>


                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <div class="ace-file-input">
                                                                <input type="file" id="id-input-file-2">
                                                                <label class="file-label" data-title="Выбрать">
                                                                    <span class="file-name" data-title="Логотип">
                                                                        <i class="icon-upload-alt"></i>
                                                                    </span>
                                                                </label>
                                                                <a class="remove" href="#"><i class="icon-remove"></i></a>
                                                            </div>

															<%--<input type="email" id="email" class="form-control" placeholder="Логотип" />--%>
															<%--<i class="icon-envelope"></i>--%>
														</span>
                                                    </label>

                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" id="email" class="form-control" placeholder="Стоимость доставки" />
															<i class="icon-envelope"></i>
														</span>
                                                    </label>

                                                    <span class="email-alert error-info"></span>

                                                    <div class="space"></div>

													<div class="clearfix">

														<button type="button" class="width-65 pull-right btn btn-sm btn-success">
															Регистрация
															<i class="icon-arrow-right icon-on-right"></i>
														</button>
													</div>
												</fieldset>
											</form>
										</div>

										<%--<div class="toolbar center">
											<a href="#" class="back-to-login-link">
												<i class="icon-arrow-left"></i>
												Назад
											</a>
										</div>--%>
									</div><!-- /widget-body -->
								</div><!-- /signup-box -->
							</div><!-- /position-relative -->
						</div>
					</div><!-- /.col -->
				</div><!-- /.row -->
			</div>
		</div><!-- /.main-container -->

        <!-- файлы thrift  -->
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
        <!-- --- -->
        <%--<script type="text/javascript" data-main="/build/shop.min.js" src="/js/require.min.js"></script>--%>
	</body>
</html>
