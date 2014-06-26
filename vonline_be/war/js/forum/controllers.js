'use strict';

/* Controllers */
angular.module('forum.controllers', [])
    .controller('baseController',function($rootScope) {
        var base = this;
        base.nextdoorsLoadStatus = "";
        base.privateMessagesLoadStatus = "";
        base.profileLoadStatus = "";

        base.mainContentTopIsHide = false;
        base.createTopicIsHide = true;

        base.isTalkTitles = true;

        resetPages(base);
        base.lentaIsActive = true;

        $rootScope.base = base;
        $rootScope.currentPage = 'lenta';
    })
  .controller('navbarController', function($rootScope) {
        this.privateMessagesBtnStatus = "";
        this.nextdoorsBtnStatus = "";
        var navbar = $rootScope.navbar = this;

        this.goToNextdoors = function(event){
            event.preventDefault();

            //resetLeftBar($rootScope.leftbar);
            $rootScope.leftbar.tab = 0;

            resetPages($rootScope.base);
            $rootScope.base.nextdoorsIsActive = true;

            resetAceNavBtns(navbar);
            navbar.nextdoorsBtnStatus = "active";
            $rootScope.base.mainContentTopIsHide = true;

            var nextdoors = $('.dynamic .nextdoors');

            if ($rootScope.base.nextdoorsLoadStatus == "") {
                nextdoors.load('ajax/forum/nextdoors.jsp .nextdoors',function(){
                    //initNextdoors();
                });
            }

            $rootScope.base.nextdoorsLoadStatus = "isLoaded";

        };

        this.goToPrivateMessages = function(event){
                event.preventDefault();

                $rootScope.leftbar.tab = 0;

                resetPages($rootScope.base);
                $rootScope.base.privateMessagesIsActive = true;

                resetAceNavBtns(navbar);
                navbar.privateMessagesBtnStatus = "active";
                $rootScope.base.mainContentTopIsHide = true;

                var privateMessages = $('.dynamic .private-messages');

                if ($rootScope.base.privateMessagesLoadStatus == "") {
                    privateMessages.load('ajax/forum/private-messages.jsp .private-messages',function(){
                        //initNextdoors();
                    });
                }

                $rootScope.base.privateMessagesLoadStatus = "isLoaded";

            };

        this.goToProfile = function(event){
            event.preventDefault();

            $rootScope.leftbar.tab = 0;

            resetPages($rootScope.base);
            $rootScope.base.profileIsActive = true;

            resetAceNavBtns(navbar);
            $rootScope.base.mainContentTopIsHide = true;

            var profile = $('.dynamic .profile');

            if ($rootScope.base.profileLoadStatus == "") {
                profile.load('ajax/forum/profile.jsp .profile',function(){
                    initProfile();
                });
            }

            $rootScope.base.profileLoadStatus = "isLoaded";

        };

        this.logout = function(event){
            event.preventDefault();

            authClient.logout();

            document.location.replace("login.html");

        }

  })
  .controller('leftBarController',function($rootScope) {

    $rootScope.leftbar = this;

    $rootScope.leftbar.tab = 1;

    $rootScope.setTab = function(event,newValue){
        event.preventDefault();

        $rootScope.leftbar.tab = newValue;
        resetPages($rootScope.base);
        resetAceNavBtns($rootScope.navbar);

        switch(newValue){
            case 1:

                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.lentaIsActive = true;
                $rootScope.currentPage = 'lenta';
                break;
            case 2:
                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.talksIsActive = true;
                $rootScope.base.isTalkTitles = true;
                $rootScope.currentPage = 'talks';
                break;
            case 3:
                $rootScope.base.servicesIsActive = true;
                $rootScope.currentPage = 'services';
                break;
            default :
                break;
        }

    };

    this.isSet = function(number){
        return this.tab === number;
    };
  })
    .controller('rightBarController',function() {
    })
    .controller('mainContentTopController',function($rootScope) {
        var topCtrl = this;

        topCtrl.groups = userClientGroups.reverse();// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        var groups = topCtrl.groups,
            groupsLength = groups.length;
       // topCtrl.allGroupsBtn = {};
        //topCtrl.allGroupsBtn.selected = true;
        groups[0].selected = true;
        $rootScope.currentGroup = groups[0];

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

        /*this.createTopicIsHide = true;
        $rootScope.createTopicIsHide = this.createTopicIsHide;
        var mainContentTop = this;*/

        topCtrl.showCreateTopic = function(event){
            event.preventDefault();

            $rootScope.base.createTopicIsHide ? $rootScope.base.createTopicIsHide = false : $rootScope.base.createTopicIsHide = true;

        };
    })
    .controller('LentaController',function($rootScope) {
        var lenta = this;
        lenta.groups = userClientGroups.reverse();// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        lenta.selectedGroup = lenta.selectedGroupInTop = $rootScope.currentGroup;
        lenta.isPollShow = false;
        //lenta.pollInputs = [0,1];
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

        lenta.wallMessageContent = "Написать сообщение";

        lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);
        //console.log("1 "+lenta.wallItems.length);

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

            //console.log(lenta.selectedGroup.id+" "+lenta.wallMessageContent);
            var newWallMessage = new com.vmesteonline.be.messageservice.Topic;
            newWallMessage.message = new com.vmesteonline.be.messageservice.Message;
            newWallMessage.message.groupId = lenta.selectedGroup.id;
            newWallMessage.message.type = 5;
            newWallMessage.message.content = lenta.wallMessageContent;
            newWallMessage.message.id = 0;
            newWallMessage.message.created = Date.parse(new Date());

            newWallMessage.subject = "";
            newWallMessage.id = 0;

            var poll;
            if(lenta.isPollShow){
                poll = new com.vmesteonline.be.messageservice.Poll;
                poll.id = 0;
                poll.names = [];
                var pollInputsLength = lenta.pollInputs.length;
                for(var i = 0; i < pollInputsLength; i++){
                    poll.names[i] = lenta.pollInputs[i].name;
                }
                newWallMessage.poll = poll;
                //messageClient.createPoll(poll);
                newWallMessage.metaType = "poll";
            }

            messageClient.postTopic(newWallMessage);

            //var newWallMessage = messageClient.createTopic(lenta.selectedGroup.id," 1",5,lenta.wallMessageContent);
            lenta.wallMessageContent = "Написать сообщение";
            newWallMessage.message.createdEdit = getTiming(newWallMessage.message.created);
            var newWallItem = new com.vmesteonline.be.messageservice.WallItem;
            newWallItem.topic = newWallMessage;
            newWallItem.topic.authorName = getAuthorName();
            newWallItem.messages = [];
            newWallItem.commentText = "Ваш ответ";
            newWallItem.answerShow = false;
            newWallItem.isFocus = false;
            newWallItem.label = getLabel(lenta.groups,lenta.selectedGroup.id);
            newWallItem.tagColor = getTagColor(newWallItem.label);

            if(lenta.selectedGroupInTop.id == lenta.selectedGroup.id){
                lenta.wallItems ?
                    lenta.wallItems.unshift(newWallItem):
                    lenta.wallItems[0] = newWallItem;

                //console.log(lenta.wallItems.length);
                /*lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);
                initWallItem();*/
            }
        };

        lenta.createWallComment = function(event,wallItem){
            event.preventDefault();

            var newWallComment = messageClient.createMessage(wallItem.topic.id,0,lenta.selectedGroupInTop.id,5,wallItem.commentText);
            wallItem.commentText = "Ваш ответ";
            newWallComment.createdEdit = getTiming(newWallComment.created);
            newWallComment.authorName = getAuthorName(newWallComment.authorId);

            //console.log(lenta.wallItems+" "+lenta.wallItems.topic);
            if(wallItem.messages){
                wallItem.messages.push(newWallComment);
            }else{
                wallItem.messages = [];
                wallItem.messages[0] = newWallComment;
            }


        };

        lenta.showAnswerInput = function(event,wallItem,wallMessage){
            event.preventDefault();

            /*wallItem.answerShow ?
                wallItem.answerShow = false :*/
                wallItem.answerShow = true ;
                wallItem.isFocus = true ;

            if(wallMessage){
                var authorName = userClient.getUserInfoExt(wallMessage.authorId).firstName;
                wallItem.commentText = authorName+", ";
            }else{
                wallItem.commentText = "";
            }

        };

        lenta.addPollInput = function(event){
            event.preventDefault();

            var newInput = {counter : 0, name:"" };
            lenta.pollInputs.push(newInput);

        };

        lenta.showPoll = function(event){
            event.preventDefault();

            lenta.isPollShow = true;
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
        };

        lenta.createPoll = function(event,poll){
            event.preventDefault();
            //wallItem.topic.poll.choose
            poll.values = [];
            var pollNamesLength = poll.names.length;
            for(var i = 0; i < pollNamesLength; i++){
                i == wallItem.topic.poll.choose ?
                    poll.values[i] = 1 :
                    poll.values[i] = 0 ;
            }

            messageClient.createPoll(poll);
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
                console.log("--"+lenta.wallItems[i].topic.poll);


                //  lenta.wallItems[i].topic.message.groupId сейчас не задана почему-то
                lenta.wallItems[i].label = getLabel(lenta.groups,lenta.wallItems[i].topic.message.groupId);

                lenta.wallItems[i].tagColor = getTagColor(lenta.wallItems[i].label);

                if(lenta.wallItems[i].topic.message.type == 1){

                    lenta.wallItems[i].topic.lastUpdateEdit = getTiming(lenta.wallItems[i].topic.lastUpdate);

                }else if(lenta.wallItems[i].topic.message.type == 5){

                    lenta.wallItems[i].topic.message.createdEdit = getTiming(lenta.wallItems[i].topic.message.created);
                    lenta.wallItems[i].topic.authorName = getAuthorName(lenta.wallItems[i].topic.message.authorId);
                    lenta.wallItems[i].topic.metaType = "message";

                    var mesLen;
                    lenta.wallItems[i].messages ?
                        mesLen = lenta.wallItems[i].messages.length:
                        mesLen = 0;

                    for(var j = 0; j < mesLen; j++){
                        lenta.wallItems[i].messages[j].createdEdit = getTiming(lenta.wallItems[i].messages[j].created);
                        lenta.wallItems[i].messages[j].authorName = getAuthorName(lenta.wallItems[i].messages[j].authorId);
                    }


                    if(lenta.wallItems[i].topic.poll != null){
                        //значит это опрос
                        lenta.wallItems[i].topic.metaType = "poll";
                        //lenta.wallItems[i].pollNames = lenta.wallItems[i].topic.poll.names;
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
        var talk = this;
        talk.isTalksLoaded = false;
        talk.groups = userClientGroups.reverse();// ? userClientGroups.reverse() : userClient.getUserGroups().reverse();

        talk.content = "Сообщение";
        talk.subject = "Заголовок";

        talk.isPollShow = false;
        talk.pollInputs = [
            {
                counter : 0,
                name:""
            },
            {
                counter : 1,
                name:""
            }
        ];

        talk.fullTalkTopic = {};
        talk.fullTalkTopic.answerInputIsShow = false;
        talk.fullTalkMessages = [];
        talk.fullTalkFirstMessages = [];
        talk.answerFirstMessage = "Ваш ответ";
        var fullTalkFirstMessagesLength,
            fullTalkMessagesLength,
            talkId;

        var groups = this.groups,
            groupsLength = groups.length;
        talk.selectedGroup = talk.groups[0];
        talk.topics = messageClient.getTopics(talk.selectedGroup.id,0,0,0,1000).topics;

        initTalks();

        if(!talk.topics) talk.topics = [];

        $rootScope.showFullTalk = function(event,talkOutside){
            //event.preventDefault();

            $rootScope.setTab(event,2);

            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;
            //talk.fullTalkTopic = talkOutside;

            talkId = talkOutside.id;
            for(var i = 0; i < topicLength; i++){
                if(talkId == talk.topics[i].id){
                    talk.fullTalkTopic = talk.topics[i];
                    talk.fullTalkTopic.message.createdEdit = getTiming(talk.fullTalkTopic.message.created);
                }
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
        };

        talk.selectGroupInDropdown = selectGroupInDropdown;

        talk.addSingleTalk = function(){
            var newWallMessage = new com.vmesteonline.be.messageservice.Topic;
            newWallMessage.message = new com.vmesteonline.be.messageservice.Message;
            newWallMessage.message.groupId = talk.selectedGroup.id;
            newWallMessage.message.type = 5;
            newWallMessage.message.content = talk.wallMessageContent;
            newWallMessage.message.id = 0;
            newWallMessage.message.created = Date.parse(new Date());

            newWallMessage.subject = "";
            newWallMessage.id = 0;

            var poll;
            if(talk.isPollShow){
                poll = new com.vmesteonline.be.messageservice.Poll;
                poll.id = 0;
                poll.names = [];
                var pollInputsLength = talk.pollInputs.length;
                for(var i = 0; i < pollInputsLength; i++){
                    poll.names[i] = talk.pollInputs[i].name;
                }
                newWallMessage.poll = poll;
                newWallMessage.metaType = "poll";
            }

            messageClient.postTopic(newWallMessage);

            //var newTopic = messageClient.createTopic(talk.selectedGroup.id,talk.subject,1,talk.content);

            $rootScope.base.createTopicIsHide = true;

            talk.topics.unshift(newTopic);

        };

        talk.showTopicAnswerInput = function(event){
            event.preventDefault();

            talk.fullTalkTopic.answerInputIsShow ?
                talk.fullTalkTopic.answerInputIsShow = false :
                talk.fullTalkTopic.answerInputIsShow = true ;
        };

        talk.showMessageAnswerInput = function(event,firstMessage,message){
            event.preventDefault();

            if(!message){
                // если это сообщение первого уровня
                if(!talk.fullTalkFirstMessages) talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,1000).messages;
                var fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length;

                firstMessage.answerInputIsShow ?
                    firstMessage.answerInputIsShow = false :
                    firstMessage.answerInputIsShow = true;


            }else{
                // если простое сообщение
                if(!talk.fullTalkMessages[firstMessage.id]) talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
                var  fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;
                message.answerInputIsShow ?
                    message.answerInputIsShow = false :
                    message.answerInputIsShow = true;


            }
        };

        talk.addSingleFirstMessage = function(event,topicId){
            event.preventDefault();

            talk.fullTalkTopic.answerInputIsShow = false;

            var newMessage = messageClient.createMessage(topicId,0,talk.selectedGroup.id,1,talk.answerFirstMessage);
            newMessage.createdEdit = getTiming(newMessage.created);

            talk.fullTalkFirstMessages ?
                talk.fullTalkFirstMessages.push(newMessage):
                talk.fullTalkFirstMessages[0] = newMessage;

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

                answer = firstMessage.answerMessage;
                firstMessage.isTreeOpen = true;
                firstMessage.answerInputIsShow = false;
                firstMessage.answerMessage = "Ваш ответ";
                parentId = firstMessage.id;



            }else{
                // если добавляем к простому сообщению
                for(var i = 0; i < fullTalkMessagesLength; i++){
                    if(talk.fullTalkMessages[firstMessage.id][i].id == message.id){
                        talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                        talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].createdEdit = getTiming(talk.fullTalkMessages[firstMessage.id][i].created);
                        answer = talk.fullTalkMessages[firstMessage.id][i].answerMessage;
                    }
                }
                parentId = message.id;

            }
            newMessage = messageClient.createMessage(topicId,parentId,talk.selectedGroup.id,1,answer,0,0,0);

            /*talk.fullTalkMessages ?
                talk.fullTalkMessages.push(newMessage):
                talk.fullTalkMessages[0] = newMessage;*/

            talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;

            talk.fullTalkMessages[firstMessage.id] ?
                fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length :
                fullTalkMessagesLength = 0;

            for(var i = 0; i < fullTalkMessagesLength; i++){
                talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].createdEdit = getTiming(talk.fullTalkMessages[firstMessage.id][i].created);
                talk.fullTalkMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
            }

            /*fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;
            for(var i = 0; i < fullTalkMessagesLength; i++){
                talk.fullTalkMessages[firstMessage.id][i].answerMessage = "Ваш ответ";
            }*/

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

        $rootScope.talksChangeGroup = function(groupId){

            talk.topics = messageClient.getTopics(groupId,0,0,0,1000).topics;

            initTalks();

        };

        function initTalks(){
            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;

            for(var i = 0; i < topicLength;i++){
                talk.topics[i].lastUpdateEdit = getTiming(talk.topics[i].lastUpdate);
            }
        }

    })
    .controller('ServicesController',function() {
    })
    .controller('privateMessagesController',function() {
    })
    .controller('nextdoorsController',function() {
    })
    .controller('ProfileController',function() {
    });


