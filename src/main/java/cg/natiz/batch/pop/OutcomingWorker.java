package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.natiz.batch.pop.util.Controller;
import cg.natiz.batch.pop.util.ControllerType;

/**
 * 
 * Receive containers from the outcoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            outcoming managed data type
 */
public class OutcomingWorker<T extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(OutcomingWorker.class);

	private Pusher<T> consumer;
	protected Repository<T> outcoming;

	/**
	 * @param consumer
	 *            a processed data consumer
	 * @param outcoming
	 *            in memory outcoming repository
	 * @return this worker object
	 */
	public OutcomingWorker<T> init(@Controller(ControllerType.CONSUMER) Pusher<T> consumer, Repository<T> outcoming) {
		this.consumer = consumer;
		this.outcoming = outcoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		Container<T> container = null;
		Container<T> current = null;
		int waiting = 3;
		while (outcoming.isOpen()) {
			logger.debug("Consumer waiting for {} ms", waiting);
			Thread.sleep(waiting);
			logger.debug("Outcoming stock = {}", outcoming.size());
			waiting = (int) Math.max(3, Math.cbrt(outcoming.size()));
			while ((container = outcoming.pull()) != null) {
				container.setReceiptDate(new Date());
				if (consumer.push(container)) {
					logger.debug("Pushed to consumer : {}", container);
				}
				current = container;
			}
		}
		StringBuilder sb = new StringBuilder().append(this.getClass().getSimpleName()).append(" : ")
				.append(current.toString());
		Reporting reporting = Pop.newInstance(Reporting.class).setDescription(sb.toString());
		return reporting;
	}
}
