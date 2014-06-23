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
                    'war/build/backoffice.min.js': 'war/js/shop/backoffice.js'
                }
            },
            loginModule:{
                files: {
                    'war/build/loginModule.min.js': 'war/js/shop/loginModule.js'
                }
            },
            commonM:{
                files: {
                    'war/build/commonM.min.js': 'war/js/shop/commonM.js'
                }
            },
            shopCommon:{
                files: {
                    'war/build/shop-common.min.js': 'war/js/shop/shop-common.js'
                }
            },
            shopBasket:{
                files: {
                    'war/build/shop-basket.min.js': 'war/js/shop/shop-basket.js'
                }
            },
            shopCategory:{
                files: {
                    'war/build/shop-category.min.js': 'war/js/shop/shop-category.js'
                }
            },
            shopSearch:{
                files: {
                    'war/build/shop-search.min.js': 'war/js/shop/shop-search.js'
                }
            },
            shopOrders:{
                files: {
                    'war/build/shop-orders.min.js': 'war/js/shop/shop-orders.js'
                }
            },
            shopSpinner:{
                files: {
                    'war/build/shop-spinner.min.js': 'war/js/shop/shop-spinner.js'
                }
            },
            shopinitThrift:{
                files: {
                    'war/build/shop-initThrift.min.js': 'war/js/shop/shop-initThrift.js'
                }
            },
            shopModules:{
                files: {
                    'war/build/shop-modules.min.js': 'war/js/shop/shop-modules.js'
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
                files: ['war/js/shop/backoffice.js'],
                tasks: ['uglify:backoffice','concat']
            },
            loginModule:{
                files: ['war/js/shop/loginModule.js'],
                tasks: ['uglify:loginModule','concat']
            },
            commonM:{
                files: ['war/js/shop/commonM.js'],
                tasks: ['uglify:commonM','concat']
            },
            shopCommon:{
                files: ['war/js/shop/shop-common.js'],
                tasks: ['uglify:shopCommon','concat']
            },
            shopBasket:{
                files: ['war/js/shop/shop-basket.js'],
                tasks: ['uglify:shopBasket','concat']
            },
            shopCategory:{
                files: ['war/js/shop/shop-category.js'],
                tasks: ['uglify:shopCategory','concat']
            },
            shopSearch:{
                files: ['war/js/shop/shop-search.js'],
                tasks: ['uglify:shopSearch','concat']
            },
            shopOrders:{
                files: ['war/js/shop/shop-orders.js'],
                tasks: ['uglify:shopOrders','concat']
            },
            shopSpinner:{
                files: ['war/js/shop/shop-spinner.js'],
                tasks: ['uglify:shopSpinner','concat']
            },
            shopinitThrift:{
                files: ['war/js/shop/shop-initThrift.js'],
                tasks: ['uglify:shopinitThrift','concat']
            },
            shopModules:{
                files: ['war/js/shop/shop-modules.js'],
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