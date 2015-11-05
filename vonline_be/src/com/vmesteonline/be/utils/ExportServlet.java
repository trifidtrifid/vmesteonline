package com.vmesteonline.be.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.datanucleus.store.types.converters.SerializableByteArrayConverter;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoCity;
import com.vmesteonline.be.jdo2.postaladdress.VoCountry;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.jdo2.shop.VoOrder;
import com.vmesteonline.be.jdo2.shop.VoOrderLine;
import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.jdo2.shop.VoShop;

@SuppressWarnings("serial")
public class ExportServlet extends HttpServlet {

	private static final Charset UTF8Cs = Charset.forName("UTF-8");

	public ExportServlet() {
		super();
	}

	private static Logger logger = Logger.getLogger(ExportServlet.class.getName());
	private static SerializableByteArrayConverter sbac = new SerializableByteArrayConverter();

	/*
	 * private static String thriftClassessMask =
	 * "com\\.vmesteonline\\.be\\.(AuthService|Building|City|Country|CurrentAttributeType|FileService|Friendship|FriendshipType|FullAddressCatalogue|Group|IdName|IdNameChilds|InvalidOperation|MatrixAsList|PostalAddress|RelationsType|Rubric|ShortProfile|ShortUserInfo|Street|UserContacts|UserInfo|UserService|UserStatus|VoError|shop\\.DateType|shop\\.DeliveryType|shop\\.FullOrder|shop\\.FullProductInfo|shop\\.Order|shop\\.OrderDate|shop\\.OrderDates|shop\\.OrderDatesType|shop\\.OrderDetails|shop\\.OrderLine|shop\\.OrderStatus|shop\\.OrderUpdateInfo|shop\\.PaymentStatus|shop\\.PaymentType|shop\\.PriceType|shop\\.Producer|shop\\.Product|shop\\.ProductCategory|shop\\.ProductDetails|shop\\.ProductListPart|shop\\.Shop|shop\\.ShopFEService|shop\\.ShopPages|shop\\.UserShopRole|shop\\.be\\.DataSet|shop\\.be\\.ExchangeFieldType|shop\\.be\\.ImExType|shop\\.be\\.ImportElement|shop\\.be\\.ShopBOService)"
	 * ; private static String thriftClassessPathMask =
	 * "com/vmesteonline/be/(AuthService|Building|City|Country|CurrentAttributeType|FileService|Friendship|FriendshipType|FullAddressCatalogue|Group|IdName|IdNameChilds|InvalidOperation|MatrixAsList|PostalAddress|RelationsType|Rubric|ShortProfile|ShortUserInfo|Street|UserContacts|UserInfo|UserService|UserStatus|VoError|shop/DateType|shop/DeliveryType|shop/FullOrder|shop/FullProductInfo|shop/Order|shop/OrderDate|shop/OrderDates|shop/OrderDatesType|shop/OrderDetails|shop/OrderLine|shop/OrderStatus|shop/OrderUpdateInfo|shop/PaymentStatus|shop/PaymentType|shop/PriceType|shop/Producer|shop/Product|shop/ProductCategory|shop/ProductDetails|shop/ProductListPart|shop/Shop|shop/ShopFEService|shop/ShopPages|shop/UserShopRole|shop/be/DataSet|shop/be/ExchangeFieldType|shop/be/ImExType|shop/be/ImportElement|shop/be/ShopBOService)"
	 * ;
	 */
	private static String getVal(Object o) {
		if( o instanceof Collection){
			String rslt = "";
			for( Object oo : (Collection)o){
					rslt += "+"+getVal(oo);
			} 
			
			if(rslt.startsWith("+")) rslt = rslt.substring(1);
			return rslt;
			
		} else if ( o instanceof String ){
			return "'" + ((String)o).replace("'", "\'") + "'";
			
		} else {

			byte[] serializedObject = sbac.toDatastoreType((Serializable) o);
			/*
			 * String classesChangedSer = new String( serializedObject
			 * ).replaceAll(thriftClassessMask, "com.vmesteonline.be.thrift.$1");
			 * classesChangedSer = new String( serializedObject
			 * ).replaceAll(thriftClassessPathMask, "com/vmesteonline/be/thrift/$1");
			 * serializedObject = classesChangedSer.getBytes(UTF8Cs);
			 */
			return null == o ? "NULL," : "0x" + Hex.encodeHexString(serializedObject).toUpperCase() + ",";
		}
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		doGet((HttpServletRequest) arg0, (HttpServletResponse) arg1);
	}

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

