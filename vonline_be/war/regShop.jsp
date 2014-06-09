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
								<div class="reg-shop signup-box widget-box no-border visible">
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
															<input type="text" id="name" class="form-control" placeholder="Название магазина" />
															<i class="fa fa-shopping-cart"></i>
														</span>
													</label>

                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="email" id="email" class="form-control" placeholder="Email владельца" />
															<i class="icon-envelope"></i>
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
                                                            <textarea name="address" id="address" cols="30" rows="10">Адрес</textarea>
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

        <!-- файлы thrift -->
        <script src="js/thrift.js" type="text/javascript"></script>
        <script src="gen-js/bedata_types.js" type="text/javascript"></script>

        <script src="gen-js/shop_types.js" type="text/javascript"></script>
        <script src="gen-js/ShopFEService.js" type="text/javascript"></script>
        <script src="gen-js/shop.bo_types.js" type="text/javascript"></script>
        <script src="gen-js/ShopBOService.js" type="text/javascript"></script>

        <script src="gen-js/authservice_types.js" type="text/javascript"></script>
        <script src="gen-js/AuthService.js" type="text/javascript"></script>
        <script src="gen-js/userservice_types.js" type="text/javascript"></script>
        <script src="gen-js/UserService.js" type="text/javascript"></script>
        <!-- -->

        <script src="js/lib/bootstrap.min.js"></script>
        <script src="js/lib/ace-elements.min.js"></script>

        <%--<script type="text/javascript" data-main="/build/build.js" src="/js/require.min.js"></script>--%>
        <script>
            $(document).ready(function(){
                var transport = new Thrift.Transport("/thrift/ShopBOService");
                var protocol = new Thrift.Protocol(transport);
                var clientBO = new com.vmesteonline.be.shop.bo.ShopBOServiceClient(protocol);

                transport = new Thrift.Transport("/thrift/ShopService");
                protocol = new Thrift.Protocol(transport);
                var client = new com.vmesteonline.be.shop.ShopFEServiceClient(protocol);

                $('.reg-shop .btn-success').click(function(e){
                    e.preventDefault();

                    var name = $('#name').val(),
                    descr = $('#descr').val(),
                    logoURL = $('.ace-file-input .file-name img').css('background-image'),
                    address = $('#address').val();

                    $('.email-alert').removeClass('info-good').addClass('error-info');

                    if(name == ""){
                        $('.error-info').text('Вы не указали имя.').show();
                    }else if(descr == ""){
                        $('.error-info').text('Вы не указали описание.').show();
                    }else if(address == ""){
                        $('.error-info').text('Вы не указали адрес.').show();
                    }else if(!logoURL){
                        $('.error-info').text('Вы не выбрали доготип.').show();
                    }else{
                        try{
                            var deliveryAddress = client.createDeliveryAddress(address,0,0,0);
                            $('.error-info').hide();

                            var shop = new com.vmesteonline.be.shop.Shop;
                            shop.id = 0;
                            shop.name = name;
                            shop.descr = descr;
                            shop.address = deliveryAddress;
                            shop.logoURL = logoURL;

                            var shopId = clientBO.registerShop(shop);

                            var ownerEmail = $('#email').val();
                            clientBO.setUserShopRole(shopId ,ownerEmail,3);

                            $('.email-alert').removeClass('error-info').addClass('info-good').text("Магазин успешно зарегистрирован.").show();
                        } catch(e){
                            $('.error-info').text('Вы указали не существующий адрес.').show();
                        }
                    }

                });

                $('#id-input-file-2').ace_file_input({
                    style:'well',
                    btn_choose:'Загрузить логотип',
                    btn_change:null,
                    no_icon:'icon-cloud-upload',
                    droppable:true,
                    thumbnail:'large'

                }).on('change', function(){
                            $('.error-info').hide();
                        });

            });
        </script>
	</body>
</html>
