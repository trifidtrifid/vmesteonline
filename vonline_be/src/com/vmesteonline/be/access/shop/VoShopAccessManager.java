package com.vmesteonline.be.access.shop;

import com.vmesteonline.be.access.VoAccessManager;

public class VoShopAccessManager extends VoAccessManager {

	public static void createAccessCatalogManager( long catalogueManagerId, long shopId ){
		createAccessPermission(shopId,catalogueManagerId,VoShopAccessRoles.CATALOGUE);
		
	}
	public static void createAccessReportManager( long reportMAnagerId, long shopId ){
		createAccessPermission(shopId,reportMAnagerId,VoShopAccessRoles.REPORT);
		
	}
	public static void createAccessBillingManager( long billingManagerId, long shopId ){
		createAccessPermission(shopId,billingManagerId,VoShopAccessRoles.BILLING);
		
	}
	
	public static void createAccessForShopOwner( long owner, long shopId ){
		createAccessPermission(shopId,owner,VoShopAccessRoles.ADMIN);
	}

	private static void createAccessPermission(long shopId, long userId, long role) {
		/*PersistenceManager pm = PMF.getPm();
		try {
			VoShopAccess vsa = new VoShopAccess( shopId, userId);
			vsa.setAccessPermission(role, true);
			pm.makePersistent(vsa);
		} finally {
			pm.close();
		}*/
	}
}
