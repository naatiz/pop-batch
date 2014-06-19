package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Pusher;

/**
 * 
 * Receive containers from the outcoming zone 
 * 
 * @author natiz
 * 
 * @param <T>
 *            outcoming managed data type
 */
public class OutcomingWorker<T extends Serializable> implements
		Callable<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(OutcomingWorker.class);

	private Repository<T> outcoming;
	private Pusher<T> recipient;

	/**
	 * @param outcoming
	 *            an outcoming repository
	 */
	public OutcomingWorker<T> addRepository(Repository<T> outcoming) {
		this.outcoming = outcoming;
		return this;
	}

	/**
	 * @param recipient
	 *            recipient worker
	 */
	public OutcomingWorker<T> addRecipient(Pusher<T> recipient) {
		this.recipient = recipient;
		return this;
	}

	@Override
	public String call() throws Exception {
		Container<T> container = null;
		Container<T> current = null;
		while (outcoming.isOpen()) {
			Thread.sleep(outcoming.getThreadSleep());
			logger.debug("Outcoming stock = {}", outcoming.size());
			container = outcoming.poll();
			if (container == null) {
				continue;
			}
			container.setReceiptDate(new Date());
			if (recipient.push(container)) {
				logger.debug("Last received : {}", container);
			}
			current = container;
		}
		return current.toString();
	}
}
