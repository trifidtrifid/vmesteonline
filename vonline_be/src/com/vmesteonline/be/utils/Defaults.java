package com.vmesteonline.be.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.GroupType;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoInviteCode;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserTopic;
import com.vmesteonline.be.jdo2.dialog.VoDialog;
import com.vmesteonline.be.jdo2.dialog.VoDialogMessage;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;

@SuppressWarnings("unchecked")
public class Defaults {

	static {

		PersistenceManager pm = PMF.getPm();
		try {
			initializeGroups(pm);
		} finally {
			pm.close();
		}
	}

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

	public static String user4lastName = "Dfamily";
	public static String user4name = "Dname";
	public static String user4email = "d";
	public static String user4pass = "d";

	public static String user5lastName = "Efamily";
	public static String user5name = "Ename";
	public static String user5email = "e";
	public static String user5pass = "e";

	public static String[] unames = new String[] { user1name, user2name, user3name, user4name, user5name };
	public static String[] ulastnames = new String[] { user1lastName, user2lastName, user3lastName, user4lastName, user5lastName };
	public static String[] uEmails = new String[] { user1email, user2email, user3email, user4email, user5email };
	public static String[] uPasses = new String[] { user1pass, user2pass, user3pass, user4pass, user5pass };

	public static int radiusStarecase = 0;
	public static int radiusHome = 50;
	public static int radiusSmall = 350;
	/*
	 * public static int radiusMedium = 1500; public static int radiusLarge = 5000;
	 */
	public static String defaultAvatarTopicUrl = "/data/da.gif";
	public static String defaultAvatarMessageUrl = "/data/da.gif";
	public static String defaultAvatarProfileUrl = "/data/da.gif";
	public static String defaultAvatarShortProfileUrl = "/data/da.gif";

	public static boolean initDefaultData(boolean loadInviteCodes) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
			clearUsers(pm);
			clearRubrics(pm);
			clearGroups(pm);
			clearLocations(pm);
			clearFiles(pm);

