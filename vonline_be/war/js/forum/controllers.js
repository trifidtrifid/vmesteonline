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
        this.groups = userClientGroups ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        var groups = this.groups,
            groupsLength = groups.length;
        groups[groupsLength-1].selected = true;

        this.isSet = function(groupId){
            //return groupId ===
        };
        this.selectGroup = function(groupId){
            for(var i = 0; i < groupsLength; i++){
                if (groups[i].id == groupId){
                    groups[i].selected = true;
                }
            }
        };

        /*this.createTopicIsHide = true;
        $rootScope.createTopicIsHide = this.createTopicIsHide;
        var mainContentTop = this;*/

        this.showCreateTopic = function(event){
            event.preventDefault();

            $rootScope.base.createTopicIsHide ? $rootScope.base.createTopicIsHide = false : $rootScope.base.createTopicIsHide = true;

        };
    })
    .controller('LentaController',function() {
        this.groups = userClientGroups ? userClientGroups.reverse() : userClient.getUserGroups().reverse();
        this.selectedGroup = this.groups[0];

        this.wallMessageContent = "Написать сообщение";
        this.wallItems = messageClient.getWallItems(this.selectedGroup.id);
        var wallItemsLength;
        this.wallItems ? wallItemsLength = this.wallItems.length :
            wallItemsLength = 0;
        var wall = this;

        for(var i = 0; i < wallItemsLength; i++){
            wall.wallItems[i].commentText = "Введите сообщение";
        }

        this.selectGroupInDropdown = selectGroupInDropdown;

        this.goToAnswerInput = function(event){
            event.preventDefault();


        };

        this.createWallMessage = function(event){
            event.preventDefault();

            var newWallMessage = messageClient.createTopic(wall.selectedGroup.id," ",5,wall.wallMessageContent);
            wall.wallMessageContent = "Написать сообщение";

            wall.wallItems.push(newWallMessage);
        };

        this.createWallComment = function(event,wallItem){
            event.preventDefault();

            var newWallComment = messageClient.createMessage(wallItem.topic.id,0,wall.selectedGroup.id,5,wallItem.commentText);
            wallItem.commentText = "Введите сообщение";

            wall.wallItems.messages.push(newWallComment);
        };

    })
    .controller('TalksController',function($rootScope) {
        var talk = this;
        talk.isTalksLoaded = false;
        talk.groups = userClientGroups ? userClientGroups.reverse() : userClient.getUserGroups().reverse();

        talk.content = "Напишите что-нибудь";
        talk.subject = "Заголовок";

        talk.fullTalkTopic = {};
        talk.fullTalkTopic.answerInputIsShow = false;
        talk.fullTalkMessages = {};
        talk.fullTalkFirstMessages = [];
        //talk.fullTalkFirstMessages.answerInputIsShow = false;
        talk.answerFirstMessage = "Ваш ответ";

        var groups = this.groups,
            groupsLength = groups.length;
        talk.selectedGroup = talk.groups[0];
        talk.topics = messageClient.getTopics(talk.selectedGroup.id,0,0,0,10).topics;
        var topicLength;
        talk.topics ? topicLength = talk.topics.length : topicLength = 0;
        if(!talk.topics) talk.topics = [];

        talk.showFullTalk = function(event,talkId){
            event.preventDefault();

            for(var i = 0; i < topicLength; i++){
                if(talkId == talk.topics[i].id){
                    talk.fullTalkTopic = talk.topics[i];
                }
            }
            talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,10).messages;
            var fullTalkFirstMessagesLength;
            talk.fullTalkFirstMessages ?
                fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length:
                fullTalkFirstMessagesLength = 0;
            if(talk.fullTalkFirstMessages === null) talk.fullTalkFirstMessages = [];

            for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                talk.fullTalkFirstMessages[i].answerInputIsShow = false;
                talk.fullTalkFirstMessages[i].isTreeOpen = false;
            }

            talk.fullTalkMessages = messageClient.getMessages(talkId,talk.selectedGroup.id,1,0,0,10);

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

        talk.showAnswerInput = function(event){
            event.preventDefault();

            talk.fullTalkTopic.answerInputIsShow ?
                talk.fullTalkTopic.answerInputIsShow = false :
                talk.fullTalkTopic.answerInputIsShow = true ;
        };

        talk.showMessageAnswerInput = function(event,messageId){
            event.preventDefault();

            if(!talk.fullTalkFirstMessages) talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,10).messages;
            var fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length;

            for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                if(messageId == talk.fullTalkFirstMessages[i].id){
                    talk.fullTalkFirstMessages[i].answerInputIsShow ?
                    talk.fullTalkFirstMessages[i].answerInputIsShow = false :
                    talk.fullTalkFirstMessages[i].answerInputIsShow = true;
                }
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

        talk.toggleTree = function($event,messageId){
            event.preventDefault();

            if(!talk.fullTalkFirstMessages) talk.fullTalkFirstMessages = messageClient.getFirstLevelMessages(talkId,talk.selectedGroup.id,1,0,0,10).messages;
            var fullTalkFirstMessagesLength = talk.fullTalkFirstMessages.length;

            for(var i = 0; i < fullTalkFirstMessagesLength; i++){
                if(messageId == talk.fullTalkFirstMessages[i].id){
                    talk.fullTalkFirstMessages[i].isTreeOpen ?
                        talk.fullTalkFirstMessages[i].isTreeOpen = false :
                        talk.fullTalkFirstMessages[i].isTreeOpen = true ;
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
var userClientGroups;// = userClient.getUserGroups();

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
