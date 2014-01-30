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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.jdo2.shop.VoShop;
import com.vmesteonline.be.shop.DateType;
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.FullProductInto;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDetails;
import com.vmesteonline.be.shop.OrderLine;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.shop.ProductListPart;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.shop.ShopService.Iface;

public class ShopServiceImpl extends ServiceImpl implements Iface {
	
	public static Logger logger = Logger.getLogger(ShopServiceImpl.class);

	@Override
	public long registerShop(Shop shop) throws InvalidOperation, TException {
		return new VoShop(shop).getId();
	}

	@Override
	public long registerProductCategory(ProductCategory productCategory, long shopId) throws InvalidOperation, TException {
		return new VoProductCategory(shopId, productCategory.getParentId(), productCategory.getName(), 
				productCategory.getDescr(), productCategory.getLogoURLset(), productCategory.getTopicSet()).getId();
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
			if( cleanShopBeforeUpload ){
				shopProducts.clear();
			}
			productIds = new HashSet<Long>();
			for( FullProductInfo fpi : products ){
				VoProduct voProduct = new VoProduct(shopId, fpi);
				productIds.add(voProduct.getId());
				shopProducts.add( voProduct);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to load Products. "+e.getMessage());
		} finally {
			pm.close();
		}
		return productIds;
	}

	@Override
	public Set<ProductCategory> uploadProductCategoies(Set<ProductCategory> categories, boolean relativeIds, boolean cleanShopBeforeUpload)
			throws InvalidOperation, TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if( null==shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to upload Product categories. SHOP ID is not set in session context.");
		}
		Set<ProductCategory> categoriesCreated = new HashSet<ProductCategory>();
		Map<Long,Long> idMap = new HashMap<Long, Long>();
		
		PersistenceManager pm = PMF.getPm();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			if(cleanShopBeforeUpload){
				voShop.getCategories().clear();
				logger.debug("All categories removed from "+voShop);
			}
			for( ProductCategory pc : categories ){
				long parentId = relativeIds && idMap.containsKey(pc.getParentId()) ? idMap.get(pc.getParentId()) : pc.getParentId(); 
				logger.debug("Use paret category "+parentId+" to instead of "+pc.getParentId());
				VoProductCategory vpc = new VoProductCategory(voShop.getId(), parentId, pc.getName(), pc.getDescr(),
						pc.getLogoURLset(), pc.getTopicSet());
				idMap.put(pc.getId(), vpc.getId());
				categoriesCreated.add(vpc.getProductCategory());
				voShop.addProductCategory(vpc);
				logger.debug("Category "+vpc+" added to "+voShop);
			}
			pm.makePersistent(voShop);
			currentTransaction.commit();
		} catch (Exception e) {
			currentTransaction.rollback();
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to upload categories. "+e);
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
			if( shopId != 0 ) {
				voquery.setFilter("shopId == theShop");
				voquery.declareParameters("theShop long");
				if( 0!=userId ) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId, userId );
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class, shopId );
				}
			} else {
				if( 0!=userId ) {
					voquery.setFilter("user == :userKey");
					results = (List<VoOrder>) voquery.execute(VoOrder.class, userId );
				} else {
					results = (List<VoOrder>) voquery.execute(VoOrder.class );
				}
			}
			ol = new ArrayList<Order>();
			for( VoOrder vo : results) {
				ol.add(vo.getOrder());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed load orders for userID="+userId+" shopId="+shopId+ "."+e);
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
			for( Entry<Long, OrderStatus> ose : orderStatusMap.entrySet()){
				VoOrder nextVO = pm.getObjectById(VoOrder.class, ose.getKey());
				if(null==nextVO){
					logger.error("No order found by ID="+ose.getKey());
				} else {
					nextVO.setStatus(ose.getValue());
					pm.makeNontransactional(nextVO);
				}
			}
			ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order statuses."+e);
		} finally {
			pm.close();
		}
	}

	@Override
	public void setDates(Map<Integer, DateType> dateDateTypeMap) throws TException {
		Long shopId = super.getCurrentAttributes().get(CurrentAttributeType.SHOP);
		if( null==shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDates. SHOP ID is not set in session context.");
		}
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			voShop.setDates(dateDateTypeMap);
			pm.makePersistent(voShop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to set dates for shopId="+shopId+ "."+e);
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
			for( VoShop vs: voshops)
				shops.add(vs.getShop());
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed toget shops."+e);
		} finally {
			pm.close();
		}
		return shops;
	}

	@Override
	public Map<Integer, DateType> getDates(int from, int to) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shop getShop(long shopId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Producer> getProducers() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductCategory> getProductCategories(long currentProductCategoryId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductListPart getProducts(int offset, int length, long categoryId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductDetails getProductDetails(long productId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getOrders(int dateFrom, int dateTo) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long createOrder() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long cancelOrder() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long confirmOrder() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OrderLine addOrderLine(long productId, double quontity, PriceType priceType) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeOrderLine(long productId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OrderDetails setOrderDeliveryType(DeliveryType deliveryType) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setOrderPaymentType(PaymentType paymentType) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OrderDetails setOrderDeliveryAddress(PostalAddress deliveryAddress) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProductPrices(Map<Long, Map<PriceType, Double>> newPricesMap) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		Transaction ct = pm.currentTransaction();
		try {
			for( Entry<Long, Map<PriceType, Double>> ppe:newPricesMap.entrySet() ){
				VoProduct vp = pm.getObjectById(VoProduct.class, ppe.getKey());
				vp.setPricesMap(ppe.getValue());
				pm.makePersistent(vp);
			}
			ct.commit();
		} catch (Exception e) {
			e.printStackTrace();
			ct.rollback();
			throw new InvalidOperation(VoError.GeneralError, "Failed to update order prices map."+e);
		} finally {
			pm.close();
		}
	}
}
