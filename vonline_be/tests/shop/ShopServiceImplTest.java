package shop;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.Group;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.MessageServiceImpl;
import com.vmesteonline.be.MessageType;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.ShopServiceImpl;
import com.vmesteonline.be.Topic;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDetails;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.shop.ProductListPart;
import com.vmesteonline.be.shop.Shop;

public class ShopServiceImplTest {

	private static final String LOGO = "http://www.ru.tele2.ru/img/logo.gif";
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
	MessageServiceImpl msi;
	private static String TAG = "TAG";
	Topic topic;
	PostalAddress userAddress;
	private ArrayList<Long> topicSet = new ArrayList<Long>();
	private static ArrayList<String> tags;
	private static HashMap<DeliveryType, Double> deliveryCosts;
	private static HashMap<PaymentType, Double> paymentTypes;

	private static List<ByteBuffer> images = new ArrayList<ByteBuffer>();
	private static List<ByteBuffer> images2 = new ArrayList<ByteBuffer>();
	private static List<ByteBuffer> images3 = new ArrayList<ByteBuffer>();

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
		msi = new MessageServiceImpl(sessionId);

		userAddress = usi.getUserHomeAddress();
		List<Group> userGroups = usi.getUserGroups();
		long gId = userGroups.get(0).getId();

		topic = msi.createTopic(gId, "AAA", MessageType.BASE, "", new HashMap<MessageType, Long>(), new HashMap<Long, String>(), usi.getUserRubrics()
				.get(0).getId(), 0);

		topicSet.add(topic.getId());
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

			Long id = si.registerShop(shop);
			Shop savedShop = si.getShop(id);

			Assert.assertEquals(savedShop.getName(), NAME);
			Assert.assertEquals(savedShop.getDescr(), DESCR);
			Assert.assertEquals(savedShop.getAddress(), userAddress);
			Assert.assertEquals(savedShop.getOwnerId(), userId);
			Assert.assertEquals(savedShop.getLogoURL(), LOGO);
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

			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			ProductCategory rootCategory = new ProductCategory(0L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet);
			Long rootCatId = si.registerProductCategory(rootCategory, shopId);

			ProductCategory secCategory = new ProductCategory(0L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set);
			Long SecCatId = si.registerProductCategory(secCategory, shopId);

			ProductCategory thirdCategory = new ProductCategory(0L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set);
			ProductCategory third2Category = new ProductCategory(0L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set);

