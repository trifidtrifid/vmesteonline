require.config({
    baseUrl: "/build",
    paths: {
        "jquery"   : "../js/lib/jquery-2.1.1.min",
        "bootstrap": "../js/lib/bootstrap.min",
        "ace_extra": "../js/lib/ace-extra.min",
        "ace_elements": "../js/lib/ace-elements.min",
        "jquery_ui": "../js/lib/jquery-ui-1.10.3.full.min",
        "flexslider": "../js/lib/jquery.flexslider-min",
        "ace_spinner": "../js/lib/fuelux/fuelux.spinner",
        "datepicker-backoffice": "../js/shop/backoffice/bootstrap-datepicker-backoffice",
        "datepicker-ru": "../js/lib/date-time/locales/bootstrap-datepicker.ru",
        "multiselect": "../js/lib/jquery.multiselect.min",
        "bootbox":"../js/bootbox.min"
    },
    shim:{
        'ace_spinner':{
            deps: ['jquery',"ace_extra","ace_elements","jquery_ui"],
            exports: 'ace_spinner'
        },
        'jquery_ui':{
            deps: ['jquery'],
            exports: 'jquery_ui'
        },
        'bootstrap':{
            deps: ['jquery'],
            exports: 'bootstrap'
        },
        'datepicker-backoffice':{
            deps: ['jquery','jquery_ui'],
            exports: 'datepicker-backoffice'
        },
         'datepicker-ru':{
         deps: ['jquery','datepicker-backoffice'],
         exports: 'datepicker-ru'
         }  ,
        'flexslider':{
            deps: ['jquery'],
            exports: 'flexslider'
        },
        'multiselect':{
            deps: ['jquery','jquery_ui'],
            exports: 'multiselect'
        },
        'bootbox':{
            deps: ['jquery','bootstrap'],
            exports: 'bootbox'
        }
    }
});

/*
* делаю переменные глобальными, чтобы они были видны в файле datepicker
* */
var deliveryFilterFlag= 0,
    statusFilterFlag = 0,
    dateFilterFlag = 0,
    searchFilterFlag = 0,
    orders;

require(["jquery",'shop-initThrift.min','commonM.min','shop-orders.min',
        'shop-common.min','bo-modules.min','bo-common.min','datepicker-backoffice','datepicker-ru',
        'bootstrap','multiselect'],
    function($,thriftModule,commonM,ordersModule,commonModule,boModules,boCommon) {
        var w = $(window);

        commonM.init();
        $('#sidebar').css('min-height', w.height()-45);

if($('.container.backoffice').hasClass('noAccess')){

    bootbox.alert("У вас нет прав доступа !", function() {
        document.location.replace("/");
    });
}else if (!$('.backoffice.dynamic').hasClass('adminka')){
    /* history */
    var urlHash = document.location.hash;

    var state = {
        type: 'default'
    };

    window.history.replaceState(state,null,urlHash);

    function makeHistoryNav(e) {
        // действия для корректной навигации по истории
        $('.navbar').removeClass('over-rightbar');
        var isHistoryNav = true;
        if (e.state) {
             if (e.state.type == 'page') {

                if (e.state.pageName == 'orders-history') {

                    $('.shop-trigger.go-to-orders').trigger('click', [isHistoryNav]);

                } else if (e.state.pageName == 'profile') {

                    $('.user-menu a:eq(0)').trigger('click');

                } else if (e.state.pageName == 'edit-profile') {

                    var loadEditPersonal = true;
                    $('.user-menu a:eq(0)').trigger('click', [loadEditPersonal]);

                }
            } else if (e.state.type == 'default') {

                //$('.shop-trigger.back-to-shop').trigger('click', [isHistoryNav]);
                 $('.page').hide();
                 $('.bo-page').show();
                 $('.navbar-header li.active').removeClass('active');
                 $('.bo-link').parent().addClass('active');
            }
        }
    }
    
    window.addEventListener('popstate', makeHistoryNav, false);

    $('.bo-link').parent().addClass('active');

    boModules.boOrdersModule.initBackofficeOrders();

    boModules.importModule.initImport();

    boModules.exportModule.initExport();

    $('.container.backoffice .nav-list a').click(function(e){
        e.preventDefault();
        $('.back-tab').hide();
        var index = $(this).parent().index();
        switch (index){
            case 0:
                $('.back-orders').show();
                break;
            case 1:
                $('.bo-edit').show();
                if(!boModules.productsModule.isEditInitSet) boModules.productsModule.initEdit();
                break;
            case 2:
                $('.export').show();
                break;
            case 3:
                $('.import').show();
                break;
            case 4:
                $('.bo-settings').show();
                if(!boModules.settingsModule.isSettingsInitSet) boModules.settingsModule.initSettings();
                break;
        }
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
        boCommon.setSidebarHeight();
    });


}else{

        /* -------------------------------------------------*/
        /* -------------------- ADMINKA --------------------*/
        /* -------------------------------------------------*/

    boModules.adminkaModule.initAdminka();


}

    });
