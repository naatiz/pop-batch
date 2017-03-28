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
 * Deliver containers in the incoming zone
 * 
 * @author natiz
 * 
 * @param <T>
 *            managed data type
 */
final class IncomingWorker<T extends Serializable> implements Callable<Reporting> {

	private static final Logger logger = LoggerFactory.getLogger(IncomingWorker.class);
	private Puller<T> provider;
	private Repository<T> incoming;

	private long currentContainerId = 0L;

	/**
	 * @param provider
	 *            a data provider
	 * @param incoming
	 *            an incoming repository
	 * @return this worker object
	 */
	public IncomingWorker<T> init(@Controller(ControllerType.PROVIDER) Puller<T> provider, Repository<T> incoming) {
		this.provider = provider;
		this.incoming = incoming;
		return this;
	}

	@Override
	public Reporting call() throws Exception {
		final Reporting reporting = Reporting.INCOMING;
		Optional<Container<T>> container = Optional.empty();
		int waiting = 1;
		LocalDateTime beginDate = LocalDateTime.now();
		do {
			logger.debug("Provider waiting for {} ms", waiting);
			Thread.sleep(waiting);
			container = provider.pull();
			if (container == null)
				continue;
			if (incoming.push(container)) {
				container.ifPresent(c -> {
					c.setReference(++currentContainerId).setSendDate(new Date());
					reporting.incrementContainersNumber().incrementItemsNumber(c.getContent().size());
				});
				logger.debug("Pushed to incoming {} \nIncoming stock = {}", container, incoming.size());
			} else {
				container.ifPresent(c -> {
					c.setReference(++currentContainerId).setSendDate(new Date());
					reporting.setDescription(c.toString());
				});
				logger.warn("Pushing to incoming fails {} \nIncoming stock = {}", container, incoming.size());
			}
		} while (container.isPresent());
		reporting.setIncomingDuration(ChronoUnit.SECONDS.between(beginDate, LocalDateTime.now()));
		logger.info("Worker ends successfully in {}s, {} containers and {} items ", reporting.getIncomingDuration(),
				reporting.getContainersNumber(), reporting.getItemsNumber());
		currentContainerId = 0L; // Initializing this ID
		return reporting;
	}
}