			si.registerProductCategory(thirdCategory, shopId);
			si.registerProductCategory(third2Category, shopId);

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

			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			long prodId = si.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google.com"), shopId);
			try {
				si.registerProducer(new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()), "http://google2.com"),
						shopId + 1);
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
			Assert.assertTrue(Arrays.equals(rc.getLogoURL(), LOGO.getBytes()));

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testUploadProducts() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);
		// initialize shop dates
					HashMap<Integer, DateType> dates = new HashMap<Integer, DateType>();
					si.setDates(dates);

			long prodId = si.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google.com"), shopId);
			long prod2Id = si.registerProducer(new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google2.com"), shopId);

			Long rootCatId = si.registerProductCategory(new ProductCategory(0L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet), shopId);
			Long SecCatId = si.registerProductCategory(new ProductCategory(0L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set), shopId);
			Long THirdCatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set), shopId);
			Long THird2CatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set),
					shopId);

			ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

			ArrayList<Long> categories1 = new ArrayList<Long>();
			categories1.add(THirdCatId);
			categories1.add(SecCatId);

			ArrayList<Long> categories2 = new ArrayList<Long>();
			categories2.add(rootCatId);
			categories2.add(THird2CatId);

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

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 1", "Описание продукта 1", 100D, ByteBuffer.wrap(LOGO.getBytes()), 11D),
					new ProductDetails(categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, prodId)));

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 2", "Описание продукта 2", 200D, ByteBuffer.wrap(LOGO.getBytes()), 12D),
					new ProductDetails(categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, prod2Id)));

			List<Long> upProductsIdl = si.uploadProducts(productsList, shopId, true);
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
			Assert.assertTrue(Arrays.equals(product2.getImageURL(), LOGO.getBytes()));
			Assert.assertEquals(product2.getWeight(), 200D);

			ProductDetails product2Details = si.getProductDetails(product2.getId());
			Assert.assertEquals(product2Details.getFullDescr(), "dsfsdfsdssssf");
			Assert.assertEquals(product2Details.getCategories(), categories2);
			Assert.assertEquals(product2Details.getImagesURLset(), images2);
			Assert.assertEquals(product2Details.getTopicSet(), topic2Set);
			Assert.assertEquals(product2Details.getOptionsMap(), optionsMap2);
			Assert.assertEquals(product2Details.getPricesMap(), pricesMap2);
			Assert.assertEquals(product2Details.getProducerId(), prod2Id);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testUploadProductCategoies() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			// create categories
			List<ProductCategory> categories = new Vector<ProductCategory>();
			ProductCategory rootCat = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet);
			ProductCategory l2Cat = new ProductCategory(2L, 1L, "Second LevelPC", "Второй уровень", images2, topic2Set);
			ProductCategory l3cat1 = new ProductCategory(3L, 2L, "THird LevelPC", "Третий уровень", images2, topic2Set);
			ProductCategory l3cat2 = new ProductCategory(4L, 2L, "THird Level2PC", "Третий уровень2", images3, topic2Set);

			categories.add(rootCat);
			categories.add(l2Cat);
			categories.add(l3cat1);
			categories.add(l3cat2);

			List<ProductCategory> uploadProductCategoies = si.uploadProductCategoies(categories, true, true);

			// check the consistency of IDs
			// get root category
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
	public void testGetFullOrders() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			// initialize shop dates
			HashMap<Integer, DateType> dates = new HashMap<Integer, DateType>();
			si.setDates(dates);
			
			// create categories
			List<ProductCategory> categories = new Vector<ProductCategory>();
			ProductCategory rootCat = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet);
			ProductCategory l2Cat = new ProductCategory(2L, 1L, "Second LevelPC", "Второй уровень", images2, topic2Set);
			ProductCategory l3cat1 = new ProductCategory(3L, 2L, "THird LevelPC", "Третий уровень", images2, topic2Set);
			ProductCategory l3cat2 = new ProductCategory(4L, 2L, "THird Level2PC", "Третий уровень2", images3, topic2Set);

			categories.add(rootCat);
			categories.add(l2Cat);
			categories.add(l3cat1);
			categories.add(l3cat2);

			List<ProductCategory> uploadProductCategoies = si.uploadProductCategoies(categories, true, true);
			
			// initialize shop dates
			si.setDates(dates);

			// create producers
			long prodId = si.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google.com"), shopId);
			long prod2Id = si.registerProducer(new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google2.com"), shopId);

			// Upload products

			ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

			ArrayList<Long> categories1 = new ArrayList<Long>();
			categories1.add(l3cat1.getId());

			ArrayList<Long> categories2 = new ArrayList<Long>();
			categories2.add(l3cat2.getId());

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

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 1", "Описание продукта 1", 100D, ByteBuffer.wrap(LOGO.getBytes()), 11D),
					new ProductDetails(categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, prodId)));

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 2", "Описание продукта 2", 200D, ByteBuffer.wrap(LOGO.getBytes()), 12D),
					new ProductDetails(categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, prod2Id)));

			List<Long> upProductsIdl = si.uploadProducts(productsList, shopId, true);

			// initialize shop dates
			dates = new HashMap<Integer, DateType>();
			si.setDates(dates);

			// si.createOrder(date)
			fail("Not finished in implementation yet");

			// check the consistency of IDs
			// get root category
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
			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			// initialize shop dates
			HashMap<Integer, DateType> dates = new HashMap<Integer, DateType>();
			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;

			dates.put(now, DateType.NEXT_ORDER);
			dates.put(now - day, DateType.CLEAN);
			dates.put(now + 2 * day, DateType.SPECIAL_PRICE);
			dates.put(now + 3 * day, DateType.CLOSED);
			dates.put(now + 30 * day, DateType.CLOSED);

			si.setDates(dates);

			si.getShop(shopId);

			Map<Integer, DateType> sd = si.getDates(now - 10 * day, now + 10 * day);
			Assert.assertEquals(sd.size(), 4);
			Assert.assertEquals(sd.get(now + 10), DateType.NEXT_ORDER);
			Assert.assertEquals(sd.get(now + 3 * day + 100), DateType.CLOSED);
			Assert.assertEquals(sd.get(now + 30 * day), null);

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

			Long shopId = si.registerShop(shop);
			Long shop2Id = si.registerShop(shop2);
			Long shop3Id = si.registerShop(shop3);

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
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);
			HashMap<Integer, DateType> dates = new HashMap<Integer, DateType>();
			si.setDates(dates);
			si.setDates(dates);
			si.setDates(dates);
			
			// create categories
			List<ProductCategory> categories = new Vector<ProductCategory>();
			ProductCategory rootCat = new ProductCategory(1L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet);
			ProductCategory l2Cat = new ProductCategory(2L, 1L, "Second LevelPC", "Второй уровень", images2, topic2Set);
			ProductCategory l3cat1 = new ProductCategory(3L, 2L, "THird LevelPC", "Третий уровень", images2, topic2Set);
			ProductCategory l3cat2 = new ProductCategory(4L, 2L, "THird Level2PC", "Третий уровень2", images3, topic2Set);

			categories.add(rootCat);
			categories.add(l2Cat);
			categories.add(l3cat1);
			categories.add(l3cat2);

			List<ProductCategory> uploadProductCategoies = si.uploadProductCategoies(categories, true, true);

			// create producers
			long prodId = si.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google.com"), shopId);
			long prod2Id = si.registerProducer(new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()),
					"http://google2.com"), shopId);

			// Upload products

			ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

			ArrayList<Long> categories1 = new ArrayList<Long>();
			categories1.add(l3cat1.getId());

			ArrayList<Long> categories2 = new ArrayList<Long>();
			categories2.add(l3cat2.getId());

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

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 1", "Описание продукта 1", 100D, ByteBuffer.wrap(LOGO.getBytes()), 11D),
					new ProductDetails(categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, prodId)));

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 2", "Описание продукта 2", 200D, ByteBuffer.wrap(LOGO.getBytes()), 12D),
					new ProductDetails(categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, prod2Id)));

			List<Long> upProductsIdl = si.uploadProducts(productsList, shopId, true);

			// initialize shop dates
			dates = new HashMap<Integer, DateType>();
			int now = (int) (System.currentTimeMillis() / 1000L);
			int day = 3600 * 24;

			dates.put(now, DateType.NEXT_ORDER);
			dates.put(now - day, DateType.CLEAN);
			dates.put(now + 2 * day, DateType.SPECIAL_PRICE);
			dates.put(now + 3 * day, DateType.CLOSED);
			dates.put(now + 6 * day, DateType.NEXT_ORDER);
			si.setDates(dates);

			si.createOrder(now+1000, PriceType.RETAIL);
			long canceledOID = si.cancelOrder();
			try {
				si.createOrder(now + 5 * day, PriceType.RETAIL);
				fail("Order could not be created in this date!");
			} catch (InvalidOperation e) {
				Assert.assertEquals(e.what, VoError.ShopNotOrderDate);
				e.printStackTrace();
			}
			long lastOrder = si.createOrder(now + 6 * day, PriceType.RETAIL);
			
			List<Order> orders = si.getOrders(now - 10 * day, now + 10 * day);
			Assert.assertEquals(orders.size(), 2);
			boolean orderFound = false;
			for (Order order : orders) {
				if (order.getStatus() == OrderStatus.CANCELED) {
					Assert.assertEquals(canceledOID, order.getId());
					Assert.assertEquals(0.0D, order.getTotalCost());
					Assert.assertEquals(now+1000, order.getDate());
					orderFound = true;
				}
			}
			Assert.assertTrue( orderFound );
			
			si.setOrderLine( upProductsIdl.get(0), 1.0D);
			OrderDetails orderDetails = si.getOrderDetails(lastOrder);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}

	}

	
	 /* @Test public void testGetOrderDetails() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testConfirmOrder() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testAppendOrder() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testMergeOrder() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testAddOrderLine() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testRemoveOrderLine() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testSetOrderDeliveryType() { fail("Not yet implemented");
	 * }
	 * 
	 * @Test public void testSetOrderPaymentType() { fail("Not yet implemented");
	 * }
	 * 
	 * @Test public void testSetOrderDeliveryAddress() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test public void testSetOrderPaymentStatus() {
	 * fail("Not yet implemented"); }
	 * 
	 * @Test public void testSetProductPrices() { fail("Not yet implemented"); }
	 * 
	 * @Test public void testSetDeliveryCosts() { fail("Not yet implemented"); }
	 */

}
