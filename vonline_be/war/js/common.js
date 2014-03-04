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

        w.resize(function(){
            if ($(this).width() > 753){
                sidebar.css({'marginLeft':'0'});
                $('.main-content').css('margin-left','190px');
            }else{
                sidebar.css({'marginLeft':'-190px'});
                $('.main-content').css('margin-left','0');
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
        $('.user-menu a').click(function(){
            var ind = $(this).parent().index();
            var dynamic = $('.dynamic');
            if (ind == 0){
                var settingsHtml = $('.settings-wrap').html();
                dynamic.html(settingsHtml);
            }else if (ind == 1){
                var profileHtml = $('.user-descr-wrap').html();
                dynamic.html(profileHtml);
                $('.edit-personal-link').click(function(){
                    var editPersonalHtml = $('.edit-personal-wrap').html();
                    dynamic.html(editPersonalHtml);
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