			initializeRubrics(pm);
			initializeGroups(pm);
			List<String> locCodes = initializeTestLocations(loadInviteCodes);
			initializeUsers(locCodes);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			pm.close();
		}
		return true;

	}

	private static void clearFiles(PersistenceManager pm) {
		Extent<VoFileAccessRecord> ext = pm.getExtent(VoFileAccessRecord.class);
		if (null != ext)
			for (VoFileAccessRecord far : ext) {
				try {
					StorageHelper.deleteImage(far.getGSFileName());
					pm.deletePersistent(far);
				} catch (Exception rte) {
					// e.printStackTrace();
				}
			}

	}

	public static boolean initDefaultData() {
		return initDefaultData(false);
	}

	// ======================================================================================================================
	private static void deletePersistentAll(PersistenceManager pm, Class pc) {
		Extent ext = pm.getExtent(pc);
		if (null != ext)
			for (Object i : ext) {
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
		deletePersistentAll(pm, VoInviteCode.class);
		deletePersistentAll(pm, VoDialog.class);
		deletePersistentAll(pm, VoDialogMessage.class);
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
					new VoRubric("rubric2", "rubric second", "rubric about second", true), new VoRubric("rubric3", "rubric third", "rubric about third", true),
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
		q = pm.newQuery(VoGroup.class);
		q.setFilter("subscribedByDefault == true");
		List<VoGroup> defGroups = (List<VoGroup>) q.execute();
		if (defGroups.isEmpty()) {
			Iterator<Integer> impIterator = Arrays.asList(new Integer[] { 200, 500, 1000, 5000 }).iterator();
			defaultGroups = new ArrayList<VoGroup>();
			for (VoGroup dg : new VoGroup[] { new VoGroup("Мой подъезд", radiusStarecase, GroupType.STAIRCASE, true),
					new VoGroup("Мой дом", radiusHome, GroupType.BUILDING, true), new VoGroup("Соседние дома", radiusSmall, GroupType.NEIGHBORS, true),
			// new VoGroup("Мой район", radiusLarge, GroupType.DISTRICT, true)
			}) {
				dg.setImportantScore(impIterator.next());
				defaultGroups.add(dg);
				pm.makePersistent(dg);
			}
		} else
			defaultGroups = defGroups;
	}

	// ======================================================================================================================
	private static void initializeUsers(List<String> locCodes) throws InvalidOperation {
		AuthServiceImpl asi = new AuthServiceImpl();
		ArrayList<Long> uids = new ArrayList<Long>();
		int counter = 0;
		PersistenceManager pm = PMF.getPm();
		try {
			for (String uname : unames) {
				try {
					long uid = asi.registerNewUser(uname, ulastnames[counter], uPasses[counter], uEmails[counter], locCodes.get(counter++), 0);
					VoUser user = pm.getObjectById(VoUser.class, uid);
					user.setEmailConfirmed(true);

					if (counter == 1)
						for (Long ug : user.getGroups())
							// the first user would moderate all of groups
							user.setGroupModerator(ug, true);

					pm.makePersistent(user);
					uids.add(uid);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			pm.close();
		}
		if (uids.size() == 0)
			throw new RuntimeException("NO USERS are CREATED> Initialization totally fucked down");

	}

	// ======================================================================================================================
	// inviteCode 1 addr zan 32 k 3 kv 5 staircase 1 user a
	// inviteCode 2 addr zan 32 k 3 kv 50 staircase 2 user b
	// inviteCode 3 addr zan 32 k 3 kv 51 staircase 2 user c
	// inviteCode 1 addr zan 35 kv 35 staircase 1 user d
	// inviteCode 1 addr resp 6 kv s5 staircase 1 user e

	private static List<String> initializeTestLocations(boolean loadInviteCodes) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();

		try {
			VoStreet streetZ = new VoStreet(new VoCity(new VoCountry(COUNTRY, pm), CITY, pm), "Заневский", pm);
			VoStreet streetR = new VoStreet(new VoCity(new VoCountry(COUNTRY, pm), CITY, pm), "Республиканская", pm);

			pm.makePersistent(streetZ);
			pm.makePersistent(streetR);

			VoPostalAddress[] addresses;
			addresses = new VoPostalAddress[] {

					// адресов должно быть минимум три! кол-во юзеров
					// хардкодится выше
					new VoPostalAddress(new VoBuilding("195213", streetZ, "32к3", null, null, pm), (byte) 1, (byte) 1, (byte) 5, ""),
					new VoPostalAddress(new VoBuilding("195213", streetZ, "32к3", null, null, pm), (byte) 2, (byte) 1, (byte) 50, ""),
					new VoPostalAddress(new VoBuilding("195213", streetZ, "32к3", null, null, pm), (byte) 2, (byte) 1, (byte) 51, ""),
					new VoPostalAddress(new VoBuilding("195213", streetZ, "35", null, null, pm), (byte) 1, (byte) 11, (byte) 35, ""),
					new VoPostalAddress(new VoBuilding("195213", streetR, "6", null, null, pm), (byte) 1, (byte) 2, (byte) 25, "") };

			String invCodes[] = { "1", "2", "3", "4", "5" };

			for (int i = 0; i < addresses.length; i++) {

				pm.makePersistent(addresses[i]);
				VoInviteCode icode = new VoInviteCode(invCodes[i], addresses[i].getId());
				pm.makePersistent(icode);
			}

			if (loadInviteCodes)
				InviteCodeUploader.uploadCodes("/data/addresses_len_7_kudrovo.csv");

			return Arrays.asList(invCodes);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to initTestLocations. "
					+ (e instanceof InvalidOperation ? ((InvalidOperation) e).why : e.getMessage()));
		} finally {
			pm.close();
		}
	}
}
