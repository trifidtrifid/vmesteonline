    $(document).ready(function(){
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

        $('.nav-list a').click(function(e){
            e.preventDefault();
            $(this).closest('ul').find('.active').removeClass('active');
            $(this).parent().addClass('active');
        });
        /* --- */
        /* переключения на настройки, профиль и выход */

        $('.user-menu a').click(function(e){
            e.preventDefault();

            var ind = $(this).parent().index();
            var dynamic = $('.dynamic');
            if (ind == 0){
                dynamic.load("ajax-settings.jsp .dynamic");
            }else if (ind == 1){
                dynamic.load("ajax-profile.jsp .dynamic",function(){
                    $('.edit-personal-link').click(function(e){
                        e.preventDefault();
                        dynamic.load("ajax-editPersonal.jsp .dynamic");
                    });
                });
            } else {
                var transport = new Thrift.Transport("/thrift/AuthService");
                var protocol = new Thrift.Protocol(transport);
                var client = new com.vmesteonline.be.AuthServiceClient(protocol);
                client.logout();

                document.location.replace("login.jsp");
            }
        });
    });

