package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.List;

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
		long userId = getUserId();
		PersistenceManager pm = PMF.getPm();
		VoUser user = pm.getObjectById(VoUser.class, userId);
		if (user == null) {
			logger.error("can't find user by id " + Long.toString(userId));
			throw new InvalidOperation(Error.NotAuthorized, "can't find user by id");
		}
		logger.info("find user name " + user.getEmail());
		pm.retrieve(user);
		if (user.getGroups() == null) {
			logger.warn("user with id " + Long.toString(userId) + " has no any groups");
			throw new InvalidOperation(Error.GeneralError, "can't find user bu id");
		}
		List<Group> groups = new ArrayList<Group>();
		for (VoUserGroup group : user.getGroups()) {
			groups.add(group.createGroup());
		}
		return groups;
	}

	@Override
	public List<Rubric> getUserRubrics() throws InvalidOperation, TException {
		long userId = getUserId();
		VoUser user = PMF.getPm().getObjectById(VoUser.class, userId);
		if (user == null) {
			logger.error("can't find user by id " + Long.toString(userId));
			throw new InvalidOperation(Error.NotAuthorized, "can't find user bu id");
		}

		logger.info("find user name " + user.getEmail());

		if (user.getRubrics() == null) {
			logger.warn("user with id " + Long.toString(userId) + " has no any rubrics");
			throw new InvalidOperation(Error.GeneralError, "No Rubrics are initialized for user=" + userId);
		}

		List<Rubric> rubrics = new ArrayList<Rubric>();
		for (VoRubric r : user.getRubrics()) {
			rubrics.add(r.createRubric());
		}
		return rubrics;
	}

	public static List<String> getLocationCodesForRegistration() {

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

	private static List<String> initializeTestLocations() {
		List<String> locations = new ArrayList<String>();
		VoStreet street = new VoStreet(new VoCity(new VoCountry("Россия"), "Санкт Петербург"), "Республиканская");
		PMF.getPm().makePersistent(street);
		VoPostalAddress[] addresses;
		try {
			Key streetId = street.getId();
			addresses = new VoPostalAddress[] { new VoPostalAddress(new VoBuilding(streetId, "32/3", 59.933146F, 30.423117F), 2, 1, 5, ""),
					new VoPostalAddress(new VoBuilding(streetId, "35", 59.932544F, 30.419684F), 1, 11, 35, ""),
					new VoPostalAddress(new VoBuilding(streetId, "6", 59.934177F, 30.404331F), 1, 2, 25, "") };
		} catch (InvalidOperation e) {
			logger.error("Failed to create a list of location codes. Street create failed. " + e);
			return locations;
		}
		for (VoPostalAddress pa : addresses) {
			PMF.getPm().makePersistent(pa);
			locations.add("" + pa.getAddressCode());
		}
		return locations;
	}

	@Override
	public List<Country> getCounties() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<City> getCities(long countryId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Street> getStreets(long cityId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Building> getBuildings(long streetId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FullAddressCatalogue getAddressCatalogue() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setUserAddress(PostalAddress newAddress) throws TException {
		// TODO Auto-generated method stub
		return false;
	}
}