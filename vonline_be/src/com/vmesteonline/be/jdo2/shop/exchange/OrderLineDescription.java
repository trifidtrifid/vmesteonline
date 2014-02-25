package com.vmesteonline.be.jdo2.shop.exchange;

import java.util.Map;
import java.util.SortedMap;

public class OrderLineDescription {
	
	public long lineId;
	public double quantity;
	public long orderId;
	public long productId;
	public String productName;
	public long producerId;
	public String producerName;
	public double price;
	public String comment;
	public SortedMap<Double,Integer> packets;
	
	/*public static final ExchangeFieldType[] fullFieldsList = new ExchangeFieldType[] {
		ExchangeFieldType.ORDER_LINE_ID, ExchangeFieldType.ORDER_LINE_QUANTITY, ExchangeFieldType.ORDER_LINE_OPRDER_ID, 
		ExchangeFieldType.ORDER_LINE_PRODUCT_ID, ExchangeFieldType.ORDER_LINE_PRODUCT_NAME, ExchangeFieldType.ORDER_LINE_PRODUCER_ID, 
		ExchangeFieldType.ORDER_LINE_PRODUCER_NAME, ExchangeFieldType.ORDER_LINE_PRICE };*/
}
