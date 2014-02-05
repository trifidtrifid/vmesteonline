package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class MessageServiceServlet extends VoServlet {

    public MessageServiceServlet() {
        super(new TJSONProtocol.Factory());
        MessageServiceImpl servImpl = new MessageServiceImpl();
        serviceImpl = servImpl;
        super.setProcessor(new MessageService.Processor<MessageServiceImpl>(servImpl));
    }

    public MessageServiceServlet(TProtocolFactory protocolFactory) {
		super(new TJSONProtocol.Factory());		
		MessageServiceImpl servImpl = new MessageServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new MessageService.Processor<MessageServiceImpl>(servImpl));
	}	

	private static final long serialVersionUID = -9014665255913474234L;
}
