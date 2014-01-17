package com.vmesteonline.be.jdo2;

import java.util.List;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class VoUser {

	public VoUser(String name, String lastName, String email, String password) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	public Long getId() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@PersistenceCapable
	@EmbeddedOnly
	public static class GroupShort {
		@Persistent
		private String visibleName;

		@Persistent
		private Long groupId;

	}

	@Persistent
	private List<GroupShort> groups;

	@Persistent
	private String name;

	@Persistent
	private String lastName;

	@Persistent
	private String email;

	@Persistent
	private String password;

}
