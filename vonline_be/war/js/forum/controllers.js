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

            document.location.replace("login.jsp");

        }

  })
  .controller('leftBarController',function($rootScope) {

    $rootScope.leftbar = this;

    this.tab = 1;

    this.setTab = function(event,newValue){
        event.preventDefault();

        this.tab = newValue;
        resetPages($rootScope.base);
        resetAceNavBtns($rootScope.navbar);

        switch(newValue){
            case 1:

                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.lentaIsActive = true;
                break;
            case 2:
                $rootScope.base.mainContentTopIsHide = false;
                $rootScope.base.talksIsActive = true;
                $rootScope.base.isTalkTitles = true;
                break;
            case 3:
                $rootScope.base.servicesIsActive = true;
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
            $rootScope.wallChangeGroup(group.id);

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

        lenta.wallMessageContent = "Написать сообщение";

        lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);

        var wallItemsLength;
        lenta.wallItems ? wallItemsLength = lenta.wallItems.length :
            wallItemsLength = 0;

        for(var i = 0; i < wallItemsLength; i++){
            lenta.wallItems[i].commentText = "Введите сообщение";
        }

        lenta.selectGroupInDropdown = selectGroupInDropdown;

        lenta.goToAnswerInput = function(event){
            event.preventDefault();

        };

        lenta.createWallMessage = function(event){
            event.preventDefault();

            console.log(lenta.selectedGroup.id+" "+lenta.wallMessageContent);
            var newWallMessage = messageClient.createTopic(lenta.selectedGroup.id," 1",5,lenta.wallMessageContent);
            lenta.wallMessageContent = "Написать сообщение";

            if(lenta.selectedGroupInTop.id == lenta.selectedGroup.id){
                lenta.wallItems = messageClient.getWallItems(lenta.selectedGroup.id);
            }

        };

        lenta.createWallComment = function(event,wallItem){
            event.preventDefault();

            var newWallComment = messageClient.createMessage(wallItem.topic.id,0,lenta.selectedGroupInTop.id,5,wallItem.commentText);
            wallItem.commentText = "Введите сообщение";

            //console.log(lenta.wallItems+" "+lenta.wallItems.topic);
            wallItem.messages.push(newWallComment);
        };

        $rootScope.wallChangeGroup = function(groupId){

            lenta.wallItems = messageClient.getWallItems(groupId);

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

        talk.content = "Напишите что-нибудь";
        talk.subject = "Заголовок";

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

        if(!talk.topics) talk.topics = [];

        talk.showFullTalk = function(event,talkOutside){
            event.preventDefault();

            var topicLength;
            talk.topics ? topicLength = talk.topics.length : topicLength = 0;
            //talk.fullTalkTopic = talkOutside;

            talkId = talkOutside.id;
            for(var i = 0; i < topicLength; i++){
                if(talkId == talk.topics[i].id){
                    talk.fullTalkTopic = talk.topics[i];
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
            var newTopic = messageClient.createTopic(talk.selectedGroup.id,talk.subject,1,talk.content);

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

                /*for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                    if(firstMessage.id == talk.fullTalkFirstMessages[i].id){
                        talk.fullTalkFirstMessages[i].answerInputIsShow ?
                        talk.fullTalkFirstMessages[i].answerInputIsShow = false :
                        talk.fullTalkFirstMessages[i].answerInputIsShow = true;
                    }
                }*/
            }else{
                // если простое сообщение
                if(!talk.fullTalkMessages[firstMessage.id]) talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,1000).messages;
                var  fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;
                message.answerInputIsShow ?
                    message.answerInputIsShow = false :
                    message.answerInputIsShow = true;

                /*for(var i = 0; i <  fullTalkMessagesLength; i++){
                    if(message.id == talk.fullTalkMessages[firstMessage.id][i].id){
                        talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow ?
                            talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false :
                            talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = true;
                    }
                }*/
            }
        };

        talk.addSingleFirstMessage = function(event,topicId){
            event.preventDefault();

            talk.fullTalkTopic.answerInputIsShow = false;

            var newMessage = messageClient.createMessage(topicId,0,talk.selectedGroup.id,1,talk.answerFirstMessage);

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
                for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                    if(talk.fullTalkFirstMessages[i].id == firstMessage.id){
                        talk.fullTalkFirstMessages[i].answerInputIsShow = false;
                        talk.fullTalkFirstMessages[i].isTreeOpen = true;
                    }
                }

                answer = talk.answerFirstMessage;
                firstMessage.isTreeOpen = true;
                parentId = firstMessage.id;

            }else{
                // если добавляем к простому сообщению
                for(var i = 0; i < fullTalkMessagesLength; i++){
                    if(talk.fullTalkMessages[firstMessage.id][i].id == message.id){
                        talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                        talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                        talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
                        answer = talk.fullTalkMessages[firstMessage.id][i].answerMessage;
                    }
                }
                parentId = message.id;

            }
            newMessage = messageClient.createMessage(topicId,parentId,talk.selectedGroup.id,1,answer,0,0,0);

            /*talk.fullTalkMessages ?
                talk.fullTalkMessages.push(newMessage):
                talk.fullTalkMessages[0] = newMessage;*/

            talk.fullTalkMessages[firstMessage.id] = messageClient.getMessages(talkId,talk.selectedGroup.id,1,firstMessage.id,0,10).messages;

            fullTalkMessagesLength = talk.fullTalkMessages[firstMessage.id].length;

            for(var i = 0; i < fullTalkMessagesLength; i++){
                talk.fullTalkMessages[firstMessage.id][i].answerInputIsShow = false;
                talk.fullTalkMessages[firstMessage.id][i].isTreeOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isOpen = true;
                talk.fullTalkMessages[firstMessage.id][i].isParentOpen = true;
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

            /*for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                if(firstMessage.id == talk.fullTalkFirstMessages[i].id){
                    talk.fullTalkFirstMessages[i].isTreeOpen ?
                        talk.fullTalkFirstMessages[i].isTreeOpen = false :
                        talk.fullTalkFirstMessages[i].isTreeOpen = true ;
                }
            }*/

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
