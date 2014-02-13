$(document).ready(function(){
/* --- */
var w = $(window),
    asideWidth = (w.width()-$('.container').width())/2;

var transport = new Thrift.Transport("/thrift/UserService");
var protocol = new Thrift.Protocol(transport);
var client = new com.vmesteonline.be.UserServiceClient(protocol);

var Groups = client.getUserGroups();
var Rubrics = client.getUserRubrics();

transport = new Thrift.Transport("/thrift/MessageService");
protocol = new Thrift.Protocol(transport);
client = new com.vmesteonline.be.MessageServiceClient(protocol);

var mesLen,
    mesNew,
    level=0,
    message,
    messageListNew = "",
    messageList = '',
    messageListTopLevel = "",
    topicsList="",
    oneTopicContent,
    messagesArray,
    iterator = 0;

$('.submenu li:first-child, #sidebar .nav-list li:first-child').addClass('active');

       $('.submenu .btn').click(function(e){
    e.preventDefault();
    $(this).closest('.submenu').find('.active').removeClass('active');
    $(this).parent().addClass('active');
    var groupID = $(this).data('groupid');
    topicsContent = client.getTopics(groupID,Rubrics[0].id,0,0,10);
    var topicLen = topicsContent.topics.length;
    $('.dd>.dd-list').html('');
    //alert('s');

    for(var i = 0; i < topicLen; i++){
        oneTopicContent = topicsContent.topics[i];
        topicsList = '<li class="dd-item dd2-item topic-item" data-id="15">'+
            '<div class="dd2-content widget-box topic-descr">'+
            '<header class="widget-header header-color-blue2">'+
            '<span class="topic-header-date">01.04.2014 10:10</span>'+
            '<span class="topic-header-left">'+
            '<i class="fa fa-minus"></i>'+
            '<i class="fa fa-sitemap"></i>'+
            '</span>'+
            '<div class="widget-toolbar no-border">'+
            '<a class="fa fa-thumb-tack fa-2x" href="#"></a>'+
            '<a class="fa fa-check-square-o fa-2x" href="#"></a>'+
            '<a class="fa fa-times fa-2x" href="#"></a>'+
            '</div>'+
            '<h2>'+ oneTopicContent.subject +'</h2>'+
            '</header>'+
            '<div class="widget-body">'+
            '<div class="widget-main">'+
            '<div class="topic-left">'+
            '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"/></a>'+
            '<div class="topic-author">'+
            '<a href="#">Иван Грозный</a>'+
            '<div class="author-rating">'+
            '<a href="#" class="fa fa-star"></a>'+
            '<a class="fa fa-star" href="#"></a>'+
            '<a class="fa fa-star" href="#"></a>'+
            '<a class="fa fa-star-half-o" href="#"></a>'+
            '<a class="fa fa-star-o" href="#"></a>'+
            '</div>'+
            '</div>'+
            '</div>'+
            '<div class="topic-right">'+
            '<a class="fa fa-link fa-relations" href="#"></a>'+
            '<p class="alert ">'+ topic.message.content +'</p>'+
            '<div class="likes">'+
            '<a href="#" class="like-item like">'+
            '<i class="fa fa-thumbs-o-up"></i>'+
            '<span>'+ oneTopicContent.likesNum +'</span>'+
            '</a>'+
            '<a href="#" class="like-item dislike">'+
            '<i class="fa fa-thumbs-o-down"></i>'+
            '<span>'+ oneTopicContent.unlikesNum +'</span>'+
            '</a>'+
            '</div>'+
            '</div>'+
            '</div>'+
            '<footer class="widget-toolbox clearfix">'+
            '<div class="btn-group ans-btn">'+
            '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">'+
            'Ответить'+
            '<span class="icon-caret-down icon-on-right"></span>'+
            '</button>'+
            '<ul class="dropdown-menu dropdown-warning">'+
            '<li>'+
            '<a href="#">Ответить лично</a>'+
            '</li>'+
            '</ul>'+
            '</div>'+
            '<div class="answers-ctrl">'+
            '<a class="fa fa-minus plus-minus" href="#"></a>'+
            '<span> <span>'+ oneTopicContent.messageNum +'</span> <a href="#">(3)</a></span>'+
            '</div>'+
            '<div class="topic-statistic">'+
            'Участников '+ oneTopicContent.usersNum + 'Просмторов '+ oneTopicContent.viewers +
            '</div>'+
            '</footer>'+
            '</div>'+
            '</div>'+
            '</li>';
        $('.dd>.dd-list').append(topicsList);
        //message = topicsContent.topics[i];
        iterator = 0;
        messageList="";
        level = 0;
        messageListNew="";
        messageListTopLevel = getMessageList(oneTopicContent.id, groupID, oneTopicContent.id);
        //console.log(messageListTopLevel);
        $('.dd>.dd-list>.topic-item:eq(' + i + ')').append(messageListTopLevel);
    }
});

//var topicsContent = client.getTopics(Groups[0].id,Rubrics[0].id, 0, 0, 10);
//var topicLen = topicsContent.topics.length;

/*        function getMessageList(topicID, groupID, parentID){
    messagesArray = client.getMessages(topicID, groupID, 1, parentID, 0, 0, 4).messages;
    mesLen=0;
    if (messagesArray){mesLen = messagesArray.length;}
    if (mesLen > 0 && level < 6){
        level++;
        while(iterator < mesLen){
            var mesNew = messagesArray[iterator];
            messageListNew += getMessageList(topicID, groupID, mesNew.id);
            //alert(messageListNew);
            iterator++;
        }
        iterator=0;
        messageList = '<ol class="dd-list">'+
            '<li class="dd-item dd2-item" data-id="19">'+
            '<div class="dd2-content topic-descr one-message widget-body">'+
            '<div class="widget-main">'+
            '<div class="topic-left">'+
            '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"></a>'+
            '</div>'+
            '<div class="topic-right">'+
            '<div class="message-author">'+
            '<a class="fa fa-link fa-relations" href="#" style="display: none;"></a>'+
            '<a class="fa fa-sitemap" href="#" style="display: none;"></a>'+
            '<a href="#">Иван Грозный</a>'+
            '</div>'+
            '<p class="alert">'+ mesNew.content+ '</p>'+
            '<div class="likes">'+
            '<div class="answer-date">' + mesNew.created + '</div>'+
            '<a href="#" class="like-item like">'+
            '<i class="fa fa-thumbs-o-up"></i>'+
            '<span>' + mesNew.likesNum + '</span>'+
            '</a>'+
            '<a href="#" class="like-item dislike">'+
            '<i class="fa fa-thumbs-o-down"></i>'+
            '<span>' + mesNew.unlikesNum + '</span>'+
            '</a>'+
            '</div>'+
            '</div>'+
            '</div>'+
            '<footer class="widget-toolbox padding-4 clearfix">'+
            '<div class="btn-group ans-btn">'+
            '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">'+
            'Ответить'+
            '<span class="icon-caret-down icon-on-right"></span>'+
            '</button>'+
            '<ul class="dropdown-menu dropdown-warning">'+
            '<li>'+
            '<a href="#">Ответить лично</a>'+
            '</li>'+
            '</ul>'+
            '</div>'+
            '<div class="answers-ctrl">'+
            '<a class="fa fa-minus plus-minus" href="#"></a>'+
            '<span> <span>8</span> <a href="#">(3)</a></span>'+
            '</div>'+
            '</footer>'+
            '</div>'+
            messageListNew +
            '</li>'+
            '</ol>';
    } else {
        messageListNew = "";
    }
    return messageList;
}*/

/* простые обработчики событий */

function ChangeOrientation() {
var orientation = Math.abs(window.orientation) === 90 ? 'landscape' : 'portrait';
alert(orientation);
//alert('1');
}

    function SetLikeClick(selector){
        selector.click(function(e){
            e.preventDefault();

            var oldLikesVal = $(this).find('span').text();
            var topicID = $(this).closest('.topic-item').data('topicid');
            $(this).find('span').text(++oldLikesVal);

            if($(this).closest('.one-message').length <= 0){
                if ($(this).hasClass('like')){
                    client.likeTopic(topicID);
                }else{
                    client.dislikeTopic(topicID);
                }
            }else{
                if ($(this).hasClass('like')){
                    client.like(topicID);
                }else{
                    client.dislike(topicID);
                }
            }

        });
    }
    SetLikeClick($('.like-item'));


window.addEventListener('orientationchange', ChangeOrientation, false);

$('.widget-main').hover(function(){
    $(this).find('.fa-relations').animate({opacity:1},200);
    $(this).find('.fa-sitemap').animate({opacity:1},200);
    },function(){
    $(this).find('.fa-relations').animate({opacity:0},200);
    $(this).find('.fa-sitemap').animate({opacity:0},200);
});

$('.fa-sitemap').click(function(){
    $(this).closest('.topic-item').toggleClass('list-view');
});
/* --- */

var topics = $('.dd>.dd-list>.topic-item'),
prevTopicsHeight = [],
topicsLen = topics.length,
topicsHeader = topics.find('>.topic-descr>.widget-header'),
topicsHeaderArray = [];

/* заносим DOM элементы в массив для оптимизации */
for (var i = 0; i < topicsLen ;i++){
    topicsHeaderArray[i] = topics.eq(i).find('>.topic-descr>.widget-header');
}
/* ---- */

/*
ф-я определения высот топиков для того, чтобы
делать fixed хэдеры. Параметр: индекс топика с которого
начинать отсчет высот.
*/
function GetTopicsHeightForFixedHeader(beginTopicIndex){

    for (var i = beginTopicIndex; i < topicsLen ; i++){
        var currentIndex = i;
        prevTopicsHeight[currentIndex] = 0;
        /*
         внутренний цикл это обход всех топиков, в том числе и
         не раскрытых, которые предшествуют этому раскрытому,
         чтобы определить их суммарную высоту, для сравнения
         с scrollTop, чтобы понять когда хэдер должен переходить
         в состояние fixed
         */
        for(var j = 0; j < currentIndex; j++){
            prevTopicsHeight[currentIndex] += topics.eq(j).height();
        }
        //console.log('++'+prevTopicsHeight[currentIndex]);
    }
}
/* --- */
GetTopicsHeightForFixedHeader(0);

/* мега раздел подгрузки и отправки сообщений  */

var zeroLevelFlag = [];
    /*
     создаем массив флагов (каждому топику по одному флагу),
     которые сигнализируют была ли загрузка
     контента к топику(значение 0) или нет(значение 1)
    */
for(i = 0; i < $('.topic-item').length; i++){
    zeroLevelFlag[i] = 1;
}
    /*
    раскрываем топик - начинается самое интересное
    */
$('.topic-item>.topic-descr .plus-minus').click(function(e){
    e.preventDefault();

    if ($(this).hasClass('fa-minus')){
        $(this).removeClass('fa-minus').addClass('fa-plus');
    }else{
        $(this).removeClass('fa-plus').addClass('fa-minus');
    }

    var topicItem = $(this).closest('.topic-item'),
        topicID  = topicItem.data('topicid'),
        currentMessages,
        currentMessagesLength,
        messageHtml,i;

    var index = topicItem.index();

    if (zeroLevelFlag[index]){
        /* Если флаг говорит, что еще не подгружали, значит подгружаем сообщения первого уровня */
        zeroLevelFlag[index] = 0;   // сбрасываем флаг
        /*
        сообщения ПЕРВОГО уровня берем от родителя с id 0 (т.е от топика)
        */
        currentMessages = client.getMessages(topicID,Groups[0].id,1,0,0,0,10).messages;
        currentMessagesLength = currentMessages.length;
        messageHtml = '';

        /*
         создаем html с сообщениями ПЕРВОГО уровня, который будем подгружать
         для этого топика
         */
        for(i = 0; i < currentMessagesLength ; i++){
            messageHtml += '<li class="dd-item dd2-item">'+
                '<div class="dd2-content topic-descr one-message level-1 widget-body" data-parentid="'+ currentMessages[i].parentId +'" data-messageid="'+ currentMessages[i].id +'">'+
                '<div class="widget-main">'+
                '<div class="topic-left">'+
                '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"></a>'+
                '</div>'+
                '<div class="topic-right">'+
                '<div class="message-author">'+
                '<a class="fa fa-link fa-relations" href="#" style="display: none;"></a>'+
                '<a class="fa fa-sitemap" href="#" style="display: none;"></a>'+
                '<a href="#">Иван Грозный</a>'+
                '</div>'+
                '<p class="alert">'+ currentMessages[i].content+ '</p>'+
                '<div class="likes">'+
                '<div class="answer-date">' + currentMessages[i].created + '</div>'+
                '<a href="#" class="like-item like">'+
                '<i class="fa fa-thumbs-o-up"></i>'+
                '<span>' + currentMessages[i].likesNum + '</span>'+
                '</a>'+
                '<a href="#" class="like-item dislike">'+
                '<i class="fa fa-thumbs-o-down"></i>'+
                '<span>' + currentMessages[i].unlikesNum + '</span>'+
                '</a>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '<footer class="widget-toolbox padding-4 clearfix">'+
                '<div class="btn-group ans-btn">'+
                '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">'+
                'Ответить'+
                '<span class="icon-caret-down icon-on-right"></span>'+
                '</button>'+
                '<ul class="dropdown-menu dropdown-warning">'+
                '<li>'+
                '<a href="#">Ответить лично</a>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="answers-ctrl">'+
                '<a class="fa fa-plus plus-minus" href="#"></a>'+
                '<span> <span>8</span> <a href="#">(3)</a></span>'+
                '</div>'+
                '</footer>'+
                '</div>'+
                '</li>';
        }
        /*
        и подгружаем его, в списке
        */
        $(this).closest('.topic-item').append('<ol class="dd-list">' + messageHtml + '</ol>');

        /*
         событие появления wysiwig редактора для сообщений 1го уровня
        (можно вынести в функцию, т.к повторяется)
        */
        $('.level-1 .ans-btn.btn-group').click(function(){
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
                            //{name:'italic' , title:'Change Title!', icon: 'icon-leaf'},
                            'italic',
                            'strikethrough',
                            'underline',
                            null,
                            'insertunorderedlist',
                            'insertorderedlist',
                            null,
                            'justifyleft',
                            'justifycenter',
                            'justifyright',
                            'createLink',
                            'unlink',
                            'insertImage'
                        ],
                    speech_button:false
                });
            }
            widget.find('+.widget-box').slideToggle(200);

            $('.wysiwig-box .btn-primary').click(function(){
                var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
                message = message.replace(new RegExp('&nbsp;','g'),' ');
                var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
                messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
                var topicID = $(this).closest('.topic-item').data('topicid');
                var parentID = $(this).closest('.dd-item').find('.one-message').data('messageid');
                if (parentID === undefined){parentID = 0;}
                //alert('1 '+parentID );
                client.createMessage(topicID,parentID,Groups[0].id,1,messageWithGoodLinks,0,0,0);
            });
        });

        var firstLevelFlag = [];
        /*
        создаем массив флагов сообщений ПЕРВОГО уровня,
         аналогично флагам для топиков (см. выше)
        */
        for (i = 0 ; i < topicItem.find('.level-1').length; i++){
            firstLevelFlag[i] = 1;
        }

        /*
        событие раскрытия/скрытия сообщений остальных урвоней
        карнавал продолжается
        */
        $('.one-message .plus-minus').click(function(e){
            e.preventDefault();

            if ($(this).hasClass('fa-minus')){
                $(this).removeClass('fa-minus').addClass('fa-plus');
            }else{
                $(this).removeClass('fa-plus').addClass('fa-minus');
            }

            var index = $(this).closest('.level-1').parent().index();

            if (firstLevelFlag[index]){
                /*
                 Если флаг стоит, значит
                подгружаем сообщения остальных уровней
                 для этого сообщения ПЕРВОГО уровня
                */
                var parentID = $(this).closest('.one-message').data('messageid');
                var messagesPart = client.getMessages(topicID,Groups[0].id,1,parentID,0,0,10);
                currentMessages = messagesPart.messages;
                currentMessagesLength = currentMessages.length;
                messageHtml = '';

                /*
                создаем html, который содержит в себе
                все остальные сообщения. Html всех сообщений
                идентичен, древовидная структура создается
                за счет параметра offset, который есть у каждого сообщения,
                он определяет margin-left для него.
                */
                for(i = 0; i < currentMessagesLength ; i++){
                    messageHtml += '<li class="dd-item dd2-item" style="margin-left:'+currentMessages[i].offset*30+'px">'+
                        '<div class="dd2-content topic-descr one-message widget-body" data-parentid="'+ currentMessages[i].parentId +'" data-messageid="'+ currentMessages[i].id +'">'+
                        '<div class="widget-main">'+
                        '<div class="topic-left">'+
                        '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"></a>'+
                        '</div>'+
                        '<div class="topic-right">'+
                        '<div class="message-author">'+
                        '<a class="fa fa-link fa-relations" href="#" style="display: none;"></a>'+
                        '<a class="fa fa-sitemap" href="#" style="display: none;"></a>'+
                        '<a href="#">Иван Грозный</a>'+
                        '</div>'+
                        '<p class="alert">'+ currentMessages[i].content + '</p>'+
                        '<div class="likes">'+
                        '<div class="answer-date">' + currentMessages[i].created + '</div>'+
                        '<a href="#" class="like-item like">'+
                        '<i class="fa fa-thumbs-o-up"></i>'+
                        '<span>' + currentMessages[i].likesNum + '</span>'+
                        '</a>'+
                        '<a href="#" class="like-item dislike">'+
                        '<i class="fa fa-thumbs-o-down"></i>'+
                        '<span>' + currentMessages[i].unlikesNum + '</span>'+
                        '</a>'+
                        '</div>'+
                        '</div>'+
                        '</div>'+
                        '<footer class="widget-toolbox padding-4 clearfix">'+
                        '<div class="btn-group ans-btn">'+
                        '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border">'+
                        'Ответить'+
                        '<span class="icon-caret-down icon-on-right"></span>'+
                        '</button>'+
                        '<ul class="dropdown-menu dropdown-warning">'+
                        '<li>'+
                        '<a href="#">Ответить лично</a>'+
                        '</li>'+
                        '</ul>'+
                        '</div>'+
                        '<div class="answers-ctrl">'+
                        '<a class="fa fa-minus plus-minus" href="#"></a>'+
                        '<span> <span>8</span> <a href="#">(3)</a></span>'+
                        '</div>'+
                        '</footer>'+
                        '</div>'+
                        '</li>';
                }
                /*
                и подгружаем его к нашему сообщению
                */
                $(this).closest('.dd-item').after(messageHtml);

                /*
                событие сворачивание разворачивания для любого сообщения,
                кроме ПЕРВОГО уровня
                */
                $('.one-message:not(.level-1) .plus-minus').click(function(e){
                    e.preventDefault();
                    var currentParent=$(this).closest('.one-message').data('messageid');
                    $('.dd-list .dd-list').find('.one-message').each(function(){
                        console.log('1');
                        if ($(this).data('parentid') == currentParent){
                            $(this).slideToggle();
                        }
                    });

                    if ($(this).hasClass('fa-minus')){
                        $(this).removeClass('fa-minus').addClass('fa-plus');
                    }else{
                        $(this).removeClass('fa-plus').addClass('fa-minus');
                    }

                    var currentTopicIndex = $(this).closest('.topic-item').index();
                    GetTopicsHeightForFixedHeader(currentTopicIndex+1);
                });

                /* появление wysiwig редактора (для остальных сообщений) */
                $('.one-message:not(.level-1)').find('.ans-btn.btn-group').click(function(){
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
                                    //{name:'italic' , title:'Change Title!', icon: 'icon-leaf'},
                                    'italic',
                                    'strikethrough',
                                    'underline',
                                    null,
                                    'insertunorderedlist',
                                    'insertorderedlist',
                                    null,
                                    'justifyleft',
                                    'justifycenter',
                                    'justifyright',
                                    'createLink',
                                    'unlink',
                                    'insertImage'
                                ],
                            speech_button:false
                        });
                    }
                    widget.find('+.widget-box').slideToggle(200);

                    $('.wysiwig-box .btn-primary').click(function(){
                        var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
                        message = message.replace(new RegExp('&nbsp;','g'),' ');
                        var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
                        messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
                        var topicID = $(this).closest('.topic-item').data('topicid');
                        var parentID = $(this).closest('.dd-item').find('.one-message').data('messageid');
                        if (parentID === undefined){parentID = 0;}
                        client.createMessage(topicID,parentID,Groups[0].id,1,messageWithGoodLinks,0,0,0);
                    });
                });
                /* --- */
                SetLikeClick($('.one-message:not(.level-1) .like-item'));
                firstLevelFlag[index] = 0;
            }else{
                /*
                Иначе сворачиваем-разворачиваем сообщения
                 Кого свернуть-развернуть смотрим с помощью параметра parentId
                */
                var currentParent=$(this).closest('.one-message').data('messageid');
                $('.dd-list .dd-list').find('.one-message').each(function(){
                    console.log('1');
                    if ($(this).data('parentid') == currentParent){
                        $(this).slideToggle();
                    }
                });
            }
        });
        SetLikeClick(topicItem.find('.level-1 .like-item'));

    } else {
        /*
         Если контент уже был загружен, то просто
        скрываем раскрываем сообщения ПЕРВОГО уроввня для данного топика
        и пересчитываем высоты для fixed хэдеров.
        */
        $(this).closest('.dd2-item').find('>.dd-list').slideToggle(200,function(){
            var currentTopicIndex = $(this).closest('.topic-item').index();
            GetTopicsHeightForFixedHeader(currentTopicIndex+1);
        });
    }
});

