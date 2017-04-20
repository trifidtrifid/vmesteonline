package com.vmesteonline.be.utils;

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
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.shop.VoProducer;
import com.vmesteonline.be.jdo2.shop.VoProduct;
import com.vmesteonline.be.jdo2.shop.VoProductCategory;
import com.vmesteonline.be.shop.PriceType;

@SuppressWarnings("serial")
public class ExportCSVServlet extends HttpServlet {

	private static final Charset UTF8Cs = Charset.forName("UTF-8");

	public ExportCSVServlet() {
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
		if (null != req.getParameter("createProducts")) {	
			logger.fine("It's the CREATE ORDERS request!");
			//exportData(req, resp, "products", new ProductsImporter());
			
		} else {
			logger.fine("It's the INITIAL queue request!");
			/*Map<String,String> paramsMap1 = new HashMap<>();
			paramsMap1.put("createProducts", "doit");
			runSameRequestInAQueue( req, resp, paramsMap1 ); //fork queue for products
*/			createDataWithoutOrders(req,resp);		
		}
	}

	
	private static interface DataImproter {
		Cursor importData(OutputStream os, Cursor curCursor, int pageSize, PersistenceManager pm) throws IOException;
	}
	
	private class ProductsImporter implements DataImproter {

		@Override
		public Cursor importData(OutputStream os, Cursor curCursor, int pageSize, PersistenceManager pm) throws IOException {
			Set<String> urlsToSave = new HashSet<String>();
			curCursor = writeProducts(os, pm, curCursor, pageSize, urlsToSave);
			writeProductCategories(os, pm);
			writeProducers(os, pm, urlsToSave);
			curCursor = writeProducts(os, pm, curCursor, pageSize, urlsToSave);
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

				EMailHelper.sendSimpleEMail("alexey@vomoloko.ru", "update", urls);
				logger.fine("Email with url is sent!");
				resp.setStatus(200);
				

			} catch (Exception e) {
				logger.severe("Failed to save data! " + e.getMessage());				
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
			String url = StorageHelper.getURL(vfar.getId(), "csv");
			outputFiles.put(indexedFileName, url);
			logger.info("File "+indexedFileName + " created under an URL: "+url);			
			fileIndex++;
			os = Channels.newOutputStream(outputChannel);
			logger.info("os CREAted");
			return os;			
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
		private int fileSize = 1024 * 1024 * 5;

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
		writeUserAddresses(outOs, pm, urlsToSave);
		writeProductCategories(outOs, pm);
		writeProducts(outOs, pm, null, 100000, urlsToSave);
		writeProducers(outOs, pm, urlsToSave);

		logger.fine("All data collected. Start saving " + urlsToSave.size() + " files.");
		logger.fine("All data DUMPED!");
	}

	
	private <T> List<T> getNextRangeOf(PersistenceManager pm, Cursor cursor, int pageSize, Class<T> t) {
		return getNextRangeOf(pm, cursor, pageSize, t, null);
	}
	private <T> List<T> getNextRangeOf(PersistenceManager pm, Cursor cursor, int pageSize, Class<T> t, String order) {
		Query nq = pm.newQuery(t);
		if(order!=null){
			nq.setOrdering(order);
		}
		if(null!=cursor) {
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			nq.setExtensions(extensionMap);
		} 
		nq.setRange(0, pageSize);
		return (List<T>) nq.execute();
	}

	public void writeProducers(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		outOs.write("===================== CUT HERE ===================================================== ".getBytes());
		outOs.write(("PRODUCERS: id, name, description, logo URL, homepage URL\r\n").getBytes()); 
		for (VoProducer ar : pm.getExtent(VoProducer.class)) {
			outOs.write((
					ar.getImportId() + ";" +
							"\"" + ar.getName() + "\";" +
							"\"" + ar.getDescr() + "\";" +
							"\"http://www.vomoloko.ru/" + ar.getLogoURL() + "\";" +
							"\"" + ar.getHomeURL() + "\";\r\n").getBytes(UTF8Cs));
			logger.fine("Producer saved: "+ar.getName());
		}
		logger.fine("voproducer collected.");
	}

