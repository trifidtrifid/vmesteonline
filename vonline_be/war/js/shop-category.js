define(
		'shop-category',
		[ 'jquery', 'shop-initThrift', 'shop-basket', 'shop-common',
				'shop-spinner' ],
		function($, thriftModule, basketModule, commonModule, spinnerModule) {
            //setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

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
							+ '?w=40&h=40" alt="картинка"/></div>'
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
					var productCategories = thriftModule.client.getProductCategories(catID);
					var categoriesLength = productCategories.length;
					var shopMenu = '';
					var firstMenuItem = "";

					if (productCategories[0] && productCategories[0].parentId != 0
							|| productCategories[0] === undefined) {
						firstMenuItem = '<li>' + '<a class="shopmenu-back" href="#">'
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
					var productsList = thriftModule.client.getProducts(0, 1000, catID).products;
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
                commonModule.setCatalogTopOffset();
            }

            function LoadCategoryByURLHash(URLHash){
                var categoriesHistory = URLHash.split(';');
                var categoriesHistoryLength = categoriesHistory.length;
                var prevCategoryIndexPos = categoriesHistoryLength - 1;
                var prevCategoryId;

                if (prevCategoryIndexPos < 0){
                    // загружаем корневую
                    prevCategoryId = 0;
                }else{
                    // загружаем родителя
                    var hashParts = categoriesHistory[prevCategoryIndexPos].split('=');
                    prevCategoryId = hashParts[1];
                }
                InitLoadCategory(prevCategoryId);
            }

/*			function initGetCookie() {
				var commonModule = require('shop-common');
				prevCatCounter = commonModule.getCookie('prevCatCounter');
				if (prevCatCounter !== undefined) {
					parentCounter = parseInt(prevCatCounter);
				}
				var arrayPrevCatCookie = commonModule.getCookie('arrayPrevCat');
				if (arrayPrevCatCookie !== undefined) {
					prevParentId = arrayPrevCatCookie.split(',');
				}
			}*/

			function InitClickOnCategory() {
                //var commonModule = require('shop-common');

                //console.log(commonModule.getCookie('arrayPrevCat')+" "+commonModule.getCookie('prevCatCounter')+" "+commonModule.getCookie('catid'));

				/*if (prevCatCounter === undefined) {
					initGetCookie();
				}*/
				try {
					$('.shop-menu li a').click(
                        function(e) {
                            e.preventDefault();
                            var isReturnBtn = $(this).hasClass('shopmenu-back');
                            var categoryid = $(this).parent().data('catid');
                            var urlHash = document.location.hash,
                            categoriesHash;


                            if (isReturnBtn) {
                                /*var categoryParentId = $(this).parent().data('parentid');
                                alert(categoryParentId);
                                InitLoadCategory(categoryParentId);*/

                                //window.history.back();

                                /*InitLoadCategory(prevParentId[parentCounter]);
                                commonModule.setCookie('catid',prevParentId[parentCounter]);
                                parentCounter--;
                                commonModule.setCookie('arrayPrevCat',prevParentId);
                                commonModule.setCookie('prevCatCounter',parentCounter);*/

                                /*var categoriesHistory = urlHash.split(';');
                                var categoriesHistoryLength = categoriesHistory.length;
                                var prevCategoryIndexPos = categoriesHistoryLength - 2;
                                var prevCategoryIndex;

                                if (prevCategoryIndexPos < 0){
                                    prevCategoryIndex = 0;
                                }else{
                                    var hashParts = categoriesHistory[prevCategoryIndexPos].split('=');
                                    prevCategoryIndex = hashParts[1];
                                }
                                InitLoadCategory(prevCategoryIndex);*/

                                //window.history.back();
                                categoriesHash = document.location.hash.split(';');
                                categoriesHash.pop();
                                categoriesHash = categoriesHash.join(';');
                                LoadCategoryByURLHash(categoriesHash);

                            } else {
                                /*parentCounter++;
                                prevParentId[parentCounter] = $(this).parent().data('parentid');
                                InitLoadCategory($(this).parent().data('catid'));
                                commonModule.setCookie('catid', $(this).parent().data('catid'));
                                commonModule.setCookie('arrayPrevCat',prevParentId);
                                commonModule.setCookie('prevCatCounter',parentCounter);*/

                                if(!urlHash){
                                    categoriesHash = '#cat='+categoryid;
                                }else{
                                    categoriesHash = urlHash+";cat="+categoryid;
                                }
                                InitLoadCategory(categoryid);

                            }

                            var state = {
                                type : 'category',
                                categoriesHash: categoriesHash
                            };
                            window.history.pushState(state,null,'shop.jsp'+categoriesHash);
                        });
				} catch (e) {
					alert(e + " Функция InitClickOnCategory");
				}
			}

			return {
				createProductsTableHtml : createProductsTableHtml,
				InitLoadCategory : InitLoadCategory,
				InitClickOnCategory : InitClickOnCategory,
                LoadCategoryByURLHash: LoadCategoryByURLHash
			}
		});