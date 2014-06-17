package com.vmesteonline.be.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.GeoLocation;
import com.vmesteonline.be.MatrixAsList;

public class VoHelper {

	public static BigDecimal earthRadius = new BigDecimal(6378137);

	// (180.0 / Math.PI) = 57,29577952383886
	public static BigDecimal degInRad = new BigDecimal("57.29577952");

	// (Pi * R) / 180
	private static BigDecimal piR = new BigDecimal("111319.4907932736");

	// (dLat * Math.cos(Math.PI * latitude/180) * PI * R) / (180 ) - в метрах по широте
	// (dLong * Pi * R) / 180 - в метрах по долготе

	public static boolean isInclude(GeoLocation a, int radius, GeoLocation b) {

		if (VoHelper.getLongitudeMax(a.getLongitude(), radius).compareTo(b.getLongitude()) >= 0)
			if (VoHelper.getLongitudeMin(a.getLongitude(), radius).compareTo(b.getLongitude()) <= 0)
				if (VoHelper.getLatitudeMax(a.getLatitude(), radius).compareTo(b.getLatitude()) >= 0)
					if (VoHelper.getLatitudeMin(a.getLatitude(), radius).compareTo(b.getLatitude()) <= 0)
						return true;
		return false;
	}

	public static int findMinimumGroupRadius(GeoLocation a, GeoLocation b) {

		if (!isInclude(a, Defaults.radiusStarecase, b)) {
			if (!isInclude(a, Defaults.radiusHome, b)) {
				if (!isInclude(a, Defaults.radiusSmall, b)) {
					if (!isInclude(a, Defaults.radiusMedium, b)) {
						if (!isInclude(a, Defaults.radiusLarge, b)) {
							return 100000;
						} else
							return Defaults.radiusLarge;
					} else
						return Defaults.radiusMedium;
				} else
					return Defaults.radiusSmall;
			} else
				return Defaults.radiusHome;
		} else
			return Defaults.radiusStarecase;

	}

	// TODO этот метод не используется. Метод принимает 2 координаты, и определяет расстояние между ними. Почему то, этот метод дает погрешность по
	// сравнению с методом который генерирует дельту
	public static int calculateRadius(GeoLocation a, GeoLocation b) {

		BigDecimal deltaLat = a.getLongitude().subtract(b.getLongitude()).abs();
		BigDecimal deltaLong = a.getLatitude().subtract(b.getLatitude()).abs();

		int rLat = deltaLong.multiply(piR).intValue();

		BigDecimal avgLat = a.getLatitude().add(b.getLatitude());
		avgLat = avgLat.divide(new BigDecimal(2));
		BigDecimal cos = new BigDecimal(Math.cos(Math.PI * avgLat.doubleValue() / 180D));

		int rLong = deltaLat.multiply(cos).multiply(piR).intValue();

		int maxRadius = rLong > rLat ? rLong : rLat;
		if (maxRadius == 0 && (a.getLongitude().compareTo(b.getLongitude()) != 0 || a.getLatitude().compareTo(b.getLatitude()) != 0))
			return Defaults.radiusHome;

		return maxRadius;
	}

	// (radius/earthRadius) * (180.0 / Math.PI)
	public static BigDecimal getLongitudeMax(BigDecimal longitude, int radius) {
		BigDecimal tmp = getLongDelta(radius);
		return longitude.add(tmp).setScale(7, RoundingMode.HALF_UP);
	}

	public static BigDecimal getLongitudeMin(BigDecimal longitude, int radius) {
		BigDecimal tmp = getLongDelta(radius);
		return longitude.subtract(tmp).setScale(7, RoundingMode.HALF_UP);
	}

	private static BigDecimal getLongDelta(int radius) {
		BigDecimal r = new BigDecimal(radius);
		BigDecimal tmp = r.divide(earthRadius, 10, RoundingMode.HALF_UP);
		tmp = tmp.multiply(degInRad);
		return tmp;
	}