/* конец мега раздела */

var staffCounterForGoodTopicsHeight = 0;

$(window).scroll(function(){
    // console.log($(this).scrollTop());
    var scrollTop = $(this).scrollTop();

    //убираем сайдбар при прокрутке
    if (w.width()>785){
        if (scrollTop > 270){
            $('.sidebar').hide();
            $('.main-content').css('margin-left','0');
            $('.widget-header h2').css('min-width','996px');
            staffCounterForGoodTopicsHeight++;
        }else {
            $('.sidebar').show();
            $('.main-content').css('margin-left','190px');
            $('.widget-header h2').css('min-width','805px');
            staffCounterForGoodTopicsHeight=0;
        }
    }
    if (staffCounterForGoodTopicsHeight == 1){
        GetTopicsHeightForFixedHeader(0);
    }

    // фиксация хэдера темы, если много сообщений

    /*
     верхний цикл: обход всех раскрытых топиков
     */
    for (var i = 0; i < topicsLen ; i++){
        var currentIndex = i;
        //console.log(scrollTop+'--'+prevTopicsHeight[i]);

        /*
         здесь сравниваем: если прокрутка больше чем высота всех
         предшествующих топиков, то хэдер этого раскрытого топика
         становится в состояние fixed
         */

        if (scrollTop > prevTopicsHeight[i]){
            topicsHeaderArray[currentIndex].addClass('fixed');
            topicsHeader.css('margin-right',asideWidth+10);////width('1206');
            if (scrollTop<270){
                topicsHeader.css('margin-right',asideWidth+10);////width('1014');
            }
        }else{
            topicsHeaderArray[currentIndex].removeClass('fixed').css('margin-right',0);
        }
    }
});

