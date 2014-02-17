package com.vmesteonline.be.utils;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;

@SuppressWarnings("unchecked")
public class Defaults {

	public static List<VoGroup> defaultGroups;
	public static List<VoRubric> defaultRubrics;

	public static String user1lastName = "Afamily";
	public static String user1name = "Aname";
	public static String user1email = "a";
	public static String user1pass = "a";

	public static String user2lastName = "Bfamily";
	public static String user2name = "Bname";
	public static String user2email = "b";
	public static String user2pass = "b";

	public static String user3lastName = "Cfamily";
	public static String user3name = "Cname";
	public static String user3email = "c";
	public static String user3pass = "c";

	public static int radiusHome = 0;
	public static int radiusSmall = 200;
	public static int radiusMedium = 2000;
	public static int radiusLarge = 5000;

	public static boolean init() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
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

			defaultGroups = new ArrayList<VoGroup>();
			q = pm.newQuery(VoGroup.class);
			q.setFilter("subscribedByDefault == true");
			List<VoGroup> defGroups = (List<VoGroup>) q.execute();
			if (defGroups.isEmpty())

				for (VoGroup dg : new VoGroup[] { new VoGroup("Мой дом", 0, true), new VoGroup("Соседи", radiusSmall, true),
						new VoGroup("Пешая доступность", radiusMedium, true), new VoGroup("Быстро Доехать", radiusLarge, true) }) {
					defaultGroups.add(dg);
					pm.makePersistent(dg);
				}

			initializeTestLocations();

			MySQLJDBCConnector con = new MySQLJDBCConnector();
			con.execute("drop table if exists topic");

			AuthServiceImpl asi = new AuthServiceImpl();
			List<String> locCodes = UserServiceImpl.getLocationCodesForRegistration();
			asi.registerNewUser(user1name, user1lastName, user1pass, user1email, locCodes.get(0));
			asi.registerNewUser(user2name, user2lastName, user2pass, user2email, locCodes.get(1));
			asi.registerNewUser(user3name, user3lastName, user3pass, user3email, locCodes.get(2));

		} catch (InvalidOperation e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			pm.close();
		}
		return true;
	}

	private static List<String> initializeTestLocations() throws InvalidOperation {
		List<String> locations = new ArrayList<String>();
		VoStreet street = new VoStreet(new VoCity(new VoCountry("Россия"), "Санкт Петербург"), "Республиканская");
		PersistenceManager pm = PMF.getPm();

		try {
			pm.makePersistent(street);
			VoPostalAddress[] addresses;
			addresses = new VoPostalAddress[] {

					// адресов должно быть минимум три! кол-во юзеров хардкодится выше
					new VoPostalAddress(new VoBuilding(street, "32/3", 59.933146F, 30.423117F), (byte) 2, (byte) 1, (byte) 5, "", pm),
					new VoPostalAddress(new VoBuilding(street, "35", 59.932544F, 30.419684F), (byte) 1, (byte) 11, (byte) 35, "", pm),
					new VoPostalAddress(new VoBuilding(street, "6", 59.934177F, 30.404331F), (byte) 1, (byte) 2, (byte) 25, "", pm) };

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

}
