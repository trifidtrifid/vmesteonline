package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
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
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.utils.StorageHelper;
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

		this.name = name;
		this.descr = descr;
		if(postalAddress != null ) this.address = new VoPostalAddress(postalAddress, pm);
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
			if( null!=topicSet){
				this.topics = new ArrayList<VoTopic>();
				for (long tid : topicSet) {
					VoTopic vt = pm.getObjectById(VoTopic.class, tid);
					topics.add(vt);
				}
			}
			categories = new ArrayList<VoProductCategory>();
			products = new ArrayList<VoProduct>();
			producers = new ArrayList<VoProducer>();
			dates = new TreeMap<Integer, Integer>();
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
		for (VoTopic vt : getTopics()) {
			topicIds.add(vt.getId().getId());
		}
		Shop shop = new Shop(id.getId(), name, descr, null==address ? null : address.getPostalAddress(), logoURL, ownerId, 
				topicIds, tags, 
				convertToDeliveryTypeMap(deliveryCosts, new HashMap<DeliveryType, Double>()),
				convertToPaymentTypeMap(paymentTypes, new HashMap<PaymentType, Double>()));
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
	private String descr;
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
	public List<VoTopic> topics;

	@Persistent
	public List<String> tags;

	@Persistent
	@Unowned
	private List<VoProduct> products;

	@Persistent
	@Unowned
	private List<VoProductCategory> categories;

	@Persistent
	@Unowned
	private List<VoProducer> producers;

	@Persistent
	@Unindexed
	private SortedMap<Integer, Integer> dates;

	@Persistent
	@Unindexed
	private Map<Integer, Double> deliveryCosts;

	@Persistent
	@Unindexed
	private Map<Integer, Double> paymentTypes;

	public Map<Integer, Double> getPaymentTypes() {
		return paymentTypes;
	}

	public Map<Integer, Double> getDeliveryCosts() {
		return deliveryCosts;
	}

	public void setDates(Map<Integer, DateType> newDates) {
		for (Entry<Integer, DateType> e : newDates.entrySet()) { // round to the
																															// begining of the
																															// day
			dates.put(e.getKey() - e.getKey() % 86400, e.getValue().getValue());
		}
		// getDates().putAll( convertFromDateTypeMap(newDates, new TreeMap<Integer,
		// Integer>()));
	}

	public SortedMap<Integer, DateType> selectDates(int fromDate, int toDate) {
		SortedMap<Integer, DateType> selectedDates = new TreeMap<Integer, DateType>();
		selectedDates.putAll(convertToDateTypeMap(dates.subMap(fromDate, toDate), new TreeMap<Integer, DateType>()));
		return selectedDates;
	}

	public void addProductCategory(VoProductCategory category) {
		categories.add(category);
	}

	public void addProduct(VoProduct product) {
		products.add(product);
	}

	public void addProducer(VoProducer producer) {
		producers.add(producer);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
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

	public List<VoProduct> getProducts() {
		return products;
	}

	public void clearProducts() {
		getProducts().clear();
	}

	public List<VoProductCategory> getCategories() {
		return categories;
	}

	public void addCategory(VoProductCategory pc) {
		categories.add(pc);
	}

	public void clearCategories() {
		categories.clear();
	}

	public List<VoTopic> getTopics() {
		return topics;
	}

	public List<VoProducer> getProducers() {
		return producers;
	}

	public SortedMap<Integer, Integer> getDates() {
		return dates;
	}

	@Override
	public String toString() {
		return "VoShop [id=" + id + ", name=" + name + "]";
	}

	public String toFullString() {
		return "VoShop [id=" + id + ", name=" + name + ", descr=" + descr + ", address=" + address + ", logoURL=" + logoURL + ", ownerId=" + ownerId
				+ "]";
	}

	public SortedMap<Integer, DateType> getDates(int from, int to) {
		return convertToDateTypeMap(dates.subMap(from - from % 86400, to + 86400 - to % 86400), new TreeMap<Integer, DateType>());
	}

	public static Map<Integer, Double> convertFromPaymentTypeMap(Map<PaymentType, Double> in, Map<Integer, Double> out) {
		for (Entry<PaymentType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static Map<PaymentType, Double> convertToPaymentTypeMap(Map<Integer, Double> in, Map<PaymentType, Double> out) {
		for (Entry<Integer, Double> e : in.entrySet())
			out.put(PaymentType.findByValue(e.getKey()), e.getValue());
		return out;
	}

	public static Map<Integer, Double> convertFromDeliveryTypeMap(Map<DeliveryType, Double> in, Map<Integer, Double> out) {
		for (Entry<DeliveryType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static Map<DeliveryType, Double> convertToDeliveryTypeMap(Map<Integer, Double> in, Map<DeliveryType, Double> out) {
		for (Entry<Integer, Double> e : in.entrySet())
			out.put(DeliveryType.findByValue(e.getKey()), e.getValue());
		return out;
	}

	public static SortedMap<Integer, Integer> convertFromDateTypeMap(Map<Integer, DateType> in, SortedMap<Integer, Integer> out) {
		for (Entry<Integer, DateType> e : in.entrySet())
			out.put(e.getKey(), e.getValue().getValue());
		return out;
	}

	public static SortedMap<Integer, DateType> convertToDateTypeMap(Map<Integer, Integer> in, SortedMap<Integer, DateType> out) {
		for (Entry<Integer, Integer> e : in.entrySet())
			out.put(e.getKey(), DateType.findByValue(e.getValue()));
		return out;
	}

	public void update(Shop newShopWithOldId, long userId, boolean isPublic, PersistenceManager pm) throws InvalidOperation, IOException {
		try {
			VoHelper.copyIfNotNull(this, "name", newShopWithOldId.name) ;
			VoHelper.copyIfNotNull(this, "descr", newShopWithOldId.descr) ;
			VoHelper.replaceURL(this, "logoURL", newShopWithOldId.logoURL, userId, isPublic, pm);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		if( 0!=newShopWithOldId.ownerId  ) this.ownerId = newShopWithOldId.ownerId ;
		this.address = new VoPostalAddress(newShopWithOldId.getAddress(), pm);
		
		if (null == (this.tags = newShopWithOldId.tags))
			this.tags = new ArrayList<String>();

		this.topics = new ArrayList<VoTopic>();
		for (long tid : newShopWithOldId.topicSet) {
			VoTopic vt = pm.getObjectById(VoTopic.class, tid);
			this.topics.add(vt);
		}
	}

	/*
	 * public static class DateMap<T extends Serializable> extends
	 * TreeMap<Integer, T> implements Serializable {
	 * 
	 * private static int DAY = 86400;
	 * 
	 * @Override public boolean containsKey(Object key) { if( key instanceof
	 * Integer ) return super.containsKey(((Integer)key).intValue() -
	 * ((Integer)key).intValue() % DAY); return super.containsKey(key); }
	 * 
	 * @Override public T get(Object key) { if( key instanceof Integer ) return
	 * super.get(((Integer)key).intValue() - ((Integer)key).intValue() % DAY);
	 * return super.get(key); }
	 * 
	 * @Override public void putAll(Map<? extends Integer, ? extends T> map) { for
	 * (Entry<? extends Integer, ? extends T> entry : map.entrySet()) {
	 * put(entry.getKey(), entry.getValue()); //rounded in overloadad method above
	 * } super.putAll(map); }
	 * 
	 * @Override public T put(Integer key, T value) { return super.put(key - key %
	 * DAY, value); }
	 * 
	 * @Override public Integer ceilingKey(Integer key) { return
	 * super.ceilingKey(key - key % DAY ); } }
	 */
}
