package com.vmesteonline.be;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.access.VoUserAccessBaseRoles;
import com.vmesteonline.be.access.shop.VoShopAccess;
import com.vmesteonline.be.access.shop.VoShopAccessRoles;
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
import com.vmesteonline.be.shop.DeliveryType;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.Order;
import com.vmesteonline.be.shop.OrderDates;
import com.vmesteonline.be.shop.OrderStatus;
import com.vmesteonline.be.shop.PaymentStatus;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Producer;
import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.shop.bo.DataSet;
import com.vmesteonline.be.shop.bo.ExchangeFieldType;
import com.vmesteonline.be.shop.bo.ImExType;
import com.vmesteonline.be.shop.bo.ImportElement;
import com.vmesteonline.be.shop.bo.ShopBOService.Iface;
import com.vmesteonline.be.utils.CSVHelper;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

public class ShopBOServiceImpl extends ServiceImpl implements Iface {
	
	public static Logger logger;

	static {
		logger = Logger.getLogger(ShopBOServiceImpl.class);
	}
	
	public ShopBOServiceImpl(String sessionId) {
		super(sessionId);
	}

	public ShopBOServiceImpl() {
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
				shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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
				shopId = ShopServiceHelper.getCurrentShopId( this, pm );
			VoShop voShop = pm.getObjectById(VoShop.class, shopId);

			productIds = new ArrayList<Long>();
			VoProduct voProduct;
			for (FullProductInfo fpi : products) {
				
				FullProductInfo fpir = fpi.details.categories == null || fpi.details.categories.size() == 0 ? fpi : VoProduct.updateCategoriesByImportId(shopId, fpi, pm);
				if( 0 != fpir.product.producerId){
					VoProducer producer = VoProducer.getByImportId(shopId, fpir.product.producerId, pm);
					if (null == producer)
						throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to find Producer:" + fpir.product.producerId + " of product:"
								+ fpi.product.getId());
	
					fpir.product.producerId = producer.getId();
				}

				if (0 != fpi.product.getId() && null != (voProduct = VoProduct.getByImportedId(shopId, fpir.product.id, pm))) {

					voProduct.update(fpir, getCurrentUserId(), pm);

				} else {
					voProduct = VoProduct.createObject(voShop, fpir, pm);
				}
				productIds.add(voProduct.getId());
			}
			removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shopId));
			
			List<VoProductCategory> pcl = (List<VoProductCategory>)pm.newQuery( VoProductCategory.class, "shopId=="+shopId).execute();
			for( VoProductCategory category: pcl){
				removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(category.getId(), shopId));
			}
			removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(0, shopId));
			
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

		dateFrom -= dateFrom % 86400;
		dateTo += ( 86400 - dateTo % 86400 );
		
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
	
	@Override
	public void setDate(OrderDates dates) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		Long shopId = super.getSessionAttribute(CurrentAttributeType.SHOP, pm);
		if (null == shopId || 0 == shopId) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to setDate. SHOP ID is not set in session context.");
		}
		try {
			VoShop voShop = pm.getObjectById(VoShop.class, shopId.longValue());
			voShop.setDates(dates);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to getDates for shopId=" + shopId + "." + e);
		} finally {
			pm.close();
		}
		
	}
	
	@Override
	public void removeDate(OrderDates dates) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		
	}
	

	
	// ======================================================================================================================
	@Override
	public void setOrderPaymentStatus(long orderId, PaymentStatus newStatus) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoOrder currentOrder =  0 == orderId ? ShopServiceHelper.getCurrentOrder( this, pm ) : pm.getObjectById(VoOrder.class, orderId);
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
		long shopId = ShopServiceHelper.getCurrentShopId( this, pm );
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
			VoShop currentShop = ShopServiceHelper.getCurrentShop( this, pm );
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
			VoShop currentShop = ShopServiceHelper.getCurrentShop( this, pm );
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
	public void updateProduct(FullProductInfo newInfoWithOldId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoProduct vop = pm.getObjectById(VoProduct.class, newInfoWithOldId.getProduct().getId());
			long cuid = getCurrentUserId(pm);
			vop.update(newInfoWithOldId, cuid, pm);
			for( Long catId: vop.getCategories())
				removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(catId, vop.getShopId()));
			
			removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(0, vop.getShopId()));
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
				StorageHelper.getFile(dataUrl, baos, null);
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
			VoShop shop = 0 == shopId ? ShopServiceHelper.getCurrentShop( this, pm ) : pm.getObjectById(VoShop.class, shopId);
			return registerProduct(fpi, shop, pm);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================
	public long registerProduct(FullProductInfo fpi, VoShop _shop, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {
			VoShop shop = _shop == null ? ShopServiceHelper.getCurrentShop( this, pm ) : _shop;
			VoProduct product = VoProduct.createObject(shop, fpi, pm);
			for( Long catId: product.getCategories())
				removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(catId, product.getShopId()));
			
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
		
		date -= date % 86400;
		DataSet ds = new DataSet();
		ds.id = 0;
		ds.name = "TotalOrdersReport";
		
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = ShopServiceHelper.getCurrentShop( this, pm );
			long currentUserId = getCurrentUserId(pm);
			
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + (0 == date ? "" : " && date == " + date) + 
					" && status == '" + OrderStatus.CONFIRMED.toString() + "'" +
					(deliveryType == DeliveryType.UNKNOWN || null == deliveryType ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();

			if( olist.size() == 0 ){
				logger.info("No orders found for Orders report.");
				return ds;
			}
			
			OrderLineDescription odInstance = new OrderLineDescription();
			OrderDescription oInstance = new OrderDescription();
			List<OrderDescription> odl = new ArrayList<OrderDescription>();
			List<List<String>> fieldsData = new ArrayList<List<String>>();

			//User ID - Order ID - product ID - Quantity Map
			Map<Long, Map<Long, Map<Long,Double>>> ordersMap = new TreeMap<Long, Map<Long,Map<Long,Double>>>();
			Map<Long,VoProduct> productsList = new TreeMap<Long, VoProduct>();
			
			Map<Long,VoUser> usersMap = new TreeMap<Long, VoUser>();		
			VoProduct vop;
			
			// collect total orders CSV
			List<List<String>> toFieldsData = new ArrayList<List<String>>();
			
			for (VoOrder voOrder : olist) {
				
				if( voOrder.getStatus() != OrderStatus.CONFIRMED )
					continue;
				
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
				
				if(null!=voOrder.getOrderLines()){
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
				} else {
					
					continue;
				}
				// collect all order line information
				ByteArrayOutputStream lbaos = new ByteArrayOutputStream();
				ImportElement ordersLinesIE = new ImportElement(ImExType.EXPORT_ORDER_LINES, "order_" + od.orderId + "_lines.csv", orderLineFIelds);
				List<List<String>> lfieldsData = new ArrayList<List<String>>();

				CSVHelper.writeCSVData(lbaos, CSVHelper.getFieldsMap(odInstance, ExchangeFieldType.ORDER_LINE_ID, orderLineFIelds), oldl, lfieldsData);

				ordersLinesIE.setFieldsData(VoHelper.matrixToList(lfieldsData));
				lbaos.close();
				byte[] fileData = lbaos.toByteArray();

				ordersLinesIE.setUrl(StorageHelper.saveImage(fileData, "text/csv", currentUserId, false, pm, ordersLinesIE.getFileName()));

				odl.add(od);
				ds.addToData(ordersLinesIE);
				
				//collect total order info
				toFieldsData.add( createModifableListFromArray( new String[]{ ""+od.orderId, od.userName, "" +user.getMobilePhone()})); //order title
				if( od.deliveryType != DeliveryType.SELF_PICKUP ) toFieldsData.add( createModifableListFromArray( new String[]{ od.deliveryAddress }));
				if( null!=od.comment && od.comment.trim().length() > 0 ) toFieldsData.add( createModifableListFromArray( new String[]{ od.comment }));
				toFieldsData.add( createModifableListFromArray( new String[]{ "-------","-------------------","----------------","-------","-------------------","----------------"})); //order
				CSVHelper.writeCSVData(lbaos, CSVHelper.getFieldsMap(odInstance, ExchangeFieldType.ORDER_LINE_ID, orderLineFIelds), oldl, toFieldsData); //orderLines
				toFieldsData.add( createModifableListFromArray( new String[]{ "-------","-------------------","----------------","-------","-------------------","----------------"})); //order
				toFieldsData.add( createModifableListFromArray( new String[]{ "Вес: "+od.weight, "Доставка: "+od.deliveryCost, "Итого: "+od.tatalCost })); //order
				toFieldsData.add( createModifableListFromArray( new String[]{ "=======","===================","================","=======","===================","================"})); //order
				toFieldsData.add( createModifableListFromArray( new String[]{ ""})); //delimiter
			}
			
		// collect total orders CSV
			ByteArrayOutputStream tobaos = new ByteArrayOutputStream();
			CSVHelper.writeCSV(tobaos, toFieldsData, null, null, null);		
			tobaos.close();
			byte[] toFileDate = tobaos.toByteArray();
			
			ImportElement ordersLinesTO = new ImportElement(ImExType.EXPORT_ORDER_LINES, "order_total_lines.csv", orderLineFIelds);
			ordersLinesTO.setUrl(StorageHelper.saveImage(toFileDate, "text/csv", currentUserId, false, pm, ordersLinesTO.getFileName()));
			ordersLinesTO.setFieldsData( VoHelper.matrixToList(toFieldsData) );
			ds.addToData(ordersLinesTO);
			
			//create orders matrix
			ds.addToData(createFullOrderMatrix(currentUserId, ordersMap, productsList, usersMap, pm));
			
			//add total orders info
			ImportElement ordersIE = new ImportElement(ImExType.EXPORT_ORDERS, "orders.csv", orderFields);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CSVHelper.writeCSVData(baos, CSVHelper.getFieldsMap(oInstance, ExchangeFieldType.ORDER_ID, orderFields), odl, fieldsData);
			ordersIE.setFieldsData( VoHelper.matrixToList(fieldsData) );
			baos.close();
			byte[] fileData2 = baos.toByteArray();
			ordersIE.setUrl(StorageHelper.saveImage(fileData2, "text/csv", currentUserId, false, pm, ordersIE.getFileName()));

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
//======================================================================================================================
	private static ArrayList<String> createModifableListFromArray(String[] array) {
		ArrayList<String> out = new ArrayList<String>( array.length );
		out.addAll( Arrays.asList( array ));
		return out;
	}
//======================================================================================================================
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
		ordersMtxIE.setUrl(StorageHelper.saveImage(fileData, "text/csv", currentUserId, false, pm, null));

		return ordersMtxIE;
	}

	// ======================================================================================================================

	@Override
	public DataSet getTotalProductsReport(int date, DeliveryType deliveryType, Map<Integer, ExchangeFieldType> productFields) throws InvalidOperation {

		date -= date % 86400;
		
		DataSet ds = new DataSet();
		ds.date = date;
		ds.id = 0;
		ds.name = "TotalProductsReport";

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = ShopServiceHelper.getCurrentShop( this, pm );
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + " && date == " + date +
					" && status == '" + OrderStatus.CONFIRMED.toString() + "'" +
					(deliveryType == DeliveryType.UNKNOWN ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();
			if( olist.size() == 0 ){
				logger.info("No orders found for TotalProductsReport.");
				return ds;
			}

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
				pIE.setUrl(StorageHelper.saveImage(baos.toByteArray(), "text/csv", currentUserId, false, pm, null));

				ds.addToData(pIE);
			}
			fbaos.close();
			fpIE.setFieldsData(VoHelper.matrixToList(ffl));
			fpIE.setUrl(StorageHelper.saveImage(fbaos.toByteArray(), "text/csv", currentUserId, false, pm, null));

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

		date -= date % 86400;
		DataSet ds = new DataSet();
		ds.date = date;
		ds.id = 0;
		ds.name = "TotalProductsPackReport";

		PersistenceManager pm = PMF.getPm();
		try {
			VoShop shop = ShopServiceHelper.getCurrentShop( this, pm );
			// import get all of orders for the shop by date
			Query q = pm.newQuery(VoOrder.class);
			q.setFilter("shopId == " + shop.getId() + " && date == " + date +
					" && status == '" + OrderStatus.CONFIRMED.toString() + "'" +
					(deliveryType == DeliveryType.UNKNOWN ? "" : " && delivery == '" + deliveryType.toString() + "'"));

			List<VoOrder> olist = (List<VoOrder>) q.execute();
			if( olist.size() == 0 ){
				logger.info("No orders found for TotalPackReport.");
				return ds;
			}

			
			// Products combined by pack size required
			SortedMap<Long, SortedMap<Double, ProductOrderDescription>> prodDescMap = new TreeMap<Long, SortedMap<Double, ProductOrderDescription>>();
			SortedSet<Double> packSizeSet = new TreeSet<Double>();
			
			for (VoOrder voOrder : olist) {

				for (Long volid : voOrder.getOrderLines().values()) {

					// TODO optimize DB requests count
					VoOrderLine vopl = pm.getObjectById(VoOrderLine.class, volid);
					VoProduct product = pm.getObjectById(VoProduct.class, vopl.getProductId());
					
					if (!product.isPrepackRequired())
						continue; // skip product that does not require prepacking

					VoProducer producer = pm.getObjectById(VoProducer.class, product.getProducer());

					ProductOrderDescription pod;

					if (!prodDescMap.containsKey(product.getId())) {
						prodDescMap.put(product.getId(), new TreeMap<Double, ProductOrderDescription>());
					}
					SortedMap<Double, ProductOrderDescription> productPackMap = prodDescMap.get(product.getId());
					
					
					Map<Double, Integer> packets = vopl.getPackets();
					if (packets == null) {
						packets = new TreeMap<Double, Integer>();
						packets.put(vopl.getQuantity(), 1);
					}
					for (Entry<Double, Integer> pqe : packets.entrySet()) {

						if (productPackMap.containsKey(pqe.getKey())) {

							pod = productPackMap.get(pqe.getKey());
							pod.orderedQuantity += pqe.getKey() * pqe.getValue();
							pod.packQuantity += pqe.getValue();
							continue;
						}
						packSizeSet.add( VoHelper.roundDouble(pqe.getKey(), 2) );
						
						productPackMap.put(pqe.getKey(), pod = new ProductOrderDescription());
						pod.producerId = producer.getId();
						pod.producerName = producer.getName();
						pod.productId = product.getId();
						pod.productName = product.getName();
						pod.minUnitSize = product.getMinProducerPack();
						pod.orderedQuantity = pqe.getKey() * pqe.getValue();
						pod.prepackRequired = product.isPrepackRequired();
						pod.packSize = pqe.getKey();
						pod.packQuantity = pqe.getValue();
						pod.deliveryType = deliveryType;
					}
				}
			}
			if( 0 < prodDescMap.size()) {
				long currentUserId = getCurrentUserId(pm);
				incapsulatePacketData(packFields, prodDescMap, ds, currentUserId, pm);
				incapsulatePaketMatrix(packSizeSet, prodDescMap, ds, currentUserId, pm);
			}

			return ds;

		} catch (InvalidOperation e) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e.what.name()+":"+ e.why);

		} catch (Exception e) {
			throw new InvalidOperation(VoError.GeneralError, "Failed to export data. " + e.getMessage());

		} finally {
			pm.close();
		}
	}
	
