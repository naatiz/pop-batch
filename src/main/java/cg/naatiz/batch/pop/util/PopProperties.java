package cg.naatiz.batch.pop.util;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * @author natiz
 */
@SuppressWarnings("serial")
public class PopProperties implements Serializable {

	@Inject
	@Property(key = "provider.worker.count", defaultValue = "1")
	int providerWorkerCount;

	@Inject
	@Property(key = "processor.worker.count", defaultValue = "1")
	int processorWorkerCount;

	@Inject
	@Property(key = "consumer.worker.count", defaultValue = "1")
	int consumerWorkerCount;

	public int getPool() {
		int pool = getProviderWorkerCount();
		pool += getProcessorWorkerCount();
		pool += getConsumerWorkerCount();
		return pool;
	}

	public int getProviderWorkerCount() {
		return providerWorkerCount;
	}

	public int getProcessorWorkerCount() {
		return processorWorkerCount;
	}

	public int getConsumerWorkerCount() {
		return consumerWorkerCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PopProperties [providerWorkerCount=").append(providerWorkerCount)
				.append(", processorWorkerCount=").append(processorWorkerCount).append(", consumerWorkerCount=")
				.append(consumerWorkerCount).append(", pool=").append(getPool()).append("]");
		return sb.toString();
	}
}
