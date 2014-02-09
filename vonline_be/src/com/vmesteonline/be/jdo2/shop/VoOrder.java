package com.vmesteonline.be.jdo2.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDetails;
import com.vmesteonline.be.shop.OrderLine;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;

@PersistenceCapable
public class VoOrder {

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public VoOrder(VoUser user, long shopId, int date, PriceType priceType, PersistenceManager _pm) throws InvalidOperation{
		this.user = user;
		this.date = date;
		this.shopId = shopId;
		this.createdAt = (int)(System.currentTimeMillis()/1000L);
		if( date < createdAt )
			throw new InvalidOperation(VoError.IncorrectParametrs, "Date for order must be in the future, but provided ("+date+") in the past: "+ new Date(date*1000));
		
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		this.status = OrderStatus.NEW;
		this.delivery = DeliveryType.SELF_PICKUP;
		this.deliveryCost = 0D;
		this.deliveryTo = user.getAddress();
		this.totalCost = 0D;
		this.paymentType = PaymentType.CASH;
		this.paymentStatus = PaymentStatus.WIAT;
		this.odrerLines = new TreeSet<VoOrderLine>( new Comparator<VoOrderLine>(){

			@Override
			public int compare(VoOrderLine o1, VoOrderLine o2) {
				return null == o1.getProduct() ? null == o1.getProduct() ? 0 : 1 :
					Long.compare(o1.getProduct().getId(),o1.getProduct().getId());
			}
			
		});
		this.priceType = priceType; 
		try{
			pm.makePersistent(this);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to create order. "+ex.getMessage());
		} finally {
			if( _pm != null) pm.close();
		}
	}
	
	public double addOrderLine( OrderLine orderLine ) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoProduct product = pm.getObjectById(VoProduct.class, orderLine.getProduct().getId());
			VoOrderLine voOrderLine = new VoOrderLine(this, product, orderLine.getQuantity(), product.getPrice( priceType));
			this.odrerLines.add( voOrderLine );
			this.incrementTotalCost(voOrderLine.getPrice());
			pm.makePersistent( voOrderLine );
			pm.makePersistent(this);
			currentTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			currentTransaction.rollback();
		} finally {
			pm.close();
		}
		return this.getTotalCost();
	}
	
	public Order getOrder(){
		return new Order(id.getId(), date, status, priceType, totalCost);
	} 
	public OrderDetails getOrderDetails(){
		OrderDetails od = new OrderDetails(createdAt, delivery, deliveryCost, 
				deliveryTo.getPostalAddress(), paymentType, paymentStatus,
				new ArrayList<OrderLine>(), comment);
		for( VoOrderLine vol: odrerLines){
			od.odrerLines.add(vol.getOrderLine());
		}
		return od;
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
  private Key id;
	
	@Persistent
	@Unowned
	private VoUser user;
	
	@Persistent
	private long shopId;
	
	@Persistent
  private int date;
	
	@Persistent
  private OrderStatus status;
	
  @Persistent
  @Unindexed
  private double totalCost;
  
  @Persistent
  @Unindexed
  private int createdAt;
  
  @Persistent
  private DeliveryType delivery;
  
  @Persistent
  @Unindexed
  private double deliveryCost;
  
  @Persistent
  @Unowned
  @Unindexed
  private VoPostalAddress deliveryTo;
  
  @Persistent
  private PaymentType paymentType;
  
  @Persistent
  private PaymentStatus paymentStatus;
  
  @Persistent(mappedBy="order")
  @OneToMany
  @Unindexed
  private SortedSet<VoOrderLine> odrerLines;
  
  @Persistent
  private PriceType priceType; 
  
  @Persistent
  @Unindexed 
  private String comment;

	public int getDate() {
		return date;
	}

	public double addCost(double cost){
		return totalCost += cost;
	}
	
	public void setDate(int date) {
		this.date = date;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	
	public double incrementTotalCost(double delta) {
		this.totalCost += delta;
		return this.totalCost;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public DeliveryType getDelivery() {
		return delivery;
	}

	public void setDelivery(DeliveryType delivery) {
		this.delivery = delivery;
	}

	public double getDeliveryCost() {
		return deliveryCost;
	}

	public void setDeliveryCost(double deliveryCost) {
		this.deliveryCost = deliveryCost;
	}

	public VoPostalAddress getDeliveryTo() {
		return deliveryTo;
	}

	public void setDeliveryTo(VoPostalAddress deliveryTo) {
		this.deliveryTo = deliveryTo;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getId() {
		return id.getId();
	}

	public VoUser getUser() {
		return user;
	}

	public SortedSet<VoOrderLine> getOdrerLines() {
		return odrerLines;
	}

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	@Override
	public String toString() {
		return "VoOrder [id=" + id + ", user=" + user + ", date=" + date + ", status=" + status + ", totalCost=" + totalCost + ", createdAt=" + createdAt
				+ "]";
	}

	public String toFullString() {
		return "VoOrder [id=" + id + ", user=" + user + ", date=" + date + ", status=" + status + ", totalCost=" + totalCost + ", createdAt=" + createdAt
				+ ", delivery=" + delivery + ", deliveryCost=" + deliveryCost + ", deliveryTo=" + deliveryTo + ", paymentType=" + paymentType
				+ ", paymentStatus=" + paymentStatus + "]";
	}
  
  
}
