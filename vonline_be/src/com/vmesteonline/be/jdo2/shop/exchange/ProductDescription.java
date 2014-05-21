package com.vmesteonline.be.jdo2.shop.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.shop.FullProductInfo;
import com.vmesteonline.be.shop.PriceType;
import com.vmesteonline.be.shop.Product;
import com.vmesteonline.be.shop.ProductDetails;
import com.vmesteonline.be.utils.VoHelper;

public class ProductDescription {

	public ProductDescription() {}
	
//PRODUCT_ID=300, PRODUCT_NAME,	PRODUCT_SHORT_DESCRIPTION, PRODUCT_WEIGHT, PRODUCT_IMAGEURL, PRODUCT_PRICE, PRODUCT_CATEGORY_IDS,
//PRODUCT_FULL_DESCRIPTION, PRODUCT_IMAGE_URLS, 
//PRODUCT_PRICE_RETAIL, PRODUCT_PRICE_INET, PRODUCT_PRICE_VIP, PRODUCT_PRICE_SPECIAL,
//PRODUCT_OPIONSAVP, PRODUCT_TOPICS, PRODUCT_PRODUCER_ID, PRODUCT_MIN_CLN_PACK_G, PRODUCT_MIN_PROD_PACK_G, 
//PRODUCT_PREPACK_REQ, PRODUCT_KNOWN_NAMES, PRODUCT_UNUT_NAME

	public long id;
	public String name;
	public String shortDescr;
	public double weight;
	public String imageURL;
	public double price;
	public List<String> categories;
	public String fullDescr;
	public List<String> imagesURLset;
	public Double priceRetail;
	public Double priceInet;
	public Double priceVIP;
	public Double priceSpecial;
	public Map<String, String> optionsMap;
	public List<String> topicSet;
	public long producerId;
	public double minClientPack;
	public double minProducerPack;
	public boolean prepackRequired;
	public Set<String> knownNames;
	public String unitName;
	
	public FullProductInfo getFullProductInfo(PersistenceManager pm){
		
	//the last argument is the Shop id but it is unknown here
		Product product = new Product(id, name, shortDescr, weight, imageURL, price, unitName,minClientPack,0,
				prepackRequired, producerId); 
		
		Map<PriceType, Double> pricesMap = new HashMap<PriceType, Double>();
		if( null != priceRetail ) pricesMap.put(PriceType.RETAIL, priceRetail);
		if( null != priceInet ) pricesMap.put(PriceType.INET, priceInet);
		if( null != priceSpecial ) pricesMap.put(PriceType.SPECIAL, priceSpecial);
		if( null != priceVIP ) pricesMap.put(PriceType.VIP, priceVIP);
		
		List<Long> topics = VoHelper.convertSet(topicSet, new ArrayList<Long>(), new Long(0));
		List<Long> categoriesSet = VoHelper.convertSet(categories, new ArrayList<Long>(), new Long(0));
		ProductDetails details = new ProductDetails(categoriesSet, fullDescr, imagesURLset, pricesMap , optionsMap, topics, 
				minProducerPack, knownNames );
		FullProductInfo fpi = new FullProductInfo(product, details);
		return fpi;
	}
	
}
