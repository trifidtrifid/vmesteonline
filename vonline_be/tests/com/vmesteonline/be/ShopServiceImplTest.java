package com.vmesteonline.be;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;


import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoOrderLine;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.UserMessage;
import com.vmesteonline.be.messageservice.UserTopic;
import com.vmesteonline.be.shop.bo.DataSet;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.bo.ExchangeFieldType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.IdNameChilds;
import com.vmesteonline.be.shop.bo.ImExType;
import com.vmesteonline.be.shop.bo.ImportElement;
import com.vmesteonline.be.MatrixAsList;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDates;
import com.vmesteonline.be.shop.OrderDatesType;
import com.vmesteonline.be.shop.OrderDetails;
import com.vmesteonline.be.shop.OrderLine;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.shop.ProductListPart;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.shop.UserShopRole;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

public class ShopServiceImplTest {
	
	Logger logger = Logger.getLogger(ShopServiceImplTest.class);

	private static final String LOGO = "http://fi.co/images/FI_logo.png";
	private static final String DESCR = "TELE2 shop";
	private static final String NAME = "Во!Магазин";
	private static final String SESSION_ID = "11111111111111111111111";

	private static final String PRC1_DESCR = "КОрневая категория";
	private static final String ROOT_PRODUCT_CAT1 = "Root ProductCat1";

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private AuthServiceImpl asi;
	private String userHomeLocation;
	private long userId;
	private UserServiceImpl usi;
	ShopServiceImpl si;
	ShopBOServiceImpl sbi;
	MessageServiceImpl msi;
	private static String TAG = "TAG";
	Topic topic;
	PostalAddress userAddress;
	PostalAddress userAddress2;

	private ArrayList<Long> topicSet = new ArrayList<Long>();
	private static ArrayList<String> tags;
	private static HashMap<DeliveryType, Double> deliveryCosts;
	private static HashMap<PaymentType, Double> paymentTypes;

	private static List<String> images = new ArrayList<String>();
	private static List<String> images2 = new ArrayList<String>();
	private static List<String> images3 = new ArrayList<String>();

	private ArrayList<Long> topic2Set = new ArrayList<Long>();
	static {
		tags = new ArrayList<String>();
		tags.add(TAG);

		deliveryCosts = new HashMap<DeliveryType, Double>();
		deliveryCosts.put(DeliveryType.SELF_PICKUP, 0.0D);
		deliveryCosts.put(DeliveryType.SHORT_RANGE, 11.0D);
		deliveryCosts.put(DeliveryType.LONG_RANGE, 22.0D);

		paymentTypes = new HashMap<PaymentType, Double>();
		paymentTypes.put(PaymentType.CASH, 1.0D);
		paymentTypes.put(PaymentType.CREDIT_CARD, 2.0D);
		paymentTypes.put(PaymentType.TRANSFER, 3.0D);
	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Defaults.initDefaultData();
		// register and login current user
		// Initialize USer Service
		String sessionId = SESSION_ID;
		asi = new AuthServiceImpl(sessionId);
		List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();
		Assert.assertNotNull(userLocation);
		Assert.assertTrue(userLocation.size() > 0);

		userHomeLocation = userLocation.get(0);
		userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
		Assert.assertTrue(userId > 0);
		asi.login("eml", "pswd");
		usi = new UserServiceImpl(sessionId);
		si = new ShopServiceImpl(sessionId);
		sbi = new ShopBOServiceImpl(sessionId);
		
		msi = new MessageServiceImpl(sessionId);

		userAddress = usi.getUserHomeAddress();
		List<Group> userGroups = usi.getUserGroups();
		if(userGroups.size() > 0 ){
			long gId = userGroups.get(0).getId();
	
			Message msg = new Message(0, 0, MessageType.BASE, 0, gId, 0, 0, 0, "", 0, 0,
					new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null);
			Topic tpc = new Topic(0, "AAA", msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, null);
			topic = msi.postTopic(tpc);

			/*
			 * topic = msi.createTopic(gId, "AAA", MessageType.BASE, "", new
			 * HashMap<MessageType, Long>(), new HashMap<Long, String>(),
			 * usi.getUserRubrics() .get(0).getId(), 0);
			 */	
			topicSet.add(topic.getId());
		} 
		Country country = usi.getCounties().get(0);
		City city = usi.getCities(country.getId()).get(0);
		Street street = usi.getStreets(city.getId()).get(0);
		Building building = usi.createNewBuilding(street.getId(), "17/3", "123.45", "54.321");
		userAddress2 = new PostalAddress(country, city, street, building, (byte) 1, (byte) 2, 3, "");
	}

	@After
	public void tearDown() throws Exception {
		asi.logout();
		helper.tearDown();
	}

	@Test
	public void testRegisterShop() {
		try {

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long id = sbi.registerShop(shop);
			Shop savedShop = si.getShop(id);

			Assert.assertEquals(savedShop.getName(), NAME);
			Assert.assertEquals(savedShop.getDescr(), DESCR);
			Assert.assertEquals(savedShop.getAddress(), userAddress);
			Assert.assertEquals(savedShop.getOwnerId(), userId);
			Assert.assertTrue(savedShop.getLogoURL() != null);
			Assert.assertEquals(savedShop.getTopicSet(), topicSet);
			Assert.assertEquals(savedShop.getTags(), tags);
			Assert.assertEquals(savedShop.getDeliveryCosts(), deliveryCosts);
			Assert.assertEquals(savedShop.getPaymentTypes(), paymentTypes);

		} catch (Throwable e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}

	}

	@Test
	public void testRegisterProductCategory() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			sbi.registerProducer( new Producer(1L, "ddd","www", "", "" ), shopId);
			
