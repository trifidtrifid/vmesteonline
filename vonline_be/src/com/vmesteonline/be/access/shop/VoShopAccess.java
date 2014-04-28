package com.vmesteonline.be.access.shop;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.vmesteonline.be.access.VoUserAccessBase;

@PersistenceCapable
@Inheritance(customStrategy = "complete-table")
public class VoShopAccess extends VoUserAccessBase {
	
	@Persistent
	private long shopId;
	
	public long getShopId() {
		return shopId;
	}
	public void setShopId(long shopId) {
		this.shopId = shopId;
	}
}
