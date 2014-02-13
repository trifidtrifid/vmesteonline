$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/ShopService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

/* инициализация плагинов */
    $('.spinner1').ace_spinner({value:1,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
        .on('change', function(){
            //alert(this.value)
        });

    var dPicker = $('.date-picker');

    dPicker.datepicker({autoclose:true,language:'ru'}).next().on(ace.click_event, function(){
        $(this).prev().focus();
    });

    dPicker.click(function(){
        $('.day').each(function(){
            var day = $(this).text();
            if  (day == '15' || day == "23"){ $(this).addClass('made')}
            if  (day == '7' || day == "13"){ $(this).addClass('soon')}
            if  (day == '26' || day == "30"){ $(this).addClass('prepare')}
        });
    });

    $('.day').click(function(){
        alert('1');
        var madeMenu = '<div class="day-menu">' +
            '<a href="#" class="day-repeat">Повторить</a>'+
            '<a href="#" class="day-add">Добавить</a>'+
            '</div>';
       $(this).append(madeMenu);
    });

    $('.product-link').click(function(){
       $(this).find('+.modal').modal();
       var carousel = $(this).find('+.modal').find('.carousel');
       var slider = $(this).find('+.modal').find('.slider');

        carousel.flexslider({
            animation: "slide",
            controlNav: false,
            animationLoop: false,
            slideshow: false,
            itemWidth: 60,
            itemMargin: 5,
            asNavFor: slider
        });

        slider.flexslider({
            animation: "slide",
            controlNav: false,
            animationLoop: false,
            slideshow: false,
            sync: carousel
        });
    });

    //custom autocomplete (category selection)
    $.widget( "custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function( ul, items ) {
            var that = this,
                currentCategory = "";
            $.each( items, function( index, item ) {
                if ( item.category != currentCategory ) {
                    ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
                    currentCategory = item.category;
                }
                that._renderItemData( ul, item );
            });
        }
    });

    var data = [
        { label: "anders", category: "" },
        { label: "andreas", category: "" },
        { label: "antal", category: "" },
        { label: "annhhx10", category: "Products" },
        { label: "annk K12", category: "Products" },
        { label: "annttop C13", category: "Products" },
        { label: "anders andersson", category: "People" },
        { label: "andreas andersson", category: "People" },
        { label: "andreas johnson", category: "People" }
    ];
    $( "#search" ).catcomplete({
        delay: 0,
        source: data
    });

