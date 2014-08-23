package com.vmesteonline.be.jdo2.postaladdress;

import java.math.BigDecimal;

public interface AddressInfo {


	public abstract String getLongLatString();

	public abstract String getStreetName();

	public abstract String getCityName();

	public abstract String getCountryName();

	public abstract String getBuildingNo();

	public abstract boolean isKindHouse();

	public abstract String getAddresText();

	public abstract boolean isExact();

	public abstract BigDecimal getLattitude();

	public abstract BigDecimal getLongitude();
	
	public abstract void setStreetName(String sn);

	public abstract void getBuildingNo(String bn);
	
	public abstract void setCityName(String bn);

	public abstract void setCountryName(String bn);

	public abstract void setLongitude(String string);

	public abstract void setLattitude(String string);

}