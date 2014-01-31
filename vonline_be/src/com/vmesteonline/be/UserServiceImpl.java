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

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.GroupHelper;

public class UserServiceImpl extends ServiceImpl implements UserService.Iface {

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	public UserServiceImpl() {

	}

	public UserServiceImpl(String sessId) {
		super(sessId);
	}

	public UserServiceImpl(HttpSession sess) {
		super(sess);
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.getPm();
		VoUser user = pm.getObjectById(VoUser.class, userId);
		if (user == null) {
			logger.error("can't find user by id " + Long.toString(userId));
			throw new InvalidOperation(VoError.NotAuthorized, "can't find user by id");
		}
		logger.info("find user name " + user.getEmail());
		pm.retrieve(user);
		if (user.getGroups() == null) {
			logger.warn("user with id " + Long.toString(userId) + " has no any groups");
			throw new InvalidOperation(VoError.GeneralError, "can't find user bu id");
		}
		List<Group> groups = new ArrayList<Group>();
		for (VoUserGroup group : user.getGroups()) {
			groups.add(group.createGroup());
		}
		return groups;
	}

	@Override
	public List<Rubric> getUserRubrics() throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		VoUser user = PMF.getPm().getObjectById(VoUser.class, userId);
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
			Key streetId = street.getId();
			addresses = new VoPostalAddress[] {
					new VoPostalAddress(new VoBuilding(streetId, "32/3", 59.933146F, 30.423117F), (byte) 2, (byte) 1, (byte) 5, ""),
					new VoPostalAddress(new VoBuilding(streetId, "35", 59.932544F, 30.419684F), (byte) 1, (byte) 11, (byte) 35, ""),
					new VoPostalAddress(new VoBuilding(streetId, "6", 59.934177F, 30.404331F), (byte) 1, (byte) 2, (byte) 25, "") };

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
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getCounties. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public List<City> getCities(long countryId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			List<City> cl = new ArrayList<City>();
			Query q = pm.newQuery(VoCity.class);
			q.setFilter("counry == :key");
			List<VoCity> cs = (List<VoCity>)q.execute(countryId);
			for (VoCity c : cs) {
				cl.add(c.getCity());
			}
			q.closeAll();
			return cl;
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getCities. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public List<Street> getStreets(long cityId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			List<Street> cl = new ArrayList<Street>();
			Query q = pm.newQuery(VoStreet.class);
			q.setFilter("city == :key");
			List<VoStreet> cs = (List<VoStreet>)q.execute(cityId);
			for (VoStreet c : cs) {
				cl.add(c.getStreet());
			}
			q.closeAll();
			return cl;
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to load STreets. "+e.getMessage());
		} finally {
			pm.close();
		}
	}
 
	@Override
	public List<Building> getBuildings(long streetId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			List<Building> cl = new ArrayList<Building>();
			Query q = pm.newQuery(VoBuilding.class);
			q.setFilter("streetId == :1");
			List<VoBuilding> cs = (List<VoBuilding>)q.execute(streetId);
			for (VoBuilding c : cs) {
				cl.add(c.getBuilding());
			}
			q.closeAll();
			return cl;
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getBuildings. "+e.getMessage());
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
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean setUserAddress(PostalAddress newAddress) throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			currentUser.setCurrentPostalAddress(new VoPostalAddress(newAddress), pm);
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. "+e.getMessage());
		} finally {
			pm.close();
		}
		return false;
	}

	@Override
	public Country createNewCountry(String name) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			//TODO check that there is no country with the same name
			VoCountry vc = new VoCountry(name);
			pm.makePersistent(vc);
			return vc.getCountry();
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCountry. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public City createNewCity(long countryId, String name) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			//TODO check that there is no country with the same name
			VoCountry vco = pm.getObjectById(VoCountry.class, countryId);
			VoCity vc = new VoCity( vco, name);
			pm.makePersistent(vc);
			return vc.getCity();
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewCity. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public Street createNewStreet(long cityId, String name) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			//TODO check that there is no street with the same name
			VoCity vc = pm.getObjectById(VoCity.class, cityId);
			VoStreet vs = new VoStreet( vc, name);
			pm.makePersistent(vc);
			return vs.getStreet();
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public Building createNewBuilding(long streetId, String fullNo, double longitude, double lattitude) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			//TODO check that there is no building with the same name
			VoStreet vs = pm.getObjectById(VoStreet.class, streetId);
			VoBuilding vb = new VoBuilding( vs.getId(), fullNo, (float)longitude, (float)lattitude);
			pm.makePersistent(vb);
			return vb.getBuilding();
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to createNewStreet. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean addUserAddress(PostalAddress newAddress) throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			currentUser.addPostalAddress(new VoPostalAddress(newAddress), pm);
			return true;
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. "+e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Override
	public Set<PostalAddress> getUserAddress() throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);
			Set<PostalAddress> pas = new HashSet<PostalAddress>();
			for( VoPostalAddress pa : currentUser.getAddresses()){
				pas.add(pa.getPostalAddress());
			}
			return pas;
		} catch (Exception e){
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "FAiled to getAddressCatalogue. "+e.getMessage());
		} finally {
			pm.close();
		}
	}
}