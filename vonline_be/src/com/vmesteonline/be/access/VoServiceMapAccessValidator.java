package com.vmesteonline.be.access;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.ServiceImpl;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;

public class VoServiceMapAccessValidator extends VoTAccessValidator {
	private static final Logger logger = Logger.getLogger(VoServiceMapAccessValidator.class.getName());
	
	public VoServiceMapAccessValidator(ServiceImpl si) {
		super(si);
	}
	
	
	@Override
	public boolean checkAccessRights(String method) {
		try {
			return null == si ? false : 
				si.isPublicMethod( method ) ? true :
					checkAccessForUser( si.getCurrentUserId(), si.categoryId(), method );
			
		} catch (InvalidOperation e) {
			if(e.what != VoError.NotAuthorized )
				e.printStackTrace();
			else
				logger.fine("Not authorized. NO user ID. Could not use method "+method);
			return false;
		}
	}


	private boolean checkAccessForUser(long currentUserId, long categoryId, String method) {
		PersistenceManager pm = PMF.getPm();
		try {
			List<VoUserAccessBase> vuabl = (List<VoUserAccessBase>) pm.newQuery( VoUserAccessBase.class, "userId == "+currentUserId+" &&"
					+ " categoryId == " + categoryId + " && methodName == '" + method +"'" ).execute();
			
			return vuabl.size() >= 0;
		} catch( Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return false;
	}

}
