package com.vmesteonline.be.jdo2.shop.exchange;

import com.vmesteonline.be.shop.DeliveryType;

public class ProductOrderDescription {

	/*product report
	TOTAL_PROUCT_ID=2000, TOTAL_PRODUCT_NAME, TOTAL_PRODUCER_ID, TOTAL_PRODUCER_NAME, TOTAL_PRODUCT_MIN_PACK, TOTAL_ORDERED, TOTAL_MIN_QUANTITY, TOTAL_REST, TOTAL_PREPACK_REQUIRED
	pack variants report by delivery type
	TOTAL_PACK_SIZE,TOTAL_PACK_QUANTYTY, TOTAL_DELIVERY_TYPE*/ 
	
	public long productId;
	public String productName;
	public long producerId;
	public String producerName;
	public double minUnitSize;
	public double orderedQuantity;
	public double minOrderQuantity; 
	public double restQuantity;
	public boolean prepackRequired;
	public double packSize;
	public int packQuantity;
	public DeliveryType deliveryType;
}
