package cg.natiz.batch.pop.util;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * @author natiz
 * @param <T>
 *            managed data type
 */
@SuppressWarnings("serial")
public class Cfg implements Serializable {

	@Inject
	@Property(key = "provider.worker.number", defaultValue = "1")
	String numberOfProviderWorker;

	@Inject
	@Property(key = "processor.worker.number", defaultValue = "1")
	String numberOfProcessorWorker;

	@Inject
	@Property(key = "consumer.worker.number", defaultValue = "1")
	String numberOfConsumerWorker;

	public int getSizeOfPool() {
		return getNumberOfProviderWorker() + getNumberOfProcessorWorker()
				+ getNumberOfConsumerWorker();
	}

	public int getNumberOfProviderWorker() {
		return Math.max(1, Math.abs(Integer.valueOf(numberOfProviderWorker)));
	}

	public int getNumberOfProcessorWorker() {
		return Math.max(1, Math.abs(Integer.valueOf(numberOfProcessorWorker)));
	}

	public int getNumberOfConsumerWorker() {
		return Math.max(1, Math.abs(Integer.valueOf(numberOfConsumerWorker)));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Cfg [numberOfProviderWorker=")
				.append(numberOfProviderWorker)
				.append(", numberOfProcessorWorker=")
				.append(numberOfProcessorWorker)
				.append(", numberOfConsumerWorker=")
				.append(numberOfConsumerWorker).append(", sizeOfPool=")
				.append(getSizeOfPool()).append("]");
		return sb.toString();
	}
}
