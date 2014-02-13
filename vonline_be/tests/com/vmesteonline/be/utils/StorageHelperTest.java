package com.vmesteonline.be.utils;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class StorageHelperTest extends StorageHelper {
	
	private static final LocalServiceTestHelper helper = 
	    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
	                               new LocalBlobstoreServiceTestConfig()
	                              );

	  @Before
	  public void setUp() {
	    helper.setUp();
	  }


	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveImage() {
		fail("Not yet implemented");
	}

	static class Result {
		public Result(){};
		public int a;
		public float f;
		public double d;
		public String s = new String();
		public List<String> set = new ArrayList<String>();
		public Map<String,String> map = new HashMap<String, String>();
		public String str;
		public boolean b;
	}
	
	@Test
	public void testLoadCSVData() {
		String input = "TRUE, 1,0.1,0.01, \"AA, AA, A\", A | B | C, A:1 | B : 2, \"  dslkjsldkjflkjsdlfj\",11\n"
				+ "FALSE,2,0.2,0.03, \"bb,bb\", a | b | c, a:1 | b : 2, \" fff,ff\",12\n";
		
		Map<Integer, String> fieldPosMap = new HashMap<Integer, String>();;
		fieldPosMap.put(0, "b");
		fieldPosMap.put(1, "a");
		fieldPosMap.put(2, "f");
		fieldPosMap.put(3, "d");
		fieldPosMap.put(4, "s");
		fieldPosMap.put(5, "set");
		fieldPosMap.put(6, "map");
		fieldPosMap.put(7, "str");
		
		try {
			List<Result> res = CSVHelper.loadCSVData(new ByteArrayInputStream( input.getBytes()), fieldPosMap, new Result(), null, null, null);
			Assert.assertTrue(null!=res);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	@Test 
	public void testStoreData(){
		try {
			String origURL = "http://3.14.by/files/e4000.jpg";
			String newImageUrl = StorageHelper.saveImage(origURL);
			String newImageUrl2 = StorageHelper.saveImage(newImageUrl);
			URL url = new URL(origURL);
			URL url2 = new URL(newImageUrl2);
			InputStream is = url.openStream();
			InputStream is2 = url2.openStream();
			int read1;
			while( -1 !=(read1 = is.read()) )
				Assert.assertEquals(read1, is2.read());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
