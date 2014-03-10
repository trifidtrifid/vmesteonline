package com.vmesteonline.be.jdo2.shop;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.utils.StorageHelper;

@PersistenceCapable
public class VoProductCategory {

	public VoProductCategory(VoShop shop,long parentId, String name, String descr, List<String> logoURLset, List<Long> topicSet, long ownedId, PersistenceManager _pm) {
		this(shop, 0, parentId, name, descr, logoURLset, topicSet, ownedId, null);
	}
	
	public VoProductCategory(VoShop shop,long parentId, String name, String descr, List<String> logoURLset, List<Long> topicSet, long ownedId) {
		this(shop, 0, parentId, name, descr, logoURLset, topicSet, ownedId, null);
	}
	
	public VoProductCategory(VoShop shop,long importId, long parentId, String name, String descr, List<String> logoURLset, List<Long> topicSet, long ownedId, PersistenceManager _pm) {
	  
		this.name = name;
		this.setDescr( descr );
		if( null!=logoURLset){
			this.logoURLset = new ArrayList<String>();
			for( String bb: logoURLset) {
				try {
					this.logoURLset.add(StorageHelper.saveImage(bb, ownedId, true, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	
	  topics = new ArrayList<Long>();
	  this.setShopId(shop.getId());
	  this.setImportId(importId);
	  
	  PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;  
	  try {
			if( 0!=parentId ) {
				pm.getObjectById(VoProductCategory.class, parentId);
			}
			this.setParentId(parentId);
			
			if(null!=topicSet)
				for( long tid: topicSet){
					pm.getObjectById(VoTopic.class, tid);
					topics.add(tid); 
				}
			pm.makePersistent(this);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( null==_pm) pm.close();
		}
	}
	
	public long getImportId() {
		return importId;
	}

	public void setImportId(long importId) {
		this.importId = importId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setDescr(Text descr) {
		this.descr = descr;
	}

	public void setTopics(List<Long> topics) {
		this.topics = topics;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public ProductCategory getProductCategory( ){
		ProductCategory pc = new ProductCategory(id.getId(), 
				parentId, name, descr.getValue(), null, null );
		
		List<Long> ts = new ArrayList<Long>();
		ts.addAll(getTopics());
		pc.setLogoURLset(getLogoURLset());
		pc.setTopicSet(ts);
		
		return pc;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	@Unique
	private long importId;
	
	@Persistent
	@Unowned
	private long parentId;
	
	@Persistent
	@Unindexed
	private String name;
	
	@Persistent
	@Unindexed
	private Text descr;
  
	@Persistent
	@Unindexed
	private List<String> logoURLset;
  
	@Persistent
	private List<Long> topics;
	
	@Persistent
	private long shopId;

	 
	public long getId(){
		return id.getId();
	}

	public long getParent() {
		return parentId;
	}

	public void setParentId(Long parent) {
		this.parentId = parent;
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
		this.descr = new Text( null == descr ? "" :descr );
	}

	public List<String> getLogoURLset() {
		return logoURLset;
	}

	public void setLogoURLset(List<String> logoURLset) {
		this.logoURLset = logoURLset;
	}

	public List<Long> getTopics() {
		return topics;
	}

	public void addTopic( Long topic) {
		this.topics.add(topic);
	}

	
	@Override
	public String toString() {
		return "VoProductCategory [id=" + id + ", importId=" + importId + ", parentId=" + parentId + ", name=" + name + ", shopId=" + shopId + "]";
	}
	
	public long getShopId() {
		return shopId;
	}

	public void update(ProductCategory newCategoryInfo, long userId, PersistenceManager pm) {
	  
		this.name = newCategoryInfo.name;
		this.setDescr(newCategoryInfo.descr);
		this.logoURLset = new ArrayList<String>();
		for( String bb: newCategoryInfo.logoURLset) {
			try {
				this.logoURLset.add(StorageHelper.saveImage(bb, userId, true, pm));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static VoProductCategory getByImportId(Long shopId, long importId, PersistenceManager pm) {
		Query qu = pm.newQuery(VoProductCategory.class);
		qu.setFilter("importId == "+importId+ " && shopId == "+shopId);
		try {
			List<VoProductCategory> cl = (List<VoProductCategory>) qu.execute();
			for (VoProductCategory voProductCategory : cl) {
						return voProductCategory;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