	// (radius / (earthRadius * Math.cos(Math.PI * latitude/180)) * (180.0 / Math.PI);
	public static BigDecimal getLatitudeMin(BigDecimal latitude, int radius) {
		BigDecimal tmp = getLatDelta(latitude, radius);
		return latitude.subtract(tmp).setScale(7, RoundingMode.HALF_UP);
	}

	public static BigDecimal getLatitudeMax(BigDecimal latitude, int radius) {
		BigDecimal tmp = getLatDelta(latitude, radius);
		return latitude.add(tmp).setScale(7, RoundingMode.HALF_UP);
	}

	private static BigDecimal getLatDelta(BigDecimal latitude, int radius) {
		double cosVal = Math.cos(Math.PI * latitude.doubleValue() / 180D);
		BigDecimal r = new BigDecimal(radius);
		BigDecimal tmp = earthRadius.multiply(new BigDecimal(cosVal));
		tmp = r.divide(tmp, 10, RoundingMode.HALF_UP);
		tmp = tmp.multiply(degInRad);
		return tmp;
	}

	// ===================================================================================================================
	public static void copyIfNotNull(Object owner, String fieldName, Object objToCopy) throws NoSuchFieldException {
		if (null != objToCopy && objToCopy instanceof Number && ((Number)objToCopy).doubleValue() != 0.0 ) {
			Field field = null;
			try {
				field = owner.getClass().getField(fieldName);
			} catch (SecurityException | NoSuchFieldException e1) {
				//field is not accessible directly
			}
			try {
				if (null != field && field.isAccessible())
					field.set(owner, objToCopy);
				else {
					Method method = owner.getClass().getMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
							new Class[] { objToCopy.getClass() });
					method.invoke(owner, new Object[] { objToCopy });
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to set. " + e.getMessage());
			}
		}
	}

	// ===================================================================================================================

	public static void replaceURL(Object owner, String fieldName, String newUrl, long userId, boolean isPublic, PersistenceManager _pm)
			throws NoSuchFieldException {
		if (null != newUrl) {
			String oldVal = null;
			try {
				Field field = null;
				try {
					field = owner.getClass().getField(fieldName);
					if (field.isAccessible())
						oldVal = field.get(owner).toString();
				} catch (Exception e) {

					Method method = owner.getClass().getMethod("get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), new Class[] {});
					oldVal = (String) method.invoke(owner, new Object[] {});
				}
				String newStorageUrl;
				if (null == oldVal) { // old URL was not set
					newStorageUrl = StorageHelper.saveImage(newUrl, userId, isPublic, _pm);
				} else {
					newStorageUrl = StorageHelper.replaceImage(newUrl, oldVal, userId, isPublic, _pm);
				}

				if (null == field) {
					Method method = owner.getClass().getMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
							new Class[] { newStorageUrl.getClass() });
					method.invoke(owner, new Object[] { newStorageUrl });
				} else
					field.set(owner, newStorageUrl);

			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to reset URL: " + e.getMessage());
			}
		}
	}

	// ===================================================================================================================
	public static <A, B> Map<A, B> convertMap(Map<String, String> mapIn, Map<A, B> mapOut, A a, B b) {
		if (null != mapIn) {
			for (Iterator<Entry<String, String>> iterator = mapIn.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> e = iterator.next();
				Object key, value;
				if (a instanceof Long)
					key = Long.parseLong(e.getKey());
				else if (a instanceof Integer)
					key = Integer.parseInt(e.getKey());
				else if (a instanceof Byte)
					key = Byte.parseByte(e.getKey());
				else if (a instanceof Double)
					key = Double.parseDouble(e.getKey());
				else if (a instanceof Float)
					key = Float.parseFloat(e.getKey());
				else
					key = e.getKey();

				if (b instanceof Long)
					value = Long.parseLong(e.getValue());
				else if (b instanceof Integer)
					value = Integer.parseInt(e.getValue());
				else if (b instanceof Byte)
					value = Byte.parseByte(e.getValue());
				else if (b instanceof Double)
					value = Double.parseDouble(e.getValue());
				else if (b instanceof Float)
					value = Float.parseFloat(e.getValue());
				else
					value = e.getValue();

				mapOut.put((A) key, (B) value);
			}
		} else {
			return null;
		}
		return mapOut;
	}

