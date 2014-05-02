namespace * com.vmesteonline.be.shop
include "bedata.thrift"
include "error.thrift"

enum DateType { CLEAN=0, NEXT_ORDER=1, SPECIAL_PRICE=2, CLOSED=3  }
enum DeliveryType { UNKNOWN=0, SELF_PICKUP=1, SHORT_RANGE=2, LONG_RANGE=3 }
enum PaymentType { UNKNOWN=0, CASH=1, CREDIT_CARD=2, TRANSFER=3, SHOP_CREDIT=5 }

struct Shop {
	1:i64 id,
	2:string name,
	3:string descr,
	4:bedata.PostalAddress address,
	5:string logoURL,
	6:i64 ownerId,
	7:list<i64> topicSet,
	8:list<string> tags,
	9:map<DeliveryType,double> deliveryCosts,
	10:map<PaymentType,double> paymentTypes,
	11:optional map<i32,i32> deliveryByWeightIncrement,
	12:optional map<i32,double> deliveryCostByDistance,
	13:optional map<DeliveryType,string> deliveryTypeAddressMasks, 
}

struct Producer {
	1:i64 id,
	2:string name,
	3:string descr,
	4:string logoURL,
	5:string homeURL
}

struct ProductCategory {
	1:i64 id,
	2:i64 parentId,
	3:string name,
	4:string descr,
	5:list<string> logoURLset,
	6:list<i64> topicSet,
	7:i32 productCount,
}

enum PriceType { RETAIL=0, INET=1, VIP=2, SPECIAL=3 }

struct ProductDetails {
	1:list<i64> categories,
	2:string fullDescr,
	3:list<string> imagesURLset
	4:map<PriceType,double> pricesMap,
	5:map<string,string> optionsMap,
	6:list<i64> topicSet,
	7:i64 producerId,
	9:double minProducerPack,
	10:set<string> knownNames,
}

struct Product {
	1:i64 id,
	2:string name,
	3:string shortDescr,
	4:double weight,
	5:string imageURL,
	6:double price,
	7:string unitName,
	8:double minClientPack,
	9:i64 shopId,
	10:bool prepackRequired,
}

struct FullProductInfo {
	1:Product product,
	2:ProductDetails details,
}

//struct to use to define dates of orde for a shop
enum OrderDatesType { ORDER_WEEKLY, ORDER_MOUNTHLY }
struct OrderDates {
	1:OrderDatesType type, 
	2:i32 orderDay,//day of week or day month and so
	3:i32 orderBefore, //days gap between the order date and create the order date dedline
	4:i32 eachOddEven, //field takes value 0,1,2 for each periods, odd periods and even periods accordingly
	5:PriceType priceTypeToUse 
}
struct OrderDate { //struct to use in responses for FE
	1:i32 orderDate,
	2:PriceType priceType
}

enum OrderStatus { UNKNOWN=0, NEW=1, CONFIRMED=2, SHIPPING=3, DELIVERED=4, CLOSED=5, CANCELED=6 }
enum PaymentStatus { UNKNOWN=0, WAIT=1, PENDING=2, COMPLETE=3, CREDIT=4 }

struct OrderLine {
	1:Product product,
	2:double quantity,
	3:double price
	4:optional map<double, i32> packs; //can be applied to prepacked products if customer wants to buy several packet of 
	//different weight
	5:optional string comment    
}

struct OrderDetails {
	1:i32 createdAt,
	2:DeliveryType delivery,
	3:double deliveryCost,
	4:bedata.PostalAddress deliveryTo,
	5:PaymentType paymentType,
	6:PaymentStatus paymentStatus,
	7:list<OrderLine> odrerLines,
	8:string comment,
	9:i32 weightGramm 
}

struct Order {
	1:i64 id,
	2:i32 date
	3:OrderStatus status,
	4:PriceType priceType,
	5:double totalCost,
	6:i64 userId,
	7:string userName
} 

struct FullOrder {
	1:Order order,
	2:OrderDetails details
}

struct ProductListPart {
	1:list<Product> products,
	2:i32 length
}



