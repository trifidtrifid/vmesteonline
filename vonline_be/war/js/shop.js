$(document).ready(function(){
/* инициализация плагинов */
    $('.spinner1').ace_spinner({value:0,min:0,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
        .on('change', function(){
            //alert(this.value)
        });

    $('.date-picker').datepicker({autoclose:true}).next().on(ace.click_event, function(){
        $(this).prev().focus();
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
        hideRight = $('.hide-right'),
        shopRight = $('.shop-right'),
        showRightTop = (w.height()-showRight.width())/2;

    showRight.css('top',showRightTop);
    $('#sidebar').css('min-height', w.height());

    showRight.click(function(){
        $(this).hide();
       $(this).parent().animate({'right':0},200);
    });
    hideRight.click(function(){
        $(this).parent().animate({'right':'-190px'},200,function(){
            showRight.show();
        });
    });

    w.resize(function(){
       if ($(this).width() > 975){
           showRight.hide();
           shopRight.css({'right':'0'});
       }else{
           showRight.show();
           shopRight.css({'right':'-190px'});
       }
    });
    /* --- */
});