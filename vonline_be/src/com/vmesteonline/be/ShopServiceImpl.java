package com.vmesteonline.be;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
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
import com.vmesteonline.be.shop.OrderLine;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.shop.ProductListPart;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.shop.ShopFEService;
import com.vmesteonline.be.utils.VoHelper;
//import com.google.api.client.util.Sets;

public class ShopServiceImpl extends ServiceImpl implements /*ShopBOService.Iface,*/ ShopFEService.Iface,  Serializable {

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
	public OrderDate getNextOrderDate(int afterDate) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
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
			pm.close();
		}
	}
	// ======================================================================================================================

	
	// ======================================================================================================================
	@Override
	public List<Producer> getProducers() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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
		Long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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
		Long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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

	static String getProcutsOfCategoryCacheKey(long categoryId, Long shopId) {
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
		dateFrom -= dateFrom % 86400;
		dateTo += ( 86400 - dateTo % 86400 );
		
		PersistenceManager pm = PMF.getPm();
		Long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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
	public long createOrder(int date, String comment) throws InvalidOperation {
		if (date < System.currentTimeMillis() / 1000L)
			throw new InvalidOperation(VoError.IncorrectParametrs, "Order could not be created for the past");

		date -= date % 86400;
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = ShopServiceHelper.getCurrentShop( this, pm );
			pm.retrieve(shop);
			PriceType pt = shop.getPriceType( date );
			
			VoUser user = getCurrentUser(pm);
			VoOrder voOrder = new VoOrder(user, shop, date, pt, comment, pm);

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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
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
	public long confirmOrder( long orderId, String comment ) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
			currentOrder.setStatus(OrderStatus.CONFIRMED);
			if( null!=comment ) currentOrder.setComment(comment);
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
				VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
				
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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress oldDeliveryTo = currentOrder.getDeliveryTo();
			VoPostalAddress newDeliveryTo = null == pa ? null : new VoPostalAddress(pa, pm);
			
			if( deliveryType != DeliveryType.SELF_PICKUP && null == newDeliveryTo )
				newDeliveryTo = currentOrder.getUser().getAddress(); //Home address as default
			
			//look if something significant is changed
			if (deliveryType != currentOrder.getDelivery() || //type changed 
					deliveryType != DeliveryType.SELF_PICKUP && oldDeliveryTo.getId() != newDeliveryTo.getId()) { //or address changed
				
				VoShop voShop = pm.getObjectById(VoShop.class, ShopServiceHelper.getCurrentShopId( this, pm ));

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
			//@TODO check that there is a cancelled order and throw exception only if no one found
			//throw new InvalidOperation(VoError.GeneralError, "Failed to Calculate Delivery cost. No order Found but at least one must exists!");
			return 0D;
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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

			VoShop voShop = pm.getObjectById(VoShop.class, ShopServiceHelper.getCurrentShopId( this, pm ));
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
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);

			VoPostalAddress address = new VoPostalAddress(deliveryAddress, pm);
			VoPostalAddress oldAddress = currentOrder.getDeliveryTo();
			
			currentOrder.setDeliveryTo(address);
			pm.makePersistent(currentOrder);
			
			updateDeliveryCost( pm.getObjectById(VoShop.class, currentOrder.getShopId()), 
					currentOrder, oldAddress, pm);
			
			VoUser currentUser = getCurrentUser(pm);
			currentUser.addDeliveryAddress( address, address.getAddressText(pm) );
			
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
		Long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
		dateFrom = dateFrom - dateFrom % 86400;
		dateTo = dateTo - dateTo % 86400 + 86400;
		
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
			int now = (int)(System.currentTimeMillis()/1000L);
			OrderDate nextDate;
			for (VoOrder nextOrder : ps) {
				
				if( 0!= userId && OrderStatus.NEW == status && nextOrder.getDate() < (nextDate = getNextOrderDate( now )).orderDate ){
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
				currentOrder = ShopServiceHelper.getCurrentOrder( this, pm );
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
	

	// ======================================================================================================================
	@Override
	public void updateOrder(long orderId, int date, String comment) throws InvalidOperation {
		date -= date % 86400;
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = 0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
			voOrder.setDate(date);
			voOrder.setComment(comment);
			pm.makePersistent(voOrder);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	

	// ======================================================================================================================

	@Override
	public boolean isPublicMethod(String method) {
		Long roleRequired;
		if( null == (roleRequired =  VoShopAccessRoles.getRequiredRole(method))){
			logger.warn("Method '"+method+"' is called but there is no role registered for it! Access denied");
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
		
		ArrayList<IdNameChilds> catwithProcuts = getObjectFromCache( createShopProductsByCategoryKey(shopId));
		
		if( null == catwithProcuts) {
			catwithProcuts = new ArrayList<IdNameChilds>(); 
			Map<Long,List<IdName>> cpm = new TreeMap<Long,List<IdName>>();
			PersistenceManager pm = PMF.getPm();
			try {
				if( 0 == shopId )
						shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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

	static Object createShopProductsByCategoryKey(long shopId) {
		return "createShopProductsByCategoryKey"+shopId;
	}
	
	// ======================================================================================================================

	
	
//======================================================================================================================
	@Override
	public PostalAddress createDeliveryAddress(String buildingAddressText, int flatNo, byte floor, byte staircase, String comment)
			throws InvalidOperation {
		
		AddressInfo addrInfo = VoGeocoder.resolveAddressString("Россия Санкт Петербург "+buildingAddressText);
		if( null == addrInfo.getBuildingNo() )
			throw new InvalidOperation(VoError.IncorrectParametrs, "No building found. Be sure that you entered a house number.");
		
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser currentUser = getCurrentUser(pm);  
			VoCountry voCountry = new VoCountry( addrInfo.getCountryName(), pm );
			VoCity voCity = new VoCity( voCountry, addrInfo.getCityName(), pm );
			VoStreet voStreet = new VoStreet(voCity, addrInfo.getStreetName(),pm );
			String no = addrInfo.getBuildingNo();
			VoBuilding voBuilding = new VoBuilding(voStreet, no, addrInfo.getLongitude(), addrInfo.getLattitude(), pm);
			VoPostalAddress pa = new VoPostalAddress( voBuilding, staircase, floor, (byte)flatNo, comment );
			currentUser.addDeliveryAddress( pa, buildingAddressText);
			return pa.getPostalAddress(pm);
			
		} finally {
			pm.close();
		}
	}

	@Override
	public MatrixAsList getUserDeliveryAddresses() throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			List<String> list = getCurrentUser(pm).getAddresses();
			return new MatrixAsList( list.size(), list);
		} finally {
			pm.close();
		}
	}

	@Override
	public PostalAddress getUserDeliveryAddress(String addressText) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoPostalAddress deliveryAddress = getCurrentUser(pm).getDeliveryAddress(addressText);
			if(null==deliveryAddress)
				throw new InvalidOperation(VoError.IncorrectParametrs, "Addrress not found for user by text '"+addressText+"'");
			return deliveryAddress.getPostalAddress();
		} finally {
			pm.close();
		}
	}

	@Override
	public void deleteDeliveryAddress(String addressText) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			getCurrentUser(pm).removeDeliveryAddress(addressText);
		} finally {
			pm.close();
		}
	}

	@Override
	public String getDeliveryAddressViewURL(String addressText, int width, int height) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoPostalAddress deliveryAddress = getCurrentUser(pm).getDeliveryAddress(addressText);
			if( null==deliveryAddress)
				return null;
			VoBuilding building = deliveryAddress.getBuilding();
			return VoGeocoder.createMapImageURL( building.getLongitude(), building.getLatitude(), width, height, addressText);
		} finally {
			pm.close();
		}
	}

//======================================================================================================================

	@Override
	public boolean accessAllowed(VoUserAccessBase voUserAccessBase, long currentUserId, long categoryId, String method, PersistenceManager pm) {

		Long roleRequired;
		if( null == (roleRequired =  VoShopAccessRoles.getRequiredRole(method))){
			logger.warn("Method '"+method+"' is called but there is no role registered for it! Access denied");
			return false;
		}
		
		if( roleRequired == VoUserAccessBaseRoles.ANYBODY || roleRequired == VoShopAccessRoles.CUSTOMER) 
			return true;
		
		long shopId = 0;
		try {
			shopId = ShopServiceHelper.getCurrentShopId( this, pm );
		} catch (InvalidOperation e) {
		}
		if( voUserAccessBase instanceof VoShopAccess ){
			
			long arShopId = ((VoShopAccess)voUserAccessBase).getShopId();
			if( (0 == arShopId || shopId == arShopId) && voUserAccessBase.getAccessPermission(roleRequired))
				return true;
		}
		return false;
	}	
	
	public Class getAuthRecordClass(){ return VoShopAccess.class; }
}
