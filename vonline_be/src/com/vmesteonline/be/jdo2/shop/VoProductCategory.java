package com.vmesteonline.be.jdo2.shop;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
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
		this.descr = descr;
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
	
	  topics = new ArrayList<VoTopic>();
	  shops = new ArrayList<VoShop>();
	  childs = new ArrayList<VoProductCategory>();
	  products = new ArrayList<VoProduct>();
	  this.importId = importId;
	  
	  PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;  
	  try {
			if( 0!=parentId ) {
				VoProductCategory pc = pm.getObjectById(VoProductCategory.class, parentId);
				this.setParent(pc);
				pc.getChilds().add(this);
				pm.makePersistent(pc);
			}
			pm.makePersistent(this);
			/*VoShop shop = pm.getObjectById(VoShop.class, shopId);*/
			shop.addProductCategory(this);
			shops.add(shop);
			
			if(null!=topicSet)
				for( long tid: topicSet){
					VoTopic vt = pm.getObjectById(VoTopic.class, tid);
					topics.add(vt); 
				}
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( null==_pm) pm.close();
		}
	}
	
	public ProductCategory getProductCategory( ){
		ProductCategory pc = new ProductCategory(id.getId(), 
				null==parent ? 0L : parent.getId(), name, descr, null, null );
		
		List<Long> ts = new ArrayList<Long>();
		for( VoTopic vt: getTopics())
			ts.add(vt.getId());
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
	private VoProductCategory parent;
	
	@Persistent
	@Unindexed
	private String name;
	
	@Persistent
	@Unindexed
	private String descr;
  
	@Persistent
	@Unindexed
	private List<String> logoURLset;
  
	@Persistent
	@Unowned
	private List<VoTopic> topics;
	
	@Persistent(mappedBy="parent")
	@OneToMany
	@Unowned
	private List<VoProductCategory> childs;
	
	@Persistent
	@Unowned
	private List<VoProduct> products;
	
	@Persistent
	@Unowned
	private List<VoShop> shops;
	
	public List<VoProduct> getProducts(){
		return products;
	} 
	public long getId(){
		return id.getId();
	}

	public VoProductCategory getParent() {
		return parent;
	}

	public void setParent(VoProductCategory parent) {
		this.parent = parent;
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

	public List<String> getLogoURLset() {
		return logoURLset;
	}

	public void setLogoURLset(List<String> logoURLset) {
		this.logoURLset = logoURLset;
	}

	public List<VoTopic> getTopics() {
		return topics;
	}

	public void addTopic( VoTopic topic) {
		this.topics.add(topic);
	}

	public List<VoProductCategory> getChilds() {
		return childs;
	}

	public void addChild(VoProductCategory child) {
		this.childs.add(child);
	}

	public void addProduct(VoProduct product) {
		this.products.add(product);
	}

	@Override
	public String toString() {
		return "VoProductCategory [id=" + id + ", parent=" + parent + ", name=" + name + "]";
	}
	
	public List<VoShop> getShops() {
		return shops;
	}

	public void update(ProductCategory newCategoryInfo, long userId, PersistenceManager pm) {
	  
		this.name = newCategoryInfo.name;
		this.descr = newCategoryInfo.descr;
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
		//TODO optimize query to fetch categories of the shop if it's possible
		Query qu = pm.newQuery(VoProductCategory.class);
		qu.setFilter("importId == "+importId);
		try {
			List<VoProductCategory> cl = (List<VoProductCategory>) qu.execute(shopId);
			for (VoProductCategory voProductCategory : cl) {
				for( VoShop vs: voProductCategory.getShops()){
					if(vs.getId() == shopId )
						return voProductCategory;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
