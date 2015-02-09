package com.vmesteonline.be;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.utils.StorageHelper;

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

		PersistenceManager pm = getPM();
		try {
			VoUser user = getCurrentUser(pm);
			user.setName(userInfo.firstName);
			user.setLastName(userInfo.lastName);
			user.setLastName(userInfo.lastName);
			pm.makePersistent(user);
		} finally {
			
		}
	}

	private static String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	private static String phonereg = "[\\d-.()+ ]{7,21}";

	@Override
	public void updateUserContacts(UserContacts contacts) throws InvalidOperation {

		PersistenceManager pm = getPM();
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
			
		}
	}

	// TODO this method is forbidden should be removed. use getShortProfile
	// instead
	@Override
	public ShortUserInfo getShortUserInfo() throws InvalidOperation {
		return getShortUserInfo(getCurrentUserId());
	}

	@Override
	public ShortProfile getShortProfile() throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			VoUser voUser = getCurrentUser(pm);
			ShortProfile sp = new ShortProfile(voUser.getId(), voUser.getName(), voUser.getLastName(), 0, voUser.getAvatarMessage(), "", "");
			VoPostalAddress pa = voUser.getAddress();
			if (pa != null) {
				sp.setAddress(pa.getBuilding().getAddressString());
			}
			return sp;
		} finally {
			
		}
	}

	public ShortUserInfo getShortUserInfo(long userId) {

		PersistenceManager pm = getPM();
		try {
			VoUser voUser = pm.getObjectById(VoUser.class, userId);
			return voUser.getShortUserInfo();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("request short user info for absent user " + userId);
		} finally {
			
		}
		return null;
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation {
		try {

			PersistenceManager pm = getPM();
			
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
					logger.warn("user with id " + Long.toString(userId) + " has no any groups");
					throw new InvalidOperation(VoError.GeneralError, "can't find user bu id");
				}
				List<Group> groups = new ArrayList<Group>();
				for (VoUserGroup group : user.getGroups()) {
					logger.info("return group " + group.getName());
					groups.add(group.createGroup());
				}

				return groups;
			} finally {
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, e.getMessage());
		}
	}

	@Override
	public List<Rubric> getUserRubrics() throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		PersistenceManager pm = getPM();
		try {

			VoUser user = pm.getObjectById(VoUser.class, userId);
			if (user == null) {
				logger.error("can't find user by id " + Long.toString(userId));
				throw new InvalidOperation(VoError.NotAuthorized, "can't find user bu id");
			}

			logger.info("find user name " + user.getEmail());

			if (user.getRubrics() == null) {
				logger.warn("user with id " + Long.toString(userId) + " has no any rubrics");
				throw new InvalidOperation(VoError.GeneralError, "No Rubrics are initialized for user=" + userId);
			}

			List<Rubric> rubrics = new ArrayList<Rubric>();
			for (VoRubric r : user.getRubrics()) {
				rubrics.add(r.createRubric());
			}
			return rubrics;
		} finally {
			
		}
	}

	public static List<String> getLocationCodesForRegistration() throws InvalidOperation {

		PersistenceManager pm = getPM();
		try {
			Extent<VoPostalAddress> postalAddresses = pm.getExtent(VoPostalAddress.class, true);
			if (!postalAddresses.iterator().hasNext()) {
				throw new InvalidOperation(VoError.GeneralError, "can't find any location codes");
			}

			List<String> locations = new ArrayList<String>();
			for (VoPostalAddress pa : postalAddresses) {
				pm.retrieve(pa);
				if (pa.getBuilding() != null)
					locations.add("" + pa.getAddressCode());
			}
			return locations;
		} finally {
			
		}
	}

	@Override
	public void deleteUserAddress(PostalAddress newAddress) throws InvalidOperation, TException {

		PersistenceManager pm = getPM();
		try {

			VoPostalAddress addr = new VoPostalAddress(newAddress, pm);
			VoUser currentUser = getCurrentUser(pm);

			List<String> addresses = currentUser.getAddresses();
			
			for(String addrName : addresses) {
				if( currentUser.getDeliveryAddress(addrName).equals(addr) ){
					currentUser.removeDeliveryAddress(addrName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to deleteUserAddress. " + e.getMessage());
		} finally {
			
		}

	}

	@Override
	public List<Country> getCounties() throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
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
			
		}
	}

	@Override
	public UserContacts getUserContacts() throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			VoUser u = getCurrentUser(pm);
			UserContacts uc = new UserContacts();
			if (u.getAddress() == null) {
				uc.setAddressStatus(UserStatus.UNCONFIRMED);
			} else {
				uc.setHomeAddress(u.getAddress().getPostalAddress(pm));
			}
			uc.setEmail(u.getEmail());
			uc.setMobilePhone(u.getMobilePhone());
			return uc;
		} finally {
			
		}
	}
	
	@Override
	public UserContacts getUserContactsExt(long userId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoUser u = pm.getObjectById(VoUser.class, userId);
			UserContacts uc = new UserContacts();
			if (u.getAddress() == null) {
				uc.setAddressStatus(UserStatus.UNCONFIRMED);
			} else {
				uc.setHomeAddress(u.getAddress().getPostalAddress(pm));
			}
			uc.setEmail(u.getEmail());
			uc.setMobilePhone(u.getMobilePhone());
			return uc;
		} catch (JDOObjectNotFoundException ioe){ 
			throw new InvalidOperation(VoError.IncorrectParametrs, "Access denied");
			
		} finally {
			
		}
	}

	@Override
	public UserInfo getUserInfo() throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoUser u = getCurrentUser(pm);
			UserInfo ui = u.getUserInfo();
			return ui;
		} finally {
			
		}
	}
	
	@Override
	public UserInfo getUserInfoExt(long userId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			return pm.getObjectById(VoUser.class, userId).getUserInfo();
		} catch( JDOObjectNotFoundException onfe){
			throw new InvalidOperation(VoError.NotAuthorized, "No access to user Info");
		} finally {
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<City> getCities(long countryId) throws InvalidOperation {
		PersistenceManager pm = getPM();
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
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Street> getStreets(long cityId) throws InvalidOperation {
		PersistenceManager pm = getPM();
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
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Building> getBuildings(long streetId) throws InvalidOperation {
		PersistenceManager pm = getPM();
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
			
		}
	}

	@Override
	public void updateUserAvatar(String url) throws InvalidOperation {
		PersistenceManager pm = getPM();
		VoUser voUser = getCurrentUser(pm);

		ImagesService imagesService = ImagesServiceFactory.getImagesService();

		try {
			VoFileAccessRecord vfar = pm.getObjectById(VoFileAccessRecord.class, StorageHelper.getFileId(url));
			if (vfar.getUserId() != voUser.getId())
				throw new InvalidOperation(VoError.IncorrectParametrs, "can't save avatar");

			Image origImage = ImagesServiceFactory.makeImageFromFilename(vfar.getGSFileName().toString());
			Transform resize = ImagesServiceFactory.makeResize(95, 95);
			String topicAvatarUrl = StorageHelper.saveImage(imagesService.applyTransform(resize, origImage).getImageData(), voUser.getId(), true, pm);

			resize = ImagesServiceFactory.makeResize(40, 40);
			String shortProfileAvatarUrl = StorageHelper
					.saveImage(imagesService.applyTransform(resize, origImage).getImageData(), voUser.getId(), true, pm);

			resize = ImagesServiceFactory.makeResize(200, 200);
			String profileAvatarUrl = StorageHelper.saveImage(imagesService.applyTransform(resize, origImage).getImageData(), voUser.getId(), true, pm);

			voUser.setAvatarTopic(topicAvatarUrl);
			voUser.setAvatarMessage(topicAvatarUrl);
			voUser.setAvatarProfileShort(shortProfileAvatarUrl);
			voUser.setAvatarProfile(profileAvatarUrl);
			pm.makePersistent(voUser);
		} catch (Exception e) {
			logger.warn("can't get VoFileAccessRecord for file " + url + " " + e.getMessage());
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "can't find image");
		} finally {
			
		}

	}

	@Override
	
	public FullAddressCatalogue getAddressCatalogue() throws InvalidOperation {
		/*FullAddressCatalogue fac = getObjectFromCache("fullAddressCatalogue");
		
		if( null==fac ){
			PersistenceManager pm = getPM();
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
				fac = new FullAddressCatalogue(cl, cil, sl, bl);
				putObjectToCache("fullAddressCatalogue", fac);
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
			} finally {
				
			}
		}
		return fac*/;
		return new FullAddressCatalogue( new HashSet<Country>(), new ArrayList<City>(), new ArrayList<Street>(),new ArrayList<Building>());
	}

	// TODO this method called only once in test. may be unused?
	@Override
	public boolean setUserAddress(PostalAddress newAddress) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoUser currentUser = getCurrentUser(pm);
			currentUser.setCurrentPostalAddress(new VoPostalAddress(newAddress, pm), pm);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Country createNewCountry(String name) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			// TODO check that there is no country with the same name
			VoCountry vc = new VoCountry(name,pm);
			Query q = pm.newQuery(VoCountry.class);
			q.setFilter("name == '" + name + "'");
			List<VoCountry> countries = (List<VoCountry>) q.execute();
			if (countries.size() > 0) {
				logger.info("City was NOT created. The same City was registered. Return an old one: " + countries.get(0));
				return countries.get(0).getCountry();
			} else {
				logger.info("Country '" + name + "'was created.");
				pm.makePersistent(vc);
				removeObjectFromCache("fullAddressCatalogue");
				return vc.getCountry();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCountry. " + e.getMessage());
		} finally {
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public City createNewCity(long countryId, String name) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoCountry vco = pm.getObjectById(VoCountry.class, countryId);
			removeObjectFromCache("fullAddressCatalogue");
			return new VoCity(vco, name, pm).getCity();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCity. " + e.getMessage());
		} finally {
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Street createNewStreet(long cityId, String name) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			// TODO check that there is no street with the same name
			VoCity vc = pm.getObjectById(VoCity.class, cityId);
			removeObjectFromCache("fullAddressCatalogue");
			return new VoStreet(vc, name,pm).getStreet();
		
		} catch (Throwable e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. " + e.getMessage());
		} finally {
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Building createNewBuilding(long streetId, String fullNo, String longitude, String lattitude) throws InvalidOperation {
		PersistenceManager pm = getPM();
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
				VoBuilding voBuilding = new VoBuilding(vs, fullNo, new BigDecimal(null == longitude || "".equals(longitude) ? "0" : longitude),
						new BigDecimal(null == lattitude || "".equals(lattitude) ? "0" : lattitude), pm);
				if (longitude == null || lattitude == null || longitude.isEmpty() || lattitude.isEmpty()) { // calculate
					// location
					try {
						Pair<String, String> position = VoGeocoder.getPosition(voBuilding,false,pm);
						voBuilding.setLocation(new BigDecimal(position.first), new BigDecimal(position.second));
					} catch (Exception e) {
						e.printStackTrace();
						throw new InvalidOperation(VoError.GeneralError, "FAiled to determine location of the building." + e.getMessage());
					}
				}
				removeObjectFromCache("fullAddressCatalogue");
				pm.makePersistent(voBuilding);
				return voBuilding.getBuilding();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. " + e.getMessage());
		} finally {
			
		}
	}

	@Override
	public boolean addUserAddress(PostalAddress newAddress) throws TException {
		PersistenceManager pm = getPM();
		try {
			VoUser currentUser = getCurrentUser(pm);
			pm.retrieve(currentUser);
			currentUser.addDeliveryAddress(new VoPostalAddress(newAddress, pm),null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			
		}

	}

	@Override
	public Set<PostalAddress> getUserAddresses() throws TException {
		PersistenceManager pm = getPM();
		try {
			VoUser currentUser = getCurrentUser(pm);
			Set<PostalAddress> pas = new HashSet<PostalAddress>();
			for (String pa : currentUser.getAddresses()) {
				pas.add(currentUser.getDeliveryAddress(pa).getPostalAddress(pm));
			}
			return pas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. " + e.getMessage());
		} finally {
			
		}
	}

	@Override
	public PostalAddress getUserHomeAddress() throws InvalidOperation {
		PersistenceManager pm = getPM();
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
}