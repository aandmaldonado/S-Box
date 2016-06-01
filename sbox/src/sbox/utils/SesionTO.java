package sbox.utils;

import org.lightcouch.Document;

public class SesionTO extends Document {
	private long timestamp;
	
	private String session;
	
	private String nameSession;
	
	private String type;
	
	private Boolean isFirst;
	
	private String typeProject;
		
	public Boolean getIsFirst() {
		return isFirst;
	}

	public void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeProject() {
		return typeProject;
	}

	public void setTypeProject(String typeProject) {
		this.typeProject = typeProject;
	}

	public String getNameSession() {
		return nameSession;
	}

	public void setNameSession(String nameSession) {
		this.nameSession = nameSession;
	}
}
