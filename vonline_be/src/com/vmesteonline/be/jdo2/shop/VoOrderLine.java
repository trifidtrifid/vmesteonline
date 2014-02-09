package com.vmesteonline.be.jdo2.shop;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.shop.OrderLine;

@PersistenceCapable
public class VoOrderLine {

	public VoOrderLine(VoOrder order, VoProduct product, double quantity, double price) throws InvalidOperation {
		this.quantity = quantity;
		this.product = product;
		this.order = order;
		this.price = price;
	}

	public VoOrderLine( long productId) {
		this.product = new VoProduct(productId);
	}
	public OrderLine getOrderLine() {
		OrderLine orderLine = new OrderLine(product.getProduct(), quantity, price);
		orderLine.product.price = price;
		return orderLine;
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	@Persistent
	private VoOrder order;

	@Persistent
	@Unowned
	private VoProduct product;
	@Persistent
	@Unindexed
	private double quantity;

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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "VoOrderLine [order=" + order + ", product=" + product + ", quontity=" + quantity + ", price=" + price + "]";
	}

	public Key getId() {
		return id;
	}

	public Double mergeWith(VoOrderLine that) throws InvalidOperation {
		if (null == that)
			return 0.0D;
		this.quantity += that.getQuantity();
		return that.quantity * this.price;
	}

}
