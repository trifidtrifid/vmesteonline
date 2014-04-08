define(
    'shop-category',
    ['jquery','shop-spinner','shop-common','shop-addProduct'],
    function( $,spinnerModule,commonModule,addProduct ){

        function createProductsTableHtml(productsList){
            var productListLength = productsList.length;
            var productsHtml = '';
            var productDetails;
            for (i = 0; i < productListLength; i++){
                productDetails = client.getProductDetails(productsList[i].id);
                var unitName = "";
                if (productsList[i].unitName){unitName = productsList[i].unitName;}
                productsHtml += '<tr data-productid="'+ productsList[i].id +'">'+
                    '<td>'+
                    '<a href="#" class="product-link">'+
                    '<img src="'+ productsList[i].imageURL +'" alt="картинка"/>'+
                    '<span><span>'+ productsList[i].name +'</span>'+ productsList[i].shortDescr +'</span>'+
                    '</a>'+
                    '<div class="modal">'+
                    '</div>'+
                    '</td>'+
                    '<td class="product-price">'+ productsList[i].price  +'</td>'+
                    '<td>'+
                    '<input type="text" data-step="'+ productsList[i].minClientPack +'" class="input-mini spinner1" /> '+
                    '</td>'+
                    '<td>'+ '<span class="unit-name">'+ unitName +'</span></td>'+
                    '<td>'+
                    '<a href="#" title="Добавить в корзину" class="fa fa-shopping-cart"></a>'+
                    '</td>'+
                    '</tr>';
            }
            return productsHtml;
        }

        function InitLoadCategory(catID){
            try{
                /* замена меню категорий */

                var productCategories = client.getProductCategories(catID);
                var categoriesLength = productCategories.length;
                var shopMenu = '';
                var firstMenuItem = "";

                if (productCategories[0] && productCategories[0].parentId != 0 || productCategories[0] === undefined){
                    firstMenuItem = '<li>'+
                        '<a href="#">'+
                        '<i class="fa fa-reply-all"></i>'+
                        '<span>Назад</span>'+
                        '</a>'+
                        '</li>';
                }

                for(var i = 0; i < categoriesLength; i++){
                    shopMenu += '<li data-parentid="'+ productCategories[i].parentId +'" data-catid="'+ productCategories[i].id +'">'+
                        '<a href="#">'+
                        '<i class="fa fa-beer"></i>'+
                        '<span>'+ productCategories[i].name +'</span>'+
                        '</a>'+
                        '</li>';
                }

                $('.shop-menu ul').html(firstMenuItem).append(shopMenu);

                /* новый список товаров */
                var productsList = client.getProducts(0,10,catID).products;
                $('.main-content .catalog table tbody').html("").append(createProductsTableHtml(productsList));

            }catch(e){
                alert(e+" Функция InitLoadCategory");
            }

            /* подключение событий */
            spinnerModule.initProductsSpinner();
            commonModule.InitProductDetailPopup($('.product-link'));
            addProduct.InitAddToBasket($('.fa-shopping-cart'));
            InitClickOnCategory();

        }

        function InitClickOnCategory(){
            try{
                $('.shop-menu li a').click(function(e){
                    e.preventDefault();
                    if ($(this).hasClass('fa-reply-all')){
                        InitLoadCategory(prevParentId[commonModule.parentCounter]);
                        commonModule.setCookie('catid',commonModule.prevParentId[commonModule.parentCounter]);
                        commonModule.parentCounter--;
                        commonModule.setCookie('arrayPrevCat',commonModule.prevParentId);
                        commonModule.setCookie('prevCatCounter',commonModule.parentCounter);

                    }
                    else {
                        commonModule.parentCounter++;
                        //console.log(prevParentId[parentCounter]);
                        commonModule.prevParentId[commonModule.parentCounter] = $(this).parent().data('parentid');
                        //console.log($(this).parent().data('catid'));
                        InitLoadCategory($(this).parent().data('catid'));
                        commonModule.setCookie('catid',$(this).parent().data('catid'));
                        commonModule.setCookie('arrayPrevCat',commonModule.prevParentId);
                        commonModule.setCookie('prevCatCounter',commonModule.parentCounter);
                    }
                });
            }catch(e){
                alert(e+" Функция InitClickOnCategory");
            }
        }
    }
);