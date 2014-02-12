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
	10:map<PaymentType,double> paymentTypes
}

struct Producer {
	1:i64 id,
	2:string name,
	3:string descr,
	4:binary logoURL,
	5:string homeURL
}

struct ProductCategory {
	1:i64 id,
	2:i64 parentId,
	3:string name,
	4:string descr,
	5:list<binary> logoURLset
	6:list<i64> topicSet
}

enum PriceType { RETAIL=0, INET=1, VIP=2, SPECIAL=3 }

struct ProductDetails {
	1:list<i64> categories,
	2:string fullDescr,
	3:list<binary> imagesURLset
	4:map<PriceType,double> pricesMap,
	5:map<string,string> optionsMap,
	6:list<i64> topicSet,
	7:i64 producerId,
}

struct Product {
	1:i64 id,
	2:string name,
	3:string shortDescr,
	4:double weight,
	5:binary imageURL,
	6:double price,
}

struct FullProductInfo {
	1:Product product,
	2:ProductDetails details,
}

enum OrderStatus { UNKNOWN=0, NEW=1, CONFIRMED=2, SHIPPING=3, DELIVERED=4, CLOSED=5, CANCELED=6 }
enum PaymentStatus { UNKNOWN=0, WAIT=1, PENDING=2, COMPLETE=3, CREDIT=4 }

struct OrderLine {
	1:Product product,
	2:double quantity,
	4:double price   
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
}

struct Order {
	1:i64 id,
	2:i32 date
	3:OrderStatus status,
	4:PriceType priceType,
	5:double totalCost,
} 

struct FullOrder {
	1:Order order,
	2:OrderDetails details
}

struct ProductListPart {
	1:list<Product> products,
	2:i32 length
}

enum ExchangeFieldType {

	SHOP_ID=10, SHOP_NAME,SHOP_DESCRIPTION,SHOP_ADDRESS,SHOP_LOGOURL,SHOP_OWNERID,SHOP_TOPICS,SHOP_TAGSAVP,
	SHOP_DELIVERY_COST_AVP,
	SHOP_PAYMENT_COST_AVP,
	
	PRODUCER_NAME_ID = 100, PRODUCER_NAME, PRODUCER_DESCRIPTION, PRODUCER_LOGOURL, PRODUCER_HOMEURL,

	CATEGORY_ID = 200, CATEGORY_PARENT_NAME, CATEGORY_NAME, CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS,
	
	PRODUCT_ID=300, PRODUCT_NAME,	PRODUCT_SHORT_DESCRIPTION, PRODUCT_WEIGHT, PRODUCT_IMAGEURL, PRODUCT_PRICE, PRODUCT_CATEGORY_NAMES,
	PRODUCT_FULL_DESCRIPTION, PRODUCT_IMAGE_URLS, PRODUCT_PRICE_RETAIL, PRODUCT_PRICE_INET, PRODUCT_PRICE_VIP, PRODUCT_PRICE_SPECIAL,
	PRODUCT_OPIONSAVP, PRODUCT_TOPICS, PRODUCT_PRODUCER_NAME,
	
	DATE_TYPE=400, DATE_DATE,
	
