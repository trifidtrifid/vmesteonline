package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Group;
import com.vmesteonline.be.GroupType;

@PersistenceCapable
public class VoUserGroup extends GeoLocation implements Comparable<VoUserGroup> {

	public VoUserGroup(BigDecimal longitude,BigDecimal latitude, int radius, String name, int impScore, int gType, PersistenceManager pm){
		setLongitude(longitude);
		setLatitude(latitude);
		this.radius = radius;
		this.name = name;
		importantScore = impScore;
		groupType = gType;
		
		String queryStr = "longitude=='"+longitude.toPlainString()+
				"' && latitude=='"+latitude.toPlainString()+"' && "
				+ "radius=="+radius
				+" && groupType=="+groupType;
		List<VoUserGroup> ugl =  (List<VoUserGroup>)pm.newQuery(VoUserGroup.class,queryStr).execute();
		
		if( 0!=ugl.size() ) {
			id = KeyFactory.createKey(this.getClass().getSimpleName(), ugl.get(0).getId());
			
		} else {
			id = null;
			
		}
	} 
	
	public VoUserGroup(VoUser user, VoGroup grp, PersistenceManager pm) {
		this(user.getLongitude(),user.getLatitude(),grp.getRadius(),grp.getVisibleName(),grp.getImportantScore(),grp.getGroupType(), pm);
	}
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Group createGroup() {
		return new Group(getId(), name, name, description, radius, GroupType.findByValue(groupType));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Persistent
	@Unindexed
	private String description;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private int radius;
	
	@Persistent
	@Unindexed
	private int importantScore;

	
	public int getImportantScore() {
		return importantScore;
	}

	@Persistent
	private int groupType;
	
	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}
	
	@Override
	public String toString() {
		return "VoUserGroup [id=" + getId() + ", name=" + name + ", longitude=" + getLongitude() + ", latitude=" + getLatitude() + ", radius=" + radius
				+ "]";
	}

	@Override
	public int compareTo(VoUserGroup that) {
		return that.getLatitude().compareTo(this.getLatitude()) != 0 ? that.getLatitude().compareTo(this.getLatitude()) : that.getLongitude().compareTo(
				this.getLongitude()) != 0 ? that.getLongitude().compareTo(this.getLongitude()) : Integer.compare(that.radius, this.radius);
	}
}