				StringBuffer sqlScript = new StringBuffer();
				PersistenceManager pm = PMF.getNewPm();
				try {
					resp.setStatus(200);
					resp.setContentType("text/plain");
					resp.addHeader("Content-Disposition", "attachment; filename=vomoloko.sql");
					getSQLScript(resp.getOutputStream(), pm);
					return;

				} finally {
					pm.close();
				}
			}

		} else { // it's a queue on production

			logger.fine("It's PRODUCTION IN QUEUE! Save data into a file!");

			processTheRequestIntsideQueue(req, resp);
		}
	}

	public void processTheRequestIntsideQueue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (null != req.getParameter("createOrders")) {	
			logger.fine("It's the CREATE ORDERS request!");
			exportData(req, resp, "orders", new OrdersImporter());
			
		} if (null != req.getParameter("createProducts")) {	
			logger.fine("It's the CREATE ORDERS request!");
			exportData(req, resp, "products", new ProductsImporter());
			
		} else {
			logger.fine("It's the INITIAL queue request!");
			Map<String,String> paramsMap1 = new HashMap<>();
			paramsMap1.put("createProducts", "doit");
			runSameRequestInAQueue( req, resp, paramsMap1 ); //fork queue for products
			Map<String,String> paramsMap2 = new HashMap<>();
			paramsMap2.put("createOrders", "doit");
			runSameRequestInAQueue( req, resp, paramsMap2 ); //fork queue for orders
			createDataWithoutOrders(req,resp);		
		}
	}

	
	private static interface DataImproter {
		Cursor importData(OutputStream os, Cursor curCursor, int pageSize, PersistenceManager pm) throws IOException;
	}
	
	private class OrdersImporter implements DataImproter {
		@Override
		public Cursor importData(OutputStream os, Cursor curCursor, int pageSize, PersistenceManager pm) throws IOException {
			return writeOrdersWithOrderLines(os, curCursor, pageSize, pm);
		}		
	}
	
	private class ProductsImporter implements DataImproter {

		@Override
		public Cursor importData(OutputStream os, Cursor curCursor, int pageSize, PersistenceManager pm) throws IOException {
			Set<String> urlsToSave = new HashSet<String>();
			curCursor = writeProducts(os, pm, curCursor, pageSize, urlsToSave);
			writeFiles(os, pm, urlsToSave);
			return curCursor;
		}		
	}
	
	private void exportData(HttpServletRequest req, HttpServletResponse resp, String fileName, DataImproter di) throws IOException {
		long startTime = System.currentTimeMillis();
		
		String startCursor = req.getParameter("cursor");
		String fileIndexStr = req.getParameter("findex");
		logger.fine("Start exportData to file "+fileName+" cursor "+startCursor+ " findex "+fileIndexStr);
		Integer findex = 0;
		if (fileIndexStr == null )
			try {
				findex = Integer.parseInt(fileIndexStr);
			} catch (NumberFormatException e) {				
				logger.severe( "Failed to parse file index: "+fileIndexStr+" "+e.getMessage());
			}
		
		Cursor curCursor = startCursor == null ? null : Cursor.fromWebSafeString(startCursor);
		PersistenceManager pm = PMF.getNewPm();
		OutputFileCollector os = new OutputFileCollector(pm, fileName+"."+findex+".sql");
		try {
    	while( System.currentTimeMillis() - startTime < 1000 * 60 * 5 ){		    
	    	if( null== (curCursor = di.importData(os, curCursor, 20, pm))) break; 		    
	    }
    	
    } finally {
    	os.close();
    	pm.close();
    }
    if(curCursor!=null) {// there are some more orders to save after timeout so run the task again
    	Map<String, String> params = new HashMap<String, String>();
    	String cursorStr = curCursor.toWebSafeString();
			params.put("cursor", cursorStr);
    	params.put("findex",""+(findex+1));
    	logger.fine("Next queueured request would be started for file "+fileName+" cursor "+cursorStr+ "findex "+findex);
  		String savedFilesParam = req.getParameter("savedFiles");
  		if( null != savedFilesParam ){
  			params.put("savedFiles", os.getFileNames()+savedFilesParam);    			
  		} else { 
  			params.put("savedFiles", os.getFileNames());
  		}
  		runSameRequestInAQueue( req, resp, params);
  		resp.setStatus(200);
  		
  	} else { // all orders are collected so it's time to send a email
  		String urls = "";
  		Object savedFiles = req.getAttribute("savedFiles");
  		if( null != savedFiles && savedFiles instanceof Map){
	  		for( Entry<? extends String, ? extends String> fileUrls: ((Map<? extends String, ? extends String>)savedFiles).entrySet())
	  			urls += "File: " + fileUrls.getKey() + " by URL: " + fileUrls.getValue()+"\n";
  		} else {
  			urls = "savedFiles are: '"+savedFiles+"'";
  		}
  		try {
				EMailHelper.sendSimpleEMail("alexey@vomoloko.ru", "update", urls);
				logger.fine("Email with '"+urls+"' is sent to alexey@vomoloko.ru");
			} catch (Exception e) {
				logger.fine("Email with '"+urls+"' is NOT sent!" + e);
			}
			
			resp.setStatus(200);
  	}
	}

	public void createDataWithoutOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PersistenceManager pm = PMF.getNewPm();
		try {

			OutputFileCollector os = new OutputFileCollector(pm, "vomoloko.sql");

			try {

				getSQLScript(os, pm);
				logger.fine("Export data created!");

				String urls = "";
				for (Entry<String, String> fne : os.outputFiles.entrySet()) {
					urls += fne.getKey() + " " + fne.getValue() + "; ";
				}
				logger.fine("Export data saved into '" + urls + "'. Email sent.");

				EMailHelper.sendSimpleEMail("ceo@vmesteonline.ru", "update", urls);
				logger.fine("Email with url is sent!");
				resp.setStatus(200);
				

			} catch (Exception e) {
				logger.severe("Failed to save data! " + e.getMessage());
				e.printStackTrace();
				resp.setStatus(200);

			} finally {
				os.close();
			}
		} finally {

			pm.close();
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

	private static class OutputFileCollector extends OutputStream {
		public Map<String, String> outputFiles = new HashMap<>();
		private OutputStream os;

		private OutputStream createNextFile() throws IOException {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			String indexedFileName = fileName + "."+ fileIndex;
			VoFileAccessRecord vfar = new VoFileAccessRecord(0, true, indexedFileName, "text/plain");
			pm.makePersistent(vfar);
			GcsFileOptions options = new GcsFileOptions.Builder().acl("public-read").mimeType(vfar.getContentType()).build();
			GcsOutputChannel outputChannel = StorageHelper.getGcsService().createOrReplace(vfar.getGSFileName(), options);
			String url = StorageHelper.getURL(vfar.getId(), "zip");
			outputFiles.put(indexedFileName, url);
			logger.info("File "+indexedFileName + " created under an URL: "+url);			
			fileIndex++;
			return os = Channels.newOutputStream(outputChannel);
		}

		public String getFileNames() {
			String fileNamesAsString = new String();
			for(Entry<String, String> fne : outputFiles.entrySet()){
				fileNamesAsString += "FN" + fne.getKey() + "FU" + fne.getValue(); 
			}
			return fileNamesAsString;
		}

		@Override
		public void write(int b) throws IOException {
			bytesSaved++;	
			os.write(b);
			if (bytesSaved > fileIndex * fileSize) {
				logger.fine("File '"+ fileName + "."+ fileIndex + ".zip reached the limit "+fileSize);				
				os.close();
				createNextFile();
			}
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			bytesSaved+=b.length;
			os.write(b);
			if (bytesSaved > fileIndex * fileSize) {
				logger.fine("File '"+ fileName + "."+ fileIndex + ".zip reached the limit "+fileSize);				
				os.close();				
				createNextFile();
			}
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			bytesSaved+=b.length;
			os.write(b,off,len);
			if (bytesSaved > fileIndex * fileSize) {
				logger.fine("File '"+ fileName + "."+ fileIndex + ".zip reached the limit "+fileSize);
				os.close();				
				createNextFile();
			}			
		}

		@Override
		public void flush() throws IOException {
			os.flush();
		}

		@Override
		public void close() throws IOException {
			if (os != null)
				os.close();
		}

		PersistenceManager pm;
		String fileName;
		int bytesSaved;
		int fileIndex;
		private int fileSize = 1024 * 1024 * 2;

		public OutputFileCollector(PersistenceManager pm, String fileName) throws IOException {
			this.pm = pm;
			this.fileName = fileName;
			this.os = null;
			this.bytesSaved = 0;
			this.fileIndex = 0;
			createNextFile();
		}

	}

	public void getSQLScript(OutputStream outOs, PersistenceManager pm) throws IOException {

		Set<String> urlsToSave = new HashSet<>();
		writeTruncateTables(outOs);

		writeCountries(outOs, pm);

		writeCities(outOs, pm);

		writeStreets(outOs, pm);

		writeBuildings(outOs, pm);

		writePostalAddresses(outOs, pm);

		writeUserAddresses(outOs, pm, urlsToSave);

		writeShops(outOs, pm, urlsToSave);

		writeProductCategories(outOs, pm);

		writeProducers(outOs, pm, urlsToSave);

		logger.fine("All data collected. Start saving " + urlsToSave.size() + " files.");
		writeFiles(outOs, pm, urlsToSave);

		outOs.write(("SET AUTOCOMMIT = 1; SET FOREIGN_KEY_CHECKS = 1; SET UNIQUE_CHECKS = 1;\n").getBytes(UTF8Cs));
		logger.fine("All data DUMPED!");
	}

	public void writeFiles(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		for(String fname : urlsToSave){
			outOs.write((fname+"\n").getBytes());
			//logger.fine("Write file URL: "+ fname);
		}		
	}
	public void writeFilesWithBody(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) {
		ByteArrayOutputStream os;
		for (String nus : urlsToSave) {

			if (null != nus)
				try {

					VoFileAccessRecord ar = pm.getObjectById(VoFileAccessRecord.class, StorageHelper.getFileId(nus));
					logger
							.fine("Save file FILE: " + ar.getFileName() + " ct: " + ar.getContentType() + " URL: " + nus + " ID: " + StorageHelper.getFileId(nus));

					if (ar.getContentType().startsWith("image")) {
						os = new ByteArrayOutputStream();
						StorageHelper.getFile(ar.getGSFileName(), os);
						os.close();
						if (os.size() > 0) {
							String ext;
							try {
								ext = new ContentType(ar.getContentType()).getSubType();
							} catch (ParseException e) {
								ext = "bin";
							}
							// REPLACE INTO `vofileaccessrecord` (`ID`, `BUCKET`,
							// `CONTENTTYPE`, `CREATEDAT`, `DATA`, `FILENAME`, `ISPUBLIC`,
							// `PUBLICFILENAME`, `URL`, `USERID`) VALUES
							// (1,'.','image/jpeg',1429716830,0xFFD8F,'1_public707_imglogo.jpg',0x01,'/img/logo.jpg',NULL,1);
							outOs
									.write(("REPLACE INTO `vofileaccessrecord` (`ID`, `BUCKET`, `CONTENTTYPE`, `CREATEDAT`, `DATA`, `FILENAME`, `ISPUBLIC`, `PUBLICFILENAME`, `URL`, `USERID`) "
											+ "VALUES ("
											+ ar.getId()
											+ ","
											+ getString(ar.getBucket())
											+ ","
											+ getString(ar.getContentType())
											+ ","
											+ ar.getCreatedAt()
											+ ","
											+ "0x"
											+ Hex.encodeHexString(os.toByteArray()).toUpperCase()
											+ ","
											+ ""
											+ getString(ar.getPublicFileName())
											+ ",0x01,"
											+ getString(ar.getFileName()) + "," + getString(StorageHelper.getURL(ar.getId(), ext)) + "," + ar.getUserId() + ");\n")
											.getBytes(UTF8Cs));

							// REPLACE INTO `fileaccessrecordversions` (`QUERY`, `KEY`,
							// `ID_VID`) VALUES (1,'/file/AQ=.jpg',1);

							if (null != ar.getVersions()) {
								for (Entry<String, VoFileAccessRecord> arv : ar.getVersions().entrySet()) {
									outOs.write(("REPLACE INTO `fileaccessrecordversions` (`QUERY`, `KEY`, `ID_VID`) VALUES (" + ar.getId() + ","
											+ getString(arv.getKey()) + "," + ar.getId() + ");\n").getBytes(UTF8Cs));
								}
							}
							logger.fine("Save file FILE: " + ar.getFileName() + " DONE!");
						} else {
							logger.severe("Failed to load FILE: " + ar.getFileName());
						}
					} else {
						logger.severe("FILE: " + ar.getFileName() + " has content type: '" + ar.getContentType() + "' skipped");
					}

				} catch (Exception e) {
					logger.severe("Failed to load File '" + nus + "'." + e);
				}
		}
	}

	public void writeOrderLines(OutputStream outOs, PersistenceManager pm, Set<String> _) throws IOException {
		// REPLACE INTO `voorderline` (`ID`, `COMMENT`, `ORDERID`, `PACKETS`,
		// `PRICE`, `PRODUCTID`, `QUANTITY`) VALUES (18,'',4,0xACED00078,204,3,0.1);

		int offset = 0;
		int pageSize = 500;
		while(true){
			Query q = pm.newQuery(VoOrderLine.class);
			q.setRange(offset, offset+pageSize );	
			int itemsOnPage = 0;
			Collection<VoOrderLine> orderLines = (Collection<VoOrderLine>) q.execute();
			for (VoOrderLine ar : orderLines) {
				outOs.write(("REPLACE INTO `voorderline` (`ID`, `COMMENT`, `ORDERID`, `PACKETS`, `PRICE`, `PRODUCTID`, `QUANTITY`) VALUES ("
						+ ar.getId().getId() + "," + getString(ar.getComment()) + "," + ar.getOrderId() + "," + getVal(ar.getPackets()) + ar.getPrice() + ","
						+ ar.getProductId() + "," + ar.getQuantity() + ");\n").getBytes(UTF8Cs));
				itemsOnPage ++;
			}
			if(itemsOnPage < pageSize - 1)
				break;
			else offset += pageSize;
			logger.fine(offset + " voorderline collected.");
		}
		logger.fine("voorderline collected.");		
	}
	
	public void writeTheOrderLines(OutputStream outOs, PersistenceManager pm, Long orederId) throws IOException {
		// REPLACE INTO `voorderline` (`ID`, `COMMENT`, `ORDERID`, `PACKETS`,
		// `PRICE`, `PRODUCTID`, `QUANTITY`) VALUES (18,'',4,0xACED00078,204,3,0.1);

		List<VoOrderLine> orderLines = (List<VoOrderLine>) pm.newQuery(VoOrderLine.class,"orderId=="+orederId).execute();
		for (VoOrderLine ar : orderLines) {
			outOs.write(("REPLACE INTO `voorderline` (`ID`, `COMMENT`, `ORDERID`, `PACKETS`, `PRICE`, `PRODUCTID`, `QUANTITY`) VALUES ("
					+ ar.getId().getId() + "," + getString(ar.getComment()) + "," + ar.getOrderId() + "," + getVal(ar.getPackets()) + ar.getPrice() + ","
					+ ar.getProductId() + "," + ar.getQuantity() + ");\n").getBytes(UTF8Cs));
		}	
	}

	public void writeOrders(OutputStream outOs, PersistenceManager pm) throws IOException {
		long offset = 0;
		while (true) {
			Iterator<VoOrder> it = getNextRangeOf(pm, offset, 999, VoOrder.class);
			offset += 999L;
			if (!it.hasNext())
				break;
			// REPLACE INTO `voorder` (`ID`, `COMMENT`, `CREATEDAT`, `DATE`,
			// `DELIVERY`, `DELIVERYCOST`, `DELIVERYTO_ID_OID`, `ORDERLINES`,
			// `PAYMENTSTATUS`, `PAYMENTTYPE`, `PRICETYPE`, `SHOPID`, `STATUS`,
			// `TOTALCOST`, `USER_ID_OID`, `WEIGHTGRAMM`)
			// VALUES
			// (4,NULL,1429735959,1430438400,'SELF_PICKUP',0,5,0xACED001078,'WAIT','CASH','INET',1,'NEW',233,12,1090);
			while (it.hasNext()) {
				VoOrder ar = it.next();
				outOs
						.write(("REPLACE INTO `voorder` (`ID`, `COMMENT`, `CREATEDAT`, `DATE`, `DELIVERY`, `DELIVERYCOST`, `DELIVERYTO_ID_OID`, `ORDERLINES`, `PAYMENTSTATUS`, `PAYMENTTYPE`, `PRICETYPE`,"
								+ " `SHOPID`, `STATUS`, `TOTALCOST`, `USER_ID_OID`, `WEIGHTGRAMM`) VALUES ("
								+ ar.getId()
								+ ","
								+ getString(ar.getComment())
								+ ","
								+ ar.getCreatedAt()
								+ ","
								+ ar.getDate()
								+ ","
								+ getString(ar.getDelivery().name())
								+ ","
								+ ar.getDeliveryCost()
								+ ","
								+ ar.getDeliveryTo().getId().getId()
								+ ","
								+ getVal(ar.getOrderLines())
								+ ""
								+ getString(ar.getPaymentStatus().name())
								+ ","
								+ getString(ar.getPaymentType().name())
								+ ",'"
								+ ar.getPriceType().name()
								+ "',"
								+ ar.getShopId()
								+ ","
								+ getString(ar.getStatus().name())
								+ ","
								+ ar.getTotalCost()
								+ ","
								+ ar.getUser().getId() + "," + ar.getWeightGramm() + ");\n").getBytes(UTF8Cs));
			}
		}
		logger.fine("voorder collected.");
	}

	
	public Cursor writeOrdersWithOrderLines(OutputStream outOs, Cursor cursor, int pageSize, PersistenceManager pm ) throws IOException {

			List<VoOrder> res = getNextRangeOf(pm, cursor, pageSize, VoOrder.class);
			Iterator<VoOrder> it = res.iterator();
			if (!it.hasNext())
				return null;
			// REPLACE INTO `voorder` (`ID`, `COMMENT`, `CREATEDAT`, `DATE`,
			// `DELIVERY`, `DELIVERYCOST`, `DELIVERYTO_ID_OID`, `ORDERLINES`,
			// `PAYMENTSTATUS`, `PAYMENTTYPE`, `PRICETYPE`, `SHOPID`, `STATUS`,
			// `TOTALCOST`, `USER_ID_OID`, `WEIGHTGRAMM`)
			// VALUES
			// (4,NULL,1429735959,1430438400,'SELF_PICKUP',0,5,0xACED001078,'WAIT','CASH','INET',1,'NEW',233,12,1090);
			while (it.hasNext()) {
				VoOrder ar = it.next();
				outOs
						.write(("REPLACE INTO `voorder` (`ID`, `COMMENT`, `CREATEDAT`, `DATE`, `DELIVERY`, `DELIVERYCOST`, `DELIVERYTO_ID_OID`, `ORDERLINES`, `PAYMENTSTATUS`, `PAYMENTTYPE`, `PRICETYPE`,"
								+ " `SHOPID`, `STATUS`, `TOTALCOST`, `USER_ID_OID`, `WEIGHTGRAMM`) VALUES ("
								+ ar.getId()
								+ ","
								+ getString(ar.getComment())
								+ ","
								+ ar.getCreatedAt()
								+ ","
								+ ar.getDate()
								+ ","
								+ getString(ar.getDelivery().name())
								+ ","
								+ ar.getDeliveryCost()
								+ ","
								+ ar.getDeliveryTo().getId().getId()
								+ ","
								+ getVal(ar.getOrderLines())
								+ ""
								+ getString(ar.getPaymentStatus().name())
								+ ","
								+ getString(ar.getPaymentType().name())
								+ ",'"
								+ ar.getPriceType().name()
								+ "',"
								+ ar.getShopId()
								+ ","
								+ getString(ar.getStatus().name())
								+ ","
								+ ar.getTotalCost()
								+ ","
								+ ar.getUser().getId() + "," + ar.getWeightGramm() + ");\n").getBytes(UTF8Cs));
				writeTheOrderLines(outOs, pm, ar.getId());
			}
		return JDOCursorHelper.getCursor(res);
	}
	
	private <T> List<T> getNextRangeOf(PersistenceManager pm, Cursor cursor, int pageSize, Class<T> t) {
		Query nq = pm.newQuery(t);
		if(null!=cursor) {
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			nq.setExtensions(extensionMap);
		}
		nq.setRange(0, pageSize);
		return (List<T>) nq.execute();
	}

	public void writeProducers(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		// REPLACE INTO `voproducer` (`ID`, `DESCR`, `HOMEURL`, `IMPORTID`,
		// `LOGOURL`, `NAME`, `SHOPID`, `SOCIALNETWORKS`) VALUES
		// (10,NULL,NULL,10000,NULL,'Вологодский Пивоваренный Завод',1,NULL);
		for (VoProducer ar : pm.getExtent(VoProducer.class)) {
			outOs.write(("REPLACE INTO `voproducer` (`ID`, `DESCR`, `HOMEURL`, `IMPORTID`, `LOGOURL`, `NAME`, `SOCIALNETWORKS`, `SHOPID`) VALUES ("
					+ ar.getId() + "," + getString(ar.getDescr()) + "," + getString(ar.getHomeURL()) + "," + ar.getImportId() + ","
					+ getString(ar.getLogoURL()) + "," + getString(ar.getName()) + "," + getVal(ar.getSocialNetworks()) + ar.getShopId() + ");\n")
					.getBytes(UTF8Cs));
			urlsToSave.add(ar.getLogoURL());
			logger.fine("Producer saved: "+ar.getName());
		}
		logger.fine("voproducer collected.");
	}

	public Cursor writeProducts(OutputStream outOs, PersistenceManager pm, Cursor curCursor, int pageSize, Set<String> urlsToSave) throws IOException {
		 
		List<VoProduct> res = getNextRangeOf(pm, curCursor, pageSize, VoProduct.class);
		Iterator<VoProduct> it = res.iterator();
		if (!it.hasNext())
			return null;
		
		int seq;
		// REPLACE INTO `voproduct` (`ID`, `FULLDESCR`, `IMAGEURL`, `IMAGESURLSET`,
		// `IMPORTID`, `KNOWNNAMES`, `MINCLIENTPACK`, `MINPRODUCERPACK`, `NAME`,
		// `OPTIONSMAP`, `PREPACKREQUIRED`, `PRICE`, `PRICESMAP`, `PRODUCERID`,
		// `SCORE`, `SHOPID`, `SHORTDESCR`, `SOCIALNETWORKS`, `TOPICSET`,
		// `UNITNAME`, `WEIGHT`) VALUES
		// (15,'',NULL,0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000077040000000078,1015,0xACED0005737200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F4000000000000078,1,12,'Творог
		// 5%
		// стакан',0xACED0005737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000377080000000400000002740010D183D0BFD0B0D0BAD0BED0B2D0BAD0B0740037D09F2FD0BF20D181D182D0B0D0BAD0B0D0BD20D18120D184D0BED0BBD18CD0B3D0BED0B2D0BED0B920D0BAD180D18BD188D0BAD0BED0B9740019D0A1D180D0BED0BA20D185D180D0B0D0BDD0B5D0BDD0B8D18F74000C3720D181D183D182D0BED0BA78,0x00,36.57,0xACED0005737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C77080000001000000004737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C75657871007E0003404B0000000000007371007E0002000000017371007E000540488000000000007371007E0002000000027371007E000500000000000000007371007E0002000000037371007E0005000000000000000078,1,3,1,'Творог
		// 5%
		// стакан',NULL,0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000077040000000078,'шт',190);
		while( it.hasNext() ){
			VoProduct ar = it.next();
			outOs
					.write(("REPLACE INTO `voproduct` (`ID`, `FULLDESCR`, `IMAGEURL`, `IMAGESURLSET`, `IMPORTID`, `KNOWNNAMES`, `MINCLIENTPACK`, `MINPRODUCERPACK`, `NAME`, "
							+ "`OPTIONSMAP`, `PREPACKREQUIRED`, `PRICE`, `PRICESMAP`, `PRODUCERID`, `SCORE`, `SHOPID`, `SHORTDESCR`, `SOCIALNETWORKS`, "
							+ "`TOPICSET`, `UNITNAME`, `WEIGHT`) VALUES ("
							+ ar.getId()
							+ ","
							+ getString(ar.getFullDescr())
							+ ","
							+ getString(ar.getImageURL())
							+ ","
							+ getVal(ar.getImagesURLset())
							+ ","
							+ ar.getImportId()
							+ ","
							+ getVal(ar.getKnownNames())
							+ ","
							+ ar.getMinClientPack()
							+ ","
							+ ar.getMinProducerPack()
							+ ","
							+ getString(ar.getName())
							+ ","
							+ getVal(ar.getOptionsMap())
							+ ","
							+ (ar.isPrepackRequired() ? "0x01" : "0x00")
							+ ","
							+ ar.getPrice()
							+ ","
							+ getVal(VoProduct.convertFromPriceTypeMap(ar.getPricesMap(), new HashMap<Integer, Double>()))
							+ ","
							+ ar.getProducerId()
							+ ","
							+ ar.getScore()
							+ ","
							+ ar.getShopId()
							+ ","
							+ getString(ar.getShortDescr())
							+ ","
							+ getVal(ar.getSocialNetworks())
							+ ","
							+ getVal(ar.getTopicSet()) + getString(ar.getUnitName()) + "," + ar.getWeight() + ");\n").getBytes(UTF8Cs));

			if (null != ar.getImageURL()) {
				urlsToSave.add(ar.getImageURL());
				//logger.fine("Product " + ar.getName() + " has image " + ar.getImageURL());
			} else {
				//logger.fine("Product " + ar.getName() + " has NO IMAGE");
			}
			if (null != ar.getImagesURLset())
				urlsToSave.addAll(ar.getImagesURLset());

			seq = 0;
			for (Long cid : ar.getCategories()) {
				// REPLACE INTO `productcategories` (`PRODUCT_ID`, `CATEGORY_ID`, `IDX`)
				// VALUES (12,14,0);
				outOs
						.write(("REPLACE INTO `productcategories` (`PRODUCT_ID`, `CATEGORY_ID`, `IDX`) VALUES (" + ar.getId() + "," + cid + "," + seq++ + ");\n")
								.getBytes(UTF8Cs));
			}
		}
		logger.fine("voproduct collected");
		return JDOCursorHelper.getCursor(res);
	}

	public void writeProductCategories(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `voproductcategory` (`ID`, `DESCR`, `IMPORTID`,
		// `LOGOURLSET`, `NAME`, `PARENTID`, `PARENTPATH`, `PRODUCTCOUNT`, `SHOPID`,
		// `SOCIALNETWORKS`) VALUES (28,NULL,29,NULL,'Квас',0,NULL,0,1,NULL);
		for (VoProductCategory ar : pm.getExtent(VoProductCategory.class)) {
			outOs
					.write(("REPLACE INTO `voproductcategory` (`ID`, `DESCR`, `IMPORTID`, `LOGOURLSET`, `NAME`, `PARENTID`, `PARENTPATH`, `PRODUCTCOUNT`, `SOCIALNETWORKS`, `SHOPID`) VALUES "
							+ "("
							+ ar.getId()
							+ ","
							+ getString(ar.getDescr())
							+ ","
							+ ar.getImportId()
							+ ",NULL,"
							+ getString(ar.getName())
							+ ","
							+ ar.getParentId() + ",NULL," + ar.getProductCount() + "," + getVal(ar.getSocialNetworks()) + ar.getShopId() + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("voproductcategory collected.");
		return;
	}

	public void writeShops(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		// REPLACE INTO `voshop` (`ID`, `ABOUTSHOPPAGECONTENTURL`, `ACTIVATED`,
		// `ADDRESS_ID_OID`, `CONDITIONSPAGECONTENTURL`, `DATES`,
		// `DELIVERYADDRESSMASKSTEXT`,
		// `DELIVERYBYWEIGHTINCREMENT`, `DELIVERYCONDITIONSTEXT`,
		// `DELIVERYCOSTBYDISTANCE`, `DELIVERYCOSTS`, `DELIVERYPAGECONTENTURL`,
		// `DESCR`, `HOSTNAME`,
		// `LOGOURL`, `NAME`, `OWNERID`, `PAYMENTTYPES`, `SOCIALNETWORKS`,
		// `VOTERESULTS`)
		// VALUES
		// (2,NULL,0x01,5,NULL,0xACE,NULL,NULL,NULL,NULL,0xACED0078,NULL,'Магазин
		// качественного мяса',
		// 'votmeat.co',NULL,'Во!Мясо',1,0xACED78,NULL,NULL);
		for (VoShop ar : pm.getExtent(VoShop.class)) {
			outOs.write(("REPLACE INTO `voshop` (`ID`, `ABOUTSHOPPAGECONTENTURL`, `ACTIVATED`, `ADDRESS_ID_OID`, `CONDITIONSPAGECONTENTURL`, `DATES`, "
					+ "`DELIVERYADDRESSMASKSTEXT`, `DELIVERYBYWEIGHTINCREMENT`, `DELIVERYCONDITIONSTEXT`, `DELIVERYCOSTBYDISTANCE`, `DELIVERYCOSTS`,"
					+ " `DELIVERYPAGECONTENTURL`, `DESCR`, `HOSTNAME`, `LOGOURL`, `NAME`, `OWNERID`, `PAYMENTTYPES`, `SOCIALNETWORKS`, `VOTERESULTS`) VALUES ("
					+ ar.getId()
					+ ","
					+ getString(ar.getAboutShopPageContentURL())
					+ ","
					+ (ar.isActivated() ? "0x01" : "0x00")
					+ ","
					+ ar.getAddress().getId().getId()
					+ ","
					+ getString(ar.getConditionsPageContentURL())
					+ ","
					+ getVal(ar.getDates())
					+ getVal(VoShop.convertFromDeliveryTypeMap(ar.getDeliveryAddressMasksText(), new HashMap<Integer, String>()))
					+ getVal(ar.getDeliveryByWeightIncrement())
					+ getVal(VoShop.convertFromDeliveryTypeMap(ar.getDeliveryConditionsText(), new HashMap<Integer, String>()))
					+ getVal(ar.getDeliveryCostByDistance())
					+ getVal(ar.getDeliveryCosts())
					+ getString(ar.getDeliveryPageContentURL())
					+ ","
					+ getString(ar.getDescr())
					+ ","
					+ getString(ar.getHostName())
					+ ","
					+ getString(ar.getLogoURL())
					+ ","
					+ getString(ar.getName())
					+ ","
					+ ar.getOwnerId() + "," + getVal(ar.getPaymentTypes()) + getVal(ar.getSocialNetworks()) + "NULL);\n").getBytes(UTF8Cs));
			urlsToSave.add(ar.getLogoURL());
		}
		logger.fine("voshop collected.");
	}

	public void writeUserAddresses(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		int seq;
		long offset = 0;
		long vouserAddressSeq = 1L;

		while (true) {
			Iterator<VoUser> it = getNextRangeOf(pm, offset, 100, VoUser.class);
			offset += 999L;
			if (!it.hasNext())
				break;
			// REPLACE INTO `vouser` (`ID`, `ADDRESS`, `AVATARPROFILE`, `BIRTHDAY`,
			// `CONFIRMCODE`, `CONFIRMMAILCODE`, `EMAIL`, `EMAILCONFIRMED`,
			// `LASTNAME`,
			// `MOBILEPHONE`, `NAME`, `PASSWORD`, `REGISTERED`, `LATITUDE`,
			// `LONGITUDE`, `DISCRIMINATOR`)
			// VALUES
			// (12,NULL,'/data/da.gif',0,118479,0,'alexey.chervyakov@gmail.com',0x01,'',NULL,'bro','123',0,'0','0','com.vmesteonline.be.jdo2.VoUser');
			while (it.hasNext()) {
				VoUser ar = it.next();

				outOs
						.write(("REPLACE INTO `vouser` (`ID`, `ADDRESS`, `AVATARPROFILE`, `BIRTHDAY`, `CONFIRMCODE`, `CONFIRMMAILCODE`, `EMAIL`, `EMAILCONFIRMED`, `LASTNAME`, `MOBILEPHONE`, `NAME`, `PASSWORD`, `REGISTERED`, `LATITUDE`, `LONGITUDE`, `DISCRIMINATOR`) VALUES ("
								+ ar.getId()
								+ ","
								+ (ar.getAddress() != null ? "" + ar.getAddress().getId().getId() : "NULL")
								+ ","
								+ getString(ar.getAvatarProfile())
								+ ",0,"
								+ ar.getConfirmCode()
								+ ",0,"
								+ getString(ar.getEmail())
								+ ","
								+ (ar.isEmailConfirmed() ? "0x01," : "0x00,")
								+ getString(ar.getLastName())
								+ ","
								+ getString(ar.getMobilePhone())
								+ ","
								+ getString(ar.getName()) + "," + getString(ar.getPassword()) + ",0,'0','0', 'com.vmesteonline.be.jdo2.VoUser');\n").getBytes(UTF8Cs));

				// REPLACE INTO `vouseraddress` (`ID`, `ADDRESS_ID_OID`, `ADDRESSNAME`)
				// VALUES (1,6,'пушкин ленинградская 67');
				seq = 0;
				for (Entry<String, VoPostalAddress> da : ar.getDeliveryAddress().entrySet()) {
					outOs.write(("REPLACE INTO `vouseraddress` (`ID`, `ADDRESS_ID_OID`, `ADDRESSNAME`) VALUES (" + vouserAddressSeq + ", "
							+ da.getValue().getId().getId() + "," + getString(da.getKey()) + ");\n").getBytes(UTF8Cs));
					outOs
							.write(("REPLACE INTO `useraddresses` (`ID`, `ADDRESS`, `IDX`) VALUES (" + ar.getId() + ", " + vouserAddressSeq++ + "," + seq++ + ");\n")
									.getBytes(UTF8Cs));
				}

				urlsToSave.add(ar.getAvatarProfile());
			}
		}
		logger.fine("vouseraddress collected.");
	}

	public void writePostalAddresses(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `vopostaladdress` (`ID`, `BUILDINGID`, `COMMENT`, `FLATNO`,
		// `FLOOR`, `STAIRCASE`) VALUES (6,5,'',57,0,0);
		for (VoPostalAddress ar : pm.getExtent(VoPostalAddress.class)) {
			outOs.write(("REPLACE INTO `vopostaladdress` (`ID`, `BUILDINGID`, `COMMENT`, `FLATNO`, `FLOOR`, `STAIRCASE`) VALUES (" + ar.getId().getId()
					+ "," + ar.getBuilding().getId().getId() + "," + getString(ar.getComment()) + "," + ar.getFlatNo() + "," + ar.getFloor() + ","
					+ ar.getStaircase() + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("vopostaladdress collected.");
	}

	public void writeBuildings(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `vobuilding` (`ID`, `ADDRESSSTRING`, `FULLNO`, `LATITUDE`,
		// `LONGITUDE`, `STREETID`, `ZIPCODE`) VALUES
		// (5,'Россия,Пушкин,Ленинградская
		// улица,67','67','59.729772','30.409104',4,'');
		for (VoBuilding ar : pm.getExtent(VoBuilding.class)) {
			outOs.write(("REPLACE INTO `vobuilding` (`ID`, `ADDRESSSTRING`, `FULLNO`, `LATITUDE`, `LONGITUDE`, `STREETID`, `ZIPCODE`) VALUES ("
					+ ar.getId().getId() + "," + getString(ar.getAddressString()) + "," + getString(ar.getFullNo()) + "," + getString(ar.getLatitude()) + ","
					+ getString(ar.getLongitude()) + "," + ar.getStreetId().getId() + "," + ar.getStreetId().getId() + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("vobuilding collected.");
	}

	public void writeStreets(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `vostreet` (`ID`, `CITYID`, `NAME`) VALUES
		// (4,2,'Ленинградская улица');
		for (VoStreet ar : pm.getExtent(VoStreet.class)) {
			outOs.write(("REPLACE INTO `vostreet` (`ID`, `CITYID`, `NAME`) VALUES (" + ar.getId().getId() + "," + ar.getCity().getId().getId() + ","
					+ getString(ar.getName()) + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("vostreet collected.");
	}

	public void writeCities(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `vocity` (`ID`, `COUNTRYID`, `NAME`) VALUES (1,1,'Санкт
		// Петербург');
		for (VoCity ar : pm.getExtent(VoCity.class)) {
			outOs.write(("REPLACE INTO `vocity` (`ID`, `COUNTRYID`, `NAME`) VALUES (" + ar.getId().getId() + "," + ar.getCountry().getId().getId() + ","
					+ getString(ar.getName()) + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("vocity collected.");
	}

	public void writeCountries(OutputStream outOs, PersistenceManager pm) throws IOException {
		// REPLACE INTO `vocountry` (`ID`, `NAME`) VALUES (1,'Россия');

		for (VoCountry ar : pm.getExtent(VoCountry.class)) {
			outOs
					.write(("REPLACE INTO `vocountry` (`ID`, `NAME`) VALUES (" + ar.getId().getId() + "," + getString(ar.getName()) + ");\n").getBytes(UTF8Cs));
		}
		logger.fine("vocountry collected.");
	}

	public void writeTruncateTables(OutputStream outOs) throws IOException {
		outOs.write(("SET AUTOCOMMIT = 0; SET FOREIGN_KEY_CHECKS = 0; SET UNIQUE_CHECKS = 0;\n" + "truncate table `fileaccessrecordversions`;"
				+ "truncate table `voorderline`;" + "truncate table `voorder`;" + "truncate table `productcategories`;" + "truncate table `voproduct`;"
				+ "truncate table `voproductcategory`;" + "truncate table `voproducer`;" + "truncate table `voshop`;"
				+ "truncate table `vofileaccessrecord`;" + "truncate table `vouseraddress`;" + "truncate table `vouser`;"
				+ "truncate table `vopostaladdress`;" + "truncate table `useraddresses`;" + "truncate table `vobuilding`;" + "truncate table `vostreet`;"
				+ "truncate table `vocity`;" + "truncate table `vocountry`;").getBytes(UTF8Cs));
	}

	private String getString(BigDecimal bd) {
		return null == bd ? "NULL" : "'" + bd.toString() + "'";
	}

	private String getString(String name) {

		return null == name || 0 == name.trim().length() ? "" : name.replace("\'", "\\\'");
		/*String rslt = null == name || 0 == name.trim().length() ? "NULL" : "0x"
				+ Hex.encodeHexString((name.trim().substring(0, Math.min(name.trim().length(), 255))).getBytes(Charset.forName("UTF8")));
		return rslt;*/
	}

	private <T> Iterator<T> getNextRangeOf(PersistenceManager pm, long offset, int size, Class<T> t) {
		Query nq = pm.newQuery(t);
		nq.setRange(offset, offset + size);
		List<T> l = (List<T>) nq.execute();
		return l.iterator();
	}
}
