define(
    'initDatepicker',
    ['jquery','shop-basket','shop-orders','shop-common'],
    function( $,basketModule, ordersModule , commonModule ){
       /* var dPicker = $('.date-picker');

        dPicker.datepicker({autoclose:true, language:'ru'}).next().on(ace.click_event, function(){
            $(this).prev().focus();
        });

        var datepickerFunc = {
            AddSingleProductToBasket: basketModule.AddSingleProductToBasket,
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
                if (basketModule.flagFromBasketClick){
                    // клик при добавленни товара в корзину
                    dPicker.on('hide',function(){
                        if (basketModule.flagFromBasketClick){
                            $(this).datepicker('triggerFlagBasket');
                            basketModule.flagFromBasketClick = 0;
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