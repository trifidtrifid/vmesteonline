package com.vmesteonline.be;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.access.VoUserAccessBaseRoles;
import com.vmesteonline.be.access.shop.VoShopAccessRoles;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoShop;

public class ShopServiceHelper {
	
	private static Logger logger = Logger.getLogger(ShopServiceHelper.class.getName());

//======================================================================================================================
	 static VoShop getCurrentShop(ServiceImpl si, PersistenceManager _pm) throws InvalidOperation {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;

		Long shopId = si.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}

		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			if (null != voShop) {
				return voShop;
			}
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is SET but SHOP not FOUND for this ID.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to SHOP by ID" + shopId + ". " + e);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	// ======================================================================================================================
	static VoOrder getCurrentOrder(ServiceImpl si, PersistenceManager _pm) throws InvalidOperation {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		Long orderId = si.getSessionAttribute(CurrentAttributeType.ORDER, pm);
		if (null == orderId || 0 == orderId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is not set in session context.");
		}

		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
				return voOrder;
			}
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is SET but ORDER not FOUND for this ID.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to ORDER by ID" + orderId + ". " + e);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}
	
	static Long getCurrentShopId(ServiceImpl si, PersistenceManager pm) throws InvalidOperation {
		Long shopId = si.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context. shopId=" + shopId);
		}
		return shopId;
	}

	static String getProcutsOfCategoryCacheKey(long categoryId, Long shopId) {
		return "VoProductsForCategory:" + shopId + ":" + categoryId;
	}
	
	// ======================================================================================================================

	public boolean isPublicMethod(String method) {
		Long roleRequired;
		if( null == (roleRequired =  VoShopAccessRoles.getRequiredRole(method))){
			logger.warning("Method '"+method+"' is called but there is no role registered for it! Access denied");
			return false;
		}
		return roleRequired == VoUserAccessBaseRoles.ANYBODY || roleRequired == VoShopAccessRoles.CUSTOMER;
	}
}
