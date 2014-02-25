package com.vmesteonline.be.access;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class VoShopAccess extends VoUserAccessBase {
	
	public static final long OWNER = 0xFFFFFFFF000000L;
	public static final long CHANGE_CATALOGUE = 1L << 32;
	public static final long CHANGE_PRICES = 2L << 32;
	public static final long EXPORT_REPORT = 4L << 32;
	public static final long IMPORT_CATALOGUE = 8L << 32;
	public static final long CHANGE_USER_PRIVILEGES = 16L << 32;
	public static final long VIEW_ORDERS = 32L  << 32 ;
	public static final long CHANGE_ORDERS = 32L  << 32;
	public static final long PAYMENT_OPERATIONS = 64L  << 32;
	
	@Persistent
	private long shopId;
}
