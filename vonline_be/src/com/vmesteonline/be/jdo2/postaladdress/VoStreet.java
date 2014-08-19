package com.vmesteonline.be.jdo2.postaladdress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Street;
import com.vmesteonline.be.VoError;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoStreet {

	public static VoStreet createVoStreet(VoCity city, String name, PersistenceManager pm) throws InvalidOperation {
		List<VoStreet> vcl = (List<VoStreet>)pm.newQuery(VoStreet.class, "cityId=="+city.getId()+"  && name=='"+name+"'").execute();
		
		if( vcl.size() == 1 ){
			return vcl.get(0);
			
		} else if( vcl.size() == 0 ){
			VoStreet vs = new VoStreet(city, name, pm);
			pm.makePersistent(vs);
			return vs;
		} else 
			throw new InvalidOperation( VoError.GeneralError, "To many cities with name '"+name+"' ");
	}

	private VoStreet(VoCity city, String name, PersistenceManager pm) throws InvalidOperation {
		this.setCity(city.getId());
		this.setName(name);
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	public void setId(long id) {
		this.id = id;
	}

	@Persistent
	private String name;

	@Persistent
	@Unowned
	private long cityId;

	public long getCity() {
		return cityId;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Street getStreet() {
		return new Street(id, cityId, name);
	}

	@Override
	public String toString() {
		return "VoStreet [id=" + id + ", name=" + name + ", city=" + cityId + "]";
	}
	
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCity(long city) {
		this.cityId = city;
	}
}
