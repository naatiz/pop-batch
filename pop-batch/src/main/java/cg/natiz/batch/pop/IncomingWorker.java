package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Poller;

/**
 * Deliver containers in the incoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 */
public class IncomingWorker<T extends Serializable> implements Callable<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(IncomingWorker.class);
	private Repository<T> incoming;
	private Poller<T> sender;

	/**
	 * @param repository
	 *            an incoming repository
	 */
	public IncomingWorker<T> addRepository(
			Repository<T> incoming) {
		this.incoming = incoming;
		return this;
	}

	/**
	 * @param sender
	 *            an input sender
	 */
	public IncomingWorker<T> addSender(Poller<T> sender) {
		this.sender = sender;
		return this;
	}

	@Override
	public String call() throws Exception {
		Container<T> current = null;
		Container<T> container = null;
		do {
			Thread.sleep(incoming.getThreadSleep());
			logger.debug("Deliver a container to incoming ");
			container = sender.poll();
			if (container != null
					&& !container.isEmpty()
					&& incoming.push(container.setReference(
							incoming.getReference()).setSendDate(new Date()))) {
				current = container;
				logger.debug("Input worker: pushed to incoming " + current);
			}
		} while (container != null);
		incoming.close();
		return current.toString();
	}
}
