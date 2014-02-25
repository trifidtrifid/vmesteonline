package com.vmesteonline.be.jdo2.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.shop.OrderLine;

@PersistenceCapable
public class VoOrderLine implements Comparable<VoOrderLine>{

	public VoOrderLine(long productId ){
		this.product = new VoProduct(productId);
	}
	public VoOrderLine(VoOrder order, VoProduct product, double quantity, double price, 
			String comment, Map<Double,Integer> packets) throws InvalidOperation {
		this.quantity = quantity;
		this.product = product;
		this.order = order;
		this.price = price;
		this.comment = comment;
		if( null!=packets && packets.size() > 1 ){
			if(product.isPrepackRequired()){
				this.packets = packets;
				//CHECK THAP PACKETS QONTITY MATCH the total quantity
				double tq = 0;
				for (Entry<Double, Integer> pe : packets.entrySet()) {
					tq += pe.getKey() * pe.getValue();
				}
				if( tq != quantity )
					throw new InvalidOperation(VoError.IncorrectParametrs, "Total quantity("+quantity+") of '"+product.getName()+"' does not meet summary of packets ("+tq+")!");
			} else { 
				throw new InvalidOperation(VoError.IncorrectParametrs, "Not prepacked product '"+product.getName()+"' can't have packets set, but provided "+packets.size()+"!");
			}
		}
	}

	public OrderLine getOrderLine() {
		OrderLine orderLine = new OrderLine(product.getProduct(), quantity, price);
		orderLine.product.price = price;
		if( null!=comment && comment.length() > 0 ) orderLine.setComment(comment);
		if( packets != null && packets.size() > 1 ) orderLine.setPacks(packets);
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
	private Map<Double,Integer> packets;

	@Persistent
	@Unindexed
	private String comment;


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

	@Override
	public int compareTo(VoOrderLine that) {
		return null == that ? -1 : null == that.product ? null == product ? 0 : -1 : product.getName().compareTo(that.product.getName());
	}
	
	public String getComment() {
		return comment;
	}
	
	public Map<Double,Integer> getPackets(){
		return packets;
	}
	public void setPackets(HashMap<Double, Integer> pMap) {
		packets = pMap;
	}

}
