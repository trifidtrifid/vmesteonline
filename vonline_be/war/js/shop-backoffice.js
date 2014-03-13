$(document).ready(function(){
    var transport = new Thrift.Transport("/thrift/ShopService");
    var protocol = new Thrift.Protocol(transport);
    var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);
    var w = $(window);

    function initOrderPlusMinus(selector){
        selector.find('.plus-minus').click(function(e){
            e.preventDefault();

            var orderItem = $(this).closest('.order-item');
            var orderProducts = orderItem.find('.order-products');
            var orderDetails = client.getOrderDetails(orderItem.data('orderid'));
            var orderLines = orderDetails.odrerLines;
            var orderLinesLength = orderLines.length;
            //var order = client.getOrder(orderItem.data('orderid'));

            if (orderProducts.find('.catalog').length == 0){
                orderProducts.append(createOrdersProductHtml(orderDetails));

                for (var i = 0; i < orderLinesLength; i++){
                    InitSpinner(orderProducts.find('tbody tr:eq('+ i +') .spinner1'),orderLines[i].quantity);
                }

                InitAddToBasket(orderProducts.find('.fa-shopping-cart'));
                InitProductDetailPopup(orderProducts.find('.product-link'));
            }

            orderProducts.slideToggle(200,function(){
                if ($('.main-content').height() > w.height()){
                    $('#sidebar, .shop-right').css('height', $('.main-content').height()+45);
                }
            });
            if ($(this).hasClass('fa-plus')){
                $(this).removeClass('fa-plus').addClass('fa-minus');
            }else{
                $(this).removeClass('fa-minus').addClass('fa-plus');
            }
        });
    }

    function InitSpinner(selector,spinnerValue,itsBasket){
        selector.ace_spinner({value:spinnerValue,min:1,max:200,step:1, btn_up_class:'btn-info' , btn_down_class:'btn-info'})
            .on('change', function(){
            });
    }

    initOrderPlusMinus($('.back-orders'));

    InitSpinner($('.spinner1'),1);

    $('.nav-list a').click(function(e){
        e.preventDefault();
        $('.back-tab').hide();
        var index = $(this).parent().index();
        switch (index){
            case 0:
                $('.back-orders').show();
                break;
            case 1:
                $('.import').show();
                break;
            case 2:
                $('.export').show();
                break;
        }
        $(this).closest('ul').find('.active').removeClass('active');
        $(this).parent().addClass('active');
    });

});