/* простые обработчики событий */
    var w = $(window),
        showRight = $('.show-right'),
        showLeft = $('.show-left'),
        hideRight = $('.hide-right'),
        shopRight = $('.shop-right'),
        sidebar = $('#sidebar'),
        showRightTop = (w.height()-showRight.width())/ 2,
        showLeftTop = (w.height()-showLeft.width())/2;

    showRight.css('top',showRightTop);
    showLeft.css('top',showLeftTop);
    $('#sidebar, .shop-right').css('min-height', w.height());

    showRight.click(function(){
        if (!$(this).hasClass('active')){
            $(this).animate({'right':'222px'},200).addClass('active');
            $(this).parent().animate({'right':0},200);
        }else{
            hideRight.trigger('click');
        }
    });
    showLeft.click(function(){
        if (!$(this).hasClass('active')){
            $(this).animate({'margin-left':'190px'},200).addClass('active');
            $(this).parent().animate({'marginLeft':0},200);
        }else{
            $(this).parent().animate({'marginLeft':'-190px'},200);
            $(this).animate({'marginLeft':'0'},200).removeClass('active');
        }
    });

    hideRight.click(function(){
        $(this).parent().animate({'right':'-250px'},200);
        showRight.animate({'right':'-28px'},200).removeClass('active');
    });

    w.resize(function(){
       if ($(this).width() > 975){
           shopRight.css({'right':'0'});
       }else{
           shopRight.css({'right':'-250px'});
       }
        if ($(this).width() > 753){
            sidebar.css({'marginLeft':'0'});
        }else{
            sidebar.css({'marginLeft':'-190px'});
        }
    });

    $('.fa-shopping-cart').click(function(){

       var currentProduct = $(this).closest('tr');
       var productHtml = '<li>'+
            '<img src="'+ currentProduct.find('.product-price') +'" alt="картинка"/>'+
            '<div class="product-right-descr">'+
            currentProduct.find('.product-link span').text()+
        '</div>'+
        '<table>'+
            '<thead>'+
                '<tr>'+
                    '<td>Цена(шт)</td>'+
                    '<td>Кол-во</td>'+
                    '<td>Сумма</td>'+
                    '<td></td>'+
                '</tr>'+
            '</thead>'+
            '<tr>'+
                '<td class="td-price">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><input type="text" class="input-mini spinner1" /></td>'+
                '<td class="td-summa">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><a href="#" class="delete-product">Удалить</a></td>'+
            '</tr>'+
        '</table>'+
        '</li>';

        $('.catalog-order').append(productHtml);

        $('.delete-product').click(function(){
            $(this).closest('li').slideUp(function(){
                $(this).detach();
                $('.itogo-right span').text(countItogo($('.catalog-order')));
            });
        });

    });

    $('.shop-menu li').click(function(){
        /* замена меню категорий */
       var catID = $(this).data('catid');
       var productCategories = client.getProductCategories(catID);
        var categoriesLength = productCategories.length;
        var shopMenu = '';
        var firstMenuItem = '<li>'+
            '<a href="#" class="fa fa-reply-all"></a>'+
            '<div>Назад</div>'+
        '</li>';

        for(var i = 0; i < categoriesLength; i++){
             shopMenu += '<li data-catid="'+ productCategories[i].id +'">'+
                 '<a href="#" class="fa fa-beer"></a>'+
                 '<div>'+ productCategories[i].name +'</div>'+
             '</li>';
        }

        $('.shop-menu ul').html(firstMenuItem).append(shopMenu);

        /* новый список товаров */

        var productsList = client.getProducts(0,10,catID).products;
        var productListLength = productsList.length;
        var productsHtml = '';
        for (i = 0; i < productListLength; i++){
            productsHtml += '<tr>'+
                '<td>'+
                    '<a href="#" class="product-link">'+
                        '<img src="'+ productsList[i].imageURL +'" alt="картинка"/>'+
                        '<span>'+ productsList[i].name + ' 2 level'+'</span>'+
                    '</a>'+
                    '<div class="modal">'+
                        '<div class="modal-body">'+
                            '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
                            '<div class="product-slider">'+
                                '<div class="slider flexslider">'+
                                    '<ul class="slides">'+
                                        '<li>'+
                                            '<img src="'+ productsList[i].imageURL +'" />'+
                                        '</li>'+
                                        '</ul>'+
                                    '</div>'+
                                    '<div class="carousel flexslider">'+
                                        '<ul class="slides">'+
                                            '<li>'+
                                                '<img src="'+ productsList[i].imageURL +'" />'+
                                            '</li>'+
                                            '</ul>'+
                                        '</div>'+
                                    '</div>'+
                                    '<div class="product-descr">'+
                                        '<h3>'+ productsList[i].name +'</h3>'+
                                        '<div class="product-text">'+
                                        '${productDetails.fullDescr}'+
                                        '</div>'+
                                        '<div class="modal-footer">'+
                                            '<span>Цена: '+ productsList[i].price +'</span>'+
                                            '<input type="text" class="input-mini spinner1" />'+
                                            '<i class="fa fa-shopping-cart"></i>'+
                                        '</div>'+
                                    '</div>'+
                                '</div>'+
                            '</div>'+
                        '</td>'+
                        '<td class="product-price">'+ productsList[i].price  +'</td>'+
                        '<td>'+
                            '<input type="text" class="input-mini spinner1" />'+
                        '</td>'+
                        '<td>'+
                            '<i class="fa fa-shopping-cart"></i>'+
                        '</td>'+
                    '</tr>';
        }
              $('.catalog table tbody').html("").append(productsHtml);

        /* подключение событий */

          $('.catalog table .spinner1').ace_spinner({value:1,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
                .on('change', function(){
                    //alert(this.value)
                });

        $('.product-link').click(function(){
            $(this).find('+.modal').modal();
            var carousel = $(this).find('+.modal').find('.carousel');
            var slider = $(this).find('+.modal').find('.slider');

            carousel.flexslider({
                animation: "slide",
                controlNav: false,
                animationLoop: false,
                slideshow: false,
                itemWidth: 60,
                itemMargin: 5,
                asNavFor: slider
            });

            slider.flexslider({
                animation: "slide",
                controlNav: false,
                animationLoop: false,
                slideshow: false,
                sync: carousel
            });
        });

        $('.fa-shopping-cart').click(function(){

            var currentProduct = $(this).closest('tr');
            var productHtml = '<li>'+
                '<img src="'+ currentProduct.find('.product-price') +'" alt="картинка"/>'+
                '<div class="product-right-descr">'+
                currentProduct.find('.product-link span').text()+
                '</div>'+
                '<table>'+
                '<thead>'+
                '<tr>'+
                '<td>Цена(шт)</td>'+
                '<td>Кол-во</td>'+
                '<td>Сумма</td>'+
                '<td></td>'+
                '</tr>'+
                '</thead>'+
                '<tr>'+
                '<td class="td-price">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><input type="text" class="input-mini spinner1" /></td>'+
                '<td class="td-summa">'+ currentProduct.find('.product-price').text() +'</td>'+
                '<td><a href="#" class="delete-product">Удалить</a></td>'+
                '</tr>'+
                '</table>'+
                '</li>';

            $('.catalog-order').append(productHtml);

            $('.delete-product').click(function(){
                $(this).closest('li').slideUp(function(){
                    $(this).detach();
                    $('.itogo-right span').text(countItogo($('.catalog-order')));
                });
            });

        });

    });

    $('.checkbox span').click(function(){
       $(this).parent().parent().find('+.input-delivery').slideToggle();
    });

    $('.delete-product').click(function(){
       $(this).closest('li').slideUp(function(){
           $(this).detach();
           $('.itogo-right span').text(countItogo($('.catalog-order')));
       });
    });

    $('.btn-order').click(function(){
        $('.modal-order-end').modal();
    });

    $('.modal-order-end .btn-grey').click(function(){
        $('.modal-order-end').modal('hide');
    });

    var price= 0,
        qnty = 0,
        myTable;
    function countItogo(sel){
        var summa = 0;
        sel.find('.td-summa').each(function(){
         summa += parseInt($(this).text());
      });
        return summa;
    }
    $('.spinner-input').on('change',function(){
       myTable = $(this).closest('tr');
       price = myTable.find('.td-price').text();
       price = parseInt(price);
       qnty = $(this).val();
       myTable.find('.td-summa').text(price*qnty+'р');
       $('.itogo-right span').text(countItogo($('.catalog-order')));
       $('.modal-itogo span').text(countItogo($('.modal-body-list')));
    });
    $('.login-link').click(function(){
        $('.modal-login').modal();
    });

    /* --- */
});