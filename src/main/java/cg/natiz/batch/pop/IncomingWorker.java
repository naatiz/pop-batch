package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * Deliver containers in the incoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 */
public class IncomingWorker<T extends Serializable> implements Callable<String> {

	private static final Logger logger = LoggerFactory.getLogger(IncomingWorker.class);
	private Puller<T> provider;
	protected Repository<T>[] incoming;

	/**
	 * @param incoming
	 *            an incoming repository
	 * @return this worker object
	 */
	public IncomingWorker<T> setIncoming(@SuppressWarnings("unchecked") Repository<T>... incoming) {
		logger.debug("Setting {} incoming repository(ies)", incoming.length);
		this.incoming = incoming;
		return this;
	}

	/**
	 * @param provider
	 *            a data provider
	 * @return this worker object
	 */
	public IncomingWorker<T> setProvider(@Controller(ControllerType.PROVIDER) Puller<T> provider) {
		this.provider = provider;
		return this;
	}

	@Override
	public String call() throws Exception {
		Container<T> current = null;
		Container<T> container = null;
		int waiting = 1;
		do {
			logger.debug("Provider waiting for {} ms", waiting);
			container = provider.pull();
			if (container != null && !container.isEmpty()
					&& incoming[0].push(container.setReference(incoming[0].getReference()).setSendDate(new Date()))) {
				current = container;
				logger.debug("Pushed to incoming {} \nIncoming stock = {}", current, incoming[0].size());
			}
			Thread.sleep(waiting);
		} while (container != null);
		incoming[0].close();

		StringBuilder sb = new StringBuilder().append(this.getClass().getSimpleName()).append(" : ")
				.append(current.toString());
		return sb.toString();
	}
}
