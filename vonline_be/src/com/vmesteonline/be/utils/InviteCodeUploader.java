package com.vmesteonline.be.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoInviteCode;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;

public class InviteCodeUploader {
	
	private static Logger logger = Logger.getLogger(InviteCodeUploader.class.getSimpleName());
	
	public static int uploadCodes( String fileName ) throws Exception {
		int uploaded=0;
		
		String url = 
				SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? 
						fileName : "http://localhost:8888"+fileName;
		PersistenceManager pm = PMF.getPm();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream fis = new URL(url).openStream();
			byte[] buf = new byte[1024];
			int len;
			while( 0 < (len = fis.read( buf )) )
				baos.write(buf, 0, len);
			baos.close();
			List<List<String>> csv = CSVHelper.parseCSV( baos.toByteArray(), ";", "|", ":" );
			int lineCounter = 0;
			for( List<String> row : csv ){
				
				if( lineCounter++ == 0 ) continue;
				
				try {
					//Код, индекс, страна, Город, Улица, дом, корпус, подъезд, квартира
					String code = row.get(0);
					String zip = row.get(1);
					String countryName = row.get(2);
					String cityName = row.get(3);
					String streetName = row.get(4);
					String corpus = row.get(6);
					String houseNo = row.get(5) + ( (null==corpus || 0==corpus.trim().length() || corpus.trim().equals("0")) ? "" : "/" +corpus); 
					byte stairCase = Byte.parseByte( row.get(7));
					byte floor = 0;;
					int flatNo = Integer.parseInt(row.get(8));
					
					VoCountry voCountry = new VoCountry( countryName, pm );
					pm.makePersistent(voCountry);
					VoCity voCity = new VoCity( voCountry, cityName, pm );
					pm.makePersistent(voCity);
					VoStreet voStreet = new VoStreet( voCity, streetName, pm);
					pm.makePersistent(voStreet);
					VoBuilding voBuilding = new VoBuilding( zip, voStreet,	houseNo, null, null, pm);
					pm.makePersistent(voBuilding);
					VoPostalAddress vpa = new VoPostalAddress(voBuilding, stairCase, floor, flatNo, "");
					pm.makePersistent(vpa);
					
					VoInviteCode ic = new VoInviteCode(code, vpa.getId());
					pm.makePersistent(ic);
					logger.fine("Created code: "+ic+" For address: "+vpa);
					uploaded++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.severe("Failed to import a line: "+lineCounter+" "+( e instanceof InvalidOperation ? ((InvalidOperation)e).getWhy() : e.getMessage()));
				}
			}
		} finally {
			pm.close();
		} 
		return uploaded;
	} 

}
