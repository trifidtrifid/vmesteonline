define(
    'bo-modules',
    ["jquery",'orders','products','import','export','settings','adminka'],
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