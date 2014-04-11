define(
    'shop-modules',
    ["jquery",'shop-initThrift','shop-common','shop-spinner','shop-category','shop-basket','shop-orders'],
    function($,thriftModule,commonModule,spinnerModule,categoryModule,basketModule,ordersModule) {
        return {
            shopCommonModule: commonModule,
            spinnerModule: spinnerModule,
            categoryModule: categoryModule,
            basketModule: basketModule,
            ordersModule: ordersModule
        }
    }
);