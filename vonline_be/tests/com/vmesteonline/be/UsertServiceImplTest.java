package com.vmesteonline.be;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;

public class UsertServiceImplTest {
	
	private static final String COUNTRY = "Russia";
	
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());
	private AuthServiceImpl asi;
	private String userHomeLocation;
	private long userId;
	private UserServiceImpl usi;

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		
		//register and login current user
		//Initialize USer Service
		String sessionId = "11111111111111111111111";
		asi = new AuthServiceImpl( sessionId );
		List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();
		Assert.assertNotNull( userLocation );
		Assert.assertTrue( userLocation.size() > 0 );
		userHomeLocation = userLocation.get(0);
		userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
		Assert.assertTrue( userId > 0 );
		asi.login("eml", "pswd");
		usi = new UserServiceImpl(sessionId);
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
		//asi.logout();
	}
	
	@Test
	public void testGetUserGroups() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserRubrics() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLocationCodesForRegistration() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAddressCatalogue() {
		fail("Not yet implemented");
	}


	@Test
	public void testCreateNewCountry() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			Assert.assertEquals(newCountry.getName(), COUNTRY);
			Assert.assertTrue(newCountry.getId() > 0 );
			
			List<Country> countries = usi.getCounties();
			Assert.assertTrue(countries.size() > 0);
			long foundId = 0L;
			for( Country c : countries ){
				if( COUNTRY.equals(c.getName())) 
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
			City newCity = usi.createNewCity(newCountry.getId(),"Saint-Petersburg");
			
			
			Assert.assertEquals(newCity.getName(), "Saint-Petersburg");
			Assert.assertEquals(newCity.getCountryId(),  newCountry.getId() );
			Assert.assertTrue(newCity.getId() > 0 );
			
			List<City> cities = usi.getCities(newCountry.getId());
			Assert.assertTrue(cities.size() > 0);
			City found = null;
			for( City c : cities ){
				if( "Saint-Petersburg".equals(c.getName())) 
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
			City newCity = usi.createNewCity(newCountry.getId(),"Saint-Petersburg");
			Street newStreet = usi.createNewStreet(newCity.getId(),"шоссе Революции");
			
			
			Assert.assertEquals(newStreet.getName(), "шоссе Революции");
			Assert.assertEquals(newStreet.getCityId(), newCity.getId() );
			Assert.assertTrue(newStreet.getId() > 0 );
			
			List<Street> streets = usi.getStreets(newCity.getId());
			Assert.assertTrue(streets.size() > 0);
			Street found = null;
			for( Street c : streets ){
				if( "шоссе Революции".equals(c.getName())) 
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
			City newCity = usi.createNewCity(newCountry.getId(),"Saint-Petersburg");
			Street newStreet = usi.createNewStreet(newCity.getId(),"шоссе Революции");
			Building newBuilding = usi.createNewBuilding(newStreet.getId(),"31",17D,53D);
			
			Assert.assertEquals(newBuilding.getFullNo(), "31");
			Assert.assertEquals(newBuilding.getStreetId(), newStreet.getId() );
			Assert.assertTrue(newBuilding.getId() > 0 );
			
			List<Building> buildings = usi.getBuildings(newStreet.getId());
			Assert.assertTrue(buildings.size() > 0);
			Building found = null;
			for( Building c : buildings ){
				if( "31".equals(c.getFullNo())) 
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
			City newCity = usi.createNewCity(newCountry.getId(),"Saint-Petersburg");
			Street newStreet = usi.createNewStreet(newCity.getId(),"шоссе Революции");
			Building newBuilding = usi.createNewBuilding(newStreet.getId(),"31",17D,53D);
			byte floor,flat,staircase;
			
			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, "Комент");
			boolean created = usi.addUserAddress(newAddress);
			Assert.assertTrue(created);
			Set<PostalAddress> userAddresses = usi.getUserAddress();
			PostalAddress found = null;
			int addressCount = userAddresses.size();
			for( PostalAddress pa : userAddresses ) {
				if(pa.getFloor() == floor && pa.getFlatNo() == flat && staircase == pa.getStaircase() )
					if( found != null ) 
						fail("Adsress dublicate detected!");
					else
						found = pa;
			}
			Assert.assertNotNull(found);
			Assert.assertEquals(found.getBuilding().getId(), newBuilding.getId());
			Assert.assertEquals(found.getStreet().getId(), newStreet.getId());
			Assert.assertEquals(found.getCity().getId(), newCity.getId());
			Assert.assertEquals(found.getCountry().getId(), newCountry.getId());
			Assert.assertEquals(found.getComment(), "Комент");
			
			created = usi.addUserAddress(newAddress);
			int nextAddressCount = usi.getUserAddress().size();
			Assert.assertEquals(addressCount, nextAddressCount);
			
			created = usi.addUserAddress(new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, "Комент"));
			int next2AddressCount = usi.getUserAddress().size();
			Assert.assertEquals(addressCount, next2AddressCount);
			
			created = usi.addUserAddress(new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 4, "Комент"));
			int next3AddressCount = usi.getUserAddress().size();
			Assert.assertEquals(addressCount+1, next3AddressCount);
			
		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUserAddress() {
		try {
			Country newCountry = usi.createNewCountry(COUNTRY);
			City newCity = usi.createNewCity(newCountry.getId(),"Saint-Petersburg");
			Street newStreet = usi.createNewStreet(newCity.getId(),"шоссе Революции");
			Building newBuilding = usi.createNewBuilding(newStreet.getId(),"31",17D,53D);
			byte floor,flat,staircase;
			
			PostalAddress newAddress = new PostalAddress(newCountry, newCity, newStreet, newBuilding, staircase = 1, floor = 2, flat = 3, "Комент");
			boolean created = usi.setUserAddress(newAddress);
			Assert.assertTrue( usi.getCurrentUser().getAddress().equals(newAddress));
		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
