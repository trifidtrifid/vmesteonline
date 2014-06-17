define(
    'shop-modules.min',
    ["jquery",'shop-initThrift.min','shop-common.min','shop-spinner.min','shop-category.min','shop-basket.min','shop-orders.min'],
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