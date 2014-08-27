package com.vmesteonline.be;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.http.HttpResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.EMailHelper;
import com.vmesteonline.be.utils.VoHelper;

public class InitServlet extends HttpServlet {
	
	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		
		long now = System.currentTimeMillis();
		
		if( null==arg0.getHeader("X-AppEngine-QueueName")){ //it's not a queue, so run the same request but in the queue
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
				if( null==arg0.getParameter("key") ){
					arg1.setStatus(HttpResponse.__200_OK, "OK");
					arg1.getOutputStream().write("key parameter must be used on production".getBytes());
					return;
				} else if( !VoHelper.checkInitKey(arg0.getParameter("key"))){
					arg1.setStatus(HttpResponse.__200_OK, "OK");
					arg1.getOutputStream().write("key sent".getBytes());
					return;
				}
			}
			
			Queue queue = QueueFactory.getDefaultQueue();
			TaskHandle th = queue.add(withUrl(arg0.getRequestURI()));
			arg1.setStatus(HttpResponse.__200_OK, "OK");
			arg1.getOutputStream().write(("Pushed to a queue with ID:"+th.getName()).getBytes());
			return;
		}
		Defaults.initDefaultData();
		arg1.setStatus(HttpResponse.__200_OK, "OK");
		arg1.getOutputStream().write("Init DONE".getBytes());
		if ( SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
			EMailHelper.sendSimpleEMail("info@vmesteonline.ru", arg0.getRequestURI() +"finished", "request "+arg0.getRequestURI()+" processed. It tooks "
					+(System.currentTimeMillis() - now) +" ms");
		}
	}
}
