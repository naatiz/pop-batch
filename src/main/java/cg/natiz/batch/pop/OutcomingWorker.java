package cg.natiz.batch.pop;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
		final Reporting reporting = Reporting.OUTCOMING;
		Optional<Container<T>> container = Optional.empty();
		int waiting = 3;
		LocalDateTime beginDate = LocalDateTime.now();
		do {
			logger.debug("Consumer waiting for {} ms", waiting);
			Thread.sleep(waiting);
			logger.debug("Outcoming stock = {}", outcoming.size());
			waiting = (int) Math.max(3, Math.cbrt(outcoming.size()));
			container = outcoming.pull();
			if (container == null)
				continue;
			container.ifPresent(c -> c.setReceiptDate(new Date()));
			if (container.isPresent() && consumer.push(container)) {
				container.ifPresent(c ->reporting.incrementContainersNumber().incrementItemsNumber(c.getContent().size()));
				logger.debug("Pushed to consumer : {}", container);
			} else {
				container.ifPresent(c -> {
					reporting.setDescription(c.toString());
					logger.warn("Pushing to consumer fails {} \nOutcoming stock = {}", c, outcoming.size());
				});
			}
		} while (container.isPresent());
		reporting.setOutcomingDuration(ChronoUnit.SECONDS.between(beginDate, LocalDateTime.now()));
		logger.info("Worker ends successfully in {}s, {} containers and {} items ", reporting.getOutcomingDuration(),
				reporting.getContainersNumber(), reporting.getItemsNumber());
		return reporting;
	}
}
