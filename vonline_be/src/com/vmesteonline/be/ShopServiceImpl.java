package com.vmesteonline.be;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.thrift.TException;

import com.vmesteonline.be.access.VoUserAccessBase;
import com.vmesteonline.be.access.VoUserAccessBaseRoles;
import com.vmesteonline.be.access.shop.VoShopAccess;
import com.vmesteonline.be.access.shop.VoShopAccessRoles;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.postaladdress.AddressInfo;
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
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDate;
import com.vmesteonline.be.shop.OrderDetails;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.OrderUpdateInfo;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.shop.ProductListPart;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.shop.ShopFEService;
import com.vmesteonline.be.shop.UserShopRole;
import com.vmesteonline.be.utils.EMailHelper;
import com.vmesteonline.be.utils.VoHelper;

//import com.google.api.client.util.Sets;

public class ShopServiceImpl extends ServiceImpl implements /*
																														 * ShopBOService.Iface
																														 * ,
																														 */ShopFEService.Iface, Serializable {

	public static Logger logger;
	private static int QUANTITY_SCALE = 5;

	static {
		logger = Logger.getLogger(ShopServiceImpl.class.getName());
	}

	// ======================================================================================================================
	private final class ProdcutNameComparator implements Comparator<Product>, Serializable {
		@Override
		public int compare(Product o1, Product o2) {
			return (o1.getName() + o1.getId()).compareTo(o2.getName() + o2.getId());
		}
	}

	private final class ProdcutScoreComparator implements Comparator<VoProduct>, Serializable {
		@Override
		public int compare(VoProduct o1, VoProduct o2) {
			return -Double.compare(o1.getScore(), o2.getScore());
		}
	}

	ShopServiceImpl() {
	}

	public ShopServiceImpl(String sessionId) {
		super(sessionId);
	}

	// ======================================================================================================================
	@Override
	public List<Shop> getShops() throws InvalidOperation {
		List<Shop> shops = new ArrayList<Shop>();
		PersistenceManager pm = getPM();
		try {
			List<VoShop> voshops = (List<VoShop>) pm.newQuery(VoShop.class).execute();// pm.getExtent(VoShop.class);
			for (VoShop vs : voshops)
				shops.add(vs.getShop(pm));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed toget shops." + e);
		} finally {

		}
		return shops;
	}

	// ======================================================================================================================
	@Override
	public Shop getShop(long shopId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			long voShopId;
			if (0 != (voShopId = shopId) || 0 != (voShopId = ShopServiceHelper.getCurrentShopId(this, pm))) {
				VoShop voShop = pm.getObjectById(VoShop.class, voShopId);
				setCurrentAttribute(CurrentAttributeType.SHOP.getValue(), voShop.getId(), pm);
				return voShop.getShop(pm);
			}
			throw new InvalidOperation(VoError.GeneralError, "No shop found by ID:'" + shopId + "' and current ID not set");
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getShop by shopId=" + shopId + "." + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public OrderDate getNextOrderDate(int afterDate) throws InvalidOperation {
		PersistenceManager pm = getPM();
		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDate. SHOP ID is not set in session context.");
		}
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			return voShop.getNextOrderDate(afterDate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getDates for shopId=" + shopId + "." + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public List<OrderDate> getOrderDates(int afterDate, int before) throws InvalidOperation {
		PersistenceManager pm = getPM();
		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDate. SHOP ID is not set in session context.");
		}
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			return voShop.getOrderDates(afterDate, before);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getDates for shopId=" + shopId + "." + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public List<Producer> getProducers() throws InvalidOperation {
		PersistenceManager pm = getPM();
		Long shopId = ShopServiceHelper.getCurrentShopId(this, pm);
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

		}
	}

	// ======================================================================================================================
	@Override
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation {

		PersistenceManager pm = getPM();
		Long shopId = ShopServiceHelper.getCurrentShopId(this, pm);
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

		}
	}

	private List<VoProduct> getProductsFromCategory(Long categoryId, Long shopId, PersistenceManager pm) {
		List<VoProduct> rslt = new ArrayList<VoProduct>();
		List<VoProductCategory> vpcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class,
				"shopId == " + shopId + " && parentId == " + categoryId).execute();
		for (VoProductCategory cat : vpcl) {
			rslt.addAll(getProductsFromCategory(cat.getId(), shopId, pm));
		}
		Query pQuery = pm.newQuery(VoProduct.class, "categories == " + categoryId);
		// pQuery.setOrdering("score DESC");
		rslt.addAll((List<VoProduct>) pQuery.execute());
		return rslt;
	}

	// ======================================================================================================================
	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation {

		if (offset < 0 || length < 1)
			throw new InvalidOperation(VoError.IncorrectParametrs, "offset must be >= 0 and length > 0 ");

		PersistenceManager pm = getPM();
		Long shopId = ShopServiceHelper.getCurrentShopId(this, pm);
		try {
			String key = ShopServiceHelper.getProcutsOfCategoryCacheKey(categoryId, shopId);
			ArrayList<Product> products = ServiceImpl.getObjectFromCache(key);

			if (null == products) { // no data in cache

				products = new ArrayList<Product>();
				List<VoProduct> vpl = getProductsFromCategory(categoryId, shopId, pm);

				Collections.sort(vpl, new ProductCategoryIdComparator(pm));
				
				Set<Long> productIds = new HashSet<Long>();
				for (VoProduct product : vpl) {
					if (!product.isDeleted() && !productIds.contains(product.getId())) {
						products.add(product.getProduct(pm));
						productIds.add(product.getId());
					}
				}

				try {
					putObjectToCache(key, products);
				} catch (Exception e) {
					logger.warning("FAiled to put product list ti the cache. " + e.getMessage());
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

		}
	}

	// ======================================================================================================================

	private static Comparator<VoProduct> productCategoryNameComparator = new Comparator<VoProduct>() {

		@Override
		public int compare(VoProduct o1, VoProduct o2) {
			if (o1.getCategories().size() == 0 || o2.getCategories().size() == 0)
				return Integer.compare(o1.getCategories().size(), o2.getCategories().size());
			int ccomp = Long.compare(o1.getCategories().get(0), o2.getCategories().get(0));
			return ccomp != 0 ? ccomp : o1.getName().compareTo(o2.getName());
		}

	};
	
	// ======================================================================================================================

	private static class ProductCategoryIdComparator implements Comparator<VoProduct> {

		private PersistenceManager pm;
		private Map<Long,Long> importIdMap;
		public ProductCategoryIdComparator( PersistenceManager pm){
			this.pm = pm;
			importIdMap = new HashMap<>();
			List<VoProductCategory> pcs = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class).execute();
			for( VoProductCategory pc: pcs){
				importIdMap.put(pc.getId(), pc.getImportId());
			}
		}
		@Override
		public int compare(VoProduct o1, VoProduct o2) {
			
			List<Long> o1cats = o1.getCategories();
			List<Long> o2cats = o2.getCategories();
			
			if (o1cats.size() == 0 || o2cats.size() == 0)
				return Integer.compare(o1cats.size(), o2cats.size());
			
			int ccomp = Long.compare(getMaxImportId(o1cats), getMaxImportId(o2cats));
			return ccomp != 0 ? ccomp : Long.compare( o1.getImportId(), o2.getImportId());
		}
		
		public long getMaxImportId(List<Long> categoryIdList) {
			long maximpId = 0L;
			for( Long cid : categoryIdList){
				Long nextCiid = importIdMap.get(cid);
				if( null!=nextCiid && nextCiid > maximpId )
					maximpId = nextCiid;
			}
			return maximpId;
		}
	};
	

	// ======================================================================================================================
	@Override
	public ProductDetails getProductDetails(long productId) throws InvalidOperation {
		PersistenceManager pm = getPM();
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

		}
	}

	// ======================================================================================================================
	@Override
	public List<Order> getOrders(int dateFrom, int dateTo) throws InvalidOperation {
		dateFrom -= dateFrom % 86400;
		dateTo += (86400 - dateTo % 86400);

		PersistenceManager pm = getPM();
		Long shopId = ShopServiceHelper.getCurrentShopId(this, pm);
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

		}
	}

	// ======================================================================================================================
	private VoOrder getCurrentOrder(PersistenceManager pm) throws InvalidOperation {

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
		}
	}

	// ======================================================================================================================
	private String generateOrderDetailsKey(long orderId) {
		return "generateOrderDetailsKey:" + orderId;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation {

		String key = generateOrderDetailsKey(orderId);
		OrderDetails od = (OrderDetails) getObjectFromCache(key);
		if (null == od) {
			PersistenceManager pm = getPM();
			try {
				VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
				if (null != voOrder) {
					pm.retrieve(voOrder);
					HashMap<Long, VoProduct> productsMap = getProductMapFromCacheByShopId(pm, voOrder.getShopId());

					od = voOrder.getOrderDetails(productsMap, pm);
					putObjectToCache(key, od);

				} else
					throw new InvalidOperation(VoError.GeneralError, "Not found");
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + orderId);
			} finally {

			}
		}
		return od;
	}

	// ======================================================================================================================
	@Override
	public Order createOrder(int date, String comment) throws InvalidOperation {
		if (0 == date)
			date = getNextOrderDate((int) (System.currentTimeMillis() / 1000L)).orderDate;
		else if (date < System.currentTimeMillis() / 1000L)
			throw new InvalidOperation(VoError.IncorrectParametrs, "Order could not be created for the past");

		PersistenceManager pm = getPM();
		try {
			VoShop shop = ShopServiceHelper.getCurrentShop(this, pm);
			pm.retrieve(shop);
			PriceType pt;
			try {
				pt = shop.getPriceType(date);
			} catch (Exception e) {
				date = getNextOrderDate((int) (System.currentTimeMillis() / 1000L)).orderDate;
				pt = shop.getPriceType(date);
			}

			VoUser user = getCurrentUser(pm);
			VoOrder voOrder = new VoOrder(user, shop, date, pt, comment, pm);

			long id = voOrder.getId();

			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), id, pm);

			removeObjectFromCache(generateOrderDetailsKey(id));

			return voOrder.getOrder();
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public long cancelOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setStatus(OrderStatus.CANCELED);
			// unset current order
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.makePersistent(currentOrder);
			// recalculate delivery cost for all other orders that gona be delivered
			// to the same address for the user
			updateDeliveryCost(pm.getObjectById(VoShop.class, currentOrder.getShopId()), currentOrder, null, pm);
			removeObjectFromCache(generateOrderDetailsKey(orderId));
			return currentOrder.getId();
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public long deleteOrder(long orderId) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);
			if (currentOrder.getStatus() == OrderStatus.CONFIRMED)
				throw new InvalidOperation(VoError.IncorrectParametrs, "Confirmed orders could not be deleted.");

			Map<Long, Long> orderLines = currentOrder.getOrderLines();
			if (null != orderLines && 0 != orderLines.size()) {
				for (Iterator<Entry<Long, Long>> oli = orderLines.entrySet().iterator(); oli.hasNext();) {
					Long olid = oli.next().getValue();
					try{
						pm.deletePersistent(pm.getObjectById(VoOrderLine.class, olid));
					} catch(Exception e){}
				}
			}
			List<VoOrderLine> orderLiness = (List<VoOrderLine>) pm.newQuery(
					VoOrderLine.class,
					"orderId==" + orderId).execute();
			if (!orderLines.isEmpty()){
				for( VoOrderLine ol: orderLiness){
						try{
							pm.deletePersistent(ol);
						} catch(Exception e){}												
				}
			}				
			// unset current order
			if (0 == orderId)
				setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.deletePersistent(currentOrder);
			return 0;
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public long confirmOrder(long orderId, String comment) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);
			moveOrderDateIfExpired(currentOrder, pm);
			currentOrder.setStatus(OrderStatus.CONFIRMED);
			if (null != comment)
				currentOrder.setComment(comment);
			// unset current order
			recalculateOrder(orderId);

			sendConfirmationMessage(currentOrder, pm);
			setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0, pm);
			pm.makePersistent(currentOrder);

			removeObjectFromCache(generateOrderDetailsKey(orderId));

			return currentOrder.getId();
		} finally {

		}
	}

	private void moveOrderDateIfExpired(VoOrder currentOrder, PersistenceManager pm) throws InvalidOperation {
		int now = (int) (System.currentTimeMillis() / 1000L);
		OrderDate nextOrderDate = pm.getObjectById(VoShop.class, currentOrder.getShopId()).getNextOrderDate(now);
		if (nextOrderDate.orderDate % 86400 > now % 86400) {
			currentOrder.setDate(nextOrderDate.orderDate);
		}
	}

	private void sendConfirmationMessage(VoOrder currentOrder, PersistenceManager pm) {

		// create OrderDescriptionHTML

		long shopId = currentOrder.getShopId();
		VoShop shop = pm.getObjectById(VoShop.class, shopId);
		VoUser shopOwner = pm.getObjectById(VoUser.class, shop.getOwnerId());
		VoUser customer = currentOrder.getUser();

		String htmlBody = "<!DOCTYPE html><html><head><style>table, th, td{ border-collapse:collapse;border:1px solid black;}"
				+ "th, td{padding:5px;}</style></head><body>";
		htmlBody += "<h3>Заказ от <a href=\"mailto:" + customer.getEmail() + "\">" + customer.getName() + " " + customer.getLastName() + " "
				+ "</a></h3>";
		htmlBody += "<p>Номер  заказа: " + currentOrder.getId() + " <br/>";
		htmlBody += "Номер клиента: " + customer.getId() + " </p>";

		htmlBody += "<br/>Дата реализации: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date((long) currentOrder.getDate() * 1000L));
		htmlBody += "<br/>Стоимость: " + VoHelper.roundDouble(currentOrder.getTotalCost(), 2) + " руб";
		if (currentOrder.getDeliveryCost() > 0)
			htmlBody += "<br/>Из них доставка: " + currentOrder.getDeliveryCost() + " руб";
		htmlBody += "<br/>Вес: " + currentOrder.getWeightGramm() / 1000 + " кг";
		htmlBody += "<br/>Контактный номер: " + customer.getMobilePhone();
		htmlBody += "<br/>"
				+ (currentOrder.getDelivery() == DeliveryType.SELF_PICKUP ? "Самовывоз" : "Доставка: " + currentOrder.getDeliveryTo().getAddressText(pm));

		String comment = currentOrder.getComment();
		if (null != comment && comment.trim().length() > 0)
			htmlBody += "<br/>Коментарий: " + comment;

		htmlBody += "<br/><table><caption>Состав заказа</caption>";
		htmlBody += "<tr><th>Код товара</th><th>Производитель</th><th>Наименование</th><th>Кол-во</th><th>Развес</th><th>Цена</th><th>Стоимость</th></tr>";
		Map<Long, Long> orderLines = currentOrder.getOrderLines();

		List<VoOrderLine> olines = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class, "orderId==" + currentOrder.getId()).execute();
		HashMap<Long, VoProduct> productsMap = getProductMapFromCacheByShopId(pm, shopId);

		if (null != orderLines)
			for (VoOrderLine orderLine : olines) {

				VoProduct product = productsMap.get(orderLine.getProductId());
				VoProducer producer = pm.getObjectById(VoProducer.class, product.getProducerId());

				htmlBody += "<tr>";
				htmlBody += "<td>" + product.getImportId() + "</td>";
				htmlBody += "<td>" + producer.getName() + "</td>";
				htmlBody += "<td>" + product.getName() + "</td>";
				htmlBody += "<td>" + orderLine.getQuantity() + " " + product.getUnitName() + "</td>";
				if (product.isPrepackRequired() && null != orderLine.getPackets() && orderLine.getPackets().size() > 0) {
					String packets = "";
					for (Entry<Double, Integer> packE : orderLine.getPackets().entrySet())
						packets += packE.getKey() + " x " + packE.getValue() + "; ";
					htmlBody += "<td>" + packets + "</td>";
				} else {
					htmlBody += "<td>-</td>";
				}
				htmlBody += "<td>" + orderLine.getPrice() + "</td>";
				htmlBody += "<td>" + VoHelper.roundDouble(orderLine.getPrice() * orderLine.getQuantity(), 2) + "</td>";
				htmlBody += "</tr>";
			}

		htmlBody += "<tr>" + "<td/><td/><td/><td/><td/><td><b>Итого:</b></td><td><b>"
				+ VoHelper.roundDouble(currentOrder.getTotalCost() - currentOrder.getDeliveryCost(), 2) + "</b></td><td/>";
		htmlBody += "</tr>";
		htmlBody += "</table></html>";

		htmlBody += "<p>Спасибо за ваш заказ!</p>";
		htmlBody += "--</br>" + shop.getName() + "<br/>";
		if (null != shopOwner.getMobilePhone())
			htmlBody += "Вопросы по тел.:" + shopOwner.getMobilePhone() + "<br/>";
		htmlBody += "mailto:" + shopOwner.getEmail() + "<br/>";

		String subject = shop.getName() + " Заказ# " + currentOrder.getId() + " Подтвержден.";
		try {
			EMailHelper.sendSimpleEMail(shopOwner.getEmail(), subject, htmlBody);
		} catch (Exception e) {
			logger.severe("FAiled to send Email : " + subject + " Reason: " + e);
			e.printStackTrace();
		}
		try {
			EMailHelper.sendSimpleEMail(shopOwner.getEmail(), customer.getEmail(), subject, htmlBody);
		} catch (Exception e) {
			logger.severe("FAiled to send Email : " + subject + " Reason: " + e);
			e.printStackTrace();
		}
	}

	// ======================================================================================================================
	/**
	 * Method adds all orderLines from order with id set in parameter to current
	 * order. All Lines with the same product ID would summarized!
	 **/
	@Override
	public OrderDetails appendOrder(long orderId, long oldOrderId) throws InvalidOperation {

		if (orderId == oldOrderId)
			return getOrderDetails(oldOrderId);

		removeObjectFromCache(generateOrderDetailsKey(orderId));
		PersistenceManager pm = getPM();
		try {
			VoOrder voOldOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			HashMap<Long, VoProduct> prdMap = getProductMapFromCacheByShopId(pm, voOldOrder.getShopId());

			{
				double addCost = 0.0;
				double addWeigth = 0.0;
				VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

				moveOrderDateIfExpired(currentOrder, pm);

				Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
				List<VoOrderLine> oldOrderLinesList = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class, "orderId==" + oldOrderId).execute();

				if (currentOdrerLines.isEmpty()) {
					for (VoOrderLine voOrderLine : oldOrderLinesList) {

						VoProduct voProduct = prdMap.get(voOrderLine.getProductId());
						if (-1 != voProduct.getImportId()) {
							double price = voProduct.getPrice(currentOrder.getPriceType());

							VoOrderLine newOrderLine = new VoOrderLine(currentOrder, voProduct, voOrderLine.getQuantity(), price, voOrderLine.getComment(),
									voOrderLine.getPackets());
							pm.makePersistent(newOrderLine);

							currentOdrerLines.put(voOrderLine.getProductId(), newOrderLine.getId().getId());
							addCost += voOrderLine.getQuantity() * price;
							addWeigth += voOrderLine.getQuantity() * voProduct.getWeight();
						}
					}

				} else {
					List<VoOrderLine> currentOrderLinesList = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class, "orderId==" + orderId).execute();

					for (VoOrderLine voOrderLine : oldOrderLinesList) {

						long pid = voOrderLine.getProductId();
						VoProduct voProduct = prdMap.get(pid);
						if (-1 != voProduct.getImportId()) {
							double price = voProduct.getPrice(currentOrder.getPriceType());

							if (currentOdrerLines.containsKey(pid)) {

								VoOrderLine currentOL = getOrderLineByProductId(currentOrderLinesList, pid);
								currentOL.setQuantity(currentOL.getQuantity() + voOrderLine.getQuantity());
								VoProduct curProduct = prdMap.get(currentOL.getProductId());
								curProduct.setScore(curProduct.getScore() + voOrderLine.getQuantity());

								// merge packets for prepack product
								if (voProduct.isPrepackRequired()) {
									mergeOrderLinePackets(voOrderLine, currentOL);
								}

								pm.makePersistent(currentOL);

							} else {
								VoOrderLine newOrderLine = new VoOrderLine(currentOrder, prdMap.get(voOrderLine.getProductId()), voOrderLine.getQuantity(), price,
										voOrderLine.getComment(), voOrderLine.getPackets());

								pm.makePersistent(newOrderLine);

								currentOdrerLines.put(voOrderLine.getProductId(), newOrderLine.getId().getId());
							}
							addWeigth += voOrderLine.getQuantity() * voProduct.getWeight();
							addCost += voOrderLine.getQuantity() * price;
						}
					}
				}
				currentOrder.addCost(addCost);
				currentOrder.addWeigthGramm(addWeigth);

				pm.makePersistent(currentOrder);
				return currentOrder.getOrderDetails(prdMap, pm);// addCost;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================

	private VoOrderLine getOrderLineByProductId(List<VoOrderLine> currentOrderLinesList, long productId) {
		for (VoOrderLine voOrderLine : currentOrderLinesList) {
			if (voOrderLine.getProductId() == productId)
				return voOrderLine;
		}
		;
		return null;
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

		if (orderId == oldOrderId)
			return getOrderDetails(oldOrderId);

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			HashMap<Long, VoProduct> prdMap = getProductMapFromCacheByShopId(pm, voOrder.getShopId());
			if (null != voOrder) {
				List<VoOrderLine> oldOrderLinesList = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class, "orderId==" + oldOrderId).execute();

				double addCost = 0.0;
				double addWeigth = 0.0;

				for (VoOrderLine oldLine : oldOrderLinesList) {

					if (!currentOdrerLines.containsKey(oldLine.getProductId())) {
						// there is no such product in the current order
						VoProduct oldOrderLineProduct = prdMap.get(oldLine.getProductId());

						if (-1 != oldOrderLineProduct.getImportId()) {
							Double price = oldOrderLineProduct.getPrice(currentOrder.getPriceType());
							// Product is detached member so the price stored in this object
							// would be actual
							VoOrderLine voOrderLine = new VoOrderLine(currentOrder, oldOrderLineProduct, oldLine.getQuantity(), price, oldLine.getComment(),
									oldLine.getPackets());
							oldOrderLineProduct.setScore(oldOrderLineProduct.getScore() + oldLine.getQuantity());

							addCost += price * oldLine.getQuantity();
							addWeigth += oldOrderLineProduct.getWeight() * oldLine.getQuantity();

							pm.makePersistent(voOrderLine);
							currentOdrerLines.put(oldLine.getProductId(), voOrderLine.getId().getId());

							currentOrder.addCost(price * oldLine.getQuantity());
						}
					}
				}
				currentOrder.addCost(addCost);
				currentOrder.addWeigthGramm(addWeigth);

				pm.makePersistent(currentOrder);
				return voOrder.getOrderDetails(prdMap, pm);
			}
			throw new InvalidOperation(VoError.GeneralError, "Order not found by ID:" + oldOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to appendOrder Id=" + oldOrderId + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public OrderUpdateInfo setOrderLine(long orderId, long productId, double quantity, String comment, Map<Double, Integer> packs)
			throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		if (0 == quantity) {
			removeOrderLine(orderId, productId);
			return null;
		}
		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);
			orderId = currentOrder.getId();
			moveOrderDateIfExpired(currentOrder, pm);
			// look if product was ordered already
			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();

			int weightDiff = 0;
			double costDiff = 0.0;
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);

			VoOrderLine oldLine = null;
			if (currentOdrerLines.containsKey(productId)) {
				try{
					oldLine = pm.getObjectById(VoOrderLine.class, currentOdrerLines.get(productId));
					double oldQty = oldLine.getQuantity();
					weightDiff -= oldQty * voProduct.getWeight();
					costDiff -= oldQty * oldLine.getPrice();
					voProduct.setScore(voProduct.getScore() - oldQty);
				} catch(Exception e){
					logger.warning("Order line registered in the linesMap but not exists orderId==" + orderId + " && productId == "+" orderLIneId="+currentOdrerLines.get(productId));
					currentOdrerLines.remove(productId);
				}
			}
			if( null == oldLine ){
				List<VoOrderLine> orderLines = (List<VoOrderLine>) pm.newQuery(
						VoOrderLine.class,
						"orderId==" + orderId + " && productId == " + productId).execute();
				if (!orderLines.isEmpty()){
					logger.warning("The map of orderLines does not contain a line that persist. orderId==" + orderId + " && productId == ");
					double oldQty = oldLine.getQuantity();
					oldLine = orderLines.get(0);
					currentOdrerLines.put(productId, oldLine.getId().getId());
					weightDiff -= oldQty * voProduct.getWeight();
					costDiff -= oldQty * oldLine.getPrice();
					voProduct.setScore(voProduct.getScore() - oldQty);
					for( VoOrderLine ol: orderLines){
						if(oldLine.getId() != ol.getId() ){
							logger.warning("There is several order lines of the same product in the order: orderId==" + orderId + " && productId == "+" orderLIneId="+ol.getId());
							pm.deletePersistent(ol);
							oldQty = oldLine.getQuantity();
							weightDiff -= oldQty * voProduct.getWeight();
							costDiff -= oldQty * oldLine.getPrice();
							voProduct.setScore(voProduct.getScore() - oldQty);							
						}							
					}
				}				
			}

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
			double price = voProduct.getPrice(currentOrder.getPriceType());

			VoOrderLine theLine = new VoOrderLine(currentOrder, voProduct, quantity, price, comment, packsRounded);
			if (null != oldLine) {
				theLine.setId(oldLine.getId());
			}
			weightDiff += quantity * voProduct.getWeight();
			costDiff += quantity * price;
			voProduct.setScore(voProduct.getScore() + quantity);
			pm.makePersistent(theLine);

			if (null == oldLine)
				currentOrder.getOrderLines().put(productId, theLine.getId().getId());

			currentOrder.addWeigthGramm(weightDiff);
			currentOrder.addCost(costDiff);

			/*
			 * VoShop voShop = pm.getObjectById(VoShop.class,
			 * currentOrder.getShopId()); Map<Integer, Integer>
			 * deliveryByWeightIncrement = voShop.getDeliveryByWeightIncrement();
			 * 
			 * if( null!=deliveryByWeightIncrement && DeliveryType.SELF_PICKUP !=
			 * currentOrder.getDelivery() && //check if order line breaks the weight
			 * step !increaseDeliveryForWeight(voShop, oldWeight
			 * ).equals(increaseDeliveryForWeight(voShop, weightDiff + oldWeight ))){
			 * 
			 * updateDeliveryCost(voShop, currentOrder, null, pm); }
			 */

			pm.makePersistent(currentOrder);
			pm.makePersistent(voProduct);
			return new OrderUpdateInfo(currentOrder.getTotalCost(), currentOrder.getDelivery(), currentOrder.getDeliveryCost(),
					currentOrder.getWeightGramm(), theLine.getOrderLine(pm));

		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("Failed to addOrderLine OrderId="+orderId+" ProductId=" + productId + ". "+e);
			throw new InvalidOperation(VoError.GeneralError, "Failed to addOrderLine OrderId="+orderId+" ProductId=" + productId + ". " + e);
		} finally {
		}
	}

	// ======================================================================================================================
	@Override
	public boolean removeOrderLine(long orderId, long productId) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

