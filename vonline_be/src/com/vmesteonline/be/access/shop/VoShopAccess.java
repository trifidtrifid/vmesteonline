package com.vmesteonline.be.access.shop;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.vmesteonline.be.ServiceImpl;
import com.vmesteonline.be.access.VoUserAccessBase;
import com.vmesteonline.be.shop.UserShopRole;

@PersistenceCapable
@Inheritance(customStrategy = "complete-table")
public class VoShopAccess extends VoUserAccessBase {
	
	public VoShopAccess(long shopId,long userId){
		super.userId = userId;
		this.shopId = shopId; 
		super.categoryId = ServiceImpl.ServiceCategoryID.SHOP_SI.ordinal();
	}
	
	@Persistent
	private long shopId;
	
	public long getShopId() {
		return shopId;
	}
	public void setShopId(long shopId) {
		this.shopId = shopId;
	}
}