service ShopFEService {
	
	//frontend functions================================================================================================
	list<Shop> getShops() throws (1:error.InvalidOperation exc),
	//map<i32,DateType> getDates(1:i32 from, 2: i32 to) throws (1:error.InvalidOperation exc),
	/**
	* MEthod returns the next date to create or change order is avialable.
	* If  afterDate = 0 then current date used instead of it
	**/
	OrderDate getNextOrderDate( 1:i32 afterDate ) throws (1:error.InvalidOperation exc),
	/**
	Method returns Shop information and set currentShopId to value of provided shopId parameter that would be used in all of methods followed below
	**/
	Shop getShop(1:i64 shopId) throws (1:error.InvalidOperation exc),

	//all of requests would use shopId that should be set by AuthService.setCurrentAttribute method
	list<Producer> getProducers(/*use shopId from current session*/) throws (1:error.InvalidOperation exc),
	//this request set session attribute PRODUCT_CATEGORY to value provided as the method call parameter 
	list<ProductCategory> getProductCategories( 1:i64 currentProductCategoryId ) throws (1:error.InvalidOperation exc),
	/** THe method returns full list of products that are included to current category and all subcategories, 
	* if categoryID is set then the current category would be set. 
	**/ 
	ProductListPart getProducts(1:i32 offset, 2:i32 length, 3:i64 categoryId ) throws (1:error.InvalidOperation exc),
	ProductDetails getProductDetails( 1:i64 productId ) throws (1:error.InvalidOperation exc),
	list<bedata.IdNameChilds> getProductsByCategories(1:i64 shopId) throws (1:error.InvalidOperation exc),
	
	/**
	Order operations use shopId that must be set by AuthService.setCurrentAttribute or by calling method getShop
	**/
	list<Order> getOrders(1:i32 dateFrom, 2:i32 dateTo) throws (1:error.InvalidOperation exc),
	list<Order> getOrdersByStatus(1:i32 dateFrom, 2:i32 dateTo, 3:OrderStatus status) throws (1:error.InvalidOperation exc),
	list<Order> getMyOrdersByStatus(1:i32 dateFrom, 2:i32 dateTo, 3:OrderStatus status) throws (1:error.InvalidOperation exc),

	//returns order and made in current
	Order getOrder( 1:i64 orderId) throws (1:error.InvalidOperation exc),
	/**
	Method sets orderId as a current order id for the session
	**/
	OrderDetails getOrderDetails(1:i64 orderId) throws (1:error.InvalidOperation exc),
	/**
	* Method returns id of new order and set is as a current
	**/
	i64 createOrder(1:i32 date, 2:string comment ) throws (1:error.InvalidOperation exc),
	void updateOrder( 1:i64 orderId, 2:i32 date, 3:string comment) throws (1:error.InvalidOperation exc),
	i64 cancelOrder(1:i64 orderId) throws (1:error.InvalidOperation exc),
	i64 deleteOrder(1:i64 orderId) throws (1:error.InvalidOperation exc),
	i64 confirmOrder(1:i64 orderId, 2:string comment) throws (1:error.InvalidOperation exc),
	/**
	* Method adds all orderLines from order with id set in parameter to current order. 
	* All Lines with the same product ID would summarized! 
	**/
	OrderDetails appendOrder(1:i64 orderId,2:i64 oldOrderId) throws (1:error.InvalidOperation exc),
	/**
	* Method adds to current order Order lines for products that are not included to current order
	**/
	OrderDetails mergeOrder(1:i64 orderId, 2:i64 oldOrderId) throws (1:error.InvalidOperation exc),
	
	/**
	* Methods adds or replaces line to the current order that set by createOrder, or getOrderDetails method
	* it returns orderline that is with price set
	**/
	OrderLine setOrderLine( 1:i64 orderId, 2:i64 productId, 3:double quantity, 4:string comment, 5:map<double, i32>  packets) throws (1:error.InvalidOperation exc),
	bool removeOrderLine(1:i64 orderId, 2:i64 productId) throws (1:error.InvalidOperation exc),
	/**
	* Method returns Order details that contains new value of postal address and delivery cost of order delivery 
	**/
	OrderDetails setOrderDeliveryType( 1:i64 orderId, 2:DeliveryType deliveryType, 3:bedata.PostalAddress deliveryAddress ) throws (1:error.InvalidOperation exc),
	bool setOrderPaymentType( 1:i64 orderId, 2:PaymentType paymentType ) throws (1:error.InvalidOperation exc),
	OrderDetails setOrderDeliveryAddress( 1:i64 orderId, 2:bedata.PostalAddress deliveryAddress ) throws (1:error.InvalidOperation exc),
	
	bedata.PostalAddress createDeliveryAddress(1:string  addressString, 2:i32 flat, 3:byte floor, 4:byte staircase, 5:string comment ) throws (1:error.InvalidOperation exc),
	bedata.MatrixAsList getUserDeliveryAddresses() throws (1:error.InvalidOperation exc),
	bedata.PostalAddress getUserDeliveryAddress(1:string addressText) throws (1:error.InvalidOperation exc),
	void deleteDeliveryAddress(1:string addressText ) throws (1:error.InvalidOperation exc),
	string getDeliveryAddressViewURL(1:string addressText, 2:i32 width, 3:i32 height ) throws (1:error.InvalidOperation exc),	
}
