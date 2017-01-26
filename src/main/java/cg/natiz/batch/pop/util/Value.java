package cg.natiz.batch.pop.util;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * @author natiz
 */
@SuppressWarnings("serial")
public class Value implements Serializable {

	@Inject
	@Property(key = "provider.worker.number", defaultValue = "1")
	String numberOfProviderWorker;

	public int getNumberOfProviderWorker() {
		return Math.max(1, Math.abs(Integer.valueOf(numberOfProviderWorker)));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PopProperties [numberOfProviderWorker=")
				.append(numberOfProviderWorker);
		return sb.toString();
	}
}
