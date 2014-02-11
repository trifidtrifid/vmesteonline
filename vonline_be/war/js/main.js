(function(){
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

        $('.fa-sitemap').click(function(){
            $(this).closest('.topic-item').toggleClass('list-view');
        });
        /* --- */
        var topics = $('.dd>.dd-list>.topic-item'),
            prevTopicsHeight = [],
            topicsLen = topics.length,
            topicsHeader = topics.find('>.topic-descr>.widget-header'),
            topicsHeaderArray = [];

        for (var i = 0; i < topicsLen ;i++){
            topicsHeaderArray[i] = topics.eq(i).find('>.topic-descr>.widget-header');
        }

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

        GetTopicsHeightForFixedHeader(0);

        $('.plus-minus').click(function(e){
            e.preventDefault();
            $(this).closest('.dd2-item').find('>.dd-list').slideToggle(200,function(){
                var currentTopicIndex = $(this).closest('.topic-item').index();
                GetTopicsHeightForFixedHeader(currentTopicIndex+1);
            });

            if ($(this).hasClass('fa-minus')){
                $(this).removeClass('fa-minus').addClass('fa-plus');
            }else{
                $(this).removeClass('fa-plus').addClass('fa-minus');
            }
        });

        var staffCounterForGoodTopicsHeight = 0;

        $(window).scroll(function(){
            // console.log($(this).scrollTop());
            var scrollTop = $(this).scrollTop();

            //убираем сайдбар при прокрутке
            if (w.width()>785){
                if (scrollTop > 270){
                    $('.sidebar').hide();
                    $('.main-content').css('margin-left','0');
                    staffCounterForGoodTopicsHeight++;
                }else {
                    $('.sidebar').show();
                    $('.main-content').css('margin-left','190px');
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
            /*$('.wysiwyg-editor').keypress(function(){
             var th = this;
             //var pos = doGetCaretPosition($('.wysiwyg-editor'));
             //console.log(pos);
             var textWithLink = AutoLinkAndVideo($(this).text());
             //$(this).text(textWithLink);
             moveToEnd(th);
             });
             $('.wysiwyg-editor').bind('paste',function(e){
             //alert(this.innerHTML);
             //alert($(this).text());
             //HandlePaste($(this),e);
             alert($(this).text());
             });*/
            $('.one-message+.wysiwig-box .btn-primary').click(function(){
                var message = $(this).closest('.widget-body').find('.wysiwyg-editor').html();
                message = message.replace(new RegExp('&nbsp;','g'),' ');
                var messageWithGoodLinks = AutoReplaceLinkAndVideo(message);
                messageWithGoodLinks = messageWithGoodLinks.replace(new RegExp('undefined','g'),"");
                alert(messageWithGoodLinks);
            });
        });
        /* --- */
/*
 -----------------------------------------------------------
 АВТОМАТИЧЕСКОЕ ОПРЕДЕЛЕНИЕ ССЫЛКИ В СТРОКЕ
 -----------------------------------------------------------
 */
        function AutoReplaceLinkAndVideo(str) {
            var regexp = /^(.* )?(http[s]?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})(\/[\/\da-z\.\-?]*)*\/?( ?.*)?$/gmi,
            arrayWithLinks = regexp.exec(str),
            res = str;
            if (arrayWithLinks && arrayWithLinks.length > 0){
                var currentLink = arrayWithLinks[2]+arrayWithLinks[3]+'.'+arrayWithLinks[4]+arrayWithLinks[5];
                var prefix = arrayWithLinks[1];
                var suffix = arrayWithLinks[6];
                var iframe = "";

                if (arrayWithLinks[3].indexOf('youtu') != -1){
                    // у ютуба несколько отличается ссылка и айфрэйм
                    iframe = '<iframe width="560" height="315" src="'+ currentLink +'" frameborder="0" allowfullscreen></iframe>';
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

        // пример вызова
        var str1 = 'тест http://fewfwef.ru тест тест http://fewfew.ru wfewefwef http://wefwf.ru';
        //alert(AutoReplaceLinkAndVideo(str1));

/* ------------ */

        /*    function moveToEnd(target) {
         var rng, sel;
         if ( document.createRange ) {
         //alert('1');
         rng = document.createRange();
         //rng.selectNodeContents(target);
         rng.setStart(target[0]*//* Это же textNode? *//*, 2);
         rng.collapse(false); // схлопываем в конечную точку
         sel = window.getSelection();
         sel.removeAllRanges();
         sel.addRange( rng );
         } else { // для IE нужно использовать TextRange
         //alert('2');
         var rng = document.body.createTextRange();
         rng.moveToElementText( target );
         rng.collapseToEnd();
         rng.select();
         }
         }
         var savedContentOld = " ";
         function HandlePaste (elem, e) {
         //alert('0');
         var savedcontent = elem.text();
         //alert(savedcontent);
         //alert(savedContentOld);
         var pasteText = savedcontent.substr(savedContentOld.length) ;
         alert('l'+pasteText);
         if (e && e.clipboardData && e.clipboardData.getData) {// Webkit - get data from clipboard, put into editdiv, cleanup, then cancel event
         if (/text\/html/.test(e.clipboardData.types)) {
         elem.text(e.clipboardData.getData('text/html'));
         }
         else if (/text\/plain/.test(e.clipboardData.types)) {
         elem.text(e.clipboardData.getData('text/plain'));
         }
         else {
         elem.text("");
         }
         WaitForPasteData(elem, savedcontent);
         if (e.preventDefault) {
         e.stopPropagation();
         e.preventDefault();
         }
         savedContentOld = savedcontent;
         return false;
         }
         else {// Everything else - empty editdiv and allow browser to paste content into it, then cleanup
         //elem.text("");
         WaitForPasteData(elem, savedcontent);
         savedContentOld = savedcontent;
         return true;
         }
         }

         function WaitForPasteData (elem, savedcontent) {
         console.log(elem.text());
         //if (elem.childNodes && elem.childNodes.length > 0) {
         if (elem.text().length > 0) {
         console.log('q');
         ProcessPaste(elem, savedcontent);
         console.log(elem.childNodes.length);
         }
         else {
         console.log('w');
         that = {
         e: elem,
         s: savedcontent
         }
         that.callself = function () {
         WaitForPasteData(that.e, that.s)
         }
         setTimeout(that.callself,20);
         }
         }

         function ProcessPaste (elem, savedcontent) {
         alert('1');
         elem.text('aaa');
         pasteddata = elem.text();
         console.log(pasteddata);
         //^^Alternatively loop through dom (elem.childNodes or elem.getElementsByTagName) here
         var updatePastedData =  '<a href="'+ pasteddata +'">'+ pasteddata +'</a>';
         //elem.text(savedcontent + updatePastedData;
         elem.text('fff');
         //alert(elem);
         //elem.insertAfter('aaa');// = savedcontent + updatePastedData;

         // Do whatever with gathered data;
         //alert(pasteddata);
         }*/


        /* Включение дерева  */
        $('.dd').nestable();

        $('.dd-handle a').on('mousedown', function(e){
            e.stopPropagation();
        });
        /* --- */
    });
})()  ;
