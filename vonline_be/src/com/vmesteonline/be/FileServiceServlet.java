package com.vmesteonline.be;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.access.VoServiceMapAccessValidator;

@SuppressWarnings("serial")
public class FileServiceServlet extends VoServlet {

	public FileServiceServlet() {
		super(new TJSONProtocol.Factory());
		FileServiceImpl servImpl = new FileServiceImpl();
		serviceImpl = servImpl;
		TBaseProcessor<FileServiceImpl> proc = new FileService.Processor<FileServiceImpl>(servImpl);
		proc.setAccessValidator( new VoServiceMapAccessValidator(servImpl));
		super.setProcessor(proc);
	}
}
