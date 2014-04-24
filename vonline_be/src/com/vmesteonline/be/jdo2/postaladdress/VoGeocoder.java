package com.vmesteonline.be.jdo2.postaladdress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
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
			addressParamName = "results=1&format=xml&kind=house&geocode";
			factory = SAXParserFactory.newInstance();
			saxParser = factory.newSAXParser();
		} catch (MalformedURLException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
			geocogingServerURL = null;
		}
	}

	public VoGeocoder() {

	}

	public static Pair<String, String> getPosition(VoBuilding building) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoStreet street = pm.getObjectById(VoStreet.class, building.getStreet());

			String address = street.getCity().getCountry().getName() + "," + street.getCity().getName() + "," + street.getName() + ","
					+ building.getFullNo();
			address = URLEncoder.encode(address);
			try {
				URL url = new URL(geocogingServerURL + "?" + addressParamName + "=" + address);
				YAMLGecodingHandler handler = new YAMLGecodingHandler();
				saxParser.parse(url.openStream(), handler);
				String longLatString = handler.longLatString();
				
				if (null != longLatString) {
					StringTokenizer st = new StringTokenizer(longLatString, " ");
					if (st.countTokens() > 1) {
						String longitude = st.nextToken();
						String lattitude = st.nextToken();
						if (!longitude.isEmpty() && !lattitude.isEmpty())
							
						//update other address information
							if(handler.getStreetName() != null && !street.getName().equals( handler.getStreetName())) { //update street name
								 List<VoStreet> streets = (List<VoStreet>) pm.newQuery(VoStreet.class, "city == :key && name == '"+handler.getStreetName().trim()+"'").execute(street.getCity().getId());
								 
								 VoStreet rightStreet;
								 if( streets.size() > 0 ){
									 rightStreet = streets.get(0);
 								 } else { //create new street
									 rightStreet = new VoStreet( street.getCity(), handler.getStreetName());
									 pm.makePersistent(rightStreet);
								 }
								 building.setStreetId(rightStreet.getId());
								 //check if old street has a buildings
								 List<VoBuilding> buildings = (List<VoBuilding>) pm.newQuery(VoBuilding.class, "streetId == :key").execute(street.getId());
								 if( buildings.size() == 0 ){
									 pm.deletePersistent(street);
								 }
							} 
							if( null!=handler.getAddresText() && null==building.getAddress())
								building.setAddressString(handler.getAddresText());
							
							return new Pair<String, String>(longitude, lattitude);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "Failed to get Location: " + e.getMessage());
			}
			throw new InvalidOperation(VoError.GeneralError, "Failed to get Location. THere is No data");
		} finally {
			pm.close();
		}
	}

	private static class YAMLGecodingHandler extends DefaultHandler {

		private enum WhatDataToRead { UNKNOWN, POS, STREET, CITY, FULLNO, HOUSEIDX, KIND, ADDRESS, PRECISION };
		private WhatDataToRead whatNext = WhatDataToRead.UNKNOWN;
		
		private String longLatString = null;
		private String streetName = null;
		private String cityName = null;
		private String buildingNo = null;
		private boolean isKindHouse = false; //<kind>house</kind> 
    private String addresText = null; //<text>Россия, Москва, улица Новый Арбат, 24</text>
    private boolean isExact = false; //<PRECISION>exact</precision>
		

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("POS")){
				whatNext = WhatDataToRead.POS;
			} else if (qName.equalsIgnoreCase("kind")){
				whatNext = WhatDataToRead.KIND;
			}  else if (qName.equalsIgnoreCase("ThoroughfareName")){
				whatNext = WhatDataToRead.STREET;
			}  else if (qName.equalsIgnoreCase("LocalityName")){
				whatNext = WhatDataToRead.CITY;
			} else if (qName.equalsIgnoreCase("PremiseNumber")){
				whatNext = WhatDataToRead.FULLNO;
			} else if (qName.equalsIgnoreCase("HOUSELETTER")){ //NOT IMPLEMENTED
				whatNext = WhatDataToRead.HOUSEIDX;
			} else if (qName.equalsIgnoreCase("precision")){
				whatNext = WhatDataToRead.PRECISION;
			} else if (qName.equalsIgnoreCase("AddressLine")){
				whatNext = WhatDataToRead.ADDRESS;
			} else {
				whatNext = WhatDataToRead.UNKNOWN;
			} 
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			whatNext = WhatDataToRead.UNKNOWN;	
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			switch(whatNext){
				case POS:
					longLatString = new String(ch, start, length);
					break;
				case KIND:
					isKindHouse = new String(ch, start, length).equalsIgnoreCase("house");
					break;
				case STREET:
					streetName = new String(ch, start, length);
					break;
				case CITY:
					cityName = new String(ch, start, length);
					break;
				case ADDRESS:
					addresText = new String(ch, start, length);
					break;
				case PRECISION:
					isExact = new String(ch, start, length).equalsIgnoreCase("exact");
					break;
				case FULLNO:
					buildingNo = new String(ch, start, length);
					break;
				default:
					break;
			}
		}

		public String longLatString() {
			return longLatString;
		}

		public String getLongLatString() {
			return longLatString;
		}

		public String getStreetName() {
			return streetName;
		}

		public String getCityName() {
			return cityName;
		}

		public String getBuildingNo() {
			return buildingNo;
		}

		public boolean isKindHouse() {
			return isKindHouse;
		}

		public String getAddresText() {
			return addresText;
		}

		public boolean isExact() {
			return isExact;
		}
		
		
	}

}
