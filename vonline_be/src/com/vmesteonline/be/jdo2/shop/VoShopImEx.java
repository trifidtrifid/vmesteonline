package com.vmesteonline.be.jdo2.shop;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.shop.bo.ExchangeFieldType;
import com.vmesteonline.be.shop.bo.ImExType;

@PersistenceCapable
public class VoShopImEx {
	public VoShopImEx() {
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	@Unindexed
	private ImExType type;
	@Persistent
	@Unindexed
	private String fileName;
	@Persistent
	@Unindexed
	private List<ExchangeFieldType> fieldsOrder;
	@Persistent
	@Unindexed
	private byte[] fileData;
	@Persistent
	@Unindexed
	private List<Serializable> rows;
}
