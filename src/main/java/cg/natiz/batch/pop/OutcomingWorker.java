package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Container;
import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;
import cg.natiz.batch.pop.util.Pusher;
import cg.natiz.batch.pop.util.Repository;

/**
 * 
 * Receive containers from the outcoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            outcoming managed data type
 */
public class OutcomingWorker<T extends Serializable> implements Callable<String> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(OutcomingWorker.class);
	
	private Pusher<T> consumer;
	protected Repository<T>[] outcoming;

	/**
	 * @param outcoming
	 *            an outcoming repository
	 * @return this worker object
	 */
	public OutcomingWorker<T> setOutcoming(@SuppressWarnings("unchecked") Repository<T> ... outcoming) {
		logger.debug("Setting {} outcoming repository(ies)", outcoming.length);
		this.outcoming = outcoming;
		return this;
	}

	/**
	 * @param consumer
	 *            a processed data consumer
	 * @return this worker object
	 */
	public OutcomingWorker<T> setConsumer(
			@Controller(ControllerType.CONSUMER) Pusher<T> consumer) {
		this.consumer = consumer;
		return this;
	}

	@Override
	public String call() throws Exception {
		Container<T> container = null;
		Container<T> current = null;
		int waiting = 3;
		while (outcoming[0].isOpen()) {
			logger.debug("Consumer waiting for {} ms", waiting);
			Thread.sleep(waiting);
			logger.debug("Outcoming stock = {}", outcoming[0].size());
			waiting = (int) Math.max(3, Math.cbrt(outcoming[0].size()));
			while ((container = outcoming[0].pull()) != null) {
				container.setReceiptDate(new Date());
				if (consumer.push(container)) {
					logger.debug("Pushed to consumer : {}", container);
				}
				current = container;
			}
		}
		StringBuilder sb = new StringBuilder()
				.append(this.getClass().getSimpleName()).append(" : ")
				.append(current.toString());
		return sb.toString();
	}
}
