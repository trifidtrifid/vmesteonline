package com.vmesteonline.be;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.GeoLocation;
import com.vmesteonline.be.jdo2.VoInviteCode;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.InviteCodeUploader;
import com.vmesteonline.be.utils.VoHelper;

public class UserServiceImpl extends ServiceImpl implements UserService.Iface {

	public UserServiceImpl() {
	}

	public UserServiceImpl(String sessId) {
		super(sessId);
	}

	public UserServiceImpl(HttpSession sess) {
		super(sess);
	}

	@Override
	public void updateUserInfo(UserInfo userInfo) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {
			VoUser user = getCurrentUser(pm);
			user.setName(userInfo.firstName);
			user.setLastName(userInfo.lastName);
			user.setGender(userInfo.gender);
			user.setBirthday(userInfo.birthday);
			// userInfo.avatar
			// VoHelper.replaceURL(user, "avatar", userInfo.avatar, 0, true, pm);
			pm.makePersistent(user);
		} finally {
			pm.close();
		}
	}

	public static String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	public static String phonereg = "[\\d-.()+ ]{7,21}";

	// TODO this method is forbidden should be removed. use getShortProfile
	// instead
	@Override
	public ShortUserInfo getShortUserInfo() throws InvalidOperation {
		return getShortUserInfo(getCurrentUserId());
	}

	@Override
	public ShortProfile getShortProfile() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser voUser = getCurrentUser(pm);
			ShortProfile sp = new ShortProfile(voUser.getId(), voUser.getName(), voUser.getLastName(), 0, voUser.getAvatarMessage(), "", "");
			VoPostalAddress pa = voUser.getAddress();

			if (pa != null) {
				VoBuilding building = pm.getObjectById(VoBuilding.class, pa.getBuilding());
				sp.setAddress(building.getAddressString());
			}
			return sp;
		} finally {
			pm.close();
		}
	}

	public static ShortUserInfo getShortUserInfo(long userId) {
		if (userId == 0)
			return null;

		PersistenceManager pm = PMF.getPm();
		try {
			VoUser voUser = pm.getObjectById(VoUser.class, userId);
			return voUser.getShortUserInfo();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("request short user info for absent user " + userId);
		} finally {
			pm.close();
		}
		return null;
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation {
		try {

			PersistenceManager pm = PMF.getPm();

			try {
				long userId = getCurrentUserId(pm);

				VoUser user;
				try {
					user = pm.getObjectById(VoUser.class, userId);
				} catch (JDOObjectNotFoundException e) {
					logger.info("Current user doues not exists. Not found by Id.");
					getCurrentSession(pm).setUserId(null);
					throw new InvalidOperation(VoError.NotAuthorized, "can't find user by id");
				}

				logger.info("find user email " + user.getEmail() + " name " + user.getName());

				if (user.getGroups() == null) {
					logger.warning("user with id " + Long.toString(userId) + " has no any groups");
					throw new InvalidOperation(VoError.GeneralError, "can't find user bu id");
				}
				List<Group> groups = new ArrayList<Group>();
				for (VoUserGroup group : user.getGroups()) {
					logger.info("return group " + group.getName());
					groups.add(group.createGroup());
				}

				return groups;
			} finally {
				pm.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, e.getMessage());
		}
	}

	@Override
	public List<Rubric> getUserRubrics() throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.getPm();
		try {

			VoUser user = pm.getObjectById(VoUser.class, userId);
			if (user == null) {
				logger.severe("can't find user by id " + Long.toString(userId));
				throw new InvalidOperation(VoError.NotAuthorized, "can't find user bu id");
			}

			logger.info("find user name " + user.getEmail());

			if (user.getRubrics() == null) {
				logger.warning("user with id " + Long.toString(userId) + " has no any rubrics");
				throw new InvalidOperation(VoError.GeneralError, "No Rubrics are initialized for user=" + userId);
			}

			List<Rubric> rubrics = new ArrayList<Rubric>();
			for (VoRubric r : user.getRubrics()) {
				rubrics.add(r.createRubric());
			}
			return rubrics;
		} finally {
			pm.close();
		}
	}

	public static List<String> getLocationCodesForRegistration() throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoPostalAddress> postalAddresses = pm.getExtent(VoPostalAddress.class, true);
			if (!postalAddresses.iterator().hasNext()) {
				throw new InvalidOperation(VoError.GeneralError, "can't find any location codes");
			}

			List<String> locations = new ArrayList<String>();
			for (VoPostalAddress pa : postalAddresses) {
				pm.retrieve(pa);
				String code = "" + pa.getAddressCode();
				pm.makePersistent(new VoInviteCode(code, pa.getId()));
				;
				locations.add(code);
			}
			return locations;
		} finally {
			pm.close();
		}
	}

	@Override
	public void deleteUserAddress(PostalAddress newAddress) throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		try {

			VoPostalAddress addr = new VoPostalAddress(newAddress, pm);
			VoUser currentUser = getCurrentUser(pm);

			List<String> addresses = currentUser.getAddresses();

			for (String addrName : addresses) {
				if (currentUser.getDeliveryAddress(addrName).equals(addr)) {
					currentUser.removeDeliveryAddress(addrName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to deleteUserAddress. " + e.getMessage());
		} finally {
			pm.close();
		}

	}

	@Override
	public List<Country> getCounties() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoCountry> vocs = pm.getExtent(VoCountry.class);
			List<Country> cl = new ArrayList<Country>();
			for (VoCountry voc : vocs) {
				cl.add(voc.getCountry());
			}
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getCounties. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public UserProfile getUserProfile(long userId) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();

		try {
			VoUser currentUser = getCurrentUser(pm);
			if (userId == 0) {
				return currentUser.getUserProfile();
			}

			VoUser user;
			try {
				user = pm.getObjectById(VoUser.class, userId);

			} catch (JDOObjectNotFoundException e) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "No user found by ID: " + userId);
			}
			UserProfile uProfile = user.getUserProfile();
			UserPrivacy uPrivacy = user.getPrivacy();
			// show everything if no privacy set
			if (uPrivacy.contacts == PrivacyType.EVERYBODY && uPrivacy.profile == PrivacyType.EVERYBODY)
				return uProfile;
			// show nothing if full privacy
			if (uPrivacy.contacts == PrivacyType.NONE && uPrivacy.profile == PrivacyType.NONE) {
				uProfile.contacts = new UserContacts();
				uProfile.interests = new UserInterests();
				uProfile.family = new UserFamily();
				uProfile.privacy = new UserPrivacy();
				return uProfile;
			}

			// otherwise lets determine users relations that would be stored as PrivacyType
			PrivacyType relation = determineProvacyByAddresses(currentUser, user);

			// filter information according to relations
			if (uPrivacy.contacts.getValue() < relation.getValue()) {// remove contacts
				uProfile.contacts = new UserContacts();
			}

			if (uPrivacy.profile.getValue() < relation.getValue()) {// remove contacts
				uProfile.interests = new UserInterests();
				uProfile.family = new UserFamily();
			}
			uProfile.importancy = user.getImportancy();
			uProfile.populatity = user.getPopularuty();
			
			return uProfile;

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "unknow user id: " + Long.toString(userId));
		} finally {
			pm.close();
		}
	}

	private PrivacyType determineProvacyByAddresses(VoUser currentUser, VoUser user) {
		PrivacyType relation = PrivacyType.EVERYBODY;

		VoPostalAddress cuAddr = currentUser.getAddress();
		VoPostalAddress uAddr;
		if (null == cuAddr || null == (uAddr = user.getAddress())) {
			relation = PrivacyType.EVERYBODY;

		} else if (cuAddr.getBuilding() == uAddr.getBuilding() && 0 == cuAddr.getBuilding()) { // the same building

			relation = PrivacyType.HOME;

			if (cuAddr.getStaircase() == uAddr.getStaircase() && 0 != uAddr.getStaircase())
				relation = PrivacyType.STAIRCASE;

			else if (cuAddr.getFloor() == uAddr.getFloor() && 0 != uAddr.getFloor())
				relation = PrivacyType.STAIRCASE; // TODO could be a Floor privacy

			else if (cuAddr.getFlatNo() == uAddr.getFlatNo() && 0 != uAddr.getFlatNo())
				relation = PrivacyType.STAIRCASE; // TODO could be a Flat privacy

		} else { // lets determine the relation as according to the distance

			int maxRadius = VoHelper.calculateRadius(user, currentUser);
			if (maxRadius <= Defaults.radiusStarecase)
				relation = PrivacyType.STAIRCASE;
			else if (maxRadius <= Defaults.radiusHome)
				relation = PrivacyType.HOME;
			else if (maxRadius <= Defaults.radiusSmall)
				relation = PrivacyType.DISTRICT;
		}
		return relation;
	}

	@Override
	public UserContacts getUserContacts() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser u = getCurrentUser(pm);
			UserContacts uc = new UserContacts();
			if (u.getAddress() == null) {
				uc.setAddressStatus(UserStatus.UNCONFIRMED);
			} else {
				uc.setHomeAddress(u.getAddress().getPostalAddress());
			}
			uc.setEmail(u.getEmail());
			uc.setMobilePhone(u.getMobilePhone());
			return uc;
		} finally {
			pm.close();
		}
	}

	@Override
	public void changePassword(String oldPwd, String newPwd) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser cu = getCurrentUser(pm);
			if (!cu.getPassword().equals(oldPwd))
				throw new InvalidOperation(VoError.IncorrectParametrs, "Old password dont match.");
			if (null == newPwd || newPwd.length() < 3) {
				throw new InvalidOperation(VoError.IncorrectPassword, "New password too short.");
			}
			cu.setPassword(newPwd);
			pm.makePersistent(cu);
		} finally {
			pm.close();
		}

	}

	@Override
	public void updatePrivacy(UserPrivacy privacy) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser cu = getCurrentUser(pm);
			cu.setPrivacy(privacy);
			pm.makePersistent(cu);
		} finally {
			pm.close();
		}
	}

	@Override
	public void updateContacts(UserContacts contacts) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser user = getCurrentUser(pm);
			if (null != contacts.getMobilePhone())
				if (contacts.getMobilePhone().matches(phonereg))
					user.setMobilePhone(contacts.getMobilePhone());
				else
					throw new InvalidOperation(VoError.IncorrectParametrs, "Invalid Phone format '" + contacts.getMobilePhone()
							+ "'. Should have format like 79219876543, +7(821)1234567, etc");

			if (null != contacts.getEmail() && !contacts.getEmail().trim().equalsIgnoreCase(user.getEmail())) {
				if (contacts.getEmail().matches(emailreg)) {
					user.setEmail(contacts.getEmail());
					user.setEmailConfirmed(false);
				} else
					throw new InvalidOperation(VoError.IncorrectParametrs, "Invalid Email format '" + contacts.getEmail() + "'. ");
			}
			if (null != contacts.getHomeAddress()) {
				VoPostalAddress pa = new VoPostalAddress(contacts.getHomeAddress(), pm);

				if (user.getAddress() == null || pa.getId() != user.getAddress().getId()) {
					try {
						user.setLocation(pa.getAddressCode(), pm);
					} catch (InvalidOperation e) {
						e.printStackTrace();
						throw new InvalidOperation(VoError.IncorrectParametrs, "Address is incorrect." + e.why);
					} catch (Exception e) {
						e.printStackTrace();
						throw new InvalidOperation(VoError.IncorrectParametrs, "Address is incorrect." + e.getMessage());
					}
				}
			}
			pm.makePersistent(user);
		} finally {
			pm.close();
		}

	}

	@Override
	public void updateFamily(UserFamily family) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser cu = getCurrentUser(pm);
			cu.setUserFamily(family);
			pm.makePersistent(cu);
		} finally {
			pm.close();
		}
	}

	@Override
	public void updateInterests(UserInterests interests) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser cu = getCurrentUser(pm);
			cu.setInterests(interests.userInterests);
			cu.setJob(interests.job);
			pm.makePersistent(cu);
		} finally {
			pm.close();
		}

	}

	@Override
	public UserContacts getUserContactsExt(long userId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser u = pm.getObjectById(VoUser.class, userId);
			UserContacts uc = new UserContacts();
			if (u.getAddress() == null) {
				uc.setAddressStatus(UserStatus.UNCONFIRMED);
			} else {
				uc.setHomeAddress(u.getAddress().getPostalAddress());
			}
			uc.setEmail(u.getEmail());
			uc.setMobilePhone(u.getMobilePhone());
			return uc;
		} catch (JDOObjectNotFoundException ioe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Access denied");

		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<City> getCities(long countryId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {

			List<City> cl = new ArrayList<City>();
			Query q = pm.newQuery(VoCity.class);
			q.setFilter("country == " + countryId);
			List<VoCity> cs = (List<VoCity>) q.execute();
			for (VoCity c : cs) {
				cl.add(c.getCity());
			}
			q.closeAll();
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getCities. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Street> getStreets(long cityId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			List<Street> cl = new ArrayList<Street>();
			Query q = pm.newQuery(VoStreet.class);
			q.setFilter("city == :key");
			List<VoStreet> cs = (List<VoStreet>) q.execute(cityId);
			for (VoStreet c : cs) {
				pm.retrieve(c);
				cl.add(c.getStreet());
			}
			q.closeAll();
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to load STreets. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Building> getBuildings(long streetId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			List<Building> cl = new ArrayList<Building>();
			Query q = pm.newQuery(VoBuilding.class);
			q.setFilter("streetId == :key");
			List<VoBuilding> cs = (List<VoBuilding>) q.execute(KeyFactory.createKey(VoStreet.class.getSimpleName(), streetId));
			for (VoBuilding c : cs) {
				cl.add(c.getBuilding());
			}
			q.closeAll();
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getBuildings. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public void updateUserAvatar(String url) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();

		try {
			VoUser voUser = getCurrentUser(pm);

			voUser.setAvatarTopic(url);
			voUser.setAvatarMessage(url);
			voUser.setAvatarProfileShort(url);
			voUser.setAvatarProfile(url);
			pm.makePersistent(voUser);

		} finally {
			pm.close();
		}

	}

	@Override
	public FullAddressCatalogue getAddressCatalogue() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {

			Extent<VoCountry> vocs = pm.getExtent(VoCountry.class);
			Set<Country> cl = new TreeSet<Country>();
			for (VoCountry voc : vocs) {
				cl.add(voc.getCountry());
			}

			Extent<VoCity> vocis = pm.getExtent(VoCity.class);
			List<City> cil = new ArrayList<City>();
			for (VoCity voc : vocis) {
				cil.add(voc.getCity());
			}

			Extent<VoStreet> voss = pm.getExtent(VoStreet.class);
			List<Street> sl = new ArrayList<Street>();
			for (VoStreet voc : voss) {
				sl.add(voc.getStreet());
			}

			Extent<VoBuilding> vobs = pm.getExtent(VoBuilding.class);
			List<Building> bl = new ArrayList<Building>();
			for (VoBuilding voc : vobs) {
				bl.add(voc.getBuilding());
			}
			return new FullAddressCatalogue(cl, cil, sl, bl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	// TODO this method called only once in test. may be unused?
	@Override
	public boolean setUserAddress(PostalAddress newAddress) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			currentUser.setCurrentPostalAddress(new VoPostalAddress(newAddress, pm), pm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			pm.close();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Country createNewCountry(String name) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			// TODO check that there is no country with the same name
			VoCountry vc = new VoCountry(name, pm);
			Query q = pm.newQuery(VoCountry.class);
			q.setFilter("name == '" + name + "'");
			List<VoCountry> countries = (List<VoCountry>) q.execute();
			if (countries.size() > 0) {
				logger.info("City was NOT created. The same City was registered. Return an old one: " + countries.get(0));
				return countries.get(0).getCountry();
			} else {
				logger.info("Country '" + name + "'was created.");
				pm.makePersistent(vc);
				return vc.getCountry();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCountry. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public City createNewCity(long countryId, String name) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoCountry vco = pm.getObjectById(VoCountry.class, countryId);
			return new VoCity(vco, name, pm).getCity();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCity. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Street createNewStreet(long cityId, String name) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			// TODO check that there is no street with the same name
			VoCity vc = pm.getObjectById(VoCity.class, cityId);
			return new VoStreet(vc, name, pm).getStreet();

		} catch (Throwable e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Building createNewBuilding(String zipCode, long streetId, String fullNo, String longitude, String lattitude) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			// TODO check that there is no building with the same name
			VoStreet vs = pm.getObjectById(VoStreet.class, streetId);
			Query q = pm.newQuery(VoBuilding.class);
			q.setFilter("streetId == " + streetId + " &&  fullNo == '" + fullNo + "'");
			List<VoBuilding> buildings = (List<VoBuilding>) q.execute();
			if (buildings.size() > 0) {
				logger.info("VoBuilding was NOT created. The same VoBuilding was registered. Return an old one: " + buildings.get(0));
				return buildings.get(0).getBuilding();
			} else {
				logger.info("VoBuilding '" + fullNo + "'was created.");
				VoBuilding voBuilding = new VoBuilding(zipCode, vs, fullNo, new BigDecimal(null == longitude || "".equals(longitude) ? "0" : longitude),
						new BigDecimal(null == lattitude || "".equals(lattitude) ? "0" : lattitude), pm);
				if (longitude == null || lattitude == null || longitude.isEmpty() || lattitude.isEmpty()) { // calculate
					// location
					try {
						Pair<String, String> position = VoGeocoder.getPosition(voBuilding, false);
						voBuilding.setLocation(new BigDecimal(position.first), new BigDecimal(position.second));
					} catch (Exception e) {
						e.printStackTrace();
						throw new InvalidOperation(VoError.GeneralError, "FAiled to determine location of the building." + e.getMessage());
					}
				}
				pm.makePersistent(voBuilding);
				return voBuilding.getBuilding();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean addUserAddress(PostalAddress newAddress) throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			pm.retrieve(currentUser);
			currentUser.addDeliveryAddress(new VoPostalAddress(newAddress, pm), null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			pm.close();
		}

	}

	@Override
	public Set<PostalAddress> getUserAddresses() throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			Set<PostalAddress> pas = new HashSet<PostalAddress>();
			for (String pa : currentUser.getAddresses()) {
				pas.add(currentUser.getDeliveryAddress(pa).getPostalAddress());
			}
			return pas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public PostalAddress getUserHomeAddress() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			if (null == currentUser)
				throw new InvalidOperation(VoError.NotAuthorized, "No currnet user is set.");
			VoPostalAddress address = currentUser.getAddress();
			if (null == address) {
				return null;
			}
			return address.getPostalAddress(pm);
		} finally {
			pm.close();
		}
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	// ======================================================================================================================

	private static final Set<String> publicMethods = new HashSet<String>(Arrays.asList(new String[] {

	"allMethods are public"

	}));

	@Override
	public boolean isPublicMethod(String method) {
		return true;// publicMethods.contains(method);
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.USER_SI.ordinal();
	}

	@Override
	public List<ShortUserInfo> getNeighbours() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser();
			List<VoUser> users = getUsersByLocation(currentUser, 30, pm);
			return VoHelper.convertMutableSet(users, new ArrayList<ShortUserInfo>(), new ShortUserInfo());
		} finally {
			pm.close();
		}
	}

	@Override
	public List<ShortUserInfo> getNeighboursByGroup(long groupId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser();
			VoUserGroup group = pm.getObjectById(VoUserGroup.class, groupId);
			List<VoUser> users = getUsersByLocation(currentUser, group.getRadius(), pm);
			return VoHelper.convertMutableSet(users, new ArrayList<ShortUserInfo>(), new ShortUserInfo());
		} finally {
			pm.close();
		}
	}

	public static List<VoUser> getUsersByLocation(GeoLocation loc, int radius, PersistenceManager pm) {
		List<VoUser> users = new ArrayList<VoUser>();

		// BigDecimal latMin = VoHelper.getLatitudeMin(loc.getLatitude(), radius).setScale(6, RoundingMode.HALF_UP);

		List<VoUser> allUsers = (List<VoUser>) pm.newQuery(VoUser.class, "").execute();
		for (VoUser user : allUsers) {
			if (VoHelper.findMinimumGroupRadius(loc, user) <= radius) {
				if (loc.getId() != user.getId())
					users.add(user);
			}
		}
		return users;
	}

	@Override
	public void updateNotifications(Notifications notifications) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser();
			currentUser.setNotifications(notifications);

		} finally {
			pm.close();
		}

	}

	@Override
	public String getGroupMap(long groupId, String color) throws InvalidOperation, TException {
		if (null == color || 0 == color.length()) {
			color = "8822DDC0";
		}
		String mapKey = "yandex.group.map." + groupId + "." + color;
		Object url = ServiceImpl.getObjectFromCache(mapKey);
		if (null != url)
			if (url instanceof String) {
				return (String) url;
			} else {
				// incorrect type of object in the cache
				ServiceImpl.removeObjectFromCache(mapKey);
			}
		PersistenceManager pm = PMF.getPm();

		try {
			VoUserGroup userGroup = pm.getObjectById(VoUserGroup.class, groupId);
			String los = userGroup.getLongitude().toPlainString();
			String las = userGroup.getLatitude().toPlainString();
			url = "http://static-maps.yandex.ru/1.x/?l=map&pt=" + los + "," + las + ",pm2am" + "&pl=c:" + color + ",f:" + color + ",w:1";

			double lad = userGroup.getLatitude().doubleValue();
			double lod = userGroup.getLongitude().doubleValue();

			double laDelta = VoHelper.getLatitudeMax(userGroup.getLatitude(), userGroup.getRadius()).doubleValue() - lad;
			double loDelta = VoHelper.getLongitudeMax(userGroup.getLongitude(), userGroup.getLatitude(), userGroup.getRadius()).doubleValue() - lod;

			for (double i = 0.0D; i < 2 * Math.PI; i += Math.PI / 30) {
				url += "," + (lod + Math.sin(i) * loDelta) + "," + (lad + Math.cos(i) * laDelta);
			}
			ServiceImpl.putObjectToCache(mapKey, (String) url);

		} finally {
			pm.close();
		}
		return (String) url;
	}

}