	// ===================================================================================================================
	public static <T> List<T> convertSet(List<String> inList, ArrayList<T> outList, T b) {
		if (null != inList) {
			Object value;
			for (String e : inList) {
				if (b instanceof Long)
					value = Long.parseLong(e);
				else if (b instanceof Integer)
					value = Integer.parseInt(e);
				else if (b instanceof Byte)
					value = Byte.parseByte(e);
				else if (b instanceof Double)
					value = Double.parseDouble(e);
				else if (b instanceof Float)
					value = Float.parseFloat(e);
				else
					value = e;
				outList.add((T) value);
			}
		} else {
			return null;
		}
		return outList;
	}

	/*
	 * ============================================================================ =================================== //Method converts list of I
	 * object from list inList to list of O objects using mutation method of O object that //tooks I objects and has name get<O.simpleName()>
	 */

	public static <I, O> List<O> convertMutableSet(List<I> inList, ArrayList<O> outList, O o) throws InvalidOperation {
		if (null == inList)
			return null;
		if (0 == inList.size())
			return outList;
		I i0 = inList.get(0);
		try {
			Method method = i0.getClass().getMethod("get" + o.getClass().getSimpleName(), new Class[] {});
			for (I i : inList) {
				outList.add((O) method.invoke(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Class " + i0.getClass().getSimpleName() + " have no acccesible method get"
					+ o.getClass().getSimpleName());
		}
		return outList;
	}
	// ===================================================================================================================
	public static <T> Map<Integer,T> listToMap( Collection<T> col ){
		int i=0;
		Map<Integer,T> res = new TreeMap<Integer, T>();
		for (T t : col) {
			res.put(i++, t);
		}
		return res;
	}
//===================================================================================================================
	public static void forgetAllPersistent(Class cl, PersistenceManager pm){
		Extent extent = pm.getExtent(cl);
		for (Object object : extent) {
			try {
				pm.deletePersistent(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//===================================================================================================================
	public static double roundDouble(double quantity, int scale) {
		BigDecimal valOfQuantity = BigDecimal.valueOf( quantity );
		valOfQuantity = valOfQuantity.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return valOfQuantity.doubleValue();
	}

	//===================================================================================================================
	public static MatrixAsList matrixToList( List < List <String> > matrix ){
		List<String> list = new ArrayList<String>();
		
		if(0!=matrix.size()){
			int maxRowLen = 0;
			for( int row = 0; row < matrix.size(); row ++)
				if( maxRowLen < matrix.get(row).size() )
					maxRowLen = matrix.get(row).size();
			
			for( int row = 0; row < matrix.size(); row ++) {
		
				List<String> rowVal = matrix.get(row);
				
				for (String val : rowVal) {
					if( !list.add( null == val ? "" : val)) {
						throw new RuntimeException( "Implemetation ERROR! Collection must support add method without check of elemnts uniqueless!");
					}
				}
				while(0 != list.size() % maxRowLen)
					list.add("");
			}
		}
		return new MatrixAsList(matrix.size(), list);
	}
	//===================================================================================================================	
	public static List < List <String> > listToMatrix( MatrixAsList mas ){
		List < List <String> > matrix = new ArrayList<List<String>>();
		int rowLen = mas.getElemsSize() / mas.rowCount;
		for( int row = 0; row < mas.rowCount; row ++ ){
			matrix.add(new ArrayList<String>( mas.elems.subList(row * rowLen, (row + 1) * rowLen )));
		}
		return matrix;
	}

	public static<T> List<List<T>> transMatrix(List<List<T>> matrix) {
		
		int ocols  = matrix.size();
		int orows = ocols > 0 ? matrix.get(0).size() : 0;
		
		List<List<T>> out = new ArrayList<List<T>>(orows);
		for( int row = 0; row < orows; row++){
			out.add(new ArrayList<T>(ocols));
			List<T> nextORow = out.get(row);
			for( int col = 0; col < ocols; col++){
				nextORow.add( matrix.get(col).get(row));
			}
		}
		return out;
	}
}
