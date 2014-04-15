package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.RelationsType;
import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.UserInfo;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.utils.Defaults;

@PersistenceCapable
public class VoUser extends GeoLocation {

	private static VoUserGroup defaultGroup = new VoUserGroup("Мой Город", 10000, new BigDecimal("60.0"), new BigDecimal("30.0"));

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
		this.deliveryAddresses = new TreeSet<VoPostalAddress>();
		this.confirmCode = 0;
		this.emailConfirmed = false;
		this.avatarMessage = Defaults.defaultAvatarTopicUrl;
		this.avatarTopic = Defaults.defaultAvatarTopicUrl;
		this.avatarProfile = Defaults.defaultAvatarProfileUrl;
		this.avatarProfileShort = Defaults.defaultAvatarShortProfileUrl;
		this.relations = RelationsType.UNKNOWN;

	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public void setRelations(RelationsType relations) {
		this.relations = relations;
	}

	public ShortUserInfo getShortUserInfo() {
		return new ShortUserInfo(getId(), name, lastName, 0, getAvatarTopic());
	}

	public UserInfo getUserInfo() {
		return new UserInfo(getId(), name, lastName, 0, getAvatarProfile(), birthday, relations);
	}

	public VoUserGroup getGroupById(long id) throws InvalidOperation {
		for (VoUserGroup g : groups) {
			if (g.getId() == id)
				return g;
		}
		throw new InvalidOperation(VoError.IncorrectParametrs, "user with id " + getId() + " have no group with id " + id);
	}

	public List<VoRubric> getRubrics() {
		return rubrics;
	}

	public void setRubrics(List<VoRubric> rubrics) {
		this.rubrics = rubrics;
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

	public long getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(long confirmCode) {
		this.confirmCode = confirmCode;
	}

	public void setLocation(long locCode, PersistenceManager pm) throws InvalidOperation {
		Key addressKey = VoPostalAddress.getKeyValue(locCode);
		try {
			VoPostalAddress userAddress = pm.getObjectById(VoPostalAddress.class, addressKey);
			setCurrentPostalAddress(userAddress, pm);
		} catch (JDOObjectNotFoundException eonf) {
			throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Location not found by CODE=" + locCode);
		}
	}

	/**
	 * MEthod set current postal address of the user and register user in the building
	 * 
	 * @param userAddress
	 *          newUSer postal address
	 * @param pm
	 *          - PersistenceManager to manage the objects
	 */

	// TODO should test removing
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

		this.address = userAddress;
		if (null != building) {
			pm.retrieve(building);
			VoUserGroup home = userAddress.getUserHomeGroup();
			this.setLatitude(home.getLatitude());
			this.setLongitude(home.getLongitude());
			if (null != groups && !groups.isEmpty()) {
				for (VoUserGroup ug : groups) {
					ug.setLatitude(home.getLatitude());
					ug.setLongitude(home.getLongitude());
				}
			} else {
				groups = new ArrayList<VoUserGroup>();
				groups.add(home);
				for (VoGroup grp : Defaults.defaultGroups) {
					if (!grp.isHome())
						groups.add(new VoUserGroup(this, grp));
				}
			}
		} else {
			groups = new ArrayList<VoUserGroup>();
		}
		addPostalAddress(userAddress);

		pm.makePersistent(this);
		pm.makePersistent(building);
	}

	// *****
	public void setDefaultUserLocation(PersistenceManager pm) {

		VoBuilding building = null;
		if (null != this.getAddress()) { // location already set, so user should
																			// be removed first
			building = this.address.getBuilding();
			if (null != building)
				building.removeUser(this);
		}
		groups = new ArrayList<VoUserGroup>();
		groups.add(defaultGroup);
		this.setLatitude(defaultGroup.getLatitude());
		this.setLongitude(defaultGroup.getLongitude());

		pm.makePersistent(this);
	}

	public void addPostalAddress(VoPostalAddress pa) {
		deliveryAddresses.add(pa);
	}

	public Set<VoPostalAddress> getAddresses() {
		return deliveryAddresses;
	}

	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	@Persistent
	@Unindexed
	@Unowned
	private VoPostalAddress address;

	@Persistent
	@Unindexed
	private String birthday;

	
	@Persistent
	@Unindexed
	@Unowned
	private Set<VoPostalAddress> deliveryAddresses;

	@Persistent
	@Unindexed
	@Unowned
	private List<VoUserGroup> groups;

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

	@Persistent
	@Unindexed
	private long confirmCode;

	@Persistent
	@Unindexed
	private boolean emailConfirmed;

	@Persistent
	@Unindexed
	private String avatarMessage;

	@Persistent
	@Unindexed
	private String avatarTopic;

	@Persistent
	@Unindexed
	private String avatarProfile;

	@Persistent
	@Unindexed
	private String avatarProfileShort;

	public String getAvatarMessage() {
		return avatarMessage;
	}

	public void setAvatarMessage(String avatarMessage) {
		this.avatarMessage = avatarMessage;
	}

	public String getAvatarTopic() {
		return avatarTopic;
	}

	public void setAvatarTopic(String avatarTopic) {
		this.avatarTopic = avatarTopic;
	}

	public String getAvatarProfile() {
		return avatarProfile;
	}

	public void setAvatarProfile(String avatarProfile) {
		this.avatarProfile = avatarProfile;
	}

	public String getAvatarProfileShort() {
		return avatarProfileShort;
	}

	public void setAvatarProfileShort(String avatarProfileShort) {
		this.avatarProfileShort = avatarProfileShort;
	}

	public int getTopicsNum() {
		return topicsNum;
	}

	public void setTopicsNum(int topicsNum) {
		this.topicsNum = topicsNum;
	}

	public int getUnlikesNum() {
		return unlikesNum;
	}

	public void setUnlikesNum(int unlikesNum) {
		this.unlikesNum = unlikesNum;
	}

	public void addRubric(VoRubric rubric) {
		rubrics.add(rubric);
	}

	@Persistent
	@Unindexed
	private String mobilePhone;

	@Persistent
	@Unindexed
	RelationsType relations;

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@Override
	public String toString() {
		return "VoUser [id=" + getId() + ", name=" + name + ", email=" + email + "]";
	}

	public String toFullString() {
		return "VoUser [id=" + getId() + ", address=" + address + ", longitude=" + getLongitude() + ", latitude=" + getLatitude() + ", name=" + name
				+ ", lastName=" + lastName + ", email=" + email + ", password=" + password + ", messagesNum=" + messagesNum + ", topicsNum=" + topicsNum
				+ ", likesNum=" + likesNum + ", unlikesNum=" + unlikesNum + "]";
	}
}