	ORDER_ID = 1000, ORDER_DATE, ORDER_STATUS, ORDER_PRICE_TYPE, ORDER_TOTAL_COST, 
	ORDER_CREATED, ORDER_DELIVERY_TYPE, ORDER_DELIVERY_COST, ORDER_DELIVERY_ADDRESS,ORDER_PAYMENT_TYPE, ORDER_PAYMENT_STATUS,
	ORDER_COMMENT,
	
//ORDER LINES CONTAINS OPRDER_ID, PRODUCT_ID, PRODUCT_NAME, PRODUCER_ID, PRODUCER_NAME,    	
	ORDER_LINE_QUANTITY = 1100, ORDER_LINE_PRICE,
} 
service ShopService {
	
	//backend functions=================================================================================================
	
	i64 registerShop( 1:Shop shop) throws (1:error.InvalidOperation exc),
	i64 registerProductCategory( 1:ProductCategory productCategory, 2:i64 shopId) throws (1:error.InvalidOperation exc),
	i64 registerProducer( 1:Producer producer, 2:i64 shopId, ) throws (1:error.InvalidOperation exc),
	
	/**
	* Method uploads products to the shop that by parameter shopId. All value of image URLS may contain a JPEG image data or HTTP url 
	* to pull the image from.   
	**/
	list<i64> uploadProducts( 1:list<FullProductInfo> products, 2:i64 shopId, 3:bool cleanShopBeforeUpload ) throws (1:error.InvalidOperation exc),
	/**
	* Method uploads categories. List in the request should contain relative values of  and return list with updated values of id, parentId
	* and URLS replaced to local. Any of URL parameter may contain JPEG image data.
	**/
	list<ProductCategory> uploadProductCategoies( 1:list<ProductCategory> categories, 2:bool relativeIds, 3:bool cleanShopBeforeUpload )
		throws (1:error.InvalidOperation exc),
	/**
	* Method returns full orders information. userId and shopId may be used as a filter by defining not 0 value 
	**/
	list<Order> getFullOrders(1:i32 dateFrom, 2:i32 dateTo, 3:i64 userId, 4:i64 shopId) throws (1:error.InvalidOperation exc),
	void updateOrderStatusesById( 1:map<i64,OrderStatus> orderStatusMap ) throws (1:error.InvalidOperation exc),
	
	void setDates( 1:map<i32,DateType> dateDateTypeMap),
	void setDeliveryCosts( 1:map<DeliveryType,double> newDeliveryCosts) throws (1:error.InvalidOperation exc),
	void setPaymentTypesCosts( 1:map<PaymentType,double> newPaymentTypeCosts) throws (1:error.InvalidOperation exc),
	
	void setOrderPaymentStatus(1:i64 orderId, 2: PaymentStatus newStatus) throws (1:error.InvalidOperation exc),
	void setOrderStatus(1:i64 orderId, 2: OrderStatus newStatus) throws (1:error.InvalidOperation exc),
	/**
	* Method updates prices for Products. Map contains productId and map of new prices values for types.
	**/
	void setProductPrices( 1: map<i64, map<PriceType,double>> newPricesMap) throws (1:error.InvalidOperation exc),
	
	void updateProduct( 1:FullProductInfo newInfoWithOldId ) throws (1:error.InvalidOperation exc),
	void updateShop( 1:Shop newShopWithOldId ) throws (1:error.InvalidOperation exc),
	void updateCategory( 1:ProductCategory newCategoryInfo) throws (1:error.InvalidOperation exc),
	
	//IMPORT-EXPORT
	
	//list<map<String>> importProducers( 1:list<ExchangeFieldType> fieldsOrder,  2:string fileName, 3:binary fileData, 4:bool doClean );
	//list<map<String>> importCategiories( 1:list<ExchangeFieldType> fieldsOrder,  2:string fileName, 3:binary fileData, 4:bool doClean );
	//list<map<String>> importProducts( 1:list<ExchangeFieldType> fieldsOrder,  2:string fileName, 3:binary fileData, 4:bool doClean );
	
	
	//list<map<String>> importProducts( 1:list<ExchangeFieldType> fieldsOrder,  2:string fileName, 3:binary fileData, 4:bool doClean );

	//frontend functions================================================================================================
	list<Shop> getShops() throws (1:error.InvalidOperation exc),
	map<i32,DateType> getDates(1:i32 from, 2: i32 to) throws (1:error.InvalidOperation exc),
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
	
	/**
	Order operations use shopId that must be set by AuthService.setCurrentAttribute or by calling method getShop
	**/
	list<Order> getOrders(1:i32 dateFrom, 2:i32 dateTo) throws (1:error.InvalidOperation exc),
	list<Order> getOrdersByStatus(1:i32 dateFrom, 2:i32 dateTo, 3:OrderStatus status) throws (1:error.InvalidOperation exc),
	//returns order and made in current
	Order getOrder( 1:i64 orderId) throws (1:error.InvalidOperation exc),
	/**
	Method sets orderId as a current order id for the session
	**/
	OrderDetails getOrderDetails(1:i64 orderId) throws (1:error.InvalidOperation exc),
	/**
	* Method returns id of new order and set is as a current
	**/
	i64 createOrder(1:i32 date, 2:PriceType priceType) throws (1:error.InvalidOperation exc),
	i64 cancelOrder() throws (1:error.InvalidOperation exc),
	i64 confirmOrder() throws (1:error.InvalidOperation exc),
	/**
	* Method adds all orderLines from order with id set in parameter to current order. 
	* All Lines with the same product ID would summarized! 
	**/
	i64 appendOrder(1:i64 oldOrderId) throws (1:error.InvalidOperation exc),
	/**
	* Method adds to current order Order lines for products that are not included to current order
	**/
	i64 mergeOrder(1:i64 oldOrderId) throws (1:error.InvalidOperation exc),
	
	/**
	* Methods adds or replaces line to the current order that set by createOrder, or getOrderDetails method
	* it returns orderline that is with price set
	**/
	OrderLine setOrderLine( 1:i64 productId, 2:double quontity ) throws (1:error.InvalidOperation exc),
	bool removeOrderLine(1:i64 productId) throws (1:error.InvalidOperation exc),
	/**
	* Method returns Order details that contains new value of postal address and delivery cost of order delivery 
	**/
	OrderDetails setOrderDeliveryType( 1:DeliveryType deliveryType ) throws (1:error.InvalidOperation exc),
	bool setOrderPaymentType( 1:PaymentType paymentType ) throws (1:error.InvalidOperation exc),
	OrderDetails setOrderDeliveryAddress( 1:bedata.PostalAddress deliveryAddress ) throws (1:error.InvalidOperation exc),
	
}
