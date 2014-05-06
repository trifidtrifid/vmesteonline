define(
		'shop-category',
		[ 'jquery', 'shop-initThrift', 'shop-basket', 'shop-common',
				'shop-spinner' ],
		function($, thriftModule, basketModule, commonModule, spinnerModule) {

			var prevParentId = [], parentCounter = 0, prevCatCounter;

			// var commonModule = require('shop-common');
			/*
			 * if(commonModule){ alert('1'); var prevCatCounter =
			 * commonModule.getCookie('prevCatCounter'); if (prevCatCounter !==
			 * undefined){ parentCounter = parseInt(prevCatCounter); } var
			 * arrayPrevCatCookie = commonModule.getCookie('arrayPrevCat'); if
			 * (arrayPrevCatCookie !== undefined){ prevParentId =
			 * arrayPrevCatCookie.split(','); } }
			 */

			function createProductsTableHtml(productsList) {
				var productListLength = productsList.length;
				var productsHtml = '';
				//var productDetails;
				for (i = 0; i < productListLength; i++) {
					//productDetails = thriftModule.client.getProductDetails(productsList[i].id);
					var unitName = "";
					if (productsList[i].unitName) {
						unitName = productsList[i].unitName;
					}
					var myPic;
					var commonModule = require('shop-common');
					(productsList[i].imageURL) ? myPic = productsList[i].imageURL
							: myPic = commonModule.noPhotoPic;
					productsHtml += '<tr class="product" data-prepack="'
							+ productsList[i].prepackRequired
							+ '" data-productid="'
							+ productsList[i].id
							+ '">'
							+ '<td>'
							+ '<a href="#" class="product-link">'
							+ '<div class="product-pic"><img src="'
							+ myPic
							+ '" alt="картинка"/></div>'
							+ '<span><span class="product-name">'
							+ productsList[i].name
							+ '</span>'
							+ productsList[i].shortDescr
							+ '</span>'
							+ '</a>'
							+ '<div class="modal">'
							+ '</div>'
							+ '</td>'
							+ '<td class="product-price">'
							+ productsList[i].price
							+ '</td>'
							+ '<td class="td-spinner">'
							+ '<input type="text" data-step="'
							+ productsList[i].minClientPack
							+ '" class="input-mini spinner1" /> '
							+ '<span class="added-text">добавлен</span>'
							+ '</td>'
							+ '<td>'
							+ '<span class="unit-name">'
							+ unitName
							+ '</span></td>'
							+ '<td>'
							+ '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>'
							+ '<span href="#" title="Продукт уже у вас в корзине" class="fa fa-check"></span>'
							+ '</td>' + '</tr>';
				}
				return productsHtml;
			}

			function InitLoadCategory(catID) {
				try {
					/* замена меню категорий */
					var commonModule = require('shop-common');
					var productCategories = thriftModule.client
							.getProductCategories(catID);
					var categoriesLength = productCategories.length;
					var shopMenu = '';
					var firstMenuItem = "";

					if (productCategories[0]
							&& productCategories[0].parentId != 0
							|| productCategories[0] === undefined) {
						firstMenuItem = '<li>' + '<a href="#">'
								+ '<i class="fa fa-reply-all"></i>'
								+ '<span>Назад</span>' + '</a>' + '</li>';
					}

					for (var i = 0; i < categoriesLength; i++) {
						shopMenu += '<li data-parentid="'
								+ productCategories[i].parentId
								+ '" data-catid="' + productCategories[i].id
								+ '">' + '<a href="#">'
								+ '<i class="fa fa-beer"></i>' + '<span>'
								+ productCategories[i].name + '</span>'
								+ '</a>' + '</li>';
					}

					$('.shop-menu ul').html(firstMenuItem).append(shopMenu);

					/* новый список товаров */
					var productsList = thriftModule.client.getProducts(0, 1000,
							catID).products;
					$('.main-content .catalog table tbody').html("").append(
							createProductsTableHtml(productsList));
					commonModule.markAddedProduct();

				} catch (e) {
					alert(e + " Функция InitLoadCategory");
				}

				/* подключение событий */
				spinnerModule.initProductsSpinner();
				var basketModule = require('shop-basket');
				commonModule.InitProductDetailPopup($('.product-link'));
				basketModule.InitAddToBasket($('.fa-shopping-cart'));
				InitClickOnCategory();
				commonModule.setSidebarHeight();
			}

			function initGetCookie() {
				var commonModule = require('shop-common');
				prevCatCounter = commonModule.getCookie('prevCatCounter');
				if (prevCatCounter !== undefined) {
					parentCounter = parseInt(prevCatCounter);
				}
				var arrayPrevCatCookie = commonModule.getCookie('arrayPrevCat');
				if (arrayPrevCatCookie !== undefined) {
					prevParentId = arrayPrevCatCookie.split(',');
				}
			}

			function InitClickOnCategory() {
				if (prevCatCounter === undefined) {
					initGetCookie();
				}
				try {
					$('.shop-menu li a').click(
                        function(e) {
                            e.preventDefault();
                            var isReturnBtn = $(this).find(
                                    '.fa-reply-all').length;
                            var commonModule = require('shop-common');

                            if (isReturnBtn) {
                                // alert('1-1 '+parentCounter);
                                InitLoadCategory(prevParentId[parentCounter]);
                                commonModule
                                        .setCookie(
                                                'catid',
                                                prevParentId[parentCounter]);
                                parentCounter--;
                                // alert('1-2 '+parentCounter);
                                commonModule.setCookie(
                                        'arrayPrevCat',
                                        prevParentId);
                                commonModule.setCookie(
                                        'prevCatCounter',
                                        parentCounter);
                            } else {
                                // alert('2-1 '+parentCounter);
                                parentCounter++;
                                // alert('2-2 '+parentCounter);
                                prevParentId[parentCounter] = $(
                                        this).parent().data(
                                        'parentid');
                                InitLoadCategory($(this).parent()
                                        .data('catid'));
                                commonModule.setCookie('catid', $(
                                        this).parent()
                                        .data('catid'));
                                commonModule.setCookie(
                                        'arrayPrevCat',
                                        prevParentId);
                                commonModule.setCookie(
                                        'prevCatCounter',
                                        parentCounter);
                            }
									});
				} catch (e) {
					alert(e + " Функция InitClickOnCategory");
				}
			}

			return {
				createProductsTableHtml : createProductsTableHtml,
				InitLoadCategory : InitLoadCategory,
				InitClickOnCategory : InitClickOnCategory,
			}
		});