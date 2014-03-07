package com.vmesteonline.be.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.Country;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.ShopServiceImpl;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.shop.DataSet;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.ExchangeFieldType;
import com.vmesteonline.be.shop.ImExType;
import com.vmesteonline.be.shop.ImportElement;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.Shop;

@SuppressWarnings("unchecked")
public class Defaults {

	private static final String CITY = "Санкт Петербург";
	private static final String COUNTRY = "Россия";
	public static List<VoGroup> defaultGroups;
	public static List<VoRubric> defaultRubrics;

	public static String user1lastName = "Afamily";
	public static String user1name = "Aname";
	public static String user1email = "a";
	public static String user1pass = "a";

	public static String user2lastName = "Bfamily";
	public static String user2name = "Bname";
	public static String user2email = "b";
	public static String user2pass = "b";

	public static String user3lastName = "Cfamily";
	public static String user3name = "Cname";
	public static String user3email = "c";
	public static String user3pass = "c";

	public static int radiusHome = 0;
	public static int radiusSmall = 200;
	public static int radiusMedium = 2000;
	public static int radiusLarge = 5000;

	private static long userId;

	public static boolean initDefaultData() {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
			initializeRubrics(pm);

			initializeGroups(pm);

			List<String> locCodes = initializeTestLocations();

			MySQLJDBCConnector con = new MySQLJDBCConnector();
			con.execute("drop table if exists topic");

			try {
				initializeUsers(locCodes);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				initializeShop();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (InvalidOperation e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			pm.close();
		}
		return true;
	}

	// ======================================================================================================================
	private static void initializeShop() {

		try {
			ShopServiceImpl ssi = new ShopServiceImpl("123");
			AuthServiceImpl asi = new AuthServiceImpl("123");
			asi.login( user1email, user1pass );

			VoStreet street = new VoStreet(new VoCity(new VoCountry(COUNTRY), CITY), "г. Пушкин, Детскосельский бульвар");
			PersistenceManager pm = PMF.getPm();

			PostalAddress postalAddress;

			try {
				pm.makePersistent(street);
				postalAddress = new VoPostalAddress(new VoBuilding(street, "9А", 0F, 0F), (byte) 1, (byte) 1, (byte) 1,
						"Угол ул. Железнодоррожная и Детскосельского бульвара", pm).getPostalAddress();
			} catch (Exception e) {
				throw new InvalidOperation(VoError.GeneralError, "Failed to create address." + e);
			}

			List<Long> topicSet = new ArrayList<Long>();
			List<String> tags = new ArrayList<String>();
			Map<DeliveryType, Double> deliveryCosts = new TreeMap<DeliveryType, Double>();
			deliveryCosts.put(DeliveryType.SELF_PICKUP, 0D);
			deliveryCosts.put(DeliveryType.SHORT_RANGE, 150.0D);
			deliveryCosts.put(DeliveryType.LONG_RANGE, 200.0D);

			Map<PaymentType, Double> paymentTypes = new TreeMap<PaymentType, Double>();

			long shop = ssi.registerShop(new Shop(10, "Во!Молоко", "Магазин свежей молочной продукции Вологодского края", postalAddress,
					"http://vomoloko.ru/img/logo.jpg", userId, topicSet, tags, deliveryCosts, paymentTypes));

			ssi.getShop(shop); // to make it current

			DataSet ds = new DataSet();
			ds.date = (int) (System.currentTimeMillis() / 1000L);
			ds.name = "Producer, Categories, Products";

			loadProducers(ds);
			loadCategories(ds);
			loadProducts(ds);

			ssi.importData(ds);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ======================================================================================================================

	private static void loadProducts(DataSet ds) throws IOException {
		ImportElement importData;
		String imgURL;
		/*
		 * PRODUCT_ID=300, PRODUCT_NAME, PRODUCT_SHORT_DESCRIPTION, PRODUCT_WEIGHT,
		 * PRODUCT_IMAGEURL, PRODUCT_PRICE, PRODUCT_CATEGORY_IDS,
		 * PRODUCT_FULL_DESCRIPTION, PRODUCT_IMAGE_URLS, PRODUCT_PRICE_RETAIL,
		 * PRODUCT_PRICE_INET, PRODUCT_PRICE_VIP, PRODUCT_PRICE_SPECIAL,
		 * PRODUCT_OPIONSAVP, PRODUCT_TOPICS, PRODUCT_PRODUCER_ID
		 */
		List<ExchangeFieldType> productFieldsOrder = new ArrayList<ExchangeFieldType>();
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_ID);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_NAME);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_SHORT_DESCRIPTION);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_WEIGHT);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_IMAGEURL);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_CATEGORY_IDS);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_FULL_DESCRIPTION);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_IMAGE_URLS);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE_RETAIL);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE_INET);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE_VIP);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE_SPECIAL);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_OPIONSAVP);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_TOPICS);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRODUCER_ID);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_MIN_CLN_PACK);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_MIN_PROD_PACK);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PREPACK_REQ);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_KNOWN_NAMES);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_UNIT_NAME);

		importData = new ImportElement(ImExType.IMPORT_PRODUCTS, "product.csv", VoHelper.listToMap(productFieldsOrder));
		importData.setUrl( StorageHelper.saveImage("http://localhost:8888/data/products_1000_sheksna.csv",userId, false, null) );

		ds.addToData(importData);
	}

	// ======================================================================================================================

	private static void loadCategories(DataSet ds) throws IOException {
		// CATEGORY_ID = 200, CATEGORY_PARENT_ID, CATEGORY_NAME,
		// CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS
		List<ExchangeFieldType> fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.CATEGORY_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_PARENT_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_NAME);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_DESCRIPTION);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_LOGOURLS);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_TOPICS);

		ImportElement importData = new ImportElement(ImExType.IMPORT_CATEGORIES, "categories.csv", VoHelper.listToMap(fieldsOrder));
		importData.setUrl( StorageHelper.saveImage("http://localhost:8888/data/product_categories.csv",userId, false, null) );

		ds.addToData(importData);
	}

	// ======================================================================================================================

	private static void loadProducers(DataSet ds) throws IOException {
		List<ExchangeFieldType> fieldsOrder;
		ImportElement importData;
		String imgURL;
		fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.PRODUCER_ID);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_NAME);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_DESCRIPTION);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_LOGOURL);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_HOMEURL);

		importData = new ImportElement(ImExType.IMPORT_PRODUCERS, "producers.csv", VoHelper.listToMap(fieldsOrder));
		importData.setUrl( StorageHelper.saveImage("http://localhost:8888/data/producers.csv",userId, false, null) );

		ds.addToData(importData);
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
		defaultGroups = new ArrayList<VoGroup>();
		q = pm.newQuery(VoGroup.class);
		q.setFilter("subscribedByDefault == true");
		List<VoGroup> defGroups = (List<VoGroup>) q.execute();
		if (defGroups.isEmpty())

			for (VoGroup dg : new VoGroup[] { new VoGroup("Мой дом", 0, true), new VoGroup("Соседи", radiusSmall, true),
					new VoGroup("Пешая доступность", radiusMedium, true), new VoGroup("Быстро Доехать", radiusLarge, true) }) {
				defaultGroups.add(dg);
				pm.makePersistent(dg);
			}
	}

	// ======================================================================================================================
	private static void initializeUsers(List<String> locCodes) throws InvalidOperation {
		AuthServiceImpl asi = new AuthServiceImpl();
		userId = asi.registerNewUser(user1name, user1lastName, user1pass, user1email, locCodes.get(0));
		asi.registerNewUser(user2name, user2lastName, user2pass, user2email, locCodes.get(1));
		asi.registerNewUser(user3name, user3lastName, user3pass, user3email, locCodes.get(2));
	}

	// ======================================================================================================================
	private static List<String> initializeTestLocations() throws InvalidOperation {
		List<String> locations = new ArrayList<String>();
		VoStreet street = new VoStreet(new VoCity(new VoCountry(COUNTRY), CITY), "Республиканская");
		PersistenceManager pm = PMF.getPm();

		try {
			pm.makePersistent(street);
			VoPostalAddress[] addresses;
			addresses = new VoPostalAddress[] {

					// адресов должно быть минимум три! кол-во юзеров хардкодится выше
					new VoPostalAddress(new VoBuilding(street, "32/3", 59.933146F, 30.423117F), (byte) 2, (byte) 1, (byte) 5, "", pm),
					new VoPostalAddress(new VoBuilding(street, "35", 59.932544F, 30.419684F), (byte) 1, (byte) 11, (byte) 35, "", pm),
					new VoPostalAddress(new VoBuilding(street, "6", 59.934177F, 30.404331F), (byte) 1, (byte) 2, (byte) 25, "", pm) };

			for (VoPostalAddress pa : addresses) {
				pm.makePersistent(pa);
				locations.add("" + pa.getAddressCode());
			}

			return locations;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to initTestLocations. " + e.getMessage());
		} finally {
			pm.close();
		}
	}
}
