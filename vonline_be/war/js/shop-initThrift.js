define(
    'shop-initThrift',
    ['jquery'],
    function( $ ){
        var transport = new Thrift.Transport("/thrift/ShopService");
        var protocol = new Thrift.Protocol(transport);
        var client = new com.vmesteonline.be.shop.ShopServiceClient(protocol);

        transport = new Thrift.Transport("/thrift/UserService");
        protocol = new Thrift.Protocol(transport);
        var userServiceClient = new com.vmesteonline.be.UserServiceClient(protocol);

        return {
            client: client,
            userServiceClient: userServiceClient
        }

    }
);