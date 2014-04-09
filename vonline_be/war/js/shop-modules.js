define(
    'shop-modules',
    ["jquery",'shop-initThrift','shop-common','shop-spinner','shop-basket','shop-category','shop-orders','shop-delivery'],
    function($,thriftModule,commonModule,spinnerModule,basketModule,categoryModule,ordersModule,deliveryModule) {
        return {
            shopCommonModule: commonModule,
            spinnerModule: spinnerModule,
            basketModule: basketModule,
            categoryModule: categoryModule,
            ordersModule: ordersModule,
            deliveryModule: deliveryModule,
        }
    }
);