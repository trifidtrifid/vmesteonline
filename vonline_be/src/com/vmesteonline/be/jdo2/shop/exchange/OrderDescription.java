package com.vmesteonline.be.jdo2.shop.exchange;

import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.ExchangeFieldType;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;

public class OrderDescription {
	
	public long orderId;
	public int date;
	public OrderStatus status;
	public PriceType priceType;
	public double tatalCost;
	public int createdDate;
	public DeliveryType deliveryType;
	public double deliveryCost;
	public String deliveryAddress;
	public PaymentType paymentType;
	public PaymentStatus paymentStatus;
	public String comment;
	public long userId;
	public String userName;
	
	/*public static final ExchangeFieldType[] fullFieldsList = new ExchangeFieldType[] {
		ExchangeFieldType.ORDER_ID, ExchangeFieldType.ORDER_DATE, ExchangeFieldType.ORDER_STATUS, ExchangeFieldType.ORDER_PRICE_TYPE,
		ExchangeFieldType.ORDER_TOTAL_COST, ExchangeFieldType.ORDER_CREATED, ExchangeFieldType.ORDER_DELIVERY_TYPE, 
		ExchangeFieldType.ORDER_DELIVERY_COST, ExchangeFieldType.ORDER_DELIVERY_ADDRESS, ExchangeFieldType.ORDER_PAYMENT_TYPE,
		ExchangeFieldType.ORDER_PAYMENT_STATUS, ExchangeFieldType.ORDER_COMMENT, ExchangeFieldType.ORDER_USER_ID, ExchangeFieldType.ORDER_USER_NAME };*/
}
