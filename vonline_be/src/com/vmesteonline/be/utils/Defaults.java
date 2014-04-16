package com.vmesteonline.be.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.FullAddressCatalogue;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.ShopServiceImpl;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.GeoLocation;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserTopic;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoOrderLine;
import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.jdo2.shop.VoShop;
import com.vmesteonline.be.shop.DataSet;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.ExchangeFieldType;
import com.vmesteonline.be.shop.ImExType;
import com.vmesteonline.be.shop.ImportElement;
import com.vmesteonline.be.shop.PaymentType;
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
	public static String zan32k3Lat = "59.933146";
	public static String zan32k3Long = "30.423117";

	public static String user2lastName = "Bfamily";
	public static String user2name = "Bname";
	public static String user2email = "b";
	public static String user2pass = "b";

	public static String user3lastName = "Cfamily";
	public static String user3name = "Cname";
	public static String user3email = "c";
	public static String user3pass = "c";

	public static int radiusStarecase = 0;
	public static int radiusHome = 20;
	public static int radiusSmall = 200;
	public static int radiusMedium = 2000;
	public static int radiusLarge = 5000;

	public static String defaultAvatarTopicUrl = "/data/da.gif";
	public static String defaultAvatarMessageUrl = "/data/da.gif";
	public static String defaultAvatarProfileUrl = "/data/da.gif";
	public static String defaultAvatarShortProfileUrl = "/data/da.gif";

	private static long userId = 0;

	public static boolean initDefaultData() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		defaultRubrics = new ArrayList<VoRubric>();
		try {
			clearRubrics(pm);
			clearGroups(pm);		
			clearLocations(pm);
			clearUsers(pm);
			
			initializeRubrics(pm);
			initializeGroups(pm);
			List<String> locCodes = initializeTestLocations();
			initializeUsers(locCodes);
			MySQLJDBCConnector con = new MySQLJDBCConnector();
			con.execute("drop table if exists topic");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			pm.close();
		}
		return true;
	}

