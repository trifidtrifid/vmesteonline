define(
    'shop-initThrift.min',
    ['jquery'],
    function( $ ){
        var transport = new Thrift.Transport("/thrift/ShopService");
        var protocol = new Thrift.Protocol(transport);
        var client = new com.vmesteonline.be.shop.ShopFEServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/ShopBOService");
        protocol = new Thrift.Protocol(transport);
        var clientBO = new com.vmesteonline.be.shop.bo.ShopBOServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/UserService");
        protocol = new Thrift.Protocol(transport);
        var userClient = new com.vmesteonline.be.UserServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/AuthService");
        protocol = new Thrift.Protocol(transport);
        var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

        return {
            client: client,
            clientBO: clientBO,
            userClient: userClient,
            authClient: authClient
        }

    }
);