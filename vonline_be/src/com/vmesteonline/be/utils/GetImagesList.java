package com.vmesteonline.be.utils;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoProduct;

@SuppressWarnings("serial")
public class GetImagesList extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PersistenceManager pm = PMF.getNewPm();
		String content = new String();
		List<VoProduct> productsList = (List<VoProduct>) pm.newQuery(VoProduct.class).execute();
		for( VoProduct np: productsList ){
			content += np.getImageURL()+"\r\n";;
			for( String url: np.getImagesURLset()){
				content += url + "\r\n";
			}
		}
		resp.setContentType("text/plain");
		ServletOutputStream os = resp.getOutputStream();
		os.write(content.getBytes());
		os.close();
	}
}
