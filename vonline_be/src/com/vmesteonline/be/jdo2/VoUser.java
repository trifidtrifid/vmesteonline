package com.vmesteonline.be.jdo2;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.utils.Defaults;

@PersistenceCapable
public class VoUser {

	public VoUser(String name, String lastName, String email, String password) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.messagesNum=0;
		this.topicsNum=0;
		this.likesNum=0;
		this.unlikesNum=0;
		this.rubrics = new ArrayList<VoRubric>(); 
	}

	public VoUserGroup getHomeGroup() {
		return home;
	}

	public List<VoRubric> getRubrics() {
		return rubrics;
	}

	public void setRubrics(List<VoRubric> rubrics) {
		this.rubrics = rubrics;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<VoUserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<VoUserGroup> groups) {
		this.groups = groups;
	}

	public void updateLikes( int likesDelta ) {
		likesNum += likesDelta;
	}
	public void updateUnlikes( int unlikesDelta ) {
		unlikesNum += unlikesDelta;
	}
	
	public void incrementMessages( int msgsDelta ) {
		messagesNum += msgsDelta;
	}
	
	public void incrementTopics( int topicsDelta ) {
		topicsNum += topicsDelta;
	}
	
	public void setLocation(long locCode, boolean doSave) throws InvalidOperation{
		Key addressKey = VoPostalAddress.getKeyValue(locCode);
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			VoPostalAddress userAddress;
			try {
				userAddress = pm.getObjectById(VoPostalAddress.class,addressKey);
			} catch (JDOObjectNotFoundException eonf) {
				throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Location not found by CODE=" + locCode);
			}
			/*if(null!=building){ //location already set, so user should be removed first
				building.removeUser(this);
			}
			building.addUser(this);*/
			pm.retrieve(userAddress);
			building = userAddress.getBuilding();
			pm.retrieve(building);
			home = building.getUserGroup();
			if( null!=groups){
				for ( VoUserGroup ug: groups){
					ug.setLatitude(home.getLatitude());
					ug.setLongitude(home.getLongitude());
				}
			} else {
				groups = new ArrayList<VoUserGroup>();
				
				groups.add(home);
				for ( VoGroup grp: Defaults.defaultGroups){
					groups.add(new VoUserGroup(this, grp));
				}
			}
			pm.makePersistent(this);
			pm.makePersistent(building);
		} finally{
			pm.close();
		}
	}
		
	@Persistent
	@Unowned
	private VoBuilding building;
	
	@Persistent
	@Unowned
	private List<VoUserGroup> groups;
	

	@Persistent
	@Unindexed
	@Unowned
	private VoUserGroup home;
	

	@Persistent
	@Unowned
	private List<VoRubric> rubrics;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String lastName;

	@Persistent
	private String email;

	@Persistent
	@Unindexed
	private String password;
	
	@Persistent
	@Unindexed
	private int messagesNum;
	
	@Persistent
	@Unindexed
	private int topicsNum;
	
	@Persistent
	@Unindexed
	private int likesNum;
	
	@Persistent
	@Unindexed
	private int unlikesNum;

	public void addRubric(VoRubric rubric) {
		rubrics.add(rubric);
		
	}
}
