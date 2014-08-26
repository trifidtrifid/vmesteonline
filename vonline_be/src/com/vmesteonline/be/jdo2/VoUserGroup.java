package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Group;
import com.vmesteonline.be.GroupType;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.utils.VoHelper;

@PersistenceCapable
public class VoUserGroup extends GeoLocation implements Comparable<VoUserGroup> {

	public static VoUserGroup createVoUserGroup(BigDecimal longitude, BigDecimal latitude, int radius, byte staircase, byte floor, String name, int impScore, int gType,
			PersistenceManager pm) throws InvalidOperation {
		
		String queryStr = "longitude=='"+longitude.toPlainString()
				+"' && latitude=='"+latitude.toPlainString()+"'"
				+" && groupType=="+gType;
		
		if( gType <= GroupType.STAIRCASE.getValue() )
			queryStr += " && staircase==" + staircase;
		
		if( gType <= GroupType.FLOOR.getValue() )
			queryStr += " && floor==" + floor;
		
		List<VoUserGroup> ugl =  (List<VoUserGroup>)pm.newQuery(VoUserGroup.class,queryStr).execute();
		
		if( 1==ugl.size() ) {
			return ugl.get(0);
			
		} else if( 1<ugl.size() ) {
			throw new InvalidOperation(VoError.GeneralError, "Two or more the same groups already registered + "+ugl.get(0)); 
			
		} else {
			VoUserGroup ug = new VoUserGroup(longitude, latitude, radius, staircase, floor, name, impScore, gType, pm);
			//all groups that could intersect should reset their intervisibility
			pm.makePersistent(ug);
			resetVisibiltyGroups(ug, pm);
			pm.makePersistent(ug);
			return ug;
		}
	}

	private static void resetVisibiltyGroups(VoUserGroup ug, PersistenceManager pm) {
		List<Long> allGroups = ug.findAllVisibleGroups(pm);
		
		for( Long nbgGrp : allGroups){
			if( nbgGrp != ug.getId() ){
				//reset visibility of group
				VoUserGroup nbGroup = pm.getObjectById(VoUserGroup.class, nbgGrp);
				nbGroup.getVisibleGroups().add(ug.getId());
				List<VoTopic> nnbgTopics = (List<VoTopic>)pm.newQuery(VoTopic.class, "userGroupId=="+nbgGrp).execute();
				if( null!=nnbgTopics)
					for( VoTopic tpc: nnbgTopics){
						tpc.setVisibleGroups(nbGroup.getVisibleGroups());
						pm.makePersistent(tpc);
					}
				pm.makePersistent(nbGroup);
			}
		}
		ug.setVisibleGroups(allGroups);
	}

	private VoUserGroup(BigDecimal longitude, BigDecimal latitude, int radius, byte staircase, byte floor, String name, int impScore, int gType, PersistenceManager pm){
		setLongitude(longitude);
		setLatitude(latitude);
		this.radius = radius;
		this.name = name;
		importantScore = impScore;
		groupType = gType;
		this.staircase = staircase;
		this.floor = floor;
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
	
	@Persistent
	private byte staircase;
	
	@Persistent
	private byte floor;
	

	@Persistent
	@Unindexed
	private List<Long> visibleGroups;

	
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
	
	public List<Long> getVisibleGroups() {
		return visibleGroups;
	}

	public void setVisibleGroups(List<Long> visibleGroups) {
		this.visibleGroups = visibleGroups;
	}

	@Override
	public String toString() {
		return "VoUserGroup [id=" + getId() + ", name=" + name + ", longitude=" + getLongitude() + ", latitude=" + getLatitude() + ", radius=" + radius +", staircase="+staircase +", floor="+floor
				+ "]";
	}

	@Override
	public int compareTo(VoUserGroup that) {
		return that.getLatitude().compareTo(this.getLatitude()) != 0 ? that.getLatitude().compareTo(this.getLatitude()) : that.getLongitude().compareTo(
				this.getLongitude()) != 0 ? that.getLongitude().compareTo(this.getLongitude()) : Integer.compare(that.radius, this.radius);
	}

	
	public List<Long> getVisibleGroups(PersistenceManager pm) {
		if( null==visibleGroups || visibleGroups.size() == 0){
			visibleGroups = findAllVisibleGroups(pm);
			pm.makePersistent(this);
		}
		return visibleGroups;
	}

	private List<Long> findAllVisibleGroups(PersistenceManager pm) {
		List<Long> vg = new ArrayList<Long>();
		
		if( groupType > GroupType.BUILDING.getValue() ){
			BigDecimal latMax = VoHelper.getLatitudeMax( new BigDecimal(latitude), radius);
			BigDecimal latMin = VoHelper.getLatitudeMin( new BigDecimal(latitude), radius);
			BigDecimal longMax = VoHelper.getLongitudeMax( new BigDecimal(longitude), new BigDecimal(latitude), radius);
			BigDecimal longMin = VoHelper.getLongitudeMin( new BigDecimal(longitude), new BigDecimal(latitude), radius);
			
			String filter = "groupType=="+groupType;
			List<VoUserGroup> groups = (List<VoUserGroup>) pm.newQuery( VoUserGroup.class, filter).execute();
			for( VoUserGroup ug: groups ){
				if( ug.getLatitude().compareTo( latMax ) <=0 && ug.getLatitude().compareTo( latMin ) >=0 
						&& ug.getLongitude().compareTo( longMax ) <= 0 && ug.getLongitude().compareTo( longMin ) >= 0)
					
					vg.add( ug.getId() );
			}
		} else {
			vg.add( this.getId() );
		}
		return vg;
	}
}
