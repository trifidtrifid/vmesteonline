$(document).ready(function(){
/* простые обработчики событий */
    var w = $(window),
        sidebar = $('#sidebar'),
        showLeft = $('.show-left'),
        showLeftTop = (w.height()-showLeft.width())/ 2,
        asideWidth = (w.width()-$('.container').width())/2;
    showLeft.css('top',showLeftTop);
    sidebar.css('min-height', w.height());

    showLeft.click(function(){
        if (!$(this).hasClass('active')){
            $(this).animate({'margin-left':'190px'},200).addClass('active');
            $(this).parent().animate({'marginLeft':0},200);
        }else{
            $(this).parent().animate({'marginLeft':'-190px'},200);
            $(this).animate({'marginLeft':'0'},200).removeClass('active');
        }
    });

    w.resize(function(){
        if ($(this).width() > 753){
            sidebar.css({'marginLeft':'0'});
            $('.main-content').css('margin-left','190px');
        }else{
            sidebar.css({'marginLeft':'-190px'});
            $('.main-content').css('margin-left','0');
        }
    });

    function ChangeOrientation() {
        var orientation = Math.abs(window.orientation) === 90 ? 'landscape' : 'portrait';
        alert(orientation);
        alert('1');
    }

    window.addEventListener('orientationchange', ChangeOrientation, false);

    $('.dropdown-menu li a').click(function(e){
        e.preventDefault();
        $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
    });

    $('.widget-main').hover(function(){
       $(this).find('.fa-relations').animate({opacity:1},200);
       $(this).find('.fa-sitemap').animate({opacity:1},200);
    },function(){
        $(this).find('.fa-relations').animate({opacity:0},200);
        $(this).find('.fa-sitemap').animate({opacity:0},200);
    });

    $('.ace-nav .btn,.nav-list a').click(function(e){
        e.preventDefault();
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });
    /*$('.submenu .btn').click(function(e){
        e.preventDefault();
        $(this).closest('.submenu').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });*/

    $('.fa-sitemap').click(function(){
        $(this).closest('.topic-item').toggleClass('list-view');
    });
/* --- */
    var Topics = $('.dd>.dd-list>.topic-item'),
        prevTopicsHeight = [],
        TopicsLen = Topics.length,
        TopicsHeader = Topics.find('>.topic-descr>.widget-header'),
        TopicsHeaderArray = [];

    for (var i = 0; i < TopicsLen ;i++){
        TopicsHeaderArray[i] = Topics.eq(i).find('>.topic-descr>.widget-header');
    }

    function GetTopicsHeightForFixedHeader(){

        for (var i = 0; i < TopicsLen ; i++){
            var currentIndex = i;
            prevTopicsHeight[i] = 0;
            /*
             внутренний цикл это обход всех топиков, в том числе и
             не раскрытых, которые предшествуют этому раскрытому,
             чтобы определить их суммарную высоту, для сравнения
             с scrollTop, чтобы понять когда хэдер должен переходить
             в состояние fixed
             */
            for(var j = 0; j < currentIndex; j++){
                prevTopicsHeight[i] += Topics.eq(j).height();
            }
          //  prevTopicsHeight[i] += Topics.eq(currentIndex).find('>.topic-descr').height()-Topics.eq(currentIndex-1).find('>.topic-descr').height();
        }
    }
    GetTopicsHeightForFixedHeader();


    $('.plus-minus').click(function(e){
        e.preventDefault();
        $(this).closest('.dd2-item').find('>.dd-list').slideToggle(200,function(){
            GetTopicsHeightForFixedHeader();
        });

        if ($(this).hasClass('fa-minus')){
            $(this).removeClass('fa-minus').addClass('fa-plus');
        }else{
            $(this).removeClass('fa-plus').addClass('fa-minus');
        }
    });

    // фиксация хэдера темы, если много сообщений

    /*
     верхний цикл: обход всех раскрытых топиков
     */

    $(window).scroll(function(){
        // console.log($(this).scrollTop());

       //убираем сайдбар при прокрутке
        if (w.width()>785){
            if ($(this).scrollTop() > 270){
                $('.sidebar').hide();
                $('.main-content').css('margin-left','0');
                //Topics.find('>.topic-descr>.widget-header').css('margin-right',asideWidth+10);////width('1206');
            }else {
                $('.sidebar').show();
                $('.main-content').css('margin-left','190px');
                //Topics.find('>.topic-descr>.widget-header').css('margin-right',0);////width('1014');
            }
        }

    // фиксация хэдера темы, если много сообщений

        /*
            верхний цикл: обход всех раскрытых топиков
         */
        for (var i = 0; i < TopicsLen ; i++){
            var currentIndex = i;
            //console.log($(this).scrollTop()+'--'+prevTopicsHeight[i]);

            /*
                здесь сравниваем: если прокрутка больше чем высота всех
                предшествующих топиков, то хэдер этого раскрытого топика
                становится в состояние fixed
             */

            if ($(this).scrollTop() > prevTopicsHeight[i]){
                TopicsHeaderArray[currentIndex].addClass('fixed');
                TopicsHeader.css('margin-right',asideWidth+10);////width('1206');
                if ($(this).scrollTop()<270){
                    TopicsHeader.css('margin-right',asideWidth+10);////width('1014');
                }
            }else{
                TopicsHeaderArray[currentIndex].removeClass('fixed').css('margin-right',0);
            }
        }
    });

/* появление wysiwig редактора */
    $('.ans-btn.btn-group').click(function(){
        var cont = $('.wysiwig-wrap').html();
        var widget = $(this).closest('.widget-body');
        if (widget.find('+.widget-box').length <= 0)
        {
            widget.after(cont);
            $('.btn-cancel').click(function(){
                $(this).closest('.widget-box').slideUp(200);
            });
            widget.find('+.widget-box .wysiwyg-editor').css({'height':'200px'}).ace_wysiwyg({
                toolbar_place: function(toolbar) {
                    return $(this).closest('.widget-box').find('.widget-header').prepend(toolbar).children(0).addClass('inline');
                },
                toolbar:
                    [
                        'bold',
                        {name:'italic' , title:'Change Title!', icon: 'icon-leaf'},
                        'strikethrough',
                        null,
                        'insertunorderedlist',
                        'insertorderedlist',
                        null,
                        'justifyleft',
                        'justifycenter',
                        'justifyright'
                    ],
                speech_button:false
            });
        }
            widget.find('+.widget-box').slideToggle(200);
    });
/* --- */

    /* Включение дерева  */
    $('.dd').nestable();

    $('.dd-handle a').on('mousedown', function(e){
        e.stopPropagation();
    });
    /* --- */
});