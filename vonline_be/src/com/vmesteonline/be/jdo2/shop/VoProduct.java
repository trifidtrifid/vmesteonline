package com.vmesteonline.be.jdo2.shop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.PaymentType;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.utils.StorageHelper;

@PersistenceCapable
public class VoProduct {

	
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
		this.imagesURLset = new HashSet<String>();
		for (ByteBuffer imgURL : details.getImagesURLset())
			try {
				imagesURLset.add(StorageHelper.saveImage(imgURL.array()));
			} catch (IOException ie) {
				throw new InvalidOperation(VoError.IncorrectParametrs, ie.getMessage());
			}

		this.price = product.getPrice();
		this.fullDescr = details.getFullDescr();

		this.pricesMap = convertFromPriceTypeMap( details.getPricesMap(), new HashMap<Integer, Double>());
		this.optionsMap = details.getOptionsMap();

		this.categories = new HashSet<VoProductCategory>();
		this.shops = new HashSet<VoShop>();
		
		PersistenceManager pm = null==_pm ? PMF.getPm() : _pm;
		
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
			
			this.topicSet = new HashSet<VoTopic>();
			for (long topicId : details.getTopicSet()) {
				VoTopic vTopic = pm.getObjectById(VoTopic.class, topicId);
				this.topicSet.add(vTopic);
			}
			VoProducer producer = pm.getObjectById(VoProducer.class, details.getProducerId());
			this.producer = producer;
			producer.getProducts().add(this);
			pm.makePersistent(producer);
			pm.makePersistent(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "Failed to create Product" + e.getMessage());
		} finally {
			if( null==_pm) pm.close();
		}
	}

	public Product getProduct() {
		return new Product(id, name, shortDescr,weight,ByteBuffer.wrap(imageURL.getBytes()),price);
	}
	
	public ProductDetails getProductDetails() {
		ProductDetails productDetails = new ProductDetails();
		PersistenceManager pm = PMF.getPm();
		
		Set<Long> cs = new HashSet<Long>();
		for( VoProductCategory pc : getCategories()){	
			cs.add(pc.getId()); 
		}
		productDetails.setCategories(cs);
		
		Set<ByteBuffer> ius = new HashSet<ByteBuffer>();
		for( String iu : getImagesURLset()){
			ius.add( ByteBuffer.wrap(iu.getBytes()));
		}
			
		productDetails.setPricesMap( convertToPriceTypeMap(pricesMap, new HashMap<PriceType, Double>())); 
		productDetails.setOptionsMap(optionsMap);
	  Set<Long> ts = new HashSet<Long>();
	  for( VoTopic vt : getTopicSet()){
			ts.add( vt.getId().getId() );
		}
	  productDetails.setProducerId( producer.getId());
	  return productDetails;
	}
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private long id;

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
	/*@ManyToMany*/
	@Unowned
	private Set<VoProductCategory> categories;

	@Persistent
	@Unindexed
	private String fullDescr;

	@Persistent
	@Unindexed
	private Set<String> imagesURLset;

	@Persistent
	@Unindexed
	private Map<Integer, Double> pricesMap;

	@Persistent
	@Unindexed
	private Map<String, String> optionsMap;

	@Persistent
	@Unowned
	private Set<VoTopic> topicSet;

	@Persistent(mappedBy = "products")
	@Unowned
	private VoProducer producer;
	
	@Persistent(mappedBy="products")
	@Unowned
	private Set<VoShop> shops;

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

	public void setPrice(double price) {
		this.price = price;
	}

	public Set<VoProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<VoProductCategory> categories) {
		this.categories = categories;
	}

	public String getFullDescr() {
		return fullDescr;
	}

	public void setFullDescr(String fullDescr) {
		this.fullDescr = fullDescr;
	}

	public Set<String> getImagesURLset() {
		return imagesURLset;
	}

	public void setImagesURLset(Set<String> imagesURLset) {
		this.imagesURLset = imagesURLset;
	}

	public Map<PriceType, Double> getPricesMap() {
		return convertToPriceTypeMap( pricesMap, new HashMap<PriceType, Double>());
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

	public Set<VoTopic> getTopicSet() {
		return topicSet;
	}

	public void setTopicSet(Set<VoTopic> topicSet) {
		this.topicSet = topicSet;
	}

	public VoProducer getProducer() {
		return producer;
	}

	public void setProducer(VoProducer producer) {
		this.producer = producer;
	}

	public long getId() {
		return id;
	}

	
	@Override
	public String toString() {
		return "VoProduct [id=" + id + ", name=" + name + ", price=" + price + "]";
	}

	public String toFullString() {
		return "VoProduct [id=" + id + ", name=" + name + ", shortDescr=" + shortDescr + ", weight=" + weight + ", imageURL=" + imageURL + ", price="
				+ price + ", producer=" + producer + "]";
	}
	
	public static Map<Integer, Double> convertFromPriceTypeMap( Map<PriceType, Double> in, Map<Integer, Double> out){
		for( Entry<PriceType, Double> e: in.entrySet()) out.put(e.getKey().getValue(), e.getValue());
		return out;
	}
	public static Map<PriceType, Double> convertToPriceTypeMap( Map<Integer, Double> in, Map<PriceType, Double> out){
		for( Entry<Integer, Double> e: in.entrySet()) out.put( PriceType.values()[e.getKey()], e.getValue());
		return out;
	}
}
