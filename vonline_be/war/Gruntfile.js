module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            main: {
                files: {
                    // Результат задачи concat
                    'build/shop.min.js': 'js/shop.js',
                    'build/shop-common.min.js': 'js/shop-common.js'
                }
            }
        }
    });

    // Загрузка плагинов, установленных с помощью npm install
    //grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    //grunt.loadNpmTasks('grunt-contrib-requirejs');

    // Задача по умолчанию
    grunt.registerTask('default', ['uglify']);

};