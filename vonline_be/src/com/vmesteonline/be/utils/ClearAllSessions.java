package com.vmesteonline.be.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
import com.vmesteonline.be.jdo2.VoSession;

@SuppressWarnings("serial")
public class ClearAllSessions extends HttpServlet {

	private static final Charset UTF8Cs = Charset.forName("UTF-8");
	private static Logger logger = Logger.getLogger(ClearAllSessions.class.getSimpleName());
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
	
	private void processTheRequestIntsideQueue(HttpServletRequest req, HttpServletResponse resp) {
		PersistenceManager pm = PMF.getNewPm();
		try{
			pm.deletePersistentAll(pm.newQuery(VoSession.class));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		try {
			VoHelper.forgetAllPersistent(VoSession.class, pm);
		} catch(Exception ex){
			ex.printStackTrace();
		}
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
