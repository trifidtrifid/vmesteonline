$(document).ready(function(){
/* простые обработчики событий */
    $('.dropdown-menu li a').click(function(e){
        e.preventDefault();
        $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
    });

    $('.plus-minus').click(function(e){
        e.preventDefault();
        $(this).closest('.dd2-item').find('>.dd-list').slideToggle(200);

        if ($(this).hasClass('fa-minus')){
            $(this).removeClass('fa-minus').addClass('fa-plus');
        }else{
            $(this).removeClass('fa-plus').addClass('fa-minus');
        }
    });

    $('.widget-main').hover(function(){
       $(this).find('.fa-relations').fadeIn(200);
       $(this).find('.fa-sitemap').fadeIn(200);
    },function(){
        $(this).find('.fa-relations').fadeOut(200);
        $(this).find('.fa-sitemap').fadeOut(200);
    });

    $('.ace-nav .btn,.nav-list a').click(function(e){
        e.preventDefault();
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });
    $('.submenu .btn').click(function(e){
        e.preventDefault();
        $(this).closest('.submenu').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });

    $('.fa-sitemap').click(function(){
        $(this).closest('.topic-item').toggleClass('list-view');
    });
/* --- */
    $(window).scroll(function(){

        var allFirstTopic = $('.dd>.dd-list>.topic-item'),
            count = 0,
            currentTopicIndex = [],
            oldPrevTopicsHeight = 0;

       //убираем сайдбар при прокрутке
        if ($(this).scrollTop() > 270){
            $('.sidebar').hide();
            $('.main-content').css('margin-left','0');
            allFirstTopic.find('>.topic-descr>.widget-header').width('956');
        }else {
            $('.sidebar').show();
            $('.main-content').css('margin-left','190px');
            allFirstTopic.find('>.topic-descr>.widget-header').width('765');
        }

    // фиксация хэдера темы, если много сообщений

        /*
        создаем массив с индексами тех топиков, которые раскрыты
        т.к только для этих топиков нужен fixed хэдера
       */
        allFirstTopic.find('>.dd-list:visible').each(function(){
            var currentTopic = $(this).parent();
            currentTopicIndex[count] = allFirstTopic.index(currentTopic);
            count++;
        });

        /*
            верхний цикл: обход всех раскрытых топиков
         */
        for (var i = 0; i < count ; i++){
            var curInd = currentTopicIndex[i],
                prevTopicsHeight = 0;
            /*
                внутренний цикл это обход всех топиков, в том числе и
                не раскрытых, которые предшествуют этому раскрытому,
                чтобы определить их суммарную высоту, для сравнения
                с scrollTop, чтобы понять когда хэдер должен переходить
                в состояние fixed
             */
            for(var j = 0; j < curInd; j++){
                prevTopicsHeight += allFirstTopic.eq(j).height();
            }
            prevTopicsHeight += allFirstTopic.eq(curInd).find('.topic-descr').height();

            /*
                здесь сравниваем: если прокрутка больше чем высота всех
                предшествующих топиков, то хэдер этого раскрытого топика
                становится в состояние fixed
             */
            //allFirstTopic.find('>.topic-descr>.widget-header').width('auto');
            if ($(this).scrollTop()>prevTopicsHeight){
                allFirstTopic.eq(curInd).find('>.topic-descr>.widget-header').addClass('fixed');
                allFirstTopic.find('>.topic-descr>.widget-header').width('956');
                allFirstTopic.eq(curInd).find('>.topic-descr>.widget-body').hide();
                if ($(this).scrollTop()<270){
                    allFirstTopic.find('>.topic-descr>.widget-header').width('765');
                }
            }else{
                allFirstTopic.eq(curInd).find('>.topic-descr>.widget-header').removeClass('fixed');
                allFirstTopic.eq(curInd).find('>.topic-descr>.widget-body').show();
            }
            oldPrevTopicsHeight = prevTopicsHeight;
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