package com.vmesteonline.be.utils;

import java.io.IOException;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.taglibs.standard.tei.ForEachTEI;

import com.google.api.client.http.HttpResponse;
import com.vmesteonline.be.ServiceImpl;
import com.vmesteonline.be.ShopServiceHelper;
import com.vmesteonline.be.ShopServiceImpl;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.jdo2.shop.VoShop;

@SuppressWarnings("serial")
public class UpdateSomething extends HttpServlet {

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoProductCategory> vpc = pm.getExtent(VoProductCategory.class);
			for( VoProductCategory pc : vpc ){
				ServiceImpl.removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(pc.getId(), pc.getShopId()));
			}
			long shopId = vpc.iterator().next().getShopId();
			ServiceImpl.removeObjectFromCache(ShopServiceImpl.createShopProductsByCategoryKey(shopId));
			ServiceImpl.removeObjectFromCache(ShopServiceHelper.getProcutsOfCategoryCacheKey(0, shopId));
			
		} finally {
			pm.close();
		}
		arg1.setStatus(HttpServletResponse.SC_OK);
	}
}
