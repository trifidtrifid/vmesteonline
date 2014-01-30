package com.vmesteonline.be.jdo2.shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.mortbay.jetty.servlet.HashSessionIdManager;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.Shop;

@PersistenceCapable
public class VoShop {

	public VoShop(Shop shop) throws InvalidOperation {
		this(shop.getName(),shop.getDescr(),shop.getAddress(), shop.getLogoURL(), shop.getOwnerId(), shop.getTopicSet(), shop.getTags());
	}
	public VoShop(String name, String descr, String address, String logoURL, long ownerId, Set<Long> topicSet, Set<String> tags) throws InvalidOperation {
		this.name = name;
		this.descr = descr;
		this.address = address;
		this.logoURL = logoURL;
		this.ownerId = ownerId;
		this.tags = tags;
		this.topics = new HashSet<VoTopic>();
		PersistenceManager pm = PMF.getPm();
		try {
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
		Shop shop = new Shop(id, name, descr, address, logoURL, ownerId, null, tags);
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
	private String address;
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

	@Persistent(mappedBy = "shops")
	@ManyToMany
	private Set<VoProduct> products;

	@Persistent(mappedBy = "shops")
	@ManyToMany
	private Set<VoProductCategory> categories;

	@Persistent(mappedBy = "shops")
	@ManyToMany
	private Set<VoProducer> producers;
	
	@Persistent
	@Unindexed
	private SortedMap<Integer,DateType> dates;
	
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
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
	
	
	@Override
	public String toString() {
		return "VoShop [id=" + id + ", name=" + name + "]";
	}
	
	public String toFullString() {
		return "VoShop [id=" + id + ", name=" + name + ", descr=" + descr + ", address=" + address + ", logoURL=" + logoURL + ", ownerId=" + ownerId
				+ "]";
	}
	
}