//=====================================================================================================================
	
	private void incapsulatePaketMatrix( SortedSet<Double> packSizeSet, SortedMap<Long, SortedMap<Double, ProductOrderDescription>> prodDescMap,
			DataSet ds, long currentUserId, PersistenceManager pm ) throws IOException {
		List<List<String>> packMatrix = new ArrayList<List<String>>();
		//create title
		ArrayList<String> title = new ArrayList<String>();
		title.add("Producer");
		title.add("Product\\Packet");
		for( Double psd: packSizeSet)
			title.add(""+VoHelper.roundDouble(psd,2));
		packMatrix.add( title );
		//fill down the content
		for( SortedMap<Double, ProductOrderDescription> pdm: prodDescMap.values() ){
			List<String> line = new ArrayList<String>();
			ProductOrderDescription pod = pdm.values().iterator().next();
			line.add( pod.producerName );
			line.add( pod.productName );
			for( Double ps : packSizeSet ){
				line.add( pdm.containsKey(ps) ? ""+pdm.get(ps).packQuantity : "" );  
			}
			packMatrix.add(line);
		}
		ImportElement packMtxIE = new ImportElement(ImExType.EXPORT_TOTAL_PACK, "pack_matrix.csv", null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CSVHelper.writeCSV(baos,packMatrix,null,null,null);
		packMtxIE.setFieldsData( VoHelper.matrixToList(packMatrix) );
		baos.close();
		byte[] fileData = baos.toByteArray();
		packMtxIE.setUrl(StorageHelper.saveImage(fileData, "text/csv", currentUserId, false, pm, null));

		ds.addToData(packMtxIE);
	}

	// =====================================================================================================================
	
	private void incapsulatePacketData(Map<Integer, ExchangeFieldType> packFields, SortedMap<Long, SortedMap<Double, ProductOrderDescription>> prodDescMap,  
			DataSet ds, long userId, PersistenceManager pm) throws IOException,
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
				pIE.setUrl(StorageHelper.saveImage(baos.toByteArray(), "text/csv", userId, false, pm, null));
	
				ds.addToData(pIE);
			}
		}
		fbaos.close();
		fpIE.setFieldsData(VoHelper.matrixToList(ffl));
		fpIE.setUrl(StorageHelper.saveImage(fbaos.toByteArray(), "text/csv", userId, false, pm, null));

		ds.addToData(fpIE);
	}

	// ======================================================================================================================


	// ======================================================================================================================
	@Override
	
	public MatrixAsList parseCSVfile(String url) throws InvalidOperation, TException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StorageHelper.getFile(url, baos, null);
			baos.close();
			List<List<String>> matrix = CSVHelper.parseCSV(baos.toByteArray(), null, null, null);
			int rowSize = 0;
			for (List<String> list : matrix) {
				if(list.size() > rowSize ) rowSize = list.size();
			}
			ArrayList<String> list = new ArrayList<String>();
			for( int row = 0; row < matrix.size(); row ++){
				list.addAll(matrix.get(row));
				for( int rest = rowSize - matrix.get(row).size(); rest > 0; rest --) list.add("");
			}
			MatrixAsList mal = new MatrixAsList(matrix.size(), list);
			return mal;
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to read data from URL '" + url + "'");
		}
	}

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


	private static Object createShopProductsByCategoryKey(long shopId) {
		return "createShopProductsByCategoryKey"+shopId;
	}
	
	// ======================================================================================================================

	
	@Override
	public void setShopDeliveryByWeightIncrement(long shopId, Map<Integer, Integer> deliveryByWeightIncrement) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoShop theShop = 0 == shopId ? ShopServiceHelper.getCurrentShop( this, pm ) : pm.getObjectById(VoShop.class, shopId );
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
			VoShop theShop = 0 == shopId ? ShopServiceHelper.getCurrentShop( this, pm ) : pm.getObjectById(VoShop.class, shopId );
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
			VoShop theShop = 0 == shopId ? ShopServiceHelper.getCurrentShop( this, pm ) : pm.getObjectById(VoShop.class, shopId );
			theShop.setDeliveryAddressMasksText(deliveryTypeAddressMasks);
			pm.makePersistent(theShop);
			
		} catch ( JDOObjectNotFoundException onfe ) {
			throw new InvalidOperation(VoError.IncorrectParametrs, "No shop found by ID:"+shopId);
		} finally {
			pm.close();
		}	
	}
	
	public Class getAuthRecordClass(){ return VoShopAccess.class; }
	
}
