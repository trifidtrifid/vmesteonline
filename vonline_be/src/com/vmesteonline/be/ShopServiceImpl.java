package com.vmesteonline.be;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;


















//import com.google.api.client.util.Sets;
import com.vmesteonline.be.ServiceImpl.ServiceCategoryID;
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
import com.vmesteonline.be.jdo2.shop.exchange.OrderDescription;
import com.vmesteonline.be.jdo2.shop.exchange.OrderLineDescription;
import com.vmesteonline.be.jdo2.shop.exchange.ProducerDescription;
import com.vmesteonline.be.jdo2.shop.exchange.ProductDescription;
import com.vmesteonline.be.jdo2.shop.exchange.ProductOrderDescription;
import com.vmesteonline.be.jdo2.shop.exchange.ShopDescription;
import com.vmesteonline.be.shop.DataSet;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.ExchangeFieldType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.IdName;
import com.vmesteonline.be.shop.IdNameChilds;
import com.vmesteonline.be.shop.ImExType;
import com.vmesteonline.be.shop.ImportElement;
import com.vmesteonline.be.shop.MatrixAsList;
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
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

public class ShopServiceImpl extends ServiceImpl implements Iface, Serializable {

	public static Logger logger;
	private static int QUANTITY_SCALE = 5;

	static {
		logger = Logger.getLogger(ShopServiceImpl.class);
	}

	// ======================================================================================================================
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

	// ======================================================================================================================
	@Override
	public long registerProductCategory(ProductCategory productCategory, long shopId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			if (0 == shopId)
				shopId = getCurrentShopId(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			VoProductCategory voProductCategory = new VoProductCategory(voShop, productCategory.getId(), productCategory.getParentId(),
					productCategory.getName(), productCategory.getDescr(), productCategory.getLogoURLset(), productCategory.getTopicSet(), voShop.getOwnerId(),
					pm);
			productCategory.setId(voProductCategory.getId());
			pm.makePersistent(voShop);
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shopId));
			
