package com.vmesteonline.be.utilityservices;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.utility.VoCounter;
import com.vmesteonline.be.utils.CSVHelper;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.VoHelper;

@SuppressWarnings("serial")
public class CountersStatisticsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bids = req.getParameter("bi");
		String ds = req.getParameter("ds");
		if( null!=bids)
			try {
				long buildingId = StorageHelper.stringToNumber(bids);
				int date = ds == null ? (int)(System.currentTimeMillis()/1000L) : (int) StorageHelper.stringToNumber(ds);
				Calendar clndr = Calendar.getInstance();
				clndr.setTimeInMillis(((long)date)*1000L);
				
				PersistenceManager pm = PMF.getPm();
				List<VoPostalAddress> pal = (List<VoPostalAddress>) pm.newQuery( VoPostalAddress.class, "buildingId=="+buildingId).execute();
				Set<Long> vgs = new HashSet<Long>();
				for (VoPostalAddress pa : pal) {
					vgs.add(pa.getId());
				}
				List<VoPostalAddress> sortedAddresses = new ArrayList<VoPostalAddress>( pal );
				Collections.sort(sortedAddresses, new Comparator<VoPostalAddress>(){
	
					@Override
					public int compare(VoPostalAddress o1, VoPostalAddress o2) {
						return Integer.compare(o1.getFlatNo(), o2.getFlatNo());
					}});
				Set<VoCounter> allCounters = VoHelper.getAllOfSet(vgs, VoCounter.class, "", "postalAddressId", pm);
				
				VoBuilding voBuilding = pm.getObjectById(VoBuilding.class, buildingId);
				List<List<String>> csvData = new ArrayList<List<String>>();
				csvData.add( Arrays.asList( new String[]{ "Показания счетчиков по дому: "+voBuilding.getAddressString()}));
				csvData.add( Arrays.asList( new String[]{ 
						"Номер подъезда", "Этаж", "Номер квартиры", "Тип счетчика", "Номер счетчика", "Место установки", "Текущее значение", "Дата снятия тз", "Предыдущее значение", "Дата снятия пз", "Расход"}));
				DateFormat df = DateFormat.getDateInstance();
				for (VoPostalAddress pa : sortedAddresses) {
					SortedSet<VoCounter> cl = new TreeSet<VoCounter>( UtilityServiceImpl.voCountersComparator );
					for (VoCounter voCounter : allCounters) {
						if( voCounter.getPostalAddressId() == pa.getId() ){
							cl.add(voCounter);
						}
					}
					allCounters.removeAll(cl);
					for (VoCounter voCounter : cl) {
						Map<Integer, Double> values = voCounter.getValues();
						if( null!=values && values.size() > 0){
							TreeMap<Integer,Double> valSorted = new TreeMap<Integer,Double>(values);
							Integer curDate = valSorted.floorKey(date);
							Integer monthAgoDate = valSorted.floorKey(date - 86400 * 30);
							if( null!=curDate) {
								csvData.add( Arrays.asList( new String[]{ 
										""+pa.getStaircase(), ""+pa.getFloor(), ""+pa.getFlatNo(), 
										voCounter.getType().name(), voCounter.getNumber(), voCounter.getLocation(), 
										""+valSorted.get(curDate),  "" + df.format(new Date( ((long)curDate)*1000L  )),
										""+valSorted.get(monthAgoDate),  "" + df.format(new Date( ((long)monthAgoDate)*1000L  )),
										""+VoHelper.roundDouble( valSorted.get(curDate) - valSorted.get(monthAgoDate), 2)}));
							}
						}
					}
					
					//write response
					resp.setStatus(HttpServletResponse.SC_OK);
					String fileName = URLEncoder.encode( "uc."+clndr.get(Calendar.YEAR)+"."+clndr.get(Calendar.MONTH)+"."+clndr.get(Calendar.DAY_OF_MONTH)+".csv","UTF-8");
					resp.setContentType("text/csv; filename=uc."+fileName);
					resp.addHeader( "Content-Disposition", "attachment; filename="+fileName);
					
					ServletOutputStream os = resp.getOutputStream();
					CSVHelper.writeCSV( os, csvData, ",", "|", ":");
					os.close();
				}
				
			} catch (Exception e){
				e.printStackTrace();
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				OutputStream os = resp.getOutputStream();
				os.write(e.getMessage().getBytes());
				os.close();
			}
	}
}
