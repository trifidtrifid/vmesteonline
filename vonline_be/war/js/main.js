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

var level=0,
    message,
    oneTopicContent;

/* простые обработчики событий */

$('.submenu li:first-child, #sidebar .nav-list li:first-child').addClass('active');

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

    /* переключение между группами и рубриками */
   $('.submenu .btn,.nav-list a').click(function(e){
    e.preventDefault();

       var forum = $('.forum');
       if (forum.hasClass('create-topic')){
           forum.removeClass('create-topic');
       }
       var forumHtml = '<div class="dd dd-draghandle"><ol class="dd-list"></ol></div>';
       forum.html('');
       forum.append(forumHtml);

       if ($(this).closest('.submenu').length){
           $(this).closest('.submenu').find('.active').removeClass('active');
       } else{
           $(this).closest('.nav-list').find('.active').removeClass('active');
       }
    $(this).parent().addClass('active');
    var groupID = $('.submenu .active .btn').data('groupid');
    var rubricID = $('.nav-list .active a').data('rubricid');
    var topicsContent = client.getTopics(groupID,rubricID,0,0,10);
       var topicLen = 0;
       if (topicsContent.topics){
           topicLen = topicsContent.topics.length;
       }

    var ddList = $('.dd>.dd-list');

       ddList.append(TopicHtmlConstructor(topicsContent,topicLen));
       SetMainEvents($('.dd>.dd-list>.topic-item'));
});

    function SetLikeClick(selector){
        selector.click(function(e){
            e.preventDefault();

            if($(this).closest('.one-message').length <= 0){
                var topicID = $(this).closest('.topic-item').data('topicid');
                if ($(this).hasClass('like')){
                    $(this).find('span').text(client.likeTopic(topicID));
                }else{
                    $(this).find('span').text(client.dislikeTopic(topicID));
                }
            }else{
                var messageID = $(this).closest('.one-message').data('messageid');
                if ($(this).hasClass('like')){
                    $(this).find('span').text(client.like(messageID));
                }else{
                    $(this).find('span').text(client.dislike(messageID));
                }
            }

        });
    }

    function SetCreateMessageClick(selector){
        selector.click(function(){
            var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
            message = message.replace(new RegExp('&nbsp;','g'),' ');
            var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
            messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
            var topicID = $(this).closest('.topic-item').data('topicid');
            var parentID = $(this).closest('.dd-item').find('.one-message').data('messageid');
            if (parentID === undefined || $(this).closest('.dd-item').hasClass('topic-item')){parentID = 0;}
            client.createMessage(topicID,parentID,Groups[0].id,1,messageWithGoodLinks,0,0,0);
        });
    }

    function SetShowEditorClick(selector){
        selector.click(function(e){

            e.preventDefault();
            //alert('1');
            
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

            SetCreateMessageClick($('.wysiwig-box .btn-primary'));
        });
    }

    function FindChildForSaveOrLoadStates(allMessages,currentMessageIndex,currentMessageOffset){

        var nextMessageIndexOnThisLevel = 0,
            nextMessageIndexOnAllLevels = allMessages.length;
        /*
         здесь ищем следующее сообщение на том же level, что и текущее (nextMessageIndexOnThisLevel), чтобы понять какие сообщения
         являются внутренними. Может быть ситуация, когда на том же уровне больше нет собщений, тогда нужно
         найти первое сообщение на уровне выше (nextMessageIndexOnAllLevels).
         */
        allMessages.each(function(){
            var tempMessageIndex = $(this).index();

            if (tempMessageIndex > currentMessageIndex && !nextMessageIndexOnThisLevel && nextMessageIndexOnAllLevels == allMessages.length ){

                if ($(this).data('offset') == currentMessageOffset){
                    nextMessageIndexOnThisLevel = $(this).index();
                }
                if($(this).data('offset') < currentMessageOffset){
                    nextMessageIndexOnAllLevels = $(this).index();
                }
            }
        });

        if (!nextMessageIndexOnThisLevel){nextMessageIndexOnThisLevel = nextMessageIndexOnAllLevels;}

        return allMessages.slice(currentMessageIndex+1,nextMessageIndexOnThisLevel);
    }

    function SaveOrLoadMessagesStates(selector,topicItem){

        var currentMessageOffset = selector.closest('.one-message').parent().data('offset');
        var currentMessageIndex = selector.closest('.one-message').parent().index();
        var allMessages = topicItem.find('.dd-list').find('.one-message').parent(),
            childOfMessage,
            topicIndex = topicItem.index();

        if (selector.hasClass('fa-minus')){
            /* клик на минус/сворачивание/запоминание состояний */

            childOfMessage =  FindChildForSaveOrLoadStates(allMessages,currentMessageIndex,currentMessageOffset);

            /* запоминание состояний */
            childOfMessage.each(function(){
                if ($(this).css('display') == 'none'){
                    topicsMessagesStates[topicIndex].arrayOfStateMessages[currentMessageIndex][$(this).index()] = 'hidden';
                }else{
                    topicsMessagesStates[topicIndex].arrayOfStateMessages[currentMessageIndex][$(this).index()] = 'visible';
                }
            });
            childOfMessage.slideUp();

        }else{
            /* клик на плюс/разворачивание/вспоминание состояний */

            childOfMessage =  FindChildForSaveOrLoadStates(allMessages,currentMessageIndex,currentMessageOffset);

            /* вспоминание состояний */
            childOfMessage.each(function(){
                if(topicsMessagesStates[topicIndex].arrayOfStateMessages[currentMessageIndex][$(this).index()] == 'hidden'){
                    $(this).slideUp();
                }else{
                    $(this).slideDown();
                }
            });

        }
    }

    function GetTopicsHeightForFixedHeader(beginTopicIndex,topics,topicsLen,prevTopicsHeight){
    /*
     ф-я определения высот топиков для того, чтобы
     делать fixed хэдеры. Параметр: индекс топика с которого
     начинать отсчет высот.
     */
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

    function MessageHtmlConstructor(arrayOfData,len,level1){
        var messageHtml = "";
        /*
         создаем html, который содержит в себе все остальные сообщения. Html всех сообщений
         идентичен, древовидная структура создается за счет параметра offset, который есть у каждого сообщения,
         он определяет margin-left для него.
         */
        for(var i = 0; i < len ; i++){
            if (level1){
                messageHtml += '<li class="dd-item dd2-item" data-offset="'+ arrayOfData[i].offset +'">'+
                    '<div class="dd2-content topic-descr one-message level-1 widget-body" data-level1index="'+ i +'" data-parentid="'+ arrayOfData[i].parentId +'" data-messageid="'+ arrayOfData[i].id +'">';
            }else{
                messageHtml += '<li class="dd-item dd2-item" data-offset="'+ arrayOfData[i].offset +'" style="margin-left:'+arrayOfData[i].offset*30+'px">'+
                    '<div class="dd2-content topic-descr one-message widget-body"  data-parentid="'+ arrayOfData[i].parentId +'" data-messageid="'+ arrayOfData[i].id +'">';
            }
            messageHtml += '<div class="widget-main">'+
                '<div class="topic-left">'+
                '<a href="#"><img src="i/avatars/clint.jpg" alt="картинка"></a>'+
                '</div>'+
                '<div class="topic-right">'+
                '<div class="message-author">'+
                '<a class="fa fa-link fa-relations" href="#" style="display: none;"></a>'+
                '<a class="fa fa-sitemap" href="#" style="display: none;"></a>'+
                '<a href="#">'+ arrayOfData[i].userInfo.firstName + " " + arrayOfData[i].userInfo.lastName +'</a>'+
                '</div>'+
                '<p class="alert">'+ arrayOfData[i].content+ '</p>'+
                '<div class="likes">'+
                '<div class="answer-date">' + arrayOfData[i].created + '</div>'+
                '<a href="#" class="like-item like">'+
                '<i class="fa fa-thumbs-o-up"></i>'+
                '<span>' + arrayOfData[i].likesNum + '</span>'+
                '</a>'+
                '<a href="#" class="like-item dislike">'+
                '<i class="fa fa-thumbs-o-down"></i>'+
                '<span>' + arrayOfData[i].unlikesNum + '</span>'+
                '</a>'+
                '</div>'+
                '</div>'+
                '</div>'+
                '<footer class="widget-toolbox padding-4 clearfix">'+
                '<div class="btn-group ans-btn">'+
                '<button class="btn btn-primary btn-sm dropdown-toggle no-border ans-all">Ответить</button>'+
                '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border ans-pers">'+
                '<span class="icon-caret-down icon-only smaller-90"></span>'+
                '</button>'+
                '<ul class="dropdown-menu dropdown-warning">'+
                '<li>'+
                '<a href="#">Ответить лично</a>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="answers-ctrl">';

            if (level1){
                messageHtml += '<a class="fa fa-plus plus-minus" href="#"></a>';
            }else{
                messageHtml += '<a class="fa fa-minus plus-minus" href="#"></a>';
            }

            messageHtml += '<span> <span>8</span> <a href="#">(3)</a></span>'+
                '</div>'+
                '</footer>'+
                '</div>'+
                '</li>';
        }
        return messageHtml;
    }

    function TopicHtmlConstructor(arrayOfData,len){
        var topicsList="";
        for(var i = 0; i < len; i++){
            oneTopicContent = arrayOfData.topics[i];
            var hide = "";
            if (oneTopicContent.messageNum == 0){
                hide = 'hide';
            }
            topicsList += '<li class="dd-item dd2-item topic-item" data-topicid="'+ oneTopicContent.id +'">'+
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
                '<a href="#">'+oneTopicContent.userInfo.firstName+' '+oneTopicContent.userInfo.lastName+'</a>'+
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
                '<p class="alert ">'+ oneTopicContent.message.content +'</p>'+
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
                '<button class="btn btn-primary btn-sm dropdown-toggle no-border ans-all">Ответить</button>'+
                '<button data-toggle="dropdown" class="btn btn-primary btn-sm dropdown-toggle no-border ans-pers">'+
                '<span class="icon-caret-down icon-only smaller-90"></span>'+
                '</button>'+
                '<ul class="dropdown-menu dropdown-warning">'+
                '<li>'+
                '<a href="#">Ответить лично</a>'+
                '</li>'+
                '</ul>'+
                '</div>'+
                '<div class="answers-ctrl">'+
                '<a class="fa fa-plus plus-minus '+ hide +'" href="#"></a>'+
                '<span> <span>'+ oneTopicContent.messageNum +'</span> <a href="#">(3)</a></span>'+
                '</div>'+
                '<div class="topic-statistic">'+
                'Участников '+ oneTopicContent.usersNum + 'Просмотров '+ oneTopicContent.viewers +
                '</div>'+
                '</footer>'+
                '</div>'+
                '</div>'+
                '</li>';
        }
        return topicsList;
    }

    function TopicHeaderOptimization(topics){
        /* заносим DOM элементы в массив для оптимизации */
        var topicsLen = topics.length;
        var topicsHeaderArray = [];
        for (var i = 0; i < topicsLen ;i++){
            topicsHeaderArray[i] = topics.eq(i).find('>.topic-descr>.widget-header');
        }

        return topicsHeaderArray;
    }

    function SetGlobalParameters(){
        topics = $('.dd>.dd-list>.topic-item');
        prevTopicsHeight = [];
        topicsLen = topics.length;
        topicsHeader = topics.find('>.topic-descr>.widget-header');
        topicsHeaderArray = TopicHeaderOptimization(topics);

        staffCounterForGoodTopicsHeight = 0;
        groupID = $('.submenu .active .btn').data('groupid');
        rubricID = $('.nav-list .active a').data('rubricid');
        lastTopicId = $('.dd .topic-item:last').data('topicid');

        FlagOfEndTopics = 1;
    }

/* --- */

/* мега раздел подгрузки и отправки сообщений  */

    /*
     конструктор для объектов топиков. У каждого топика выведенного на страницу
     есть массив состояний для каждого из его внутренних сообщений. Это двумерный массив, где первый
     индекс это индекс сообщения в контексте которого сохранено состояние, а второй индекс
     это индекс "внутреннего" сообщения чье состояние нам нужно в этом контексте.
     Эта штука нужна для корректного сворачивания-разворачивания сообщений. Здесь же мы просто
     иниицализируем эту структуры данных.
     */
    function StateClass(){}

    var topicsMessagesStates = [];
    var tempLen = $('.topic-item').length;

    for(i = 0; i < tempLen; i++){
        topicsMessagesStates[i] = new StateClass();
        topicsMessagesStates[i].arrayOfStateMessages = new Array(20);
        var tempLen2 = topicsMessagesStates[i].arrayOfStateMessages.length;
        for(var j = 0; j < tempLen2; j++) {
            topicsMessagesStates[i].arrayOfStateMessages[j]=[];
        }
    }
    /* --- */

    function SetMainEvents(topicsSelector){
        SetGlobalParameters();
        /*
         конструктор для объектов топиков. У каждого топика выведенного на страницу
         есть массив состояний для каждого из его внутренних сообщений. Это двумерный массив, где первый
         индекс это индекс сообщения в контексте которого сохранено состояние, а второй индекс
         это индекс "внутреннего" сообщения чье состояние нам нужно в этом контексте.
         Эта штука нужна для корректного сворачивания-разворачивания сообщений. Здесь же мы просто
         иниицализируем эту структуры данных.
         */
        function StateClass(){}

        topicsMessagesStates = [];
        var tempLen = $('.topic-item').length;

        for(var i = 0; i < tempLen; i++){
            topicsMessagesStates[i] = new StateClass();
            topicsMessagesStates[i].arrayOfStateMessages = new Array(20);
            var tempLen2 = topicsMessagesStates[i].arrayOfStateMessages.length;
            for(var j = 0; j < tempLen2; j++) {
                topicsMessagesStates[i].arrayOfStateMessages[j]=[];
            }
        }
        /* --- */

        var zeroLevelFlag = [];
        /*
         создаем массив флагов (каждому топику по одному флагу), которые сигнализируют была ли загрузка
         контента к топику(значение 0) или нет(значение 1)
         */
        for(i = 0; i < tempLen; i++){
            zeroLevelFlag[i] = 1;
        }

        /* раскрываем топик - начинается самое интересное */
        topicsSelector.find('>.topic-descr .plus-minus').click(function(e){
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

                /* сообщения ПЕРВОГО уровня берем от родителя с id 0 (т.е от топика) */
                currentMessages = client.getMessages(topicID,Groups[0].id,1,0,0,0,10).messages;
                currentMessagesLength = currentMessages.length;
                messageHtml = '';

                /* создаем html с сообщениями ПЕРВОГО уровня, который будем подгружать для этого топика */
                /* и подгружаем его, в списке */
                topicItem.append('<ol class="dd-list">' + MessageHtmlConstructor(currentMessages,currentMessagesLength,true) + '</ol>');

                var firstLevelFlag = [];
                /* создаем массив флагов сообщений ПЕРВОГО уровня, аналогично флагам для топиков (см. выше) */
                var tempLen = topicItem.find('.level-1').length;
                for (i = 0 ; i < tempLen; i++){
                    firstLevelFlag[i] = 1;
                }

                /* событие раскрытия/скрытия сообщений остальных урвоней. Чтобы не было повторений важно указывать в селекторе topicItem. */
                topicItem.find('.level-1 .plus-minus').click(function(e){
                    e.preventDefault();

                    /* класс служит для того чтобы не вешать событие по нескольку раз на одно и тоже сообщение */
                    topicItem.find('.one-message').addClass('withPlusMinusClick');
                    var index = $(this).closest('.level-1').data('level1index');//parent().index();

                    if (firstLevelFlag[index]){
                        /* Если флаг стоит, значит подгружаем сообщения остальных уровней для этого сообщения ПЕРВОГО уровня */
                        var parentID = $(this).closest('.one-message').data('messageid');
                        var messagesPart = client.getMessages(topicID,Groups[0].id,1,parentID,0,0,10);
                        if (messagesPart.messages){       // добавляем html только если есть внутренние сообщения
                        currentMessages = messagesPart.messages;
                        currentMessagesLength = currentMessages.length;
                        messageHtml = '';

                        /* подгружаем остальные сообщения к этому сообщению первого уровня */
                        $(this).closest('.dd-item').after(MessageHtmlConstructor(currentMessages,currentMessagesLength,false));

                        /* событие сворачивание разворачивания для любого сообщения, кроме ПЕРВОГО уровня */
                         topicItem.find('.one-message:not(.level-1):not(.withPlusMinusClick) .plus-minus').click(function(e){

                            e.preventDefault();
                             topicItem.find('.one-message').addClass('withPlusMinusClick');

                             /* Иначе сворачиваем-разворачиваем сообщения */
                             SaveOrLoadMessagesStates($(this),topicItem);

                            if ($(this).hasClass('fa-minus')){
                                $(this).removeClass('fa-minus').addClass('fa-plus');
                            }else{
                                $(this).removeClass('fa-plus').addClass('fa-minus');
                            }

                            var currentTopicIndex = topicItem.index();
                            GetTopicsHeightForFixedHeader(currentTopicIndex+1,topics,topicsLen,prevTopicsHeight);
                        });

                        /* появление wysiwig редактора (для остальных сообщений) */
                        SetShowEditorClick(topicItem.find('.one-message:not(.level-1):not(.withPlusMinusClick)').find('.ans-btn.btn-group .ans-all,.ans-btn.btn-group .dropdown-menu a'));
                        /* --- */
                        SetLikeClick(topicItem.find('.one-message:not(.level-1):not(.withPlusMinusClick) .like-item'));
                        firstLevelFlag[index] = 0;
                        }
                    }else{
                        /* Иначе сворачиваем-разворачиваем сообщения */
                        SaveOrLoadMessagesStates($(this),topicItem);
                    }

                    if ($(this).hasClass('fa-minus')){
                        $(this).removeClass('fa-minus').addClass('fa-plus');
                    }else{
                        $(this).removeClass('fa-plus').addClass('fa-minus');
                    }
                });
                /*
                 событие появления wysiwig редактора для сообщений 1го уровня
                 (можно вынести в функцию, т.к повторяется)
                 */
                SetShowEditorClick(topicItem.find('.level-1 .ans-btn.btn-group .ans-all,.level-1 .ans-btn.btn-group .dropdown-menu a'));
                SetLikeClick(topicItem.find('.level-1 .like-item'));

            } else {
                /*
                 Если контент уже был загружен, то просто скрываем раскрываем сообщения ПЕРВОГО уроввня для данного топика
                 и пересчитываем высоты для fixed хэдеров.
                 */
                $(this).closest('.dd2-item').find('>.dd-list').slideToggle(200,function(){
                    var currentTopicIndex = topicItem.index();
                    GetTopicsHeightForFixedHeader(currentTopicIndex+1,topics,topicsLen,prevTopicsHeight);
                });
            }
        });
        /* появление wysiwig редактора (для топиков) */
        SetShowEditorClick(topicsSelector.find('.ans-btn.btn-group .ans-all,.ans-btn.btn-group .dropdown-menu a'));
        /* --- */
        SetLikeClick(topicsSelector.find('.like-item'));

        GetTopicsHeightForFixedHeader(0,topics,topicsLen,prevTopicsHeight);
    }
    SetMainEvents($('.dd>.dd-list>.topic-item'));

