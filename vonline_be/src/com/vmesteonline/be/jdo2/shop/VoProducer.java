package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

@PersistenceCapable
public class VoProducer {

	public VoProducer(long shopId, long userId, Producer producer) throws InvalidOperation {
		this(shopId, userId, producer, null);
	}

	public VoProducer(long shopId, long userId, Producer producer, PersistenceManager _pm)throws InvalidOperation {
			
		this.name = producer.getName();
		this.descr = producer.getDescr();
		this.homeURL = producer.getHomeURL();
	
		this.shops = new HashSet<VoShop>();
		this.products = new HashSet<VoProduct>();
		this.importId = producer.id;
		
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;

		try {
			this.logoURL = StorageHelper.saveImage(producer.getLogoURL(), userId, true, _pm);
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to load Image: "+e);
		}
		
		try {
			pm.makePersistent(this);
			VoShop voShop = null;
			try {
				voShop = pm.getObjectById(VoShop.class, shopId);
				pm.retrieve(voShop);
			} catch (JDOObjectNotFoundException e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID=" + shopId + ". " + e);
			}
			
			try {
				this.logoURL = null;
				if (null != logoURL && logoURL.length() > 0)
					this.logoURL = StorageHelper.saveImage(logoURL, voShop.getOwnerId(), true, pm);
			} catch (IOException e) {
				throw new InvalidOperation(VoError.IncorrectParametrs, e.getMessage());
			}
			
			shops.add(voShop);
			voShop.addProducer(this);
			pm.makePersistent(this);
			pm.makePersistent(voShop);

		} finally {
			if( _pm == null ) pm.close();
		}
	}

	public Producer createProducer() {
		return new Producer(id.getId(), name, descr, logoURL, homeURL);
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private long importId;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String descr;

	@Persistent
	@Unindexed
	private String logoURL;

	@Persistent
	@Unindexed
	private String homeURL;

	@Persistent
	@Unowned
	private Set<VoShop> shops;

	@Persistent(mappedBy = "producer")
	@OneToMany
	@Unowned
	private Set<VoProduct> products;

	public Set<VoProduct> getProducts() {
		return products;
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

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getHomeURL() {
		return homeURL;
	}

	public void setHomeURL(String homeURL) {
		this.homeURL = homeURL;
	}

	public long getId() {
		return id.getId();
	}

	public Set<VoShop> getShops() {
		return shops;
	}
	

	@Override
	public String toString() {
		return "VoProducer [id=" + id + ", name=" + name + "]";
	}

	public void update(Producer newInfoWithOldId, long userId, boolean isPublic, PersistenceManager pm) throws InvalidOperation {
		this.id = KeyFactory.createKey(this.getClass().getSimpleName(), newInfoWithOldId.id);
		try {
			VoHelper.copyIfNotNull(this, "descr", newInfoWithOldId.descr);
			VoHelper.replaceURL(this, "homeURL", newInfoWithOldId.homeURL, userId, isPublic, pm);
			VoHelper.replaceURL(this, "logoURL", newInfoWithOldId.logoURL, userId, isPublic, pm);
			VoHelper.copyIfNotNull(this, "name", newInfoWithOldId.name);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new InvalidOperation( VoError.IncorrectParametrs, "Failed to update Producer:"+e.getMessage());
		}
	}

	public static VoProducer getByImportId(Long shopId, long importId, PersistenceManager pm) {
		Query q = pm.newQuery(VoProducer.class);
		q.setFilter("importId == "+importId);
		try {
			List<VoProducer> cl = (List<VoProducer>) q.execute(shopId);
			for (VoProducer voProducer : cl) {
				for( VoShop vs: voProducer.getShops()){
					if(vs.getId() == shopId )
						return voProducer;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
