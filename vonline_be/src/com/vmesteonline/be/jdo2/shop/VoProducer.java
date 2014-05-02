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
import com.google.appengine.api.datastore.Text;
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
		this.setDescr( producer.getDescr());
		this.homeURL = producer.getHomeURL();
		this.importId = producer.id;
		
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;

		try {
			String logoURL2 = producer.getLogoURL();
			this.logoURL = logoURL2 == null || logoURL2.trim().isEmpty() ? null : StorageHelper.saveImage(logoURL2, userId, true, _pm);
		} catch (IOException e) {
			//e.printStackTrace();
			
			//throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to load Image: "+e);
		}
		
		try {

			try {
				pm.getObjectById(VoShop.class, shopId);
				
			} catch (JDOObjectNotFoundException e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID=" + shopId + ". " + e);
			}
			
			this.shopId = shopId;
			pm.makePersistent(this);

		} finally {
			if( _pm == null ) pm.close();
		}
	}

	public Producer createProducer() {
		return new Producer(id.getId(), name, descr.getValue(), logoURL, homeURL);
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private long importId;

	@Persistent
	private long shopId;
	
	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private Text descr;

	@Persistent
	@Unindexed
	private String logoURL;

	@Persistent
	@Unindexed
	private String homeURL;

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
		this.descr = new Text( null == descr ? "" : descr );
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

	public long getShopId() {
		return shopId;
	}
	

	@Override
	public String toString() {
		return "VoProducer [id=" + id + ", importId=" + importId + ", shopId=" + shopId + ", name=" + name + ", logoURL=" + logoURL + ", homeURL="
				+ homeURL + "]";
	}
	

	public void update(Producer newInfoWithOldId, long userId, boolean isPublic, PersistenceManager pm) throws InvalidOperation {
		this.id = KeyFactory.createKey(this.getClass().getSimpleName(), newInfoWithOldId.id);
		try {
			VoHelper.copyIfNotNull(this, "descr", newInfoWithOldId.descr);
			VoHelper.copyIfNotNull(this, "homeURL", newInfoWithOldId.homeURL);
			VoHelper.replaceURL(this, "logoURL", newInfoWithOldId.logoURL, userId, isPublic, pm);
			VoHelper.copyIfNotNull(this, "name", newInfoWithOldId.name);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new InvalidOperation( VoError.IncorrectParametrs, "Failed to update Producer:"+e.getMessage());
		}
	}

	public static VoProducer getByImportId(long shopId, long importId, PersistenceManager pm) {
		Query q = pm.newQuery(VoProducer.class);
		q.setFilter("importId == "+importId + " && shopId == "+shopId);
		try {
			List<VoProducer> cl = (List<VoProducer>) q.execute();
			for (VoProducer voProducer : cl) {
				return voProducer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
