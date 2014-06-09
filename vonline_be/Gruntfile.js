module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            shop:{
                files: {
                    'war/build/shop.min.js': 'war/js/shop.js'
                }
            },
            backoffice:{
                files: {
                    'war/build/backoffice.min.js': 'war/js/backoffice.js'
                }
            },
            loginModule:{
                files: {
                    'war/build/loginModule.min.js': 'war/js/loginModule.js'
                }
            },
            commonM:{
                files: {
                    'war/build/commonM.min.js': 'war/js/commonM.js'
                }
            },
            shopCommon:{
                files: {
                    'war/build/shop-common.min.js': 'war/js/shop-common.js'
                }
            },
            shopBasket:{
                files: {
                    'war/build/shop-basket.min.js': 'war/js/shop-basket.js'
                }
            },
            shopCategory:{
                files: {
                    'war/build/shop-category.min.js': 'war/js/shop-category.js'
                }
            },
            shopSearch:{
                files: {
                    'war/build/shop-search.min.js': 'war/js/shop-search.js'
                }
            },
            shopOrders:{
                files: {
                    'war/build/shop-orders.min.js': 'war/js/shop-orders.js'
                }
            },
            shopSpinner:{
                files: {
                    'war/build/shop-spinner.min.js': 'war/js/shop-spinner.js'
                }
            },
            shopinitThrift:{
                files: {
                    'war/build/shop-initThrift.min.js': 'war/js/shop-initThrift.js'
                }
            },
            shopModules:{
                files: {
                    'war/build/shop-modules.min.js': 'war/js/shop-modules.js'
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
                files: ['war/js/shop.js'],
                tasks: ['uglify:shop']
            },
            backoffice:{
                files: ['war/js/backoffice.js'],
                tasks: ['uglify:backoffice']
            },
            loginModule:{
                files: ['war/js/loginModule.js'],
                tasks: ['uglify:loginModule']
            },
            commonM:{
                files: ['war/js/commonM.js'],
                tasks: ['uglify:commonM']
            },
            shopCommon:{
                files: ['war/js/shop-common.js'],
                tasks: ['uglify:shopCommon']
            },
            shopBasket:{
                files: ['war/js/shop-basket.js'],
                tasks: ['uglify:shopBasket']
            },
            shopCategory:{
                files: ['war/js/shop-category.js'],
                tasks: ['uglify:shopCategory']
            },
            shopSearch:{
                files: ['war/js/shop-search.js'],
                tasks: ['uglify:shopSearch']
            },
            shopOrders:{
                files: ['war/js/shop-orders.js'],
                tasks: ['uglify:shopOrders']
            },
            shopSpinner:{
                files: ['war/js/shop-spinner.js'],
                tasks: ['uglify:shopSpinner']
            },
            shopinitThrift:{
                files: ['war/js/shop-initThrift.js'],
                tasks: ['uglify:shopinitThrift']
            },
            shopModules:{
                files: ['war/js/shop-modules.js'],
                tasks: ['uglify:shopModules']
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