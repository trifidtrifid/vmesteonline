module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            main: {
                files: {
                    'build/shop.min.js': 'js/shop.js',
                    'build/shop-common.min.js': 'js/shop-common.js',
                    'build/shop-basket.min.js': 'js/shop-basket.js',
                    'build/shop-orders.min.js': 'js/shop-orders.js',
                    'build/shop-spinner.min.js': 'js/shop-spinner.js',
                    'build/shop-search.min.js': 'js/shop-search.js',
                    'build/shop-category.min.js': 'js/shop-category.js',
                    'build/shop-modules.min.js': 'js/shop-modules.js',
                    'build/loginModule.min.js': 'js/loginModule.js',
                    'build/commonM.min.js': 'js/commonM.js',
                    'build/shop-initThrift.min.js': 'js/shop-initThrift.js'
                }
            }
        },
        watch: {
            scripts: {
                files: ['js/*.js'],
                tasks: ['default']
            }
        }
    });

    // Загрузка плагинов, установленных с помощью npm install
    //grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    //grunt.loadNpmTasks('grunt-contrib-requirejs');

    // Задача по умолчанию
    grunt.registerTask('default', ['uglify','watch']);

};