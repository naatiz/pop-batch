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

	final static Reporting INCOMING = new Reporting(WorkerType.INCOMING);
	final static Reporting PROCESSING = new Reporting(WorkerType.PROCESSING);
	final static Reporting OUTCOMING = new Reporting(WorkerType.OUTCOMING);

	private long containersNumber = 0;
	private long itemsNumber = 0;

	private long incomingDuration = 0;
	private long processingDuration = 0;
	private long outcomingDuration = 0;

	private enum WorkerType {
		INCOMING, PROCESSING, OUTCOMING
	}

	private WorkerType type;

	/**
	 * 
	 */
	private String description;

	protected Reporting(WorkerType type) {
		this.type = type;
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

	public WorkerType getType() {
		return type;
	}

	public long getContainersNumber() {
		return containersNumber;
	}

	public Reporting incrementContainersNumber() {
		this.containersNumber++;
		return this;
	}

	public long getItemsNumber() {
		return itemsNumber;
	}

	public Reporting incrementItemsNumber(long itemsNumber) {
		this.itemsNumber += itemsNumber;
		return this;
	}

	public long getIncomingDuration() {
		return incomingDuration;
	}

	public Reporting setIncomingDuration(long incomingDuration) {
		this.incomingDuration = incomingDuration;
		return this;
	}

	public long getProcessingDuration() {
		return processingDuration;
	}

	public Reporting setProcessingDuration(long processingDuration) {
		this.processingDuration = processingDuration;
		return this;
	}

	public long getOutcomingDuration() {
		return outcomingDuration;
	}

	public Reporting setOutcomingDuration(long outcomingDuration) {
		this.outcomingDuration = outcomingDuration;
		return this;
	}
}
