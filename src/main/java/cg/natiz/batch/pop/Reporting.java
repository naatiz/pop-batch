/**
 * 
 */
package cg.natiz.batch.pop;

import java.io.Serializable;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Reporting implements Serializable {

	/**
	 * 
	 */
	private String description;
	
	protected Reporting() {
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public Reporting setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(this.getDescription());
		return sb.toString();
	}
}
