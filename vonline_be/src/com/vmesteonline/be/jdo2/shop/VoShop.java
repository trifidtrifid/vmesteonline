package com.vmesteonline.be.jdo2.shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.utils.Helper;

@PersistenceCapable
public class VoShop {

	public VoShop(Shop shop) throws InvalidOperation {
		this(shop.getName(),shop.getDescr(), shop.getAddress(), shop.getLogoURL(), shop.getOwnerId(), shop.getTopicSet(), 
				shop.getTags(), shop.getDeliveryCosts(), shop.getPaymentTypes());
	}
	public VoShop(String name, String descr, PostalAddress postalAddress, String logoURL, long ownerId, Set<Long> topicSet, 
			Set<String> tags, Map<DeliveryType,Double> deliveryCosts, 
			Map<PaymentType,Double> paymentTypes) throws InvalidOperation {
		
		PersistenceManager pm = PMF.getPm();
		
		this.name = name;
		this.descr = descr;
		this.address = new VoPostalAddress( postalAddress, pm );
		this.logoURL = logoURL;
		this.ownerId = ownerId;
		if( null == (this.tags = tags)) this.tags = new HashSet<String>();
		if( null == (this.deliveryCosts = Helper.copyTheMap( deliveryCosts, new HashMap<Integer, Double>()))) this.deliveryCosts = new HashMap<Integer, Double>();
		if( null == (this.paymentTypes = Helper.copyTheMap( paymentTypes, new HashMap<Integer, Double>()))) {
			this.paymentTypes = new HashMap<Integer, Double>();
			this.paymentTypes.put( PaymentType.CASH.getValue(), 0D );
		}
		
		try {
			this.topics = new HashSet<VoTopic>();
			for(long tid: topicSet ){
				VoTopic vt = pm.getObjectById(VoTopic.class, tid);
				topics.add(vt);
			}
			categories = new HashSet<VoProductCategory>();
			products = new HashSet<VoProduct>();
			producers = new HashSet<VoProducer>();
			dates = new TreeMap<Integer, DateType>();
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to create shop."+e.getMessage());
		} finally {
			pm.close();
		}
	}

	public Shop getShop() {
		Shop shop = new Shop(id, name, descr, address.getPostalAddress(), logoURL, ownerId, null, tags, 
				Helper.copyTheMap( deliveryCosts, new HashMap<DeliveryType, Double>()),
				Helper.copyTheMap( paymentTypes, new HashMap<PaymentType, Double>()));
		Set<Long> topicIds = new HashSet<Long>();
		for (VoTopic vt : getTopics()) {
			topicIds.add(vt.getId().getId());
		}
		return shop;
	}

	@Persistent
	@PrimaryKey
	private long id;

	@Persistent
	@Unindexed
	private String name;
	@Persistent
	@Unindexed
	private String descr;
	@Persistent
	@Unindexed
	private VoPostalAddress address;
	@Persistent
	@Unindexed
	private String logoURL;

	@Persistent
	public long ownerId;

	@Persistent
	@Unowned
	public Set<VoTopic> topics;

	@Persistent
	public Set<String> tags;

	@Persistent
	@Unowned
	private Set<VoProduct> products;

	@Persistent
	@Unowned
	private Set<VoProductCategory> categories;

	@Persistent
	@Unowned
	private Set<VoProducer> producers;
	
	@Persistent
	@Unindexed
	private SortedMap<Integer,DateType> dates;
	
	@Persistent
	@Unindexed
	private Map<Integer,Double> deliveryCosts;
	
	@Persistent
	@Unindexed
	private Map<Integer,Double> paymentTypes;
	
	public Map<Integer, Double> getPaymentTypes() {
		return paymentTypes;
	}
	public Map<Integer, Double> getDeliveryCosts() {
		return deliveryCosts;
	}
	public void setDates( Map<Integer,DateType> newDates ){
		dates.putAll(newDates);
	}
	
	public Map<Integer,DateType> selectDates( int fromDate, int toDate){
		Map<Integer,DateType> selectedDates = new TreeMap<Integer, DateType>();
		selectedDates.putAll(dates.subMap(fromDate, toDate));
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

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public long getId() {
		return id;
	}

	public Set<VoProduct> getProducts() {
		return products;
	}

	public Set<VoProductCategory> getCategories() {
		return categories;
	}

	public Set<VoTopic> getTopics() {
		return topics;
	}
	
	public Set<VoProducer> getProducers() {
		return producers;
	}
	public SortedMap<Integer, DateType> getDates() {
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
	public SortedMap<Integer,DateType> getDates(int from, int to) {
		return dates.subMap(from,  to);
	}
}
