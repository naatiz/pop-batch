package cg.natiz.batch.pop;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
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
class OutcomingWorker<T extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(OutcomingWorker.class);

	private Pusher<T> consumer;
	private Repository<T> outcoming;

	/**
	 * @param consumer
	 *            a processed data consumer
	 * @param outcoming
	 *            outcoming repository
	 * @return this worker object
	 */
	public OutcomingWorker<T> init(@Controller(ControllerType.CONSUMER) Pusher<T> consumer, Repository<T> outcoming) {
		this.consumer = consumer;
		this.outcoming = outcoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		final Reporting reporting = Reporting.newOutcomingRepository();
		Optional<Container<T>> container = Optional.empty();
		int waiting = 3;
		do {
			logger.debug("Consumer waiting for {} ms", waiting);
			Thread.sleep(waiting);
			logger.debug("Outcoming stock = {}", outcoming.size());
			waiting = (int) Math.max(3, Math.cbrt(outcoming.size()));
			container = Optional.ofNullable(outcoming.pull()).orElseGet(Optional::empty);
			container.ifPresent(c -> {
				c.setReceiptDate(new Date());
				try {
					if (consumer.push(Optional.of(c))) {
						reporting.incrementContainersNumber().incrementItemsNumber(c.getItems().size());
						logger.debug("Pushed to consumer : {}", c);
					} else {
						String message = String.format("Pushing to consumer fails %s \nOutcoming stock = %d", c,
								outcoming.size());
						throw new Exception(message);
					}
				} catch (Exception e) {
					reporting.addRapport(e.getMessage());
					logger.warn("Cannot push container to consumer: " + c, e);
				}
			});
		} while (outcoming.isOpen());
		logger.info("Worker ends successfully. {} ", reporting.stop());
		return reporting;
	}
}
