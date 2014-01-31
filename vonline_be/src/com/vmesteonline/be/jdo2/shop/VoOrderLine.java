package com.vmesteonline.be.jdo2.shop;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.shop.OrderLine;
import com.vmesteonline.be.shop.PriceType;

@PersistenceCapable
public class VoOrderLine {

	public VoOrderLine(VoOrder order, VoProduct product, double quontity, PriceType priceType, double price) throws InvalidOperation {	
			this.quontity = quontity;
			this.priceType = priceType;
			this.price = price;
			this.product = product;
			this.order = order;
	}


	public OrderLine getOrderLine() {
		return new OrderLine(product.getProduct(), quontity, priceType, price);
	}

	@Persistent
	private VoOrder order;

	@Persistent
	@Unowned
	private VoProduct product;
	@Persistent
	@Unindexed
	private double quontity;

	@Persistent
	private PriceType priceType;

	@Persistent
	@Unindexed
	private double price;

	public VoOrder getOrder() {
		return order;
	}

	public void setOrder(VoOrder order) {
		this.order = order;
	}

	public VoProduct getProduct() {
		return product;
	}

	public void setProduct(VoProduct product) {
		this.product = product;
	}

	public double getQuontity() {
		return quontity;
	}

	public void setQuontity(double quontity) {
		this.quontity = quontity;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "VoOrderLine [order=" + order + ", product=" + product + ", quontity=" + quontity + ", priceType=" + priceType + ", price=" + price + "]";
	}


	public VoOrderLine mergeWith(VoOrder toOrder, VoOrderLine that) throws InvalidOperation {
		if(null==that)
			return new VoOrderLine(toOrder, this.product, this.quontity, this.priceType, this.price);
		else
			return new VoOrderLine(toOrder, this.product, this.quontity + that.getQuontity(), 
					this.priceType == that.priceType ? this.priceType : PriceType.MERGED, this.price + that.quontity * this.product.getPrice());
	}
	
	
}
