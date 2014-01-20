package com.vmesteonline.be.jdo2;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class VoUser {

	public VoUser() {
	}

	public VoUser(String name, String lastName, String email, String password) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	public VoUserGroup getHomeGroup() {
		for (VoUserGroup r : groups) {
			if (r.getRadius() == 0)
				return r;
		}
		return null;
	}

	public List<VoRubric> getRubrics() {
		return rubrics;
	}

	public void setRubrics(List<VoRubric> rubrics) {
		this.rubrics = rubrics;
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

	public List<VoUserGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<VoUserGroup> groups) {
		this.groups = groups;
	}

	@Persistent
	private List<VoUserGroup> groups = new ArrayList<VoUserGroup>();

	@Persistent
	private List<VoRubric> rubrics = new ArrayList<VoRubric>();

	@Persistent
	private String name;

	@Persistent
	private String lastName;

	@Persistent
	private String email;

	@Persistent
	private String password;

}
