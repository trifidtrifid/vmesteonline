package com.vmesteonline.be;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.Extent;
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
import com.vmesteonline.be.ServiceImpl.ServiceCategoryID;
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

		/*
		 * 4: i32 rating 5: string avatar, 6: string birthday, 7: string relations,
		 */

		PersistenceManager pm = PMF.getPm();
		VoUser user = getCurrentUser(pm);
		user.setName(userInfo.firstName);
		user.setLastName(userInfo.lastName);
		user.setLastName(userInfo.lastName);
		pm.makePersistent(user);
		// user.(userInfo.lastName);

	}

	@Override
	public void updateUserContacts(UserContacts contacts) throws InvalidOperation {
		// TODO Auto-generated method stub

	}

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
				sp.setAddress(pa.getBuilding().getAddressString());
			}
			return sp;
		} finally {
			pm.close();
		}
	}

	public static ShortUserInfo getShortUserInfo(long userId) {

		PersistenceManager pm = PMF.getPm();
		try {
			VoUser voUser = pm.getObjectById(VoUser.class, userId);
			return voUser.getShortUserInfo();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("request short user info for absent user " + userId);
		} finally {
			pm.close();
		}
		return null;
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation {
		try {

			long userId = getCurrentUserId();
			PersistenceManager pm = PMF.getPm();
			try {
				VoUser user = pm.getObjectById(VoUser.class, userId);

				if (user == null) {
					logger.error("can't find user by id " + Long.toString(userId));
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
				locations.add("" + pa.getAddressCode());
			}
			return locations;
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
	public UserInfo getUserInfo() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser u = getCurrentUser(pm);
			UserInfo ui = new UserInfo(u.getId(), u.getName(), u.getLastName(), 0, "avatar path", "birthday", "relations");
			return ui;
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<City> getCities(long countryId) throws InvalidOperation, TException {
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
	public List<Street> getStreets(long cityId) throws InvalidOperation, TException {
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
	public List<Building> getBuildings(long streetId) throws InvalidOperation, TException {
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
		VoUser voUser = getCurrentUser(pm);

		ImagesService imagesService = ImagesServiceFactory.getImagesService();

		try {
			VoFileAccessRecord vfar = pm.getObjectById(VoFileAccessRecord.class, StorageHelper.getFileId(url));
			if (vfar.getUserId() != voUser.getId())
				throw new InvalidOperation(VoError.IncorrectParametrs, "can't save avatar");

			Image origImage = ImagesServiceFactory.makeImageFromFilename(vfar.getFileName().toString());
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
			pm.close();
		}

	}

	@Override
	public FullAddressCatalogue getAddressCatalogue() throws InvalidOperation, TException {
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
			VoCountry vc = new VoCountry(name);
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
			// TODO check that there is no country with the same name
			VoCountry vco = pm.getObjectById(VoCountry.class, countryId);
			Query q = pm.newQuery(VoCity.class);
			q.setFilter("country == " + countryId);
			q.setFilter("name == '" + name + "'");
			List<VoCity> cities = (List<VoCity>) q.execute();
			if (cities.size() > 0) {
				logger.info("City was NOT created. The same City was registered. Return an old one: " + cities.get(0));
				return cities.get(0).getCity();
			} else {
				logger.info("City '" + name + "'was created.");
				VoCity vc = new VoCity(vco, name);
				pm.makePersistent(vco);
				return vc.getCity();
			}
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
			VoCity vc = null;
			vc = pm.getObjectById(VoCity.class, cityId);
			Query q = pm.newQuery(VoStreet.class);
			q.setFilter("city == " + cityId);
			q.setFilter("name == '" + name + "'");
			List<VoStreet> streets = (List<VoStreet>) q.execute();
			if (streets.size() > 0) {
				logger.info("Street was NOT created. The same sreet was registered. Return an old one: " + streets.get(0));
				return streets.get(0).getStreet();
			} else {
				logger.info("Street '" + name + "'was created.");
				VoStreet vs = new VoStreet(vc, name);
				pm.makePersistent(vs);
				return vs.getStreet();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Building createNewBuilding(long streetId, String fullNo, String longitude, String lattitude) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			// TODO check that there is no building with the same name
			VoStreet vs = pm.getObjectById(VoStreet.class, streetId);
			Query q = pm.newQuery(VoBuilding.class);
			q.setFilter("streetId == " + streetId);
			q.setFilter("fullNo == '" + fullNo + "'");
			List<VoBuilding> buildings = (List<VoBuilding>) q.execute();
			if (buildings.size() > 0) {
				logger.info("VoBuilding was NOT created. The same VoBuilding was registered. Return an old one: " + buildings.get(0));
				return buildings.get(0).getBuilding();
			} else {
				logger.info("VoBuilding '" + fullNo + "'was created.");
				VoBuilding voBuilding = new VoBuilding(vs, fullNo, new BigDecimal(null == longitude || "".equals(longitude) ? "0" : longitude),
						new BigDecimal(null == lattitude || "".equals(lattitude) ? "0" : lattitude));
				if (longitude.isEmpty() || lattitude.isEmpty()) { // calculate
					// location
					try {
						Pair<String, String> position = VoGeocoder.getPosition(voBuilding);
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
			currentUser.addPostalAddress(new VoPostalAddress(newAddress, pm));
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
			for (VoPostalAddress pa : currentUser.getAddresses()) {
				pas.add(pa.getPostalAddress(pm));
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
}