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

                                                    <label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="text" id="price-delivery" class="form-control" placeholder="Стоимость доставки" />
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

                    var deliveryAddress = client.createDeliveryAddress("Ленинградская 7 ",0,0,0);

                    /*var deliveryAddress = new com.vmesteonline.be.PostalAddress();

                    var country = new com.vmesteonline.be.Country();
                    country.id = 9999999999999999;
                    country.name = "Россия";
                    var city = new com.vmesteonline.be.City();
                    city.id = 8888888888888888;
                    city.countryId = 9999999999999999;
                    city.name = "Питер";
                    var street = new com.vmesteonline.be.Street();
                    street.id = 7777777777777777;
                    street.cityId = 8888888888888888;
                    street.name = "улица";
                    var building = new com.vmesteonline.be.Building();
                    building.id = 6666666666666666;
                    building.streetId = 7777777777777777;
                    building.fullNo = "3";

                    deliveryAddress.country = country;
                    deliveryAddress.city = city;
                    deliveryAddress.street = street;
                    deliveryAddress.building = building;
                    deliveryAddress.staircase = 0;
                    deliveryAddress.floor= 0;
                    deliveryAddress.flatNo = 1;*/

                    var shop = new com.vmesteonline.be.shop.Shop;

                    var name = $('#name').val();
                    var descr = $('#descr').val();
                    var logoURL = $('#descr').val();

                    shop.id = 1111111111111111;
                    shop.name = name;
                    shop.descr = descr;
                    shop.address = deliveryAddress;
                    shop.logoURL = logoURL;
                    shop.ownerId = 4714705859903488;

                    clientBO.registerShop(shop);

                });

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

            });
        </script>
	</body>
</html>
