package com.vmesteonline.be;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.utils.StorageHelper;

/**
 * A simple servlet that proxies reads and writes to its Google Cloud Storage
 * bucket.
 */
@SuppressWarnings("serial")
public class VoFileAccess extends HttpServlet {

	ServiceImpl serviceImpl;

	public VoFileAccess() {
		super();
		this.serviceImpl = new ServiceImpl();
	}

	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		serviceImpl.setSession(req.getSession());

		// TODO check user rights
		
		if(null!=req.getParameter("delete")){
			if( StorageHelper.deleteImage(req.getRequestURI())) {
				resp.setStatus(HttpServletResponse.SC_OK, "Deleted");
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND, "No file '"+req.getRequestURI()+"' found");
			}
		} else {
			StorageHelper.getFile( req.getRequestURI(), resp.getOutputStream());
		}
	}

	/**
	 * Writes the payload of the incoming post as the contents of a file to GCS.
	 * If the request path is /file/Foo/Bar this will be interpreted as a request
	 * to create a GCS file named Bar in bucket Foo.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		serviceImpl.setSession(req.getSession());
		// TODO create user rights

		resp.getOutputStream().write( StorageHelper.saveImage( req.getRequestURI(), req.getInputStream()).getBytes());
	}
}
