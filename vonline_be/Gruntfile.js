module.exports = function(grunt) {

    // Задачи
    grunt.initConfig({
        // Сжимаем
        uglify: {
            static_mappings: {
                files: {
                    'war/build/shop.min.js': 'war/js/shop.js',
                    'war/build/backoffice.min.js': 'war/js/backoffice.js',
                    'war/build/loginModule.min.js': 'war/js/loginModule.js',
                    'war/build/commonM.min.js': 'war/js/commonM.js',
                    'war/build/thrift.min.js': 'war/js/thrift.js'
                }
            },
            dynamic_mappings: {
                files: [
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'war/js/',      // Src matches are relative to this path.
                        src: ['shop-*.js'], // Actual pattern(s) to match.
                        dest: 'war/build/',   // Destination path prefix.
                        ext: '.min.js'   // Dest filepaths will have this extension.
                    },
                    {
                        expand: true,     // Enable dynamic expansion.
                        cwd: 'war/gen-js/',      // Src matches are relative to this path.
                        src: ['*.js','*.*.js'], // Actual pattern(s) to match.
                        dest: 'war/build/gen-js',   // Destination path prefix.
                        ext: '.js'   // Dest filepaths will have this extension.
                    }
                ]
            },
            special: {
                files: {
                    'war/build/gen-js/shop.bo_types.js': 'war/gen-js/shop.bo_types.js',
                    'war/js/lib/bootstrap.min.js': 'war/js/lib/bootstrap.js'
                }
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
            scripts: {
                files: ['war/js/*.js'],
                tasks: ['uglify']
            },
            css:{
                files: ['war/css/*.css'],
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