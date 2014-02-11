package com.vmesteonline.be.data;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;

public class VoDatastoreHelper {

	public static void exist(Class<?> className, long id) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			exist(className, id, pm);
		} finally {
			pm.close();
		}
	}

	public static void exist(Class<?> className, long id, PersistenceManager pm) throws InvalidOperation {
		try {
			pm.getObjectById(className, id);
		} catch (JDOObjectNotFoundException e) {
			throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "can't find object " + className.getSimpleName() + " id: "
					+ Long.toString(id));
		}
	}
}
