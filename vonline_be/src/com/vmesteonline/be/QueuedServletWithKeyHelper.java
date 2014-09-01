package com.vmesteonline.be;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.http.HttpResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.utils.EMailHelper;
import com.vmesteonline.be.utils.VoHelper;

public class QueuedServletWithKeyHelper extends HttpServlet {

	protected void sendTheResultNotification(HttpServletRequest arg0, HttpServletResponse arg1, long now, String resultText) throws IOException {
		arg1.setStatus(HttpResponse.__200_OK, "OK");
		arg1.getOutputStream().write(resultText.getBytes());
		if ( SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
			EMailHelper.sendSimpleEMail("info@vmesteonline.ru", arg0.getRequestURI() +"finished", "request "+arg0.getRequestURI()+" processed. It tooks "
					+(System.currentTimeMillis() - now) +" ms");
		}
	}

	protected boolean keyRequestAndQueuePush(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException {
		if( null==arg0.getHeader("X-AppEngine-QueueName")){ //it's not a queue, so run the same request but in the queue
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
				if( null==arg0.getParameter("key") ){
					arg1.setStatus(HttpResponse.__200_OK, "OK");
					arg1.getOutputStream().write("key parameter must be used on production".getBytes());
					return false;
				} else if( !VoHelper.checkInitKey(arg0.getParameter("key"))){
					arg1.setStatus(HttpResponse.__200_OK, "OK");
					arg1.getOutputStream().write("key sent".getBytes());
					return false;
				}
			}
			
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions withUrl = withUrl(arg0.getRequestURI());
			@SuppressWarnings("unchecked")
			Set<Entry<String,String[]>> entrySet = arg0.getParameterMap().entrySet();
			for( Entry<String,String[]> e :entrySet ) for( String val: e.getValue() )
				withUrl.param(e.getKey(), val);
			TaskHandle th = queue.add(withUrl);
			arg1.setStatus(HttpResponse.__200_OK, "OK");
			arg1.getOutputStream().write(("Pushed to a queue with ID:"+th.getName()).getBytes());
			return false;
		} else {
			return true;
		}
	}
}