/* глобальные переменные */
    var topics,
        prevTopicsHeight,
        topicsLen,
        topicsHeader,
        topicsHeaderArray,
        staffCounterForGoodTopicsHeight,
        groupID,
        rubricID,
        lastTopicId,
        FlagOfEndTopics = 1;
/* --------------------- */
    SetGlobalParameters();

    $(window).scroll(function(){
        var scrollTop = $(this).scrollTop();

        //убираем сайдбар при прокрутке
        if (w.width()>785){
            if (scrollTop > 270){
                $('.sidebar').hide();
                $('.main-content').css('margin-left','0');
                $('.widget-header h2').css('min-width','994px');
                staffCounterForGoodTopicsHeight++;
            }else {
                $('.sidebar').show();
                $('.main-content').css('margin-left','190px');
                $('.widget-header h2').css('min-width','804px');
                staffCounterForGoodTopicsHeight=0;
            }
        }
        if (staffCounterForGoodTopicsHeight == 1){
            GetTopicsHeightForFixedHeader(0,topics,topicsLen,prevTopicsHeight);
        }

        // фиксация хэдера темы, если много сообщений

        for (var i = 0; i < topicsLen ; i++){
            var currentIndex = i;

            /*
             здесь сравниваем: если прокрутка больше чем высота всех предшествующих топиков, то хэдер этого раскрытого топика
             становится в состояние fixed
             */
            if (scrollTop > prevTopicsHeight[i]){
                topicsHeaderArray[currentIndex].addClass('fixed');
                topicsHeader.css('margin-right',asideWidth+10);

                if( currentIndex == topicsLen-5 && FlagOfEndTopics){
                    var topicsContent = client.getTopics(groupID,rubricID,0,lastTopicId,10);
                    if (topicsContent.topics){
                        var tempTopicLen = topicsContent.topics.length;
                        $('.dd>.dd-list').append(TopicHtmlConstructor(topicsContent,tempTopicLen));
                        topics = $('.dd>.dd-list>.topic-item');
                        topicsLen = topics.length;
                        lastTopicId = $('.dd .topic-item:last').data('topicid');
                        var newTopicsSelector = topics.slice(topicsLen-10,topicsLen);
                        SetMainEvents(newTopicsSelector);
                    }else{
                        FlagOfEndTopics = 0;
                    }
                }
                if (scrollTop<270){
                    topicsHeader.css('margin-right',asideWidth+10);
                }
            }else{
                topicsHeaderArray[currentIndex].removeClass('fixed').css('margin-right',0);
            }
        }

    });

/* конец мега раздела */

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

    /* переключение на создание топика */
$('.create-topic-show').click(function(){
    var forum = $('.forum');
    var activeGroupItem = $('.submenu .active .btn');
    var activeRubricItem = $('.nav-list .active a');
    forum.html('');
    var createTopicHtml = $('.create-topic').html();

    forum.html(createTopicHtml).addClass('create-topic').show(200,function(){

        $('.create-topic .wysiwig-box .btn-primary').click(function(){
            var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
            message = message.replace(new RegExp('&nbsp;','g'),' ');
            message = message.replace(new RegExp('<div>','g'),'<div> ');
            var head = $('.head').val();
            var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
            messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
            var groupID = activeGroupItem.data('groupid');
            var rubricID = activeRubricItem.data('rubricid');
            client.createTopic(groupID,head,1,messageWithGoodLinks,0,0,rubricID,1);
            activeGroupItem.trigger('click');
            activeRubricItem.trigger('click');
        });
    });
});
});

