package com.vmesteonline.be;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.Defaults;

public class UserServiceImplTest {

	private static final String SESSION_ID = "11111111111111111111111";
	private static final String COMMENT = "Комент";
	private static final String BUILDING_NO = "31";
	private static final String STREET = "шоссе Революции";
	private static final String CITY = "Saint-Petersburg";
	private static final String COUNTRY = "Russia";

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private AuthServiceImpl asi;
	private String userHomeLocation;
	private long userId;
	private UserServiceImpl usi;

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Defaults.init();
		// register and login current user
		// Initialize USer Service
		String sessionId = SESSION_ID;
		asi = new AuthServiceImpl(sessionId);
		List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();
		Assert.assertNotNull(userLocation);
		Assert.assertTrue(userLocation.size() > 0);
		userHomeLocation = userLocation.get(0);
		userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
		Assert.assertTrue(userId > 0);
		asi.login("eml", "pswd");
		usi = new UserServiceImpl(sessionId);
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
		// asi.logout();
	}

	@Test
	public void testMinimumtDuff() {
		VoUserGroup g = new VoUserGroup("test", 1, 30.423618F, 59.93326F);
		Assert.assertEquals(g.getLongitudeDelta(), 0.000001, 0.000001F);
	}

	@Test
	public void testGetUserGroups() {
		try {
			List<Group> userGroups = usi.getUserGroups();
			boolean homeFound = false;
			for (Group ug : userGroups) {
				if (0 == ug.getRadius()) {
					Assert.assertFalse(homeFound);
					homeFound = true;
					ug.getName();
				}
			}
			Assert.assertTrue(userGroups.size() > 0);
		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserRubrics() {
		try {
			List<Rubric> userRubrics = usi.getUserRubrics();
			assertTrue(userRubrics.size() > 0);
		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAddressCatalogue() {
		try {
			FullAddressCatalogue addressCatalogue = usi.getAddressCatalogue();
			Assert.assertTrue(addressCatalogue.countries.size() > 0);
			Assert.assertTrue(addressCatalogue.cities.size() > 0);
			Assert.assertTrue(addressCatalogue.streets.size() > 0);
			Assert.assertTrue(addressCatalogue.buildings.size() > 0);
		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateNewCountry() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			Assert.assertEquals(newCountry.getName(), COUNTRY);
			Assert.assertTrue(newCountry.getId() > 0);

			List<Country> countries = usi.getCounties();
			Assert.assertTrue(countries.size() > 0);
			long foundId = 0L;
			for (Country c : countries) {
				if (COUNTRY.equals(c.getName()))
					foundId = c.getId();
			}
			Assert.assertEquals(foundId, newCountry.getId());

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateNewCity() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(), CITY);

			Assert.assertEquals(newCity.getName(), CITY);
			Assert.assertEquals(newCity.getCountryId(), newCountry.getId());
			Assert.assertTrue(newCity.getId() > 0);

			List<City> cities = usi.getCities(newCountry.getId());
			Assert.assertTrue(cities.size() > 0);
			City found = null;
			for (City c : cities) {
				if (CITY.equals(c.getName()))
					found = c;
			}
			Assert.assertEquals(found.getId(), newCity.getId());
			Assert.assertEquals(found.getCountryId(), newCountry.getId());

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateNewStreet() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(), CITY);
			Street newStreet = usi.createNewStreet(newCity.getId(), STREET);

			Assert.assertEquals(newStreet.getName(), STREET);
			Assert.assertEquals(newStreet.getCityId(), newCity.getId());
			Assert.assertTrue(newStreet.getId() > 0);

			List<Street> streets = usi.getStreets(newCity.getId());
			Assert.assertTrue(streets.size() > 0);
			Street found = null;
			for (Street c : streets) {
				if (STREET.equals(c.getName()))
					found = c;
			}
			Assert.assertNotNull(found);
			Assert.assertEquals(found.getId(), newStreet.getId());
			Assert.assertEquals(found.getCityId(), newCity.getId());

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateNewBuilding() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(), CITY);
			Street newStreet = usi.createNewStreet(newCity.getId(), STREET);
			Building newBuilding = usi.createNewBuilding(newStreet.getId(), BUILDING_NO, 0D, 0D);

			Assert.assertEquals(newBuilding.getFullNo(), BUILDING_NO);
			Assert.assertEquals(newBuilding.getStreetId(), newStreet.getId());
			Assert.assertTrue(newBuilding.getId() > 0);

			List<Building> buildings = usi.getBuildings(newStreet.getId());
			Assert.assertTrue(buildings.size() > 0);
			Building found = null;
			for (Building c : buildings) {
				if (BUILDING_NO.equals(c.getFullNo()))
					found = c;
			}
			Assert.assertNotNull(found);
			Assert.assertEquals(found.getId(), newBuilding.getId());
			Assert.assertEquals(found.getStreetId(), newStreet.getId());

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAddUserAddress() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(), CITY);
			Street newStreet = usi.createNewStreet(newCity.getId(), STREET);
			Building newBuilding = usi.createNewBuilding(newStreet.getId(), BUILDING_NO, 17D, 53D);
			byte floor, flat, staircase;

			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, COMMENT);
			boolean created = usi.addUserAddress(newAddress);
			Assert.assertTrue(created);
			Set<PostalAddress> userAddresses = usi.getUserAddresses();
			PostalAddress found = null;
			int addressCount = userAddresses.size();
			for (PostalAddress pa : userAddresses) {
				if (pa.getFloor() == floor && pa.getFlatNo() == flat && staircase == pa.getStaircase())
					if (found != null)
						fail("Adsress dublicate detected!");
					else
						found = pa;
			}
			Assert.assertNotNull(found);
			Assert.assertEquals(found.getBuilding().getId(), newBuilding.getId());
			Assert.assertEquals(found.getStreet().getId(), newStreet.getId());
			Assert.assertEquals(found.getCity().getId(), newCity.getId());
			Assert.assertEquals(found.getCountry().getId(), newCountry.getId());
			Assert.assertEquals(found.getComment(), COMMENT);

			created = usi.addUserAddress(newAddress);
			int nextAddressCount = usi.getUserAddresses().size();
			Assert.assertEquals(addressCount, nextAddressCount);

			created = usi.addUserAddress(new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, COMMENT));
			int next2AddressCount = usi.getUserAddresses().size();
			Assert.assertEquals(addressCount, next2AddressCount);

			created = usi.addUserAddress(new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 4, COMMENT));
			int next3AddressCount = usi.getUserAddresses().size();
			Assert.assertEquals(addressCount + 1, next3AddressCount);

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserAddress() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(), CITY);
			Street newStreet = usi.createNewStreet(newCity.getId(), STREET);
			Building newBuilding = usi.createNewBuilding(newStreet.getId(), BUILDING_NO, 17D, 53D);
			byte floor;
			byte flat;
			byte staircase;
			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, COMMENT);

			boolean created = usi.setUserAddress(newAddress);
			PostalAddress userHomeAddress = usi.getUserHomeAddress();
			Assert.assertTrue(userHomeAddress.equals(newAddress));
		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail("Exception " + e.getMessage());
		}
	}
}
