$(document).ready(function(){
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

/* появление wysiwig редактора */
    $('.widget-box .wysiwyg-editor').css({'height':'300px'}).ace_wysiwyg({
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
/* --- */

});