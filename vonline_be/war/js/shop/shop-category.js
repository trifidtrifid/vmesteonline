define(
		'shop-category.min',
		[ 'jquery', 'shop-initThrift.min', 'shop-basket.min', 'shop-common.min',
				'shop-spinner.min','shop-search.min' ],
		function($, thriftModule, basketModule, commonModule, spinnerModule, searchModule) {
            //setCookie('arrayPrevCat',0); setCookie('prevCatCounter',0);  setCookie('catid',0);

            var producers;
            var producersLength;

			function createProductsTableHtml(productsList) {
				var productListLength = productsList.length;
				var productsHtml = '';
				for (i = 0; i < productListLength; i++) {
					var unitName = "";
					if (productsList[i].unitName) {
						unitName = productsList[i].unitName;
					}
                    var producerName,producerId;
                    producers = (producers) ? producers : thriftModule.client.getProducers();
                    producersLength = (producersLength) ? producersLength : producers.length;

                    for(var j = 0; j < producersLength; j++){
                        if(producers[j].id == productsList[i].producerId){
                            producerName = producers[j].name;
                            producerId = producers[j].id;
                            break;
                        }
                    }
					var myPic;
					var commonModule = require('shop-common.min');
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
							+ '?w=40&h=40" alt="'+ productsList[i].name +'"/></div>'
							+ '<span><span class="product-name">'
							+ productsList[i].name
							+ '</span>'
							+ productsList[i].shortDescr
							+ '</span>'
							+ '</a>'
							+ '<div class="modal">'
							+ '</div>'
							+ '</td>'
							+ '<td class="td-producer" data-producerid="'+ producerId +'">'
                            + producerName
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
					var commonModule = require('shop-common.min');
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
                        var zIndex = 50-i*2;
                        shopMenu += '<li data-parentid="'
								+ productCategories[i].parentId
								+ '" data-catid="' + productCategories[i].id + '">'
                                + '<a href="#" style="z-index: '+ zIndex +'" class="btn btn-app btn-info btn-sm">'
								+ '<span>'+ productCategories[i].name + '</span>'
								+ '</a>';

                        if(productCategories[i].logoURLset.length){

                            shopMenu +='<div class="category-label no-init"></div>'
                            +'<div class="category-soc-links" style="z-index: '+ --zIndex +'">';

                            var logoURLset = productCategories[i].logoURLset,
                            logoURLsetLength = productCategories[i].logoURLset.length,
                            imgSrc=" ";

                            for( var j = 0; j < logoURLsetLength ; j ++ ) {
                                if (logoURLset[j].indexOf("vk") != -1) {
                                    imgSrc = "i/vk.png";
                                } else if (logoURLset[j].indexOf("fb") != -1) {
                                    imgSrc = "i/fb.png";
                                }

                                shopMenu += '<a class="category-soc-single" href="'+ logoURLset[j] +'"><img src="'+ imgSrc +'" alt="картинка"/></a>';
                            }
                            shopMenu +="</div>";
                        }

                        shopMenu += '</li>';
					}

					$('.shop-menu ul').html(firstMenuItem).append(shopMenu);

                    var categoryLabelNoInit = $('.category-label.no-init');
                    initCategoryLabelHover(categoryLabelNoInit);
                    categoryLabelNoInit.removeClass('.no-init');

					/* новый список товаров */
					var productsList = thriftModule.client.getProducts(0, 1000, catID).products;
					$('.main-content .catalog table tbody').html("").append(createProductsTableHtml(productsList));
					commonModule.markAddedProduct();

                    var producerDropdownId = $('.producer-dropdown .btn').data('producerid');
                    if(producerDropdownId != 0){
                        var searchModule = require('shop-search.min');
                        searchModule.filterByProducer(producerDropdownId);
                    }

				} catch (e) {
					alert(e + " Функция InitLoadCategory");
				}

				/* подключение событий */
				spinnerModule.initProductsSpinner();
				var basketModule = require('shop-basket.min');
				commonModule.InitProductDetailPopup($('.product-link'));
				basketModule.InitAddToBasket($('.fa-shopping-cart'));
				InitClickOnCategory();
				commonModule.setSidebarHeight();
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

			function InitClickOnCategory() {
				try {
					$('.shop-menu li a').click(
                        function(e,historyBool) {

                            e.preventDefault();

                            // очищаем если был поиск
                            $('.main-content .catalog').removeClass('searched').removeAttr('data-searchWord');

                            var isReturnBtn = $(this).hasClass('shopmenu-back');
                            var categoryid = $(this).parent().data('catid');
                            var urlHash = document.location.hash,
                            categoriesHash;

                            if (isReturnBtn) {
                                categoriesHash = document.location.hash.split(';');
                                categoriesHash.pop();
                                categoriesHash = categoriesHash.join(';');
                                LoadCategoryByURLHash(categoriesHash);

                                if(categoriesHash.length == 0) categoriesHash = " ";

                            } else {
                                if(!urlHash){
                                    categoriesHash = '#cat='+categoryid;
                                }else{
                                    categoriesHash = urlHash+";cat="+categoryid;
                                }
                                InitLoadCategory(categoryid);

                            }

                            var isHistoryNav = (historyBool) ? historyBool : false;
                            if(!isHistoryNav) {
                                var state = {
                                    type: 'category',
                                    categoriesHash: categoriesHash
                                };
                                window.history.pushState(state, null, categoriesHash);
                            }

                        });
				} catch (e) {
					alert(e + " Функция InitClickOnCategory");
				}
			}

            initCategoryLabelHover($('.category-label'));

            function initCategoryLabelHover(selector){
                selector.hover(function(){
                    var categorySocLinks = $(this).find('+.category-soc-links'),
                        categorySocLinksWidth = categorySocLinks.width();
                    //categorySocLinks.css({'width':0,'right': -categorySocLinksWidth+'px'});
                    categorySocLinks.animate({right: -categorySocLinksWidth+'px'},200)
                },function(){
                    var categorySocLinks = $(this).find('+.category-soc-links');
                    categorySocLinks.animate({right: '0'},200)
                });
            }


			return {
				createProductsTableHtml : createProductsTableHtml,
				InitLoadCategory : InitLoadCategory,
				InitClickOnCategory : InitClickOnCategory,
                LoadCategoryByURLHash: LoadCategoryByURLHash
			}
		});