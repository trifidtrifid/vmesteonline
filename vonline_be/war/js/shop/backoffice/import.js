define(
    'import',
    ['jquery','shop-initThrift','bo-common'],
    function( $,thriftModule,boCommon ){

        function initImport(){
            var nowTime = parseInt(new Date().getTime()/1000);
            nowTime -= nowTime%86400;
            var day = 3600*24;

            var dataCSV = [],
                elems, rowCount,colCount;

            var fileUrl;
            $('.form-import').submit(function(e){
                e.preventDefault();
                var path = $('#import-data').val();
                if($('.import-dropdown .btn-group-text').text() == 'Тип импортируемых данных'){
                    $('.import').find('.error-info').text('Пожалуйста, укажите тип импортируемых данных.').show();
                }else if(!path){
                    $('.import').find('.error-info').text('Пожалуйста, выберите файл.').show();
                }else{
                    var data = path;
                    var importPublic = $('#import-public').val();
                    path = path.split("\\");
                    var pathLength = path.length;
                    var fname = path[pathLength-1];

                    var fd = new FormData();
                    var input = $('.form-import #import-data');
                    fd.append( 'data', input[0].files[0]);
                    /*for(var p in input[0].files[0]){
                     alert(p+" "+ input[0].files[0][p]);
                     }*/

                    $.ajax({
                        type: "POST",
                        url: "/file/",
                        cache: false,
                        processData: false,
                        //contentType: 'multipart/form-data',
                        contentType: false,
                        data: fd,
                        /*data: {
                         fname: fname,
                         data: data,
                         public: importPublic
                         },*/
                        success: function(html) {
                            fileUrl = html;

                            var matrixAsList = thriftModule.clientBO.parseCSVfile(fileUrl);
                            elems = matrixAsList.elems;
                            rowCount = matrixAsList.rowCount;
                            var dataCSVLength = elems.length;
                            colCount = dataCSVLength/rowCount;
                            var counter = 0;
                            dataCSV = [];

                            for(var i = 0; i < rowCount; i++){
                                dataCSV[i] = [];
                                for(var j = 0; j < colCount; j++){
                                    dataCSV[i][j] = elems[counter++];
                                }
                            }

                            // формирование таблицы для вывода на экран
                            var importType = $('.import-dropdown .btn-group-text').text();
                            var dropdownColArray = [];
                            var dropdownColArrayFieldType = [];
                            var ExField = com.vmesteonline.be.shop.bo.ExchangeFieldType;
                            counter = 0;

                            switch (importType){
                                case "Продукты" :
                                    for(var p in ExField){
                                        if (ExField[p] >= 300 && ExField[p] < 330  ){
                                            dropdownColArray[counter] = p;
                                            dropdownColArrayFieldType[counter++] = ExField[p];
                                        }
                                    }
                                    break;
                                case "Категории продуктов" :
                                    for(p in ExField){
                                        if (ExField[p] >= 200 && ExField[p] < 230  ){
                                            dropdownColArray[counter] = p;
                                            dropdownColArrayFieldType[counter++] = ExField[p];
                                        }
                                    }
                                    break;
                                case "Производители" :
                                    for(p in ExField){
                                        if (ExField[p] >= 100 && ExField[p] < 130  ){
                                            dropdownColArray[counter] = p;
                                            dropdownColArrayFieldType[counter++] = ExField[p];
                                        }
                                    }
                                    break;
                            }

                            //использую эти переменные для меню дроплауна, которое
                            //должно быть неизменным независимо от кол-ва загруженных столбцов
                            var dropdownColArrayMenu = dropdownColArray.slice(0);
                            var dropdownColArrayMenuFieldType = dropdownColArrayFieldType.slice(0);

                            var begin,end;
                            if(colCount < counter ){
                                // если стобцов в таблице меньше чем default удаляем лишние default столбцы
                                begin = counter;
                                end = colCount;

                                for(i = begin-1; i >= end ; i--){
                                    dropdownColArray.pop();
                                    dropdownColArrayFieldType.pop();
                                }
                            }
                            if(colCount > counter){
                                // если столбцов больше, то добавляем столбцы со значеним SKIP
                                begin = colCount;
                                end = counter;

                                for(i = begin-1; i >= end ; i--){
                                    dropdownColArray[i] = skipConst;
                                    dropdownColArrayFieldType[i] = -1;
                                }
                            }

                            var importHtml = '<table><thead>' +
                                '<tr>' +
                                createImportDropdownLine(dropdownColArray,dropdownColArrayFieldType,dropdownColArrayMenu,dropdownColArrayMenuFieldType)+
                                '</tr>'+
                                '</thead>' +
                                '<tbody>'+
                                createImportTable(dataCSV,dropdownColArray.length)+
                                '</tbody>' +
                                '</table>';

                            $('.import-table').html("").prepend(importHtml).parent().show();

                            $('.top-scroll').remove();
                            boCommon.DoubleScroll(document.getElementById('doublescroll'));

                            boCommon.initShowFullText();
                            boCommon.initCloseFullText();

                            $('.import-field-dropdown .dropdown-toggle').click(function(e){
                                e.preventDefault();

                                var ind = $(this).closest('td').index();
                                var coordX = 0, tdIndex = 0;
                                $('.import-table table').find('td').each(function(){
                                    if (tdIndex < ind){
                                        coordX += $(this).width();
                                    }
                                    tdIndex ++;
                                });
                                coordX -= $('.import-table').scrollLeft();

                                var coordY = 94;
                                $(this).parent().find('.dropdown-menu').css({'left':coordX,'top':coordY});
                            });

                            $('.import-field-dropdown .dropdown-menu').find('li a').click(function(e){
                                e.preventDefault();

                                var fieldName = $(this).parent().text();
                                var fieldType = $(this).parent().data('fieldtype');

                                $('.import-field-dropdown').each(function(){
                                    if ($(this).find('.btn-group-text').text() == fieldName){
                                        $(this).find('.dropdown-toggle').attr('data-fieldtype',-1);
                                        $(this).find('.btn-group-text').text(skipConst);
                                    }
                                });

                                var currentDropdown = $(this).closest('.import-field-dropdown');
                                //alert(fieldType);
                                currentDropdown.find('.btn-group-text').text(fieldName).parent().data('fieldtype',fieldType);
                                //currentDropdown.find('.btn-group-text').text(fieldName).closest('.btn').dataset.fieldtype = fieldType;
                                //alert(currentDropdown.find('.btn-group-text').text(fieldName).closest('.btn').data('fieldtype'));
                            });

                            boCommon.setSidebarHeight($('.main-content').height());

                        }
                    });

                }
            });


            function createImportDropdownLine(dropdownColArray,dropdownColArrayFieldType,dropdownColArrayMenu,dropdownColArrayMenuFieldType){

                var importDropdownLine = "";
                var importDropdownLineLength = dropdownColArray.length;
                var dropdownColArrayMenuLength = dropdownColArrayMenu.length;
                //alert(importDropdownLineLength+" "+dropdownColArrayMenuLength);

                var importDropdownMenu = "",
                    haveSkip = false;
                for(var i = 0; i < dropdownColArrayMenuLength; i++){
                    if(!haveSkip) importDropdownMenu += '<li data-fieldtype="'+ dropdownColArrayMenuFieldType[i] +'"><a href="#">'+ dropdownColArrayMenu[i] +'</a></li>';

                    if(dropdownColArrayMenu[i] == skipConst){
                        // на случай если добавляется таблица с большим чем Default кол-вом стобцов, чтобы SKIP не
                        // дублировался в dropdown
                        haveSkip = true;
                    }
                }
                if(!haveSkip) importDropdownMenu += '<li data-fieldtype="'+ -1 +'"><a href="#">'+ skipConst +'</a></li>';

                for(i = 0; i < importDropdownLineLength; i++){
                    importDropdownLine += '<td>' +
                        '<div class="btn-group import-field-dropdown">'+
                        '<button data-toggle="dropdown" data-fieldtype="'+ dropdownColArrayFieldType[i] +'" class="btn btn-info btn-sm dropdown-toggle no-border">'+
                        '<span class="btn-group-text">'+ dropdownColArray[i] +'</span>'+
                        '<span class="icon-caret-down icon-on-right"></span>'+
                        '</button>'+
                        '<ul class="dropdown-menu dropdown-blue">'+
                        importDropdownMenu+
                        '</ul>'+
                        '</div>'+
                        '</td>';
                }

                return importDropdownLine;

            }

            function createImportTable(dataCSV,colCount){
                var importTable = "";
                var dataCSVLength = dataCSV.length;

                for(var i = 0; i < dataCSVLength; i++){
                    importTable += '<tr>'+
                        createImportLine(dataCSV[i],colCount)+
                        '</tr>';
                }

                function createImportLine(dataCSVrow,colCount){
                    var importLine = "";
                    var cont = "",fullText;
                    //alert(dataCSVrow.length+" "+colCount);

                    for(var i = 0; i < colCount; i++){
                        cont = dataCSVrow[i];
                        if (cont && cont.length > 30){
                            // урезаем длинный текст
                            fullText = cont;
                            cont = cont.slice(0,30);
                            cont += " <a class='show-full-text' href='#'>...</a>"+
                                '<div class="full-text"><a href="#" class="close">×</a>'+ fullText +'</div>';
                        }
                        importLine += '<td>'+
                            cont +
                            '</td>';
                    }

                    return importLine;
                }
                return importTable;
            }

            $('#import-data').click(function(){
                $('.import').find('.error-info').hide();
            });

            $('.checkbox .lbl').click(function(){
                $(this).closest('.checkbox').toggleClass('active');
            });

            $('.import-dropdown .dropdown-menu li').click(function(){
                $('.import').find('.error-info').hide();
            });

            var skipConst = "SKIP";
            $('.import-btn').click(function(e){
                e.preventDefault();

                var dropdowns = $('.import-field-dropdown');
                var dropdownLength = dropdowns.length;
                var repeatDropdown = "";
                var haveRepeat = false;
                dropdowns.each(function(){
                    if(!haveRepeat){
                        var ind = $(this).parent().index();
                        var dropdownName = $(this).find('.btn-group-text').text();
                        for(var i = ind+1; i < dropdownLength; i++){
                            if (dropdownName == dropdowns.eq(i).find('.btn-group-text').text() && dropdownName != skipConst){
                                repeatDropdown = dropdownName;
                                haveRepeat = true;
                                break;
                            }
                        }
                    }
                });

                var confirmInfo = $(this).closest('.back-tab').find('.confirm-info');
                if(haveRepeat){
                    confirmInfo.text('Вы указали два столбца с одинаковым занчением : ' + repeatDropdown ).show();
                }else{
                    confirmInfo.hide();

                    var importType = $('.import-dropdown .btn-group-text').text();
                    var ImExType;

                    switch (importType){
                        case 'Продукты':
                            ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_PRODUCTS;
                            break;
                        case 'Категории продуктов':
                            ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_CATEGORIES;
                            break;
                        case 'Производители':
                            ImExType = com.vmesteonline.be.shop.bo.ImExType.IMPORT_PRODUCERS;
                            break;
                    }

                    var fieldsMap = [];
                    var fieldsCounter = 0;
                    $('.import .import-field-dropdown').each(function(){
                        var fieldName = $(this).find('.btn-group-text').text();
                        if(fieldName != skipConst){
                            //alert(fieldsCounter+ " " + $(this).find('.btn .btn-group-text').text() +" "+ $(this).find('.btn').data('fieldtype'));
                            fieldsMap[fieldsCounter] = parseInt($(this).find('.btn').data('fieldtype'));
                        }
                        fieldsCounter++;
                    });

                    var importElement = new com.vmesteonline.be.shop.bo.ImportElement;
                    importElement.type = ImExType;
                    importElement.filename = 'filename';
                    importElement.fieldsMap = fieldsMap;
                    importElement.url = fileUrl;
                    var temp = [];
                    temp[0] = importElement;

                    var dataSet = new com.vmesteonline.be.shop.bo.DataSet;
                    dataSet.name = "name";
                    dataSet.date = nowTime;
                    dataSet.data = temp;


                    $('.loading').fadeIn(function(){
                        try{
                            thriftModule.clientBO.importData(dataSet);
                            confirmInfo.text('Данные успешно импортированы.').addClass('info-good').show();
                        }catch(e){
                            confirmInfo.text('Ошибка импорта.').show();
                        }
                        $('.loading').fadeOut(0);
                        setTimeout(hideConfirm,3000);
                    });

                    function hideConfirm(){
                        $('.confirm-info').hide();
                    }

                }

            });

            /* /import */
        }


        return {
            initImport : initImport
        }

    });