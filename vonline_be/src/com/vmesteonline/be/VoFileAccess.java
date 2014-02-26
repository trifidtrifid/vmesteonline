package com.vmesteonline.be;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
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

	/*
	 * Method returns content of file by provided URL or deletes the file depend on
	 * presence of 'delete' parameter of request
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		serviceImpl.setSession(req.getSession());

		// TODO check user rights

		PersistenceManager pm = PMF.getPm();
		try {
			long fileId = StorageHelper.getFileId(req.getRequestURI());
			VoFileAccessRecord far = pm.getObjectById(VoFileAccessRecord.class, fileId);
			long currentUserId = serviceImpl.getCurrentUserId(pm);
			
			if (null != req.getParameter("delete")) {
			
				if (far.getUserId() == serviceImpl.getCurrentUserId(pm)) {
				
					StorageHelper.deleteImage(req.getRequestURI(),pm);
					resp.setStatus(HttpServletResponse.SC_OK, "Deleted");
					pm.deletePersistent(far);

				} else {
					resp.setStatus(HttpServletResponse.SC_FORBIDDEN, "Access denied");
				}

			} else {
				if (far.isPublic() || far.getUserId() == serviceImpl.getCurrentUserId(pm)) {
					resp.setStatus(HttpServletResponse.SC_OK, "OK");
					StorageHelper.getFile(req.getRequestURI(), resp.getOutputStream());
				} else {
					resp.setStatus(HttpServletResponse.SC_FORBIDDEN, "Access denied");
				}
			}
		} catch (InvalidOperation e){
			throw new IOException("Failed to process request:"+e.getMessage(),e);
		} finally {
			pm.close();
		}
	}
	/*
	 * Saves content of the request and returns URL as the rsponse
	 * If 'public' paramenetr of request found - file would be saved as public, private otherwise
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		serviceImpl.setSession(req.getSession());
		
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = serviceImpl.getCurrentUserId(pm);
			boolean isPublic = null != req.getParameter("public");
			resp.getOutputStream().write(StorageHelper.saveImage(req.getRequestURI(), currentUserId, isPublic, req.getInputStream(), pm).getBytes());
		} catch( InvalidOperation e){
			throw new IOException("Failed to save file:"+e.getMessage(), e);
		} finally {
			pm.close();
		}		
	}
}
