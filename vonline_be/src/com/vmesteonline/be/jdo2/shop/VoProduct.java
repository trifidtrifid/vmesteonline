package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.utils.StorageHelper;

@PersistenceCapable
public class VoProduct {

	private VoProduct() {}

	VoProduct( long productId) {
		this.id = KeyFactory.createKey(VoProduct.class.getSimpleName(), productId);
	}

	public static VoProduct createObject(long shopId, FullProductInfo fpi, PersistenceManager _pm) throws InvalidOperation {
		VoProduct vp = new VoProduct();
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			Product product = fpi.getProduct();
			ProductDetails details = fpi.getDetails();

			VoShop shop = pm.getObjectById(VoShop.class, shopId);

			vp.name = product.getName();
			vp.shortDescr = product.shortDescr;
			vp.weight = product.getWeight();
			try {
				vp.imageURL = StorageHelper.saveImage(product.getImageURL());
			} catch (IOException ie) {
				throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
			}
			vp.imagesURLset = new ArrayList<String>();
			for (ByteBuffer imgURL : details.getImagesURLset())
				try {
					vp.imagesURLset.add(StorageHelper.saveImage(imgURL.array()));
				} catch (IOException ie) {
					throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
				}

			vp.price = product.getPrice();
			vp.fullDescr = details.getFullDescr();

			vp.pricesMap = convertFromPriceTypeMap(details.getPricesMap(), new HashMap<Integer, Double>());
			vp.optionsMap = details.getOptionsMap();

			vp.categories = new ArrayList<VoProductCategory>();
			vp.shops = new ArrayList<VoShop>();

			vp.shops.add(shop);
			pm.makePersistent(vp);
			shop.addProduct(vp);

			for (long categoryId : details.getCategories()) {
				VoProductCategory vpc = pm.getObjectById(VoProductCategory.class, categoryId);
				vpc.getProducts().add(vp);
				vp.categories.add(vpc);
				pm.makePersistent(vpc);
			}

			vp.topicSet = new ArrayList<VoTopic>();
			for (long topicId : details.getTopicSet()) {
				VoTopic vTopic = pm.getObjectById(VoTopic.class, topicId);
				vp.topicSet.add(vTopic);
			}
			VoProducer producer = pm.getObjectById(VoProducer.class, details.getProducerId());
			vp.producer = producer;
			producer.getProducts().add(vp);
			pm.makePersistent(vp);
			pm.makePersistent(producer);
			return vp;
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	public VoProduct(long shopId, FullProductInfo fpi) throws InvalidOperation {
		this(shopId, fpi, null);
	}

	public VoProduct(long shopId, FullProductInfo fpi, PersistenceManager _pm) throws InvalidOperation {
		Product product = fpi.getProduct();
		ProductDetails details = fpi.getDetails();

		this.name = product.getName();
		this.shortDescr = product.shortDescr;
		this.weight = product.getWeight();
		try {
			this.imageURL = StorageHelper.saveImage(product.getImageURL());
		} catch (IOException ie) {
			throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
		}
		this.imagesURLset = new ArrayList<String>();
		for (ByteBuffer imgURL : details.getImagesURLset())
			try {
				imagesURLset.add(StorageHelper.saveImage(imgURL.array()));
			} catch (IOException ie) {
				throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
			}

		this.price = product.getPrice();
		this.fullDescr = details.getFullDescr();

		this.pricesMap = convertFromPriceTypeMap(details.getPricesMap(), new HashMap<Integer, Double>());
		this.optionsMap = details.getOptionsMap();

		this.categories = new ArrayList<VoProductCategory>();
		this.shops = new ArrayList<VoShop>();

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;

		try {
			pm.makePersistent(this);

			VoShop shop = pm.getObjectById(VoShop.class, shopId);
			shop.addProduct(this);
			shops.add(shop);

			for (long categoryId : details.getCategories()) {
				VoProductCategory vpc = pm.getObjectById(VoProductCategory.class, categoryId);
				vpc.getProducts().add(this);
				this.categories.add(vpc);
				pm.makePersistent(vpc);
			}

			this.topicSet = new ArrayList<VoTopic>();
			for (long topicId : details.getTopicSet()) {
				VoTopic vTopic = pm.getObjectById(VoTopic.class, topicId);
				this.topicSet.add(vTopic);
			}
			VoProducer producer = pm.getObjectById(VoProducer.class, details.getProducerId());
			this.producer = producer;
			producer.getProducts().add(this);
			pm.makePersistent(this);
			pm.makePersistent(producer);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to create Product" + e.getMessage());
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	public Product getProduct() {
		return new Product(id.getId(), name, shortDescr, weight, ByteBuffer.wrap(imageURL.getBytes()), price);
	}

	public ProductDetails getProductDetails() {
		ProductDetails productDetails = new ProductDetails();

		List<Long> cs = new ArrayList<Long>();
		for (VoProductCategory pc : getCategories()) {
			cs.add(pc.getId());
		}
		productDetails.setCategories(cs);

		List<ByteBuffer> ius = new ArrayList<ByteBuffer>();
		for (String iu : getImagesURLset()) {
			ius.add(ByteBuffer.wrap(iu.getBytes()));
		}

		productDetails.setPricesMap(convertToPriceTypeMap(pricesMap, new HashMap<PriceType, Double>()));
		productDetails.setOptionsMap(optionsMap);
		List<Long> ts = new ArrayList<Long>();
		for (VoTopic vt : getTopicSet()) {
			ts.add(vt.getId().getId());
		}
		productDetails.setProducerId(producer.getId());
		productDetails.setFullDescr(fullDescr);
		productDetails.setTopicSet(ts);
		productDetails.setImagesURLset(ius);

		return productDetails;
	}

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

	@Persistent(mappedBy = "products")
	/* @ManyToMany */
	@Unowned
	private List<VoProductCategory> categories;

	@Persistent
	@Unindexed
	private String fullDescr;

	@Persistent
	@Unindexed
	private List<String> imagesURLset;

	@Persistent
	@Unindexed
	private Map<Integer, Double> pricesMap;

	@Persistent
	@Unindexed
	private Map<String, String> optionsMap;

	@Persistent
	@Unowned
	private List<VoTopic> topicSet;

	@Persistent(mappedBy = "products")
	@Unowned
	private VoProducer producer;

	@Persistent(mappedBy = "products")
	@Unowned
	private List<VoShop> shops;

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
		return pricesMap.containsKey(priceType) ? pricesMap.get(priceType) : price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<VoProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<VoProductCategory> categories) {
		this.categories = categories;
	}

	public String getFullDescr() {
		return fullDescr;
	}

	public void setFullDescr(String fullDescr) {
		this.fullDescr = fullDescr;
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

	public List<VoTopic> getTopicSet() {
		return topicSet;
	}

	public void setTopicSet(List<VoTopic> topicSet) {
		this.topicSet = topicSet;
	}

	public VoProducer getProducer() {
		return producer;
	}

	public void setProducer(VoProducer producer) {
		this.producer = producer;
	}

	public long getId() {
		return id.getId();
	}

	@Override
	public String toString() {
		return "VoProduct [id=" + id + ", name=" + name + ", price=" + price + "]";
	}

	public String toFullString() {
		return "VoProduct [id=" + id + ", name=" + name + ", shortDescr=" + shortDescr + ", weight=" + weight + ", imageURL=" + imageURL + ", price="
				+ price + ", producer=" + producer + "]";
	}

	public static Map<Integer, Double> convertFromPriceTypeMap(Map<PriceType, Double> in, Map<Integer, Double> out) {
		for (Entry<PriceType, Double> e : in.entrySet())
			out.put(e.getKey().getValue(), e.getValue());
		return out;
	}

	public static Map<PriceType, Double> convertToPriceTypeMap(Map<Integer, Double> in, Map<PriceType, Double> out) {
		for (Entry<Integer, Double> e : in.entrySet())
			out.put(PriceType.values()[e.getKey()], e.getValue());
		return out;
	}
}
