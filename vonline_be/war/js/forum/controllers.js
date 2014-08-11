'use strict';

/* Controllers */
angular.module('forum.controllers', ['ui.select2'])
    .controller('baseController',function($rootScope) {
        $rootScope.isTopSearchShow = true;
        var base = this;
        base.neighboursLoadStatus = "";
        base.privateMessagesLoadStatus = "";
        base.profileLoadStatus = "";
        base.settingsLoadStatus = "";
        base.mapsLoadStatus = "";

        base.mainContentTopIsHide = false;
        base.createTopicIsHide = true;
        base.me = shortUserInfo;

        base.isFooterBottom = false;

        base.isTalkTitles = true;

        resetPages(base);
        base.lentaIsActive = true;

        base.addPollInput = function(event,obj){
            event.preventDefault();

            var newInput = {counter : 0, name:"" };
            obj.pollInputs.push(newInput);

        };

        base.showPoll = function(event,obj){
            event.preventDefault();

            obj.isPollShow = true;
            obj.pollInputs = [
                {
                    counter : 0,
                    name:""
                },
                {
                    counter : 1,
                    name:""
                }
            ];
            obj.isPollAvailable = false;
        };

        base.doPoll = function(event,poll){
            event.preventDefault();
            poll.values = [];
            var pollNamesLength = poll.editNames.length;
            var item;

            for(var i = 0; i < pollNamesLength; i++){
                if(poll.editNames[i].value == 1) {
                    item = i;
                    break;
                }
            }

            //console.log(poll.pollId+"--"+item);
            //poll = {};
            var tempPoll = messageClient.doPoll(poll.pollId,item);
            poll.alreadyPoll = true;
            poll.values = tempPoll.values;

            console.log("000"+poll.alreadyPoll);
            setPollEditNames(poll);

        };

        base.oldTextLength = 0;
        base.messageChange = function(event,textareaType){

            var clientHeight = event.target.clientHeight,
                scrollHeight = event.target.scrollHeight,
                textLength = event.target.textLength,
                clientWidth = event.target.clientWidth,
                textLengthPX, newHeight,removeRowCount,
                defaultHeight, newRowCount;

            /*if(textareaType == 1){
                defaultHeight = 90;
            }else if(textareaType == 2){
                defaultHeight = 44;
            }*/
            defaultHeight = 44;

            /*
            Исходные данные:
                На один символ приходится ~8px в ширину
                Высота строки текста ~14px

            * Здесь выполняем такие действия :
             * 1) Считаем длину текста в пикселях
             * 2) Определяем целое количестов строк, которые удалили
             * 3) Определям новую высоту с учетом высоты удаленного текста
            * */

            console.log("0 "+scrollHeight+" "+clientHeight);
             if(scrollHeight > clientHeight){
                 console.log('1');
                event.target.style.height = scrollHeight+'px';
            }else if(scrollHeight > defaultHeight){
                textLengthPX = (parseInt(base.oldTextLength) - textLength) * 8; // 1
                 console.log("2 "+textLengthPX+" "+clientWidth+" "+textLength);
                if (textLengthPX > clientWidth){
                    console.log("3 "+textLengthPX+" "+clientWidth);
                    removeRowCount = Math.floor(textLengthPX/clientWidth); // 2
                    newHeight = parseInt(event.target.style.height) - removeRowCount*14; // 3
                    newHeight > defaultHeight ? event.target.style.height = newHeight+"px":
                                    event.target.style.height = defaultHeight+'px';

                }else{
                    //newRowCount = parseInt(textLength*8/clientWidth);

                    event.target.style.height = scrollHeight-6+'px';

                    /*if(newRowCount*14 < defaultHeight){
                        console.log("3.5 "+event.target.style.height+" "+scrollHeight/textLength);
                        //event.target.style.height = parseInt(event.target.style.height) - scrollHeight/textLength+"px";
                        event.target.style.height = scrollHeight;

                    }else{
                        event.target.style.height = newRowCount*14+'px';
                    }*/
                    console.log("5 "+textLength+" "+textLength*8/clientWidth);
                }
            }else{
                 console.log('4');
                event.target.style.height = defaultHeight+'px';
            }
            base.oldTextLength = textLength;
        };

        base.pageTitle = "Новости";

        base.user = userClient.getShortUserInfo();

        base.bufferSelectedGroup = userClientGroups[0];

        base.markImportant = function(event,message){
            event.preventDefault();
            var isImportant;

            if (message.important == 3 || message.important == 2){
                message.important = 1;
                isImportant = true;
                message.importantText = 'Снять метку "Важное"';
            }else{
                message.important = 3;
                isImportant = false;
                message.importantText = 'Пометить как "Важное"';
            }

            messageClient.markMessageImportant(message.id,isImportant);
        };
        base.markLike = function(event,message){
            event.preventDefault();
            var isLike;

            if(message.like == 1){

                $('#like-help-'+message.id).fadeIn(200);

                setTimeout(hideLikeHelp,2000,message.id);

            }

            message.like = 1;
            messageClient.markMessageLike(message.id);
        };

        var hideLikeHelp = function(messageId){
            $('#like-help-'+messageId).fadeOut(200);
        };

        base.showAllGroups = function(){
            var groupsLength = $rootScope.groups.length;
            for(var i = 0; i < groupsLength; i++){
                $rootScope.groups[i].isShow = true;
                $rootScope.groups[i].selected = false;
            }
            $rootScope.groups[0].selected = true;
            $rootScope.base.bufferSelectedGroup = $rootScope.groups[0];
        };

        $rootScope.base = base;
        $rootScope.currentPage = 'lenta';

        $rootScope.leftbar = {};
    })
  .controller('navbarController', function($rootScope) {
        this.privateMessagesBtnStatus = "";
        $rootScope.navbar = this;

        this.logout = function(event){
            event.preventDefault();

            localStorage.removeItem('groupId');
            authClient.logout();

            document.location.replace("login.html");

        }

  })
  .controller('leftBarController',function($rootScope) {

    $rootScope.setTab = function(newValue){

        $rootScope.leftbar.tab = newValue;
        $rootScope.isTopSearchShow = true;
        resetPages($rootScope.base);
        resetAceNavBtns($rootScope.navbar);

        switch(newValue){
            case 1:
                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.lentaIsActive = true;
                $rootScope.currentPage = 'lenta';
                $rootScope.base.pageTitle = "Новости";
                break;
            case 2:
                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.isTalkTitles = true;
                $rootScope.base.talksIsActive = true;
                $rootScope.currentPage = 'talks';
                $rootScope.base.pageTitle = "Обсуждения";
                break;
            case 3:
                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.isAdvertsTitles = true;
                $rootScope.base.advertsIsActive = true;
                $rootScope.currentPage = 'adverts';
                $rootScope.base.pageTitle = "Объявления";
                break;
            default :
                break;
        }

    };

    $rootScope.isSet = function(number){
        //return this.tab === number;
        //alert($rootScope.leftbar.tab);
        return $rootScope.leftbar.tab === number;
    };
  })
    .controller('rightBarController',function($rootScope) {
        //var rightbar = this;

        $rootScope.importantTopics = messageClient.getImportantTopics(userClientGroups[0].id);
        //alert(rightbar.importantTopics.totalSize);
    })
    .controller('mainContentTopController',function($rootScope) {
        var topCtrl = this;

        topCtrl.groups = userClientGroups;// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        var groups = $rootScope.groups = topCtrl.groups,
            groupsLength = groups.length;

        for(var i = 0; i < groupsLength; i++){
            groups[i].isShow = true;
        }

        var lsGroupId = localStorage.getItem('groupId');

        if(!lsGroupId){
            groups[0].selected = true;
            $rootScope.currentGroup = groups[0];
        }else{
            for(var i = 0; i < groupsLength; i++){
                if(groups[i].id == lsGroupId){
                    groups[i].selected = true;
                    $rootScope.currentGroup = groups[i];
                }
            }
        }


        topCtrl.isSet = function(groupId){
            //return groupId ===
        };

        $rootScope.selectGroup = function(group){
            var groupId;

            for(var i = 0; i < groupsLength; i++){
                groups[i].selected = false;
            }

                group.selected = true;
                groupId = group.id;

            $rootScope.currentGroup = group;
            $rootScope.base.bufferSelectedGroup = selectGroupInDropdown(group.id);

            $rootScope.importantTopics = messageClient.getImportantTopics(group.id);

            if($rootScope.currentPage == 'lenta'){
                $rootScope.wallChangeGroup(group.id);
                $rootScope.selectGroupInDropdown_lenta(group.id);
            }else if($rootScope.currentPage == 'talks'){
                $rootScope.talksChangeGroup(group.id);
                $rootScope.selectGroupInDropdown_talks(group.id);
            }else if($rootScope.currentPage == 'adverts'){
                $rootScope.advertsChangeGroup(group.id);
                $rootScope.selectGroupInDropdown_adverts(group.id);
            }else if($rootScope.currentPage == 'neighbours'){
                $rootScope.neighboursChangeGroup(group.id);
            }else if($rootScope.currentPage == 'maps'){
                $rootScope.mapsChangeGroup(group.id);
            }

            localStorage.setItem('groupId',group.id);
            //$rootScope.currentGroup = $rootScope.base.selectGroupInDropdown(group.id);

        };

        topCtrl.showCreateTopic = function(event){
            event.preventDefault();

            $rootScope.base.createTopicIsHide ? $rootScope.base.createTopicIsHide = false : $rootScope.base.createTopicIsHide = true;

        };

        $('.ng-cloak').removeClass('ng-cloak');
    })
    .controller('LentaController',function($rootScope) {

        $rootScope.setTab(1);
        $rootScope.base.showAllGroups();
        $rootScope.base.isFooterBottom = false;

        initAttachImage($('#attachImage-0'),$('#attach-area-0')); // для ленты новостей
        initAttachDoc($('#attachDoc-0'),$('#attach-doc-area-0'));
        initFancyBox($('.forum'));

        var lenta = this;
        lenta.groups = userClientGroups;// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        lenta.selectedGroup = $rootScope.base.bufferSelectedGroup =
            lenta.selectedGroupInTop = $rootScope.currentGroup;
        lenta.isPollShow = false;
        lenta.pollSubject = "";
        lenta.pollInputs = [
            {
            counter : 0,
            name:""
            },
            {
            counter : 1,
            name:""
            }
        ];
        lenta.isPollAvailable = true;

        lenta.attachedImages = [];

        lenta.wallMessageContent = TEXT_DEFAULT_1;
        lenta.isCreateMessageError = false;

        lenta.wallItems = messageClient.getWallItems($rootScope.base.bufferSelectedGroup.id);

        var wallItemsLength;
        lenta.wallItems ? wallItemsLength = lenta.wallItems.length :
            wallItemsLength = 0;

        initWallItem();

        //lenta.selectGroupInDropdown = selectGroupInDropdown;
        /*lenta.selectGroupInDropdown = function(groupId){
            lenta.selectedGroup = $rootScope.base.bufferSelectedGroup = selectGroupInDropdown(groupId);

        };*/
        $rootScope.selectGroupInDropdown_lenta = function(groupId){
            lenta.selectedGroup = $rootScope.base.bufferSelectedGroup = selectGroupInDropdown(groupId);
        };

        lenta.goToAnswerInput = function(event){
            event.preventDefault();
        };

        lenta.createWallMessage = function(event){
            event.preventDefault();

            lenta.attachedImages = getAttachedImages($('#attach-area-0'));
            lenta.attachedDocs = getAttachedDocs($('#attach-doc-area-0'));

            if(lenta.attachedImages.length == 0 && lenta.attachedDocs && lenta.attachedDocs.length == 0 && !lenta.isPollShow
                && lenta.wallMessageContent == TEXT_DEFAULT_1){

                lenta.isCreateMessageError = true;
                lenta.createMessageErrorText = "Вы не ввели сообщение";

            }else if(lenta.isPollShow && (!lenta.pollSubject || lenta.pollInputs[0].name == "" || lenta.pollInputs[1].name == "")){

                lenta.isCreateMessageError = true;
                lenta.createMessageErrorText = "Вы не указали данные для опроса";

            }else{

                if(lenta.wallMessageContent == TEXT_DEFAULT_1 && (lenta.attachedImages || lenta.attachedDocs || lenta.isPollShow)){
                    lenta.wallMessageContent = "";
                }
                lenta.isCreateMessageError = false;

                var isWall = 1,
                    newTopic = postTopic(lenta, isWall);

                var newWallItem = new com.vmesteonline.be.messageservice.WallItem();
                newWallItem.topic = newTopic;
                newWallItem.topic.authorName = getAuthorName();
                newWallItem.messages = [];
                newWallItem.commentText = "Ваш ответ";
                newWallItem.answerShow = false;
                newWallItem.isFocus = false;
                newWallItem.label = getLabel(lenta.groups, $rootScope.base.bufferSelectedGroup.type);
                newWallItem.tagColor = getTagColor(newWallItem.label);

                cleanAttached($('#attach-area-0'));
                cleanAttached($('#attach-doc-area-0'));

                $rootScope.selectGroup($rootScope.base.bufferSelectedGroup);
                /*if (lenta.selectedGroupInTop.id == $rootScope.base.bufferSelectedGroup.id) {
                    lenta.wallItems ?
                        lenta.wallItems.unshift(newWallItem) :
                        lenta.wallItems[0] = newWallItem;

                }*/
            }
        };

        lenta.createWallComment = function(event,wallItem){
            event.preventDefault();

            wallItem.groupId = lenta.selectedGroupInTop.id;

                var isWall = true,
                    message = postMessage(wallItem, isWall);

                /*var message =  new com.vmesteonline.be.messageservice.Message();
                 message.id = 0;
                 message.topicId = wallItem.topic.id;
                 message.parentId = 0;
                 message.groupId = lenta.selectedGroupInTop.id;
                 message.type = com.vmesteonline.be.messageservice.MessageType.WALL;//5;
                 message.content = wallItem.commentText;
                 message.images = getAttachedImages($('#attach-area-'+wallItem.topic.id));
                 message.created = Date.parse(new Date)/1000;

                 var newMessage = messageClient.postMessage(message);
                 wallItem.commentText = "Ваш ответ";
                 message.createdEdit = getTiming(newMessage.created);
                 console.log(newMessage.created);
                 message.authorName = getAuthorName();
                 message.userInfo = newMessage.userInfo;
                 message.images = newMessage.images;
                 message.id = newMessage.id;*/

            if(message == 0){
                wallItem.isCreateCommentError = true;
                wallItem.createCommentErrorText = "Вы не ввели сообщение";
            }else {
                wallItem.isCreateCommentError = false;

                if (wallItem.messages) {
                    wallItem.messages.push(message);
                } else {
                    wallItem.messages = [];
                    wallItem.messages[0] = message;
                }

                wallItem.answerShow = false;
            }

        };

        var initFlagsArray = [];
        lenta.showAnswerInput = function(event,wallItem,wallMessage){
            event.preventDefault();

            /*wallItem.answerShow ?
                wallItem.answerShow = false :*/
                wallItem.answerShow = true ;
                wallItem.isFocus = true ;

            if(wallMessage){
                //var authorName = userClient.getUserInfoExt(wallMessage.authorId).firstName;
                var authorName;
                wallMessage.userInfo ?
                    authorName = wallMessage.userInfo.firstName :
                    authorName = wallMessage.authorName.split(' ')[0];
                wallItem.commentText = authorName+", ";
            }else{
                wallItem.commentText = "";
            }

            if(!initFlagsArray[wallItem.topic.id]) {
                // инифицализацмю AttachImage нужно делать только один раз для каждого сообщения
                initAttachImage($('#attachImage-' + wallItem.topic.id), $('#attach-area-' + wallItem.topic.id));
                initAttachDoc($('#attachDoc-' + wallItem.topic.id), $('#attach-doc-area-' + wallItem.topic.id));
                initFlagsArray[wallItem.topic.id] = true;
            }

        };

        $rootScope.wallChangeGroup = function(groupId){

            lenta.wallItems = messageClient.getWallItems(groupId);

            if(lenta.wallItems.length) {
                initWallItem();
            }

        };

        function initWallItem(){
            wallItemsLength = lenta.wallItems.length;
            for(var i = 0; i < wallItemsLength; i++){

                lenta.wallItems[i].commentText = "Ваш ответ";
                lenta.wallItems[i].answerShow = false;
                lenta.wallItems[i].isFocus = false;
                lenta.wallItems[i].isCreateCommentError = false;

                //  lenta.wallItems[i].topic.message.groupId сейчас не задана почему-то
                lenta.wallItems[i].label = getLabel(lenta.groups,lenta.wallItems[i].topic.groupType);

                lenta.wallItems[i].tagColor = getTagColor(lenta.wallItems[i].label);

                if(lenta.wallItems[i].topic.message.important == 1){
                    lenta.wallItems[i].topic.message.importantText = 'Снять метку "Важное"';
                }else{
                    lenta.wallItems[i].topic.message.importantText = 'Пометить как "Важное"';
                }


                if(lenta.wallItems[i].topic.message.type == 1){

                    lenta.wallItems[i].topic.lastUpdateEdit = getTiming(lenta.wallItems[i].topic.lastUpdate);

                }else if(lenta.wallItems[i].topic.message.type == 5){

                    lenta.wallItems[i].topic.message.createdEdit = getTiming(lenta.wallItems[i].topic.message.created);
                    lenta.wallItems[i].topic.authorName = getAuthorName(lenta.wallItems[i].topic.userInfo);
                    lenta.wallItems[i].topic.metaType = "message";

                    var mesLen;
                    lenta.wallItems[i].messages ?
                        mesLen = lenta.wallItems[i].messages.length:
                        mesLen = 0;

                    for(var j = 0; j < mesLen; j++){
                        lenta.wallItems[i].messages[j].createdEdit = getTiming(lenta.wallItems[i].messages[j].created);
                        lenta.wallItems[i].messages[j].authorName = getAuthorName(lenta.wallItems[i].messages[j].userInfo);
                    }


                    if(lenta.wallItems[i].topic.poll != null){
                        //значит это опрос
                        setPollEditNames(lenta.wallItems[i].topic.poll);

                        lenta.wallItems[i].topic.metaType = "poll";
                    }
                }
            }
        }

        $('.ng-cloak').removeClass('ng-cloak');

    })
    .controller('WallSingleController',function($rootScope, $stateParams){
        var wallSingle = this;

        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.base.isFooterBottom = false;

        // временно, нужна функция getWallItem(topicId)
        var wallItems = messageClient.getWallItems($rootScope.currentGroup.id),
        wallItemsLength = wallItems.length;
        for(var i = 0; i < wallItemsLength; i++){
            if(wallItems[i].topic.id == $stateParams.topicId){
                wallSingle.wallItem = wallItems[i];
            }
        }

        wallSingle.wallItem.commentText = "Ваш ответ";
        wallSingle.wallItem.answerShow = false;
        wallSingle.wallItem.isFocus = false;
        wallSingle.wallItem.isCreateCommentError = false;

        if(wallSingle.wallItem.topic.message.important == 1){
            wallSingle.wallItem.topic.message.importantText = 'Снять метку "Важное"';
        }else{
            wallSingle.wallItem.topic.message.importantText = 'Пометить как "Важное"';
        }

        //  lenta.wallItems[i].topic.message.groupId сейчас не задана почему-то
        wallSingle.wallItem.label = getLabel(userClientGroups,wallSingle.wallItem.topic.groupType);

        wallSingle.wallItem.tagColor = getTagColor(wallSingle.wallItem.label);

        if(wallSingle.wallItem.topic.message.type == 1){

            wallSingle.wallItem.topic.lastUpdateEdit = getTiming(wallSingle.wallItem.topic.lastUpdate);

        }else if(wallSingle.wallItem.topic.message.type == 5){

            wallSingle.wallItem.topic.message.createdEdit = getTiming(wallSingle.wallItem.topic.message.created);
            wallSingle.wallItem.topic.authorName = getAuthorName(wallSingle.wallItem.topic.userInfo);
            wallSingle.wallItem.topic.metaType = "message";

            var mesLen;
            wallSingle.wallItem.messages ?
                mesLen = wallSingle.wallItem.messages.length:
                mesLen = 0;

            for(var j = 0; j < mesLen; j++){
                wallSingle.wallItem.messages[j].createdEdit = getTiming(wallSingle.wallItem.messages[j].created);
                wallSingle.wallItem.messages[j].authorName = getAuthorName(wallSingle.wallItem.messages[j].userInfo);
            }


            if(wallSingle.wallItem.topic.poll != null){
                //значит это опрос
                setPollEditNames(wallSingle.wallItem.topic.poll);

                wallSingle.wallItem.topic.metaType = "poll";
            }
        }

        var initFlagsArray = [];
        wallSingle.showAnswerInput = function(event,wallItem,wallMessage){
            event.preventDefault();

            /*wallItem.answerShow ?
             wallItem.answerShow = false :*/
            wallItem.answerShow = true ;
            wallItem.isFocus = true ;

            if(wallMessage){
                var authorName;
                wallMessage.userInfo ?
                    authorName = wallMessage.userInfo.firstName :
                    authorName = wallMessage.authorName.split(' ')[0];
                wallItem.commentText = authorName+", ";
            }else{
                wallItem.commentText = "";
            }

            if(!initFlagsArray[wallItem.topic.id]) {
                // инифицализацмю AttachImage нужно делать только один раз для каждого сообщения
                initAttachImage($('#attachImage-' + wallItem.topic.id), $('#attach-area-' + wallItem.topic.id));
                initAttachDoc($('#attachDoc-' + wallItem.topic.id), $('#attach-doc-area-' + wallItem.topic.id));
                initFlagsArray[wallItem.topic.id] = true;
            }

        };

        wallSingle.createWallComment = function(event,wallItem){
            event.preventDefault();

            wallItem.groupId = $rootScope.currentGroup.id;

            var isWall = true,
                message = postMessage(wallItem, isWall);

            if(message == 0){
                wallItem.isCreateCommentError = true;
                wallItem.createCommentErrorText = "Вы не ввели сообщение";
            }else {
                wallItem.isCreateCommentError = false;

                if (wallItem.messages) {
                    wallItem.messages.push(message);
                } else {
                    wallItem.messages = [];
                    wallItem.messages[0] = message;
                }

                wallItem.answerShow = false;
            }

        };

        $('.ng-cloak').removeClass('ng-cloak');
    })
    .controller('TalksController',function($rootScope) {
        /*
        * при работе с обсждениями нужно учесть следующее:
        * есть три типа сообщения :
        * 1) топик. На странице обсуждения может быть только один. Его дети
        * это сообщения первого уровня. Его дети всегда открыты, поэтому у него
        * нет контрола плюс-минус. Страница топика загружается в методе showFullTalk
        * через topicId который передается в функцию при вызове
        * Хранится в объекте talk.fullTalkTopic.
        *
        * 2) Сообщение первого уровня. Берутся через getFirstLevelMessages. Изначально
        * все потомки скрыты и не подгружены. При первом нажатии на контрол плюс-минус
        * подгружаются, потом просто переключается show-hide. ParentId у таких сообщений
        * равен 0. Внимание! : ParentId передается в getFirstLevelMessages через lastLoadedId.        *
        * У каждого сообщения первого уровня есть свой массив сообщений 3го типа.
        * Хранятся в массиве talk.fullTalkFirstMessages.
        *
        * 3) Просто сообщение. Береутся через getMessages(). Через параметр lastLoadedId передается
        * id последнего загруженного простого сообщения, для подгрузки. У каждого простого сообщения
        * есть offset, который задается на БЕ. offset'ы определяют вложенность сообщений и за счет них
        * создается древовидная структура форума.
        * Хранятся в двумерном массиве talk.fullTalkMessages[firstMessage.id][]
        *
        *
        * Есть следующие типы контролов, реализованные для разных типов сообщений:
        * 1) showAnswerInput : реализует клик на "Ответить", показвает поле для отправки
        * сообщения.
        * 2) addMessage: клик на "Отправить", создает и отображает новое сообщение
        * 3) toggleTree: контрол "плюс-минус", скрвает-показвает внутренние сообщения этого
        * сообщения.
        * */
            $rootScope.setTab(2);

            initAttachImage($('#attachImage-00'), $('#attach-area-00')); // для обсуждений
            initAttachDoc($('#attachDoc-00'), $('#attach-doc-area-00')); // для обсуждений
            initFancyBox($('.talks'));
            $rootScope.base.showAllGroups();
            $rootScope.base.isFooterBottom = false;

            $rootScope.base.createTopicIsHide = true;
            var talk = this;
            talk.isTalksLoaded = false;
            talk.groups = userClientGroups;

            talk.content = TEXT_DEFAULT_3;
            talk.subject = TEXT_DEFAULT_4;

            talk.isPollShow = false;
            talk.pollSubject = "";
            talk.pollInputs = [
                {
                    counter: 0,
                    name: ""
                },
                {
                    counter: 1,
                    name: ""
                }
            ];
            talk.isPollAvailable = true;

            talk.fullTalkTopic = {};
            talk.fullTalkTopic.answerInputIsShow = false;
            talk.fullTalkMessages = [];
            talk.fullTalkFirstMessages = [];
            talk.answerFirstMessage = "Ваш ответ";
            var fullTalkFirstMessagesLength,
                talkId;

            //$rootScope.currentGroup = talk.selectedGroup = talk.groups[0];
            $rootScope.base.bufferSelectedGroup = talk.selectedGroup = $rootScope.currentGroup;

            talk.topics = messageClient.getTopics(talk.selectedGroup.id, 0, 0, 0, 1000).topics;

            initTalks();

            if (!talk.topics) talk.topics = [];

            $rootScope.selectGroupInDropdown_talks = function(groupId){
                talk.selectedGroup = $rootScope.base.bufferSelectedGroup = selectGroupInDropdown(groupId);
            };

        talk.addSingleTalk = function(){
            talk.attachedImages = getAttachedImages($('#attach-area-00'));
            talk.attachedDocs = getAttachedDocs($('#attach-doc-area-00'));
            if(talk.subject == TEXT_DEFAULT_4 || talk.subject == ""){

                talk.isCreateTalkError = true;
                talk.createTalkErrorText = "Вы не указали заголовок";

            }else if(talk.attachedImages.length == 0 && (talk.attachedDocs === undefined || talk.attachedDocs.length == 0) && !talk.isPollShow
                && talk.content == TEXT_DEFAULT_3){

                talk.isCreateTalkError = true;
                talk.createTalkErrorText = "Вы не ввели сообщение";

            }else if(talk.isPollShow && (!talk.pollSubject || talk.pollInputs[0].name == "" || talk.pollInputs[1].name == "")){

                talk.isCreateTalkError = true;
                talk.createTalkErrorText = "Вы не указали данные для опроса";

            }else {

                if (talk.content == TEXT_DEFAULT_3 && (talk.attachedImages || talk.attachedDocs || talk.isPollShow)) {
                    talk.content = "";
                }
                talk.isCreateTalkError = false;
                var isWall = 0,
                    newTopic = postTopic(talk, isWall);
                newTopic.label = getLabel(talk.groups,newTopic.groupType);
                newTopic.tagColor = getTagColor(newTopic.label);

                /*var newTopic = new com.vmesteonline.be.messageservice.Topic;
                 newTopic.message = new com.vmesteonline.be.messageservice.Message;
                 newTopic.message.groupId = talk.selectedGroup.id;
                 newTopic.message.type = 1;
                 newTopic.message.content = talk.content;
                 newTopic.message.id = 0;
                 newTopic.message.created = Date.parse(new Date());

                 newTopic.subject = talk.subject;
                 newTopic.id = 0;

                 var poll;
                 if(talk.isPollShow){
                 poll = new com.vmesteonline.be.messageservice.Poll;
                 poll.pollId = 0;
                 poll.names = [];
                 var pollInputsLength = talk.pollInputs.length;
                 for(var i = 0; i < pollInputsLength; i++){
                 poll.names[i] = talk.pollInputs[i].name;
                 }
                 newTopic.poll = poll;
                 newTopic.metaType = "poll";
                 }

                 newTopic = messageClient.postTopic(newTopic);

                 if(talk.isPollShow){
                 poll.pollId = newTopic.poll.pollId;
                 talk.isPollShow = false;
                 talk.pollSubject= "";
                 talk.isPollAvailable = true;
                 }*/

                $rootScope.base.createTopicIsHide = true;

                //talk.topics.unshift(newTopic);
                $rootScope.selectGroup($rootScope.base.bufferSelectedGroup);
            }

            cleanAttached($('#attach-area-00'));
            cleanAttached($('#attach-doc-area-00'));
            talk.subject = TEXT_DEFAULT_4;

        };

        function initTalks(){
            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;

            for(var i = 0; i < topicLength;i++){
                talk.topics[i].lastUpdateEdit = getTiming(talk.topics[i].lastUpdate);
                talk.topics[i].label = getLabel(talk.groups,talk.topics[i].groupType);
                talk.topics[i].tagColor = getTagColor(talk.topics[i].label);

                if(talk.topics[i].message.important == 1){
                    talk.topics[i].message.importantText = 'Снять метку "Важное"';
                }else{
                    talk.topics[i].message.importantText = 'Пометить как "Важное"';
                }
            }
        }

        $rootScope.talksChangeGroup = function(groupId){

            talk.topics = messageClient.getTopics(groupId,0,0,0,1000).topics;

            if(talk.topics) {
                initTalks();
            }

        };


    })
    .controller('TalksSingleController',function($rootScope,$stateParams){

        $rootScope.base.isFooterBottom = false;

        var talk = this,
            fullTalkMessagesLength,
            talkId = $stateParams.talkId;

        talk.selectedGroup = $rootScope.currentGroup;
        talk.topics = messageClient.getTopics(talk.selectedGroup.id, 0, 0, 0, 1000).topics;
        talk.fullTalkTopic = {};
        talk.fullTalkMessages = {};
        talk.fullTalkFirstMessages = [];
        talk.groups = userClientGroups;

        var showFullTalk = function(talk,talkOutsideId){

            initFancyBox($('.talks-single'));
            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;
            //talk.fullTalkTopic = talkOutside;

            var talkId = talkOutsideId,
                fullTalkFirstMessagesLength;
            for(var i = 0; i < topicLength; i++){
                if(talkId == talk.topics[i].id){
                    talk.fullTalkTopic = talk.topics[i];
                    talk.fullTalkTopic.message.createdEdit = getTiming(talk.fullTalkTopic.message.created);
                    talk.fullTalkTopic.label = getLabel(talk.groups,talk.fullTalkTopic.groupType);
                    talk.fullTalkTopic.tagColor = getTagColor(talk.fullTalkTopic.label);

                    if(talk.fullTalkTopic.message.important == 1){
                        talk.fullTalkTopic.message.importantText = 'Снять метку "Важное"';
                    }else{
                        talk.fullTalkTopic.message.importantText = 'Пометить как "Важное"';
                    }
                }
            }
            if(talk.fullTalkTopic.poll != null){
                setPollEditNames(talk.fullTalkTopic.poll);
                talk.fullTalkTopic.metaType = "poll";
            }else{
                talk.fullTalkTopic.metaType = "message";
            }

            talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,1000).messages;

            talk.fullTalkFirstMessages ?
                fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length:
                fullTalkFirstMessagesLength = 0;
            if(talk.fullTalkFirstMessages === null) talk.fullTalkFirstMessages = [];

            for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                talk.fullTalkFirstMessages[i].answerInputIsShow = false;
                talk.fullTalkFirstMessages[i].isTreeOpen = false;
                talk.fullTalkFirstMessages[i].isLoaded = false;
                talk.fullTalkFirstMessages[i].answerMessage = "Ваш ответ";
                talk.fullTalkFirstMessages[i].createdEdit = getTiming(talk.fullTalkFirstMessages[i].created);
            }

            $rootScope.base.isTalkTitles = false;
            //alert($rootScope.base.isTalkTitles);
            $rootScope.base.mainContentTopIsHide = true;
            $rootScope.base.createTopicIsHide = true;

            var talksBlock = $('.talks').find('.talks-block');

            if(!talk.isTalksLoaded){
                /*talksBlock.load('ajax/forum/talks-single.jsp .talks-single',function(){

                 talk.isTreeOpen = false;

                 talk.toggleInsideTreeOfMessages = function(){
                 if(!talk.isTreeOpen){

                 //$(this).removeClass('fa-plus').addClass('fa-minus');
                 talk.isTreeOpen = true;
                 //$(this).closest('.dd-item').find('>.dd-list').slideDown();

                 }else{

                 //$(this).removeClass('fa-minus').addClass('fa-plus');
                 talk.isTreeOpen = false;
                 //$(this).closest('.dd-item').find('>.dd-list').slideUp();
                 }
                 };
                 //SetShowEditorClick($('.answer-link'));

                 });*/
            }

            $rootScope.base.talk = talk;
            //$rootScope.base.talkId = talkId;


        };

        showFullTalk(talk,talkId);

        var initFlagsTopic = [];
        talk.showTopicAnswerInput = function(event,fullTalkTopic){
            event.preventDefault();

            if(!initFlagsTopic[fullTalkTopic.id]) {
                initAttachImage($('#attachImage-' + fullTalkTopic.id), $('#attach-area-' + fullTalkTopic.id));
                initAttachDoc($('#attachDoc-' + fullTalkTopic.id), $('#attach-doc-area-' + fullTalkTopic.id));
                initFlagsTopic[fullTalkTopic.id] = true;
            }

            talk.fullTalkTopic.answerInputIsShow ?
                talk.fullTalkTopic.answerInputIsShow = false :
                talk.fullTalkTopic.answerInputIsShow = true ;
        };

        var initFlagsMessage = [];
        talk.showMessageAnswerInput = function(event,fullTalkTopic,firstMessage,message){
            event.preventDefault();
            var attachId;

            if(!message){
                // если это сообщение первого уровня
                attachId = fullTalkTopic.id+'-'+firstMessage.id;

                if(!talk.fullTalkFirstMessages) talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,1000).messages;
                var fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length;

                firstMessage.answerInputIsShow ?
                    firstMessage.answerInputIsShow = false :
                    firstMessage.answerInputIsShow = true;


            }else{
                // если простое сообщение
                attachId = fullTalkTopic.id+'-'+message.id;

                if(!talk.fullTalkMessages[firstMessage.id]) talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
                var  fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;
                message.answerInputIsShow ?
                    message.answerInputIsShow = false :
                    message.answerInputIsShow = true;


            }

            if(!initFlagsMessage[attachId]) {
                initAttachImage($('#attachImage-' + attachId), $('#attach-area-' + attachId));
                initAttachDoc($('#attachDoc-' + attachId), $('#attach-doc-area-' + attachId));

                initFlagsMessage[attachId] = true;
            }
        };

        talk.addSingleFirstMessage = function(event,topicId){
            event.preventDefault();

            /* var message =  new com.vmesteonline.be.messageservice.Message();

             var newMessage = messageClient.createMessage(topicId,0,talk.selectedGroup.id,1,talk.answerFirstMessage);
             newMessage.createdEdit = getTiming(newMessage.created);*/

            talk.topicId = topicId;

            var isWall = false,
                isFirstLevel = true,
                newMessage = postMessage(talk,isWall,isFirstLevel);

            if(newMessage == 0){
                talk.isCreateFirstMessageError = true;
                talk.createFirstMessageErrorText = "Вы не ввели сообщение";
            }else {
                talk.fullTalkTopic.answerInputIsShow = false;

                talk.isCreateFirstMessageError = false;
                talk.fullTalkFirstMessages ?
                    talk.fullTalkFirstMessages.push(newMessage) :
                    talk.fullTalkFirstMessages[0] = newMessage;
            }

        };

        talk.addSingleMessage = function(event,topicId,firstMessage,message){
            event.preventDefault();

            if (!talk.fullTalkMessages[firstMessage.id]) talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            talk.fullTalkMessages[firstMessage.id] ?
                fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length:
                fullTalkMessagesLength = 0;

            var newMessage,answer,parentId;

            if(!message){
                // если добавляем к сообщению первого уровня
                talk.messageId = firstMessage.id;

                answer = firstMessage.answerMessage;
                firstMessage.isTreeOpen = true;
                firstMessage.answerMessage = "Ваш ответ";
                parentId = firstMessage.id;

            }else{
                // если добавляем к простому сообщению
                talk.messageId = message.id;

                for(var i = 0; i < fullTalkMessagesLength; i++){
                    if(talk.fullTalkMessages[firstMessage.id][i].id == message.id){
                        //talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                        talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].createdEdit = getTiming(talk.fullTalkMessages[firstMessage.id][i].created);
                        answer = talk.fullTalkMessages[firstMessage.id][i].answerMessage;
                    }
                }
                parentId = message.id;

            }
//            newMessage = messageClient.createMessage(topicId,parentId,talk.selectedGroup.id,1,answer,0,0,0);
            var isWall = false,
                isFirstLevel = false;
            talk.topicId = topicId;
            talk.parentId = parentId;
            talk.answerMessage = answer;

            newMessage = postMessage(talk,isWall,isFirstLevel);

            if(newMessage == 0){
                if(!message){
                    talk.isCreateMessageToFirstError = true;
                    talk.createMessageToFirstErrorText = "Вы не ввели сообщение";
                }else{
                    talk.isCreateMessageError = true;
                    talk.createMessageErrorText = "Вы не ввели сообщение";
                }
            }else {
                if(!message){
                    talk.isCreateMessageToFirstError = false;
                    firstMessage.answerInputIsShow = false;

                }else{
                    talk.isCreateMessageError = false;
                    for(var i = 0; i < fullTalkMessagesLength; i++){
                        if(talk.fullTalkMessages[firstMessage.id][i].id == message.id){
                            talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                        }
                    }
                }

                talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId, talk.selectedGroup.id, 1, firstMessage.id, 0, 1000).messages;

                talk.fullTalkMessages[firstMessage.id] ?
                    fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length :
                    fullTalkMessagesLength = 0;

                for (var i = 0; i < fullTalkMessagesLength; i++) {
                    talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                    talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                    talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                    talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                    talk.fullTalkMessages[firstMessage.id][i].createdEdit = getTiming(talk.fullTalkMessages[firstMessage.id][i].created);
                    talk.fullTalkMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
                }
            }

        };

        talk.toggleTreeFirstMessage = function($event,firstMessage){
            event.preventDefault();

            firstMessage.isTreeOpen ?
                firstMessage.isTreeOpen = false :
                firstMessage.isTreeOpen = true ;


            // --------

            talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            talk.fullTalkMessages[firstMessage.id] ?
                fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length:
                fullTalkMessagesLength = 0;
            if(talk.fullTalkMessages[firstMessage.id] === null) talk.fullTalkMessages[firstMessage.id] = [];

            for(var i = 0; i < fullTalkMessagesLength; i++){
                talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].createdEdit = getTiming(talk.fullTalkMessages[firstMessage.id][i].created);
                talk.fullTalkMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
            }

        };

        talk.toggleTree = function($event,message,firstMessage){
            event.preventDefault();

            if(!talk.fullTalkMessages[firstMessage.id]) talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            var fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;

            message.isTreeOpen ?
                message.isTreeOpen = false :
                message.isTreeOpen = true ;

            var afterCurrentIndex = false,
                nextMessageOnCurrentLevel = false,
                loopMessageOffset,
                parentOpenStatus,
                areAllMyParentsTreeOpen = [],
                checkAreAllMyParentsTreeOpen = true,
                beginOffset = message.offset,
                parentOpenStatusArray = [];

            for(var i = 0; i < fullTalkMessagesLength; i++){
                loopMessageOffset = talk.fullTalkMessages[firstMessage.id][i].offset;

                if(afterCurrentIndex && !nextMessageOnCurrentLevel
                    && message.offset < loopMessageOffset){

                    areAllMyParentsTreeOpen[loopMessageOffset] = true;

                    if(loopMessageOffset - message.offset == 1){
                        //если это непосредственный потомок

                        talk.fullTalkMessages[firstMessage.id][i].isOpen ?
                            talk.fullTalkMessages[firstMessage.id][i].isOpen = false :
                            talk.fullTalkMessages[firstMessage.id][i].isOpen = true ;

                        parentOpenStatusArray[loopMessageOffset] = true;
                        parentOpenStatus = talk.fullTalkMessages[firstMessage.id][i].isOpen;

                        if (!talk.fullTalkMessages[firstMessage.id][i].isTreeOpen){
                            areAllMyParentsTreeOpen[loopMessageOffset] = false;
                        }
                    }else{
                        // если это птомки потомка

                        checkAreAllMyParentsTreeOpen = true;
                        for(var j = beginOffset; j < loopMessageOffset; j++){
                            // проверяем нет ли у кого в предках isTreeOpen = false
                            if(areAllMyParentsTreeOpen[j] == false){
                                checkAreAllMyParentsTreeOpen = false;
                            }
                        }
                        parentOpenStatus && checkAreAllMyParentsTreeOpen ?
                            talk.fullTalkMessages[firstMessage.id][i].isOpen = true :
                            talk.fullTalkMessages[firstMessage.id][i].isOpen = false ;

                        if (!talk.fullTalkMessages[firstMessage.id][i].isTreeOpen){
                            // если у кого-то из предков не открыто дерево
                            areAllMyParentsTreeOpen[loopMessageOffset] = false;
                        }

                        parentOpenStatusArray[loopMessageOffset] = true;
                    }
                }

                if (afterCurrentIndex && loopMessageOffset == message.offset){
                    nextMessageOnCurrentLevel = true;
                    break;
                }
                if(message.id == talk.fullTalkMessages[firstMessage.id][i].id){
                    afterCurrentIndex = true;
                }
            }
        };


    })
    .controller('AdvertsController',function($rootScope) {
        var adverts = this;

        $rootScope.setTab(3);
        $rootScope.base.showAllGroups();
        $rootScope.base.isFooterBottom = false;

        initAttachImage($('#attachImage-00000'), $('#attach-area-00000')); // для обсуждений
        initAttachDoc($('#attachDoc-00000'), $('#attach-doc-area-00000')); // для обсуждений
        initFancyBox($('.adverts'));

        $rootScope.base.createTopicIsHide = true;
        adverts.isAdvertsLoaded = false;
        adverts.groups = userClientGroups;

        adverts.content = TEXT_DEFAULT_3;
        adverts.subject = TEXT_DEFAULT_4;

        adverts.isPollShow = false;
        adverts.pollSubject = "";
        adverts.pollInputs = [
            {
                counter: 0,
                name: ""
            },
            {
                counter: 1,
                name: ""
            }
        ];
        adverts.isPollAvailable = true;
        adverts.answerFirstMessage = "Ваш ответ";

       /* adverts.fullAdvertsTopic = {};
        adverts.fullAdvertsTopic.answerInputIsShow = false;
        adverts.fullAdvertsMessages = [];
        adverts.fullAdvertsFirstMessages = [];
        var fullAdvertsFirstMessagesLength,
            advertsId;*/

        //$rootScope.currentGroup = adverts.selectedGroup = adverts.groups[0];
        $rootScope.base.bufferSelectedGroup = adverts.selectedGroup = $rootScope.currentGroup;

        adverts.topics = messageClient.getAdverts(adverts.selectedGroup.id, 0, 1000).topics;
        //adverts.topics = messageClient.getTopics(adverts.selectedGroup.id, 0, 0, 0, 1000).topics;

        initAdverts();

        if (!adverts.topics) adverts.topics = [];

        $rootScope.selectGroupInDropdown_adverts = function(groupId){
            adverts.selectedGroup = $rootScope.base.bufferSelectedGroup = selectGroupInDropdown(groupId);
        };

        adverts.addSingleAdverts = function(){
            adverts.attachedImages = getAttachedImages($('#attach-area-00000'));
            adverts.attachedDocs = getAttachedDocs($('#attach-doc-area-00000'));
            if(adverts.subject == TEXT_DEFAULT_4 || adverts.subject == ""){

                adverts.isCreateAdvertsError = true;
                adverts.createAdvertsErrorText = "Вы не указали заголовок";

            }else if(adverts.attachedImages.length == 0 && (adverts.attachedDocs === undefined || adverts.attachedDocs.length == 0) && !adverts.isPollShow
                && adverts.content == TEXT_DEFAULT_3){

                adverts.isCreateAdvertsError = true;
                adverts.createAdvertsErrorText = "Вы не ввели сообщение";

            }else if(adverts.isPollShow && (!adverts.pollSubject || adverts.pollInputs[0].name == "" || adverts.pollInputs[1].name == "")){

                adverts.isCreateAdvertsError = true;
                adverts.createAdvertsErrorText = "Вы не указали данные для опроса";

            }else {

                if (adverts.content == TEXT_DEFAULT_3 && (adverts.attachedImages || adverts.attachedDocs || adverts.isPollShow)) {
                    adverts.content = "";
                }
                adverts.isCreateAdvertsError = false;
                var isWall = 0, isAdverts = true,
                    newTopic = postTopic(adverts, isWall,isAdverts);
                newTopic.label = getLabel(adverts.groups,newTopic.groupType);
                newTopic.tagColor = getTagColor(newTopic.label);

                $rootScope.base.createTopicIsHide = true;

                //adverts.topics.unshift(newTopic);
                $rootScope.selectGroup($rootScope.base.bufferSelectedGroup);
            }

            cleanAttached($('#attach-area-00000'));
            cleanAttached($('#attach-doc-area-00000'));
            adverts.subject = TEXT_DEFAULT_4;

        };

        function initAdverts(){
            var topicLength;
            adverts.topics ? topicLength = adverts.topics.length : topicLength = 0;

            for(var i = 0; i < topicLength;i++){
                adverts.topics[i].lastUpdateEdit = getTiming(adverts.topics[i].lastUpdate);
                adverts.topics[i].label = getLabel(adverts.groups,adverts.topics[i].groupType);
                adverts.topics[i].tagColor = getTagColor(adverts.topics[i].label);
            }
        }

        $rootScope.advertsChangeGroup = function(groupId){

            adverts.topics = messageClient.getAdverts(groupId,0,1000).topics;

            if(adverts.topics) {
                initAdverts();
            }

        };

    })
    .controller('AdvertsSingleController',function($rootScope,$stateParams) {
        var advert = this,
            fullAdvertMessagesLength,
            advertId = $stateParams.advertId;

        $rootScope.base.isFooterBottom = false;

        advert.selectedGroup = $rootScope.currentGroup;
        advert.topics = messageClient.getAdverts(advert.selectedGroup.id, 0, 1000).topics;
        advert.fullAdvertTopic = {};
        advert.fullAdvertMessages = {};
        advert.fullAdvertFirstMessages = [];
        advert.groups = userClientGroups;

        var showFullTalk = function(advert,advertOutsideId){

            initFancyBox($('.adverts-single'));
            var topicLength;
            advert.topics ? topicLength = advert.topics.length : topicLength = 0;

            var advertId = advertOutsideId,
                fullAdvertFirstMessagesLength;
            for(var i = 0; i < topicLength; i++){
                if(advertId == advert.topics[i].id){
                    advert.fullAdvertTopic = advert.topics[i];
                    advert.fullAdvertTopic.message.createdEdit = getTiming(advert.fullAdvertTopic.message.created);
                    advert.fullAdvertTopic.label = getLabel(advert.groups,advert.fullAdvertTopic.groupType);
                    advert.fullAdvertTopic.tagColor = getTagColor(advert.fullAdvertTopic.label);
                }
            }
            if(advert.fullAdvertTopic.poll != null){
                setPollEditNames(advert.fullAdvertTopic.poll);
                advert.fullAdvertTopic.metaType = "poll";
            }else{
                advert.fullAdvertTopic.metaType = "message";
            }

            advert.fullAdvertFirstMessages = messageClient.getFirstLevelMessages(advertId,advert.selectedGroup.id,6,0,0,1000).messages;

            advert.fulladvertFirstMessages ?
                fullAdvertFirstMessagesLength = advert.fullAdvertFirstMessages.length:
                fullAdvertFirstMessagesLength = 0;
            if(advert.fullAdvertFirstMessages === null) advert.fullAdvertFirstMessages = [];

            for(var i = 0; i < fullAdvertFirstMessagesLength; i++){
                advert.fullAdvertFirstMessages[i].answerInputIsShow = false;
                advert.fullAdvertFirstMessages[i].isTreeOpen = false;
                advert.fullAdvertFirstMessages[i].isLoaded = false;
                advert.fullAdvertFirstMessages[i].answerMessage = "Ваш ответ";
                advert.fullAdvertFirstMessages[i].createdEdit = getTiming(advert.fullAdvertFirstMessages[i].created);
            }

            $rootScope.base.isAdvertTitles = false;
            $rootScope.base.mainContentTopIsHide = true;
            $rootScope.base.createTopicIsHide = true;

            $rootScope.base.advert = advert;

        };

        showFullTalk(advert,advertId);

        var initFlagsTopic = [];
        advert.showTopicAnswerInput = function(event,fullAdvertTopic){
            event.preventDefault();

            if(!initFlagsTopic[fullAdvertTopic.id]) {
                initAttachImage($('#attachImage-' + fullAdvertTopic.id), $('#attach-area-' + fullAdvertTopic.id));
                initAttachDoc($('#attachDoc-' + fullAdvertTopic.id), $('#attach-doc-area-' + fullAdvertTopic.id));
                initFlagsTopic[fullAdvertTopic.id] = true;
            }

            advert.fullAdvertTopic.answerInputIsShow ?
                advert.fullAdvertTopic.answerInputIsShow = false :
                advert.fullAdvertTopic.answerInputIsShow = true ;
        };

        var initFlagsMessage = [];
        advert.showMessageAnswerInput = function(event,fullAdvertTopic,firstMessage,message){
            event.preventDefault();
            var attachId;

            if(!message){
                // если это сообщение первого уровня
                attachId = fullAdvertTopic.id+'-'+firstMessage.id;

                if(!advert.fulladvertFirstMessages) advert.fulladvertFirstMessages = messageClient.getFirstLevelMessages(advertId,advert.selectedGroup.id,6,0,0,1000).messages;
                var fulladvertFirstMessagesLength = advert.fulladvertFirstMessages.length;

                firstMessage.answerInputIsShow ?
                    firstMessage.answerInputIsShow = false :
                    firstMessage.answerInputIsShow = true;


            }else{
                // если простое сообщение
                attachId = fullAdvertTopic.id+'-'+message.id;

                if(!advert.fullAdvertMessages[firstMessage.id]) advert.fullAdvertMessages[firstMessage.id] = messageClient.getMessages(advertId,advert.selectedGroup.id,6,firstMessage.id,0,1000).messages;
                var  fullAdvertMessagesLength = advert.fullAdvertMessages[firstMessage.id].length;
                message.answerInputIsShow ?
                    message.answerInputIsShow = false :
                    message.answerInputIsShow = true;


            }

            if(!initFlagsMessage[attachId]) {
                initAttachImage($('#attachImage-' + attachId), $('#attach-area-' + attachId));
                initAttachDoc($('#attachDoc-' + attachId), $('#attach-doc-area-' + attachId));

                initFlagsMessage[attachId] = true;
            }
        };

        advert.addSingleFirstMessage = function(event,topicId){
            event.preventDefault();

            advert.topicId = topicId;

            var isWall = false,
                isFirstLevel = true,
                newMessage = postMessage(advert,isWall,isFirstLevel);

            if(newMessage == 0){
                advert.isCreateFirstMessageError = true;
                advert.createFirstMessageErrorText = "Вы не ввели сообщение";
            }else {
                advert.fullAdvertTopic.answerInputIsShow = false;

                advert.isCreateFirstMessageError = false;
                advert.fullAdvertFirstMessages ?
                    advert.fullAdvertFirstMessages.push(newMessage) :
                    advert.fullAdvertFirstMessages[0] = newMessage;
            }

        };

        advert.addSingleMessage = function(event,topicId,firstMessage,message){
            event.preventDefault();

            if (!advert.fullAdvertMessages[firstMessage.id]) advert.fullAdvertMessages[firstMessage.id] = messageClient.getMessages(advertId,advert.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            advert.fullAdvertMessages[firstMessage.id] ?
                fullAdvertMessagesLength = advert.fullAdvertMessages[firstMessage.id].length:
                fullAdvertMessagesLength = 0;

            var newMessage,answer,parentId;

            if(!message){
                // если добавляем к сообщению первого уровня
                advert.messageId = firstMessage.id;

                answer = firstMessage.answerMessage;
                firstMessage.isTreeOpen = true;
                firstMessage.answerMessage = "Ваш ответ";
                parentId = firstMessage.id;

            }else{
                // если добавляем к простому сообщению
                advert.messageId = message.id;

                for(var i = 0; i < fullAdvertMessagesLength; i++){
                    if(advert.fullAdvertMessages[firstMessage.id][i].id == message.id){
                        //talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                        advert.fullAdvertMessages[firstMessage.id][i].isTreeOpen = true;
                        advert.fullAdvertMessages[firstMessage.id][i].isOpen = true;
                        advert.fullAdvertMessages[firstMessage.id][i].isParentOpen = true;
                        advert.fullAdvertMessages[firstMessage.id][i].createdEdit = getTiming(advert.fullAdvertMessages[firstMessage.id][i].created);
                        answer = advert.fullAdvertMessages[firstMessage.id][i].answerMessage;
                    }
                }
                parentId = message.id;

            }
            var isWall = false,
                isFirstLevel = false;
            advert.topicId = topicId;
            advert.parentId = parentId;
            advert.answerMessage = answer;

            newMessage = postMessage(advert,isWall,isFirstLevel);

            if(newMessage == 0){
                if(!message){
                    advert.isCreateMessageToFirstError = true;
                    advert.createMessageToFirstErrorText = "Вы не ввели сообщение";
                }else{
                    advert.isCreateMessageError = true;
                    advert.createMessageErrorText = "Вы не ввели сообщение";
                }
            }else {
                if(!message){
                    advert.isCreateMessageToFirstError = false;
                    firstMessage.answerInputIsShow = false;

                }else{
                    advert.isCreateMessageError = false;
                    for(var i = 0; i < fullAdvertMessagesLength; i++){
                        if(advert.fullAdvertMessages[firstMessage.id][i].id == message.id){
                            advert.fullAdvertMessages[firstMessage.id][i].answerInputIsShow = false;
                        }
                    }
                }

                advert.fullAdvertMessages[firstMessage.id] = messageClient.getMessages(advertId, advert.selectedGroup.id, 1, firstMessage.id, 0, 1000).messages;

                advert.fullAdvertMessages[firstMessage.id] ?
                    fullAdvertMessagesLength = advert.fullAdvertMessages[firstMessage.id].length :
                    fullAdvertMessagesLength = 0;

                for (var i = 0; i < fullAdvertMessagesLength; i++) {
                    advert.fullAdvertMessages[firstMessage.id][i].answerInputIsShow = false;
                    advert.fullAdvertMessages[firstMessage.id][i].isTreeOpen = true;
                    advert.fullAdvertMessages[firstMessage.id][i].isOpen = true;
                    advert.fullAdvertMessages[firstMessage.id][i].isParentOpen = true;
                    advert.fullAdvertMessages[firstMessage.id][i].createdEdit = getTiming(advert.fullAdvertMessages[firstMessage.id][i].created);
                    advert.fullAdvertMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
                }
            }

        };

        advert.toggleTreeFirstMessage = function($event,firstMessage){
            event.preventDefault();

            firstMessage.isTreeOpen ?
                firstMessage.isTreeOpen = false :
                firstMessage.isTreeOpen = true ;


            // --------

            advert.fullAdvertMessages[firstMessage.id] = messageClient.getMessages(advertId,advert.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            advert.fullAdvertMessages[firstMessage.id] ?
                fullAdvertMessagesLength = advert.fullAdvertMessages[firstMessage.id].length:
                fullAdvertMessagesLength = 0;
            if(advert.fullAdvertMessages[firstMessage.id] === null) advert.fullAdvertMessages[firstMessage.id] = [];

            for(var i = 0; i < fullAdvertMessagesLength; i++){
                advert.fullAdvertMessages[firstMessage.id][i].answerInputIsShow = false;
                advert.fullAdvertMessages[firstMessage.id][i].isTreeOpen = true;
                advert.fullAdvertMessages[firstMessage.id][i].isOpen = true;
                advert.fullAdvertMessages[firstMessage.id][i].isParentOpen = true;
                advert.fullAdvertMessages[firstMessage.id][i].createdEdit = getTiming(advert.fullAdvertMessages[firstMessage.id][i].created);
                advert.fullAdvertMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
            }

        };

        advert.toggleTree = function($event,message,firstMessage){
            event.preventDefault();

            if(!advert.fullAdvertMessages[firstMessage.id]) advert.fullAdvertMessages[firstMessage.id] = messageClient.getMessages(advertId,advert.selectedGroup.id,1,firstMessage.id,0,1000).messages;
            var fullAdvertMessagesLength = advert.fullAdvertMessages[firstMessage.id].length;

            message.isTreeOpen ?
                message.isTreeOpen = false :
                message.isTreeOpen = true ;

            var afterCurrentIndex = false,
                nextMessageOnCurrentLevel = false,
                loopMessageOffset,
                parentOpenStatus,
                areAllMyParentsTreeOpen = [],
                checkAreAllMyParentsTreeOpen = true,
                beginOffset = message.offset,
                parentOpenStatusArray = [];

            for(var i = 0; i < fullAdvertMessagesLength; i++){
                loopMessageOffset = advert.fullAdvertMessages[firstMessage.id][i].offset;

                if(afterCurrentIndex && !nextMessageOnCurrentLevel
                    && message.offset < loopMessageOffset){

                    areAllMyParentsTreeOpen[loopMessageOffset] = true;

                    if(loopMessageOffset - message.offset == 1){
                        //если это непосредственный потомок

                        advert.fullAdvertMessages[firstMessage.id][i].isOpen ?
                            advert.fullAdvertMessages[firstMessage.id][i].isOpen = false :
                            advert.fullAdvertMessages[firstMessage.id][i].isOpen = true ;

                        parentOpenStatusArray[loopMessageOffset] = true;
                        parentOpenStatus = advert.fullAdvertMessages[firstMessage.id][i].isOpen;

                        if (!advert.fullAdvertMessages[firstMessage.id][i].isTreeOpen){
                            areAllMyParentsTreeOpen[loopMessageOffset] = false;
                        }
                    }else{
                        // если это птомки потомка

                        checkAreAllMyParentsTreeOpen = true;
                        for(var j = beginOffset; j < loopMessageOffset; j++){
                            // проверяем нет ли у кого в предках isTreeOpen = false
                            if(areAllMyParentsTreeOpen[j] == false){
                                checkAreAllMyParentsTreeOpen = false;
                            }
                        }
                        parentOpenStatus && checkAreAllMyParentsTreeOpen ?
                            advert.fullAdvertMessages[firstMessage.id][i].isOpen = true :
                            advert.fullAdvertMessages[firstMessage.id][i].isOpen = false ;

                        if (!advert.fullAdvertMessages[firstMessage.id][i].isTreeOpen){
                            // если у кого-то из предков не открыто дерево
                            areAllMyParentsTreeOpen[loopMessageOffset] = false;
                        }

                        parentOpenStatusArray[loopMessageOffset] = true;
                    }
                }

                if (afterCurrentIndex && loopMessageOffset == message.offset){
                    nextMessageOnCurrentLevel = true;
                    break;
                }
                if(message.id == advert.fullAdvertMessages[firstMessage.id][i].id){
                    afterCurrentIndex = true;
                }
            }
        };
    })
    .controller('neighboursController',function($rootScope,$state) {
        $rootScope.currentPage = "neighbours";
        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;
        $rootScope.base.showAllGroups();
        $rootScope.base.isFooterBottom = false;

        resetPages($rootScope.base);
        $rootScope.base.mainContentTopIsHide = false;
        $rootScope.base.neighboursIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.navbar.neighboursBtnStatus = "active";
        $rootScope.base.pageTitle = "";

        $rootScope.base.neighboursLoadStatus = "isLoaded";

        var neighbours = this;
        neighbours.neighboors = userClient.getNeighboursByGroup($rootScope.currentGroup.id);

        $rootScope.neighboursChangeGroup = function(groupId){
            neighbours.neighboors = userClient.getNeighboursByGroup(groupId);
        };

        neighbours.neighboorsSize = neighbours.neighboors.length;

        neighbours.goToDialog = function(userId){
            var users = [];
            users[0] = userId;
            var dialog = dialogClient.getDialog(users,0);

            $state.go('dialog-single',{ 'dialogId' : dialog.id});
        };

        /*function usersToInt(users){
            var usersLength = users.length,
                usersInt = [];
            for(var i = 0; i < usersLength; i++){
                usersInt[i] = parseInt(writeMessage.users[i]);
            }

            return usersInt;
        }*/


    })
    .controller('ProfileController',function($rootScope, $stateParams) {

        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.profileIsActive = true;
        $rootScope.base.isFooterBottom = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.base.profileLoadStatus = "isLoaded";

        var profile = this, userId;
        profile.isMayEdit = false;

        $("#dialog-message").addClass('hide');

        //alert($stateParams.userId+" "+shortUserInfo.id);
        if ($stateParams.userId && $stateParams.userId != 0 && $stateParams.userId != shortUserInfo.id){
            userId = $stateParams.userId;
            profile.userContacts = userClient.getUserContactsExt(userId);
        }else{
            userId = 0;
            profile.isMayEdit = true;
            profile.userContacts = userClient.getUserContacts();
        }

        profile.userProfile = userClient.getUserProfile(userId);

        //alert(userId+" "+profile.userProfile.family.relations);

        $rootScope.base.avatarBuffer = profile.userProfile.userInfo.avatar;

        if(profile.userProfile.family && profile.userProfile.family.relations == 0){

            if(profile.userProfile.userInfo.gender == 0){
                profile.userProfile.family.relationsMeta = "За мужем";
            }else if(profile.userProfile.userInfo.gender == 1){
                profile.userProfile.family.relationsMeta = "Женат";
            }

        }else if(profile.userProfile.family && profile.userProfile.family.relations == 1){
            if(profile.userProfile.userInfo.gender == 0){
                profile.userProfile.family.relationsMeta = "Не замужем";
            }else if(profile.userProfile.userInfo.gender == 1){
                profile.userProfile.family.relationsMeta = "Холост";
            }
        }

        if(profile.userProfile.family && profile.userProfile.family.pets && profile.userProfile.family.pets.length != 0){
           var petsLength = profile.userProfile.family.pets.length;
            var pets = profile.userProfile.family.pets;
            for(var i = 0; i < petsLength; i++){
                switch(profile.userProfile.family.pets[i].type){
                    case 0:
                        profile.userProfile.family.pets[i].typeMeta = "Кот/кошка";
                        break;
                    case 1:
                        profile.userProfile.family.pets[i].typeMeta = "Собака";
                        break;
                    case 2:
                        profile.userProfile.family.pets[i].typeMeta = "Птичка";
                        break;
                }

            }
        }

        profile.map = userClient.getGroupMap($rootScope.groups[0].id, MAP_COLOR);

        $rootScope.chageIndex = 0;

})
    .controller('SettingsController',function($rootScope,$scope) {
        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.settingsIsActive = true;
        $rootScope.base.isFooterBottom = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.base.mainContentTopIsHide = true;

        $rootScope.base.settingsLoadStatus = "isLoaded";

        var settings = this,
            userContatcsMeta = userClient.getUserContacts(),
            userProfileMeta = userClient.getUserProfile(),
            userInfoMeta = userProfileMeta.userInfo,
            userPrivacyMeta = userProfileMeta.privacy,
            userNotificationsMeta = userProfileMeta.notifications,
            userFamilyMeta = userProfileMeta.family,
            userInterestsMeta = userProfileMeta.interests;

        if(userFamilyMeta === null){
            userFamilyMeta = new com.vmesteonline.be.UserFamily();
        }

        settings.userContacts = clone(userContatcsMeta);
        settings.userProfile = clone(userProfileMeta);
        settings.userInfo = clone(userInfoMeta);
        settings.userPrivacy = clone(userPrivacyMeta);
        settings.userNotifications = clone(userNotificationsMeta);
        settings.family = clone(userFamilyMeta);
        settings.interests = clone(userInterestsMeta);

        if (settings.userInfo.gender == 0) {
            settings.married = "Замужем";
            settings.notMarried = "Не замужем";
        }else{
            settings.married = "Женат";
            settings.notMarried = "Не женат";
        }

        settings.years= [];
        var ind = 0;
        for(var i = 1940; i < 2015; i++){
            settings.years[ind++] = i;
        }

        settings.userInfo.birthday ?
        settings.userInfo.birthdayMeta = new Date(settings.userInfo.birthday*1000) :
        settings.userInfo.birthdayMeta = "";

        if(settings.userInfo.birthdayMeta){
            var month = settings.userInfo.birthdayMeta.getMonth()+1+"";
            if(month.length == 1) month = "0"+month;

            var day = ""+settings.userInfo.birthdayMeta.getDate();
            if(day.length == 1) day = "0"+day;

            var year = settings.userInfo.birthdayMeta.getFullYear();

            settings.userInfo.birthdayMeta = month+"."+day+"."+year;
        }
        //alert(settings.userInfo.birthday+" "+settings.userInfo.birthdayMeta);

        if(settings.family.childs === null || settings.family.childs.length == 0){
            settings.family.childs = [];
            settings.family.childs[0] = new com.vmesteonline.be.Children();
            settings.family.childs[0].name = "";
            var nowYear = new Date();
            nowYear = nowYear.getFullYear();
            settings.family.childs[0].birthday = Date.parse('01.15.'+nowYear);
            settings.family.childs[0].isNotRemove = true;
        }
        var childsLength = settings.family.childs.length;
        for(var i = 0; i < childsLength; i++){
            if(settings.family.childs[i].birthday) {

                var birthDate = new Date(settings.family.childs[i].birthday*1000);
                //alert(settings.family.childs[i].birthday);
                    settings.family.childs[i].month = ""+birthDate.getMonth();

                if(settings.family.childs[i].month.length == 1)
                    settings.family.childs[i].month = "0"+settings.family.childs[i].month;

                    settings.family.childs[i].year = birthDate.getFullYear();
            }

        }

        if(settings.family.pets === null || settings.family.pets.length == 0){
            settings.family.pets = [];
            settings.family.pets[0] = new com.vmesteonline.be.Pet();
            settings.family.pets[0].name = "";
            settings.family.pets[0].type = "0";
            settings.family.pets[0].breed = "";
            settings.family.pets[0].isNotRemove = true;
        }

        settings.oldPassw = "";
        settings.newPassw = "";

        settings.canSave = function(num){
            switch(num){
                case 1:
                    return $scope.formUserInfo.$valid;
                    break;
                case 2:
                    return $scope.formPrivate.$valid;
                    break;
                case 3:
                    return $scope.formAlerts.$valid;
                    break;
                case 4:
                    return $scope.formContacts.$valid;
                    break;
                case 5:
                    return $scope.formFamily.$valid;
                    break;
                case 6:
                    return $scope.formInterests.$valid;
                    break;
            }

        };

        settings.isProfileError = false;
        settings.isProfileResult = false;
        settings.updateUserInfo = function(){
            var temp = new com.vmesteonline.be.UserInfo();

            settings.userInfo.birthdayMeta ?
                temp.birthday = Date.parse(settings.userInfo.birthdayMeta)/1000 :
                temp.birthday = 0;
            //alert(temp.birthday+" "+new Date(temp.birthday));

            temp.gender = settings.userInfo.gender;
            temp.firstName = settings.userInfo.firstName;
            temp.lastName = settings.userInfo.lastName;

            userClient.updateUserInfo(temp);
            settings.isProfileResult = true;
            settings.isProfileError = false;
            settings.profileInfo = "Сохранено";

        };
        settings.updatePassword = function(){
            if (settings.newPassw.length < 3){
                settings.isPasswResult = true;
                settings.isPasswError = true;
                settings.passwInfo = "Вы указали слишком короткий пароль";
            }else{
                settings.isPasswResult = true;
                try {
                    userClient.changePassword(settings.oldPassw, settings.newPassw);
                    settings.isPasswError = false;
                    settings.passwInfo = "Сохранено";
                }catch(e){
                    settings.isPasswError = true;
                    settings.passwInfo = "Вы указали не верный старый пароль";
                }
            }
        };

        settings.updatePrivacy = function(){
            userClient.updatePrivacy(settings.userPrivacy);
        };
        settings.updateContacts = function(){
            var temp = new com.vmesteonline.be.UserContacts();
            temp.email = settings.userContacts.email;
            temp.mobilePhone = settings.userContacts.mobilePhone;
            userClient.updateContacts(temp);
        };
        settings.updateNotifications = function(){
            if(settings.userNotifications && (settings.userNotifications.email || settings.userNotifications.freq) ){
                var temp = new com.vmesteonline.be.Notifications();
                temp.email = settings.userNotifications.email;
                temp.freq = settings.userNotifications.freq;

                userClient.updateNotifications(temp);
            }
        };
        settings.updateFamily = function(){
            var temp = new com.vmesteonline.be.UserFamily();
            temp.relations = settings.family.relations;
            temp.childs = settings.family.childs;
            //temp.childs = [];
            //temp.childs[0] = settings.firstChild;

            temp.pets = settings.family.pets;

            var childsLength = settings.family.childs.length;
            for(var i = 0; i < childsLength; i++){
                if(settings.family.childs[i].name && settings.family.childs[i].month && settings.family.childs[i].year){

                    var tempMonth = parseInt(settings.family.childs[i].month)+1;
                    temp.childs[i].birthday = Date.parse(tempMonth+".15."+ settings.family.childs[i].year)/1000;
                }
            }
            var petsLength = settings.family.pets.length;
            for(var i = 0; i < petsLength; i++){
                if(!temp.pets[i].name){

                    //temp.pets.splice(i,1);
                }
            }
            userClient.updateFamily(temp);
        };
        settings.updateInterests = function(){
            var temp = new com.vmesteonline.be.UserInterests();
            temp.job = settings.interests.job;
            temp.userInterests = settings.interests.userInterests;
            userClient.updateInterests(temp);
        };

        settings.childAdd = function(event){
            event.preventDefault();

            var newChild = new com.vmesteonline.be.Children();
            newChild.name = " ";
            var nowYear = new Date();
            nowYear = nowYear.getFullYear();
            newChild.birthday = Date.parse('01.15.'+nowYear);

            var birthDate = new Date(newChild.birthday);
            //alert(settings.family.childs[i].birthday);
            newChild.month = ""+birthDate.getMonth();

            if(newChild.length == 1)
                newChild.month = "0"+newChild.month;

            newChild.year = birthDate.getFullYear();


            if(settings.family == null){
                settings.family = new com.vmesteonline.be.UserFamily();
            }
            if(settings.family.childs == null){
                settings.family.childs= [];
            }

            settings.family.childs.length == 0 ?
            settings.family.childs[0] = newChild :
            settings.family.childs.push(newChild);

        };
        settings.removeChild = function(childName){
            var childsLength = settings.family.childs.length;
            for(var i = 0; i < childsLength; i++){
                if(settings.family.childs[i].name == childName) {
                    settings.family.childs.splice(i,1);
                }

            }
        };
        settings.petAdd = function(event){
            event.preventDefault();

            var newPet = new com.vmesteonline.be.Pet();
            newPet.name = " ";
            newPet.type = "0";

            if(settings.family == null){
                settings.family = new com.vmesteonline.be.UserFamily();
            }
            if(settings.family.pets == null){
                settings.family.pets= [];
            }

            settings.family.pets.length == 0 ?
                settings.family.pets[0] = newPet :
                settings.family.pets.push(newPet);
        };
        settings.removePet = function(petName){
            var petsLength = settings.family.pets.length;
            for(var i = 0; i < petsLength; i++){
                if(settings.family.pets[i].name == petName) {
                    settings.family.pets.splice(i,1);
                }

            }
        };

        settings.passwChange = false;
        settings.changePassw = function(){
            settings.passwChange = true;
        };

        /*(settings.userInfo.birthday != 0) ?
        settings.birthday = settings.userInfo.birthday :
        settings.birthday = "";*/

        $('#settings-input-3').datepicker({changeMonth:true, changeYear:true,dateFormat: "mm.dd.yy",yearRange:'c-100:+c'});
        $.datepicker.setDefaults($.datepicker.regional['ru']);


    })
    .controller('dialogsController', function($rootScope,$state){
        $rootScope.isTopSearchShow = false;
        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.leftbar.tab = 0;
        $rootScope.base.isFooterBottom = false;

        resetPages($rootScope.base);
        $rootScope.base.privateMessagesIsActive = true;
        $rootScope.base.pageTitle = "Личные сообщения";

        resetAceNavBtns($rootScope.navbar);
        $rootScope.navbar.privateMessagesBtnStatus = "active";

        $rootScope.base.privateMessagesLoadStatus = "isLoaded";

        $rootScope.isNewPrivateMessageAdded = false;

        var dialogs = this;

        dialogs.dialogsList = dialogClient.getDialogs(0);
        var dialogsListLength = dialogs.dialogsList.length;
        for(var i = 0; i < dialogsListLength; i++){
            (dialogs.dialogsList[i].users[0].id != $rootScope.base.me.id) ?
                dialogs.dialogsList[i].anotherUser = dialogs.dialogsList[i].users[0] :
                dialogs.dialogsList[i].anotherUser = dialogs.dialogsList[i].users[1];
        }

        dialogs.goToSingleDialog = function(dialogId){
            var usersInfoArray = [],
                usersInfoLength,
                usersId = [];
            for(var i = 0; i < dialogsListLength; i++){
                if(dialogs.dialogsList[i].id == dialogId){
                    usersInfoArray = dialogs.dialogsList[i].users;
                }
            }
            if(usersInfoArray){
                usersInfoLength = usersInfoArray.length;
                for(var i = 0; i < usersInfoLength; i++){
                    usersId[i] = usersInfoArray[i].id
                }
            }
            //$rootScope.currentDialog = dialogClient.getDialog(usersId);
            $state.go('dialog-single',{ dialogId : dialogId});
        };

    })
    .controller('dialogController',function($rootScope,$stateParams) {

        initFancyBox($('.dialog'));
        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.base.isFooterBottom = false;

        var dialog = this;
        var currentDialog = dialogClient.getDialogById($stateParams.dialogId);
        dialog.users = currentDialog.users;
        var dialogUsersLength = dialog.users.length;
        for(var i = 0; i < dialogUsersLength; i++){
            //console.log(dialog.users[i].id+" "+$rootScope.base.me.id);
            if (dialog.users[i] && (dialog.users[i].id == $rootScope.base.me.id)){
                dialog.users.splice(i,1);
            }
        }
        initAttachImage($('#attachImage-000'),$('#attach-area-000'));
        initAttachDoc($('#attachDoc-000'),$('#attach-doc-area-000'));

        if ($stateParams.dialogId){
            dialog.privateMessages = dialogClient.getDialogMessages($stateParams.dialogId);
            var privateMessagesLength = dialog.privateMessages.length;
                dialog.authors = [];
            for(var i = 0; i < privateMessagesLength; i++){
                dialog.privateMessages[i].authorProfile = userClient.getUserProfile(dialog.privateMessages[i].author);
            }
        }

        dialog.messageText = TEXT_DEFAULT_1;
        dialog.sendMessage = function(){
            var attach = [];
            attach = getAttachedImages($('#attach-area-000')).concat(getAttachedDocs($('#attach-doc-area-000')));

            if((dialog.messageText != TEXT_DEFAULT_1 && dialog.messageText != "") || attach.length != 0){

                var newDialogMessage = new com.vmesteonline.be.messageservice.DialogMessage();

                (dialog.messageText == TEXT_DEFAULT_1) ?
                    newDialogMessage.content = "" :
                    newDialogMessage.content = dialog.messageText;

                newDialogMessage.author = $rootScope.base.me.id;

                newDialogMessage.created = Date.parse(new Date())/1000;
                newDialogMessage.authorProfile = userClient.getUserProfile(newDialogMessage.author);

                var tempMessage = dialogClient.postMessage($stateParams.dialogId, newDialogMessage.content,attach);
                newDialogMessage.images = tempMessage.images;
                newDialogMessage.documents = tempMessage.documents;

                dialog.privateMessages.push(newDialogMessage);

                dialog.messageText = TEXT_DEFAULT_1;
                cleanAttached($('#attach-area-000'));
                cleanAttached($('#attach-doc-area-000'));
            }
        }

    })
    .controller('changeAvatarController',function($state,$rootScope){
        var changeAvatar = this, newSrc,
            x1 = 50, y1 = 50, x2 = 200, y2 = 200,
            imageWidth = 150, imageHeight = 150;

        changeAvatar.save = function(){

            var saveSrc = newSrc+"?w="+ imageWidth +"&h="+ imageHeight +"&s="+x1+","+y1+","+x2+","+y2;
            userClient.updateUserAvatar(saveSrc);
            $rootScope.base.user.avatar = $rootScope.base.avatarBuffer = saveSrc;

            $("#dialog-message").dialog('close');
            $state.go('profile');

            $('.preview-container').addClass('hidden');

            $('.ui-dialog').each(function(){
                if($(this).attr('aria-describedby') == 'dialog-message'){
                    $(this).detach();
                }
            });
        };

        changeAvatar.back = function(){
            $('.load-avatar').find('.file-label').html("").
                removeClass("hide-placeholder selected").
                attr("data-title","Загрузить аватар");

            $('.loadAvatar-area').removeClass('hidden');
            $('.crop-area').addClass('hidden');

            $('.preview-container').addClass('hidden');
            $('.loading').removeClass('hidden');

            $('#image-for-crop').detach();
            $('.jcrop-holder').detach();

            $('.btn-save-avatar').before('<img src="#" id="image-for-crop" alt="#" class="hidden" />');

        };

            initModalAndCrop();

        function initModalAndCrop() {

            $("#dialog-message").removeClass('hide').dialog({
                modal: true,
                width: 504,
                position: ['center', 100],
                title_html: false,
                closeText: "",
                create: function (event, ui) {

                    $('.load-avatar input').ace_file_input({
                        style: 'well',
                        btn_choose: 'Загрузить аватар',
                        btn_change: null,
                        no_icon: '',
                        droppable: true,
                        thumbnail: 'large',
                        icon_remove: null
                    }).on('change', function () {
                        var imageForCrop = $('#image-for-crop');

                        $('.loadAvatar-area').addClass('hidden');
                        $('.crop-area').removeClass('hidden');

                        setTimeout(saveNewAva, 1000);

                        function saveNewAva() {

                            var bg = $('.load-avatar').find('.file-label img').css('background-image'),
                                src = $('.load-avatar').find('.file-label img').attr('src');

                            newSrc = fileClient.saveFileContent(bg, true);

                            $('#preview').attr('src', newSrc);

                            imageForCrop.attr('src', newSrc);
                            imageForCrop.css({'max-width': '500px', 'max-height': '500px'});

                            imageForCrop.Jcrop({
                                aspectRatio: 1,
                                setSelect: [ 200, 200, 50, 50 ],
                                onChange: updateCoords,
                                onSelect: updateCoords
                            }).removeClass('hidden');

                            $('.preview-container').removeClass('hidden');
                            $('.loading').addClass('hidden');


                        }

                        function updateCoords(c) {
                            imageWidth = imageForCrop.width();
                            imageHeight = imageForCrop.height();

                            x1 = c.x;
                            y1 = c.y;
                            x2 = c.x2;
                            y2 = c.y2;
                            /*$('#x').val(c.x);
                             $('#y').val(c.y);
                             $('#w').val(c.w);
                             $('#h').val(c.h);

                             $('#x2').val(c.x2);
                             $('#y2').val(c.y2);*/

                            var rx = 150 / c.w; // 150 - размер окна предварительного просмотра
                            var ry = 150 / c.h;

                            $('#preview').css({
                                width: Math.round(rx * imageWidth) + 'px',
                                height: Math.round(ry * imageHeight) + 'px',
                                marginLeft: '-' + Math.round(rx * c.x) + 'px',
                                marginTop: '-' + Math.round(ry * c.y) + 'px'
                            });
                        };
                    });

                },
                close: function (event, ui) {
                    $state.go('profile');

                    $('.preview-container').addClass('hidden');

                    $('.ui-dialog').each(function(){
                        if($(this).attr('aria-describedby') == 'dialog-message'){
                            $(this).detach();
                        }
                    });
                }
            });
        }
    })
    .controller('MapsController',function($rootScope) {
        var maps = this;

        $rootScope.currentPage = "maps";
        $rootScope.isTopSearchShow = false;
        $rootScope.base.mainContentTopIsHide = false;
        $rootScope.leftbar.tab = 0;
        $rootScope.base.pageTitle = "Карты";

        resetPages($rootScope.base);
        $rootScope.base.mapsIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.navbar.mapsBtnStatus = "active";

        $rootScope.base.mapsLoadStatus = "isLoaded";

        $rootScope.groups[0].isShow = false;
        //$rootScope.groups[1].selected = true;

        if($rootScope.currentGroup.id == $rootScope.groups[0].id){
            $rootScope.currentGroup = $rootScope.groups[1];
        }

        $rootScope.base.isFooterBottom = true;

        maps.url = userClient.getGroupMap($rootScope.currentGroup.id,MAP_COLOR);

        $rootScope.mapsChangeGroup = function(groupId){
             maps.url = userClient.getGroupMap(groupId,MAP_COLOR);
        };
    });
    /*.controller('BlogController',function($state,$rootScope) {
        var blog = this;

        blog = messageClient.getBlog();

    });*/


/* const */
var TEXT_DEFAULT_1 = "Написать сообщение";
var TEXT_DEFAULT_2 = "Ваш ответ";
var TEXT_DEFAULT_3 = "Сообщение";
var TEXT_DEFAULT_4 = "Заголовок";

var MAP_COLOR = "6FB3E040";

/* functions */

var transport = new Thrift.Transport("/thrift/MessageService");
var protocol = new Thrift.Protocol(transport);
var messageClient = new com.vmesteonline.be.messageservice.MessageServiceClient(protocol);

transport = new Thrift.Transport("/thrift/DialogService");
protocol = new Thrift.Protocol(transport);
var dialogClient = new com.vmesteonline.be.messageservice.DialogServiceClient(protocol);

transport = new Thrift.Transport("/thrift/UserService");
protocol = new Thrift.Protocol(transport);
var userClient = new com.vmesteonline.be.UserServiceClient(protocol);

var userClientGroups = userClient.getUserGroups();
var shortUserInfo = userClient.getShortUserInfo();

transport = new Thrift.Transport("/thrift/AuthService");
protocol = new Thrift.Protocol(transport);
var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

transport = new Thrift.Transport("/thrift/fs");
protocol = new Thrift.Protocol(transport);
var fileClient = new com.vmesteonline.be.FileServiceClient(protocol);

function resetPages(base){
    base.neighboursIsActive = false;
    base.privateMessagesIsActive = false;
    base.mapsIsActive = false;
    base.profileIsActive = false;
    base.settingsIsActive = false;
    base.talksIsActive = false;
    base.lentaIsActive = false;
    base.advertsIsActive = false;
}
function resetAceNavBtns(navbar){
    navbar.neighboursBtnStatus = "";
    navbar.privateMessagesBtnStatus = "";
    navbar.mapsBtnStatus = "";
}
function initProfileAva(obj){

    $('.load-avatar input').ace_file_input({
        style:'well',
        btn_choose:'Загрузить аватар',
        btn_change:null,
        no_icon:'',
        droppable:true,
        thumbnail:'large',
        icon_remove:null
    }).on('change', function(){
        obj.isMaySave = true;

        //$('.logo-container>img').hide();

        /*setTimeout(saveNewAva,1000);

        function saveNewAva(){
            //console.log($('.ace-file-input').find('.file-name img').css('background-image'));
            var imgBase64 = $('.profile .ace-file-input').find('.file-name img').css('background-image');
            var url = fileClient.saveFileContent(imgBase64,false);

            userClient.updateUserAvatar(url);
        }*/
    });

}
function initAttachImage(selector,attachAreaSelector){
    var title;

    selector.ace_file_input({
        style:'well',
        btn_choose:'Изображение',
        btn_change:null,
        no_icon:'',
        droppable:true,
        thumbnail: 'large',
        icon_remove:null,
        before_change: function(files, dropped){
            title = $(this).find('+.file-label').data('title');
            return true;
        }
    }).on('change', function(){
        attachAreaSelector.find('.loading').removeClass('hidden');

        var fileLabel = $(this).find('+.file-label'),
        type = selector[0].files[0].type;

        fileLabel.attr('data-title',title).removeClass('hide-placeholder');
        fileLabel.find('.file-name').hide();

        setTimeout(copyImage,200);

        function copyImage() {
            var copyImgSrc = fileLabel.find('.file-name img').css('background-image');

            if(copyImgSrc == 'none'){
                setTimeout(copyImage,200);
            }else {
                var url = fileClient.saveFileContent(copyImgSrc, true),
                    fileName = fileLabel.find('.file-name').attr('data-title');

                attachAreaSelector.find('.loading').addClass('hidden');

                attachAreaSelector.find('.loading').before("<span class='attach-item new-attached'>" +
                    "<a href='#' title='Не прикреплять' class='remove-attach-img'>&times;</a>" +
                    "<img data-title='" + fileName + "' data-type='" + type + "' class='attached-img' style='background-image:url(" + url + ")'></span>");

                $('.new-attached .remove-attach-img').click(function (e) {
                    e.preventDefault();
                    $(this).closest('.attach-item').hide().detach();
                    fileClient.deleteFile(url);
                });

                $('.new-attached').removeClass('new-attached');
            }
        }

    });
}

var docsBase64 = [],
    docsInd = [];
function initAttachDoc(selector,attachAreaSelector){
    var title;
        docsBase64[attachAreaSelector] = [];
        docsInd[attachAreaSelector] = 0;

    selector.ace_file_input({
        style:'well',
        btn_choose:'Документ',
        btn_change:null,
        no_icon:'',
        droppable:true,
        thumbnail: false,
        icon_remove:null,
        before_change: function(files, dropped){
            title = $(this).find('+.file-label').data('title');
            return true;
        }
    }).on('change', function(){
        var fileLabel = $(this).find('+.file-label');
        fileLabel.attr('data-title',title).removeClass('hide-placeholder');
        fileLabel.find('.file-name').hide();

        setTimeout(insertDoc,200);
        //var input = selector.clone();

        function insertDoc() {
            var docName = fileLabel.find('.file-name').attr('data-title');

            var reader = new FileReader();
            reader.readAsBinaryString(selector[0].files[0]);
            var dataType = selector[0].files[0].type;

            reader.onload = function(e){
                docsBase64[attachAreaSelector][docsInd[attachAreaSelector]] = new com.vmesteonline.be.messageservice.Attach();
                docsBase64[attachAreaSelector][docsInd[attachAreaSelector]].fileName = docName;
                docsBase64[attachAreaSelector][docsInd[attachAreaSelector]].contentType = dataType;
                var url = docsBase64[attachAreaSelector][docsInd[attachAreaSelector]].URL = fileClient.saveFileContent(base64encode(reader.result));
                docsInd[attachAreaSelector]++;

                attachAreaSelector.append("<span class='attach-item new-attached' data-fakepath='"+ docName +"'>" +
                    "<a href='#' title='Не прикреплять' class='remove-attach-img'>&times;</a>" +
                    docName+
                    "</span>");

                $('.new-attached .remove-attach-img').click(function(e){
                    e.preventDefault();
                    var attachItem = $(this).closest('.attach-item');
                    var ind = attachItem.index();
                    attachItem.hide().detach();
                    docsBase64[attachAreaSelector].splice(ind,1);
                    fileClient.deleteFile(url);
                });

                $('.new-attached').removeClass('new-attached');
            };


        }
    });
}
function selectGroupInDropdown(groupId){
    var groupsLength = userClientGroups.length,
        selectedGroup;
    for(var i = 0; i < groupsLength; i++){
        if(groupId == userClientGroups[i].id){
            selectedGroup = userClientGroups[i];
        }
    }
    return selectedGroup;
}
function getTiming(messageObjDate){
    var minute = 60*1000,
        hour = minute*60,
        day = hour*24,
        threeDays = day* 3,
        now = Date.parse(new Date()),
        timing = (now - messageObjDate*1000),
        timeTemp;

    if(timing < minute){
        timing = "только что";
    }else if(timing < hour){
        timing = new Date(timing);
        timing = timing.getMinutes()+" мин назад";
    }else if(timing < day){
        timing = new Date(timing);
        timeTemp = timing.getHours();
        if(timeTemp == 1 || timeTemp == 0){
            timing = "1 час назад";
        }else if(timeTemp > 1 && timeTemp < 5){
            timing = timeTemp + " часа назад";
        }else{
            timing = timeTemp + " часов назад";
        }
    }else if(timing < threeDays){
        timing = new Date(timing);
        timeTemp = timing.getDate();
        if(timeTemp == 1){
            timing = timeTemp+" день назад";
        }else{
            timing = timeTemp+" дней назад";
        }
    }else{
        timeTemp = new Date(messageObjDate*1000).toLocaleDateString();
        var arr = timeTemp.split('.');
        if(arr[0].length == 1) arr[0] = "0"+arr[0];
        if(arr[1].length == 1) arr[1] = "0"+arr[1];
        timing = arr[0]+"."+arr[1]+"."+arr[2];
    }

    return timing;
}

function getLabel(groupsArray,groupType){
    var groupsArrayLen = groupsArray.length;
    var label="";
    for(var i = 0; i < groupsArrayLen; i++){

        if(groupsArray[i].type == groupType){
            label = groupsArray[i].visibleName;
        }
    }

    return label;
}
function getAuthorName(userInfo){
    var userInf = userInfo;
    if(!userInfo){
        userInf = shortUserInfo;
    }

    return userInf.firstName+" "+userInf.lastName;
}
function getTagColor(labelName){
    var color;
    switch(labelName){
        case "Мой район":
            color = 'label-pink';
            break;
        case "Мои соседи":
            color = 'label-success';
            break;
        case "Мой дом":
            color = 'label-yellow';
            break;
        case "Мой подъезд":
            color = 'label-purple';
            break;
        default :
            break;
    }
    return color;
}

function postTopic(obj,isWall,isAdverts){
    var messageType,
        messageContent,
        subject;
    if (isWall){
        messageType = 5; // wall
        messageContent = obj.wallMessageContent;
        obj.wallMessageContent = TEXT_DEFAULT_1;
        subject = "";
    }else{
        if(!isAdverts){
            messageType = 1; // talks
        }else{
            messageType = 6; // adverts
        }
        messageContent = obj.content;
        obj.content = TEXT_DEFAULT_3;
        subject = obj.subject;
    }
    console.log(messageContent+" "+messageType+" "+subject);

    var newTopic = new com.vmesteonline.be.messageservice.Topic();
    newTopic.message = new com.vmesteonline.be.messageservice.Message();
    newTopic.message.groupId = obj.selectedGroup.id;
    newTopic.message.type = messageType;
    newTopic.message.content = messageContent;
    newTopic.message.images = obj.attachedImages;
    newTopic.message.documents = obj.attachedDocs;
    newTopic.message.id = 0;
    newTopic.message.created = Date.parse(new Date())/1000;

    newTopic.subject = subject;
    newTopic.id = 0;
    newTopic.metaType = "message";
    newTopic.messageNum = 0;

    var poll;
    if(obj.isPollShow){
        poll = new com.vmesteonline.be.messageservice.Poll();
        poll.pollId = 0;
        poll.editNames = [];
        poll.names = [];
        poll.subject = obj.pollSubject;
        poll.alreadyPoll = false;
        var pollInputsLength = obj.pollInputs.length;
        for(var i = 0; i < pollInputsLength; i++){
            if(obj.pollInputs[i].name != "") {
                poll.names[i] = obj.pollInputs[i].name;
                poll.editNames[i] = {
                    id: i,
                    name: obj.pollInputs[i].name
                }
            }
        }

        newTopic.poll = poll;
        newTopic.metaType = "poll";
    }

    var tempTopic = messageClient.postTopic(newTopic);
    newTopic.id = tempTopic.id;
    newTopic.message.images = tempTopic.message.images;
    newTopic.message.documents = tempTopic.message.documents;
    newTopic.userInfo = tempTopic.userInfo;

    if(obj.isPollShow){
        newTopic.poll.pollId = tempTopic.poll.pollId;
        obj.isPollShow = false;
        obj.pollSubject= "";
        obj.isPollAvailable = true;
    }
    if (isWall) {
        newTopic.message.createdEdit = getTiming(newTopic.message.created);
    }else{
        newTopic.lastUpdateEdit = getTiming(newTopic.message.created);
    }

    console.log(messageContent+" "+messageType+" "+subject);
    console.log("---");
    console.log(newTopic.message.content);

    return newTopic;

}

function postMessage(obj,isWall,isFirstLevel){
    var message =  new com.vmesteonline.be.messageservice.Message(),
        attachId, isEmptyText = false;

    if(isWall){
        message.type = com.vmesteonline.be.messageservice.MessageType.WALL;//5;
        attachId = message.topicId = obj.topic.id;
        message.groupId = obj.groupId;
        message.content = obj.commentText;
        message.parentId = 0;
        isEmptyText = (obj.commentText == TEXT_DEFAULT_2 || obj.commentText == "");
    }else{
        message.type = com.vmesteonline.be.messageservice.MessageType.BASE;//1;
        attachId = message.topicId = obj.topicId;
        message.groupId = obj.selectedGroup.id;

        if(isFirstLevel) {
            message.content = obj.answerFirstMessage;
            message.parentId = 0;
        }else{
            message.content = obj.answerMessage;
            message.parentId = obj.parentId;
            attachId = attachId +"-"+ obj.messageId;
        }

        isEmptyText = (message.content == TEXT_DEFAULT_2 || message.content == "" || message.content === undefined);
    }

    message.id = 0;
    message.images = getAttachedImages($('#attach-area-'+attachId));
    message.documents = getAttachedDocs($('#attach-doc-area-'+attachId));
    /*for(var p in message.documents[0]){
        alert(p+" "+message.documents[0][p]);
    }*/
    cleanAttached($('#attach-area-'+ attachId));
    cleanAttached($('#attach-doc-area-'+ attachId));
    //message.images = obj.attachedImages;
    message.created = Date.parse(new Date)/1000;

    if(isEmptyText && message.images.length == 0 && (message.documents === undefined || message.documents.length == 0)){

        return 0;

    }else {
        if ( message.content == TEXT_DEFAULT_2 && (message.images.length != 0 || message.documents.length != 0)) {
            message.content = "";
        }
        var newMessage = messageClient.postMessage(message);

        obj.commentText = "Ваш ответ";
        message.createdEdit = getTiming(newMessage.created);
        console.log(newMessage.created);
        message.authorName = getAuthorName();
        message.userInfo = newMessage.userInfo;
        message.images = newMessage.images;
        message.documents = newMessage.documents;
        message.id = newMessage.id;

        return message;
    }
}

function setPollEditNames(poll){
    // obj.wallItems[i].topic
    poll.editNames = [];
    var namesLength,
        amount = 0,
        votersNum = 0,
        votersPercent = 0;
    poll.names ?
        namesLength = poll.names.length:
        namesLength = 0;
    //console.log(poll.alreadyPoll);

    // нужно знать полный amount для вычисления процентной длины
    for(var j = 0; j < namesLength; j++){
        if(poll && poll.values && poll.values[j]) {
            amount += poll.values[j];
        }
    }

    for(var j = 0; j < namesLength; j++){
        if(poll && poll.values && poll.values[j]) {
            votersNum = poll.values[j];
            votersPercent = votersNum*100/amount;
        }else{
            votersNum = votersPercent = 0;
        }

        poll.editNames[j] = {
            id : j,
            value: 0,
            name : poll.names[j],
            votersNum : votersNum,
            votersPercent: votersPercent+"%"
        };

    }
    poll.amount = amount;
}

function getAttachedImages(selector){
    var imgList = [], ind = 0;

    selector.find('.attach-item img').each(function(){
        var bgImg = $(this).css('background-image'),
            name = $(this).attr('data-title'),
            type = $(this).attr('data-type'),
            result,content;

        var i = bgImg.indexOf('base64,');
        content = bgImg.slice(4,bgImg.length-1);

        result = new com.vmesteonline.be.messageservice.Attach();
        result.fileName = name;
        result.contentType = type;
        result.URL = content;
        //console.log(content);
        //result = 'obj(name:'+ base64encode(name) +';data:'+ type +';content:'+content+")";

        imgList[ind++] = result;
    });

    return imgList;
}
function getAttachedDocs(selector){
    /*var docsBase64Length = docsBase64[selector].length;

    for(var i = 0; i < docsBase64Length; i++){
        docList
    }
    var docList = [], ind = 0;

    selector.find('.attach-item').each(function(){
        docList[ind++] = $(this).attr('data-fakepath');
    });*/
    //alert(selector+" "+docsBase64[selector]);

    return docsBase64[selector];
}
function cleanAttached(selector){
    //selector.html('').append('<div class="loading hidden"><img src="i/loading2.gif"></div>');
    selector.find('.attach-item').detach();
    //docsBase64 = [];
    docsInd[selector] = 0;
    docsBase64[selector] = [];
}

function initFancyBox(selector){
    selector.find(".fancybox").fancybox();
}
function clone(obj){
    if(obj == null || typeof(obj) != 'object')
        return obj;
    var temp = new obj.constructor();
    for(var key in obj)
        temp[key] = clone(obj[key]);
    return temp;
}
function base64encode(str) {
    // Символы для base64-преобразования
    var b64chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefg'+
        'hijklmnopqrstuvwxyz0123456789+/=';
    var b64encoded = '';
    var chr1, chr2, chr3;
    var enc1, enc2, enc3, enc4;

    for (var i=0; i<str.length;) {
        chr1 = str.charCodeAt(i++);
        chr2 = str.charCodeAt(i++);
        chr3 = str.charCodeAt(i++);

        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);

        enc3 = isNaN(chr2) ? 64:(((chr2 & 15) << 2) | (chr3 >> 6));
        enc4 = isNaN(chr3) ? 64:(chr3 & 63);

        b64encoded += b64chars.charAt(enc1) + b64chars.charAt(enc2) +
            b64chars.charAt(enc3) + b64chars.charAt(enc4);
    }
    return b64encoded;
}