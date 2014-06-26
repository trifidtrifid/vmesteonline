package com.vmesteonline.be;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.VoHelper;

public class UserServiceImplTest extends TestWorkAround {

	private static final String COMMENT = "Комент";
	private static final String BUILDING_NO = "31";
	private static final String STREET = "шоссе Революции";
	private static final String STREET1 = "Полюстровский пр";
	private static final String CITY = "Saint-Petersburg";
	private static final String COUNTRY = "Russia";

	private String userHomeLocation;
	private long userId;
	Country newCountry;
	City newCity;
	Street newStreet;
	Street newStreet1;
	Building newBuilding;

	@Before
	public void setUp() throws Exception {

		Assert.assertTrue(init());
		// register and login current user
		// Initialize USer Service

		List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();
		Assert.assertNotNull(userLocation);
		Assert.assertTrue(userLocation.size() > 0);
		userHomeLocation = userLocation.get(0);
		userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
		Assert.assertTrue(userId > 0);
		asi.login("eml", "pswd");

		newCountry = usi.createNewCountry(COUNTRY);
		newCity = usi.createNewCity(newCountry.getId(), CITY);
		newStreet = usi.createNewStreet(newCity.getId(), STREET);
		newStreet1 = usi.createNewStreet(newCity.getId(), STREET1);

		newBuilding = usi.createNewBuilding(newStreet.getId(), BUILDING_NO, "17", "53");

		Assert.assertEquals(newBuilding.getFullNo(), BUILDING_NO);
		Assert.assertEquals(newBuilding.getStreetId(), newStreet.getId());
		Assert.assertTrue(newBuilding.getId() > 0);

	}

	@Test
	public void testGetUserShortProfile() {

		PersistenceManager pm = PMF.getPm();
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);

			VoUser voUserA = asi.getCurrentUser(pm);
			ShortProfile sp = usi.getShortProfile();

			Assert.assertEquals(voUserA.getId(), sp.getId());
			Assert.assertEquals(Defaults.user1name, sp.getFirstName());
			Assert.assertEquals(Defaults.user1lastName, sp.getLastName());
			Assert.assertEquals("Республиканская, 32/3", sp.getAddress());
			Assert.assertEquals("/data/da.gif", sp.getAvatar());
			Assert.assertEquals("", sp.getBalance());
			Assert.assertEquals(0, sp.getRating());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Test
	public void testGetUserInfo() {

		PersistenceManager pm = PMF.getPm();
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);

			VoUser voUserA = asi.getCurrentUser(pm);
			UserInfo ui = usi.getUserInfo();

			Assert.assertEquals(voUserA.getId(), ui.getId());
			Assert.assertEquals(Defaults.user1name, ui.getFirstName());
			Assert.assertEquals(Defaults.user1lastName, ui.getLastName());
			// Assert.assertEquals(Defaults.user1lastName, ui.());

			// Assert.assertEquals("Республиканская, 32/3", ui.getAddress());
			// fail("should implement");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Test
	public void testUpdateUserInfo() {

		PersistenceManager pm = PMF.getPm();
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);

			UserInfo ui = new UserInfo();
			ui.birthday = "1984-07-18";
			ui.firstName = "FirstName";
			ui.lastName = "LastName";
			ui.relations = RelationsType.MARRIED;
			usi.updateUserInfo(ui);

			UserInfo uiBack = usi.getUserInfo();

			Assert.assertEquals(ui.birthday, uiBack.birthday);
			Assert.assertEquals(ui.firstName, uiBack.firstName);
			Assert.assertEquals(ui.lastName, uiBack.lastName);
			Assert.assertEquals(ui.relations, uiBack.relations);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Test
	public void testUpdateUserContacts() {

		PersistenceManager pm = PMF.getPm();
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);

			UserContacts uc = new UserContacts();
			uc.email = "z@z";
			uc.mobilePhone = "7921336";
			usi.updateUserContacts(uc);

			UserContacts ucBack = usi.getUserContacts();

			Assert.assertEquals(uc.email, ucBack.email);
			Assert.assertEquals(uc.mobilePhone, ucBack.mobilePhone);