//======================================================================================================================
	private static void deletePersistentAll(PersistenceManager pm, Class pc){
		Extent ext = pm.getExtent(pc);
		for (Object i : ext) {
			try {
				pm.deletePersistent(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//======================================================================================================================

	private static void clearUsers(PersistenceManager pm) {
			deletePersistentAll(pm, VoUserTopic.class);
			deletePersistentAll(pm, VoUserGroup.class);
			deletePersistentAll(pm, VoTopic.class);
			deletePersistentAll(pm, VoMessage.class);
			deletePersistentAll(pm, VoUser.class);
	}
//======================================================================================================================

	private static void clearLocations(PersistenceManager pm) {
			deletePersistentAll(pm,VoPostalAddress.class);
			deletePersistentAll(pm,VoBuilding.class);
			deletePersistentAll(pm,VoStreet.class);
			deletePersistentAll(pm,VoCity.class);
			deletePersistentAll(pm,VoCountry.class);
	}
	//======================================================================================================================

	private static void clearGroups(PersistenceManager pm) {
		deletePersistentAll(pm,VoGroup.class);
		deletePersistentAll(pm,VoUserGroup.class);	
	}
//======================================================================================================================
	private static void clearRubrics(PersistenceManager pm) {
		deletePersistentAll(pm, VoRubric.class);
	}

	// ======================================================================================================================
	public static void initializeShop() {

		try {
			ShopServiceImpl ssi = new ShopServiceImpl("123");
			AuthServiceImpl asi = new AuthServiceImpl("123");
			asi.login(user1email, user1pass);

			PersistenceManager pm = PMF.getPm();
			PostalAddress postalAddress;

			try {
	
				VoCity vocity = pm.getExtent(VoCity.class).iterator().next();
				VoStreet street = new VoStreet( vocity, "г. Пушкин, Детскосельский бульвар");
				VoBuilding building = new VoBuilding(street, "9А", new BigDecimal("0"), new BigDecimal("0"),pm);
				VoPostalAddress voPostalAddress = new VoPostalAddress(building, (byte) 1, (byte) 1, (byte) 1,
						"Угол ул. Железнодоррожная и Детскосельского бульвара");
				pm.makePersistent(street);
				pm.makePersistent(building);
				pm.makePersistent(voPostalAddress);
				
				postalAddress = voPostalAddress.getPostalAddress(pm);

				VoHelper.forgetAllPersistent(VoShop.class, pm);
				VoHelper.forgetAllPersistent(VoProducer.class, pm);
				VoHelper.forgetAllPersistent(VoProductCategory.class, pm);
				VoHelper.forgetAllPersistent(VoProduct.class, pm);
				VoHelper.forgetAllPersistent(VoOrderLine.class, pm);
				VoHelper.forgetAllPersistent(VoOrder.class, pm);

			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "Failed to create address." + e);
			} finally {
				pm.close();
			}

			List<Long> topicSet = new ArrayList<Long>();
			List<String> tags = new ArrayList<String>();
			Map<DeliveryType, Double> deliveryCosts = new TreeMap<DeliveryType, Double>();
			deliveryCosts.put(DeliveryType.SELF_PICKUP, 0D);
			deliveryCosts.put(DeliveryType.SHORT_RANGE, 100.0D);
			deliveryCosts.put(DeliveryType.LONG_RANGE, 150.0D);

			Map<PaymentType, Double> paymentTypes = new TreeMap<PaymentType, Double>();

			long shop = ssi.registerShop(new Shop(10, "Во!Молоко", "Магазин свежей молочной продукции Вологодского края", postalAddress,
					"http://vomoloko.ru/img/logo.jpg", userId, topicSet, tags, deliveryCosts, paymentTypes));

			ssi.getShop(shop); // to make it current
			// set dates
			int now = (int) (System.currentTimeMillis() / 1000L);
			Map<Integer, DateType> dates = new HashMap<Integer, DateType>();
			for (int dt = now; dt < now + 86400 * 365; dt += 7 * 86400) {
				dates.put(dt, DateType.NEXT_ORDER);
				dates.put(dt - 3 * 86400, DateType.NEXT_ORDER);
				dates.put(dt - 86400, DateType.SPECIAL_PRICE);
				dates.put(dt - 4 * 86400, DateType.SPECIAL_PRICE);
			}
			ssi.setDates(dates);
			
			Map<Integer, Integer> deliveryByWeightIncrement = new HashMap<Integer, Integer>();
			deliveryByWeightIncrement.put( 15000, 50); //50 rub each 10 kg
			ssi.setShopDeliveryByWeightIncrement(shop, deliveryByWeightIncrement );
			
			Map<DeliveryType, String> deliveryTypeAddressMasks = new HashMap<DeliveryType, String>();
			deliveryTypeAddressMasks.put(DeliveryType.SHORT_RANGE, ".*(Пушкин|Павловск|Шушары|Колпино).*");
			deliveryTypeAddressMasks.put(DeliveryType.LONG_RANGE, ".*");
			ssi.setShopDeliveryTypeAddressMasks(shop, deliveryTypeAddressMasks ); 
			
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
		 * PRODUCT_ID=300, PRODUCT_NAME, PRODUCT_SHORT_DESCRIPTION, PRODUCT_WEIGHT, PRODUCT_IMAGEURL, PRODUCT_PRICE, PRODUCT_CATEGORY_IDS,
		 * PRODUCT_FULL_DESCRIPTION, PRODUCT_IMAGE_URLS, PRODUCT_PRICE_RETAIL, PRODUCT_PRICE_INET, PRODUCT_PRICE_VIP, PRODUCT_PRICE_SPECIAL,
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
		importData.setUrl(StorageHelper.saveImage("http://localhost:8888/data/products_1000_sheksna.csv", userId, false, null));

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
		importData.setUrl(StorageHelper.saveImage("http://localhost:8888/data/product_categories.csv", userId, false, null));

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
		importData.setUrl(StorageHelper.saveImage("http://localhost:8888/data/producers.csv", userId, false, null));

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

			for (VoGroup dg : new VoGroup[] { new VoGroup("Мой подъзд", radiusStarecase, true), new VoGroup("Мой дом", radiusHome, true),
					new VoGroup("Соседи", radiusSmall, true), new VoGroup("Пешая доступность", radiusMedium, true),
					new VoGroup("Быстро Доехать", radiusLarge, true) }) {
				defaultGroups.add(dg);
				pm.makePersistent(dg);
			}
	}

	// ======================================================================================================================
	private static void initializeUsers(List<String> locCodes) throws InvalidOperation {
		AuthServiceImpl asi = new AuthServiceImpl();
		long user2Id, user3Id;
		userId = user2Id = user3Id = 0;
		try {
			userId = asi.registerNewUser(user1name, user1lastName, user1pass, user1email, locCodes.get(0));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		try {
			user2Id = asi.registerNewUser(user2name, user2lastName, user2pass, user2email, locCodes.get(1));
		} catch (Exception e1) {

		}
		userId = user2Id;
		try {
			user3Id = asi.registerNewUser(user3name, user3lastName, user3pass, user3email, locCodes.get(2));
		} catch (Exception e) {

		}
		userId = userId == 0 ? user2Id == 0 ? user3Id : user2Id : userId;
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
					new VoPostalAddress(new VoBuilding(street, "32/3", new BigDecimal(zan32k3Long), new BigDecimal(zan32k3Lat), pm), (byte) 1, (byte) 1, (byte) 5,
							""),
					new VoPostalAddress(new VoBuilding(street, "32/3", new BigDecimal(zan32k3Long), new BigDecimal(zan32k3Lat), pm), (byte) 2, (byte) 1, (byte) 50,
							""),
					new VoPostalAddress(new VoBuilding(street, "35", new BigDecimal("30.419684"), new BigDecimal("59.932544"), pm), (byte) 1, (byte) 11, (byte) 35,
							""),
					new VoPostalAddress(new VoBuilding(street, "6", new BigDecimal("30.404331"), new BigDecimal("59.934177"), pm), (byte) 1, (byte) 2, (byte) 25,
							"") };
			

			for (VoPostalAddress pa : addresses) {
				try {
					Pair<String, String> position = VoGeocoder.getPosition(pa.getBuilding());
					pa.getBuilding().setLocation( new BigDecimal(position.first), new BigDecimal(position.second));
				} catch (Exception e) {
					e.printStackTrace();
				}
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
