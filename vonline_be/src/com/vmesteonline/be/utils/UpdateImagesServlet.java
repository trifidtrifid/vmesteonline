package com.vmesteonline.be.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoProduct;

@SuppressWarnings("serial")
public class UpdateImagesServlet extends HttpServlet {

	private static final Charset UTF8Cs = Charset.forName("UTF-8");
	private static Logger logger = Logger.getLogger(UpdateImagesServlet.class.getSimpleName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		//resp.setStatus(200);
		//return;
		
		logger.fine("doGet request");
		if (null == req.getHeader("X-AppEngine-QueueName")) { // it's not a queue,
																													// so run the same
																													// request but in a
																													// queue

			logger.fine("It's not a queue request>, so start in queue if production");

			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// run in queue
				logger.fine("It's PRODUCTION start queuered request!!!");

				runSameRequestInAQueue(req, resp, null);
				return;

			} else { // it's not a production so no queue reqired

				logger.fine("It's NOT a PRODUCTION so prepare the response with data!!!");

				processTheRequestIntsideQueue(req, resp);
			}

		} else { // it's a queue on production

			logger.fine("It's PRODUCTION IN QUEUE! Save data into a file!");

			processTheRequestIntsideQueue(req, resp);
		}
	}
	
	private String newPrefix = "http://vomoloko.ru";
	private void processTheRequestIntsideQueue(HttpServletRequest req, HttpServletResponse resp) {
		PersistenceManager pm = PMF.getNewPm();

		List<VoProduct> productsList = (List<VoProduct>) pm.newQuery(VoProduct.class).execute();
		for( VoProduct np: productsList ){
			if( null != np.getImageURL()) 
				np.setImageURL( newPrefix + np.getImageURL());
			List<String> newUrls = new Vector<String>();
			for( String url: np.getImagesURLset()){
				if( null != url )
					newUrls.add( newPrefix + url);
			}
		}
		pm.makePersistentAll(productsList);
	}

	public void runSameRequestInAQueue(HttpServletRequest req, HttpServletResponse resp, Map<String, String> params) throws IOException {
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions withUrl = TaskOptions.Builder.withUrl(req.getRequestURI());
		@SuppressWarnings("unchecked")
		Set<Entry<String, String[]>> entrySet = req.getParameterMap().entrySet();
		for (Entry<String, String[]> e : entrySet)
			for (String val : e.getValue())
				withUrl.param(e.getKey(), val);
		if (params != null ) for (Entry<String, String> e : params.entrySet())
				withUrl.param(e.getKey(), e.getValue());
		 
		TaskHandle th = queue.add(withUrl);
		resp.setStatus(200, "OK");
		resp.setContentType("text/plain");
		resp.getOutputStream().write(("Pushed to a queue with ID:" + th.getName()).getBytes(UTF8Cs));
	}
}
