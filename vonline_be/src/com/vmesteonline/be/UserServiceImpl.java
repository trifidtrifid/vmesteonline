package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;

public class UserServiceImpl extends ServiceImpl implements UserService.Iface {

	public UserServiceImpl() {
	}

	public UserServiceImpl(String sessId) {
		super(sessId);
	}

	public UserServiceImpl(HttpSession sess) {
		super(sess);
	}

	ShortUserInfo getShortUserInfo(long userId) {

		ShortUserInfo sui = new ShortUserInfo();
		
		
		
		return sui;
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation, TException {
		try {
			long userId = getCurrentUserId();
			PersistenceManager pm = PMF.getPm();
			try {
				VoUser user = pm.getObjectById(VoUser.class, userId);
				if (user == null) {
					logger.error("can't find user by id " + Long.toString(userId));
					throw new InvalidOperation(VoError.NotAuthorized, "can't find user by id");
				}
				logger.info("find user name " + user.getEmail());

				if (user.getGroups() == null) {
					logger.warn("user with id " + Long.toString(userId) + " has no any groups");
					throw new InvalidOperation(VoError.GeneralError, "can't find user bu id");
				}
				List<Group> groups = new ArrayList<Group>();
				for (VoUserGroup group : user.getGroups()) {
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
				return initializeTestLocations();
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

	private static List<String> initializeTestLocations() throws InvalidOperation {
		List<String> locations = new ArrayList<String>();
		VoStreet street = new VoStreet(new VoCity(new VoCountry("Россия"), "Санкт Петербург"), "Республиканская");
		PersistenceManager pm = PMF.getPm();

		try {
			pm.makePersistent(street);
			VoPostalAddress[] addresses;
			addresses = new VoPostalAddress[] {

			/*
			 * new VoPostalAddress(new VoBuilding(street, "32/3", 59.933146F,
			 * 30.423117F), (byte) 2, (byte) 1, (byte) 5, "", pm), new
			 * VoPostalAddress(new VoBuilding(street, "35", 59.932544F, 30.419684F),
			 * (byte) 1, (byte) 11, (byte) 35, "", pm), new VoPostalAddress(new
			 * VoBuilding(street, "6", 59.934177F, 30.404331F), (byte) 1, (byte) 2,
			 * (byte) 25, "", pm) };
			 */

			new VoPostalAddress(new VoBuilding(street, "32/3", 0F, 0F), (byte) 2, (byte) 1, (byte) 5, "", pm),
					new VoPostalAddress(new VoBuilding(street, "35", 0F, 0F), (byte) 1, (byte) 11, (byte) 35, "", pm),
					new VoPostalAddress(new VoBuilding(street, "6", 0F, 0F), (byte) 1, (byte) 2, (byte) 25, "", pm) };

			for (VoPostalAddress pa : addresses) {
				pm.makePersistent(pa);
				locations.add("" + pa.getAddressCode());
			}

			return locations;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to initTestLocations. " + e.getMessage());
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
	public FullAddressCatalogue getAddressCatalogue() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {

			Extent<VoCountry> vocs = pm.getExtent(VoCountry.class);
			Set<Country> cl = new HashSet<Country>();
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

	@Override
	public boolean setUserAddress(PostalAddress newAddress) throws TException {
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
	public Country createNewCountry(String name) throws InvalidOperation, TException {
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
	public City createNewCity(long countryId, String name) throws InvalidOperation, TException {
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
	public Street createNewStreet(long cityId, String name) throws InvalidOperation, TException {
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
	public Building createNewBuilding(long streetId, String fullNo, double longitude, double lattitude) throws InvalidOperation, TException {
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
				VoBuilding voBuilding = new VoBuilding(vs, fullNo, (float) longitude, (float) lattitude);
				if (0 == longitude || 0 == lattitude) { // calculate location
					try {
						Pair<Float, Float> position = VoGeocoder.getPosition(voBuilding);
						voBuilding.setLocation(position.first, position.second);
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
			currentUser.addPostalAddress(new VoPostalAddress(newAddress, pm), pm);
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
				pas.add(pa.getPostalAddress());
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
	public PostalAddress getUserHomeAddress() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			if (null == currentUser)
				throw new InvalidOperation(VoError.NotAuthorized, "No currnet user is set.");
			VoPostalAddress address = currentUser.getAddress();
			if (null == address) {
				return null;
			}
			return address.getPostalAddress();
		} finally {
			pm.close();
		}
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

}