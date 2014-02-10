package com.vmesteonline.be;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.postaladdress.VoDistance;

public class VoDistanceHelper {

	public static void saveDistance(Long idA, Long idB, int dist) {

		VoDistance vd = new VoDistance(idA, idB, dist);
		PersistenceManager pm = PMF.getPm();
		try {
			pm.makePersistent(vd);
		} finally {
			pm.close();
		}
	}
}
