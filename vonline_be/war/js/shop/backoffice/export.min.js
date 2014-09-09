define(
    'export.min',
    ['jquery','shop-initThrift.min','bo-common.min','multiselect'],
    function( $,thriftModule, boCommon ){

        function initExport(){
            $('.export-orders-checklist select, .export-products-checklist select, .export-packs-checklist select').multiselect({
                noneSelectedText: "Опции",
                selectedText: "Опции (# выбрано)",
                checkAllText: "Выбрать все",
                uncheckAllText: "Отменить все"
            });
            $('.ui-multiselect').addClass('btn btn-sm btn-info no-border');
            $('.ui-multiselect .ui-icon').addClass('icon-caret-down');

            $('.export .nav-tabs').find('li').click(function(){
                var ind = $(this).index();

                var contentH = $('.export .tab-pane:eq('+ ind +')').addClass('active').height();
                boCommon.setSidebarHeight(contentH);
            });

            $('.export-btn').click(function(e){
                e.preventDefault();
                var currentTab = $(this).closest('.tab-pane');
                var deliveryText = currentTab.find('.export-delivery-dropdown .btn-group-text').text();
                //var selectOrderDate = currentTab.find('.datepicker-export').data('selectorderdate');
                var selectOrderDate = currentTab.find('.datepicker-export').attr('data-selectorderdate');
                //alert('0 '+selectOrderDate);

                if(!selectOrderDate){
                    currentTab.find('.error-info').text('Пожалуйста, укажите дату с заказом.').show();
                }else{
                    currentTab.find('.error-info').hide();
                    currentTab.find('.confirm-info').hide();

                    var tabs = $('.export .nav-tabs').find('li');
                    var currentInd = 0;
                    tabs.each(function(){
                        if ($(this).hasClass('active')){
                            currentInd = $(this).index();
                        }
                    });

                    var deliveryType;
                    (deliveryText != 'Тип доставки' && deliveryText != 'Все') ? deliveryType = boCommon.getDeliveryTypeByText(deliveryText):deliveryType=0;

                    var dataSet;

                    try{
                        switch (currentInd){
                            case 0:
                                var exportFieldsOrder = getExportFields('orders');

                                var exportFieldsOrderLine = getExportFields('orderLine');

                                dataSet = thriftModule.clientBO.getTotalOrdersReport(selectOrderDate,deliveryType,exportFieldsOrder.fieldsMap,exportFieldsOrderLine.fieldsMap);

                                var tablesCount;

                                if(dataSet.data){

                                    tablesCount = dataSet.data.length;  // кол-во таблиц в нашем отчете

                                    drawExportTables(dataSet,tablesCount,exportFieldsOrder,exportFieldsOrderLine);

                                }

                                break;
                            case 1:
                                var exportFields = getExportFields('products');

                                dataSet = thriftModule.clientBO.getTotalProductsReport(selectOrderDate,deliveryType,exportFields.fieldsMap);

                                if(dataSet.data){

                                    tablesCount = dataSet.data.length;  // кол-во таблиц в нашем отчете

                                    drawExportTables(dataSet,tablesCount,exportFields);

                                }

                                break;
                            case 2:
                                exportFields = getExportFields('packs');

                                dataSet = thriftModule.clientBO.getTotalPackReport(selectOrderDate,deliveryType,exportFields.fieldsMap);

                                if(dataSet.data){

                                    //tablesCount = 1;  // кол-во таблиц в нашем отчете
                                    tablesCount = dataSet.data.length;

                                    drawExportTables(dataSet,tablesCount,exportFields);
                                }

                                break;
                        }
                        boCommon.initShowFullText();
                        boCommon.initCloseFullText();

                        if(!dataSet.data) currentTab.find('.confirm-info').text('Нет данных на такое сочетание даты и типа доставки.').show();

                        boCommon.setSidebarHeight(0);

                    }catch(e){
                        currentTab.find('.confirm-info').text('Ошибка экспорта.').show();
                    }
                }
            });

            function getExportFields(exportType){
                var fieldsMap = [];
                var fieldsCounter = 0;
                var currentCheckbox;
                var headColArray = [];
                counter = 0;

                var delimiterIndexes = [];
                var orderCheckboxes = $('.ui-multiselect-menu:eq(0) .ui-multiselect-checkboxes li');

                orderCheckboxes.each(function(){
                    if($(this).hasClass('ui-multiselect-optgroup-label')){
                        delimiterIndexes[counter++] = $(this).index();
                    }
                });

                switch (exportType){
                    case 'orders':
                        var myCheckboxes = orderCheckboxes.slice(delimiterIndexes[0]+1,delimiterIndexes[1]);
                        currentCheckbox = myCheckboxes.find('input[aria-selected="true"]');
                        break;
                    case 'orderLine':
                        myCheckboxes = orderCheckboxes.slice(delimiterIndexes[1]+1);
                        currentCheckbox = myCheckboxes.find('input[aria-selected="true"]');
                        break;
                    case 'products':
                        var productsCheckboxes = $('.ui-multiselect-menu:eq(1) .ui-multiselect-checkboxes li');
                        currentCheckbox =  productsCheckboxes.find('input[aria-selected="true"]');
                        //currentCheckbox =  $('.export-products-checklist .checkbox.active');
                        break;
                    case "packs":
                        var packsCheckboxes = $('.ui-multiselect-menu:eq(2) .ui-multiselect-checkboxes li');
                        currentCheckbox =  packsCheckboxes.find('input[aria-selected="true"]');
                        break;
                }

                counter = 0;
                currentCheckbox.each(function(){
                    //fieldsMap[fieldsCounter++] = parseInt($(this).data('exchange'));
                    fieldsMap[fieldsCounter++] = parseInt($(this).val());
                    headColArray[counter++] = $(this).find('+span').text();
                });

                return {fieldsMap: fieldsMap,headColArray: headColArray }
            };

            function drawExportTables(dataSet,tablesCount,exportFields,exportFieldsOrderLine){

                var currentExportTable = $('.tab-pane.active').find('.export-table');
                currentExportTable.html("");
                var exportData = [];
                for(var z = tablesCount-1 ; z >= 0; z--){
                    // формирование данных экспорта для каждой таблицы
                    exportData = [];
                    var fieldsData = dataSet.data[z].fieldsData;
                    var rowCount = fieldsData.rowCount;
                    // округляем до наибольшего целого
                    var colCount = Math.ceil(fieldsData.elems.length/rowCount);
                    var counter = 0;
                    for(var i = 0; i < rowCount; i++){
                        exportData[i] = [];
                        for(var j = 0; j < colCount; j++){
                            exportData[i][j] = fieldsData.elems[counter++];
                        }
                    }

                    //var headColArray = exportFields.headColArray;
                    var headColArray, beginRow = 0;

                    if(dataSet.data[z].fieldsMap){
                        headColArray = exportFields.headColArray;
                        if(exportFieldsOrderLine){
                            (z == tablesCount-1) ? headColArray = exportFields.headColArray: headColArray = exportFieldsOrderLine.headColArray;
                        }
                    }else{
                        headColArray = exportData[0];
                        beginRow = 1;
                    }

                    var reportTable = '<div class="export-single-table"><table>' +
                        '<thead>' +
                        '<tr>' +
                        createExportHeadLine(headColArray)+
                        '</tr>'+
                        '</thead>'+
                        '<tbody>' +
                        createExportTable(exportData,colCount,beginRow)+
                        '</tbody>'+
                        '</table></div>';

                    reportTable += '<div class="report-link"><a href="'+ dataSet.data[z].url +'">Скачать отчет</a>';

                    currentExportTable.append(reportTable);
                }

            }

            function createExportHeadLine(headColArray){

                var exportHeadLine = "";
                var exportHeadLineLength = headColArray.length;

                for(i = 0; i < exportHeadLineLength; i++){
                    exportHeadLine += '<td>' +
                        headColArray[i]+
                        '</td>';
                }

                return exportHeadLine;

            }

            function createExportTable(exportData,colCount,beginRow){
                var exportTable = "";
                var exportDataLength = exportData.length;

                for(var i = beginRow; i < exportDataLength; i++){
                    exportTable += '<tr>'+
                        createExportLine(exportData[i],colCount)+
                        '</tr>';
                }

                function createExportLine(exportDatarow,colCount){
                    var exportLine = "";
                    var cont = "",fullText;

                    for(var i = 0; i < colCount; i++){
                        cont = exportDatarow[i];
                        if (cont && cont.length > 30){
                            fullText = cont;
                            cont = cont.slice(0,30);
                            cont += " <a class='show-full-text' href='#'>...</a>"+
                                '<div class="full-text"><a href="#" class="close">×</a>'+ fullText +'</div>';
                        }
                        exportLine += '<td>'+
                            cont +
                            '</td>';
                    }

                    return exportLine;
                }
                return exportTable;
            }
        }

        return {
            initExport : initExport
        }

    });