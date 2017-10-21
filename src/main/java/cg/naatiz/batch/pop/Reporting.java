/**
 * 
 */
package cg.naatiz.batch.pop;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Reporting implements Serializable {

	private final static int MAX_RAPPORT_SIZE = 200;

	private long containersNumber = 0;
	private long itemsNumber = 0;

	private enum WorkerType {
		INCOMING, PROCESSING, OUTCOMING
	}

	private WorkerType type;

	private LocalDateTime startDate = LocalDateTime.now();
	private LocalDateTime endDate = startDate;

	private List<String> rapports = Lists.newArrayList();

	/**
	 * 
	 * @param type
	 */
	private Reporting(WorkerType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newIncomingRepository() {
		return new Reporting(WorkerType.INCOMING).start();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newProcessingRepository() {
		return new Reporting(WorkerType.PROCESSING).start();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newOutcomingRepository() {
		return new Reporting(WorkerType.OUTCOMING).start();
	}

	public List<String> getRapports() {
		return rapports;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public Reporting addRapport(String message) {
		this.rapports.add(message);
		if (this.rapports.size() > MAX_RAPPORT_SIZE)
			throw new IllegalStateException("Too many (" + MAX_RAPPORT_SIZE + ") wrong rapports have been generated");
		return this;
	}

	@Override
	public String toString() {
		String report = String.format("Total time/containers/items: %ds/%d/%d", this.getDuration(),
				this.getContainersNumber(), this.getItemsNumber());
		return report;
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

	public long getDuration() {
		return ChronoUnit.SECONDS.between(startDate, endDate);
	}

	public Reporting start() {
		this.startDate = LocalDateTime.now();
		return this;
	}

	public Reporting stop() {
		this.endDate = LocalDateTime.now();
		return this;
	}
}