			ProductCategory rootCategory = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet, 0);
			Long rootCatId = sbi.registerProductCategory(rootCategory, shopId);

			ProductCategory secCategory = new ProductCategory(2L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set, 0);
			Long SecCatId = sbi.registerProductCategory(secCategory, shopId);

			ProductCategory thirdCategory = new ProductCategory(3L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set, 0);
			ProductCategory third2Category = new ProductCategory(4L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set, 0);

			sbi.registerProductCategory(thirdCategory, shopId);
			sbi.registerProductCategory(third2Category, shopId);

			try {
				importProductsForTest();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ProductCategory> rootPcs = si.getProductCategories(0);
			Assert.assertEquals(rootPcs.size(), 1);
			ProductCategory rc = rootPcs.get(0);
			validateCategory(rc, rootCategory);

			List<ProductCategory> slPcs = si.getProductCategories(rootCatId);
			Assert.assertEquals(slPcs.size(), 1);
			ProductCategory l2c = slPcs.get(0);
			validateCategory(l2c, secCategory);

			List<ProductCategory> tlPcs = si.getProductCategories(SecCatId);
			Assert.assertEquals(tlPcs.size(), 2);
			ProductCategory l3c = tlPcs.get(1);
			validateCategory(l3c.getId() == third2Category.getId() ? third2Category : thirdCategory, l3c);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	private void validateCategory(ProductCategory left, ProductCategory right) {
		Assert.assertEquals((long) left.getId(), (long) right.getId());
		Assert.assertEquals((long) left.getParentId(), (long) right.getParentId());
		Assert.assertEquals(left.getName(), right.getName());
		Assert.assertEquals(left.getDescr(), right.getDescr());
		Assert.assertEquals(left.getLogoURLset(), right.getLogoURLset());
		Assert.assertEquals(left.getTopicSet(), right.getTopicSet());
	}

	@Test
	public void testRegisterProducer() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			long prodId = sbi.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", LOGO, "http://google.com"), shopId);
			try {
				sbi.registerProducer(new Producer(1L, "Производитель2", "Описание производителя2", LOGO, "http://google2.com"), shopId + 1);
				fail("Created Producer with incorrect shopId");
			} catch (InvalidOperation ioe) {
				Assert.assertEquals(ioe.getWhat(), VoError.IncorrectParametrs);
			}

			List<Producer> producers = si.getProducers();
			Assert.assertEquals(producers.size(), 1);
			Producer rc = producers.get(0);
			Assert.assertEquals((long) rc.getId(), (long) prodId);
			Assert.assertEquals(rc.getName(), "Производитель1");
			Assert.assertEquals(rc.getDescr(), "Описание производителя");
			Assert.assertEquals(rc.getHomeURL(), "http://google.com");
			Assert.assertTrue(rc.getLogoURL() != null);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testUploadProducts() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);
			// initialize shop dates
			setAllDates();
			

			/* long prodId = */sbi.registerProducer(new Producer(1L, "Производитель1", "Описание производителя", LOGO, "http://google.com"), shopId);
			long prod2Id = sbi.registerProducer(new Producer(2L, "Производитель2", "Описание производителя2", LOGO, "http://google2.com"), shopId);

			Long rootCatId = sbi.registerProductCategory(new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet, 0), shopId);
			Long SecCatId = sbi.registerProductCategory(new ProductCategory(2L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set, 0),
					shopId);
			Long THirdCatId = sbi.registerProductCategory(new ProductCategory(3L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set, 0),
					shopId);
			Long THird2CatId = sbi.registerProductCategory(new ProductCategory(4L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set, 0),
					shopId);

			ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

			ArrayList<Long> categories1 = new ArrayList<Long>();
			categories1.add(3L);
			categories1.add(2L);

			ArrayList<Long> categories2 = new ArrayList<Long>();
			categories2.add(1L);
			categories2.add(4L);

			HashMap<PriceType, Double> pricesMap1 = new HashMap<PriceType, Double>();
			pricesMap1.put(PriceType.INET, 12.0D);
			pricesMap1.put(PriceType.INET, 13.0D);

			HashMap<String, String> optionsMap1 = new HashMap<String, String>();
			optionsMap1.put("цвет", "белый");
			optionsMap1.put("вкус", "слабый");

			HashMap<PriceType, Double> pricesMap2 = new HashMap<PriceType, Double>();
			pricesMap2.put(PriceType.INET, 14.0D);
			pricesMap2.put(PriceType.RETAIL, 15.0D);

			HashMap<String, String> optionsMap2 = new HashMap<String, String>();
			optionsMap2.put("цвет", "черный");
			optionsMap2.put("вкус", "мерзкий");

			productsList.add(new FullProductInfo(new Product(1, "Пролукт 1", "Описание продукта 1", 100D, LOGO, 11D, "стакан", 1000, shopId, true, 1), new ProductDetails(
					categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, 3000, new HashSet<String>())));

			productsList.add(new FullProductInfo(new Product(2, "Пролукт 2", "Описание продукта 2", 200D, LOGO, 12D, "кг", 1000, shopId, true, 2), new ProductDetails(
					categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, 3000, new HashSet<String>())));

			List<Long> upProductsIdl = sbi.uploadProducts(productsList, shopId, true);
			// expects to get all of products
			// the first product is posted to second level and one of the third, the
			// second - in the root and the other third
			// so both of third categorise will have one of product, root and second
			// level will have both of the products

			ProductListPart rootProductList = si.getProducts(0, 1, rootCatId);
			ProductListPart Lev2PoductList = si.getProducts(0, 100, SecCatId);
			ProductListPart Lev3_1PoductList = si.getProducts(0, 1, THirdCatId);
			ProductListPart Lev3_2PoductList = si.getProducts(0, 1000, THird2CatId);

			Assert.assertEquals(rootProductList.getLength(), 2);
			Assert.assertEquals(Lev2PoductList.getLength(), 2);
			Assert.assertEquals(Lev3_1PoductList.getLength(), 1);
			Assert.assertEquals(Lev3_2PoductList.getLength(), 1);

			Assert.assertEquals(rootProductList.getProducts().size(), 1);
			Assert.assertEquals(Lev2PoductList.getProducts().size(), 2);
			// List is sored by Name
			Assert.assertEquals((long) Lev2PoductList.getProducts().get(0).getId(), (long) upProductsIdl.get(0));

			Product product2 = Lev2PoductList.getProducts().get(1);
			Assert.assertEquals((long) product2.getId(), (long) upProductsIdl.get(1));
			Assert.assertEquals(product2.getName(), "Пролукт 2");
			Assert.assertEquals(product2.getShortDescr(), "Описание продукта 2");
			Assert.assertEquals(product2.getPrice(), 12D);
			Assert.assertTrue(product2.getImageURL() != null);
			Assert.assertEquals(product2.getWeight(), 200D);

			ProductDetails product2Details = si.getProductDetails(product2.getId());
			Assert.assertEquals(product2Details.getFullDescr(), "dsfsdfsdssssf");
			Assert.assertEquals(product2Details.getCategories(), Arrays.asList(new Long[] { rootCatId, THird2CatId }));
			Assert.assertEquals(product2Details.getImagesURLset(), images2);
			Assert.assertEquals(product2Details.getTopicSet(), topic2Set);
			Assert.assertEquals(product2Details.getOptionsMap(), optionsMap2);
			Assert.assertEquals(product2Details.getPricesMap(), pricesMap2);
			
		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	private void setAllDates() throws InvalidOperation {
		sbi.setDate(new OrderDates(OrderDatesType.ORDER_WEEKLY, Calendar.MONDAY, 3, 0, PriceType.INET));
		sbi.setDate(new OrderDates(OrderDatesType.ORDER_WEEKLY, Calendar.THURSDAY, 4, 0, PriceType.INET));
	}

	@Test
	public void testUploadProductCategoies() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			// create categories
			List<ProductCategory> categories = new Vector<ProductCategory>();
			sbi.registerProducer( new Producer(1L, "ddd","www", "", "" ), shopId);
			ProductCategory rootCat = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet, 0);
			ProductCategory l2Cat = new ProductCategory(2L, 1L, "Second LevelPC", "Второй уровень", images2, topic2Set, 0);
			ProductCategory l3cat1 = new ProductCategory(3L, 2L, "THird LevelPC", "Третий уровень", images2, topic2Set, 0);
			ProductCategory l3cat2 = new ProductCategory(4L, 2L, "THird Level2PC", "Третий уровень2", images3, topic2Set, 0);

			categories.add(rootCat);
			categories.add(l2Cat);
			categories.add(l3cat1);
			categories.add(l3cat2);

			/* List<ProductCategory> uploadProductCategoies = */sbi.uploadProductCategoies(categories, true);

			// check the consistency of IDs
			// get root category
			try {
				importProductsForTest();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<ProductCategory> rootCats = si.getProductCategories(0);
			Assert.assertEquals(1, rootCats.size());
			validateCategory(rootCat, rootCats.get(0));

			List<ProductCategory> l2Cats = si.getProductCategories(rootCats.get(0).getId());
			Assert.assertEquals(1, l2Cats.size());
			validateCategory(l2Cat, l2Cats.get(0));

			List<ProductCategory> l3Cats = si.getProductCategories(l2Cats.get(0).getId());
			Assert.assertEquals(2, l3Cats.size());
			validateCategory(l3cat1, l3Cats.get(0));
			validateCategory(l3cat2, l3Cats.get(1));

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testSetDates() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			// initialize shop dates
			sbi.setDate( new OrderDates(OrderDatesType.ORDER_WEEKLY, Calendar.THURSDAY, 3, 0, PriceType.INET));
			Calendar now = Calendar.getInstance();
			int nowDate = (int)(now.getTimeInMillis() / 1000L);
			int mondayBeforeNow = nowDate - ( now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY ) * 86400;
			int tuesBeforeNow = nowDate - ( now.get(Calendar.DAY_OF_WEEK) - Calendar.TUESDAY ) * 86400;
			int nextThursdayAfterNow = nowDate - ( now.get(Calendar.DAY_OF_WEEK) - Calendar.THURSDAY) * 86400;
			
			Assert.assertEquals( nextThursdayAfterNow/86400, si.getNextOrderDate( mondayBeforeNow ).orderDate/86400 );
			Assert.assertEquals( nextThursdayAfterNow/86400 + 7, si.getNextOrderDate( tuesBeforeNow ).orderDate/86400);
			Assert.assertEquals( nextThursdayAfterNow/86400 + 7, si.getNextOrderDate( nextThursdayAfterNow ).orderDate/86400);
			
			sbi.setDate( new OrderDates(OrderDatesType.ORDER_WEEKLY, Calendar.MONDAY, 2, 0, PriceType.RETAIL));
			Assert.assertEquals( nextThursdayAfterNow/86400, si.getNextOrderDate( mondayBeforeNow ).orderDate/86400 );
			Assert.assertEquals( mondayBeforeNow/86400 + 7, si.getNextOrderDate( tuesBeforeNow ).orderDate/86400);
			Assert.assertEquals( mondayBeforeNow/86400 + 7, si.getNextOrderDate( nextThursdayAfterNow ).orderDate/86400);
			
		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testGetShops() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Shop shop2 = new Shop(0L, NAME + 1, DESCR + 1, userAddress, LOGO + 1, userId, topic2Set, tags, deliveryCosts, paymentTypes);
			Shop shop3 = new Shop(0L, NAME + 2, DESCR + 2, userAddress, LOGO + 2, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long shopId = sbi.registerShop(shop);
			Long shop2Id = sbi.registerShop(shop2);
			/* Long shop3Id = */sbi.registerShop(shop3);

			// set current shop
			List<Shop> shops = si.getShops();
			Assert.assertEquals(3, shops.size());
			for (Shop s : shops) {
				if (s.getId() == shopId)
					Assert.assertEquals(s.getName(), NAME);
				else if (s.getId() == shop2Id)
					Assert.assertEquals(s.getName(), NAME + 1);
				else
					Assert.assertEquals(s.getName(), NAME + 2);
			}

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testCreateOrder() {
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);

			long canceledOID = si.cancelOrder(0);
			try {
				si.createOrder(now + 5 * day, "aaaa");
				fail("Order could not be created in this date!");
			} catch (InvalidOperation e) {
				Assert.assertEquals(e.what, VoError.ShopNotOrderDate);
			}
			long lastOrder = si.createOrder(now + 6 * day, "aaaa").getId();

			List<Order> orders = si.getOrders(now - 10 * day, now + 10 * day);
			Assert.assertEquals(orders.size(), 2);
			boolean orderFound = false;
			for (Order order : orders) {
				if (order.getStatus() == OrderStatus.CANCELED) {
					Assert.assertEquals(canceledOID, order.getId());
					Assert.assertEquals(0.0D, order.getTotalCost());
					Assert.assertEquals(now + 1000, order.getDate());
					orderFound = true;
				}
			}
			Assert.assertTrue(orderFound);

			OrderLine newOrderLine = si.setOrderLine(0,upProductsIdl.get(0), 1.0D, null, null).newOrderLine;
			Assert.assertEquals(newOrderLine.getProduct().getId(), upProductsIdl.get(0).longValue());
			Assert.assertEquals(newOrderLine.getQuantity(), 1.0D);
			Assert.assertEquals(newOrderLine.getPrice(), 12.0D);

			si.setOrderLine(0,upProductsIdl.get(0), 1.0D, null, null); // set the same
																																// quantity
			// again
			si.setOrderLine(0,upProductsIdl.get(0), 2.0D, null, null); // set new
																																// quantity
			si.setOrderLine(0,upProductsIdl.get(1), 3.0D, null, null); // add new
																																// product

			OrderDetails orderDetails = si.getOrderDetails(lastOrder);
			List<OrderLine> odrerLines = orderDetails.getOdrerLines();
			Assert.assertEquals(odrerLines.size(), 2);
			orders = si.getOrders(now + 5 * day, now + 7 * day);
			Assert.assertEquals(orders.size(), 1);
			Assert.assertEquals(orders.get(0).getTotalCost(), 12.0D * 2.0D + 15.0D * 3.0D);
			Assert.assertEquals(odrerLines.get(0).getQuantity(), 2.0D);
			si.removeOrderLine(0,upProductsIdl.get(0));

			orderDetails = si.getOrderDetails(lastOrder);
			odrerLines = orderDetails.getOdrerLines();
			Assert.assertEquals(odrerLines.size(), 1);
			orders = si.getOrders(now + 5 * day, now + 7 * day);
			Assert.assertEquals(orders.size(), 1);
			Assert.assertEquals(orders.get(0).getTotalCost(), 15.0D * 3.0D);
			Assert.assertEquals(odrerLines.get(0).getQuantity(), 3.0D);
			// here we have an order with One product of second type lets try merge
			// and add other orders

			si.createOrder(now + 10 * day, "aaaa");
			si.appendOrder(0,orders.get(0).getId()); // here we expect to have a copy of
																							// an old order
			si.appendOrder(0,orders.get(0).getId()); // here we expect to have doubled
																							// order
			orders = si.getOrders(now + 5 * day, now + 11 * day);
			Assert.assertEquals(orders.size(), 2);
			orderDetails = si.getOrderDetails(orders.get(1).getId());
			odrerLines = orderDetails.getOdrerLines();
			Assert.assertEquals(orders.get(1).getTotalCost(), 14.0D * 6.0D);
			Assert.assertEquals(odrerLines.get(0).getQuantity(), 6.0D);

			// merge an order
			si.mergeOrder(0,orders.get(0).getId()); // merge will add only products that
																						// are not included yet, so no lines
																						// expected to be added
			orders = si.getOrders(now + 5 * day, now + 11 * day);
			Assert.assertEquals(orders.size(), 2);
			orderDetails = si.getOrderDetails(orders.get(1).getId());
			Assert.assertEquals(orders.get(1).getTotalCost(), 14.0D * 6.0D);
			Assert.assertEquals(odrerLines.get(0).getQuantity(), 6.0D);

			// clean the order lines add a product 1 and merge with an old order with
			// product 2
			si.removeOrderLine(0,odrerLines.get(0).getProduct().getId());
			si.setOrderLine(0,upProductsIdl.get(0), 2.0D, null, null);
			si.mergeOrder(0,orders.get(0).getId());
			orders = si.getOrders(now + 9 * day, now + 11 * day);
			Assert.assertEquals(orders.size(), 1);
			orderDetails = si.getOrderDetails(orders.get(0).getId());

			// void testConfirmOrder() {
			long curOrderId = orders.get(0).getId();
			long confirmedOrder = si.confirmOrder(0, "");
			Assert.assertEquals(curOrderId, confirmedOrder);
			orders = si.getOrders(now + 9 * day, now + 11 * day);
			Assert.assertEquals(orders.size(), 1);
			Assert.assertEquals(orders.get(0).getStatus(), OrderStatus.CONFIRMED);

			List<Order> fullOrders = sbi.getFullOrders(0, now + 1000 * day, 0, 0);
			Assert.assertEquals(fullOrders.size(), 3);
			fullOrders = sbi.getFullOrders(0, now + 1000 * day, userId + 1, 0);
			Assert.assertEquals(fullOrders.size(), 0);
			fullOrders = sbi.getFullOrders(0, now + 1000 * day, userId, 0);
			Assert.assertEquals(fullOrders.size(), 3);
			fullOrders = sbi.getFullOrders(0, now + 1000 * day, userId, shopId + 1);
			Assert.assertEquals(fullOrders.size(), 0);
			fullOrders = sbi.getFullOrders(0, now + 1000 * day, userId, shopId);
			Assert.assertEquals(fullOrders.size(), 3);
			fullOrders = sbi.getFullOrders(0, now + 1000 * day, 0, shopId);
			Assert.assertEquals(fullOrders.size(), 3);

			orders = si.getOrdersByStatus(0, now + 1000 * day, OrderStatus.CONFIRMED);
			Assert.assertEquals(orders.size(), 1);
			Assert.assertEquals(orders.get(0).getId(), confirmedOrder);

			Order curOrder = si.getOrder(confirmedOrder);
			OrderDetails curOrderDetails = si.getOrderDetails(curOrder.getId());

			/*
			 * @Test public void testSetProductPrices() {
			 */
			Map<Long, Map<PriceType, Double>> productPrices = new HashMap<Long, Map<PriceType, Double>>();

			HashMap<PriceType, Double> pricesMap11 = new HashMap<PriceType, Double>();
			pricesMap11.put(PriceType.RETAIL, 22.0D);
			pricesMap11.put(PriceType.INET, 23.0D);

			HashMap<PriceType, Double> pricesMap21 = new HashMap<PriceType, Double>();
			pricesMap21.put(PriceType.INET, 24.0D);
			pricesMap21.put(PriceType.RETAIL, 25.0D);
			productPrices.put(upProductsIdl.get(0), pricesMap11);
			productPrices.put(upProductsIdl.get(1), pricesMap21);

			// CHeck costs before change of prices
			si.setOrderLine(0,upProductsIdl.get(0), 10.0D, null, null);
			si.setOrderLine(0,upProductsIdl.get(1), 1.0D, null, null);

			curOrder = si.getOrder(0);
			curOrderDetails = si.getOrderDetails(curOrder.getId());
			Assert.assertEquals(curOrder.getTotalCost(), 144.0);
			Assert.assertEquals(curOrderDetails.getOdrerLines().size(), 2);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(0).getPrice(), 13.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(0).getQuantity(), 10.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(1).getPrice(), 14.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(1).getQuantity(), 1.0D);

			// change Prices and check again
			sbi.setProductPrices(productPrices);
			si.setOrderLine(0,upProductsIdl.get(0), 10.0D, null, null);
			si.setOrderLine(0,upProductsIdl.get(1), 1.0D, null, null);

			curOrder = si.getOrder(0);
			curOrderDetails = si.getOrderDetails(curOrder.getId());
			Assert.assertEquals(curOrder.getTotalCost(), 254.0);
			Assert.assertEquals(curOrderDetails.getOdrerLines().size(), 2);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(0).getPrice(), 23.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(0).getQuantity(), 10.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(1).getPrice(), 24.0D);
			Assert.assertEquals(curOrderDetails.getOdrerLines().get(1).getQuantity(), 1.0D);

			// }

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}
	
	@Test
	public void testDeleteOrder(){
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			si.setOrderLine(0, upProductsIdl.get(0), 10D, "", null);
			si.setOrderLine(0, upProductsIdl.get(1), 20D, "", null);
			si.deleteOrder(0);
			
		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	private List<Long> createCategoriesAndProductsAndOrder(int now, int day, Long shopId) throws TException {
		List<Long> upProductsIdl;
		// set current shop
		si.getShop(shopId);
		setAllDates();

		// create categories
		List<ProductCategory> categories = new Vector<ProductCategory>();
		ProductCategory rootCat = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet, 0);
		ProductCategory l2Cat = new ProductCategory(2L, 1L, "Second LevelPC", "Второй уровень", images2, topic2Set, 0);
		ProductCategory l3cat1 = new ProductCategory(3L, 2L, "THird LevelPC", "Третий уровень", images2, topic2Set, 0);
		ProductCategory l3cat2 = new ProductCategory(4L, 2L, "THird Level2PC", "Третий уровень2", images3, topic2Set, 0);

		categories.add(rootCat);
		categories.add(l2Cat);
		categories.add(l3cat1);
		categories.add(l3cat2);

		/* List<ProductCategory> uploadProductCategoies = */sbi.uploadProductCategoies(categories, true);

		// create producers
		sbi.registerProducer(new Producer(1L, "Производитель1", "Описание производителя", LOGO, "http://google.com"), shopId);
		sbi.registerProducer(new Producer(2L, "Производитель2", "Описание производителя2", LOGO, "http://google2.com"), shopId);

		// Upload products

		ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

		ArrayList<Long> categories1 = new ArrayList<Long>();
		categories1.add(3L);

		ArrayList<Long> categories2 = new ArrayList<Long>();
		categories2.add(4L);

		HashMap<PriceType, Double> pricesMap1 = new HashMap<PriceType, Double>();
		pricesMap1.put(PriceType.RETAIL, 12.0D);
		pricesMap1.put(PriceType.INET, 13.0D);

		HashMap<String, String> optionsMap1 = new HashMap<String, String>();
		optionsMap1.put("цвет", "белый");
		optionsMap1.put("вкус", "слабый");

		HashMap<PriceType, Double> pricesMap2 = new HashMap<PriceType, Double>();
		pricesMap2.put(PriceType.INET, 14.0D);
		pricesMap2.put(PriceType.RETAIL, 15.0D);

		HashMap<String, String> optionsMap2 = new HashMap<String, String>();
		optionsMap2.put("цвет", "черный");
		optionsMap2.put("вкус", "мерзкий");

		productsList.add(new FullProductInfo(new Product(0, "Пролукт 1", "Описание продукта 1", 100D, LOGO, 11D, "стакан", 1000, shopId, true, 1), new ProductDetails(
				categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, 3000, new HashSet<String>())));

		productsList.add(new FullProductInfo(new Product(0, "Пролукт 2", "Описание продукта 2", 200D, LOGO, 12D, "кг", 1000, shopId, true, 2), new ProductDetails(
				categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, 3000, new HashSet<String>())));

		upProductsIdl = sbi.uploadProducts(productsList, shopId, true);

		try {
			setAllDates();
		} catch (TException e) {
			e.printStackTrace();
		}

		int date = (int) (System.currentTimeMillis() / 1000L);
		date = si.getNextOrderDate( date ).orderDate;
		
		si.createOrder(date, "aaaa");
		return upProductsIdl;
	}

	@Test
	public void testSetOrderDeliveryType() {

		try {

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			//Map<Integer, DateType> dateDateTypeMap = new HashMap<Integer, DateType>();
			int date = (int) (System.currentTimeMillis() / 1000L);
			//dateDateTypeMap.put(date, DateType.NEXT_ORDER);
			setAllDates();
			date = si.getNextOrderDate( date ).orderDate;

			long order = si.createOrder(date = si.getNextOrderDate( date ).orderDate, "aaaa").getId();
			Map<DeliveryType, Double> newDeliveryCosts = new HashMap<DeliveryType, Double>();
			newDeliveryCosts.put(DeliveryType.LONG_RANGE, 10.0D);
			newDeliveryCosts.put(DeliveryType.SHORT_RANGE, 5.0D);
			newDeliveryCosts.put(DeliveryType.SELF_PICKUP, 0.0D);
			sbi.setDeliveryCosts(newDeliveryCosts);

			si.setOrderDeliveryType(0, DeliveryType.SHORT_RANGE, null );
			List<Order> curOrders = si.getOrders(date, date + 1);
			Assert.assertEquals(curOrders.size(), 1);
			Assert.assertEquals(curOrders.get(0).getTotalCost(), 5.0D);
			si.setOrderDeliveryType(0, DeliveryType.LONG_RANGE, null);
			curOrders = si.getOrders(date, date + 1);
			Assert.assertEquals(curOrders.size(), 1);
			Assert.assertEquals(curOrders.get(0).getTotalCost(), 10.0D);
			Assert.assertEquals(si.getOrderDetails(order).getDelivery(), DeliveryType.LONG_RANGE);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}

	}

	// =====================================================================================================================
	@Test
	public void testSetOrderPaymentType() {

		try {

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			Map<Integer, DateType> dateDateTypeMap = new HashMap<Integer, DateType>();
			int date = (int) (System.currentTimeMillis() / 1000L) + 1000;
			dateDateTypeMap.put(date, DateType.NEXT_ORDER);
			setAllDates();
			date = si.getNextOrderDate(date).orderDate;
			long order = si.createOrder(date, "aaaa").getId();

			Map<PaymentType, Double> newPaymentCosts = new HashMap<PaymentType, Double>();
			newPaymentCosts.put(PaymentType.CREDIT_CARD, 10.0D);
			newPaymentCosts.put(PaymentType.SHOP_CREDIT, 5.0D);
			newPaymentCosts.put(PaymentType.CASH, 0.0D);
			sbi.setPaymentTypesCosts(newPaymentCosts);

			si.setOrderPaymentType(0, PaymentType.SHOP_CREDIT);
			List<Order> curOrders = si.getOrders(date, date + 1);
			Assert.assertEquals(curOrders.size(), 1);
			Assert.assertEquals(curOrders.get(0).getTotalCost(), 5.0D);
			si.setOrderPaymentType(0, PaymentType.CREDIT_CARD);
			curOrders = si.getOrders(date, date + 1);
			Assert.assertEquals(curOrders.size(), 1);
			Assert.assertEquals(curOrders.get(0).getTotalCost(), 10.0D);
			Assert.assertEquals(si.getOrderDetails(order).getPaymentType(), PaymentType.CREDIT_CARD);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	// =====================================================================================================================
	@Test
	public void testSetOrderDeliveryAddress() {
		try {

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			Map<Integer, DateType> dateDateTypeMap = new HashMap<Integer, DateType>();
			int date = (int) (System.currentTimeMillis() / 1000L) + 1000;
			dateDateTypeMap.put(date, DateType.NEXT_ORDER);
			setAllDates();
			date = si.getNextOrderDate(date).orderDate;
			long order = si.createOrder(date, "aaaa").getId();
			si.setOrderDeliveryAddress(0, userAddress2);
			OrderDetails orderDetails = si.getOrderDetails(order);
			Assert.assertEquals(orderDetails.getDeliveryTo(), userAddress2);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	// =====================================================================================================================
	@Test
	public void testSetOrderPaymentStatus() {

		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			Map<Integer, DateType> dateDateTypeMap = new HashMap<Integer, DateType>();
			int date = (int) (System.currentTimeMillis() / 1000L) + 1000;
			dateDateTypeMap.put(date, DateType.NEXT_ORDER);
			setAllDates();
			date = si.getNextOrderDate( (int) (System.currentTimeMillis() / 1000L) + 1000).orderDate;

			long order = si.createOrder(date, "aaaa").getId();
			PaymentStatus ps = si.getOrderDetails(order).getPaymentStatus();
			Assert.assertEquals(ps, PaymentStatus.WAIT);
			sbi.setOrderPaymentStatus(order, PaymentStatus.COMPLETE);
			ps = si.getOrderDetails(order).getPaymentStatus();
			Assert.assertEquals(ps, PaymentStatus.COMPLETE);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	// =====================================================================================================================
	@Test
	public void testDataImportProducersTest() {
		DataSet ds = new DataSet();
		ds.date = (int) (System.currentTimeMillis() / 1000L);
		ds.name = " Producers UPDATE Test";

		List<ExchangeFieldType> fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.PRODUCER_ID);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_NAME);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_HOMEURL);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_LOGOURL);
		fieldsOrder.add(ExchangeFieldType.PRODUCER_DESCRIPTION);
		try {

			ImportElement importData = new ImportElement(ImExType.IMPORT_PRODUCERS, "producers.csv", listToMap(fieldsOrder));
			String imgURL = StorageHelper
					.saveImage(
							("1; Производитель 1; http://yandex.ru/; \"http://fast.ulmart.ru/good_small_pics2/255807s.jpg\"; \"Длинный текст описания; с заятыми...\"\n"
									+ "2; Производитель 2; http://google.ru/; \"http://fast.ulmart.ru/good_small_pics2/255807s.jpg\"; \"JОпять и снова; Длинный текст описания; с заятыми...\"\n")
									.getBytes(), userId, false, null);
			importData.setUrl(imgURL);

			ds.addToData(importData);

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long id = sbi.registerShop(shop);
			/* Shop savedShop = */si.getShop(id);

			/* DataSet importData2 = */sbi.importData(ds);
			List<Producer> producers = si.getProducers();
			Assert.assertEquals(producers.size(), 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	public static <T> Map<Integer, T> listToMap(Collection<T> col) {
		int i = 0;
		Map<Integer, T> res = new TreeMap<Integer, T>();
		for (T t : col) {
			res.put(i++, t);
		}
		return res;
	}

	// =====================================================================================================================
	@Test
	public void testDataImportShopsTest() {
		DataSet ds = new DataSet();
		ds.date = (int) (System.currentTimeMillis() / 1000L);
		ds.name = " Shops UPDATE Test";

		List<ExchangeFieldType> fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.SHOP_NAME);
		fieldsOrder.add(ExchangeFieldType.SHOP_DESCRIPTION);
		fieldsOrder.add(ExchangeFieldType.SHOP_LOGOURL);
		fieldsOrder.add(ExchangeFieldType.SHOP_TAGS);

		ImportElement importData = new ImportElement(ImExType.IMPORT_SHOP, "shops.csv", listToMap(fieldsOrder));
		try {
			String imgURL = StorageHelper.saveImage(("Магазин %1; Магазин бытовой техники; http://yandex.st/www/1.807/yaru/i/logo.png; 1 | 2 | tag 3\n"
					+ "Техношок; Магазин Электроники; http://yandex.st/www/1.807/yaru/i/logo.png;").getBytes(), userId, false, null);
			importData.setUrl(imgURL);

			ds.addToData(importData);

			/* DataSet importData2 = */sbi.importData(ds);
			/* List<Shop> shops = */si.getShops();
			// Assert.assertEquals(shops.size(), 2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	// =====================================================================================================================
	@Test
	public void testDataImportCategoryTest() {
		DataSet ds = new DataSet();
		ds.date = (int) (System.currentTimeMillis() / 1000L);
		ds.name = " Categories UPDATE Test";

		// CATEGORY_ID = 200, CATEGORY_PARENT_ID, CATEGORY_NAME,
		// CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS
		List<ExchangeFieldType> fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.CATEGORY_PARENT_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_NAME);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_DESCRIPTION);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_LOGOURLS);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_TOPICS);

		ImportElement importData = new ImportElement(ImExType.IMPORT_CATEGORIES, "categories.csv", listToMap(fieldsOrder));
		try {

			String imgURL = StorageHelper.saveImage(("0; 1; КОпмы; Копьютеры и комплектующие; "
					+ "http://www.radionetplus.narod.ru/mini/images/radionetplus_ru_mini_128.gif | "
					+ "http://www.radionetplus.narod.ru/mini/images/radionetplus_ru_mini_130.gif;\n" + "1; 2; Ноутбуки;Ноуты и Планшеты;;;\n"
					+ "2; 3; Ноуты; ТОлько ноуты;;;\n" + "2; 4; Планшеты;Только планшеты;;;\n" + "1; 5; Переферия;\"Принтеры; мышы; клавы\";;;\n").getBytes(),
					userId, false, null);
			importData.setUrl(imgURL);

			ds.addToData(importData);

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			/* DataSet importData2 = */sbi.importData(ds);
			importProductsForTest();
			
			List<ProductCategory> productCategories = si.getProductCategories(0);
			Assert.assertEquals(productCategories.size(), 1);
			productCategories = si.getProductCategories(productCategories.get(0).getId());
			Assert.assertEquals(productCategories.size(), 2);
			productCategories = si.getProductCategories(productCategories.get(0).getId());
			Assert.assertEquals(productCategories.size(), 2);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	// =====================================================================================================================
	@Test
	public void testDataImportProductTest() {

		DataSet ds = new DataSet();
		ds.date = (int) (System.currentTimeMillis() / 1000L);
		ds.name = " Categories, Producer";

		// CATEGORY_ID = 200, CATEGORY_PARENT_ID, CATEGORY_NAME,
		// CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS
		List<ExchangeFieldType> fieldsOrder = new ArrayList<ExchangeFieldType>();
		fieldsOrder.add(ExchangeFieldType.CATEGORY_PARENT_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_ID);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_NAME);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_DESCRIPTION);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_LOGOURLS);
		fieldsOrder.add(ExchangeFieldType.CATEGORY_TOPICS);

		ImportElement importData = new ImportElement(ImExType.IMPORT_CATEGORIES, "categories.csv", listToMap(fieldsOrder));

		try {
			String imgURL = StorageHelper
					.saveImage(
							("0; 1; КОпмы; Копьютеры и комплектующие; http://www.radionetplus.narod.ru/mini/images/radionetplus_ru_mini_128.gif |http://www.radionetplus.narod.ru/mini/images/radionetplus_ru_mini_130.gif;\n"
									+ "1; 2; Ноутбуки;Ноуты и Планшеты;;;\n" + "1; 3; Планшеты;Только планшеты;;;\n" + "1; 5; Переферия;\"Принтеры; мышы; клавы\";;;\n")
									.getBytes(), userId, false, null);
			importData.setUrl(imgURL);

			ds.addToData(importData);

			fieldsOrder = new ArrayList<ExchangeFieldType>();
			fieldsOrder.add(ExchangeFieldType.PRODUCER_ID);
			fieldsOrder.add(ExchangeFieldType.PRODUCER_NAME);
			fieldsOrder.add(ExchangeFieldType.PRODUCER_HOMEURL);
			fieldsOrder.add(ExchangeFieldType.PRODUCER_LOGOURL);
			fieldsOrder.add(ExchangeFieldType.PRODUCER_DESCRIPTION);

			importData = new ImportElement(ImExType.IMPORT_PRODUCERS, "producers.csv", listToMap(fieldsOrder));
			imgURL = StorageHelper
					.saveImage(
							("1; Производитель 1; http://yandex.ru/; \"http://fast.ulmart.ru/good_small_pics2/255807s.jpg\"; \"Длинный текст описания; с заятыми...\"\n"
									+ "2; Производитель 2; http://google.ru/; \"http://fast.ulmart.ru/good_small_pics2/255807s.jpg\"; \"JОпять и снова; Длинный текст описания; с заятыми...\"\n")
									.getBytes(), userId, false, null);
			importData.setUrl(imgURL);

			ds.addToData(importData);

			List<ProductCategory> productCategories = null;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			/* DataSet importData2 = */sbi.importData(ds);

			importProductsForTest();

			productCategories = si.getProductCategories(0);
			productCategories = si.getProductCategories(productCategories.get(0).id);
			ProductListPart products = si.getProducts(0, 100, 0);
			Assert.assertEquals(products.length, 2);
			products = si.getProducts(0, 100, productCategories.get(0).getId());
			Assert.assertEquals(products.length, 1);
			products = si.getProducts(0, 100, productCategories.get(1).getId());
			Assert.assertEquals(products.length, 2);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	private void importProductsForTest() throws IOException, InvalidOperation {
		ImportElement importData;
		String imgURL;
		DataSet ds2 = new DataSet();
		ds2.date = (int) (System.currentTimeMillis() / 1000L);
		ds2.name = "Products";

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
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRICE_RETAIL);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_OPIONSAVP);
		productFieldsOrder.add(ExchangeFieldType.PRODUCT_PRODUCER_ID);

		importData = new ImportElement(ImExType.IMPORT_PRODUCTS, "product.csv", listToMap(productFieldsOrder));
		imgURL = StorageHelper
				.saveImage(
						("1;Keyboard; Клавистура 101 кнопка; 250.0; http://yandex.st/www/1.808/yaru/i/logo.png; 125.0; 2|3 ; 123.0; \"цвет:черный|материал:пластик\"; 1\n"
								+ "2;Mouse; Мышь 3 кнопки; 1250.0;; 1125.0; 3; 1123.0; цвет:зеленый; " + 2 + "\n").getBytes(), userId, false, null);
		importData.setUrl(imgURL);

		ds2.addToData(importData);

		/* DataSet importData2 = */sbi.importData(ds2);
	}

	// =====================================================================================================================
	@SuppressWarnings("unchecked")
	@Test
	public void testMergeOrderLinePackets() {
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);

			// Check merge lines of the same product
			/* OrderLine ol1 = */si.setOrderLine(0,upProductsIdl.get(0), 1.0D, null, null);
			/* OrderLine ol2 = */si.setOrderLine(0,upProductsIdl.get(1), 1.0D, null, null);
			PersistenceManager pm = PMF.getPm();
			try {
				Query q = pm.newQuery(VoOrder.class);
				q.setFilter("id == " + si.getSessionAttribute(CurrentAttributeType.ORDER, pm));
				List<VoOrder> vol = (List<VoOrder>) q.execute();
				Assert.assertEquals(vol.size(), 1);
				VoOrder vo = vol.get(0);
				Assert.assertEquals(vo.getOrderLines().size(), 2);

				Object[] ola = vo.getOrderLines().values().toArray();

				VoOrderLine vol1 = pm.getObjectById(VoOrderLine.class, ola[0]);
				VoOrderLine vol2 = pm.getObjectById(VoOrderLine.class, ola[1]);

				si.mergeOrderLinePackets(vol2, vol1);

				Map<Double, Integer> vol1ps = vol1.getPackets();
				Assert.assertTrue(vol1ps != null);
				Assert.assertEquals(vol1ps.size(), 1);
				Assert.assertEquals((int) vol1ps.get(1.0D), 2);

				si.mergeOrderLinePackets(vol2, vol1);
				vol1ps = vol1.getPackets();
				Assert.assertTrue(vol1ps != null);
				Assert.assertEquals(vol1ps.size(), 1);
				Assert.assertEquals((int) vol1ps.get(1.0D), 3);

				si.mergeOrderLinePackets(vol1, vol1);
				vol1ps = vol1.getPackets();
				Assert.assertTrue(vol1ps != null);
				Assert.assertEquals(vol1ps.size(), 1);
				Assert.assertEquals((int) vol1ps.get(1.0D), 6);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				pm.close();
			}

			pm = PMF.getPm();
			try {
				si.setOrderLine(0,upProductsIdl.get(1), 2.0D, null, null);

				Query q = pm.newQuery(VoOrder.class);
				q.setFilter("id == " + si.getSessionAttribute(CurrentAttributeType.ORDER, pm));
				List<VoOrder> vol = (List<VoOrder>) q.execute();
				Assert.assertEquals(vol.size(), 1);
				VoOrder vo = vol.get(0);
				Assert.assertEquals(vo.getOrderLines().size(), 2);

				Object[] ola = vo.getOrderLines().values().toArray();

				VoOrderLine vol1 = pm.getObjectById(VoOrderLine.class, ola[0]);
				VoOrderLine vol2 = pm.getObjectById(VoOrderLine.class, ola[1]);

				si.mergeOrderLinePackets(vol2, vol1);
				Map<Double, Integer> vol1ps = vol1.getPackets();
				Assert.assertTrue(vol1ps != null);
				Assert.assertEquals(vol1ps.size(), 2);
				Assert.assertEquals((int) vol1ps.get(1.0D), 6);
				Assert.assertEquals((int) vol1ps.get(2.0D), 1);
			} finally {
				pm.close();
			}

		} catch (TException e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	// ======================================================================================================================
	@Test
	public void testGetTotalOrdersReport() {
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			
			Long order1ID = createFourOrders(now, upProductsIdl);

			List<ExchangeFieldType> orderFields = Arrays.asList(new ExchangeFieldType[] { ExchangeFieldType.ORDER_DATE, ExchangeFieldType.ORDER_STATUS,
					ExchangeFieldType.ORDER_PRICE_TYPE, ExchangeFieldType.ORDER_TOTAL_COST, ExchangeFieldType.ORDER_CREATED,
					ExchangeFieldType.ORDER_DELIVERY_TYPE, ExchangeFieldType.ORDER_DELIVERY_COST, ExchangeFieldType.ORDER_DELIVERY_ADDRESS,
					ExchangeFieldType.ORDER_PAYMENT_TYPE, ExchangeFieldType.ORDER_PAYMENT_STATUS, ExchangeFieldType.ORDER_COMMENT,
					ExchangeFieldType.ORDER_USER_ID, ExchangeFieldType.ORDER_USER_NAME });
			List<ExchangeFieldType> orderLineFIelds = Arrays.asList(new ExchangeFieldType[] { ExchangeFieldType.ORDER_LINE_QUANTITY,
					ExchangeFieldType.ORDER_LINE_OPRDER_ID, ExchangeFieldType.ORDER_LINE_PRODUCT_ID, ExchangeFieldType.ORDER_LINE_PRODUCT_NAME,
					ExchangeFieldType.ORDER_LINE_PRODUCER_ID, ExchangeFieldType.ORDER_LINE_PRODUCER_NAME, ExchangeFieldType.ORDER_LINE_PRICE,
					ExchangeFieldType.ORDER_LINE_COMMENT, ExchangeFieldType.ORDER_LINE_PACKETS });

			DataSet totalOrdersReport = sbi.getTotalOrdersReport(si.getNextOrderDate( now ).orderDate, DeliveryType.SELF_PICKUP, listToMap(orderFields), listToMap(orderLineFIelds));

			Assert.assertTrue(totalOrdersReport != null);
			Assert.assertEquals(totalOrdersReport.data.size(), 6); // two order lines and one orders
			Assert.assertEquals(totalOrdersReport.data.get(0).getType(), ImExType.EXPORT_ORDER_LINES); // lines of the first order
			Assert.assertEquals(totalOrdersReport.data.get(1).getType(), ImExType.EXPORT_ORDER_LINES); // lines of the second order
			Assert.assertEquals(totalOrdersReport.data.get(5).getType(), ImExType.EXPORT_ORDERS); // orders
			Assert.assertEquals(VoHelper.listToMatrix( totalOrdersReport.data.get(2).getFieldsData()).size(), 2); // two orders
			// check content of order 1
			Assert.assertEquals(VoHelper.listToMatrix(totalOrdersReport.data.get(0).getFieldsData()).size(), 2);
			List<String> o1l1 = VoHelper.listToMatrix( totalOrdersReport.data.get(0).getFieldsData()).get(0);
			// upProductsIdl.get(0), 1.0D, "comment11", null
			Assert.assertEquals(o1l1.size(), 9);
			Assert.assertEquals(o1l1.get(0), "" + 35.0D);
			Assert.assertEquals(o1l1.get(1), "" + order1ID);
			Assert.assertEquals(o1l1.get(2), "" + upProductsIdl.get(0));
			Assert.assertEquals(o1l1.get(3), "Пролукт 1");
			Assert.assertEquals(o1l1.get(5), "Производитель1");
			Assert.assertEquals(o1l1.get(6), "" + 1.0D * 12.0D);
			Assert.assertEquals(o1l1.get(7), "comment11");
			Assert.assertEquals(o1l1.get(8), "11.0:1|12.0:2");

			// check order 1 description
			List<String> o1d = VoHelper.listToMatrix( totalOrdersReport.data.get(2).getFieldsData() ).get(0);
			Assert.assertEquals(o1d.size(), 13);
			Assert.assertEquals(o1d.get(0), "" + (now + 1000));
			Assert.assertEquals(o1d.get(1), OrderStatus.CONFIRMED.name());
			Assert.assertEquals(o1d.get(2), PriceType.RETAIL.name());
			Assert.assertEquals(o1d.get(3), "" + 450.0D);
			Assert.assertEquals(o1d.get(5), DeliveryType.SELF_PICKUP.name());
			Assert.assertEquals(o1d.get(6), "" + 0.0D);
			Assert.assertEquals(o1d.get(8), PaymentType.CASH.name());
			Assert.assertEquals(o1d.get(9), PaymentStatus.WAIT.name());
			Assert.assertEquals(o1d.get(10), "aaaa");
			Assert.assertEquals(o1d.get(11), "" + userId);
			Assert.assertEquals(o1d.get(12), "fn ln");

		} catch (TException e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	private Long createFourOrders(int now, List<Long> upProductsIdl) throws InvalidOperation {
		
		Long order1ID = si.getSessionAttribute(CurrentAttributeType.ORDER, null);
		// Check merge lines of the same product
		Map<Double, Integer> packets = new HashMap<Double, Integer>();
		packets.put(11D, 1);
		packets.put(12D, 2);
		/*OrderLine ol1 = */si.setOrderLine(0,upProductsIdl.get(0), 35.0D, "comment11", packets);
		/*OrderLine ol2 = */si.setOrderLine(0,upProductsIdl.get(1), 2.0D, "comment12", null);
		
		si.confirmOrder(0,"");
		
		int date = (int) (System.currentTimeMillis() / 1000L);
		try {
			date = si.getNextOrderDate( date ).orderDate;
		} catch (TException e) {
			e.printStackTrace();
		}
		
		/*long order2ID = */si.createOrder(date, "22aaaa");
		/*OrderLine ol21 = */si.setOrderLine(0,upProductsIdl.get(0), 35.0D, "comment21", packets);
		si.confirmOrder(0, "");
		
		/*long order3ID = */si.createOrder(date, "33aaaa");
		/*OrderLine ol31 = */si.setOrderLine(0,upProductsIdl.get(1), 33.0D, "comment31", null);
		si.setOrderDeliveryType(0,DeliveryType.LONG_RANGE,null);
		si.confirmOrder(0, "");
		
		/*long order4ID = */si.createOrder(date, "44aaaa");
		/*OrderLine ol41 = */si.setOrderLine(0,upProductsIdl.get(1), 44.0D, "comment41", null);

		si.confirmOrder(0, "");
		return order1ID;
	}

	// ======================================================================================================================

	@Test
	public void testGetTotalProductsReport() {
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			/*Long order1ID = */createFourOrders(now, upProductsIdl);

			List<ExchangeFieldType> productFields = Arrays.asList(new ExchangeFieldType[] { ExchangeFieldType.TOTAL_PROUCT_ID,
					ExchangeFieldType.TOTAL_PRODUCT_NAME, ExchangeFieldType.TOTAL_PRODUCER_ID, ExchangeFieldType.TOTAL_PRODUCER_NAME,
					ExchangeFieldType.TOTAL_PRODUCT_MIN_PACK, ExchangeFieldType.TOTAL_ORDERED, ExchangeFieldType.TOTAL_MIN_QUANTITY,
					ExchangeFieldType.TOTAL_REST, ExchangeFieldType.TOTAL_PREPACK_REQUIRED, ExchangeFieldType.TOTAL_PACK_SIZE,
					ExchangeFieldType.TOTAL_PACK_QUANTYTY, ExchangeFieldType.TOTAL_DELIVERY_TYPE });

			DataSet totalProductsReport = sbi.getTotalProductsReport(now + 1000, DeliveryType.SELF_PICKUP, listToMap(productFields));
			Assert.assertEquals(totalProductsReport.getDataSize(), 3);

		} catch (TException e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}

	// ======================================================================================================================

	@Test
	public void testGetTotalPackReport() {
		try {

			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;

			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);

			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			/*Long order1ID = */createFourOrders(now, upProductsIdl);

			List<ExchangeFieldType> productFields = Arrays.asList(new ExchangeFieldType[] { ExchangeFieldType.TOTAL_PROUCT_ID,
					ExchangeFieldType.TOTAL_PRODUCT_NAME, ExchangeFieldType.TOTAL_PRODUCER_ID, ExchangeFieldType.TOTAL_PRODUCER_NAME,
					ExchangeFieldType.TOTAL_PRODUCT_MIN_PACK, ExchangeFieldType.TOTAL_ORDERED, ExchangeFieldType.TOTAL_MIN_QUANTITY,
					ExchangeFieldType.TOTAL_REST, ExchangeFieldType.TOTAL_PREPACK_REQUIRED, ExchangeFieldType.TOTAL_PACK_SIZE,
					ExchangeFieldType.TOTAL_PACK_QUANTYTY, ExchangeFieldType.TOTAL_DELIVERY_TYPE });

			DataSet totalProductsReport = sbi.getTotalPackReport(now + 1000, DeliveryType.SELF_PICKUP, listToMap(productFields));
			Assert.assertEquals(totalProductsReport.getDataSize(), 3);

		} catch (TException e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		}
	}
//======================================================================================================================

	@Test
	public void testCalculateTheDistance() {
		PersistenceManager pm = PMF.getPm();
		try {
			
			createAddress("площадь Карла Фаберже", "6"); //650m from the shop
			createAddress("Косыгина", "4"); //2 km from the shop
			createAddress("Водопроводная", "74"); //5 km from the shop
			
			VoPostalAddress pa0 = null;
			for( VoPostalAddress pa : pm.getExtent(VoPostalAddress.class)){
				if( null != pa0 ){
					Assert.assertEquals( pa.getDistance(pa0), pa0.getDistance(pa));
					Assert.assertTrue( pa.getDistance(pa0) >= 0 );
					Assert.assertTrue( pa.getDistance(pa0) < 20000 );
					if( pa.getBuilding().toString().equals(pa0.getBuilding().toString()) )
						Assert.assertTrue(  0 == pa.getDistance(pa0));
					else
						Assert.assertTrue(  0 != pa.getDistance(pa0));

					System.out.println("Distance between " + addressString(pm, pa0) + " and " + addressString( pm, pa ) +
							" is " + pa.getDistance(pa0) + "km");
				}
				pa0 = pa;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		} finally {
			pm.close();
		}
	}
//======================================================================================================================

	private String addressString(PersistenceManager pm, VoPostalAddress pa) {
		return pm.getObjectById(VoStreet.class, pa.getBuilding().getStreetId()).getName() 
									+ " "+pa.getBuilding().getFullNo();
	}
//======================================================================================================================

	@Test
	public void testDeliveryDependOnRange() throws InvalidOperation{
		PersistenceManager pm = PMF.getPm();
			
		try{
			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;
			List<Long> upProductsIdl;
	
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = sbi.registerShop(shop);
			
			VoPostalAddress sa = new VoPostalAddress( userAddress, pm);
			logger.debug("Shop is at: " + addressString(pm, sa) + " located at: "+sa.getBuilding().getLatitude()+":"+sa.getBuilding().getLongitude());
			
			Map<Integer, Double> deliveryCostByDistance = new HashMap<Integer, Double>();
			deliveryCostByDistance.put(0, 100.0D); 
			deliveryCostByDistance.put(5, 150.0D);
			deliveryCostByDistance.put(10, 200.0D);
			sbi.setShopDeliveryCostByDistance(shopId, deliveryCostByDistance );
			
			upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			
			Long order1ID = createFourOrders(now, upProductsIdl);
			
			List<Order> orders = si.getOrders(now+1000, now+1005);
			PostalAddress pa0 = createAddress("площадь Карла Фаберже", "6"); //650m from the shop
			PostalAddress pa2 = createAddress("Косыгина", "4"); //2 km from the shop
			PostalAddress pa5 = createAddress("Водопроводная", "74"); //5 km from the shop
			
			si.setOrderDeliveryType(order1ID, DeliveryType.SHORT_RANGE, pa0);
			assertEquals(""+si.getOrderDetails(order1ID).getDeliveryCost(), ""+211.0D);
			
			si.setOrderDeliveryType(order1ID, DeliveryType.SHORT_RANGE, pa2);
			assertEquals(""+si.getOrderDetails(order1ID).getDeliveryCost(), ""+100.0D);
			
			si.setOrderDeliveryType(order1ID, DeliveryType.SHORT_RANGE, pa5);
			assertEquals(""+si.getOrderDetails(order1ID).getDeliveryCost(), ""+150.0D);
			
			
	} catch (Exception e) {
		e.printStackTrace();
		fail("Import failed!" + e);
	}  finally {
		pm.close();
	}
	
	}
//======================================================================================================================

	private PostalAddress createAddress(String streetName, String buuildingNAme) throws InvalidOperation, TException {
		
		PersistenceManager pm = PMF.getPm();
		try {
			
			VoCity city = pm.getObjectById(VoCity.class, usi.getCities(usi.getCounties().get(0).getId()).get(0).getId());
			VoStreet voStreet = new VoStreet( city, streetName, pm );
			VoBuilding voBuilding = new VoBuilding(voStreet, buuildingNAme, new BigDecimal("0"), new BigDecimal("0"),pm);
			Pair<String, String> position = VoGeocoder.getPosition(voBuilding);
			voBuilding.setLocation(new BigDecimal(position.first), new BigDecimal(position.second));
			VoPostalAddress voPostalAddress = new VoPostalAddress( voBuilding, (byte)1, (byte)1, (byte)1, "");
			pm.makePersistent(voPostalAddress);
			
			return voPostalAddress.getPostalAddress();
		} finally {
			pm.close();
		}
	}

	@Test
	public void testDeliveryDependOnWeight(){
		
	}
//======================================================================================================================

	@Test
	public void testDeliveryDependOnAddressMask() {
		PersistenceManager pm = PMF.getPm();
		try {
			
		
		
		} catch (Exception e) {
			e.printStackTrace();
			fail("Import failed!" + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Test
	public void testParseCSVfile() {
		String csv = " 1; 2;3\n4;5;6;\n\"7;8\";8;9";
		MatrixAsList parseCSVfile;
		try {
			String url = StorageHelper.saveImage(csv.getBytes(), userId, true, null);
			parseCSVfile = sbi.parseCSVfile(url);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Assert.assertEquals(parseCSVfile.rowCount, 3);
		Assert.assertEquals(parseCSVfile.elems.get(0), "1");
		Assert.assertEquals(parseCSVfile.elems.get(3), "4");
		Assert.assertEquals(parseCSVfile.elems.get(8), "9");
	}
//======================================================================================================================

	@Test
	public void testUserDeliveryAddress() {

		try {
			String addressText = "детскосельский 3";
			PostalAddress deliveryAddress = si.createDeliveryAddress( addressText, 81, (byte)2, (byte)8,"");
			List<String> addressList = si.getUserDeliveryAddresses().getElems();
			PostalAddress da = si.getUserDeliveryAddress(addressText);
			String deliveryAddressViewURL = si.getDeliveryAddressViewURL(addressText, 200, 200);
			String nullVal = si.getDeliveryAddressViewURL(null, 200, 200);
			si.deleteDeliveryAddress(addressText);
			List<String> addressListWithoutAddrText = si.getUserDeliveryAddresses().getElems();
			PostalAddress daNull = si.getUserDeliveryAddress(addressText);
			
		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	//======================================================================================================================
	
	@Test
	public void testGetProductsByCategories() {

		int now = (int) (System.currentTimeMillis() / 1000L);
		int day = 3600 * 24;
		List<Long> upProductsIdl;

		Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
		try {
			Long shopId = sbi.registerShop(shop);
			try {
				upProductsIdl = createCategoriesAndProductsAndOrder(now, day, shopId);
			} catch (TException e) {
				e.printStackTrace();
			}
			List<IdNameChilds> productsByCategories = si.getProductsByCategories(shopId);

			Assert.assertEquals(productsByCategories.size(), 2);

		} catch (InvalidOperation e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSetUserShopRole() {
		int now = (int) (System.currentTimeMillis() / 1000L);
		int day = 3600 * 24;
		List<Long> upProductsIdl;

		Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
		try {
			
			long shopId = sbi.registerShop(shop);

			UserShopRole userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.OWNER);
			
			long userId = asi.registerNewUser("fn1", "ln1", "pswd1", "eml1", userHomeLocation);
			Assert.assertTrue(asi.login("eml1", "pswd1"));
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.CUSTOMER);
			
			sbi.setUserShopRole(shop.getId(), "eml1", UserShopRole.BACKOFFICER );
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.BACKOFFICER);
			
			sbi.setUserShopRole(shop.getId(), "eml1", UserShopRole.CUSTOMER );
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.CUSTOMER);
			
			sbi.setUserShopRole(shop.getId(), "eml1", UserShopRole.OWNER );
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.OWNER);
			
			sbi.setUserShopRole(shop.getId(), "eml1", UserShopRole.CUSTOMER );
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.CUSTOMER);
			
			sbi.setUserShopRole(shop.getId(), "eml1", UserShopRole.ADMIN );
			userShopRole = si.getUserShopRole(shopId);
			Assert.assertEquals(userShopRole, UserShopRole.ADMIN);
			
		} catch (InvalidOperation e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetUserShopRole() {

		int now = (int) (System.currentTimeMillis() / 1000L);
		int day = 3600 * 24;

		Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
		try {
			
			UserShopRole userShopRole = si.getUserShopRole(sbi.registerShop(shop));
			Assert.assertEquals(userShopRole, UserShopRole.BACKOFFICER);

		} catch (InvalidOperation e) {
			e.printStackTrace();
		}
	}
	
	
}
