namespace * com.vmesteonline.be.shop.bo
include "shop.thrift"
include "bedata.thrift"
include "error.thrift"

enum ExchangeFieldType {
	
	SHOP_ID=10, SHOP_NAME,SHOP_DESCRIPTION,SHOP_ADDRESS,SHOP_LOGOURL,SHOP_OWNERID,SHOP_TOPICS,SHOP_TAGS,
	SHOP_DELIVERY_COST_AVP,
	SHOP_PAYMENT_COST_AVP,
	
	PRODUCER_ID = 100, PRODUCER_NAME, PRODUCER_DESCRIPTION, PRODUCER_LOGOURL, PRODUCER_HOMEURL,

	CATEGORY_ID = 200, CATEGORY_PARENT_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS,
	
	PRODUCT_ID=300, PRODUCT_NAME,	PRODUCT_SHORT_DESCRIPTION, PRODUCT_WEIGHT, PRODUCT_IMAGEURL, PRODUCT_PRICE, PRODUCT_CATEGORY_IDS,
	PRODUCT_FULL_DESCRIPTION, PRODUCT_IMAGE_URLS, PRODUCT_PRICE_RETAIL, PRODUCT_PRICE_INET, PRODUCT_PRICE_VIP, PRODUCT_PRICE_SPECIAL,
	PRODUCT_OPIONSAVP, PRODUCT_TOPICS, PRODUCT_PRODUCER_ID, PRODUCT_MIN_CLN_PACK, PRODUCT_MIN_PROD_PACK, PRODUCT_PREPACK_REQ, 
	PRODUCT_KNOWN_NAMES, PRODUCT_UNIT_NAME
	
	DATE_TYPE=400, DATE_DATE,
	
	ORDER_ID = 1000, ORDER_DATE, ORDER_STATUS, ORDER_PRICE_TYPE, ORDER_TOTAL_COST, 
	ORDER_CREATED, ORDER_DELIVERY_TYPE, ORDER_DELIVERY_COST, ORDER_DELIVERY_ADDRESS, ORDER_PAYMENT_TYPE, ORDER_PAYMENT_STATUS,
	ORDER_COMMENT, ORDER_WEIGHT, ORDER_USER_ID, ORDER_USER_NAME,
	
	ORDER_LINE_ID = 1100, ORDER_LINE_QUANTITY, ORDER_LINE_OPRDER_ID, ORDER_LINE_PRODUCT_ID, ORDER_LINE_PRODUCT_NAME, ORDER_LINE_PRODUCER_ID, ORDER_LINE_PRODUCER_NAME, 
	ORDER_LINE_PRICE, ORDER_LINE_COMMENT, ORDER_LINE_PACKETS
	
	//product report
	TOTAL_PROUCT_ID=2000, TOTAL_PRODUCT_NAME, TOTAL_PRODUCER_ID, TOTAL_PRODUCER_NAME, TOTAL_PRODUCT_MIN_PACK, TOTAL_ORDERED, TOTAL_MIN_QUANTITY, TOTAL_REST, TOTAL_PREPACK_REQUIRED,
	//pack variants report by delivery type
	TOTAL_PACK_SIZE,TOTAL_PACK_QUANTYTY, TOTAL_DELIVERY_TYPE  
} 

enum ImExType { IMPORT_SHOP = 10, IMPORT_PRODUCERS, IMPORT_CATEGORIES, IMPORT_PRODUCTS, 
	EXPORT_ORDERS=20, EXPORT_ORDER_LINES, EXPORT_TOTAL_PRODUCT, EXPORT_TOTAL_PACK  }


struct ImportElement {
	1:ImExType type,
	2:string fileName,
	3:map<i32,ExchangeFieldType> fieldsMap,
	4:optional string url,
	5:optional bedata.MatrixAsList fieldsData, //returned in response if empty in request
}

struct DataSet {
	1:optional i64 id, //should not been initialized first time by FE.
	2:string name,
	3:i32 date,
	4:list<ImportElement> data,
}


