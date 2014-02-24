package com.vmesteonline.be.jdo2.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Extension;
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

	private static final class OrderLineComparator implements Comparator<VoOrderLine> {
		@Override
		public int compare(VoOrderLine o1, VoOrderLine o2) {
			return null == o1.getProduct() ? null == o1.getProduct() ? 0 : 1 :
				o1.getProduct().getName().compareTo(o1.getProduct().getName());
		}
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public VoOrder(VoUser user, long shopId, int date, PriceType priceType, String comment, PersistenceManager _pm) throws InvalidOperation{
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
		this.paymentStatus = PaymentStatus.WAIT;
		this.orderLines = new HashMap<Long,VoOrderLine>();
		this.priceType = priceType;
		this.comment = comment;
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
		try {
			VoProduct product = pm.getObjectById(VoProduct.class, orderLine.getProduct().getId());
			
			VoOrderLine voOrderLine = new VoOrderLine(this, product, orderLine.getQuantity(), product.getPrice( priceType));
			VoOrderLine oldLine = this.orderLines.put( voOrderLine.getProduct().getId(), voOrderLine );
			this.incrementTotalCost(voOrderLine.getPrice()*voOrderLine.getQuantity() - oldLine.getPrice() * oldLine.getQuantity());
			pm.makePersistent( voOrderLine );
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return this.getTotalCost();
	}
	
	public Order getOrder(){
		return new Order(id.getId(), date, status, priceType, totalCost, user.getId(), user.getName() + " " + user.getLastName());
	} 
	public OrderDetails getOrderDetails(){
		OrderDetails od = new OrderDetails(createdAt, delivery, deliveryCost, 
				deliveryTo.getPostalAddress(), paymentType, paymentStatus,
				new ArrayList<OrderLine>(), comment);
		SortedSet<VoOrderLine> ols = new TreeSet<VoOrderLine>();
		if(null!=orderLines) ols.addAll(orderLines.values());
		for( VoOrderLine vol: ols){
			od.odrerLines.add(vol.getOrderLine());
		}
		return od;
	}
	
	public VoOrderLine getOrderLineByProduct( long productId ){
		return orderLines.get(productId);
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
  @Extension(key="comparator-name", value="com.vmesteonline.be.jdo2.shop.VoOrder.OrderLineComparator", vendorName="datanucleus")
  private Map<Long,VoOrderLine> orderLines;
  
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

	public Map<Long,VoOrderLine> getOrderLines() {
		return orderLines;
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
