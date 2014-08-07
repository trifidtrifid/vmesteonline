package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.NotificationFreq;
import com.vmesteonline.be.Notifications;
import com.vmesteonline.be.PrivacyType;
import com.vmesteonline.be.RelationsType;
import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.UserContacts;
import com.vmesteonline.be.UserFamily;
import com.vmesteonline.be.UserInfo;
import com.vmesteonline.be.UserInterests;
import com.vmesteonline.be.UserPrivacy;
import com.vmesteonline.be.UserProfile;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.UserStatus;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.utils.Defaults;

@PersistenceCapable
public class VoUser extends GeoLocation {
	
	public static int BASE_USER_SCORE = 100;

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
		this.deliveryAddresses = new HashMap<String, VoPostalAddress>();
		this.confirmCode = 0;
		this.emailConfirmed = false;
		this.avatarMessage = Defaults.defaultAvatarTopicUrl;
		this.avatarTopic = Defaults.defaultAvatarTopicUrl;
		this.avatarProfile = Defaults.defaultAvatarProfileUrl;
		this.avatarProfileShort = Defaults.defaultAvatarShortProfileUrl;
		this.relations = RelationsType.UNKNOWN;
		this.notificationsFreq = NotificationFreq.DAYLY.getValue();
		this.importancy = BASE_USER_SCORE;
		this.popularuty = BASE_USER_SCORE;
		this.lastNotified = this.registered = (int)(System.currentTimeMillis()/1000L);
		}

	public UserProfile getUserProfile() {
		UserProfile up = new UserProfile();
		up.contacts = getContacts();
		up.userInfo = getUserInfo();
		up.family = getUserFamily();
		up.privacy = getPrivacy();
		up.interests = new UserInterests( getInterests(), getJob() );
		up.importancy = getImportancy();
		up.populatity = getPopularuty();
		return up;
	}

	public UserFamily getUserFamily() {
		return userFamily;
	}

	public UserContacts getContacts() {
		return new UserContacts(getId(), UserStatus.CONFIRMED, null, mobilePhone, email);
	}

	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	public void setRelations(RelationsType relations) {
		this.relations = relations;
	}

	public ShortUserInfo getShortUserInfo() {
		return new ShortUserInfo(getId(), name, lastName, birthday, getAvatarTopic());
	}

	public UserInfo getUserInfo() {
		return new UserInfo(getId(), name, lastName, birthday, gender, avatarProfile);
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
		return 0 == confirmCode ? confirmCode = System.currentTimeMillis() % 98765 : confirmCode;
	}

	public void setConfirmCode(long confirmCode) {
		this.confirmCode = confirmCode;
	}

	public VoPostalAddress getDeliveryAddress(String key) {
		return deliveryAddresses != null ? deliveryAddresses.get(key) : null;
	}
	
	public int getLastNotified() {
		return lastNotified;
	}

	public void setLastNotified(int lastNotified) {
		this.lastNotified = lastNotified;
	}

	public void setLocation(long locCode, PersistenceManager pm) throws InvalidOperation {
		try {
			VoPostalAddress userAddress = pm.getObjectById(VoPostalAddress.class, locCode);
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

		// building from new address
		VoBuilding building = pm.getObjectById(VoBuilding.class, userAddress.getBuilding());

		// check if location is set
		if (null == building.getLatitude() || 0 == building.getLatitude().intValue()) {
			try {
				VoGeocoder.getPosition(building, false);
			} catch (InvalidOperation e) {
				e.printStackTrace();
			}
		}

		this.address = userAddress;

		this.setLatitude(building.getLatitude());
		this.setLongitude(building.getLongitude());
		if (null != groups && !groups.isEmpty()) {
			for (VoUserGroup ug : groups) {
				ug.setLatitude(building.getLatitude());
				ug.setLongitude(building.getLongitude());
				pm.makePersistent(ug);
			}
		} else {
			groups = new ArrayList<VoUserGroup>();
			for (VoGroup group : Defaults.defaultGroups) {
				VoUserGroup ug = new VoUserGroup(this, group);
				ug.setLatitude(building.getLatitude());
				ug.setLongitude(building.getLongitude());
				pm.makePersistent(ug);
				groups.add(ug);
			}
		}

		pm.makePersistent(this);
		pm.makePersistent(building);
	}

	// *****
	public void setDefaultUserLocation(PersistenceManager pm) {

		groups = new ArrayList<VoUserGroup>();
		groups.add(defaultGroup);
		this.setLatitude(defaultGroup.getLatitude());
		this.setLongitude(defaultGroup.getLongitude());

		pm.makePersistent(this);
	}

	public void addDeliveryAddress(VoPostalAddress pa, String textKey) throws InvalidOperation {
		if (deliveryAddresses == null)
			deliveryAddresses = new HashMap<String, VoPostalAddress>();
		deliveryAddresses.put(textKey, pa);
	}

	public boolean removeDeliveryAddress(String key) {
		return null == deliveryAddresses ? false : deliveryAddresses.remove(key) != null;
	}

	public List<String> getAddresses() {
		List<String> out = new ArrayList<String>();
		if (null != deliveryAddresses && deliveryAddresses.size() > 0) {
			Set<String> keySet = deliveryAddresses.keySet();
			if (null != keySet && keySet.size() > 0)
				out.addAll(keySet);
		}
		return out;
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
	private int birthday;

	@Persistent
	@Unindexed
	@Unowned
	private Map<String, VoPostalAddress> deliveryAddresses;

	@Persistent
	@Unindexed
	@Unowned
	private List<VoUserGroup> groups;

	@Persistent
	@Unowned
	@Unindexed
	private List<VoRubric> rubrics;

	@Persistent
	private int registered;
	
	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String lastName;

	@Persistent
	@Unindexed
	private int gender;

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

	@Persistent
	@Unindexed
	private String interests;
	
	@Persistent
	@Unindexed
	private String job;
	
	
	@Persistent(serialized="true")
	@Unindexed
	private UserFamily userFamily;
	
	@Persistent
	@Unindexed
	private String mobilePhone;

	@Persistent
	@Unindexed
	private RelationsType relations;
	
	@Persistent(serialized="true")
	@Unindexed
	private UserPrivacy privacy;
	
	@Persistent
	private int notificationsFreq;
	
	@Persistent
	@Unindexed
	private int importancy;
	
	@Persistent
	@Unindexed
	private int popularuty;
	
	@Persistent
	@Unindexed
	private int lastNotified;
	
	public UserPrivacy getPrivacy() {
		return null == privacy ? new UserPrivacy(0L, PrivacyType.NONE, PrivacyType.NONE ) : privacy;
	}

	public void setPrivacy(UserPrivacy privacy) {
		this.privacy = privacy;
	}

	public int getMessagesNum() {
		return messagesNum;
	}

	public void setMessagesNum(int messagesNum) {
		this.messagesNum = messagesNum;
	}

	public int getLikesNum() {
		return likesNum;
	}

	public void setLikesNum(int likesNum) {
		this.likesNum = likesNum;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public int getBirthday() {
		return birthday;
	}

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

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public RelationsType getRelations() {
		return relations;
	}

	public void setUserFamily(UserFamily userFamily) {
		this.userFamily = userFamily;
	}

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
	
	public Notifications getNotificationFreq(){
		return new Notifications( email, NotificationFreq.findByValue(notificationsFreq));
	}
	
	public void setNotifications( Notifications ntf ) throws InvalidOperation{
		if( null != ntf.email && ntf.email.trim().length() != 0 && !ntf.email.trim().equals( email ) ){ 
			if( !ntf.email.trim().matches(UserServiceImpl.emailreg))
				throw new InvalidOperation( VoError.IncorrectParametrs, "Invalid email '"+ntf.email+"'");
			setEmail( ntf.email.trim() );
		}
		if( NotificationFreq.findByValue(notificationsFreq) != ntf.freq ) setNotificationsFreq(ntf.freq.getValue());
	}
	
	public int getNotificationsFreq() {
		return notificationsFreq;
	}

	public void setNotificationsFreq(int notificationsFreq) {
		this.notificationsFreq = notificationsFreq;
	}

	public String toFullString() {
		return "VoUser [id=" + getId() + ", address=" + address + ", longitude=" + getLongitude() + ", latitude=" + getLatitude() + ", name=" + name
				+ ", lastName=" + lastName + ", email=" + email + ", password=" + password + ", messagesNum=" + messagesNum + ", topicsNum=" + topicsNum
				+ ", likesNum=" + likesNum + ", unlikesNum=" + unlikesNum + "]";
	}

	public int getImportancy() {
		return importancy;
	}

	public void setImportancy(int importancy) {
		this.importancy = importancy;
	}

	public int getPopularuty() {
		return popularuty;
	}

	public void setPopularuty(int popularuty) {
		this.popularuty = popularuty;
	}
	
}