/*			Map<Long, Long> currentOdrerLines = currentOrder.getOrderLines();
			Long removedLineID = currentOdrerLines.remove(productId);
			if (null == removedLineID) {
				logger.warning("No order line found for product id=" + productId + " in order: " + orderId);
				return false;
			}
*/
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);	
			List<VoOrderLine> orderLines = (List<VoOrderLine>) pm.newQuery(
					VoOrderLine.class,
					"orderId==" + currentOrder.getId() + " && productId == " + productId).execute();
			if (!orderLines.isEmpty()){
				for( VoOrderLine ol: orderLines){
					VoOrderLine removedLine = ol;
					voProduct.setScore(voProduct.getScore() - removedLine.getQuantity());
					currentOrder.addCost(-removedLine.getPrice() * removedLine.getQuantity());
					currentOrder.addWeigthGramm(-removedLine.getQuantity() * voProduct.getWeight());
					pm.deletePersistent(removedLine);
				}
			}	
			
			pm.makePersistent(currentOrder);
			pm.makePersistent(voProduct);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to removeOrderLine Id=" + productId + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public OrderDetails setOrderDeliveryType(long orderId, DeliveryType deliveryType, PostalAddress pa) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress oldDeliveryTo = currentOrder.getDeliveryTo();
			VoPostalAddress newDeliveryTo = null == pa ? null : new VoPostalAddress(pa, pm);

			if (deliveryType != DeliveryType.SELF_PICKUP && null == newDeliveryTo)
				newDeliveryTo = currentOrder.getUser().getAddress(); // Home address as
																															// default

			// look if something significant is changed
			if (deliveryType != currentOrder.getDelivery() || // type changed
					deliveryType != DeliveryType.SELF_PICKUP && oldDeliveryTo.getId() != newDeliveryTo.getId()) { // or
																																																				// address
																																																				// changed

				VoShop voShop = pm.getObjectById(VoShop.class, ShopServiceHelper.getCurrentShopId(this, pm));

				currentOrder.setDelivery(deliveryType);

				if (deliveryType == DeliveryType.SELF_PICKUP) {
					currentOrder.setDeliveryTo(null==newDeliveryTo ? voShop.getAddress() : newDeliveryTo);

				} else if (null != newDeliveryTo) {
					currentOrder.setDeliveryTo(newDeliveryTo);

				} else {
					throw new InvalidOperation(VoError.IncorrectParametrs,
							"No delivery to address set and the user has no Home addreee defined to usse as delivery to");

				}

				pm.makePersistent(currentOrder); // save changes before analyze all
																					// orders of the user
				updateDeliveryCost(voShop, currentOrder, oldDeliveryTo, pm);

			}
			HashMap<Long, VoProduct> prdMap = getProductMapFromCacheByShopId(pm, currentOrder.getShopId());
			return currentOrder.getOrderDetails(prdMap, pm);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryType=" + deliveryType.name() + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================
	// Calculate delivery costs according to:
	// One user can have several orders that should be delivered together and pay
	// only once
	// delivery cost depend on distance, address substring and weight
	private double updateDeliveryCost(VoShop shop, VoOrder order, VoPostalAddress oldAddress, PersistenceManager pm) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(order.getId()));

		double newDeliveryCost = 0;
		VoPostalAddress newAddress = order.getDeliveryTo();

		// check if update required
		if (null == newAddress && null == oldAddress || null != newAddress && null != oldAddress && newAddress.getId() == oldAddress.getId()
				&& order.getDelivery() != DeliveryType.SELF_PICKUP) // nothing changed
			return order.getDeliveryCost();

		// collect all orders of the same user and the same date and shop
		List<VoOrder> orders = (List<VoOrder>) pm.newQuery(
				VoOrder.class,
				"shopId==" + order.getShopId() + " && date == " + order.getDate() + " && user == " + order.getUser().getId() + " && ( status == '"
						+ OrderStatus.NEW + "' || status == '" + OrderStatus.CONFIRMED + "')").execute();

		if (null == orders || 0 == orders.size()) {
			// @TODO check that there is a cancelled order and throw exception only if
			// no one found
			// throw new InvalidOperation(VoError.GeneralError,
			// "Failed to Calculate Delivery cost. No order Found but at least one must exists!");
			return 0D;
		}
		// orders that stores the delivery cost of old and new delivery group
		VoOrder voOrderWithDelivery = null, voOrderWithDeliverySet = null;
		VoOrder voOldOrderWithDelivery = null, voOldOrderWithDeliverySet = null;

		int totalWeight = 0, totalOldWeight = 0;

		for (VoOrder nextOrder : orders) { // collect total weight of orders for new
																				// and for old address and select an
																				// order to apply delivery cost to.

			double orderDeliveryCost = nextOrder.getDeliveryCost();

			if (DeliveryType.SELF_PICKUP == nextOrder.getDelivery()) {

				if (orderDeliveryCost != 0) { // update delivery cost and total cost
					nextOrder.setDeliveryCost(0);
					nextOrder.addCost(-orderDeliveryCost);
					pm.makePersistent(nextOrder);
					continue;
				}

				// combine delivery to new address
			} else if (null != nextOrder.getDeliveryTo() && null != newAddress && nextOrder.getDeliveryTo().getId() == newAddress.getId()
					&& order.getDelivery() != DeliveryType.SELF_PICKUP) { // don't take
																																// delivery
																																// address into
																																// account if
																																// it's self
																																// pickup

				totalWeight += nextOrder.getWeightGramm();

				if (null == voOrderWithDelivery)
					voOrderWithDelivery = nextOrder; // remember an order to set delivery
																						// cost to if no one set

				if (0 != nextOrder.getDeliveryCost())
					if (null == voOrderWithDeliverySet)
						voOrderWithDeliverySet = nextOrder; // remember an order with
																								// delivery cost is already set
																								// to update it
					else {// it's the second order with delivery set, so it should be
								// removed
						nextOrder.setDeliveryCost(0.0D);
						nextOrder.addCost(-orderDeliveryCost);
					}

				// old delivery group
			} else if (null != nextOrder.getDeliveryTo() && null != oldAddress && null != oldAddress
					&& nextOrder.getDeliveryTo().getId() == oldAddress.getId()) {

				totalOldWeight += nextOrder.getWeightGramm();

				if (null == voOldOrderWithDelivery)
					voOldOrderWithDelivery = nextOrder; // remember an order to set
																							// delivery cost to if no one set

				if (0 != nextOrder.getDeliveryCost())
					if (null != voOldOrderWithDeliverySet) {// it never could happens, but
																									// it better to check
						nextOrder.setDeliveryCost(0.0D);
						nextOrder.addCost(-orderDeliveryCost);

					} else
						voOldOrderWithDeliverySet = nextOrder; // remember an order with
																										// delivery cost is set to
																										// update it
			}
		}

		// update cost of delivery to new address
		if (null == voOrderWithDeliverySet) // no orders with delivery cost was set
																				// so the first one would be used to
																				// charge delivery fee
			voOrderWithDeliverySet = voOrderWithDelivery;

		if (DeliveryType.SELF_PICKUP != order.getDelivery())
			newDeliveryCost = setDeliveryGroupDeliveryCost(voOrderWithDeliverySet, shop, totalWeight, pm);
		else
			newDeliveryCost = 0;

		if (null != oldAddress && null != voOldOrderWithDelivery)
			setDeliveryGroupDeliveryCost(voOldOrderWithDeliverySet == null ? voOldOrderWithDelivery : voOldOrderWithDeliverySet, shop, totalOldWeight, pm);

		return newDeliveryCost;
	}

	// ======================================================================================================================

	private double setDeliveryGroupDeliveryCost(VoOrder order, VoShop voShop, int totalWeight, PersistenceManager pm) {

		Map<Integer, Double> deliveryCosts = voShop.getDeliveryCosts(); // use as a
																																		// base cost
		Double newDeliveryCost = null == deliveryCosts ? 0.0D : deliveryCosts.get(order.getDelivery().getValue());

		if (null == newDeliveryCost)
			newDeliveryCost = 0D;

		VoPostalAddress deliveryTo = order.getDeliveryTo();

		int distance = -1;
		String addressMathces = null;
		// get delivery cost by address mask if set
		Map<DeliveryType, String> dam = voShop.getDeliveryAddressMasksText();
		if (null != dam && null != deliveryCosts) { // look for delivery cost by
																								// address mask
			String addressString = deliveryTo.getAddressText(pm);

			// sort the map by DelivertType to math cheapest delivery first
			SortedMap<DeliveryType, String> sm = new TreeMap<DeliveryType, String>(dam);

			for (Entry<DeliveryType, String> dame : sm.entrySet()) {
				if (addressString.matches(dame.getValue())) {
					addressMathces = dame.getKey().name();
					Double ndc = deliveryCosts.get(dame.getKey().getValue());
					if (null != ndc) { // if there is a cost set for this kind of delivery
						newDeliveryCost = ndc;
						order.setDelivery(dame.getKey());
						break;
					}
				}
			}

		} else {// check if there is a delivery cost by range is set
			VoPostalAddress shopAddress;

			if (deliveryTo != null && null != (shopAddress = voShop.getAddress())) {
				Map<Integer, Double> dcbd = voShop.getDeliveryCostByDistance();
				if (null != dcbd) {

					// sort the map by DelivertType to math cheapest delivery first
					SortedMap<Integer, Double> sm = new TreeMap<Integer, Double>(dcbd);
					distance = shopAddress.getDistance(deliveryTo).intValue();
					SortedMap<Integer, Double> minDistMap = sm.headMap(distance);
					if (minDistMap.size() > 0) {
						// get the cost of the biggest distance that is less then distance
						// between the shop and delivery to address
						newDeliveryCost += minDistMap.get(minDistMap.lastKey());
					}
				}
			}
		}

		// calculate change of cost depend on weight
		newDeliveryCost += increaseDeliveryForWeight(voShop, totalWeight);
		order.setDeliveryCost(newDeliveryCost);
		order.addCost(newDeliveryCost);
		logger.info("New delivery fee " + newDeliveryCost + " for order:" + order.getId() + " for weight:" + totalWeight
				+ (distance != -1 ? " distance " + distance + "km" : "") + (null != addressMathces ? " matches by address text to " + addressMathces : ""));

		return newDeliveryCost;
	}

	// ======================================================================================================================
	private Double increaseDeliveryForWeight(VoShop voShop, int totalWeight) {
		Map<Integer, Integer> dbwi = voShop.getDeliveryByWeightIncrement();
		if (null != dbwi) {
			SortedMap<Integer, Integer> dbwis = new TreeMap<Integer, Integer>(dbwi);
			SortedMap<Integer, Integer> headMap = dbwis.headMap(totalWeight);
			if (0 != headMap.size()) {
				// increment the cost to value of how much times repeated the value
				return (double) (headMap.get(headMap.lastKey()) * (totalWeight / headMap.lastKey()));
			}
		}
		return 0D;
	}

	// ======================================================================================================================
	@Override
	public boolean setOrderPaymentType(long orderId, PaymentType paymentType) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			VoShop voShop = pm.getObjectById(VoShop.class, ShopServiceHelper.getCurrentShopId(this, pm));
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
					logger.warning("" + voShop + " have no Payment type " + paymentType.name() + " Payment type will not been changed");
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentType=" + paymentType.name() + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public OrderDetails setOrderDeliveryAddress(long orderId, PostalAddress deliveryAddress) throws InvalidOperation {

		removeObjectFromCache(generateOrderDetailsKey(orderId));

		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress address = new VoPostalAddress(deliveryAddress, pm);
			VoPostalAddress oldAddress = currentOrder.getDeliveryTo();

			currentOrder.setDeliveryTo(address);
			pm.makePersistent(currentOrder);

			updateDeliveryCost(pm.getObjectById(VoShop.class, currentOrder.getShopId()), currentOrder, oldAddress, pm);

			VoUser currentUser = getCurrentUser(pm);
			currentUser.addDeliveryAddress(address, address.getAddressText(pm));

			pm.makePersistent(currentUser);
			HashMap<Long, VoProduct> prdMap = getProductMapFromCacheByShopId(pm, currentOrder.getShopId());
			return currentOrder.getOrderDetails(prdMap, pm);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryAddress to " + deliveryAddress + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public List<Order> getOrdersByStatus(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		if (0 == dateFrom) // it's to expensive to show all orders
			dateFrom = getNextOrderDate((int) (System.currentTimeMillis() / 1000L - 7L * 86400000L)).orderDate;

		return getOrdersByStatus(0, dateFrom, dateTo + 10, status);
	}

	// ======================================================================================================================

	@Override
	public List<Order> getMyOrdersByStatus(int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		return getOrdersByStatus(getCurrentUserId(), dateFrom, dateTo, status);
	}

	// ======================================================================================================================

	private List<Order> getOrdersByStatus(long userId, int dateFrom, int dateTo, OrderStatus status) throws InvalidOperation {
		PersistenceManager pm = getPM();
		Long shopId = ShopServiceHelper.getCurrentShopId(this, pm);
		dateFrom = dateFrom - dateFrom % 86400;
		dateTo = dateTo - dateTo % 86400 + 86400;

		try {
			Query pcq = pm.newQuery(VoOrder.class);
			pcq.setOrdering("date inc, createdAt desc");
			List<VoOrder> ps;
			if (0 != userId) {
				pcq.setFilter("user == :key && shopId == " + shopId + " && date >= " + dateFrom
						+ (status != OrderStatus.UNKNOWN ? " && status == '" + status + "'" : ""));
				ps = (List<VoOrder>) pcq.execute(userId, dateFrom);
			} else {

				pcq.setFilter("shopId == " + shopId + " && date >= " + dateFrom + (status != OrderStatus.UNKNOWN ? " && status == '" + status + "'" : ""));
				ps = (List<VoOrder>) pcq.execute(dateFrom);
			}
			List<Order> lo = new ArrayList<Order>();
			int now = (int) (System.currentTimeMillis() / 1000L);
			OrderDate nextDate;
			Iterator<VoOrder> oit = ps.iterator();
			while (oit.hasNext()) {
				VoOrder nextOrder;
				try {
					nextOrder = oit.next();
				} catch (RuntimeException tre) {
					tre.printStackTrace();
					continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if (0 != userId && OrderStatus.NEW == status && nextOrder.getDate() < (nextDate = getNextOrderDate(now)).orderDate) {
					nextOrder.setDate(nextDate.orderDate);
					nextOrder.setPriceType(nextDate.priceType, pm);
				}
				if (nextOrder.getDate() < dateTo)
					lo.add(nextOrder.getOrder());

			}
			pcq.closeAll();
			return lo;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrdersByStatus for shopId=" + shopId + "." + e);
		} finally {

		}
	}

	// ======================================================================================================================
	@Override
	public Order getOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoOrder currentOrder;
			if (0 == orderId) {
				currentOrder = ShopServiceHelper.getCurrentOrder(this, pm);
			} else {
				currentOrder = pm.getObjectById(VoOrder.class, orderId);
				super.setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), orderId, pm);
			}
			return currentOrder.getOrder();
		} catch (Exception e) {
			// e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to get Order by ID " + orderId + ". " + e);
		} finally {

		}
	}

	// ======================================================================================================================

	// ======================================================================================================================
	@Override
	public void updateOrder(long orderId, int date, String comment) throws InvalidOperation {
		date -= date % 86400;
		PersistenceManager pm = getPM();
		try {
			VoOrder voOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			removeObjectFromCache(generateOrderDetailsKey(orderId));

			voOrder.setDate(date);
			voOrder.setComment(comment);
			pm.makePersistent(voOrder);
		} finally {

		}
	}

	// ======================================================================================================================
	static String productMapCacheKey(long shopId) {
		return "productMapCacheKey:" + shopId;
	}

	// ======================================================================================================================
	public void recalculateOrder(long orderId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoOrder voOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder(this, pm) : pm.getObjectById(VoOrder.class, orderId);

			removeObjectFromCache(generateOrderDetailsKey(orderId));

			List<VoOrderLine> olines = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class, "orderId==" + voOrder.getId()).execute();
			double totalCost = 0.0D;
			int weightGramm = 0;

			long shopId = voOrder.getShopId();
			HashMap<Long, VoProduct> productsMap = getProductMapFromCacheByShopId(pm, shopId);

			for (VoOrderLine voOrderLine : olines) {
				VoProduct voProduct = productsMap.get(voOrderLine.getProductId());
				if (null != voProduct) {
					totalCost += voOrderLine.getQuantity() * voProduct.getPrice(voOrder.getPriceType());
					weightGramm += voOrderLine.getQuantity() * voProduct.getWeight();
				}
			}
			voOrder.setTotalCost(totalCost);
			voOrder.setWeightGramm(weightGramm);
			voOrder.setDeliveryCost(0.0);
			updateDeliveryCost(pm.getObjectById(VoShop.class, shopId), voOrder, null, pm);
			pm.makePersistent(voOrder);
		} finally {

		}
	}

	// ======================================================================================================================

	private static HashMap<Long, VoProduct> getProductMapFromCacheByShopId(PersistenceManager pm, long shopId) {
		String productMapCacheKey = productMapCacheKey(shopId);
		HashMap<Long, VoProduct> productsMap = getObjectFromCache(productMapCacheKey);

		if (null == productsMap) {
			productsMap = new HashMap<Long, VoProduct>();
			List<VoProduct> products = (List<VoProduct>) pm.newQuery(VoProduct.class, "shopId==" + shopId).execute();
			for (VoProduct voProduct : products) {
				if (!voProduct.isDeleted())
					productsMap.put(voProduct.getId(), pm.detachCopy(voProduct));
			}
			putObjectToCache(productMapCacheKey, productsMap);
		}
		return productsMap;
	}

	static void dropProductMapFromCache(long shopId) {
		removeObjectFromCache(productMapCacheKey(shopId));
	}

	// ======================================================================================================================

	@Override
	public boolean isPublicMethod(String method) {
		Long roleRequired;
		if (null == (roleRequired = VoShopAccessRoles.getRequiredRole(method))) {
			logger.warning("Method '" + method + "' is called but there is no role registered for it! Access denied");
			return false;
		}
		return roleRequired == VoUserAccessBaseRoles.ANYBODY || roleRequired == VoShopAccessRoles.CUSTOMER;
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.SHOP_SI.ordinal();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IdNameChilds> getProductsByCategories(long shopId) throws InvalidOperation {

		ArrayList<IdNameChilds> catwithProcuts = getObjectFromCache(createShopProductsByCategoryKey(shopId));

		if (null == catwithProcuts) {
			catwithProcuts = new ArrayList<IdNameChilds>();
			Map<Long, List<IdName>> cpm = new TreeMap<Long, List<IdName>>();
			PersistenceManager pm = getPM();
			try {
				if (0 == shopId)
					shopId = ShopServiceHelper.getCurrentShopId(this, pm);
				Query prodQuery = pm.newQuery(VoProduct.class, "shopId==" + shopId);
				// prodQuery.setOrdering("score DESC");
				List<VoProduct> products = (List<VoProduct>) prodQuery.execute();
				//Collections.sort(products, new ProdcutScoreComparator());
				for (VoProduct voProduct : products) {
					for (Long catId : voProduct.getCategories()) {
						if (!cpm.containsKey(catId)) {
							cpm.put(catId, new ArrayList<IdName>());
						}
						if (!voProduct.isDeleted())
							cpm.get(catId).add(new IdName(voProduct.getId(), voProduct.getName()));
					}
				}
				// Load all categories and filter them by products
				List<VoProductCategory> vpcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class, "shopId==" + shopId).execute();
				for (VoProductCategory voProductCategory : vpcl) {
					if (!voProductCategory.isDeleted()) {
						long nextCatId = voProductCategory.getId();
						if (cpm.containsKey(nextCatId)) {
							catwithProcuts.add(new IdNameChilds(nextCatId, voProductCategory.getName(), cpm.get(nextCatId)));
						}
					}
				}
			} finally {

			}
			putObjectToCache(createShopProductsByCategoryKey(shopId), catwithProcuts);
		}
		return catwithProcuts;
	}

	static public Object createShopProductsByCategoryKey(long shopId) {
		return "createShopProductsByCategoryKey" + shopId;
	}

	// ======================================================================================================================

	@Override
	public MatrixAsList getUserDeliveryAddresses() throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			List<String> list = getCurrentUser(pm).getAddresses();
			return new MatrixAsList(list.size(), list);
		} finally {

		}
	}

	@Override
	public PostalAddress getUserDeliveryAddress(String addressText) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoPostalAddress deliveryAddress = getCurrentUser(pm).getDeliveryAddress(addressText);
			if (null == deliveryAddress)
				throw new InvalidOperation(VoError.IncorrectParametrs, "Addrress not found for user by text '" + addressText + "'");
			return deliveryAddress.getPostalAddress(pm);
		} finally {

		}
	}

	@Override
	public void deleteDeliveryAddress(String addressText) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			getCurrentUser(pm).removeDeliveryAddress(addressText);
		} finally {

		}
	}

	@Override
	public String getDeliveryAddressViewURL(String addressText, int width, int height) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			VoPostalAddress deliveryAddress = getCurrentUser(pm).getDeliveryAddress(addressText);
			if (null == deliveryAddress)
				return null;
			VoBuilding building = deliveryAddress.getBuilding();
			return VoGeocoder.createMapImageURL(building.getLongitude(), building.getLatitude(), width, height, addressText);
		} finally {

		}
	}

	// ======================================================================================================================

	@Override
	public boolean accessAllowed(VoUserAccessBase voUserAccessBase, long currentUserId, long categoryId, String method, PersistenceManager pm) {

		Long roleRequired;
		if (null == (roleRequired = VoShopAccessRoles.getRequiredRole(method))) {
			logger.warning("Method '" + method + "' is called but there is no role registered for it! Access denied");
			return false;
		}

		if (roleRequired == VoUserAccessBaseRoles.ANYBODY || roleRequired == VoShopAccessRoles.CUSTOMER)
			return true;

		long shopId = 0;
		try {
			shopId = ShopServiceHelper.getCurrentShopId(this, pm);
		} catch (InvalidOperation e) {
		}
		if (voUserAccessBase instanceof VoShopAccess) {

			long arShopId = ((VoShopAccess) voUserAccessBase).getShopId();
			if ((0 == arShopId || shopId == arShopId) && voUserAccessBase.getAccessPermission(roleRequired))
				return true;
		}
		return false;
	}

	// ======================================================================================================================

	public Class getAuthRecordClass() {
		return VoShopAccess.class;
	}

	// ======================================================================================================================

	@Override
	public UserShopRole getUserShopRole(long shopId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		try {
			long currentUserId = getCurrentUserId(pm);
			if (shopId == 0)
				shopId = ShopServiceHelper.getCurrentShopId(this, pm);
			List<VoShopAccess> rslt = (List<VoShopAccess>) pm.newQuery(VoShopAccess.class, "userId==" + currentUserId + " && shopId==" + shopId).execute();
			if (pm.getObjectById(VoShop.class, shopId).getOwnerId() == currentUserId)
				return UserShopRole.OWNER;

			if (rslt.size() == 0)
				return UserShopRole.CUSTOMER;

			long access = 0;
			for (VoShopAccess vsa : rslt) {
				access |= vsa.getPermissionBits();
			}
			return (access & VoShopAccessRoles.ADMIN) == VoShopAccessRoles.ADMIN ? UserShopRole.ADMIN
					: pm.getObjectById(VoShop.class, shopId).getOwnerId() == currentUserId
							|| (access & VoShopAccessRoles.CATALOGUE) == VoShopAccessRoles.CATALOGUE
							|| (access & VoShopAccessRoles.REPORT) == VoShopAccessRoles.REPORT || (access & VoShopAccessRoles.BILLING) == VoShopAccessRoles.BILLING ? UserShopRole.BACKOFFICER
							: UserShopRole.CUSTOMER;

		} catch (Exception e) {
			logger.info("Failed to get USer ROLE: " + e);
			return UserShopRole.UNKNOWN;

		} finally {

		}
	}

	@Override
	public List<ProductCategory> getAllCategories(long shopId) throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			List<VoProductCategory> vpcl = (List<VoProductCategory>) pm.newQuery(VoProductCategory.class, "shopId==" + shopId).execute();
			List<ProductCategory> pcl = new ArrayList<ProductCategory>();
			for (VoProductCategory vpc : vpcl) {
				pcl.add(vpc.getProductCategory());
			}
			return pcl;
		} finally {

		}
	}

	@Override
	public boolean canVote(long shopId) throws InvalidOperation, TException {
		if (0 == shopId)
			return false;

		PersistenceManager pm = getPM();
		try {
			long uid = getCurrentUserId();
			return pm.getObjectById(VoShop.class, shopId).canVote(uid);
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:" + shopId);
		} finally {

		}
	}

	@Override
	public int vote(long shopId, String value) throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			long uid = getCurrentUserId();
			return pm.getObjectById(VoShop.class, shopId).vote(uid, value);
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:" + shopId);
		}
	}

	@Override
	public Map<String, Integer> getVotes(long shopId) throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			Map<String, Set<Long>> voteResults = pm.getObjectById(VoShop.class, shopId).getVoteResults();
			Map<String, Integer> voteRslts = new HashMap<String, Integer>();
			if (null != voteResults) {
				for (Entry<String, Set<Long>> ve : voteResults.entrySet())
					voteRslts.put(ve.getKey(), ve.getValue().size());
			}
			return voteRslts;
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:" + shopId);
		}
	}

	@Override
	public boolean isActivated(long shopId) throws InvalidOperation, TException {
		PersistenceManager pm = getPM();
		try {
			return pm.getObjectById(VoShop.class, shopId).isActivated();
		} catch (JDOObjectNotFoundException onfe) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:" + shopId);
		}
	}

	@Override
	public List<PostalAddress> getPickupAddresses(long shopId) throws InvalidOperation {
		PersistenceManager pm = getPM();
		VoCountry country = new VoCountry("Россия", pm);
		VoCity spb = new VoCity(country, "Санкт-Петербург", pm);
		VoCity pishkin = new VoCity(country, "Пушкин", pm);
		VoStreet lensovetaStr = new VoStreet( spb, "Ленсовета", pm);
		VoStreet gdStr = new VoStreet( pishkin, "Железнодорожная", pm);
		VoStreet pargStr = new VoStreet( spb, "Парголовская", pm);
		VoBuilding vblsv = new VoBuilding(lensovetaStr, "37", null, null, pm);
		VoBuilding vbgd = new VoBuilding(gdStr, "56А", null, null, pm);
		VoBuilding vbparg = new VoBuilding(pargStr, "7", null, null, pm);
		
		List<PostalAddress> pl = new ArrayList<PostalAddress>(3);
		pl.add( new VoPostalAddress( vblsv, (byte)0, (byte)0, 0, "").getPostalAddress(pm));
		pl.add( new VoPostalAddress( vbgd, (byte)0, (byte)0, 0, "").getPostalAddress(pm));
		pl.add( new VoPostalAddress( vbparg, (byte)0, (byte)0, 0, "").getPostalAddress(pm));
		return pl;
	}

	// ======================================================================================================================
	
	// ======================================================================================================================
	@Override
	public PostalAddress createDeliveryAddress(String buildingAddressText, int flatNo, byte floor, byte staircase, String comment)
			throws InvalidOperation {
	
		AddressInfo addrInfo = VoGeocoder.resolveAddressString("Россия Санкт Петербург " + buildingAddressText);
		if (null == addrInfo.getBuildingNo()) {
			addrInfo.setStreetName(buildingAddressText);
			addrInfo.getBuildingNo("- дом не найден на карте! Уточните, пожалуйста, или введите в комментарии ниже.");
			addrInfo.setLongitude("30.419349");
			addrInfo.setLattitude("59.734401");
		}
		if (null == addrInfo.getCityName())
			addrInfo.setCityName("Санкт Петербург");
		if (null == addrInfo.getCountryName())
			addrInfo.setCountryName("Россия");
	
		PersistenceManager pm = getPM();
		try {
			VoUser currentUser = getCurrentUser(pm);
			VoCountry voCountry = new VoCountry(addrInfo.getCountryName(), pm);
			VoCity voCity = new VoCity(voCountry, addrInfo.getCityName(), pm);
			VoStreet voStreet = new VoStreet(voCity, addrInfo.getStreetName(), pm);
			String no = addrInfo.getBuildingNo();
			VoBuilding voBuilding = new VoBuilding(voStreet, no, addrInfo.getLongitude(), addrInfo.getLattitude(), pm);
			VoPostalAddress pa = new VoPostalAddress(voBuilding, staircase, floor, flatNo, comment);
			currentUser.addDeliveryAddress(pa, buildingAddressText);
			return pa.getPostalAddress(pm);
	
		} finally {
	
		}
	}
}
