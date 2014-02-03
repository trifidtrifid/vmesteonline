<<<<<<< HEAD
package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.Extent;
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
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
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
import com.vmesteonline.be.utils.Helper;

public class ShopServiceImpl extends ServiceImpl implements Iface {

	public static Logger logger = Logger.getLogger(ShopServiceImpl.class);

	public ShopServiceImpl(String sessionId) {
		super(sessionId);
	}

	@Override
	public long registerShop(Shop shop) throws InvalidOperation, TException {
		return new VoShop(shop).getId();
	}

	@Override
	public long registerProductCategory(ProductCategory productCategory, long shopId) throws InvalidOperation, TException {
		return new VoProductCategory(shopId, productCategory.getParentId(), productCategory.getName(), productCategory.getDescr(),
				productCategory.getLogoURLset(), productCategory.getTopicSet()).getId();
	}

	@Override
	public long registerProducer(Producer producer, long shopId) throws InvalidOperation, TException {
		return new VoProducer(shopId, producer.getName(), producer.getDescr(), producer.getLogoURL(), producer.getHomeURL()).getId();
	}

	@Override
	public Set<Long> uploadProducts(List<FullProductInfo> products, long shopId, boolean cleanShopBeforeUpload) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		Set<Long> productIds;
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			Set<VoProduct> shopProducts = voShop.getProducts();
			if (cleanShopBeforeUpload) {
				shopProducts.clear();
			}
			productIds = new HashSet<Long>();
			for (FullProductInfo fpi : products) {
				VoProduct voProduct = new VoProduct(shopId, fpi);
				productIds.add(voProduct.getId());
				shopProducts.add(voProduct);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to load Products. " + e.getMessage());
		} finally {
			pm.close();
		}
		return productIds;
	}

	@Override
	public Set<ProductCategory> uploadProductCategoies(Set<ProductCategory> categories, boolean relativeIds, boolean cleanShopBeforeUpload)
			throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Product categories. SHOP ID is not set in session context.");
		}
		Set<ProductCategory> categoriesCreated = new HashSet<ProductCategory>();
		Map<Long, Long> idMap = new HashMap<Long, Long>();

		PersistenceManager pm = PMF.getPm();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (cleanShopBeforeUpload) {
				voShop.getCategories().clear();
				logger.debug("All categories removed from " + voShop);
			}
			for (ProductCategory pc : categories) {
				long parentId = relativeIds && idMap.containsKey(pc.getParentId()) ? idMap.get(pc.getParentId()) : pc.getParentId();
				logger.debug("Use paret category " + parentId + " to instead of " + pc.getParentId());
				VoProductCategory vpc = new VoProductCategory(voShop.getId(), parentId, pc.getName(), pc.getDescr(), pc.getLogoURLset(), pc.getTopicSet());
				idMap.put(pc.getId(), vpc.getId());
				categoriesCreated.add(vpc.getProductCategory());
				voShop.addProductCategory(vpc);
				logger.debug("Category " + vpc + " added to " + voShop);
			}
			pm.makePersistent(voShop);
			currentTransaction.commit();
		} catch (Exception e) {
			currentTransaction.rollback();
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. " + e);
		} finally {
			pm.close();
		}
		return categoriesCreated;
	}

	@Override
	public List<Order> getFullOrders(int dateFrom, int dateTo, long userId, long shopId) throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		List<Order> ol;
		try {
			Query voquery = pm.newQuery(VoOrder.class);
			List<VoOrder> results = null;
			if (shopId != 0) {
				voquery.setFilter("shopId == theShop");
				voquery.declareParameters("theShop long");
				if (0 != userId) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId, userId);
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId);
				}
			} else {
				if (0 != userId) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, userId);
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class);
				}
			}
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
	public void updateOrderStatusesById(Map<Long, OrderStatus> orderStatusMap) throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
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
	public void setDates(Map<Integer, DateType> dateDateTypeMap) throws TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDates. SHOP ID is not set in session context.");
		}
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
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
	public List<Shop> getShops() throws InvalidOperation, TException {
		List<Shop> shops = new ArrayList<Shop>();
		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoShop> voshops = pm.getExtent(VoShop.class);
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
	public Map<Integer, DateType> getDates(int from, int to) throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to getDates. SHOP ID is not set in session context.");
		}
		PersistenceManager pm = PMF.getPm();
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
	public Shop getShop(long shopId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			if (null != voShop) {
				setCurrentAttribute(CurrentAttributeType.SHOP.getValue(), voShop.getId());
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
	public List<Producer> getProducers() throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
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
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoProductCategory.class);
				pcq.setFilter("parent == :key ");
				pcq.setFilter("shops == :key ");
				List<VoProductCategory> cats = (List<VoProductCategory>) pcq.execute(currentProductCategoryId, shopId);
				List<ProductCategory> lpc = new ArrayList<ProductCategory>();
				for (VoProductCategory cat : cats) {
					lpc.add(cat.getProductCategory());
				}
				pcq.closeAll();
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

	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoProduct.class);
				pcq.setFilter("categories == :key ");
				pcq.setFilter("shops == :key ");
				List<VoProduct> ps = (List<VoProduct>) pcq.execute(categoryId, shopId);
				List<Product> lp = new ArrayList<Product>();
				for (VoProduct p : ps.subList(offset, Math.min(length, ps.size() - offset))) {
					lp.add(p.getProduct());
				}
				pcq.closeAll();
				return new ProductListPart(lp, ps.size());
			} else {
				throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProducts for shopId=" + shopId + " currentProductCategoryId=" + categoryId + "."
					+ e);
		} finally {
			pm.close();
		}
	}

	@Override
	public ProductDetails getProductDetails(long productId) throws InvalidOperation, TException {
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
	public List<Order> getOrders(int dateFrom, int dateTo) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoOrder.class);
				pcq.setFilter("shops == :key ");
				pcq.setFilter("date > :1 ");
				pcq.setFilter("date <= :2 ");
				List<VoOrder> ps = (List<VoOrder>) pcq.execute(dateFrom, dateTo);
				List<Order> lo = new ArrayList<Order>();
				for (VoOrder p : ps) {
					lo.add(p.getOrder());
				}
				pcq.closeAll();
				return lo;
			} else {
				throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrders for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	private Long getCurrentShopId() throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}
		return shopId;
	}

	private VoShop getCurrentShop(PersistenceManager _pm) throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
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

	private VoOrder getCurrentOrder(PersistenceManager _pm) throws InvalidOperation, TException {
		Long orderId = super.getCurrentAttributes().get(CurrentAttributeType.ORDER);
		if (null == orderId || 0 == orderId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is not set in session context.");
		}
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
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
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
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
	public long createOrder(int date) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		VoUser user = getCurrentUser();
		long id = new VoOrder(user, shopId.longValue(), date).getId();
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), id);
		return id;
	}

	@Override
	public long cancelOrder() throws InvalidOperation, TException {
		VoOrder currentOrder = getCurrentOrder(null);
		currentOrder.setStatus(OrderStatus.CANCELED);
		// unset current order
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
		return currentOrder.getId();
	}

	@Override
	public long confirmOrder() throws InvalidOperation, TException {
		VoOrder currentOrder = getCurrentOrder(null);
		currentOrder.setStatus(OrderStatus.CONFIRMED);
		// unset current order
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
		return currentOrder.getId();
	}

	@Override
	public long appendOrder(long oldOrderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();

			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (Entry<Long, VoOrderLine> ole : voOrder.getOdrerLines().entrySet()) {
					currentOdrerLines.put(ole.getKey(), ole.getValue().mergeWith(currentOrder, currentOdrerLines.get(ole.getKey())));
				}
				pm.makePersistent(currentOrder);
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
	public long mergeOrder(long oldOrderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();

			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (Entry<Long, VoOrderLine> ole : voOrder.getOdrerLines().entrySet()) {
					if (!currentOdrerLines.containsKey(ole.getKey())) {
						VoOrderLine oldLine = ole.getValue();
						currentOdrerLines.put(ole.getKey(), new VoOrderLine(currentOrder, oldLine.getProduct(), oldLine.getQuontity(), oldLine.getPriceType(),
								oldLine.getProduct().getPrice() * oldLine.getQuontity()));
					}
				}
				pm.makePersistent(currentOrder);
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
	public OrderLine addOrderLine(long productId, double quontity, PriceType priceType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
			if (null != voProduct) {
				currentOdrerLines.put(voProduct.getId(),
						new VoOrderLine(currentOrder, voProduct, quontity, priceType, voProduct.getPricesMap().containsKey(priceType) ? voProduct.getPricesMap()
								.get(priceType) : voProduct.getPrice()));

				pm.makePersistent(currentOrder);
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
	public boolean removeOrderLine(long productId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoOrderLine oldLine = currentOdrerLines.remove(productId);
			pm.makePersistent(currentOrder);
			return null != oldLine;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to removeOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryType(DeliveryType deliveryType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId());

			Map<Integer, Double> deliveryCosts = voShop.getDeliveryCosts();
			if (deliveryCosts.containsKey(deliveryType)) {
				currentOrder.setDeliveryCost(deliveryCosts.get(deliveryType));
				VoUser voUSer = getCurrentUser(pm);
				currentOrder.setDelivery(deliveryType);
				if (deliveryType == DeliveryType.SELF_PICKUP) {
					currentOrder.setDeliveryTo(voShop.getAddress());
				} else {
					currentOrder.setDeliveryTo(voUSer.getAddress());
				}
				pm.makePersistent(currentOrder);
			} else {
				logger.warn("" + voShop + " have no cost for delivery " + deliveryType.name() + " delivery type will not been changed");
			}
			;
			return currentOrder.getOrderDetails();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryType=" + deliveryType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean setOrderPaymentType(PaymentType paymentType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId());

			Map<Integer, Double> paymentTypes = voShop.getPaymentTypes();
			if (paymentTypes.containsKey(paymentType)) {
				double paymentFee = paymentTypes.get(currentOrder.getPaymentType());
				currentOrder.setPaymentType(paymentType);
				currentOrder.setTotalCost(currentOrder.getTotalCost() - paymentFee + paymentTypes.get(paymentType));
				currentOrder.setPaymentType(paymentType);
				pm.makePersistent(currentOrder);
				return false;
			} else {
				logger.warn("" + voShop + " have no Payment type " + paymentType.name() + " Payment type will not been changed");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentType=" + paymentType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryAddress(PostalAddress deliveryAddress) throws InvalidOperation, TException {
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
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation, TException {
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
	public void setProductPrices(Map<Long, Map<PriceType, Double>> newPricesMap) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
		try {
			for (Entry<Long, Map<PriceType, Double>> ppe : newPricesMap.entrySet()) {
				VoProduct vp = pm.getObjectById(VoProduct.class, ppe.getKey());
				vp.setPricesMap(ppe.getValue());
				pm.makePersistent(vp);
			}
			ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order prices map." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setDeliveryCosts(Map<DeliveryType, Double> newDeliveryCosts) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop currentShop = getCurrentShop(pm);
			currentShop.getDeliveryCosts().putAll( VoShop.convertFromDeliveryTypeMap(newDeliveryCosts, new HashMap<Integer,Double>()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setDeliveryCosts." + e);
		} finally {
			pm.close();
		}
	}
	
	@Override
	public void setPaymentTypesCosts(Map<PaymentType, Double> setPaymentTypesCosts) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop currentShop = getCurrentShop(pm);
			currentShop.getPaymentTypes().putAll( VoShop.convertFromPaymentTypeMap(setPaymentTypesCosts, new HashMap<Integer,Double>()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setDeliveryCosts." + e);
		} finally {
			pm.close();
		}
	}
}
=======
package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.Extent;
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
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
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
import com.vmesteonline.be.utils.Helper;

public class ShopServiceImpl extends ServiceImpl implements Iface {

	public static Logger logger = Logger.getLogger(ShopServiceImpl.class);

	public ShopServiceImpl(String sessionId) {
		super(sessionId);
	}

	@Override
	public long registerShop(Shop shop) throws InvalidOperation, TException {
		return new VoShop(shop).getId();
	}

	@Override
	public long registerProductCategory(ProductCategory productCategory, long shopId) throws InvalidOperation, TException {
		return new VoProductCategory(shopId, productCategory.getParentId(), productCategory.getName(), productCategory.getDescr(),
				productCategory.getLogoURLset(), productCategory.getTopicSet()).getId();
	}

	@Override
	public long registerProducer(Producer producer, long shopId) throws InvalidOperation, TException {
		return new VoProducer(shopId, producer.getName(), producer.getDescr(), producer.getLogoURL(), producer.getHomeURL()).getId();
	}

	@Override
	public Set<Long> uploadProducts(List<FullProductInfo> products, long shopId, boolean cleanShopBeforeUpload) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		Set<Long> productIds;
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			Set<VoProduct> shopProducts = voShop.getProducts();
			if (cleanShopBeforeUpload) {
				shopProducts.clear();
			}
			productIds = new HashSet<Long>();
			for (FullProductInfo fpi : products) {
				VoProduct voProduct = new VoProduct(shopId, fpi);
				productIds.add(voProduct.getId());
				shopProducts.add(voProduct);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to load Products. " + e.getMessage());
		} finally {
			pm.close();
		}
		return productIds;
	}

	@Override
	public Set<ProductCategory> uploadProductCategoies(Set<ProductCategory> categories, boolean relativeIds, boolean cleanShopBeforeUpload)
			throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Product categories. SHOP ID is not set in session context.");
		}
		Set<ProductCategory> categoriesCreated = new HashSet<ProductCategory>();
		Map<Long, Long> idMap = new HashMap<Long, Long>();

		PersistenceManager pm = PMF.getPm();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (cleanShopBeforeUpload) {
				voShop.getCategories().clear();
				logger.debug("All categories removed from " + voShop);
			}
			for (ProductCategory pc : categories) {
				long parentId = relativeIds && idMap.containsKey(pc.getParentId()) ? idMap.get(pc.getParentId()) : pc.getParentId();
				logger.debug("Use paret category " + parentId + " to instead of " + pc.getParentId());
				VoProductCategory vpc = new VoProductCategory(voShop.getId(), parentId, pc.getName(), pc.getDescr(), pc.getLogoURLset(), pc.getTopicSet());
				idMap.put(pc.getId(), vpc.getId());
				categoriesCreated.add(vpc.getProductCategory());
				voShop.addProductCategory(vpc);
				logger.debug("Category " + vpc + " added to " + voShop);
			}
			pm.makePersistent(voShop);
			currentTransaction.commit();
		} catch (Exception e) {
			currentTransaction.rollback();
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. " + e);
		} finally {
			pm.close();
		}
		return categoriesCreated;
	}

	@Override
	public List<Order> getFullOrders(int dateFrom, int dateTo, long userId, long shopId) throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		List<Order> ol;
		try {
			Query voquery = pm.newQuery(VoOrder.class);
			List<VoOrder> results = null;
			if (shopId != 0) {
				voquery.setFilter("shopId == theShop");
				voquery.declareParameters("theShop long");
				if (0 != userId) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId, userId);
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId);
				}
			} else {
				if (0 != userId) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, userId);
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class);
				}
			}
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
	public void updateOrderStatusesById(Map<Long, OrderStatus> orderStatusMap) throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
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
	public void setDates(Map<Integer, DateType> dateDateTypeMap) throws TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDates. SHOP ID is not set in session context.");
		}
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
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
	public List<Shop> getShops() throws InvalidOperation, TException {
		List<Shop> shops = new ArrayList<Shop>();
		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoShop> voshops = pm.getExtent(VoShop.class);
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
	public Map<Integer, DateType> getDates(int from, int to) throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to getDates. SHOP ID is not set in session context.");
		}
		PersistenceManager pm = PMF.getPm();
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
	public Shop getShop(long shopId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);
			if (null != voShop) {
				setCurrentAttribute(CurrentAttributeType.SHOP.getValue(), voShop.getId());
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
	public List<Producer> getProducers() throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
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
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoProductCategory.class);
				pcq.setFilter("parent == :key ");
				pcq.setFilter("shops == :key ");
				List<VoProductCategory> cats = (List<VoProductCategory>) pcq.execute(currentProductCategoryId, shopId);
				List<ProductCategory> lpc = new ArrayList<ProductCategory>();
				for (VoProductCategory cat : cats) {
					lpc.add(cat.getProductCategory());
				}
				pcq.closeAll();
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

	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoProduct.class);
				pcq.setFilter("categories == :key ");
				pcq.setFilter("shops == :key ");
				List<VoProduct> ps = (List<VoProduct>) pcq.execute(categoryId, shopId);
				List<Product> lp = new ArrayList<Product>();
				for (VoProduct p : ps.subList(offset, Math.min(length, ps.size() - offset))) {
					lp.add(p.getProduct());
				}
				pcq.closeAll();
				return new ProductListPart(lp, ps.size());
			} else {
				throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getProducts for shopId=" + shopId + " currentProductCategoryId=" + categoryId + "."
					+ e);
		} finally {
			pm.close();
		}
	}

	@Override
	public ProductDetails getProductDetails(long productId) throws InvalidOperation, TException {
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
	public List<Order> getOrders(int dateFrom, int dateTo) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if (null != voShop) {
				Query pcq = pm.newQuery(VoOrder.class);
				pcq.setFilter("shops == :key ");
				pcq.setFilter("date > :1 ");
				pcq.setFilter("date <= :2 ");
				List<VoOrder> ps = (List<VoOrder>) pcq.execute(dateFrom, dateTo);
				List<Order> lo = new ArrayList<Order>();
				for (VoOrder p : ps) {
					lo.add(p.getOrder());
				}
				pcq.closeAll();
				return lo;
			} else {
				throw new InvalidOperation(VoError.GeneralError, "No shop found by ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getOrders for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
	}

	private Long getCurrentShopId() throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}
		return shopId;
	}

	private VoShop getCurrentShop(PersistenceManager _pm) throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "SHOP ID is not set in session context.");
		}
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
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

	private VoOrder getCurrentOrder(PersistenceManager _pm) throws InvalidOperation, TException {
		Long orderId = super.getCurrentAttributes().get(CurrentAttributeType.ORDER);
		if (null == orderId || 0 == orderId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "ORDER ID is not set in session context.");
		}
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
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
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder voOrder = pm.getObjectById(VoOrder.class, orderId);
			if (null != voOrder) {
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
	public long createOrder(int date) throws InvalidOperation, TException {
		Long shopId = getCurrentShopId();
		VoUser user = getCurrentUser();
		long id = new VoOrder(user, shopId.longValue(), date).getId();
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), id);
		return id;
	}

	@Override
	public long cancelOrder() throws InvalidOperation, TException {
		VoOrder currentOrder = getCurrentOrder(null);
		currentOrder.setStatus(OrderStatus.CANCELED);
		// unset current order
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
		return currentOrder.getId();
	}

	@Override
	public long confirmOrder() throws InvalidOperation, TException {
		VoOrder currentOrder = getCurrentOrder(null);
		currentOrder.setStatus(OrderStatus.CONFIRMED);
		// unset current order
		setCurrentAttribute(CurrentAttributeType.ORDER.getValue(), 0);
		return currentOrder.getId();
	}

	@Override
	public long appendOrder(long oldOrderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();

			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (Entry<Long, VoOrderLine> ole : voOrder.getOdrerLines().entrySet()) {
					currentOdrerLines.put(ole.getKey(), ole.getValue().mergeWith(currentOrder, currentOdrerLines.get(ole.getKey())));
				}
				pm.makePersistent(currentOrder);
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
	public long mergeOrder(long oldOrderId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();

			VoOrder voOrder = pm.getObjectById(VoOrder.class, oldOrderId);
			if (null != voOrder) {
				for (Entry<Long, VoOrderLine> ole : voOrder.getOdrerLines().entrySet()) {
					if (!currentOdrerLines.containsKey(ole.getKey())) {
						VoOrderLine oldLine = ole.getValue();
						currentOdrerLines.put(ole.getKey(), new VoOrderLine(currentOrder, oldLine.getProduct(), oldLine.getQuontity(), oldLine.getPriceType(),
								oldLine.getProduct().getPrice() * oldLine.getQuontity()));
					}
				}
				pm.makePersistent(currentOrder);
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
	public OrderLine addOrderLine(long productId, double quontity, PriceType priceType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoProduct voProduct = pm.getObjectById(VoProduct.class, productId);
			if (null != voProduct) {
				currentOdrerLines.put(voProduct.getId(),
						new VoOrderLine(currentOrder, voProduct, quontity, priceType, voProduct.getPricesMap().containsKey(priceType) ? voProduct.getPricesMap()
								.get(priceType) : voProduct.getPrice()));

				pm.makePersistent(currentOrder);
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
	public boolean removeOrderLine(long productId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			Map<Long, VoOrderLine> currentOdrerLines = currentOrder.getOdrerLines();
			VoOrderLine oldLine = currentOdrerLines.remove(productId);
			pm.makePersistent(currentOrder);
			return null != oldLine;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to removeOrderLine Id=" + productId + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryType(DeliveryType deliveryType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId());

			Map<Integer, Double> deliveryCosts = voShop.getDeliveryCosts();
			if (deliveryCosts.containsKey(deliveryType)) {
				currentOrder.setDeliveryCost(deliveryCosts.get(deliveryType));
				VoUser voUSer = getCurrentUser(pm);
				currentOrder.setDelivery(deliveryType);
				if (deliveryType == DeliveryType.SELF_PICKUP) {
					currentOrder.setDeliveryTo(voShop.getAddress());
				} else {
					currentOrder.setDeliveryTo(voUSer.getAddress());
				}
				pm.makePersistent(currentOrder);
			} else {
				logger.warn("" + voShop + " have no cost for delivery " + deliveryType.name() + " delivery type will not been changed");
			}
			;
			return currentOrder.getOrderDetails();
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderDeliveryType=" + deliveryType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean setOrderPaymentType(PaymentType paymentType) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder = getCurrentOrder(pm);
			VoShop voShop = pm.getObjectById(VoShop.class, getCurrentShopId());

			Map<Integer, Double> paymentTypes = voShop.getPaymentTypes();
			if (paymentTypes.containsKey(paymentType)) {
				double paymentFee = paymentTypes.get(currentOrder.getPaymentType());
				currentOrder.setPaymentType(paymentType);
				currentOrder.setTotalCost(currentOrder.getTotalCost() - paymentFee + paymentTypes.get(paymentType));
				currentOrder.setPaymentType(paymentType);
				pm.makePersistent(currentOrder);
				return false;
			} else {
				logger.warn("" + voShop + " have no Payment type " + paymentType.name() + " Payment type will not been changed");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setOrderPaymentType=" + paymentType.name() + ". " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public OrderDetails setOrderDeliveryAddress(PostalAddress deliveryAddress) throws InvalidOperation, TException {
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
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation, TException {
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
	public void setProductPrices(Map<Long, Map<PriceType, Double>> newPricesMap) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
		try {
			for (Entry<Long, Map<PriceType, Double>> ppe : newPricesMap.entrySet()) {
				VoProduct vp = pm.getObjectById(VoProduct.class, ppe.getKey());
				vp.setPricesMap(ppe.getValue());
				pm.makePersistent(vp);
			}
			ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order prices map." + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setDeliveryCosts(Map<DeliveryType, Double> newDeliveryCosts) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop currentShop = getCurrentShop(pm);
			currentShop.getDeliveryCosts().putAll( Helper.copyTheMap(newDeliveryCosts, new HashMap<Integer,Double>()));
			pm.makePersistent(currentShop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to setDeliveryCosts." + e);
		} finally {
			pm.close();
		}
	}
}
>>>>>>> branch 'master' of https://github.com/trifidtrifid/vmesteonline