	public Cursor writeProducts(OutputStream outOs, PersistenceManager pm, Cursor curCursor, int pageSize, Set<String> urlsToSave) throws IOException {
		
		outOs.write("===================== CUT HERE =====================================================\r\n".getBytes());
		outOs.write(("PRODUCTS: id, name, short name, weigth, image URL, invoice price, categories, description, images, retail price, internet price, vip price, " +
				 "special price, params, producer id, customers min quantity, producers min quantity, weighted, unit name\r\n").getBytes()); 
		
		List<VoProduct> res = getNextRangeOf(pm, curCursor, pageSize, VoProduct.class, "importId");
		Iterator<VoProduct> it = res.iterator();
		if (!it.hasNext())
			return null;
		
		int seq;
		while( it.hasNext() ){
			//code, name, краткое наименование	вес	ссылка на катринку	цена закупки	список номеров категорий разделенный |	
			//описание;	0 и более ссылок на картинки раздеоленный | (сейчас не используется);
			//розничная цена	цена интернет магаза	спец цена для кого то	цена для акций
			//параметры. типа жирность. упаковка и пр. - список разделенный "|". где параметр и значение отделены знаком ":"	не исп.
			//номер производителя	
			//минимальная единица. которую может купить покупатель. для развесных может быть дробная. для остальных. чаще всего 1.0	
			//минимальное число заказа у производителя	1 если продукт можно фасовать. 0 иначе	не используем	единицы измерения

			VoProduct ar = it.next();
			
			seq = 0;
			String catList = "";
			if(null!=ar.getCategories()) for (Long cid : ar.getCategories()) {
				if( 0!=cid) {
					catList += (seq == 0 ? "" : "|") + pm.getObjectById(VoProductCategory.class,cid).getImportId();
				}
				++seq;
			}
			seq = 0;
			String options = "";
			if( null!=ar.getProductDetails() && null != ar.getProductDetails().optionsMap)
				for( Entry<String, String> opt  : ar.getProductDetails().optionsMap.entrySet()){
					options += (seq == 0 ? "" : "|") + opt.getKey() + ":" + opt.getValue();
					++seq;
				}
			
			if(-1!=ar.getImportId()) outOs
					.write((
							ar.getImportId() + ";" +
									getString(ar.getName()) + ";" +
									getString(ar.getShortDescr()) + ";" +
									ar.getWeight() + ";" +
									(ar.getImageURL() == null ? "" : "http://www.vomoloko.ru"+ar.getImageURL()) + ";" +
									ar.getPrice(PriceType.INET) + ";" +
									catList + ";" +
									(null == ar.getFullDescr() ? "" : getString(ar.getFullDescr())) + ";" +
									ar.getPrice() + ";" +
									ar.getPrice(PriceType.RETAIL) + ";" +
									ar.getPrice(PriceType.INET) + ";" +
									ar.getPrice(PriceType.VIP) + ";" +
									ar.getPrice(PriceType.SPECIAL) + ";" +
									options  + ";;" +
									(0 == ar.getProducerId() ? "" : pm.getObjectById(VoProducer.class, ar.getProducerId()).getImportId()) + ";" +
									(ar.isPrepackRequired() ? "0.25" : "1" ) + ";" +
									(ar.getProductDetails() == null ? 1 : ar.getProductDetails().getMinProducerPack())  + ";" +
									(ar.isPrepackRequired() ? "1" : "0" ) + ";;" +
									ar.getUnitName()+";\r\n").getBytes(UTF8Cs));
									
		}
		logger.fine("voproduct collected");
		return JDOCursorHelper.getCursor(res);
	}

	public void writeProductCategories(OutputStream outOs, PersistenceManager pm) throws IOException {
		outOs.write("===================== CUT HERE =====================================================\r\n".getBytes());
		outOs.write("PRODUCT CATEGORIES: id, parent, name, description\r\n".getBytes()); 
		
		for (VoProductCategory ar : pm.getExtent(VoProductCategory.class)) {
			outOs
					.write(( 
							ar.getImportId() + ";" +
									(0 == ar.getParent() ? 0 : pm.getObjectById(VoProductCategory.class, ar.getParent()).getImportId()) + ";" +
									ar.getName() + ";" +
									ar.getDescr() + ";" +
									";\r\n").getBytes(UTF8Cs));
		}
		logger.fine("voproductcategory collected.");
		return;
	}

	public void writeUserAddresses(OutputStream outOs, PersistenceManager pm, Set<String> urlsToSave) throws IOException {
		int seq;
		long offset = 0;
		
		outOs.write("===================== CUT HERE ===================================================== ".getBytes());
		outOs.write("USERS: id, name, last name, mobile phone, email, passwrd, address, avatar\r\n".getBytes()); 
		while (true) {
			Iterator<VoUser> it = getNextRangeOf(pm, offset, 1000, VoUser.class);
			offset += 999L;
			if (!it.hasNext())
				break;
			while (it.hasNext()) {
				VoUser ar = it.next();
				String addr = "";
				seq = 0;
				if( ar.getDeliveryAddress() != null && !ar.getDeliveryAddress().isEmpty() ){
					for( VoPostalAddress pa: ar.getDeliveryAddress().values()){
						addr += (seq>0? "|":"") + getString(pa.getAddressText(pm));
						++seq;
					}					
				}
				outOs
						.write((
								ar.getId() + ";" +
								getString(ar.getName()) + ";" +
								getString(ar.getLastName()) + ";" +
								getString(ar.getMobilePhone()) + ";" +
								getString(ar.getEmail()) + ";" +
								getString(ar.getPassword()) + ";" +
								getString(ar.getAddress() == null ? "" : ar.getAddress().getAddressText(pm)) + ";" +
								getString("http://www.vomoloko.ru/"+ar.getAvatarProfile()) + ";" +
								addr + ";\r\n").getBytes(UTF8Cs));
			}
		}
		logger.fine("vouseraddress collected.");
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
