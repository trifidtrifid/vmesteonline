package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

@PersistenceCapable(detachable="true")
public class VoProduct implements Serializable {

	//private static Logger logger = Logger.getLogger(VoProduct.class);
	
	private VoProduct() {
	}

	VoProduct(long productId) {
		this.id = KeyFactory.createKey(VoProduct.class.getSimpleName(), productId);
		importId = 0;
	}

	// =====================================================================================================================
	public void update(FullProductInfo newInfo, long userId, PersistenceManager _pm) throws InvalidOperation {
		try {
			
						if(null!=newInfo.details.pricesMap)
				for( Entry<PriceType, Double> pme : newInfo.details.pricesMap.entrySet())
					this.pricesMap.put( pme.getKey().getValue(), pme.getValue());

			VoHelper.copyIfNotNull(this, "producerId", newInfo.product.producerId);
			VoHelper.copyIfNotNull(this, "minClientPack", newInfo.product.minClientPack);
			VoHelper.copyIfNotNull(this, "minProducerPack", newInfo.details.minProducerPack);
			VoHelper.copyIfNotNull(this, "prepackRequired", newInfo.product.prepackRequired);
			
			VoHelper.copyIfNotNull(this, "name", newInfo.product.name);
			VoHelper.copyIfNotNull(this, "shortDescr",newInfo.product.shortDescr);
			VoHelper.copyIfNotNull(this, "weight",newInfo.product.weight);
			VoHelper.copyIfNotNull(this, "minClientPack",newInfo.product.minClientPack);
			try {
				String imageURL2 = newInfo.product.getImageURL();
				if( null != imageURL2 && imageURL2.trim().length() > 0 ) 
					this.imageURL =  StorageHelper.replaceImage( imageURL2, this.imageURL, userId, true, _pm);
			} catch (Exception e) {
				e.printStackTrace();
				//setImageURL(null);
			}
			VoHelper.copyIfNotNull(this, "price",newInfo.product.price);
			VoHelper.copyIfNotNull(this, "fullDescr",newInfo.details.fullDescr);
			
			if(null!=newInfo.details.getImagesURLset() && newInfo.details.getImagesURLset().size() > 0){
				this.imagesURLset = new ArrayList<String>();
			
				for (String imgURL : newInfo.details.getImagesURLset())
					try {
						this.imagesURLset.add(StorageHelper.saveImage(imgURL, userId, true, _pm));
					} catch (IOException ie) {
						ie.printStackTrace();
						//throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
					}
			}

			if( null!=newInfo.details.getPricesMap() ) 
				this.pricesMap.putAll( convertFromPriceTypeMap(newInfo.details.getPricesMap(), new HashMap<Integer, Double>()) );
			
			if( null!=newInfo.details.optionsMap ) setOptionsMap(optionsMap);
			
			if (null != newInfo.details.getTopicSet()){
				this.topicSet = new ArrayList<Long>();
				this.topicSet.addAll(newInfo.details.getTopicSet());
			}
			VoHelper.copyIfNotNull(this, "unitName", newInfo.product.unitName);
			
			this.setKnownNames( new HashSet<String>() );
			if( null!=newInfo.details.knownNames )
				this.knownNames.addAll(newInfo.details.knownNames);
			
			this.socialNetworks = newInfo.details.socialNetworks;
			
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		_pm.makePersistent(this);
		
		if( null!=newInfo.details.categories &&  0 != newInfo.details.categories.size() ) updateCategoriesList(newInfo, _pm);
	}

	// =====================================================================================================================
	private void updateCategoriesList(FullProductInfo newInfo, PersistenceManager _pm) {
		// remove categories if they not included to new list
		for( Long opcid: this.categories){
			VoProductCategory pcat = _pm.getObjectById(VoProductCategory.class, opcid);
			pcat.updateProductCount(-1,_pm);
			_pm.makePersistent(pcat);
		}
		this.categories = new ArrayList<Long>();
		this.categories.addAll(newInfo.details.getCategories());
		for( Long opcid: this.categories){
			VoProductCategory pcat = _pm.getObjectById(VoProductCategory.class, opcid);
			pcat.updateProductCount(1,_pm);
			_pm.makePersistent(pcat);
		}
		
		/*
		 * List<Long> categoriesFromNewList = new ArrayList<Long>(); for( VoProductCategory vpc: this.getCategories()){ if(
		 * newInfo.details.categories.contains(vpc.getId())) categoriesFromNewList.add(vpc.getId()); else { for( VoProduct vp: vpc.getProducts()){ if(
		 * vp.getId() == this.getId() ) { vpc.getProducts().remove(vp); break; } } } } for( Long pcid: newInfo.details.categories ){
		 * 
		 * if( !categoriesFromNewList.contains( pcid )){ // if it's an added category for the product VoProductCategory nvpc =
		 * _pm.getObjectById(VoProductCategory.class, pcid); nvpc.addProduct(this); this.categories.add(nvpc); } }
		 */
	}

	// =====================================================================================================================
	public static VoProduct createObject(VoShop shop, long importId, FullProductInfo fpi, PersistenceManager _pm) throws InvalidOperation {
		return createObject(shop, fpi, _pm);
	}

	public static VoProduct createObject(VoShop shop, FullProductInfo fpi, PersistenceManager pm) throws InvalidOperation {
		VoProduct vp = new VoProduct();
		
		Product product = fpi.getProduct();
		ProductDetails details = fpi.getDetails();

		vp.name = product.getName();
		vp.shortDescr = product.shortDescr;
		vp.weight = product.getWeight();
	
		if (null != product.getImageURL() && product.getImageURL().length() > 0)
			try {
				vp.imageURL = StorageHelper.saveImage(product.getImageURL(), shop.ownerId, true, pm);
			} catch (Throwable ie) {
				ie.printStackTrace();
				// throw new InvalidOperation(VoError.IncorrectParametrs,
				// ie.getMessage());
			}
		vp.imagesURLset = new ArrayList<String>();
		if (null != details.getImagesURLset())
			for (String imgURL : details.getImagesURLset())
				if (null != imgURL && imgURL.length() > 0)
					try {
						vp.imagesURLset.add(StorageHelper.saveImage(imgURL, shop.ownerId, true, pm));
					} catch (Throwable ie) {
						//logger.warn("Failed to update image from URL: '"+imgURL+"'. "+ie.getMessage());
						// throw new InvalidOperation(VoError.IncorrectParametrs,
						// ie.getMessage());
					}

		vp.price = product.getPrice();
		vp.setScore(1);
		vp.setFullDescr(details.getFullDescr());

		vp.pricesMap = convertFromPriceTypeMap(details.getPricesMap(), new HashMap<Integer, Double>());
		vp.optionsMap = details.getOptionsMap();

		vp.shopId = shop.getId();
		vp.categories = details.getCategories();
		for (Long cid : vp.categories) {
			VoProductCategory pcat = pm.getObjectById(VoProductCategory.class, cid);
			pcat.updateProductCount(1,pm);
			pm.makePersistent(pcat);
		}
		vp.topicSet = details.getTopicSet();

		/*VoProducer producer = */pm.getObjectById(VoProducer.class, product.getProducerId());
		vp.producerId = product.getProducerId();
		vp.minClientPack = product.minClientPack;
		vp.minProducerPack = details.minProducerPack;
		vp.prepackRequired = product.prepackRequired;
		vp.knownNames = new HashSet<String>();
		if (details.knownNames != null)
			for (String name : details.knownNames) {
				vp.knownNames.add(name);
			}
		vp.unitName = product.unitName;
		vp.importId = product.id;
		vp.socialNetworks = details.socialNetworks;

		pm.makePersistent(vp);
		return vp;

	}

	// =====================================================================================================================

	public void markDeleted(){
		importId = -1;
	}
	
	public boolean isDeleted(){
		return importId == -1;
	}
	
	// =====================================================================================================================
	public VoProduct(long shopId, FullProductInfo fpi) throws InvalidOperation {
		this(shopId, fpi, null);
	}

	// =====================================================================================================================
	public VoProduct(long shopId, FullProductInfo fpi, PersistenceManager pm) throws InvalidOperation {
		this.setScore(1);
		Product product = fpi.getProduct();
		ProductDetails details = fpi.getDetails();

		this.name = product.getName();
		this.shortDescr = product.shortDescr;
		this.weight = product.getWeight();

		this.price = product.getPrice();
		this.setFullDescr(details.getFullDescr());

		this.pricesMap = convertFromPriceTypeMap(details.getPricesMap(), new HashMap<Integer, Double>());
		this.optionsMap = details.getOptionsMap();

		this.categories = new ArrayList<Long>();
		this.shopId = shopId;
		this.knownNames = new HashSet<String>();
		this.unitName = product.unitName;
		this.importId = product.id;
		this.minClientPack = product.minClientPack;
		this.minProducerPack = details.minProducerPack;
		this.prepackRequired = product.prepackRequired;
		
		try{
			VoShop shop = pm.getObjectById(VoShop.class, shopId);

			try {
				this.imageURL = StorageHelper.saveImage(product.getImageURL(), shop.getOwnerId(), true, pm);
			} catch (IOException ie) {
				throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
			}
			this.imagesURLset = new ArrayList<String>();
			for (String imgURL : details.getImagesURLset())
				try {
					imagesURLset.add(StorageHelper.saveImage(imgURL, shop.getOwnerId(), true, pm));
				} catch (IOException ie) {
					throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
				}

			for (long categoryId : details.getCategories()) {
				VoProductCategory pcat = pm.getObjectById(VoProductCategory.class, categoryId);
				pcat.updateProductCount(1,pm);
				pm.makePersistent(pcat);
				this.categories.add(categoryId);
			}

			pm.getObjectById(VoProducer.class, product.getProducerId());
			this.producerId = product.getProducerId();
			
			this.socialNetworks = details.socialNetworks;

			pm.makePersistent(this);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to create Product" + e.getMessage());
		} 
	}

	// =====================================================================================================================
	public Product getProduct(PersistenceManager pm) {
		//@TODO the price should depend on shop type of day of order. Now use INET version if set, RETAIL otherwise and regular at last
		double thePrice = pricesMap == null || pricesMap.size() == 0 ? price : 
			null == pricesMap.get(PriceType.INET.getValue()) ? 
					pricesMap.get(PriceType.RETAIL.getValue()) == null ? price : pricesMap.get(PriceType.RETAIL.getValue()) :
						pricesMap.get(PriceType.INET.getValue());
		Product product = new Product(id.getId(), name, shortDescr, weight, imageURL, thePrice, unitName, minClientPack, shopId, prepackRequired,producerId);
		return product;
	}

	public ProductDetails getProductDetails() {
		ProductDetails productDetails = new ProductDetails();

		productDetails.setCategories(getCategories());
		productDetails.setPricesMap(convertToPriceTypeMap(pricesMap, new HashMap<PriceType, Double>()));
		productDetails.setOptionsMap(optionsMap);
		productDetails.setFullDescr(fullDescr.getValue());
		productDetails.setTopicSet(getTopicSet());
		productDetails.setImagesURLset(getImagesURLset());
		productDetails.setKnownNames(knownNames);
		productDetails.setSocialNetworks(socialNetworks);

		return productDetails;
	}
	
	public long getProducerId() {
		return producerId;
	}

	public void setProducerId(long producerId) {
		this.producerId = producerId;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public void setFullDescr(Text fullDescr) {
		this.fullDescr = fullDescr;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public void setMinClientPack(double minClientPack) {
		this.minClientPack = minClientPack;
	}
	public void setMinClientPack(Double minClientPack) {
		this.minClientPack = minClientPack;
	}

	public void setMinProducerPack(double minProducerPack) {
		this.minProducerPack = minProducerPack;
	}
	
	public void setMinProducerPack(Double minProducerPack) {
		this.minProducerPack = minProducerPack;
	}

	public void setKnownNames(Set<String> knownNames) {
		this.knownNames = knownNames;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setImportId(long importId) {
		this.importId = importId;
	}


	// =====================================================================================================================
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String shortDescr;

	@Persistent
	@Unindexed
	private double weight;

	@Persistent
	@Unindexed
	private String imageURL;

	@Persistent
	@Unindexed
	private double price;
	
	@Persistent
	private double score;

	@Persistent
	private List<Long> categories;

	@Persistent
	@Unindexed
	private Text fullDescr;

	@Persistent
	@Unindexed
	private List<String> imagesURLset;

	@Persistent(defaultFetchGroup="true")
	@Unindexed
	private Map<Integer, Double> pricesMap;

	@Persistent
	@Unindexed
	private Map<String, String> optionsMap;

	@Persistent
	@Unindexed
	private List<Long> topicSet;

	@Persistent
	private long shopId;

	@Persistent
	private long producerId;

	@Persistent
	@Unindexed
	private double minClientPack;

	@Persistent
	@Unindexed
	private double minProducerPack;

	@Persistent
	@Unindexed
	private boolean prepackRequired;

	@Persistent
	@Unindexed
	private Set<String> knownNames;

	@Persistent
	@Unindexed
	private String unitName;

	@Persistent
	private long importId;
	
	@Persistent
	@Unindexed
	private Map<String,String> socialNetworks;
	
	public Map<String, String> getSocialNetworks() {
		return socialNetworks;
	}

	public void setSocialNetworks(Map<String, String> socialNetworks) {
		this.socialNetworks = socialNetworks;
	}

	public double getMinClientPack() {
		return minClientPack;
	}

	public void setMinClientPackGramms(Double minClientPack) {
		this.minClientPack = minClientPack;
	}

	public double getMinProducerPack() {
		return minProducerPack;
	}

	public void setMinProducerPackGramms(Double minProducerPack) {
		this.minProducerPack = minProducerPack;
	}

	public boolean isPrepackRequired() {
		return prepackRequired;
	}

	public void setPrepackRequired(Boolean prepackRequired) {
		this.prepackRequired = prepackRequired;
	}

	public long getShopId() {
		return shopId;
	}

	public Set<String> getKnownNames() {
		return knownNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescr() {
		return shortDescr;
	}

	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public double getPrice() {
		return price;
	}

	public double getPrice(PriceType priceType) {
		if( null==priceType )
			priceType = PriceType.INET;
		
		return null==pricesMap || !pricesMap.containsKey( priceType.getValue() ) ? price : pricesMap.get(priceType.getValue());
	}

	public void setPrice(double price) {
		this.price = price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Long> getCategories() {
		return categories;
	}

	public void setCategories(List<Long> categories) {
		this.categories = categories;
	}

	public String getFullDescr() {
		return fullDescr.getValue();
	}

	public void setFullDescr(String fullDescr) {
		this.fullDescr = new Text(fullDescr == null ? "" : fullDescr);
	}

	public List<String> getImagesURLset() {
		return imagesURLset;
	}

	public void setImagesURLset(List<String> imagesURLset) {
		this.imagesURLset = imagesURLset;
	}

	public Map<PriceType, Double> getPricesMap() {
		return convertToPriceTypeMap(pricesMap, new HashMap<PriceType, Double>());
	}

	public void setPricesMap(Map<PriceType, Double> pricesMap) {
		this.pricesMap = convertFromPriceTypeMap(pricesMap, new HashMap<Integer, Double>());
	}

	public Map<String, String> getOptionsMap() {
		return optionsMap;
	}

	public void setOptionsMap(Map<String, String> optionsMap) {
		this.optionsMap = optionsMap;
	}

	public List<Long> getTopicSet() {
		return topicSet;
	}

	public void setTopicSet(List<Long> topicSet) {
		this.topicSet = topicSet;
	}

	public void setProducerId(Long producer) {
		this.producerId = producer;
	}

	public long getId() {
		return id.getId();
	}

	@Override
	public String toString() {
		return "VoProduct [id=" + id + ", name=" + name + ", score=" + score + "]";
	}

	public String toFullString() {
		return "VoProduct [id=" + id + ", name=" + name + ", shortDescr=" + shortDescr + ", weight=" + weight + ", imageURL=" + imageURL + ", price="
				+ price + ", producerId=" + producerId + "]";
	}

	public static Map<Integer, Double> convertFromPriceTypeMap(Map<PriceType, Double> in, Map<Integer, Double> out) {
		if (null == in)
			return null;
		for (Entry<PriceType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static Map<PriceType, Double> convertToPriceTypeMap(Map<Integer, Double> in, Map<PriceType, Double> out) {
		if (null == in)
			return null;
		for (Entry<Integer, Double> e : in.entrySet())
			out.put(PriceType.values()[e.getKey()], e.getValue());
		return out;
	}

	public static FullProductInfo updateCategoriesByImportId(long shopId, FullProductInfo fpi, PersistenceManager pm) throws InvalidOperation {
		FullProductInfo res = new FullProductInfo(fpi);
		res.details.categories = new ArrayList<Long>();
		Query q = pm.newQuery(VoProductCategory.class);
		q.setFilter("importId == importIdParam && shopId == " + shopId);
		q.declareParameters("long importIdParam");
		if(null == fpi.details || null == fpi.details.categories || fpi.details.categories.size() == 0 )
			throw new InvalidOperation(VoError.IncorrectParametrs, "No Category set for product " + fpi.product.id);
		
		for (Long pc : fpi.details.categories) {
			List<VoProductCategory> cl = (List<VoProductCategory>) q.execute(pc);
			if (0 == cl.size()) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "No Category found for Product ID:" + fpi.product.id + " By Category ID:" + pc);
			}
			for (VoProductCategory voProductCategory : cl) {
				res.details.categories.add(voProductCategory.getId());
				break;
			}
		}
		return res;
	}

	public static VoProduct getByImportedId(long shopId, long importedId, PersistenceManager pm) throws InvalidOperation {
		Query q = pm.newQuery(VoProduct.class);
		q.setFilter("importId == " + importedId + " && shopId == " + shopId);
		List<VoProduct> cl = (List<VoProduct>) q.execute(importedId);
		for (VoProduct voProduct : cl) {
			return voProduct;
		}
		return null;
	}

	public Long getImportId() {
		return importId;
	}

	public String getUnitName() {
		return unitName;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
}
