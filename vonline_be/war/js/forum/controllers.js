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

        base.mainContentTopIsHide = false;
        base.createTopicIsHide = true;
        base.me = shortUserInfo;

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
            /*for(var p in event.target){
             console.log(p+" "+event[p]);
             };*/

            /*console.log(event.target.clientHeight);
            console.log(event.target.scrollHeight);
            console.log(event.target.scrollTop);
            console.log(event.target.value);
            console.log(event.target.textLength);*/

            var clientHeight = event.target.clientHeight,
                scrollHeight = event.target.scrollHeight,
                textLength = event.target.textLength,
                clientWidth = event.target.clientWidth,
                textLengthPX, newHeight,removeRowCount,
                defaultHeight;

            if(textareaType == 1){
                defaultHeight = 90;
            }else if(textareaType == 2){
                defaultHeight = 44;
            }

            /*
            Исходные данные:
                На один символ приходится ~8px в ширину
                Высота строки текста ~14px

            * Здесь выполняем такие действия :
             * 1) Считаем длину текста в пикселях
             * 2) Определяем целое количестов строк, которые удалили
             * 3) Определям новую высоту с учетом высоты удаленного текста
            * */
            if(scrollHeight > clientHeight){
                event.target.style.height = scrollHeight+'px';
            }else if(scrollHeight > defaultHeight){
                //console.log('2 '+base.oldTextLength);
                textLengthPX = (parseInt(base.oldTextLength) - textLength) * 8; // 1
                //console.log(textLengthPX);
                if (textLengthPX > clientWidth){
                    //console.log('3');
                    removeRowCount = Math.floor(textLengthPX/clientWidth); // 2
                    //console.log(k);
                    //console.log(event.target.style.height);
                    newHeight = parseInt(event.target.style.height) - removeRowCount*14; // 3
                    //console.log(newHeight);
                    newHeight > defaultHeight ? event.target.style.height = newHeight+"px":
                                    event.target.style.height = defaultHeight+'px';

                    //console.log(event.target.style.height);
                }
            }else{
                event.target.style.height = defaultHeight+'px';
            }
            base.oldTextLength = textLength;
        };

        base.pageTitle = "Новости";

        $rootScope.base = base;
        $rootScope.currentPage = 'lenta';

        $rootScope.leftbar = {};
    })
  .controller('navbarController', function($rootScope) {
        this.privateMessagesBtnStatus = "";
        this.neighboursBtnStatus = "";
        $rootScope.navbar = this;

        this.logout = function(event){
            event.preventDefault();

            authClient.logout();

            document.location.replace("login.html");

        }

  })
  .controller('leftBarController',function($rootScope) {

    //$rootScope.leftbar = this;

    //$rootScope.leftbar.tab = 1;

    $rootScope.setTab = function(newValue){

        $rootScope.leftbar.tab = newValue;
        $rootScope.isTopSearchShow = true;
        //var tempTalksBool = false;
        //if($rootScope.base.talksIsActive) tempTalksBool = true;
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

                //if(!tempTalksBool)
                $rootScope.base.isTalkTitles = true;

                $rootScope.base.talksIsActive = true;
                $rootScope.currentPage = 'talks';
                $rootScope.base.pageTitle = "Обсуждения";
                break;
            case 3:
                $rootScope.base.servicesIsActive = true;
                $rootScope.currentPage = 'services';
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
    .controller('rightBarController',function() {
    })
    .controller('mainContentTopController',function($rootScope) {
        var topCtrl = this;

        topCtrl.groups = userClientGroups.reverse();// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        var groups = topCtrl.groups,
            groupsLength = groups.length;
        groups[0].selected = true;
        $rootScope.currentGroup = $rootScope.biggestGroup = groups[0];

        topCtrl.isSet = function(groupId){
            //return groupId ===
        };

        topCtrl.selectGroup = function(group){
            var groupId;

            for(var i = 0; i < groupsLength; i++){
                groups[i].selected = false;
            }

            /*if(isAllBtn){
                topCtrl.allGroupsBtn.selected = true;
                groupId = 0;
            }else{*/
                //topCtrl.allGroupsBtn.selected = false;
                group.selected = true;
                groupId = group.id;
            //}

            //$rootScope.currentMessages = messageClient.getWallItems(groupId);
            $rootScope.currentGroup = group;
            if($rootScope.currentPage == 'lenta'){
                $rootScope.wallChangeGroup(group.id);
            }else if($rootScope.currentPage == 'talks'){
                $rootScope.talksChangeGroup(group.id);
            }

        };

        topCtrl.showCreateTopic = function(event){
            event.preventDefault();

            $rootScope.base.createTopicIsHide ? $rootScope.base.createTopicIsHide = false : $rootScope.base.createTopicIsHide = true;

        };
    })
    .controller('LentaController',function($rootScope) {
        $rootScope.setTab(1);
        initAttachImage($('#attachImage-0'),$('#attach-area-0')); // для ленты новостей
        initAttachDoc($('#attachDoc-0'),$('#attach-doc-area-0'));
        initFancyBox($('.forum'));

        var lenta = this;
        lenta.groups = userClientGroups.reverse();// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        lenta.selectedGroup = lenta.selectedGroupInTop = $rootScope.currentGroup;
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

        lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);
        /*console.log("1 "+lenta.wallItems.length);
        for(var i = 0; i < lenta.wallItems.length; i++){
            if(lenta.wallItems[i].topic.message.images) {
                console.log("llll " + lenta.wallItems[i].topic.message.images.length);
            }
        }*/

        var wallItemsLength;
        lenta.wallItems ? wallItemsLength = lenta.wallItems.length :
            wallItemsLength = 0;

        initWallItem();

        lenta.selectGroupInDropdown = selectGroupInDropdown;

        lenta.goToAnswerInput = function(event){
            event.preventDefault();

        };

        lenta.createWallMessage = function(event){
            event.preventDefault();

            lenta.attachedImages = getAttachedImages($('#attach-area-0'));
            lenta.attachedDocs = getAttachedDocs($('#attach-doc-area-0'));

            if(lenta.attachedImages.length == 0 && lenta.attachedDocs.length == 0 && !lenta.isPollShow
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
                //newWallItem.images = newTopic.message.images;
                console.log("++" + newWallItem.topic.message.content);
                newWallItem.topic.authorName = getAuthorName();
                newWallItem.messages = [];
                newWallItem.commentText = "Ваш ответ";
                newWallItem.answerShow = false;
                newWallItem.isFocus = false;
                newWallItem.label = getLabel(lenta.groups, lenta.selectedGroup.id);
                newWallItem.tagColor = getTagColor(newWallItem.label);

                cleanAttached($('#attach-area-0'));
                cleanAttached($('#attach-doc-area-0'));

                if (lenta.selectedGroupInTop.id == lenta.selectedGroup.id) {
                    lenta.wallItems ?
                        lenta.wallItems.unshift(newWallItem) :
                        lenta.wallItems[0] = newWallItem;

                    //console.log(lenta.wallItems.length);
                    /*lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);
                     initWallItem();*/
                }
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
                initFlagsArray[wallItem.topic.id] = true;
            }

        };

        $rootScope.wallChangeGroup = function(groupId){

            lenta.wallItems = messageClient.getWallItems(groupId);

            initWallItem();

        };

        function initWallItem(){
            for(var i = 0; i < wallItemsLength; i++){

                lenta.wallItems[i].commentText = "Ваш ответ";
                lenta.wallItems[i].answerShow = false;
                lenta.wallItems[i].isFocus = false;
                lenta.wallItems[i].isCreateCommentError = false;

                //  lenta.wallItems[i].topic.message.groupId сейчас не задана почему-то
                lenta.wallItems[i].label = getLabel(lenta.groups,lenta.wallItems[i].topic.message.groupId);

                lenta.wallItems[i].tagColor = getTagColor(lenta.wallItems[i].label);

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
            initFancyBox($('.talks'));

            $rootScope.base.createTopicIsHide = true;
            var talk = this;
            talk.isTalksLoaded = false;
            talk.groups = userClientGroups.reverse();

            talk.content = "Сообщение";
            talk.subject = "Заголовок";

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

            $rootScope.currentGroup = talk.selectedGroup = talk.groups[0];
            talk.topics = messageClient.getTopics(talk.selectedGroup.id, 0, 0, 0, 1000).topics;

            initTalks();

            if (!talk.topics) talk.topics = [];

            talk.selectGroupInDropdown = selectGroupInDropdown;

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

                talk.topics.unshift(newTopic);
            }

        };

        function initTalks(){
            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;

            for(var i = 0; i < topicLength;i++){
                talk.topics[i].lastUpdateEdit = getTiming(talk.topics[i].lastUpdate);
                talk.topics[i].label = getLabel(talk.groups,talk.topics[i].message.groupId);
                talk.topics[i].tagColor = getTagColor(talk.topics[i].label);
            }
        }

        $rootScope.talksChangeGroup = function(groupId){

            talk.topics = messageClient.getTopics(groupId,0,0,0,1000).topics;

            initTalks();

        };

    })
    .controller('TalksSingleController',function($rootScope,$stateParams){

        var talk = this,
            fullTalkMessagesLength,
            talkId = $stateParams.talkId;

        talk.selectedGroup = $rootScope.currentGroup;
        talk.topics = messageClient.getTopics(talk.selectedGroup.id, 0, 0, 0, 1000).topics;
        talk.fullTalkTopic = {};
        talk.fullTalkMessages = {};
        talk.fullTalkFirstMessages = [];
        talk.groups = userClientGroups.reverse();

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
                    talk.fullTalkTopic.label = getLabel(talk.groups,talk.fullTalkTopic.message.groupId);
                    talk.fullTalkTopic.tagColor = getTagColor(talk.fullTalkTopic.label);
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
    .controller('ServicesController',function() {
    })
    .controller('neighboursController',function($rootScope) {
        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.mainContentTopIsHide = false;
        $rootScope.base.neighboursIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.navbar.neighboursBtnStatus = "active";
        $rootScope.base.pageTitle = "";

        $rootScope.base.neighboursLoadStatus = "isLoaded";

        var neighbours = this;
        neighbours.neighboors = userClient.getNeighboursByGroup($rootScope.currentGroup.id);
        neighbours.neighboorsSize = neighbours.neighboors.length;

    })
    .controller('ProfileController',function($rootScope, $stateParams) {
        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.profileIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.base.profileLoadStatus = "isLoaded";

        var profile = this, userId;
        profile.isMayEdit = false;

        $("#dialog-message").addClass('hide');

        if ($stateParams.userId && $stateParams.userId != shortUserInfo.id){
            userId = $stateParams.userId;
            profile.userContacts = userClient.getUserContactsExt(userId);
        }else{
            userId = null;
            profile.isMayEdit = true;
            profile.userContacts = userClient.getUserContacts();
            //initProfileAva();
        }

        profile.userProfile = userClient.getUserProfile(userId);

        $rootScope.chageIndex = 0;


        $('#test').Jcrop({
            aspectRatio: 1,
            onChange: updateCoords,
            onSelect: updateCoords
        });

        function updateCoords(c) {
            $('#x').val(c.x);
            $('#y').val(c.y);
            $('#w').val(c.w);
            $('#h').val(c.h);
            $('#x2').val(c.x2);
            $('#y2').val(c.y2);
            var rx = 200 / c.w; // 200 - размер окна предварительного просмотра
            var ry = 200 / c.h;
            $('#preview').css({
                width: Math.round(rx * 800) + 'px',
                height: Math.round(ry * 600) + 'px',
                marginLeft: '-' + Math.round(rx * c.x) + 'px',
                marginTop: '-' + Math.round(ry * c.y) + 'px'
            });
        };



})
    .controller('SettingsController',function($rootScope,$scope) {
        $rootScope.isTopSearchShow = false;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.settingsIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.base.mainContentTopIsHide = true;

        $rootScope.base.settingsLoadStatus = "isLoaded";

        var settings = this,
            userContatcsMeta = userClient.getUserContacts(),
            userProfileMeta = userClient.getUserProfile(),
            userInfoMeta = userProfileMeta.userInfo,
            userFamilyMeta = userProfileMeta.family,
            userInterestsMeta = userProfileMeta.interests;

        settings.userContacts = clone(userContatcsMeta);
        settings.userProfile = clone(userProfileMeta);
        settings.userInfo = clone(userInfoMeta);
        settings.family = clone(userFamilyMeta);
        settings.interests = clone(userInterestsMeta);
        //settings.userInfo.canSaveBool = true;
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

        settings.updatePasswordOrUserInfo = function(){
            if (!settings.passwChange){
                userClient.updateUserInfo(settings.userInfo);
            }else{
                userClient.changePassword(settings.oldPassw, settings.newPassw);
            }
        };

        settings.updatePrivacy = function(){
            userClient.updatePrivacy();
        };

        settings.updateContacts = function(){
            userClient.updateContacts(settings.userContacts);
        };
        settings.updateFamily = function(){
            userClient.updateFamily(settings.family);
        };
        settings.updateInterests = function(){
            userClient.updateInterests(settings.interests);
        };

        settings.childAdd = function(event){
            event.preventDefault();

            var newChild = new com.vmesteonline.be.Children();
            newChild.name = " ";

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
        settings.petAdd = function(event){
            event.preventDefault();

            var newPet = new com.vmesteonline.be.Pet();
            newPet.name = " ";

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

        settings.passwChange = false;
        settings.changePassw = function(){
            settings.passwChange = true;
        };

        (settings.userInfo.birthday != 0) ?
        settings.birthday = settings.userInfo.birthday :
        settings.birthday = "";

        $('#settings-input-3').datepicker({changeMonth:true, changeYear:true,yearRange:'c-100:+c'});
        $.datepicker.setDefaults($.datepicker.regional['ru']);


    })
    .controller('dialogsController', function($rootScope,$state){
        $rootScope.isTopSearchShow = false;
        $rootScope.base.mainContentTopIsHide = true;
        $rootScope.leftbar.tab = 0;

        resetPages($rootScope.base);
        $rootScope.base.privateMessagesIsActive = true;

        resetAceNavBtns($rootScope.navbar);
        $rootScope.navbar.privateMessagesBtnStatus = "active";

        $rootScope.base.mainContentTopIsHide = true;

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
            $rootScope.currentDialog = dialogClient.getDialog(usersId);
            $state.go('dialog-single',{ dialogId : dialogId});
        };

    })
    .controller('dialogController',function($rootScope,$stateParams) {
        $rootScope.base.mainContentTopIsHide = true;
        var dialog = this;
        /*dialog.users = $rootScope.currentDialog.users;
        var dialogUsersLength = dialog.users.length;
        for(var i = 0; i < dialogUsersLength; i++){
            if (dialog.users[i].id == $rootScope.base.me.id){
                dialog.users.splice(i,1);
            }
        }*/
        initAttachImage($('#attachImage-000'),$('#attach-area-000'));

        if ($stateParams.dialogId){
            dialog.privateMessages = dialogClient.getDialogMessages($stateParams.dialogId,0);
            var privateMessagesLength = dialog.privateMessages.length;
                dialog.authors = [];
            for(var i = 0; i < privateMessagesLength; i++){
                dialog.privateMessages[i].authorProfile = userClient.getUserProfile(dialog.privateMessages[i].author);
            }
        }

        dialog.messageText = TEXT_DEFAULT_1;
        dialog.sendMessage = function(){
            if(dialog.messageText != TEXT_DEFAULT_1 && dialog.messageText != ""){

                var newDialogMessage = new com.vmesteonline.be.messageservice.DialogMessage();
                newDialogMessage.content = dialog.messageText;
                newDialogMessage.author = $rootScope.base.me.id;

                newDialogMessage.created = Date.parse(new Date())/1000;
                newDialogMessage.authorProfile = userClient.getUserProfile(newDialogMessage.author);

                dialogClient.postMessage($stateParams.dialogId, dialog.messageText);
                dialog.privateMessages.push(newDialogMessage);

                dialog.messageText = TEXT_DEFAULT_1;
                cleanAttached($('#attach-area-000'));
                cleanAttached($('#attach-doc-area-000'));
            }
        }

    })
    .controller('writeMessageController',function($rootScope,$state) {
        $rootScope.base.mainContentTopIsHide = true;
        var writeMessage = this, dialog;

        writeMessage.newMessage = {};
        writeMessage.users = [];
        writeMessage.neighboors = userClient.getNeighbours();
        writeMessage.newMessage.text = "Сообщение";
        writeMessage.dialogId = 0;
        writeMessage.isError = false;

        function usersToInt(users){
            var usersLength = users.length,
                usersInt = [];
            for(var i = 0; i < usersLength; i++){
                usersInt[i] = parseInt(writeMessage.users[i]);
            }

            //alert(usersInt[0] + " "+typeof(usersInt[0]));

            return usersInt;
        }

        writeMessage.change = function(){
            $rootScope.currentDialog = dialog = dialogClient.getDialog(usersToInt(writeMessage.users),0);
            writeMessage.dialogId = dialog.id;
        };

        initAttachImage($('#attachImage-0000'),$('#attach-area-0000'));
        //initAttachdocs($('#attachImage-0000'),$('#attach-area-0000'));

        //var dialog = dialogClient.getDialog(userId);
        //writeMessage.dialogId = dialog.id;

        writeMessage.send = function(){
            if(writeMessage.newMessage.text == TEXT_DEFAULT_3){
                writeMessage.isError = true;
                writeMessage.errorText = "Вы не ввели сообщение";
            }else if(!dialog || dialog.id == 0){
                writeMessage.isError = true;
                writeMessage.errorText = "Вы не указали получателя";
            }else{
                dialogClient.postMessage(dialog.id, writeMessage.newMessage.text);
                cleanAttached($('#attach-area-0000'));
                $state.go('dialog-single',{ 'dialogId' : dialog.id});
            }
        }

    })
    .controller('changeAvatarController',function($state,$rootScope){
        var changeAvatar = this;

        changeAvatar.save = function(){

            dialog.dialog('close');
            $state.go('profile');
        };

        var dialog = $("#dialog-message").removeClass('hide').dialog({
            modal: true,
            width: 500,
            position: ['center',100],
            title_html: false,
            closeText: "",
            create: function(event,ui){

                $('.load-avatar input').ace_file_input({
                    style:'well',
                    btn_choose:'Загрузить аватар',
                    btn_change:null,
                    no_icon:'',
                    droppable:true,
                    thumbnail:'large',
                    icon_remove:null
                }).on('change', function(){

                    $('.loadAvatar-area').addClass('hidden');
                    $('.crop-area').removeClass('hidden');

                    setTimeout(saveNewAva,1000);

                     function saveNewAva(){

                     /*var imgBase64 = $('.profile .ace-file-input').find('.file-name img').css('background-image');
                     var url = fileClient.saveFileContent(imgBase64,false);
                     userClient.updateUserAvatar(url);*/

                         var bg = $('.load-avatar').find('.file-label img').css('background-image'),
                             src = $('.load-avatar').find('.file-label img').attr('src');

                         $('#image-for-crop').css('background-image',bg).attr('src',src);
                         //alert(fileClient.saveFileContent(bg,true));
                         $('#image-for-crop').Jcrop({
                             aspectRatio: 0,
                             onChange: updateCoords,
                             onSelect: updateCoords
                         });

                     }

                    function updateCoords(c) {
                        $('#x').val(c.x);
                        $('#y').val(c.y);
                        $('#w').val(c.w);
                        $('#h').val(c.h);

                        $('#x2').val(c.x2);
                        $('#y2').val(c.y2);


                        var rx = 200 / c.w; // 200 - размер окна предварительного просмотра
                        var ry = 200 / c.h;

                        $('#preview').css({
                            width: Math.round(rx * 800) + 'px',
                            height: Math.round(ry * 600) + 'px',
                            marginLeft: '-' + Math.round(rx * c.x) + 'px',
                            marginTop: '-' + Math.round(ry * c.y) + 'px'
                        });
                    };


                });

            },
            close: function(event, ui){
                $state.go('profile');
            }
        });
    });

/* const */
var TEXT_DEFAULT_1 = "Написать сообщение";
var TEXT_DEFAULT_2 = "Ваш ответ";
var TEXT_DEFAULT_3 = "Сообщение";
var TEXT_DEFAULT_4 = "Заголовок";

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
    base.profileIsActive = false;
    base.settingsIsActive = false;
    base.talksIsActive = false;
    base.lentaIsActive = false;
    base.servicesIsActive = false;
}
function resetAceNavBtns(navbar){
    navbar.neighboursBtnStatus = "";
    navbar.privateMessagesBtnStatus = "";
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
        var fileLabel = $(this).find('+.file-label'),
        type = selector[0].files[0].type;

        fileLabel.attr('data-title',title).removeClass('hide-placeholder');
        fileLabel.find('.file-name').hide();

        setTimeout(copyImage,200);

        function copyImage() {
            var copyImgSrc = fileLabel.find('.file-name img').css('background-image'),
                fileName = fileLabel.find('.file-name').attr('data-title');

            //$('.attach-area')
            attachAreaSelector.append("<span class='attach-item new-attached'>" +
                "<a href='#' title='Не прикреплять' class='remove-attach-img'>&times;</a>" +
                "<img data-title='"+ fileName +"' data-type='"+ type +"' class='attached-img' style='background-image:"+ copyImgSrc +"'></span>");

            $('.new-attached .remove-attach-img').click(function(e){
                e.preventDefault();
               $(this).closest('.attach-item').hide().detach();
            });

            $('.new-attached').removeClass('new-attached');
        }

    });
}

var docsBase64 = [];
function initAttachDoc(selector,attachAreaSelector){
    var title,ind = 0;
        docsBase64[attachAreaSelector] = [];

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
                docsBase64[attachAreaSelector][ind++] = "obj(name:"+ base64encode(docName)
                    +";data:"+ dataType +";content:"+base64encode(reader.result)+")";
                //alert(docsBase64[attachAreaSelector][ind-1]);
            };

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
            });

            $('.new-attached').removeClass('new-attached');
        }
    });
}
function selectGroupInDropdown(groupId,objCtrl){
    var groupsLength = objCtrl.groups.length;
    for(var i = 0; i < groupsLength; i++){
        if(groupId == objCtrl.groups[i].id){
            objCtrl.selectedGroup = objCtrl.groups[i];
        }
    }
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

function getLabel(groupsArray,groupId){
    var groupsArrayLen = groupsArray.length;
    var label="";
    for(var i = 0; i < groupsArrayLen; i++){

        if(groupsArray[i].id == groupId){
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
        case "Парадная 1":
            color = 'label-purple';
            break;
        default :
            break;
    }
    return color;
}

function postTopic(obj,isWall){
    var messageType,
        messageContent,
        subject;
    if (isWall){
        messageType = 5;
        messageContent = obj.wallMessageContent;
        obj.wallMessageContent = TEXT_DEFAULT_1;
        subject = "";
    }else{
        messageType = 1;
        messageContent = obj.content;
        obj.content = "Сообщение";
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
            poll.names[i] = obj.pollInputs[i].name;
            poll.editNames[i] = {
                id: i,
                name: obj.pollInputs[i].name
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
    //alert(message.documents[0]);
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

    selector.find('img').each(function(){
        var bgImg = $(this).css('background-image'),
            name = $(this).attr('data-title'),
            type = $(this).attr('data-type'),
            result;

        var i = bgImg.indexOf('base64,');
        result = bgImg.slice(i+7);

        result = 'obj(name:'+ base64encode(name) +';data:'+ type +';content:'+result+")";

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

    return docsBase64[selector];
}
function cleanAttached(selector){
    selector.html('');
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