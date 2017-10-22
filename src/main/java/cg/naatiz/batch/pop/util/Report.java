package cg.naatiz.batch.pop.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Report implements Serializable {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
