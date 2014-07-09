package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.OrderDate;
import com.vmesteonline.be.shop.OrderDates;
import com.vmesteonline.be.shop.OrderDatesType;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.utils.VoHelper;

@PersistenceCapable
public class VoShop {

	public VoShop(Shop shop) throws InvalidOperation {
		this(shop.getName(), shop.getDescr(), shop.getAddress(), shop.getLogoURL(), shop.getOwnerId(), shop.getTopicSet(), shop.getTags(), shop
				.getDeliveryCosts(), shop.getPaymentTypes());
	}

	public VoShop(String name, String descr, PostalAddress postalAddress, String logoURL, long ownerId, List<Long> topicSet, List<String> tags,
			Map<DeliveryType, Double> deliveryCosts, Map<PaymentType, Double> paymentTypes) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		this.activated = false;
		this.name = name;
		this.setDescr(descr);
		if (postalAddress != null)
			this.address = new VoPostalAddress(postalAddress, pm);
		try {
			VoHelper.replaceURL(this, "logoURL", logoURL, ownerId, true, pm);
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		}
		this.ownerId = ownerId;
		if (null == (this.tags = tags))
			this.tags = new ArrayList<String>();
		this.deliveryCosts = null == deliveryCosts ? new HashMap<Integer, Double>() : convertFromDeliveryTypeMap(deliveryCosts,
				new HashMap<Integer, Double>());
		if (null == deliveryCosts)
			this.deliveryCosts.put(DeliveryType.SELF_PICKUP.getValue(), 0D);
		this.paymentTypes = null == paymentTypes ? new HashMap<Integer, Double>() : convertFromPaymentTypeMap(paymentTypes,
				new HashMap<Integer, Double>());
		if (null == paymentTypes)
			this.paymentTypes.put(PaymentType.CASH.getValue(), 0D);

