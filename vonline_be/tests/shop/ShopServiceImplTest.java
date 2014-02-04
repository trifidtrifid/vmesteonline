package shop;

import static org.junit.Assert.fail;


import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.Shop;

public class ShopServiceImplTest {
	
	private static final String PRC1_DESCR = "КОрневая категория";
	private static final String ROOT_PRODUCT_CAT1 = "Root ProductCat1";
	private static final String LOGO = "http://www.ru.tele2.ru/img/logo.gif";
	private static final String DESCR = "TELE2 shop";
	private static final String NAME = "Во!Магазин";
	private static final String SESSION_ID = "11111111111111111111111";
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
	private HashSet<Long> topic2Set = new HashSet<Long>();
	private static HashSet<String> tags;
	private static HashMap<DeliveryType, Double> deliveryCosts;
	private static HashMap<PaymentType, Double> paymentTypes;
	private static Set<ByteBuffer> images = new HashSet<ByteBuffer>();
	private static Set<ByteBuffer> images2 = new HashSet<ByteBuffer>();
	private static Set<ByteBuffer> images3 = new HashSet<ByteBuffer>();
	
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
		
		images.add(ByteBuffer.wrap("http://ya.ru".getBytes()));
		images2.add(ByteBuffer.wrap("http://google.com".getBytes()));
		images2.add(ByteBuffer.wrap("http://ya.ru".getBytes()));
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
			Shop shop = new Shop(0L, NAME, DESCR, userAddress, LOGO, 
					userId, topicSet, tags, deliveryCosts, paymentTypes);
			
			Long shopId = si.registerShop( shop);

			Long rootCatId = si.registerProductCategory(new ProductCategory(0L, 0L, ROOT_PRODUCT_CAT1, PRC1_DESCR, images, topicSet), shopId);
			Long SecCatId = si.registerProductCategory(new ProductCategory(0L, rootCatId, "Second LevelPC", "Второй уровень", images2, topic2Set), shopId);
			Long THirdCatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird LevelPC", "Третий уровень", images2, topic2Set), shopId);
			Long THird2CatId = si.registerProductCategory(new ProductCategory(0L, SecCatId, "THird Level2PC", "Третий уровень2", images3, topic2Set), shopId);
			
		}  catch (TException e) {
			e.printStackTrace();
			fail("Exception thrown: "+ e.getMessage());
		}
		
	}
/*
	@Test
	public void testRegisterProducer() {
		fail("Not yet implemented");
	}

	@Test
	public void testUploadProducts() {
		fail("Not yet implemented");
	}

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

