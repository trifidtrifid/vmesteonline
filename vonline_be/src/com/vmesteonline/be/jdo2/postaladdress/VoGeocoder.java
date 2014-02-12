package com.vmesteonline.be.jdo2.postaladdress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;

public class VoGeocoder {

	private static URL geocogingServerURL;
	private static String addressParamName;
	private static SAXParserFactory factory;
	private static SAXParser saxParser;
	
	static {
		geocogingServerURL = null;
		try {
			geocogingServerURL = new URL("http://geocode-maps.yandex.ru/1.x/");
			addressParamName = "results=1&format=xml&geocode";
			factory = SAXParserFactory.newInstance();
			saxParser = factory.newSAXParser();
		} catch (MalformedURLException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
			geocogingServerURL = null;
		} 
	}
	public VoGeocoder() {
		
	}
	
	public static Pair<Float,Float> getPosition( VoBuilding building) throws InvalidOperation{
		PersistenceManager pm = PMF.getPm();
		try{
			VoStreet street = pm.getObjectById(VoStreet.class, building.getStreet());
			
			String address = street.getCity().getCountry().getName() + "," + street.getCity().getName() + "," + street.getName() +
					","+building.getFullNo();
			address = URLEncoder.encode(address);
			try {
				URL url = new URL(geocogingServerURL + "?" + addressParamName + "="+address);
				YAMLGecodingHandler handler = new YAMLGecodingHandler();
				saxParser.parse(url.openStream(), handler);
				String longLatString = handler.longLatString();
				if( null!=longLatString){
					StringTokenizer st = new StringTokenizer(longLatString, " ");
					if( st.countTokens() > 1  ){
						float longitude = Float.parseFloat(st.nextToken());
						float lattitude = Float.parseFloat(st.nextToken());
						if( longitude > 0 && lattitude > 0  )
							return new Pair<Float, Float>(longitude, lattitude);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "Failed to get Location: "+e.getMessage());
			}
			throw new InvalidOperation(VoError.GeneralError, "Failed to get Location. THere is No data");
		} finally {
			pm.close();
		}
	}
	
	private static class YAMLGecodingHandler extends DefaultHandler {
		boolean doDeadThePos = false;
		private String longLatString = null;
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if(qName.equalsIgnoreCase("POS")) 
				doDeadThePos = true;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if( doDeadThePos) doDeadThePos = false;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if(doDeadThePos) 
				longLatString = new String(ch, start, length);
		}
		public String longLatString(){ 
			return longLatString;
		}
	}

}
