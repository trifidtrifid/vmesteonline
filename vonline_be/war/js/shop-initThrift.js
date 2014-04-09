define(
    'shop-initThrift',
    ['jquery'],
    function( $ ){
        var transport = new Thrift.Transport("/thrift/ShopService");
        var protocol = new Thrift.Protocol(transport);
        var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/UserService");
        protocol = new Thrift.Protocol(transport);
        var userClient = new com.vmesteonline.be.UserServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/AuthService");
        protocol = new Thrift.Protocol(transport);
        var authClient = new com.vmesteonline.be.AuthServiceClient(protocol);

        return {
            client: client,
            userClient: userClient,
            authClient: authClient
        }

    }
);