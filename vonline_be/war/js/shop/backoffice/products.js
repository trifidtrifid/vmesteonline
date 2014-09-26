define(
    'products',
    ['jquery','shop-initThrift','bo-common'],
    function( $,thriftModule,boCommon){

        var isProductInitSet = 0,
            isCategoryInitSet = 0,
            isProducerInitSet = 0,
            isEditInitSet = 0;

        function initEdit(){
            var shopId = $('.backoffice.dynamic').attr('id'),
                allCategories;

            function getCategoriesHtml(searchCategory){
                //var shopId = $('.backoffice.dynamic').attr('id');
                var categories;
                (allCategories) ? categories = allCategories : allCategories = categories = thriftModule.client.getAllCategories(shopId);
                var categoriesLength = categories.length;
                var categoriesListHtml = '';
                if(searchCategory){
                    var searchedCategory;
                    for(var i = 0; i < categoriesLength; i++){
                        if(categories[i].id == searchCategory){
                            searchedCategory = categories[i];
                        }
                    }

                    return searchedCategory;
                }else{
                    for(var i = 0; i < categoriesLength; i++){
                        categoriesListHtml += '<li data-categoryid="'+ categories[i].id +'"><a href="#">'+categories[i].name+'</a></li>';
                    }

                    var categoriesHtml = '<div class="btn-group categories-dropdown">'+
                        '<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                        '<span class="btn-group-text">Добавить</span>'+
                        '<span class="icon-caret-down icon-on-right"></span>'+
                        '</button>'+
                        '<ul class="dropdown-menu dropdown-blue">'+
                        categoriesListHtml+
                        '</ul>'+
                        '</div>';

                    return categoriesHtml;
                }
            }

            var allCategoriesHtml = getCategoriesHtml(),
                allProducers = thriftModule.client.getProducers();

            if (!isProductInitSet) initEditProduct();

            /* общие */
            if(!isEditInitSet){

                $('.edit-show-add').click(function(e){

                    e.preventDefault();

                    $(this).find('+.table-add').slideToggle();

                    var currentPane = $(this).closest('.tab-pane').attr('id');
                    switch (currentPane){
                        case "edit-product" :
                            //------------------
                            $('.table-add-product .modal-editProduct').modal();

                            var tr = $('.table-add-product ul');
                            $('#imageURL-add').ace_file_input({
                                style:'well',
                                btn_choose:'',
                                btn_change:null,
                                no_icon:'',
                                droppable:true,
                                icon_remove:null,
                                thumbnail:'large'
                            }).on('change', function(){
                                tr.find('.product-imageURL .file-label').css('opacity',1);
                                tr.find('.product-imageURL>img').hide();
                            });

                            //------------------

                            $('#imageURLSet-add').ace_file_input({
                                style:'well',
                                btn_choose:'Добавить',
                                btn_change:null,
                                no_icon:'',
                                droppable:true,
                                thumbnail:'large'
                            }).on('change', function(){
                                var el = $(this);
                                setAddUrlImageSet(0,0,el)
                            });
                            initRemoveImageSetItem($('.table-add-product .product-imagesSet'));

                            //------------------

                            var categoriesHtml = allCategoriesHtml;

                            var categoryTd = $('.table-add-product .product-categories');
                            categoryTd.html(categoriesHtml);
                            initRemoveCategoryItem(categoryTd);
                            initCategoryDropdownClick(categoryTd);
                            var tableSelector = $('.table-add-product .table-overflow');
                            //boCommon.setDropdownWithoutOverFlow(tableSelctor.find('.categories-dropdown'),tableSelector,84);

                            //------------------

                            var optionsTd = $('.table-add-product .product-options'),
                                linksTd = $('.table-add-product .product-links');
                            initRemoveOptionsItem(optionsTd);
                            initAddOptionsItem(optionsTd);

                            //initAddLinksItem(linksTd);
                            //initRemoveLinksItem(linksTd);
                            //boCommon.setDropdownWithoutOverFlow(tableSelector.find('.producers-dropdown'),tableSelector,84);

                            //------------------

                            $(this).find('+.table-add .product-producer .dropdown-menu a').click(function(){
                                var producerId = $(this).parent().data('producerid');
                                $(this).closest('.edit-product-item').attr('data-producerid',producerId);
                            });
                            break;
                        case "edit-category" :
                            /*linksTd = $('.table-add-category .category-links');
                             initAddLinksItem(linksTd);
                             initRemoveLinksItem(linksTd);*/
                            break;
                        case "edit-producer" :
                            break;
                    }


                });

                $('.bo-edit .nav-tabs a').click(function() {

                    var ind = $(this).parent().index();

                    switch (ind) {
                        case 1:
                            if (!isCategoryInitSet) initEditCategory();
                            break;
                        case 2:
                            if (!isProducerInitSet) initEditProducer();
                            break;
                    }
                    setTimeout(setSidebarHeightTemp,200);

                    function setSidebarHeightTemp() {
                        boCommon.setSidebarHeight($('.main-content').height());
                    }

                });
            }

            /* редактирование продуктов */

            function initEditProduct(){

                var productsTable = $('.products-table>table');

                productsTable.find('>tbody>tr').each(function(){
                    initProductDetails($(this));
                });

                boCommon.DoubleScroll(document.getElementById('doublescroll-2'));

                /* добавление нового продукта  */

                $('.table-add-product .edit-add').click(function(e){
                    e.preventDefault();

                    var shopId = $('.backoffice.dynamic').attr('id');
                    var tableLine = $(this).closest('.table-add-product').find('ul'),
                        isAdd = true;
                    var productInfo = createProductInfoObject(tableLine,isAdd);

                    if(!productInfo.product.producerId){
                        $('.error-info').text('Вы не указали производителя !').show();
                    }else{
                        $('.error-info').hide();
                        var productId = thriftModule.clientBO.registerProduct(productInfo, shopId);

                        $('.table-add-product .modal').modal('hide');

                        $(this).closest('.table-add-product').slideUp(function(){
                            var newLine = tableLine.clone();
                            //newLine.addClass('hidden');

                            var html = '<tr class="new" id="'+ productId +'">'+
                                '<td class="product-name">'+
                                tableLine.find('.product-name textarea').val()+
                                '<div class="modal modal-editProduct">'+
                                    '<div class="modal-body">'+
                                        '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'+
                                        '<h3>Редактирование продукта</h3>'+
                                        '<ul class="edit-product-list clearfix">'+
                                    '<li><label>Название</label>'+
                                                '<span class="edit-product-item product-name"><textarea>'+tableLine.find('.product-name textarea').val()+'</textarea></span>'+
                                            '</li>'+
                                            '<li>'+
                                                '<label>Сокр. описание</label>'+
                                                '<span class="edit-product-item product-shortDescr"><textarea>'+tableLine.find('.product-shortDescr textarea').val()+'</textarea></span>'+
                                            '</li>'+
                                            '<li>'+
                                                '<label>Полное описание</label>'+
                                                '<span class="edit-product-item product-fullDescr"><textarea>'+tableLine.find('.product-fullDescr textarea').val()+'</textarea></span>'+
                                            '</li>'+
                                            '<li class="pull-left short1">'+
                                                '<label>Аватар</label>'+
                                                '<span class="edit-product-item product-imageURL">'+
                                                    '<input type="file" id="imageURL-'+productId+'">';

                                                    if(tableLine.find('.product-imageURL .file-label img').length){
                                                        html += '<img src="'+tableLine.find('.product-imageURL .file-label img').attr('src')+'" alt="картинка"/>';
                                                    }else{
                                                        html += '<img src="../i/no-photo.png" alt="картинка"/>';
                                                    }
                                                   html += '</span>'+
                                                '</li>'+
                                                '<li class="pull-left short1">'+
                                                    '<label>Другие изображения</label>'+
                                                    '<span class="edit-product-item product-imagesSet">'+
                                                        '<input type="file" id="imagesSetURL">'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short1 clear">'+
                                                        '<label>Категории</label>'+
                                                        '<span class="edit-product-item product-categories"></span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short1">'+
                                                        '<label>Производитель</label>'+
                                                        '<span class="edit-product-item product-producer" data-producerid="'+tableLine.find('.product-producer').attr('data-producerid')+'">'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short2 clear">'+
                                                        '<label>Вес</label>'+
                                                        '<span class="edit-product-item product-weight">'+
                                                            '<input type="text" value="'+tableLine.find('.product-weight input').val()+'"/>'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short2">'+
                                                        '<label>Цена</label>'+
                                                        '<span class="edit-product-item product-price">'+
                                                            //'<input type="text" value="'+tableLine.find('.product-price input').val()+'"/>'+
                                                            '<input type="text" value=""/>'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short2">'+
                                                        '<label>Ед.изм</label>'+
                                                        '<span class="edit-product-item product-unitName">'+
                                                            '<input type="text" value="'+tableLine.find('.product-unitName input').val()+'"/>'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short2">'+
                                                        '<label>Мин.шаг</label>'+
                                                        '<span class="edit-product-item product-pack">'+
                                                            '<input type="text" value="'+tableLine.find('.product-pack input').val()+'"/>'+
                                                        '</span>'+
                                                    '</li>'+
                                                    '<li class="pull-left short2">'+
                                                        '<label>Весовой</label>'+
                                                        '<span class="edit-product-item product-prepack">';

                                                            if(tableLine.find('.product-prepack input').prop('checked')){
                                                                html += '<input type="checkbox" checked />';
                                                            }else{
                                                                html += '<input type="checkbox" />';
                                                            }

                                                            html += '</span>'+
                                                        '</li>'+
                                                        '<li class="pull-left short1 clear">'+
                                                            '<label>Опции</label>'+
                                                            '<span class="edit-product-item product-options"></span>'+
                                                        '</li>'+
                                                        '<li class="pull-left short1">'+
                                                            '<label>Ссылки</label>'+
                                                            '<span class="edit-product-item product-links">'+
                                                                '<table>'+
                                                                    '<tbody>'+
                                                                        '<tr>'+
                                                                            '<td class="link-key">vk</td>'+
                                                                            '<td><input type="text"/></td>'+
                                                                        '</tr>'+
                                                                        '<tr>'+
                                                                            '<td class="link-key">fb</td>'+
                                                                            '<td><input type="text"/></td>'+
                                                                        '</tr>'+
                                                                    '</tbody>'+
                                                                '</table>'+
                                                            '</span>'+
                                                        '</li>'+
                                                    '</ul>'+
                                                    '<a class="btn btn-sm btn-primary no-border save-products clear" href="#">Сохранить</a>'+
                                                            '</div>'+
                                                        '</div>'+
                                                    '</td>'+
                                                        '<td class="product-shortDescr">'+
                                                            tableLine.find('.product-shortDescr textarea').val()+
                                                        '</td>'+
                                                            '<td class="product-imageURL">';

                                                            if(tableLine.find('.product-imageURL .file-label img').length){
                                                                html += '<img src="'+tableLine.find('.product-imageURL .file-label img').attr('src')+'" alt="картинка"/>';
                                                            }else{
                                                                html += '<img src="../i/no-photo.png" alt="картинка"/>';
                                                            }
                                                                html += '</td>'+
                                                                        '<td class="product-weight">'+tableLine.find('.product-weight input').val()+'</td>'+
                                                                        '<td class="product-unitName">'+tableLine.find('.product-unitName input').val()+'</td>'+
                                                                        '<td class="product-pack">'+tableLine.find('.product-pack input').val()+'</td>'+
                                                                        '<td class="product-prepack">';

                                                                                if(tableLine.find('.product-prepack input').prop('checked')){
                                                                                    html += '<input type="checkbox" checked />';
                                                                                }else{
                                                                                    html += '<input type="checkbox" />';
                                                                                }
                                                                            html += '</td>'+
                                                                            '<td class="product-remove"><a href="#" title="Удалить" class="remove-item">&times;</a></td>'+
                                                                        '</tr>';

                            $('.products-table>table>tbody').prepend(html);
                            //newLine.slideDown();

                            initProductDetails($('.products-table>table>tbody .new'));
                            initRemoveProduct($('.products-table>table>tbody .new .product-remove'));
                            $('.products-table>table>tbody .new').removeClass('new');
                        });
                    }

                });

                function createProductInfoObject(tableLine,isAdd){
                    var productInfo = new com.vmesteonline.be.shop.FullProductInfo();
                    productInfo.product = new com.vmesteonline.be.shop.Product();
                    productInfo.details = new com.vmesteonline.be.shop.ProductDetails();

                    isAdd ? productInfo.product.id = 0 : productInfo.product.id = tableLine.closest('tr').attr('id');

                    productInfo.product.name = tableLine.find('.product-name textarea').val();
                    productInfo.product.shortDescr = tableLine.find('.product-shortDescr textarea').val();

                    productInfo.product.producerId = tableLine.find('.product-producer').data('producerid');

                    var loadedLogo = tableLine.find('.product-imageURL .file-name img');
                    loadedLogo.length ? productInfo.product.imageURL = loadedLogo.css('background-image')
                        : productInfo.product.imageURL = tableLine.find('.product-imageURL>img').attr('src');

                    tableLine.find('.product-weight input').val() ?
                        productInfo.product.weight = parseFloat(tableLine.find('.product-weight input').val()) :
                        productInfo.product.weight = 0;

                    // '1' - priceType INET
                    productInfo.details.pricesMap = [];
                    tableLine.find('.product-price input').val() ?
                        productInfo.details.pricesMap['1'] = parseFloat(tableLine.find('.product-price input').val()):
                        productInfo.details.pricesMap['1'] = 0;

                    productInfo.product.unitName = tableLine.find('.product-unitName input').val();

                    tableLine.find('.product-pack input').val() ?
                        productInfo.product.minClientPack = parseFloat(tableLine.find('.product-pack input').val()):
                        productInfo.product.minClientPack = 0;

                    productInfo.product.prepackRequired = tableLine.find('.product-prepack input').attr('checked') ? true : false;

                    productInfo.details.fullDescr = tableLine.find('.product-fullDescr textarea').val();

                    var counter = 0;
                    var isImagesLoaded = tableLine.find('.product-imagesSet').find('.file-name img').length;
                    if(isImagesLoaded){
                        var imagesURLset = [];

                        tableLine.find('.product-imagesSet .ace-file-input.added').each(function(){
                            imagesURLset[counter++] = $(this).find('.file-name img').css('background-image');
                        });

                        productInfo.details.imagesURLset = imagesURLset;
                    }

                    counter = 0;
                    var categoriesIdList = [];
                    tableLine.find('.product-categories .category-item').each(function(){
                        categoriesIdList[counter++] = $(this).data('categoryid');
                    });
                    productInfo.details.categories = categoriesIdList;

                    var optionsMap = [];
                    tableLine.find('.product-options table tr').each(function(){
                        optionsMap[$(this).find('td:eq(0)').text()] = $(this).find('td:eq(1)').text();
                    });
                    productInfo.details.options = optionsMap;

                    var linksMap = [],ind = 0;
                    tableLine.find('.product-links>table>tbody>tr').each(function(){
                        var socVal = $(this).find('input').val(),
                            key= $(this).find('.link-key').text();
                        linksMap[key] = socVal;

                    });
                    productInfo.details.socialNetworks = linksMap;

                    return productInfo;
                }

                /* первоначальное заполнение таблицы продуктов */

                var laodedProductDetails = [];
                function initProductDetails(selector){
                    var productsTable = $('.products-table>table'),
                        productDetails;
//                    productsTable.find('>tbody>tr').each(function(){
                    selector.click(function(e){

                        var productId = $(this).attr('id');

                        if(!laodedProductDetails[productId]) {
                            productDetails = thriftModule.client.getProductDetails(productId);
                            laodedProductDetails[productId] = productDetails;
                        }else{
                            productDetails = laodedProductDetails[productId];
                        }

                        $(this).find('.modal').modal();

                        selector.find('.modal-editProduct .close').click(function(e){
                            e.preventDefault();
                            e.stopPropagation();

                            $(this).closest('.modal').modal('hide');
                        });
                        selector.find('.modal-editProduct').click(function(e){
                            e.stopPropagation();

                            if($(this).find('.categories-dropdown').hasClass('open')){
                                $(this).find('.categories-dropdown').removeClass('open');
                            }

                            if($(this).find('.producers-dropdown').hasClass('open')){
                                $(this).find('.producers-dropdown').removeClass('open');
                            }
                        })

                        // -------------
                        var imagesURLSet = productDetails.imagesURLset,
                            imagesURLSetLength = imagesURLSet.length,
                            imagesURLSetHtml = "<div class='images-set'>";
                        if(imagesURLSetLength != 0){
                            for(var i = 0; i < imagesURLSetLength; i++){
                                imagesURLSetHtml += "<div class='image-item map-item'>" +
                                    "<a href='#' class='remove-image-item remove-item'>&times</a>"+
                                    "<img src='"+ imagesURLSet[i] +"'>"+
                                    "</div>";
                            }
                        }
                        imagesURLSetHtml += "</div>"+
                            "<input type='file' id='imagesURLSet-"+ productId+"-"+imagesURLSetLength +"'>";
                        // -------------
                        var categories = productDetails.categories,
                            categoriesLength = categories.length,
                            categoriesHtml = "";
                        for(var i = 0; i < categoriesLength; i++){
                            categoriesHtml += "<div class='category-item map-item' data-categoryid='"+ categories[i] +"'>"+
                                "<a href='#' class='remove-category-item remove-item'>&times</a>"+
                                getCategoriesHtml(categories[i]).name+
                                "</div>";
                        }

                        categoriesHtml += allCategoriesHtml;
                        // -------------
                        var options = productDetails.optionsMap,
                            optionsHtml = "<table><tbody>";
                        for(var p in options){
                            optionsHtml += "<tr>" +
                                "<td><input type='text' value='"+ p +"'></td>" +
                                "<td><input type='text' value='"+ options[p] +"'></td>" +
                                "<td class='td-remove-options'><a href='' class='remove-options-item remove-item'>&times;</a></td>" +
                                "</tr>";
                        }
                        optionsHtml += "</tbody></table><a href='#' class='add-options-item add-item'>Добавить</a>";
                        // -------------
                        var links = productDetails.socialNetworks;
                        var productLinks = $(this).find('.product-links');
                        for(var p in links){
                            if(p == "vk"){
                                productLinks.find('tr:eq(0) td:eq(1) input').val(links[p]);
                            }else if(p == "fb"){
                                productLinks.find('tr:eq(1) td:eq(1) input').val(links[p]);
                            }
                        }
                        // -------------
                        var currentProducer,
                            producerId = $(this).find('.product-producer').data('producerid'),
                            producersList = allProducers,
                            producersListLength = producersList.length,
                            producersListHtml = "";
                        for(var i = 0; i < producersListLength; i++ ){
                            if(producersList[i].id == producerId){
                                currentProducer = producersList[i];
                            }
                            producersListHtml += "<li data-producerid='"+ producersList[i].id +"'><a href='#'>"+ producersList[i].name +"</a></li>";
                        }
                        var producersHtml = "<span>" + currentProducer.name + "</span>"+
                            '<div class="btn-group producers-dropdown">'+
                            '<button data-toggle="dropdown" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                            '<span class="btn-group-text">Изменить</span>'+
                            '<span class="icon-caret-down icon-on-right"></span>'+
                            '</button>'+
                            '<ul class="dropdown-menu dropdown-blue">'+
                            producersListHtml+
                            '</ul>'+
                            '</div>';

                        // -------------

                        var tr = $(this);
                        $('#imageURL-'+productId).ace_file_input({
                            style:'well',
                            btn_choose:'',
                            btn_change:null,
                            no_icon:'',
                            droppable:true,
                            icon_remove:null,
                            thumbnail:'large'
                        }).on('change', function(){
                            tr.find('.product-imageURL .file-label').css('opacity',1);
                            tr.find('.product-imageURL>img').hide();
                        });

                        $(this).find('.product-fullDescr textarea').val(productDetails.fullDescr);

                        for(var p in productDetails.pricesMap){
                            if(p == '1'){
                                $(this).find('.product-price input').val(productDetails.pricesMap[p]);
                            }
                        }

                        $(this).find('.product-imagesSet').html(imagesURLSetHtml);

                        $('#imagesURLSet-' + productId + "-" + imagesURLSetLength).ace_file_input({
                            style:'well',
                            btn_choose:'Добавить', //
                            btn_change:null,
                            no_icon:'',
                            droppable:true,
                            //icon_remove:null,
                            thumbnail:'large'
                        }).on('change', function(){
                            var el = $(this);
                            setAddUrlImageSet(productId,imagesURLSetLength,el);
                        });
                        initRemoveImageSetItem($(this).find('.ace-file-input'));

                        $(this).find('.product-categories').html(categoriesHtml);
                        initRemoveCategoryItem($(this));
                        initCategoryDropdownClick($(this));

                        $(this).find('.product-options').html(optionsHtml);
                        initRemoveOptionsItem($(this));
                        initAddOptionsItem($(this));

                        $(this).find('.product-producer').html(producersHtml);
                        initChangeProducer($(this));
                    });
                }

                /* действия внутри таблицы продуктов */

                function initRemoveProduct(selector){
                    var sel;
                    if(selector){
                        sel = selector;
                    }else{
                        sel = $('.product-remove a');
                    }

                    sel.click(function(e){
                        e.preventDefault();
                        e.stopPropagation();

                        var productId = $(this).closest('tr').attr('id');
                        var shopId = $('.backoffice.dynamic').attr('id');

                        $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                            $(this).detach();
                        });

                        thriftModule.clientBO.deleteProduct(productId,shopId);
                    });
                }
                initRemoveProduct();

                function initChangeProducer(selector){
                    selector.find('.producers-dropdown .btn').click(function(e){
                        e.stopPropagation();

                        if($(this).closest('.modal').length) {
                            if($(this).closest('.producers-dropdown').hasClass('open')) {

                                $(this).closest('.producers-dropdown').removeClass('open');

                            }else{

                                $(this).closest('.producers-dropdown').addClass('open');

                            }
                        }
                    });

                    selector.find('.producers-dropdown .dropdown-menu a').click(function(e){
                        e.preventDefault();

                        var producerName = $(this).text();
                        var producerId = $(this).closest('li').data('producerid');
                        $(this).closest('.edit-product-item').attr('id',producerId).find('>span').text(producerName);

                        $(this).closest('.producers-dropdown').removeClass('open');
                    });
                }

                /* сохранение изменений в таблице продуктов */

                $('.save-products').click(function(e){
                    e.preventDefault();

                    var tableLine = $(this).closest('.modal-body').find('.edit-product-list'),
                        isAdd = false;

                    //$('.products-table>table>tbody>.changed').each(function(){

                        var productInfo = createProductInfoObject(tableLine,isAdd);
                    //var productInfo = new com.vmesteonline.be.shop.FullProductInfo();

                        if(productInfo.imageURL === undefined || productInfo.imageURL == "/i/no-photo.png"){
                            productInfo.imageURL = null;
                        }


                        thriftModule.clientBO.updateProduct(productInfo);

                    $(this).closest('.modal').modal('hide');

                    //});
                });

                $('#edit-product .products-table table tr,' +
                    '#edit-category .category-table tr,' +
                    '#edit-producer .producer-table tr').click(function(){

                    $(this).addClass('changed');

                });

                isProductInitSet = 1;
            }

            function initRemoveImageSetItem(selector){
                selector.find('.remove').click(function(e){
                    e.preventDefault();

                    $(this).closest('.ace-file-input').hide().detach();

                })
            }

            function setAddUrlImageSet(productId,imagesURLSetLength,el){
                if(!el.parent().hasClass('added')){
                    el.parent().addClass('added');
                    imagesURLSetLength++;
                    var newInputHtml = "<input type='file' class='new-input' id='imagesURLSet-"+ productId+"-"+imagesURLSetLength +"'>";
                    el.parent().after(newInputHtml);

                    $('#imagesURLSet-' + productId + "-" + imagesURLSetLength).ace_file_input({
                        style:'well',
                        btn_choose:'Добавить', //
                        btn_change:null,
                        no_icon:'',
                        droppable:true,
                        //icon_remove:null,
                        thumbnail:'large'
                    }).on('change',function(){
                        var el = $(this);
                        setAddUrlImageSet(productId,imagesURLSetLength,el);
                    });
                    initRemoveImageSetItem(el.parent());
                }
            }

            function initRemoveCategoryItem(selector){
                selector.find('.remove-category-item').click(function(e){
                    e.preventDefault();

                    $(this).closest('.map-item').fadeOut(200);

                });
            }


            function initCategoryDropdownClick(selector){
                selector.find('.categories-dropdown .btn').click(function(e){
                    e.stopPropagation();

                    //if($(this).closest('.modal').length) {
                        if($(this).closest('.categories-dropdown').hasClass('open')) {

                            $(this).closest('.categories-dropdown').removeClass('open');

                        }else{

                            $(this).closest('.categories-dropdown').addClass('open');

                        }
                    //}
                });

                selector.find('.categories-dropdown .dropdown-menu a').click(function(e){
                    e.preventDefault();
                    e.stopPropagation();

                    var newCategoryItemHtml = '<div class="category-item map-item" data-categoryid="'+ $(this).closest('li').data('categoryid') +'">' +
                        '<a href="#" class="remove-category-item remove-item">×</a>'+ $(this).text() +'</div>';
                    $(this).closest('td').find('.categories-dropdown').before(newCategoryItemHtml);

                    $(this).closest('.categories-dropdown').removeClass('open');

                    initRemoveCategoryItem($(this).closest('td'));
                });
            }

            function initRemoveOptionsItem(selector){
                selector.find('.remove-options-item').click(function(e){
                    e.preventDefault();
                    e.stopPropagation();

                    $(this).closest('tr').slideUp(200,function(){
                        $(this).detach();
                    });
                })
            }

            function initAddOptionsItem(selector){
                selector.find('.add-options-item').click(function(e){
                    e.preventDefault();
                    e.stopPropagation();

                    var newOptionsLine = '<tr>' +
                        '<td><input type="text" value=""></td>' +
                        '<td><input type="text" value=""></td>' +
                        '<td class="td-remove-options"><a href="" class="remove-options-item remove-item">×</a></td>' +
                        '</tr>';

                    $(this).closest('.product-options').find('table tbody').append(newOptionsLine);

                    initRemoveOptionsItem($(this).closest('.product-options'));

                });
            }


            /* редактирование категорий */

            function initEditCategory() {

                var shopId = $('.backoffice.dynamic').attr('id'),
                    categories = thriftModule.client.getAllCategories(shopId),
                    categoriesLength = categories.length,
                    socialNetworks = [],ind = 0;

                for(var i = 0; i < categoriesLength ; i++){
                    socialNetworks[i] = categories[i].socialNetworks;
                }

                if(socialNetworks.length) {
                    $('.category-table>tbody>tr').each(function () {
                        var currentSocNetworks = socialNetworks[ind];
                        for(var p in currentSocNetworks){
                            if(p == "vk"){
                                $(this).find('.category-links tr:eq(0) td:eq(1) input').val(currentSocNetworks[p]);
                            }else if(p == "fb"){
                                $(this).find('.category-links tr:eq(1) td:eq(1) input').val(currentSocNetworks[p]);
                            }
                        }

                        ind++;
                    });
                }

                function setParentCategory(selector){
                    var newCategory = selector.text();
                    var categoryId = selector.closest('li').data('categoryid');
                    selector.closest('.category-parent').attr('data-parentid',categoryId).find('.btn-group-text').text(newCategory);
                }

                $('.category-parent').each(function(){
                    //var categoriesHtml = getCategoriesHtml();
                    $(this).html(allCategoriesHtml);

                    $(this).find('.btn-group-text').text('Выбрать родительскую');

                    $(this).find('.dropdown-menu a').click(function(e){
                        e.preventDefault();

                        setParentCategory($(this));

                    });

                    if($(this).closest('.table-add').length == 0){
                        var parentId = $(this).data('parentid');

                        $(this).find('.dropdown-menu li').each(function(){
                            if($(this).data('categoryid') == parentId){
                                setParentCategory($(this));
                            }
                        })
                    }

                });

                $('.table-add-category .edit-add').click(function(e){
                    e.preventDefault();

                    var shopId = $('.backoffice.dynamic').attr('id');
                    var tableLine = $(this).closest('.table-add-category').find('>table>tbody>tr'),
                        isAdd = true;
                    var productCategory = createCategoryObject(tableLine,isAdd);

                    $('.error-info').hide();
                    thriftModule.clientBO.registerProductCategory(productCategory, shopId);

                    $(this).closest('.table-add-category').slideUp(function(){
                        var newLine = tableLine.clone();
                        //newLine.addClass('hidden');
                        $('.category-table>tbody').prepend(newLine);
                        newLine.slideDown();
                    });
                });

                function createCategoryObject(tableLine,isAdd){
                    var productCategory = new com.vmesteonline.be.shop.ProductCategory();

                    isAdd ? productCategory.id = 0 : productCategory.id = tableLine.attr('id');
                    productCategory.name = tableLine.find('.category-name textarea').val();
                    productCategory.descr = tableLine.find('.category-descr textarea').val();
                    productCategory.parentId = tableLine.find('.category-parent').attr('data-parentid');
                    productCategory.socialNetworks = [];
                    tableLine.find('.category-links table tr').each(function(){
                        var socVal = $(this).find('input').val(),
                            key = $(this).find('.link-key').text();

                        productCategory.socialNetworks[key] = socVal;
                    });

                    return productCategory;
                }

                $('.save-categories').click(function(e){
                    e.preventDefault();

                    $('.category-table>tbody>tr.changed').each(function(){
                        var tableLine = $(this),
                            isAdd = false;

                        var productCategory = createCategoryObject(tableLine,isAdd);
                        thriftModule.clientBO.updateCategory(productCategory);

                    })
                });

                $('.category-remove a').click(function(e){
                    e.preventDefault();

                    var categoryId = $(this).closest('tr').attr('id');
                    var shopId = $('.backoffice.dynamic').attr('id');

                    $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                        $(this).detach();
                    });

                    thriftModule.clientBO.deleteCategory(categoryId,shopId);
                });

                isCategoryInitSet = 1;

            }

            /* редактирование производителей */

            function initEditProducer(){

                var shopId = $('.backoffice.dynamic').attr('id'),
                    producers = thriftModule.client.getProducers(),
                    producersLength = producers.length,
                    socialNetworks = [],ind = 0;

                for(var i = 0; i < producersLength ; i++){
                    socialNetworks[i] = producers[i].socialNetworks;
                }

                if(socialNetworks.length) {
                    $('.producer-table>tbody>tr').each(function () {
                        var currentSocNetworks = socialNetworks[ind];
                        for(var p in currentSocNetworks){
                            if(p == "vk"){
                                $(this).find('.producer-links tr:eq(0) td:eq(1) input').val(currentSocNetworks[p]);
                            }else if(p == "fb"){
                                $(this).find('.producer-links tr:eq(1) td:eq(1) input').val(currentSocNetworks[p]);
                            }
                        }

                        ind++;
                    });
                }

                $('.table-add-producer .edit-add').click(function(e){
                    e.preventDefault();

                    var shopId = $('.backoffice.dynamic').attr('id');
                    var tableLine = $(this).closest('.table-add-producer').find('>table>tbody>tr'),
                        isAdd = true;
                    var producer = createProducerObject(tableLine,isAdd);

                    $('.error-info').hide();
                    thriftModule.clientBO.registerProducer(producer, shopId);

                    $(this).closest('.table-add-producer').slideUp(function(){
                        var newLine = tableLine.clone();
                        //newLine.addClass('hidden');
                        $('.producer-table >tbody').prepend(newLine);
                        newLine.slideDown();
                    });
                });

                function createProducerObject(tableLine,isAdd){
                    var producer = new com.vmesteonline.be.shop.Producer();

                    isAdd ? producer.id = 0 : producer.id = tableLine.attr('id');
                    producer.name = tableLine.find('.producer-name textarea').val();
                    producer.descr = tableLine.find('.producer-descr textarea').val();
                    producer.socialNetworks = [];
                    tableLine.find('.producer-links table tr').each(function(){
                        var socVal = $(this).find('input').val(),
                            key = $(this).find('.link-key').text();

                        producer.socialNetworks[key] = socVal;
                    });

                    return producer;
                }

                $('.save-producers').click(function(e){
                    e.preventDefault();

                    $('.producer-table>tbody>tr.changed').each(function(){
                        var tableLine = $(this),
                            isAdd = false;

                        var producer = createProducerObject(tableLine,isAdd);
                        thriftModule.clientBO.updateProducer(producer);
                    })
                });

                $('.producer-remove a').click(function(e){
                    e.preventDefault();

                    var producerId = $(this).closest('tr').attr('id');
                    var shopId = $('.backoffice.dynamic').attr('id');

                    $(this).closest('tr').addClass('removing').fadeOut(600,function(){
                        $(this).detach();
                    });

                    thriftModule.clientBO.deleteProducer(producerId,shopId);
                });

                isProducerInitSet = 1;

            }

            isEditInitSet = 1;
        }

        return{
            initEdit : initEdit,
            isEditInitSet: isEditInitSet
        }

    });