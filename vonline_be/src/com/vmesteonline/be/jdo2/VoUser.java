package com.vmesteonline.be.jdo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.VoError;
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
		this.messagesNum = 0;
		this.topicsNum = 0;
		this.likesNum = 0;
		this.unlikesNum = 0;
		this.rubrics = new ArrayList<VoRubric>();
		this.addresses = new TreeSet<VoPostalAddress>();
	}

	public ShortUserInfo getShortUserInfo() {
		return new ShortUserInfo(getId(), name, lastName, 0, null);
	}

	public VoUserGroup getGroup(long id) throws InvalidOperation {
		if (home.getId().getId() == id)
			return home;

		for (VoUserGroup g : groups) {
			if (g.getId().getId() == id)
				return g;
		}
		throw new InvalidOperation(VoError.IncorrectParametrs, "user with id " + getId() + " have no group with id " + id);
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

	public Set<VoUserGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<VoUserGroup> groups) {
		this.groups = groups;
	}

	public void updateLikes(int likesDelta) {
		likesNum += likesDelta;
	}

	public void updateUnlikes(int unlikesDelta) {
		unlikesNum += unlikesDelta;
	}

	public void incrementMessages(int msgsDelta) {
		messagesNum += msgsDelta;
	}

	public void incrementTopics(int topicsDelta) {
		topicsNum += topicsDelta;
	}

	public VoPostalAddress getAddress() {
		return address;
	}

	public void setLocation(long locCode, boolean doSave) throws InvalidOperation {
		setLocation(locCode, doSave, null);
	}

	public void setLocation(long locCode, boolean doSave, PersistenceManager _pm) throws InvalidOperation {
		Key addressKey = VoPostalAddress.getKeyValue(locCode);
		PersistenceManager pm = null == _pm ? PMF.get().getPersistenceManager() : _pm;
		try {
			VoPostalAddress userAddress;
			try {
				userAddress = pm.getObjectById(VoPostalAddress.class, addressKey);
			} catch (JDOObjectNotFoundException eonf) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Location not found by CODE=" + locCode);
			}
			setCurrentPostalAddress(userAddress, pm);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	/**
	 * MEthod set current postal address of the user and register user in the
	 * building
	 * 
	 * @param userAddress
	 *          newUSer postal address
	 * @param pm
	 *          - PersistenceManager to manage the objects
	 */
	public void setCurrentPostalAddress(VoPostalAddress userAddress, PersistenceManager pm) {
		VoBuilding building = null;

		if (null != this.getAddress()) { // location already set, so user should
																			// be removed first
			building = this.address.getBuilding();
			if (null != building)
				building.removeUser(this);
		}
		pm.retrieve(userAddress);
		// building from new address
		building = userAddress.getBuilding();
		if (null != building)
			building.addUser(this);

		home = null;
		this.address = userAddress;
		if (null != building) {
			pm.retrieve(building);
			home = building.getUserGroup();
			if (null != groups && !groups.isEmpty()) {
				for (VoUserGroup ug : groups) {
					ug.setLatitude(home.getLatitude());
					ug.setLongitude(home.getLongitude());
				}
			} else {
				groups = new TreeSet<VoUserGroup>();

				groups.add(home);
				for (VoGroup grp : Defaults.defaultGroups) {
					if (!grp.isHome())
						groups.add(new VoUserGroup(this, grp));
				}
			}
		} else {
			groups = new TreeSet<VoUserGroup>();
		}
		addPostalAddress(userAddress, pm);

		pm.makePersistent(this);
		pm.makePersistent(building);
	}

	public void addPostalAddress(VoPostalAddress pa) {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			addPostalAddress(pa, pm);
		} finally {
			pm.close();
		}
	}

	public void addPostalAddress(VoPostalAddress pa, PersistenceManager pm) {
		addresses.add(pa);
	}

	public void setCurrentPostalAddress(VoPostalAddress pa) {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			setCurrentPostalAddress(pa, pm);
		} finally {
			pm.close();
		}
	}

	public Set<VoPostalAddress> getAddresses() {
		return addresses;
	}

	@Persistent
	@Unowned
	private VoPostalAddress address;

	@Persistent
	@Unowned
	private Set<VoPostalAddress> addresses;

	@Persistent
	@Unowned
	private Set<VoUserGroup> groups;

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

	@Override
	public String toString() {
		return "VoUser [id=" + id + ", name=" + name + ", email=" + email + "]";
	}

	public String toFullString() {
		return "VoUser [id=" + id + ", address=" + address + ", home=" + home + ", name=" + name + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", messagesNum=" + messagesNum + ", topicsNum=" + topicsNum + ", likesNum=" + likesNum + ", unlikesNum="
				+ unlikesNum + "]";
	}
}
