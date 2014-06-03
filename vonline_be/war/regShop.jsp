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

	<body class="login-layout signup-shop">
		<div class="main-container">
			<div class="main-content">
				<div class="row">
					<div class="col-sm-10 col-sm-offset-1">
						<div class="login-container">

							<div class="space-6"></div>

							<div class="position-relative">
								<div id="signup-box" class="signup-box widget-box no-border visible">
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
															<i class="fa fa-shopping-cart"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <textarea name="descr" id="descr" cols="30" rows="10">Описание</textarea>
															<i class="fa fa-reorder"></i>
														</span>
													</label>

													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <textarea name="descr" id="address" cols="30" rows="10">Адрес</textarea>
															<i class="fa fa-map-marker"></i>
														</span>
													</label>


                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
                                                            <div class="ace-file-input">
                                                                <input type="file" id="id-input-file-2">
                                                            </div>

														</span>
                                                    </label>

                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" id="price-delivery" class="form-control" placeholder="Стоимость доставки" />
															<i class="fa fa-dropbox"></i>
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
        <script src="js/lib/jquery-ui-1.10.3.custom.min.js"></script>
        <script src="js/lib/jquery.ui.touch-punch.min.js"></script>
        <script src="js/lib/chosen.jquery.min.js"></script>
        <script src="js/lib/fuelux/fuelux.spinner.min.js"></script>
        <script src="js/lib/date-time/bootstrap-datepicker.min.js"></script>
        <script src="js/lib/date-time/bootstrap-timepicker.min.js"></script>
        <script src="js/lib/date-time/moment.min.js"></script>
        <script src="js/lib/date-time/daterangepicker.min.js"></script>
        <%--<script src="js/lib/bootstrap-colorpicker.min.js"></script>--%>
        <script src="js/lib/jquery.knob.min.js"></script>
        <script src="js/lib/jquery.autosize.min.js"></script>
        <script src="js/lib/jquery.inputlimiter.1.3.1.min.js"></script>

        <script src="js/lib/jquery.maskedinput.min.js"></script>
        <script src="js/lib/bootstrap-tag.min.js"></script>

        <script src="js/lib/bootstrap.min.js"></script>
        <script src="js/lib/ace-extra.min.js"></script>
        <script src="js/lib/ace-elements.min.js"></script>
        <script src="js/lib/ace.min.js"></script>

        <script>
            $(document).ready(function(){
                $('#id-input-file-2').ace_file_input({
                    style:'well',
                    btn_choose:'Загрузить логотип',
                    btn_change:null,
                    no_icon:'icon-cloud-upload',
                    droppable:true,
                    thumbnail:'large'//large | fit
                    //,icon_remove:null//set null, to hide remove/reset button
                    /**,before_change:function(files, dropped) {
						//Check an example below
						//or examples/file-upload.html
						return true;
					}*/
                    /**,before_remove : function() {
						return true;
					}*/
                    ,
                    preview_error : function(filename, error_code) {
                        //name of the file that failed
                        //error_code values
                        //1 = 'FILE_LOAD_FAILED',
                        //2 = 'IMAGE_LOAD_FAILED',
                        //3 = 'THUMBNAIL_FAILED'
                        //alert(error_code);
                    }

                }).on('change', function(){
                            //console.log($(this).data('ace_input_files'));
                            //console.log($(this).data('ace_input_method'));
                        });

                /*$('#id-input-file-2').ace_file_input({
                    no_file:'Выберите логотип',
                    btn_choose:'Обзор',
                    btn_change:'Заменить',
                    droppable:false,
                    onchange:null,
                    thumbnail: 'large' //false //| true | large
                    //whitelist:'gif|png|jpg|jpeg'
                    //blacklist:'exe|php'
                    //onchange:''
                    //
                });*/
            });
        </script>
        <%--<script type="text/javascript" data-main="/build/shop.min.js" src="/js/require.min.js"></script>--%>
	</body>
</html>
