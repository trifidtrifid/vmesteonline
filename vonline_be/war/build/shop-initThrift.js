define("shop-initThrift",["jquery"],function(){var a=new Thrift.Transport("/thrift/ShopService"),b=new Thrift.Protocol(a),c=new com.vmesteonline.be.shop.ShopFEServiceClient(b);a=new Thrift.Transport("/thrift/ShopBOService"),b=new Thrift.Protocol(a);var d=new com.vmesteonline.be.shop.bo.ShopBOServiceClient(b);a=new Thrift.Transport("/thrift/UserService"),b=new Thrift.Protocol(a);var e=new com.vmesteonline.be.UserServiceClient(b);a=new Thrift.Transport("/thrift/AuthService"),b=new Thrift.Protocol(a);var f=new com.vmesteonline.be.AuthServiceClient(b);return{client:c,clientBO:d,userClient:e,authClient:f}});