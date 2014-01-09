package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TServlet;

import com.vmesteonline.be.GroupService;

public class GroupServiceServlet extends TServlet {
	private static final long serialVersionUID = -1277374365828109223L;

	public GroupServiceServlet() {
		super(
				new GroupService.Processor<GroupServiceImpl>(
				new GroupServiceImpl()),
				new TJSONProtocol.Factory()
		);
	}
}

