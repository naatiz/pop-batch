/**
 * 
 */
package cg.naatiz.batch.pop;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.google.common.collect.Lists;

import cg.naatiz.batch.pop.util.ControllerType;
import cg.naatiz.batch.pop.util.Report;

/**
 * @author natiz
 * 
 */
@SuppressWarnings("serial")
public class Reporting implements Serializable {

	private final static int MAX_RAPPORT_SIZE = 200;

	private long containersNumber = 0;
	private long itemsNumber = 0;

	private ControllerType type;

	private LocalDateTime startDate = LocalDateTime.now();
	private LocalDateTime endDate = startDate;

	private List<Report> reports = Lists.newArrayList();

	/**
	 * 
	 * @param type
	 */
	private Reporting(ControllerType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newIncomingRepository() {
		return new Reporting(ControllerType.PROVIDER).start();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newProcessingRepository() {
		return new Reporting(ControllerType.PROCESSOR).start();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Reporting newOutcomingRepository() {
		return new Reporting(ControllerType.CONSUMER).start();
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public Reporting addReport(String message) {
		Report report = new Report();
		report.setMessage(message);
		this.reports.add(report);
		if (this.reports.size() > MAX_RAPPORT_SIZE)
			throw new IllegalStateException("Too many (" + MAX_RAPPORT_SIZE + ") wrong rapports have been generated");
		return this;
	}

	@Override
	public String toString() {
		String report = String.format("Total duration/containers/items: %ds/%d/%d", this.getDuration(),
				this.getContainersNumber(), this.getItemsNumber());
		return report;
	}

	public ControllerType getType() {
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
