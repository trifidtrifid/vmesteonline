package com.vmesteonline.be.jdo2.shop.exchange;

import com.vmesteonline.be.shop.Producer;

public class ProducerDescription {
	public ProducerDescription() {
	}

	public long id;
	public String name;
	public String descr;
	public String logoURL;
	public String homeURL;

	public Producer getProducer(){
		return new Producer(id, name, descr, logoURL, homeURL);
	}
}

// PRODUCER_NAME_ID = 100, PRODUCER_NAME, PRODUCER_DESCRIPTION,
// PRODUCER_LOGOURL, PRODUCER_HOMEURL,