/* functions */

var transport = new Thrift.Transport("/thrift/MessageService");
var protocol = new Thrift.Protocol(transport);
var messageClient = new com.vmesteonline.be.messageservice.MessageServiceClient(protocol);

transport = new Thrift.Transport("/thrift/UserService");
protocol = new Thrift.Protocol(transport);
var userClient = new com.vmesteonline.be.UserServiceClient(protocol);

var userClientGroups = userClient.getUserGroups();

transport = new Thrift.Transport("/thrift/AuthService");
protocol = new Thrift.Protocol(transport);
var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

function resetPages(base){
    base.nextdoorsIsActive = false;
    base.privateMessagesIsActive = false;
    base.profileIsActive = false;
    base.talksIsActive = false;
    base.lentaIsActive = false;
    base.servicesIsActive = false;
}
function resetAceNavBtns(navbar){
    navbar.nextdoorsBtnStatus = "";
    navbar.privateMessagesBtnStatus = "";
}
function initProfile(){
    $('#profile-ava').ace_file_input({
        style:'well',
        btn_choose:'Изменить фото',
        btn_change:null,
        no_icon:'',
        droppable:true,
        thumbnail:'large',
        icon_remove:null
    }).on('change', function(){
            $('.logo-container>img').hide();
            //userClient.updateUserAvatar();

        setTimeout(saveNewAva,1000);

        function saveNewAva(){
            //console.log($('.ace-file-input').find('.file-name img').css('background-image'));
            var imgBase64 = $('.ace-file-input').find('.file-name img').css('background-image');

           /* var fd = new FormData();
            var input = $('#profile-ava');
            console.log(input[0].files[0]);
            fd.append( 'data', input[0].files[0]);*/

            userClient.updateUserAvatar(imgBase64);
        }
        //console.log($('#profile-ava').find('.file-name img').length);
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
function getAuthorName(authorId){
    var user;
    if(authorId){
        user = userClient.getUserInfoExt(authorId);
    }else{
        user = userClient.getShortUserInfo();
    }

    return user.firstName+" "+user.lastName;
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
