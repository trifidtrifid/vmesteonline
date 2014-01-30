package com.vmesteonline.be.jdo2.shop;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.utils.StorageHelper;

@PersistenceCapable
public class VoProductCategory {

	public VoProductCategory(long shopId,long parentId, String name, String descr, Set<ByteBuffer> logoURLset, Set<Long> topicSet) {
	  
		this.name = name;
		this.descr = descr;
		this.logoURLset = new HashSet<String>();
		for( ByteBuffer bb: logoURLset) {
			try {
				this.logoURLset.add(StorageHelper.saveImage(bb.array()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	  topics = new HashSet<VoTopic>();
	  shops = new HashSet<VoShop>();
	  childs = new HashSet<VoProductCategory>();
	  products = new HashSet<VoProduct>();
	  
	  PersistenceManager pm = PMF.getPm();  
	  try {
			if( 0!=parentId ) {
				VoProductCategory pc = pm.getObjectById(VoProductCategory.class, parentId);
				this.setParent(pc);
				pc.getChilds().add(this);
				pm.makePersistent(pc);
			}
			pm.makePersistent(this);
			VoShop shop = pm.getObjectById(VoShop.class, shopId);
			shop.addProductCategory(this);
			shops.add(shop);
			
			for( long tid: topicSet){
				VoTopic vt = pm.getObjectById(VoTopic.class, tid);
				topics.add(vt);
			}
			
			pm.makePersistent(shop);
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	
	public ProductCategory getProductCategory( ){
		ProductCategory pc = new ProductCategory(id, 
				null==parent ? 0L : parent.getId(), name, descr, null, null );
		
		Set<ByteBuffer> lus = new HashSet<ByteBuffer>();
		for( String lu : getLogoURLset()) {
			lus.add(ByteBuffer.wrap(lu.getBytes()));
		}
		Set<Long> ts = new HashSet<Long>();
		for( VoTopic vt: getTopics())
			ts.add(vt.getId().getId());
		pc.setLogoURLset(lus);
		pc.setTopicSet(ts);
		
		return pc;
	}
	@Persistent
	@PrimaryKey
	private long id;
	
	@Persistent
	private VoProductCategory parent;
	
	@Persistent
	@Unindexed
	private String name;
	
	@Persistent
	@Unindexed
	private String descr;
  
	@Persistent
	@Unindexed
	private Set<String> logoURLset;
  
	@Persistent
	@Unowned
	private Set<VoTopic> topics;
	
	@Persistent(mappedBy="parent")
	@OneToMany
	private Set<VoProductCategory> childs;
	
	@Persistent(mappedBy="categories")
	@ManyToMany
	private Set<VoProduct> products;
	
	@Persistent(mappedBy="categories")
	@ManyToMany
	private Set<VoShop> shops;
	
	public Set<VoProduct> getProducts(){
		return products;
	} 
	public long getId(){
		return id;
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

	public Set<String> getLogoURLset() {
		return logoURLset;
	}

	public void setLogoURLset(Set<String> logoURLset) {
		this.logoURLset = logoURLset;
	}

	public Set<VoTopic> getTopics() {
		return topics;
	}

	public void addTopic( VoTopic topic) {
		this.topics.add(topic);
	}

	public Set<VoProductCategory> getChilds() {
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
}