service ShopBOService {
	//backend functions=================================================================================================
	
	i64 registerShop( 1:shop.Shop shop) throws (1:error.InvalidOperation exc),
	i64 registerProductCategory( 1:shop.ProductCategory productCategory, 2:i64 shopId) throws (1:error.InvalidOperation exc),
	i64 registerProducer( 1:shop.Producer producer, 2:i64 shopId, ) throws (1:error.InvalidOperation exc),
	i64 registerProduct( 1:shop.FullProductInfo fpi, 2:i64 shopId ) throws (1:error.InvalidOperation exc),
	
	void setShopDeliveryByWeightIncrement( 1:i64 shopId, 2:map<i32,i32> deliveryByWeightIncrement) throws (1:error.InvalidOperation exc),
	void setShopDeliveryCostByDistance( 1:i64 shopId, 2:map<i32,double> deliveryCostByDistance) throws (1:error.InvalidOperation exc),
	void setShopDeliveryTypeAddressMasks( 1:i64 shopId, 2:map<shop.DeliveryType,string> deliveryTypeAddressMasks) throws (1:error.InvalidOperation exc),
	
	/**
	* Method uploads products to the shop that by parameter shopId. All value of image URLS may contain a JPEG image data or HTTP url 
	* to pull the image from.   
	**/
	list<i64> uploadProducts( 1:list<shop.FullProductInfo> products, 2:i64 shopId, 3:bool cleanShopBeforeUpload ) throws (1:error.InvalidOperation exc),
	/**
	* Method uploads categories. List in the request should contain relative values of  and return list with updated values of id, parentId
	* and URLS replaced to local. Any of URL parameter may contain JPEG image data.
	**/
	list<shop.ProductCategory> uploadProductCategoies( 1:list<shop.ProductCategory> categories, 2:bool cleanShopBeforeUpload )
		throws (1:error.InvalidOperation exc),
	/**
	* Method returns full orders information. userId and shopId may be used as a filter by defining not 0 value 
	**/
	list<shop.Order> getFullOrders(1:i32 dateFrom, 2:i32 dateTo, 3:i64 userId, 4:i64 shopId) throws (1:error.InvalidOperation exc),
	void updateOrderStatusesById( 1:map<i64,shop.OrderStatus> orderStatusMap ) throws (1:error.InvalidOperation exc),
	
	//void setDates( 1:map<i32,DateType> dateDateTypeMap),
	void setDate( 1:shop.OrderDates dates ) throws (1:error.InvalidOperation exc), 
	void removeDate( 1:shop.OrderDates dates ) throws (1:error.InvalidOperation exc), 
	
	void setDeliveryCosts( 1:map<shop.DeliveryType,double> newDeliveryCosts) throws (1:error.InvalidOperation exc),
	void setPaymentTypesCosts( 1:map<shop.PaymentType,double> newPaymentTypeCosts) throws (1:error.InvalidOperation exc),
	
	void setOrderPaymentStatus(1:i64 orderId, 2: shop.PaymentStatus newStatus) throws (1:error.InvalidOperation exc),
	void setOrderStatus(1:i64 orderId, 2: shop.OrderStatus newStatus) throws (1:error.InvalidOperation exc),
	/**
	* Method updates prices for Products. Map contains productId and map of new prices values for types.
	**/
	void setProductPrices( 1: map<i64, map<shop.PriceType,double>> newPricesMap) throws (1:error.InvalidOperation exc),
	
	void updateProduct( 1:shop.FullProductInfo newInfoWithOldId ) throws (1:error.InvalidOperation exc),
	void updateShop( 1:shop.Shop newShopWithOldId ) throws (1:error.InvalidOperation exc),
	void updateCategory( 1:shop.ProductCategory newCategoryInfo) throws (1:error.InvalidOperation exc),
	void updateProducer( 1:shop.Producer newInfoWithOldId ) throws (1:error.InvalidOperation exc),
	
	//IMPORT-EXPORT BACK OFFICE

	DataSet importData(1:DataSet data) throws (1:error.InvalidOperation exc),
	DataSet getTotalOrdersReport( 1:i32 date, 2:shop.DeliveryType deliveryType, 
		3:map<i32,ExchangeFieldType> orderFields, 4:map<i32,ExchangeFieldType> orderLineFIelds) throws (1:error.InvalidOperation exc),
	DataSet getTotalProductsReport( 1:i32 date, 2:shop.DeliveryType deliveryType,
		3:map<i32,ExchangeFieldType> productFields ) throws (1:error.InvalidOperation exc),
	DataSet getTotalPackReport( 1:i32 date, 2:shop.DeliveryType deliveryType,
		3:map<i32,ExchangeFieldType> packFields ) throws (1:error.InvalidOperation exc),
		
	bedata.MatrixAsList parseCSVfile( 1:string url ) throws (1:error.InvalidOperation exc),
}