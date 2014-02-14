package com.vmesteonline.be;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoOrderLine;
import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.jdo2.shop.VoShop;
import com.vmesteonline.be.jdo2.shop.exchange.CategoryDesrciption;
import com.vmesteonline.be.jdo2.shop.exchange.FieldTranslator;
import com.vmesteonline.be.jdo2.shop.exchange.ProducerDescription;
import com.vmesteonline.be.jdo2.shop.exchange.ShopDescription;
import com.vmesteonline.be.shop.DataSet;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.ExchangeFieldType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.ImportElement;
import com.vmesteonline.be.shop.Order;
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
import com.vmesteonline.be.shop.ShopService.Iface;
import com.vmesteonline.be.utils.CSVHelper;
import com.vmesteonline.be.utils.VoHelper;

public class ShopServiceImpl extends ServiceImpl implements Iface, Serializable {

	public static Logger logger;

	static {

		logger = Logger.getLogger(ShopServiceImpl.class);
		// create fake data for tests

		String LOGO = "http://www.ru.tele2.ru/img/logo.gif";
		String DESCR = "TELE2 shop";
		String NAME = "Во!Магазин";
		String SESSION_ID = "11111111111111111111111";

		String PRC1_DESCR = "КОрневая категория";
		String ROOT_PRODUCT_CAT1 = "Root ProductCat1";

		AuthServiceImpl asi;
		String userHomeLocation;
		long userId;
		UserServiceImpl usi;
		ShopServiceImpl si;
		MessageServiceImpl msi;
		String TAG = "TAG";
		Topic topic;
		PostalAddress userAddress;
		PostalAddress userAddress2;

		ArrayList<Long> topicSet = new ArrayList<Long>();
		ArrayList<String> tags;
		HashMap<DeliveryType, Double> deliveryCosts;
		HashMap<PaymentType, Double> paymentTypes;

		List<String> images = new ArrayList<String>();
		List<String> images2 = new ArrayList<String>();
		List<String> images3 = new ArrayList<String>();

		List<Long> topic2Set = new ArrayList<Long>();

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

		// register and login current user
		// Initialize USer Service
		String sessionId = "1111";
		asi = new AuthServiceImpl(sessionId);
		try {
			List<String> userLocation = UserServiceImpl.getLocationCodesForRegistration();

			userHomeLocation = userLocation.get(0);
			userId = 0;
			try {
				userId = asi.registerNewUser("fn", "ln", "pswd", "eml", userHomeLocation);
			} catch (InvalidOperation e1) {
				e1.printStackTrace();
			}
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

			Country country = usi.getCounties().get(0);
			City city = usi.getCities(country.getId()).get(0);
			Street street = usi.getStreets(city.getId()).get(0);
			Building building = usi.createNewBuilding(street.getId(), "17/3", 123.45, 54.321);
			userAddress2 = new PostalAddress(country, city, street, building, (byte) 1, (byte) 2, 3, "");

			Shop shop = new Shop(0L, NAME, DESCR, userAddress2, LOGO, userId, topicSet, tags, deliveryCosts, paymentTypes);
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
			long prodId = si.registerProducer(new Producer(0L, "Производитель1", "Описание производителя", LOGO, "http://google.com"), shopId);
			long prod2Id = si.registerProducer(new Producer(0L, "Производитель2", "Описание производителя2", LOGO, "http://google2.com"), shopId);

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

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 1", "Описание продукта 1", 100D, LOGO, 11D), new ProductDetails(categories1,
					"dsfsdfsdf", images3, pricesMap1, optionsMap1, topicSet, prodId)));

			productsList.add(new FullProductInfo(new Product(0, "Пролукт 2", "Описание продукта 2", 200D, LOGO, 12D), new ProductDetails(categories2,
					"dsfsdfsdssssf", images2, pricesMap2, optionsMap2, topic2Set, prod2Id)));

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
			dates.put(now + 10 * day, DateType.NEXT_ORDER);
			si.setDates(dates);

			si.createOrder(now + 1000, PriceType.RETAIL);
			long canceledOID = si.cancelOrder();
			long lastOrder = si.createOrder(now + 6 * day, PriceType.RETAIL);

			List<Order> orders = si.getOrders(now - 10 * day, now + 10 * day);
			OrderLine newOrderLine = si.setOrderLine(upProductsIdl.get(0), 1.0D);

			si.setOrderLine(upProductsIdl.get(0), 1.0D); // set the same quantity
																										// again
			si.setOrderLine(upProductsIdl.get(0), 2.0D); // set new quantity
			si.setOrderLine(upProductsIdl.get(1), 3.0D); // add new product

			orders = si.getOrders(now + 5 * day, now + 7 * day);
			si.removeOrderLine(upProductsIdl.get(0));

			si.createOrder(now + 10 * day, PriceType.INET);
			// merge an order
			si.mergeOrder(orders.get(0).getId());
			si.setOrderLine(upProductsIdl.get(0), 2.0D);
			si.mergeOrder(orders.get(0).getId());
		} catch (InvalidOperation e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	private final class ProdcutNameComparator implements Comparator<Product>, Serializable {
		@Override
		public int compare(Product o1, Product o2) {
			return (o1.getName() + o1.getId()).compareTo(o2.getName() + o2.getId());
		}
	}

	ShopServiceImpl() {
	}

	public ShopServiceImpl(String sessionId) {
		super(sessionId);
	}

	@Override
	public long registerShop(Shop shop) throws InvalidOperation {
		return shop.id = new VoShop(shop).getId();
	}

	@Override
	public long registerProductCategory(ProductCategory productCategory, long shopId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			if( 0==shopId) shopId = getCurrentShopId(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			VoProductCategory voProductCategory = new VoProductCategory(voShop, productCategory.getParentId(), productCategory.getName(),
					productCategory.getDescr(), productCategory.getLogoURLset(), productCategory.getTopicSet(), pm);
			productCategory.setId(voProductCategory.getId());
			pm.makePersistent(voShop);
			return voProductCategory.getId();
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No Vo Shop found by ID=" + shopId);
		} finally {
			pm.close();
		}
	}

	@Override
	public long registerProducer(Producer producer, long shopId) throws InvalidOperation {
		return producer.id = new VoProducer(shopId, producer.getName(), producer.getDescr(), producer.getLogoURL(), producer.getHomeURL()).getId();
	}

	@Override
	public List<Long> uploadProducts(List<FullProductInfo> products, long shopId, boolean cleanShopBeforeUpload) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		List<Long> productIds;
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			pm.retrieve(voShop);
			if (cleanShopBeforeUpload && !voShop.getProducts().isEmpty())
				voShop.clearProducts();

			productIds = new ArrayList<Long>();
			VoProduct voProduct;
			for (FullProductInfo fpi : products) {
				voShop.addProduct(voProduct = VoProduct.createObject(shopId, fpi, pm));
				productIds.add(voProduct.getId());
			}
			pm.retrieve(voShop);
			pm.makePersistent(voShop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to load Products. " + e.getMessage());
		} finally {
			pm.close();
		}
		return productIds;
	}

	@Override
	public List<ProductCategory> uploadProductCategoies(List<ProductCategory> categories, boolean relativeIds, boolean cleanShopBeforeUpload)
			throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();

		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Product categories. SHOP ID is not set in session context.");
		}
		List<ProductCategory> categoriesCreated = new ArrayList<ProductCategory>();
		Map<Long, Long> idMap = new HashMap<Long, Long>();

		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			pm.retrieve(voShop);
			if (cleanShopBeforeUpload) {
				voShop.clearCategories();
				logger.debug("All categories removed from " + voShop);
			}
			for (ProductCategory pc : categories) {
				if (relativeIds && 0 != pc.getParentId()) {
					if (!idMap.containsKey(pc.getParentId())) {
						throw new InvalidOperation(VoError.IncorrectParametrs, "parent Id " + pc.getParentId()
								+ "not found as Id of categories above in a list provided");
					}
				}
				long parentId = relativeIds && idMap.containsKey(pc.getParentId()) ? idMap.get(pc.getParentId()) : pc.getParentId();
				logger.debug("Use paret category " + parentId + " to instead of " + pc.getParentId());
				VoProductCategory vpc = new VoProductCategory(voShop, parentId, pc.getName(), pc.getDescr(), pc.getLogoURLset(), pc.getTopicSet(), pm);
				idMap.put(pc.getId(), vpc.getId());
				categoriesCreated.add(vpc.getProductCategory());
				voShop.addProductCategory(vpc);
				pc.setId(vpc.getId());
				pc.setParentId(parentId);
				logger.debug("Category " + vpc + " added to " + voShop);
			}
			pm.makePersistent(voShop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. " + e);
		} finally {
			pm.close();
		}
		return categoriesCreated;
	}

	@Override
	public List<Order> getFullOrders(int dateFrom, int dateTo, long userId, long shopId) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		List<Order> ol;
		try {
			Query voquery = pm.newQuery(VoOrder.class);
			List<VoOrder> results = null;
			if (shopId != 0) {
				if (0 != userId) {
					voquery.setFilter("user == " + userId + " && shopId == " + shopId);
				} else {
					voquery.setFilter("shopId == " + shopId);
				}
			} else {
				if (0 != userId) {
					voquery.setFilter("user == " + userId);
				}
			}
			results = (List<VoOrder>) voquery.execute();
			ol = new ArrayList<Order>();
			for (VoOrder vo : results) {
				ol.add(vo.getOrder());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed load orders for userID=" + userId + " shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
		return ol;
	}

	@Override
	public void updateOrderStatusesById(Map<Long, OrderStatus> orderStatusMap) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
		ct.begin();
		try {
			for (Entry<Long, OrderStatus> ose : orderStatusMap.entrySet()) {
				VoOrder nextVO = pm.getObjectById(VoOrder.class, ose.getKey());
				if (null == nextVO) {
					logger.error("No order found by ID=" + ose.getKey());
				} else {
					nextVO.setStatus(ose.getValue());
					pm.makeNontransactional(nextVO);
				}
			}
			ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order statuses." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setDates(Map<Integer, DateType> dateDateTypeMap) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();

		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDates. SHOP ID is not set in session context.");
		}
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			pm.retrieve(voShop);
			voShop.setDates(dateDateTypeMap);
			pm.makePersistent(voShop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to set dates for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<Shop> getShops() throws InvalidOperation {
		List<Shop> shops = new ArrayList<Shop>();
		PersistenceManager pm = PMF.getPm();
		try {
			List<VoShop> voshops = (List<VoShop>) pm.newQuery(VoShop.class).execute();// pm.getExtent(VoShop.class);
			for (VoShop vs : voshops)
				shops.add(vs.getShop());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed toget shops." + e);
		} finally {
			pm.close();
		}
		return shops;
	}

	@Override
	public Map<Integer, DateType> getDates(int from, int to) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to getDates. SHOP ID is not set in session context.");
		}
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			return voShop.getDates(from, to);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getDates for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public Shop getShop(long shopId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			if (null != voShop) {
				setCurrentAttribute(CurrentAttributeType.SHOP.getValue(), voShop.getId(), pm);
				return voShop.getShop();
			}
			throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getShop by shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<Producer> getProducers() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				List<Producer> pl = new ArrayList<Producer>();
				for (VoProducer vp : voShop.getProducers())
					pl.add(vp.createProducer());
				return pl;
			}
			throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProducers for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				List<ProductCategory> lpc = new ArrayList<ProductCategory>();
				if (0 == currentProductCategoryId) {
					Query newQuery = pm.newQuery(VoProductCategory.class);
					newQuery.setFilter("parent == null");
					for (VoProductCategory voProductCategory : (List<VoProductCategory>) newQuery.execute()) {
						lpc.add(voProductCategory.getProductCategory());
					}
				} else {
					VoProductCategory parent = pm.getObjectById(VoProductCategory.class, currentProductCategoryId);
					for (VoProductCategory child : parent.getChilds()) {
						lpc.add(child.getProductCategory());
					}
				}
				return lpc;

			} else {
				throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProductCategories for shopId=" + shopId + " currentProductCategoryId="
					+ currentProductCategoryId + "." + e);
		} finally {
			pm.close();
		}
	}

	private SortedSet<Product> getProductsFromCategory(VoProductCategory category) {
		SortedSet<Product> rslt = new TreeSet<Product>(new ProdcutNameComparator());
		for (VoProductCategory cat : category.getChilds()) {
			rslt.addAll(getProductsFromCategory(cat));
		}
		for (VoProduct product : category.getProducts()) {
			rslt.add(product.getProduct());
		}
		return rslt;
	}

	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation {

		if (offset < 0 || length < 1)
			throw new InvalidOperation(VoError.IncorrectParametrs, "offset must be >= 0 and length > 0 ");

		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			String key = "VoProductsForCategory:" + shopId + ":" + categoryId;
			ArrayList<Product> products = ServiceImpl.getObjectFromCache(key);
			if (null == products) {

				VoProductCategory voPC = pm.getObjectById(VoProductCategory.class, categoryId);
				if (null != voPC) {
					SortedSet<Product> pfc = getProductsFromCategory(voPC);
					products = new ArrayList<Product>();
					products.addAll(pfc);

					try {
						putObjectToCache(key, products);
					} catch (Exception e) {
						logger.warn("FAiled to put product list ti the cache. " + e.getMessage());
						e.printStackTrace();
					}
					products = new ArrayList<Product>();
					products.addAll(pfc);
				} else {
					throw new InvalidOperation(VoError.GeneralError, "No Category found by ID:" + categoryId);
				}
			}
			if (offset >= products.size())
				return new ProductListPart(new ArrayList<Product>(), products.size());
			else
				return new ProductListPart(products.subList(offset, Math.min(offset + length, products.size())), products.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProducts for shopId=" + shopId + " currentProductCategoryId=" + categoryId + "."
					+ e);
		} finally {
			pm.close();
		}
	}

	@Override
	public ProductDetails getProductDetails(long productId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
			if (null != voProduct) {
				return voProduct.getProductDetails();
			}
			throw new InvalidOperation(VoError.GeneralError, "Not found");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProductDetails Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<Order> getOrders(int dateFrom, int dateTo) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			Query pcq = pm.newQuery(VoOrder.class);
			pcq.setFilter("shopId == " + shopId + " && user == " + getCurrentUserId(pm) + " && date >= " + dateFrom);
			List<VoOrder> ps = (List<VoOrder>) pcq.execute(dateFrom);
			List<Order> lo = new ArrayList<Order>();
			for (VoOrder p : ps) {
				if (p.getDate() < dateTo)
					lo.add(p.getOrder());
			}
			pcq.closeAll();
			return lo;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrders for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	Long getCurrentShopId(PersistenceManager pm) throws InvalidOperation {
		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context. shopId=" + shopId);
		}
		return shopId;
	}

	private VoShop getCurrentShop(PersistenceManager _pm) throws InvalidOperation {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;

		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}

		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			if (null != voShop) {
				return voShop;
			}
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is SET but SHOP not FOUND for this ID.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to SHOP by ID" + shopId + ". " + e);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	private VoOrder getCurrentOrder(PersistenceManager _pm) throws InvalidOperation {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		Long orderId = super.getSessionAttribute(CurrentAttributeType.ORDER, pm);
		if (null == orderId || 0 == orderId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is not set in session context.");
		}

		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
				return voOrder;
			}
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is SET but ORDER not FOUND for this ID.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to ORDER by ID" + orderId + ". " + e);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
				pm.retrieve(voOrder);
				return voOrder.getOrderDetails();
			}
			throw new InvalidOperation(VoError.GeneralError, "Not found");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrderDetails Id=" + orderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public long createOrder(int date, PriceType priceType) throws InvalidOperation {
		if (date < System.currentTimeMillis() / 1000L)
			throw new InvalidOperation(VoError.IncorrectParametrs, "Order could not be created for the past");

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = getCurrentShop(pm);
			pm.retrieve(shop);
			Collection<Integer> dateTypes = shop.getDates().subMap(date - date % 86400, date + 86400 - date % 86400).values();
			boolean NEXT_ORDERfound = false;
			for (Integer dt : dateTypes) {
				if (DateType.NEXT_ORDER.getValue() == dt) {
					NEXT_ORDERfound = true;
					break;
				}
			}

			if (!NEXT_ORDERfound)
				throw new InvalidOperation(VoError.ShopNotOrderDate, "The date is not avialable for order");

			VoUser user = getCurrentUser(pm);
			long id = new VoOrder(user, shop.getId(), date, priceType, pm).getId();
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), id, pm);
			return id;
		} finally {
			pm.close();
		}
	}

	@Override
	public long cancelOrder() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);

			currentOrder.setStatus(OrderStatus.CANCELED);
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.makePersistent(currentOrder);
			return currentOrder.getId();
		} finally {
			pm.close();
		}
	}

	@Override
	public long confirmOrder() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			currentOrder.setStatus(OrderStatus.CONFIRMED);
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
			pm.makePersistent(currentOrder);
			return currentOrder.getId();
		} finally {
			pm.close();
		}
	}

	/**
	 * Method adds all orderLines from order with id set in parameter to current
	 * order. All Lines with the same product ID would summarized!
	 **/
	@Override
	public long appendOrder(long oldOrderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				double addCost = 0;
				VoOrder currentOrder = getCurrentOrder(pm);
				Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
				if (currentOdrerLines.isEmpty()) {
					for (VoOrderLine voOrderLine : voOrder.getOdrerLines().values()) {
						double price = voOrderLine.getProduct().getPrice(currentOrder.getPriceType());
						currentOdrerLines.put(voOrderLine.getProduct().getId(), new VoOrderLine(currentOrder, voOrderLine.getProduct(),
								voOrderLine.getQuantity(), price));
						addCost += voOrderLine.getQuantity() * price;
					}

				} else {
					for (VoOrderLine voOrderLine : voOrder.getOdrerLines().values()) {
						double price = voOrderLine.getProduct().getPrice(currentOrder.getPriceType());
						long pid = voOrderLine.getProduct().getId();
						if (currentOdrerLines.containsKey(pid)) {
							VoOrderLine currentOL = currentOdrerLines.get(pid);
							currentOL.setQuantity(currentOL.getQuantity() + voOrderLine.getQuantity());
						} else {
							currentOdrerLines.put(voOrderLine.getProduct().getId(),
									new VoOrderLine(currentOrder, voOrderLine.getProduct(), voOrderLine.getQuantity(), price));
						}
						addCost += voOrderLine.getQuantity() * price;
					}

				}
				currentOrder.addCost(addCost);
				pm.makePersistent(currentOrder);
				return 0L;// addCost;
			}
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + oldOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	/**
	 * Method adds to current order lines for products that are not included to
	 * current order
	 **/
	@Override
	public long mergeOrder(long oldOrderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (VoOrderLine oldLine : voOrder.getOdrerLines().values()) {
					if (!currentOdrerLines.containsKey(oldLine.getProduct().getId())) {
						// there is no such product in the current order
						Double price = oldLine.getProduct().getPrice(currentOrder.getPriceType());
						// Product is detached member so the price stored in this object
						// would be actual
						currentOdrerLines.put(oldLine.getProduct().getId(), new VoOrderLine(currentOrder, oldLine.getProduct(), oldLine.getQuantity(), price));
						currentOrder.addCost(price * oldLine.getQuantity());
					}
				}
				pm.makePersistent(currentOrder);
				return 0;
			}
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + oldOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderLine setOrderLine(long productId, double quantity) throws InvalidOperation {
		if (0 == quantity) {
			removeOrderLine(productId);
			return null;
		}
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
			if (null != voProduct) {
				double price = voProduct.getPrice(currentOrder.getPriceType());
				VoOrderLine theLine = new VoOrderLine(currentOrder, voProduct, quantity, price);
				VoOrderLine oldLine = currentOdrerLines.put(productId, theLine);
				currentOrder.addCost(quantity * price - (null == oldLine ? 0 : oldLine.getPrice() * oldLine.getQuantity()));
				pm.makePersistent(currentOrder);
				return theLine.getOrderLine();
			}
			throw new InvalidOperation(VoError.GeneralError, "PRoduct not found by ID:" + productId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to addOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean removeOrderLine(long productId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoOrderLine removedLine = currentOdrerLines.remove(productId);
			if (null == removedLine)
				throw new InvalidOperation(VoError.IncorrectParametrs, "No order line found for product id=" + productId);
			currentOrder.addCost(-removedLine.getPrice() * removedLine.getQuantity());
			pm.makePersistent(currentOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to removeOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryType(DeliveryType deliveryType) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			DeliveryType oldDelivery;
			if (deliveryType != (oldDelivery = currentOrder.getDelivery())) {
				VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId(pm));

				Map<Integer, Double> deliveryCosts = voShop.getDeliveryCosts();
				if (deliveryCosts.containsKey(deliveryType.getValue())) {

					currentOrder.setDeliveryCost(deliveryCosts.get(deliveryType.getValue()));
					VoUser voUSer = getCurrentUser(pm);
					currentOrder.setDelivery(deliveryType);
					if (deliveryType == DeliveryType.SELF_PICKUP) {
						currentOrder.setDeliveryTo(voShop.getAddress());
					} else {
						currentOrder.setDeliveryTo(voUSer.getAddress());
					}
					currentOrder.addCost(voShop.getDeliveryCosts().get(deliveryType.getValue()) - voShop.getDeliveryCosts().get(oldDelivery.getValue()));
					pm.makePersistent(currentOrder);

				} else {
					logger.warn("" + voShop + " have no cost for delivery " + deliveryType.name() + " delivery type will not been changed");
				}
			}
			return currentOrder.getOrderDetails();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryType=" + deliveryType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean setOrderPaymentType(PaymentType paymentType) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId(pm));
			PaymentType oldPaymentType = currentOrder.getPaymentType();

			if (oldPaymentType != paymentType) {
				Map<Integer, Double> paymentTypes = voShop.getPaymentTypes();
				if (paymentTypes.containsKey(paymentType.getValue())) {
					double paymentFee = paymentTypes.get(oldPaymentType.getValue());
					currentOrder.setPaymentType(paymentType);
					currentOrder.setTotalCost(currentOrder.getTotalCost() - paymentFee + paymentTypes.get(paymentType.getValue()));
					currentOrder.setPaymentType(paymentType);
					pm.makePersistent(currentOrder);
					return false;
				} else {
					logger.warn("" + voShop + " have no Payment type " + paymentType.name() + " Payment type will not been changed");
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentType=" + paymentType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryAddress(PostalAddress deliveryAddress) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			currentOrder.setDeliveryTo(new VoPostalAddress(deliveryAddress, pm));
			pm.makePersistent(currentOrder);
			return currentOrder.getOrderDetails();

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryAddress to " + deliveryAddress + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			currentOrder.setPaymentStatus(newStatus);
			pm.makePersistent(currentOrder);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentStatus to " + newStatus.name() + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setProductPrices(Map<Long, Map<PriceType, Double>> newPricesMap) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		long shopId = getCurrentShopId(pm);
		// Transaction ct = pm.currentTransaction(); //cross tranaction required
		// ct.begin();
		try {
			for (Entry<Long, Map<PriceType, Double>> ppe : newPricesMap.entrySet()) {
				VoProduct vp = pm.getObjectById(VoProduct.class, ppe.getKey());
				vp.setPricesMap(ppe.getValue());
				pm.makePersistent(vp);
			}// Now time to update all of orders that not processed yet
			Query voquery = pm.newQuery(VoOrder.class);
			voquery.setFilter("status == '" + OrderStatus.NEW + "' && shopId == " + shopId);
			List<VoOrder> orders = (List<VoOrder>) voquery.execute();
			for (VoOrder voOrder : orders) {
				Map<Long, VoOrderLine> odrerLines = voOrder.getOdrerLines();
				double costChange = 0;
				for (VoOrderLine ol : odrerLines.values()) {
					// check if update make sense on the current order line for order's
					// kinda price type
					if (newPricesMap.containsKey(ol.getProduct().getId()) && newPricesMap.get(ol.getProduct().getId()).containsKey(voOrder.getPriceType())) {
						double oldPrice = ol.getPrice(), newPrice = newPricesMap.get(ol.getProduct().getId()).get(voOrder.getPriceType());
						ol.setPrice(newPrice);
						costChange += (newPrice - oldPrice) * ol.getQuantity();
					}
				}
				voOrder.addCost(costChange);
				pm.makePersistent(voOrder);
			}
			// ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			// ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order prices map." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setDeliveryCosts(Map<DeliveryType, Double> newDeliveryCosts) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop currentShop = getCurrentShop(pm);
			currentShop.getDeliveryCosts().putAll(VoShop.convertFromDeliveryTypeMap(newDeliveryCosts, new HashMap<Integer, Double>()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setDeliveryCosts." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setPaymentTypesCosts(Map<PaymentType, Double> setPaymentTypesCosts) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop currentShop = getCurrentShop(pm);
			currentShop.getPaymentTypes().putAll(VoShop.convertFromPaymentTypeMap(setPaymentTypesCosts, new HashMap<Integer, Double>()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setDeliveryCosts." + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void setOrderStatus(long orderId, OrderStatus newStatus) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setStatus(newStatus);
			pm.makePersistent(currentOrder);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentStatus to " + newStatus.name() + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public List<Order> getOrdersByStatus(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			Query pcq = pm.newQuery(VoOrder.class);
			pcq.setFilter("shopId == " + shopId + " && user == " + getCurrentUserId(pm) + " && date >= " + dateFrom + " && status == '" + status + "'");
			List<VoOrder> ps = (List<VoOrder>) pcq.execute(dateFrom);
			List<Order> lo = new ArrayList<Order>();
			for (VoOrder p : ps) {
				if (p.getDate() < dateTo)
					lo.add(p.getOrder());
			}
			pcq.closeAll();
			return lo;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrders for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public Order getOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder;
			if (0 == orderId) {
				currentOrder = getCurrentOrder(pm);
			} else {
				currentOrder = pm.getObjectById(VoOrder.class, orderId);
				super.setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), orderId, pm);
			}
			return currentOrder.getOrder();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to get Order by ID " + orderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void updateProduct(FullProductInfo newInfoWithOldId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoProduct vop = pm.getObjectById(VoProduct.class, newInfoWithOldId.getProduct().getId());
			vop.update(newInfoWithOldId, pm);
			pm.makePersistent(vop);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update product: " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void updateShop(Shop newShopWithOldId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop vos = pm.getObjectById(VoShop.class, newShopWithOldId.getId());
			vos.update(newShopWithOldId, pm);
			pm.makePersistent(vos);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update shop: " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void updateCategory(ProductCategory newCategoryInfo) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoProductCategory vopc = pm.getObjectById(VoProductCategory.class, newCategoryInfo.getId());
			vopc.update(newCategoryInfo, pm);
			pm.makePersistent(vopc);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update CAtegory: " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void updateProducer(Producer newInfoWithOldId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoProducer vopc = pm.getObjectById(VoProducer.class, newInfoWithOldId.getId());
			vopc.update(newInfoWithOldId, pm);
			pm.makePersistent(vopc);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update shop: " + e.getMessage());
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	protected static interface ImportDataProcessor<T> {
		public void process(List<T> list) throws InvalidOperation;
	}

	// ======================================================================================================================

	@Override
	public DataSet importData(DataSet data) throws InvalidOperation {

		for (ImportElement ie : data.getData()) {
			switch (ie.getType()) {
			case IMPORT_SHOP: {
				importInformation(ie, ExchangeFieldType.SHOP_ID, new ShopDescription(), new ImportDataProcessor() {
					@Override
					public void process(List list) throws InvalidOperation {
						processUpdateShops(list);
					}
				});
			}
				break;
			case IMPORT_PRODUCTS: {
				// new FieldTranslator(100, ie.getFieldsOrder(), )
			}
				break;
			case IMPORT_PRODUCERS: {
				importInformation(ie, ExchangeFieldType.PRODUCER_ID, new ProducerDescription(), new ImportDataProcessor() {
					@Override
					public void process(List list) throws InvalidOperation {
						processUpdateProducers(list);
					}
				});
			}
				break;
			case IMPORT_CATEGORIES: {

				importInformation(ie, ExchangeFieldType.CATEGORY_ID, new CategoryDesrciption(), new ImportDataProcessor() {
					@Override
					public void process(List list) throws InvalidOperation {
						processUpdateCategories(list);
					}
				});
			}
				break;
			default:
				break;
			}
		}
		return data;
	}

	// ======================================================================================================================

	private <T> void importInformation(ImportElement ie, ExchangeFieldType idField, T descriptionObject, ImportDataProcessor<T> processor)
			throws InvalidOperation {
		Map<Integer, String> fieldsMap = FieldTranslator.Translate(idField.getValue(), ie.getFieldsOrder(), descriptionObject);
		new ByteArrayInputStream(ie.fileData.array());

		try {
			List<T> infoRows = CSVHelper.loadCSVData(new ByteArrayInputStream(ie.fileData.array()), fieldsMap, descriptionObject);

			processor.process(infoRows);

			// Prepare new CSV data
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (!fieldsMap.containsValue("id")) {// insert ID As a last field if
																							// was not set
				fieldsMap.put(fieldsMap.size(), "id");
			}
			CSVHelper.writeCSVData(baos, fieldsMap, infoRows);
			baos.close();
			ie.setFileData(baos.toByteArray());

		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read Update " + e.getMessage());
		}
	}

	// ======================================================================================================================
	private void processUpdateProducers(List<ProducerDescription> producerRows) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			Long currentShopId = getCurrentShopId(pm);
			for (ProducerDescription producerDescription : producerRows) {
				if (0 != producerDescription.id)
					updateProducer(producerDescription.getProducer());
				else
					producerDescription.id = registerProducer(producerDescription.getProducer(), currentShopId);
			}
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	private void processUpdateShops(List<ShopDescription> shopRows) throws InvalidOperation {
		for (ShopDescription shopDescription : shopRows) {
			if (0 != shopDescription.id)
				updateShop(shopDescription.getShop());
			else
				shopDescription.id = registerShop(shopDescription.getShop());
		}
	}

	// ======================================================================================================================

	protected void processUpdateCategories(List<CategoryDesrciption> caregoryROws) throws InvalidOperation {
		
		//check if file contains update information for product categories
		if( 0 < caregoryROws.size() ) {
			
			if( caregoryROws.get(0).parentId == 0 ) { //so looks like it's an new category upload
				//convert presentation
				ArrayList<ProductCategory> pcl = new ArrayList<ProductCategory>();
				VoHelper.convertMutableSet(caregoryROws, pcl, new ProductCategory());
				this.uploadProductCategoies(pcl, true, false); //do not delete categories
			
			} else {
				for (CategoryDesrciption categoryDesrciption : caregoryROws) {
					if (0 != categoryDesrciption.id)
						updateCategory(categoryDesrciption.getProductCategory());
					else
						categoryDesrciption.id = registerProductCategory(categoryDesrciption.getProductCategory(), 0);
				}
			}
		}
	}

	// ======================================================================================================================

	@Override
	public DataSet exportOrders(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		// TODO Auto-generated method stub
		return null;
	}

	// ======================================================================================================================
	@Override
	public DataSet exportOrderLines(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		// TODO Auto-generated method stub
		return null;
	}

	// ======================================================================================================================
	@Override
	public DataSet exportProductsOrdered(int dateFrom, int dateTo, long producerId, long productCategoryId) throws InvalidOperation {
		// TODO Auto-generated method stub
		return null;
	}
}
