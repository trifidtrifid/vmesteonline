package com.vmesteonline.be.data;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.jdo2.VoUserObject;

public class VoDatastoreHelper {

	public static void exist(Class<?> className, long id) throws InvalidOperation {
		exist(className, id, PMF.getPm());
	}

	public static void exist(Class<?> className, long id, PersistenceManager pm) throws InvalidOperation {
		try {
			pm.getObjectById(className, id);
		} catch (JDOObjectNotFoundException e) {
			throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "can't find object " + className.getSimpleName() + " id: "
					+ Long.toString(id));
		}
	}

	public static <T> T getUserMsg(Class<T> className, long userId, long msgId, PersistenceManager pm) throws InvalidOperation {
		try {
			T t = pm.getObjectById(className, VoUserObject.<T> createKey(className, userId, msgId));
			return t;
		} catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

}
