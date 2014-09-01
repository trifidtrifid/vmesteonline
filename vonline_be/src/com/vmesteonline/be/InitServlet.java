package com.vmesteonline.be;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.utils.Defaults;

public class InitServlet extends QueuedServletWithKeyHelper {
	
	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		
		long now = System.currentTimeMillis();
		
		if( keyRequestAndQueuePush(arg0, arg1) ){
		
			Defaults.initDefaultData();
			String resultText = "Init DONE";
			sendTheResultNotification(arg0, arg1, now, resultText);
		}
	}

	
}
