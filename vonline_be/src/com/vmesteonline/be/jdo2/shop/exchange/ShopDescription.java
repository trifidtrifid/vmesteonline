package com.vmesteonline.be.jdo2.shop.exchange;

import java.util.ArrayList;
import java.util.List;

import com.vmesteonline.be.shop.Shop;
import com.vmesteonline.be.utils.VoHelper;

public class ShopDescription {
	
	public ShopDescription(){}
	
	public long id;
	public String name;
	public String descr;
	public String address;
	public String logoURL;
	public long ownedId;
	public List<String> topics;
	public List<String> tags;
	
	public Shop getShop() {
		Shop shop = new Shop(id, name, descr, null, logoURL, 0L, 
				null, tags, 
				null, null, null);
		shop.topicSet = VoHelper.convertSet( topics, new ArrayList<Long>(), new Long(0));
		return shop;
	}
}
//SHOP_ID=10, SHOP_NAME,SHOP_DESCRIPTION,SHOP_ADDRESS,SHOP_LOGOURL,SHOP_OWNERID,SHOP_TOPICS,SHOP_TAGS