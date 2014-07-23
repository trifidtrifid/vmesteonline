package com.vmesteonline.be.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserTopic;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;

@SuppressWarnings("unchecked")
public class Defaults {

	private static final String CITY = "Санкт Петербург";
	private static final String COUNTRY = "Россия";
	public static List<VoGroup> defaultGroups;
	public static List<VoRubric> defaultRubrics;

	public static String user1lastName = "Afamily";
	public static String user1name = "Aname";
	public static String user1email = "a";
	public static String user1pass = "a";
	public static String zan32k3Lat = "59.933146";
	public static String zan32k3Long = "30.423117";

	public static String user2lastName = "Bfamily";
	public static String user2name = "Bname";
	public static String user2email = "b";
	public static String user2pass = "b";

	public static String user3lastName = "Cfamily";
	public static String user3name = "Cname";
	public static String user3email = "c";
	public static String user3pass = "c";

	public static int radiusStarecase = 0;
	public static int radiusHome = 20;
	public static int radiusSmall = 200;
	public static int radiusMedium = 2000;
	public static int radiusLarge = 5000;

	public static String defaultAvatarTopicUrl = "/data/da.gif";
	public static String defaultAvatarMessageUrl = "/data/da.gif";
	public static String defaultAvatarProfileUrl = "/data/da.gif";
	public static String defaultAvatarShortProfileUrl = "/data/da.gif";

	private static long userId = 0;

	public static boolean initDefaultData() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
			clearRubrics(pm);
			clearGroups(pm);
			clearLocations(pm);
			clearUsers(pm);

			initializeRubrics(pm);
			initializeGroups(pm);
			List<String> locCodes = initializeTestLocations();
			initializeUsers(locCodes);
			MySQLJDBCConnector con = new MySQLJDBCConnector();
			con.execute("drop table if exists topic");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			pm.close();
		}
		return true;
	}

	// ======================================================================================================================
	private static void deletePersistentAll(PersistenceManager pm, Class pc) {
		Extent ext = pm.getExtent(pc);
		if(null!=ext) for (Object i : ext) {
			try {
				pm.deletePersistent(i);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	// ======================================================================================================================

	private static void clearUsers(PersistenceManager pm) {
		deletePersistentAll(pm, VoUserTopic.class);
		deletePersistentAll(pm, VoUserGroup.class);
		deletePersistentAll(pm, VoTopic.class);
		deletePersistentAll(pm, VoMessage.class);
		deletePersistentAll(pm, VoUser.class);
	}

	// ======================================================================================================================

	private static void clearLocations(PersistenceManager pm) {
		deletePersistentAll(pm, VoPostalAddress.class);
		deletePersistentAll(pm, VoBuilding.class);
		deletePersistentAll(pm, VoStreet.class);
		deletePersistentAll(pm, VoCity.class);
		deletePersistentAll(pm, VoCountry.class);
	}

	// ======================================================================================================================

	private static void clearGroups(PersistenceManager pm) {
		deletePersistentAll(pm, VoGroup.class);
		deletePersistentAll(pm, VoUserGroup.class);
	}

	// ======================================================================================================================
	private static void clearRubrics(PersistenceManager pm) {
		deletePersistentAll(pm, VoRubric.class);
	}

	// ======================================================================================================================
	private static void initializeRubrics(PersistenceManager pm) {
		Query q = pm.newQuery(VoRubric.class);
		q.setFilter(" subscribedByDefault == true");
		List<VoRubric> defRubrics = (List<VoRubric>) q.execute();
		if (defRubrics.isEmpty()) {
			for (VoRubric dr : new VoRubric[] { new VoRubric("rubric1", "rubric first", "rubric about first", true),
					new VoRubric("rubric2", "rubric second", "rubric about second", true),
					new VoRubric("rubric3", "rubric third", "rubric about third", true),
					new VoRubric("rubric4", "rubric fourth", "rubric about fourth", true) }) {

				pm.makePersistent(dr);
				defaultRubrics.add(dr);
			}
		}
		q.closeAll();
	}

	// ======================================================================================================================
	private static void initializeGroups(PersistenceManager pm) {
		Query q;
		defaultGroups = new ArrayList<VoGroup>();
		q = pm.newQuery(VoGroup.class);
		q.setFilter("subscribedByDefault == true");
		List<VoGroup> defGroups = (List<VoGroup>) q.execute();
		if (defGroups.isEmpty())

			for (VoGroup dg : new VoGroup[] { new VoGroup("Мой дом", radiusHome, true), new VoGroup("Мои соседи", radiusMedium, true),
					new VoGroup("Мой район", radiusLarge, true) }) {
				defaultGroups.add(dg);
				pm.makePersistent(dg);
			}
	}

	// ======================================================================================================================
	private static void initializeUsers(List<String> locCodes) throws InvalidOperation {
		AuthServiceImpl asi = new AuthServiceImpl();
		long user2Id, user3Id;
		userId = user2Id = user3Id = 0;
		try {
			userId = asi.registerNewUser(user1name, user1lastName, user1pass, user1email, locCodes.get(0));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		try {
			user2Id = asi.registerNewUser(user2name, user2lastName, user2pass, user2email, locCodes.get(1));
		} catch (Exception e1) {

		}
		try {
			user3Id = asi.registerNewUser(user3name, user3lastName, user3pass, user3email, locCodes.get(2));
		} catch (Exception e) {

		}
		userId = userId == 0 ? user2Id == 0 ? user3Id : user2Id : userId;
	}

	// ======================================================================================================================
	private static List<String> initializeTestLocations() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();

		try {
			List<String> locations = new ArrayList<String>();
			VoStreet street = new VoStreet(new VoCity(new VoCountry(COUNTRY, pm), CITY, pm), "Республиканская", pm);

			pm.makePersistent(street);
			VoPostalAddress[] addresses;
			addresses = new VoPostalAddress[] {

					// адресов должно быть минимум три! кол-во юзеров
					// хардкодится выше
					new VoPostalAddress(new VoBuilding("195213", street, "32/3", new BigDecimal(zan32k3Long), new BigDecimal(zan32k3Lat), pm), (byte) 1,
							(byte) 1, (byte) 5, ""),
					new VoPostalAddress(new VoBuilding("195213", street, "32/3", new BigDecimal(zan32k3Long), new BigDecimal(zan32k3Lat), pm), (byte) 2,
							(byte) 1, (byte) 50, ""),
					new VoPostalAddress(new VoBuilding("195213", street, "35", new BigDecimal("30.419684"), new BigDecimal("59.932544"), pm), (byte) 1,
							(byte) 11, (byte) 35, ""),
					new VoPostalAddress(new VoBuilding("195213", street, "6", new BigDecimal("30.404331"), new BigDecimal("59.934177"), pm), (byte) 1,
							(byte) 2, (byte) 25, "") };

			for (VoPostalAddress pa : addresses) {
				pm.makePersistent(pa);
				locations.add("" + pa.getAddressCode());
			}
			
			InviteCodeUploader.uploadCodes("/data/addresses_len_7_kudrovo.csv");
			
			return locations;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to initTestLocations. " + e.getMessage());
		} finally {
			pm.close();
		}
	}
}
