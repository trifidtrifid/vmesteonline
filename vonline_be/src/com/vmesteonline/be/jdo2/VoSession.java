package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.Session;

@PersistenceCapable
public class VoSession {

	public VoSession(VoUser user) {
		this.name = user.getName();
		this.lastName = user.getLastName();
	}

	public Session feSession() {
		Session sess = new Session();
		sess.setAccessGranted(true);
		sess.setError("access granted");
		return sess;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Persistent
	private String name;

	@Persistent
	private String lastName;

}
