define(
    'commonM',
    ['jquery','shop-initThrift'],
    function( $,thriftModule ){

        function init(){
        /* простые обработчики событий */
        var w = $(window),
            sidebar = $('#sidebar'),
            showLeft = $('.show-left'),
            showLeftTop = (w.height()-showLeft.width())/ 2;

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

        $('.dropdown-menu li a').click(function(e){
            e.preventDefault();
            $(this).closest('.btn-group').find('.btn-group-text').text($(this).text());
        });

        $('.nav-list a,.navbar .nav a').click(function(e){
            e.preventDefault();
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });
        /* --- */
        /* переключения на настройки, профиль и выход */

        function SetJSForEditPersonal(){
            $('#date-picker-birthday').datepickerSimple({startView: 2, viewMode: 2,autoclose:true, language:'ru'});

            $('.save-changes').click(function(e){
                e.preventDefault();
                if ($('#main').hasClass('active')){

                    var newName = $('#edit-name').val();
                    var newSurname = $('#edit-surname').val();
                    var newBiz = $('#edit-biz option:selected').text();
                    var newBirth = $('#date-picker-birthday').val();
                    var userInfo = thriftModule.userClient.getUserInfo();

                    userInfo.firstName = newName;
                    userInfo.lastName = newSurname;
                    userInfo.birthday = newBirth;

                    thriftModule.userClient.updateUserInfo(userInfo);
                    //userInfo = thriftModule.userClient.getUserInfo();

                }else if($('#contacts').hasClass('active')){

                    var newEmail = $('#edit-email').val();
                    var newPhone = $('#edit-phone').val();
                    var userContacts = thriftModule.userClient.getUserContacts();

                    userContacts.email = newEmail;
                    userContacts.mobilePhone = newPhone;

                    thriftModule.userClient.updateUserContacts(userContacts);

                }else if($('#interests').hasClass('active')){

                }

                $('.save-status').addClass('active');
                function hideSaveStatus(){
                    $('.save-status').removeClass('active')
                }
                setTimeout(hideSaveStatus,2000);


            });
        }

        function SetJSForProfile(){

            $('.edit-personal-link').click(function(e){
                e.preventDefault();
                $('.dynamic').load("ajax-editPersonal.jsp .dynamic",function(){
                    SetJSForEditPersonal();
                });
            });

            $('.sendConfirmCode').click(function(e){
                e.preventDefault();
                var to = thriftModule.userClient.getUserContacts().email;
                var resourcefileName = "mailTemplates/changePasswordConfirm.html";
                thriftModule.authClient.sendConfirmCode(to,resourcefileName);
                $('.confirm-info').text('На ваш e-mail отправлен код').addClass('info-good').show();
            });

            $('.useConfirmCode').click(function(e){
                e.preventDefault();
                var email = thriftModule.userClient.getUserContacts().email;
                var confirmCode = $('#confirmCode').val();
                var confirmInfo = $('.confirm-info');
                try{
                    thriftModule.authClient.confirmRequest(email,confirmCode);
                    confirmInfo.text('Код принят !').addClass('info-good').show();
                    function closeConfirm(){
                        $('.account-no-confirm').slideUp();
                    }
                    setTimeout(closeConfirm,4000);
                }catch(e){
                    confirmInfo.text('Неверный код подтверждения !').removeClass('info-good').show();
                }
            });
        }

        $('.user-menu a').click(function(e){
            e.preventDefault();
            $(this).closest('.user-short').removeClass('open');
            e.stopPropagation();

            var ind = $(this).parent().index();
            var dynamic = $('.dynamic');
            if (ind == 0){
                dynamic.load("ajax-settings.jsp .dynamic");
            }else if (ind == 1){
                dynamic.load("ajax-profile.jsp .dynamic",function(){
                    SetJSForProfile();
                });
            } else {
                thriftModule.authClient.logout();

                document.location.replace("login.jsp");
            }
        });
            $('.user-short').click(function(){
                $(this).addClass('open');
            })
        }

        return {
            init: init
        };

    });