			return voProductCategory.getId();
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No Vo Shop found by ID=" + shopId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public long registerProducer(Producer producer, long shopId) throws InvalidOperation {
		return producer.id = new VoProducer(shopId, getCurrentUserId(), producer).getId();
	}

	// ======================================================================================================================
	@Override
	public List<Long> uploadProducts(List<FullProductInfo> products, long shopId, boolean cleanShopBeforeUpload) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		List<Long> productIds;
		try {
			if (0 == shopId)
				shopId = getCurrentShopId(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);

			productIds = new ArrayList<Long>();
			VoProduct voProduct;
			for (FullProductInfo fpi : products) {
				FullProductInfo fpir = VoProduct.updateCategoriesByImportId(shopId, fpi, pm);
				VoProducer producer = VoProducer.getByImportId(shopId, fpir.details.producerId, pm);
				if (null == producer)
					throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to find Producer:" + fpir.details.producerId + " of product:"
							+ fpi.product.getId());

				fpir.details.producerId = producer.getId();

				if (0 != fpi.product.getId() && null != (voProduct = VoProduct.getByImportedId(shopId, fpir.product.id, pm))) {

					voProduct.update(fpir, getCurrentUserId(), pm);

				} else {
					voProduct = VoProduct.createObject(voShop, fpir, pm);
				}
				productIds.add(voProduct.getId());
			}
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shopId));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to load Products. " + e);
		} finally {
			pm.close();
		}
		return productIds;
	}

	// ======================================================================================================================
	private void uploadProducers(ArrayList<Producer> producers, boolean clean) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();

		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		Long userId = super.getCurrentUserId(pm);

		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Producers. SHOP ID is not set in session context.");
		}

		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());

			for (Producer pc : producers) {

				VoProducer vp = VoProducer.getByImportId(shopId, pc.getId(), pm);

				if (vp != null) {
					pc.setId(vp.getId());
					updateProducer(pc, pm);

				} else {

					vp = new VoProducer(shopId, userId, pc, pm);

					logger.debug("Producer " + vp + " added to " + voShop);
				}
				pm.makePersistent(vp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public List<ProductCategory> uploadProductCategoies(List<ProductCategory> categories, boolean cleanShopBeforeUpload) throws InvalidOperation {

		Map<Long, VoProductCategory> categoresCacheMap = new HashMap<Long, VoProductCategory>();

		PersistenceManager pm = PMF.getPm();

		List<ProductCategory> categoriesCreated = new ArrayList<ProductCategory>();

		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Product categories. SHOP ID is not set in session context.");
		}

		try {
			long impordedId;

			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());

			for (ProductCategory pc : categories) {

				VoProductCategory vpc = VoProductCategory.getByImportId(shopId, impordedId = pc.getId(), pm);

				VoProductCategory vppc = null;
				if (0 != pc.getParentId()) {

					vppc = categoresCacheMap.containsKey(pc.getParentId()) ? categoresCacheMap.get(pc.getParentId()) : VoProductCategory.getByImportId(shopId,
							pc.getParentId(), pm);

					if (null == vppc) {

						String err = "No category found by shopId:" + shopId + " parentId(importedId):" + pc.getParentId() + "\nCurrent categories are:";

						for (VoProductCategory pce : pm.getExtent(VoProductCategory.class)) {
							if (pce.getShopId() == shopId && pce.getImportId() == pc.getParentId()) {

								vppc = pce;
							}
							err += "\n\t" + pce;
						}

						if (vppc == null) {

							logger.warn(err);
							throw new InvalidOperation(VoError.IncorrectParametrs, "parent Id " + pc.getParentId()
									+ " not found as Id of categories above in a list provided");
						} else {
							while (null == (vppc = VoProductCategory.getByImportId(shopId, pc.getParentId(), pm)))
								;
							logger.warn("It sounds like index is broken. Category found by one-by-one search! Because " + err);
						}

					}

					pc.setParentId(vppc.getId());
				}

				if (vpc != null) {
					pc.setId(vpc.getId());
					pc.setParentId(null == vppc ? 0 : vppc.getId());
					vpc.update(pc, 0, pm);
				} else {
					logger.debug("Use parent category " + pc.getParentId());
					vpc = new VoProductCategory(voShop, pc.getId(), pc.getParentId(), pc.getName(), pc.getDescr(), pc.getLogoURLset(), pc.getTopicSet(),
							voShop.getOwnerId(), pm);

					pc.setId(vpc.getId());
					logger.debug("Category " + vpc + " added to " + voShop);
				}

				pm.makePersistent(vpc);
				categoresCacheMap.put(impordedId, vpc);

				pm.flush();
				categoriesCreated.add(vpc.getProductCategory());
			}
			pm.makePersistent(voShop);
			
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shopId));

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. " + e);
		} finally {
			pm.close();
		}
		return categoriesCreated;
	}

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
	@Override
	public List<Producer> getProducers() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			List<VoProducer> vpl = (List<VoProducer>) pm.newQuery(VoProducer.class, "shopId == " + shopId).execute();
			List<Producer> pl = new ArrayList<Producer>();
			for (VoProducer vp : vpl)
				pl.add(vp.createProducer());

			return pl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProducers for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			pm.getObjectById(VoShop.class, shopId);
			List<ProductCategory> lpc = new ArrayList<ProductCategory>();

			List<VoProductCategory> pcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class,
					"parentId == " + currentProductCategoryId + " && shopId == " + shopId).execute();
			for (VoProductCategory voProductCategory : pcl) {
				if (voProductCategory.getProductCount() > 0)
					lpc.add(voProductCategory.getProductCategory());
			}
			return lpc;

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProductCategories for shopId=" + shopId + " currentProductCategoryId="
					+ currentProductCategoryId + "." + e);
		} finally {
			pm.close();
		}
	}

	private SortedSet<Product> getProductsFromCategory(Long categoryId, Long shopId, PersistenceManager pm) {
		SortedSet<Product> rslt = new TreeSet<Product>(new ProdcutNameComparator());
		List<VoProductCategory> vpcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class,
				"shopId == " + shopId + " && parentId == " + categoryId).execute();
		for (VoProductCategory cat : vpcl) {
			rslt.addAll(getProductsFromCategory(cat.getId(), shopId, pm));
		}
		List<VoProduct> vpl = (List<VoProduct>) pm.newQuery(VoProduct.class, "categories == " + categoryId).execute();

		for (VoProduct product : vpl) {
			rslt.add(product.getProduct());
		}
		return rslt;
	}

	// ======================================================================================================================
	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation {

		if (offset < 0 || length < 1)
			throw new InvalidOperation(VoError.IncorrectParametrs, "offset must be >= 0 and length > 0 ");

		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			String key = getProcutsOfCategoryCacheKey(categoryId, shopId);
			ArrayList<Product> products = ServiceImpl.getObjectFromCache(key);

			if (null == products) { // no data in cache

				products = new ArrayList<Product>();
				products.addAll(getProductsFromCategory(categoryId, shopId, pm));
				try {
					putObjectToCache(key, products);
				} catch (Exception e) {
					logger.warn("FAiled to put product list ti the cache. " + e.getMessage());
					e.printStackTrace();
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

	private static String getProcutsOfCategoryCacheKey(long categoryId, Long shopId) {
		return "VoProductsForCategory:" + shopId + ":" + categoryId;
	}

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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

	// ======================================================================================================================
	@Override
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
				pm.retrieve(voOrder);
				return voOrder.getOrderDetails(pm);
			}
			throw new InvalidOperation(VoError.GeneralError, "Not found");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + orderId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public long createOrder(int date, String comment, PriceType priceType) throws InvalidOperation {
		if (date < System.currentTimeMillis() / 1000L)
			throw new InvalidOperation(VoError.IncorrectParametrs, "Order could not be created for the past");

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = getCurrentShop(pm);
			pm.retrieve(shop);
			Collection<DateType> dateTypes = shop.getDates(date, date + 1).values();
			boolean NEXT_ORDERfound = false;
			for (DateType dt : dateTypes) {
				if (DateType.NEXT_ORDER == dt) {
					NEXT_ORDERfound = true;
					break;
				}
			}

			if (!NEXT_ORDERfound)
				throw new InvalidOperation(VoError.ShopNotOrderDate, "The date is not avialable for order");

			VoUser user = getCurrentUser(pm);
			VoOrder voOrder = new VoOrder(user, shop, date, priceType, comment, pm);

			long id = voOrder.getId();

			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), id, pm);
			return id;
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public long cancelOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setStatus(OrderStatus.CANCELED);
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.makePersistent(currentOrder);
			//recalculate delivery cost for all other orders that gona be delivered to the same address for the user
			updateDeliveryCost(pm.getObjectById(VoShop.class, currentOrder.getShopId()), currentOrder, null, pm);
			return currentOrder.getId();
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public long deleteOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
			if (null != currentOrder.getOrderLines() && 0 != currentOrder.getOrderLines().size()) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "Only an ampry order could be deleted!");
			}
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.deletePersistent(currentOrder);
			return 0;
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public long confirmOrder( long orderId ) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setStatus(OrderStatus.CONFIRMED);
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
			pm.makePersistent(currentOrder);
			return currentOrder.getId();
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	/**
	 * Method adds all orderLines from order with id set in parameter to current
	 * order. All Lines with the same product ID would summarized!
	 **/
	@Override
	public OrderDetails appendOrder(long orderId, long oldOrderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				double addCost = 0;
				VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
				
				Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();

				if (currentOdrerLines.isEmpty()) {
					for (Long voOrderLineId : voOrder.getOrderLines().values()) {

						VoOrderLine voOrderLine = pm.getObjectById(VoOrderLine.class, voOrderLineId);
						VoProduct voProduct = pm.getObjectById(VoProduct.class, voOrderLine.getProductId());
						double price = voProduct.getPrice(currentOrder.getPriceType());

						VoOrderLine newOrderLine = new VoOrderLine(currentOrder, voProduct, voOrderLine.getQuantity(), price, voOrderLine.getComment(),
								voOrderLine.getPackets());
						pm.makePersistent(newOrderLine);

						currentOdrerLines.put(voOrderLine.getProductId(), newOrderLine.getId().getId());
						addCost += voOrderLine.getQuantity() * price;
					}

				} else {

					for (Long voOrderLineId : voOrder.getOrderLines().values()) {

						VoOrderLine voOrderLine = pm.getObjectById(VoOrderLine.class, voOrderLineId);
						VoProduct voProduct = pm.getObjectById(VoProduct.class, voOrderLine.getProductId());

						double price = voProduct.getPrice(currentOrder.getPriceType());
						long pid = voOrderLine.getProductId();

						if (currentOdrerLines.containsKey(pid)) {

							VoOrderLine currentOL = pm.getObjectById(VoOrderLine.class, currentOdrerLines.get(pid));
							currentOL.setQuantity(currentOL.getQuantity() + voOrderLine.getQuantity());
							VoProduct curProduct = pm.getObjectById(VoProduct.class, currentOL.getProductId());

							// merge packets for prepack product
							if (curProduct.isPrepackRequired()) {
								mergeOrderLinePackets(voOrderLine, currentOL);
							}
							pm.makePersistent(currentOL);

						} else {
							VoOrderLine newOrderLine = new VoOrderLine(currentOrder, pm.getObjectById(VoProduct.class, voOrderLine.getProductId()),
									voOrderLine.getQuantity(), price, voOrderLine.getComment(), voOrderLine.getPackets());

							pm.makePersistent(newOrderLine);

							currentOdrerLines.put(voOrderLine.getProductId(), newOrderLine.getId().getId());
						}
						addCost += voOrderLine.getQuantity() * price;
					}
				}
				currentOrder.addCost(addCost);
				pm.makePersistent(currentOrder);
				return currentOrder.getOrderDetails(pm);// addCost;
			}
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + oldOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	public void mergeOrderLinePackets(VoOrderLine voOrderLine, VoOrderLine currentOL) {
		Map<Double, Integer> cpm = currentOL.getPackets();
		Map<Double, Integer> nol = voOrderLine.getPackets();

		if (null == cpm && null == nol) {
			HashMap<Double, Integer> pMap = new HashMap<Double, Integer>();
			if (currentOL.getQuantity() == voOrderLine.getQuantity())
				pMap.put(currentOL.getQuantity(), 2);
			else {
				pMap.put(currentOL.getQuantity(), 1);
				pMap.put(voOrderLine.getQuantity(), 1);
			}
			currentOL.setPackets(pMap);

		} else if (null != cpm && null == nol) {

			cpm.put(voOrderLine.getQuantity(), cpm.containsKey(voOrderLine.getQuantity()) ? cpm.get(voOrderLine.getQuantity()) + 1 : 1);
		} else if (null == cpm && null != nol) {

			HashMap<Double, Integer> pMap = new HashMap<Double, Integer>();
			pMap.putAll(nol);
			if (pMap.containsKey(currentOL.getQuantity()))
				pMap.put(currentOL.getQuantity(), pMap.containsKey(currentOL.getQuantity()) ? pMap.get(currentOL.getQuantity()) + 1 : 1);
			currentOL.setPackets(pMap);
		} else {
			for (Entry<Double, Integer> npe : nol.entrySet()) {
				cpm.put(npe.getKey(), cpm.containsKey(npe.getKey()) ? cpm.get(npe.getKey()) + npe.getValue() : npe.getValue());
			}
		}

	}

	// ======================================================================================================================
	/**
	 * Method adds to current order lines for products that are not included to
	 * current order
	 **/
	@Override
	public OrderDetails mergeOrder(long orderId, long oldOrderId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (Long oldLineId : voOrder.getOrderLines().values()) {
					VoOrderLine oldLine = pm.getObjectById(VoOrderLine.class, oldLineId);
					if (!currentOdrerLines.containsKey(oldLine.getProductId())) {
						// there is no such product in the current order
						VoProduct oldOrderLineProduct = pm.getObjectById(VoProduct.class, oldLine.getProductId());
						Double price = oldOrderLineProduct.getPrice(currentOrder.getPriceType());
						// Product is detached member so the price stored in this object
						// would be actual
						VoOrderLine voOrderLine = new VoOrderLine(currentOrder, oldOrderLineProduct, oldLine.getQuantity(), price, oldLine.getComment(),
								oldLine.getPackets());
						pm.makePersistent(voOrderLine);
						currentOdrerLines.put(oldLine.getProductId(), voOrderLine.getId().getId());
						currentOrder.addCost(price * oldLine.getQuantity());
					}
				}
				pm.makePersistent(currentOrder);
				return voOrder.getOrderDetails(pm);
			}
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + oldOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public OrderLine setOrderLine(long orderId, long productId, double quantity, String comment, Map<Double, Integer> packs) throws InvalidOperation {
		if (0 == quantity) {
			removeOrderLine(orderId, productId);
			return null;
		}
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
	
			double price = voProduct.getPrice(currentOrder.getPriceType());

			Map<Double, Integer> packsRounded;
			if (null == packs)
				packsRounded = null;
			else {
				quantity = VoHelper.roundDouble(quantity, QUANTITY_SCALE);
				packsRounded = new HashMap<Double, Integer>();
				for (Entry<Double, Integer> e : packs.entrySet()) {
					packsRounded.put(VoHelper.roundDouble(e.getKey(), QUANTITY_SCALE), e.getValue());
				}
			}

			VoOrderLine theLine = new VoOrderLine(currentOrder, voProduct, quantity, price, comment, packsRounded);
			pm.makePersistent(theLine);

			int weightDiff;
			Long oldLineId = currentOdrerLines.put(productId, theLine.getId().getId());
			if (null != oldLineId) {
				VoOrderLine oldLine = pm.getObjectById(VoOrderLine.class, oldLineId);
				currentOrder.addCost(quantity * price - oldLine.getPrice() * oldLine.getQuantity());
				weightDiff = (int)(( quantity - oldLine.getQuantity() )* voProduct.getWeight()); 
			} else {
				currentOrder.addCost(quantity * price);
				weightDiff = (int) (quantity * voProduct.getWeight());
			}
			VoShop voShop = pm.getObjectById(VoShop.class, currentOrder.getShopId());
			Map<Integer, Integer> deliveryByWeightIncrement = voShop.getDeliveryByWeightIncrement();
			Integer oldWeight = currentOrder.getWeightGramm();
			currentOrder.setWeightGramm( oldWeight + weightDiff );
			
			if( null!=deliveryByWeightIncrement && DeliveryType.SELF_PICKUP != currentOrder.getDelivery()  &&
					//check if order line breaks the weight step 
					!increaseDeliveryForWeight(voShop, oldWeight ).equals(increaseDeliveryForWeight(voShop, weightDiff + oldWeight ))){ 
					
				updateDeliveryCost(voShop, currentOrder, null, pm);
			}
			
			pm.makePersistent(currentOrder);
			return theLine.getOrderLine(pm);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to addOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public boolean removeOrderLine(long orderId, long productId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
			Long removedLineID = currentOdrerLines.remove(productId);
			if (null == removedLineID)
				throw new InvalidOperation(VoError.IncorrectParametrs, "No order line found for product id=" + productId);

			VoOrderLine removedLine = pm.getObjectById(VoOrderLine.class, removedLineID);
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
			
			currentOrder.addCost(-removedLine.getPrice() * removedLine.getQuantity());
			currentOrder.setWeightGramm( currentOrder.getWeightGramm() - (int)(removedLine.getQuantity() * voProduct.getWeight()));
			pm.makePersistent(currentOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to removeOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public OrderDetails setOrderDeliveryType(long orderId, DeliveryType deliveryType, PostalAddress pa) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress oldDeliveryTo = currentOrder.getDeliveryTo();
			VoPostalAddress newDeliveryTo = null == pa ? null : new VoPostalAddress(pa, pm);
			
			if( deliveryType != DeliveryType.SELF_PICKUP && null == newDeliveryTo )
				newDeliveryTo = currentOrder.getUser().getAddress(); //Home address as default
			
			//look if something significant is changed
			if (deliveryType != currentOrder.getDelivery() || //type changed 
					deliveryType != DeliveryType.SELF_PICKUP && oldDeliveryTo.getId() != newDeliveryTo.getId()) { //or address changed
				
				VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId(pm));

				currentOrder.setDelivery(deliveryType);
				
				if (deliveryType == DeliveryType.SELF_PICKUP) {
					currentOrder.setDeliveryTo(voShop.getAddress());
					
				} else if( null!=newDeliveryTo ) {
					currentOrder.setDeliveryTo(newDeliveryTo);
						
				} else {
					throw new InvalidOperation(VoError.IncorrectParametrs, "No delivery to address set and the user has no Home addreee defined to usse as delivery to");
					
				}
				
				pm.makePersistent(currentOrder); //save changes before analyze all orders of the user
				updateDeliveryCost(voShop, currentOrder, oldDeliveryTo, pm);

			}
			return currentOrder.getOrderDetails(pm);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryType=" + deliveryType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}
	// ======================================================================================================================
	// Calculate delivery costs according to:
	// One user can have several orders that should be delivered together and pay only once
	// delivery cost depend on distance, address substring and weight
	private double updateDeliveryCost(VoShop shop, VoOrder order, VoPostalAddress oldAddress, PersistenceManager pm) throws InvalidOperation {
		
		double newDeliveryCost = 0;
		VoPostalAddress newAddress = order.getDeliveryTo();
		
		//check if update required
		if( null == newAddress &&  null == oldAddress || 
				null != newAddress &&  null != oldAddress &&
				newAddress.getId() == oldAddress.getId() && 
				order.getDelivery() != DeliveryType.SELF_PICKUP) //nothing changed
			return order.getDeliveryCost();
		
		
		//collect all orders of the same user and the same date and shop 
		List<VoOrder> orders = (List<VoOrder>) pm.newQuery(VoOrder.class, "shopId==" + order.getShopId() + 
				" && date == " + order.getDate() +
				" && user == " + order.getUser().getId() +
				" && ( status == '" + OrderStatus.NEW + "' || status == '" + OrderStatus.CONFIRMED + "')" ).execute();
		
		if( null==orders || 0==orders.size()) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to Calculate Delivery cost. No order Found but at least one must exists!");
		}
		//orders that stores the delivery cost of old and new delivery group
		VoOrder voOrderWithDelivery = null, voOrderWithDeliverySet = null;
		VoOrder voOldOrderWithDelivery = null, voOldOrderWithDeliverySet = null;
		
		int totalWeight = 0, totalOldWeight = 0;
		
		for (VoOrder nextOrder : orders) { //collect total weight of orders for new and for old address and select an order to apply delivery cost to.
			
			double orderDeliveryCost = nextOrder.getDeliveryCost();
			
			if( DeliveryType.SELF_PICKUP == nextOrder.getDelivery()){ 
				
				if( orderDeliveryCost != 0 ){ //update delivery cost and total cost
					nextOrder.setDeliveryCost(0);
					nextOrder.addCost( -orderDeliveryCost );
					pm.makePersistent(nextOrder);
					continue;
				} 
	
			//combine delivery to new address
			} else if( null != nextOrder.getDeliveryTo() && null!= newAddress &&
					nextOrder.getDeliveryTo().getId() == newAddress.getId() &&
					order.getDelivery() != DeliveryType.SELF_PICKUP ){ //don't take delivery address into account if it's self pickup 
			
				totalWeight += nextOrder.getWeightGramm();
				
				if(null == voOrderWithDelivery) 
					voOrderWithDelivery = nextOrder; //remember an order to set delivery cost to if no one set
				
				if( 0 != nextOrder.getDeliveryCost() ) 
					if( null==voOrderWithDeliverySet )
						voOrderWithDeliverySet = nextOrder; //remember an order with delivery cost is already set to update it
					else {//it's the second order with delivery set, so it should be removed
						nextOrder.setDeliveryCost(0.0D);
						nextOrder.addCost( -orderDeliveryCost );
					}
						

			//old delivery group 
			} else if( null != nextOrder.getDeliveryTo() && null!= oldAddress && 
					null!= oldAddress && nextOrder.getDeliveryTo().getId() == oldAddress.getId() ){ 
				
				totalOldWeight += nextOrder.getWeightGramm();
				
				if(null == voOldOrderWithDelivery) 
					voOldOrderWithDelivery = nextOrder; //remember an order to set delivery cost to if no one set
				
				if(0 != nextOrder.getDeliveryCost() ) 
					if( null!=voOldOrderWithDeliverySet ){//it never could happens, but it better to check
						nextOrder.setDeliveryCost(0.0D);
						nextOrder.addCost( -orderDeliveryCost );
						
					} else
						voOldOrderWithDeliverySet = nextOrder; //remember an order with delivery cost is set to update it
			}
		} 
		
		//update cost of delivery to new address 
		if( null == voOrderWithDeliverySet )  //no orders with delivery cost was set so the first one would be used to charge delivery fee
			voOrderWithDeliverySet = voOrderWithDelivery;
		
		
		if( DeliveryType.SELF_PICKUP != order.getDelivery() )
			newDeliveryCost = setDeliveryGroupDeliveryCost(voOrderWithDeliverySet, shop, totalWeight, pm);
		else
			newDeliveryCost = 0;
		
			
		if( null != oldAddress && null != voOldOrderWithDelivery )
			setDeliveryGroupDeliveryCost(voOldOrderWithDeliverySet == null ? voOldOrderWithDelivery : voOldOrderWithDeliverySet, shop, totalOldWeight, pm);
		
		return newDeliveryCost;
	}

//======================================================================================================================
	
	private double setDeliveryGroupDeliveryCost(VoOrder order, VoShop voShop, int totalWeight, PersistenceManager pm) {
		
		Map<Integer, Double> deliveryCosts = voShop.getDeliveryCosts(); //use as a base cost
		Double newDeliveryCost = null == deliveryCosts ? 0.0D : 
			deliveryCosts.get( order.getDelivery().getValue() );
		
		if( null == newDeliveryCost )
			newDeliveryCost = 0D;
		
		 VoPostalAddress deliveryTo = order.getDeliveryTo();
		 
		 int distance = -1;
		 String addressMathces = null;
		//get delivery cost by address mask if set
		Map<DeliveryType, String> dam = voShop.getDeliveryAddressMasksText();
		if( null!=dam && null!=deliveryCosts ){ //look for delivery cost by address mask
			String addressString = deliveryTo.getAddressText(pm);
			
			//sort the map by DelivertType to math cheapest delivery first
			SortedMap<DeliveryType, String> sm = new TreeMap<DeliveryType, String>(dam);
			
			for( Entry<DeliveryType, String> dame: sm.entrySet()) {
				if( addressString.matches( dame.getValue() )){
					addressMathces = dame.getKey().name();
					Double ndc = deliveryCosts.get(dame.getKey().getValue());
					if( null!=ndc) { //if there is a cost set for this kind of delivery 
						newDeliveryCost = ndc; 
						order.setDelivery(dame.getKey());
						break;
					}
				}
			}
			
		} else {//check if there is a delivery cost by range is set
			VoPostalAddress shopAddress; 
			
			if(deliveryTo != null && null!=(shopAddress = voShop.getAddress())){
				Map<Integer, Double> dcbd = voShop.getDeliveryCostByDistance();
				if( null != dcbd ) {
				
					//sort the map by DelivertType to math cheapest delivery first
					SortedMap<Integer, Double> sm = new TreeMap<Integer, Double>(dcbd);
					distance = shopAddress.getDistance( deliveryTo ).intValue();
					SortedMap<Integer, Double> minDistMap = sm.headMap( distance );
					if( minDistMap.size() > 0 ) {
					//get the cost of the biggest distance that is less then distance between the shop and delivery to address
						newDeliveryCost += minDistMap.get( minDistMap.lastKey());
					}
				}
			}
		}
		
		//calculate change of cost depend on weight
		newDeliveryCost += increaseDeliveryForWeight(voShop, totalWeight);
		order.setDeliveryCost(newDeliveryCost);
		order.addCost(newDeliveryCost);
		logger.info("New delivery fee "+newDeliveryCost+" for order:"+order.getId()+" for weight:" + totalWeight +
				(distance!=-1?" distance "+distance+"km":"")+
				(null!=addressMathces ? " matches by address text to "+addressMathces:""));
		return newDeliveryCost;
	}
//======================================================================================================================
	private Double increaseDeliveryForWeight(VoShop voShop, int totalWeight) {
		Map<Integer, Integer> dbwi = voShop.getDeliveryByWeightIncrement();
		if( null!=dbwi ){
			SortedMap<Integer,Integer> dbwis = new TreeMap<Integer,Integer>(dbwi);
			SortedMap<Integer, Integer> headMap = dbwis.headMap(totalWeight);
			if( 0 != headMap.size() ) {
				//increment the cost to value of how much times repeated the value 
				return (double) (headMap.get( headMap.lastKey() ) * ( totalWeight / headMap.lastKey())); 
			}
		}
		return 0D;
	}

	// ======================================================================================================================
	@Override
	public boolean setOrderPaymentType(long orderId, PaymentType paymentType) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

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

	// ======================================================================================================================
	@Override
	public OrderDetails setOrderDeliveryAddress(long orderId, PostalAddress deliveryAddress) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress adrress = new VoPostalAddress(deliveryAddress, pm);
			VoPostalAddress oldAddress = currentOrder.getDeliveryTo();
			
			currentOrder.setDeliveryTo(adrress);
			pm.makePersistent(currentOrder);
			
			updateDeliveryCost( pm.getObjectById(VoShop.class, currentOrder.getShopId()), 
					currentOrder, oldAddress, pm);
			
			VoUser currentUser = getCurrentUser(pm);
			currentUser.addPostalAddress(adrress);
			pm.makePersistent(currentUser);
			return currentOrder.getOrderDetails(pm);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryAddress to " + deliveryAddress + e);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setPaymentStatus(newStatus);
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
				Map<Long, Long> odrerLines = voOrder.getOrderLines();
				double costChange = 0;
				for (Long olid : odrerLines.values()) {
					// check if update make sense on the current order line for order's
					// kinda price type
					VoOrderLine ol = pm.getObjectById(VoOrderLine.class, olid);
					if (newPricesMap.containsKey(ol.getProductId()) && newPricesMap.get(ol.getProductId()).containsKey(voOrder.getPriceType())) {
						double oldPrice = ol.getPrice(), newPrice = newPricesMap.get(ol.getProductId()).get(voOrder.getPriceType());
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

	// ======================================================================================================================
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

	// ======================================================================================================================
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
		return getOrdersByStatus( 0, dateFrom, dateTo, status);
	}

	// ======================================================================================================================

	@Override
	public List<Order> getMyOrdersByStatus(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		return getOrdersByStatus( getCurrentUserId(), dateFrom, dateTo, status);
	}
	// ======================================================================================================================
	
	private List<Order> getOrdersByStatus(long userId, int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = getCurrentShopId(pm);
		try {
			Query pcq = pm.newQuery(VoOrder.class);
			List<VoOrder> ps;
			if( 0!=userId){
				pcq.setFilter("user == :key && shopId == " + shopId + " && date >= " + dateFrom + 
						(status != OrderStatus.UNKNOWN ? " && status == '" + status + "'": ""));
				ps = (List<VoOrder>) pcq.execute(userId, dateFrom);
			} else  {
				pcq.setFilter("shopId == " + shopId + " && date >= " + dateFrom + 
						(status != OrderStatus.UNKNOWN ? " && status == '" + status + "'": ""));
				ps = (List<VoOrder>) pcq.execute(dateFrom);
			}
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
			//e.printStackTrace();
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
			long cuid = getCurrentUserId(pm);
			vop.update(newInfoWithOldId, cuid, pm);
			for( Long catId: vop.getCategories())
				removeObjectFromCache(ShopServiceImpl.getProcutsOfCategoryCacheKey(catId, vop.getShopId()));
			
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(vop.getShopId()));
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
			long cuid = getCurrentUserId(pm);
			vos.update(newShopWithOldId, cuid, true, pm);
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
		updateCategory(newCategoryInfo, null);
	}

	public void updateCategory(ProductCategory newCategoryInfo, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoProductCategory vopc = pm.getObjectById(VoProductCategory.class, newCategoryInfo.getId());
			long cuid = getCurrentUserId(pm);
			vopc.update(newCategoryInfo, cuid, pm);
			pm.makePersistent(vopc);
			
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(vopc.getShopId()));
			
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update CAtegory: " + e.getMessage());
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	// ======================================================================================================================
	public void updateProducer(Producer newInfoWithOldId, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoProducer vopc = pm.getObjectById(VoProducer.class, newInfoWithOldId.getId());
			long cuid = getCurrentUserId(pm);
			vopc.update(newInfoWithOldId, cuid, true, pm);
			pm.makePersistent(vopc);
		} catch (Exception e) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to update shop: " + e.getMessage());
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	@Override
	public void updateProducer(Producer newInfoWithOldId) throws InvalidOperation {
		updateProducer(newInfoWithOldId, null);
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
				this.<ShopDescription> importInformation(ie, ExchangeFieldType.SHOP_ID, new ShopDescription(), new ImportDataProcessor<ShopDescription>() {
					@Override
					public void process(List<ShopDescription> list) throws InvalidOperation {
						processUpdateShops(list);
					}
				});
			}
				break;
			case IMPORT_PRODUCTS: {
				this.<ProductDescription> importInformation(ie, ExchangeFieldType.PRODUCT_ID, new ProductDescription(),
						new ImportDataProcessor<ProductDescription>() {
							@Override
							public void process(List<ProductDescription> list) throws InvalidOperation {
								processUpdateProducts(list);
							}
						});
			}
				break;
			case IMPORT_PRODUCERS: {
				this.<ProducerDescription> importInformation(ie, ExchangeFieldType.PRODUCER_ID, new ProducerDescription(),
						new ImportDataProcessor<ProducerDescription>() {
							@Override
							public void process(List<ProducerDescription> list) throws InvalidOperation {
								processUpdateProducers(list);
							}
						});
			}
				break;
			case IMPORT_CATEGORIES: {

				importInformation(ie, ExchangeFieldType.CATEGORY_ID, new CategoryDesrciption(), new ImportDataProcessor<CategoryDesrciption>() {
					@Override
					public void process(List<CategoryDesrciption> list) throws InvalidOperation {
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
		Map<Integer, String> fieldsMap = FieldTranslator.Translate(idField.getValue(), ie.getFieldsMap(), descriptionObject);
		String dataUrl = ie.getUrl();
		byte[] csvData;
		if (null != dataUrl) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				StorageHelper.getFile(dataUrl, baos);
				baos.close();
			} catch (IOException e) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read data from URL:" + dataUrl + ". " + e.getLocalizedMessage());
			}
			csvData = baos.toByteArray();
		} else {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read data from URL:" + dataUrl);
		}

		try {
			// ByteArrayInputStream dataStream = new ByteArrayInputStream(csvData);
			List<T> infoRows = CSVHelper.loadCSVData(csvData, fieldsMap, descriptionObject);

			processor.process(infoRows);

			// Prepare new CSV data
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (!fieldsMap.containsValue("id")) {// insert ID As a last field if
																						// was not set
				fieldsMap.put(fieldsMap.size(), "id");
			}
			CSVHelper.writeCSVData(baos, fieldsMap, infoRows, null);
			baos.close();
			String newURL = StorageHelper.replaceImage(baos.toString(), dataUrl, 0, null, null);
			ie.setUrl(newURL);

		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read Update " + e.getMessage());
		}
	}

	// ======================================================================================================================
	private void processUpdateProducers(List<ProducerDescription> producerRows) throws InvalidOperation {

		ArrayList<Producer> pcl = new ArrayList<Producer>();
		VoHelper.convertMutableSet(producerRows, pcl, new Producer());
		this.uploadProducers(pcl, false); // do not delete producers
	}

	// ======================================================================================================================
	private void processUpdateShops(List<ShopDescription> shopRows) throws InvalidOperation {
		ArrayList<Shop> pcl = new ArrayList<Shop>();
		VoHelper.convertMutableSet(shopRows, pcl, new Shop());
		logger.warn("SHOPS could not be uploaded, all of them must be created mutualy!");
		// this.uploadShops(pcl, false);
	}

	// ======================================================================================================================

	protected void processUpdateCategories(List<CategoryDesrciption> caregoryROws) throws InvalidOperation {
		ArrayList<ProductCategory> pcl = new ArrayList<ProductCategory>();
		VoHelper.convertMutableSet(caregoryROws, pcl, new ProductCategory());
		this.uploadProductCategoies(pcl, false); // do not delete categories
	}

	// ======================================================================================================================
	private void processUpdateProducts(List<ProductDescription> productROws) throws InvalidOperation {
		ArrayList<FullProductInfo> pcl = new ArrayList<FullProductInfo>();
		VoHelper.convertMutableSet(productROws, pcl, new FullProductInfo());
		uploadProducts(pcl, 0, false); // do not delete products
	}

	// ======================================================================================================================
	@Override
	public long registerProduct(FullProductInfo fpi, long shopId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = 0 == shopId ? getCurrentShop(pm) : pm.getObjectById(VoShop.class, shopId);
			return registerProduct(fpi, shop, pm);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	public long registerProduct(FullProductInfo fpi, VoShop _shop, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {
			VoShop shop = _shop == null ? getCurrentShop(pm) : _shop;
			VoProduct product = VoProduct.createObject(shop, fpi, pm);
			for( Long catId: product.getCategories())
				removeObjectFromCache(ShopServiceImpl.getProcutsOfCategoryCacheKey(catId, product.getShopId()));
			
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shop.getId()));
			
			return product.getId();
		} finally {
			if (_pm == null)
				pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public DataSet getTotalOrdersReport(int date, DeliveryType deliveryType, Map<Integer, ExchangeFieldType> orderFields,
			Map<Integer, ExchangeFieldType> orderLineFIelds) throws InvalidOperation {
		
		DataSet ds = new DataSet();
		ds.date = date;
		ds.id = 0;
		ds.name = "TotalOrdersReport";
		
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = getCurrentShop(pm);
			long currentUserId = getCurrentUserId(pm);
			
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + (0 == date ? "" : " && date == " + date)
					+ (deliveryType == DeliveryType.UNKNOWN || null == deliveryType ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();

			OrderLineDescription odInstance = new OrderLineDescription();
			OrderDescription oInstance = new OrderDescription();
			List<OrderDescription> odl = new ArrayList<OrderDescription>();
			List<List<String>> fieldsData = new ArrayList<List<String>>();

			//User ID - Order ID - product ID - Quantity Map
			Map<Long, Map<Long, Map<Long,Double>>> ordersMap = new TreeMap<Long, Map<Long,Map<Long,Double>>>();
			Map<Long,VoProduct> productsList = new TreeMap<Long, VoProduct>();
			Map<Long,VoUser> usersMap = new TreeMap<Long, VoUser>();		
			VoProduct vop;
			
			for (VoOrder voOrder : olist) {
				
				OrderDescription od = new OrderDescription();
				od.orderId = voOrder.getId();
				od.date = new Date( ((long)date) * 1000L ).toString();
				od.status = voOrder.getStatus();
				od.priceType = voOrder.getPriceType();
				od.tatalCost = voOrder.getTotalCost();
				od.createdDate = new Date( ((long)voOrder.getCreatedAt()) * 1000L).toString();
				od.deliveryType = voOrder.getDelivery();
				od.deliveryCost = voOrder.getDeliveryCost();
				VoPostalAddress deliveryTo = voOrder.getDeliveryTo();
				od.deliveryAddress = deliveryTo == null ? null : deliveryTo.getAddressText(pm);
				od.paymentType = voOrder.getPaymentType();
				od.paymentStatus = voOrder.getPaymentStatus();
				od.comment = voOrder.getComment();
				VoUser user = voOrder.getUser();
				od.userId = user.getId();
				od.userName = user.getName() + " " + user.getLastName();
				od.weight = voOrder.getWeightGramm();
				
				ArrayList<OrderLineDescription> oldl = new ArrayList<OrderLineDescription>();
				Map<Long,Double> productQM = new TreeMap<Long, Double>();
				
				if( !usersMap.containsKey(od.userId) ){ 
					ordersMap.put( od.userId, new TreeMap<Long, Map<Long,Double>>());
					usersMap.put(od.userId,user);
				}
				ordersMap.get(od.userId).put(od.orderId, productQM);
				
				if(null!=voOrder.getOrderLines())
					for (Long volId : voOrder.getOrderLines().values()) {
						
						VoOrderLine vol = pm.getObjectById(VoOrderLine.class, volId);
						
						
						if(productsList.containsKey( vol.getProductId() ))
								vop = productsList.get(vol.getProductId());
						else {
								vop = pm.getObjectById(VoProduct.class, vol.getProductId());
								productsList.put(vol.getProductId(), vop);
						}
								
						OrderLineDescription old = new OrderLineDescription();
						old.lineId = vol.getId().getId();
						old.quantity = vol.getQuantity();
						old.orderId = voOrder.getId();
						// TODO optimize count of requests to DB
						old.productId = vol.getProductId();
						old.productName = vop.getName();
						old.producerId = vop.getProducer();
	
						VoProducer vopr = pm.getObjectById(VoProducer.class, old.producerId);
						old.producerName = vopr.getName();
						old.price = vol.getPrice();
						old.comment = vol.getComment();
						if (null != vol.getPackets()) {
							old.packets = new TreeMap<Double, Integer>();
							old.packets.putAll(vol.getPackets());
						}
						oldl.add(old);
			
						productQM.put(old.productId, old.quantity);			
				}
				// collect all order line information
				ByteArrayOutputStream lbaos = new ByteArrayOutputStream();
				ImportElement ordersLinesIE = new ImportElement(ImExType.EXPORT_ORDER_LINES, "order_" + od.orderId + "_lines.csv", orderLineFIelds);
				List<List<String>> lfieldsData = new ArrayList<List<String>>();

				CSVHelper.writeCSVData(lbaos, CSVHelper.getFieldsMap(odInstance, ExchangeFieldType.ORDER_LINE_ID, orderLineFIelds), oldl, lfieldsData);

				ordersLinesIE.setFieldsData(VoHelper.matrixToList(lfieldsData));
				lbaos.close();
				byte[] fileData = lbaos.toByteArray();

				ordersLinesIE.setUrl(StorageHelper.saveImage(fileData, "text/csv", currentUserId, false, pm));

				odl.add(od);
				ds.addToData(ordersLinesIE);
			}
			
			//create orders matrix
			ds.addToData(createFullOrderMatrix(currentUserId, ordersMap, productsList, usersMap, pm));
			
			//add total orders info
			ImportElement ordersIE = new ImportElement(ImExType.EXPORT_ORDERS, "orders.csv", orderFields);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CSVHelper.writeCSVData(baos, CSVHelper.getFieldsMap(oInstance, ExchangeFieldType.ORDER_ID, orderFields), odl, fieldsData);
			ordersIE.setFieldsData( VoHelper.matrixToList(fieldsData) );
			baos.close();
			byte[] fileData2 = baos.toByteArray();
			ordersIE.setUrl(StorageHelper.saveImage(fileData2, "text/csv", currentUserId, false, pm));

			ds.addToData(ordersIE);

			return ds;

		} catch (InvalidOperation ei) {
			ei.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + ei.why);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e);

		}finally {
			pm.close();
		}
	}

	private ImportElement createFullOrderMatrix(long currentUserId, Map<Long, Map<Long, Map<Long, Double>>> ordersMap, Map<Long, VoProduct> productsList,
			Map<Long, VoUser> usersMap, PersistenceManager pm) throws IOException {
		/*
		User ID - Order ID - product ID - Quantity Map
		Map<Long, Map<Long, Map<Long,Double>>> ordersMap = new TreeMap<Long, Map<Long,Map<Long,Double>>>();
		Map<Long,VoProduct> productsList = new TreeMap<Long, VoProduct>();
		Map<Long,VoUser> usersMap = new TreeMap<Long, VoUser>();
		*/
		List<List<String>> productsMatrix = new ArrayList<List<String>>();
		//create line for products
		ArrayList<String> lineOfProductNames = new ArrayList<String>();
		ArrayList<String> lineOfProductVendors = new ArrayList<String>();
		ArrayList<String> lineOfProductId = new ArrayList<String>();
		ArrayList<String> lineOfProductImportId = new ArrayList<String>();
		
		lineOfProductNames.add("");//skip three lines
		lineOfProductNames.add("");
		lineOfProductNames.add("");
		lineOfProductVendors.addAll(lineOfProductNames);
		lineOfProductId.addAll(lineOfProductNames);
		lineOfProductImportId.addAll(lineOfProductNames);
		
		for( VoProduct nextProduct : productsList.values() ){
			lineOfProductNames.add(nextProduct.getName());
			lineOfProductVendors.add(pm.getObjectById(VoProducer.class,nextProduct.getProducer()).getName());
			lineOfProductId.add(""+nextProduct.getId());
			lineOfProductImportId.add(""+nextProduct.getImportId());
		}
		
		productsMatrix.add(lineOfProductVendors);
		lineOfProductVendors.set(0, "VENDOR");
		
		productsMatrix.add(lineOfProductId);
		lineOfProductId.set(0,"INT PRODUCT ID");
		
		productsMatrix.add(lineOfProductImportId);
		lineOfProductImportId.set(0, "PRODUCT ID");
		
		productsMatrix.add(lineOfProductNames);
		lineOfProductNames.set(0, "PRODUCT NAME");
		
		for(  VoUser nextUser : usersMap.values() ){
			int userOrdrersCounter = 1;
			for( Entry<Long, Map<Long,Double>> orderEntry : ordersMap.get(nextUser.getId()).entrySet()){
				List<String> line = new ArrayList<String>();
				
				productsMatrix.add( line ); //create new row
				line.add( "user:"+nextUser.getId() ); //three line of row head
				line.add( nextUser.getName());
				line.add( "order["+userOrdrersCounter+"]:"+orderEntry.getKey() );
				//fill the row
				for( VoProduct nextProduct : productsList.values() ){
					Double quantity = orderEntry.getValue().get( nextProduct.getId() );
					line.add( "" + (quantity == null ? "" : ""+quantity));
				}
				userOrdrersCounter ++;
			}
		}
		productsMatrix = VoHelper.transMatrix(productsMatrix);
		//add orders matrix
		ImportElement ordersMtxIE = new ImportElement(ImExType.EXPORT_ORDERS, "orders_matrix.csv", null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CSVHelper.writeCSV(baos,productsMatrix,null,null,null);
		ordersMtxIE.setFieldsData( VoHelper.matrixToList(productsMatrix) );
		baos.close();
		byte[] fileData = baos.toByteArray();
		ordersMtxIE.setUrl(StorageHelper.saveImage(fileData, "text/csv", currentUserId, false, pm));

		return ordersMtxIE;
	}

	// ======================================================================================================================

	@Override
	public DataSet getTotalProductsReport(int date, DeliveryType deliveryType, Map<Integer, ExchangeFieldType> productFields) throws InvalidOperation {

		DataSet ds = new DataSet();
		ds.date = date;
		ds.id = 0;
		ds.name = "TotalProductsReport";

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = getCurrentShop(pm);
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + " && date == " + date
					+ (deliveryType == DeliveryType.UNKNOWN ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();

			// Products combined by producer
			SortedMap<Long, SortedMap<Long, ProductOrderDescription>> prodDescMap = new TreeMap<Long, SortedMap<Long, ProductOrderDescription>>();

			for (VoOrder voOrder : olist) {

				for (Long volid : voOrder.getOrderLines().values()) {

					// TODO optimize DB requests count
					VoOrderLine vopl = pm.getObjectById(VoOrderLine.class, volid);
					VoProduct product = pm.getObjectById(VoProduct.class, vopl.getProductId());
					VoProducer producer = pm.getObjectById(VoProducer.class, product.getProducer());

					ProductOrderDescription pod;

					if (!prodDescMap.containsKey(producer.getId())) {
						prodDescMap.put(producer.getId(), new TreeMap<Long, ProductOrderDescription>());
					}

					if (!prodDescMap.get(producer.getId()).containsKey(product.getId())) {

						prodDescMap.get(producer.getId()).put(product.getId(), pod = new ProductOrderDescription());
						pod.producerId = producer.getId();
						pod.producerName = producer.getName();
						pod.productId = product.getId();
						pod.productName = product.getName();
						pod.minUnitSize = product.getMinProducerPack();
						pod.orderedQuantity = vopl.getQuantity();
						pod.prepackRequired = product.isPrepackRequired();
						pod.packSize = product.getMinProducerPack();
						pod.deliveryType = deliveryType;
				
					} else {	
						
						pod = prodDescMap.get(producer.getId()).get(product.getId());
						pod.orderedQuantity += vopl.getQuantity();
					}
				
					pod.packQuantity = 0 != product.getMinProducerPack() ? 1 + (int) (pod.orderedQuantity / product.getMinProducerPack()) : 0;					
					pod.restQuantity = ((double) (pod.packQuantity * product.getMinProducerPack() - pod.orderedQuantity));
				}
			}

			ProductOrderDescription pod = new ProductOrderDescription();

			ImportElement fpIE = new ImportElement(ImExType.EXPORT_TOTAL_PRODUCT, "products.csv", productFields);
			ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
			List<List<String>> ffl = new ArrayList<List<String>>();

			long currentUserId = getCurrentUserId(pm);
			for (Entry<Long, SortedMap<Long, ProductOrderDescription>> podme : prodDescMap.entrySet()) {

				SortedMap<Long, ProductOrderDescription> podm = podme.getValue();

				ImportElement pIE = new ImportElement(ImExType.EXPORT_TOTAL_PRODUCT, "product_" + podme.getKey() + ".csv", productFields);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				List<List<String>> fl = new ArrayList<List<String>>();
				List<ProductOrderDescription> podl = new ArrayList<ProductOrderDescription>();
				podl.addAll(podm.values());

				CSVHelper.writeCSVData(baos, CSVHelper.getFieldsMap(pod, ExchangeFieldType.TOTAL_PROUCT_ID, productFields), podl, fl);
				baos.close();
				fbaos.write(baos.toByteArray());
				ffl.addAll(fl);

				pIE.setFieldsData(VoHelper.matrixToList(fl));
				pIE.setUrl(StorageHelper.saveImage(baos.toByteArray(), "text/csv", currentUserId, false, pm));

				ds.addToData(pIE);
			}
			fbaos.close();
			fpIE.setFieldsData(VoHelper.matrixToList(ffl));
			fpIE.setUrl(StorageHelper.saveImage(fbaos.toByteArray(), "text/csv", currentUserId, false, pm));

			ds.addToData(fpIE);

			return ds;

		} catch (Exception e) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e.getMessage());

		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public DataSet getTotalPackReport(int date, DeliveryType deliveryType, Map<Integer, ExchangeFieldType> packFields) throws InvalidOperation {

		DataSet ds = new DataSet();
		ds.date = date;
		ds.id = 0;
		ds.name = "TotalProductsPackReport";

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = getCurrentShop(pm);
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + " && date == " + date
					+ (deliveryType == DeliveryType.UNKNOWN ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();

			// Products combined by pack size required
			SortedMap<Long, SortedMap<Double, ProductOrderDescription>> prodDescMap = new TreeMap<Long, SortedMap<Double, ProductOrderDescription>>();

			for (VoOrder voOrder : olist) {

				for (Long volid : voOrder.getOrderLines().values()) {

					// TODO optimize DB requests count
					VoOrderLine vopl = pm.getObjectById(VoOrderLine.class, volid);
					VoProduct product = pm.getObjectById(VoProduct.class, vopl.getProductId());
					VoProducer producer = pm.getObjectById(VoProducer.class, product.getProducer());

					ProductOrderDescription pod;

					if (!prodDescMap.containsKey(product.getId())) {
						prodDescMap.put(product.getId(), new TreeMap<Double, ProductOrderDescription>());
					}

					if (!product.isPrepackRequired())
						continue; // skip product that does not require prepacking

					Map<Double, Integer> packets = vopl.getPackets();
					if (packets == null) {
						packets = new HashMap<Double, Integer>();
						packets.put(vopl.getQuantity(), 1);
					}
					for (Entry<Double, Integer> pqe : packets.entrySet()) {

						if (prodDescMap.get(product.getId()).containsKey(pqe.getKey())) {

							pod = prodDescMap.get(product.getId()).get(pqe.getKey());
							pod.orderedQuantity += pqe.getKey();
							pod.packQuantity += pqe.getValue();
							continue;
						}
						prodDescMap.get(product.getId()).put(pqe.getKey(), pod = new ProductOrderDescription());
						pod.producerId = producer.getId();
						pod.producerName = producer.getName();
						pod.productId = product.getId();
						pod.productName = product.getName();
						pod.minUnitSize = product.getMinProducerPack();
						pod.orderedQuantity = pqe.getKey();
						pod.prepackRequired = product.isPrepackRequired();
						pod.packSize = pqe.getKey();
						pod.packQuantity = 1;
						pod.deliveryType = deliveryType;
					}
				}
			}

			incapsulatePacketData(packFields, ds, prodDescMap, getCurrentUserId(pm), pm);

			return ds;

		} catch (InvalidOperation e) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e.what.name()+":"+ e.why);

		} catch (Exception e) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e.getMessage());

		} finally {
			pm.close();
		}
	}

	// =====================================================================================================================

	private void incapsulatePacketData(Map<Integer, ExchangeFieldType> packFields, DataSet ds,
			SortedMap<Long, SortedMap<Double, ProductOrderDescription>> prodDescMap, long userId, PersistenceManager pm) throws IOException,
			InvalidOperation {

		ProductOrderDescription pod = new ProductOrderDescription();

		ImportElement fpIE = new ImportElement(ImExType.EXPORT_TOTAL_PRODUCT, "products.csv", packFields);
		ByteArrayOutputStream fbaos = new ByteArrayOutputStream();
		List<List<String>> ffl = new ArrayList<List<String>>();

		for (Entry<Long, SortedMap<Double, ProductOrderDescription>> podme : prodDescMap.entrySet()) {

			SortedMap<Double, ProductOrderDescription> podm = podme.getValue();
			if(podm.size()>0){
				ImportElement pIE = new ImportElement(ImExType.EXPORT_TOTAL_PRODUCT, "product_" + podme.getKey() + ".csv", packFields);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				List<List<String>> fl = new ArrayList<List<String>>();
				List<ProductOrderDescription> podl = new ArrayList<ProductOrderDescription>();
				podl.addAll(podm.values());
	
				CSVHelper.writeCSVData(baos, CSVHelper.getFieldsMap(pod, ExchangeFieldType.TOTAL_PROUCT_ID, packFields), podl, fl);
				baos.close();
				fbaos.write(baos.toByteArray());
				ffl.addAll(fl);
	
				pIE.setFieldsData(VoHelper.matrixToList(fl));
				pIE.setUrl(StorageHelper.saveImage(baos.toByteArray(), "text/csv", userId, false, pm));
	
				ds.addToData(pIE);
			}
		}
		fbaos.close();
		fpIE.setFieldsData(VoHelper.matrixToList(ffl));
		fpIE.setUrl(StorageHelper.saveImage(fbaos.toByteArray(), "text/csv", userId, false, pm));

		ds.addToData(fpIE);
	}

	// ======================================================================================================================
	@Override
	public void updateOrder(long orderId, int date, String comment) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = 0 == orderId ? getCurrentOrder(pm) : pm.getObjectById(VoOrder.class, orderId);
			voOrder.setDate(date);
			voOrder.setComment(comment);
			pm.makePersistent(voOrder);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	@Override
	
	public MatrixAsList parseCSVfile(String url) throws InvalidOperation, TException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StorageHelper.getFile(url, baos);
			baos.close();
			List<List<String>> matrix = CSVHelper.parseCSV(baos.toByteArray(), null, null, null);
			ArrayList list = new ArrayList();
			for( int row = 0; row < matrix.size(); row ++){
				list.addAll(matrix.get(row));
			}
			MatrixAsList mal = new MatrixAsList(matrix.size(), list);
			return mal;
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read data from URL '" + url + "'");
		}
	}

	// ======================================================================================================================

	private static final Set<String> publicMethods = new HashSet<String>(Arrays.asList(new String[] {

	"getShops", "getDates", "getShop", "getProducers", "getProductCategories", "getProducts", "getProductDetails", "getOrders", "getOrdersByStatus",
			"getOrder", "getOrderDetails", "createOrder", "updateOrder", "cancelOrder", "deleteOrder", "confirmOrder", "appendOrder", "mergeOrder",
			"setOrderLine", "removeOrderLine", "setOrderDeliveryType", "setOrderPaymentType", "setOrderDeliveryAddress", "getProductsByCategories"

	}));

	@Override
	public boolean isPublicMethod(String method) {
		return true;// publicMethods.contains(method);
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.SHOP_SI.ordinal();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<IdNameChilds> getProductsByCategories(long shopId) throws InvalidOperation {
		
		ArrayList<IdNameChilds> catwithProcuts = getObjectFromCache( createShopProductsByCategoryKey(shopId));
		
		if( null == catwithProcuts) {
			catwithProcuts = new ArrayList<IdNameChilds>(); 
			Map<Long,List<IdName>> cpm = new TreeMap<Long,List<IdName>>();
			PersistenceManager pm = PMF.getPm();
			try {
				if( 0 == shopId )
						shopId = getCurrentShopId(pm);
				List<VoProduct> products = (List<VoProduct>) pm.newQuery(VoProduct.class, "shopId=="+shopId ).execute();
				for (VoProduct voProduct : products) {
					for(Long catId : voProduct.getCategories()){
						if(!cpm.containsKey(catId)){
							cpm.put(catId, new ArrayList<IdName>());
						}
						cpm.get(catId).add( new IdName( voProduct.getId(), voProduct.getName()));
					}
				}
				//Load all categories and filter them by products
				List<VoProductCategory> vpcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class, "shopId=="+shopId).execute();
				for (VoProductCategory voProductCategory : vpcl) {
					long nextCatId = voProductCategory.getId();
					if(cpm.containsKey(nextCatId)){
						catwithProcuts.add( new IdNameChilds(nextCatId, voProductCategory.getName(), cpm.get(nextCatId)));
					}
				}
			} finally {
				pm.close();
			}
			putObjectToCache( createShopProductsByCategoryKey(shopId), catwithProcuts );
		}
		return catwithProcuts;
	}

	private static Object createShopProductsByCategoryKey(long shopId) {
		return "createShopProductsByCategoryKey"+shopId;
	}
	
	// ======================================================================================================================

	
	@Override
	public void setShopDeliveryByWeightIncrement(long shopId, Map<Integer, Integer> deliveryByWeightIncrement) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop theShop = 0 == shopId ? getCurrentShop( pm ) : pm.getObjectById(VoShop.class, shopId );
			theShop.setDeliveryByWeightIncrement(deliveryByWeightIncrement);
			pm.makePersistent(theShop);
			
		} catch ( JDOObjectNotFoundException onfe ) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:"+shopId);
		} finally {
			pm.close();
		}
	}
	// ======================================================================================================================

	@Override
	public void setShopDeliveryCostByDistance(long shopId, Map<Integer, Double> deliveryCostByDistance) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop theShop = 0 == shopId ? getCurrentShop( pm ) : pm.getObjectById(VoShop.class, shopId );
			theShop.setDeliveryCostByDistance(deliveryCostByDistance);
			pm.makePersistent(theShop);
			
		} catch ( JDOObjectNotFoundException onfe ) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:"+shopId);
		} finally {
			pm.close();
		}
	}
	// ======================================================================================================================

	@Override
	public void setShopDeliveryTypeAddressMasks(long shopId, Map<DeliveryType, String> deliveryTypeAddressMasks) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop theShop = 0 == shopId ? getCurrentShop( pm ) : pm.getObjectById(VoShop.class, shopId );
			theShop.setDeliveryAddressMasksText(deliveryTypeAddressMasks);
			pm.makePersistent(theShop);
			
		} catch ( JDOObjectNotFoundException onfe ) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:"+shopId);
		} finally {
			pm.close();
		}	
	}	
}
