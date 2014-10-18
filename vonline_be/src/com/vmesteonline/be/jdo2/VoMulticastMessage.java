package com.vmesteonline.be.jdo2;

import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class VoMulticastMessage {

	
	public VoMulticastMessage(List<Long> visibleGroups, int startAfter, int endBefore, String message) {
		super();
		this.visibleGroups = visibleGroups;
		this.startAfter = startAfter;
		this.endBefore = endBefore;
		this.message = message;
	}
	
	public List<Long> getVisibleGroups() {
		return visibleGroups;
	}

	public void setVisibleGroups(List<Long> visibleGroups) {
		this.visibleGroups = visibleGroups;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStartAfter() {
		return startAfter;
	}

	public void setStartAfter(int startAfter) {
		this.startAfter = startAfter;
	}

	public int getEndBefore() {
		return endBefore;
	}

	public void setEndBefore(int endBefore) {
		this.endBefore = endBefore;
	}

	@Persistent
	private List<Long> visibleGroups;
	
	@Persistent
	private String message;
	
	@Persistent
	private int startAfter;
	
	@Persistent
	private int endBefore;
}
