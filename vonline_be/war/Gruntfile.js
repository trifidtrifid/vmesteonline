module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            static_mappings: {
                files: {
                    'build/shop.min.js': 'js/shop.js',
                    'build/backoffice.min.js': 'js/backoffice.js',
                    'build/loginModule.min.js': 'js/loginModule.js',
                    'build/commonM.min.js': 'js/commonM.js',
                    'build/thrift.min.js': 'js/thrift.js'
                }
            },
            dynamic_mappings: {
                files: [
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'js/',      // Src matches are relative to this path.
                        src: ['shop-*.js'], // Actual pattern(s) to match.
                        dest: 'build/',   // Destination path prefix.
                        ext: '.min.js'   // Dest filepaths will have this extension.
                    },
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'gen-js/',      // Src matches are relative to this path.
                        src: ['*.js','*.*.js'], // Actual pattern(s) to match.
                        dest: 'build/gen-js',   // Destination path prefix.
                        ext: '.js'   // Dest filepaths will have this extension.
                    }
                ]
            },
            special: {
                files: {
                    'build/gen-js/shop.bo_types.js': 'gen-js/shop.bo_types.js',
                    'js/lib/bootstrap.min.js': 'js/lib/bootstrap.js'
                }
            }
        },
        cssmin: {
            minify: {
                expand: true,
                cwd: 'css/',
                src: ['*.css', '!*.min.css'],
                dest: 'build/',
                ext: '.min.css'
            }
        },
        watch: {
            scripts: {
                files: ['js/*.js'],
                tasks: ['uglify']
            },
            css:{
                files: ['css/*.css'],
                tasks: ['cssmin']
            }
        }
    });

    // Загрузка плагинов, установленных с помощью npm install
    //grunt.loadNpmTasks('grunt-contrib-concat');

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-watch');

    // Задача по умолчанию
    grunt.registerTask('default', ['uglify','cssmin','watch']);

};