package com.vmesteonline.be.access.shop;

import java.util.HashMap;
import java.util.Map;

import com.vmesteonline.be.access.VoUserAccessBaseRoles;

public class VoShopAccessRoles extends VoUserAccessBaseRoles {

	public static Long getRequiredRole(String method) {
		return methodsAccessMask.get(method);
	}

	public static final long ADMIN = 0xFFFFFFFF00000000L;
	public static final long CATALOGUE = 1L << 32;
	public static final long REPORT = 2L << 32;
	public static final long BILLING = 4L << 32;
	public static final long CUSTOMER = 8L << 32;
	protected static Map<String,Long> methodsAccessMask;

	public VoShopAccessRoles() {
		super();
	}
	
	static {
		methodsAccessMask = new HashMap<String,Long>();
		
		//SYS Admin
		fillRoleMethods( methodsAccessMask, VoUserAccessBaseRoles.SYS_ADMIN, new String[] {
				"registerShop","updateShop", "activate"
				});

		//product/content manager
		fillRoleMethods( methodsAccessMask, CATALOGUE, new String[] {
				"registerProductCategory","registerProducer","registerProduct",
				"uploadProducts","uploadProductCategoies",
				"setProductPrices","updateProduct","updateCategory","updateProducer","importData",
				"deleteProduct", "deleteCategory", "deleteProducer",
				"registerProduct","registerProducer","registerProductCategory", "getAllCategories"
		});
				

		//shop Options
		fillRoleMethods( methodsAccessMask, ADMIN, new String[] {
			"setShopDeliveryByWeightIncrement","setShopDeliveryCostByDistance","setShopDeliveryTypeAddressMasks",
			"setDate","removeDate","setDeliveryCost","setPaymentTypesCosts","updateShop",
			"setUserShopRole", "activate", "setShopPages", "totalShopReturn" 
		});


		//reports and orders
		fillRoleMethods( methodsAccessMask, REPORT, new String[] {
			"getFullOrders","getTotalOrdersReport","getTotalProductsReport","getTotalPackReport",
			"getOrders","getOrdersByStatus",
		});

		//billing and order processing
		fillRoleMethods( methodsAccessMask, BILLING, new String[] {
			"updateOrderStatusesById","setOrderPaymentStatus","setOrderStatus",
		});

		//Registered User
		fillRoleMethods( methodsAccessMask, CUSTOMER, new String[] {
			"getMyOrdersByStatus","getOrder","getOrderDetails","createOrder","updateOrder","cancelOrder",
			"deleteOrder","confirmOrder","appendOrder","mergeOrder",
			"setOrderLine","removeOrderLine","setOrderDeliveryType","setOrderPaymentType",
			"setOrderDeliveryAddress","createDeliveryAddress","getUserDeliveryAddresses","getUserDeliveryAddress",
			"deleteDeliveryAddress","getDeliveryAddressViewURL",
			"canVote","vote"
		});

		//Unregistered User 
		fillRoleMethods( methodsAccessMask, VoUserAccessBaseRoles.ANYBODY, new String[] {
				"getUserShopRole", "getProducers", "parseCSVfile","getShops","getShop","getDates","getNextOrderDate","getProducer",
				"getProductCategories","getProducts","getProductDetails","getProductsByCategories",
				"getVotes", "isActivated", "getShopPages", "getOrderDates"
		});
	}
	
	public static void fillRoleMethods( Map<String,Long> methodsAccessMask, long role, String[] methods){
		for( String mthd: methods) 
			methodsAccessMask.put(mthd, role);
	}
}