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

        base.content = "Напишите что-нибудь";
        base.subject = "Заголовок";

        resetPages(base);
        base.lentaIsActive = true;

        base.showCreateTopic = function(event){
            event.preventDefault();

            base.createTopicIsHide ? base.createTopicIsHide = false : base.createTopicIsHide = true;

        };

        base.addSingleTalk = function(){
            messageClient.createTopic(0,base.subject,1,base.content);
        };

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
    .controller('mainContentTopController',function() {
        this.groups = userClient.getUserGroups().reverse();
        var groups = this.groups,
            groupsLength = groups.length;

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
    })
    .controller('LentaController',function() {
    })
    .controller('TalksController',function() {
        var talk = this;
        talk.isTitles = true;
        talk.isTalksLoaded = false;
        talk.topics = messageClient.getTopics(0,0,0,0,0).topics;

        talk.showFullTalk = function(event){
            event.preventDefault();

            talk.isTitles = false;

            var talksBlock = $('.talks').find('.talks-block');

            if(!talk.isTalksLoaded){
                talksBlock.load('ajax/forum/talks-single.jsp .talks-single',function(){

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

                });
            }
        };

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