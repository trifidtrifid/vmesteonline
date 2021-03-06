package com.vmesteonline.be;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import com.google.appengine.api.images.ServingUrlOptions;

/**
 * Servlet implementation class ThriftServer
 */
public class VoServlet extends HttpServlet {

	private static final long serialVersionUID = 6849485191443776061L;

	protected TProcessor processor;

	private final TProtocolFactory inProtocolFactory;
	private final TProtocolFactory outProtocolFactory;
	ServiceImpl serviceImpl;
	private final Collection<Map.Entry<String, String>> customHeaders;

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VoServlet(TProcessor processor, TProtocolFactory inProtocolFactory, TProtocolFactory outProtocolFactory) {
		super();
		this.processor = processor;
		this.inProtocolFactory = inProtocolFactory;
		this.outProtocolFactory = outProtocolFactory;
		this.customHeaders = new ArrayList<Map.Entry<String, String>>();
	}

	public VoServlet(TProtocolFactory inProtocolFactory, TProtocolFactory outProtocolFactory) {
		super();
		this.inProtocolFactory = inProtocolFactory;
		this.outProtocolFactory = outProtocolFactory;
		this.customHeaders = new ArrayList<Map.Entry<String, String>>();
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VoServlet(TProcessor processor, TProtocolFactory protocolFactory) {
		this(processor, protocolFactory, protocolFactory);
	}

	public VoServlet(TProtocolFactory protocolFactory) {
		this(protocolFactory, protocolFactory);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if( null==serviceImpl)
			throw new IOException("serviceImpl not initialized.");
		
		serviceImpl.setSession(request.getSession());
		TTransport inTransport = null;
		TTransport outTransport = null;
		try {
			response.setContentType("application/x-thrift");

			if (null != this.customHeaders) {
				for (Map.Entry<String, String> header : this.customHeaders) {
					response.addHeader(header.getKey(), header.getValue());
				}
			}
			final InputStream in = request.getInputStream();
			final OutputStream out = response.getOutputStream();

			final ByteArrayOutputStream writeBaos = new ByteArrayOutputStream();
			OutputStream writerSniffer = new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					writeBaos.write(b);
					out.write(b);
				}
			};

			final ByteArrayOutputStream readBaos = new ByteArrayOutputStream();
			InputStream readerSniffer = new InputStream() {

				@Override
				public int read() throws IOException {
					int i = in.read();
					readBaos.write(i);
					return i;
				}
			};
			TTransport transport = new TIOStreamTransport(readerSniffer, writerSniffer);
			inTransport = transport;
			outTransport = transport;

			TProtocol inProtocol = inProtocolFactory.getProtocol(inTransport);
			TProtocol outProtocol = outProtocolFactory
					.getProtocol(outTransport);
			
			processor.process(inProtocol, outProtocol);
			out.flush();

			writerSniffer.close();
			readerSniffer.close();
			System.out.println("THRIFT Got request: '" + readBaos.toString() + "'");
			System.out.println("THRIFT Sent a response: '" + writeBaos.toString() + "'");

			out.flush();
		} catch (TException te) {
			throw new ServletException(te);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void addCustomHeader(final String key, final String value) {
		this.customHeaders.add(new Map.Entry<String, String>() {
			public String getKey() {
				return key;
			}

			public String getValue() {
				return value;
			}

			public String setValue(String value) {
				return null;
			}
		});
	}

	public void setCustomHeaders(Collection<Map.Entry<String, String>> headers) {
		this.customHeaders.clear();
		this.customHeaders.addAll(headers);
	}
}