			VoUser u = asi.getCurrentUser(pm);
			Assert.assertEquals(u.getEmail(), ucBack.email);
			Assert.assertEquals(u.isEmailConfirmed(), false);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			pm.close();
		}
	}

	@Test
	public void testGetUserAandBVoGroups() {

		try {
			PersistenceManager pmA = PMF.getPm();
			PersistenceManager pmB = PMF.getPm();

			try {

				asi.login(Defaults.user1email, Defaults.user1pass);
				VoUser uA = asi.getCurrentUser(pmA);
				List<VoUserGroup> voUserGroupsA = uA.getGroups();

				asi.login(Defaults.user3email, Defaults.user3pass);
				VoUser uB = asi.getCurrentUser(pmB);
				List<VoUserGroup> voUserGroupsB = uB.getGroups();

				Assert.assertEquals(5, voUserGroupsB.size());
				Assert.assertEquals(0, voUserGroupsB.get(0).getRadius());
				Assert.assertEquals(20, voUserGroupsB.get(1).getRadius());
				Assert.assertEquals(200, voUserGroupsB.get(2).getRadius());
				Assert.assertEquals(2000, voUserGroupsB.get(3).getRadius());
				Assert.assertEquals(5000, voUserGroupsB.get(4).getRadius());

				Assert.assertEquals(5, voUserGroupsA.size());
				Assert.assertEquals(0, voUserGroupsA.get(0).getRadius());
				Assert.assertEquals(20, voUserGroupsA.get(1).getRadius());
				Assert.assertEquals(200, voUserGroupsA.get(2).getRadius());
				Assert.assertEquals(2000, voUserGroupsA.get(3).getRadius());
				Assert.assertEquals(5000, voUserGroupsA.get(4).getRadius());

				/*
				 * Assert.assertEquals(voUserGroupsA.get(0).getLongitude(), new
				 * BigDecimal("59.9331461"));
				 * Assert.assertEquals(voUserGroupsB.get(0).getLongitude(), new
				 * BigDecimal("59.9331462"));
				 */System.out.print("max = " + VoHelper.getLongitudeMax(voUserGroupsA.get(0).getLongitude(), 200).toPlainString() + " origin = "
						+ voUserGroupsA.get(0).getLongitude() + "\n");
				System.out.print("lat max = " + VoHelper.getLatitudeMax(voUserGroupsA.get(0).getLatitude(), 200).toPlainString() + " origin = "
						+ voUserGroupsA.get(0).getLatitude() + "\n");

				System.out.print("delta = " + VoHelper.calculateRadius(voUserGroupsA.get(0), voUserGroupsB.get(0)));
			} finally {
				pmA.close();
				pmB.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserAandBGroups() {

		try {
			asi.login(Defaults.user1email, Defaults.user1pass);
			List<Group> userAgroups = usi.getUserGroups();
			asi.login(Defaults.user2email, Defaults.user2pass);
			List<Group> userBgroups = usi.getUserGroups();

			Assert.assertEquals(5, userAgroups.size());
			Assert.assertEquals(0, userAgroups.get(0).getRadius());
			Assert.assertEquals(20, userAgroups.get(1).getRadius());
			Assert.assertEquals(200, userAgroups.get(2).getRadius());
			Assert.assertEquals(2000, userAgroups.get(3).getRadius());
			Assert.assertEquals(5000, userAgroups.get(4).getRadius());

			Assert.assertEquals(5, userBgroups.size());
			Assert.assertEquals(0, userBgroups.get(0).getRadius());
			Assert.assertEquals(20, userBgroups.get(1).getRadius());
			Assert.assertEquals(200, userBgroups.get(2).getRadius());
			Assert.assertEquals(2000, userBgroups.get(3).getRadius());
			Assert.assertEquals(5000, userBgroups.get(4).getRadius());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
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
	public void testRemoveUserAddress() {
		try {
			byte floor = 2, flat = 3, staircase = 1;
			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase, floor, flat, COMMENT);
			Assert.assertTrue(usi.addUserAddress(newAddress));
			PostalAddress newAddress1 = new PostalAddress(newCountry, newCity, newStreet1, newBuilding, staircase, floor, flat, COMMENT);
			Assert.assertTrue(usi.addUserAddress(newAddress1));

			Set<PostalAddress> userAddresses = usi.getUserAddresses();
			Assert.assertEquals(2, userAddresses.size());

			usi.deleteUserAddress(newAddress1);

			userAddresses = usi.getUserAddresses();
			Assert.assertEquals(1, userAddresses.size());

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserAddress() {
		try {
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

	@Test
	public void testsetUserContacts() {
		try {
			byte floor = 2, flat = 3, staircase = 1;
			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase, floor, flat, COMMENT);

			usi.updateUserContacts(new UserContacts(UserStatus.CONFIRMED, null, "8(812)123-45-67", "a@b.com"));
			usi.updateUserContacts(new UserContacts(UserStatus.CONFIRMED, newAddress, null, null));
			usi.updateUserContacts(new UserContacts(UserStatus.CONFIRMED, newAddress, "+7 812 123-45-67", " a@b.com"));

			// Assert.assertTrue(userHomeAddress.equals(newAddress));
		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail("Exception " + e.getMessage());
		}
	}

	@Test
	public void testsetGetUserInfoExt() {
		try {

			UserInfo userInfoExt = usi.getUserInfoExt(userId);
			Assert.assertEquals(userInfoExt.firstName, "fn");
			Assert.assertEquals(userInfoExt.lastName, "ln");

			// Assert.assertTrue(userHomeAddress.equals(newAddress));
		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail("Exception " + e.getMessage());
		}
	}

	@Test
	public void testGetNeighbors() {
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);
			List<ShortUserInfo> user = usi.getNeighbors(getUserGroupId(Defaults.user1email, Defaults.radiusHome));
			Assert.assertEquals(3, user.size());
		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail("Exception " + e.getMessage());
		}
	}

}
