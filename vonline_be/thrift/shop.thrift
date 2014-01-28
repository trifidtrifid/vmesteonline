namespace * com.vmesteonline.be.shop
include "bedata.thrift"
include "error.thrift"

struct Shop {
	1:i64 id,
	2:string name,
	3:string descr,
	4:string address,
	5:string logoURL,
	6:i64 ownerId,
	7:set<i64> topicSet,
	8:set<string> tags
}

struct ProductCategory {
	1:i64 id,
	2:i64 parentId,
	3:string name,
	4:string descr,
	5:set<string> logoURLset
	6:set<i64> topicSet
}

enum PriceType { RETAIL=0, INET=1, VIP=2, SPECIAL=3 }

struct ProductDetails {
	1:set<i64> categories,
	2:string fullDescr,
	3:set<string> imagesURLset
	4:map<PriceType,double> pricesMap,
	5:map<string,string> optionsMap,
	6:set<i64> topicSet,
	7:i64 producerId,
}

struct Producer {
	1:i64 id,
	2:string name,
	3:string descr,
	4:string logoURL,
	5:string homeURL
}

struct Product {
	1:i64 id,
	2:i64 shopId,
	4:string name,
	5:string shortDescr,
	6:double weight,
	7:string imageURL,
	8:double price,
}

enum OrderStatus { UNKNOWN=0, NEW=1, PENDING=2, SHIPPING=3, DELIVERED=4, CLOSED=5 }
enum DeliveryType { UNKNOWN=0, SELF_PICKUP=1, SHORT_RANGE=2, LONG_RANGE=3 }
enum PaymentType { UNKNOWN=0, CASH=1, CREDIT_CARD=2, TRANSFER=3 }
enum PaymentStatus { UNKNOWN=0, CASH=1, CREDIT_CARD=2, TRANSFER=3 }

struct PostalAddress {
	1:i32 countryId,
	2:i32 cityId,
}

struct OrderDetails {
	1:i32 dateCreated,
	2:DeliveryType delivery,
	3:double deliveryCost,
	4:bedata.PostalAddress deliveryTo,
	5:PaymentType paymentType,
	6:PaymentStatus paymentStatus
	7:string comment, 
}

struct Order {
	1:i64 id,
	2:i32 date
	3:OrderStatus status,
	4:double totalCost,
	5:OrderDetails details
} 

service ShopService {
	
	bool login( 1:string email, 2:string password ) throws (1:error.InvalidOperation exc),
	i64 registerNewUser(1:string firstname, 2:string lastname, 3:string password, 4:string email, 5:string locationId) throws (1:error.InvalidOperation exc)
	void logout() throws (1:error.InvalidOperation exc),
	
	//administrative functions
	i64 registerShop( 1:Shop shop)
	i64 registerProductCategory( 1:ProductCategory productCategory)
	i64 registerProducer( 1:Producer producer )
}