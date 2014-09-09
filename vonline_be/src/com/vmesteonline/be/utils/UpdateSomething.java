package com.vmesteonline.be.utils;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.ServiceImpl;
import com.vmesteonline.be.ShopServiceHelper;
import com.vmesteonline.be.ShopServiceImpl;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;

@SuppressWarnings("serial")
public class UpdateSomething extends HttpServlet {

	private static Logger logger = Logger.getLogger(UpdateSomething.class.getSimpleName());
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
