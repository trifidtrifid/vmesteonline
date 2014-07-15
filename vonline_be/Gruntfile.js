module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            shop:{
                files: {
                    'war/build/shop.min.js': 'war/js/shop/shop.js'
                }
            },
            backoffice:{
                files: {
                    'war/build/backoffice.min.js': 'war/js/shop/backoffice/backoffice.js'
                }
            },
            boModules:{
                files: {
                    'war/build/bo-modules.min.js': 'war/js/shop/backoffice/bo-modules.min.js'
                }
            },
            boOrders:{
                files: {
                    'war/build/orders.min.js': 'war/js/shop/backoffice/orders.min.js'
                }
            },
            boProducts:{
                files: {
                    'war/build/products.min.js': 'war/js/shop/backoffice/products.min.js'
                }
            },
            boImport:{
                files: {
                    'war/build/import.min.js': 'war/js/shop/backoffice/import.min.js'
                }
            },
            boExport:{
                files: {
                    'war/build/export.min.js': 'war/js/shop/backoffice/export.min.js'
                }
            },
            boSettings:{
                files: {
                    'war/build/settings.min.js': 'war/js/shop/backoffice/settings.min.js'
                }
            },
            boAdminka:{
                files: {
                    'war/build/adminka.min.js': 'war/js/shop/backoffice/adminka.min.js'
                }
            },
            boCommon:{
                files: {
                    'war/build/bo-common.min.js': 'war/js/shop/backoffice/bo-common.min.js'
                }
            },
            loginModule:{
                files: {
                    'war/build/loginModule.min.js': 'war/js/shop/loginModule.min.js'
                }
            },
            commonM:{
                files: {
                    'war/build/commonM.min.js': 'war/js/shop/commonM.min.js'
                }
            },
            shopCommon:{
                files: {
                    'war/build/shop-common.min.js': 'war/js/shop/shop-common.min.js'
                }
            },
            shopBasket:{
                files: {
                    'war/build/shop-basket.min.js': 'war/js/shop/shop-basket.min.js'
                }
            },
            shopCategory:{
                files: {
                    'war/build/shop-category.min.js': 'war/js/shop/shop-category.min.js'
                }
            },
            shopSearch:{
                files: {
                    'war/build/shop-search.min.js': 'war/js/shop/shop-search.min.js'
                }
            },
            shopOrders:{
                files: {
                    'war/build/shop-orders.min.js': 'war/js/shop/shop-orders.min.js'
                }
            },
            shopSpinner:{
                files: {
                    'war/build/shop-spinner.min.js': 'war/js/shop/shop-spinner.min.js'
                }
            },
            shopinitThrift:{
                files: {
                    'war/build/shop-initThrift.min.js': 'war/js/shop/shop-initThrift.min.js'
                }
            },
            shopModules:{
                files: {
                    'war/build/shop-modules.min.js': 'war/js/shop/shop-modules.min.js'
                }
            },
            /*modules: {
                files: [
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'war/js/',      // Src matches are relative to this path.
                        src: ['shop-*.js'], // Actual pattern(s) to match.
                        dest: 'war/build/',   // Destination path prefix.
                        ext: '.min.js'   // Dest filepaths will have this extension.
                    }
                ]
            },*/
            thrift: {
                files: [
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'war/gen-js/',      // Src matches are relative to this path.
                        src: ['*.js','*.*.js'], // Actual pattern(s) to match.
                        dest: 'war/build/gen-js',   // Destination path prefix.
                        ext: '.js'   // Dest filepaths will have this extension.
                    },
                    {
                        'war/build/thrift.min.js': 'war/js/thrift.js'
                    }
                ]
            },
            special: {
                files: {
                    'war/build/gen-js/shop.bo_types.js': 'war/gen-js/shop.bo_types.js'
                    //'war/js/lib/bootstrap.min.js': 'war/js/lib/bootstrap.js'
                }
            }
        },
        concat: {
            options: {
                separator: ';'
            },
            dist: {
                src: ['war/build/jquery.js','war/build/jquery_ui.js','war/build/bootstrap.js',
                    'war/build/ace-elements.js','war/build/ace_spinner.js',/*'war/js/lib/ace-extra.min.js',*/
                    'war/build/flexslider.js','war/build/bootbox.js',
                    'war/build/thrift.min.js','war/build/gen-js/bedata_types.js','war/build/gen-js/shop_types.js',
                    'war/build/gen-js/ShopFEService.js','war/build/gen-js/shop.bo_types.js','war/build/gen-js/ShopBOService.js',
                    'war/build/gen-js/authservice_types.js','war/build/gen-js/AuthService.js','war/build/gen-js/userservice_types.js',
                    'war/build/gen-js/UserService.js',
                    'war/build/shop-*.js','war/build/loginModule.min.js','war/build/commonM.min.js','war/build/shop.min.js'],
                dest: 'war/build/build.js'
            }
        },
        cssmin: {
            minify: {
                expand: true,
                cwd: 'war/css/',
                src: ['*.css', '!*.min.css'],
                dest: 'war/build/',
                ext: '.min.css'
            }
        },
        watch: {
            shop:{
                files: ['war/js/shop/shop.js'],
                tasks: ['uglify:shop','concat']
            },
            backoffice:{
                files: ['war/js/shop/backoffice/backoffice.js'],
                tasks: ['uglify:backoffice','concat']
            },
            boModules:{
                files: ['war/js/shop/backoffice/bo-modules.min.js'],
                tasks: ['uglify:boModules','concat']
            },
            boOrders:{
                files: ['war/js/shop/backoffice/orders.min.js'],
                tasks: ['uglify:boOrders','concat']
            },
            boProducts:{
                files: ['war/js/shop/backoffice/products.min.js'],
                tasks: ['uglify:boProducts','concat']
            },
            boImport:{
                files: ['war/js/shop/backoffice/import.min.js'],
                tasks: ['uglify:boImport','concat']
            },
            boExport:{
                files: ['war/js/shop/backoffice/export.min.js'],
                tasks: ['uglify:boExport','concat']
            },
            boSettings:{
                files: ['war/js/shop/backoffice/settings.min.js'],
                tasks: ['uglify:boSettings','concat']
            },
            boAdminka:{
                files: ['war/js/shop/backoffice/adminka.min.js'],
                tasks: ['uglify:boAdminka','concat']
            },
            boCommon:{
                files: ['war/js/shop/backoffice/bo-common.min.js'],
                tasks: ['uglify:boCommon','concat']
            },
            loginModule:{
                files: ['war/js/shop/loginModule.min.js'],
                tasks: ['uglify:loginModule','concat']
            },
            commonM:{
                files: ['war/js/shop/commonM.min.js'],
                tasks: ['uglify:commonM','concat']
            },
            shopCommon:{
                files: ['war/js/shop/shop-common.min.js'],
                tasks: ['uglify:shopCommon','concat']
            },
            shopBasket:{
                files: ['war/js/shop/shop-basket.min.js'],
                tasks: ['uglify:shopBasket','concat']
            },
            shopCategory:{
                files: ['war/js/shop/shop-category.min.js'],
                tasks: ['uglify:shopCategory','concat']
            },
            shopSearch:{
                files: ['war/js/shop/shop-search.min.js'],
                tasks: ['uglify:shopSearch','concat']
            },
            shopOrders:{
                files: ['war/js/shop/shop-orders.min.js'],
                tasks: ['uglify:shopOrders','concat']
            },
            shopSpinner:{
                files: ['war/js/shop/shop-spinner.min.js'],
                tasks: ['uglify:shopSpinner','concat']
            },
            shopinitThrift:{
                files: ['war/js/shop/shop-initThrift.min.js'],
                tasks: ['uglify:shopinitThrift','concat']
            },
            shopModules:{
                files: ['war/js/shop/shop-modules.min.js'],
                tasks: ['uglify:shopModules','concat']
            },
            /*scripts: {
                files: ['war/js*//*.js'],
                tasks: ['uglify']
            },*/
            css:{
                files: ['war/css/*.css'],
                tasks: ['cssmin']
            }
        }
    });

    // Загрузка плагинов, установленных с помощью npm install
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-watch');

    // Задача по умолчанию
    grunt.registerTask('default', ['uglify','cssmin','concat','watch']);
    grunt.registerTask('css', ['cssmin','watch']);
    grunt.registerTask('watcher', ['watch']);

};