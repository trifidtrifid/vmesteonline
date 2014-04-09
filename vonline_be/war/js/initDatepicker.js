define(
    'initDatepicker',
    ['jquery','shop-addProduct','shop-orders','shop-common'],
    function( $,addProduct, ordersModule , commonModule ){
       /* var dPicker = $('.date-picker');

        dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
            $(this).prev().focus();
        });

        var datepickerFunc = {
            AddSingleProductToBasket: addProduct.AddSingleProductToBasket,
            initVarForMoreOrders: ordersModule.initVarForMoreOrders,
            createOrdersHtml: ordersModule.createOrdersHtml,
            initShowMoreOrders: ordersModule.initShowMoreOrders,
            initOrderPlusMinus: ordersModule.initOrderPlusMinus,
            initOrderBtns: ordersModule.initOrderBtns,
            setSidebarHeight: commonModule.setSidebarHeight,
            initOrdersLinks: ordersModule.initOrdersLinks
        };

        dPicker.datepicker('setVarOrderDates',datepickerFunc);

        dPicker.click(function(){
            try{
                if (addProduct.flagFromBasketClick){
                    // клик при добавленни товара в корзину
                    dPicker.on('hide',function(){
                        if (addProduct.flagFromBasketClick){
                            $(this).datepicker('triggerFlagBasket');
                            addProduct.flagFromBasketClick = 0;
                        }
                    });
                }
            }catch(e){
                alert(e+" Функция dPicker.click");
            }
        });

        return{
            dPicker: dPicker,
            datepickerFunc: datepickerFunc
        }
     */
    }
);