		try {
			if (null != topicSet) {
				this.topics = new ArrayList<Long>();
				topics.addAll(topicSet);
			}
			dates = new ArrayList<OrderDates>();
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to create shop." + e.getMessage());
		} finally {
			pm.close();
		}
	}

	public Shop getShop() {
		List<Long> topicIds = new ArrayList<Long>();
		List<Long> topicss = getTopics();
		if(null!=topicss) topicIds.addAll(topicss);
		Shop shop = new Shop(id.getId(), name, descr.getValue(), null == address ? null : address.getPostalAddress(), logoURL, ownerId, topicIds, tags,
				convertToDeliveryTypeMap(deliveryCosts, new HashMap<DeliveryType, Double>()), convertToPaymentTypeMap(paymentTypes,
						new HashMap<PaymentType, Double>()));
		shop.deliveryByWeightIncrement = deliveryByWeightIncrement;
		shop.deliveryCostByDistance = deliveryCostByDistance;
		shop.deliveryTypeAddressMasks = convertToDeliveryTypeMap( this.deliveryAddressMasksText, new HashMap<DeliveryType, String>());
		return shop;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unindexed
	private String name;
	@Persistent
	@Unindexed
	private Text descr;
	@Persistent
	@Unindexed
	@Unowned
	private VoPostalAddress address;
	@Persistent
	@Unindexed
	private String logoURL;

	@Persistent
	public long ownerId;

	@Persistent
	@Unowned
	public List<Long> topics;

	@Persistent
	public List<String> tags;

	@Persistent(serialized = "true")
	@Unindexed
	private List<OrderDates> dates;

	@Persistent
	@Unindexed
	private Map<Integer, Double> deliveryCosts;

	//Defines a text that describes delivery conditions for customer
	@Persistent
	@Unindexed
	private Map<Integer, String> deliveryConditionsText;
	
	@Persistent
	@Unindexed
	private Map<Integer, String> deliveryAddressMasksText;
	
	@Persistent
	@Unindexed
	private Map<Integer, Integer> deliveryByWeightIncrement;
	
	@Persistent
	@Unindexed
	private Map<Integer, Double> deliveryCostByDistance;
	
	@Persistent
	@Unindexed
	private Map<String, String> socialNetworks;
	
	@Persistent
	@Unindexed
	private String aboutShopPageContentURL;
	
	@Persistent
	@Unindexed
	private String conditionsPageContentURL;
	
	@Persistent
	@Unindexed
	private String deliveryPageContentURL;
	
	@Persistent
	private boolean activated;
	
	@Persistent
	@Unindexed
	private Map<String, Set<Long>> voteResults;
	
	
	@Persistent
	@Unindexed
	private Map<Integer, Double> paymentTypes;
	
	
	public Map<DeliveryType, String> getDeliveryConditionsText() {
		if(null==deliveryConditionsText) return null;
		
		Map<DeliveryType, String> out = new HashMap<DeliveryType, String>();
		for( Entry<Integer,String> in : deliveryConditionsText.entrySet())
			out.put( DeliveryType.findByValue(in.getKey()), in.getValue());
		return out;
	}

	public void setDeliveryConditionsText(Map<DeliveryType, String> deliveryConditionsText) {
		if( null==deliveryConditionsText) 
			this.deliveryConditionsText = null;
		else {
			this.deliveryConditionsText = new HashMap<Integer, String>();
			for( Entry<DeliveryType, String> in: deliveryConditionsText.entrySet()){
				this.deliveryConditionsText.put( in.getKey().getValue(), in.getValue());
			}
		}
	}

	public Map<DeliveryType, String> getDeliveryAddressMasksText() {
		if(null==deliveryAddressMasksText) return null;
		
		Map<DeliveryType, String> out = new HashMap<DeliveryType, String>();
		for( Entry<Integer,String> in : deliveryAddressMasksText.entrySet())
			out.put( DeliveryType.findByValue(in.getKey()), in.getValue());
		return out;
	}

	public void setDeliveryAddressMasksText(Map<DeliveryType, String> deliveryAddressMasksText) {
		if( null==deliveryAddressMasksText) 
			this.deliveryAddressMasksText = null;
		else {
			this.deliveryAddressMasksText = new HashMap<Integer, String>();
			for( Entry<DeliveryType, String> in: deliveryAddressMasksText.entrySet()){
				this.deliveryAddressMasksText.put( in.getKey().getValue(), in.getValue());
			}
		}
	}

	public void setDescr(Text descr) {
		this.descr = descr;
	}

	public void setTopics(List<Long> topics) {
		this.topics = topics;
	}


	public void setDeliveryCosts(Map<Integer, Double> deliveryCosts) {
		this.deliveryCosts = deliveryCosts;
	}

	public void setPaymentTypes(Map<Integer, Double> paymentTypes) {
		this.paymentTypes = paymentTypes;
	}
	

	public Map<Integer, Double> getPaymentTypes() {
		return paymentTypes;
	}

	public Map<Integer, Double> getDeliveryCosts() {
		return deliveryCosts;
	}

	public Map<Integer, Integer> getDeliveryByWeightIncrement() {
		return deliveryByWeightIncrement;
	}

	public void setDeliveryByWeightIncrement(Map<Integer, Integer> deliveryByWeightIncrement) {
		this.deliveryByWeightIncrement = deliveryByWeightIncrement;
	}

	public Map<Integer, Double> getDeliveryCostByDistance() {
		return deliveryCostByDistance;
	}

	public void setDeliveryCostByDistance(Map<Integer, Double> deliveryCostByDistance) {
		this.deliveryCostByDistance = deliveryCostByDistance;
	}

	public void setDates(OrderDates newDates) {
		for( OrderDates ods : dates ){
			if( ods.orderDay == newDates.orderDay ) {//replace the dates
				ods.eachOddEven = newDates.eachOddEven;
				ods.orderBefore = newDates.orderBefore;
				ods.priceTypeToUse = newDates.priceTypeToUse;
				return;
			}
		}
		dates.add(newDates);
	}
	
	public void setDates(List<OrderDates> newDates) {
		
		dates = newDates;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr.getValue();
	}

	public void setDescr(String descr) {
		this.descr = new Text(descr == null ? "" : descr);
	}

	public VoPostalAddress getAddress() {
		return address;
	}

	public void setAddress(VoPostalAddress address) {
		this.address = address;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public long getId() {
		return id.getId();
	}

	/*
	 * public List<VoProduct> getProducts() { return products; }
	 * 
	 * public void clearProducts(PersistenceManager pm) { for( VoProduct vp: products ){ if( 1==vp.getShops().size() && vp.getShops().get(0).getId() ==
	 * id.getId() ) pm.deletePersistent(vp); } if( products.size() > 0) products.clear(); }
	 * 
	 * public List<VoProductCategory> getCategories() { return categories; }
	 * 
	 * public void addCategory(VoProductCategory pc) { categories.add(pc); }
	 * 
	 * public void clearCategories( PersistenceManager pm) { for( VoProductCategory vpc: categories ){ if( 1==vpc.getShops().size() &&
	 * vpc.getShops().get(0).getId() == id.getId() ) pm.deletePersistent(vpc); } categories.clear(); }
	 */

	public List<Long> getTopics() {
		return topics;
	}

	/*
	 * public List<VoProducer> getProducers() { return producers; }
	 */

	public List<OrderDates> getDates() {
		return dates;
	}

	
	
	public String getAboutShopPageContentURL() {
		return aboutShopPageContentURL;
	}

	public void setAboutShopPageContentURL(String aboutShopPageContentURL) {
		this.aboutShopPageContentURL = aboutShopPageContentURL;
	}

	public String getConditionsPageContentURL() {
		return conditionsPageContentURL;
	}

	public void setConditionsPageContentURL(String conditionsPageContentURL) {
		this.conditionsPageContentURL = conditionsPageContentURL;
	}

	public String getDeliveryPageContentURL() {
		return deliveryPageContentURL;
	}

	public void setDeliveryPageContentURL(String deliveryPageContentURL) {
		this.deliveryPageContentURL = deliveryPageContentURL;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Map<String, String> getSocialNetworks() {
		return socialNetworks;
	}

	public void setSocialNetworks(Map<String, String> socialNetworks) {
		this.socialNetworks = socialNetworks;
	}

	public Map<String, Set<Long>> getVoteResults() {
		return voteResults;
	}

	@Override
	public String toString() {
		return "VoShop [id=" + id + ", name=" + name + "]";
	}

	public String toFullString() {
		return "VoShop [id=" + id + ", name=" + name + ", descr=" + descr + ", address=" + address + ", logoURL=" + logoURL + ", ownerId=" + ownerId
				+ "]";
	}
	
	public static String printDate( int date, DateType type){
		return type.name() + "[" + new Date(((long)date)*1000L).toLocaleString()+"]";
	}

	
	public static Map<Integer, Double> convertFromPaymentTypeMap(Map<PaymentType, Double> in, Map<Integer, Double> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new HashMap<Integer, Double>();

		for (Entry<PaymentType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static Map<PaymentType, Double> convertToPaymentTypeMap(Map<Integer, Double> in, Map<PaymentType, Double> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new HashMap<PaymentType, Double>();

		for (Entry<Integer, Double> e : in.entrySet())
			out.put(PaymentType.findByValue(e.getKey()), e.getValue());
		return out;
	}

	public static Map<Integer, Double> convertFromDeliveryTypeMap(Map<DeliveryType, Double> in, Map<Integer, Double> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new HashMap<Integer, Double>();

		for (Entry<DeliveryType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static <T> Map<DeliveryType, T> convertToDeliveryTypeMap(Map<Integer, T> in, Map<DeliveryType, T> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new HashMap<DeliveryType, T>();

		for (Entry<Integer, T> e : in.entrySet())
			out.put(DeliveryType.findByValue(e.getKey()), e.getValue());
		return out;
	}

	public static SortedMap<Integer, Integer> convertFromDateTypeMap(Map<Integer, DateType> in, SortedMap<Integer, Integer> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new TreeMap<Integer, Integer>();

		for (Entry<Integer, DateType> e : in.entrySet())
			out.put(e.getKey(), e.getValue().getValue());
		return out;
	}

	public static SortedMap<Integer, DateType> convertToDateTypeMap(Map<Integer, Integer> in, SortedMap<Integer, DateType> out) {
		if (null == in)
			return out;
		if (null == out)
			out = new TreeMap<Integer, DateType>();

		for (Entry<Integer, Integer> e : in.entrySet())
			out.put(e.getKey(), DateType.findByValue(e.getValue()));
		return out;
	}

	public void update(Shop ns, long userId, boolean isPublic, PersistenceManager pm) throws InvalidOperation, IOException {
		
		if(  null!=ns.getAddress() && !ns.getAddress().equals(address.getPostalAddress()) )
			this.setAddress( new VoPostalAddress( ns.getAddress(), pm ));
		if( ns.getOwnerId() != 0L && ns.getOwnerId() != this.ownerId )
			this.setOwnerId(ns.getOwnerId());
		
		try {
			VoHelper.copyIfNotNull(this, "name", ns.name);
			VoHelper.copyIfNotNull(this, "descr", ns.descr);
			VoHelper.replaceURL(this, "logoURL", ns.logoURL, userId, isPublic, pm);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		if (null == (this.tags = ns.tags))
			this.tags = new ArrayList<String>();

		if (null != ns.topicSet) {
			this.topics = new ArrayList<Long>();
			this.topics.addAll(ns.topicSet);
		}
	}
//======================================================================================================================
	public PriceType getPriceType(int date) throws InvalidOperation {
		
		date -= date % 86400;
		
		Calendar now = Calendar.getInstance();
		Calendar theDate = Calendar.getInstance();
		theDate.setTimeInMillis(((long)date)*1000L);
		
		for( OrderDates d : dates ){
			if( d.type == OrderDatesType.ORDER_WEEKLY && 
				d.orderDay == theDate.get(Calendar.DAY_OF_WEEK) &&
				( d.eachOddEven == 0 || 
						d.eachOddEven == 1 && 1 == theDate.get(Calendar.WEEK_OF_YEAR) % 2  ||
						d.eachOddEven == 2 && 0 == theDate.get(Calendar.WEEK_OF_YEAR) % 2 ) ) {
				if( now.getTimeInMillis() < theDate.getTimeInMillis() - 86400000L * (long)(d.orderBefore - 1) - now.get(Calendar.ZONE_OFFSET) )
					return d.getPriceTypeToUse();
				
			} else if( d.type == OrderDatesType.ORDER_MOUNTHLY &&
					( d.eachOddEven == 0 || 
					d.eachOddEven == 1 && 1 == theDate.get(Calendar.MONTH) % 2  ||
					d.eachOddEven == 2 && 0 == theDate.get(Calendar.MONTH) % 2 ) ) {
			if( now.getTimeInMillis() < theDate.getTimeInMillis() - 86400000L * (long)d.orderBefore )
				return d.getPriceTypeToUse();
			}
		}
		throw new InvalidOperation( VoError.IncorrectParametrs, "Order could not be created for date "+new Date(1000L * (long)date).toGMTString());
	}

	//=====================================================================================================================
	
	public OrderDate getNextOrderDate(int afterDate) throws InvalidOperation {
		
		afterDate -= afterDate % 86400;
		
		Calendar afterDateCldr = Calendar.getInstance();
		afterDateCldr.setTimeInMillis(((long)afterDate)*1000L);
		int closestDelta = 1000;
		PriceType pt = PriceType.INET;

		for( OrderDates d : dates ){
			int delta;
			
			if( d.type == OrderDatesType.ORDER_WEEKLY ){
				int afterDateDayOfWeek = afterDateCldr.get(Calendar.DAY_OF_WEEK); //day of week of the date
				int scheduleDayOfWeek = d.orderDay - d.orderBefore; //day of week for order before
				if( scheduleDayOfWeek < 0 ) 
					scheduleDayOfWeek = 7 + scheduleDayOfWeek;
				delta = afterDateDayOfWeek > scheduleDayOfWeek ? 7 - afterDateDayOfWeek + scheduleDayOfWeek : scheduleDayOfWeek - afterDateDayOfWeek;
				
				if( 1 == d.eachOddEven && 1 == afterDateCldr.get(Calendar.WEEK_OF_YEAR % 2 ) || 
						2 == d.eachOddEven && 0 == afterDateCldr.get(Calendar.WEEK_OF_YEAR % 2 ) || 
						0 == d.eachOddEven );
				else
					delta += 7;
				
			} else { //d.type == OrderDatesType.ORDER_MONTHLY
				
				int ddow = afterDateCldr.get(Calendar.DAY_OF_WEEK); //day of week of the date
				int dobow = d.orderDay - d.orderBefore; //day of week for order before
				if( dobow < 0 ) 
					dobow = afterDateCldr.getActualMaximum(Calendar.DAY_OF_MONTH) - dobow;
				delta = ddow > dobow ? afterDateCldr.getActualMaximum(Calendar.DAY_OF_MONTH) - ddow + dobow : dobow - ddow;
				
				if( 1 == d.eachOddEven && 1 == afterDateCldr.get(Calendar.MONTH % 2 ) || 
						2 == d.eachOddEven && 0 == afterDateCldr.get(Calendar.MONTH % 2 ) || 
						0 == d.eachOddEven );
				else
					delta += afterDateCldr.getActualMaximum(Calendar.DAY_OF_MONTH);
			} 
			delta += d.orderBefore; 
					
			if( closestDelta > delta ){
				closestDelta = delta;
				pt = d.priceTypeToUse;
			}
		}
		
		if( closestDelta == 1000 )
			throw new InvalidOperation(VoError.IncorrectParametrs, "No order dates found in nearest 1000 days after " + 
		new Date(1000L * (long)afterDate));
		
		return new OrderDate( afterDate + closestDelta * 86400, pt);
	}
	
	//VOTING
	public boolean canVote( long userId ){
		if( null == voteResults ) return true;
		for( Entry< String, Set<Long>> ve: voteResults.entrySet()){
			if( ve.getValue() != null && ve.getValue().contains( userId ))
				return false;
		}
		return true;
	}
	
	public int vote( long userId, String decision ) throws InvalidOperation {
		if( !canVote(userId) )
			throw new InvalidOperation(VoError.IncorrectParametrs, "The user can't vote more then once");
		
		if( null == getVoteResults() ) 
			voteResults = new HashMap<String, Set<Long>>();
		
		Set<Long> sameVoters;
		
		if( null == (sameVoters = voteResults.get(decision)))
			voteResults.put(decision, sameVoters = new HashSet<Long>());
		
		sameVoters.add(userId);
		return sameVoters.size();
	}
	

	/*
	 * public static class DateMap<T extends Serializable> extends TreeMap<Integer, T> implements Serializable {
	 * 
	 * private static int DAY = 86400;
	 * 
	 * @Override public boolean containsKey(Object key) { if( key instanceof Integer ) return super.containsKey(((Integer)key).intValue() -
	 * ((Integer)key).intValue() % DAY); return super.containsKey(key); }
	 * 
	 * @Override public T get(Object key) { if( key instanceof Integer ) return super.get(((Integer)key).intValue() - ((Integer)key).intValue() % DAY);
	 * return super.get(key); }
	 * 
	 * @Override public void putAll(Map<? extends Integer, ? extends T> map) { for (Entry<? extends Integer, ? extends T> entry : map.entrySet()) {
	 * put(entry.getKey(), entry.getValue()); //rounded in overloadad method above } super.putAll(map); }
	 * 
	 * @Override public T put(Integer key, T value) { return super.put(key - key % DAY, value); }
	 * 
	 * @Override public Integer ceilingKey(Integer key) { return super.ceilingKey(key - key % DAY ); } }
	 */
	
}
