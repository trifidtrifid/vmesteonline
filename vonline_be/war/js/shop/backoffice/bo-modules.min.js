define(
    'bo-modules.min',
    ["jquery",'orders.min','products.min','import.min','export.min','settings.min','adminka.min'],
    function($,boOrdersModule,productsModule,importModule,exportModule,settingsModule,adminkaModule) {
        return {
            boOrdersModule: boOrdersModule,
            productsModule: productsModule,
            importModule: importModule,
            exportModule: exportModule,
            settingsModule: settingsModule,
            adminkaModule: adminkaModule
        }
    }
);