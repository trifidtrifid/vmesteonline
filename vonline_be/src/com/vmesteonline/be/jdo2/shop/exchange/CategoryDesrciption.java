package com.vmesteonline.be.jdo2.shop.exchange;

import java.util.ArrayList;
import java.util.List;

import com.vmesteonline.be.shop.ProductCategory;
import com.vmesteonline.be.utils.VoHelper;

public class CategoryDesrciption {

	public CategoryDesrciption() {
	}

	public long id;
	public long parentId;
	public String name;
	public String descr;
	public List<String> logoUrls;
	public List<String> topicSet;
	
	public ProductCategory getProductCategory(){
		return new ProductCategory(id, parentId, name, descr, logoUrls, VoHelper.convertSet(topicSet, new ArrayList<Long>(), new Long(0)));
	}
	//CATEGORY_ID = 200, CATEGORY_PARENT_NAME, CATEGORY_NAME, CATEGORY_DESCRIPTION, CATEGORY_LOGOURLS, CATEGORY_TOPICS
}
