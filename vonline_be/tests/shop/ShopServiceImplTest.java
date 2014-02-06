package shop;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
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

	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());
	
	private AuthServiceImpl asi;
	private String userHomeLocation;
	private long userId;
	private UserServiceImpl usi;
	ShopServiceImpl si;
	MessageServiceImpl msi;
	private static String TAG = "TAG";
	Topic topic;
	PostalAddress userAddress;
	private HashSet<Long> topicSet = new HashSet<Long>();
	private static HashSet<String> tags;
	private static HashMap<DeliveryType, Double> deliveryCosts;
	private static HashMap<PaymentType, Double> paymentTypes;
	
	private static Set<ByteBuffer> images = new HashSet<ByteBuffer>();
	private static Set<ByteBuffer> images2 = new HashSet<ByteBuffer>();
	private static Set<ByteBuffer> images3 = new HashSet<ByteBuffer>();

	private HashSet<Long> topic2Set = new HashSet<Long>();
	static {
		tags = new HashSet<String>();
		tags.add(TAG);
		
		deliveryCosts = new HashMap<DeliveryType,Double>();
		deliveryCosts.put(DeliveryType.SELF_PICKUP, 0.0D);
		deliveryCosts.put(DeliveryType.SHORT_RANGE, 11.0D);
		deliveryCosts.put(DeliveryType.LONG_RANGE, 22.0D);
	
		paymentTypes = new HashMap<PaymentType,Double>();
		paymentTypes.put(PaymentType.CASH, 1.0D);
		paymentTypes.put(PaymentType.CREDIT_CARD, 2.0D);
		paymentTypes.put(PaymentType.TRANSFER, 3.0D);
	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		
		//register and login current user
		//Initialize USer Service
		String sessionId = SESSION_ID;
		asi = new AuthServiceImpl( sessionId );
		List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();
		Assert.assertNotNull( userLocation );
		Assert.assertTrue( userLocation.size() > 0 );

		userHomeLocation = userLocation.get(0);
		userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
		Assert.assertTrue( userId > 0 );
		asi.login("eml", "pswd");
		usi = new UserServiceImpl(sessionId);
		si = new ShopServiceImpl(sessionId);
		msi = new MessageServiceImpl(sessionId);
		
		userAddress = usi.getUserHomeAddress();
		List<Group> userGroups = usi.getUserGroups();
		long gId = userGroups.get(0).getId();
		
		topic = msi.createTopic(gId, "AAA", MessageType.BASE, "", new HashMap<MessageType, Long>() , 
				new HashMap<Long, String>(), usi.getUserRubrics().get(0).getId(), 0);
		
		topicSet.add(topic.getId());
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
		//asi.logout();
	}

	@Test
	public void testRegisterShop() {
		try {
			
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, 
					userId, topicSet, tags, deliveryCosts, paymentTypes);
			
			Long id = si.registerShop( shop);
			Shop savedShop = si.getShop( id );

			Assert.assertEquals(savedShop.getName(), NAME);
			Assert.assertEquals(savedShop.getDescr(),DESCR);
			Assert.assertEquals(savedShop.getAddress(),userAddress);
			Assert.assertEquals(savedShop.getOwnerId(),userId);
			Assert.assertEquals(savedShop.getLogoURL(),LOGO);
			Assert.assertEquals(savedShop.getTopicSet(), topicSet);
			Assert.assertEquals(savedShop.getTags(),tags);
			Assert.assertEquals(savedShop.getDeliveryCosts(),deliveryCosts);
			Assert.assertEquals(savedShop.getPaymentTypes(), paymentTypes);
			
		}  catch (Throwable e) {
			e.printStackTrace();
			fail("Exception thrown: "+ e.getMessage());
		}
	
	}

	@Test
	public void testRegisterProductCategory() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			Long rootCatId = si.registerProductCategory(new ProductCategory(0L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet), shopId);
			Long SecCatId = si.registerProductCategory(new ProductCategory(0L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set), shopId);
			/* Long THirdCatId = */si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set),
					shopId);
			Long THird2CatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set),
					shopId);

			List<ProductCategory> rootPcs = si.getProductCategories(0);
			Assert.assertEquals(rootPcs.size(), 1);
			ProductCategory rc = rootPcs.get(0);
			Assert.assertEquals((long) rc.getId(), (long) rootCatId);
			Assert.assertEquals((long) rc.getParentId(), 0L);
			Assert.assertEquals(rc.getName(), ROOT_PRODUCT_CAT1);
			Assert.assertEquals(rc.getDescr(), PRC1_DESCR);
			Assert.assertEquals(rc.getLogoURLset(), images);
			Assert.assertEquals(rc.getTopicSet(), topicSet);

			List<ProductCategory> slPcs = si.getProductCategories(rootCatId);
			Assert.assertEquals(slPcs.size(), 1);
			ProductCategory l2c = slPcs.get(0);
			Assert.assertEquals((long) l2c.getId(), (long) SecCatId);
			Assert.assertEquals((long) l2c.getParentId(), (long) rootCatId);
			Assert.assertEquals(l2c.getName(), "Second LevelPC");
			Assert.assertEquals(l2c.getDescr(), "Второй уровень");
			Assert.assertEquals(l2c.getLogoURLset(), images2);
			Assert.assertEquals(l2c.getTopicSet(), topic2Set);

			List<ProductCategory> tlPcs = si.getProductCategories(SecCatId);
			Assert.assertEquals(tlPcs.size(), 2);
			ProductCategory l3c = tlPcs.get(1);
			Assert.assertEquals((long) l3c.getId(), (long) THird2CatId);
			Assert.assertEquals((long) l3c.getParentId(), (long) SecCatId);
			Assert.assertEquals(l3c.getName(), "THird Level2PC");
			Assert.assertEquals(l3c.getDescr(), "Третий уровень2");
			Assert.assertEquals(l3c.getLogoURLset(), images3);
			Assert.assertEquals(l3c.getTopicSet(), topic2Set);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testRegisterProducer() {
		try {
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);

			Long shopId = si.registerShop(shop);
			// set current shop
			si.getShop(shopId);

			long prodId = si.registerProducer( new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()), "http://google.com"), shopId);
			try {
				si.registerProducer( new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()), "http://google2.com"), shopId+1);
				fail("Created Producer with incorrect shopId");
			} catch( InvalidOperation ioe ){
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

			long prodId = si.registerProducer( new Producer(0L, "Производитель1", "Описание производителя", ByteBuffer.wrap(LOGO.getBytes()), "http://google.com"), shopId);
			long prod2Id = si.registerProducer( new Producer(0L, "Производитель2", "Описание производителя2", ByteBuffer.wrap(LOGO.getBytes()), "http://google2.com"), shopId);
		
			Long rootCatId = si.registerProductCategory(new ProductCategory(0L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet), shopId);
			Long SecCatId = si.registerProductCategory(new ProductCategory(0L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set), shopId);
			Long THirdCatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set),
					shopId);
			Long THird2CatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set),
					shopId);
			
			ArrayList<FullProductInfo> productsList = new ArrayList<FullProductInfo>();

			HashSet<Long> categories1 = new HashSet<Long>();
			categories1.add(THirdCatId);
			categories1.add(SecCatId);
			
			HashSet<Long> categories2 = new HashSet<Long>();
			categories2.add(rootCatId);
			categories2.add(THird2CatId);
			
			HashMap<PriceType, Double> pricesMap1 = new HashMap<PriceType,Double>();
			pricesMap1.put(PriceType.INET, 12.0D);
			pricesMap1.put(PriceType.INET, 13.0D);
			
			HashMap<String, String> optionsMap1 = new HashMap<String, String>();
			optionsMap1.put("цвет", "белый");
			optionsMap1.put("вкус", "слабый");
			
			HashMap<PriceType, Double> pricesMap2 = new HashMap<PriceType,Double>();
			pricesMap2.put(PriceType.INET, 14.0D);
			pricesMap2.put(PriceType.RETAIL, 15.0D);
			
			HashMap<String, String> optionsMap2 = new HashMap<String, String>();
			optionsMap2.put("цвет", "черный");
			optionsMap2.put("вкус", "мерзкий");
			
			productsList.add( new FullProductInfo( new Product(0, "Пролукт 1", "Описание продукта 1", 100D, ByteBuffer.wrap(LOGO.getBytes()), 11D), 
					new ProductDetails( categories1, "dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, prodId)));
			
			productsList.add( new FullProductInfo( new Product(0, "Пролукт 2", "Описание продукта 2", 200D, ByteBuffer.wrap(LOGO.getBytes()), 12D), 
					new ProductDetails( categories2, "dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, prod2Id)));
					
			Set<Long> upProductsIdl = si.uploadProducts( productsList, shopId, true);
			//expects to get all of products
			ProductListPart allProductList = si.getProducts(0, 1, rootCatId);
	
			Assert.assertEquals(allProductList.getLength(), 2);
			List<Product> products = allProductList.getProducts();
			Assert.assertEquals(products.size(), 1);
			
		} catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}
/*
	@Test
	public void testUploadProductCategoies() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFullOrders() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateOrderStatusesById() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDates() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetShops() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDates() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetShop() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProducers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProductCategories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProducts() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProductDetails() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOrders() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOrderDetails() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testCancelOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testConfirmOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testAppendOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testMergeOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddOrderLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveOrderLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetOrderDeliveryType() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetOrderPaymentType() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetOrderDeliveryAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetOrderPaymentStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetProductPrices() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDeliveryCosts() {
		fail("Not yet implemented");
	}*/

}