/* появление wysiwig редактора (для топиков) */
$('.ans-btn.btn-group').click(function(){
    //alert('d');
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
                    //{name:'italic' , title:'Change Title!', icon: 'icon-leaf'},
                    'italic',
                    'strikethrough',
                    'underline',
                    null,
                    'insertunorderedlist',
                    'insertorderedlist',
                    null,
                    'justifyleft',
                    'justifycenter',
                    'justifyright',
                    'createLink',
                    'unlink',
                    'insertImage'
                ],
            speech_button:false
        });
    }
    widget.find('+.widget-box').slideToggle(200);

    //$('.one-message+.wysiwig-box .btn-primary');
    $('.wysiwig-box .btn-primary').click(function(){
        var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
        message = message.replace(new RegExp('&nbsp;','g'),' ');
        var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
        messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
        //client.createTopic(Groups[0].id,'Тест тема-1',1,messageWithGoodLinks,0,0,Rubrics[0].id,1)
        var topicID = $(this).closest('.topic-item').data('topicid');
        var parentID = $(this).closest('.dd-tem').find('.one-message').data('messageid');
        if (parentID === undefined){parentID = 0;}

        client.createMessage(topicID,parentID,Groups[0].id,1,messageWithGoodLinks,0,0,0);
        //alert(messageWithGoodLinks);
    });
});
/* --- */
/*
-----------------------------------------------------------
АВТОМАТИЧЕСКОЕ ОПРЕДЕЛЕНИЕ ССЫЛКИ В СТРОКЕ
-----------------------------------------------------------
*/
function AutoReplaceLinkAndVideo(str) {
    var regexp = /^(.* )?(http[s]?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})(\/[\/\da-z\.=\-?]*)*\/?( ?.*)?$/gmi,
    arrayWithLinks = regexp.exec(str),
    res = str;
    if (arrayWithLinks && arrayWithLinks.length > 0){
        var currentLink = arrayWithLinks[2]+arrayWithLinks[3]+'.'+arrayWithLinks[4]+arrayWithLinks[5];
        var prefix = arrayWithLinks[1];
        var suffix = arrayWithLinks[6];
        var iframe = "";

        if (arrayWithLinks[3].indexOf('youtu') != -1){
            // у ютуба несколько отличается ссылка и айфрэйм
            iframe = '<iframe width="560" height="315" src="//www.youtube.com/embed/'+ arrayWithLinks[5] +'" frameborder="0" allowfullscreen></iframe>';
        }else if(arrayWithLinks[3].indexOf('vimeo') != -1){
            iframe = '<iframe src="'+ currentLink +'" width="500" height="281" frameborder="0"'+
                ' webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>';
        }else{
            iframe = '<a href="'+currentLink+'" target="_blank">'+currentLink+'</a>';
        }

        res = AutoReplaceLinkAndVideo(prefix) + iframe + AutoReplaceLinkAndVideo(suffix)
}
    return res;
}

/* ------------ */

/* Включение дерева (а нужно ли теперь ?) */
$('.dd').nestable();

$('.dd-handle a').on('mousedown', function(e){
    e.stopPropagation();
});
/* --- */
});

